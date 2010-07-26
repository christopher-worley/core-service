
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
 */package core.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

/**
 * Singleton containing values for the 
 * information-service.properties file
 * found in the classpath
 * 
 * @author worleyc
 *
 */
public class InformationServiceProperties
{
    /** logger for this class */
    private static final Logger log = LogFactory.getLogger(InformationServiceProperties.class);
    
    /** information service properties filename */
    private static final String FILENAME = "information-service.properties";
    
    /** context key prefix */
    private static final String PREFIX_CONTEXT = "core.information.context.";
    
    /** singleton instance */
    private static volatile InformationServiceProperties instance;
    
    /** true if context file loaded successfully */
    private boolean failed = false;

    /**
     * Return singleton instance
     * 
     * @return
     */
    public static InformationServiceProperties getInstance()
    {
        if (instance == null)
        {
            synchronized (InformationServiceProperties.class)
            {
                if (instance == null)
                {
                    instance = new InformationServiceProperties();
                }
            }
        }
        return instance;
    }
    
    private Properties properties = new Properties();

    
    /**
     * 
     */
    private InformationServiceProperties()
    {
        super();
        initialize();
    }
    
    
    /**
     * Return all context filenames to be loaded in the information context
     *  
     * @return null if no properties file was loaded; otherwise list of context filenames
     */
    public List<String> getContextFilenames()
    {
        if (failed)
        {
            return null;
        }
        
        List<String> filenames = new ArrayList<String>();
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements())
        {
            String key = (String)keys.nextElement();
            if (key.startsWith(PREFIX_CONTEXT))
            {
                filenames.add(properties.getProperty(key));
            }
        }
        return filenames;
    }
    
    /**
     * Open file if it exist and prepare the properties object
     * 
     */
    private void initialize()
    {
        try
        {
            InputStream input = ClassLoader.getSystemResourceAsStream(FILENAME);
            if (input != null)
            {
                properties.load(input);
            }
            else
            {
                log.info("Information service properties file not found on classpath.");
                failed = true;
            }
        }
        catch (IOException e)
        {
            log.info("Exception occured when attempting to read information service properties: " + e.getMessage(), e);
            failed = true;
        }
    }
    

}
