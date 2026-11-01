package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Mongo query collection")
@Description({
    "Defines the collection in which the query will run.",
    "This is required for the query to know where to execute."
})
@Examples({
    "set {_query} to new mongosk query",
    "set mongo collection of {_query} to {mycollection}"
})
@Since("2.0.0")
public class ExprMongoQueryCollection extends SimplePropertyExpression<MongoSKQuery, MongoSKCollection> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
            PropertyExpression.infoBuilder(ExprMongoQueryCollection.class, MongoSKCollection.class, "mongo[(db|sk)] collection", "mongoskqueries", false).build());
    }

    @NotNull
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
                return null;
        }
    }

    @Override
    public void change(@NotNull Event e, Object[] delta, @NotNull Changer.ChangeMode mode) {
        MongoSKQuery mongoSKQuery = getExpr().getSingle(e);
        if (mongoSKQuery == null || delta == null)
            return;
        Object mongoSKCollection = delta[0];
        if (!(mongoSKCollection instanceof MongoSKCollection))
            return;
        mongoSKQuery.setMongoSKCollection((MongoSKCollection) delta[0]);
    }

    @NotNull
    @Override
    public Class<? extends MongoSKCollection> getReturnType() {
        return MongoSKCollection.class;
    }

    @NotNull
    @Override
    protected String getPropertyName() {
        return "mongo collection";
    }

}
