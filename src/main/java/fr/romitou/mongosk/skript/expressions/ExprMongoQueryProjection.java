package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKProjection;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

@Name("Mongo query projection")
@Description({
    "Defines the projection to be applied to the query, determining which fields are returned.",
    "This is optional."
})
@Examples({
    "set {_query} to new mongosk query",
    "set mongo projection of {_query} to mongo projection excluding fields \"_id\" and \"bulky_data\""
})
@Since("2.4.2")
public class ExprMongoQueryProjection extends SimplePropertyExpression<MongoSKQuery, MongoSKProjection> {

    static {
        register(
            ExprMongoQueryProjection.class,
            MongoSKProjection.class,
            "mongo[(db|sk)] projection",
            "mongoskqueries"
        );
    }

    @Nonnull
    @Override
    public MongoSKProjection convert(MongoSKQuery query) {
        return query.getMongoSKProjection();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(MongoSKProjection.class);
            default:
                return null;
        }
    }

    @Override
    public void change(@Nonnull Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        MongoSKQuery mongoSKQuery = getExpr().getSingle(e);
        if (mongoSKQuery == null)
            return;

        if (mode == Changer.ChangeMode.DELETE) {
            mongoSKQuery.setMongoSKProjection(null);
            return;
        }

        if (delta != null && delta.length > 0 && delta[0] instanceof MongoSKProjection) {
            mongoSKQuery.setMongoSKProjection((MongoSKProjection) delta[0]);
        }
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKProjection> getReturnType() {
        return MongoSKProjection.class;
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo projection";
    }
}
