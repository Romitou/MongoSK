package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("Drop Mongo database")
@Description("This effect will allow you to completely delete a database, and thus all its associated collections. " +
    "Be careful, this effect is not saved and cannot be used by default to avoid any problem. " +
    "If you want to activate it, go to the MongoSK configuration.")
@Examples({"set {mydatabase} to mongo database named \"exampleDatabase\" from {myserver}",
    "drop mongo database {mydatabase}"})
@Since("2.0.3")
public class EffDropMongoDatabase extends Effect {

    private final static Boolean CAN_DROP = MongoSK.getInstance().getConfig().getBoolean("allow-drop.database", false);

    static {
        if (CAN_DROP)
            Skript.registerEffect(
                EffDropMongoDatabase.class,
                "drop [the] mongo[(sk|db)] database %mongoskdatabase%"
            );
    }

    private Expression<MongoSKDatabase> exprMongoSKDatabase;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprMongoSKDatabase = (Expression<MongoSKDatabase>) exprs[0];
        return true;
    }

    @Override
    protected void execute(@Nonnull Event e) {
        MongoSKDatabase mongoSKDatabase = exprMongoSKDatabase.getSingle(e);
        if (mongoSKDatabase == null)
            return;
        SubscriberHelpers.ObservableSubscriber<Void> voidSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        mongoSKDatabase.getMongoDatabase().drop().subscribe(voidSubscriber);
        voidSubscriber.await();
    }

    @Nonnull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "drop the mongo database " + exprMongoSKDatabase.toString(e, debug);
    }
}
