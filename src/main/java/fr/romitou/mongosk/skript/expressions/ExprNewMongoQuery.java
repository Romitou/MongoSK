package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
