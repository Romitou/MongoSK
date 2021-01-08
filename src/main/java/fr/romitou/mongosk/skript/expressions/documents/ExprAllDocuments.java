package fr.romitou.mongosk.skript.expressions.documents;

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
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

@Name("All Mongo Documents")
@Description("This expression allows you to retrieve all the documents in a specific collection. Note that this may take some time if you have a lot of documents, so it is best to use the search expression with a where in order to fine tune your search.")
@Examples({"loop all documents in {collection}:",
        "\tbroadcast \"%loop-value's json%\""})
@Since("1.1.1")
public class ExprAllDocuments extends SimpleExpression<Document> {

    static {
        Skript.registerExpression(ExprAllDocuments.class, Document.class, ExpressionType.SIMPLE, "(all|every) mongo[db] documents (in|of) %mongocollection%");
    }

    private Expression<MongoCollection> exprCollection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprCollection = (Expression<MongoCollection>) exprs[0];
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Document[] get(Event e) {
        MongoCollection collection = exprCollection.getSingle(e);
        if (collection == null) return new Document[0];
        List<Document> list = new ArrayList<>();
        try {
            FindIterable<Document> findIterable = collection.find();
            findIterable.cursor().forEachRemaining(list::add);
        } catch (ClassCastException | NullPointerException ex) {
            Skript.error("An error occurred while retrieving all documents. " + ex.getMessage());
        }
        return list.toArray(new Document[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Document> getReturnType() {
        return Document.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "all mongo documents of " + exprCollection.toString(e, debug);
    }
}
