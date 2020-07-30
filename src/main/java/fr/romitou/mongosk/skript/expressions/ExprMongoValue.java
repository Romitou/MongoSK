package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.utils.MongoManager;
import org.bukkit.event.Event;
import com.mongodb.client.model.Filters;

/*
* This expression is not complete, but actually functional to GET data.
* TODO: allow to DELETE and SET a document.
*/
public class ExprMongoValue extends SimpleExpression<String> {

    private Expression<String> query, whereQuery, whereValue, collection, database;

    static {
        Skript.registerExpression(
                ExprMongoValue.class,
                String.class,
                ExpressionType.SIMPLE,
                "mongo[db] value %string% where %string% is %string% in collection %string% and database %string%"
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        query = (Expression<String>) exprs[0];
        whereQuery = (Expression<String>) exprs[1];
        whereValue = (Expression<String>) exprs[2];
        collection = (Expression<String>) exprs[3];
        database = (Expression<String>) exprs[4];
        return true;
    }

    @Override
    protected String[] get(Event e) {
        if (!MongoManager.isConnected()) {
            Skript.error("[MongoSK] You cannot make a query while you're not connected to a host!");
            return null;
        }
        final String document;
        document = MongoManager
                .getClient()
                .getDatabase(database.getSingle(e))
                .getCollection(collection.getSingle(e))
                .find(Filters.eq(whereQuery.getSingle(e), whereValue.getSingle(e)))
                .first()
                .getString(query.getSingle(e));
        return new String[] { document };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongodb value " + query.toString(e, debug) + " where " + whereQuery.toString(e, debug) + " is " + whereValue.toString(e, debug) + " in collection " + collection.toString(e, debug) + " and database " + database.toString(e, debug);
    }

}
