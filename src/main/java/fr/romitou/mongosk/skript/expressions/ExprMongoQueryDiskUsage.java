package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

@Name("Mongo query disk usage")
@Description("Enables writing to temporary files on the server. " +
    "When set to true, the server can write temporary data to disk while executing the operation. This is optional.")
@Examples({"set {_query} to new mongosk query",
    "set {_query}'s mongo disk usage to true"})
@Since("2.0.0")
public class ExprMongoQueryDiskUsage extends SimplePropertyExpression<MongoSKQuery, Boolean> {

    static {
        register(
            ExprMongoQueryDiskUsage.class,
            Boolean.class,
            "mongo[(db|sk)] disk usage",
            "mongoskqueries"
        );
    }

    @Nonnull
    @Override
    public Boolean convert(MongoSKQuery query) {
        return query.getDiskUsage();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(Boolean.class);
            default:
                return null;
        }
    }

    @Override
    public void change(@Nonnull Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        MongoSKQuery mongoSKQuery = getExpr().getSingle(e);
        if (mongoSKQuery == null || delta == null)
            return;
        if (!(delta[0] instanceof Boolean))
            return;
        switch (mode) {
            case SET:
                mongoSKQuery.setDiskUsage((Boolean) delta[0]);
                break;
            case DELETE:
                mongoSKQuery.setDiskUsage(null);
                break;
        }
    }

    @Nonnull
    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo disk usage";
    }
}
