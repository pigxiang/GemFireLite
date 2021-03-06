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
package gemlite.core.internal.common;

import gemlite.core.common.DateUtil;
import gemlite.core.util.LogUtil;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;

/**
 * 用于定义java基本类型
 * @author GSONG
 * 2015年4月9日
 */
//TODO 需要完善其它类型
public class JavaTypes
{
   public static final String STRING = "java.lang.String";
   public static final String INT1 = "int";
   public static final String INT2 = "java.lang.Integer";
   public static final String INT3 = "java.math.BigInteger";
   public static final String LONG1 = "long";
   public static final String LONG2 = "java.lang.Long";
   public static final String FLOAT1 = "float";
   public static final String FLOAT2 = "java.lang.Float";
   public static final String DOUBLE1 = "double";
   public static final String DOUBLE2 = "java.lang.Double";
   public static final String BOOLEAN1 = "boolean";
   public static final String BOOLEAN2 = "java.lang.Boolean";
   public static final String BIGDECIMAL = "java.math.BigDecimal";
   public static final String DATE_SQL= "java.sql.Date";
   public static final String DATE_UTIL= "java.util.Date";
   public static final String TIME = "java.sql.Time";
   public static final String TIMESTAMP = "java.sql.TIMESTAMP";
   public static final String OBJECT = "java.lang.Object";
   
   public static final Set<String> set = new HashSet<String>();
   
   static
   {
       set.add(STRING);
       set.add(INT1);
       set.add(INT2);
       set.add(INT3);
       set.add(LONG1);
       set.add(LONG2);
       set.add(FLOAT1);
       set.add(FLOAT2);
       set.add(DOUBLE1);
       set.add(DOUBLE2);
       set.add(BOOLEAN1);
       set.add(BOOLEAN2);
       set.add(BIGDECIMAL);
       set.add(DATE_SQL);
       set.add(DATE_UTIL);
       set.add(TIME);
       set.add(TIMESTAMP);
       set.add(OBJECT);
   }
   
   public static boolean contains(String type)
   {
      return set.contains(type);
   }
   
   /**
    * 根据传入类型转换成相关类型数据
    * @param type
    * @param value
    * @return
    */
   public static Object convert(String type,Object value)
   {
       Assert.notNull(type,"convert type must has a value");
       Assert.notNull(value, "convert value must has a value");
       try
       {
           //其它复杂类型暂不支持
           switch(type)
           {
              case STRING:
               return value;
             case INT1:
             case INT2:
             case INT3:
                 return Integer.parseInt(value.toString());
             case LONG1:
             case LONG2:
                 return Long.parseLong(value.toString());
             case FLOAT1:
             case FLOAT2:
                 return Float.parseFloat(value.toString());
             case DOUBLE1:
             case DOUBLE2:
                 return Double.parseDouble(value.toString());
             case BOOLEAN1:
             case BOOLEAN2:
                 return Boolean.parseBoolean(value.toString());
             case BIGDECIMAL:
                 return BigDecimal.valueOf(Double.parseDouble(value.toString()));
             case DATE_SQL:
                 return new java.sql.Date(DateUtil.parse(value.toString()).getTime());
             case DATE_UTIL:
                 return DateUtil.parse(value.toString());
             case TIME:
                 return Time.valueOf(value.toString());
             case TIMESTAMP:
                 return new Timestamp(DateUtil.parse(value.toString()).getTime());
              default:
                return value;
           }
       }
       catch(Exception e)
       {
           LogUtil.getCoreLog().error("convert data type:"+type+" value:"+value,e);
       }
       return 0;
   }
}
