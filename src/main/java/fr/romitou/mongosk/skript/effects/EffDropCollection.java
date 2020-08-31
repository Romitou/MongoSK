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
import com.mongodb.client.MongoCollection;
import fr.romitou.mongosk.MongoSK;
import org.bukkit.event.Event;

@Name("Drop Mongo Collection")
@Description("This effect allows you to delete a Mongo collection. This action is irreversible! Disabled by default: go to the MongoSK configuration to enable this effect.")
@Examples({"set {_collection} to collection named \"example\" in database \"mongosk\" of client \"mongosk\"" +
        "drop without going back {_collection}"})
@Since("1.0.0")
public class EffDropCollection extends Effect {

    static {
        Skript.registerEffect(EffDropCollection.class, "[mongo[db]] (drop|delete) without going back [the] %mongocollection%");
    }

    private Expression<MongoCollection> exprCollection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprCollection = (Expression<MongoCollection>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event e) {
        if (!MongoSK.getConfigFile().getBoolean("allow-drop.collections")) {
            Skript.error("You cannot delete a collection. Go to the plugin configuration and enable the option, which was initially disabled for security reasons.");
            return;
        }
        MongoCollection collection = exprCollection.getSingle(e);
        if (collection == null)
            return;
        collection.drop();
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "drop without going back " + exprCollection.toString(e, debug);
    }

}
