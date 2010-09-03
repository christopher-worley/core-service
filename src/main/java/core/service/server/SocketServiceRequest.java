
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

import java.util.Date;

import core.service.bus.socket.SocketWrapper;

public class SocketServiceRequest
{
    
    /** socket used to initialize and receive service request */
    private volatile SocketWrapper receiveSocket;
    
    /** socket used to send service result */
    private volatile SocketWrapper sendSocket;
    
    private Date requestTime;
    
    private String interfaceClassName;
    
    private String methodName;
    
    private Class[] paramTypes;
    
    private Object[] arguments;
    
    private String messageId;
    
    /**
     * @param socket
     */
    public SocketServiceRequest(SocketWrapper socket) 
    {
        this(socket, socket);
    }

    /**
     * @param receiveSocket
     * @param sendSocket
     */
    public SocketServiceRequest(SocketWrapper receiveSocket, SocketWrapper sendSocket) 
    {
        this.receiveSocket = receiveSocket;
        this.sendSocket = sendSocket;
        this.requestTime = new Date();        
    }

    public void close()
    {
        receiveSocket.close();
    }
    
    public Object[] getArguments()
    {
        return arguments;
    }
    
    public String getInterfaceClassName()
    {
        return interfaceClassName;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public Class[] getParamTypes()
    {
        return paramTypes;
    }

    public SocketWrapper getReceiveSocketWrapper()
    {
        return receiveSocket;
    }

    public Date getRequestTime()
    {
        return requestTime;
    }

    public SocketWrapper getSendSocketWrapper()
    {
        return sendSocket;
    }

    public void setArguments(Object[] arguments)
    {
        this.arguments = arguments;
    }

    public void setInterfaceClassName(String interfaceClassName)
    {
        this.interfaceClassName = interfaceClassName;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }
    
    public void setParamTypes(Class[] paramTypes)
    {
        this.paramTypes = paramTypes;
    }
}
