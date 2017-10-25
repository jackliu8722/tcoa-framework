package com.touclick.tcoa.framework.server.core;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对生成代码Processor的封装，可以实现以下功能：
 * 1. 从输入协议中获取调用函数名称，函数内容可读数据等
 * 2. 从输入协议中获取调用者 ip:port 等信息
 * 3. TODO: 获取完整RPC协议数据
 */
public class Processor implements TProcessor {
    private static Logger logger = LoggerFactory.getLogger(Processor.class);

    /**
     * 默认的慢函数时间<br>
     *     超过 timeoutInvode 时间的函数调用会在log打印出来函数信息<br>
     *
     * TODO: 根据业务类型的不同在配置文件中传入
     */
    private int slowThreshold = 100;

    /**
     * 代码生成的实际 Processor
     */
    private TProcessor realProcessor;

    public Processor(TProcessor realProcessor) {
    	this.realProcessor = realProcessor;
    }
    public void setSlowThreshold(int slowThreshold){
        this.slowThreshold = slowThreshold;
    }
    
    @Override
    public boolean process(TProtocol in, TProtocol out) throws TException {
        StopWatch stopWatch = new Log4JStopWatch();

        ProtocolTransit tcoaProtocol = ProtocolTransit.getProtocol(in);
        stopWatch.start();
        
        boolean success = realProcessor.process(tcoaProtocol, out);

        // 获取对端信息
        String desc = in.getTransport().toString();

        StringBuffer key = new StringBuffer();
        key.append('.').append(tcoaProtocol.getFuncName());

        long elapsed = stopWatch.getElapsedTime();

        if (elapsed > slowThreshold) {
            key.append(".SLOW");
            logger.warn(desc + " called " + key + " time=" + elapsed +
                    " [" + tcoaProtocol.getReadableText() + "]");
        } else {
            if (logger.isDebugEnabled()) {
                logger.warn(desc + " called " + key + " time=" + elapsed +
                        " [" + tcoaProtocol.getReadableText() + "]");
            }
        }

        stopWatch.stop(tcoaProtocol.getFuncName());
        
        return success;
    }
}
