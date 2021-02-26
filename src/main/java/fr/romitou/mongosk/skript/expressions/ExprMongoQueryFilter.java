package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKFilter;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

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
                return new Class[0];
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
