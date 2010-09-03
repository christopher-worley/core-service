
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import core.service.exception.ServiceException;
import core.service.exception.ServiceRollback;
import core.service.executor.ServiceExecutor;
import core.service.result.ServiceResult;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class LocalServiceExecutor implements ServiceExecutor, ApplicationContextAware
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(LocalServiceExecutor.class);

    /** decimal formatter for logging */
    private DecimalFormat decimalFormat = new DecimalFormat("##0.0000");

    @Autowired
    private ApplicationContext context;
    
    /**
     * Constructor with InformationDomain
     * 
     * @param domain
     */
    public LocalServiceExecutor()
    {
        super();
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
    private ServiceResult doExecution(Object serviceObject, Method method, Class[] paramTypes, Object... args)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException
    {
        ServiceResult result = null;
        // get method and invoke it
        Method methodImpl = serviceObject.getClass().getMethod(method.getName(), paramTypes);
        Object returnObject = methodImpl.invoke(serviceObject, args);
        // If the return value is not a s ServiceResult then wrap it in a
        // ServiceResult
        if (returnObject instanceof ServiceResult)
        {
            result = (ServiceResult) returnObject;
        } 
        else
        {
            result = ServiceResult.success(null, returnObject);
        }

        return result;
    }

    @Override
    public ServiceResult execute(Class interfaceClass, Method method, Class[] paramTypes, Object... args)
            throws ServiceException
    {
        // calculate elapsed time
        long startTime = System.currentTimeMillis();
        
        ServiceResult result = null;
        boolean shouldCommit = false;

        try
        {
            // create instance of the service
            ServiceInstantiator instantiator = (ServiceInstantiator) context.getBean("serviceInstantiator");
            Object serviceObject = instantiator.instantiateService(interfaceClass);

            logger.debug("Invoking serivice (class={0},method={1})", 
                    serviceObject.getClass().getName(),
                    method.getName());

            // execute
            result = doExecution(serviceObject, method, paramTypes, args);

            shouldCommit = true;
        } 
        catch (ServiceRollback r)
        {
            logger.error("Service rolled back. {0}", r.getMessage());
            result = ServiceResult.error(r.getMessage());
        } 
//        catch (NoSuchMethodException e)
//        {
//            result = ServiceResult.exception("Failed to execute service.", e);
//        }
//        catch (InvocationTargetException e)
//        {
//            result = ServiceResult.exception("Failed to execute service.", e);
//        } 
//        catch (IllegalAccessException e)
//        {
//            result = ServiceResult.exception("Failed to execute service.", e);
//        } 
        catch (Exception e)
        {
            result = ServiceResult.exception("Failed to execute service.", e);
        } 
        finally
        {
            // handle after execution events
            // doAfterExecution(adapters, shouldCommit);
            long endTime = System.currentTimeMillis();
            logger.debug("Local service execution finished (class={0},method={1},elapsedTime={2},responeType={3},message={4}).", 
                    interfaceClass.getName(),
                    method.getName(),
                    decimalFormat.format((endTime - startTime) / 60000.0),
                    result == null ? "N/A" : ServiceResult.getResponseDescription(result.getResponseType()),
                    result == null ? "N/A" : result.getMessage());
        }

        // if an exception was create then log it
        if (result.isException())
        {
            Exception e = (Exception) result.getPayload();
            logger.error("An exception occured when executing service: {0}", e, e.getMessage());
        }

        return result;
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.context = applicationContext;
	}

}
