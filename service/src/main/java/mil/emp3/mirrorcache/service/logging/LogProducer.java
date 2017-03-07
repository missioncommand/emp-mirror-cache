package mil.emp3.mirrorcache.service.logging;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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