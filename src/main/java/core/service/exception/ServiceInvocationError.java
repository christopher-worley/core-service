package core.service.exception;

public class ServiceInvocationError extends ServiceException
{

	public ServiceInvocationError()
	{
		super();
	}

	public ServiceInvocationError(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ServiceInvocationError(String message)
	{
		super(message);
	}

	public ServiceInvocationError(Throwable cause)
	{
		super(cause);
	}

}
