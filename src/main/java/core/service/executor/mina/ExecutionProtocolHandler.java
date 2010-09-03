package core.service.executor.mina;

import java.lang.reflect.Method;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.context.ApplicationContext;

import core.service.factory.ServiceFactory;
import core.service.result.ServiceResult;
import core.service.server.ServiceRequest;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class ExecutionProtocolHandler extends IoHandlerAdapter
{
	private final static Logger LOGGER = LogFactory.getLogger(ExecutionProtocolHandler.class);

	private ApplicationContext context;	
	
	/**
	 * @param context
	 */
	public ExecutionProtocolHandler(ApplicationContext context)
	{
		super();
		this.context = context;
	}

	@Override
	public void sessionCreated(IoSession session)
	{
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

//		// We're going to use SSL negotiation notification.
//		session.setAttribute(SslFilter.USE_NOTIFICATION);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception
	{
		LOGGER.info("CLOSED");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception
	{
		LOGGER.info("OPENED");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
	{
		LOGGER.info("*** IDLE #" + session.getIdleCount(IdleStatus.BOTH_IDLE) + " ***");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
	{
		LOGGER.error(cause.getMessage(), cause);
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception
	{
		LOGGER.info("Received : " + message);
		ServiceRequest request = (ServiceRequest) message;
		Class interfaceClass = Class.forName(request.getServiceInterfaceName());
		
		ServiceFactory serviceFactory = (ServiceFactory) context.getBean("serviceFactory");
		Object service = serviceFactory.createService(interfaceClass);
		Method serviceMethod = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
		Object returnValue = serviceMethod.invoke(service, request.getArguments());
		
		// if service result then return it
		if (returnValue instanceof ServiceResult) 
		{
			session.write(returnValue);
		}
		else 
		{
			session.write(ServiceResult.success("Message received and service invoked successfully.", returnValue));
		}
		
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception
	{
		LOGGER.info("Sent : " + message);

		// TODO Auto-generated method stub
		super.messageSent(session, message);
	}
	
	

}
