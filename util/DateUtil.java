package gemlite.core.util;

import gemlite.core.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

public class DateUtil
{
  public final static String DATE_FORMAT_1 = "yyyy-MM-dd";
  public final static String F_yyyyMMdd = "yyyyMMdd";
  public final static String F_yyyyMMddHHmmss = "yyyyMMddHHmmss";
  public final static String yyyy_MM_dd_HHmmss_SSS = "yyyy/MM/dd HH:mm:ss.SSS";
  public final static String yyyyMMdd_HHmmssSSS = "yyyyMMdd HH:mm:ss:SSS";
  public final static String yyyy_MM_dd_HHmmssSSS = "yyyy-MM-dd HH:mm:ss:SSS";
  public final static String US_MMM_dd_yyyy_hhmmssSSSaa = "MMM dd yyyy hh:mm:ss:SSSaa";
  public final static String US_EEE_MMM_dd_hhmmsszyyyy = "EEE MMM dd HH:mm:ss z yyyy";

    
  public static String dateToString(Date dt, String format)
  {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(dt);
  }
  
  public static String[] supportPatterns = new String[] { "yyyyMMdd", "yyyy-MM-dd HH:mm:ss" };
  static
  {
    String currentSetting = System.getProperty("SUPPORT_DATE_PATTERNS");
    if (currentSetting != null)
    {
      StringTokenizer stk = new StringTokenizer(currentSetting, ",");
      supportPatterns = new String[stk.countTokens()];
      int i = 0;
      while (stk.hasMoreTokens())
      {
        String pattern = stk.nextToken();
        supportPatterns[i++] = pattern;
      }
    }
    
  }
  
  public final static Date parse(String strDate)
  {
    Date dt = null;
    try
    {
      dt = DateUtils.parseDate(strDate, supportPatterns);
    }
    catch (ParseException e)
    {
      LogUtil.getCoreLog().error("Date:" + strDate, e);
    }
    return dt;
  }
  
  public final static Date parse(String strDate,String pattern)
  {
    Date dt = null;
    try
    {
      dt = DateUtils.parseDate(strDate, new String[]{pattern});
    }
    catch (ParseException e)
    {
      LogUtil.getCoreLog().error("Date:" + strDate, e);
    }
    return dt;
  }
  
  public static String format(Date dt, String fmtStr)
  {
    if (dt == null)
      return "";
    String str = DateFormatUtils.format(dt, fmtStr);
    return str;
  }
  
  public static String format(Date dt)
  {
    if (dt == null)
      return "";
    String str = DateFormatUtils.format(dt, "yyyyMMdd");
    return str;
  }
  

  public static Date stringToDate(String dateStr)
  {
    return stringToDate(dateStr, DATE_FORMAT_1);
  }
  
  public static Date stringToDate(String dateStr, String format)
  {
    return stringToDate(dateStr, format, Locale.getDefault());
  }
  
  public static Date stringToDate(String dateStr, String format, Locale locale)
  {
    SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
    Date s_date = null;
    try
    {
      s_date = (Date) sdf.parse(dateStr);
    }
    catch (ParseException e)
    {
      LogUtil.getAppLog().error("DateUtil.stringToDate errr.DateStr:" + dateStr + " fmt:" + format);
      s_date = correctDate(dateStr, format);
    }
    return s_date;
  }
  
  private static Date correctDate(String dateStr, String format)
  {
    Map<String, Locale> set = new HashMap<String, Locale>();
    set.put(US_MMM_dd_yyyy_hhmmssSSSaa, Locale.US);
    set.put(yyyyMMdd_HHmmssSSS, Locale.US);
    set.put(US_EEE_MMM_dd_hhmmsszyyyy, Locale.US);
    set.put(yyyyMMdd_HHmmssSSS, Locale.getDefault());
    set.put(yyyy_MM_dd_HHmmss_SSS, Locale.getDefault());
    set.remove(format);
    for (Entry<String, Locale> es : set.entrySet())
    {
      SimpleDateFormat sdf = new SimpleDateFormat(es.getKey(), es.getValue());
      
      try
      {
        Date s_date = sdf.parse(dateStr);
        LogUtil.getAppLog().error("DateUtil.correctDate work.DateStr:" + dateStr + " fmt:" + es.getKey());
        return s_date;
      }
      catch (ParseException e)
      {
        LogUtil.getAppLog().error("DateUtil.correctDate errr.DateStr:" + dateStr + " fmt:" + es.getKey());
      }
    }
    return null;
  }
  
  /**
   * 今天
   * @return
   */
  public static long today()
  {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.HOUR_OF_DAY, 0);
    return c.getTimeInMillis();
  }
  
  
  /**
   * 添加天数
   * 
   * @param date
   * @param difference
   * @return
   */
  public static long addDay(long date, int difference)
  {
    long lTime = 0L;
    try
    {
      lTime = difference * 24 * 60 * 60 * 1000L;
    }
    catch (Exception e)
    {
      LogUtil.getCoreLog().error("DateFormatUtil.addDay: {}", e);
    }
    return date + lTime;
  }
  
  /**
   * yyyyMMdd转成long
   * @param dateStr
   * @return
   */
  public static long toLong(String dateStr)
  {
    try
    {
      SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyyMMdd");
      Date dt = YYYY_MM_DD.parse(dateStr);
      return dt.getTime();
    }
    catch (ParseException e)
    {
      LogUtil.getLogger().error("DateFormatUtil.toLong parser error:{}",dateStr);
    }
    return 0;
  }
}
