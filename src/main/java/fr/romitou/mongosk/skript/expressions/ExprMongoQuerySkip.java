package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

public class ExprMongoQuerySkip extends SimplePropertyExpression<MongoSKQuery, Integer> {

    static {
        register(
            ExprMongoQuerySkip.class,
            Integer.class,
            "mongo[(db|sk)] skip",
            "mongoskqueries"
        );
    }

    @Nonnull
    @Override
    public Integer convert(MongoSKQuery query) {
        return query.getLimit();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(Integer.class);
            default:
                return new Class[0];
        }
    }

    @Override
    public void change(@Nonnull Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        MongoSKQuery mongoSKQuery = getExpr().getSingle(e);
        if (mongoSKQuery == null || delta == null)
            return;
        if (!(delta[0] instanceof Integer))
            return;
        switch (mode) {
            case SET:
                mongoSKQuery.setSkip((Integer) delta[0]);
                break;
            case DELETE:
                mongoSKQuery.setSkip(null);
                break;
        }
    }

    @Nonnull
    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo skip";
    }
}
