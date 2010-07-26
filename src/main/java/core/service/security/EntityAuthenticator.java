package core.service.security;

public interface EntityAuthenticator {

	public AuthenticationResult authenticate(SecurityEntity entity);
}
