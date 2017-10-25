package com.touclick.tcoa.framework.server.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service conf
 */
public final class ServiceConf {

    /** Logger */
    private static final Logger LOGGER  = LoggerFactory.getLogger(ServiceConf.class);

    /** service tag*/
    public static final String SERVER_TAG = "service";

    public static final String SERVER_ID_ATTR = "id";

    public static final String SERVER_VERSION_ATTR = "version";

    public static final String SERVER_CLASS_ATTR = "class";

    public static final String SERVER_PORT_ATTR = "port";


    /** worker-thread-pool tag*/
    public static final String WORKER_PTHREAD_POOL_TAG = "worker-thread-pool";

    public static final String WORKER_PTHREAD_POOL_CORE_SIZE_ATTR = "core-size";

    public static final String WORKER_PTHREAD_POOL_MAX_SIZE_ATTR = "max-size";

    public static final String WORKET_PTHREAD_POOL_QUEUE_SIZE_ATTR = "queueSize";


    /** Default configuration file name*/
    public static final String DEFAULT_SERVICE_CONFIG_FILE_NAME = "/tcoa-service.xml";

    /** Service configuration cache */
    private static Map<String,ServiceConf> serviceConfCache = new HashMap<String,ServiceConf>();

    /**
     * Load the service config
     */
    public static void load(){
        load(DEFAULT_SERVICE_CONFIG_FILE_NAME);
    }

    /**
     * Load the service config
     * @param configName
     */
    public static void load(String configName){
        if(configName == null || configName.trim().length() == 0){
            throw new IllegalArgumentException("Service config filename is empty.");
        }

        File file = new File(ServiceConf.class.getResource(configName).getPath());
        try{
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

            NodeList serviceList = doc.getElementsByTagName(SERVER_TAG);

            if(serviceList == null || serviceList.getLength() == 0){
                LOGGER.error("tcoa service configuration file has no service");
                return ;
            }

            for(int i = 0 ; i < serviceList.getLength(); i++){
                Node serviceNode = serviceList.item(i);
                NamedNodeMap attributes = serviceNode.getAttributes();

                ServiceConf conf = new ServiceConf();

                String id = attributes.getNamedItem(SERVER_ID_ATTR).getNodeValue();
                conf.setServiceId(id);
                String version = attributes.getNamedItem(SERVER_VERSION_ATTR).getNodeValue();
                conf.setServiceVersion(version);
                conf.setServiceClass(attributes.getNamedItem(SERVER_CLASS_ATTR).getNodeValue());
                conf.setServicePort(Integer.parseInt(attributes.getNamedItem(SERVER_PORT_ATTR).getNodeValue()));

                NodeList tagList = serviceNode.getChildNodes();
                if(tagList != null){
                    for(int j = 0 ; j < tagList.getLength(); j++){
                        Node tagNode = tagList.item(j);
                        String tagName = tagNode.getNodeName();
                        NamedNodeMap tagAttributes = tagNode.getAttributes();

                        if(WORKER_PTHREAD_POOL_TAG.equals(tagName)){
                            int coreSize = Integer.parseInt(tagAttributes.getNamedItem(WORKER_PTHREAD_POOL_CORE_SIZE_ATTR)
                                            .getNodeValue());
                            int maxSize = Integer.parseInt(tagAttributes.getNamedItem(WORKER_PTHREAD_POOL_MAX_SIZE_ATTR)
                                            .getNodeValue());
                            int queueSize = Integer.parseInt(tagAttributes.getNamedItem(WORKET_PTHREAD_POOL_QUEUE_SIZE_ATTR)
                                            .getNodeValue());

                            conf.setWorkerThreadPoolCoreSize(coreSize);
                            conf.setWorkerThreadPoolMaxSize(maxSize);
                            conf.setWorkerThreadPoolQueueSize(queueSize);

                        }
                    }
                }

                if(id == null || id.trim().length() == 0 || version == null ||
                        version.length() == 0){
                    LOGGER.warn("Some service has no id or version in service configuration.");
                    continue;
                }

                serviceConfCache.put(constructKey(id,version),conf);
            }
        }catch (SAXException e) {
            LOGGER.error("tcoa configuration file is NOT legal.", e);
        } catch (IOException e) {
            LOGGER.error("tcoa configuration file is NOT legal.", e);
        } catch (Exception e) {
            LOGGER.error("tcoa configuration file is NOT legal.", e);
        }
    }

    private static String constructKey(String serviceId,String version){
        return serviceId + "-" + version;
    }

    /**
     * Return all the serviceConf
     * @return
     */
    public static Map<String,ServiceConf> getServiceConfCache(){
        return serviceConfCache;
    }

    /**
     * Get the serviceConf by given serviceId and version.
     * @param serviceId
     * @param version
     * @return
     */
    public static ServiceConf getServiceConf(String serviceId,String version){
        return serviceConfCache.get(constructKey(serviceId,version));
    }

    /** Service attr */
    private String serviceId;

    private String serviceVersion;

    private int servicePort;

    private String serviceClass;

    /** worker thread pool attr */
    private int workerThreadPoolCoreSize;

    private int workerThreadPoolMaxSize;

    private int workerThreadPoolQueueSize;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(" serviceId=").append(serviceId).append(",servicePort=").append(servicePort)
                .append(",serviceClass=").append(serviceClass)
                .append(",threadPoolCoreSize=").append(workerThreadPoolCoreSize)
                .append(",threadPoolMaxSize=").append(workerThreadPoolMaxSize);
        return sb.toString();
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public int getWorkerThreadPoolCoreSize() {
        return workerThreadPoolCoreSize;
    }

    public void setWorkerThreadPoolCoreSize(int workerThreadPoolCoreSize) {
        this.workerThreadPoolCoreSize = workerThreadPoolCoreSize;
    }

    public int getWorkerThreadPoolMaxSize() {
        return workerThreadPoolMaxSize;
    }

    public void setWorkerThreadPoolMaxSize(int workerThreadPoolMaxSize) {
        this.workerThreadPoolMaxSize = workerThreadPoolMaxSize;
    }

    public int getWorkerThreadPoolQueueSize() {
        return workerThreadPoolQueueSize;
    }

    public void setWorkerThreadPoolQueueSize(int workerThreadPoolQueueSize) {
        this.workerThreadPoolQueueSize = workerThreadPoolQueueSize;
    }
}
