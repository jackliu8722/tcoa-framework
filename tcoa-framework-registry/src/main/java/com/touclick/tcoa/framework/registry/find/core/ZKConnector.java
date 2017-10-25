package com.touclick.tcoa.framework.registry.find.core;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * The main Zookeeper connector
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public class ZKConnector {
    /** Logger */
    protected static final Logger LOGGER = LoggerFactory.getLogger(ZKConnector.class);

    private final String clusterAddress;

    private final Set<WatcherItem> watcherSet = Collections.synchronizedSet(new HashSet<WatcherItem>());

    private final Map<String,CreateItem> createItemMap = Collections.synchronizedMap(new HashMap<String, CreateItem>());

    private final int sessionTimeout;

    private byte[] auth;

    protected String root;

    protected ZooKeeper zk;

    private static final int WATCH_FAIL_LIMIT = 5;

    private static final int TIME_INTERVEL_TO_DO_WATCHER_SEC = 10 * 60;

    private static final int TIME_INTERVEL_TO_DO_RECREATE_NODE_MILLI = 5 * 1000;

    private static final int TIME_RAND_LIMIT_MILLI = 100;

    private static volatile boolean connected = false;

    public ZKConnector(String clusterAddress,int sessionTimeout) throws IOException,IllegalArgumentException{
        this.clusterAddress = clusterAddress;
        this.sessionTimeout = sessionTimeout;

        if(sessionTimeout < 3000){
            throw new IllegalArgumentException("Session timeout too small.");
        }

        connectToZookeeper();

        Thread thread = new Thread(new CyclistActiveWatcher(),"Thread-CyslistActiveWatcher");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Connect to the zookeeper
     * @throws IOException
     */
    private void connectToZookeeper() throws IOException{
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Start connectiong to the zookeeper,cluster = " + clusterAddress
                + ", sessionTimeout = " + sessionTimeout);
        }
        long startTime = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(1);
        this.zk = new ZooKeeper(clusterAddress,sessionTimeout,new ZKConnWatcher(this,latch));
        waitForConnectedTillDeath(latch);
        connected = true;

        long useTimes = (System.currentTimeMillis() - startTime) / 1000;
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Connect to zookeeper successfully, cluster = " + clusterAddress
                + ", sessionTimeout = " + sessionTimeout + ", useTime = " + useTimes + "(s)");
        }
    }

    /**
     * Wait for zookeeper to be connected, if can not connect, wait forever.
     * @param latch
     */
    private void waitForConnectedTillDeath(CountDownLatch latch) {
        while (true) {
            try {
                latch.await();
                return;
            } catch (InterruptedException e) {}
        }
    }

    /**
     * Add authenticate info for this zookeeper client.
     * @param username
     * @param password
     */
    public void addAuthInfo(String username,String password){
        try{
            auth = (username + ":" + password).getBytes("utf8");
            this.zk.addAuthInfo("digest",auth);
        }catch (UnsupportedEncodingException e){
            LOGGER.error("Failed to add auth info beacause your jdk doesn't support utf7.",e);
        }
    }

    public void close(){
        try{
            this.zk.close();
            connected = false;
        }catch (Exception e){
            throw FindException.makeInstance("Failed to close zookeeper client.",e);
        }
    }

    /**
     * Try to reconnect to zookeeper cluster.
     */
    public void reconnect(){
        while(true){
            try{
                LOGGER.warn("Reconnecting to new zookeeper.");
                close();
                connectToZookeeper();
                LOGGER.warn("Reconnected to new zookeeper.");
                if(auth != null){
                    this.zk.addAuthInfo("digest",auth);
                }

                new Thread(){
                    public void run(){
                        synchronized (createItemMap){
                            for(final CreateItem item : createItemMap.values()){
                                int interval = new Random(System.currentTimeMillis()).nextInt(TIME_RAND_LIMIT_MILLI);
                                while(connected){
                                    try{
                                        TimeUnit.MILLISECONDS.sleep(interval);
                                    }catch (InterruptedException e1){
                                        LOGGER.error("",e1);
                                    }

                                    try{
                                        doCreateNode(item.getPath(),item.getData(),item.getMode());
                                        LOGGER.info("Recreate path : " + item.getPath() + ", date : " + new String(item.getData()));
                                        doWatch(item.getPath(),item.getWatcher(),false);
                                    }catch (KeeperException.NodeExistsException e){
                                        break;
                                    }catch (Exception e){
                                        LOGGER.error("Falied to create path: " + item.getPath() + ", date:" + new String(item.getData()),e);
                                    }

                                    interval = interval << 1;
                                    interval = interval > TIME_INTERVEL_TO_DO_RECREATE_NODE_MILLI ? TIME_INTERVEL_TO_DO_RECREATE_NODE_MILLI : interval;
                                }
                            }
                        }
                    }
                }.start();

                synchronized (watcherSet){
                    for(WatcherItem item : watcherSet){
                        if(!createItemMap.containsKey(item.getPath())){
                            try{
                                item.setFailedTimes(0);
                                doWatch(item);
                            }catch (Exception e){
                                LOGGER.error("Failed to watch path: " + item.path,e);
                                item.setFailedTimes(1);
                            }
                        }
                    }
                }
                break;
            }catch (Exception e){
                LOGGER.error("Error occured when reconnect, system will retry.",e);
                try{
                    Thread.sleep(sessionTimeout / 3);
                }catch (Exception e1){

                }
            }
        }
    }

    public Object submitWatcher(String path,Watcher watcher,boolean isChildren){
        WatcherItem item = new WatcherItem(path,watcher,isChildren);
        Object r = doWatch(item);
        watcherSet.add(item);
        return r;
    }

    protected boolean deleteWatcher(String path,Watcher watcher,boolean isChildren){
        LOGGER.info("Try to remove " + (isChildren ? "node" : "data") + " watcher for " + path);
        return watcherSet.remove(new WatcherItem(path,watcher,isChildren));
    }

    /**
     * Do real watch
     * @param item
     * @return
     */
    private Object doWatch(WatcherItem item){
        return doWatch(item.getPath(), item.getWat(), item.isChildren());
    }

    private Object doWatch(String path,Watcher watcher,boolean isChildren){
        try{
            if(isChildren){
                return this.zk.getChildren(path,watcher);
            }else{
                Stat st = new Stat();
                return this.zk.getData(path,watcher,st);
            }
        }catch (Exception e){
            throw FindException.makeInstance("Failed to do watch.",e);
        }
    }

    /**
     * Create node and add request to list if it is a temporary node.
     * Temporary node will be created when reconnected to zookeeper.
     * @param path
     * @param isTemp
     */
    public void createNode(String path,boolean isTemp){
        createNode(path,null,isTemp,false);
    }

    public String createNode(String path,byte[] data,boolean isTmp,boolean isSequential){
        return createNode(path,data,isTmp,isSequential,true);
    }

    public String createNode(String path,byte[] data,boolean isTmp,boolean isSequential,boolean doWatch){
        try{
            CreateMode createMode = null;
            if(isTmp){
                if(isSequential){
                    createMode = CreateMode.EPHEMERAL_SEQUENTIAL;
                }else{
                    createMode = CreateMode.EPHEMERAL;
                }
            }else{
                if(isSequential){
                    createMode = CreateMode.PERSISTENT_SEQUENTIAL;
                }else{
                    createMode = CreateMode.PERSISTENT;
                }
            }

            String s = doCreateNode(path,data,createMode);

            if(isTmp && doWatch){
                final CreateItem item = new CreateItem(path,createMode,data);
                item.setWatcher(new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        if(watchedEvent.getType() != Event.EventType.NodeDataChanged){
                            return;
                        }

                        try{
                            Stat st = new Stat();

                            byte[] data = zk.getData(watchedEvent.getPath(),this,st);

                            if(!Arrays.equals(data,item.getData())){
                                LOGGER.info("Path: " + item.getPath() + ", data changed to :" + new String(data));
                                item.setData(data);
                            }
                        }catch (Exception e){
                            LOGGER.error("Falied to do Watch, path=" + watchedEvent.getPath(),e);
                        }
                    }
                });

                createItemMap.put(path, item);

                submitWatcher(path,item.getWatcher(),false);
            }

            return s;
        }catch (Exception e){
            throw FindException.makeInstance("Failed to create Node.",e);
        }
    }

    /**
     * Update the node data
     * @param path
     * @param data
     */
    public void updateNode(String path,byte[] data){
        try{
            zk.setData(path,data,-1);
            CreateItem item = createItemMap.get(path);
            if(item != null){
                item.setData(data);
            }
        }catch (Exception e){
            throw FindException.makeInstance("Failed to update node data.",e);
        }
    }

    /**
     * Do create node
     * @param path
     * @param data
     * @param createMode
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    private String doCreateNode(String path,byte[] data,CreateMode createMode)
            throws KeeperException,InterruptedException {
        return zk.create(path,data, ZooDefs.Ids.OPEN_ACL_UNSAFE,createMode);
    }

    /**
     * Delete the node
     * @param path
     * @return
     */
    protected boolean deleteNode(String path){
        try{
            zk.delete(path, -1);
            createItemMap.remove(path);
            return true;
        }catch (Exception e){
            throw FindException.makeInstance("Failed to delete node.",e);
        }
    }

    /**
     * return the root
     * @return
     */
    public String getRoot(){
        return root;
    }

    /**
     * Set the root
     * @param root
     */
    public void setRoot(String root){
        this.root = root;
    }

    private class CyclistActiveWatcher implements Runnable{

        @Override
        public void run(){
            while(true){
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("CyclistActiveWatcher running correctly.");
                }

                try{
                    synchronized (ZKConnector.this.watcherSet){
                        for(WatcherItem item: ZKConnector.this.watcherSet.toArray(new WatcherItem[0])){
                            if(!connected){
                                break;
                            }

                            try{
                                if(item.isChildren()){
                                    item.getWat().process(new WatchedEvent(Watcher.Event.EventType.NodeChildrenChanged,
                                            null,item.getPath()));
                                }else{
                                    item.getWat().process(new WatchedEvent(Watcher.Event.EventType.NodeDataChanged,
                                            null,item.getPath()));
                                }

                                item.setFailedTimes(0);
                            }catch (Exception e){
                                item.setFailedTimes(item.getFailedTimes() + 1);
                                if(item.getFailedTimes() >= WATCH_FAIL_LIMIT){
                                    LOGGER.warn("Try to remove " + (item.isChildren() ? "node" : "data") +
                                        " watcher for " + item.getPath());
                                    ZKConnector.this.watcherSet.remove(item);
                                }
                            }
                        }
                    }

                    TimeUnit.SECONDS.sleep(TIME_INTERVEL_TO_DO_WATCHER_SEC);
                }catch (InterruptedException e){
                    LOGGER.error("Watcher deamon thread is interrupted.",e);
                }catch (Exception e){
                    LOGGER.error("Undefined exception.",e);
                }
            }
        }
    }
}
