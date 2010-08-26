package core.service.test.validation;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.validation.Required;
import core.service.validation.ServiceValidationException;
import core.service.validation.DefaultAnnotationValidator;
import core.service.validation.Validator;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;
import core.tooling.property.SystemPropertyFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-core-service-context.xml")
public class TestRequired
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(TestRequired.class);
	
    @Autowired
    private ApplicationContext context;

    @Before
    public void setup()
    {
        new SystemPropertyFileReader("test-service.properties");
    }

    @Test
    public void testRequired() 
    {
    	final class RequiredClass
    	{
    		@Required(message="foobar")
    		private Object foobar;

			public Object getFoobar()
			{
				return foobar;
			}

			public void setFoobar(Object foobar)
			{
				this.foobar = foobar;
			}
    	}
    	
    	// create object to validate
    	RequiredClass required = new RequiredClass();
    	required.setFoobar(new Object());
    	
    	Validator executor = new DefaultAnnotationValidator(new Object[] {required});
    	executor.validate();
    	
    	// set to null, should fail validation
    	required.setFoobar(null);
    	try
    	{
    		executor.validate();
    	}
    	catch (ServiceValidationException e) 
    	{
    		Assert.assertEquals("foobar", e.getMessage());
    	}
    }
}
