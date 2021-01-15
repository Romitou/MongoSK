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
import fr.romitou.mongosk.objects.MongoFilter;
import org.bson.Document;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

@Name("Mongo Document")
@Description("This expression allows you to retrieve a document according to a specific value from a specific collection.")
@Examples({"set {_client} to client named \"default\"",
        "set {_database} to database named \"mongosk\" with {_client}",
        "set {_collection} to collection named \"example\" from {_database}",
        "set {_document} to first document where \"points\" is \"10\" in {_collection}"})
@Since("1.0.0")
public class ExprDocument extends SimpleExpression<Document> {

    static {
        Skript.registerExpression(ExprDocument.class, Document.class, ExpressionType.SIMPLE, "[(1¦first|2¦all)] [mongo[db]] document[s] [where] %mongofilter% (of|in) %mongocollection%");
    }

    private Expression<MongoFilter> exprMongoFilter;
    private Expression<MongoCollection> exprCollection;
    private boolean isSingle;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprMongoFilter = (Expression<MongoFilter>) exprs[0];
        exprCollection = (Expression<MongoCollection>) exprs[1];
        isSingle = parseResult.mark == 0 || parseResult.mark == 1;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Document[] get(Event e) {
        MongoFilter filter = exprMongoFilter.getSingle(e);
        MongoCollection collection = exprCollection.getSingle(e);
        if (filter == null || filter.get() == null || collection == null)
            return new Document[0];
        FindIterable<Document> findIterable = collection.find(filter.get());
        if (isSingle) {
            Document document = findIterable.first();
            return (document == null)
                    ? new Document[0]
                    : new Document[]{document};
        } else {
            List<Document> list = new ArrayList<>();
            try {
                findIterable.into(list);
            } catch (ClassCastException | NullPointerException ex) {
                Skript.error("An error occurred while retrieving " + toString(e, false) + ". " + ex.getMessage());
            }
            return list.toArray(new Document[0]);
        }
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public Class<? extends Document> getReturnType() {
        return Document.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return (isSingle ? "first mongo document " : "all mongo documents ") + exprMongoFilter.toString(e, debug) + " in " + exprCollection.toString(e, debug);
    }

}
