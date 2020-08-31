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
import com.mongodb.MongoCommandException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoDatabase;
import org.bukkit.event.Event;

@Name("Create Mongo Collection")
@Description("This effect allows you to simply create a new Mongo collection in a database. The name must comply with the Mongo rules and those of your host.")
@Examples({"set {_database} to mongo database named \"mongosk\" of client named \"mongosk\"" +
        "create a new mongo collection named \"example\" in {_database}"})
@Since("1.0.0")
public class EffCreateCollection extends Effect {

    static {
        Skript.registerEffect(EffCreateCollection.class, "create [a] [new] [mongo[db]] collection [(named|with name|called)] %string% in %mongodatabase%");
    }

    private Expression<String> exprName;
    private Expression<MongoDatabase> exprDatabase;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprName = (Expression<String>) exprs[0];
        exprDatabase = (Expression<MongoDatabase>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event e) {
        String name = exprName.getSingle(e);
        MongoDatabase database = exprDatabase.getSingle(e);
        if (name == null || database == null)
            return;
        try {
            MongoNamespace.checkCollectionNameValidity(name);
            database.createCollection(name);
        } catch (IllegalArgumentException | MongoCommandException ex) {
            Skript.error("Ups! There was an error. " + ex.getMessage());
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "create a new mongo collection collection named " + exprName.toString(e, debug) + " in " + exprDatabase.toString(e, debug);
    }

}
