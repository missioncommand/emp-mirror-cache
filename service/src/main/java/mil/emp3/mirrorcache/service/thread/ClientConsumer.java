package mil.emp3.mirrorcache.service.thread;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.service.SessionManager;
import mil.emp3.mirrorcache.support.ProtoMessageEntry;

public class ClientConsumer implements Runnable {
    static final Logger LOG = LoggerFactory.getLogger(ClientConsumer.class);
    
    final private String sessionId;
    final private BlockingQueue<ProtoMessageEntry> queue;
    final private SessionManager sessionManager;

    public ClientConsumer(String sessionId, BlockingQueue<ProtoMessageEntry> queue, SessionManager sessionManager) {
        this.sessionId      = sessionId;
        this.queue          = queue;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        final Thread currentThread = Thread.currentThread();
        currentThread.setName("ClientConsumer-" + sessionId);
        LOG.info("starting..");

        try {
            while (true) {
                final ProtoMessageEntry message = queue.take();
//Thread.sleep(2000); // simulates delayed response to clients
                sessionManager.getAsync(sessionId).sendObject(message.getEntry());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Thread was interrupted.");
        }
        
        LOG.info("finishing..");
    }
}
