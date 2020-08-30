package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoDatabase;
import org.bukkit.event.Event;

public class EffDropDatabase extends Effect {

    static {
        Skript.registerEffect(EffDropDatabase.class, "[mongo[db]] (drop|delete) without going back [the] %mongodatabase%");
    }

    private Expression<MongoDatabase> exprDatabase;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprDatabase = (Expression<MongoDatabase>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event e) {
        MongoDatabase database = exprDatabase.getSingle(e);
        if (database == null)
            return;
        database.drop();
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "drop without going back " + exprDatabase.toString(e, debug);
    }

}
