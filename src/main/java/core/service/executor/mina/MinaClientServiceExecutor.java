/**
 * 
 */
package core.service.executor.mina;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import core.service.config.ServiceProperties;
import core.service.exception.ServiceException;
import core.service.result.ServiceResult;
import core.service.server.ServiceRequestImpl;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * @author cworley
 *
 */
public class MinaClientServiceExecutor implements InvocationHandler
{
	
	private class Handler extends IoHandlerAdapter
	{

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			super.exceptionCaught(session, cause);
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception
		{
			super.messageReceived(session, message);
			result = (ServiceResult) message;
			latch.countDown();
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception
		{
			super.sessionClosed(session);
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception
		{
			super.sessionIdle(session, status);
		}
		
	}
	private Logger logger = LogFactory.getLogger(MinaClientServiceExecutor.class);
	
	private IoSession session;
	private Object syncObject = new Object();
	
	private CountDownLatch latch;
	
	private ServiceResult result = null;

    
    /** servce interface */
    private Class serviceInterface;
    
    /** service properties */
    private ServiceProperties serviceProperties;
	
    public MinaClientServiceExecutor(Class serviceInterface, ServiceProperties serviceProperties)
    {
        super();
        this.serviceInterface = serviceInterface;
        this.serviceProperties = serviceProperties;
    }

	/* (non-Javadoc)
	 * @see core.service.executor.ServiceExecutor#execute(java.lang.Class, java.lang.reflect.Method, java.lang.Class[], java.lang.Object[])
	 */
	public ServiceResult execute(Class interfaceClass, Method method, Class[] paramTypes, Object... args)
			throws ServiceException
	{
		ServiceRequestImpl request = new ServiceRequestImpl();
		request.setArguments(args);
		request.setMethodName(method.getName());
		request.setParamTypes(paramTypes);
		request.setServiceInterfaceClassName(interfaceClass.getName());
		
		// Send the first ping message
		logger.info("Writing service request...");
		getSession().write(request);

		// Wait until the match ends.
//		getSession().getCloseFuture().awaitUninterruptibly();

		latch = new CountDownLatch(1);
		
		try
		{
			latch.await();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	private IoSession getSession()
	{
		if (session == null) 
		{
			start();
		}
		return session;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		Class[] paramTypes = new Class[args.length];
		for(int index = 0; index < args.length; index++) {
			paramTypes[index] = args[index].getClass();
		}
		return execute(serviceInterface, method, paramTypes, args);
	}

	/**
	 * 
	 */
	public void start()
	{
		// Connect to the server.
		NioSocketConnector connector = new NioSocketConnector();
		connector.setHandler(new Handler());
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		ConnectFuture future = connector.connect(new InetSocketAddress("localhost", MinaExecutorServer.PORT));
		future.awaitUninterruptibly();
		session = future.getSession();
	}

}
