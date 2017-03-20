package mil.emp3.mirrorcache.support;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;


public class LatchedContent<T extends Message> {
    static final private Logger LOG = LoggerFactory.getLogger(LatchedContent.class);
    
    private T content;
    
    final private CountDownLatch latch;
    
    public LatchedContent(int latchCount) {
        this.latch = new CountDownLatch(latchCount);
    }
    
    public boolean await(String id, long timeout, TimeUnit unit) throws InterruptedException {
        LOG.debug("await() : id=" + id);
        return latch.await(timeout, unit);
    }
    
    synchronized public void setContent(T content) {
        LOG.debug("setContent() : id=" + content.getId());
        
        if (this.content != null) {
            throw new IllegalStateException("this.content != null");
        }
        this.content = content;
        latch.countDown();
    }
    
    public T getContent() {
        return content;
    }
}
