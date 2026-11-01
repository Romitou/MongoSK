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

@Name("Mongo query comment")
@Description({
    "Defines the comment of the request, aiding in query monitoring on your MongoDB server.",
    "This is optional and helps administrators trace query origins."
})
@Examples({
    "set {_query} to new mongosk query",
    "set {_query}'s mongo comment to \"player data fetch query\""
})
@Since("2.0.0")
public class ExprMongoQueryComment extends SimplePropertyExpression<MongoSKQuery, String> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
            PropertyExpression.infoBuilder(ExprMongoQueryComment.class, String.class, "mongo[(db|sk)] comment", "mongoskqueries", false).build());
    }

    @NotNull
    @Override
    public String convert(MongoSKQuery query) {
        return query.getComment();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(String.class);
            default:
                return null;
        }
    }

    @Override
    public void change(@NotNull Event e, Object[] delta, @NotNull Changer.ChangeMode mode) {
        MongoSKQuery mongoSKQuery = getExpr().getSingle(e);
        if (mongoSKQuery == null || delta == null)
            return;
        if (!(delta[0] instanceof String))
            return;
        switch (mode) {
            case SET:
                mongoSKQuery.setComment((String) delta[0]);
                break;
            case DELETE:
                mongoSKQuery.setComment(null);
                break;
        }
    }

    @NotNull
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @NotNull
    @Override
    protected String getPropertyName() {
        return "mongo comment";
    }
}
