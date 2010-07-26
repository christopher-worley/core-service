
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
import java.net.Socket;
import java.net.UnknownHostException;

import core.service.ServiceDefs;
import core.service.bus.ServiceBus;
import core.service.bus.ServiceBusDefs;
import core.service.bus.ServiceRequestMessage;
import core.service.exception.ServiceException;
import core.service.result.ServiceResult;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * SimpleServiceBus makes a connection to the server on each request
 * and disconnects after the service result is received.
 * 
 * @author worleyc
 *
 */
public class SimpleServiceBus extends AbstractSocketServiceBus implements ServiceBus
{
    // logger for this class
    private static final Logger logger = LogFactory.getLogger(SimpleServiceBus.class);
    
    /** socket connected to server */
    private SocketWrapper socket;
    private String connectionId;
    
    /**
     * 
     */
    public SimpleServiceBus()
    {
        super();
    }


    /**
     * Connect to server
     * 
     * @throws IOException 
     * @throws UnknownHostException 
     * 
     */
    private void connect() 
    {
        try
        {
            socket = new SocketWrapper(new Socket(ServiceDefs.SERVICE_HOST, ServiceDefs.SERVICE_PORT));

            // send bus id
            socket.getObjectOutputStream().writeObject(ServiceBusDefs.TYPE_BUS_SIMPLE);
            socket.getObjectOutputStream().flush();
            
            connectionId = (String) socket.getObjectInputStream().readObject();
            socket.setConnectionId(connectionId);
            logger.debug("Client connected simple bus (connectionId={0}).", connectionId);
            
        }
        catch (UnknownHostException e)
        {
            throw new ServiceException("Failed to connect to server.", e);
        }
        catch (IOException e)
        {
            throw new ServiceException("Failed to connect to server.", e);
        }
        catch (ClassNotFoundException e)
        {
            throw new ServiceException("Failed to connect to server.", e);
        }
    }

    /**
     * Disconnect from server
     * 
     */
    private void disconnect()
    {
        socket.close();
    }


    @Override
    public ServiceResult<?> execute(ServiceRequestMessage message)
            throws ServiceException
    {
        connect();
        try 
        {
            logger.debug("Sending service request message (connectionId={0},messageId={1}).", socket.getConnectionId(), message.getMessageId());
            sendServiceRequest(socket, message);
            logger.debug("Attempting to receive service result (connectionId={0},messageId={1}).", socket.getConnectionId(), message.getMessageId());
            ServiceResult result = receiveServiceResult(socket).getServiceResult();
            disconnect();
            
            return result;
        }
        catch (Exception e) 
        {
            throw new ServiceException("Failed to invoke remote service: ", e);
        }
    }
}
