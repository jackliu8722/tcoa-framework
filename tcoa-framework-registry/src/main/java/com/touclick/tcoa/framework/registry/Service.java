package com.touclick.tcoa.framework.registry;

import java.util.ArrayList;
import java.util.List;

/**
 * Service
 *
 * @author bing.liu
 * @date 2015-08-13
 * @version 1.0
 */
public class Service {
    /** Service id*/
    private String serviceId;

    /** Version */
    private String version;

    /** Node list */
    private List<Node> nodes = new ArrayList<Node>();

    public Service(String serviceId,String version,List<Node> n){
        this.serviceId = serviceId;
        this.version = version;
        if(n != null && n.size() > 0){
            this.nodes.addAll(n);
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Service [id=").append(serviceId).append(",version=").append(version);
        sb.append(",nodes=").append(getNodes()).append("]");
        return sb.toString();
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
