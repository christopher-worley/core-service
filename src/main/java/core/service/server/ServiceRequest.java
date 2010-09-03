package core.service.server;

public interface ServiceRequest<T>
{

	String getServiceInterfaceName();
	
	String getMethodName();
	
	Class[] getParamTypes();
	
	Object[] getArguments();
}
