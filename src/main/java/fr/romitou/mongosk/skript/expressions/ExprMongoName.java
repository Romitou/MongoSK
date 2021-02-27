package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprMongoName extends SimpleExpression<String> {

    static {
        Skript.registerExpression(
            ExprMongoName.class,
            String.class,
            ExpressionType.PROPERTY,
            "[the] mongo[(db|sk)] name of %mongoskdatabase/mongoskcollection%",
            "%mongoskdatabase/mongoskcollection%'s mongo[(db|sk)] name"
        );
    }

    private Expression<Object> exprObject;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprObject = (Expression<Object>) exprs[0];
        return (exprObject.getReturnType().isInstance(MongoSKDatabase.class)
            || exprObject.getReturnType().isInstance(MongoSKCollection.class));
    }

    @Override
    protected String[] get(@Nonnull Event e) {
        Object object = exprObject.getSingle(e);
        if (object == null)
            return new String[0];
        if (object instanceof MongoSKCollection)
            return new String[]{(((MongoSKCollection) object).getMongoCollection().getNamespace().getCollectionName())};
        else if (object instanceof MongoSKDatabase)
            return new String[]{((MongoSKDatabase) object).getMongoDatabase().getName()};
        return new String[0];
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
    public String toString(@Nullable final Event e, boolean debug) {
        return "mongo name of " + exprObject.toString(e, debug);
    }
}
