package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bukkit.event.Event;

public class ExprMongoCollection extends SimpleExpression<MongoCollection> {

    static {
        Skript.registerExpression(ExprMongoCollection.class, MongoCollection.class, ExpressionType.SIMPLE, "[mongo[db]] collection [(named|with name|called)] %string% in %mongodatabase%");
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
        if (name == null || database == null)
            return null;
        return new MongoCollection[]{database.getCollection(name)};
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
