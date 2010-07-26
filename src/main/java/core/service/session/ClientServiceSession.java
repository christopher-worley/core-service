package core.service.session;

import core.service.factory.ServiceFactory;
import core.service.security.SecurityEntity;

public interface ClientServiceSession extends ServiceFactory
{

	public SecurityEntity getSecurityEntity();
}
