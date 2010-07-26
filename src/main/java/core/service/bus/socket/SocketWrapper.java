
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.IOUtils;

/**
 * socket wrapper making it easy to 
 * access object streams
 * 
 * @author worleyc
 *
 */
public class SocketWrapper
{
    
    private Socket socket;
    
    private String connectionId;
    
    private volatile ObjectInputStream input;
    
    private volatile Semaphore lock;
    
    private volatile ObjectOutputStream output;
    
    public SocketWrapper(Socket socket)
    {
        super();
        this.socket = socket;
        lock = new Semaphore(1);
    }

    public void acquire()
    {
        try
        {
            lock.acquire();
        } 
        catch (InterruptedException e)
        {
        }
    }

    public void close()
    {
        lock.release();
        IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(output);
        try
        {
            socket.close();
        } 
        catch (IOException e)
        {
            // ignore
        }
    }

    public String getConnectionId()
    {
        return connectionId;
    }

    public ObjectInputStream getObjectInputStream() throws IOException 
    {
        synchronized (this)
        {
            if (input == null) 
            {
                input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
        }
        return input;
    }

    public ObjectOutputStream getObjectOutputStream() throws IOException
    {
        synchronized (this)
        {
            if (output == null)
            {
                output = new ObjectOutputStream(socket.getOutputStream());
            }
        }
        return output;
    }

    public void release()
    {
        lock.release();        
    }

    public void setConnectionId(String connectionId)
    {
        this.connectionId = connectionId;
    }

}
