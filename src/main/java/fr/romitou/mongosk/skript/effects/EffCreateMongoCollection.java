package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EffCreateMongoCollection extends Effect {

    static {
        Skript.registerEffect(
            EffCreateMongoCollection.class,
            "create [a] mongo[(sk|db)] collection [named] %string% in %mongoskdatabase%"
        );
    }

    private Expression<String> exprCollectionName;
    private Expression<MongoSKDatabase> exprMongoSKDatabase;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprCollectionName = (Expression<String>) exprs[0];
        exprMongoSKDatabase = (Expression<MongoSKDatabase>) exprs[1];
        return true;
    }

    @Override
    protected void execute(@Nonnull Event e) {
        String databaseName = exprCollectionName.getSingle(e);
        MongoSKDatabase mongoSKDatabase = exprMongoSKDatabase.getSingle(e);
        if (databaseName == null || mongoSKDatabase == null)
            return;
        SubscriberHelpers.ObservableSubscriber<Void> voidSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        mongoSKDatabase.getMongoDatabase().createCollection(databaseName).subscribe(voidSubscriber);
        voidSubscriber.await();
    }

    @Nonnull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "create mongo collection named " + exprCollectionName.toString(e, debug) + " in " + exprMongoSKDatabase.toString(e, debug);
    }
}
