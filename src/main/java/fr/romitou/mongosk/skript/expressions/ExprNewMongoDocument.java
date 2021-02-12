package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprNewMongoDocument extends SimpleExpression<MongoSKDocument> {

    static {
        Skript.registerExpression(
            ExprNewMongoDocument.class,
            MongoSKDocument.class,
            ExpressionType.COMBINED,
            "[[a] new] mongo[(sk|db)] [empty] document"
        );
    }

    @Override
    public boolean init(@Nonnull Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected MongoSKDocument[] get(@Nonnull final Event e) {
        return new MongoSKDocument[]{new MongoSKDocument(new Document(), null)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @Nonnull
    public Class<? extends MongoSKDocument> getReturnType() {
        return MongoSKDocument.class;
    }

    @Override
    @Nonnull
    public String toString(@Nullable final Event e, boolean debug) {
        return "a new mongosk document";
    }
}
