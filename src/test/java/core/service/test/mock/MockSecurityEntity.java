package core.service.test.mock;

import core.service.security.SecurityEntity;

public class MockSecurityEntity implements SecurityEntity
{
	
	private String username;
	
	private String[] permissionIds;

	public MockSecurityEntity(String username, String[] permissionIds)
	{
		super();
		this.username = username;
		this.permissionIds = permissionIds;
	}

	@Override
	public String[] getCredentials()
	{
		return new String[] {username};
	}

	@Override
	public String[] getPermissionIds()
	{
		return permissionIds;
	}

	@Override
	public String[] getRoleIds()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return "MockSecurityEntity(username=" 
			+ username
			+ "permissionIds="
			+ permissionIds
			+ ")";
	}

	
}
