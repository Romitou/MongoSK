package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bukkit.event.Event;

@Name("Mongo Collection")
@Description("This expression allows you to retrieve a Mongo collection from a specific database.")
@Examples({"set {_client} to client named \"default\"",
        "set {_database} to database named \"mongosk\" with {_client}",
        "set {_collection} to collection named \"example\" from {_database}"})
@Since("1.0.0")
public class ExprCollection extends SimpleExpression<MongoCollection> {

    static {
        Skript.registerExpression(ExprCollection.class, MongoCollection.class, ExpressionType.SIMPLE, "[mongo[db]] collection [(named|with name|called)] %string% (in|of) %mongodatabase%");
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
    protected MongoCollection[] get(Event e) {
        String name = exprName.getSingle(e);
        MongoDatabase database = exprDatabase.getSingle(e);
        try {
            return (name == null || database == null)
                    ? new MongoCollection[0]
                    : new MongoCollection[]{database.getCollection(name)};
        } catch (IllegalArgumentException ex) {
            return new MongoCollection[0]; // A MongoDB collection with that name doesn't exist!
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MongoCollection> getReturnType() {
        return MongoCollection.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo collection " + exprName.toString(e, debug) + " in " + exprDatabase.toString(e, debug);
    }

}
