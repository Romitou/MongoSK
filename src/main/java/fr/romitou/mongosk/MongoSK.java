package fr.romitou.mongosk;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MongoSK extends JavaPlugin {

    private static SkriptAddon addon;
    private static MongoSK instance;

    public static SkriptAddon getAddon() {
        return addon;
    }

    public static MongoSK getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = Bukkit.getPluginManager();

        if ((pm.getPlugin("Skript") != null) && Skript.isAcceptRegistrations()) {
            addon = Skript.registerAddon(this);
            try {
                addon.loadClasses("fr.romitou.mongosk.skript");
            } catch (IOException e) {
                Skript.error("Wait, this is anormal. Please report this error on GitHub.");
                e.printStackTrace();
            }
        } else {
            Skript.error("Skript is not installed or doesn't accept registrations.");
            pm.disablePlugin(this);
        }
    }

}
