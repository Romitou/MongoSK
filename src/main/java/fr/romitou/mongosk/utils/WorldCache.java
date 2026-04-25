package fr.romitou.mongosk.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldCache implements Listener {

    private static final Map<String, WeakReference<World>> CACHE = new ConcurrentHashMap<>();

    public static World getWorld(String name) {
        if (name == null) {
            return null;
        }

        WeakReference<World> ref = CACHE.get(name);
        if (ref != null) {
            World world = ref.get();
            if (world != null) {
                return world;
            }
        }

        World world = Bukkit.getWorld(name);
        if (world != null) {
            CACHE.put(name, new WeakReference<>(world));
        } else {
            CACHE.remove(name);
        }

        return world;
    }

    public static void clear() {
        CACHE.clear();
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        CACHE.remove(event.getWorld().getName());
    }
}
