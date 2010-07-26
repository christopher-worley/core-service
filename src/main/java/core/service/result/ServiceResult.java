
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
 */package core.service.result;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;


/**
 * Return object for service executioner.  Service result object
 * is used as the return value with in the service framework.
 * If a service methods return type is <code>ServiceResult</code>
 * then it will be passed threw.  Any other return type will be wrapped
 * into as <code>ServiceResult</code> and set as the payload.  Before
 * the proxy returns the return value it will check and make sure
 * the service method's return type is the same as the payload.
 * if not an exception will be thrown otherwise it will return the payload.
 * 
 * @author worleyc
 *
 */
public class ServiceResult<T> implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 4031613785557561354L;
    
    /** response types */
    public static int SUCCESS = 0;
    public static int ERROR = 1;
    public static int EXCEPTION = 2;
    public static int PERMISSION = 3;
    public static int AUTHENTICATION = 4;
    
    /** response descriptions */
    public static String DESCRIPTION_SUCCESS = "Success";
    public static String DESCRIPTION_ERROR = "Error";
    public static String DESCRIPTION_EXCEPTION = "Exception";
    public static String DESCRIPTION_AUTHENTICATION = "Authentication";
    public static String DESCRIPTION_PERMISSION = "Permission";
    
    private static Map responseDescription = new HashMap();
    static 
    {
        responseDescription.put(SUCCESS, DESCRIPTION_SUCCESS);
        responseDescription.put(ERROR, DESCRIPTION_ERROR);
        responseDescription.put(EXCEPTION, DESCRIPTION_EXCEPTION);
        responseDescription.put(AUTHENTICATION, DESCRIPTION_AUTHENTICATION);
        responseDescription.put(PERMISSION, DESCRIPTION_PERMISSION);
    }
    

    /**
     * static helper to create a authentication result
     * 
     * @param string
     * @return
     */
    public static ServiceResult authentication(String string)
    {
        return new ServiceResult(AUTHENTICATION, string);
    }
    
    /**
     * static helper to create a error result
     * 
     * @param string
     * @return
     */
    public static ServiceResult error(String string)
    {
        return new ServiceResult(ERROR, string);
    }
    
    /**
     * static helper to create a error result
     * 
     * @param payload
     * @param string
     * @return
     */
    public static ServiceResult error(String string, Object payload)
    {
        return new ServiceResult(ERROR, string, payload);
    }

    /**
     * static helper to create an exception result
     * 
     * @param string
     * @param e
     * @return
     */
    public static ServiceResult exception(String string, Exception e)
    {
        return new ServiceResult(EXCEPTION, string, e);
    }

    /**
     * get description for response type 
     * @param responseType2
     * @return
     */
    public static String getResponseDescription(int responseType)
    {
        return (String) responseDescription.get(responseType);
    }
    
    /**
     * static helper to create an permission result
     * 
     * @param string
     * @return
     */
    public static ServiceResult permission(String string)
    {
        return new ServiceResult(PERMISSION, string);
    }
    
    
    public static ServiceResult permission(String string, Exception securityException)
    {
        return new ServiceResult(PERMISSION, string, securityException);
    }
    
    /**
     * static helper to create a success result
     * 
     * @param string
     * @return
     */
    public static ServiceResult success(String string)
    {
        return new ServiceResult(SUCCESS, string);
    }
    
    /**
     * static helper to create a success with payload
     * 
     * @param payload
     * @return
     */
    public static ServiceResult success(String message, Object payload)
    {
        return new ServiceResult(SUCCESS, message, payload);
    }
    
    /** response type */
    private int responseType = -1;

    /** response message */
    private String message;
    
    /** result payload */
    private T payload;
    
    
    /**
     * Default constructor
     */
    public ServiceResult()
    {
        super();
    }

    /**
     * 
     * @param result
     * @param message
     */
    public ServiceResult(int result, String message)
    {
        this(result, message, null);
    }

    /**
     * 
     * @param responseType
     * @param message
     * @param payload
     */
    public ServiceResult(int responseType, String message, T payload)
    {
        this.responseType = responseType;
        this.message = message;
        this.payload = payload;
    }

    /**
     * 
     * @param payload
     */
    public ServiceResult(T payload)
    {
        this(SUCCESS, null, payload);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        ServiceResult right = (ServiceResult) obj;
        return ObjectUtils.equals(getMessage(), right.getMessage())
            && ObjectUtils.equals(getPayload(), right.getPayload())
            && getResponseType() == right.getResponseType();
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }
    
    /**
     * @return the payload
     */
    public T getPayload()
    {
        return payload;
    }
    
    /**
     * @return the responseType
     */
    public int getResponseType()
    {
        return responseType;
    }
    
    /**
     * Return true is the response type is <code>AUTHENTICATION</code>, otherwise return false
     * @return
     */
    public boolean isAuthentication()
    {
        return isResponseType(AUTHENTICATION);
    }

    /**
     * Return true is the response type is <code>ERROR</code>, otherwise return false
     * @return
     */
    public boolean isError()
    {
        return isResponseType(ERROR);
    }

    /**
     * Return true if the respone type is <code>EXCEPTION</code>, otherwise return false
     * @return
     */
    public boolean isException()
    {
        return isResponseType(EXCEPTION);
    }

    /**
     * Return true if the respone type is <code>PERMISSION</code>, otherwise return false
     * @return
     */
    public boolean isPermission()
    {
        return isResponseType(PERMISSION);
    }
    
    /**
     * return true if response type equals the given response type
     * 
     * @param responseType
     * @return
     */
    public boolean isResponseType(int responseType)
    {
        return getResponseType() == responseType;
    }

    /**
     * Return true if the response type is <code>SUCCESS</code>, otherwise return false
     * 
     * @return
     */
    public boolean isSuccess()
    {
        return isResponseType(SUCCESS);
    }
    
    /**
     * Set response type to error and set message to given message
     * 
     * @param message
     */
    public void setErrorMessage(String message)
    {
        responseType = ERROR;
        this.message = message;
    }

}
