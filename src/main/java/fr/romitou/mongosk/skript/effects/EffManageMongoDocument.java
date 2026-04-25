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
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import com.mongodb.client.model.WriteModel;

@Name("Manage Mongo document")
@Description({
    "This effect provides shortcuts to manage documents within a collection.",
    "You can insert a document (generating a new ID if needed), update/replace an existing document, upsert (insert if not exists, update if exists), or remove it entirely.",
    "Note: This effect relies on the \"_id\" field as the unique identifier. If you have modified the \"_id\" field manually, behavior may be unexpected and using specialized queries is recommended."
})
@Examples({
    "set {_document} to a new mongo document",
    "set mongo value \"playerName\" of {_document} to \"MongoUser\"",
    "insert mongo document {_document} into collection {mycollection}",
    "update mongo document {_document} of {mycollection}",
    "upsert mongo document {_document} into {mycollection}",
    "remove mongo documents {_doc1} and {_doc2} from {mycollection}"
})
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
