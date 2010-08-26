package core.service.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import core.service.exception.ServiceException;
import core.service.util.NumberComparator;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class DefaultAnnotationValidator implements Validator
{
	/** logger for this class */
	Logger logger = LogFactory.getLogger(DefaultAnnotationValidator.class);

	/** objects to validate */
	Object[] objects;

	/**
	 * @param method
	 * @param objects
	 */
	public DefaultAnnotationValidator(Object[] objects) 
	{
		super();
		this.objects = objects;
	}
	
	
	/**
	 * @param object
	 * @param field
	 * @param annotation
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void doMax(Object object, Field field, Annotation annotation) throws IllegalArgumentException, IllegalAccessException
	{
		logger.debug("Max value validation found on field (object=" 
				+ object.getClass().getSimpleName()
				+ ",field="
				+ field.getName()
				+ ",annotation=" 
				+ annotation
				+ ").");
		
		// cast annotation
		Max max = ((Max)annotation);
		// get field value
		Object value = field.get(object);
		
		if (value instanceof Number
				&& new NumberComparator().compare(value, max.value()) > 0) 
		{
			logger.debug("Max value exceeded for field (object="
        			+ object.getClass().getSimpleName()
        			+ ",field="
        			+ field.getName()
        			+ ",value="
        			+ value
        			+ ")");
			throw new ServiceValidationException(max.message());
		}
	}
	
	/**
	 * @param object
	 * @param field
	 * @param annotation
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void doMin(Object object, Field field, Annotation annotation) throws IllegalArgumentException, IllegalAccessException
	{
		logger.debug("Min value validation found on field (object=" 
				+ object.getClass().getSimpleName()
				+ ",field="
				+ field.getName()
				+ ",annotation=" 
				+ annotation
				+ ").");
		
		// cast annotation
		Min min = ((Min)annotation);
		// get field value
		Object value = field.get(object);
		
		if (value instanceof Number
				&& new NumberComparator().compare(min.value(), value) > -1) 
		{
			logger.debug("Min value exceeded for field (object="
        			+ object.getClass().getSimpleName()
        			+ ",field="
        			+ field.getName()
        			+ ",value="
        			+ value
        			+ ")");
			throw new ServiceValidationException(min.message());
		}
	}


	/**
	 * @param object
	 * @param field
	 * @param annotation
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void doRequired(Object object, Field field, Annotation annotation) throws IllegalArgumentException, IllegalAccessException
	{
		logger.debug("Required value validation found on field (object=" 
				+ object.getClass().getSimpleName()
				+ ",field="
				+ field.getName()
				+ ",annotation=" 
				+ annotation
				+ ").");
		
		Required required = (Required) annotation;

		Object value = field.get(object);
		
		if (value == null) 
		{
			logger.debug("Required field cannot have null value (object="
        			+ object.getClass().getSimpleName()
        			+ ",field="
        			+ field.getName()
        			+ ",value="
        			+ value
        			+ ")");
			throw new ServiceValidationException(required.message());
		}
	}


	/**
	 * @param object
	 * @param field
	 * @param annotation
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void doValidateString(Object object, Field field,
			Annotation annotation) throws IllegalArgumentException, IllegalAccessException
	{
		logger.debug("String validation found on field (object=" 
				+ object.getClass().getSimpleName()
				+ ",field="
				+ field.getName()
				+ ",annotation=" 
				+ annotation
				+ ").");
		
		StringValidation stringValidation = ((StringValidation)annotation);
		Object value = field.get(object);
		
		// verify java.lang.String type is annotated
		if (!field.getType().equals(java.lang.String.class)) 
		{
			throw new IllegalArgumentException("StringValidation can only annotate java.lang.String types (object="
					+ object.getClass().getSimpleName()
					+ ",field="
					+ field.getName()
					+ ").");
		}
		
		// cast a string value
		String stringValue = (String) value;
		String info = "(object="
			+ object.getClass().getSimpleName()
			+ ",field="
			+ field.getName()
			+ ",value="
			+ stringValue
			+ ")";
		
		// validate annotation options
		if (!stringValidation.allowNull()
				&& value == null) 
		{
			logger.debug("String validation does not allow null, value was found to be null " + info + ".");
			throw new ServiceValidationException(stringValidation.message());
		}
		else if (!stringValidation.allowEmpty()
				&& (stringValue != null && stringValue.length() < 1))
		{
			logger.debug("String validation does not allow empty strings, value was found to be empty " + info + ".");
			throw new ServiceValidationException(stringValidation.message());
		}
		// only check if stringValue is not empty
		else if (stringValue != null 
				&& stringValue.length() > 0 
				&& stringValidation.anyOf() != null 
				&& stringValidation.anyOf().trim().length() > 0) 
		{
			String[] anyOf = stringValidation.anyOf().split(",");
			boolean found = false;
			for (String oneOf : anyOf) 
			{
				if (oneOf.equals(stringValue)) 
				{
					found = true;
				}
			}
			if (!found) 
			{
    			logger.debug("String validation provides a list of possible values, the string does not equal any of the given values " + info + ".");
    			throw new ServiceValidationException(stringValidation.message());
			}
		}
		else if (stringValidation.maxSize() > -1 
				&& stringValue != null
				&& stringValue.length() > stringValidation.maxSize())
			
		{
			logger.debug("String validation limits the max size of the string, the value of the string was found to exceed the limit " + info + ".");
			throw new ServiceValidationException(stringValidation.message());
		}
		
	}


	/* (non-Javadoc)
	 * @see core.service.validation.Validator#validate()
	 */
	public void validate() 
	{
		logger.debug("Validating objects (count=" + (objects == null ? "null" : objects.length) + ").");
		if (objects != null) 
		{
			for (Object object : objects)
			{
				try
				{
					validate(object);
				} 
				catch (IllegalArgumentException e)
				{
					throw new ServiceException("Failed to validate objects.", e);
				} 
				catch (IllegalAccessException e)
				{
					throw new ServiceException("Failed to validate objects.", e);
				} 
				catch (InstantiationException e)
				{
					throw new ServiceException("Failed to validate objects.", e);
				} 
				catch (SecurityException e)
				{
					throw new ServiceException("Failed to validate objects.", e);
				} 
				catch (NoSuchMethodException e)
				{
					throw new ServiceException("Failed to validate objects.", e);
				} 
				catch (InvocationTargetException e)
				{
					throw new ServiceException("Failed to validate objects.", e);
				}
			}
		}
	}


	/**
	 * find any validation annotations on class fields.  Validate any that
	 * are found.
	 * 
	 * @param object
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	void validate(Object object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, SecurityException, NoSuchMethodException, InvocationTargetException 
	{
		Field[] fields = object.getClass().getDeclaredFields();
		if (fields != null) 
		{
			logger.debug("Validating fields for object (objectClass=" + object.getClass().getSimpleName() + ",fieldCount=" + fields.length + ").");
    		for (Field field : fields) 
    		{
    			field.setAccessible(true);
    			validate(object, field);
    		}
		}
		else 
		{
			logger.debug("No fields found for object (objectClass=" + object.getClass().getSimpleName() + ").");
		}
	}


	/**
	 * find any validation annotations on the field.  Validate any that 
	 * are found.
	 * 
	 * @param object
	 * @param field
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	private void validate(Object object, Field field) throws IllegalArgumentException, IllegalAccessException, InstantiationException, SecurityException, NoSuchMethodException, InvocationTargetException 
	{
		Annotation[] annotations = field.getAnnotations();
		if (annotations != null) 
		{
			for (Annotation annotation : annotations) 
			{
				if (annotation instanceof StringValidation)
				{
					doValidateString(object, field, annotation);
				} 
				else if (annotation instanceof Required) 
				{
					doRequired(object, field, annotation);
				}
				else if (annotation instanceof Max) 
				{
					doMax(object, field, annotation);
				}
				else if (annotation instanceof Min) 
				{
					doMin(object, field, annotation);
				}
				else if (annotation instanceof Validate) 
				{
					doValidate(object, field, annotation);
				}
				else
				{
					validate(object, field, annotation);
				}
			}
		}
	}


	/**
	 * @param object
	 * @param field
	 * @param annotation
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	private void doValidate(Object object, Field field, Annotation annotation) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
	{
		// cast validate annotation
		Validate validate = (Validate) annotation;
		// get constructor 
		Constructor constructor = validate.validationClass().getConstructor(Object[].class);
		// instantiate validator class for validating the value of the field
		Object[] args = new Object[]{field.get(object)};
		// pass array to constructor, wrap in Object[] to deal with java 1.5 ... args
		Validator validator = (Validator) constructor.newInstance(new Object[] {args});
		// validate 
		try 
		{
			validator.validate();
		}
		catch (ServiceValidationException e)
		{
			throw new ServiceValidationException(validate.message() + ": " + e.getMessage());
		}
	}


	/**
	 * Override to implement validation for more annotations
	 * 
	 * @param object
	 * @param field
	 * @param annotation
	 */
	protected void validate(Object object, Field field, Annotation annotation)
	{
		logger.debug("Not validation annotation: " + annotation.getClass().getSimpleName());
	}
	
}
