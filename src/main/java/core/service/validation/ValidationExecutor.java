package core.service.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class ValidationExecutor 
{
	/** logger for this class */
	private Logger logger = LogFactory.getLogger(ValidationExecutor.class);

	/** objects to validate */
	private Object[] objects;

	/**
	 * @param method
	 * @param objects
	 */
	public ValidationExecutor(Object[] objects) 
	{
		super();
		this.objects = objects;
	}
	
	
	private void doRequired(Object object, Field field, Annotation annotation)
	{
		// TODO Auto-generated method stub
		
	}


	private void doValidateString(Object object, Field field,
			Annotation annotation)
	{
		// TODO Auto-generated method stub
		
	}


	/**
	 * 
	 */
	public void validate() 
	{
		if (objects != null) 
		{
			for (Object object : objects)
			{
				validate(object);
			}
		}
	}


	/**
	 * find any validation annotations on class fields.  Validate any that
	 * are found.
	 * 
	 * @param object
	 */
	private void validate(Object object) 
	{
		Field[] fields = object.getClass().getFields();
		for (Field field : fields) 
		{
			validate(object, field);
		}
	}


	/**
	 * find any validation annotations on the field.  Validate any that 
	 * are found.
	 * 
	 * @param object
	 * @param field
	 */
	private void validate(Object object, Field field) 
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
				else if (annotation instanceof Required) {
					doRequired(object, field, annotation);
				}
				else
				{
					logger.debug("Not validation annotation: " + annotation.getClass().getSimpleName());
				}
			}
		}
	}
	
}
