package core.service.invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import core.service.annotation.ApplyRules;
import core.service.exception.ServiceException;
import core.service.result.ServiceResult;
import core.service.rule.RuleExecutor;
import core.service.server.ServiceRequest;
import core.service.session.ClientServiceSession;

public class RuleInvocationAction implements ServiceInvocationAction
{
	@Autowired
	private RuleExecutor ruleExecutor;

	@Override
	public void executeAction(ClientServiceSession session, ServiceRequest request, ServiceResult result)
	{
        List<Object> objects = new ArrayList<Object>();
        
    	Class interfaceClass;
    	Method method;
		try
		{
			interfaceClass = Class.forName(request.getServiceInterfaceName());
			method = interfaceClass.getMethod(request.getMethodName(), request.getParamTypes());
		} catch (SecurityException e)
		{
			throw new ServiceException(e);
		} catch (ClassNotFoundException e)
		{
			throw new ServiceException(e);
		} catch (NoSuchMethodException e)
		{
			throw new ServiceException(e);
		}

    	ApplyRules context = method.getAnnotation(ApplyRules.class);
        
        if (context != null)
        {
            objects.addAll(Arrays.asList(request.getArguments()));
        }
        else
        {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int paramIndex = 0; paramIndex < parameterAnnotations.length; paramIndex++)
            {
                for (int annotIndex = 0; annotIndex < parameterAnnotations[paramIndex].length; annotIndex++)
                {
                    if (parameterAnnotations[paramIndex][annotIndex] instanceof ApplyRules)
                    {
                        objects.add(request.getArguments()[paramIndex]);
                    }
                }
            }
        }

        result = new ServiceResult();
        if (objects.size() > 0)
        {
            objects.add(result);
            ruleExecutor.execute(objects);
        }
	}

}
