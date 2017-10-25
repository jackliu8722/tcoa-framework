package com.touclick.tcoa.framework.registry;

/**
 *
 * Registry
 *
 * @author bing.liu
 * @date 2015-08-13
 * @version 1.0
 */
public interface TcoaRegistry {

    /**
     * Register a service node in the registry center
     * @param serviceId
     * @param version
     * @param node
     */
    public void registerNode(String serviceId, String version,Node node);

    /**
     * Destroy a service node in the registry center
     * @param serviceId
     * @param version
     * @param node
     */
    public void destroyNode(String serviceId,String version,Node node);

    /**
     * Query the service
     * @param serviceId
     * @param version
     * @return
     */
    public Service queryService(String serviceId,String version);
}
