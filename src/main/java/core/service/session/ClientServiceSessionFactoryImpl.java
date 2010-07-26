package core.service.session;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import core.service.security.SecurityEntity;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class ClientServiceSessionFactoryImpl implements ClientServiceSessionFactory, ApplicationContextAware
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(ClientServiceSessionFactoryImpl.class);
	
    /** context */
	private ApplicationContext context;

	@Override
	public boolean authenticate(SecurityEntity entity)
	{
		return true;
	}

	@Override
	public ClientServiceSession createSession(SecurityEntity entity)
	{
		logger.debug("Creating session for entity (entity=" + entity + ").");
		return (ClientServiceSession) context.getBean("clientServiceSession", new Object[] {entity});
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException
	{
		this.context = context;
	}

}
