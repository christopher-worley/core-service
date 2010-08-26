package core.service.factory;

import core.service.proxy.ServiceProxy;
import core.service.session.ClientServiceSession;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class ServiceFactoryImpl implements ServiceFactory
{
	/** logger for this class */
	private Logger logger = LogFactory.getLogger(ServiceFactoryImpl.class);

	private ClientServiceSession session;

	public ServiceFactoryImpl()
	{
		this(null);
	}

	public ServiceFactoryImpl(ClientServiceSession session)
	{
		super();
		this.session = session;
	}

	@Override
	public Object createService(Class serviceInterface)
	{
		logger.debug("Creating service for session (session=" + this + ",serviceInterface=" + serviceInterface.getName() + ").");

		// // check if the bean is a service
		// Service service = (Service)
		// serviceInterface.getAnnotation(Service.class);
		// if (service == null)
		// {
		// throw new ServiceException("No service is defined for " +
		// serviceInterface.getName());
		// }

		return ServiceProxy.newInstance(serviceInterface, session);
	}

}
