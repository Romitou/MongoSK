package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
public class ExprMongoCollectionDocCount extends SimplePropertyExpression<MongoSKCollection, Long> {

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(org.skriptlang.skript.registration.SyntaxRegistry.EXPRESSION, org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder(ExprMongoCollectionDocCount.class, Long.class).addPatterns(ch.njol.skript.expressions.base.PropertyExpression.getPatterns("mongo[(db|sk)] estimated doc[ument][s] count",
            "mongoskcollections")).priority(ExpressionType.PROPERTY.priority()).build());
    }

    @Nullable
    @Override
    public Long convert(MongoSKCollection collection) {
        SubscriberHelpers.ObservableSubscriber<Long> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        collection.getMongoCollection().estimatedDocumentCount().subscribe(observableSubscriber);
        List<Long> counts = observableSubscriber.get();
        // The list is empty if the request failed or timed out (the error is already logged).
        return counts.isEmpty() ? null : counts.get(0);
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo estimated document count";
    }

    @Nonnull
    @Override
    public Class<? extends Long> getReturnType() {
        return Long.class;
    }
}
