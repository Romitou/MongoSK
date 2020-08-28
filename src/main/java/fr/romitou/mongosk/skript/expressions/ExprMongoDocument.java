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

public class ExprMongoDocument extends SimpleExpression<Document> {

    static {
        Skript.registerExpression(ExprMongoDocument.class, Document.class, ExpressionType.SIMPLE, "(1|first) [mongo[db]] document where %string% (is|equals to) %object% in %mongocollection%");
    }

    private Expression<String> whereName;
    private Expression<Object> whereValue;
    private Expression<MongoCollection> collection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        whereName = (Expression<String>) exprs[0];
        whereValue = (Expression<Object>) exprs[1];
        collection = (Expression<MongoCollection>) exprs[2];
        return true;
    }

    @Override
    protected Document[] get(Event e) {
        String wn = whereName.getSingle(e);
        Object wv = whereValue.getSingle(e);
        MongoCollection c = collection.getSingle(e);
        if (wn == null || wv == null || c == null)
            return null;
        return new Document[]{(Document) c.find(Filters.eq(wn, wv)).first()};
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
        return "mongo document where " + whereName.toString(e, debug) + " is " + whereValue.toString(e, debug);
    }

}
