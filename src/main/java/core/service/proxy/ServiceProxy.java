
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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.service.ApplyRules;
import core.service.Security;
import core.service.exception.ServiceException;
import core.service.exception.ServiceResultException;
import core.service.exception.ServiceSecurityException;
import core.service.executor.ServiceExecutor;
import core.service.result.ServiceResult;
import core.service.rule.RuleExecutor;
import core.service.security.ServiceSecurity;
import core.service.session.ClientServiceSession;
import core.service.util.ServiceContextUtil;
import core.service.validation.ServiceValidationException;
import core.service.validation.ValidationExecutor;
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
	public static Object newInstance(Class serviceClass)
	{
		return newInstance(serviceClass, null);
	}
    
    /**
     * new instance
     * 
     * @param serviceInterface
     * @param executor
     * @return
     */
    public static Object newInstance(Class serviceInterface, ClientServiceSession session)
    {
        return java.lang.reflect.Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[] {serviceInterface}, 
                new ServiceProxy(serviceInterface, session));
    }
    
    /** decimal formatter for logging */
    private DecimalFormat decimalFormat = new DecimalFormat("##0.0000");
    
    /** rule executor */
    private RuleExecutor ruleExecutor = null;

    private ClientServiceSession session = null;
    
    /** service interface */
    private Class serviceInterface;

    /**
     * Create service proxy to use the given executor 
     * when invoking services
     * 
     * @param executor
     */
    protected ServiceProxy(Class serviceInterface, ClientServiceSession session)
    {
        super();
        this.serviceInterface = serviceInterface;
        this.session = session;
        ruleExecutor = new RuleExecutor();
    }

    /**
     * Invoke rules to validate parameters
     */
    private ServiceResult businessRulesValidation(Method method, Object[] args)
    {
        List<Object> objects = new ArrayList<Object>();
        ApplyRules context = method.getAnnotation(ApplyRules.class);
        
        if (context != null)
        {
            objects.addAll(Arrays.asList(args));
        }
        else
        {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int paramIndex = 0; paramIndex < parameterAnnotations.length; paramIndex++)
            {
                for (int annotIndex = 0; annotIndex < parameterAnnotations[paramIndex].length; annotIndex++)
                {
                    if (parameterAnnotations[paramIndex][annotIndex] instanceof ApplyRules)
                    {
                        objects.add(args[paramIndex]);
                    }
                }
            }
        }

        ServiceResult<?> result = new ServiceResult();
        if (objects.size() > 0)
        {
            objects.add(result);
            ruleExecutor.execute(objects);
        }
        return result;
    }

    /**
     * Check security on service method for current user
     * 
     * @param method
     * @param args
     */
    private void checkSecurity(Method method, Object[] args)
    {
        Security security = method.getAnnotation(Security.class);
        if (security == null)
        {
            // no security defined
            logger.debug("No security required for service (serviceInterface={0},method={1}).",
                    serviceInterface.getName(),
                    method.getName());
            return;
        }
        
        // check permissions
        ServiceSecurity serviceSecurity = (ServiceSecurity) ServiceContextUtil.getApplicationContext().getBean("serviceSecurity");
        logger.debug("Authenticating service request (session={0},serviceInterface={1},method={2},securityClass={3}).",
        		session,
                serviceInterface.getName(),
                method.getName(),
                serviceSecurity.getClass().getName());
        serviceSecurity.authenticate(session, serviceInterface, method, args);
    }

	/**
	 * @param method
	 * @param args
	 */
	private void checkValidation(Method method, Object[] args) {
		ValidationExecutor validationExecutoin = new ValidationExecutor(args);
		
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
        
        // validation
        Exception validationException = null;
        try 
        {
        	checkValidation(method, args);
        }
        catch (ServiceValidationException e) 
        {
        	validationException = e;
        }

        // security check
        Exception securityException = null;
        try
        {
            checkSecurity(method, args);
        }
        catch (ServiceSecurityException e)
        {
            securityException = e;
        }
        catch (Exception e)
        {
            logger.error("An unexpected exception occured while authenticating service: {0}.", e.getMessage(), e);
            securityException = e;
        }
        
        // business rules validation, if result response type is error then return the result 
        ServiceResult<?> result = businessRulesValidation(method, args);
        if (result.isError())
        {
            return result;
        }

        // only invoke service if security was checked with no exceptions
        if (securityException == null)
        {
            // get executor and execute service
            ServiceExecutor executor = (ServiceExecutor) ServiceContextUtil.getApplicationContext().getBean("serviceExecutor");
            result = executor.execute(serviceInterface, method, method.getParameterTypes(), args);
            
            long endTime = System.currentTimeMillis();
            logger.info("Proxy service execution complete (serviceInterface={0},method={1},elapsedTime={2}).",
                    serviceInterface.getName(),
                    method.getName(),
                    decimalFormat.format((endTime - startTime) / 60000.0));
        }

        // NOTE: This is the only condition we will return ServiceResult
        // handle security exception
        if (securityException != null)
        {
            // if return type is ServiceResult return service result with response type PERISSION
            if (method.getReturnType().equals(ServiceResult.class))
            {
                return ServiceResult.permission("Service authentication failed: " + securityException.getMessage(), securityException);
            }
            // otherwise, re throw the exception
            else
            {
                throw new ServiceException("Service authentication failed.", securityException);
            }
        }
        // if return type is ServiceResult then return it
        else if (method.getReturnType().equals(ServiceResult.class))
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

}
