package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.model.Filters;
import fr.romitou.mongosk.elements.MongoSKFilter;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprMongoJoinedFilters extends SimpleExpression<MongoSKFilter> {

    static {
        Skript.registerExpression(
            ExprMongoJoinedFilters.class,
            MongoSKFilter.class,
            ExpressionType.COMBINED,
            "mongo[(sk|db)] joined filters %mongoskfilter% (1¦and|2¦or|3¦nor) %mongoskfilter%"
        );
    }

    private Expression<MongoSKFilter> exprFirstFilter, exprSecondFilter;
    private int parseMark;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprFirstFilter = (Expression<MongoSKFilter>) exprs[0];
        exprSecondFilter = (Expression<MongoSKFilter>) exprs[1];
        this.parseMark = parseResult.mark;
        return true;
    }
    @Override
    protected MongoSKFilter[] get(@Nonnull Event e) {
        MongoSKFilter firstFilter = exprFirstFilter.getSingle(e);
        MongoSKFilter secondFilter = exprSecondFilter.getSingle(e);
        if (firstFilter == null || secondFilter == null)
            return new MongoSKFilter[0];
        Bson joinedFilter;
        switch (parseMark) {
            case 1:
                joinedFilter = Filters.and(firstFilter.getFilter(), secondFilter.getFilter());
                break;
            case 2:
                joinedFilter = Filters.or(firstFilter.getFilter(), secondFilter.getFilter());
                break;
            case 3:
                joinedFilter = Filters.nor(firstFilter.getFilter(), secondFilter.getFilter());
                break;
            default:
                return new MongoSKFilter[0];
        }
        return new MongoSKFilter[]{new MongoSKFilter(joinedFilter, toString(e, false))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKFilter> getReturnType() {
        return MongoSKFilter.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable final Event e, boolean debug) {
        return exprFirstFilter.toString(e, debug) + (parseMark == 1 ? "and " : parseMark == 2 ? "or " : "nor ") + exprSecondFilter.toString(e, debug);
    }
}
