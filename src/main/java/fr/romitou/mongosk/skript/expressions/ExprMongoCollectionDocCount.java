package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;

import javax.annotation.Nonnull;

@Name("Mongo collection document count")
@Description({
    "Counts the total number of documents in a collection.",
    "You can also provide a filter to count only documents matching specific criteria."
})
@Examples({
    "set {_total} to number of mongo documents of {mycollection}",
    "set {_filteredCount} to number of mongo documents with filter {_filter} of {mycollection}"
})
@Since("2.0.0")
public class ExprMongoCollectionDocCount extends SimplePropertyExpression<MongoSKCollection, Integer> {

    static {
        register(
            ExprMongoCollectionDocCount.class,
            Integer.class,
            "mongo[(db|sk)] estimated doc[ument][s] count",
            "mongoskcollections"
        );
    }

    @Nonnull
    @Override
    public Integer convert(MongoSKCollection collection) {
        SubscriberHelpers.ObservableSubscriber<Long> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        collection.getMongoCollection().estimatedDocumentCount().subscribe(observableSubscriber);
        return observableSubscriber.get().get(0).intValue();
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
