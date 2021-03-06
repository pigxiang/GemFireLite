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
package gemlite.core.internal.batch;


import gemlite.core.internal.domain.DomainRegistry;
import gemlite.core.internal.domain.IMapperTool;
import gemlite.core.internal.support.GemliteException;
import gemlite.core.util.LogUtil;

import java.util.HashMap;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;

/***
 * 取得一批数据
 * 
 */
@SuppressWarnings("rawtypes")
public class GFItemDataWriter implements ItemWriter<Object>, InitializingBean
{
  
  private ClientCache clientCache;
  private String regionName;
  
  @SuppressWarnings("unchecked")
  public void write(List<? extends Object> items) throws Exception
  {
    try
    {
      Region region = getClientCache().getRegion(regionName);
      HashMap<Object, Object> map = new HashMap<Object, Object>();
      IMapperTool tool  = DomainRegistry.getMapperTool(regionName);
      LogUtil logUtil = LogUtil.newInstance();
      LogUtil.getLogger().info("Item write to gemfire :" + items.size() + " region:" + regionName + " " + Thread.currentThread().getName());
      for (Object value : items)
      {
        Object key = tool.value2Key(value);
        map.put(key, value);
      }
      region.putAll(map);
      
      LogUtil.getLogger().info(
          "GFItemDataWriter size:" + map.size() + " region:" + regionName + " cost:" + logUtil.cost() + " "
              + Thread.currentThread().getName());
    }
    catch (Exception e)
    {
      if(LogUtil.getLogger().isErrorEnabled())
        LogUtil.getCoreLog().error("Region:"+regionName,e);
      throw new GemliteException(e);
    }
   
  }
  
  public ClientCache getClientCache()
  {
    if(clientCache==null)
      clientCache = ClientCacheFactory.getAnyInstance();
    return clientCache;
  }
  
  
  @Override
  public void afterPropertiesSet() throws Exception
  {
  }
  
  public String getRegionName()
  {
    return regionName;
  }
  
  public void setRegionName(String regionName)
  {
    this.regionName = regionName;
  }
  
}
