package org.cmapi.mirrorcache;

import java.util.List;

public interface History<T> {

    String getName();
    long getStartTime();
    long getEndTime();
    
    List<Entry<T>> getEntries();
    
    static public interface Entry<T> {
        int getId();
        long getTime();
        T getValue();
    }
}
