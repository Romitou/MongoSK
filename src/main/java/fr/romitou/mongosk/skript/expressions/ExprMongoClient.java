package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoClient;
import fr.romitou.mongosk.skript.MongoManager;
import org.bukkit.event.Event;

public class ExprMongoClient extends SimpleExpression<MongoClient> {

    static {
        Skript.registerExpression(ExprMongoClient.class, MongoClient.class, ExpressionType.SIMPLE, "[mongo[db]] client [(named|with name|called)] %string%");
    }

    private Expression<String> exprName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprName = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected MongoClient[] get(Event e) {
        String name = exprName.getSingle(e);
        if (name == null)
            return null;
        return new MongoClient[]{MongoManager.getClient(name)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MongoClient> getReturnType() {
        return MongoClient.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo client named " + exprName.toString(e, debug);
    }

}
