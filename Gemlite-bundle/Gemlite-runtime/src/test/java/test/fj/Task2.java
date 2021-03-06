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
package test.fj;

import gemlite.core.internal.mq.domain.MqSyncDomain;
import gemlite.core.internal.mq.domain.ParseredValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

public class Task2 extends RecursiveAction
{
  public List<ParseredValue> values;
  
  /**
   * domains by region name
   * order by timestamp
   * old key and new key all in one
   */
  public Map<Object,List<MqSyncDomain>> domainByRegion;
  @Override
  protected void compute()
  {
    //for values
    //pvToDomain
    //group by region
    //sort by timestamp
    //if pkChanged
    //originPK->pk1->pk2
    //
    for(ParseredValue pv:values)
    {
      MqSyncDomain d = null;
    }
    
  }
  
}
