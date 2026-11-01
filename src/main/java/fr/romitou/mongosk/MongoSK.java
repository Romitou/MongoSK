package fr.romitou.mongosk;

import ch.njol.skript.Skript;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKServer;
import fr.romitou.mongosk.skript.MongoSKModule;
import fr.romitou.mongosk.utils.WorldCache;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.addon.SkriptAddon;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MongoSK extends JavaPlugin {

    private static final int BSTATS_PLUGIN_ID = 8537;
    private static final List<MongoSKServer> mongoSKServers = new CopyOnWriteArrayList<>();
    private static MongoSK instance;

    @Override
    public void onEnable() {
        instance = this;

        long start = System.currentTimeMillis();

        this.saveDefaultConfig();

        if (!Skript.isAcceptRegistrations()) {
            LoggerHelper.severe("Skript is not ready to accept registrations!",
                "MongoSK cannot work without Skript.",
                "Please check that Skript is enabled, and report this issue in our Discord server or on GitHub otherwise."
            );
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        LoggerHelper.info("Registration of the MongoSK syntaxes...");
        try {
            SkriptAddon addon = Skript.instance().registerAddon(MongoSK.class, this.getName());
            addon.loadModules(new MongoSKModule());
        } catch (LinkageError e) {
            LoggerHelper.severe("MongoSK could not load and register some syntax elements.",
                "Your version of Skript is probably not supported: please update it, and update MongoSK.",
                "If the problem persists, please open an issue on GitHub.",
                "More information about this exception: " + e.getMessage()
            );
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (MongoSKAdapter.ADAPTERS_ENABLED) {
            LoggerHelper.info("Loading MongoSK adapters and codecs...");
            MongoSKAdapter.loadCodecs();
        }

        LoggerHelper.info("MongoSK has been activated and the syntaxes has been loaded successfully in " + (System.currentTimeMillis() - start) + "ms!",
            "MongoSK version: " + this.getDescription().getVersion(),
            "Skript version: " + Skript.getVersion(),
            "Server version: " + this.getServer().getVersion()
        );
        LoggerHelper.info("If you need help, go to GitHub or to our Discord: https://discord.com/invite/6jeQkRcMkk");

        this.getServer().getPluginManager().registerEvents(new WorldCache(), this);

        // Learn more: https://bstats.org/getting-started
        Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);
        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));
        metrics.addCustomChart(new SimplePie("adapters_enabled", () -> String.valueOf(MongoSKAdapter.ADAPTERS_ENABLED)));
    }

    @Override
    public void onDisable() {
        // Writes made just before the server stops must reach MongoDB before the clients are closed.
        MongoRequest.awaitPending();
        mongoSKServers.forEach(MongoSKServer::close);
        mongoSKServers.clear();
    }

    public void callEvent(Event event) {
        if (!this.isEnabled())
            return;
        getServer().getScheduler().runTask(this, () -> getServer().getPluginManager().callEvent(event));
    }

    public static void registerServer(MongoSKServer mongoSKServer) {
        mongoSKServers.add(mongoSKServer);
    }

    public static void unregisterServer(MongoSKServer mongoSKServer) {
        mongoSKServers.remove(mongoSKServer);
    }

    @Nullable
    public static MongoSKServer getOpenServer(String connectionString) {
        for (MongoSKServer mongoSKServer : mongoSKServers) {
            if (!mongoSKServer.isClosed() && connectionString.equals(mongoSKServer.getConnectionString()))
                return mongoSKServer;
        }
        return null;
    }

    public static MongoSK getInstance() {
        return instance;
    }

}
