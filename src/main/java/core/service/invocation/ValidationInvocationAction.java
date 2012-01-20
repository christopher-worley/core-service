package core.service.invocation;

import core.service.result.ServiceResult;
import core.service.server.ServiceRequest;
import core.service.session.ClientServiceSession;
import core.service.validation.ValidationExecutor;

public class ValidationInvocationAction implements ServiceInvocationAction
{

	@Override
	public void executeAction(ClientServiceSession session, ServiceRequest request, ServiceResult result)
	{
		ValidationExecutor validation = new ValidationExecutor(request.getArguments());
		validation.validate();
	}

}
