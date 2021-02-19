package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mongodb.client.result.InsertManyResult;
import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class EffInsertMongoDocument extends Effect {

    static {
        Skript.registerEffect(
            EffInsertMongoDocument.class,
            "insert mongo[(sk|db)] doc[ument][s] %mongoskdocuments% in[to] [collection] %mongoskcollection%"
        );
    }

    private Expression<MongoSKDocument> exprMongoSKDocument;
    private Expression<MongoSKCollection> exprMongoSKCollection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprMongoSKDocument = (Expression<MongoSKDocument>) exprs[0];
        exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[1];
        return true;
    }

    @Override
    protected void execute(@Nonnull final Event e) {
        MongoSKDocument[] mongoSKDocuments = exprMongoSKDocument.getArray(e);
        MongoSKCollection mongoSKCollection = exprMongoSKCollection.getSingle(e);
        if (mongoSKDocuments.length == 0 || mongoSKCollection == null)
            return;
        long insertQuery = System.currentTimeMillis();
        SubscriberHelpers.ObservableSubscriber<InsertManyResult> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        mongoSKCollection.getMongoCollection()
            .insertMany(Arrays.stream(mongoSKDocuments)
                .map(MongoSKDocument::getBsonDocument)
                .collect(Collectors.toList()))
            .subscribe(observableSubscriber);
        observableSubscriber.await();
        Logger.debug("Insert query executed in " + (System.currentTimeMillis() - insertQuery) + "ms.");
    }

    @Override
    @Nonnull
    public String toString(@Nullable final Event e, boolean debug) {
        return "save mongo document " + exprMongoSKDocument.toString(e, debug) + " into " + exprMongoSKCollection.toString(e, debug);
    }
}
