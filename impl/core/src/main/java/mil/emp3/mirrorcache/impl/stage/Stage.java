package mil.emp3.mirrorcache.impl.stage;

public enum Stage {

//    RECEIVE,
    IN_TRANSPORT,
    DESERIALIZE,
    TRANSLATE,
    SERIALIZE,
    OUT_TRANSPORT,
//    SEND,
    
//    IN_PROPERTIES, - extract headers/properties from the transport object
//    IN_HANDLERS, - individual handlers for specific operations
//    IN_EVENTS,
    ;
    
}
