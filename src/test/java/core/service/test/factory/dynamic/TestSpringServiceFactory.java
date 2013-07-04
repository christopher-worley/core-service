package core.service.test.factory.dynamic;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.config.DefaultServiceConfig;
import core.service.config.ServiceConfig;
import core.service.factory.SpringServiceFactory;
import core.service.plugin.AnnotatedValidationPlugin;
import core.service.test.mock.MathService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-spring-factory-context.xml")
public class TestSpringServiceFactory {
	
	
	/**
	 * Test spring factory with dynamic context
	 * 
	 * Create context dynamically with no xml configuration.  Then
	 * from the context create a service factory to test a service.
	 * 
	 */
	@Test
	public void testDynamicContext() {
		
		ServiceConfig serviceConfig = new DefaultServiceConfig();
		serviceConfig.getServicePlugins().add(new AnnotatedValidationPlugin());
		
		SpringServiceFactory serviceFactory = new SpringServiceFactory(serviceConfig, new String[] {"core.service.test.mock"});

		MathService mathService = (MathService) serviceFactory.createService(MathService.class);
		
		Assert.assertEquals(new Integer(2), mathService.divide(4, 2));
		
		
		
	}
	
}
