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
package gemlite.shell.converters;

import gemlite.core.internal.support.context.JpaContext;
import gemlite.core.internal.support.jpa.files.service.BatchService;
import gemlite.core.util.LogUtil;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

@Component
public class JobIdConverter implements Converter<String>
{
    @Override
    public boolean supports(Class<?> type, String optionContext)
    {
        if(LogUtil.getCoreLog().isDebugEnabled())
            LogUtil.getCoreLog().debug("JobIdConverter->supports?param.context.job.id=>"+optionContext);
        return (String.class.equals(type)) && (StringUtils.contains(optionContext, "param.context.job.id"));
    }

    @Override
    public String convertFromText(String value, Class<?> targetType, String optionContext)
    {
        if(LogUtil.getCoreLog().isDebugEnabled())
            LogUtil.getCoreLog().debug("JobIdConverter->convertFromText"+value);
        if ((String.class.equals(targetType)) && (StringUtils.contains(optionContext, "param.context.job.id")))
        {
            //判断输入值是不是可以直接转化成Long,如果不是,则需要解析
            String arr[] = value.split("_");
            if(arr.length==3)
            {
                String idstr = arr[1];
                return idstr;
            }
            //直接输入数字形的
            else if(arr.length==1)
            {
                return value;
            }
            else 
            {
                LogUtil.getCoreLog().error("error input :"+value+" must Long or get by TAB completion");
                return "0";
            }
        }
        else
        {
            return  value;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData, String optionContext, MethodTarget target)
    {
        if ((String.class.equals(targetType)) && (StringUtils.contains(optionContext, "param.context.job.id")))
        {
            BatchService batchService = JpaContext.getService(BatchService.class);
            Collection<Map> jobs = batchService.listJobExecutions("");
            for (Map job : jobs)
            {
                Long id = Long.parseLong(job.get("job_execution_id").toString());
                String jobName = (String)job.get("job_name");
                Timestamp startTime = (Timestamp)job.get("start_time");
                String str = jobName+ "_" +id+ "_"+startTime;
                if (existingData != null)
                {
                    if (str.startsWith(existingData))
                    {
                        completions.add(new Completion(str));
                    }
                }
                else
                {
                    completions.add(new Completion(str));
                }
            }
        }
        if(LogUtil.getCoreLog().isDebugEnabled())
            LogUtil.getCoreLog().debug("JobIdConverter->getAllPossibleValues:"+completions.size());
        return !completions.isEmpty();
    }
}
