package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.*;
import com.mongodb.client.result.InsertManyResult;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Name("Manage Mongo document")
@Description("Firstly, you can insert your Mongo document in the collection you want, with an automatically generated " +
    "unique identifier if not specified or already existing. This will not replace existing documents with the same " +
    "identifier, but will insert a new one. Secondly, if you want to replace the document, you can use the replace syntax. " +
    "Thirdly, you can use upsert to insert the document if it does not exist, or replace it if it does. " +
    "Finally, to remove the document, simply use remove. " +
    "This effect is only a shortcut and simply executes a query with \"_id\" as the unique identifier. " +
    "If you have changed the unique ID field, you should not use this effect.")
@Examples({"set {_document} to a new mongo document",
    "set mongo field \"playerName\" of {_document} to \"Romitou\"",
    "insert mongo document {_document} into collection {mycollection}",
    "",
    "update mongo document {_document} of {mycollection}",
    "upsert mongo document {_document} into {mycollection}",
    "remove mongo documents {_doc1} and {_doc2} from {mycollection}"})
@Since("2.0.0")
public class EffManageMongoDocument extends Effect {

    static {
        Skript.registerEffect(
            EffManageMongoDocument.class,
            "(1¦insert|2¦(replace|update)|3¦remove|4¦upsert) mongo[(sk|db)] doc[ument][s] %mongoskdocuments% (in[to]|of|from) [collection] %mongoskcollection%"
        );
    }

    private Expression<MongoSKDocument> exprMongoSKDocument;
    private Expression<MongoSKCollection> exprMongoSKCollection;
    private int parseMark;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprMongoSKDocument = (Expression<MongoSKDocument>) exprs[0];
        exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[1];
        this.parseMark = parseResult.mark;
        return true;
    }

    @Override
    protected void execute(@Nonnull final Event e) {
        MongoSKDocument[] mongoSKDocuments = exprMongoSKDocument.getArray(e);
        MongoSKCollection mongoSKCollection = exprMongoSKCollection.getSingle(e);
        if (mongoSKDocuments.length == 0 || mongoSKCollection == null)
            return;
        switch (parseMark) {
            case 1:
                List<Document> insertList = Arrays.stream(mongoSKDocuments)
                    .map(MongoSKDocument::getBsonDocument)
                    .collect(Collectors.toList());
                if (!insertList.isEmpty()) {
                    long insertQuery = System.currentTimeMillis();
                    SubscriberHelpers.ObservableSubscriber<InsertManyResult> observableInsertSubscriber = new SubscriberHelpers.OperationSubscriber<>();
                    mongoSKCollection.getMongoCollection()
                        .insertMany(insertList)
                        .subscribe(observableInsertSubscriber);
                    observableInsertSubscriber.await();
                    LoggerHelper.debug("Insert query executed in " + (System.currentTimeMillis() - insertQuery) + "ms.");
                }
                break;
            case 2:
                List<WriteModel<Document>> updateList = Arrays.stream(mongoSKDocuments)
                    .map(doc -> (WriteModel<Document>) new ReplaceOneModel<Document>(Filters.eq("_id", doc.getBsonDocument().get("_id")), doc.getBsonDocument()))
                    .collect(Collectors.toList());
                if (!updateList.isEmpty()) {
                    long updateQuery = System.currentTimeMillis();
                    SubscriberHelpers.ObservableSubscriber<BulkWriteResult> observableUpdateSubscriber = new SubscriberHelpers.OperationSubscriber<>();
                    mongoSKCollection.getMongoCollection()
                        .bulkWrite(updateList)
                        .subscribe(observableUpdateSubscriber);
                    observableUpdateSubscriber.await();
                    LoggerHelper.debug("Update query executed in " + (System.currentTimeMillis() - updateQuery) + "ms.");
                }
                break;
            case 3:
                List<WriteModel<Document>> deleteList = Arrays.stream(mongoSKDocuments)
                    .map(doc -> (WriteModel<Document>) new DeleteOneModel<Document>(Filters.eq("_id", doc.getBsonDocument().get("_id"))))
                    .collect(Collectors.toList());
                if (!deleteList.isEmpty()) {
                    long deleteQuery = System.currentTimeMillis();
                    SubscriberHelpers.ObservableSubscriber<BulkWriteResult> observableDeleteSubscriber = new SubscriberHelpers.OperationSubscriber<>();
                    mongoSKCollection.getMongoCollection()
                        .bulkWrite(deleteList)
                        .subscribe(observableDeleteSubscriber);
                    observableDeleteSubscriber.await();
                    LoggerHelper.debug("Delete query executed in " + (System.currentTimeMillis() - deleteQuery) + "ms.");
                }
                break;
            case 4:
                List<WriteModel<Document>> upsertList = Arrays.stream(mongoSKDocuments)
                    .map(doc -> (WriteModel<Document>) new ReplaceOneModel<Document>(Filters.eq("_id", doc.getBsonDocument().get("_id")), doc.getBsonDocument(), new ReplaceOptions().upsert(true)))
                    .collect(Collectors.toList());
                if (!upsertList.isEmpty()) {
                    long upsertQuery = System.currentTimeMillis();
                    SubscriberHelpers.ObservableSubscriber<BulkWriteResult> observableUpsertSubscriber = new SubscriberHelpers.OperationSubscriber<>();
                    mongoSKCollection.getMongoCollection()
                        .bulkWrite(upsertList)
                        .subscribe(observableUpsertSubscriber);
                    observableUpsertSubscriber.await();
                    LoggerHelper.debug("Upsert query executed in " + (System.currentTimeMillis() - upsertQuery) + "ms.");
                }
                break;
        }

    }

    @Override
    @Nonnull
    public String toString(@Nullable final Event e, boolean debug) {
        return "save mongo document " + exprMongoSKDocument.toString(e, debug) + " into " + exprMongoSKCollection.toString(e, debug);
    }
}
