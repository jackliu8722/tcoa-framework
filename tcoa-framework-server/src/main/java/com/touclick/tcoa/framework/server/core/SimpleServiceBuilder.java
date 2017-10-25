package com.touclick.tcoa.framework.server.core;

import com.touclick.tcoa.framework.server.conf.ServiceConf;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

/**
 * Simple service builder implementation
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public class SimpleServiceBuilder extends AbstractServiceBuilder{

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleServiceBuilder.class);

    /** Max transport length (byte) */
    private final static long RPC_DATA_LENGTH_MAX = 16384000;

    @Override
    protected TProcessor buildProcessor(ServiceConf conf,TService service){

        LOGGER.debug("Begin to build TProcessor.");

        final String className = conf.getServiceClass();

        String ifaceClassName = "";
        String processorClassName = "";
        try{
            Class clz = Class.forName(className);

            String interfacePackageName = clz.getInterfaces()[0].
                    getPackage().getName();
            String interfaceName = clz.getInterfaces()[0].getSimpleName();
            /** remove 'I' */
            interfaceName = interfaceName.substring(1);

            ifaceClassName = interfacePackageName + "." + interfaceName + "$Iface";
            Class<?> ifaceClass = Class.forName(ifaceClassName);

            processorClassName = interfacePackageName + "." + interfaceName + "$Processor";
            Class<?> processorClass = Class.forName(processorClassName);
            Constructor<?> constructor = processorClass.getConstructor(ifaceClass);

            TProcessor processor = (TProcessor) constructor.newInstance(clz.newInstance());

            return processor;

        } catch (ClassNotFoundException e) {
            LOGGER.error(ifaceClassName + " or " + processorClassName + " is not found", e);
        } catch (SecurityException e) {
            LOGGER.error("", e);
        } catch (NoSuchMethodException e) {
            LOGGER.error("", e);
        } catch (InstantiationException e) {
            LOGGER.error("", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("", e);
        } catch (Throwable e) {
            LOGGER.error("Unknown throwable:", e);
        }

        return null;
    }

    /**
     * Generate TServerTransport
     * @param conf
     * @return
     */
    protected TServerTransport buildTServerTransport(ServiceConf conf){
        final int port = conf.getServicePort();
        TServerTransport serverTransport = null;
        try {
            serverTransport = new TNonblockingServerSocket(port);
        } catch (TTransportException e) {
            LOGGER.error(" buildTServerTransport error at conf: " + conf,e);
        }
        return serverTransport;
    }

    /**
     * Generate TServer
     * @param conf
     * @param serverTransport
     * @param processor
     * @return
     */
    protected TServer buildTServer(ServiceConf conf,
                                            TServerTransport serverTransport,
                                            TProcessor processor){
        THsHaServer.Args serverArgs = new THsHaServer.Args((TNonblockingServerSocket)serverTransport);
        serverArgs.maxReadBufferBytes = RPC_DATA_LENGTH_MAX;
        serverArgs.protocolFactory(new TBinaryProtocol.Factory(true, true));
        serverArgs.executorService(createExecuteService(conf));
        serverArgs.processor(new Processor(processor));

        TServer server = new THsHaServer(serverArgs);

        LOGGER.debug("TServer build complete for conf: " + conf);

        return server;
    }

    /**
     * Create execute service
     * @param conf
     * @return
     */
    private ExecutorService createExecuteService(ServiceConf conf){
        int coreSize = conf.getWorkerThreadPoolCoreSize();
        int maxSize = conf.getWorkerThreadPoolMaxSize();
        int queueSize = conf.getWorkerThreadPoolQueueSize();

        if(coreSize < 0 || maxSize < 0){
            coreSize = 2 * Runtime.getRuntime().availableProcessors();
            conf.setWorkerThreadPoolCoreSize(coreSize);

            maxSize = 2 * coreSize;
            conf.setWorkerThreadPoolMaxSize(maxSize);
        }

        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(queueSize);

        return new ThreadPoolExecutor(coreSize,maxSize,60,TimeUnit.SECONDS,
                    queue,new ServerThreadFactory());
    }
}
