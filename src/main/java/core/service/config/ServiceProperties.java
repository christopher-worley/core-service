package core.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import core.service.exception.ServiceException;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;
 
// TODO: Pull file related code up to ServiceFileProperties, change Service Properties to interface and create a pojo impl for default use.
@Deprecated
public class ServiceProperties
{
    /** logger for this class */
    private static final Logger log = LogFactory.getLogger(ServiceProperties.class);
    
    /** information service properties filename */
    private static final String FILENAME = "service.properties";
    
    /** context key prefix */
    private static final String PREFIX_CONTEXT = "core.information.context.";
    
    /** properties filename */
    private String filename = FILENAME;
    
    /** true if context file loaded successfully */
    private boolean failed = false;
    
    /** properties */
    private Properties properties = new Properties();
    
    /** map of interface and implementing class pairs for all services */
    private Map<Class, Class> interfaceImplMap = new HashMap<Class, Class>();

    /**
     * Default constructor to use default filename
     */
    public ServiceProperties()
    {
        super();
    }
    
    /**
     * Use this constructor to override default filename
     * 
     * @param filename
     */
    public ServiceProperties(String filename) {
    	super();
    	this.filename = filename;
    }
    
    /**
     * Add service interface and implementation relationships into map
     * 
     * @param serviceInterface
     * @param serviceImpl
     */
    public void addInterfaceImpl(Class serviceInterface, Class serviceImpl) {
    	interfaceImplMap.put(serviceInterface, serviceImpl);
    }
    
    /**
     * @return
     */
    public Class<?> getDefaultServiceExecutor() {
    	initialize();
    	try
		{
    		String className = properties.getProperty("default.executor");
			return Class.forName(className);
		} 
    	catch (ClassNotFoundException e)
		{
			throw new ServiceException("Failed to find default service executor: " + e.getMessage(), e);
		}
    }
    
    /**
     * Get the class implementing the service interface
     * 
     * @param serviceInterface
     * @return
     */
    public Class getImplementingClass(Class serviceInterface) {
    	return interfaceImplMap.get(serviceInterface);
    }
    
    /**
     * Get service executor to be used for the given
     * service name.
     * 
     * @param name
     * @return
     */
    public Class<?> getServiceExecutor(String name) {
    	initialize();
    	// TODO: implement to check name against properties defined in properties file
    	return getDefaultServiceExecutor();
    }
    
    /**
     * Open file if it exist and prepare the properties object
     * 
     */
    private void initialize()
    {
    	if (failed) {
    		return;
    	}
        try
        {
            InputStream input = ClassLoader.getSystemResourceAsStream(filename);
            if (input != null)
            {
                properties.load(input);
            }
            else
            {
                log.info("Information service properties file not found on classpath (filename=" + filename + ").");
                failed = true;
            }
        }
        catch (IOException e)
        {
            log.info("Exception occured when attempting to read information service properties (filename=" + filename + "): " + e.getMessage(), e);
            failed = true;
        }
    }
    

}
