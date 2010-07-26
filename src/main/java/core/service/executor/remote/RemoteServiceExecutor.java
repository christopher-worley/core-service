
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
 */package core.service.executor.remote;

import java.lang.reflect.Method;

import core.service.bus.ServiceBus;
import core.service.bus.ServiceRequestMessage;
import core.service.bus.ServiceRequestMessageImpl;
import core.service.exception.ServiceException;
import core.service.executor.ServiceExecutor;
import core.service.result.ServiceResult;
import core.service.util.ServiceContextUtil;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class RemoteServiceExecutor implements ServiceExecutor
{
    
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(RemoteServiceExecutor.class);
    
    /** service bus */
    private ServiceBus serviceBus;
    
    private static volatile long nextMessageId = 0;
    
    /**
     * 
     */
    public RemoteServiceExecutor()
    {
        super();
        serviceBus = (ServiceBus) ServiceContextUtil.getApplicationContext().getBean("serviceBus");
    }

    @Override
    public ServiceResult execute(Class interfaceClass, Method method, Class[] paramTypes, Object... args)
            throws ServiceException
    {
        ServiceResult result;
        try 
        {
            long startTime = System.currentTimeMillis();
            String messageId = generateMessageId();
            logger.debug("About to create message (messageId={0}).", messageId);
            ServiceRequestMessage message = new ServiceRequestMessageImpl(
                    messageId,
                    interfaceClass.getName(), 
                    method.getName(), 
                    paramTypes, 
                    args);
            logger.debug("Message created (messageId={0}).", message.getMessageId());
            result = serviceBus.execute(message);
            long endTime = System.currentTimeMillis();
            logger.debug("Remote service execution finished (messageId={0},elapsedTime={1}).",
                    message.getMessageId(),
                    endTime - startTime
                    );
        }
        catch (Exception e) 
        {
            throw new ServiceException("Failed to invoke remote service: ", e);
        }
        return result;
    }

    /**
     * generate next message id
     * 
     * @return
     */
    private synchronized String generateMessageId()
    {
        nextMessageId++;
        String messageId = "message:" + nextMessageId;
        logger.debug("Generated message {0}.", messageId);
        return messageId;
    }

}
