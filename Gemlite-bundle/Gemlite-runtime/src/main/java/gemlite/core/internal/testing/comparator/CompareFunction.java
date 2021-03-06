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
package gemlite.core.internal.testing.comparator;

import gemlite.core.api.index.IndexEntrySet;
import gemlite.core.internal.index.BenchmarkLocalCache;
import gemlite.core.internal.index.ITestIndexTool;
import gemlite.core.internal.index.IndexTool;
import gemlite.core.internal.support.annotations.IndexRegion;
import gemlite.core.internal.support.context.GemliteContext;
import gemlite.core.internal.support.context.GemliteIndexContext;
import gemlite.core.internal.support.context.IIndexContext;
import gemlite.core.internal.support.system.ServerConfigHelper;
import gemlite.core.internal.support.system.ServerConfigHelper.ITEMS;
import gemlite.core.internal.testing.generator.writer.impl.ResultWriter;
import gemlite.core.internal.testing.generator.writer.impl.SqlWriter;
import gemlite.core.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;

public class CompareFunction implements Function
{
	private static final long serialVersionUID = -6558228940129431710L;

	@Override
	public boolean hasResult()
	{
		return true;
	}

	@Override
	public void execute(FunctionContext functioncontext)
	{
		String regionName = (String) functioncontext.getArguments();
		boolean flag1 = regionCompare(regionName);
		boolean flag2 = indexCompare(regionName);
		Boolean flag = new Boolean(flag1 && flag2);

		StringBuffer result = new StringBuffer();
		result.append("[").append(getHostIPAndName()).append("]  Region[")
				.append(regionName).append("]")
				.append(" local data and region data is ");
		if (flag)
		{
			result.append(" matched.");
		} else
		{
			result.append(" unmatched.");
		}
		functioncontext.getResultSender().lastResult(result.toString());
	}

	@Override
	public String getId()
	{
		return this.getClass().getName();
	}

	@Override
	public boolean optimizeForWrite()
	{
		return false;
	}

