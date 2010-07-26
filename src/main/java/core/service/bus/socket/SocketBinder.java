
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


/**
 * Pair up sockets for dual connection
 * 
 * @author worleyc
 *
 */
public class SocketBinder
{

    /** socket to send data to remote host */
    private SocketWrapper send;
    
    /** socket to receive data from remote host */
    private SocketWrapper receive;

    public SocketBinder()
    {
        super();
    }
    
    public void close()
    {
        send.close();
        receive.close();
    }

    public SocketWrapper getSend()
    {
        return send;
    }

    public void setSend(SocketWrapper send)
    {
        this.send = send;
    }

    public SocketWrapper getReceive()
    {
        return receive;
    }

    public void setReceive(SocketWrapper receive)
    {
        this.receive = receive;
    }
    
    
}
