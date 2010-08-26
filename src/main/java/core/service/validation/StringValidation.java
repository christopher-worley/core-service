package core.service.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringValidation {
	
	/**
	 * String cannot be null and must have zero size
	 * 
	 * @return
	 */
    boolean allowEmpty() default true;

    /**
     * String cannot be null
     * 
     * @return
     */
    boolean allowNull() default false;
    
    /**
     * Comma separated list of acceptable values
     * 
     * If string is empty or null, check will not occur
     * 
     * @return
     */
    String anyOf() default "";
    
    /**
     * If not equal to -1 then check that string length <= maxSize value
     * 
     */
    int maxSize() default -1;

    /**
     * Message to report in ServiceValidationException
     * 
     * @return
     */
    String message() default "";
}
