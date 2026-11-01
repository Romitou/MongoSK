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

@Name("Mongo query skip")
@Description({
    "Sets the number of documents to be skipped during the query execution.",
    "This is optional and commonly used for pagination."
})
@Examples({
    "set {_query} to new mongosk query",
    "set mongo skip of {_query} to 12"
})
@Since("2.0.0")
public class ExprMongoQuerySkip extends SimplePropertyExpression<MongoSKQuery, Number> {

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(org.skriptlang.skript.registration.SyntaxRegistry.EXPRESSION, org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder(ExprMongoQuerySkip.class, Number.class).addPatterns(ch.njol.skript.expressions.base.PropertyExpression.getPatterns("mongo[(db|sk)] skip",
            "mongoskqueries")).priority(ExpressionType.PROPERTY.priority()).build());
    }

    @Nonnull
    @Override
    public Number convert(MongoSKQuery query) {
        return query.getSkip();
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
                mongoSKQuery.setSkip(((Number) delta[0]).intValue());
                break;
            case DELETE:
                mongoSKQuery.setSkip(null);
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
        return "mongo skip";
    }
}
