package org.cmapi.mirrorcache;

public class Payload<T> {
    final private String id;
    final private String type;
    final private T data;
    
    public Payload(String id, String type, T data) {
        this.id   = id;
        this.type = type;
        this.data = data;
    }

    public String getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public T getData() {
        return data;
    }
}
