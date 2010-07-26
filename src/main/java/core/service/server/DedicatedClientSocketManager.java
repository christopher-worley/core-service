
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import core.service.bus.socket.SocketWrapper;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * For the dedicated bus connections need to be shared 
 * across multiple threads.  
 *  
 * @author worleyc
 *
 */
public class DedicatedClientSocketManager
{
    private static final Logger logger = LogFactory.getLogger(DedicatedClientSocketManager.class);
    
    /** dedicated socket wrappers keyed by connection id */
    private Map<String, SocketWrapper> sockets;
    
    /** tie receive connection id with send connection id, keyed by receiveConnectionId */
    private Map<String, String> pairs; 
    
    /** mutex for sockets map */
    private Object lock;
    
    /** service invoker server using this manager instance */
    private ServiceInvokerServer server;
    
    /** list of dedicated listener threads created */
    private List<DedicatedRequestListenerThread> dedicatedThreads;
    
    /** shutdown flag */
    private volatile boolean shutdown = false;
    
    /**
     * @param serviceInvokerServer
     */
    public DedicatedClientSocketManager(ServiceInvokerServer serviceInvokerServer)
    {
        super();
        sockets = new HashMap<String, SocketWrapper>();
        pairs = new HashMap<String, String>();
        lock = new Object();
        server = serviceInvokerServer;
        dedicatedThreads = new ArrayList<DedicatedRequestListenerThread>();
    }

    /**
     * Return send connection id associated with the receiveConnectionId
     * 
     * @param receiveConnectionId
     * @return
     */
    public String getSendConnectionId(String receiveConnectionId)
    {
        return pairs.get(receiveConnectionId);
    }

    /**
     * Get socket wrapper for give connection
     * 
     * @param sendConnectionId
     * @return
     */
    public SocketWrapper getSocket(String sendConnectionId)
    {
        return sockets.get(sendConnectionId);
    }

    /**
     * register a receive socket
     * 
     * @param connectionId
     * @param wrapper
     */
    public void registerReceiveSocket(String connectionId, SocketWrapper socket)
    {
        synchronized (lock) 
        {
            if (!shutdown) 
            {
                sockets.put(connectionId, socket);
                startRequestListenerThread(connectionId);
            }
        }
    }

    /**
     * register a send socket
     * 
     * @param connectionId
     * @param wrapper
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void registerSendSocket(String connectionId, SocketWrapper socket) throws IOException, ClassNotFoundException
    {
        synchronized (lock) 
        {
            if (!shutdown)
            {
                sockets.put(connectionId, socket);
                String receiveConnectionId = (String) socket.getObjectInputStream().readObject();
                logger.debug("Tied receive and send connections together (sendConnectionId={0},receiveConnectionId={0}).", connectionId, receiveConnectionId);
                pairs.put(receiveConnectionId, connectionId);
            }
        }
    }

    /**
     * Start request listener thread for given dedicated socket wrapper
     * 
     * @param wrapper
     */
    private void startRequestListenerThread(String receiveConnectionId)
    {
        DedicatedRequestListenerThread thread = new DedicatedRequestListenerThread(sockets.get(receiveConnectionId), server.getQueue(), server.getManager());
        dedicatedThreads.add(thread);
        thread.start();
    }

    /**
     * interrupt dedicated threads
     */
    public void shutdown()
    {
        logger.debug("Shutting down dedicated threads (count={0}).", dedicatedThreads.size());
        synchronized (lock) 
        {
            shutdown = true;
            for (DedicatedRequestListenerThread thread : dedicatedThreads)
            {
                thread.shutdown();
            }
            for (Iterator<String> iter = sockets.keySet().iterator(); iter.hasNext();)
            {
                String key = iter.next();
                sockets.get(key).close();
            }
        }
    }

}
