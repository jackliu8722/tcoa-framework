package com.touclick.tcoa.framework.registry.accessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration for information
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public class Configuration {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    /** type field */
    private final static String TYPE_FIELD = "type";

    /** root field */
    public final static String ROOT_FIELD = "root";

    /** cluster field */
    public final static String CLUSTER_FIELD = "cluster";

    /** server username field */
    public final static String SERVER_USERNAME_FIELD = "server.username";

    /** server password field */
    public final static String SERVER_PASSOWRD_FIELD = "server.passowrd";

    /** client username field */
    public final static String CLIENT_USERNAME_FIELD = "client.username";

    /** client password field */
    public final static String CLIENT_PASSOWRD_FIELD = "client.passowrd";

    /** Configuration file format */
    private static final String CONFIGURATION_FILE_FORMAT = "/%s-%s-tcoa.properties";

    private Type type;

    private String root;

    private String cluster;

    private String serverUsername;

    private String serverPassword;

    private String clientUsername;

    private String clientPassword;

    public Configuration(String service,String version) throws InvalidConfigurationException{
        Properties props = new Properties();
        String path = String.format(CONFIGURATION_FILE_FORMAT,service,version);

        try{
            InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(path));
            props.load(in);
        }catch (FileNotFoundException e){
            throw new InvalidConfigurationException(path + " is not found.",e);
        }catch (IOException e){
            throw new InvalidConfigurationException("Can not load config file: " + path,e);
        }

        initConfiguration(props);
    }

    private void initConfiguration(Properties props) throws InvalidConfigurationException{
        if(LOGGER.isDebugEnabled()){
            StringBuilder sb = new StringBuilder();
            sb.append("Loaded config: [ ");

            for(Map.Entry<?,?> e: props.entrySet()){
                if(CLIENT_PASSOWRD_FIELD.equals(e.getKey().toString())||
                        SERVER_PASSOWRD_FIELD.equals(e.getKey().toString())){
                    sb.append(e.getKey() + ":******").append(", ");
                }else{
                    sb.append(e.getKey() + ":"  + e.getValue()).append(", ");
                }
            }
            sb.append("]");
            LOGGER.debug(sb.toString());
        }

        if(!props.containsKey(TYPE_FIELD)){
            throw new InvalidConfigurationException("Config is missing required filed '" +
            TYPE_FIELD + "'");
        }

        if(!props.containsKey(ROOT_FIELD)){
            throw new InvalidConfigurationException("Config is missing required field '" +
                ROOT_FIELD + "'");
        }

        if(!props.containsKey(CLUSTER_FIELD)){
            throw new InvalidConfigurationException("Config is missing required field '" +
                CLUSTER_FIELD + "'");
        }

        /** Get the type */
        try{
            this.type = Type.valueOf(props.getProperty(TYPE_FIELD));
        }catch (Exception e){
            throw new InvalidConfigurationException("Tcoa does not support for type: "
                + props.getProperty(TYPE_FIELD) + ", please check it.",e);
        }

        this.root = props.getProperty(ROOT_FIELD);
        this.cluster = props.getProperty(CLUSTER_FIELD);

        this.clientUsername = props.getProperty(CLIENT_USERNAME_FIELD,"");
        this.clientPassword = props.getProperty(CLIENT_PASSOWRD_FIELD,"");

        this.serverUsername = props.getProperty(SERVER_USERNAME_FIELD,"");
        this.serverPassword = props.getProperty(SERVER_PASSOWRD_FIELD,"");
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + ((clientPassword == null) ? 0 : clientPassword.hashCode());
        result = prime * result + ((clientUsername == null) ? 0 : clientUsername.hashCode());
        result = prime * result + ((root == null) ? 0 : root.hashCode());
        result = prime * result + ((cluster == null) ? 0 : cluster.hashCode());
        result = prime * result + ((serverUsername == null) ? 0 : serverUsername.hashCode());
        result = prime * result + ((serverPassword == null) ? 0 : serverPassword.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) {
            return true;
        }

        if(obj == null){
            return false;
        }

        if(getClass() != obj.getClass()){
            return false;
        }

        Configuration other = (Configuration) obj;

        if(clientPassword == null){
            if(other.clientPassword != null){
                return false;
            }else if(!clientPassword.equals(other.clientPassword)){
                return false;
            }
        }

        if(clientUsername == null){
            if(other.clientUsername != null){
                return false;
            }else if(!clientUsername.equals(other.clientUsername)){
                return false;
            }
        }

        if(cluster == null){
            if(other.cluster != null){
                return false;
            }else if(!cluster.equals(other.cluster)){
                return false;
            }
        }

        if(root == null){
            if(other.root != null){
                return false;
            }else if(!root.equals(other.root)){
                return false;
            }
        }

        if(serverPassword == null){
            if(other.serverPassword != null){
                return false;
            }else if(!serverPassword.equals(other.serverPassword)){
                return false;
            }
        }

        if(serverUsername == null){
            if(other.serverUsername != null){
                return false;
            }else if(!serverUsername.equals(other.serverUsername)){
                return false;
            }
        }
        return true;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getServerUsername() {
        return serverUsername;
    }

    public void setServerUsername(String serverUsername) {
        this.serverUsername = serverUsername;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * The enum of the type supported.
     */

    public enum Type{

        /** Support zookeeper */
        zookeeper("zookeeper",0),

        /** Support only given hosts list*/
        host("host",1);

        final String name;
        final int index;
        private Type(String name,int index){
            this.name = name;
            this.index = index;
        }

        public String getName(){
            return name;
        }
        public int getIndex(){
            return index;
        }
    }
}
