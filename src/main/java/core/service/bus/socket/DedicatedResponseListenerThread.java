
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
import java.util.HashMap;
import java.util.Map;

import core.service.bus.ServiceBusDefs;
import core.service.bus.ServiceRequestMessage;
import core.service.bus.ServiceRequestResponse;
import core.service.exception.ServiceException;
import core.service.result.ServiceResult;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * This thread listens on the receiving socket stream
 * for service response messages.  When a response comes 
 * in it is added to the response map.  The latch will
 * be released by the RequestResponseLatchManager for
 * that message.  The waiting thread will then get the response
 * remove it from the response map and return the result.
 * 
 * @author worleyc
 *
 */
public class DedicatedResponseListenerThread extends Thread
{
    /**
     * Get the singletone instance
     * 
     * @return
     */
    public static DedicatedResponseListenerThread getInstance()
    {
        if (instance == null)
        {
            synchronized (DedicatedResponseListenerThread.class)
            {
                if (instance == null) 
                {
                    instance = new DedicatedResponseListenerThread();
                }
            }
        }
        return instance;
    }
    
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(DedicatedResponseListenerThread.class);
   
    /** responses received to be picked up from requesting thread */
    private Map<String, ServiceRequestResponse> responseMap;
    
    /** singleton instance */
    private static volatile DedicatedResponseListenerThread instance;

    /**
     * 
     */
    protected DedicatedResponseListenerThread()
    {
        super();
        responseMap = new HashMap<String, ServiceRequestResponse>();
        DedicatedConnectionManager connection = DedicatedConnectionManager.getInstance();
        setName("Response Listener " 
                + connection.getSocketBinder().getReceive().getConnectionId()
                + ":"
                + connection.getSocketBinder().getSend().getConnectionId());
        start();
    }

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

    @Override
    public void run()
    {
        SocketWrapper receiveSocket = DedicatedConnectionManager.getInstance().getSocketBinder().getReceive();
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                logger.debug("Ready to receive service response from socket (connectionId={0}).", receiveSocket.getConnectionId()); 
                receiveSocket.acquire();
                // receive intention
                confirmation(receiveSocket.getObjectInputStream(), ServiceBusDefs.SENDING_SERVICE_RESULT);
                // receive data
                ServiceRequestResponse response = (ServiceRequestResponse) receiveSocket.getObjectInputStream().readObject();
                // send confirmation
                receiveSocket.getObjectOutputStream().writeObject(ServiceBusDefs.RECEIVED_SERVICE_RESULT);
                receiveSocket.getObjectOutputStream().flush();
                receiveSocket.release();

                responseMap.put(response.getMessageId(), response);
                RequestResponseLatchManager.getInstance().release(response.getMessageId());
            }
        }
        catch (Exception e)
        {
            // don't show exception if IO
            if (!(e instanceof IOException)) 
            {
                logger.error("Exception occured in message router (connectionId=" + receiveSocket.getConnectionId() + ").", e);
            }
        }
        finally 
        {
            instance = null;
            RequestResponseLatchManager.reset();
        }
        logger.info("Shutting down message router (connectionId={0}).", receiveSocket.getConnectionId());
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

    /**
     * Wait for message response
     * @return
     * @throws InterruptedException 
     */
    public ServiceResult<?> waitForResponse(ServiceRequestMessage message) throws InterruptedException
    {
        String connectionId = DedicatedConnectionManager.getInstance().getSocketBinder().getReceive().getConnectionId();
        logger.debug("Waiting for response (connectionId={0},messageId={1}).", 
                connectionId, 
                message.getMessageId());
        RequestResponseLatchManager.getInstance().await(message.getMessageId());

        ServiceRequestResponse response = responseMap.get(message.getMessageId());
        responseMap.remove(message.getMessageId());
        if (response == null)
        {
            throw new IllegalStateException("No response found for message (messageId="+ response.getMessageId() + ").");
        }
        
        logger.debug("Response recieved, returning result (connectionId={0},messageId={1}).", connectionId, message.getMessageId());
        return response.getServiceResult();
    }

}
