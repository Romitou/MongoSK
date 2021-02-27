package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.reactivestreams.client.FindPublisher;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprMongoQueryResult extends SimpleExpression<MongoSKDocument> {

    static {
        Skript.registerExpression(
            ExprMongoQueryResult.class,
            MongoSKDocument.class,
            ExpressionType.PROPERTY,
            "[all] [the] (1¦first|2¦all) mongo[(db|sk)] (document|result)[s] of query %mongoskquery%",
            "[all] query %mongoskquery%'[s] (1¦first|2¦all) mongo[(db|sk)] (document|result)[s]"
        );
    }

    private Expression<MongoSKQuery> exprMongoSKQuery;
    private boolean isFirstDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprMongoSKQuery = (Expression<MongoSKQuery>) exprs[0];
        isFirstDocument = parseResult.mark == 1;
        return true;
    }

    @Override
    protected MongoSKDocument[] get(@Nonnull Event e) {
        MongoSKQuery mongoSKQuery = exprMongoSKQuery.getSingle(e);
        if (mongoSKQuery == null || mongoSKQuery.buildIterable() == null)
            return new MongoSKDocument[0];
        FindPublisher<Document> findPublisher = mongoSKQuery.buildIterable();
        if (findPublisher == null)
            return new MongoSKDocument[0];
        SubscriberHelpers.ObservableSubscriber<Document> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        if (isFirstDocument)
            findPublisher.first().subscribe(observableSubscriber);
        else
            findPublisher.subscribe(observableSubscriber);
        return observableSubscriber.get()
            .stream()
            .map(document -> new MongoSKDocument(document, mongoSKQuery.getMongoSKCollection()))
            .toArray(MongoSKDocument[]::new);
    }

    @Override
    public boolean isSingle() {
        return isFirstDocument;
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKDocument> getReturnType() {
        return MongoSKDocument.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable final Event e, boolean debug) {
        return (isFirstDocument ? "first" : "all") + exprMongoSKQuery.toString(e, debug);
    }
}
