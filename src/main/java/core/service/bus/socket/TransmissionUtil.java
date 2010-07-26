
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
 */package core.service.bus.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import core.service.bus.ServiceBusDefs;
import core.service.bus.ServiceRequestMessage;
import core.service.bus.ServiceRequestResponse;
import core.service.exception.ServiceException;

/**
 * Utilities for transmitting service request data
 * over sockets.
 * 
 * @author worleyc
 *
 */
public class TransmissionUtil
{
    
    /**
     * Confirm the given message is received
     * 
     * @param sendInput
     * @param message
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void confirmation(ObjectInputStream sendInput, int message) throws IOException, ClassNotFoundException
    {
        Integer received = (Integer) sendInput.readObject();
        verify(message, received);
    }

    /**
     * 
     * @return
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public static ServiceRequestResponse receiveServiceResult(SocketWrapper wrapper) throws IOException, ClassNotFoundException
    {
        // receive intention
        confirmation(wrapper.getObjectInputStream(), ServiceBusDefs.SENDING_SERVICE_RESULT);
        
        // receive data
        ServiceRequestResponse response = (ServiceRequestResponse) wrapper.getObjectInputStream().readObject();
        
        // send confirmation
        wrapper.getObjectOutputStream().writeObject(ServiceBusDefs.RECEIVED_SERVICE_RESULT);
        
        return response;
    }
    
    /**
     * Send service request
     * 
     * @param wrapper
     * @param message
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void sendServiceRequest(SocketWrapper wrapper, ServiceRequestMessage message) throws IOException, ClassNotFoundException
    {
        ObjectOutputStream output = wrapper.getObjectOutputStream();
        // send intention
        output.writeObject(ServiceBusDefs.SENDING_CLIENT_REQUEST);
        
        // send data
        output.writeObject(message);
        output.flush();
        
        // receive confirmation
        confirmation(wrapper.getObjectInputStream(), ServiceBusDefs.RECEIVED_CLIENT_REQUEST);
    }
    
    /**
     * verify the values match
     * 
     * @param receivedClientRequest
     * @param received
     */
    public static void verify(Integer receivedClientRequest, Integer received)
    {
        if (!receivedClientRequest.equals(received)) 
        {
            throw new ServiceException("Communication failed.");
        }
    }
}
