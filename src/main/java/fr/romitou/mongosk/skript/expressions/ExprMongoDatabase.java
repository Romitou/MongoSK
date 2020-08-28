package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoDatabase;
import fr.romitou.mongosk.utils.MongoManager;
import org.bukkit.event.Event;

public class ExprMongoDatabase extends SimpleExpression<MongoDatabase> {

    static {
        Skript.registerExpression(ExprMongoDatabase.class, MongoDatabase.class, ExpressionType.SIMPLE, "database [named] %string%");
    }

    private Expression<String> name;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        name = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected MongoDatabase[] get(Event e) {
        String databaseName = name.getSingle(e);
        if (databaseName == null)
            return null;
        return new MongoDatabase[]{MongoManager.getClient().getDatabase(databaseName)};
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
        return "database named " + name.toString(e, debug);
    }

}
