
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

import org.springframework.context.annotation.Configuration;


@Configuration
public class MathServiceImpl implements MathService
{

    public MathServiceImpl()
	{
		super();
	}

	@Override
    public Integer add(Integer x, Integer y)
    {
        return x + y;
    }

    @Override
    public Integer subtract(Integer x, Integer y)
    {
        return x - y;
    }

	@Override
	public Integer multiply(Integer x, Integer y) {
		return x * y;
	}

	@Override
	public Integer divide(Integer x, Integer y) 
	{
		return x / y;
	}

}
