
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import core.service.ServiceDefs;
import core.service.bus.ServiceBusDefs;
import core.service.bus.socket.SocketWrapper;
import core.service.exception.ServiceException;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * Server thread responsible for accepting connections then handing
 * them off to a thread pool to determine what the intent of the connect.
 * 
 * For connections from the SimpleServiceBus the socket wrapper will be
 * added to the ServiceRequestQueue to be invoked
 * 
 * Dedicated bus connections will have there socket wrapper registered
 * with the DedicatedConnectionManager
 * 
 * @author cworley
 *
 */
public class ServiceInvokerServer extends Thread
{
    /** logger for this class */
    private static final Logger logger = LogFactory.getLogger(ServiceInvokerServer.class);
    
    private int port = ServiceDefs.SERVICE_PORT;

    private ServerSocket serverSocket;

    private ServiceRequestQueue queue;

    private DedicatedClientSocketManager manager;

    // queue and pool to handle routing incomming connections
    private BlockingQueue<Runnable> routeQueue;
    
    private ThreadPoolExecutor routePool;
    
    private boolean shuttingDown = false;
    
    private long nextConnectionId = 500;

    /**
     * 
     */
    public ServiceInvokerServer() 
    {
        queue = new ServiceRequestQueue();
        manager = new DedicatedClientSocketManager(this);
        routeQueue = new LinkedBlockingQueue<Runnable>();
        routePool = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                routeQueue
                );
        
    }
    
    /**
     * 
     * @param port
     */
    public ServiceInvokerServer(int port) 
    {
        this();
        this.port = port;
    }
    
    /**
     * Close server socket safely
     */
    private void closeServerSocket()
    {
        if (serverSocket != null) 
        {
            try
            {
                serverSocket.close();
            } 
            catch (IOException e)
            {
            }
        }
    }
    
    /**
     * Generate connection id
     * @return
     */
    private synchronized String generateConnectionId()
    {
        return String.valueOf(nextConnectionId++);
    }
    
    public DedicatedClientSocketManager getManager()
    {
        return manager;
    }
    
    /**
     * 
     * @return
     */
    public ServiceRequestQueue getQueue()
    {
        return queue;
    }
    

    /**
     * Handle socket connection on the simple service request bus.  Confirm 
     * the intention is SENDING_CLIENT_REQEST, if so add a ServiceRequest
     * to the ServiceRequestQueue
     * 
     * @param connectionId
     * @param socketWrapper
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void handleSimpleServiceRequest(String connectionId, SocketWrapper socketWrapper) throws IOException, ClassNotFoundException
    {
        Integer intention = (Integer) socketWrapper.getObjectInputStream().readObject();
        if (intention.equals(ServiceBusDefs.SENDING_CLIENT_REQUEST))
        {
            logger.debug("Adding new service request to queue (connectionId={0}).", connectionId);
            queue.add(new SocketServiceRequest(socketWrapper));
        }
        else
        {
            throw new ServiceException("Unknown intention: " + intention);
        }
    }

    /**
     * List for clients to connect
     */
    public void listen() 
    {
        setName("Service Invoker Server");
        start();
    }
    
    
    /**
     * Register dedicated socket wrapper
     * 
     * 1) Read the dedicated socket type.  Verify its DEDICATED_CLIENT or DEDICATED_SERVER
     * 2) register the socket wrapper with the dedicated connection manager
     * 3) Release the lock on the socket 
     * 
     * @param connectionId
     * @param socketWrapper
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void registerDedicatedSocket(String connectionId, SocketWrapper socketWrapper) throws IOException, ClassNotFoundException
    {
        Integer type = (Integer) socketWrapper.getObjectInputStream().readObject();
        if (ServiceBusDefs.TYPE_DEDICATED_CLIENT.equals(type))
        {
            logger.debug("Registered dedicated receive socket (connectionId={0}).", connectionId);
            manager.registerReceiveSocket(connectionId, socketWrapper);
        }
        else if (ServiceBusDefs.TYPE_DEDICATED_SERVER.equals(type))
        {
            logger.debug("Registered dedicated send socket (connectionId={0}).", connectionId);
            manager.registerSendSocket(connectionId, socketWrapper);
        }
        else
        {
            throw new IllegalArgumentException("Unkown dedicated type: " + type);
        }
        // release permit acquired when connection was accepted
        socketWrapper.release();
        
    }
    
    /** 
     * route socket connection
     * 
     * 1) Create SocketWrapper for connectioin and acquire the lock
     * 2) Read the busType and connectionId from the input stream
     * 3) Create a SocketWrapper instance for the connection
     * 4) Based on the busType pass the sockets off  
     * 
     * @param socket
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void route(Socket socket) throws IOException, ClassNotFoundException
    {
        logger.debug("Routing new client connection.");
        SocketWrapper socketWrapper = new SocketWrapper(socket);
        socketWrapper.acquire();
        
        // read the bus type
        String busType = (String) socketWrapper.getObjectInputStream().readObject();
        
        // write the connection id
        String connectionId = generateConnectionId();
        socketWrapper.setConnectionId(connectionId);
        socketWrapper.getObjectOutputStream().writeObject(connectionId);
        socketWrapper.getObjectOutputStream().flush();
        // pass socket off
        // TODO: move to factory 
        if (ServiceBusDefs.TYPE_BUS_SIMPLE.equals(busType)) 
        {
            handleSimpleServiceRequest(connectionId, socketWrapper);
        }
        else if (ServiceBusDefs.TYPE_BUS_DEDICATED.equals(busType))
        {
            registerDedicatedSocket(connectionId, socketWrapper);
        }
        else
        {
            throw new IllegalArgumentException("Unkown busType: " + busType);
        }
    }

    /**
     * 
     */
    public void run() 
    {
        try
        {
            // create server socket
            serverSocket = new ServerSocket(port);
            logger.info("Waiting to accept connections.");
            while (!Thread.currentThread().isInterrupted()) 
            {
                // accept request and quickly push to the request queue
                Socket socket = serverSocket.accept();
                submitRouteRequest(socket);
            }
        } 
        catch (IOException e)
        {
            if (!shuttingDown)
            {
                throw new ServiceException(e);
            }
        }
        finally 
        {
            closeServerSocket();
            shutdown();
        }
    }

    /**
     * Shutdown server
     */
    public void shutdown()
    {
        if (!shuttingDown) 
        {
            shuttingDown = true;
            logger.info("Shutting down service invoker server.");
            queue.shutdown();
            manager.shutdown();
            routePool.shutdown();
            // closing the socket will most likely kill the thread, but in case it was not blocked on Socket.accept()
            closeServerSocket();
        }
    }

    /**
     * Submit route request to thread pool
     * 
     * @param socket
     */
    private void submitRouteRequest(final Socket socket)
    {
        routePool.submit(new Runnable() 
        {
            @Override
            public void run()
            {
                try
                {
                    route(socket);
                }
                catch (Exception e)
                {
                    logger.error("Failed to route connection.", e);
                }
            }
            
        });
    }

}
