package gemlite.core.util;

import gemlite.core.internal.support.FunctionIds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;

public class ViewUtil
{
	public enum CommandType
	{
		LIST, DESCRIBE, DROP, CREATE, REFULLCREATE
	}

	/**
	 * Drop target view
	 * 
	 * @param viewName view name
	 * @return
	 */
	public final static List<String> dropView(String viewName)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CommandType", CommandType.DROP.name());
		params.put("ViewName", viewName);

		List<String> result = executeFunction(params);
		return result;
	}

	/**
	 * Describe target view
	 * 
	 * @param viewName view name
	 * @return
	 */
	public final static List<String> describeView(String viewName)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CommandType", CommandType.DESCRIBE.name());
		params.put("ViewName", viewName);

		List<String> result = executeFunction(params);
		return result;
	}

	/**
	 * List all views.
	 * 
	 * @return
	 */
	public final static List<String> listViews(String regionName)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CommandType", CommandType.LIST.name());
		if (StringUtils.isEmpty(regionName))
			params.put("RegionName", "");	
		else
			params.put("RegionName", regionName);		
		List<String> result = executeFunction(params);
		return result;
	}

	/**
	 * Refresh view data by full create.
	 * @param viewName view name
	 * @return
	 */
	public final static List<String> refullcreate(String viewName)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CommandType", CommandType.REFULLCREATE.name());
		params.put("ViewName", viewName);

		List<String> result = executeFunction(params);
		return result;
	}

	/**
	 * ReCreate view after view deleted.
	 * @param clsName
	 * @return
	 */
	public final static List<String> create(String clsName)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CommandType", CommandType.CREATE.name());
		params.put("ClassName", clsName);

		List<String> result = executeFunction(params);
		return result;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final static List<String> executeFunction(Map<String, Object> params)
	{
		try
		{
			ClientCache client = ClientCacheFactory.getAnyInstance();
			Execution ex = FunctionService.onServer(client.getDefaultPool());
			ex = ex.withArgs(params);
			ResultCollector rc = ex.execute(FunctionIds.MANAGER_VIEW_FUNCTION);
			List<String> rs = (List) rc.getResult();
			return rs;
		} catch (FunctionException e)
		{
			LogUtil.getAppLog().error(
					"Excute function " + FunctionIds.MANAGER_VIEW_FUNCTION
							+ "failed.", e);
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

}
