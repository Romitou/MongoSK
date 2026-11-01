package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKQuery;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("New Mongo query")
@Description({
    "This expression allows you to create a new Mongo query, which you can refine using the many available expressions.",
    "Queries encapsulate collection targets, filters, limits, sorting, and other configurations."
})
@Examples({
    "set {_query} to new mongosk query",
    "set mongo collection of {_query} to {mycollection}",
    "set {_query}'s mongo comment to \"player query\"",
    "set {_query}'s mongo disk usage to true",
    "set {_query}'s mongo filter to new mongosk filter where field \"coins\" is 10",
    "set mongo skip of {_query} to 12",
    "set mongo sort of {_query} to mongo ascending sort by field \"test\""
})
@Since("2.0.0")
public class ExprNewMongoQuery extends SimpleExpression<MongoSKQuery> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprNewMongoQuery.class, MongoSKQuery.class)
            .addPatterns("[[a] new] mongo[(sk|db)] [empty] (query|request)")
            .priority(SyntaxInfo.SIMPLE)
            .build());
    }

    @Override
    public boolean init(@NotNull Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected MongoSKQuery[] get(@NotNull Event e) {
        return new MongoSKQuery[]{new MongoSKQuery()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @NotNull
    @Override
    public Class<? extends MongoSKQuery> getReturnType() {
        return MongoSKQuery.class;
    }

    @NotNull
    @Override
    public String toString(@Nullable final Event e, boolean debug) {
        return "a new mongosk query";
    }
}
