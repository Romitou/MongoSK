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
import com.mongodb.reactivestreams.client.MongoDatabase;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("Mongo database")
@Description("Retrieve a specific database from a Mongo server. " +
    "You must specify in which Mongo server your collection is located.")
@Examples({"set {mydatabase} to mongo database named \"exampleDatabase\" from {myserver}",
    "broadcast \"Yay! I retrieved my %{mydatabase}'s mongo name% database!\""})
@Since("2.0.0")
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
        return "mongosk database named " + exprDatabaseName.toString(e, debug) + " from " + exprMongoSKServer.toString(e, debug);
    }
}
