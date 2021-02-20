package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.model.Filters;
import fr.romitou.mongosk.elements.MongoSKFilter;
import fr.romitou.mongosk.skript.MongoSKComparator;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class ExprMongoFilter extends SimpleExpression<MongoSKFilter> {

    private static final String BASE_PATTERN = "[a [new]] mongo[(db|sk)] filter [where] field %string% ";

    static {
        Skript.registerExpression(
            ExprMongoFilter.class,
            MongoSKFilter.class,
            ExpressionType.COMBINED,
            Arrays.stream(MongoSKComparator.values())
                .map(comp -> BASE_PATTERN + comp.getPattern())
                .toArray(String[]::new)
        );
    }

    private Expression<String> exprField;
    private Expression<Object> exprObject;
    private MongoSKComparator mongoSKComparator;

    private static Bson getFilter(MongoSKComparator comparator, String field, Object value) {
        switch (comparator) {
            case EXISTS:
                return Filters.exists(field, true);
            case NOT_EXIST:
                return Filters.exists(field, false);
            case LESS_THAN:
                return Filters.lt(field, value);
            case LESS_THAN_OR_EQUAL:
                return Filters.lte(field, value);
            case GREATER_THAN:
                return Filters.gt(field, value);
            case GREATER_THAN_OR_EQUAL:
                return Filters.gte(field, value);
            case EQUALS:
                return Filters.eq(field, value);
            case NOT_EQUAL:
                return Filters.ne(field, value);
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprField = (Expression<String>) exprs[0];
        exprObject = (Expression<Object>) exprs[1];
        this.mongoSKComparator = MongoSKComparator.values()[matchedPattern];
        return true;
    }

    @Override
    protected MongoSKFilter[] get(@Nonnull final Event e) {
        String field = exprField.getSingle(e);
        Object value = exprObject.getSingle(e);
        if (field == null)
            return new MongoSKFilter[0];
        if (mongoSKComparator == null
            || value == null
            && !(mongoSKComparator == MongoSKComparator.EQUALS
            || mongoSKComparator == MongoSKComparator.NOT_EQUAL))
            return new MongoSKFilter[0];
        Bson filter = getFilter(mongoSKComparator, field, value);
        if (filter == null)
            return new MongoSKFilter[0];
        MongoSKFilter mongoSKFilter = new MongoSKFilter(filter, toString(e, false));
        return new MongoSKFilter[]{mongoSKFilter};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @Nonnull
    public Class<? extends MongoSKFilter> getReturnType() {
        return MongoSKFilter.class;
    }

    @Override
    @Nonnull
    public String toString(@Nullable Event e, boolean debug) {
        return "mongosk filter where field " + exprField.toString(e, debug) + " " + mongoSKComparator.toString() + " " + (exprObject != null ? exprObject.toString(e, debug) : "");
    }
}
