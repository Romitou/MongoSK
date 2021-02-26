package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

public class ExprMongoQueryCollection extends SimplePropertyExpression<MongoSKQuery, MongoSKCollection> {

    static {
        register(
            ExprMongoQueryCollection.class,
            MongoSKCollection.class,
            "mongo[(db|sk)] collection",
            "mongoskqueries"
        );
    }

    @Nonnull
    @Override
    public MongoSKCollection convert(MongoSKQuery query) {
        return query.getMongoSKCollection();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(MongoSKCollection.class);
            default:
                return new Class[0];
        }
    }

    @Override
    public void change(@Nonnull Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        MongoSKQuery mongoSKQuery = getExpr().getSingle(e);
        if (mongoSKQuery == null || delta == null)
            return;
        Object mongoSKCollection = delta[0];
        if (!(mongoSKCollection instanceof MongoSKCollection))
            return;
        mongoSKQuery.setMongoSKCollection((MongoSKCollection) delta[0]);
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKCollection> getReturnType() {
        return MongoSKCollection.class;
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo collection";
    }

}
