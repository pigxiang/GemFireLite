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
package gemlite.core.util;

import gemlite.core.common.DESUtil;
import gemlite.core.internal.support.context.JpaContext;
import gemlite.core.internal.support.jpa.files.domain.ConfigKeys;
import gemlite.core.internal.support.jpa.files.domain.ConfigTypes;
import gemlite.core.internal.support.jpa.files.service.ConfigService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class CmdUtils
{
    /**
     * �pn,{0} w� {1} ȹip {2} ȹ�U
     */
    public static final String scp = "scp -r {0} {1}:{2}&";
    
    /**
     * /�locator,{0}home�U,{1} /��p
     */
    public static final String start_locator = "{0}/bin/locator.sh {1}&";
    /**
     * /�datastore,{0}home�U,{1} /��p
     */
    public static final String start_datastore = "{0}/bin/run.sh {1}&";
    
    /**
     * /�monitor
     */
    public static final String start_monitor = "{0}/bin/gemlite_monitor.sh&";
    
    /**
     * ��/&X(monitor
     */
    public static final String grep_monitor = "ps aux|grep java|grep GemliteMonitor";
    
    /**
     * ��/&X(locator
     */
    public static final String grep_locator = "ps aux|grep java|grep Locator";
    
    /**
     * ��/&X(locator
     */
    public static final String grep_datastore = "ps aux|grep java|grep DataStore";
    
    

    public static String format(String pattern, Object argument)
    {
        return format(pattern, new Object[] { argument });
    }

    public static String format(String pattern, Object... arguments)
    {
        return MessageFormat.format(pattern, arguments);
    }
    
    /**
     * (;�
bgL}�
     * @param cmd
     * @return
     * @throws IOException
     * @throws Exception
     */
   public static String exeCmd(String cmd) throws IOException,Exception
   {
       //, e��ƤMn
       ConfigService service = JpaContext.getService(ConfigService.class);
       Map<String,String> map = service.getConfig(ConfigTypes.clusterconfig.getValue());
       //�0:hh�;�:h
       String cluster_primaryip = map.get(ConfigKeys.cluster_primaryip.getValue());
       String username = map.get(ConfigKeys.cluster_username.getValue());
       String password = map.get(ConfigKeys.cluster_userpsw.getValue());
       password = DESUtil.decrypt(password);
       Connection conn = new Connection(cluster_primaryip);
       Session sess = null;
       StringBuilder sb = new StringBuilder();
       try
       {
         conn.connect();
         boolean isAuthenticated = conn.authenticateWithPassword(username, password);
         if (isAuthenticated == false)
           throw new IOException("Authentication failed.");
         sess = conn.openSession();
         sess.execCommand(cmd);
         InputStream stdout = sess.getStdout();
         BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
         String line = br.readLine();
         if(!StringUtils.contains(cmd, "&"))
         while (true)
         {
           if (line == null)
             break;
           sb.append(line);
           line = br.readLine();
         }
       }
       catch (IOException e)
       {
         throw e;
       }
       catch(Exception e)
       {
           throw e;
       }
       finally
       {
         sess.close();
         conn.close();
       }
       return sb.toString();
   }
   
   /**
    * ޥ�*��;:,gL}�
    * @param ip
    * @param username
    * @param psw
    * @param cmd
    * @return
    * @throws IOException
    * @throws Exception
    */
   public static String exeCmd(String ip,String username,String psw,String cmd) throws IOException,Exception
   {
       psw = DESUtil.decrypt(psw);
       Connection conn = new Connection(ip);
       Session sess = null;
       StringBuilder sb = new StringBuilder();
       try
       {
         conn.connect();
         boolean isAuthenticated = conn.authenticateWithPassword(username, psw);
         if (isAuthenticated == false)
           throw new IOException("Authentication failed.");
         sess = conn.openSession();
         sess.execCommand(cmd);
         InputStream stdout = sess.getStdout();
         //&�gL�,M��Ӝ,&����
         BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
         String line = br.readLine();
         if(!StringUtils.contains(cmd, "&"))
         while (true)
         {
           if (line == null)
             break;
           sb.append(line);
           line = br.readLine();
         }
       }
       catch (IOException e)
       {
         throw e;
       }
       catch(Exception e)
       {
           throw e;
       }
       finally
       {
         sess.close();
         conn.close();
       }
       return sb.toString();
   }
}
