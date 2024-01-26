package fr.romitou.mongosk;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MongoSK extends JavaPlugin {
    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        Logger.info("Checking if Skript is enabled...");
        final PluginManager pluginManager = this.getServer().getPluginManager();
        final Plugin skriptPlugin = pluginManager.getPlugin("Skript");
        if (skriptPlugin == null || !skriptPlugin.isEnabled() || !Skript.isAcceptRegistrations()) {
            Logger.severe("Skript is not enabled or not compatible with MongoSK.",
                    "Please update your version of Skript and try again."
            );
            pluginManager.disablePlugin(this);
            return;
        }

        try {
            SkriptAddon skriptAddon = Skript.registerAddon(this);
            skriptAddon.loadClasses(
                    "fr.romitou.mongosk.skript"
                    //"conditions", "effects", "events", "expressions", "sections"
            );
        } catch (IOException e) {
            Logger.severe("MongoSK could not load and register some syntax elements.",
                    "More information about this exception: " + e.getMessage()
            );
            return;
        }

        Logger.info("MongoSK has been activated and the syntaxes has been loaded successfully in " + (System.currentTimeMillis() - start) + "ms!",
                "MongoSK version: " + this.getDescription().getVersion(),
                "Skript version: " + skriptPlugin.getDescription().getVersion(),
                "Server version: " + this.getServer().getVersion()
        );

    }

    @Override
    public void onDisable() {
    }

}
