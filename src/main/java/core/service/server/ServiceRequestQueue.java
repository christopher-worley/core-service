
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
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import core.service.ServiceDefs;
import core.service.bus.ServiceBusDefs;
import core.service.bus.ServiceRequestMessage;
import core.service.exception.ServiceException;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * The service request queue accepts new service request.
 * First the service request is added to the processor queue
 * waiting in line to be processed.  Processing consist of 
 * authentication and determining what the request is for.
 * After the request has been processed it is then added to
 * the wait queue where it waits to be executed by the invoker
 * thread.  
 * 
 * The invoker thread will invoke service request concurrently 
 * based on the limit set in the configuration.  
 * 
 * @author worleyc
 *
 */
public class ServiceRequestQueue
{
    
    /**
     * 
     * @author worleyc
     *
     */
    private class ProcessorThread extends Thread
    {
        private ServiceRequest request;
        
        public ProcessorThread(ServiceRequest request)
        {
            super();
            this.request = request;
            setName("Request Processor");
        }

        @Override
        public void run()
        {
            logger.info("Processing service request...");
            try
            {
                // read in request info
                ObjectInputStream input = request.getReceiveSocketWrapper().getObjectInputStream();
                ServiceRequestMessage message = (ServiceRequestMessage) input.readObject();
                request.setMessageId(message.getMessageId());
                request.setInterfaceClassName(message.getClassName());
                request.setMethodName(message.getMethodName());
                request.setParamTypes(message.getParamTypes());
                request.setArguments(message.getArguments());
                logger.info("Received client request (connectionId={0}, messageId={1},interfaceClassName={2},methodName={3}).",
                        request.getReceiveSocketWrapper().getConnectionId(),
                        request.getMessageId(),
                        request.getInterfaceClassName(),
                        request.getMethodName());
                
                // send confirmation
                request.getReceiveSocketWrapper().getObjectOutputStream().writeObject(ServiceBusDefs.RECEIVED_CLIENT_REQUEST);
                
                // release input permit
                request.getReceiveSocketWrapper().release();
                
                logger.info("Service request added to invoker pool (connectionId={0}, messageId={1},interfaceClassName={2},methodName={3}).",
                        request.getReceiveSocketWrapper().getConnectionId(),
                        request.getMessageId(),
                        request.getInterfaceClassName(),
                        request.getMethodName());
                InvokeServiceThread invoker = new InvokeServiceThread(request);
                invokerPool.submit(invoker);
            } 
            catch (IOException e)
            {
                throw new ServiceException("Failed to process service request: ", e);
            } 
            catch (ClassNotFoundException e)
            {
                throw new ServiceException("Failed to process service request: ", e);
            }
        }
    }

    /** logger for this class */
    private static final Logger logger = LogFactory.getLogger(ServiceRequestQueue.class);

    private BlockingQueue<Runnable> newQueue;
    
    private BlockingQueue<Runnable> invokerQueue;

    private ThreadPoolExecutor processPool;
    
    private ThreadPoolExecutor invokerPool;
    
    /**
     * 
     */
    public ServiceRequestQueue()
    {
        super();
        // initialize queues
        newQueue = new LinkedBlockingQueue<Runnable>();
        processPool = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                newQueue
                );
        
        invokerQueue = new LinkedBlockingQueue<Runnable>();
        invokerPool = new ThreadPoolExecutor(
                Integer.valueOf(System.getProperty(ServiceDefs.PROPERTY_SERVER_INVOKER_LIMIT)),
                Integer.valueOf(System.getProperty(ServiceDefs.PROPERTY_SERVER_INVOKER_LIMIT)),
                0L,
                TimeUnit.MILLISECONDS,
                invokerQueue
                );
    }

    /**
     * Add service request to queue
     * 
     * @param serviceRequest
     */
    public void add(ServiceRequest serviceRequest)
    {
        invokerPool.submit(new ProcessorThread(serviceRequest));
    }

    /**
     * Shutdown request queue
     */
    public void shutdown()
    {
        logger.info("Shutting down service request queue.");
        invokerPool.shutdown();
        processPool.shutdown();
    }
}
