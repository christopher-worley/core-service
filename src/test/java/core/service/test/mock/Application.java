
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
 */package core.service.test.mock;

import java.io.Serializable;

public class Application implements Serializable
{

    public static final int TYPE_DRIVER_LICENSEE = 0;
    public static final int TYPE_GUN_LICENSEE = 1;

    private int type = -1;

    private Applicant applicant;
    
    private boolean accepted;

    private String message;

    private String licenseeNumber;
    
    public Application(int type)
    {
        super();
        this.type = type;
    }

    public String getLicenseeNumber()
    {
        return licenseeNumber;
    }

    public String getMessage()
    {
        return message;
    }
    
    public int getType()
    {
        return type;
    }

    public boolean isAccepted()
    {
        return accepted;
    }

    public void setAccepted(boolean accepted)
    {
        this.accepted = accepted;
    }

    public void setApplicant(Applicant applicant)
    {
        this.applicant = applicant;
    }

    public void setLicenseeNumber(String licenseeNumber)
    {
        this.licenseeNumber = licenseeNumber;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setType(int type)
    {
        this.type = type;
    }
    
    
}
