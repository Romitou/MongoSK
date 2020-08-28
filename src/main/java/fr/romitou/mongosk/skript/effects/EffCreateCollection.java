package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.utils.MongoManager;
import org.bukkit.event.Event;

public class EffCreateCollection extends Effect {

    static {
        Skript.registerEffect(EffCreateCollection.class, "create [a] [mongo[db]] collection named %string% in database %string%");
    }

    private Expression<String> collection, database;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        collection = (Expression<String>) exprs[0];
        database = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event e) {
        if (!MongoManager.isConnected()) {
            MongoManager.queryError();
            return;
        }
        MongoManager.getClient().getDatabase(database.getSingle(e)).createCollection(collection.getSingle(e));
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "create a mongodb collection named " + collection.toString(e, debug) + " in database " + database.toString(e, debug);
    }

}
