package core.service.test.mock;

import core.service.factory.DefaultServiceFactory;

/**
 * Factory for mock services.  
 * 
 * Using DefaultServiceFactory all mock services are added
 * and ready ready for use after instantiating class.
 * 
 * @author cworley
 *
 */
public class TestMockServiceFactory extends DefaultServiceFactory {

	public TestMockServiceFactory() {
		super();
		addServices();
	}

	private void addServices() {
		addService(MathService.class, MathServiceImpl.class);
		addService(ProcessApplicationService.class, ProcessApplicationServiceImpl.class);
		addService(SleepService.class, SleepServiceImpl.class);
	}

}
