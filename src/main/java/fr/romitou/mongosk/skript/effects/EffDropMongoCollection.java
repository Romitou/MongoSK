package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKCollection;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EffDropMongoCollection extends Effect {

    private final static Boolean CAN_DROP = MongoSK.getConfiguration().getBoolean("allow-drop.collection", false);

    static {
        if (CAN_DROP)
            Skript.registerEffect(
                EffDropMongoCollection.class,
                "drop [the] mongo[(sk|db)] collection %mongoskcollection%"
            );
    }

    private Expression<MongoSKCollection> exprMongoSKCollection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[0];
        return true;
    }

    @Override
    protected void execute(@Nonnull Event e) {
        MongoSKCollection mongoSKCollection = exprMongoSKCollection.getSingle(e);
        if (mongoSKCollection == null)
            return;
        SubscriberHelpers.ObservableSubscriber<Void> voidSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        mongoSKCollection.getMongoCollection().drop().subscribe(voidSubscriber);
        voidSubscriber.await();
    }

    @Nonnull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "drop the mongo collection " + exprMongoSKCollection.toString(e, debug);
    }
}
