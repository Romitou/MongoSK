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
import fr.romitou.mongosk.elements.MongoSKSort;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Mongo query sort")
@Description({
    "Defines the sorting criteria to be applied to the query.",
    "This is optional and helps in ordering the returned documents."
})
@Examples({
    "set {_query} to new mongosk query",
    "set mongo sort of {_query} to mongo ascending sort by field \"test\""
})
@Since("2.0.0")
public class ExprMongoQuerySort extends SimplePropertyExpression<MongoSKQuery, MongoSKSort> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
            PropertyExpression.infoBuilder(ExprMongoQuerySort.class, MongoSKSort.class, "mongo[(db|sk)] sort", "mongoskqueries", false).build());
    }

    @NotNull
    @Override
    public MongoSKSort convert(MongoSKQuery query) {
        return query.getMongoSKSort();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(MongoSKSort.class);
            default:
                return null;
        }
    }

    @Override
    public void change(@NotNull Event e, Object[] delta, @NotNull Changer.ChangeMode mode) {
        MongoSKQuery mongoSKQuery = getExpr().getSingle(e);
        if (mongoSKQuery == null || delta == null)
            return;
        if (!(delta[0] instanceof MongoSKSort))
            return;
        switch (mode) {
            case SET:
                mongoSKQuery.setMongoSKSort((MongoSKSort) delta[0]);
                break;
            case DELETE:
                mongoSKQuery.setMongoSKSort(null);
                break;
        }
    }

    @NotNull
    @Override
    public Class<? extends MongoSKSort> getReturnType() {
        return MongoSKSort.class;
    }

    @NotNull
    @Override
    protected String getPropertyName() {
        return "mongo sort";
    }
}
