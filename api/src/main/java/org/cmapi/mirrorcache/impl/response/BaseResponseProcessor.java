package org.cmapi.mirrorcache.impl.response;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseResponseProcessor implements ResponseProcessor, Runnable {
    static final private Logger LOG = LoggerFactory.getLogger(BaseResponseProcessor.class);
    
    final private MessageDispatcher dispatcher;
    final private String threadName;
    final private BlockingQueue<Message> inbox;
    
    private ExecutorService executor;
    
    public BaseResponseProcessor(MessageDispatcher dispatcher, String threadName, int queueSize) {
        this.dispatcher = dispatcher;
        this.threadName = threadName;
        this.inbox      = new ArrayBlockingQueue<>(5);
        this.executor   = Executors.newSingleThreadExecutor();
    }
    
    abstract protected void onMessage(Message message) throws MirrorCacheException;
    
    protected MessageDispatcher getMessageDispatcher() {
        return dispatcher;
    }
    
    @Override
    public void init() {
        if (executor.isTerminated()) {
            executor = Executors.newSingleThreadExecutor();
        }
        executor.execute(this);
    }
    
    @Override
    public void shutdown() {
        LOG.debug("shutdown()");
        
        /*
         * Shutdown executor service.
         */
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                if (executor.shutdownNow().size() > 0) {
                    throw new IllegalStateException("There should not be any pending tasks.");
                }
                
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    LOG.error("executor did not shutdown.");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            LOG.warn("Thread was interrupted.");
        }
    }
    
    @Override
    public void run() {
        Thread.currentThread().setName(threadName);
        LOG.info("starting.. ");
        
        try {
            while (true) {
                final Message message = inbox.take();
                onMessage(message);
            }
            
        } catch (MirrorCacheException e) {
            LOG.error(e.getMessage(), e);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Thread was interrupted.");
        }
    }
    
    @Override
    public void processMessage(Message message) {
        if (message == null) {
            throw new IllegalStateException("message == null");
        }
        
        try {
            if (!inbox.offer(message, 1, TimeUnit.SECONDS)) {
                LOG.info(Reason.QUEUE_OFFER_TIMEOUT.getMsg() + ", command: " + message.getCommand());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Thread was interrupted.");
        }
    }
    
}
