package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.reactivestreams.client.MongoDatabase;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Mongo database")
@Description({
    "Retrieve a specific database from a Mongo server connection.",
    "You must specify the Mongo server instance from which to retrieve the database."
})
@Examples({
    "set {mydatabase} to mongo database named \"exampleDatabase\" from {myserver}",
    "broadcast \"Yay! I retrieved my %{mydatabase}'s mongo name% database!\""
})
@Since("2.0.0")
public class ExprMongoDatabase extends SimpleExpression<MongoSKDatabase> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprMongoDatabase.class, MongoSKDatabase.class)
            .addPatterns("[the] mongo[(sk|db)] database [named] %string% (of|from) %mongoskserver%")
            .priority(SyntaxInfo.SIMPLE)
            .build());
    }

    private Expression<String> exprDatabaseName;
    private Expression<MongoSKServer> exprMongoSKServer;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprDatabaseName = (Expression<String>) exprs[0];
        exprMongoSKServer = (Expression<MongoSKServer>) exprs[1];
        return true;
    }

    @Override
    protected MongoSKDatabase[] get(@NotNull final Event e) {
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
    @NotNull
    public Class<? extends MongoSKDatabase> getReturnType() {
        return MongoSKDatabase.class;
    }

    @Override
    @NotNull
    public String toString(@Nullable Event e, boolean debug) {
        return "mongosk database named " + exprDatabaseName.toString(e, debug) + " from " + exprMongoSKServer.toString(e, debug);
    }
}
