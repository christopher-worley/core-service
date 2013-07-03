package core.service.test.factory.dynamic;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import core.service.exception.ServiceException;
import core.service.factory.DefaultServiceFactory;
import core.service.test.mock.MathService;
import core.service.test.mock.MathServiceImpl;

public class TestDefaultServiceFactory {
	
	/**
	 * testing purposes only, not functional
	 * 
	 * @author cworley
	 *
	 */
	private interface ZzzMockService {
		
		public void foobar();
		
	}
	
	/**
	 * testing purposes only, not functional
	 * 
	 * @author cworley
	 *
	 */
	private class ZzzMockServiceImpl implements ZzzMockService {

		@Override
		public void foobar() {
			System.out.println("ZzzMockService was -->here<-- !");
		}
		
	}
	
	/**
	 * testing purposes only, not functional
	 * 
	 * @author cworley
	 *
	 */
	private class ZzzZzzMockServiceImpl implements ZzzMockService {
 
		@Override
		public void foobar() {
			System.out.println("ZzzMockService was -->here<-- !");
		}
		
	}
	
	/**
	 * Test DefaultServiceFactory.addService(Class, Class)
	 * 
	 * Create instance of service factory, add some services and
	 * finally test that the service exist.
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void testAddService() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		DefaultServiceFactory factory = new DefaultServiceFactory();

		// Access the services map threw reflection to get an unaltered version of the factory registry.
		Field servicesField = DefaultServiceFactory.class.getDeclaredField("services");
		servicesField.setAccessible(true);
		Map<Class,Class> services = (Map<Class,Class>) servicesField.get(factory);

		// Test for first service
		Assert.assertNull(services.get(MathService.class));
		
		factory.addService(MathService.class, MathServiceImpl.class);
		
		Assert.assertNotNull(services.get(MathService.class));
		
		// Test for a second service
		Assert.assertNull(services.get(ZzzMockService.class));

		factory.addService(ZzzMockService.class, ZzzMockServiceImpl.class);
		
		Assert.assertNotNull(services.get(ZzzMockService.class));
	}
	
	/**
	 * Test DefaultServiceFactory.addService(Class, Class) null implementation not allowed
	 * 
	 * Create instance of <code>DefaultServiceFactory</code> and attempt to add a service
	 * interface with null for the implementation class.  This should always throw a 
	 * <code>ServiceException</code>.  
	 * 
	 */
	@Test(expected = ServiceException.class)
	public void testAddService_null() {
		DefaultServiceFactory factory = new DefaultServiceFactory();
		
		// should throw exception
		factory.addService(MathService.class, null);
	}
	
	/**
	 * Test DefaultServiceFactory.addService(Class, Class) overwrite functionality
	 * 
	 * Create instance of <code>DefaultServiceFactory</code> and add two services with 
	 * the same interface class.  This is allowed and the first implementation class
	 * should be overwritten in the factory registry.
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * 
	 */
	@Test
	public void testAddService_overwrite() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		DefaultServiceFactory factory = new DefaultServiceFactory();

		// Access the services map threw reflection to get an unaltered version of the factory registry.
		Field servicesField = DefaultServiceFactory.class.getDeclaredField("services");
		servicesField.setAccessible(true);
		Map<Class,Class> services = (Map<Class,Class>) servicesField.get(factory);

		factory.addService(ZzzMockService.class, ZzzMockServiceImpl.class);
		
		Assert.assertEquals(ZzzMockServiceImpl.class, services.get(ZzzMockService.class));
		
		// Overwrite the previous service and check for new service.
		factory.addService(ZzzMockService.class, ZzzZzzMockServiceImpl.class);
		
		Assert.assertEquals(ZzzZzzMockServiceImpl.class, services.get(ZzzMockService.class));
	}

}
