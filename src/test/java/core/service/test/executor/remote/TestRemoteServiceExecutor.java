
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
 */package core.service.test.executor.remote;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.bus.socket.DedicatedConnectionManager;
import core.service.executor.remote.RemoteServiceExecutor;
import core.service.result.ServiceResult;
import core.service.server.ServiceInvokerServer;
import core.service.server.ServiceRequestImpl;
import core.service.test.mock.MathService;
import core.service.test.mock.SleepService;
import core.service.test.mock.ThrowExceptionService;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;
import core.tooling.property.SystemPropertyFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-core-service-context.xml")
public class TestRemoteServiceExecutor
{
    
    private static final Logger logger = LogFactory.getLogger(TestRemoteServiceExecutor.class);
    
    private ServiceInvokerServer server;
    
    @Autowired
    private ApplicationContext context;

    @After
    public void finish()
    {
        try
        {
            DedicatedConnectionManager.getInstance().disconnect();
        }
        catch (IOException e)
        {
        }
        server.shutdown();
        pause();
    }
    
    @Before
    public void setup()
    {
        pause();
        server = new ServiceInvokerServer();
        server.listen();
    }

    /**
     * Need to add some wait time so sockets can reset in the OS
     */
    private void pause()
    {
        // give the port some time to be released
        try
        {
            Thread.sleep(4000);
        }
        catch (InterruptedException e)
        {
        }
    }

    @Test(timeout=60000)
    public void testExecute() throws SecurityException, NoSuchMethodException
    {
        RemoteServiceExecutor executor = new RemoteServiceExecutor();
        Method add = MathService.class.getMethod("add", Integer.class, Integer.class);
        ServiceRequestImpl request = new ServiceRequestImpl();
        request.setArguments(new Object[] {2, 2});
        request.setParamTypes(new Class[] {Integer.class, Integer.class});
        request.setMethodName("add");
        request.setServiceInterfaceClassName(MathService.class.getName());
        
        ServiceResult result = executor.execute(request);
        
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(4, result.getPayload());                
    }

    @Test(timeout=60000)
    public void testExecute_concurrent() throws SecurityException, NoSuchMethodException, InterruptedException
    {
        int max = 20;
        final CountDownLatch latch = new CountDownLatch(max);
        
        for (int count = 0; count < max; count++)
        {
            new Thread() 
            {
                public void run() 
                {
                    try
                    {
                        RemoteServiceExecutor executor = (RemoteServiceExecutor) context.getBean("remoteServiceExecutor");
                        
                        // invoke math service
                        ServiceRequestImpl request = new ServiceRequestImpl();
                        request.setArguments(new Object[] {2, 2});
                        request.setParamTypes(new Class[] {Integer.class, Integer.class});
                        request.setMethodName("add");
                        request.setServiceInterfaceClassName(MathService.class.getName());
                        ServiceResult result = executor.execute(request);
                        
                        Assert.assertTrue(result.isSuccess());
                        Assert.assertEquals(4, result.getPayload());
                        
                        // invoke exception service
                        request = new ServiceRequestImpl();
                        request.setArguments(null);
                        request.setParamTypes(null);
                        request.setMethodName("throwException");
                        request.setServiceInterfaceClassName(ThrowExceptionService.class.getName());
                        ServiceResult exceptionResult = executor.execute(request);
                        Assert.assertTrue(exceptionResult.isException());

                        // invoke sleep service
                        request = new ServiceRequestImpl();
                        request.setArguments(new Object[] {1000L});
                        request.setParamTypes(new Class[] {long.class});
                        request.setMethodName("sleep");
                        request.setServiceInterfaceClassName(SleepService.class.getName());
                        executor.execute(request);
                    }
                    catch (SecurityException e)
                    {
                    }
                    synchronized (TestRemoteServiceExecutor.this) 
                    {
                        latch.countDown();
                    }
                }
            }.start();
        }
        
        latch.await();
        logger.info("Finished remote service executor test.");
    }

    @Test(timeout=60000)
    public void testExecute_exception() throws SecurityException, NoSuchMethodException
    {
        RemoteServiceExecutor executor = new RemoteServiceExecutor();
        ServiceRequestImpl request = new ServiceRequestImpl();
        request.setArguments(null);
        request.setParamTypes(null);
        request.setMethodName("throwException");
        request.setServiceInterfaceClassName(ThrowExceptionService.class.getName());
        ServiceResult result = executor.execute(request);
        
        Assert.assertTrue(result.isException());
    }

    @Test(timeout=60000)
    public void testExecute_multiple() throws SecurityException, NoSuchMethodException
    {
        RemoteServiceExecutor executor = new RemoteServiceExecutor();

        ServiceRequestImpl request = new ServiceRequestImpl();
        request.setArguments(new Object[] {2, 2});
        request.setParamTypes(new Class[] {Integer.class, Integer.class});
        request.setMethodName("add");
        request.setServiceInterfaceClassName(MathService.class.getName());

        ServiceResult result = executor.execute(request);
        
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(4, result.getPayload());                

        request = new ServiceRequestImpl();
        request.setArguments(null);
        request.setParamTypes(null);
        request.setMethodName("throwException");
        request.setServiceInterfaceClassName(ThrowExceptionService.class.getName());

        result = executor.execute(request);
        
        Assert.assertTrue(result.isException());
    }
}
