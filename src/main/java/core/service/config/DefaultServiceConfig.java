package core.service.config;

import java.util.ArrayList;
import java.util.List;

import core.service.plugin.ServicePlugin;


public class DefaultServiceConfig implements ServiceConfig {

	
	/** service plugins to invoke before service is executed */
	private List<ServicePlugin> plugins;
	
	/**
	 * Default constructor
	 * 
	 * Construct configuration with empty list of plugins. 
	 */
	public DefaultServiceConfig() {
		plugins = new ArrayList<ServicePlugin>();		
	}

	@Override
	public List<ServicePlugin> getServicePlugins() {
		return plugins;
	}


}
