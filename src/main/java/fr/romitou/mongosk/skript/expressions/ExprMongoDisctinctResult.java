package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.reactivestreams.client.DistinctPublisher;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.adapters.MongoDeserializers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.elements.MongoSKFilter;
import org.bson.BsonValue;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Name("Mongo distinct query")
@Description("The distinct query is useful when you want to retrieve a specific field from each document in a given " +
    "collection. For example, if you have documents with a \"name\" field, you can use this expression to retrieve " +
    "all the \"name\" fields of the documents in the collection. Useful, isn't it?" +
    "" +
    "This expression does not accept constructed queries because there is no need to use it. " +
    "They can only be used with the find query.")
@Examples({"set {_names::*} to all mongo fields distincted by \"name\" of collection {myCollection}",
    "",
    "set {_filter} to mongosk filter where field \"admin\" is true",
    "set {_adminNames::*} to all mongo fields distincted by \"name\" with filter {_filter} of collection {myCollection}"})
@Since("2.2.1")
public class ExprMongoDisctinctResult extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(
            ExprMongoDisctinctResult.class,
            Object.class,
            ExpressionType.COMBINED,
            "(1¦first|2¦all) mongo[(sk|db)] field[s] distincted by [field] %string% [(with|by) [filter] %-mongoskfilter%] (of|from) collection %mongoskcollection%"
        );
    }

    private Expression<String> exprDistinctedBy;
    private Expression<MongoSKFilter> exprMongoSKFilter;
    private Expression<MongoSKCollection> exprMongoSKCollection;
    private Boolean isFirstDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(@Nonnull Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprDistinctedBy = (Expression<String>) exprs[0];
        exprMongoSKFilter = (Expression<MongoSKFilter>) exprs[1];
        exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[2];
        isFirstDocument = parseResult.mark == 1;
        return true;
    }

    @Override
    protected Object[] get(@Nonnull final Event e) {

        String distinctedBy = exprDistinctedBy.getSingle(e);
        MongoSKCollection mongoSKCollection = exprMongoSKCollection.getSingle(e);

        if (distinctedBy == null || mongoSKCollection == null)
            return new Object[0];

        DistinctPublisher<BsonValue> distinctPublisher = mongoSKCollection.getMongoCollection().distinct(distinctedBy, BsonValue.class);
        if (exprMongoSKFilter != null) {
            MongoSKFilter filter = exprMongoSKFilter.getSingle(e);
            if (filter != null) {
                distinctPublisher.filter(filter.getFilter());
            }
        }

        long distinctQuery = System.currentTimeMillis();
        SubscriberHelpers.ObservableSubscriber<BsonValue> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        if (isFirstDocument)
            distinctPublisher.first().subscribe(observableSubscriber);
        else
            distinctPublisher.subscribe(observableSubscriber);
        List<BsonValue> values = observableSubscriber.get();
        LoggerHelper.debug("Simple distinct query executed in " + (System.currentTimeMillis() - distinctQuery) + "ms.");

        return MongoDeserializers.deserializeBsonValues(values.toArray(new BsonValue[0]));
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
        return (isFirstDocument ? "first mongosk document" : "all mongosk documents") + " distincted by field " + exprDistinctedBy.toString(e, debug) + (exprMongoSKFilter == null ? "" : "with filter " + exprMongoSKFilter.toString(e, debug)) + " of collection " + exprMongoSKCollection.toString(e, debug);
    }
}
