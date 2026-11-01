package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.reactivestreams.client.MongoCollection;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Arrays;
import java.util.List;

@Name("Manage Mongo document")
@Description({
    "This effect provides shortcuts to manage documents within a collection.",
    "You can insert a document (generating a new ID if needed), update/replace an existing document, upsert (insert if not exists, update if exists), or remove it entirely.",
    "Documents without \"_id\" field are skipped when updating or removing, and get a new ID when upserting.",
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
public class EffManageMongoDocument extends MongoEffect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffManageMongoDocument.class)
            .addPatterns("(1:insert|2:(replace|update)|3:remove|4:upsert) mongo[(sk|db)] doc[ument][s] %mongoskdocuments% (in[to]|of|from) [collection] %mongoskcollection%")
            .build());
    }

    private Expression<MongoSKDocument> exprMongoSKDocument;
    private Expression<MongoSKCollection> exprMongoSKCollection;
    private int parseMark;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult) {
        exprMongoSKDocument = (Expression<MongoSKDocument>) exprs[0];
        exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[1];
        this.parseMark = parseResult.mark;
        return true;
    }

    @Override
    protected @Nullable Request<?> prepare(@NotNull final Event e) {
        MongoSKDocument[] mongoSKDocuments = exprMongoSKDocument.getArray(e);
        MongoSKCollection mongoSKCollection = exprMongoSKCollection.getSingle(e);
        if (mongoSKDocuments.length == 0 || mongoSKCollection == null)
            return null;
        MongoCollection<Document> collection = mongoSKCollection.getMongoCollection();
        if (parseMark == 1) {
            return Request.withoutResult(collection.insertMany(Arrays.stream(mongoSKDocuments)
                .map(MongoSKDocument::getBsonDocument)
                .toList()));
        }
        List<WriteModel<Document>> writes = switch (parseMark) {
            case 2 -> Arrays.stream(mongoSKDocuments)
                .filter(doc -> hasId(doc, "update"))
                .<WriteModel<Document>>map(doc -> new ReplaceOneModel<>(Filters.eq("_id", doc.getBsonDocument().get("_id")), doc.getBsonDocument()))
                .toList();
            case 3 -> Arrays.stream(mongoSKDocuments)
                .filter(doc -> hasId(doc, "remove"))
                .<WriteModel<Document>>map(doc -> new DeleteOneModel<>(Filters.eq("_id", doc.getBsonDocument().get("_id"))))
                .toList();
            default -> Arrays.stream(mongoSKDocuments)
                // Without an ID, the document cannot exist in the collection: give it one so it is inserted
                // like with the insert mode, instead of being stored with a null ID.
                .peek(doc -> doc.getBsonDocument().putIfAbsent("_id", new ObjectId()))
                .<WriteModel<Document>>map(doc -> new ReplaceOneModel<>(Filters.eq("_id", doc.getBsonDocument().get("_id")), doc.getBsonDocument(), new ReplaceOptions().upsert(true)))
                .toList();
        };
        return writes.isEmpty() ? null : Request.withoutResult(collection.bulkWrite(writes));
    }

    private static boolean hasId(MongoSKDocument document, String action) {
        if (document.getBsonDocument().get("_id") != null)
            return true;
        LoggerHelper.warn("Cannot " + action + " a Mongo document without '_id' field, it has been skipped.",
            "Document: " + document.getBsonDocument().toJson());
        return false;
    }

    @Override
    @NotNull
    public String toString(@Nullable final Event e, boolean debug) {
        return "save mongo document " + exprMongoSKDocument.toString(e, debug) + " into " + exprMongoSKCollection.toString(e, debug);
    }
}
