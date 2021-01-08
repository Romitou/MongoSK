package fr.romitou.mongosk.skript.effects.documents;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.event.Event;

@Name("Delete Mongo Document")
@Description("This effect allows you to delete a document from a specific collection.")
@Examples({"set {_doc} to first document where \"player\" is \"Romitou\" in {collection}",
        "delete mongo document {_doc} from {collection}"
})
@Since("1.1.1")
public class EffDeleteDocument extends Effect {

    static {
        Skript.registerEffect(EffDeleteDocument.class,
                "(delete|remove) mongo[db] [document[s]] %mongodocuments% from %mongocollection%",
                "(delete|remove) mongo[db] [document] with id[entifier] %object% from %mongocollection%"
        );
    }

    private Expression<Document> exprDocument;
    private Expression<MongoCollection> exprCollection;
    private Expression<Object> exprId;
    private boolean isDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        isDocument = matchedPattern == 0;
        if (isDocument)
            exprDocument = (Expression<Document>) exprs[0];
        else
            exprId = (Expression<Object>) exprs[0];
        exprCollection = (Expression<MongoCollection>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event e) {
        MongoCollection collection = exprCollection.getSingle(e);
        if (collection == null) return;
        if (!isDocument) {
            Object id = exprId.getSingle(e);
            if (id == null) return;
            collection.deleteOne(Filters.eq("_id", id));
            return;
        }
        Document[] documents = exprDocument.getArray(e);
        if (documents.length == 0) return;
        for (Document doc : documents) {
            collection.deleteOne(Filters.eq("_id", doc.get("_id")));
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "delete mongo document " + (exprDocument == null ? "with identifier " + exprId.toString(e, debug) : exprDocument.toString(e, debug)) + " from " + exprCollection.toString(e, debug);
    }
}
