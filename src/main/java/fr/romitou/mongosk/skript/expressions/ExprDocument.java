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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.event.Event;

@Name("Mongo Document")
@Description("This expression allows you to retrieve a document according to a specific value from a specific collection.")
@Examples({"set {_client} to client named \"default\"",
        "set {_database} to database named \"mongosk\" with {_client}",
        "set {_collection} to collection named \"example\" from {_database}",
        "set {_document} to first document where \"points\" is \"10\" in {_collection}"})
@Since("1.0.0")
public class ExprDocument extends SimpleExpression<Document> {

    static {
        Skript.registerExpression(ExprDocument.class, Document.class, ExpressionType.SIMPLE, "[first] [mongo[db]] document where %string% (is|equals to) %object% (of|in) %mongocollection%");
    }

    private Expression<String> exprWhereName;
    private Expression<Object> exprWhereValue;
    private Expression<MongoCollection> exprCollection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprWhereName = (Expression<String>) exprs[0];
        exprWhereValue = (Expression<Object>) exprs[1];
        exprCollection = (Expression<MongoCollection>) exprs[2];
        return true;
    }

    @Override
    protected Document[] get(Event e) {
        String whereName = exprWhereName.getSingle(e);
        Object whereValue = exprWhereValue.getSingle(e);
        MongoCollection collection = exprCollection.getSingle(e);
        if (whereName == null || whereValue == null || collection == null)
            return new Document[0];
        Document document = (Document) collection.find(Filters.eq(whereName, whereValue)).first();
        return (document == null)
            ? new Document[0]
            : new Document[]{document};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Document> getReturnType() {
        return Document.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "first mongo document where " + exprWhereName.toString(e, debug) + " is " + exprWhereValue.toString(e, debug) + " of " + exprCollection.toString(e, debug);
    }

}
