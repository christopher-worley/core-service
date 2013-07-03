package core.service.plugin;

import java.lang.reflect.Method;


public interface ServicePlugin {
	
	public void after(Object serviceObject, Method method,
			Class[] paramTypes, Object[] args);
	
	public void before(Object serviceObject, Method method,
			Class[] paramTypes, Object[] args);
	
	public void initialize();

	public void shutdown();
	
}
