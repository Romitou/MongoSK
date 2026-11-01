package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.reactivestreams.client.DistinctPublisher;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.adapters.MongoDeserializers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKFilter;
import org.bson.BsonValue;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

@Name("Mongo distinct query")
@Description({
    "Retrieves all distinct values for a specific field across a collection.",
    "The expression will return a list of unique values found in the specified field."
})
@Examples({
    "set {_distinctNames::*} to mongo distinct values of field \"name\" of {mycollection}",
    "broadcast \"Unique names: %{_distinctNames::*}%\""
})
@Since("2.2.1")
public class ExprMongoDistinctResult extends SimpleExpression<Object> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprMongoDistinctResult.class, Object.class)
            .addPatterns("(1:first|2:all) mongo[(sk|db)] field[s] distincted by [field] %string% [(with|by) [filter] %-mongoskfilter%] (of|from) collection %mongoskcollection%")
            .priority(SyntaxInfo.SIMPLE)
            .build());
    }

    private Expression<String> exprDistinctedBy;
    private Expression<MongoSKFilter> exprMongoSKFilter;
    private Expression<MongoSKCollection> exprMongoSKCollection;
    private boolean isFirstDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(@NotNull Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprDistinctedBy = (Expression<String>) exprs[0];
        exprMongoSKFilter = (Expression<MongoSKFilter>) exprs[1];
        exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[2];
        isFirstDocument = parseResult.mark == 1;
        return true;
    }

    @Override
    protected Object[] get(@NotNull final Event e) {

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
    @NotNull
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    @NotNull
    public String toString(@Nullable final Event e, final boolean debug) {
        return (isFirstDocument ? "first mongosk document" : "all mongosk documents") + " distincted by field " + exprDistinctedBy.toString(e, debug) + (exprMongoSKFilter == null ? "" : "with filter " + exprMongoSKFilter.toString(e, debug)) + " of collection " + exprMongoSKCollection.toString(e, debug);
    }
}
