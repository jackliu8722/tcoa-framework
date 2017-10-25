package com.touclick.tcoa.framework.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerThreadFactory implements ThreadFactory {

    private static final UncaughtExceptionHandler handler =
            new ServerUncaughtExceptionHandler();
    private static AtomicInteger threadNameSuffix = new AtomicInteger(1);
    private static final String threadNamePrefix = "server_threadpool_thread_";

    /**
     * 错误处理，暂时仅仅进行日志打印 TODO: 其他需要的策略
     */
    private static class ServerUncaughtExceptionHandler implements UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Logger logger =
                    LoggerFactory.getLogger(ServerUncaughtExceptionHandler.class);
            logger.error("Service thread exception/error in : " + t.getName(),
                e);
        }
    }

    public ServerThreadFactory() {
        Thread.setDefaultUncaughtExceptionHandler(null);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(threadNamePrefix + threadNameSuffix.getAndIncrement());
        thread.setUncaughtExceptionHandler(handler);
        return thread;
    }
}
