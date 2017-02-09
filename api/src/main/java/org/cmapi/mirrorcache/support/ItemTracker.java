package org.cmapi.mirrorcache.support;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;

/**
 * Simple tool to track throughput.
 */
public class ItemTracker {

    static public interface LogCallback {
        void logStart(String msg);
        void logEnd(String msg);
    }
    
    private int receiveCount;
    private long lastReceive;
    
    private Timer timer;
    private String uuid = "";
    
    private LogCallback log;
    
    public ItemTracker(final Logger log) {
        this(new LogCallback() {
            @Override public void logStart(String msg) {
                log.info(msg);
            }
            @Override public void logEnd(String msg) {
                log.info(msg);
            }
        });
    }
    public ItemTracker(LogCallback log) {
        this.log = log;
    }
    
    public String getId() {
        return uuid;
    }
    public void setId(String uuid) {
        this.uuid = uuid;
    }
    
    public int getReceiveCount() {
        return receiveCount;
    }
    public void setReceiveCount(int receiveCount) {
        this.receiveCount = receiveCount;
    }
    
    public void setLogCallback(LogCallback log) {
        this.log = log;
    }
    
    public void track() {
        /*
         * Assume if 3 seconds have lapsed since our previous
         * message that we are working with a new batch.
         */
        if (System.currentTimeMillis() - lastReceive > 3_000) {
            log.logStart("Receiving(" + uuid + ")...");

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                private final long startTime = System.currentTimeMillis();
                private final int startCount = receiveCount;

                @Override
                public void run() {
                    if (System.currentTimeMillis() - lastReceive > 2_000) { // are we still receiving msgs?
                        final int receivedCount = receiveCount - startCount;
                        final long receivedTimeMs = lastReceive - startTime;
                        
                        final int itemsPerSecond = Math.round((receivedCount / (receivedTimeMs / (float) 1000)));
                        log.logEnd("\tReceived (" + uuid + ") " + receivedCount + " items in " + receivedTimeMs + "ms (" + itemsPerSecond + " items/sec)");

                        cancel();
                        timer = null;
                    }
                }
            }, 0, 1_000); // check every one second to see if we are
                          // still receiving messages
        }
        lastReceive = System.currentTimeMillis();

        receiveCount++;
    }
}
