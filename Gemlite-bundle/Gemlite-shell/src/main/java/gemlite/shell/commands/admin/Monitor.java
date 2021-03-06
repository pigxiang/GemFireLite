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
package gemlite.shell.commands.admin;

import gemlite.core.internal.admin.measurement.ScannedMethodItem;
import gemlite.core.internal.measurement.RemoteServiceStatItem;
import gemlite.core.internal.support.jmx.MBeanHelper;
import gemlite.core.util.LogUtil;
import gemlite.shell.commands.CommandMeta;
import gemlite.shell.service.admin.JMXService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.ObjectInstance;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * 监控相关shell
 * 
 * @author GSONG
 *         2015年3月13日
 */
@Component
public class Monitor extends AbstractAdminCommand
{
  @Autowired
  private JMXService jmxSrv;
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @CliCommand(value = "list services", help = "list services")
  public String services()
  {
    Set<ObjectInstance> names = jmxSrv.listMBeans();
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    if (names == null)
      return "no services";
    Object[] a = names.toArray();
    //按名字排好序
    Arrays.sort(a, (Comparator)new Comparator<ObjectInstance>() {
      @Override
      public int compare(ObjectInstance o1, ObjectInstance o2) {
        return (o1.getObjectName().toString()).compareTo(o2.getObjectName().toString());
      }
    });
    
    if(LogUtil.getCoreLog().isDebugEnabled())
      LogUtil.getCoreLog().debug("get names size:{} values:{}",names.size(),names.toString());
    
    //对每个service进行数据更新
    for (int i=0;i<a.length;i++)
    {
      ObjectInstance oi = (ObjectInstance)a[i];
      // 将service存储起来,下一步再获取数据
      HashMap<String, Object> service = new HashMap<String, Object>();
      service.put(oi.getObjectName().toString(), service);
      service.put("serviceName", service);
      try
      {
        Map<String, Object> map = jmxSrv.getAttributesValues(oi.getObjectName().toString());
        if (map.size() <= 0)
        {
          map.put("serviceName", oi.getObjectName().toString());
          LogUtil.getCoreLog().warn("jmxSrv.getAttributesValues is null ,ObjectName {}", oi.getObjectName().toString());
          result.add(map);
        }
        else
        {
          result.add(map);
        }
      }
      catch (Exception e)
      {
        LogUtil.getCoreLog().error("jmxSrv.getAttributesValues is null ,ObjectName {},error:{}",
            oi.getObjectName().toString(), e);
      }
    }
    
    LinkedHashMap<String, HashMap<String, Object>> rs =  (LinkedHashMap<String, HashMap<String, Object>>)get(CommandMeta.LIST_SERVICES);
    if(rs == null)
     rs = new LinkedHashMap<String, HashMap<String, Object>>();
    for (Map<String, Object> map : result)
    {
      // 然后进行填充
      HashMap<String, Object> service = rs.get(map.get("serviceName"));
      if (service == null)
      {
        service = new HashMap<String, Object>();
      }
      service.putAll(map);
      
      // 将数据填进去
      rs.put((String) map.get("serviceName"), service);
    }
    
    // 方便ws取数据
    put(CommandMeta.LIST_SERVICES, rs);
    if (!result.isEmpty())
      return result.toString();
    return "no services";
  }
  
