package core.service.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import core.service.config.ServiceProperties;
import core.service.exception.ServiceException;
import core.service.executor.local.CoreServiceExecutor;
import core.service.util.ServiceUtil;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * 
 * This service factory dynamically loads beans into the
 * application context.  No xml configuration is needed.
 * 
 * @author cworley
 *
 */
@Configuration(value="serviceFactory")
public class SpringServiceFactory implements ServiceFactory
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(SpringServiceFactory.class);
	
	@Autowired
	private ApplicationContext context;
	
	@Override
	public Object createService(Class serviceInterface)
	{
		logger.debug("Creating service " + new ServiceUtil().getServiceName(serviceInterface));

		// get properties which were added to the context by the SpringBeanFactoryPostProcessor
		ServiceProperties serviceProperties = (ServiceProperties) context.getBean("serviceProperties");
		Class<?> executorClazz = serviceProperties.getServiceExecutor(serviceInterface.getName());

        try
		{
        	Constructor<?> constructor = executorClazz.getConstructor(new Class[] {Class.class, ServiceProperties.class});
        	Object executorObject = constructor.newInstance(serviceInterface, serviceProperties);
        	
			return java.lang.reflect.Proxy.newProxyInstance(
			        serviceInterface.getClassLoader(),
			        new Class[] {serviceInterface}, 
			        (InvocationHandler)executorObject);
		} 
        catch (IllegalArgumentException e)
		{
        	throw new ServiceException("Failed to instantiate service executor: " + e.getMessage(), e);
		} 
        catch (InstantiationException e)
		{
        	throw new ServiceException("Failed to instantiate service executor: " + e.getMessage(), e);
		} 
        catch (IllegalAccessException e)
		{
        	throw new ServiceException("Failed to instantiate service executor: " + e.getMessage(), e);
		} 
        catch (InvocationTargetException e)
		{
        	throw new ServiceException("Failed to instantiate service executor: " + e.getMessage(), e);
		} 
        catch (SecurityException e)
		{
        	throw new ServiceException("Failed to instantiate service executor: " + e.getMessage(), e);
		} 
        catch (NoSuchMethodException e)
		{
        	throw new ServiceException("Failed to instantiate service executor: " + e.getMessage(), e);
		}
	}

}
