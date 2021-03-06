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
package gemlite.core.internal.mq.sender;

import gemlite.core.internal.mq.domain.MqSyncDomain;
import gemlite.core.internal.mq.server.LocalCacheSyncFunction;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;

@SuppressWarnings({"rawtypes"})
public class FileTestingSender implements IDomainSender
{
	@Override
	public Boolean doSend(Object obj)
	{
		MqSyncDomain md = (MqSyncDomain)obj;
		Cache cache = CacheFactory.getAnyInstance();
		Region region = cache.getRegion(md.getRegionName());
		Execution ex = FunctionService.onRegion(region);
		ex.withArgs(md).execute(new LocalCacheSyncFunction().getId());
		return true;
	}
}
