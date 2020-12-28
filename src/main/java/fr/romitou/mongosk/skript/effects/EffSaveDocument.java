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
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;

import java.util.Arrays;

@Name("Save Mongo Document")
@Description("This effect allows you to save one or more documents in a Mongo collection. The document will be replaced by the new one if the identifier \"_id\" already exists. Otherwise it will be created.")
@Examples({"set {_collection} to collection named \"example\" of database \"mongosk\" with client \"test\"",
        "set {_doc} to document where \"player\" is \"Romitou\" in {_collection}",
        "set value \"points\" of {_doc} to 1",
        "save {_doc} in {_collection}"})
@Since("1.0.0")
public class EffSaveDocument extends Effect {

    static {
        Skript.registerEffect(EffSaveDocument.class, "save [mongo[db]] [document] %mongodocuments% (in|into) %mongocollection%");
    }

    private Expression<Document> exprDocument;
    private Expression<MongoCollection> exprCollection;
    // private int parseMark;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        // parseMark = parseResult.mark;
        exprDocument = (Expression<Document>) exprs[0];
        exprCollection = (Expression<MongoCollection>) exprs[1];
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(Event e) {
        Document[] document = exprDocument.getArray(e);
        MongoCollection collection = exprCollection.getSingle(e);
        if (document.length == 0 || collection == null)
            return;
        try {
            Arrays.stream(document).forEach(doc -> {
                Bson filter = Filters.eq("_id", doc.get("_id"));
                if (collection.find(filter).first() == null) {
                    collection.insertOne(doc);
                } else {
                    collection.replaceOne(filter, doc);
                }
            });
        } catch (MongoException ex) {
            Skript.error("Exception encountered while saving the MongoDB document!\n" + ex.getMessage());
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "save mongo document " + exprDocument.toString(e, debug) + (exprCollection != null ? (" in collection " + exprCollection.toString(e, debug)) : "");
    }

}
