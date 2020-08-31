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

    public static FileConfiguration getConfigFile() {
        return config;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("This plugin is in beta, it may be that some things are not going well. If you encounter any error, please report it!");

        // Register configuration.
        this.saveDefaultConfig();
        config = this.getConfig();

        // Register addon to Skript.
        PluginManager pm = Bukkit.getPluginManager();
        if ((pm.getPlugin("Skript") != null) && Skript.isAcceptRegistrations()) {
            SkriptAddon addon = Skript.registerAddon(this);
            try {
                addon.loadClasses("fr.romitou.mongosk.skript");
            } catch (IOException e) {
                Skript.error("Wait, this is anormal. Please report this error on GitHub.");
                e.printStackTrace();
            }
        } else {
            Skript.error("Skript isn't installed or doesn't accept registrations.");
            pm.disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        MongoManager.getClients().values().forEach(MongoClient::close);
    }
}
