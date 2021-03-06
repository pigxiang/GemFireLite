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
package gemlite.shell.commands.admin;

import gemlite.shell.commands.AbstractCommand;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAdminCommand extends AbstractCommand
{
    /**
     * 用于记录某条执行的命令,及最新的结果Key:command名称,value数据
     */
    private Map<String, Object> command = new HashMap<String, Object>();

    public Object get(String key)
    {
        return command.get(key);
    }

    public void put(String key,Object value)
    {
        this.command.put(key, value);
    }
}
