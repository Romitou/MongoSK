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
        Skript.registerExpression(ExprMongoCollection.class, MongoCollection.class, ExpressionType.SIMPLE, "collection [named] %string% in %mongodatabase%");
    }

    private Expression<String> name;
    private Expression<MongoDatabase> database;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        name = (Expression<String>) exprs[0];
        database = (Expression<MongoDatabase>) exprs[1];
        return true;
    }

    @Override
    protected MongoCollection[] get(Event e) {
        String mongoName = name.getSingle(e);
        MongoDatabase mongoDatabase = database.getSingle(e);
        if (mongoName == null || mongoDatabase == null)
            return null;
        return new MongoCollection[]{mongoDatabase.getCollection(mongoName)};
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
        return "collection " + name.toString(e, debug) + " in " + database.toString(e, debug);
    }

}
