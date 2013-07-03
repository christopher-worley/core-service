package core.service.config;

import java.util.ArrayList;
import java.util.List;

import core.service.factory.DefaultServiceFactory;
import core.service.plugin.ServicePlugin;

public class DefaultServiceConfig implements ServiceConfig<DefaultServiceFactory> {
	
	/** using default service factory until there is reason to make the type optional */
	private DefaultServiceFactory serviceFactory;
	
	/** service plugins to invoke before service is executed */
	private List<ServicePlugin> plugins;
	
	/**
	 * Default constructor
	 * 
	 * Construct configuration instance with default service factory.
	 */
	public DefaultServiceConfig() {
		serviceFactory = new DefaultServiceFactory();
		plugins = new ArrayList<ServicePlugin>();		
	}

	@Override
	public List<ServicePlugin> getServicePlugins() {
		return plugins;
	}


	@Override
	public DefaultServiceFactory getServiceFactory() {
		return serviceFactory;
	}

}
