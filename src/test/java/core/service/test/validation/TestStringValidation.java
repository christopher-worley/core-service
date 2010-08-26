package core.service.test.validation;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.service.validation.ServiceValidationException;
import core.service.validation.StringValidation;
import core.service.validation.DefaultAnnotationValidator;
import core.service.validation.Validator;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;
import core.tooling.property.SystemPropertyFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-core-service-context.xml")
public class TestStringValidation
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(TestStringValidation.class);
	
    @Autowired
    private ApplicationContext context;

    @Before
    public void setup()
    {
        new SystemPropertyFileReader("test-service.properties");
    }

    @Test
    public void testStringValidation_allowEmpty() 
    {
        
    	final class ValidateClass 
        {
        	@StringValidation(allowEmpty=true, allowNull=true, message="canBeEmpty")
        	private String canBeEmpty;
        	
        	@StringValidation(allowEmpty=false, message="cannotBeEmpty")
        	private String cannotBeEmpty;

			public String getCanBeEmpty()
			{
				return canBeEmpty;
			}

			public String getCannotBeEmpty()
    		{
    			return cannotBeEmpty;
    		}
        	
    		public void setCanBeEmpty(String canBeEmpty)
			{
				this.canBeEmpty = canBeEmpty;
			}

    		public void setCannotBeEmpty(String cannotBeEmpty)
    		{
    			this.cannotBeEmpty = cannotBeEmpty;
    		}

        }

    	// create class to validate with valid initial values
        ValidateClass validate = new ValidateClass();
        validate.setCannotBeEmpty("zzz");
        validate.setCanBeEmpty("");
    	
    	// should be ok
    	Validator executor = new DefaultAnnotationValidator(new Object[] {validate});
    	executor.validate();

    	// should be ok
    	validate.setCanBeEmpty(null);
    	executor.validate();

    	// should be ok
    	validate.setCanBeEmpty("abc");
    	executor.validate();

    	// should fail since it can't be empty
        validate.setCannotBeEmpty("");
        try 
        {
        	executor.validate();
        }
        catch (ServiceValidationException e)
        {
        	Assert.assertEquals("cannotBeEmpty", e.getMessage());
        }
    	
    }

    @Test
    public void testStringValidation_allowNull() 
    {
    	/**
    	 * Class to validate, validation message string will be used 
    	 * to fail test when catching the exception
    	 * 
    	 * @author cworley
    	 *
    	 */
    	final class ValidateClass 
        {
        	@StringValidation(allowNull=true, message="should not fail")
        	private String canBeNull;

        	@StringValidation(allowNull=false, message="its ok to fail!")
        	private String cannotBeNull;

    		public String getCanBeNull()
    		{
    			return canBeNull;
    		}

			public String getCannotBeNull()
			{
				return cannotBeNull;
			}

			public void setCanBeNull(String canBeNull)
    		{
    			this.canBeNull = canBeNull;
    		}

    		public void setCannotBeNull(String cannotBeNull)
			{
				this.cannotBeNull = cannotBeNull;
			}
        }

    	// create object to validate
        ValidateClass validate = new ValidateClass();
        validate.setCanBeNull(null);
        validate.setCannotBeNull("its not null, because it can't be");
    	
        // everything should be ok
    	Validator executor = new DefaultAnnotationValidator(new Object[] {validate});
    	executor.validate();
    	
        // everything should be ok
    	validate.setCanBeNull("not null");
    	executor.validate();
    	
        // should throw exception, check validation message to validate the correct exception was thrown
    	validate.setCannotBeNull(null);
    	try 
    	{
    		executor.validate();
    	}
    	catch (ServiceValidationException e) 
    	{
    		Assert.assertEquals("its ok to fail!", e.getMessage());
    	}
    }

    @Test
    public void testStringValidation_anyOf() 
    {
        
        final class ValidateClass 
        {
        	
        	@StringValidation(anyOf="apple,orange,grape",allowNull=true,message="anyOfTheseFruits")
        	private String someFruits;
    		public String getSomeFruits()
    		{
    			return someFruits;
    		}

    		public void setSomeFruits(String someFruits)
    		{
    			this.someFruits = someFruits;
    		}
        }

        // create object to validate with valid inital value
        ValidateClass validate = new ValidateClass();
        validate.setSomeFruits("grape");
    	
    	Validator executor = new DefaultAnnotationValidator(new Object[] {validate});
    	executor.validate();
    	
    	// change to another valid value
    	validate.setSomeFruits("apple");
    	executor.validate();
    	
    	// change to an empty string
		validate.setSomeFruits("");
    	executor.validate();
    	
    	// set to null to test for NPE
		validate.setSomeFruits(null);
    	executor.validate();
    	
    	// change to an invalid value
		validate.setSomeFruits("onion");
    	try
    	{
        	executor.validate();
    	}
    	catch (ServiceValidationException e) 
    	{
    		Assert.assertEquals("anyOfTheseFruits",e.getMessage());
    	}
    }

    @Test
    public void testStringValidation_maxSize() 
    {
        
    	final class ValidateClass 
        {
        	@StringValidation(maxSize=10,allowNull=true,message="maxLength")
        	private String maxLength;
    		public String getMaxLength()
    		{
    			return maxLength;
    		}

    		public void setMaxLength(String maxLength)
    		{
    			this.maxLength = maxLength;
    		}

        }

    	// create object to validate with a valid initial value
        ValidateClass validate = new ValidateClass();
        validate.setMaxLength("0123456789");
    	
    	Validator executor = new DefaultAnnotationValidator(new Object[] {validate});
    	executor.validate();

    	// set to empty string
        validate.setMaxLength("");
        executor.validate();

        // set to null, to ensure no NPE
        validate.setMaxLength(null);
        executor.validate();

    	validate.setMaxLength("0123456789zzzzz");
    	try
    	{
    		executor.validate();
    	}
    	catch (ServiceValidationException e)
    	{
    		Assert.assertEquals("maxLength", e.getMessage());
    	}
    }
}
