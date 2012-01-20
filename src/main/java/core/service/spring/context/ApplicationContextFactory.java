package core.service.spring.context;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import core.service.config.ServiceProperties;
import core.service.spring.bean.factory.ServiceBeanFactoryPostProcessor;

/**
 * Assist in create application context when using 
 * Core Service Framework. 
 * 
 * It is not necessary to use this class to create the 
 * <code>ApplicationContext</code>; however, it helps avoid
 * trying to figure it out yourself.
 * 
 * @author cworley
 *
 */
public class ApplicationContextFactory
{
	
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
	public AnnotationConfigApplicationContext createAnnotationConfigApplicationContext(
			ServiceProperties serviceProperties, 
			String[] packagesToScan)
	{
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.setAllowBeanDefinitionOverriding(true);
		context.addBeanFactoryPostProcessor(new ServiceBeanFactoryPostProcessor(serviceProperties, context));
		context.scan("core.service.factory");
		for (int index = 0; packagesToScan != null && index < packagesToScan.length; index++) {
			context.scan(packagesToScan[index]);
		}
		context.refresh();
		
		return context;
	}
}
