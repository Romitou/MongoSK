package fr.romitou.mongosk.skript.expressions;

import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import ch.njol.skript.lang.ExpressionType;
import org.skriptlang.skript.registration.SyntaxRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.model.Sorts;
import fr.romitou.mongosk.elements.MongoSKSort;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("Mongo sort")
@Description({
    "Creates a sorting rule for query results based on one or more fields.",
    "You can specify ascending or descending order."
})
@Examples({
    "set {_sort} to mongo ascending sort by field \"score\"",
    "set {_sort} to mongo descending sort by fields \"level\" and \"experience\""
})
@Since("2.0.0")
public class ExprMongoSort extends SimpleExpression<MongoSKSort> {

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(SyntaxRegistry.EXPRESSION, DefaultSyntaxInfos.Expression.builder(ExprMongoSort.class, MongoSKSort.class).addPatterns("[[a] new] mongo[(db|sk)] (1¦asc[ending]|2¦desc[ending]) sort[ing] [by] field[s] %strings%").priority(ExpressionType.SIMPLE.priority()).build());
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
