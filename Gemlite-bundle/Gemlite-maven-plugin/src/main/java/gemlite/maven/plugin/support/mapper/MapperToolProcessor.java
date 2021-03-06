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
package gemlite.maven.plugin.support.mapper;

import gemlite.core.internal.asm.AsmHelper;
import gemlite.core.internal.support.context.DomainMapperHelper;
import gemlite.maven.plugin.support.ClassProcessor;
import gemlite.maven.plugin.support.DomainMojoConstant;
import gemlite.maven.plugin.support.DomainMojoHelper;
import gemlite.maven.plugin.support.ProcessResult;
import gemlite.maven.plugin.support.mapper.MapperToolRegistry.Item1;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/***
 * 
 * @author ynd 读取domain配置，生成IMapperTool工具类
 *         1.工具类全名= domain.name + "$$IMapperToolImpl"
 *         例如：gemlite.sample.domain.Order$$IMapperToolImpl
 *         2.需要生成工具类的注册信息
 */
@SuppressWarnings("unchecked")
public class MapperToolProcessor implements ClassProcessor, Opcodes, DomainMojoConstant
{
  private CreateMapperRegister mapperRegister = new CreateMapperRegister();
  public final static String INTERFACE_NAME = "gemlite/core/internal/domain/IMapperTool";
  
  @Override
  public ProcessResult process(File originFile, byte[] bytes, ClassNode domain)
  {
    ProcessResult result = new ProcessResult();
    // 需要创建新文件
    try
    {
      ClassNode mapper = createIMapperToolClass(domain);
      String mapperClassName = mapper.name.replaceAll("\\/", "\\.");
      if (DomainMojoHelper.log().isDebugEnabled())
        DomainMojoHelper.log().debug("Domain:" + domain.name + ", create mapper class:" + mapper.name);
      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
      mapper.accept(cw);
      byte[] bt = cw.toByteArray();
      writeClassFile(originFile, bt);
      if (DomainMojoHelper.log().isDebugEnabled())
        DomainMojoHelper.log().debug(domain.name + " write file done.");
      result.node = mapper;
      result.success = true;
      result.newBytes = bt;
      
      // create domain mapper register
      mapperRegister.getIDomainRegistryClass(domain);
      mapperRegister.addRegistryItem(domain, mapperClassName);
      
    }
    catch (Exception e)
    {
      DomainMojoHelper.log().error(domain.name + " " + originFile.toString(), e);
      result.success = false;
    }
    return result;
  }
  
