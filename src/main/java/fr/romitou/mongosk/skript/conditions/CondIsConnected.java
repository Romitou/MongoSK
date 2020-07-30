package fr.romitou.mongosk.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.utils.MongoManager;
import org.bukkit.event.Event;

public class CondIsConnected extends Condition {

    // TODO: Add the support of a negated condition.
    static {
        Skript.registerCondition(
                CondIsConnected.class,
                "mongo[db] [client] is connected [to [the] server]"
        );
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event e) {
        return MongoManager.isConnected();
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo client is connected";
    }

}
