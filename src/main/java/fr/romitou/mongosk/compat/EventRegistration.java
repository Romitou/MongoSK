package fr.romitou.mongosk.compat;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import org.bukkit.event.Event;
import org.skriptlang.skript.lang.converter.Converter;

/**
 * Registers the events and their values with the API available in the running version of Skript.
 * The registries used by the newest versions of Skript (2.15+) don't exist in the older ones, so they are only used
 * through {@link ModernEventRegistration}, which is loaded only when they are available.
 */
@SuppressWarnings("deprecation")
public final class EventRegistration {

    private static final boolean MODERN_EVENTS = classExists("org.skriptlang.skript.bukkit.lang.eventvalue.EventValueRegistry")
        && fieldExists("org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos$Event", "KEY");

    private EventRegistration() {
    }

    public static void registerEvent(String name, Class<? extends Event> eventClass, String pattern) {
        if (MODERN_EVENTS)
            ModernEventRegistration.registerEvent(name, eventClass, pattern);
        else
            Skript.registerEvent(name, SimpleEvent.class, eventClass, pattern);
    }

    public static <E extends Event, T> void registerEventValue(Class<E> eventClass, Class<T> valueClass, Converter<E, T> converter) {
        if (MODERN_EVENTS)
            ModernEventRegistration.registerEventValue(eventClass, valueClass, converter);
        else
            EventValues.registerEventValue(eventClass, valueClass, converter);
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean fieldExists(String className, String fieldName) {
        try {
            Class.forName(className).getField(fieldName);
            return true;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
