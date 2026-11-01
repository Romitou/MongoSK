package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.UnparsedLiteral;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.model.Filters;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKFilter;
import fr.romitou.mongosk.skript.MongoSKComparator;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Arrays;

@Name("Mongo filter")
@Description({
    "Create custom Mongo filters to target specific data in your queries to collections.",
    "The comparators map to standard Skript comparators, providing intuitive filtering conditions."
})
@Examples({
    "set {_filter} to a new mongosk filter where field \"example\" is true",
    "set {_doc} to first mongo document with filter {_filter} of collection {mycollection}",
    "set {_docs::*} to all mongo documents with mongosk filter where \"example\" equals true of collection {mycollection}"
})
@Since("2.0.0")
public class ExprMongoFilter extends SimpleExpression<MongoSKFilter> {

    private static final String BASE_PATTERN = "[[a] new] mongo[(db|sk)] filter [where] field %string% ";

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprMongoFilter.class, MongoSKFilter.class)
            .addPatterns(Arrays.stream(MongoSKComparator.values())
                .map(comp -> BASE_PATTERN + comp.getPattern())
                .toArray(String[]::new))
            .build());
    }

    private Expression<String> exprField;
    private Expression<?> exprObject;
    private MongoSKComparator mongoSKComparator;

    private static Bson getFilter(MongoSKComparator comparator, String field, Object value) {
        return switch (comparator) {
            case EXISTS -> Filters.exists(field, true);
            case NOT_EXIST -> Filters.exists(field, false);
            case LESS_THAN -> Filters.lt(field, value);
            case LESS_THAN_OR_EQUAL -> Filters.lte(field, value);
            case GREATER_THAN -> Filters.gt(field, value);
            case GREATER_THAN_OR_EQUAL -> Filters.gte(field, value);
            case EQUALS -> Filters.eq(field, value);
            case NOT_EQUAL -> Filters.ne(field, value);
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprField = (Expression<String>) exprs[0];
        if (exprs.length > 1) {
            exprObject = exprs[1];
            if (exprObject instanceof UnparsedLiteral literal) {
                exprObject = literal.getConvertedExpression(Object.class);
                if (exprObject == null) {
                    return false;
                }
            }
        }
        this.mongoSKComparator = MongoSKComparator.values()[matchedPattern];
        return true;
    }

    @Override
    protected MongoSKFilter[] get(@NotNull final Event e) {
        String field = exprField.getSingle(e);
        if (field == null || mongoSKComparator == null)
            return new MongoSKFilter[0];
        if (exprObject == null) {
            return new MongoSKFilter[]{new MongoSKFilter(getFilter(mongoSKComparator, field, null), toString(e, false))};
        }
        Object value = exprObject.getSingle(e);
        if (value == null)
            return new MongoSKFilter[0];
        // Serialize input.
        value = MongoSKAdapter.serializeObject(value);
        return new MongoSKFilter[]{new MongoSKFilter(getFilter(mongoSKComparator, field, value), toString(e, false))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @NotNull
    public Class<? extends MongoSKFilter> getReturnType() {
        return MongoSKFilter.class;
    }

    @Override
    @NotNull
    public String toString(@Nullable Event e, boolean debug) {
        return "mongosk filter where field " + exprField.toString(e, debug) + " " + mongoSKComparator.toString() + " " + (exprObject != null ? exprObject.toString(e, debug) : "");
    }
}
