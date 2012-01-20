package core.service.test.validation;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.validation.DefaultAnnotationValidator;
import core.service.validation.Max;
import core.service.validation.ServiceValidationException;
import core.service.validation.Validator;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;
import core.tooling.property.SystemPropertyFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-core-service-context.xml")
public class TestMax
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(TestMax.class);
	
    @Autowired
    private ApplicationContext context;

    @Before
    public void setup()
    {
        new SystemPropertyFileReader("test-service.properties");
    }
    
    @Test
    public void testMax() 
    {
    	final class MaxClass
    	{
    		@Max(value=10,message="integer")
    		private Integer integer;
    		
    		@Max(value=100.1,message="money")
    		private Double money;

			public Integer getInteger()
			{
				return integer;
			}

			public Double getMoney()
			{
				return money;
			}

			public void setInteger(Integer integer)
			{
				this.integer = integer;
			}

			public void setMoney(Double money)
			{
				this.money = money;
			}
    	}
    	
    	// Create object to validate
    	MaxClass maxClass = new MaxClass();
    	maxClass.setInteger(5);
    	maxClass.setMoney(50.0);
    	
    	Validator executor = new DefaultAnnotationValidator(new Object[] {maxClass});
    	// validate valid initial values
    	executor.validate();

    	// set to higher value that will fail
    	maxClass.setInteger(11);
    	try
    	{
    		executor.validate();
    	}
    	catch (ServiceValidationException e)
    	{
    		Assert.assertEquals("integer", e.getMessage());
    	}

    	// set integer back to valid value, set money to invalid value
    	maxClass.setInteger(5);
    	maxClass.setMoney(200.5);
    	
    	try
    	{
    		executor.validate();
    	}
    	catch (ServiceValidationException e)
    	{
    		Assert.assertEquals("money", e.getMessage());
    	}
    	
    }

}
