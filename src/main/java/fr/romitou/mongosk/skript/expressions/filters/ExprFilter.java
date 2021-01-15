package fr.romitou.mongosk.skript.expressions.filters;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.UnparsedLiteral;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.model.Filters;
import fr.romitou.mongosk.objects.MongoFilter;
import org.bukkit.event.Event;

@Name("Mongo Filter")
@Description("This expression will be used to create a filter that you can use when querying your database.")
@Examples({"set {_filter} to new mongo filter where \"points\" is greater or equal to 100",
        "set {_players::*} to all documents with filter {_filter} in {collection}",
        "set {_players::*} to all documents where \"points\" is greater or equal to 100 in {collection}"
})
@Since("1.1.2")
public class ExprFilter extends SimpleExpression<MongoFilter> {

    private static final String PATTERN_PREFIX = "[[a] [new] [mongo[db]] [filter]] where %string% ";

    static {
        Skript.registerExpression(ExprFilter.class, MongoFilter.class, ExpressionType.SIMPLE,
                PATTERN_PREFIX + "is ((greater|more|higher|bigger|larger|above) [than] or (equal to|the same as)|\\>=) %number%",
                PATTERN_PREFIX + "is (((greater|more|higher|bigger|larger) than|above)|\\>) %number%",
                PATTERN_PREFIX + "is ((less|smaller|below) [than] or (equal to|the same as)|\\<=) %number%",
                PATTERN_PREFIX + "is (((less|smaller) than|below)|\\<) %number%",
                PATTERN_PREFIX + "is ((not|neither)|isn't|!=) [equal to] %object%",
                PATTERN_PREFIX + "(is|equals|=) %object%"
        );
    }

    private Expression<String> exprKey;
    private Expression<Object> exprValue;
    private int matchedPattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprKey = (Expression<String>) exprs[0];
        exprValue = (Expression<Object>) exprs[1];
        this.matchedPattern = matchedPattern;
        return !(exprValue instanceof UnparsedLiteral); // Don't support UnparsedLiteral-s.
    }

    @Override
    protected MongoFilter[] get(Event e) {
        MongoFilter mongoFilter;
        String key = exprKey.getSingle(e);
        Object value = exprValue.getSingle(e);
        if (key == null || value == null)
            return new MongoFilter[0];
        switch (matchedPattern) {
            case 0:
                mongoFilter = new MongoFilter(Filters.gte(key, value), toString(e, false));
                break;
            case 1:
                mongoFilter = new MongoFilter(Filters.gt(key, value), toString(e, false));
                break;
            case 2:
                mongoFilter = new MongoFilter(Filters.lte(key, value), toString(e, false));
                break;
            case 3:
                mongoFilter = new MongoFilter(Filters.lt(key, value), toString(e, false));
                break;
            case 4:
                mongoFilter = new MongoFilter(Filters.ne(key, value), toString(e, false));
                break;
            case 5:
                mongoFilter = new MongoFilter(Filters.eq(key, value), toString(e, false));
                break;
            default:
                return new MongoFilter[0];
        }
        return new MongoFilter[]{mongoFilter};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MongoFilter> getReturnType() {
        return MongoFilter.class;
    }

    public String getPattern() {
        switch (this.matchedPattern) {
            case 1:
                return "is greater than or equal to";
            case 2:
                return "is greater than";
            case 3:
                return "is less than or equal to";
            case 4:
                return "is less than";
            case 5:
                return "is not equal to";
            case 6:
                return "equals to";
            default:
                return null;
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "where " + exprKey.toString(e, debug) + " " + getPattern() + " " + exprValue.toString(e, debug);
    }
}
