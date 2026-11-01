package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import com.mongodb.reactivestreams.client.MongoCollection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Delete or replace Mongo documents of a query")
@Description({
    "Deletes the documents targeted by a Mongo query, or replaces the first one, without blocking the server.",
    "The collection and the filter of the query are used. The script waits for the end of the request like with the wait effect."
})
@Examples({
    "delete all mongo documents of query {_query}",
    "delete first mongo document of query {_query}",
    "replace first mongo document of query {_query} with {_document}"
})
@Since("3.0.0")
public class EffMongoModifyQuery extends MongoEffect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffMongoModifyQuery.class)
            .addPatterns(
                "delete (1:first|2:all) mongo[(sk|db)] doc[ument][s] (of|from) query %mongoskquery%",
                "replace first mongo[(sk|db)] doc[ument] (of|from) query %mongoskquery% with %mongoskdocument%"
            )
            .build());
    }

    private Expression<MongoSKQuery> exprMongoSKQuery;
    private Expression<MongoSKDocument> exprReplacement;
    private boolean onlyFirst;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult) {
        exprMongoSKQuery = (Expression<MongoSKQuery>) exprs[0];
        if (matchedPattern == 1)
            exprReplacement = (Expression<MongoSKDocument>) exprs[1];
        onlyFirst = parseResult.mark == 1;
        return true;
    }

    @Override
    protected @Nullable Request<?> prepare(Event event) {
        MongoSKQuery query = exprMongoSKQuery.getSingle(event);
        if (query == null)
            return null;
        MongoCollection<Document> collection = query.requireMongoCollection();
        Bson filter = query.getMongoSKFilter().getFilter();
        if (exprReplacement != null) {
            MongoSKDocument replacement = exprReplacement.getSingle(event);
            if (replacement == null)
                return null;
            return Request.withoutResult(collection.replaceOne(filter, replacement.getBsonDocument()));
        }
        return Request.withoutResult(onlyFirst ? collection.deleteOne(filter) : collection.deleteMany(filter));
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        String query = " mongo documents of query " + exprMongoSKQuery.toString(e, debug);
        if (exprReplacement != null)
            return "replace first" + query + " with " + exprReplacement.toString(e, debug);
        return "delete " + (onlyFirst ? "first" : "all") + query;
    }
}
