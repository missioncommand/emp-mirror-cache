package mil.emp3.mirrorcache.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.cmapi.primitives.proto.CmapiProto.QueueInfo;
import org.slf4j.Logger;

import mil.emp3.mirrorcache.service.entity.SessionInfo;
import mil.emp3.mirrorcache.service.event.ConnectEvent;
import mil.emp3.mirrorcache.service.event.DisconnectEvent;
import mil.emp3.mirrorcache.service.thread.ClientConsumer;
import mil.emp3.mirrorcache.support.ProtoMessageEntry;

@ApplicationScoped
public class SessionManager {
    
    @Inject
    private Logger LOG;
    
    final private Map<String, Session> sessionMap; // map<sessionId, session>
    final private Map<String, Async> asyncMap;     // map<sessionId, async>
    
    /** Outbound queue for each session. */
    final private Map<String, BlockingQueue<ProtoMessageEntry>> outboundQueueMap;
    
    /** Retain handle to the dedicated thread for each session. This allows us to cancel it. */ 
    final private Map<String, Future<?>> outboundThreadMap;
    
    /** Each session is assigned a dedicated thread for sending outbound messages from a bounded queue. */
    final private ExecutorService outboundPool;
    final private BlockingQueue<Runnable> outboundThreadQueue;
    
    static final private int MAX_NUM_SESSIONS = 10; // only support specific number of sessions
    
    public SessionManager() {
        this.sessionMap          = new HashMap<>();
        this.asyncMap            = new HashMap<>();
        this.outboundQueueMap    = new HashMap<>();
        this.outboundThreadMap   = new HashMap<>();
        
//        this.outboundPool        = Executors.newFixedThreadPool(10);
        this.outboundThreadQueue = new ArrayBlockingQueue<>(MAX_NUM_SESSIONS);
        this.outboundPool        = new ThreadPoolExecutor(MAX_NUM_SESSIONS, MAX_NUM_SESSIONS, 0L, TimeUnit.MILLISECONDS, outboundThreadQueue);
    }
    
    @PreDestroy
    public void shutdown() {
        /*
         * Shutdown executor service.
         */
        outboundPool.shutdown();
        try {
            if (!outboundPool.awaitTermination(3, TimeUnit.SECONDS)) {
                if (outboundPool.shutdownNow().size() > 0) {
                    throw new IllegalStateException("There should not be any pending tasks.");
                }
                
                if (!outboundPool.awaitTermination(3, TimeUnit.SECONDS)) {
                    LOG.error("outboundPool did not shutdown.");
                }
            }
        } catch (InterruptedException e) {
            outboundPool.shutdownNow();
            Thread.currentThread().interrupt();
            LOG.warn(Thread.currentThread().getName() + " thread was interrupted.");
        }
    }
    
    public void onConnect(@Observes ConnectEvent event) {
        sessionMap.put(event.getSessionId(), event.getSession());
        
        /*
         * Store a reference to async 
         */
        final Async async = event.getSession().getAsyncRemote();
        if (null != asyncMap.put(event.getSessionId(), async)) {
            throw new IllegalStateException("asyncMap entry already exists for sessionId: " + event.getSessionId());
        }
        
        /*
         * Create outbound queue for this session.
         */
        final BlockingQueue<ProtoMessageEntry> queue = new PriorityBlockingQueue<>();
        
        if (null != outboundQueueMap.put(event.getSessionId(), queue)) {
            throw new IllegalStateException("outboundQueueMap entry already exists for sessionId: " + event.getSessionId());
        }
        
        /*
         * Create outbound consumer thread for this session.
         */
        final Future<?> handle = outboundPool.submit(new ClientConsumer(event.getSessionId(), queue, this));
        if (null != outboundThreadMap.put(event.getSessionId(), handle)) {
            throw new IllegalStateException("outboundThreadMap entry already exists for sessionId: " + event.getSessionId());
        }
    }
    
    public void onDisconnect(@Observes DisconnectEvent event) {
        sessionMap.remove(event.getSessionId());
        asyncMap.remove(event.getSessionId());
        outboundQueueMap.remove(event.getSessionId());
        
        // shutdown clientConsumerThread
        final Future<?> handle = outboundThreadMap.get(event.getSessionId());
        if (!handle.cancel(true)) {
            try {
                if (null != handle.get(3, TimeUnit.SECONDS)) {
                    LOG.error("clientConsumerThread failed to complete: " + event.getSessionId());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.warn(Thread.currentThread().getName() + " thread was interrupted.");
                
            } catch (TimeoutException | ExecutionException e) {
                LOG.error("Unable to cancel clientConsumerThread: " + event.getSessionId(), e);
            }
        }
        
        outboundThreadMap.remove(event.getSessionId());
    }
    
    public Async getAsync(String sessionId) { //this method is an optimization
        final Async async = asyncMap.get(sessionId);
        return async;
    }
    public BlockingQueue<ProtoMessageEntry> getOutboundQueue(String sessionId) {
        final BlockingQueue<ProtoMessageEntry> queue = outboundQueueMap.get(sessionId);
        return queue;
    }
    
    public List<SessionInfo> status() {
        final List<SessionInfo> status = new ArrayList<>();
        
        for (Iterator<Session> iter = sessionMap.values().iterator(); iter.hasNext(); ) {
            final Session session = iter.next();

            status.add(new SessionInfo.Builder()
                    .setSessionId(session.getId())
                    .setAgent(session.getRequestParameterMap().get("agent").get(0))
                    .setOutboundQueueSize(outboundQueueMap.get(session.getId()).size())
                .build());
        }
        
        return status;
    }
    
    public List<QueueInfo> statusQueues() {
        final List<QueueInfo> status = new ArrayList<>();
        
        for (Entry<String, BlockingQueue<ProtoMessageEntry>> entry : outboundQueueMap.entrySet()) {
            final QueueInfo.Builder builder = QueueInfo.newBuilder().setQueueName(entry.getKey());
            
            final ProtoMessageEntry[] sorted = entry.getValue().toArray(new ProtoMessageEntry[0]);
            Arrays.sort(sorted);
            
            for (ProtoMessageEntry protoMessageEntry : sorted) {
                builder.addEntry(new StringBuilder()
                                        .append("seqNum-").append(protoMessageEntry.getSeqNum())
                                        .append(", priority-").append(protoMessageEntry.getEntry().getPriority())
                                        .append(", command-").append(protoMessageEntry.getEntry().getCommand().getCommandCase())
                                    .toString());
            }
            
            status.add(builder.build());
        }
        
        return status;
    }
}
