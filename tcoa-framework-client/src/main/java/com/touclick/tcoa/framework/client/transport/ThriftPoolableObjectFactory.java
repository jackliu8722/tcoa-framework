package com.touclick.tcoa.framework.client.transport;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PoolableObjectFactory implement
 *
 * @author bing.liu
 * @date 2015-08-05
 * @version 1.0
 */
public class ThriftPoolableObjectFactory implements PoolableObjectFactory{

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThriftPoolableObjectFactory.class);

    /** service ip*/
    private String serviceIp;

    /** service port */
    private int servicePort;

    /** time out*/
    private int timeout;

    public ThriftPoolableObjectFactory(String serviceIp,int servicePort,int timeout){
        this.serviceIp = serviceIp;
        this.servicePort = servicePort;
        this.timeout = timeout;
    }

    /**
     * Destroy connection
     *
     * @param arg0
     * @throws Exception
     */
    @Override
    public void destroyObject(Object arg0) throws Exception{
        if(arg0 instanceof TTransport){
            TTransport transport = (TTransport) arg0;
            if(transport.isOpen()){
                transport.close();
            }
        }
    }

    /**
     * Make a connection
     * @return
     * @throws Exception
     */
    @Override
    public Object makeObject() throws Exception{
        try{
            TSocket socket = new TSocket(this.serviceIp,this.servicePort);
            socket.getSocket().setKeepAlive(true);
            socket.getSocket().setTcpNoDelay(true);
            socket.getSocket().setSoLinger(false, 0);
            socket.getSocket().setSoTimeout(this.timeout);

            TTransport transport = new TFastFramedTransport(socket);

            transport.open();

            if(LOGGER.isDebugEnabled()){
                LOGGER.debug(String.format("Pool make object success at Service [%s:%d].",
                        this.serviceIp, this.servicePort));
            }
            return transport;
        }catch (Exception e){
            LOGGER.warn(String.format("Pool make object error at Service [%s:%d].",
                    this.serviceIp, this.servicePort));
            throw new RuntimeException(e);
        }
    }

    /**
     * Validate the connection
     * @param arg0
     * @return
     */
    @Override
    public boolean validateObject(Object arg0){
        try{
            if(arg0 instanceof TTransport){
                TTransport transport = (TTransport) arg0;

                if(transport.isOpen()){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void passivateObject(Object arg0) throws Exception{
        //Do nothing
    }

    @Override
    public void activateObject(Object arg0) throws Exception {
        // Do nothing
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
