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
package gemlite.shell.commands;

import gemlite.core.internal.jmx.manage.KeyConstants.Indexs;
import gemlite.core.internal.support.jmx.MBeanHelper;
import gemlite.shell.commands.admin.AbstractAdminCommand;
import gemlite.shell.common.GemliteStatus;
import gemlite.shell.service.admin.IndexService;
import gemlite.shell.service.admin.JMXService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class IndexCommand extends AbstractAdminCommand
{
  @Autowired
  private IndexService indexService;
  @Autowired
  private JMXService jmxService;
  
  
  @CliAvailabilityIndicator({ "create index", "drop index", "list index", "describe index", "query index" })
  public boolean onlineCommand()
  {
    return true;
  }
  
  @CliCommand(value = "create index", help = "create index --clause")
  public String create(
		  @CliOption(key="jmx", specifiedDefaultValue="true", unspecifiedDefaultValue="false", help="using jmx service") boolean jmx,
		  @CliOption(key="clause", mandatory=true)
		  String clause)
  {
	  String result = "";
	  if(jmx)
	  {
		  MBeanServerConnection connection = jmxService.getConnection();
		  if(connection == null)
			  return "Please connect to the Gemlite Mangaer first.";
		  
		  String oname = MBeanHelper.createManagerMBeanName("IndexManager");
		  Object obj = jmxService.invokeOperation(oname, "createIndex", clause.trim());
		  if(obj instanceof List)
		  {
		      result = obj.toString();
		  }
		  else
		  {
		      result += (String)obj;
		  }
		  
	  }
	  else
	  {
		  if(!GemliteStatus.isConnected())
			  return "Please start Gemlite Client first";
		  else
			  result = indexService.create(clause.trim());
	  }
	  
	  return result;
  }
  
  @CliCommand(value = "drop index", help = "drop index --name")
  public String drop(
		  @CliOption(key="jmx", specifiedDefaultValue="true", unspecifiedDefaultValue="false", help="using jmx service") boolean jmx,
		  @CliOption(key = "name", mandatory=true, help="index name")
		  String indexName)
  {
	  String result = "";
	  if(jmx)
	  {
		  MBeanServerConnection connection = jmxService.getConnection();
		  if(connection == null)
			  return "Please connect to the Gemlite Mangaer first.";
		  
		  String oname = MBeanHelper.createManagerMBeanName("IndexManager");
		  Object obj = jmxService.invokeOperation(oname, "dropIndex", indexName.trim());
          if(obj instanceof List)
          {
              result = obj.toString();
          }
          else
          {
              result += (String)obj;
          }
	  }
	  else
	  {
		  if(!GemliteStatus.isConnected())
			  return "Please start Gemlite Client first";
		  else
			  result = indexService.drop(indexName.trim());
	  }
	  
	  return result;
  }
  
  @SuppressWarnings("unchecked")
  @CliCommand(value = "list index", help = "list index --region")
  public String list(
          @CliOption(key="jmx", specifiedDefaultValue="true", unspecifiedDefaultValue="false", help="using jmx service") boolean jmx,
          @CliOption(key = "region", mandatory=true, help="region name",optionContext="disable-string-converter param.context.region")String regionName
          )
  {
      if(jmx)
      {
          MBeanServerConnection connection = jmxService.getConnection();
          if(connection == null)
              return "Please connect to the Gemlite Mangaer first.";
          
          String oname = MBeanHelper.createManagerMBeanName("IndexManager");
          Object obj = jmxService.invokeOperation(oname, "listIndex", regionName.trim());
          List<String> list = new ArrayList<String>();
          if(obj instanceof List)
          {
              //取一个点的数据就行了
              list = (List<String>)obj;
          }
          else
              list =  (List<String>)obj;
          
          //为ws传递数据
          put(CommandMeta.LIST_INDEX,list);
          
          StringBuffer sb = new StringBuffer();
            for(int i=0; i<list.size(); i++)
                sb.append((i+1) + ": " + list.get(i) + "\n");
            return sb.toString();	    
      }
      else
      {
          if(!GemliteStatus.isConnected())
              return "Please start Gemlite Client first";
          else
              return indexService.list(regionName.trim());
      }
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
@CliCommand(value = {"describe index"}, help = "describe index --name")
  public String describeIndex(
		  @CliOption(key="jmx", specifiedDefaultValue="true", unspecifiedDefaultValue="false", help="using jmx service") boolean jmx,
		  @CliOption(key = "name", mandatory=true, help="index name")String indexName
		  )
  {
	  String result = "";
	  if(jmx)
	  {
		  MBeanServerConnection connection = jmxService.getConnection();
		  if(connection == null)
			  return "Please connect to the Gemlite Mangaer first.";
		  
		  String oname = MBeanHelper.createManagerMBeanName("IndexManager");
		  Object obj = jmxService.invokeOperation(oname, "describeIndex", indexName.trim());
		  
		  List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		  if(obj instanceof List)
		  {
		      list = (List)obj;
		  }
		  else
		  {
		      HashMap<String,Object> o = (HashMap<String,Object>)obj;
		      list.add(o);
		  }
		  
		  //按ip信息排序
		  Collections.sort(list, new Comparator<HashMap<String,Object>>()
          {
		      public int compare(HashMap<String,Object> o1,HashMap<String,Object> o2)
		      {
		          String ip1 = (String)o1.get(Indexs.ip.name());
		          String ip2 = (String)o2.get(Indexs.ip.name());
		          return ip1.compareTo(ip2);
		      }
         });
		
		//为ws传递数据
		put(CommandMeta.DESCRIBE_INDEX,list);
		  
		//对所有点的数据进行输出
		for(HashMap<String,Object> o:list)
		{
		    StringBuffer sb = new StringBuffer();
            sb.append(o.get(Indexs.ipinfo.name()) + "\n");
            sb.append("-------------------------------\n");
            sb.append("Index Name: " + o.get(Indexs.indexName.name()) + "\n");
            sb.append("Region Name: " + o.get(Indexs.regionName.name()) + "\n");
            sb.append("Region Type: " + o.get(Indexs.regionType.name()) + "\n");
            sb.append("Index Entry Size: " + o.get(Indexs.entrySize.name()) + "\n");
            sb.append("Index Key Fields: \n");
            int pos = 1;
            Map<String,String> keyFields = (Map<String,String>)o.get(Indexs.keyFields.name());
            for(Iterator<String> it=keyFields.keySet().iterator(); it.hasNext();)
            {
                String field = it.next();
                String type = keyFields.get(field);
                sb.append(pos + ": " + field + "(" + type + ")\n");
                pos++;
            }
            
            pos = 1;
            sb.append("Index Value Fields: \n");
            Map<String,String> valueFields = (Map<String,String>)o.get(Indexs.valueFields.name());
            for(Iterator<String> it=valueFields.keySet().iterator(); it.hasNext();)
            {
                String field = it.next();
                String type = keyFields.get(field);
                sb.append(pos + ": " + field + "(" + type + ")\n");
                pos++;
            }
            result+=sb.toString();
		}
	  }
	  else
	  {
		  if(!GemliteStatus.isConnected())
			  return "Please start Gemlite Client first";
		  else
			  result = indexService.describeIndex(indexName.trim());
	  }
	  return result;
  }
  
  @CliCommand(value = "query index", help = "query index --name --param")
  public String queryIndex(
		  @CliOption(key="jmx", specifiedDefaultValue="true", unspecifiedDefaultValue="false", help="using jmx service") boolean jmx,
		  @CliOption(key = "name", mandatory=true, help="index name")String indexName,
		  @CliOption(key = {"parameter", "param"}, mandatory=true, help="search parameter")String param,
		  @CliOption(key = "pageNum", unspecifiedDefaultValue="1", help="page number")int pageNum,
		  @CliOption(key = "pageSize", unspecifiedDefaultValue="100", help="page size")int pageSize
		  )
  {
		if (pageNum <= 0 || pageSize <= 0)
			return "Page Number or Page Size should larger than 0.";

		String result = "";
		if (jmx)
		{
			MBeanServerConnection connection = jmxService.getConnection();
			if (connection == null)
				return "Please connect to the Gemlite Mangaer first.";

			String oname = MBeanHelper.createManagerMBeanName("IndexManager");
			Object obj = jmxService.invokeOperation(oname, "printIndexValue",
					indexName.trim(), param, pageNum, pageSize);
			List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

			if (obj==null || obj.toString().length() < 10)
			{
				list.add(new HashMap<String, Object>());
			}
			else
			{
				if (obj instanceof List)
				{
					list = (List)obj;
				} 
				else
				{
					HashMap<String, Object> o = (HashMap<String, Object>) obj;
					list.add(o);
				}
			}
			// 为ws传递数据
			put(CommandMeta.QUERY_INDEX, list);

			StringBuffer sb = new StringBuffer();
			// 对所有点的数据进行输出
			for (HashMap<String, Object> o : list)
			{
				sb.append(o.get("") + "\n");
				sb.append("MemberID: " + o.get("MemberID"));
				sb.append("\tIP: " + o.get("IP"));
				sb.append("\tEntrySize: " + o.get("EntrySize"));
				sb.append("\tPageNumber: " + o.get("PageNumber"));
				sb.append("\tPageSize: " + o.get("PageSize") + "\n");
				List<Object> valueList = (List<Object>) o.get("EntryValue");
				if (valueList != null)
				{
					for (Object value : valueList)
					{
						sb.append(value.toString() + "\n");
					}
				}
			}
			result = sb.toString();

		} else
		{
			if (!GemliteStatus.isConnected())
				return "Please start Gemlite Client first";
			else
				result = indexService.printIndexValue(indexName.trim(),
						param.trim(), pageNum, pageSize).toString();
		}
		return result;
  }
}
