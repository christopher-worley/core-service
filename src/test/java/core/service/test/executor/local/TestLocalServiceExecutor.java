
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

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.executor.local.LocalServiceExecutor;
import core.service.result.ServiceResult;
import core.service.test.mock.MathService;
import core.tooling.property.SystemPropertyFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-core-service-context.xml")
public class TestLocalServiceExecutor
{

    @Before
    public void setup()
    {
        new SystemPropertyFileReader("test-service.properties");
    }
    
    @Test
    public void testDoExecute() throws SecurityException, NoSuchMethodException 
    {
        LocalServiceExecutor executor = new LocalServiceExecutor();
        
        Method addMethod = MathService.class.getMethod("add", Integer.class, Integer.class);
        
        ServiceResult result = executor.execute(MathService.class, addMethod, new Class[] {Integer.class, Integer.class}, new Object[] {2, 2});
        
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(new Integer(4), result.getPayload());
    }

}
