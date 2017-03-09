package mil.emp3.mirrorcache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.emp3.mirrorcache.event.EventHandler;
import mil.emp3.mirrorcache.event.EventRegistration;
import mil.emp3.mirrorcache.event.MirrorCacheEvent;

public interface MessageDispatcher {

    void init();
    void shutdown();
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    <T extends EventHandler> EventRegistration on(MirrorCacheEvent.Type<T> type, T handler);
    
    <T extends RequestProcessor<?, ?>> T getRequestProcessor(Class<T> clazz);
    
    @SuppressWarnings( "rawtypes")
    void dispatchEvent(MirrorCacheEvent event);
    void dispatchMessage(Message message) throws MirrorCacheException;
    Message awaitResponse(Message reqMessage) throws MirrorCacheException, InterruptedException;
    
    Chain getInProcessorPipeline();
    Chain getOutProcessorPipeline();
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public class Chain implements MessageProcessor<Message> {
        final private List<MessageProcessor<Message>> links;

        public Chain() {
            this.links = new ArrayList<>();
        }
        
        public void shutdown() {
            links.clear();
        }

        public Chain link(MessageProcessor<Message> link) {
            links.add(link);
            return this;
        }

        public List<MessageProcessor<Message>> links() {
            return Collections.unmodifiableList(links);
        }

        @Override
        public void processMessage(Message message) throws MirrorCacheException {
            for (MessageProcessor<Message> link : links()) {
                link.processMessage(message);
            }
        }
    }
}
