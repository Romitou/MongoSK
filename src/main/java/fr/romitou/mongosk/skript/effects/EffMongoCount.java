package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

@Name("Count Mongo documents")
@Description({
    "Counts the documents without blocking the server. The script waits for the result like with the wait effect.",
    "Counting the documents of a query uses its collection, filter, limit and skip, and gives an exact result. Its sort and projection have no effect on the count.",
    "The estimated count of a collection is faster, but it may be less accurate."
})
@Examples({
    "count mongo documents of query {_query} and store the result in {_count}",
    "count estimated mongo documents of collection {mycollection} and store the result in {_estimation}"
})
@Since("3.0.0")
public class EffMongoCount extends MongoEffect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffMongoCount.class)
            .addPatterns(
                "count mongo[(sk|db)] doc[ument][s] (of|from) query %mongoskquery%" + STORE_PATTERN,
                "count estimated mongo[(sk|db)] doc[ument][s] (of|from) [collection] %mongoskcollection%" + STORE_PATTERN
            )
            .build());
    }

    private Expression<MongoSKQuery> exprMongoSKQuery;
    private Expression<MongoSKCollection> exprMongoSKCollection;
    private Expression<?> exprStoredIn;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult) {
        if (matchedPattern == 0)
            exprMongoSKQuery = (Expression<MongoSKQuery>) exprs[0];
        else
            exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[0];
        exprStoredIn = exprs[1];
        return storeIn(exprStoredIn);
    }

    @Override
    protected @Nullable Request<?> prepare(Event event) {
        if (exprMongoSKQuery != null) {
            MongoSKQuery query = exprMongoSKQuery.getSingle(event);
            if (query == null)
                return null;
            return new Request<>(query.requireMongoCollection().countDocuments(query.getMongoSKFilter().getFilter(), query.buildCountOptions()), List::toArray);
        }
        MongoSKCollection collection = exprMongoSKCollection.getSingle(event);
        if (collection == null)
            return null;
        return new Request<>(collection.getMongoCollection().estimatedDocumentCount(), List::toArray);
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        String target = exprMongoSKQuery != null
            ? "mongo documents of query " + exprMongoSKQuery.toString(e, debug)
            : "estimated mongo documents of collection " + exprMongoSKCollection.toString(e, debug);
        return "count " + target + " and store the result in " + exprStoredIn.toString(e, debug);
    }
}
