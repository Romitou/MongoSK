package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoDatabase;
import fr.romitou.mongosk.MongoSK;
import org.bukkit.event.Event;

@Name("Drop Mongo Database")
@Description("This effect allows you to delete a Mongo database. This action is irreversible! Disabled by default: go to the MongoSK configuration to enable this effect.")
@Examples("drop without going back database named \"mongosk\" of client \"mongosk\"")
@Since("1.0.0")
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
        if (!MongoSK.getConfigFile().getBoolean("allow-drop.databases")) {
            Skript.error("You cannot delete a database. Go to the plugin configuration and enable the option, which was initially disabled for security reasons.");
            return;
        }
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
