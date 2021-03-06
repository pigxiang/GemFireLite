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
import gemlite.core.internal.mq.MqConstant;
import gemlite.core.internal.mq.domain.ItemByKey;
import gemlite.core.internal.mq.domain.ItemByRegion;
import gemlite.core.internal.mq.domain.MqSyncDomain;
import gemlite.core.internal.mq.domain.ParseredValue;
import gemlite.core.util.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

public class MergeTask extends RecursiveAction
{
  private static final long serialVersionUID = -387976502005454041L;
  private List<ParseredValue> pvList;
  private IMapperDao dao;
  private int lo;
  private int hi;
  private int threshold = 10;
  private Map<Object, Object> originKeyMap;
  
  private Map<String, ItemByRegion> items;
  
  public MergeTask(List<ParseredValue> pvList, IMapperDao dao, int lo, int hi, Map<Object, Object> originKeyMap,
      Map<String, ItemByRegion> items)
  {
    super();
    this.pvList = pvList;
    this.dao = dao;
    this.lo = lo;
    this.hi = hi;
    this.originKeyMap = originKeyMap;
    this.items = items;
  }
  
  @Override
  protected void compute()
  {
    int left = hi - lo;
    if (LogUtil.getMqSyncLog().isDebugEnabled())
      LogUtil.getMqSyncLog().debug("Merge domain,threshold=" + threshold + "," + lo + "-" + hi + " left=" + left);
    if (left <= threshold)
    {
      
      for (int i = lo; i < hi; i++)
      {
        ParseredValue pv = pvList.get(i);
        MqSyncDomain md = pvToMqSyncDomain(pv);
        if (md == null)
          continue;
        
        ItemByRegion ir = items.get(md.getRegionName());
        if (ir == null)
        {
          ir = new ItemByRegion();
          ir.regionName = md.getRegionName();
          items.put(ir.regionName, ir);
        }
        
        Object pk = md.getKey();
        ItemByKey ik = ir.itemsByKey.get(pk);
        if (ik == null)
        {
          ik = new ItemByKey(md.getKey(), md.getOldKey(), md.getValue(), false, md.getOp());
        }
        if (MqConstant.INSERT.equals(md.getOp()))
        {
        	//TODO 若有相同key不同value的insert 语句，此处值会错误
        	if (!(md.getValue().equals(ik.getValues())))
        	{
        	  HashMap<String, String> values = ik.getValues();
              values.putAll(md.getValue());
              ik.setValues(values);
        	}
        	//如果同一个包中先delete,后insert,则需要修改ik中的operation
        	if (!ik.getOperation().equals(MqConstant.INSERT))
        		ik.setOperation(MqConstant.INSERT);
            ik.setNodeChange(true);
        }
        else if (MqConstant.UPDATE.equals(md.getOp()))
        {
          ItemByKey mergedIK = (ItemByKey) ir.itemsByKey.get(md.getOldKey());
          if (mergedIK != null)
          {
            // merge values
            HashMap<String, String> values = mergedIK.getValues();
            values.putAll(md.getValue());
            ik.setValues(values);
            // merge operation
            if (MqConstant.INSERT.equals(mergedIK.getOperation()))
              ik.setOperation(MqConstant.INSERT);
            if (md.isPkChanged())
            {
              // merge originKey
              ik.setOriginKey(mergedIK.getOriginKey());
              ik.setNodeChange(true);
              ir.itemsByKey.remove(mergedIK.getKey());
            }
          }
        }
        ir.itemsByKey.put(pk, ik);
      }
      if (LogUtil.getMqSyncLog().isDebugEnabled())
        LogUtil.getMqSyncLog().debug("Done. " + lo + "-" + hi + 1);
    }
    else
    {
      int mid = (lo + hi) / 2;
      if (LogUtil.getMqSyncLog().isDebugEnabled())
        LogUtil.getMqSyncLog().debug("Split," + lo + "-" + (mid-1) + " and " + (mid) + "-" + (hi-1));
      
      MergeTask t1 = new MergeTask(pvList, dao, lo, mid, originKeyMap, items);
      MergeTask t2 = new MergeTask(pvList, dao, mid, hi, originKeyMap, items);
      t1.invoke();
      t2.invoke();
      t1.join();
      //invokeAll(t1, t2);
    }
  }
  
  private MqSyncDomain pvToMqSyncDomain(ParseredValue pv)
  {
    MqSyncDomain md = dao.mapperPV(pv);
    if (md == null)
    {
      if (DomainRegistry.tableToRegion(pv.getTableName()) != null)
        LogUtil.getAppLog().error("md is null:" + pv.getTableName());
      // System.exit(-1);
      return null;
    }
    return md;
  }
}
