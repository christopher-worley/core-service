
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
 */package core.service.executor;

import core.service.exception.ServiceException;
import core.service.result.ServiceResult;
import core.service.server.ServiceRequest;


/**
 * Interface to be used for implementation classes 
 * who invoke services.
 * 
 * @author worleyc
 *
 */
public interface ServiceExecutor
{

    public ServiceResult execute(ServiceRequest request) throws ServiceException;

}
