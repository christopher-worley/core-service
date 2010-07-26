
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

import core.service.bus.ServiceRequestMessage;
import core.service.bus.ServiceRequestResponse;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public abstract class AbstractSocketServiceBus
{
    
    /** logger for this class */
    private static final Logger logger = LogFactory.getLogger(AbstractSocketServiceBus.class);


    /**
     * 
     * @param sendInput
     * @param receivedClientRequest
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    protected void confirmation(ObjectInputStream sendInput, int message) throws IOException, ClassNotFoundException
    {
        TransmissionUtil.confirmation(sendInput, message);
    }

    /**
     * 
     * @return
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    protected ServiceRequestResponse receiveServiceResult(SocketWrapper wrapper) throws IOException, ClassNotFoundException
    {
        return TransmissionUtil.receiveServiceResult(wrapper);
    }

    /**
     * Send service request
     * 
     * @param wrapper
     * @param message
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected void sendServiceRequest(SocketWrapper wrapper, ServiceRequestMessage message) throws IOException, ClassNotFoundException
    {
        logger.debug("Sending message (connectionId={0}, message={1}).", wrapper.getConnectionId(), message.getMessageId());
        TransmissionUtil.sendServiceRequest(wrapper, message);
    }


    /**
     * verify the values match
     * 
     * @param receivedClientRequest
     * @param received
     */
    protected void verify(Integer receivedClientRequest, Integer received)
    {
        TransmissionUtil.verify(receivedClientRequest, received);        
    }

}
