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
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("All Mongo collections")
@Description("Quickly retrieve all the collections contained in a Mongo database.")
@Examples({"set {_collections::*} to all mongo collections of {mydatabase}",
    "broadcast \"Collections of my database: %{_collections::*}'s mongo names%\""})
@Since("2.0.0")
public class ExprAllMongoCollections extends SimpleExpression<MongoSKCollection> {

    static {
        Skript.registerExpression(
            ExprAllMongoCollections.class,
            MongoSKCollection.class,
            ExpressionType.PROPERTY,
            "[all] [the] mongo[(db|sk)] collections of %mongoskdatabase%",
            "[all] %mongoskdatabase%'[s] mongo[(db|sk)] collections"
        );
    }

    private Expression<MongoSKDatabase> exprMongoSKDatabase;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprMongoSKDatabase = (Expression<MongoSKDatabase>) exprs[0];
        return true;
    }

    @Override
    protected MongoSKCollection[] get(@Nonnull final Event e) {
        MongoSKDatabase mongoSKDatabase = exprMongoSKDatabase.getSingle(e);
        if (mongoSKDatabase == null)
            return new MongoSKCollection[0];
        SubscriberHelpers.ObservableSubscriber<Document> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        mongoSKDatabase.getMongoDatabase().listCollections().subscribe(observableSubscriber);
        return observableSubscriber.await()
            .getReceived()
            .stream()
            .map(doc -> doc.getString("name"))
            .map(name -> mongoSKDatabase.getMongoDatabase().getCollection(name))
            .map(MongoSKCollection::new)
            .toArray(MongoSKCollection[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKCollection> getReturnType() {
        return MongoSKCollection.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "all mongosk collections of " + exprMongoSKDatabase.toString(e, debug);
    }
}
