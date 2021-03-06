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
package shell;
//
//import gemlite.core.api.SimpleClient;
//import gemlite.core.api.logic.LogicServices;
//import gemlite.core.api.logic.RemoteResult;
//import gemlite.sample.order.domain.Customer;
//import gemlite.sample.order.domain.CustomerDBKey;
//import gemlite.testing.data.CustomerTestData;
//import gemlite.testing.data.TestDataImpl;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.lang.StringUtils;
//import org.kohsuke.args4j.CmdLineException;
//import org.kohsuke.args4j.CmdLineParser;
//import org.kohsuke.args4j.Option;
//
//public class TestDbSync
//{
//	@Option(name = "-name")
//	private String tableName;
//
//	public static void main(String[] args)
//	{
//		TestDbSync dbSync = new TestDbSync();
//		CmdLineParser parser = new CmdLineParser(dbSync);
//		try
//		{
//			parser.parseArgument(args);
//		} catch (CmdLineException e)
//		{
//			e.printStackTrace();
//		}
//		dbSync.run();
//
//	}
//
//	private void run()
//	{
//		SimpleClient.connect();
//
//		if (StringUtils.isBlank(tableName))
//			tableName = "customer";
//		if (tableName.toLowerCase().equals("customer"))
//		{
//			customerSyncTest();
//		}
//
//		SimpleClient.disconnect();
//	}
//
//	/**
//	 * customerK�7�
//	 * 
//	 */
//	private void customerSyncTest()
//	{
//		// �epn0pn�
//		TestDataImpl<Customer> testData = new CustomerTestData();
//		testData.CreateAndInsertData();
//		List<Customer> dataList = testData.getDataList();
//		System.out
//				.println("Data begin to send from database to gemfire server...");
//		try
//		{
//			//I��\
//			//1I�Goldgengateبpn�sql��0Rabbitmq 
//			//2MqClient�,��sqlv�e0Gemfire node
//			Thread.sleep(5 * 1000);
//		} catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
//		System.out.println("Data begin to compare...");
//		// ��Lpnԃ�K�pn:�@Gemfire-�regionpn�L��
//		List<String> errList = this.compareData(dataList);
//		if (errList.size() == 0)
//		{
//			System.out.println("Data compare finished. [pn��eK�pnGemfire�hpn�h �]");
//		} else
//		{
//			for (String errInfo : errList)
//			{
//				System.out.println(errInfo);
//			}
//		}
//
//	}
//
//	/**
//	 * �;.��Customer region-�pn
//	 * 
//	 * @param name (7
//	 * @param id id_num
//	 */
//	private Customer queryByCustomerPK(String name, String id)
//	{
//		CustomerDBKey key = new CustomerDBKey();
//		key.setName(name);
//		key.setId_num(id);
//		RemoteResult rr = LogicServices.createRequestWithFilter("customer",
//				"QueryByCustomerDBKey", key, key);
//		Customer customer = rr.getResult(Customer.class);
//
//		return customer;
//	}
//
//	/**
//	 * pn��
//	 * 
//	 * @param dataList ��K�pn
//	 * @return ԃӜ
//	 */
//	private List<String> compareData(List<Customer> dataList)
//	{
//		List<String> errList = new ArrayList<String>();
//		for (Customer customer : dataList)
//		{
//			Customer regionCust = this.queryByCustomerPK(customer.getName(),
//					customer.getId_num());
//			if (regionCust != null)
//			{
//				if (!regionCust.toString().equals(customer.toString()))
//				{
//					errList.add("Compare failed, database -->["
//							+ customer.toString() + "], region-->["
//							+ regionCust.toString() + "]");
//				}
//			} else
//			{
//				errList.add("Compare failed, database -->["
//						+ customer.toString() + "], region-->null]");
//			}
//		}
//
//		return errList;
//	}
//
//}
