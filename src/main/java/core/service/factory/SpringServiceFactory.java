package core.service.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import core.service.config.ServiceConfig;
import core.service.executor.local.CoreServiceExecutor;
import core.service.spring.bean.factory.ServiceBeanFactoryPostProcessor;
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
public class SpringServiceFactory implements ServiceFactory
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(SpringServiceFactory.class);
	
	private ApplicationContext context;
	
	private ServiceConfig serviceConfig;
	
	private String[] servicePackages;
	
	public SpringServiceFactory(ServiceConfig serviceConfig, String[] servicePackages) {
		this.serviceConfig = serviceConfig;
		this.servicePackages = servicePackages;
		initialize();
	}
	
	/**
	 * Return <code>AnnotationConfigApplicationContext</code> already configured with
	 * services for Core Service Framework.
	 * 
	 * Packages containing services for you product must be included on the <code>packagesToScan</code> parameter.
	 * 
	 * @param serviceProperties
	 * @param packagesToScan list of packages, will throw NPE if null
	 * @return
	 */
	private AnnotationConfigApplicationContext createApplicationcontext(
			ServiceConfig serviceConfig, 
			String[] packagesToScan)
	{
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.setAllowBeanDefinitionOverriding(true);
		context.addBeanFactoryPostProcessor(new ServiceBeanFactoryPostProcessor(this, context));
		context.scan("core.service.factory");
		for (int index = 0; packagesToScan != null && index < packagesToScan.length; index++) {
			context.scan(packagesToScan[index]);
		}
		context.refresh();
		
		return context;
	}
	
	@Override
	public Object createService(Class serviceInterface)
	{
		logger.debug("Creating service " + new ServiceUtil().getServiceName(serviceInterface));

		CoreServiceExecutor executor = new CoreServiceExecutor(serviceInterface, this);
    	
    	Object service = java.lang.reflect.Proxy.newProxyInstance(
    			serviceInterface.getClassLoader(),
                new Class[] {serviceInterface}, 
                executor);
    	
    	return service;
	}
	
	
	@Override
	public ServiceConfig getServiceConfig() {
		return serviceConfig;
	}

	private void initialize() {
		context = createApplicationcontext(serviceConfig, servicePackages);
	}

}
