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
import gemlite.core.internal.jmx.manage.KeyConstants.Views;
import gemlite.core.internal.support.jmx.MBeanHelper;
import gemlite.shell.commands.admin.AbstractAdminCommand;
import gemlite.shell.service.admin.JMXService;
import gemlite.shell.service.admin.ViewService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ViewCommand extends AbstractAdminCommand
{
	@Autowired
	private ViewService viewService;

	@Autowired
	private JMXService jmxService;

	@CliAvailabilityIndicator({ "list views", "drop view", "describe view",
			"refullcreate view", "create view" })
	public boolean onlineCommand()
	{
		return true;
	}

	// @CliCommand(value = "list views", help = "list views")
	// public String list()
	// {
	// String result = viewService.listViews();
	// return result;
	// }

	@SuppressWarnings("unchecked")
	@CliCommand(value = "list views", help = "list views --region")
	public String list(
			@CliOption(key = "jmx", specifiedDefaultValue = "true", unspecifiedDefaultValue = "false", help = "using jmx service") boolean jmx,
			@CliOption(key = "region", mandatory = false, help = "regi on name") String regionName)
	{
		String result = "";
		if (jmx)
		{
			MBeanServerConnection connection = jmxService.getConnection();
			if (connection == null)
				return "Please connect to the Gemlite Mangaer first.";

			String oname = MBeanHelper.createManagerMBeanName("ViewManager");
			Object obj = null;
			if (StringUtils.isEmpty(regionName))
				obj = jmxService.invokeOperation(oname, "listViews", "");
			else
				obj = jmxService.invokeOperation(oname, "listViews",
						regionName.trim());
			List<String> list = new ArrayList<String>();
			if (obj instanceof List)
			{
				// 取一个点的数据就行了
				list = (List<String>) obj;
			} else
				list = (List<String>) obj;

			// 为ws传递数据
			put(CommandMeta.LIST_VIEW, list);

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < list.size(); i++)
				sb.append((i + 1) + ": " + list.get(i) + "\n");
			return sb.toString();
		} else
		{
			List<String> list = viewService.listViews(regionName);
			StringBuffer sb = new StringBuffer();
			if (list != null && list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
					sb.append(list.get(i) + "\n");
			}

			result = sb.toString();
		}
		return result;
	}

	@CliCommand(value = "refullcreate view", help = "refullcreate view --name")
	public String refullcreate(
			@CliOption(key = "name", mandatory = true, help = "view name") String viewName)
	{
		String result = viewService.refullcreate(viewName);
		return result;
	}

	@CliCommand(value = "create view", help = "create view --class")
	public String create(
			@CliOption(key = "class", mandatory = true, help = "View Tool class name") String clsName)
	{
		String result = viewService.create(clsName);
		return result;
	}

	@CliCommand(value = "drop view", help = "drop index --name")
	public String drop(
			@CliOption(key = "name", mandatory = true, help = "view name") String viewName)
	{
		String result = viewService.dropView(viewName);
		return result;
	}

	@CliCommand(value = { "describe view" }, help = "describe view --name")
	public String describeView(
			@CliOption(key = "jmx", specifiedDefaultValue = "true", unspecifiedDefaultValue = "false", help = "using jmx service") boolean jmx,
			@CliOption(key = "name", mandatory = true, help = "view name") String viewName)
	{
		String result = "";
		if (jmx)
		{
			MBeanServerConnection connection = jmxService.getConnection();
			if (connection == null)
				return "Please connect to the Gemlite Mangaer first.";

			String oname = MBeanHelper.createManagerMBeanName("ViewManager");
			Object obj = jmxService.invokeOperation(oname, "describeView",
					viewName.trim());

			List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			if (obj instanceof List)
			{
				HashSet<HashMap<String, Object>> set = (HashSet<HashMap<String, Object>>) obj;
				list.addAll(set);
			} else
			{
				HashMap<String, Object> o = (HashMap<String, Object>) obj;
				list.add(o);
			}

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < list.size(); i++)
			{
				HashMap<String, Object> valueMap = list.get(i);

				sb.append(valueMap.get(Indexs.ipinfo.name()) + "\n");
				sb.append("-------------------------------\n");
				sb.append("View Name: " + valueMap.get(Views.viewName.name())
						+ "\n");
				sb.append("View Type: " + valueMap.get(Views.viewType.name())
						+ "\n");
				sb.append("Trigger On: " + valueMap.get(Views.triggerOn.name())
						+ "\n");
				sb.append("Target Name: "
						+ valueMap.get(Views.targetName.name()) + "\n");
				sb.append("Trigger Type: "
						+ valueMap.get(Views.triggerType.name()) + "\n");
				sb.append("Entry time to live: "
						+ valueMap.get(Views.EntryTimeToLive.name()) + "\n");
				sb.append("View Entry Count: "
						+ valueMap.get(Views.entrySize.name()) + "\n");
			}

			// 按ip信息排序
			Collections.sort(list, new Comparator<HashMap<String, Object>>()
			{
				public int compare(HashMap<String, Object> o1,
						HashMap<String, Object> o2)
				{
					String ip1 = (String) o1.get(Indexs.ip.name());
					String ip2 = (String) o2.get(Indexs.ip.name());
					return ip1.compareTo(ip2);
				}
			});

			// 为ws传递数据
			put(CommandMeta.DESCRIBE_VIEW, list);

			return sb.toString();
		} else
		{
			List<String> list = viewService.describeView(viewName);
			StringBuffer sb = new StringBuffer();
			if (list != null && list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
					sb.append(list.get(i) + "\n");
			}
			;

			result = sb.toString();
		}
		return result;
	}

	private void fomartAndPutResultList(List<String> list, int type)
	{
		if (type == 1)
		{
			/*
			 * list view result MemberID: sunmy-nb<v1>:29629 IP: 192.168.120.180
			 * Port: 2683 ------------------------------- 1:
			 * UserSelectedBrandView
			 */
			List<String> resultList = new ArrayList();
			String[] strArry = list.toString().split("\n");
			for (int i = 0; i < strArry.length; i++)
			{
				String value = strArry[i].trim();
				if (!StringUtils.isEmpty(value))
				{
					if (value.startsWith("[MemberID")
							|| value.startsWith("--------")
							|| value.startsWith("]"))
						continue;
					if (value.indexOf(":") > 0)
						value = value.substring(value.indexOf(":") + 1).trim();
					resultList.add(value);
				}
			}
			put(CommandMeta.LIST_VIEW, resultList);
		} else
		{
			/*
			 * describe view detail MemberID: sunmy-nb<v1>:29629 IP:
			 * 192.168.120.180 Port: 2683 ------------------------------- View
			 * Name: UserSelectedBrandView View Type: LOCAL Trigger On: REGION
			 * Region Name: user_selected_brand Index Name: Trigger Type: REAL
			 * Entry time to live: 0 View Entry Count: 0 View Tool:
			 * gemlite.cmcc.view.UserSelectedBrandView
			 */
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			String[] strArry = list.toString().split("\n");
			Map<String, String> valueMap = new HashMap<String, String>();
			for (int i = 0; i < strArry.length; i++)
			{
				String value = strArry[i].trim();
				if (!StringUtils.isEmpty(value))
				{
					if (value.startsWith("[MemberID")
							|| value.startsWith("--------")
							|| value.startsWith("]"))
						continue;
					if (value.indexOf(":") > 0)
					{
						String key = value.substring(0, value.indexOf(":"))
								.trim();
						String keyValue = value.substring(
								value.indexOf(":") + 1).trim();
						if (!StringUtils.isEmpty(key))
						{
							valueMap.put(key, keyValue);
						}
					}
				}
			}
			resultList.add(valueMap);
			put(CommandMeta.DESCRIBE_VIEW, resultList);
		}

	}
}
