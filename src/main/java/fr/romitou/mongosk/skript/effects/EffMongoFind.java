package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import com.mongodb.reactivestreams.client.FindPublisher;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bson.Document;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Execute Mongo query")
@Description({
    "Executes a Mongo query and stores the documents found in a variable, without blocking the server.",
    "The script waits for the result like with the wait effect, so its event cannot be cancelled afterwards, and a function cannot return a value afterwards.",
    "If the variable is single, only the first document is retrieved. Use the Last Mongo error expression to know whether the request failed."
})
@Examples({
    "set {_query} to a new mongo query with:",
    "\tcollection: {mycollection}",
    "\tfilter: a new mongosk filter where field \"uuid\" is \"%uuid of player%\"",
    "execute mongo query {_query} and store the result in {_doc}"
})
@Since("3.0.0")
public class EffMongoFind extends MongoEffect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffMongoFind.class)
            .addPatterns("execute mongo[(sk|db)] query %mongoskquery%" + STORE_PATTERN)
            .build());
    }

    private Expression<MongoSKQuery> exprMongoSKQuery;
    private Expression<?> exprStoredIn;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult) {
        exprMongoSKQuery = (Expression<MongoSKQuery>) exprs[0];
        exprStoredIn = exprs[1];
        return storeIn(exprStoredIn);
    }

    @Override
    protected @Nullable Request<?> prepare(Event event) {
        MongoSKQuery query = exprMongoSKQuery.getSingle(event);
        if (query == null)
            return null;
        FindPublisher<Document> publisher = query.buildFindPublisher();
        return new Request<>(
            storesSingleResult() ? publisher.first() : publisher,
            documents -> documents.stream().map(document -> new MongoSKDocument(document, query.getMongoSKCollection())).toArray()
        );
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "execute mongo query " + exprMongoSKQuery.toString(e, debug) + " and store the results in " + exprStoredIn.toString(e, debug);
    }
}
