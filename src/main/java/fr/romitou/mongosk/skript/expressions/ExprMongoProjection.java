package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.model.Projections;
import fr.romitou.mongosk.elements.MongoSKProjection;
import org.bson.conversions.Bson;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

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
@Since("3.0.0")
public class ExprMongoProjection extends SimpleExpression<MongoSKProjection> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprMongoProjection.class, MongoSKProjection.class)
            .addPatterns("mongo[(db|sk)] projection (1:including|2:excluding) [the] field[s] %strings%")
            .priority(SyntaxInfo.SIMPLE)
            .build());
    }

    private Expression<String> exprFields;
    private boolean isIncluding;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(@NotNull Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprFields = (Expression<String>) exprs[0];
        isIncluding = parseResult.mark == 1;
        return true;
    }

    @Override
    protected MongoSKProjection[] get(@NotNull final Event e) {
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

    @NotNull
    @Override
    public Class<? extends MongoSKProjection> getReturnType() {
        return MongoSKProjection.class;
    }

    @NotNull
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
