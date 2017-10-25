package com.touclick.tcoa.framework.registry;

import com.touclick.tcoa.framework.registry.accessor.FindServiceRegistryFactory;
import com.touclick.tcoa.framework.registry.impl.TcoaRegistryImpl;

/**
 * TcoaRegistry factory
 *
 * @author bing.liu
 * @date 2015-08-13
 * @version 1.0
 */
public class TcoaRegistryFactory {

    private static TcoaRegistryFactory instance = new TcoaRegistryFactory();

    public static TcoaRegistryFactory getInstance(){
        return instance;
    }

    private TcoaRegistry registry;

    private TcoaRegistryFactory(){
        this.registry = new TcoaRegistryImpl(new FindServiceRegistryFactory());
    }

    public TcoaRegistry getRegistry(){
        return registry;
    }
}
