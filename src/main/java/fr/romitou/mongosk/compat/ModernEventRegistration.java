package fr.romitou.mongosk.compat;

import ch.njol.skript.lang.util.SimpleEvent;
import fr.romitou.mongosk.MongoSK;
import org.bukkit.event.Event;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValueRegistry;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.lang.converter.Converter;

/**
 * Uses the registries of Skript 2.15+. This class must only be loaded when they are available.
 */
final class ModernEventRegistration {

    private ModernEventRegistration() {
    }

    static void registerEvent(String name, Class<? extends Event> eventClass, String pattern) {
        MongoSK.getAddonInstance().syntaxRegistry().register(
            BukkitSyntaxInfos.Event.KEY,
            BukkitSyntaxInfos.Event.builder(SimpleEvent.class, name)
                .addEvent(eventClass)
                .addPattern(pattern)
                .build()
        );
    }

    static <E extends Event, T> void registerEventValue(Class<E> eventClass, Class<T> valueClass, Converter<E, T> converter) {
        MongoSK.getAddonInstance().registry(EventValueRegistry.class).register(EventValue.simple(eventClass, valueClass, converter));
    }
}
