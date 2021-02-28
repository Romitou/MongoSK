package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("New Mongo query")
@Description("This expression will allow you to create a new Mongo query, " +
    "which you can refine with the many expressions available.")
@Examples({"set {_query} to new mongosk query",
    "set mongo collection of {_query} to {mycollection}",
    "set {_query}'s mongo comment to \"player query\"",
    "set {_query}'s mongo disk usage to true",
    "set {_query}'s mongo filter to new mongosk filter where field \"coins\" is 10",
    "set mongo skip of {_query} to 12",
    "set mongo sort of {_query} to mongo ascending sort by field \"test\""})
@Since("2.0.0")
public class ExprNewMongoQuery extends SimpleExpression<MongoSKQuery> {

    static {
        Skript.registerExpression(
            ExprNewMongoQuery.class,
            MongoSKQuery.class,
            ExpressionType.SIMPLE,
            "[[a] new] mongo[(sk|db)] [empty] (query|request)"
        );
    }

    @Override
    public boolean init(@Nonnull Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected MongoSKQuery[] get(@Nonnull Event e) {
        return new MongoSKQuery[]{new MongoSKQuery()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKQuery> getReturnType() {
        return MongoSKQuery.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable final Event e, boolean debug) {
        return "a new mongosk query";
    }
}