  private void writeClassFile(File originFile, byte[] processedBytes) throws IOException
  {
    // "D:/springsource/gemlite_prod/ynd.test.domain/target/classes/ynd/test/Ac01.class"
    // "D:/springsource/gemlite_prod/ynd.test.domain/target/classes/ynd/test/Ac01$$$IMapperToolImpl.class"
    String fname = originFile.getAbsolutePath();
    fname = fname.replace(".class", DomainMapperHelper.DEFAULT_SUBFFIX + ".class");
    File mapperFile = new File(fname);
    mapperFile.delete();
    BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(mapperFile));
    bo.write(processedBytes);
    bo.close();
  }
  
  /***
   * @param domain
   * @return
   * @throws IOException
   */
  private ClassNode createIMapperToolClass(ClassNode domain) throws IOException
  {
    ClassNode mapper = new ClassNode();
    mapper.version = V1_7;
    mapper.access = ACC_PUBLIC + ACC_SUPER;
    mapper.name = domain.name + DomainMapperHelper.DEFAULT_SUBFFIX;
    mapper.superName = "java/lang/Object";
    mapper.interfaces.add(INTERFACE_NAME);
    addConstructFunction(domain, mapper);
    
    String keyDesc = implementInterface(domain, mapper);
    String name = AsmHelper.toFullName(keyDesc);
    mapper.signature = "Ljava/lang/Object;Lgemlite/core/internal/domain/IMapperTool<" + name + "L" + domain.name
        + ";>;";
    
    return mapper;
  }
  
  private void addConstructFunction(ClassNode domain, ClassNode mapper)
  {
    MethodNode mn = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);
    InsnList insn = mn.instructions;
    insn.add(new VarInsnNode(ALOAD, 0));
    insn.add(AsmHelper.newMethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
    insn.add(new InsnNode(RETURN));
    mapper.methods.add(mn);
  }
  
  private ClassNode findKeyClass(ClassNode domain) throws IOException
  {
    ClassNode key = null;
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug(
          "Traverse domain annotations to find if there exists key class, domain:" + domain.name);
    for (Object o : domain.visibleAnnotations)
    {
      AnnotationNode an = (AnnotationNode) o;
      if (DomainMojoHelper.log().isDebugEnabled())
        DomainMojoHelper.log().debug("Annotation:" + an.desc + " value:" + an.values);
      if (AN_Key.equals(an.desc))
      {
        Type t = (Type) an.values.get(1);
        InputStream keyClassIn = DomainMojoHelper.getLoader().getResourceAsStream(t.getInternalName() + ".class");
        byte[] keyClassBytes = new byte[keyClassIn.available()];
        keyClassIn.read(keyClassBytes);
        keyClassIn.close();
        ClassReader cr = new ClassReader(keyClassBytes);
        key = new ClassNode();
        cr.accept(key, 0);
      }
    }
    return key;
  }
  
  private boolean isKeyField(FieldNode fn)
  {
    if (fn.visibleAnnotations != null)
    {
      for (Object o : fn.visibleAnnotations)
      {
        AnnotationNode an = (AnnotationNode) o;
        if (AN_Key.equals(an.desc))
        {
          if (DomainMojoHelper.log().isDebugEnabled())
            DomainMojoHelper.log().debug("Found key field:" + fn.name + "," + fn.desc);
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * 1.检查Domain类的annotation，查找 @Key
   * 2.遍历field,查找@Key、关联field.name->field.set/get method
   * 3.若domain->@Key存在,读取keyClass
   * 
   * @param domain
   * @param mapper
   * @throws IOException
   */
  private String implementInterface(ClassNode domain, ClassNode mapper) throws IOException
  {
    String keyDesc = "";
    ClassNode key = findKeyClass(domain);
    addMapperValueMethod(domain, mapper);
    FieldNode fnKey = addMergeValueMethod(domain, mapper, key == null);
    if (key != null)
    {
      addValue2KeyComplex(domain, mapper, key);
      addMapperKeyComplex(domain, mapper, key);
      keyDesc = "L" + key.name + ";";
    }
    else if (fnKey != null)
    {
      addValue2KeySimple(domain, mapper, fnKey);
      addMapperKeySimple(mapper, fnKey);
      keyDesc = fnKey.desc;
    }
    else
      throw new RuntimeException("Domain:" + domain.name + " not define key class or key property!");
    
    addFieldNames(domain, mapper, key, fnKey);
    addKeyValueTypeMethod(domain, mapper, keyDesc);
    addBridgeMethod(domain, mapper, keyDesc);
    return keyDesc;
  }
  
  private void addMapperValueMethod(ClassNode domain, ClassNode mapper)
  {
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add mapperValue(IDataSource)V method...");
    MethodNode mn = new MethodNode(ACC_PUBLIC, "mapperValue", "(Lgemlite/core/internal/domain/IDataSource;)L"
        + domain.name + ";", null, null);
    InsnList insn = mn.instructions;
    insn.add(new TypeInsnNode(NEW, domain.name));
    insn.add(new InsnNode(DUP));
    insn.add(AsmHelper.newMethodInsnNode(INVOKESPECIAL, domain.name, "<init>", "()V", false));
    insn.add(new VarInsnNode(ASTORE, 2));
    insn.add(new VarInsnNode(ALOAD, 0));
    insn.add(new VarInsnNode(ALOAD, 1));
    insn.add(new VarInsnNode(ALOAD, 2));
    insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, mapper.name, "mapperValue",
        "(Lgemlite/core/internal/domain/IDataSource;L" + domain.name + ";)L" + domain.name + ";", false));
    insn.add(new InsnNode(POP));
    insn.add(new VarInsnNode(ALOAD, 2));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add mapperValue(IDataSource)V method done.");
    
  }
  
  private FieldNode addMergeValueMethod(ClassNode domain, ClassNode mapper, boolean findKey)
  {
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add mapperValue(IDataSource,V)V method...");
    MethodNode mn = new MethodNode(ACC_PUBLIC, "mapperValue", "(Lgemlite/core/internal/domain/IDataSource;L"
        + domain.name + ";)L" + domain.name + ";", null, null);
    InsnList insn = mn.instructions;
    
    FieldNode fnKey = null;
    for (Object o : domain.fields)
    {
      FieldNode fn = (FieldNode) o;
      if (!DomainMojoHelper.isValidField(fn))
        continue;
      if (findKey && isKeyField(fn))
        fnKey = fn;
      Item1 mti = MapperToolRegistry.getDataSItem(fn.desc);
      if (DomainMojoHelper.log().isDebugEnabled())
        DomainMojoHelper.log().debug("desc:" + fn.desc + " item:" + mti);
      String s1 = fn.name.substring(0, 1);
      String s2 = fn.name.substring(1);
      String setMethod = "set" + s1.toUpperCase() + s2;
      // field start
      insn.add(new VarInsnNode(ALOAD, 2));
      insn.add(new VarInsnNode(ALOAD, 1));
      insn.add(new LdcInsnNode(fn.name));// aab001
      
      insn.add(AsmHelper.newMethodInsnNode(INVOKEINTERFACE, "gemlite/core/internal/domain/IDataSource", mti.getMethod,
          "(Ljava/lang/String;)" + mti.desc, true));
      
      // Integer prop;
      if (AsmHelper.needTypeConvert(mti.desc, fn.desc))
        AsmHelper.addTypeConvert(insn, mti.desc);
      insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, domain.name, setMethod, "(" + fn.desc + ")V", false));
      // field end
    }
    insn.add(new VarInsnNode(ALOAD, 2));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add mapperValue(IDataSource,V)V method done.");
    
    return fnKey;
  }
  
  private void addBridgeMethod(ClassNode domain, ClassNode mapper, String keyClassDesc)
  {
    // 过桥
    MethodNode mn = new MethodNode(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "mapperValue",
        "(Lgemlite/core/internal/domain/IDataSource;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
    InsnList insn = mn.instructions;
    insn.add(new VarInsnNode(ALOAD, 0));
    insn.add(new VarInsnNode(ALOAD, 1));
    insn.add(new VarInsnNode(ALOAD, 2));
    insn.add(new TypeInsnNode(CHECKCAST, domain.name));
    insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, mapper.name, "mapperValue",
        "(Lgemlite/core/internal/domain/IDataSource;L" + domain.name + ";)L" + domain.name + ";", false));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    
    // 过桥
    mn = new MethodNode(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "mapperValue",
        "(Lgemlite/core/internal/domain/IDataSource;)Ljava/lang/Object;", null, null);
    insn = mn.instructions;
    insn.add(new VarInsnNode(ALOAD, 0));
    insn.add(new VarInsnNode(ALOAD, 1));
    insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, mapper.name, "mapperValue",
        "(Lgemlite/core/internal/domain/IDataSource;)L" + domain.name + ";", false));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    
    // 过桥
    mn = new MethodNode(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "value2Key", "(Ljava/lang/Object;)Ljava/lang/Object;",
        null, null);
    insn = mn.instructions;
    insn.add(new VarInsnNode(ALOAD, 0));
    insn.add(new VarInsnNode(ALOAD, 1));
    insn.add(new TypeInsnNode(CHECKCAST, domain.name));
    keyClassDesc = AsmHelper.toFullName(keyClassDesc);
    insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, mapper.name, "value2Key", "(L" + domain.name + ";)"
        + keyClassDesc + "", false));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    
    // 过桥
    mn = new MethodNode(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "mapperKey",
        "(Lgemlite/core/internal/domain/IDataSource;)Ljava/lang/Object;", null, null);
    insn = mn.instructions;
    insn.add(new VarInsnNode(ALOAD, 0));
    insn.add(new VarInsnNode(ALOAD, 1));
    insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, mapper.name, "mapperKey",
        "(Lgemlite/core/internal/domain/IDataSource;)" + keyClassDesc + "", false));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
  }
  
  private void addValue2KeyComplex(ClassNode domain, ClassNode mapper, ClassNode key)
  {
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add value2key(V)K method, key:" + key.name);
    MethodNode mn = new MethodNode(ACC_PUBLIC, "value2Key", "(L" + domain.name + ";)L" + key.name + ";", null, null);
    InsnList insn = mn.instructions;
    insn.add(new TypeInsnNode(NEW, key.name));
    insn.add(new InsnNode(DUP));
    insn.add(AsmHelper.newMethodInsnNode(INVOKESPECIAL, key.name, "<init>", "()V", false));
    insn.add(new VarInsnNode(ASTORE, 2));
    
    for (Object o : key.fields)
    {
      FieldNode fn = (FieldNode) o;
      String s1 = fn.name.substring(0, 1);
      String s2 = fn.name.substring(1);
      String setMethod = "set" + s1.toUpperCase() + s2;
      String getMethod = "get" + s1.toUpperCase() + s2;
      // field start
      insn.add(new VarInsnNode(ALOAD, 2));
      insn.add(new VarInsnNode(ALOAD, 1));
      insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, domain.name, getMethod, "()" + fn.desc, false));
      insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, key.name, setMethod, "(" + fn.desc + ")V", false));
      // field end
    }
    insn.add(new VarInsnNode(ALOAD, 2));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add value2key(V)K method, key:" + key.name + " done.");
  }
  
  private void addMapperKeyComplex(ClassNode domain, ClassNode mapper, ClassNode key)
  {
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add mapperKey(mapperKey)K method, key:" + key.name);
    MethodNode mn = new MethodNode(ACC_PUBLIC, "mapperKey", "(Lgemlite/core/internal/domain/IDataSource;)L" + key.name
        + ";", null, null);
    InsnList insn = mn.instructions;
    insn.add(new TypeInsnNode(NEW, key.name));
    insn.add(new InsnNode(DUP));
    insn.add(AsmHelper.newMethodInsnNode(INVOKESPECIAL, key.name, "<init>", "()V", false));
    insn.add(new VarInsnNode(ASTORE, 2));
    for (Object o : key.fields)
    {
      FieldNode fn = (FieldNode) o;
      if (!DomainMojoHelper.isValidField(fn))
        continue;
      
      Item1 mti = MapperToolRegistry.getDataSItem(fn.desc);
      String s1 = fn.name.substring(0, 1);
      String s2 = fn.name.substring(1);
      String setMethod = "set" + s1.toUpperCase() + s2;
      // field start
      insn.add(new VarInsnNode(ALOAD, 2));
      insn.add(new VarInsnNode(ALOAD, 1));
      insn.add(new LdcInsnNode(fn.name));// aab001
      insn.add(AsmHelper.newMethodInsnNode(INVOKEINTERFACE, "gemlite/core/internal/domain/IDataSource", mti.getMethod,
          "(Ljava/lang/String;)" + fn.desc, true));
      insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, key.name, setMethod, "(" + fn.desc + ")V", false));
      // field end
    }
    insn.add(new VarInsnNode(ALOAD, 2));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add mapperKey(mapperKey)K method, key:" + key.name + " done.");
  }
  
  private void addValue2KeySimple(ClassNode domain, ClassNode mapper, FieldNode key)
  {
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add value2key(V)K method, key field:" + key.name);
    String s1 = key.name.substring(0, 1);
    String s2 = key.name.substring(1);
    String getMethod = "get" + s1.toUpperCase() + s2;
    String fullDesc = AsmHelper.toFullName(key.desc);
    MethodNode mn = new MethodNode(ACC_PUBLIC, "value2Key", "(L" + domain.name + ";)" + fullDesc, null, null);
    InsnList insn = mn.instructions;
    insn.add(new VarInsnNode(ALOAD, 1));
    insn.add(AsmHelper.newMethodInsnNode(INVOKEVIRTUAL, domain.name, getMethod, "()" + key.desc, false));
    AsmHelper.addTypeConvert(insn, key.desc);
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add value2key(V)K method, key field:" + key.name + " done.");
  }
  
  private void addMapperKeySimple(ClassNode mapper, FieldNode key)
  {
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add mapperKey(IDataSource)K method, key field:" + key.name);
    String fullDesc = AsmHelper.toFullName(key.desc);
    MethodNode mn = new MethodNode(ACC_PUBLIC, "mapperKey", "(Lgemlite/core/internal/domain/IDataSource;)" + fullDesc,
        null, null);
    InsnList insn = mn.instructions;
    insn.add(new VarInsnNode(ALOAD, 1));
    insn.add(new LdcInsnNode(key.name));
    
    Item1 mti = MapperToolRegistry.getDataSItem(key.desc);
    insn.add(AsmHelper.newMethodInsnNode(INVOKEINTERFACE, "gemlite/core/internal/domain/IDataSource", mti.getMethod,
        "(Ljava/lang/String;)" + key.desc, true));
    AsmHelper.addTypeConvert(insn, key.desc);
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    if (DomainMojoHelper.log().isDebugEnabled())
      DomainMojoHelper.log().debug("Add mapperKey(IDataSource)K method, key field:" + key.name);
  }
  
  private void addFieldNames(ClassNode domain, ClassNode mapper, ClassNode kcn, FieldNode kfn)
  {
    // 静态方法，初始化fieldNames
    MethodNode mn = new MethodNode(ACC_STATIC, "<clinit>", "()V", null, null);
    InsnList insn = mn.instructions;
    // new valueFieldNames
    insn.add(new TypeInsnNode(NEW, "java/util/ArrayList"));
    insn.add(new InsnNode(DUP));
    insn.add(AsmHelper.newMethodInsnNode(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
    insn.add(new FieldInsnNode(PUTSTATIC, mapper.name, "valueFieldNames", "Ljava/util/List;"));
    // new keyFieldNames
    insn.add(new TypeInsnNode(NEW, "java/util/ArrayList"));
    insn.add(new InsnNode(DUP));
    insn.add(AsmHelper.newMethodInsnNode(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
    insn.add(new FieldInsnNode(PUTSTATIC, mapper.name, "keyFieldNames", "Ljava/util/List;"));
    // value fields
    for (Object o : domain.fields)
    {
      FieldNode fn = (FieldNode) o;
      if (!DomainMojoHelper.isValidField(fn))
        continue;
      
      insn.add(new FieldInsnNode(GETSTATIC, mapper.name, "valueFieldNames", "Ljava/util/List;"));
      insn.add(new LdcInsnNode(fn.name));
      insn.add(AsmHelper.newMethodInsnNode(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
      insn.add(new InsnNode(POP));
    }
    // key fields
    if (kcn != null)
    {
      for (Object o : kcn.fields)
      {
        FieldNode fn = (FieldNode) o;
        insn.add(new FieldInsnNode(GETSTATIC, mapper.name, "keyFieldNames", "Ljava/util/List;"));
        insn.add(new LdcInsnNode(fn.name));
        insn.add(AsmHelper.newMethodInsnNode(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
        insn.add(new InsnNode(POP));
      }
    }
    else
    {
      insn.add(new FieldInsnNode(GETSTATIC, mapper.name, "keyFieldNames", "Ljava/util/List;"));
      insn.add(new LdcInsnNode(kfn.name));
      insn.add(AsmHelper.newMethodInsnNode(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
      insn.add(new InsnNode(POP));
    }
    
    insn.add(new InsnNode(RETURN));
    mapper.methods.add(mn);
    
    // 定义fieldNames 静态变量
    FieldNode fnFieldNames = new FieldNode(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, "valueFieldNames", "Ljava/util/List;",
        "Ljava/util/List<Ljava/lang/String;>;", null);
    mapper.fields.add(fnFieldNames);
    fnFieldNames = new FieldNode(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, "keyFieldNames", "Ljava/util/List;",
        "Ljava/util/List<Ljava/lang/String;>;", null);
    mapper.fields.add(fnFieldNames);
    
    // getFieldNames方法
    mn = new MethodNode(ACC_PUBLIC, "getValueFieldNames", "()Ljava/util/List;", "()Ljava/util/List<Ljava/lang/String;>;",
        null);
    insn = mn.instructions;
    insn.add(new FieldInsnNode(GETSTATIC, mapper.name, "valueFieldNames", "Ljava/util/List;"));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    // getKeyFieldNames
    mn = new MethodNode(ACC_PUBLIC, "getKeyFieldNames", "()Ljava/util/List;", "()Ljava/util/List<Ljava/lang/String;>;",
        null);
    insn = mn.instructions;
    insn.add(new FieldInsnNode(GETSTATIC, mapper.name, "keyFieldNames", "Ljava/util/List;"));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
  }
  
  private void addKeyValueTypeMethod(ClassNode domain, ClassNode mapper, String keyClassDesc)
  {
    keyClassDesc = AsmHelper.toFullName(keyClassDesc);
    MethodNode mn = new MethodNode(ACC_PUBLIC, "getKeyClass", "()Ljava/lang/Class;", "()Ljava/lang/Class<"
        + keyClassDesc + ">;", null);
    InsnList insn = mn.instructions;
    insn.add(new LdcInsnNode(Type.getType(keyClassDesc)));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
    
    String valueClassDesc = "L" + domain.name + ";";
    mn = new MethodNode(ACC_PUBLIC, "getValueClass", "()Ljava/lang/Class;", "()Ljava/lang/Class<" + valueClassDesc
        + ">;", null);
    insn = mn.instructions;
    insn.add(new LdcInsnNode(Type.getType(valueClassDesc)));
    insn.add(new InsnNode(ARETURN));
    mapper.methods.add(mn);
  }
  
  public void endProcess()
  {
    mapperRegister.endProcess();
  }
  
  public CreateMapperRegister getMapperRegister()
  {
    return mapperRegister;
  }
}
