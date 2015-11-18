package gemlite.core.util;

import gemlite.core.internal.support.GemliteException;
import gemlite.core.internal.support.annotations.GemliteAnnotation;
import gemlite.core.internal.support.hotdeploy.scanner.ScannedItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.springframework.core.io.Resource;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.partition.PartitionListener;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.internal.cache.PartitionedRegion;

public class GemliteHelper
{
  public final static Object unsafeInvoke(Object inst ,String name,Object[] params)
  {
    try
    {
      Class<?>[] paramTypes = null;
      if(params!=null)
      {
        paramTypes = new Class[params.length];
        for(int i=0;i<params.length;i++)
        {
          paramTypes[i] = params[i].getClass();
        }
      }
      Method m= inst.getClass().getDeclaredMethod(name, paramTypes);
      if( m.isAccessible() )
        throw new GemliteException("Why u use this unsafe method to visit a safe method?");
      m.setAccessible(true);
      Object result= m.invoke(inst, params);
      m.setAccessible(false);
      return result;
    }
    catch (Exception e)
    {
      LogUtil.getCoreLog().warn("Unsafe invoke failure.Object={},name={}",inst,name,e);
    }
    return null;
  }
  
  public final static Object unsafeGet(Object inst ,String name)
  {
    try
    {
      Field m= inst.getClass().getDeclaredField(name);
      if( m.isAccessible() )
        throw new GemliteException("Why u use this unsafe method to visit a safe property?");
      m.setAccessible(true);
      Object result= m.get(inst);
      m.setAccessible(false);
      return result;
    }
    catch (Exception e)
    {
      LogUtil.getCoreLog().warn("Unsafe invoke failure.Object={},name={}",inst,name,e);
    }
    return null;
  }
  
  public final static ClassNode toClassNode(byte[] content)
  {
    ClassReader cr = new ClassReader(content);
    ClassNode cn = new ClassNode();
    cr.accept(cn, 0);
    return cn;
  }
  
  public final static ClassNode toClassNode(Resource res)
  {
    InputStream in=null;;
    try
    {
      in = res.getURL().openStream();
    }
    catch (IOException e)
    {
      LogUtil.getCoreLog().error("Resource is ", res.toString(), e);
      return null;
    }
    ClassNode cn= toClassNode(in);
    try
    {
      in.close();
    }
    catch (IOException e)
    {
      LogUtil.getCoreLog().warn("Resource is {} close failure", res.toString(), e);
    }
    return cn;
  }
  public final static ClassNode toClassNode(InputStream in)
  {
    ClassReader cr = null;
    try
    {
      cr = new ClassReader(in);
    }
    catch (IOException e)
    {
      LogUtil.getCoreLog().error("ClassReader new error", e);
      return null;
    }
    ClassNode cn = new ClassNode();
    cr.accept(cn, 0);
    return cn;
  }
  public final static GemliteAnnotation readAnnotations(ClassNode cn)
  {
    Map<String, Map<String, Object>> anns = new HashMap<>();
    for (int j = 0; cn.visibleAnnotations!=null&& j < cn.visibleAnnotations.size(); j++)
    {
      AnnotationNode ann = (AnnotationNode) cn.visibleAnnotations.get(j);
      for (int i = 0; i < ann.values.size(); i += 2)
      { 
        Object k = ann.values.get(i);
        Object v = ann.values.get(i + 1);
        if (v instanceof Object[])
        {
          Object[] v0 = (Object[]) v;
          v = v0[1];
        }
        Map<String, Object> m1 = anns.get(ann.desc);
        if (m1 == null)
        {
          m1 = new HashMap<>();
          anns.put(ann.desc, m1);
        }
        m1.put(k.toString(), v);
      }
    }
    String clsname = cn.name.replace('/','.');
    GemliteAnnotation ga = new GemliteAnnotation(clsname,anns);
    return ga;
    
  }
  
  public final static void readAnnotationValues(ScannedItem scanItem, AnnotationNode ann)
  {
	if(ann.values != null)
	{
	    for (int i = 0; i < ann.values.size(); i += 2)
	    {
	      Object k = ann.values.get(i);
	      Object v = ann.values.get(i + 1);
	      if (v instanceof Object[])
	      {
	        Object[] v0 = (Object[]) v;
	        v = v0[1];
	      }
	      scanItem.addAnnValue(k, v);
	    }
	}
  }
  
