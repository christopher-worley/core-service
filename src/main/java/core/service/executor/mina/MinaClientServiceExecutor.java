/**
 * 
 */
package core.service.executor.mina;

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

import core.service.exception.ServiceException;
import core.service.executor.ServiceExecutor;
import core.service.result.ServiceResult;
import core.service.server.ServiceRequest;
import core.service.server.ServiceRequestImpl;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * @author cworley
 *
 */
public class MinaClientServiceExecutor implements ServiceExecutor
{
	
	private Logger logger = LogFactory.getLogger(MinaClientServiceExecutor.class);
	private IoSession session;
	
	private Object syncObject = new Object();
	private CountDownLatch latch;
	
	private ServiceResult result = null;
	
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

	/**
	 * 
	 */
	public MinaClientServiceExecutor()
	{
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

	
	/* (non-Javadoc)
	 * @see core.service.executor.ServiceExecutor#execute(core.service.server.ServiceRequest)
	 */
	@Override
	public ServiceResult execute(ServiceRequest request) throws ServiceException
	{
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

}
