package core.service.invocation;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import core.service.Security;
import core.service.exception.ServiceException;
import core.service.result.ServiceResult;
import core.service.security.ServiceSecurity;
import core.service.server.ServiceRequest;
import core.service.session.ClientServiceSession;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class SecurityInvocationAction implements ServiceInvocationAction
{
	
	private static final Logger logger = LogFactory.getLogger(SecurityInvocationAction.class);
	
	@Autowired
	private ApplicationContext context;

	@Override
	public void executeAction(ClientServiceSession session, ServiceRequest request, ServiceResult result)
	{
		// get service class and method
		Class serviceInterface;
		Method method;
		try
		{
			serviceInterface = Class.forName(request.getServiceInterfaceName());
			method = serviceInterface.getMethod(request.getMethodName(), request.getParamTypes());
		}
		catch (SecurityException e)
		{
			throw new ServiceException(e);
		}
		catch (NoSuchMethodException e)
		{
			throw new ServiceException(e);
		} 
		catch (ClassNotFoundException e)
    	{
			throw new ServiceException(e);
    	}
		
        Security security = method.getAnnotation(Security.class);
        
        // If no Security annotation exist then service does not require and security
        if (security == null)
        {
            logger.debug("No security required for service (serviceInterface={0},method={1}).",
                    request.getServiceInterfaceName(),
                    method.getName());
            return;
        }
        
        // check permissions
        ServiceSecurity serviceSecurity = (ServiceSecurity) context.getBean("serviceSecurity");
        logger.debug("Authenticating service request (session={0},serviceInterface={1},method={2},securityClass={3}).",
        		session,
                serviceInterface.getName(),
                method.getName(),
                serviceSecurity.getClass().getName());
        serviceSecurity.authenticate(session, serviceInterface, method, request.getArguments());
	}

}
