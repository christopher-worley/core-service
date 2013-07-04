
/**
 * Copyright 2009 Core Information Solutions LLC
 *
 * This file is part of Core Service Framework.
 *
 * Core Service Framework is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * Core Service Framework is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Core Service Framework.  If not, see 
 * <http://www.gnu.org/licenses/>.
 */package core.service.executor.local;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

import core.service.config.ServiceConfig;
import core.service.exception.ServiceException;
import core.service.factory.ServiceFactory;
import core.service.plugin.ServicePlugin;
import core.service.result.ServiceResult;
import core.service.util.ServiceUtil;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;


/**
 * Primary service executor to be used in most cases.
 * 
 * In distributed environment this service executor should be used
 * on the server side.  Accepts service plugins to be invoked
 * during service execution.
 * 
 * @author cworley
 *
 */
public class CoreServiceExecutor implements InvocationHandler
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(CoreServiceExecutor.class);

    /** decimal formatter for logging */
    private DecimalFormat decimalFormat = new DecimalFormat("##0.0000");
    
    /** service interface */
    private Class serviceInterface;
    
    /** service factory */
    private ServiceFactory serviceFactory;
    
    /**
     * Executor constructor for service interface
     * 
     * Create instance for the service interface and configuration.
     * 
     */
    public CoreServiceExecutor(Class serviceInterface, ServiceFactory serviceFactory)
    {
        super();
        this.serviceInterface = serviceInterface;
        this.serviceFactory = serviceFactory;
    }

    /**
     * invoke the service
     * 
     * @param serviceObject
     * @param method
     * @param paramTypes
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private Object doExecution(Object serviceObject, Method method, Class[] paramTypes, Object... args)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException
    {
        long startTime = System.currentTimeMillis();
        logger.debug("Invoking serivice (class={0},method={1})", 
                serviceObject.getClass().getName(),
                method.getName());

        Object returnObject = null;
        try 
        {
        	// invoke service method
            Method methodImpl = serviceObject.getClass().getMethod(method.getName(), paramTypes);
            returnObject = methodImpl.invoke(serviceObject, args);
        }
        catch (Exception e)
        {
        	if (method.getReturnType().equals(ServiceResult.class)) 
        	{
                returnObject = ServiceResult.exception("Failed to execute service.", e);
        	}
        	else 
        	{
        		throw new ServiceException("An exception occured while executing service.", e);
        	}
        } 
        finally
        {
            // handle after execution events
            // doAfterExecution(adapters, shouldCommit);
            long endTime = System.currentTimeMillis();
            logger.debug("Local service execution finished (class={0},method={1},elapsedTime={2}).", 
                    new ServiceUtil().getServiceName(serviceInterface),
                    method.getName(),
                    decimalFormat.format((endTime - startTime) / 60000.0));
        }

        return returnObject;
    }

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		// get instance of service from the configured factory
		Object serviceObject = serviceFactory.createService(serviceInterface);
		
		Class[] paramTypes = new Class[args.length];
		for(int index = 0; index < args.length; index++) {
			paramTypes[index] = args[index].getClass();
		}
		
		
		// handle the before execution details
		beforeExecution(serviceObject, method, paramTypes, args);
		
		// execute the service method
		Object result = doExecution(serviceObject, method, paramTypes, args);
		
		// handle the after execution details
		afterExecution(serviceObject, method, paramTypes, args);
		
		return result;
	}

	/**
	 * Invoke after execution plugins that are configured
	 * 
	 * @param serviceObject
	 * @param method
	 * @param paramTypes
	 * @param args
	 */
	private void afterExecution(Object serviceObject, Method method,
			Class[] paramTypes, Object[] args) {
		for (ServicePlugin plugin : serviceFactory.getServiceConfig().getServicePlugins()) {
			plugin.after(serviceObject, method, paramTypes, args);
		}
	}

	/** 
	 * Invoke before execution plugins that are configured
	 * 
	 * @param serviceObject
	 * @param method
	 * @param paramTypes
	 * @param args
	 */
	private void beforeExecution(Object serviceObject, Method method,
			Class[] paramTypes, Object[] args) {
		for (ServicePlugin plugin : serviceFactory.getServiceConfig().getServicePlugins()) {
			plugin.before(serviceObject, method, paramTypes, args);
		}
	}

}