  @CliCommand(value = "describe service", help = "describe service")
  public String describeService(
      @CliOption(key = "name", mandatory = true, optionContext = "disable-string-converter param.context.service.name", help = "Name of the service to be described.") String name)
  {
    Map<String, Object> map = null;
    StringBuilder sb = new StringBuilder();
    try
    {
      map = jmxSrv.getAttributesValues(name);
      if(map==null)
        return "no details";
      int maxHistorySize = 5;
      int recentHistorySize = 15;
      Iterator<Entry<String, Object>> it = map.entrySet().iterator();
      while (it.hasNext())
      {
        Entry<String,Object> a = it.next();
        
        // 取出maxHistorySize,recentHistorySize
        if (StringUtils.equals("maxHistorySize", a.getKey()))
          maxHistorySize = (Integer) a.getValue();
        if (StringUtils.equals("recentHistorySize", a.getKey()))
          recentHistorySize = (Integer) a.getValue();
        
        sb.append(a.getKey());
        sb.append(":");
        sb.append(a.getValue() + "\n");
      }
      
      // 取recentHistory
      List<RemoteServiceStatItem> recents = getOperation(name, "getRecentHistory", recentHistorySize);
      sb.append("recentHistory:").append("\n");
      for (RemoteServiceStatItem item : recents)
      {
        sb.append("ModuleName:" + item.getModuleName()).append("\n");
        sb.append("ServiceName:" + item.getServiceName()).append("\n");
        sb.append("Start:" + item.getStart()).append("\n");
        sb.append("End" + item.getEnd()).append("\n");
        sb.append("CheckPoints:" + item.getCheckPoints()).append("\n");
      }
      // 取maxHistory
      List<RemoteServiceStatItem> max = getOperation(name, "getMaxHistory", maxHistorySize);
      sb.append("MaxHistory:").append("\n");
      for (RemoteServiceStatItem item : max)
      {
        sb.append("ModuleName:" + item.getModuleName()).append("\n");
        sb.append("ServiceName:" + item.getServiceName()).append("\n");
        sb.append("Start:" + item.getStart()).append("\n");
        sb.append("End" + item.getEnd()).append("\n");
        sb.append("CheckPoints:" + item.getCheckPoints()).append("\n");
      }
    }
    catch (Exception e)
    {
      LogUtil.getCoreLog().error("jmx error:{}", e);
      sb.append("jmx error:");
      sb.append(e.getMessage());
      sb.append("Command failed,see debug log for more information");
    }
    return sb.toString();
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public List<RemoteServiceStatItem> getOperation(String name, String operation, int size)
  {
    List<RemoteServiceStatItem> list = new ArrayList<RemoteServiceStatItem>();
    // 取recentHistory
    String oname = MBeanHelper.createServiceMBeanName(name);
    Object obj = jmxSrv.invokeOperation(oname, operation);
    List<RemoteServiceStatItem> total = new ArrayList<RemoteServiceStatItem>();
    if (obj instanceof List<?>)
    {
      total.addAll((List) obj);
    }
    else if (obj instanceof RemoteServiceStatItem)
    {
      total.add((RemoteServiceStatItem) obj);
    }
    else
    {
      LogUtil.getCoreLog().error(
          "Monitor getOperation(" + name + "," + operation + "," + size + "):" + obj + " is not a validate type!");
      return list;
    }
    
    // 排序
    // 如果是最近的,则按开始时间排序
    if ("getRecentHistory".equals(operation))
    {
      Collections.sort(total, new Comparator<RemoteServiceStatItem>()
      {
        @Override
        public int compare(RemoteServiceStatItem o1, RemoteServiceStatItem o2)
        {
          return new Long(o2.getStart()).compareTo(new Long(o1.getStart()));
        }
      });
    }
    
    // 如果是最大值,则按耗时排序
    if ("getMaxHistory".equals(operation))
    {
      Collections.sort(total, new Comparator<RemoteServiceStatItem>()
      {
        @Override
        public int compare(RemoteServiceStatItem o1, RemoteServiceStatItem o2)
        {
          return new Long(o2.getEnd() - o2.getStart()).compareTo(new Long(o1.getEnd() - o1.getStart()));
        }
      });
    }
    if (total.size() >= size)
      return total.subList(0, size - 1);
    else
      return total;
  }
  
  /**
   * 根据serviceName列出所有CheckPoint
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  @CliCommand(value = "list checkpoints", help = "list checkpoints")
  public String checkpoints(
      @CliOption(key = "moduleName", mandatory = true, help = "Name of the module") String moduleName,
      @CliOption(key = "serviceName", mandatory = true, optionContext = "disable-string-converter param.context.service.name", help = "Name of the service to be described.") String serviceName)
  {
    String oname = MBeanHelper.createManagerMBeanName("CheckPointService");
    Object obj = jmxSrv.invokeOperation(oname, "listMethods", moduleName, serviceName);
    List<ScannedMethodItem> list = new ArrayList<ScannedMethodItem>();
    
    if (obj instanceof List)
    {
      list = (List<ScannedMethodItem>) obj;
    }
    else
    {
      LogUtil.getCoreLog().error("list Methods error:{}", obj);
    }
    
    // 方便ws取数据
    put(CommandMeta.LIST_CHECKPOINTS, list);
    if (!list.isEmpty())
      return list.toString();
    return "no checkpoint";
  }
  
  /**
   * 根据serviceName列出所有CheckPoint
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  @CliCommand(value = "reload checkpoints", help = "list checkpoints")
  public String reload(@CliOption(key = "moduleName", mandatory = true) String moduleName)
  {
    String oname = MBeanHelper.createManagerMBeanName("HotDeploy");
    Object obj = jmxSrv.invokeOperation(oname, "reload", moduleName);
    
    boolean rs = true;
    if (obj instanceof List)
    {
      List<Boolean> list = (List<Boolean>) obj;
      // 遍历查看是否有失败的
      if (list != null)
        for (Boolean r : list)
        {
          // 如果遇到失败的,则停止,返回false
          if (!r)
          {
            rs = r;
            break;
          }
        }
    }
    else
    {
      rs = (boolean) obj;
    }
    
    // 方便ws取数据
    put(CommandMeta.RELOAD_CHECKPOINTS, rs);
    return obj.toString();
  }
}
