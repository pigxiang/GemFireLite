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
package gemlite.core.internal.measurement.index;

import gemlite.core.annotations.mbean.GemliteMBean;
import gemlite.core.internal.support.jmx.AggregateType;
import gemlite.core.internal.support.jmx.annotation.AggregateAttribute;

import java.io.Serializable;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@GemliteMBean(name="ReplicateRegionIndex")
@ManagedResource(description="Replicate Region Index Stat")
public class IndexReplicateStat extends AbstractIndexStat implements Serializable
{
	private static final long serialVersionUID = -1415119793358782712L;

	@AggregateAttribute(AggregateType.SAME)
	@ManagedAttribute(description="Index Entry Size")
	public long getEntrySize() 
	{
		return entrySize.get();
	}

	@Override
	public boolean isPartitionedRegion()
	{
		return false;
	}
}
