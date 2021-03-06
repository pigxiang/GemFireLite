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

import gemlite.core.internal.support.GemliteException;
import gemlite.core.internal.support.jpa.files.dao.ActiveFileDao;
import gemlite.core.internal.support.jpa.files.dao.GmBatchDao;
import gemlite.core.internal.support.jpa.files.dao.GmConfigDao;
import gemlite.core.internal.support.jpa.files.domain.ConfigKeys;
import gemlite.core.internal.support.jpa.files.domain.ConfigTypes;
import gemlite.core.internal.support.jpa.files.domain.GmBatch;
import gemlite.core.internal.support.jpa.files.domain.GmConfig;
import gemlite.core.util.LogUtil;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("rawtypes")
@Service
public class BatchService
{
    @Autowired
    private GmBatchDao batchDao;
    
    @Autowired
    private GmConfigDao configDao;

    @Autowired
    private ActiveFileDao daoActive;

    public List<GmBatch> findAll()
    {
        return (List<GmBatch>) batchDao.findAll();
    }

    public GmBatch findOne(Long id)
    {
        return batchDao.findOne(id);
    }

    public void saveJob(GmBatch vo)
    {
        batchDao.save(vo);
    }

    public GmBatch findByRegionName(String region)
    {
        GmBatch batch = new GmBatch();
        batch.setRegion(region);
        GmBatch job = batchDao.getByRegion(region);
        return job;
    }

    public void deleteJob(Long id)
    {
        batchDao.delete(id);
    }

    public int countJobExecutions()
    {
        return batchDao.countJobExecutions();
    }

    public Collection<Map> listJobExecutions(String status)
    {
        return batchDao.queryJobExecutions(status);
    }

    public int countRunningJob()
    {
        return batchDao.countRunningJob();
    }

    public Map getJobExecution(Long executionId)
    {
        return batchDao.queryJobExecutionById(executionId);
    }

    public List<Map> getStepNamesForJob(String jobName)
    {
        return batchDao.queryStepNamesForJob(jobName);
    }

    public List<Map> getStepExecutions(Long jobExecutionId)
    {
        return batchDao.queryStepExecutionsById(jobExecutionId);
    }
    
    
    public boolean saveJob(String template, String file, String delimiter, String quote, boolean skipable, String columns, String region, String table,
            String encoding, int linesToSkip,String dbdriver,String dburl,String dbuser,String dbpsw,String sortKey,String where,int pageSize,int fetchSize,boolean forceUpdate)
    {
        try
        {
            //生成cmd
            StringBuilder sb = new StringBuilder();
            sb.append("import");
            sb.append(" --columns ").append(columns);
            sb.append(" --region ").append(region);
            sb.append(" --table ").append(table);
            sb.append(" --template ").append(template);
            if(skipable)
            sb.append(" --skipable ").append(skipable);
            sb.append(" --encoding ").append(encoding);
            if(forceUpdate)
            sb.append(" --update ").append(forceUpdate);
            if(StringUtils.equals(template, "file"))
            {
                sb.append(" --file ").append(file);
                sb.append(" --delimiter ").append(delimiter);
                sb.append(" --quote ").append(quote);
                sb.append(" --linesToSkip ").append(linesToSkip);
            }
            else
            {
                sb.append(" --dbdriver ").append(dbdriver);
                sb.append(" --dburl ").append(dburl);
                sb.append(" --dbuser ").append(dbuser);
                sb.append(" --dbpsw ").append(dbpsw);
                sb.append(" --sortKey ").append(sortKey);
                if(StringUtils.isNotEmpty(where))
                sb.append(" --where ").append(where);
                sb.append(" --pageSize ").append(pageSize);
                sb.append(" --fetchSize ").append(fetchSize);
            }
            
            //根据region,template查找,如果找到则更新数据,否则新增数据
            GmBatch oldBatch = batchDao.getByRegionAndTemplate(region, template);
            if(oldBatch!=null)
            {
                oldBatch.setUpdate_time(new Date());
                oldBatch.setCmd(sb.toString());
                batchDao.save(oldBatch);
                return true;
            }
            
            //如果上面没有查到一样的cmd,则新增
            GmBatch batch = new GmBatch();
            batch.setRegion(region);
            batch.setTemplate(template);
            batch.setCmd(sb.toString());
            batch.setCreate_time(new Date());
            batchDao.save(batch);
            return true;
        }
        catch(Exception e)
        {
            LogUtil.getCoreLog().info("Job define error.",e);
            throw new GemliteException(e);
        }
    }
}
