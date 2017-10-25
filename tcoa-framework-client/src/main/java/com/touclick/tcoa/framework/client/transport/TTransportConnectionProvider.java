package com.touclick.tcoa.framework.client.transport;

import com.touclick.tcoa.framework.client.exception.TcoaException;
import com.touclick.tcoa.framework.registry.Node;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;

/**
 * The manager class of the connection pool
 *
 * @author bing.liu
 * @date 2015-08-05
 * @version 1.0
 */
public class TTransportConnectionProvider implements ConnectionProvider{

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(TTransportConnectionProvider.class);

    /** The max number of the active object in the pool*/
    private static int maxActive = 200;

    /** The max number of the idle object in the pool*/
    private static int maxIdle = 40;

    /** The min number of the idle object in the pool*/
    private static int minIdle = 10;

    /** The max wait time (ms)*/
    private static long maxWait = 10;

    /** Weather execute PoolableObjectFactory.validateObject method
     * when allocate object from the pool*/
    private boolean testOnBorrow = false;

    /** Weather execute PoolableObjectFactory.validateObject method
     * when return object */
    private boolean testOnReturn = false;

    private boolean testWhileIdel = false;

    private static HashMap<String,ObjectPool> servicePoolMap = new HashMap<String, ObjectPool>();

    public TTransportConnectionProvider(){

    }

    /**
     * Set the pool parameter
     *
     * @param maxActive
     * @param maxIdle
     * @param minIdle
     * @param maxWait
     */
    public static void setPoolParam(int maxActive,int maxIdle,int minIdle,int maxWait){
        TTransportConnectionProvider.maxActive = maxActive;
        TTransportConnectionProvider.maxIdle = maxIdle;
        TTransportConnectionProvider.minIdle = minIdle;
        TTransportConnectionProvider.maxWait = maxWait;
    }

    /**
     * Create the pool given the servicde node and connection time out
     * @param node
     * @param conTimeout
     * @return
     */
    ObjectPool createPool(Node node,int conTimeout){
        /** Create factory*/
        ThriftPoolableObjectFactory thriftPoolableObjectFactory =
                new ThriftPoolableObjectFactory(node.getHost(),node.getPort(),conTimeout);

        GenericObjectPool objectPool = new GenericObjectPool(thriftPoolableObjectFactory);

        objectPool.setMaxActive(maxActive);
        objectPool.setMaxIdle(maxIdle);
        objectPool.setMinIdle(minIdle);
        objectPool.setMaxWait(maxWait);

        objectPool.setTestOnBorrow(testOnBorrow);
        objectPool.setTestOnReturn(testOnReturn);
        objectPool.setTestWhileIdle(testWhileIdel);

        /** borrowObject method will lock when there is no object in the pool*/
        objectPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);

        return objectPool;
    }

    /**
     * Get the pool connection status
     * @return
     */
    public static String getConnStatus(){
        StringBuilder message = new StringBuilder();
        for(Map.Entry<?,?> entry : servicePoolMap.entrySet()){
            String key = (String) entry.getKey();
            ObjectPool pool = (ObjectPool) entry.getValue();

            message.append("Status of connection [").append(key).append("] is:\n");
            message.append("\tpool using size:").append(pool.getNumActive()).append("\n");
            message.append("\tpool idle size:").append(pool.getNumIdle()).append("\n");
        }
        return message.toString();
    }

    /**
     * Get a connection
     * @param node
     * @param connTimeout
     * @return
     * @throws TcoaException
     */
    @Override
    public TTransport getConnection(Node node,long connTimeout) throws TcoaException {
        TTransport transport = null;
        String key = node.getNodeKey();
        ObjectPool pool = null;

        try{
            pool = servicePoolMap.get(key);
            if(pool == null){
                synchronized (key.intern()){
                    if(!servicePoolMap.containsKey(key)){
                        pool = createPool(node,(int) connTimeout);
                        servicePoolMap.put(key,pool);
                        LOGGER.info("Pool construction " + key);
                    }else{
                        pool = servicePoolMap.get(key);
                    }
                }
            }

            transport = (TTransport) pool.borrowObject();
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("Pool-stat: getConnection at key=" + key + ", active=" + pool.getNumActive()
                    + ",idle=" + pool.getNumIdle());
            }
            return transport;
        }catch (java.util.NoSuchElementException e){
            String msg = "Client pool exhausted and cannot or will not return another instance : " + key +
                 ",active=" + pool.getNumActive() + ",idle=" + pool.getNumIdle();
            throw new TcoaException(msg,e);
        }catch (java.lang.IllegalStateException e){
            throw new TcoaException("Client pool you caled has been closed.",e);
        }catch (Exception e){
            throw new TcoaException("Client pool other exception at " + key + "," + e.getMessage(),e );
        }
    }

    /**
     * Return a connection
     * @param tcoaTransport
     * @throws TooManyListenersException
     */
    @Override
    public void returnConnection(TcoaTransport tcoaTransport) throws TcoaException {
        String key = tcoaTransport.getServiceNode().getNodeKey();
        ObjectPool pool = null;

        try{
            pool = servicePoolMap.get(key);
            if(pool != null){
                if(tcoaTransport.getTransport() != null){
                    pool.returnObject(tcoaTransport.getTransport());
                }
            }else{
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("Pool-stat: returnConnection " + tcoaTransport.getTransport() +
                            ", pool key : " + tcoaTransport.getServiceNode().getNodeKey() + ", pool not exist.");
                    LOGGER.debug("servercPoolMap : " + servicePoolMap);
                }
            }
        }catch (Exception e){
            throw new TcoaException("Return connction error at key=" + key,e);
        }
    }


    /**
     * Invalidate connection
     * @param tcoaTransport
     */
    @Override
    public void invalidateConnection(TcoaTransport tcoaTransport){
        String key = tcoaTransport.getServiceNode().getNodeKey();
        TTransport transport = tcoaTransport.getTransport();
        ObjectPool pool = null;

        try{
            pool = servicePoolMap.get(key);
            if(pool != null){
                if(transport != null){
                    pool.invalidateObject(transport);
                }
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("Pool-stat: invalidate " + transport + ",active=" + pool.getNumActive()
                        + ",idle=" + pool.getNumIdle());
                }
            }
        }catch (Exception e){
            LOGGER.warn("InvalidateConnection error.",e);
        }
    }

    @Override
    public void clearConnections(Node node){
        String key = node.getNodeKey();
        ObjectPool pool = null;

        try{
            pool = servicePoolMap.get(key);
            if(pool != null){
                pool.clear();
            }
        }catch (Exception e){
            LOGGER.warn("ClearConnection error at key=" + key,e);
        }
        LOGGER.info("Pool-stat: pool destruction at key=" + key);
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdel() {
        return testWhileIdel;
    }

    public void setTestWhileIdel(boolean testWhileIdel) {
        this.testWhileIdel = testWhileIdel;
    }
}
