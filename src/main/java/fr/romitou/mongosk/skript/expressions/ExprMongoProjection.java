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
import com.mongodb.client.model.Projections;
import fr.romitou.mongosk.elements.MongoSKProjection;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

@Name("Mongo projection")
@Description({
    "Creates a projection for Mongo queries, allowing you to include or exclude specific fields from the returned documents.",
    "This is useful for optimizing query payloads by only requesting necessary data."
})
@Examples({
    "set {_proj} to mongo projection including fields \"field1\" and \"field2\"",
    "set {_proj} to mongo projection excluding fields \"field3\" and \"field4\""
})
@Since("2.4.2")
public class ExprMongoProjection extends SimpleExpression<MongoSKProjection> {

    static {
        Skript.registerExpression(
            ExprMongoProjection.class,
            MongoSKProjection.class,
            ExpressionType.COMBINED,
            "mongo[(db|sk)] projection (1¦including|2¦excluding) [the] field[s] %strings%"
        );
    }

    private Expression<String> exprFields;
    private boolean isIncluding;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(@Nonnull Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprFields = (Expression<String>) exprs[0];
        isIncluding = parseResult.mark == 1;
        return true;
    }

    @Override
    protected MongoSKProjection[] get(@Nonnull final Event e) {
        String[] fields = exprFields.getArray(e);
        if (fields == null || fields.length == 0) return new MongoSKProjection[0];

        Bson projection;
        if (isIncluding) {
            projection = Projections.include(Arrays.asList(fields));
        } else {
            projection = Projections.exclude(Arrays.asList(fields));
        }

        String display = "projection " + (isIncluding ? "including" : "excluding") + " fields " + Arrays.toString(fields);
        return new MongoSKProjection[]{new MongoSKProjection(projection, display)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKProjection> getReturnType() {
        return MongoSKProjection.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable final Event e, final boolean debug) {
        if (e != null) {
            String[] fields = exprFields.getArray(e);
            if (fields != null) {
                return "mongo projection " + (isIncluding ? "including" : "excluding") + " fields " + Arrays.toString(fields);
            }
        }
        return "mongo projection " + (isIncluding ? "including" : "excluding") + " fields";
    }
}
