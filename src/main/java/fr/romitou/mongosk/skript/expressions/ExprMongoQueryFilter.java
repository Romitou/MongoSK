package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKFilter;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

@Name("Mongo query filter")
@Description("Allows you to set a filter associated with the query. This is optional for find queries, but mandatory " +
    "for delete or replace queries!")
@Examples({"set {_query} to new mongosk query",
    "set {_query}'s mongo filter to new mongosk filter where field \"coins\" is 10"})
@Since("2.0.0")
public class ExprMongoQueryFilter extends SimplePropertyExpression<MongoSKQuery, MongoSKFilter> {

    static {
        register(
            ExprMongoQueryFilter.class,
            MongoSKFilter.class,
            "mongo[(db|sk)] filter",
            "mongoskqueries"
        );
    }

    @Nonnull
    @Override
    public MongoSKFilter convert(MongoSKQuery query) {
        return query.getMongoSKFilter();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(MongoSKFilter.class);
            default:
                return null;
        }
    }

    @Override
    public void change(@Nonnull Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        MongoSKQuery mongoSKQuery = getExpr().getSingle(e);
        if (mongoSKQuery == null || delta == null)
            return;
        if (!(delta[0] instanceof MongoSKFilter))
            return;
        switch (mode) {
            case SET:
                mongoSKQuery.setMongoSKFilter((MongoSKFilter) delta[0]);
                break;
            case DELETE:
                mongoSKQuery.setMongoSKFilter(null);
                break;
        }
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKFilter> getReturnType() {
        return MongoSKFilter.class;
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo filter";
    }
}
