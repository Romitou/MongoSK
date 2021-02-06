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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import fr.romitou.mongosk.objects.MongoFilter;
import fr.romitou.mongosk.objects.MongoQuery;
import org.bson.Document;
import org.bukkit.event.Event;

@Name("Mongo Document")
@Description("This expression allows you to retrieve a document according to a specific value from a specific collection.")
@Examples({"set {_client} to client named \"default\"",
        "set {_database} to database named \"mongosk\" with {_client}",
        "set {_collection} to collection named \"example\" from {_database}",
        "set {_document} to first document where \"points\" is \"10\" in {_collection}"})
@Since("1.0.0")
public class ExprDocumentQuery extends SimpleExpression<MongoQuery> {

    static {
        Skript.registerExpression(ExprDocumentQuery.class, MongoQuery.class, ExpressionType.SIMPLE, "[mongo[db]] document[s] [(1¦[with filter] %-mongofilter%)] (of|in|from) %mongocollection%");
    }

    private Expression<MongoFilter> exprMongoFilter;
    private Expression<MongoCollection> exprCollection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (parseResult.mark == 1)
            exprMongoFilter = (Expression<MongoFilter>) exprs[0];
        exprCollection = (Expression<MongoCollection>) exprs[1];
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MongoQuery[] get(Event e) {
        MongoCollection collection = exprCollection.getSingle(e);
        if (collection == null)
            return new MongoQuery[0];
        MongoIterable<Document> iterable;
        if (exprMongoFilter != null) {
            MongoFilter filter = exprMongoFilter.getSingle(e);
            if (filter == null || filter.get() == null)
                return new MongoQuery[0];
            iterable = collection.find(filter.get());
        } else {
            iterable = collection.find();
        }
        return new MongoQuery[]{new MongoQuery(iterable, toString(e, false))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MongoQuery> getReturnType() {
        return MongoQuery.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return ((exprMongoFilter != null) ? exprMongoFilter.toString(e, debug) : "") + " in " + exprCollection.toString(e, debug);
    }

}
