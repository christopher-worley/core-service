
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

import core.service.bus.ServiceBus;
import core.service.bus.ServiceRequestMessage;
import core.service.exception.ServiceException;
import core.service.result.ServiceResult;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * singleton which provides a connection
 * to the server.  The connection can
 * be shared with multiple request to the
 * server. 
 * 
 * @author worleyc
 *
 */
public class DedicatedServiceBus extends AbstractSocketServiceBus implements ServiceBus
{

    /** logger for this class */
    private static final Logger logger = LogFactory.getLogger(DedicatedServiceBus.class);
    
    /** dedicated connection manager */
    private DedicatedConnectionManager connection;
    
    /** service response listener */
    private DedicatedResponseListenerThread responseListener;
    
    /**
     * 
     */
    public DedicatedServiceBus()
    {
        super();
        initialize();
    }

    @Override
    public ServiceResult<?> execute(ServiceRequestMessage message)
            throws ServiceException
    {
        ServiceResult<?> result = null;
        try 
        {
            logger.info(
                    "Sending service request on dedicated bus (sendConnectionId={0},receiveConnectionId={1},messageId={2}).", 
                    connection.getSocketBinder().getSend().getConnectionId(), 
                    connection.getSocketBinder().getReceive().getConnectionId(),
                    message.getMessageId());
            
            connection.getSocketBinder().getSend().acquire();
            RequestResponseLatchManager.getInstance().register(message.getMessageId());
            sendServiceRequest(connection.getSocketBinder().getSend(), message);
            connection.getSocketBinder().getSend().release();
            result = responseListener.waitForResponse(message);
        }
        catch (Exception e) 
        {
            throw new ServiceException("Failed to invoke remote service: ", e);
        }
        return result;
    }
    
    /**
     * Return connection id for the receive socket connection
     * 
     * @return
     */
    public String getReceiveConnectionId()
    {
        return connection.getSocketBinder().getReceive().getConnectionId();
    }
    
    /**
     * Return connection id for the send socket connection
     * 
     * @return
     */
    public String getSendConnectionId()
    {
        return connection.getSocketBinder().getSend().getConnectionId();
    }



    /**
     * Initialize the connection and start the response listener thread 
     */
    private void initialize()
    {
        try
        {
            connection = DedicatedConnectionManager.getInstance();
            responseListener = DedicatedResponseListenerThread.getInstance();
        } 
        catch (Exception e)
        {
            throw new ServiceException("Failed to connect to server.", e);
        }
    }

}

