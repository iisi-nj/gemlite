/**
 * 
 */
package gemlite.core.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;


/**
 * @author ynd
 * 
 */
public class LogUtil
{
  private static Logger appLog;
  private static Logger mqSyncLog;
  private static Logger checkLog;
  private static Logger sqlLog;
  private static Logger coreLog;
  private static Logger testLog;
  private static Logger jmxLog;
  
  public static void init()
  {
    coreLog = LogManager.getLogger("gemlite.coreLog");
    appLog = LogManager.getLogger("gemlite.appLog");
    mqSyncLog = LogManager.getLogger("gemlite.mqSyncLog");
    checkLog = LogManager.getLogger("gemlite.checkLog");
    sqlLog = LogManager.getLogger("gemlite.sqlLog");
    testLog = LogManager.getLogger("gemlite.testLog");
    jmxLog = LogManager.getLogger("gemlite.jmxLog");
  }
  public static Logger getJMXLog()
  {
    return jmxLog;
  }
  
  public static Logger getTestLog()
  {
	  return testLog;
  }
  
  public static Logger getCoreLog()
  {
    return coreLog;
  }
  
  public static Logger getMqSyncLog()
  {
    return mqSyncLog;
  }
  
  /**
   * @return the appLog
   */
  public static Logger getAppLog()
  {
    return appLog;
  }
  
  /**
   * @return the logicLog
   */
  public static Logger getLogicLog()
  {
    return appLog;
  }
  
  public static Logger getLogger()
  {
    return appLog;
  }
  
  public static Logger getCheckLog()
  {
    return checkLog;
  }
  
  public static Logger getSqllog()
  {
    return sqlLog;
  }
  
  private long t1;
  private long t2;
  
  public static LogUtil newInstance()
  {
    LogUtil logUtil = new LogUtil();
    logUtil.startTimer();
    return logUtil;
  }
  
  public void startTimer()
  {
    t1 = System.currentTimeMillis();
  }
  
  public long cost()
  {
    t2 = System.currentTimeMillis();
    long d = t2 - t1;
    startTimer();
    return d;
  }
  
  public static void modifyLevel(Logger logger,Level level)
  {
    LoggerContext context = (LoggerContext) LogManager.getContext(true);
    LoggerConfig conf = context.getConfiguration().getLoggerConfig(logger.getName());
    conf.setLevel(level);
    context.updateLoggers();
  }
  
}
