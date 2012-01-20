package core.service.test.executor.mina;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import core.service.config.ServiceProperties;
import core.service.executor.mina.MinaExecutorServer;
import core.service.result.ServiceResult;
import core.service.security.SecurityEntity;
import core.service.session.ClientServiceSession;
import core.service.spring.context.ApplicationContextFactory;
import core.service.test.mock.Applicant;
import core.service.test.mock.Application;
import core.service.test.mock.MockPermission;
import core.service.test.mock.MockSecurityEntity;
import core.service.test.mock.ProcessApplicationService;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

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
    
    private ApplicationContext serverContext;
    
    private ApplicationContext clientContext;

	@Before
    public void setup() throws IOException
    {
    	SecurityEntity sysadminEntity = new MockSecurityEntity("sysadmin", new String[] {MockPermission.ADD, MockPermission.SUBTRACT});
    	SecurityEntity joeEntity = new MockSecurityEntity("joe.blow", new String[] {MockPermission.ADD});

		ApplicationContextFactory contextFactory = new ApplicationContextFactory();

		// initialize server
		ServiceProperties serverProperties = new ServiceProperties("local-service.properties");
		serverContext = contextFactory.createAnnotationConfigApplicationContext(
				serverProperties,
				new String[] {
						"core.service.test.mock"
				});


        server = new MinaExecutorServer();
        server.start(serverContext);
        
        // initialize client
		ServiceProperties clientProperties = new ServiceProperties("mina-service.properties");
		clientContext = contextFactory.createAnnotationConfigApplicationContext(
				clientProperties,
				new String[] {
						"core.service.test.mock"
				});
        
    }
	
	@After
	public void tearDown() {
		server.stop();
	}
    
    @Test
    public void testDoExecute()  
    {
    	ProcessApplicationService applicationService = (ProcessApplicationService) clientContext.getBean(ProcessApplicationService.class);
    	
        // acceptable application
        Application application = new Application(Application.TYPE_DRIVER_LICENSEE);
        Applicant applicant = new Applicant("Jerry", "Cantrell", 16);
        application.setApplicant(applicant);

        ServiceResult result = applicationService.applyForDriving(application, applicant);
        if (result.isSuccess()) 
        {
        	logger.info("Driving application accepted.");
        } 
        else 
        {
        	logger.info("Driving application not accepted: " + result.getMessage());
        }
    }
    
    @Test
    public void textNoServer() 
    {
    	server.stop();

    	ProcessApplicationService applicationService = (ProcessApplicationService) clientContext.getBean(ProcessApplicationService.class);
    	
        // acceptable application
        Application application = new Application(Application.TYPE_DRIVER_LICENSEE);
        Applicant applicant = new Applicant("Jerry", "Cantrell", 16);
        application.setApplicant(applicant);

        ServiceResult result = applicationService.applyForDriving(application, applicant);
        
        Assert.assertFalse(result.isSuccess());
    }

}
