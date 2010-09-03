/**
 * 
 */
package core.service.main;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import core.service.executor.mina.MinaExecutorServer;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * @author cworley
 *
 */
public class MinaServerMain
{
	
	private Logger logger = LogFactory.getLogger(MinaServerMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MinaServerMain().run();
	}
	
	private void run()
	{
		MinaExecutorServer server = new MinaExecutorServer();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/core-service-context.xml");
		try
		{
			logger.info("Starting MINA server.");
			server.start(applicationContext);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public MinaServerMain()
	{
		// TODO Auto-generated constructor stub
	}

}
