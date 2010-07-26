
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
 */package core.service.exception;

import core.service.result.ServiceResult;

/**
 * used to rollback server transaction and bring error message to the 
 * executor which will return a service result error with the
 * message
 * 
 * @author cworley
 *
 */
public class ServiceRollback extends RuntimeException
{
    
    /** response type to be set in the service result */
    int responseType = ServiceResult.ERROR;

    public ServiceRollback()
    {
        super();
    }

    public ServiceRollback(int responseType, String message)
    {
        super(message);
        this.responseType = responseType;
    }

    public ServiceRollback(int responseType, String message, Throwable cause)
    {
        super(message, cause);
        this.responseType = responseType;
    }

    public ServiceRollback(String message)
    {
        super(message);
    }

    public ServiceRollback(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServiceRollback(Throwable cause)
    {
        super(cause);
    }

}
