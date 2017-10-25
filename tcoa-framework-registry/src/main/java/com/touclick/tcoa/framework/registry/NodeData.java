package com.touclick.tcoa.framework.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Node data class
 *
 * @author bing.liu
 * @date 2015-08-16
 * @version 1.0
 * 
 */
public class NodeData {
   
    private static Logger LOGGER = LoggerFactory.getLogger(NodeData.class.getName());

    public final static String HEALTHY_FIELD = "healthy";

    public final static String DISABLED_FIELD = "disabled";

    private boolean disabled;

    private boolean healthy;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public NodeData(boolean disabled, boolean healthy) {
        this.disabled = disabled;
        this.healthy = healthy;
    }

    @Override
    public String toString() {
        return "NodeData [disabled=" + disabled + ", healthy=" + healthy + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (disabled ? 1231 : 1237);
        result = prime * result + (healthy ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        NodeData other = (NodeData) obj;
        if (disabled != other.disabled) {
            return false;
        }
        if (healthy != other.healthy) {
            return false;
        }
        return true;
    }

    public byte[] toBytes() {
        return new StringBuffer().append(DISABLED_FIELD).append("=")
                .append(String.valueOf(this.disabled)).append("\n").append(HEALTHY_FIELD)
                .append("=").append(String.valueOf(this.healthy)).append("\n").toString().getBytes();
    }

    public static NodeData valueOf(String data) {
        return valueOf(data.getBytes());
    }

    public static NodeData valueOf(byte[] data) {
        Properties prop = new Properties();
        try {
            prop.load(new ByteArrayInputStream(data));
        } catch (IOException e) {
            LOGGER.warn("Failed to read NodeData: " + new String(data), e);
            throw new IllegalArgumentException("Invalid data");
        }

        String healthy = prop.getProperty(HEALTHY_FIELD);
        String disabled = prop.getProperty(DISABLED_FIELD);

        if (disabled == null || (!"true".equals(disabled) && !"false".equals(disabled))) {
            throw new IllegalArgumentException("Invalid disabled field in data.");
        }

        if (healthy == null || (!"true".equals(healthy) && !"false".equals(healthy))) {
            throw new IllegalArgumentException("Invalid healthy field in data.");
        }

        return new NodeData("true".equals(disabled) ? true : false,
                "true".equals(healthy) ? true : false);
    }

    public static void main(String[] args) {
        valueOf(new String(new NodeData(true, true).toBytes()));
    }
}
