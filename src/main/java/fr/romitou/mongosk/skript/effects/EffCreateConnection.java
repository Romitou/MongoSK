package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import fr.romitou.mongosk.utils.MongoManager;

@Name("Create Connection")
@Examples({"on script laod:",
    "\tconnect to the mongodb server \"mongodb://127.0.0.1\""})
@Since("1.0")

public class EffCreateConnection extends Effect {

    private Expression<String> connection;

    static {
        Skript.registerEffect(
                EffCreateConnection.class,
                "[create [a]] connect[ion] to [the] mongo[db] server %string%"
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        connection = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event e) {
        MongoManager.createClient(connection.getSingle(e));
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "connect to mongodb server " + connection.toString(e, debug);
    }

}
