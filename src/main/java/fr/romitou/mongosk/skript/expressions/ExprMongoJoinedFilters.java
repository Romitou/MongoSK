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
import com.mongodb.client.model.Filters;
import fr.romitou.mongosk.elements.MongoSKFilter;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

@Name("Mongo joined filters")
@Description("Thanks to this expression, you will be able to combine two filters to refine your queries. " +
    "Several types of filter comparison are possible, and, or, and nor.")
@Examples({"set {_filter1} to mongosk filter where field \"test\" is true",
    "set {_filter2} to mongosk filter where field \"coins\" is higher than or equals to 10",
    "set {_filter} to mongo joined filters {_filter1} or {_filter2}"})
@Since("2.0.0")
public class ExprMongoJoinedFilters extends SimpleExpression<MongoSKFilter> {

    static {
        Skript.registerExpression(
            ExprMongoJoinedFilters.class,
            MongoSKFilter.class,
            ExpressionType.COMBINED,
            "mongo[(sk|db)] joined filters %mongoskfilter% (1¦and|2¦or|3¦nor) %mongoskfilter%",
            "mongo[(sk|db)] joined filters %mongoskfilters% with (1¦and|2¦or|3¦nor) [join] mode"
        );
    }

    private Expression<MongoSKFilter> exprFirstFilter, exprSecondFilter;
    private int parseMark;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprFirstFilter = (Expression<MongoSKFilter>) exprs[0];
        if (matchedPattern == 0)
            exprSecondFilter = (Expression<MongoSKFilter>) exprs[1];
        this.parseMark = parseResult.mark;
        return true;
    }

    @Override
    protected MongoSKFilter[] get(@Nonnull Event e) {
        Bson[] filters;
        if (exprSecondFilter == null) {
            MongoSKFilter[] mongoSKFilters = exprFirstFilter.getArray(e);
            if (mongoSKFilters.length == 0)
                return new MongoSKFilter[0];
            filters = Arrays.stream(mongoSKFilters)
                .map(MongoSKFilter::getFilter)
                .toArray(Bson[]::new);
        } else {
            MongoSKFilter firstFilter = exprFirstFilter.getSingle(e);
            MongoSKFilter secondFilter = exprSecondFilter.getSingle(e);
            if (firstFilter == null || secondFilter == null)
                return new MongoSKFilter[0];
            filters = new Bson[]{firstFilter.getFilter(), secondFilter.getFilter()};
        }
        Bson joinedFilter;
        switch (parseMark) {
            case 1:
                joinedFilter = Filters.and(filters);
                break;
            case 2:
                joinedFilter = Filters.or(filters);
                break;
            case 3:
                joinedFilter = Filters.nor(filters);
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
        String joinMode = parseMark == 1 ? " and " : parseMark == 2 ? " or " : " nor ";
        if (exprSecondFilter == null)
            return exprFirstFilter.toString(e, debug) + " with" + joinMode + "join mode";
        return exprFirstFilter.toString(e, debug) + joinMode + exprSecondFilter.toString(e, debug);
    }
}
