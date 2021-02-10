package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.reactivestreams.client.FindPublisher;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKFilter;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprMongoSimpleQuery extends SimpleExpression<MongoSKDocument> {

    static {
        Skript.registerExpression(
            ExprMongoSimpleQuery.class,
            MongoSKDocument.class,
            ExpressionType.COMBINED,
            "(1¦first|2¦all) mongo[(sk|db)] document[s] (with|by) [filter] %mongoskfilter% (of|from) %mongoskcollection%"
        );
    }

    private Expression<MongoSKFilter> exprMongoSKFilter;
    private Expression<MongoSKCollection> exprMongoSKCollection;
    private Boolean isFirstDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprMongoSKFilter = (Expression<MongoSKFilter>) exprs[0];
        exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[1];
        isFirstDocument = parseResult.mark == 1;
        return true;
    }

    @Override
    protected MongoSKDocument[] get(@Nonnull final Event e) {
        MongoSKFilter mongoSKFilter = exprMongoSKFilter.getSingle(e);
        MongoSKCollection mongoSKCollection = exprMongoSKCollection.getSingle(e);
        if (mongoSKFilter == null || mongoSKCollection == null)
            return new MongoSKDocument[0];
        FindPublisher<Document> findPublisher = mongoSKCollection.getMongoCollection().find(mongoSKFilter.getFilter());
        SubscriberHelpers.ObservableSubscriber<Document> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        if (isFirstDocument)
            findPublisher.first().subscribe(observableSubscriber);
        else
            findPublisher.subscribe(observableSubscriber);
        return observableSubscriber
            .await()
            .getReceived()
            .stream()
            .map(document -> new MongoSKDocument(document, mongoSKCollection))
            .toArray(MongoSKDocument[]::new);
    }

    @Override
    public boolean isSingle() {
        return isFirstDocument;
    }

    @Override
    @Nonnull
    public Class<? extends MongoSKDocument> getReturnType() {
        return MongoSKDocument.class;
    }

    @Override
    @Nonnull
    public String toString(@Nullable final Event e, final boolean debug) {
        return (isFirstDocument ? "first mongosk document" : "all mongosk documents") + " where " + exprMongoSKFilter.toString(e, debug) + " of " + exprMongoSKCollection.toString(e, debug);
    }
}
