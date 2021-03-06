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
package gemlite.core.internal.batch;

import gemlite.core.util.LogUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Simple minded partitioner for a range of values of a column in a database
 * table. Works best if the values are uniformly distributed (e.g.
 * auto-generated primary key values).
 * 
 * @author Dave Syer
 * 
 */
public class ColumnRangePartitioner implements Partitioner,InitializingBean
{
  
  private JdbcTemplate jdbcTemplate;
  
  private String table;
  
  private String column;
  
  private String startValue;
  private String endValue;
  private volatile int page = 0;
  int index = 0;
  
  /**
   * The name of the SQL table the data are in.
   * 
   * @param table
   *          the name of the table
   */
  public void setTable(String table)
  {
    this.table = table;
  }
  
  /**
   * The name of the column to partition.
   * 
   * @param column
   *          the column name.
   */
  public void setColumn(String column)
  {
    this.column = column;
  }
  
  /**
   * Partition a database table assuming that the data in the column specified
   * are uniformly distributed. The execution context values will have keys
   * <code>minValue</code> and <code>maxValue</code> specifying the range of
   * values to consider in each partition.
   * 
   * @see Partitioner#partition(int)
   */
  public Map<String, ExecutionContext> partition(int gridSize)
  {
      //判断数据库类型
      try
      {
          DatabaseType type =  DatabaseType.fromMetaData(jdbcTemplate.getDataSource());
          if(DatabaseType.SYBASE.equals(type))
          {
              return partitionSybase(gridSize);
          }
          else
              return partitionCommon(gridSize);      
      }
      catch(Exception e)
      {
          LogUtil.getLogger().error("DatabaseType error:",e);
          return partitionCommon(gridSize);
      }
  }
  
  
  public Map<String, ExecutionContext> partitionCommon(int gridSize)
  {
    LogUtil.getLogger().info("ColumnRangePartitioner start...");
    LogUtil logUtil =  LogUtil.newInstance();
    long min = jdbcTemplate.queryForObject("SELECT MIN(gfa." + column + ") from (" + table+ ") gfa",Long.class);
    long max = jdbcTemplate.queryForObject("SELECT MAX(gfa." + column + ") from (" + table+ ") gfa",Long.class);
    long targetSize = (max - min) / gridSize + 1;
    LogUtil.getLogger().info("+++++++++++++++++最大值:"+max+"最小值:"+min+"+++++++++++++++++++++++++++++++++");
    Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
    long number = 0;
    long start = min;
    long end = start + targetSize - 1;
    
    while (start <= max)
    {
      ExecutionContext value = new ExecutionContext();
      result.put("partition" + number, value);
      
      if (end >= max)
      {
        end = max;
      }
      value.putLong("min", start);
      value.putLong("max", end);
      LogUtil.getLogger().info("min:" + start + " max:" + end);
      start += targetSize;
      end += targetSize;
      number++;
    }
    LogUtil.getLogger().info("ColumnRangePartitioner end. Cost:"+logUtil.cost());
    return result;
  }
  
  public Map<String, ExecutionContext> partitionSybase(int gridSize)
  {
      //判断有多少行,然后判断出每一页的大小
      String countSql = "select count(1) from ("+ table + ") GF_table_c";
      Long count = jdbcTemplate.queryForObject(countSql,Long.class);
      long targetSize = count / gridSize + 1;
      String firstPageSql = "select top " + targetSize  +" * from ("+ table +") GF_table order by gf_rowid asc";
      String remainingPagesSql = "select top " + targetSize +" * from ("+ table + ") GF_table where gf_rowid > ?" + " order by gf_rowid asc";
      Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
      PagingRowMapper rowCallback = new PagingRowMapper();
      while(index<count)
      {
          if (page == 0) 
          {
              if (LogUtil.getLogger().isDebugEnabled()) 
              {
                  LogUtil.getLogger().debug("SQL used for partition first page: [" + firstPageSql + "]");
              }
              getJdbcTemplate().query(firstPageSql, rowCallback);
              
              //设置最大值,最小值
              ExecutionContext value = new ExecutionContext();
              result.put("partition "+page, value);
              value.putString("min", startValue);
              value.putString("max", endValue);
          }
          else
          {
              if (LogUtil.getLogger().isDebugEnabled()) {
                  LogUtil.getLogger().debug("SQL used for partition remaining pages: [" + remainingPagesSql + "]");
              }
             startValue = new String();
             getJdbcTemplate().query(remainingPagesSql,new String[]{endValue}, rowCallback);
             //设置最大值,最小值
             ExecutionContext value = new ExecutionContext();
             result.put("partition "+page, value);
             value.putString("min", startValue);
             value.putString("max", endValue);
          }
          page++; 
      }
      return result;
  }
  
  //用于向下读数,取每页最后一条数据
  private class PagingRowMapper implements RowMapper<Object> 
  {
      @Override
      public Object mapRow(ResultSet rs, int rowNum) throws SQLException 
      {
          //每次都重置endValue
          endValue = new String();
          String value = "";
          for (String sortKey : column.split(",")) 
          {
              value += rs.getObject(sortKey);
          }
          //赋值,其中只对startValue设置一次
          if(StringUtils.isEmpty(startValue))
              startValue = value;
          endValue = value;
          
          index++;
          return rs;
      }
  }
  
  public JdbcTemplate getJdbcTemplate()
  {
    return jdbcTemplate;
  }
  
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
  {
    this.jdbcTemplate = jdbcTemplate;
  }
  
  public String getTable()
  {
    return table;
  }
  
  public String getColumn()
  {
    return column;
  }

  @Override
  public void afterPropertiesSet() throws Exception
  {
//    setTable(BatchParameter.partitionTable);
//    setColumn(BatchParameter.partitionColumn);
  }
  
}
