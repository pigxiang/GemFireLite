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
package gemlite.core.internal.hotdeploy;
//
//import gemlite.core.annotations.ModuleType;
//import gemlite.core.internal.context.GemliteRegistryHelper;
//import gemlite.core.internal.context.ModuleContextImpl;
//import gemlite.core.internal.support.GemliteException;
//import gemlite.core.internal.support.IGemliteClassScanner;
//import gemlite.core.internal.support.context.IGemliteRegistry;
//import gemlite.core.internal.support.context.IModuleContext;
//import gemlite.core.internal.support.hotdeploy.GemliteJarLoader;
//import gemlite.core.internal.support.hotdeploy.GemliteSibingsLoader;
//import gemlite.core.util.LogUtil;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.commons.lang.StringUtils;
//import org.objectweb.asm.ClassReader;
//import org.objectweb.asm.tree.ClassNode;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.util.ResourceUtils;
//
///***
// * 
// * @author ynd
// * 
// */
//
//public class GemliteClassScanner implements IGemliteClassScanner
//{
//  private GemliteRegistryHelper grHelper;
//  
//  public GemliteClassScanner()
//  {
//    grHelper = new GemliteRegistryHelper();
//  }
//  
//  public static void main(String[] args)
//  {
//    try
//    {
//      File f = ResourceUtils.getFile("d:/tmp/g1.jar");
//      System.out.println(f.toURI().toURL() + " " + f.exists());
//      
//      // URL url = new
//      // URL("file://D:\Code\vmgemlite\vmgemlite\Gemlite-bundle\Gemlite-runtime\target\Gemlite-runtime-0.0.1-SNAPSHOT.jar");
//      GemliteClassScanner ggg = new GemliteClassScanner();
//      GemliteJarLoader loader = new GemliteJarLoader(f.toURI().toURL());
//      ggg.scanRegistryDefinition(loader);
//      // ggg.scan(loader);
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//    }
//    
//  }
//  
//  // private void test2()
//  // {
//  // try
//  // {
//  //
//  // URL url = new URL("http://192.168.120.73:8383/glws/jars/file/1");
//  // GemliteJarLoader loader =new GemliteJarLoader("", url);
//  // String name = findContextName(loader);
//  // System.out.println("name="+name);
//  // }
//  // catch (Exception e)
//  // {
//  // e.printStackdebug();
//  // }
//  // }
//  
//  // private void test1()
//  // {
//  // String jar = "D:/Code/vmgemlite/vmgemlite/Gemlite-bundle/Gemlite-runtime/target/classes";
//  // String jar2 = "D:/Code/vmgemlite/vmsample/Sample-bundle/Sample-domain/target/Sample-domain-0.0.1-SNAPSHOT.jar";
//  // try
//  // {
//  // File f = new File(jar);
//  // GemliteJarLoader lo = new GemliteJarLoader( f.toURI().toURL());
//  // String name = findContextName(lo);
//  // System.out.println(name);
//  // ScannerIterator si = new ScannerIterator(f.toURI().toURL());
//  // Object x = si.getNext();
//  // while (x != null)
//  // {
//  // System.out.println(si.name);
//  // x = si.getNext();
//  // }
//  // }
//  // catch (IOException | URISyntaxException e)
//  // {
//  // e.printStackdebug();
//  // }
//  // }
//  
//  // private static String INDEX_DEF_SUFFIX = System.getProperty("gemlite.core.index-def-suffix", ".def");
//  
//  private final static String class_suffix = ".class";
//  
//  private IGemliteRegistry getRegistry(Map<String, IGemliteRegistry> registryCache, String type)
//  {
//    // 每种类型创建一个注册器
//    IGemliteRegistry registry = registryCache.get(type);
//    if (registry == null)
//    {
//      registry = grHelper.createRegistry(type);
//      if (registry != null)
//        registryCache.put(type, registry);
//      else
//      {
//        throw new GemliteException(" Type=" + type + " has no IGemliteRegistry implements");
//      }
//    }
//    return registry;
//  }
//  
//  private void scanRegistryDefinition(GemliteJarLoader loader)
//  {
//    try
//    {
//      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
//      // gemlite/core/internal/context/registryClass/
//      Resource[] urls = resolver.getResources("gemlite/core/internal/context/registryClass/**/*.class");
//      for (Resource res : urls)
//      {
//        loader.getResourceAsStream(res.getURL().toString());
//        System.out.println(res.getClass());
//      }
//    }
//    catch (IOException e)
//    {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//  }
//  
//  /***
//   * 
//   * 1.资源分为Class和文件，根据扩展名来判断
//   * 2.文件类型目前只有索引的定义文件 ， .def
//   * 3.Class需要分析接口、Annotation、方法的Annotation,更复杂的，需要分析内部类的定义
//   * 4.资源通过对Jar包的扫描得到，针对资源要做的主要工 作是注册，按类型将资源注册进不同的处理器中
//   */
//  public IModuleContext scan(GemliteSibingsLoader loader)
//  {
//    ModuleContextImpl module = null;
//    Map<String, IGemliteRegistry> registryCache = new ConcurrentHashMap<>();
//    LogUtil lu = LogUtil.newInstance();
//    if (LogUtil.getCoreLog().isDebugEnabled())
//      LogUtil.getCoreLog().debug("Start scan classes in jar or path," + loader.getJarurl());
//    ScannerIterator scannerIterator = null;
//    try
//    {
//      URL url = loader.getJarurl();
//      scannerIterator = new ScannerIterator(url);
//      while (scannerIterator.hasNext())
//      {
//        ScannerIteratorItem item = scannerIterator.next();
//        if (item == null)
//          break;
//        String name = item.getName();
//        // 处理类文件
//        // 1.检查接口，是否function
//        // 2.检查类的annotation,是否需要实现序列化
//        // 3.检查方法的annotation,是否需要处理统计
//        ScannedItem scannedItem = null;
//        if (name.endsWith(class_suffix))
//        {
//          byte[] content = item.getBytes(loader);
//          ClassReader cr = new ClassReader(content);
//          ClassNode cn = new ClassNode();
//          cr.accept(cn, 0);
//          String className = null;
//          className = name.replace('/', '.');
//          className = className.replace('\\', '.');
//          className = className.substring(0, name.length() - 6);// 5
//          loader.recordAsOwner(className);
//          scannedItem = GemliteRegistryHelper.analyzeClassBytes(className, cn);
//          
//          if (scannedItem == null)
//          {
//            continue;
//          }
//          
//          if (LogUtil.getCoreLog().isDebugEnabled())
//            LogUtil.getCoreLog().debug("Scan class:" + className + ", registry type:" + scannedItem.getRegistryType());
//          
//          if (scannedItem.getBytesTransformed() != null)
//            loader.addAsmClass(scannedItem.getClassName(), scannedItem.getBytesTransformed());
//          
//          if (GemliteRegistryHelper.DEPLOY_CONFIGURE.equals(scannedItem.getRegistryType()))
//          {
//            String moduleName = (String) scannedItem.getAnnValue();
//            String type = (String) scannedItem.getAnnValue("type");
//            ModuleType moduleType = ModuleType.LOGIC;
//            if (!StringUtils.isEmpty(type))
//              moduleType = ModuleType.valueOf(type);
//            module = null;//new ModuleContextImpl(moduleName, moduleType, loader);
//          }
//          
//          // scannedItem.addParam("content", content);
//          IGemliteRegistry registry = getRegistry(registryCache, scannedItem.getRegistryType());
//          registry.addParam(scannedItem);
//        }
//        // else if (name.endsWith(INDEX_DEF_SUFFIX))
//        // {
//        // scannedItem = new ScannedItem(name, GemliteRegistryHelper.INDEX_DEF);
//        // scannedItem.addParam("filePath", name);
//        // IGemliteRegistry registry = getRegistry(registryCache, scannedItem.getRegistryType());
//        // registry.addParam(scannedItem);
//        // }
//        
//      }
//      if (module != null)
//        module.updateRegistryCache(registryCache);
//      LogUtil.getCoreLog().debug("END scan classes in jar,cost=" + lu.cost() + " --------");
//      
//    }
//    catch (Exception e)
//    {
//      LogUtil.getCoreLog().warn("Deploy occur error,url=" + loader.getJarurl(), e);
//    }
//    finally
//    {
//      try
//      {
//        if (scannerIterator != null)
//        {
//          scannerIterator.close();
//        }
//      }
//      catch (IOException e)
//      {
//        LogUtil.getCoreLog().warn("Close jar inputstream error.");
//      }
//    }
//    return module;
//    
//  }
//  
//}
