package com.touclick.tcoa.framework.client.router;

import com.touclick.tcoa.framework.registry.Node;
import com.touclick.tcoa.framework.registry.Service;
import com.touclick.tcoa.framework.registry.TcoaRegistry;
import com.touclick.tcoa.framework.registry.TcoaRegistryFactory;

import java.util.Collections;
import java.util.List;

/**
 * The implementation of the registry loader
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public class RegistryNodeLoader implements NodeLoader{

    private static TcoaRegistryFactory tcoaRegistryFactory = TcoaRegistryFactory.getInstance();

    protected static void setTcoaRegistryFactory(TcoaRegistryFactory factory){
        tcoaRegistryFactory = factory;
    }

    private TcoaRegistry tcoaRegistry = tcoaRegistryFactory.getRegistry();

    public void setTcoaRegistry(TcoaRegistry registry){
        this.tcoaRegistry = registry;
    }

    @Override
    public List<Node> load(String serviceId,String version){
        Service service = tcoaRegistry.queryService(serviceId,version);
        List<Node> nodes = Collections.emptyList();

        if(service != null){
            nodes = service.getNodes();
        }

        return nodes;
    }
}
