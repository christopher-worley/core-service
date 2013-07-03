package core.service.spring.bean.factory;

import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import core.service.annotation.Service;
import core.service.config.ServiceProperties;
import core.service.exception.ServiceException;
import core.service.executor.local.CoreServiceExecutor;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * Post processor for services used in the Core Service Framework
 * 
 * This factory modifies bead definitions for beans that are defined as
 * services to be used with in the Core Service Framework.  These are 
 * any bean with the @Service annotation. 
 * 
 * The <code>ServiceFactory</code> used to create services must be
 * defined within the sprint context.  The default bean name and
 * method name are defined in the constants <code>DEFAULT_FACTORY</code>
 * and <code>DEFAULT_METHOD_NAME</code> respectively.  The default factory
 * values can be overridden using the appropriate constructor.
 * 
 * @author cworley
 *
 */
public class ServiceBeanFactoryPostProcessor implements BeanFactoryPostProcessor
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(CoreServiceExecutor.class);
	
	public static final String DEFAULT_FACTORY = "serviceFactory";
	
	public static final String DEFAULT_METHOD_NAME = "createService";
	
	private String serviceFactoryBeanName = DEFAULT_FACTORY;
	
	private String serviceFactoryMethodName = DEFAULT_METHOD_NAME;

	/** 
	 * Application context this this factory is from.  Would rather not use the context
	 * within this class; however, could not figure out how to replace the
	 * bean definition with out using context.registerBeanDefinition(...)  
	 */
	private AnnotationConfigApplicationContext context;
	
	private ServiceProperties serviceProperties;
	
	/**
	 * Default constructor 
	 */
	public ServiceBeanFactoryPostProcessor(ServiceProperties serviceProperties, AnnotationConfigApplicationContext context)
	{
		super();
		this.context = context;
		this.serviceProperties = serviceProperties;
	}

	/**
	 * Use this constructor to override the default service factory settings.
	 * 
	 * @param serviceFactoryBeanName
	 * @param serviceFactoryMethodName
	 */
	public ServiceBeanFactoryPostProcessor(String serviceFactoryBeanName, String serviceFactoryMethodName)
	{
		super();
		this.serviceFactoryBeanName = serviceFactoryBeanName;
		this.serviceFactoryMethodName = serviceFactoryMethodName;
	}

	/**
	 * Set constructor values to allow for the spring bean name to be passed  
	 * to the factory method.
	 * 
	 * If the default factory settings are overridden and the desired factory
	 * method signature differs from the default.
	 * 
	 * @param beanName
	 * @return
	 */
	protected ConstructorArgumentValues createConstructorArgumentValues(String beanName, BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory) {
		
		// Find the service interface
		try
		{
			Class<?> beanClass = getServiceImplementation(beanDefinition);
			
			// check for service annotation on the class
			Service serviceAnnotation = beanClass.getAnnotation(Service.class);
			Class serviceInterface = null;
			// TODO: should we throw the exception below here?
			if (serviceAnnotation == null) 
			{
				// find interface with service annotation
				Class[] interfaces = beanClass.getInterfaces();
				String serviceName = null;
				for (int index = 0; index < interfaces.length; index++) 
				{
					Service interfaceServiceAnnotation = (Service) interfaces[index].getAnnotation(Service.class);
					if (serviceAnnotation == null) {
						serviceAnnotation = interfaceServiceAnnotation;
						serviceInterface = interfaces[index];
					} 
					else 
					{
						logger.warn("Found service annotation on more than one interface.  The first interface will be used to identify the service. ("
								+ "class="
								+ beanClass.getName()
								+ ").");
					}
				}
			}
			
			// TODO: This exception could be thrown earlier in the above if statement when serviceAnnotation != null when it is retried from the class
			if (serviceInterface == null) {
				throw new RuntimeException("Illegal use of Service annotation.  The annotation must exist on the service interface ("
								+ "class="
								+ beanClass.getName()
								+ ").");
			}
		
    		ConstructorArgumentValues values = new ConstructorArgumentValues();
    		ValueHolder value = new ValueHolder(serviceInterface);
    		values.addGenericArgumentValue(value);
    		return values;
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
	
	/**
	 * Return the implementation class configured in the bean factory
	 * 
	 * @return
	 * @throws ClassNotFoundException 
	 */
	private Class<?> getServiceImplementation(BeanDefinition beanDefinition) throws ClassNotFoundException {
		Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());

		// Find type if a proxy type has been returned.
		if (beanClass.getName().contains("$$")) 
		{
			beanClass = Class.forName(beanClass.getName().substring(0, beanClass.getName().indexOf("$$")));
		}

		return beanClass;
	}
	
	/**
	 * Return the service interface that is annotated as a service and implemented
	 * by the implementing class for the bean definition.
	 * 
	 * @return
	 * @throws ClassNotFoundException 
	 */
	private Class<?> getServiceInterface(BeanDefinition beanDefinition) throws ClassNotFoundException {
		Class<?>[] interfaces = getServiceImplementation(beanDefinition).getInterfaces();
		for (int index = 0; index < interfaces.length; index++) 
		{
			Service interfaceServiceAnnotation = (Service) interfaces[index].getAnnotation(Service.class);
			if (interfaceServiceAnnotation != null) {
				return interfaces[index];
			}
		}
		throw new IllegalArgumentException("Bean definition does not represent a class that implements a service interface.");
	}
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
	{
		Map<String,Object> serviceBeans = beanFactory.getBeansWithAnnotation(Service.class);
		for (Iterator<String> iter = serviceBeans.keySet().iterator(); iter.hasNext();) {
			String beanName = iter.next();
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			logger.debug("Setting service factory for bean " + beanDefinition.getBeanClassName());
			
			beanDefinition.setFactoryBeanName(serviceFactoryBeanName);
			beanDefinition.setFactoryMethodName(serviceFactoryMethodName);
			beanDefinition.getConstructorArgumentValues().addArgumentValues(createConstructorArgumentValues(beanName, beanDefinition, beanFactory));

			// replace bean definition in context.  
			// NOTE: Not sure how to do this with the BeanFactory.  Would rather not use the context if possible.
			context.registerBeanDefinition(beanName, beanDefinition);
			
			// add to service config interface and implementing class pairs
			try
			{
				serviceProperties.addInterfaceImpl(getServiceInterface(beanDefinition), getServiceImplementation(beanDefinition));
			} 
			catch (ClassNotFoundException e)
			{
				throw new ServiceException("Cannot determine either service interface or implementation class from bean definition (beanName=" + beanName + ").");
			}
		}

		// add service properties to bean factory
		beanFactory.registerSingleton("serviceProperties", serviceProperties);
	}

}
