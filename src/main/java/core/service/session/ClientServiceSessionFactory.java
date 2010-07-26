package core.service.session;

import core.service.security.SecurityEntity;

public interface ClientServiceSessionFactory
{

	/**
	 * @return
	 */
	public ClientServiceSession createSession(SecurityEntity entity);

	/**
	 * @param entity
	 * @return
	 */
	public boolean authenticate(SecurityEntity entity);

}
