
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
 */package core.service.server;

import java.io.IOException;

import core.service.bus.ServiceBusDefs;
import core.service.bus.socket.SocketWrapper;
import core.service.exception.ServiceException;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class DedicatedRequestListenerThread extends Thread implements Runnable
{
    private Logger logger = LogFactory.getLogger(DedicatedRequestListenerThread.class);
    
    /** socket wrapper for this thread */
    private SocketWrapper receiveSocket;
    
    /** service request queue */
    private ServiceRequestQueue queue;
    
    /** dedicated connection manager from server object */
    private DedicatedClientSocketManager manager;
    
    private boolean shutdown = false;

    /**
     * 
     */
    public DedicatedRequestListenerThread(SocketWrapper receiveSocket, ServiceRequestQueue queue, DedicatedClientSocketManager manager)
    {
        super();
        this.receiveSocket = receiveSocket;
        this.queue = queue;
        this.manager = manager;
        setName("Dedicated Request " + receiveSocket.getConnectionId());
    }

    @Override
    public void run()
    {
        while (!shutdown)
        {
            try
            {
                // acquire input permit, will be released after the request is processed
                receiveSocket.acquire();
                Integer intention = (Integer) receiveSocket.getObjectInputStream().readObject();
                logger.debug("Service request initiated (connectionId={0},intent={1}", receiveSocket.getConnectionId(), intention);
                if (intention.equals(ServiceBusDefs.SENDING_CLIENT_REQUEST))
                {
                    handleClientRequest();
                }
                else
                {
                    logger.error("Unknown intention: {0}", intention);
                }
            } 
            catch (IOException e)
            {
                logger.warn("Socket exception occured.");
            }
            catch (Exception e)
            {
                logger.error("An exception occured when handling service request ({0}).", e, e.getMessage());
                shutdown();
            }
        }
        logger.info("Dedicated listener thread shutting down (receiveConnectionId={0}).", receiveSocket.getConnectionId());
    }
    
    public void shutdown() 
    {
        shutdown = true;
    }
    
    /**
     * handle service request
     * 
     * TODO: Hacky code.  Need to do a not accept request until both connections are established.
     */
    private void handleClientRequest()
    {
        int count = 0;
        String sendConnectionId = null;
        do
        {
            if (count > 20) 
            {
                throw new ServiceException("Timeout occured waiting for send connection match for receive connection (receiveConnectionId={0}" + receiveSocket.getConnectionId() + ").");
            }
            sendConnectionId = manager.getSendConnectionId(receiveSocket.getConnectionId());
            if (sendConnectionId == null)
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                }
                count++;
            }
        }
        while (sendConnectionId == null);
        SocketWrapper sendSocket = manager.getSocket(sendConnectionId);
        logger.debug("Adding service request to the ServiceRequestQueue (connectionId={0}).", 
               receiveSocket.getConnectionId());
        queue.add(new ServiceRequest(receiveSocket, sendSocket));
    }
    

}
