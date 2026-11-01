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

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(org.skriptlang.skript.registration.SyntaxRegistry.EXPRESSION, org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder(ExprMongoQueryComment.class, String.class).addPatterns(ch.njol.skript.expressions.base.PropertyExpression.getPatterns("mongo[(db|sk)] comment",
            "mongoskqueries")).priority(ExpressionType.PROPERTY.priority()).build());
    }

    @Nonnull
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
    public void change(@Nonnull Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
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

    @Nonnull
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo comment";
    }
}
