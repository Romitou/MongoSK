package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.FindPublisher;
import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKFilter;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("Mongo simple query")
@Description("Use this expression if you want to make simple requests, easily and quickly. " +
    "To do so, you must specify the type of your query: are you looking for several documents or the first document in " +
    "the collection? Then you can use a filter to specify the type of data you are looking for. " +
    "Finally, you need to specify where the query should ask for the data, i.e. in which collection." +
    "" +
    "You also have the possibility to modify already existing documents and delete documents, " +
    "in the same way as for making your queries.")
@Examples({"set {_docs::*} to all mongo documents of {mycollection}",
    "set {_filter} to mongosk filter where field \"example\" is true",
    "set {_mydoc} to first mongo document with filter {_filter} of {mycollection}",
    "set first mongo document with mongosk filter where field \"_id\" equals \"507f1f77bcf86cd799439011\" to {_mydoc}",
    "delete all mongosk documents of {mycollection}"})
@Since("2.0.0")
public class ExprMongoSimpleQuery extends SimpleExpression<MongoSKDocument> {

    static {
        Skript.registerExpression(
            ExprMongoSimpleQuery.class,
            MongoSKDocument.class,
            ExpressionType.COMBINED,
            "(1¦first|2¦all) mongo[(sk|db)] document[s] [(with|by) [filter] %-mongoskfilter%] (of|from) %mongoskcollection%"
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
        if (mongoSKCollection == null)
            return new MongoSKDocument[0];
        long getQuery = System.currentTimeMillis();
        FindPublisher<Document> findPublisher = mongoSKFilter == null
            ? mongoSKCollection.getMongoCollection().find()
            : mongoSKCollection.getMongoCollection().find(mongoSKFilter.getFilter());
        SubscriberHelpers.ObservableSubscriber<Document> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        if (isFirstDocument)
            findPublisher.first().subscribe(observableSubscriber);
        else
            findPublisher.subscribe(observableSubscriber);
        Logger.debug("Simple get query executed in " + (System.currentTimeMillis() - getQuery) + "ms.");
        return observableSubscriber
            .await()
            .getReceived()
            .stream()
            .map(document -> new MongoSKDocument(document, mongoSKCollection))
            .toArray(MongoSKDocument[]::new);
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
                if (isFirstDocument) // MongoDB doesn't support replacing multiple documents
                    return CollectionUtils.array(MongoSKDocument.class);
            case DELETE:
                return CollectionUtils.array();
            default:
                return new Class[0];
        }
    }

    @Override
    public void change(@Nonnull final Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        MongoSKFilter mongoSKFilter = exprMongoSKFilter.getSingle(e);
        MongoSKCollection mongoSKCollection = exprMongoSKCollection.getSingle(e);
        if (mongoSKFilter == null || mongoSKCollection == null)
            return;
        switch (mode) {
            case DELETE:
                long deleteQuery = System.currentTimeMillis();
                SubscriberHelpers.ObservableSubscriber<DeleteResult> deleteSubscriber = new SubscriberHelpers.OperationSubscriber<>();
                if (isFirstDocument)
                    mongoSKCollection.getMongoCollection()
                        .deleteOne(mongoSKFilter.getFilter())
                        .subscribe(deleteSubscriber);
                else
                    mongoSKCollection.getMongoCollection()
                        .deleteMany(mongoSKFilter.getFilter())
                        .subscribe(deleteSubscriber);
                deleteSubscriber.await();
                Logger.debug("Simple delete query executed in " + (System.currentTimeMillis() - deleteQuery) + "ms.");
                break;
            case SET:
                long replaceQuery = System.currentTimeMillis();
                MongoSKDocument mongoSKDocument = (MongoSKDocument) delta[0];
                if (mongoSKDocument == null)
                    return;
                SubscriberHelpers.ObservableSubscriber<UpdateResult> updateSubscriber = new SubscriberHelpers.OperationSubscriber<>();
                mongoSKCollection.getMongoCollection()
                    .replaceOne(mongoSKFilter.getFilter(), mongoSKDocument.getBsonDocument())
                    .subscribe(updateSubscriber);
                updateSubscriber.await();
                Logger.debug("Simple replace query executed in " + (System.currentTimeMillis() - replaceQuery) + "ms.");
            default:
                break;
        }
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
        return (isFirstDocument ? "first mongosk document" : "all mongosk documents") + (exprMongoSKFilter == null ? "" : " where " + exprMongoSKFilter.toString(e, debug)) + " of " + exprMongoSKCollection.toString(e, debug);
    }
}
