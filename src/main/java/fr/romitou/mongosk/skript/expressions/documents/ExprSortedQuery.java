package fr.romitou.mongosk.skript.expressions.documents;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Sorts;
import fr.romitou.mongosk.objects.MongoQuery;
import org.bson.Document;
import org.bukkit.event.Event;

public class ExprSortedQuery extends SimpleExpression<MongoQuery> {

    static {
        Skript.registerExpression(ExprSortedQuery.class, MongoQuery.class, ExpressionType.SIMPLE, "[mongo[db]] (1¦asc[ending]|2¦desc[ending]) sorted %mongoquery% (on|for|by) field[s] %strings%");
    }

    private Expression<MongoQuery> exprMongoQuery;
    private Expression<String> exprFields;
    private boolean isAscending;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprMongoQuery = (Expression<MongoQuery>) exprs[0];
        exprFields = (Expression<String>) exprs[1];
        isAscending = matchedPattern == 1;
        return true;
    }

    @Override
    protected MongoQuery[] get(Event e) {
        MongoQuery mongoQuery = exprMongoQuery.getSingle(e);
        String[] fields = exprFields.getArray(e);
        if (mongoQuery == null || fields.length == 0)
            return new MongoQuery[0];
        MongoIterable<Document> iterable;
        if (isAscending)
            iterable = ((FindIterable<Document>) mongoQuery.getIterable()).sort(Sorts.ascending(fields));
        else
            iterable = ((FindIterable<Document>) mongoQuery.getIterable()).sort(Sorts.descending(fields));
        mongoQuery.setIterable(iterable);
        mongoQuery.setQuery(toString(e, false));
        return new MongoQuery[]{mongoQuery};
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
        return ((isAscending) ? "ascending" : "descending") + " sorted " + exprMongoQuery.toString(e, debug) + " by fields " + exprFields.toString(e, debug);
    }
}
