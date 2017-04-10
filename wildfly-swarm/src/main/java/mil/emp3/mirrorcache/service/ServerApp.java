package mil.emp3.mirrorcache.service;


import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.config.logging.ConsoleHandler;
import org.wildfly.swarm.config.logging.ConsoleHandlerConsumer;
import org.wildfly.swarm.config.logging.Level;
import org.wildfly.swarm.config.logging.Logger;
import org.wildfly.swarm.config.logging.LoggerConsumer;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.logging.LoggingFraction;

public class ServerApp {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {
        System.out.println("here we go\n\n");
        
        final Swarm swarm = new Swarm();
        swarm.fraction(new LoggingFraction().defaultFormatter()
                                            .consoleHandler("CONSOLE", new ConsoleHandlerConsumer() {
                                                @Override public void accept(ConsoleHandler c) {
                                                    c.formatter("%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n");
                                                }
                                            })
                                            .logger("mil.emp3.mirrorcache", new LoggerConsumer() {
                                                @Override public void accept(Logger l) {
                                                    l.level(Level.DEBUG);
                                                }
                                            })
                                            .rootLogger(Level.INFO, "CONSOLE"));
 
        
        final JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
        deployment.staticContent();
        deployment.addAllDependencies();

        deployment.addPackage("mil.emp3.mirrorcache");
        deployment.addPackage("mil.emp3.mirrorcache.channel");
        deployment.addPackage("mil.emp3.mirrorcache.impl.channel");
        deployment.addPackage("mil.emp3.mirrorcache.support");
        
        deployment.addPackage("mil.emp3.mirrorcache.service");
        deployment.addPackage("mil.emp3.mirrorcache.service.logging");
        deployment.addPackage("mil.emp3.mirrorcache.service.cache");
        deployment.addPackage("mil.emp3.mirrorcache.service.event");
        deployment.addPackage("mil.emp3.mirrorcache.service.entity");
        deployment.addPackage("mil.emp3.mirrorcache.service.processor");
        deployment.addPackage("mil.emp3.mirrorcache.service.resources");
        deployment.addPackage("mil.emp3.mirrorcache.service.thread");
        
        deployment.addPackage("org.cmapi.primitives.proto");
        
        swarm.start().deploy(deployment);
    }
}
