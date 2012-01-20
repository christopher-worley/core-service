
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
 */package core.service.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;

import org.springframework.context.ApplicationContext;

import core.service.exception.ServiceException;
import core.service.exception.ServiceResultException;
import core.service.exception.ServiceSecurityException;
import core.service.executor.ServiceExecutor;
import core.service.result.ServiceResult;
import core.service.server.ServiceRequest;
import core.service.server.ServiceRequestImpl;
import core.service.session.ClientServiceSession;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * 
 * @author cworley
 *
 */
public class ServiceProxy implements InvocationHandler
{
    /** logger for this class */
    private static final Logger logger = LogFactory.getLogger(ServiceProxy.class);
    
    /**
	 * @param serviceClass
	 * @return
	 */
	public static Object newInstance(Class serviceClass, ApplicationContext context)
	{
		return newInstance(serviceClass, context, null);
	}
    
    /**
     * new instance
     * 
     * @param serviceInterface
     * @param executor
     * @return
     */
    public static Object newInstance(Class serviceInterface, ApplicationContext context, ClientServiceSession session)
    {
        return java.lang.reflect.Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[] {serviceInterface}, 
                new ServiceProxy(serviceInterface, context, session));
    }
    
    /** decimal formatter for logging */
    private DecimalFormat decimalFormat = new DecimalFormat("##0.0000");

    /** session */
    private ClientServiceSession session = null;
    
    /** service interface */
    private Class serviceInterface;

    /** application context */
    private ApplicationContext context;
    
    /** actions to invoke before service is invoked */
    private List<ServiceInvocationAction> beforeActions;

	/** actions to invoke after service is invoked */
    private List<ServiceInvocationAction> afterActions;

	/**
     * Create service proxy to use the given executor 
     * when invoking services
     * 
     * @param executor
     */
    protected ServiceProxy(Class serviceInterface, ApplicationContext context, ClientServiceSession session)
    {
        super();
        this.serviceInterface = serviceInterface;
        this.session = session;
        this.context = context;
    }

	public List<ServiceInvocationAction> getAfterActions()
	{
		return afterActions;
	}

	public List<ServiceInvocationAction> getBeforeActions()
	{
		return beforeActions;
	}
    
    /**
     * @param objects
     * @return
     */
    private Class[] getTypes(Object[] objects) {
    	Class[] types = new Class[objects.length];
    	for (int index = 0; index < objects.length; index++) {
    		types[index] = objects[index].getClass();
    	}
    	return types;
    }

    /**
     * Execute the service with the given executor.  
     *
     * If the service methods return type is <code>ServiceResult</code> then return
     * the service result from the executor
     * 
     * If the payload is an exception throw a service exception with the payload nested
     * 
     * If the payload is null then return null
     * 
     * If the methods return type equals the payload type then return the payload
     * 
     * Otherwise a result cannot be determined, throw a ServiceResultException 
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        // track time
        long startTime = System.currentTimeMillis();
        
        // create service request
        ServiceRequestImpl request = new ServiceRequestImpl();
        request.setArguments(args);
        request.setMethodName(method.getName());
        request.setParamTypes(getTypes(args));
        request.setServiceInterfaceClassName(serviceInterface.getName());
        
        // invoke before actions
    	ServiceResult beforeResult = invokeActions(beforeActions, request);
    	if (beforeResult != null) 
    	{
    		return beforeResult;
    	}
        
    	// invoke service
        ServiceResult result = invokeService(request);

        // invoke before actions
    	ServiceResult afterResult = invokeActions(afterActions, request);
    	if (afterResult != null) 
    	{
    		return afterResult;
    	}
        
        long endTime = System.currentTimeMillis();
        logger.info("Proxy service execution complete (serviceInterface={0},method={1},elapsedTime={2}).",
                serviceInterface.getName(),
                method.getName(),
                decimalFormat.format((endTime - startTime) / 60000.0));

        // if return type is ServiceResult then return it
        if (method.getReturnType().equals(ServiceResult.class))
        {
            return result;
        }
        // if its an exception then throw it
        else if (result.isException())
        {
            throw new ServiceException("The service threw an exception.", (Exception) result.getPayload());
        }
        // if payload is null return null
        else if (result.getPayload() == null)
        {
            return null;
        }
        // if the payload class equals the method return type, then return the payload
        else if (result.getPayload().getClass().equals(method.getReturnType()))
        {
            return result.getPayload();
        }
        
        throw new ServiceResultException(
                "Failed to determine service result for service (method.returnType="
                + method.getReturnType().getName() 
                + ",result.payload.class=" 
                + result.getPayload().getClass().getName()
                + ").");
    }
    
    /**
	 * @param beforeActions2
	 * @param request
	 * @return
	 */
	private ServiceResult invokeActions(List<ServiceInvocationAction> actions,
			ServiceRequestImpl request)
	{
		if (actions == null) 
		{
			return null;
		}
		
    	try 
    	{
    		for (ServiceInvocationAction action : actions) 
    		{
        		action.executeAction(session, request, null);
    		}
    	} 
    	// if security exception was thrown then return result of type permission
    	catch (ServiceSecurityException e) 
    	{
    		return ServiceResult.permission(e.getMessage());
    	}
    	// if invocation error excetion was thrown then return result type of error
    	catch (ServiceInvocationError e) 
    	{
    		return ServiceResult.error(e.getMessage());
    	} 
    	catch (Exception e) 
    	{
    		return ServiceResult.exception(e.getMessage(), e);
    	}
    	return null;
	}

	/**
	 * @param method
	 * @param args
	 */
	private ServiceResult invokeService(ServiceRequest request)
	{
        // get executor and execute service
        ServiceExecutor executor = (ServiceExecutor) context.getBean("serviceExecutor");
        return executor.execute(request);
	}

	public void setAfterActions(List<ServiceInvocationAction> afterActions)
	{
		this.afterActions = afterActions;
	}

	public void setBeforeActions(List<ServiceInvocationAction> beforeActions)
	{
		this.beforeActions = beforeActions;
	}

}
