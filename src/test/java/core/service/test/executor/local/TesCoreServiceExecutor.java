
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
 */package core.service.test.executor.local;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.config.DefaultServiceConfig;
import core.service.exception.ServiceException;
import core.service.executor.local.CoreServiceExecutor;
import core.service.plugin.AnnotatedValidationPlugin;
import core.service.test.mock.MathService;
import core.service.test.mock.MathServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-core-service-context.xml")
public class TesCoreServiceExecutor
{
	
	@Autowired
	private ApplicationContext context;

    @Before
    public void setup()
    {
    }
    
    /**
     * Executor service with CoreServiceExecutor in the simplest fasion.
     * 
     * Use DefaultServiceConfig and DefaultServiceFactory to setup test for MathService.
     * Invoke method on MathService and check the results.
     * 
     * This should test that the CoreServiceExecutor can invoke a service method using
     * the ServiceConfig interface.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void textExecute_default() throws SecurityException, NoSuchMethodException
    {
    	DefaultServiceConfig serviceConfig = new DefaultServiceConfig();
    	serviceConfig.getServiceFactory().addService(MathService.class, MathServiceImpl.class);
    	
    	CoreServiceExecutor executor = new CoreServiceExecutor(MathService.class, serviceConfig);
    	
    	MathService mathService = (MathService) java.lang.reflect.Proxy.newProxyInstance(
    			MathService.class.getClassLoader(),
                new Class[] {MathService.class}, 
                executor);
    	
    	int x = mathService.add(1, 1);
    	Assert.assertEquals("Math does not work out.", 2, x);
    	
    }
    
    @Test
    public void testExecutor_plugin() 
    {
    	DefaultServiceConfig serviceConfig = new DefaultServiceConfig();
    	serviceConfig.getServiceFactory().addService(MathService.class, MathServiceImpl.class);
    	serviceConfig.getServicePlugins().add(new AnnotatedValidationPlugin());
    	
    	CoreServiceExecutor executor = new CoreServiceExecutor(MathService.class, serviceConfig);
    	
    	MathService mathService = (MathService) java.lang.reflect.Proxy.newProxyInstance(
    			MathService.class.getClassLoader(),
                new Class[] {MathService.class}, 
                executor);
    	
    	int a = mathService.add(1, 1);
    	Assert.assertEquals("Math does not work out.", 2, a);
    }
    
    @Test(expected=ServiceException.class)
    public void testExecutor_pluginFail() 
    {
    	DefaultServiceConfig serviceConfig = new DefaultServiceConfig();
    	serviceConfig.getServiceFactory().addService(MathService.class, MathServiceImpl.class);
    	serviceConfig.getServicePlugins().add(new AnnotatedValidationPlugin());
    	
    	CoreServiceExecutor executor = new CoreServiceExecutor(MathService.class, serviceConfig);
    	
    	MathService mathService = (MathService) java.lang.reflect.Proxy.newProxyInstance(
    			MathService.class.getClassLoader(),
                new Class[] {MathService.class}, 
                executor);
    	
    	
    	int b = mathService.divide(1, 0);
    	
    }

}
