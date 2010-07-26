
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
import core.service.bus.ServiceBusDefs;
import core.service.exception.ServiceException;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class DedicatedConnectionManager
{
    
    /** logger for this class */
    private static final Logger logger = LogFactory.getLogger(DedicatedConnectionManager.class);
    
    /** singleton instance */
    private volatile static DedicatedConnectionManager instance;

    /**
     * Return singletone instance
     * 
     * @return
     */
    public static DedicatedConnectionManager getInstance()
    {
        if (instance == null)
        {
            synchronized (DedicatedConnectionManager.class)
            {
                if (instance == null)
                {
                    instance = new DedicatedConnectionManager();
                }
            }
        }
        return instance;
    }
    
    /** socket binder */
    private SocketBinder socketBinder;

    /**
     * 
     */
    protected DedicatedConnectionManager()
    {
        super();
        connect();
    }


    /**
     * Connect to server
     * @throws IOException 
     * @throws UnknownHostException 
     * 
     */
    private void connect() 
    {
        try
        {
            Socket writeSocket = new Socket(ServiceDefs.SERVICE_HOST, ServiceDefs.SERVICE_PORT);
            Socket readSocket = new Socket(ServiceDefs.SERVICE_HOST, ServiceDefs.SERVICE_PORT);
            
            socketBinder = new SocketBinder();
            socketBinder.setReceive(new SocketWrapper(readSocket));
            socketBinder.setSend(new SocketWrapper(writeSocket));

            initializeSockets();
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
     * @throws IOException
     */
    public void disconnect() throws IOException
    {
        logger.debug("Disconnecting dedicated connection.");
        socketBinder.close();
        instance = null;
    }

    /**
     * Getter for socketBinder
     * 
     * @return
     */
    public SocketBinder getSocketBinder()
    {
        return socketBinder;
    }

    /**
     * Initialize read and write sockets.  Send socket types
     * and get connection ids.
     * 
     * @throws IOException 
     * @throws ClassNotFoundException 
     * 
     */
    private void initializeSockets() throws IOException, ClassNotFoundException
    {
        socketBinder.getSend().getObjectOutputStream().writeObject(ServiceBusDefs.TYPE_BUS_DEDICATED);
        String sendConnectionId = (String) socketBinder.getSend().getObjectInputStream().readObject();
        socketBinder.getSend().setConnectionId(sendConnectionId);
        socketBinder.getSend().getObjectOutputStream().writeObject(ServiceBusDefs.TYPE_DEDICATED_CLIENT);
        logger.debug("Initialized client side dedicated send socket (connectionId={0}).", sendConnectionId);
        
        socketBinder.getReceive().getObjectOutputStream().writeObject(ServiceBusDefs.TYPE_BUS_DEDICATED);
        String receiveConnectionId = (String) socketBinder.getReceive().getObjectInputStream().readObject();
        socketBinder.getReceive().setConnectionId(receiveConnectionId);
        socketBinder.getReceive().getObjectOutputStream().writeObject(ServiceBusDefs.TYPE_DEDICATED_SERVER);
        socketBinder.getReceive().getObjectOutputStream().writeObject(sendConnectionId);
        logger.debug("Initialized client side dedicated receive socket (connectionId={0}).", receiveConnectionId);
    }
    
}
