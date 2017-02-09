package org.cmapi.mirrorcache.service;


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
        swarm.fraction(new LoggingFraction().consoleHandler("CONSOLE", new ConsoleHandlerConsumer() {
            @Override public void accept(ConsoleHandler c) {
                c.formatter("%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n");
            }
        }).logger("org.cmapi.mirrorcache", new LoggerConsumer() {
            @Override public void accept(Logger l) {
                l.level(Level.DEBUG).handler("CONSOLE");
            }
        }).rootLogger(Level.INFO, "CONSOLE"));
        
        final JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
        deployment.staticContent();
        deployment.addAllDependencies();

        deployment.addPackage("org.cmapi.mirrorcache");
        deployment.addPackage("org.cmapi.mirrorcache.channel");
        deployment.addPackage("org.cmapi.mirrorcache.impl.channel");
        deployment.addPackage("org.cmapi.mirrorcache.support");
        
        deployment.addPackage("org.cmapi.mirrorcache.service");
        deployment.addPackage("org.cmapi.mirrorcache.service.logging");
        deployment.addPackage("org.cmapi.mirrorcache.service.cache");
        deployment.addPackage("org.cmapi.mirrorcache.service.event");
        deployment.addPackage("org.cmapi.mirrorcache.service.entity");
        deployment.addPackage("org.cmapi.mirrorcache.service.processor");
        deployment.addPackage("org.cmapi.mirrorcache.service.resources");
        deployment.addPackage("org.cmapi.mirrorcache.service.thread");
        
        deployment.addPackage("org.cmapi.primitives.proto");
        
        swarm.start().deploy(deployment);
    }
}
