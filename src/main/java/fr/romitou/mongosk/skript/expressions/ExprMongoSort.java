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

public class ExprMongoSort extends SimpleExpression<MongoSKSort> {

    static {
        Skript.registerExpression(
            ExprMongoSort.class,
            MongoSKSort.class,
            ExpressionType.SIMPLE,
            "[a [new]] mongo[(db|sk)] (1¦asc[ending]|2¦desc[ending]) sort[ing] [by] field[s] %strings%"
        );
    }

    private Expression<String> exprFields;
    private boolean isAscending;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprFields = (Expression<String>) exprs[0];
        isAscending = parseResult.mark == 1;
        return true;
    }

    @Override
    protected MongoSKSort[] get(@Nonnull final Event e) {
        String[] fields = exprFields.getArray(e);
        if (fields.length == 0)
            return new MongoSKSort[0];
        Bson sort;
        if (isAscending)
            sort = Sorts.ascending(fields);
        else
            sort = Sorts.descending(fields);
        return new MongoSKSort[]{new MongoSKSort(sort, toString(e, false))};
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
    public String toString(@Nullable Event e, boolean debug) {
        return "mongosk " + (isAscending ? "ascending" : "descending") + " sort by fields " + exprFields.toString(e, debug);
    }
}
