package core.service.test.session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.exception.ServiceException;
import core.service.exception.ServiceSecurityException;
import core.service.security.SecurityEntity;
import core.service.session.ClientServiceSession;
import core.service.session.ClientServiceSessionFactory;
import core.service.test.mock.MathService;
import core.service.test.mock.MockPermission;
import core.service.test.mock.MockSecurityEntity;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;
import core.tooling.property.SystemPropertyFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-core-service-context.xml")
public class TestServiceSession {
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(TestServiceSession.class);
	
    @Autowired
    private ApplicationContext context;

	
    @Before
    public void setup()
    {
        new SystemPropertyFileReader("test-service.properties");
    }

    /**
     * Create two users.  Give sysadmin both permissions and give joe one permission.
     * Invoke the services for the permission and expect them to be successful.
     */
    @Test
    public void testServiceSession_withPermission() {
    	SecurityEntity sysadminEntity = new MockSecurityEntity("sysadmin", new String[] {MockPermission.ADD, MockPermission.SUBTRACT});
    	SecurityEntity joeEntity = new MockSecurityEntity("joe.blow", new String[] {MockPermission.ADD});

    	ClientServiceSessionFactory sessionFactory = (ClientServiceSessionFactory) context.getBean("clientServiceSessionFactory");
    	
    	logger.info("*** Invoking Service With 'sysadmin' Session ***");
    	ClientServiceSession sysadminSession = sessionFactory.createSession(sysadminEntity);

    	MathService sysadminMathService = (MathService) sysadminSession.createService(MathService.class);
    	Integer twoPlusTwo = sysadminMathService.add(2, 2);
    	System.out.println("2 + 2 = " + twoPlusTwo);
    	Integer twoMinusTwo = sysadminMathService.subtract(2, 2);
    	System.out.println("2 - 2 = " + twoMinusTwo);
    	
    	logger.info("*** Invoking Service With 'joe' Session ***");
    	ClientServiceSession joeSession = sessionFactory.createSession(joeEntity);

    	MathService joeMathService = (MathService) joeSession.createService(MathService.class);
    	twoPlusTwo = joeMathService.add(2, 2);
    	System.out.println("2 + 2 = " + twoPlusTwo);
    	
    }

    /**
     * Only give joe ADD permission.  In the test perform subtract and expect a failure.
     */
    @Test(expected=ServiceSecurityException.class)
    public void testServiceSession_withNoPermission() {
    	SecurityEntity joeEntity = new MockSecurityEntity("joe.blow", new String[] {MockPermission.ADD});

    	ClientServiceSessionFactory sessionFactory = (ClientServiceSessionFactory) context.getBean("clientServiceSessionFactory");
    	
    	logger.info("*** Invoking Service With 'joe' Session, NO PERMISSION ***");
    	ClientServiceSession joeSession = sessionFactory.createSession(joeEntity);

    	MathService joeMathService = (MathService) joeSession.createService(MathService.class);
    	// Joe does not have subtract permission, this will fail.  We throw the cause 
    	try {
        	Integer twoMinusTwo = joeMathService.subtract(2, 2);
        	System.out.println("2 - 2 = " + twoMinusTwo);
    	} catch (ServiceException e) {
    		throw (ServiceSecurityException) e.getCause();
    	}
    }
}
