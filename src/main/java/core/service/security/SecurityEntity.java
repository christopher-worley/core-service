package core.service.security;

/**
 * Security entity to be authenticated and
 * act as the subject of the session. 
 * 
 * The service entity allows for multiple 
 * role and permission assignments.
 * 
 * The value of the credential array returned
 * depends on the class instance of the Entity.
 * Each Entity sub class should have a related
 * Authenticator class.
 * 
 * @author cworley
 *
 */
public interface SecurityEntity {

	public String[] getCredentials();
	
	public String[] getRoleIds();
	
	public String[] getPermissionIds();
}
