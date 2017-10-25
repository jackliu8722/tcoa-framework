package com.touclick.tcoa.framework.server.core;

import com.touclick.tcoa.framework.server.conf.ServiceConf;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract service builder implement.
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public abstract class AbstractServiceBuilder implements IServiceBuilder{
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceBuilder.class);

    /**
     * Generate TProcessor
     * @param conf
     * @param service
     * @return
     */
    protected abstract TProcessor buildProcessor(ServiceConf conf,TService service);

    /**
     * Generate TServerTransport
     * @param conf
     * @return
     */
    protected abstract TServerTransport buildTServerTransport(ServiceConf conf);

    /**
     * Generate TServer
     * @param conf
     * @param serverTransport
     * @param processor
     * @return
     */
    protected abstract TServer buildTServer(ServiceConf conf,
                                            TServerTransport serverTransport,
                                            TProcessor processor);

    @Override
    public final TService build(ServiceConf conf){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Start build service: " + conf);
        }

        TService service = new TService();
        service.setConf(conf);

        TProcessor processor = this.buildProcessor(conf,service);

        if(processor != null){
            service.setProcessor(processor);
        }else{
            LOGGER.error(" buildProcessor Error, system exit with 1, please check serviceConf: " + conf);
            System.exit(1);
        }

        TServerTransport serverTransport = null;
        serverTransport = this.buildTServerTransport(conf);
        if(serverTransport != null){
            service.setServerTransport(serverTransport);
        }else{
            LOGGER.error(" buildTServerTransport Error, system exit with 1, please check serviceConf: " + conf);
            System.exit(1);
        }

        TServer server = this.buildTServer(conf,serverTransport,processor);
        if(server != null){
            service.setServer(server);
        }else{
            LOGGER.error(" buildTServer Error, system exit with 1, please check serviceConf: " + conf);
            System.exit(1);
        }

        LOGGER.debug("TService build complete: " + conf);

        return service;
    }
}
