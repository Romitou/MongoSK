package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;

import javax.annotation.Nonnull;

public class ExprMongoCollectionDocCount extends SimplePropertyExpression<MongoSKCollection, Integer> {

    static {
        register(
            ExprMongoCollectionDocCount.class,
            Integer.class,
            "mongo[(db|sk)] [estimated] doc[ument][s] count",
            "mongoskcollections"
        );
    }

    @Nonnull
    @Override
    public Integer convert(MongoSKCollection collection) {
        SubscriberHelpers.ObservableSubscriber<Long> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        collection.getMongoCollection().estimatedDocumentCount().subscribe(observableSubscriber);
        return observableSubscriber.await().getReceived().get(0).intValue();
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo estimated document count";
    }

    @Nonnull
    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }
}
