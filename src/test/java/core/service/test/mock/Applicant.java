
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

import core.service.validation.Max;
import core.service.validation.Min;
import core.service.validation.StringValidation;

public class Applicant implements Serializable
{
    
	@StringValidation(allowEmpty=false, maxSize=30)
    private String firstName;
    
	@StringValidation(allowEmpty=false, maxSize=60)
    private String lastName;
    
    @StringValidation(allowNull=true, maxSize=1)
    private String middleInitial;
    
    @Min(value=1)
    @Max(value=110)
    private Integer age;

    
    public Applicant(String firstName, String lastName, Integer age)
    {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public Integer getAge()
    {
        return age;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

}
