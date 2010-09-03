package core.service.test.executor.mina;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.executor.mina.MinaExecutorServer;
import core.service.result.ServiceResult;
import core.service.security.SecurityEntity;
import core.service.session.ClientServiceSession;
import core.service.session.ClientServiceSessionFactory;
import core.service.test.mock.Applicant;
import core.service.test.mock.Application;
import core.service.test.mock.MockPermission;
import core.service.test.mock.MockSecurityEntity;
import core.service.test.mock.ProcessApplicationService;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;
import core.tooling.property.SystemPropertyFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-core-remote-service-context.xml")
public class TestMinaExecutor
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(TestMinaExecutor.class);
	
	/** mina server */
    private MinaExecutorServer server;

    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private ClientServiceSession session;

	@Before
    public void setup() throws IOException
    {
    	SecurityEntity sysadminEntity = new MockSecurityEntity("sysadmin", new String[] {MockPermission.ADD, MockPermission.SUBTRACT});
    	SecurityEntity joeEntity = new MockSecurityEntity("joe.blow", new String[] {MockPermission.ADD});

    	ClientServiceSessionFactory sessionFactory = (ClientServiceSessionFactory) context.getBean("clientServiceSessionFactory");
    	
    	logger.info("*** Invoking Service With 'sysadmin' Session ***");
    	session = sessionFactory.createSession(sysadminEntity);
        
//        // start mina server
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("core-service-context.xml");
        server = new MinaExecutorServer();
        server.start(applicationContext);
    }
    
    @Test
    public void testDoExecute()  
    {
    	ProcessApplicationService applicationService = (ProcessApplicationService) session.createService(ProcessApplicationService.class);
    	
        // acceptable application
        Application application = new Application(Application.TYPE_DRIVER_LICENSEE);
        Applicant applicant = new Applicant("Jerry", "Cantrell", 16);
        application.setApplicant(applicant);

        ServiceResult result = applicationService.applyForDriving(application, applicant);
        if (result.isSuccess()) 
        {
        	logger.info("Driving application accepted.");
        } else 
        {
        	logger.info("Driving application not accepted: " + result.getMessage());
        }
    }

}
