
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * Manages latches for the threads to wait on for the service
 * response to be received.  When a request is made the latch
 * is registered for the message.  The thread will then wait
 * on the latch, when the response is received and ready the
 * latch will be released at which time the thread can
 * get its response and return the service result.
 * 
 * @author worleyc
 *
 */
public class RequestResponseLatchManager
{
    private Logger logger = LogFactory.getLogger(RequestResponseLatchManager.class);

    /** singleton instance */
    private static volatile RequestResponseLatchManager instance;
    
    /**
     * Getter for singleton instance
     * 
     * @return
     */
    public static RequestResponseLatchManager getInstance()
    {
        if (instance == null)
        {
            synchronized (RequestResponseLatchManager.class)
            {
                if (instance == null)
                {
                    instance = new RequestResponseLatchManager();
                }
            }
        }
        return instance;
    }
    
    private Map<String, CountDownLatch> latchMap;

    /**
     * 
     */
    protected RequestResponseLatchManager()
    {
        super();
        latchMap = new HashMap<String, CountDownLatch>();
    }
    
    /**
     * @param messageId
     */
    public void register(String messageId)
    {
        logger.debug("Registering latch for message (messageId={0}).", messageId);
        synchronized (latchMap)
        {
            if (latchMap.containsKey(messageId))
            {
                throw new IllegalStateException("Latch already exist for message (messageId=" + messageId + ").");
            }
            CountDownLatch latch = new CountDownLatch(1);
            latchMap.put(messageId, latch);
        }
    }
    
    /**
     * 
     */
    public static void reset()
    {
        instance = null;
    }
    
 
    /**
     * @param messageId
     */
    public void await(String messageId)
    {
        logger.debug("Awaiting latch release for message (messageId={0}).", messageId);
        CountDownLatch latch;
        synchronized (latchMap)
        {
            latch = latchMap.get(messageId);
            if (latch == null)
            {
                logger.debug("Latch does not exist for message, not waiting (messageId={0}).", messageId);
                return;
            }
        }
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
        }
    }
    
    /**
     * @param messageId
     */
    public void release(String messageId)
    {
        logger.debug("Releasing latch for message (messageId={0}).", messageId);
        CountDownLatch latch;
        synchronized (latchMap)
        {
            latch = latchMap.get(messageId);
            if (latch == null)
            {
                logger.debug("No latch to release for message (messageId={0}).", messageId);
                return;
            }
            latchMap.remove(messageId);
        }
        latch.countDown();
    }
}
