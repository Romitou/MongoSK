package fr.romitou.mongosk;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import fr.romitou.mongosk.elements.MongoSKClient;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MongoSK extends JavaPlugin {

    private static List<MongoSKClient> mongoSKClients;
    private FileConfiguration configuration;

    @Override
    public void onEnable() {

        // Load the configuration.
        this.loadConfiguration();

        // Make some safe checks to be sure Skript is installed, enabled, and ready to register this addon.
        final PluginManager pluginManager = this.getServer().getPluginManager();
        final Plugin skriptPlugin = pluginManager.getPlugin("Skript");
        if (skriptPlugin == null || !skriptPlugin.isEnabled() || !Skript.isAcceptRegistrations()) {
            Logger.severe("Skript is not installed or does not accept registrations. Disabling.",
                    "Is Skript plugin present: " + (skriptPlugin != null),
                    "Is Skript enabled: " + (skriptPlugin != null && skriptPlugin.isEnabled()),
                    "Is Skript accept registrations: " + (skriptPlugin != null && skriptPlugin.isEnabled() && Skript.isAcceptRegistrations())
            );
            pluginManager.disablePlugin(this);
            return;
        }

        // Register the SkriptAddon and try to load classes.
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
        Logger.info("MongoSK has been activated and the syntax has been loaded successfully!",
                "MongoSK version: " + this.getDescription().getVersion(),
                "Skript version: " + skriptPlugin.getDescription().getVersion(),
                "Server version: " + this.getServer().getVersion()
        );

        // Register Metrics.
        // Learn more: https://bstats.org/getting-started
        this.registerMetrics();
    }

    private void registerMetrics() {

        Metrics metrics = new Metrics(this, 8537);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));
    }

    private void loadConfiguration() {
        // Save the default config if not existent
        this.saveDefaultConfig();

        // Load and store the config.
        this.configuration = this.getConfig();
    }

    public FileConfiguration getConfiguration() {
        return this.configuration;
    }

    public static List<MongoSKClient> getMongoSKClients() {
        return mongoSKClients;
    }

    public static void addMongoSKClient(MongoSKClient mongoSKClient) {
        mongoSKClients.add(mongoSKClient);
    }

    public static Optional<MongoSKClient> getMongoSKClient(String clientName) {
        return mongoSKClients.stream().filter(client -> client.getClientName().equals(clientName)).findFirst();
    }

}
