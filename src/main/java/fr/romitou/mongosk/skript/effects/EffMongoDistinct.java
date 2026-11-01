package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import com.mongodb.reactivestreams.client.DistinctPublisher;
import fr.romitou.mongosk.adapters.MongoDeserializers;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bson.BsonValue;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Execute Mongo distinct query")
@Description({
    "Retrieves the distinct values of a field among the documents targeted by a Mongo query, without blocking the server.",
    "Only the collection and the filter of the query are used: its sort, projection, limit and skip are ignored, as MongoDB does not support them for this request.",
    "The script waits for the result like with the wait effect."
})
@Examples({
    "set {_query} to a new mongo query with:",
    "\tcollection: {mycollection}",
    "execute mongo distinct on field \"rank\" of query {_query} and store the values in {_ranks::*}"
})
@Since("3.0.0")
public class EffMongoDistinct extends MongoEffect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffMongoDistinct.class)
            .addPatterns("execute mongo[(sk|db)] distinct [on] [field] %string% (of|for) query %mongoskquery%" + STORE_PATTERN)
            .build());
    }

    private Expression<String> exprField;
    private Expression<MongoSKQuery> exprMongoSKQuery;
    private Expression<?> exprStoredIn;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult) {
        exprField = (Expression<String>) exprs[0];
        exprMongoSKQuery = (Expression<MongoSKQuery>) exprs[1];
        exprStoredIn = exprs[2];
        return storeIn(exprStoredIn);
    }

    @Override
    protected @Nullable Request<?> prepare(Event event) {
        String field = exprField.getSingle(event);
        MongoSKQuery query = exprMongoSKQuery.getSingle(event);
        if (field == null || query == null)
            return null;
        DistinctPublisher<BsonValue> publisher = query.requireMongoCollection()
            .distinct(field, BsonValue.class)
            .filter(query.getMongoSKFilter().getFilter());
        return new Request<>(
            storesSingleResult() ? publisher.first() : publisher,
            values -> MongoDeserializers.deserializeBsonValues(values.toArray(new BsonValue[0]))
        );
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "execute mongo distinct on field " + exprField.toString(e, debug) + " of query " + exprMongoSKQuery.toString(e, debug)
            + " and store the values in " + exprStoredIn.toString(e, debug);
    }
}
