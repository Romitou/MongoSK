package fr.romitou.mongosk.skript.expressions.documents;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.objects.MongoQuery;
import org.bson.Document;
import org.bukkit.event.Event;

import java.util.ArrayList;

public class ExprDocument extends SimpleExpression<Document> {

    static {
        Skript.registerExpression(ExprDocument.class, Document.class, ExpressionType.SIMPLE, "[(1¦first|2¦all)] [mongo[db]] [document[s]] [(of|from|with) [query]] %mongoquery%");
    }

    private Expression<MongoQuery> exprMongoQuery;
    private boolean isSingle;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprMongoQuery = (Expression<MongoQuery>) exprs[0];
        isSingle = parseResult.mark == 0 || parseResult.mark == 1;
        return true;
    }

    @Override
    protected Document[] get(Event e) {
        MongoQuery mongoIterable = exprMongoQuery.getSingle(e);
        if (mongoIterable == null)
            return new Document[0];
        if (isSingle)
            return new Document[]{mongoIterable.getIterable().first()};
        ArrayList<Document> documents = new ArrayList<>();
        mongoIterable.getIterable().into(documents);
        return documents.toArray(new Document[0]);
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
        return (isSingle ? "first mongo document " : "all mongo documents ") + exprMongoQuery.toString(e, debug);
    }

}
