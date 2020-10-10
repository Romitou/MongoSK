package fr.romitou.mongosk;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.mongodb.client.MongoClient;
import fr.romitou.mongosk.skript.MongoManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MongoSK extends JavaPlugin {

    private static FileConfiguration config;
    private static PluginManager pluginManager;

    public static FileConfiguration getConfigFile() {
        return config;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public void onEnable() {

        // Register add-on.
        pluginManager = Bukkit.getPluginManager();
        if ((pluginManager.getPlugin("Skript") != null) && Skript.isAcceptRegistrations()) {
            SkriptAddon addon = Skript.registerAddon(this);
            try {
                addon.loadClasses("fr.romitou.mongosk.skript");
            } catch (IOException e) {
                Skript.error("Wait, this is anormal. Please report this error on GitHub.");
                e.printStackTrace();
            }
        } else {
            Skript.error("Skript isn't installed or doesn't accept registrations.");
            pluginManager.disablePlugin(this);
        }

        // Load or create configuration.
        this.saveDefaultConfig();
        config = this.getConfig();

        // Register Metrics (<3).
        Metrics metrics = new Metrics(this, 8537);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));
    }

    @Override
    public void onDisable() {
        MongoManager.getClients().values().forEach(MongoClient::close);
    }
}
