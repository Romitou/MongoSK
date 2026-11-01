package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxRegistry;

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

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
            PropertyExpression.infoBuilder(ExprMongoCollectionDocCount.class, Long.class, "mongo[(db|sk)] estimated doc[ument][s] count", "mongoskcollections", false).build());
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

    @NotNull
    @Override
    protected String getPropertyName() {
        return "mongo estimated document count";
    }

    @NotNull
    @Override
    public Class<? extends Long> getReturnType() {
        return Long.class;
    }
}
