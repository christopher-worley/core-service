package core.service.config;

import java.util.List;

import core.service.factory.ServiceFactory;
import core.service.plugin.ServicePlugin;

public interface ServiceConfig<SF extends ServiceFactory> {
	
	public List<ServicePlugin> getServicePlugins();
	
	public SF getServiceFactory();

}
