package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.SubscriberHelpers;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Last Mongo error")
@Description({
    "Returns the error message of the last Mongo request, or nothing if it succeeded.",
    "It allows to distinguish a request that failed from a request that returned no result.",
    "It is reset each time a new request is executed."
})
@Examples({
    "set {_docs::*} to all mongo documents of collection {mycollection}",
    "if last mongo error is set:",
    "\tbroadcast \"The request failed: %last mongo error%\""
})
@Since("3.0.0")
public class ExprLastMongoError extends SimpleExpression<String> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprLastMongoError.class, String.class)
            .addPatterns("[the] last mongo[(sk|db)] error")
            .priority(SyntaxInfo.SIMPLE)
            .build());
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected String[] get(@NotNull Event e) {
        String lastError = SubscriberHelpers.getLastError();
        return lastError == null ? new String[0] : new String[]{lastError};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @NotNull
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "last mongo error";
    }
}
