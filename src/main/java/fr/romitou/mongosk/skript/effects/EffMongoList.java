package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("List Mongo databases and collections")
@Description("Retrieves the databases of a Mongo client or the collections of a database, without blocking the server. The script waits for the result like with the wait effect.")
@Examples({
    "list mongo databases of {myserver} and store the results in {_databases::*}",
    "list mongo collections of {mydatabase} and store the results in {_collections::*}"
})
@Since("3.0.0")
public class EffMongoList extends MongoEffect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffMongoList.class)
            .addPatterns(
                "list [all] [the] mongo[(sk|db)] databases of %mongoskserver%" + STORE_PATTERN,
                "list [all] [the] mongo[(sk|db)] collections of %mongoskdatabase%" + STORE_PATTERN
            )
            .build());
    }

    private Expression<MongoSKServer> exprMongoSKServer;
    private Expression<MongoSKDatabase> exprMongoSKDatabase;
    private Expression<?> exprStoredIn;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult) {
        if (matchedPattern == 0)
            exprMongoSKServer = (Expression<MongoSKServer>) exprs[0];
        else
            exprMongoSKDatabase = (Expression<MongoSKDatabase>) exprs[0];
        exprStoredIn = exprs[1];
        return storeIn(exprStoredIn);
    }

    @Override
    protected @Nullable Request<?> prepare(Event event) {
        if (exprMongoSKServer != null) {
            MongoSKServer server = exprMongoSKServer.getSingle(event);
            if (server == null)
                return null;
            return new Request<>(server.getMongoClient().listDatabases(), databases -> databases.stream()
                .map(database -> new MongoSKDatabase(server.getMongoClient().getDatabase(database.getString("name"))))
                .toArray());
        }
        MongoSKDatabase database = exprMongoSKDatabase.getSingle(event);
        if (database == null)
            return null;
        return new Request<>(database.getMongoDatabase().listCollections(), collections -> collections.stream()
            .map(collection -> new MongoSKCollection(database.getMongoDatabase().getCollection(collection.getString("name"))))
            .toArray());
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        String target = exprMongoSKServer != null
            ? "databases of " + exprMongoSKServer.toString(e, debug)
            : "collections of " + exprMongoSKDatabase.toString(e, debug);
        return "list mongo " + target + " and store the results in " + exprStoredIn.toString(e, debug);
    }
}
