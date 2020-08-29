package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.event.Event;

import java.util.Arrays;

public class EffSaveDocument extends Effect {

    static {
        Skript.registerEffect(EffSaveDocument.class, "save [mongo[db]] [document] %mongodocuments% in %mongocollection%");
    }

    private Expression<Document> exprDocument;
    private Expression<MongoCollection> exprCollection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprDocument = (Expression<Document>) exprs[0];
        exprCollection = (Expression<MongoCollection>) exprs[1];
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(Event e) {
        Document[] document = exprDocument.getArray(e);
        MongoCollection collection = exprCollection.getSingle(e);
        if (collection == null)
            return;
        Arrays.stream(document).forEach(doc -> {
            collection.replaceOne(Filters.eq("_id", (ObjectId) doc.get("_id")), doc);
        });
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "save mongo document " + exprDocument.toString(e, debug) + (exprCollection != null ? (" in collection " + exprCollection.toString(e, debug)) : "");
    }

}
