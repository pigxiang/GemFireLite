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
import java.util.concurrent.ArrayBlockingQueue;

public class UserIndexCounter
{
  
  private static ArrayBlockingQueue<CounterItem> theQueue = new ArrayBlockingQueue<>(1000);
  private UserIndexCounterMonitorTask task;
  
  class CounterItem
  {
    // StringBuilder sb = new StringBuilder();
    // sb.append(mobile_no + "#" + user_name + ">>>>>>")
    // .append("costGetIndexRegion=" + costGetIndexRegion + "#")
    // .append("costStartIterator=" + costStartIterator + "#")
    // .append("costGetMobileNoAndUserName=" + costGetMobileNoAndUserName + "#")
    // .append("costGetDBKeySetFromIndexRegion=" + costGetDBKeySetFromIndexRegion + "#")
    // .append("DBKeySet.size()=" + dbKeySet.size() + "#")
    // .append("costDBKeySetIsNull=" + costDBKeySetIsNull + "#")
    // .append("costDBKeySetIsNotNull=" + costDBKeySetIsNotNull + "$");
    String mobile_no;
    String user_name;
    // ...
    long costStart = 0;
    long costFinish = 0;
    long costGetMobileNoAndUserName = 0;
    
    public CounterItem()
    {
      costStart = System.currentTimeMillis();
    }
    
  }
  
  class UserIndexCounterMonitorTask extends Thread
  {
    private boolean flag = false;
    
    public UserIndexCounterMonitorTask()
    {
      super.setDaemon(true);
    }
    
    /***
     * U�
     * 	PA�pn"1
     * ߡ<�n(�-�
     * �� '<v�U
     * 
     */
    @Override
    public void run()
    {
      
      while (!flag)
      {
        try
        {
          CounterItem rsi = theQueue.take();
          
          // ���
//          sb.append(mobile_no + "#" + user_name + ">>>>>>")
//          .append("costGetIndexRegion=" + costGetIndexRegion + "#")
//          .append("costStartIterator=" + costStartIterator + "#")
//          .append("costGetMobileNoAndUserName=" + costGetMobileNoAndUserName + "#")
//          .append("costGetDBKeySetFromIndexRegion=" + costGetDBKeySetFromIndexRegion + "#")
//          .append("DBKeySet.size()=" + dbKeySet.size() + "#")
//          .append("costDBKeySetIsNull=" + costDBKeySetIsNull + "#")
//          .append("costDBKeySetIsNotNull=" + costDBKeySetIsNotNull + "$");
//        log.debug(sb.toString());
          
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  public CounterItem newCounterItem()
  {
    return new CounterItem();
  }
  
  public void costGetMobileNoAndUserName(CounterItem item)
  {
    item.costFinish = System.currentTimeMillis();
    item.costGetMobileNoAndUserName = item.costFinish - item.costStart;
  }
  
  public void recordItem(CounterItem item)
  {
    theQueue.offer(item);
  }
  
  
  public void init()
  {
    task= new UserIndexCounterMonitorTask();
    task.start();
  }
}