	@Override
	public boolean isHA()
	{
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean indexCompare(String regionName)
	{
		boolean result = true;
		GemliteIndexContext idc = GemliteContext.getTopIndexContext();
		Set<IndexRegion> indexSet = idc.getIndexNamesByRegion(regionName);
		if (indexSet == null)
			return result;
		
		for (IndexRegion index : indexSet)
		{
			String indexName = index.indexName();
			IIndexContext context = idc.getIndexContext(indexName);
			if (context.isRegion())
				continue;

			ResultWriter writer = new ResultWriter(regionName, indexName, true);

			IndexTool tool = (IndexTool) context.getIndexInstance();
			IndexEntrySet indexData = tool.getIndex();

			ITestIndexTool testTool = (ITestIndexTool) context.getIndexInstance(true);
			try
			{
				Map<?, ?> testIndexData = testTool.getCachedMap();
				for (Iterator indexIt = indexData.getKeyIterator(); indexIt.hasNext();)
				{
					Object key = indexIt.next();
					Object value = indexData.getValue(key);
					Object testValue = testIndexData.get(key);
					if (testValue != null)
					{
						if (value != null && !value.equals(testValue))
						{
							String errInfo = "region data-->" + value.toString() + " , " + "local data-->" + testValue.toString();
							writer.write(errInfo);
							result = false;
						}
					} else
					{
						if (value != null)
						{
							String errInfo = "region data-->" + value.toString() + " , " + "local data-->null";
							writer.write(errInfo);
							result = false;
						}

					}
				}
				if (result)
					System.out.println("Region [" + regionName + "], index ["
							+ indexName
							+ "]索引数据已比较完毕，region index与local index数据完全一致.");
				else{
					System.out.println("Region [" + regionName + "], index ["
							+ indexName
							+ "]索引数据已比较完毕，region index与local index数据不匹配.");
				}

			} catch (Exception e)
			{
				LogUtil.getAppLog().error("Compare index failed.", e);
				e.printStackTrace();
			}

			writer.close();
		}

		return result;
	}

	private boolean regionCompare(String regionName)
	{
		boolean result = true;
		ResultWriter writer = new ResultWriter(regionName, null, false);
		// Gemfire Cache
		Cache c = CacheFactory.getAnyInstance();
		Region r = c.getRegion(regionName);
		Map<?, ?> cachedMap = BenchmarkLocalCache.getLocalCache(regionName);

		saveLocalData(regionName, cachedMap);
		saveRegionData(r, regionName);

		if (PartitionRegionHelper.isPartitionedRegion(r))
		{
			r = PartitionRegionHelper.getLocalData(r);
		}

		for (Iterator it = r.keySet().iterator(); it.hasNext();)
		{
			Object key = it.next();
			Object regionValue = r.get(key);
			Object cacheValue = cachedMap.get(key);
			if (regionValue != null && cacheValue != null && !regionValue.equals(cacheValue))
			{
				String errInfo = "region data-->" + regionValue.toString()
						+ " , " + "local data-->" + cacheValue.toString();
				writer.write(errInfo);
				result = false;
			}else if (regionValue != null && cacheValue == null)
			{
				String errInfo = "region data-->" + regionValue.toString() + " , " + "local data-->null";
				writer.write(errInfo);
				result = false;
			}
		}

		writer.close();
		if (result)
			System.out.println("Region [" + regionName
					+ "]已比较完毕，region数据与local数据完全一致.");
		else{
			System.out.println("Region [" + regionName
					+ "]已比较完毕，region数据与local数据不匹配.");	
		}

		return result;

	}

	private String getHostIPAndName()
	{
		String memberId = ServerConfigHelper.getProperty("MEMBER_ID");
		String ip = ServerConfigHelper.getConfig(ITEMS.BINDIP);
		String serverName = ServerConfigHelper.getConfig(ITEMS.NODE_NAME);
		String port = ServerConfigHelper.getProperty("JMX_RMI_PORT");
		return serverName + "/" + ip + "(" + memberId + "/" + port + ")";
	}

	private void saveLocalData(String regionName, Map<?, ?> cachedMap)
	{
		SqlWriter writer = new SqlWriter(regionName + "-local");
		if (cachedMap != null && cachedMap.entrySet().size() > 0)
		{
			saveLocalSortData(writer, cachedMap);
			/*
			 * Iterator<?> iters = cachedMap.entrySet().iterator(); while
			 * (iters.hasNext()) { Entry<?, ?> entry = (Entry<?, ?>)
			 * iters.next(); if (entry != null) {
			 * writer.write(entry.getValue().toString()); } }
			 */
		}
		writer.close();
	}

	@SuppressWarnings("unchecked")
	private void saveRegionData(Region region, String regionName)
	{
		SqlWriter writer = new SqlWriter(regionName + "-region");

		if (PartitionRegionHelper.isPartitionedRegion(region))
		{
			region = PartitionRegionHelper.getLocalData(region);
		}
		saveRegionSortData(writer, region);
		/*
		 * if (region != null && region.entrySet().size() > 0) { Iterator<?>
		 * iters = region.entrySet().iterator(); while (iters.hasNext()) {
		 * Entry<?, ?> entry = (Entry<?, ?>) iters.next(); if (entry != null) {
		 * writer.write(entry.getValue().toString()); } } }
		 */
		writer.close();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void saveLocalSortData(SqlWriter writer, Map<?, ?> dataMap)
	{
		if (dataMap == null)
			return;

		List<?> dataList = new ArrayList(dataMap.entrySet());

		Collections.sort(dataList, new Comparator()
		{
			@SuppressWarnings("rawtypes")
			public int compare(Object arg1, Object arg2)
			{
				Map.Entry obj1 = (Map.Entry) arg1;
				Map.Entry obj2 = (Map.Entry) arg2;
				return (obj1.getKey()).toString().compareTo(
						obj2.getKey().toString());
			}
		});

		for (Iterator<?> iter = dataList.iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			if (key != null)
			{
				Object value = dataMap.get(key);
				writer.write(value.toString());
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void saveRegionSortData(SqlWriter writer, Region region)
	{
		if (region == null)
			return;

		List<?> dataList = new ArrayList(region.entrySet());

		Collections.sort(dataList, new Comparator()
		{
			@SuppressWarnings("rawtypes")
			public int compare(Object arg1, Object arg2)
			{
				Map.Entry obj1 = (Map.Entry) arg1;
				Map.Entry obj2 = (Map.Entry) arg2;
				return (obj1.getKey()).toString().compareTo(
						obj2.getKey().toString());
			}
		});

		for (Iterator<?> iter = dataList.iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			if (key != null)
			{
				Object value = region.get(key);
				writer.write(value.toString());
			}

		}
	}
}
