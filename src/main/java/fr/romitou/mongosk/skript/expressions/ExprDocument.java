package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.event.Event;

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
            return null;
        Document document = (Document) collection.find(Filters.eq(whereName, whereValue)).first();
        if (document == null)
            return null;
        return new Document[]{document};
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
