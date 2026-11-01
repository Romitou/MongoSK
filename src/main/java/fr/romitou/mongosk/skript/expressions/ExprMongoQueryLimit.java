package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

@Name("Mongo query filter")
@Description({
    "Sets the maximum number of documents to be returned by the query.",
    "This is optional and helps prevent loading too many documents into memory."
})
@Examples({
    "set {_query} to new mongosk query",
    "set mongo limit of {_query} to 50"
})
@Since("2.0.0")
public class ExprMongoQueryLimit extends SimplePropertyExpression<MongoSKQuery, Number> {

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(org.skriptlang.skript.registration.SyntaxRegistry.EXPRESSION, org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder(ExprMongoQueryLimit.class, Number.class).addPatterns(ch.njol.skript.expressions.base.PropertyExpression.getPatterns("mongo[(db|sk)] limit",
            "mongoskqueries")).priority(ExpressionType.PROPERTY.priority()).build());
    }

    @Nonnull
    @Override
    public Number convert(MongoSKQuery query) {
        return query.getLimit();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(Number.class);
            default:
                return null;
        }
    }

    @Override
    public void change(@Nonnull Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        MongoSKQuery mongoSKQuery = getExpr().getSingle(e);
        if (mongoSKQuery == null || delta == null)
            return;
        if (!(delta[0] instanceof Number))
            return;
        switch (mode) {
            case SET:
                mongoSKQuery.setLimit(((Number) delta[0]).intValue());
                break;
            case DELETE:
                mongoSKQuery.setLimit(null);
                break;
        }
    }

    @Nonnull
    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo limit";
    }
}
