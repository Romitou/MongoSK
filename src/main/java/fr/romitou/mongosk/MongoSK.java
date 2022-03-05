package fr.romitou.mongosk;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.lang.parser.ParserInstance;
import com.mongodb.reactivestreams.client.MongoClient;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKServer;
import fr.romitou.mongosk.skript.SkriptTypes;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MongoSK extends JavaPlugin {

    private static final List<MongoSKServer> mongoSKServers = new ArrayList<>();
    private static MongoSK instance;

    @Override
    public void onEnable() {
        instance = this;

        long start = System.currentTimeMillis();

        // Load the configuration.
        this.loadConfiguration();

        // Make some safe checks to be sure Skript is installed, enabled, and ready to register this addon.
        LoggerHelper.info("Checking the availability of Skript...");
        final PluginManager pluginManager = this.getServer().getPluginManager();
        final Plugin skriptPlugin = pluginManager.getPlugin("Skript");
        if (skriptPlugin == null || !skriptPlugin.isEnabled() || !Skript.isAcceptRegistrations()) {
            LoggerHelper.severe("Skript is not installed or does not accept registrations. Disabling.",
                "Is Skript plugin present: " + (skriptPlugin != null),
                "Is Skript enabled: " + (skriptPlugin != null && skriptPlugin.isEnabled()),
                "Does Skript accept registrations: " + (skriptPlugin != null && skriptPlugin.isEnabled() && Skript.isAcceptRegistrations())
            );
            pluginManager.disablePlugin(this);
            return;
        }

        // Register the SkriptAddon and try to load classes.
        LoggerHelper.info("Registration of the MongoSK syntaxes...");
        try {
            SkriptAddon skriptAddon = Skript.registerAddon(this);
            skriptAddon.loadClasses(
                "fr.romitou.mongosk.skript",
                "conditions", "effects", "events", "expressions", (isUsingNewSections() ? "sections" : "legacySections")
            );
            new SkriptTypes();
        } catch (IOException e) {
            LoggerHelper.severe("MongoSK could not load and register some syntax elements.",
                "Try to update your version of Skript and MongoSK, and try again only with these two plugins.",
                "If the problem persists, please open an issue on GitHub.",
                "More information about this exception: " + e.getMessage()
            );
            return;
        }

        // Register MongoSK codecs.
        if (MongoSKAdapter.ADAPTERS_ENABLED) {
            LoggerHelper.info("Loading MongoSK adapters and codecs...");
            MongoSKAdapter.loadCodecs();
        }

        LoggerHelper.info("MongoSK has been activated and the syntaxes has been loaded successfully in " + (System.currentTimeMillis() - start) + "ms!",
            "MongoSK version: " + this.getDescription().getVersion(),
            "Skript version: " + skriptPlugin.getDescription().getVersion(),
            "Server version: " + this.getServer().getVersion()
        );
        LoggerHelper.info("If you need help, go to GitHub or to our Discord: https://discord.com/invite/6jeQkRcMkk");

        // Register Metrics.
        // Learn more: https://bstats.org/getting-started
        this.registerMetrics();
    }

    @Override
    public void onDisable() {
        this.closeAllConnections();
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, 8537);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));
        metrics.addCustomChart(new Metrics.SimplePie("adapters_enabled", MongoSKAdapter.ADAPTERS_ENABLED::toString));
    }

    private void loadConfiguration() {
        // Save the default config if not existent
        this.saveDefaultConfig();
    }

    private void closeAllConnections() {
        mongoSKServers
            .stream()
            .map(MongoSKServer::getMongoClient)
            .forEach(MongoClient::close);
    }

    public void callEvent(Event event) {
        if (!this.isEnabled())
            return;
        getServer().getScheduler().runTask(this, () -> getServer().getPluginManager().callEvent(event));
    }

    public static void registerServer(MongoSKServer mongoSKServer) {
        mongoSKServers.add(mongoSKServer);
    }

    public static @NotNull Boolean isUsingNewParser() {
        try {
            ParserInstance.class.getDeclaredMethod("get");
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    public static @NotNull Boolean isUsingNewSections() {
        try {
            Class.forName("ch.njol.skript.lang.Section");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static MongoSK getInstance() {
        return instance;
    }

}
