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
package gemlite.shell.codegen.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class AbstractGeneratorStrategy implements GeneratorStrategy
{
  // -------------------------------------------------------------------------
  // Strategy methods
  // -------------------------------------------------------------------------
  @Override
  public final String getFileName(Definition definition)
  {
    return getFileName(definition, Mode.DEFAULT);
  }
  
  @Override
  public final String getFileName(Definition definition, Mode mode)
  {
    return getJavaClassName(definition, mode) + ".java";
  }
  
  @Override
  public final File getFile(Definition definition)
  {
    return getFile(definition, Mode.DEFAULT);
  }
  
  @Override
  public final File getFile(Definition definition, Mode mode)
  {
    String dir = getTargetDirectory();
    String pkg = getJavaPackageName(definition, mode).replaceAll("\\.", "/");
    return new File(dir + "/" + pkg, getFileName(definition, mode));
  }
  
  @Override
  public final String getFullJavaIdentifier(Definition definition)
  {
    StringBuilder sb = new StringBuilder();
    // Columns
    if (definition instanceof ColumnDefinition)
    {
      TypedElementDefinition<?> e = (TypedElementDefinition<?>) definition;
      if (getInstanceFields())
      {
        sb.append(getFullJavaIdentifier(e.getContainer()));
      }
      else
      {
        sb.append(getFullJavaClassName(e.getContainer()));
      }
    }
    // Attributes, Parameters
    else if (definition instanceof TypedElementDefinition)
    {
      TypedElementDefinition<?> e = (TypedElementDefinition<?>) definition;
      sb.append(getFullJavaClassName(e.getContainer()));
    }
    // Unique Keys
    else if (definition instanceof UniqueKeyDefinition)
    {
      sb.append(getJavaPackageName(definition.getSchema()));
      sb.append(".Keys");
    }
    // Table, UDT, Schema, etc
    else
    {
      sb.append(getFullJavaClassName(definition));
    }
    sb.append(".");
    sb.append(getJavaIdentifier(definition));
    return sb.toString();
  }
  
  @Override
  public final String getJavaSetterName(Definition definition)
  {
    return getJavaSetterName(definition, Mode.DEFAULT);
  }
  
  @Override
  public final String getJavaGetterName(Definition definition)
  {
    return getJavaGetterName(definition, Mode.DEFAULT);
  }
  
  @Override
  public final String getJavaMethodName(Definition definition)
  {
    return getJavaMethodName(definition, Mode.DEFAULT);
  }
  
  @Override
  public final String getJavaClassExtends(Definition definition)
  {
    return getJavaClassExtends(definition, Mode.DEFAULT);
  }
  
  @Override
  public final List<String> getJavaClassImplements(Definition definition)
  {
    return getJavaClassImplements(definition, Mode.DEFAULT);
  }
  
  @Override
  public final String getJavaClassName(Definition definition)
  {
    return getJavaClassName(definition, Mode.DEFAULT);
  }
  
  @Override
  public final String getJavaPackageName(Definition definition)
  {
    return getJavaPackageName(definition, Mode.DEFAULT);
  }
  
  @Override
  public final String getJavaMemberName(Definition definition)
  {
    return getJavaMemberName(definition, Mode.DEFAULT);
  }
  
  @Override
  public final String getFullJavaClassName(Definition definition)
  {
    return getFullJavaClassName(definition, Mode.DEFAULT);
  }
  
  @Override
  public final String getFullJavaClassName(Definition definition, Mode mode)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(getJavaPackageName(definition, mode));
    sb.append(".");
    sb.append(getJavaClassName(definition, mode));
    return sb.toString();
  }
  
  // -------------------------------------------------------------------------
  // List methods
  // -------------------------------------------------------------------------
  @Override
  public final List<String> getJavaIdentifiers(Collection<? extends Definition> definitions)
  {
    List<String> result = new ArrayList<String>();
    for (Definition definition : nonNull(definitions))
    {
      result.add(getJavaIdentifier(definition));
    }
    return result;
  }
  
  @Override
  public final List<String> getJavaIdentifiers(Definition... definitions)
  {
    return getJavaIdentifiers(Arrays.asList(definitions));
  }
  
  @Override
  public final List<String> getFullJavaIdentifiers(Collection<? extends Definition> definitions)
  {
    List<String> result = new ArrayList<String>();
    for (Definition definition : nonNull(definitions))
    {
      result.add(getFullJavaIdentifier(definition));
    }
    return result;
  }
  
  @Override
  public final List<String> getFullJavaIdentifiers(Definition... definitions)
  {
    return getFullJavaIdentifiers(Arrays.asList(definitions));
  }
  
  private static final <T> List<T> nonNull(Collection<? extends T> collection)
  {
    List<T> result = new ArrayList<T>();
    for (T t : collection)
    {
      if (t != null)
      {
        result.add(t);
      }
    }
    return result;
  }
}
