package org.cmapi.mirrorcache.impl.stage;

public enum Stage {

//    RECEIVE,
    IN_TRANSPORT,
    DESERIALIZE,
    TRANSLATE,
    SERIALIZE,
    OUT_TRANSPORT,
//    SEND,
    
//    IN_PROPERTIES, - extract headers/properties from the transport object
//    IN_COMMAND,    - anything special to do with the server-command received?
//    IN_HANDLERS,
//    IN_EVENTS,
    ;
    
}
