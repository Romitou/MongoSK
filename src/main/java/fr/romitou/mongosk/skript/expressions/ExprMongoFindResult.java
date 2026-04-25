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
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKFilter;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Name("Mongo find query")
@Description({
    "Executes a Mongo query or filter and returns the resulting documents from the collection.",
    "You can choose to retrieve the first matching document, or all matching documents."
})
@Examples({
    "set {_doc} to first mongo document of {mycollection}",
    "set {_docs::*} to all mongo documents with filter {_filter} of collection {mycollection}",
    "set {_queryDocs::*} to all mongo documents for query {_query}"
})
@Since("2.0.0")
public class ExprMongoFindResult extends SimpleExpression<MongoSKDocument> {

    static {
        Skript.registerExpression(
            ExprMongoFindResult.class,
            MongoSKDocument.class,
            ExpressionType.COMBINED,
            "[find] (1¦first|2¦all) mongo[(sk|db)] document[s] [(with|by) [filter] %-mongoskfilter%] (of|from) collection %mongoskcollection%",
            "[find] (1¦first|2¦all) mongo[(sk|db)] document[s] (of|from) query %mongoskquery%"
        );
    }

    private Expression<MongoSKFilter> exprMongoSKFilter;
    private Expression<MongoSKCollection> exprMongoSKCollection;
    private Expression<MongoSKQuery> exprMongoSKQuery;
    private Boolean isFirstDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(@Nonnull Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        if (matchedPattern == 0) {
            exprMongoSKFilter = (Expression<MongoSKFilter>) exprs[0];
            exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[1];
        } else if (matchedPattern == 1)
            exprMongoSKQuery = (Expression<MongoSKQuery>) exprs[0];
        isFirstDocument = parseResult.mark == 1;
        return true;
    }

    @Override
    protected MongoSKDocument[] get(@Nonnull final Event e) {
        MongoSKQuery query = buildQuery(e);
        if (query == null || query.getMongoSKCollection() == null)
            return new MongoSKDocument[0];
        long getQuery = System.currentTimeMillis();
        SubscriberHelpers.ObservableSubscriber<Document> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        if (isFirstDocument)
            query.buildFindPublisher().first().subscribe(observableSubscriber);
        else
            query.buildFindPublisher().subscribe(observableSubscriber);
        List<Document> documents = observableSubscriber.get();
        LoggerHelper.debug("Simple get query executed in " + (System.currentTimeMillis() - getQuery) + "ms.");
        return documents.stream()
            .map(document -> new MongoSKDocument(document, query.getMongoSKCollection()))
            .toArray(MongoSKDocument[]::new);
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
                if (!isFirstDocument) // MongoDB doesn't support replacing multiple documents
                    return null;
                return CollectionUtils.array(MongoSKDocument.class);
            case DELETE:
                return CollectionUtils.array();
            default:
                return null;
        }
    }

    @Override
    public void change(@Nonnull final Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        MongoSKQuery query = buildQuery(e);
        if (query == null)
            return;
        switch (mode) {
            case DELETE:
                long deleteQuery = System.currentTimeMillis();
                SubscriberHelpers.ObservableSubscriber<DeleteResult> deleteSubscriber = new SubscriberHelpers.OperationSubscriber<>();
                if (isFirstDocument)
                    query.getMongoSKCollection().getMongoCollection()
                        .deleteOne(query.getMongoSKFilter().getFilter())
                        .subscribe(deleteSubscriber);
                else
                    query.getMongoSKCollection().getMongoCollection()
                        .deleteMany(query.getMongoSKFilter().getFilter())
                        .subscribe(deleteSubscriber);
                deleteSubscriber.await();
                LoggerHelper.debug("Simple delete query executed in " + (System.currentTimeMillis() - deleteQuery) + "ms.");
                break;
            case SET:
                long replaceQuery = System.currentTimeMillis();
                MongoSKDocument mongoSKDocument = (MongoSKDocument) delta[0];
                if (mongoSKDocument == null)
                    return;
                SubscriberHelpers.ObservableSubscriber<UpdateResult> updateSubscriber = new SubscriberHelpers.OperationSubscriber<>();
                query.getMongoSKCollection().getMongoCollection()
                    .replaceOne(query.getMongoSKFilter().getFilter(), mongoSKDocument.getBsonDocument())
                    .subscribe(updateSubscriber);
                updateSubscriber.await();
                LoggerHelper.debug("Simple replace query executed in " + (System.currentTimeMillis() - replaceQuery) + "ms.");
            default:
                break;
        }
    }

    private MongoSKQuery buildQuery(Event e) {
        if (exprMongoSKQuery != null)
            return exprMongoSKQuery.getSingle(e);
        MongoSKFilter mongoSKFilter = null;
        if (exprMongoSKFilter != null)
            mongoSKFilter = exprMongoSKFilter.getSingle(e);
        MongoSKCollection collection = exprMongoSKCollection.getSingle(e);
        if (collection == null)
            return null;
        MongoSKQuery query = new MongoSKQuery();
        query.setMongoSKFilter(mongoSKFilter);
        query.setMongoSKCollection(collection);
        return query;
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
        if (e != null) {
            MongoSKQuery query = buildQuery(e);
            if (query != null)
                return (isFirstDocument ? "first mongosk document" : "all mongosk documents") + query.getDisplay();
        }
        return (isFirstDocument ? "first mongosk document" : "all mongosk documents") + " of a query";
    }
}
