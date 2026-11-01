package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.registration.SyntaxRegistry;

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

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
            PropertyExpression.infoBuilder(ExprMongoQueryLimit.class, Number.class, "mongo[(db|sk)] limit", "mongoskqueries", false).build());
    }

    @NotNull
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
    public void change(@NotNull Event e, Object[] delta, @NotNull Changer.ChangeMode mode) {
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

    @NotNull
    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @NotNull
    @Override
    protected String getPropertyName() {
        return "mongo limit";
    }
}
