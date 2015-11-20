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
package gemlite.core.internal.support.jpa.files.service;

import gemlite.core.internal.support.jpa.files.dao.GmDataLogDao;
import gemlite.core.internal.support.jpa.files.domain.GmDataLog;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DataLogService
{
  @Autowired
  private GmDataLogDao dao;
  
  public Page<GmDataLog> findAll(Pageable pageable)
  {
    return (Page<GmDataLog>)dao.findAll(pageable);
  }
  
  public GmDataLog findOne(Long id)
  {
    return dao.findOne(id);
  }
  
  public void saveData(GmDataLog data)
  {
    dao.save(data);
  }
}