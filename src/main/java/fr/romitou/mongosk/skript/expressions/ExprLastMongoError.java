package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.SubscriberHelpers;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
@Since("2.4.2")
public class ExprLastMongoError extends SimpleExpression<String> {

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(org.skriptlang.skript.registration.SyntaxRegistry.EXPRESSION, org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder(ExprLastMongoError.class, String.class).addPatterns("[the] last mongo[(sk|db)] error").priority(ExpressionType.SIMPLE.priority()).build());
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected String[] get(@Nonnull Event e) {
        String lastError = SubscriberHelpers.getLastError();
        return lastError == null ? new String[0] : new String[]{lastError};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Nonnull
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "last mongo error";
    }
}
