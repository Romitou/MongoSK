package fr.romitou.mongosk;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.mongodb.client.MongoClient;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoSK extends JavaPlugin {

    private static SkriptAddon addon;
    private static MongoSK instance;

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = Bukkit.getPluginManager();

        if ((pm.getPlugin("Skript") != null) && Skript.isAcceptRegistrations()) {
            addon = Skript.registerAddon(this);
            try {
                addon.loadClasses("fr.romitou.mongosk.skript");
                MongoSetup();
                Skript.info("We have a important message ; MongoSK is successfully loaded.");
            } catch (IOException e) {
                Skript.error("Wait, this is anormal. Please report this error on GitHub.");
                e.printStackTrace();
            }
        } else {
            Skript.error("Uh oh. There was an error when enabling MongoSK : Skript is not installed or doesn't accept registrations.");
            pm.disablePlugin(this);
        }
    }

    private void MongoSetup() {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
    }

    public static SkriptAddon getAddon() {
        return addon;
    }

    public static MongoSK getInstance() {
        return instance;
    }

}
