package com.touclick.tcoa.framework.registry.find.consumer;

import com.touclick.tcoa.framework.registry.NodeData;

import java.util.ArrayList;
import java.util.List;

/**
 * The consumer for host
 *
 * @author bing.liu
 * @date 2015-10-24
 * @version 1.0
 */
public class HostConsumer implements Consumer {

    private List<String> hostList = new ArrayList<String>();

    public HostConsumer(String hosts){
        if(hosts != null){
            String h[] = hosts.split(",");
            if(h != null){
                for(String hostAndPort : h){
                    hostList.add(hostAndPort.trim());
                }
            }
        }
    }

    @Override
    public List<String> getAllStatAndListenChange(String service, String version, NodeChangeListener listener) {
        return hostList;
    }

    @Override
    public byte[] getMetaDataAndListenChange(String service, String version, DataChangeListener listener) {
        return new byte[0];
    }

    @Override
    public List<String> getAddressAndListenChange(String service, String version, NodeChangeListener listener) {
        return hostList;
    }

    @Override
    public byte[] getAddressAndListenChange(String service, String version, String node, DataChangeListener listener) {
        return new NodeData(false,true).toBytes();
    }
}
