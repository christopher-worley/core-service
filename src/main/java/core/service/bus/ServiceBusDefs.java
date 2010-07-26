
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
 */package core.service.bus;

public class ServiceBusDefs
{
    
    /** bus type ids */
    public static final String TYPE_BUS_SIMPLE = "serive.bus.type.SIMPLE";
    public static final String TYPE_BUS_DEDICATED = "service.bus.type.DEDICATED";

    /** dedicated socket types */
    public static final Integer TYPE_DEDICATED_CLIENT = 20;
    public static final Integer TYPE_DEDICATED_SERVER = 21;
    
    /** send receive confirmation values used for dedicated service bus */
    public static final Integer SENDING_CLIENT_REQUEST = 100; 
    public static final Integer RECEIVED_CLIENT_REQUEST = 200;
    
    public static final Integer SENDING_SERVICE_RESULT = 101;
    public static final Integer RECEIVED_SERVICE_RESULT = 201;
}
