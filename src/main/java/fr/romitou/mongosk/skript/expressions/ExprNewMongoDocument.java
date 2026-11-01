package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("New Mongo document")
@Description({
    "Creates a new, empty Mongo document.",
    "You can then use other expressions to populate it with fields, values, and lists."
})
@Examples({
    "set {_doc} to a new mongo document",
    "set mongo value \"name\" of {_doc} to \"MongoSK\""
})
@Since("2.0.0")
public class ExprNewMongoDocument extends SimpleExpression<MongoSKDocument> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprNewMongoDocument.class, MongoSKDocument.class)
            .addPatterns("[[a] new] mongo[(sk|db)] [empty] document")
            .priority(SyntaxInfo.SIMPLE)
            .build());
    }

    @Override
    public boolean init(@NotNull Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected MongoSKDocument[] get(@NotNull final Event e) {
        return new MongoSKDocument[]{new MongoSKDocument()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @NotNull
    public Class<? extends MongoSKDocument> getReturnType() {
        return MongoSKDocument.class;
    }

    @Override
    @NotNull
    public String toString(@Nullable final Event e, boolean debug) {
        return "a new mongosk document";
    }
}
