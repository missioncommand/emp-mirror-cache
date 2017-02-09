package org.cmapi.mirrorcache.client.utils;

import org.cmapi.mirrorcache.MirrorCacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Publisher<T> implements Runnable {
    static final private Logger LOG = LoggerFactory.getLogger(Publisher.class);
    
    private MirrorCacheException exception;
    
    final private String threadName;
    final private int numIter;
    
    public Publisher(String threadName, int numIter) {
        this.threadName = threadName;
        this.numIter    = numIter;
    }
    
    abstract public void publish(T payload) throws MirrorCacheException;
    abstract public void log(String msg);
    abstract public T constructPayload(String name);
    abstract public void statusUpdate(int iter);
    abstract public void finished();
    
    protected boolean hasException() {
        return exception != null;
    }
    protected MirrorCacheException getException() {
        return exception;
    }
    
    @Override
    public void run() {
        Thread.currentThread().setName(threadName + "Thread");
        LOG.trace("starting..");
        
        try {
            log("\nSending " + numIter + " symbols..");
    
            final long startSend = System.currentTimeMillis();
            
            for (int i = 0; i < numIter; i++) {
                if (i % 1000 == 0) { // update every 1000
                    final int iNow = i;
                    statusUpdate(iNow);
                }
                
                final T payload = constructPayload("Unit " + i);
                publish(payload);
            }
            final long endSend = System.currentTimeMillis();
            log("\tTotal send time: " + (endSend - startSend) + "ms");
            
        } catch (MirrorCacheException e) {
            this.exception = e;
            
        } finally {
            LOG.trace("finishing..");
            finished();
        }
    }
}
