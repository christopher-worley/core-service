package core.service.session;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import core.service.factory.ServiceFactory;
import core.service.security.SecurityEntity;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class ClientServiceSessionImpl implements ClientServiceSession, ApplicationContextAware
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(ClientServiceSessionImpl.class);

	private ApplicationContext context;

	private String connectionURL;

	private SecurityEntity entity;

	private ServiceFactory serviceFactory;

	/**
	 * @param entity
	 */
	public ClientServiceSessionImpl(SecurityEntity entity)
	{
		super();
		this.entity = entity;
	}

	@Override
	public Object createService(Class serviceInterface)
	{
		logger.debug("Creating service for session (session=" + this + ",serviceInterface=" + serviceInterface.getName() + ").");
		return getServiceFactory().createService(serviceInterface);
	}

	@Override
	public SecurityEntity getSecurityEntity()
	{
		return entity;
	}

	private ServiceFactory getServiceFactory()
	{
		if (serviceFactory == null)
		{
			serviceFactory = (ServiceFactory) context.getBean("serviceFactory", new Object[] {this});
		}
		return serviceFactory;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException
	{
		this.context = context;
	}

	@Override
	public String toString()
	{
		return "ClientServiceSessionImpl(entity=" 
			+ entity
			+ ",connectionURL="
			+ connectionURL
			+ ").";
	}

	
}
