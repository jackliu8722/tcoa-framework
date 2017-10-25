package com.touclick.tcoa.framework.server.core;

import com.touclick.tcoa.framework.registry.Node;
import com.touclick.tcoa.framework.registry.TcoaRegistry;
import com.touclick.tcoa.framework.server.conf.ServiceConf;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TService which a service is running.
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public class TService implements Runnable{
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(TService.class);

    private TProcessor processor;

    private TServerTransport serverTransport;

    private TServer server;

    private ServiceConf conf;

    private List<Thread> threadList;

    private List<TService> serverList;

    private Node node;

    private TcoaRegistry registry;

    private String configPath;

    private boolean hasBase = false;

    private final ConcurrentHashMap<String,String> serviceMethodOptions_ =
            new ConcurrentHashMap<String, String>();

    protected TService(){

    }

    @Override
    public void run(){
        try{
            LOGGER.info(conf + " started.");

            server.serve();

            LOGGER.info(conf + " stoped.");
        }catch (Throwable t){
            LOGGER.error("Exception occur: ",t);
        }
    }

    public void stopServer(){
        server.stop();
    }

    public void disconnectZK() throws TException{

    }

    /**
     * Registry to the zookeeper
     * @throws TException
     */
    public void connectZK() throws TException{
        registry.registerNode(conf.getServiceId(),conf.getServiceVersion(),node);
    }

    public ConcurrentHashMap<String,String> getOptions(){
        return serviceMethodOptions_;
    }

    public TProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(TProcessor processor) {
        this.processor = processor;
    }

    public TServerTransport getServerTransport() {
        return serverTransport;
    }

    public void setServerTransport(TServerTransport serverTransport) {
        this.serverTransport = serverTransport;
    }

    public TServer getServer() {
        return server;
    }

    public void setServer(TServer server) {
        this.server = server;
    }

    public ServiceConf getConf() {
        return conf;
    }

    public void setConf(ServiceConf conf) {
        this.conf = conf;
    }

    public List<Thread> getThreadList() {
        return threadList;
    }

    public void setThreadList(List<Thread> threadList) {
        this.threadList = threadList;
    }

    public List<TService> getServerList() {
        return serverList;
    }

    public void setServerList(List<TService> serverList) {
        this.serverList = serverList;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public TcoaRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(TcoaRegistry registry) {
        this.registry = registry;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public boolean isHasBase() {
        return hasBase;
    }

    public void setHasBase(boolean hasBase) {
        this.hasBase = hasBase;
    }
}
