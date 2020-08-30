package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bukkit.event.Event;

public class ExprDatabase extends SimpleExpression<MongoDatabase> {

    static {
        Skript.registerExpression(ExprDatabase.class, MongoDatabase.class, ExpressionType.SIMPLE, "[mongo[db]] database [(named|with name|called)] %string% (of|with) %mongoclient%");
    }

    private Expression<String> exprName;
    private Expression<MongoClient> exprClient;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprName = (Expression<String>) exprs[0];
        exprClient = (Expression<MongoClient>) exprs[1];
        return true;
    }

    @Override
    protected MongoDatabase[] get(Event e) {
        String name = exprName.getSingle(e);
        MongoClient client = exprClient.getSingle(e);
        if (name == null || client == null)
            return null;
        return new MongoDatabase[]{client.getDatabase(name)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MongoDatabase> getReturnType() {
        return MongoDatabase.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo database named " + exprName.toString(e, debug) + " with " + exprClient.toString();
    }

}
