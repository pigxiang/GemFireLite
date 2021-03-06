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
package gemlite.shell.admin.dao;

import gemlite.core.util.FunctionUtil;
import gemlite.core.util.LogUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.distributed.DistributedMember;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AdminDao
{
  private Pool clientPool;
  private static final String primary = "Primary Buckets - ";
  private static final String redundant = "Redundant Buckets - ";
  
  public String doReblance()
  {
    Map param = new HashMap();
    param.put("beanName", "RebalanceService");
    Map args = new HashMap();
    param.put("userArgs", args);
    Execution execution = FunctionService.onServer(clientPool).withArgs(param);
    ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
    Object result = rc.getResult();
    return result.toString();
  }
  
  public String doClean(String regionName)
  {
    Map param = new HashMap();
    param.put("beanName", "CleanService");
    Map args = new HashMap();
    args.put("REGIONPATH", regionName);
    param.put("userArgs", args);
    Object obj = FunctionService.onServers(clientPool).withArgs(param).execute("REMOTE_ADMIN_FUNCTION").getResult();
    LogUtil.getCoreLog().info(obj.toString());
    args.put("REGIONPATH", regionName);
    args.put("COMMAND_CANCEL", true);
    param.put("userArgs", args);
    obj = FunctionService.onServers(clientPool).withArgs(param).execute("REMOTE_ADMIN_FUNCTION").getResult();
    return obj.toString();
  }
  
  /**
   * level
   * 
   * @param regionName
   */
  public void setLevel(String level)
  {
    try
    {
      Map param = new HashMap();
      param.put("beanName", "ConfService");
      Map args = new HashMap();
      args.put("CACHE_LOGLEVEL", level);
      param.put("userArgs", args);
      Execution execution = FunctionService.onServers(clientPool).withArgs(param);
      ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
      Object obj = rc.getResult();
      if (obj != null)
      {
        ArrayList list = (ArrayList) obj;
        StringBuilder sb = new StringBuilder();
        for (Object o : list)
        {
          sb.append(o.toString() + "\n");
        }
        System.out.println(sb.toString());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  /**
   * 查看配置信息,只看一台server的
   * 
   * @param conf
   */
  public void lookConf()
  {
    try
    {
      FileWriter fw = new FileWriter("conf.info");
      BufferedWriter bw = new BufferedWriter(fw);
      PrintWriter pw = new PrintWriter(bw);
      Map param = new HashMap();
      
      param.put("beanName", "ConfService");
      Map args = new HashMap();
      param.put("userArgs", args);
      Execution execution = FunctionService.onServers(clientPool).withArgs(param);
      ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
      Object result = rc.getResult();
      System.out.println(result);
      pw.write(result.toString());
      pw.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void processRegion()
  {
    Map param = new HashMap();
    
    param.put("beanName", "DeserializeService");
    Map args = new HashMap();
    args.put("REGIONPATH", "ALL");
    param.put("userArgs", args);
    Execution execution = FunctionService.onServers(clientPool).withArgs(param)
        .withCollector(new PrintResultCollector());
    ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
    rc.getResult();
  }
  
  public void processReadRegion(ClientCache clientCache, String rr)
  {
    try
    {
      FileWriter writer = new FileWriter(rr, true);
      Region region = clientCache.getRegion(rr);
      Set set = region.keySetOnServer();
      HashMap map = (HashMap) region.getAll(set);
      Iterator it = map.entrySet().iterator();
      while (it.hasNext())
      {
        writer.write(it.next().toString() + "\n");
      }
      writer.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  /**
   * 刷新log4j配置
   */
  public void refreshLog4j()
  {
    Map param = new HashMap();
    
    param.put("beanName", "Log4jService");
    Map args = new HashMap();
    param.put("userArgs", args);
    Execution execution = FunctionService.onServers(clientPool).withArgs(param);
    ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
    Object obj = rc.getResult();
    if (obj != null)
    {
      ArrayList list = (ArrayList) obj;
      StringBuilder sb = new StringBuilder();
      for (Object o : list)
      {
        sb.append(o.toString() + "\n");
      }
      System.out.println(sb.toString());
    }
  }
  
  public void listmissingdiskstores()
  {
    Map param = new HashMap();
    
    param.put("beanName", "ListMissingDiskStoresService");
    Map args = new HashMap();
    param.put("userArgs", args);
    Execution execution = FunctionService.onServer(clientPool).withArgs(param);
    ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
    Object obj = rc.getResult();
    if (obj != null)
    {
      ArrayList list = (ArrayList) obj;
      StringBuilder sb = new StringBuilder();
      for (Object o : list)
      {
        sb.append(o.toString() + "\n");
      }
      System.out.println(sb.toString());
    }
  }
  
  public List<HashMap<String,Object>> sizeM(String regionName)
  {
    List<HashMap<String,Object>>  rs = new ArrayList<HashMap<String,Object>>();
    Map param = new HashMap();
    param.put("moduleName", "Runtime");
    param.put("beanName", "SizemService");
    Map args = new HashMap();
    args.put("REGIONPATH", regionName);
    param.put("userArgs", args);
    Execution execution = FunctionService.onServers(clientPool).withArgs(param);
    FunctionUtil.onServer("REMOTE_ADMIN_FUNCTION", null);
    ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
    Object obj = rc.getResult();
    HashMap<String, Object> resultMap = new HashMap<String, Object>();
    if (obj != null)
    {
      ArrayList list = (ArrayList) obj;
      int total = 0;
      
      boolean isPR = false;
      for (int i = 0; i < list.size(); i++)
      {
        resultMap = (HashMap<String, Object>) list.get(i);
        if (resultMap.get("isPR") != null)
          isPR = (Boolean) resultMap.get("isPR");
        if (resultMap == null || resultMap.get("code") == null || resultMap.get("code").equals("-1"))
        {
          continue;
        }
        HashMap<DistributedMember, Integer> memberMap = (HashMap<DistributedMember, Integer>) resultMap
            .get("memberMap");
        Iterator it = memberMap.entrySet().iterator();
        while (it.hasNext())
        {
          Entry<DistributedMember, Integer> entry = (Entry<DistributedMember, Integer>) it.next();
          HashMap<String,Object> map = new HashMap<String, Object>();
          map.put(entry.getKey().getId(), entry.getValue());
          rs.add(map);
          if (isPR)
            total += entry.getValue();
        }
      }
      if (isPR)
      {
          HashMap<String,Object> map = new HashMap<String, Object>();
          map.put("Total", total);
          rs.add(map);
      }
      return rs;
    }
    return null;
  }
  
  public String prB(String regionName)
  {
    Map param = new HashMap();
    
    param.put("beanName", "PrService");
    Map args = new HashMap();
    args.put("REGIONPATH", regionName);
    param.put("userArgs", args);
    Execution execution = FunctionService.onServers(clientPool).withArgs(param);
    ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
    ArrayList rs = (ArrayList) rc.getResult();
    StringBuilder sb = new StringBuilder();
    int pNum = 0, rNum = 0, tNum = 0;
    // 循环一遍历处理所有的点的ip+node字符串数据放进TreeSet
    TreeSet<String> ipNodeSet = new TreeSet<String>();
    TreeSet<String> ipSet = new TreeSet<String>(); // 记录独立ip
    HashMap<String, Set<String>> nodeMap = new HashMap<String, Set<String>>(); // 记录每个独立ip对应哪些node
    // 将数据放进HashMap,k:ip+node+主备信息 v:bucket数据,显示时按bucketId排序
    HashMap<String, HashMap<Integer, String>> data = new HashMap<String, HashMap<Integer, String>>();
    if (rs != null)
    {
      for (Object obj : rs)
      {
        ArrayList list = (ArrayList) obj;
        for (Object o : list)
        {
          if (!(o instanceof Map))
          {
            System.out.println(o.toString());
            continue;
          }
          HashMap map = (HashMap) o;
          // 单纯的主机信息,不带主备bucket信息
          String host = (String) map.get("host");
          String node = (String) map.get("node");
          Integer BucketId = (Integer) map.get("BucketId");
          if (!ipSet.contains(host))
            ipSet.add(host);
          Set<String> nodeSet = nodeMap.get(host);
          if (nodeSet == null)
          {
            nodeSet = new TreeSet<String>();
            nodeSet.add(node);
            nodeMap.put(host, nodeSet);
          }
          else
          {
            if (!nodeSet.contains(node))
              nodeSet.add(node);
          }
          String hostAndNode = host + node;
          String singleHostNode = hostAndNode;
          tNum = (Integer) map.get("TotalNumBuckets");
          ipNodeSet.add(hostAndNode);
          // 判断主备
          if ("primary".equals(map.get("type")))
          {
            singleHostNode = primary + singleHostNode;
            pNum++;
          }
          else
          {
            singleHostNode = redundant + singleHostNode;
            rNum++;
          }
          if (data.containsKey(singleHostNode))
          {
            HashMap<Integer, String> buckets = data.get(singleHostNode);
            buckets.put(BucketId, BucketId + "\t" + map.get("Bytes") + "\t" + map.get("Size"));
          }
          else
          {
            HashMap<Integer, String> buckets = new HashMap<Integer, String>();
            buckets.put(BucketId, BucketId + "\t" + map.get("Bytes") + "\t" + map.get("Size"));
            data.put(singleHostNode, buckets);
          }
        }
      }
    }
    
    // 开始解析,按ip主机排列,对ipset遍历
    Iterator<String> it = ipNodeSet.iterator();
    int i = 0;
    while (it.hasNext())
    {
      i++;
      String host = it.next();
      // 取主bucket数据
      // 输出一行,主机及主备信息
      String p = primary + host;
      sb.append(i + ". " + p).append("\n");
      sb.append(paraseSingleNode(data, p));
      // 取备bucket数据
      // 输出一行,主机及主备信息
      String r = redundant + host;
      sb.append(i + ". " + r).append("\n");
      sb.append(paraseSingleNode(data, r));
    }
    // 最后输出总结信息
    sb.append("Primary Bucket Count:" + pNum).append("\n");
    sb.append("Redundant Bucket Count:" + rNum).append("\n");
    sb.append("total-num-buckets (max):" + tNum).append("\n");
    
    // 检查集群中有主备bucket存在于同个机器的
    checkPr(ipSet, nodeMap, data, sb);
    return sb.toString();
    //System.out.println(sb.toString());
  }
  
  private String paraseSingleNode(HashMap<String, HashMap<Integer, String>> data, String singleHostNode)
  {
    StringBuilder sb = new StringBuilder();
    HashMap<Integer, String> buckets = data.get(singleHostNode);
    if (buckets != null)
    {
      Object rkeys[] = buckets.keySet().toArray();
      Arrays.sort(rkeys);
      // 输出表头
      sb.append("Row" + "\t" + "BucketId" + "\t" + "Bytes" + "\t" + "Size").append("\n");
      StringBuilder tmp = new StringBuilder();
      for (int j = 0; j < rkeys.length; j++)
      {
        Integer k = (Integer) rkeys[j];
        String bucket = buckets.get(k);
        tmp.append((j + 1) + "\t" + bucket + "\n");
      }
      sb.append(tmp.toString()).append("\n");
    }
    return sb.toString();
  }
  
  private void checkPr(TreeSet<String> ipSet, HashMap<String, Set<String>> nodeMap,
      HashMap<String, HashMap<Integer, String>> data, StringBuilder sb)
  {
    StringBuilder tmp = new StringBuilder();
    // 开始检验是否同一主机包含相同的主备bucket
    Iterator<String> ipIt = ipSet.iterator();
    while (ipIt.hasNext())
    {
      String host = ipIt.next();
      // 取此主机的所有主备bucket数据
      Iterator<String> pNodeIt = nodeMap.get(host).iterator();
      while (pNodeIt.hasNext())
      {
        String pnode = pNodeIt.next();
        String phostAndNode = host + pnode;
        // 主
        String pKey = primary + phostAndNode;
        HashMap<Integer, String> pMap = data.get(pKey);
        Iterator<String> rNodeIt = nodeMap.get(host).iterator();
        // 检查本机其它node的备bucket是否存在此节点的主bucketId
        while (rNodeIt.hasNext())
        {
          String rnode = rNodeIt.next();
          String rhostAndNode = host + rnode;
          // 备
          String rKey = redundant + rhostAndNode;
          HashMap<Integer, String> rMap = data.get(rKey);
          if (rMap == null || rMap.size() == 0)
            continue;
          // 对主节点的每个bucketId进行检验,看是否存在于备bucket中
          Iterator<Integer> pBucketIt = pMap.keySet().iterator();
          while (pBucketIt.hasNext())
          {
            Integer bucketId = pBucketIt.next();
            if (rMap.keySet().contains(bucketId))
            {
              tmp.append("primary bucket:" + phostAndNode + "-" + bucketId + " exist in redundant:" + rhostAndNode)
                  .append("\n");
            }
          }
        }
      }
    }
    
    if (tmp.length() > 0)
    {
      sb.append(tmp.toString());
    }
    else
    {
      sb.append("No primary and redundant bucket exist in the same host!");
    }
  }
  
  public void export(String regionName,String filePath,String memberId,String ip,String showLog)
  {
    Map param = new HashMap();
    param.put("beanName", "ExportDataService");
    
    Map args = new HashMap();
    args.put("REGIONPATH", regionName);
    args.put("FILEPATH", filePath);
    args.put("showLog", showLog);
    args.put("IP", ip);
    args.put("MEMBERID", memberId);
    param.put("userArgs", args);
    
    Execution execution = FunctionService.onServer(clientPool).withArgs(param)
        .withCollector(new PrintResultCollector());
    ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
    rc.getResult();
  }
  
  public void Import() throws IOException
  {
    TreeMap<String, String> memberTreeMap = new TreeMap<String, String>();
    String memberId = showMembers(memberTreeMap);
    if ("X".equalsIgnoreCase(memberId) || StringUtils.isEmpty(memberId))
      return;
    String regionName = showRegions();
    if ("X".equalsIgnoreCase(regionName) || StringUtils.isEmpty(regionName))
      return;
    String filePath = showFilePath();
    if ("X".equalsIgnoreCase(filePath))
      return;
    String showLog = showLog();
    if ("X".equalsIgnoreCase(showLog))
      return;
    Map param = new HashMap();
    param.put("beanName", "ImportDataService");
    
    Map args = new HashMap();
    args.put("REGIONPATH", regionName);
    args.put("FILEPATH", filePath);
    args.put("showLog", showLog);
    args.put("IP", memberTreeMap.get(memberId));
    args.put("MEMBERID", memberId);
    param.put("userArgs", args);
    
    LogUtil logUtil = LogUtil.newInstance();
    try
    {
      // 开启batch模式
      // CoreFunctions.batchModeOn();
      Execution execution = FunctionService.onServer(clientPool).withArgs(param)
          .withCollector(new PrintResultCollector());
      ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
      rc.getResult();
      // 关闭batch模式
      // CoreFunctions.batchModeOff();
    }
    catch (Exception e)
    {
      // CoreFunctions.batchModeOff();
      e.printStackTrace();
    }
    long cost = logUtil.cost();
    System.out.println("Import data total cost:" + cost + " min: " + (cost / (1000 * 60)) + " minutes");
  }
  
  /**
   * 显示所有members
   * 
   * @return
   * @throws IOException
   */
  private String showMembers(TreeMap<String, String> memberTreeMap) throws IOException
  {
    do
    {
      System.out.println("------------------------");
      Map param = new HashMap();
      param.put("beanName", "ListMembersService");
      
      Execution execution = FunctionService.onServers(clientPool).withArgs(param);
      ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
      Object obj = rc.getResult();
      if (obj == null)
      {
        System.out.println("can't get members list");
        return null;
      }
      ArrayList list = (ArrayList) obj;
      if (!(list.get(0) instanceof HashMap))
      {
        System.out.println(list.get(0));
        return null;
      }
      HashMap<String, String> memberMap = new HashMap<String, String>();
      StringBuilder sb = new StringBuilder();
      sb.append("NO.").append("\t").append("memberId").append("\t").append("ip").append("\n");
      int no = 1;
      if (memberTreeMap == null)
        memberTreeMap = new TreeMap<String, String>();
      for (int i = 0; i < list.size(); i++)
      {
        HashMap map = (HashMap) list.get(i);
        Iterator memberIters = map.keySet().iterator();
        while (memberIters.hasNext())
        {
          String memberId = (String) memberIters.next();
          String ip = (String) map.get(memberId);
          memberTreeMap.put(memberId, ip);
        }
      }
      // 按固定顺序输入,按ip排序
      Iterator<String> treeIt = memberTreeMap.keySet().iterator();
      while (treeIt.hasNext())
      {
        String memberId = treeIt.next();
        String ip = memberTreeMap.get(memberId);
        sb.append(no).append(" ").append(memberId).append(" ").append(ip).append("\r\n");
        memberMap.put(String.valueOf(no), memberId);
        no++;
      }
      
      System.out.println(sb.toString());
      System.out.println("------------------------\nmemberId,Your choice,No. or memberId?X to exit");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      String line = bufferedReader.readLine();
      if (line == null)
      {
        System.out.println("no input memberId!");
      }
      else if (!"x".equalsIgnoreCase(line.trim()) && !memberMap.entrySet().contains(line.trim())
          && !memberMap.keySet().contains(line.trim()))
      {
        System.out.println("error input:" + line);
      }
      else
      {
        if (memberMap.keySet().contains(line.trim()))
          return memberMap.get(String.valueOf(line.trim()));
        return line.trim();
      }
    }
    while (true);
  }
  
  /**
   * 显示所有region
   * 
   * @return
   * @throws IOException
   */
  private String showRegions() throws IOException
  {
    do
    {
      System.out.println("------------------------");
      Map param = new HashMap();
      param.put("beanName", "ListRegionsService");
      
      Execution execution = FunctionService.onServer(clientPool).withArgs(param);
      ResultCollector rc = execution.execute("REMOTE_ADMIN_FUNCTION");
      Object obj = rc.getResult();
      if (obj == null)
      {
        System.out.println("can't get regions list");
        return null;
      }
      ArrayList list = (ArrayList) obj;
      if (!(list.get(0) instanceof Set))
      {
        System.out.println(list.get(0));
        return null;
      }
      TreeSet regionSet = (TreeSet) list.get(0);
      Iterator regionIters = regionSet.iterator();
      StringBuilder sb = new StringBuilder();
      TreeMap<String, String> regionMap = new TreeMap<String, String>();
      int no = 1;
      sb.append("NO.").append("\t").append("RegionName").append("\n");
      while (regionIters.hasNext())
      {
        String fullPath = (String) regionIters.next();
        sb.append(no).append("\t").append(fullPath).append("\n");
        regionMap.put(String.valueOf(no), fullPath);
        no++;
      }
      System.out.println(sb.toString());
      System.out
          .println("------------------------\nRegionNames,Your choice?No.or regionName,ALL(all) means export all regions,X to exit");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      String line = bufferedReader.readLine();
      if (line == null)
      {
        System.out.println("no input regionName!");
      }
      else if (!"x".equalsIgnoreCase(line.trim()) && !regionMap.entrySet().contains(line.trim())
          && !"ALL".equalsIgnoreCase(line.trim()) && !regionMap.keySet().contains(line.trim()))
      {
        System.out.println("error input:" + line);
      }
      else
      {
        if (regionMap.keySet().contains(line.trim()))
          return regionMap.get(String.valueOf(line.trim()));
        return line.trim();
      }
    }
    while (true);
  }
  
  /**
   * 输入文件保存路径或文件名
   * 
   * @return
   * @throws IOException
   */
  private String showFilePath() throws IOException
  {
    do
    {
      System.out
          .println("------------------------\ninput save filePath,such as /home/data/ or /home/data/StopTime.gfd, empty means use GS_WORK,X to exit");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      String line = bufferedReader.readLine();
      if (line == null)
      {
        System.out.println("no input filePath!");
      }
      else
      {
        return line.trim();
      }
    }
    while (true);
  }
  
  private String showLog() throws IOException
  {
    do
    {
      System.out.println("------------------------\nshow Log?(Y,N), empty means N,X to exit");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      String line = bufferedReader.readLine();
      if (StringUtils.isEmpty(line) || StringUtils.isEmpty(line.trim()))
      {
        return "Y";
      }
      else
      {
        return line.trim();
      }
    }
    while (true);
  }
  
  public Pool getClientPool()
  {
    return clientPool;
  }
  
  public void setClientPool(Pool clientPool)
  {
    this.clientPool = clientPool;
  }
}
