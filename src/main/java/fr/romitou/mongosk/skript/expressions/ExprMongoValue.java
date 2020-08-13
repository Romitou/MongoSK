package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.model.Updates;
import fr.romitou.mongosk.utils.MongoManager;
import org.bukkit.event.Event;
import com.mongodb.client.model.Filters;

/*
* This expression is not complete, but actually functional to GET and SET data.
* TODO: Take into account the types of values.
*  Example: return an integer if the value is an integer.
*/

@Name("Mongo Value")
@Examples("set {_points} to mongo value \"points\" where \"player\" is \"Romitou\" in collection \"playerdata\" and database \"MongoSK\"")
@Since("1.0")

public class ExprMongoValue extends SimpleExpression<Object> {

    private Expression<String> query, whereQuery, whereValue, collection, database;

    static {
        Skript.registerExpression(
                ExprMongoValue.class,
                Object.class,
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
    protected Object[] get(Event e) {
        if (!MongoManager.isConnected()) {
            MongoManager.queryError();
            return null;
        }

        Object document;
        try {
            document = MongoManager
                    .getClient()
                    .getDatabase(database.getSingle(e))
                    .getCollection(collection.getSingle(e))
                    .find(Filters.eq(whereQuery.getSingle(e), whereValue.getSingle(e)))
                    .first()
                    .get(query.getSingle(e));
        } catch (IllegalArgumentException | NullPointerException ex) {
            MongoManager.queryError(ex);
            return null;
        }

        return new Object[] { document };
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode != Changer.ChangeMode.SET) return null;
        return new Class[] { Object.class };
    }

    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            if (!MongoManager.isConnected()) {
                MongoManager.queryError();
                return;
            }
            try {
                MongoManager
                        .getClient()
                        .getDatabase(database.getSingle(e))
                        .getCollection(collection.getSingle(e))
                        .updateOne(
                                Filters.eq(whereQuery.getSingle(e), whereValue.getSingle(e)),
                                Updates.set(query.getSingle(e), delta[0])
                        );
            } catch (IllegalArgumentException | NullPointerException ex) {
                MongoManager.queryError(ex);
            }
        }

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
