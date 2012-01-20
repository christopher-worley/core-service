package core.service.invocation;

import core.service.result.ServiceResult;
import core.service.server.ServiceRequest;
import core.service.session.ClientServiceSession;

/**
 * When services are executed invocation actions can be
 * executed before or after the service invocation.
 * 
 * @author cworley
 *
 */
public interface ServiceInvocationAction
{
	
	public void executeAction(ClientServiceSession session, ServiceRequest request, ServiceResult result);

}
