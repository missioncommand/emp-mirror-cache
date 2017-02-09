package org.cmapi.mirrorcache.service.logging;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@Singleton
public class LogProducer {

    private Map<Class<?>, Logger> loggers = new HashMap<>();

    @Produces
    public Logger getProducer(InjectionPoint ip) {
        final Class<?> key = getKeyFromIp(ip);
        if (!loggers.containsKey(key)) {
            loggers.put(key, LoggerFactory.getLogger(key));
        }
        return loggers.get(key);
    }

    private Class<?> getKeyFromIp(InjectionPoint ip) {
        return ip.getMember().getDeclaringClass();
    }
}