package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.reactivestreams.client.FindPublisher;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprMongoQueryResult extends SimpleExpression<Document> {

    static {
        Skript.registerExpression(
            ExprMongoQueryResult.class,
            Document.class,
            ExpressionType.PROPERTY,
            "[all] [the] mongo[(db|sk)] (1¦first|2¦all) document[s] of %mongoskquery%",
            "[all] %mongoskquery%'[s] mongo[(db|sk)] (1¦first|2¦all) document[s]"
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
    protected Document[] get(@Nonnull Event e) {
        MongoSKQuery mongoSKQuery = exprMongoSKQuery.getSingle(e);
        if (mongoSKQuery == null || mongoSKQuery.buildIterable() == null)
            return new Document[0];
        FindPublisher<Document> findPublisher = mongoSKQuery.buildIterable();
        if (findPublisher == null)
            return new Document[0];
        SubscriberHelpers.ObservableSubscriber<Document> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        if (isFirstDocument)
            findPublisher.first().subscribe(observableSubscriber);
        else
            findPublisher.subscribe(observableSubscriber);
        return observableSubscriber.await().getReceived().toArray(new Document[0]);
    }

    @Override
    public boolean isSingle() {
        return isFirstDocument;
    }

    @Nonnull
    @Override
    public Class<? extends Document> getReturnType() {
        return Document.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable final Event e, boolean debug) {
        return (isFirstDocument ? "first" : "all") + exprMongoSKQuery.toString(e, debug);
    }
}
