package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.reactivestreams.client.MongoDatabase;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprMongoDatabase extends SimpleExpression<MongoSKDatabase> {

    static {
        Skript.registerExpression(
            ExprMongoDatabase.class,
            MongoSKDatabase.class,
            ExpressionType.COMBINED,
            "[the] mongo[(sk|db)] database [named] %string% (of|from) %mongoskserver%"
        );
    }

    private Expression<String> exprDatabaseName;
    private Expression<MongoSKServer> exprMongoSKServer;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprDatabaseName = (Expression<String>) exprs[0];
        exprMongoSKServer = (Expression<MongoSKServer>) exprs[1];
        return true;
    }

    @Override
    protected MongoSKDatabase[] get(@Nonnull final Event e) {
        String databaseName = exprDatabaseName.getSingle(e);
        MongoSKServer mongoSKServer = exprMongoSKServer.getSingle(e);
        if (databaseName == null || mongoSKServer == null)
            return new MongoSKDatabase[0];
        MongoDatabase mongoDatabase = mongoSKServer.getMongoClient().getDatabase(databaseName);
        if (mongoDatabase == null)
            return new MongoSKDatabase[0];
        return new MongoSKDatabase[]{new MongoSKDatabase(mongoDatabase)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @Nonnull
    public Class<? extends MongoSKDatabase> getReturnType() {
        return MongoSKDatabase.class;
    }

    @Override
    @Nonnull
    public String toString(@Nullable Event e, boolean debug) {
        return "the mongosk database named " + exprDatabaseName.toString(e, debug) + " from " + exprMongoSKServer.toString(e, debug);
    }
}
