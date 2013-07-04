
/**
 * Copyright 2009 Core Information Solutions LLC
 *
 * This file is part of Core Service Framework.
 *
 * Core Service Framework is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * Core Service Framework is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Core Service Framework.  If not, see 
 * <http://www.gnu.org/licenses/>.
 */package core.service.factory;

import core.service.config.ServiceConfig;


/**
 * Factory for creating services.  
 * 
 * Instantiate and configure service objects for
 * execution.
 * 
 * @author worleyc
 *
 */
public interface ServiceFactory
{

    /**
     * Create service object for the given service interface.
     * 
     * In most cases it is expected that a ServiceProxy is returned.  However,
     * this is not enforced by the method signature.
     * 
     * @param serviceInterface
     * @return
     */
	public Object createService(Class serviceInterface);
	
	/**
	 * Service configuration for this factory
	 * 
	 * When a factory is created configuration can be applied.  It is expected that
	 * the executor and other components of the service framework using this factory
	 * will respect the configuration details.
	 * 
	 * @return
	 */
	public ServiceConfig getServiceConfig();

    
}
