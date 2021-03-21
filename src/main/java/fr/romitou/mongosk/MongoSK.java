package fr.romitou.mongosk;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.mongodb.reactivestreams.client.MongoClient;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MongoSK extends JavaPlugin {

    private static final List<MongoSKServer> mongoSKServers = new ArrayList<>();
    private static FileConfiguration configuration;

    public static FileConfiguration getConfiguration() {
        return configuration;
    }

    public static List<MongoSKServer> getMongoSKServers() {
        return mongoSKServers;
    }

    public static void addMongoSKServer(MongoSKServer mongoSKServer) {
        mongoSKServers.add(mongoSKServer);
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        // Load the configuration.
        this.loadConfiguration();

        // Make some safe checks to be sure Skript is installed, enabled, and ready to register this addon.
        Logger.info("Checking the availability of Skript...");
        final PluginManager pluginManager = this.getServer().getPluginManager();
        final Plugin skriptPlugin = pluginManager.getPlugin("Skript");
        if (skriptPlugin == null || !skriptPlugin.isEnabled() || !Skript.isAcceptRegistrations()) {
            Logger.severe("Skript is not installed or does not accept registrations. Disabling.",
                "Is Skript plugin present: " + (skriptPlugin != null),
                "Is Skript enabled: " + (skriptPlugin != null && skriptPlugin.isEnabled()),
                "Does Skript accept registrations: " + (skriptPlugin != null && skriptPlugin.isEnabled() && Skript.isAcceptRegistrations())
            );
            pluginManager.disablePlugin(this);
            return;
        }

        // Register the SkriptAddon and try to load classes.
        Logger.info("Registration of the MongoSK syntaxes...");
        try {
            SkriptAddon skriptAddon = Skript.registerAddon(this);
            skriptAddon.loadClasses("fr.romitou.mongosk.skript");
        } catch (IOException e) {
            Logger.severe("MongoSK could not load and register some syntax elements.",
                "Try to update your version of Skript and MongoSK, and try again only with these two plugins.",
                "If the problem persists, please open an exit on GitHub.",
                "More information about this exception: " + e.getMessage()
            );
            return;
        }

        // Register MongoSK codecs.
        if (MongoSKAdapter.ADAPTERS_ENABLED) {
            Logger.info("Loading MongoSK adapters and codecs...");
            List<String> codecs = MongoSKAdapter.loadCodecs();
            Logger.info("Loaded " + codecs.size() + " codecs!",
                "Name of the loaded codecs: " + String.join(", ", codecs),
                "If you have problems with these, do not hesitate to report them."
            );
        }

        Logger.info("MongoSK has been activated and the syntaxes has been loaded successfully in " + (System.currentTimeMillis() - start) + "ms!",
            "MongoSK version: " + this.getDescription().getVersion(),
            "Skript version: " + skriptPlugin.getDescription().getVersion(),
            "Server version: " + this.getServer().getVersion()
        );
        Logger.info("As a reminder, if you encounter problems, activate the debug mode in the MongoSK configuration to get additional useful information.");

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

        // Load and store the config.
        configuration = this.getConfig();
    }

    private void closeAllConnections() {
        getMongoSKServers()
            .stream()
            .map(MongoSKServer::getMongoClient)
            .forEach(MongoClient::close);
    }

}
