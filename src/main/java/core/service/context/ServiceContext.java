package core.service.context;

import java.util.HashMap;
import java.util.Map;

public class ServiceContext
{
    
    private static final ThreadLocal context = new ThreadLocal();

    public ServiceContext()
    {
        super();
        context.set(new HashMap<String, Object>());
    }
    
    public void setAttribute(String key, Object object)
    {
        ((Map)context.get()).put(key, object);
    }
    
    public Object getAttribute(String key)
    {
        return ((Map)context.get()).get(key);
    }
    
    public void remove()
    {
        context.remove();
    }
    
}
