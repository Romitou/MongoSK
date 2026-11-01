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

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
            PropertyExpression.infoBuilder(ExprMongoQuerySkip.class, Number.class, "mongo[(db|sk)] skip", "mongoskqueries", false).build());
    }

    @NotNull
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
    public void change(@NotNull Event e, Object[] delta, @NotNull Changer.ChangeMode mode) {
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

    @NotNull
    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @NotNull
    @Override
    protected String getPropertyName() {
        return "mongo skip";
    }
}
