package gemlite.core.util;

import gemlite.core.internal.support.FunctionIds;
import gemlite.core.internal.support.context.IModuleContext;
import gemlite.core.internal.support.hotdeploy.DeployParameter;
import gemlite.core.internal.support.hotdeploy.GemliteDeployer;

import java.net.URL;
import java.util.List;

import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;

@SuppressWarnings("rawtypes")
public class FunctionUtil
{
  
  public final static String deploy(URL url)
  {
    IModuleContext mc= GemliteDeployer.getInstance().deploy(url);
    DeployParameter param = new DeployParameter(mc.getModuleType(),url);
    return deploy(param);
  }
  public final static String deploy(DeployParameter param)
  {
    Execution ex = FunctionService.onServers(ClientCacheFactory.getAnyInstance().getDefaultPool());
    ex = ex.withArgs(param);
    ResultCollector rc = ex.execute(FunctionIds.DEPLOY_FUNCTION);
    List rs = (List) rc.getResult();
    String str = (String) rs.get(0);
    return str;
  }
  public final static <T> T onServer(String functionId, Class<T> T)
  {
    return onServer(functionId, null, T);
  }
  
  @SuppressWarnings("unchecked")
  public final static <T> T onServer(String functionId, Object args, Class<T> T)
  {
    Execution ex = FunctionService.onServer(ClientCacheFactory.getAnyInstance().getDefaultPool());
    if (args != null)
      ex = ex.withArgs(args);
    ResultCollector rc = ex.execute(functionId);
    List<T> l = (List<T>) rc.getResult();
    return l.get(0);
  }

}
