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
package gemlite.core.internal.mq.processor;

import gemlite.core.internal.domain.DomainRegistry;
import gemlite.core.internal.domain.DomainUtil;
import gemlite.core.internal.domain.IDataSource;
import gemlite.core.internal.domain.IMapperTool;
import gemlite.core.internal.mq.MqConstant;
import gemlite.core.internal.mq.domain.MqSyncDomain;
import gemlite.core.internal.mq.domain.ParseredValue;
import gemlite.core.util.LogUtil;

public class SimpleMapperDao implements IMapperDao
{
  
  @SuppressWarnings("rawtypes")
  public MqSyncDomain mapperPV(ParseredValue pv)
  {
    if (pv == null)
    {
      LogUtil.getLogger().error("Sync Error:pv is null!");
      return null;
    }
    try
    {
      //获取表对应的mapper
      String regionName = DomainRegistry.tableToRegion(pv.getTableName());
      //regionNam为空，有可能是该table不需要处理
      if(regionName==null)
        return null;
      IMapperTool tool  = DomainRegistry.getMapperTool(regionName);
      IDataSource ds = DomainUtil.getDataSource(pv.getValueMap());
      Object key  =tool.mapperKey(ds);
      if (key != null)
      {
        MqSyncDomain d = new MqSyncDomain();
        d.setKey(key);
        d.setTableName(pv.getTableName());
        d.setRegionName(regionName);
        d.setTimestamp(pv.getTimestamp());
        d.setOp(pv.getOp());
        d.setValue(pv.getValueMap());
        if (MqConstant.UPDATE.equals(pv.getOp()))
        {
          Object oldKey = getWhereKey(pv);
          d.setOldKey(oldKey);
          d.setOp(MqConstant.UPDATE);
        }
        return d;
      }
    }
    catch (Throwable e)
    {
      LogUtil.getLogger().error("Sync Error:table is:" + pv.getTableName() + " Op is:" + pv.getOp(), e);
    }
    return null;
  }
  
  @SuppressWarnings("rawtypes")
  private Object getWhereKey(ParseredValue pv)
  {
    String regionName = DomainRegistry.tableToRegion(pv.getTableName());
    //regionNam为空，有可能是该table不需要处理
    if(regionName==null)
      return null;
    IMapperTool tool  = DomainRegistry.getMapperTool(regionName);
    IDataSource ds = DomainUtil.getDataSource(pv.getWhereMap());
    Object oldKey  = tool.mapperKey(ds);
    return oldKey;
  }
}
