package com.touclick.tcoa.framework.server.core;

import com.touclick.tcoa.framework.server.conf.ServiceConf;

/**
 * Service builder api
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public interface IServiceBuilder {

    /**
     * Build TService by given service config
     * @param conf
     * @return
     */
    public TService build(ServiceConf conf);
}
