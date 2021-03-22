package fr.romitou.mongosk.listeners;

import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.elements.MongoSKServer;
import fr.romitou.mongosk.skript.events.MongoCommandFailed;
import fr.romitou.mongosk.skript.events.MongoCommandStarted;
import fr.romitou.mongosk.skript.events.MongoCommandSucceeded;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class MongoCommandListener implements CommandListener {

    private static final MongoSK MONGOSK = JavaPlugin.getPlugin(MongoSK.class);
    public MongoSKServer mongoSKServer;

    public MongoCommandListener() {
    }

    public void commandStarted(CommandStartedEvent event) {
        MONGOSK.callEvent(new MongoCommandStarted(mongoSKServer, event.getCommand(), event.getDatabaseName()));
    }

    /**
     * Listener for command completed events
     *
     * @param event the event
     */
    public void commandSucceeded(CommandSucceededEvent event) {
        MONGOSK.callEvent(new MongoCommandSucceeded(mongoSKServer, event.getResponse(), event.getElapsedTime(TimeUnit.MILLISECONDS)));
    }

    /**
     * Listener for command failure events
     *
     * @param event the event
     */
    public void commandFailed(CommandFailedEvent event) {
        MONGOSK.callEvent(new MongoCommandFailed(mongoSKServer, event.getThrowable(), event.getElapsedTime(TimeUnit.MILLISECONDS)));
    }

    public void setMongoSKServer(MongoSKServer mongoSKServer) {
        this.mongoSKServer = mongoSKServer;
    }
}

