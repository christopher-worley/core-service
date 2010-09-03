
/**
 * Copyright 2009 Core Information Solutions LLC
 *
 * This file is part of Core Service Framework.
 *
 * Core Service Framework is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * Core Service Framework is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Core Service Framework.  If not, see 
 * <http://www.gnu.org/licenses/>.
 */package core.service.test.rule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.proxy.ServiceProxy;
import core.service.result.ServiceResult;
import core.service.rule.RuleExecutor;
import core.service.test.mock.Applicant;
import core.service.test.mock.Application;
import core.service.test.mock.ProcessApplicationService;
import core.service.util.ServiceContextUtil;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-core-service-context.xml")
public class TestProcessApplication implements ApplicationContextAware
{
    
    private static Logger logger = LogFactory.getLogger(TestProcessApplication.class);
    
    private ApplicationContext applicationContext;

    @Before
    public void setup()
    {
        RuleExecutor.addResources(new String[] {"rules/valid-application.drl"});
        ServiceContextUtil.setApplicationContext(applicationContext);
    }
 
    
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }



    @Test
    public void testGunLicensee()
    {
        ProcessApplicationService service = (ProcessApplicationService) ServiceProxy.newInstance(ProcessApplicationService.class, applicationContext);
        
        // invalid application
        Application application = new Application(Application.TYPE_GUN_LICENSEE);
        Applicant applicant = new Applicant("Henry", "Simpson", 17);
        application.setApplicant(applicant);
        
        ServiceResult<Application> result = service.applyForGun(application, applicant);
        Assert.assertTrue(result.isError());
        logger.debug(result.getMessage());

        // acceptable application
        application = new Application(Application.TYPE_GUN_LICENSEE);
        applicant = new Applicant("Gene", "Hall", 20);
        
        result = service.applyForGun(application, applicant);
        Assert.assertTrue(result.isSuccess());
        application = result.getPayload();
        Assert.assertTrue(application.isAccepted());
        Assert.assertNotNull(application.getLicenseeNumber());
        
    }
    
    @Test
    public void testDriverLicesee()
    {
        ProcessApplicationService service = (ProcessApplicationService) ServiceProxy.newInstance(ProcessApplicationService.class, applicationContext);

        // invalid application
        Application application = new Application(Application.TYPE_DRIVER_LICENSEE);
        Applicant applicant = new Applicant("Joe", "Quigley", 13);
        application.setApplicant(applicant);
        
        ServiceResult<Application> result = service.applyForDriving(application, applicant);
        Assert.assertTrue(result.isError());
        logger.debug(result.getMessage());
        
        // acceptable application
        application = new Application(Application.TYPE_DRIVER_LICENSEE);
        applicant = new Applicant("Jerry", "Cantrell", 16);
        application.setApplicant(applicant);
        
        result = service.applyForDriving(application, applicant);
        Assert.assertTrue(result.isSuccess());
        application = result.getPayload();
        Assert.assertTrue(application.isAccepted());
        Assert.assertNotNull(application.getLicenseeNumber());
    }
}
