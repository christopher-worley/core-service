
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

import core.service.annotation.Security;
import core.service.annotation.Service;
import core.tooling.validation.NotEqual;
import core.tooling.validation.Required;

@Service
public interface MathService
{
    
	@Security(permissionKey=MockPermission.ADD)
    public Integer add(@Required(message="X is required") Integer x, 
    		@Required(message="Y is required") Integer y);
    
	@Security(permissionKey=MockPermission.SUBTRACT)
    public Integer subtract(@Required(message="X is required") Integer x, 
    		@Required(message="Y is required") Integer y);
	
	public Integer multiply(@Required(message="X is required") Integer x,
			@Required(message="Y is required") Integer y);
	
	public Integer divide(@Required(message="X is required") Integer x, 
			@Required(message="Y is required") 
			@NotEqual(value=1, message="Y cannot be equal to 0, divide by zero.") 
			Integer y);

}