  public final static Object getAnnotationValue(AnnotationNode ann, String name)
  {
    if (ann.values != null && ann.values.size() > 0)
    {
      for (int i = 0; i < ann.values.size(); i++)
      {
        Object key = ann.values.get(i);
        if (name.equals(key))
          return ann.values.get(i + 1);
      }
    }
    return null;
  }
  
  public final static byte[] classNodeToBytes(ClassNode cn)
  {
    byte[] bt = null;
    try
    {
      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
      cn.accept(cw);
      bt = cw.toByteArray();
    }
    catch (Exception e)
    {
      throw new GemliteException(cn.name, e);
    }
    return bt;
  }
  
  
  public static void main(String[] args) throws Exception
  {
//    File f = new File("D:/Projects/JnJ/iisi/PadoAll-0.0.1-all-pado-core3/com/netcrest/pado/server/PadoServerManager.class");
    File f= new File("D:/Projects/JnJ/iisi/a1/pado/com/netcrest/pado/server/PadoServerManager.class");
//    File f = new File("D:/Projects/JnJ/iisi/a2/com/netcrest/pado/server/PadoServerManager.class");
    FileInputStream fi = new FileInputStream(f);
    byte[] bt = new byte[fi.available()];
    fi.read(bt);
    fi.close();
    PrintWriter pw = new PrintWriter(new File("d:/tmp/pado/Padoserverbc6.java"));
    checkAsmBytes(bt, pw);
  }
  /***
   * 
   * @param cr
   */
  public final static void checkAsmBytes(byte[] bytes, String checkFile)
  {
    try
    {
      FileOutputStream fo = new FileOutputStream(checkFile);
      PrintWriter pw = new PrintWriter(fo);
      checkAsmBytes(bytes, pw);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public final static void checkAsmBytes(byte[] bytes, PrintWriter pw)
  {
    try
    {
      ClassReader cr = new ClassReader(bytes);
      CheckClassAdapter.verify(cr, true, pw);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public final static void dumpBytecode(byte[] bytes, PrintWriter pw)
  {
    ClassReader cr = new ClassReader(bytes);
    ClassNode cn = new ClassNode();
    cr.accept(cn, 0);
    dumpBytecode(cn, pw);
  }
  
  public final static void dumpBytecode(ClassNode cn, PrintWriter pw)
  {
    pw = pw == null ? new PrintWriter(System.out) : pw;
    TraceClassVisitor ca = new TraceClassVisitor(pw);
    cn.accept(ca);
  }
  
  public final static void writeTempClassFile(byte[] bt, String className)
  {
    FileOutputStream fo;
    try
    {
      fo = new FileOutputStream("d:/11.class");
      fo.write(bt);
      fo.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
  }
  
  public final static void addPartitionListener(Region<?, ?> r, PartitionListener listener)
  {
    if (PartitionRegionHelper.isPartitionedRegion(r))
    {
      try
      {
        PartitionedRegion pr = (PartitionedRegion) r;
        PartitionListener[] pl = pr.getPartitionListeners();
        PartitionListener[] newPl = new PartitionListener[pl.length + 1];
        System.arraycopy(pl, 0, newPl, 0, pl.length);
        newPl[pl.length] = listener;
        
        Field f = pr.getClass().getDeclaredField("partitionListeners");
        f.setAccessible(true);
        f.set(pr, newPl);
        f.setAccessible(false);
      }
      catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
      {
        LogUtil.getCoreLog().error("Set partitionListener error on:" + r.getName(), e);
      }
    }
  }
  
  public final static void removePartitionListener(Region<?, ?> r, PartitionListener listener)
  {
    if (PartitionRegionHelper.isPartitionedRegion(r))
    {
      try
      {
        PartitionedRegion pr = (PartitionedRegion) r;
        PartitionListener[] pl = pr.getPartitionListeners();
        List<PartitionListener> newPl = new ArrayList<PartitionListener>();
        for (int i = 0; i < pl.length; i++)
        {
          PartitionListener l = pl[i];
          if (l != listener)
            newPl.add(l);
        }
        
        Field f = r.getClass().getDeclaredField("partitionListeners");
        f.setAccessible(true);
        f.set(pr, newPl.toArray(new PartitionListener[0]));
        f.setAccessible(false);
      }
      catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
      {
        LogUtil.getCoreLog().error("Set partitionListener error on:" + r.getName(), e);
      }
    }
  }
  
}
