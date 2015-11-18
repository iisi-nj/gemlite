package gemlite.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Util
{
  
  
  protected static MessageDigest messageDigest = null;
  static
  {
    try
    {
      messageDigest = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException e)
    {
      LogUtil.getCoreLog().error("MD 5FileUtil messageDigest 初始化失败", e);
    }
  }
  public final static synchronized String makeMD5String(byte[] bytes)
  {
    
    byte[] md5hash = messageDigest.digest(bytes);
    String md5Str = Util.encodeToBASE64(md5hash);
    return md5Str;
  }
  public final static URL makeFullURL(String config_server,String suffix)
  {
    URL url = null;
    if(config_server.endsWith("/"))
      config_server = config_server.substring(0,config_server.length()-1);
    try
    {
      url = new URL(config_server + suffix);
    }
    catch (MalformedURLException e)
    {
      LogUtil.getCoreLog().error(suffix,e);
    }
    return url;
  }
  
  
  public final static  URL strToURL(String str)
  {
    URL url = null;
    try
    {
      if (!(str.startsWith("file:") || str.startsWith("http:")))
        str = "file:/" + str;
      if (str.startsWith("file:") && !str.endsWith("/"))
        str += "/";
      url = new URL(str);
    }
    catch (Exception e)
    {
    }
    return url;
  }
  
  /***
   * Lgemlite/core/util/Util;
   * @param cls
   * @return
   */
  public final static String getInternalDesc(Class<?> cls)
  {
    String name = cls.getName();
    StringBuilder builder = new StringBuilder();
    builder.append("L");
    builder.append(name.replaceAll("\\.", "\\/"));
    builder.append(";");
    return builder.toString();
  }
  /**
   * gemlite/core/util/Util
   * 用于方法调用
   * @param cls
   * @return
   */
  public final static String getInternalName(Class<?> cls)
  {
    String name = cls.getName();
    StringBuilder builder = new StringBuilder();
    builder.append(name.replaceAll("\\.", "\\/"));
    return builder.toString();
  }
  public final static String getCallingClassName()
  {
//    LocationInfo info = new LocationInfo(new Throwable(),"gemlite");
    return "cls";//info.getClassName();
  }
  
  public final static  String getCallingMethodName()
  {
//    LocationInfo info = new LocationInfo(new Throwable(),"gemlite");
    return "method";//info.getMethodName();
  }
  
  /**
   * 取当前进程ID
   * 
   * @return
   */
  public final static long getPID()
  {
    String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
    return Long.parseLong(processName.split("@")[0]);
  }
  
  
  public final static String printProperties()
  {
    StringBuilder bu = new StringBuilder();
    Properties props = System.getProperties();
    Iterator<Entry<Object,Object>> it = props.entrySet().iterator();
    while(it.hasNext())
    {
      Entry<Object,Object> e = it.next();
      bu.append(e.getKey()).append("=").append(e.getValue()).append("\n");
    }
    Map<String, String> map =  System.getenv();
    Iterator<Entry<String,String>> it2 = map.entrySet().iterator();
    while(it.hasNext())
    {
      Entry<String,String> e = it2.next();
      bu.append(e.getKey()).append("=").append(e.getValue()).append("\n");
    }
    System.out.println(bu.toString());
    return bu.toString();
  }
  /***
   * 显示当前classpath
   */
  public final static String printClasspath()
  {
    String cp = System.getProperty("java.class.path");
    String[] ps = cp.split("\\;");
    List<String> l = new ArrayList<>();
    for (String s : ps)
    {
      int i = s.lastIndexOf("\\");
      String s1 = s.substring(i + 1);
      String s2 = s1 + " \t\t\t " + s;
      l.add(s2);
    }
    Collections.sort(l);
    StringBuilder bu=new StringBuilder();
    for (String s : l)
    {
      System.out.println(s);
      bu.append(s).append("\r\n");
    }
    return bu.toString();
  }
  
  public final static ClassPathXmlApplicationContext initContext(boolean refresh, String... resources)
  {
    ClassPathXmlApplicationContext mainContext = new ClassPathXmlApplicationContext(resources, false);
    mainContext.setValidating(true);
    if (refresh)
      mainContext.refresh();
    return mainContext;
  }
  
  public final static ClassPathXmlApplicationContext initContext(String... resources)
  {
    return initContext(true, resources);
  }
  
  /***
   * 将 base64的byte[] 进行编码，得到base64编码的字符串
   * 
   * @param byteArray
   * @return
   */
  public final static String encodeToBASE64(byte[] byteArray)
  {
    if (byteArray.length == 0)
      return "";
    return (new BASE64Encoder()).encodeBuffer(byteArray);
  }
  
  /**
   * 将 BASE64 编码的字符串 s 进行解码
   * 
   * @param s
   * @param charSet
   * @return
   */
  
  public final static String decodeFromBASE64(String s, String charSet)
  {
    String decodedStr;
    
    if (StringUtils.trimToEmpty(s).equals(""))
      return "";
    
    BASE64Decoder decoder = new BASE64Decoder();
    try
    {
      byte[] b = decoder.decodeBuffer(s);
      if (StringUtils.trimToEmpty(charSet).equals(""))
        decodedStr = new String(b);
      else
        decodedStr = new String(b, charSet);
      
      return decodedStr;
    }
    catch (Exception e)
    {
      LogUtil.getAppLog().error("decodeFromBASE64 Error for the string: " + s + "with CharSet " + charSet, e);
      return "";
    }
  }
  
  public final static byte[] toBase64(String msg, String encoding)
  {
    byte[] msgBytes;
    try
    {
      msgBytes = msg.getBytes(encoding);
      sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
      sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
      String b64Str = enc.encodeBuffer(msgBytes);
      byte[] decodeBytes2 = dec.decodeBuffer(b64Str);
      return decodeBytes2;
    }
    catch (UnsupportedEncodingException e)
    {
      LogUtil.getAppLog().error("", e);
    }
    catch (IOException e)
    {
      LogUtil.getAppLog().error("", e);
    }
    return null;
  }
  
  public final static boolean isInterface(Class<?> c, String szInterface)
  {
    Class<?>[] face = c.getInterfaces();
    for (int i = 0, j = face.length; i < j; i++)
    {
      if (face[i].getName().equals(szInterface))
      {
        return true;
      }
      else
      {
        Class<?>[] face1 = face[i].getInterfaces();
        for (int x = 0; x < face1.length; x++)
        {
          if (face1[x].getName().equals(szInterface))
          {
            return true;
          }
          else if (isInterface(face1[x], szInterface))
          {
            return true;
          }
        }
      }
    }
    if (null != c.getSuperclass())
    {
      return isInterface(c.getSuperclass(), szInterface);
    }
    return false;
  }
  
  public final static boolean isAbstractFunction(Class<?> c)
  {
    int modifiers = c.getModifiers();
    return Modifier.isAbstract(modifiers);
  }
  
  public final static boolean isPublicFunction(Class<?> c)
  {
    int modifiers = c.getModifiers();
    return Modifier.isPublic(modifiers);
  }
  
  public final static Resource toResource(String file)
  {
    return toResource(Thread.currentThread().getContextClassLoader(), file);
  }
  
  public static Resource toResource(ClassLoader loader, String file)
  {
    InputStream in = loader.getResourceAsStream(file);
    byte[] bt;
    try
    {
      bt = new byte[in.available()];
      in.read(bt);
      in.close();
      ByteArrayResource res = new ByteArrayResource(bt);
      return res;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }
}
