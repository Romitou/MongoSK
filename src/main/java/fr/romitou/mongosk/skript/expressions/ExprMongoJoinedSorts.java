package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.model.Sorts;
import fr.romitou.mongosk.elements.MongoSKSort;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ExprMongoJoinedSorts extends SimpleExpression<MongoSKSort> {

    static {
        Skript.registerExpression(
            ExprMongoJoinedSorts.class,
            MongoSKSort.class,
            ExpressionType.COMBINED,
            "mongo[(sk|db)] joined sorts %mongosksorts%"
        );
    }

    private Expression<MongoSKSort> exprSort;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprSort = (Expression<MongoSKSort>) exprs[0];
        return true;
    }

    @Override
    protected MongoSKSort[] get(@Nonnull Event e) {
        MongoSKSort[] sorts = exprSort.getArray(e);
        if (sorts.length == 0)
            return new MongoSKSort[0];
        Bson newSort = Sorts.orderBy(Arrays.stream(sorts).map(MongoSKSort::getSort).collect(Collectors.toList()));
        return new MongoSKSort[]{new MongoSKSort(newSort, toString(e, false))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKSort> getReturnType() {
        return MongoSKSort.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable final Event e, boolean debug) {
        return "mongo joined sorts " + exprSort.toString(e, debug);
    }
}
