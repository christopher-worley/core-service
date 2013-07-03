
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
import java.lang.reflect.Method;

import core.service.bus.ServiceBusDefs;
import core.service.bus.ServiceRequestResponse;
import core.service.bus.ServiceRequestResponseImpl;
import core.service.config.DefaultServiceConfig;
import core.service.executor.local.CoreServiceExecutor;
import core.service.result.ServiceResult;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;


public class InvokeServiceThread implements Runnable
{
    /** logger for this class */
    private static final Logger logger = LogFactory.getLogger(InvokeServiceThread.class);
    
    /** service request to invoke */
    private SocketServiceRequest request;

    /**
     * @param request
     */
    public InvokeServiceThread(SocketServiceRequest request)
    {
        this.request = request;
    }

    @Override
    public void run()
    {
        CoreServiceExecutor executor = new CoreServiceExecutor(request.getClass(), new DefaultServiceConfig());
        
        try
        {
            Class clazz = Class.forName(request.getInterfaceClassName());
            Method method = clazz.getMethod(request.getMethodName(), request.getParamTypes());
          
            Object methodResult = method.invoke(executor, request.getArguments());
            
            ServiceResult result = ServiceResult.success("Service executed without errors.", methodResult);
            
            // lock send socket
            request.getSendSocketWrapper().acquire();
            
            // send intention
            request.getSendSocketWrapper().getObjectOutputStream().writeObject(ServiceBusDefs.SENDING_SERVICE_RESULT);
            
            // send data
            logger.debug("Sending service response (connectionId={0},messageId={1}).", request.getReceiveSocketWrapper().getConnectionId(), request.getMessageId());
            ServiceRequestResponse response = new ServiceRequestResponseImpl(request.getMessageId(), result);
            request.getSendSocketWrapper().getObjectOutputStream().writeObject(response);
            request.getSendSocketWrapper().getObjectOutputStream().flush();
            
            // receive confirmation
            Integer confirm = (Integer) request.getSendSocketWrapper().getObjectInputStream().readObject();
            if (!ServiceBusDefs.RECEIVED_SERVICE_RESULT.equals(confirm))
            {
                throw new IllegalArgumentException("Incorrect confirmation: " + confirm);
            }
            
            // release lock on send socket
            request.getSendSocketWrapper().release();
        } 
        catch (IOException e)
        {
            logger.warn("An IOException occured, client may have disconnected: ", e.getMessage(), e);
        } 
        catch (Exception e)
        {
            logger.error("Failed to invoke service ({0}).", e.getMessage(), e);
        } 
    }

}
