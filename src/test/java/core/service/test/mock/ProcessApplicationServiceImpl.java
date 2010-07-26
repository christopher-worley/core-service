
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

import core.service.result.ServiceResult;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class ProcessApplicationServiceImpl implements ProcessApplicationService
{
    /** logger for this class */
    private static Logger logger = LogFactory.getLogger(ProcessApplicationServiceImpl.class);

    @Override
    public ServiceResult<Application> applyForDriving(Application application, Applicant applicant)
    {
        application.setLicenseeNumber("124575");
        application.setAccepted(true);
        return ServiceResult.success("Driver application accepted.", application);
    }

    @Override
    public ServiceResult<Application> applyForGun(Application application, Applicant applicant)
    {
        logger.info("Get your gun!");
        application.setLicenseeNumber("0810252");
        application.setAccepted(true);
        return ServiceResult.success("Gun application accepted.", application);
    }

}
