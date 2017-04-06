package mil.emp3.mirrorcache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

import mil.emp3.mirrorcache.event.MirrorCacheEvent;


public class Message {

    final private Map<String, String> properties;
    
    private String id;
    private Priority priority;
    private Payload<?> payload;
    private OneOfCommand command;
    private MirrorCacheEvent.Type<?> eventType;
    private Throwable error;
    
    public Message() {
        this.id         = UUID.randomUUID().toString();
        this.properties = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Payload<T> getPayload(Class<T> type) {
        return (Payload<T>) payload;
    }
    @SuppressWarnings("rawtypes")
    public Payload getPayload() {
        return payload;
    }
    public Priority getPriority() {
        return priority;
    }
    public OneOfCommand getCommand() {
        return command;
    }
    
    public MirrorCacheEvent.Type<?> getEventType() {
        return eventType;
    }
    public String getProperty(String name) {
        return (String) properties.get(name);
    }
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    public String getId() {
        return id;
    }
    
    public Message setException(Throwable error) {
        this.error = error;
        return this;
    }
    public Message setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }
    public Message setCommand(OneOfCommand command) {
        this.command = command;
        return this;
    }
    public Message setPayload(Payload<?> payload) {
        this.payload = payload;
        return this;
    }
    public Message setEventType(MirrorCacheEvent.Type<?> eventType) {
        this.eventType = eventType;
        return this;
    }
    public Message setProperty(String name, String value) {
        properties.put(name, value);
        return this;
    }
    public Message setProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
        return this;
    }
    public Message setId(String id) {
        this.id = id;
        return this;
    }
    
    public boolean hasException() {
        return error != null;
    }
    public boolean hasPayload() {
        return payload != null;
    }
}
