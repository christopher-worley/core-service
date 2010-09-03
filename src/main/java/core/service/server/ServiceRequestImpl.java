package core.service.server;

import java.io.Serializable;

public class ServiceRequestImpl implements ServiceRequest, Serializable
{
	private Object[] arguments;
	
	private String methodName;
	
	private String serviceInterfaceClassName;
	
	private Class[] paramTypes;

	@Override
	public Object[] getArguments()
	{
		return arguments;
	}

	@Override
	public String getMethodName()
	{
		return methodName;
	}

	@Override
	public Class[] getParamTypes()
	{
		return paramTypes;
	}

	public String getServiceInterfaceClassName()
	{
		return serviceInterfaceClassName;
	}

	@Override
	public String getServiceInterfaceName()
	{
		return serviceInterfaceClassName;
	}

	public void setArguments(Object[] arguments)
	{
		this.arguments = arguments;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	public void setParamTypes(Class[] paramTypes)
	{
		this.paramTypes = paramTypes;
	}

	public void setServiceInterfaceClassName(String serviceInterfaceClassName)
	{
		this.serviceInterfaceClassName = serviceInterfaceClassName;
	}

}
