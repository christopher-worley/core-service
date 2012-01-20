package core.service.test.factory.dynamic;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import core.service.config.ServiceProperties;
import core.service.spring.context.ApplicationContextFactory;
import core.service.test.mock.MathService;

/**
 * Test for dynamic configuration of the application
 * context.
 * @author cworley
 *
 */
public class TestDynamicSpringServiceFactory
{
	
	
	public TestDynamicSpringServiceFactory()
	{
		super();
	}



	@Test
	public void testBuildServiceFactory() {
		
		ServiceProperties serviceProperties = new ServiceProperties("local-service.properties");
		
		ApplicationContextFactory contextFactory = new ApplicationContextFactory();
		ApplicationContext context = contextFactory.createAnnotationConfigApplicationContext(
				serviceProperties,
				new String[] {
						"core.service.test.mock"
				});
		
		
		MathService service = context.getBean(MathService.class);
		Assert.assertEquals(service.add(1, 1), new Integer(2));
		
	}
}
