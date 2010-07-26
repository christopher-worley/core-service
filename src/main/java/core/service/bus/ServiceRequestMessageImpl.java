
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
 */package core.service.bus;

public class ServiceRequestMessageImpl implements ServiceRequestMessage
{
    /**
     * 
     */
    private static final long serialVersionUID = 255717743804762376L;

    private String messageId;
    
    private String className;
    
    private String methodName;
    
    private Class[] paramTypes;
    
    private Object[] arguments;
    

    /**
     * @param messageId
     * @param className
     * @param methodName
     * @param paramTypes
     * @param arguments
     */
    public ServiceRequestMessageImpl(String messageId, String className, String methodName, Class[] paramTypes, Object[] arguments)
    {
        super();
        this.messageId = messageId;
        this.className = className;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.arguments = arguments;
    }

    
    
    @Override
    public Object[] getArguments()
    {
        return arguments;
    }



    @Override
    public String getClassName()
    {
        return className;
    }

    @Override
    public String getMessageId()
    {
        return messageId;
    }

    @Override
    public String getMethodName()
    {
        return methodName;
    }

    @Override
    public Class[] getParamTypes()
    {
        return paramTypes;
    }

}
