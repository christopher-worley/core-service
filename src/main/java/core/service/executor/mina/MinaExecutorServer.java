package core.service.executor.mina;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.context.ApplicationContext;

import core.service.session.ClientServiceSessionFactoryImpl;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class MinaExecutorServer
{
	public static final int PORT = 9123;

    private static final Logger LOGGER = LogFactory.getLogger(MinaExecutorServer.class);

	/**
	 * 
	 */
	public MinaExecutorServer()
	{
		super();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public void start(ApplicationContext context) throws IOException
	{
		LOGGER.info("Server starting...");

		IoAcceptor acceptor = new NioSocketAcceptor();

		//acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		acceptor.setHandler(new ExecutionProtocolHandler(context));
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(PORT));
	}
}
