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
package gemlite.shell.codegen.util.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Strategy", propOrder = { "name", "matchers" })
@SuppressWarnings({ "all" })
public class Strategy implements Serializable
{
  private final static long serialVersionUID = 360L;
  @XmlElement(defaultValue = "gemlite.shell.codegen.util.DefaultGeneratorStrategy")
  @XmlJavaTypeAdapter(TrimAdapter.class)
  protected String name = "gemlite.shell.codegen.util.DefaultGeneratorStrategy";
  protected Matchers matchers;
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String value)
  {
    this.name = value;
  }
  
  public Matchers getMatchers()
  {
    return matchers;
  }
  
  public void setMatchers(Matchers value)
  {
    this.matchers = value;
  }
  
  public Strategy withName(String value)
  {
    setName(value);
    return this;
  }
  
  public Strategy withMatchers(Matchers value)
  {
    setMatchers(value);
    return this;
  }
}
