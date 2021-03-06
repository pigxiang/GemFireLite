/*                                                                         
 * Copyright 2010-2013 the original author or authors.                     
 *                                                                         
 * Licensed under the Apache License, Version 2.0 (the "License");         
 * you may not use this file except in compliance with the License.        
 * You may obtain a copy of the License at                                 
 *                                                                         
 *      http://www.apache.org/licenses/LICENSE-2.0                         
 *                                                                         
 * Unless required by applicable law or agreed to in writing, software     
 * distributed under the License is distributed on an "AS IS" BASIS,       
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and     
 * limitations under the License.                                          
 */                                                                        
package gemlite.core.batch;

import gemlite.core.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/***
 * 1.读取配置信息 2.读取模版 3.根据条件填充模版
 * 
 * @author ynd
 * 
 */
class JobItem
{
  public String jobName;
  public String type;
  public Map<String, Object> props = new HashMap<String, Object>();
}

public class FileImportTemplate
{
  public final static String COMMON_JOB = "common";
  private JobItem commonItem = new JobItem();
  private Map<String, JobItem> jobItems = new HashMap<String, JobItem>();
  private String[] jobNames;
  private StringTemplateGroup stGroup;
  private String templateFile;
  private String propertyFile;
  private Properties props;
  private boolean propInited = false;
  
  public final String[] getJobNames()
  {
    return this.jobNames;
  }
  
  protected synchronized void initProperties(String templateType)
  {
    if (propInited)
      return;
    propInited = true;
    try
    {
      // 可以传值,也可以读配置文件
      if (props == null)
      {
        props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile);
        props.load(in);
        in.close();
      }
      
      for (Entry<Object, Object> e : props.entrySet())
      {
        String key = (String) e.getKey();
        String value = (String) e.getValue();
        int i = key.indexOf(".");
        String name = key.substring(0, i);
        String type = key.substring(i + 1);
        LogUtil.getAppLog().debug("Name:" + name + "type:" + type + " value:" + value);
        JobItem ji = jobItems.get(name);
        if (ji == null)
        {
          ji = new JobItem();
          ji.jobName = name;
          ji.type = templateType;
          jobItems.put(name, ji);
        }
        ji.props.put(type, value);
        
        if (type.equalsIgnoreCase("files"))
          ji.type = "file";
        else if (type.equalsIgnoreCase("sortKey"))
        {
          ji.type = templateType + "Paging";
          String[] keys = value.split(",");
          ji.props.put(type, Arrays.asList(keys));
        }
        else if (type.equalsIgnoreCase("sql"))
          ji.type = templateType;
      }
      commonItem = jobItems.get(COMMON_JOB);
      jobItems.remove(COMMON_JOB);
    }
    catch (IOException e)
    {
      LogUtil.getAppLog().error(propertyFile, e);
      throw new RuntimeException(e);
    }
  }
  
  protected synchronized void initOneProperties(String templateType)
  {
    if (propInited)
      return;
    propInited = true;
    try
    {
      // 可以传值,也可以读配置文件
      if (props == null)
      {
        props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile);
        props.load(in);
        in.close();
      }
      
      String name = (String) props.getProperty("jobName");
      JobItem ji = jobItems.get(name);
      if (ji == null)
      {
        ji = new JobItem();
        ji.jobName = name;
        ji.type = templateType;
        jobItems.put(name, ji);
      }
      
      // 对props遍历,取属性
      for (Entry<Object, Object> e : props.entrySet())
      {
        String key = (String) e.getKey();
        String value = (String) e.getValue();
        ji.props.put(key, value);
      }
    }
    catch (IOException e)
    {
      LogUtil.getAppLog().error(propertyFile, e);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 加载模版和配置
   */
  public void prepareTemplate(String templateType)
  {
    initProperties(templateType);
    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateFile);
    stGroup = new StringTemplateGroup(new InputStreamReader(in));
  }
  
  /**
   * 处理单独的job
   */
  public void prepareOneTemplate(String templateType)
  {
    initOneProperties(templateType);
    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateFile);
    stGroup = new StringTemplateGroup(new InputStreamReader(in));
  }
  
  /**
   * 执行一个job
   * 
   * @param ir
   * @param template
   * @param meta
   */
  public List<Resource> generateAllJob()
  {
    List<Resource> list = new ArrayList<Resource>();
    for (JobItem job : jobItems.values())
    {
      Resource res = generateOneJob(job);
      list.add(res);
    }
    return list;
  }
  
  public Resource generateOneJob(String jobName)
  {
    return generateOneJob(jobItems.get(jobName));
  }
  
  private Resource generateOneJob(JobItem job)
  {
    String tpl = applyTemplate(job);
    byte[] content = tpl.getBytes();
    Resource res = new ByteArrayResource(content, job.jobName);
    return res;
  }
  
  /**
   * 写死的4个字段
   * 
   * @param template
   * @param meta
   * @return
   */
  private String applyTemplate(JobItem job)
  {
    Map<String, Object> attributes = new HashMap<String, Object>();
    for (String type : commonItem.props.keySet())
    {
      Object replace = commonItem.props.get(type);
      attributes.put(type, replace);
    }
    LogUtil.getAppLog().info("Job.props->" + job.props);
    for (String type : job.props.keySet())
    {
      Object replace = job.props.get(type);
      attributes.put(type, replace);
    }
    
    StringTemplate template = stGroup.getInstanceOf(job.type + "Template");
    template.setAttributes(attributes);
    return template.toString();
  }
  
  public String getTemplateFile()
  {
    return templateFile;
  }
  
  public void setTemplateFile(String templateFile)
  {
    this.templateFile = templateFile;
  }
  
  public String getPropertyFile()
  {
    return propertyFile;
  }
  
  public void setPropertyFile(String propertyFile)
  {
    this.propertyFile = propertyFile;
  }
  
  public Properties getProps()
  {
    return props;
  }
  
  public void setProps(Properties props)
  {
    this.props = props;
  }
}
