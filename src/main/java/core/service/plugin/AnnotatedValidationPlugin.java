package core.service.plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Required;

import core.tooling.validation.Max;
import core.tooling.validation.Min;
import core.tooling.validation.NotEqual;

public class AnnotatedValidationPlugin implements ServicePlugin {

	@Override
	public void after(Object serviceObject, Method method, Class[] paramTypes,
			Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void before(Object serviceObject, Method method, Class[] paramTypes,
			Object[] args) {
		// get dual array of parameter annotations
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		for (int parameterIndex = 0; parameterIndex < args.length; parameterIndex++) {
			for (int annotationIndex = 0; annotationIndex < parameterAnnotations[parameterIndex].length; annotationIndex++) {
				Annotation annotation = parameterAnnotations[parameterIndex][annotationIndex];
				if (annotation instanceof Max) {
					System.out.println("max found");
				} else if (annotation instanceof Min) {
					System.out.println("min found");
				} else if (annotation instanceof NotEqual) {
					System.out.println("not equal found");
				} else if (annotation instanceof Required) {
					System.out.println("required found");
				}
					
			}
		}

	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
