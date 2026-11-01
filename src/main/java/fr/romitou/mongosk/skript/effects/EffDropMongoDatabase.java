package fr.romitou.mongosk.skript.effects;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Drop Mongo database")
@Description({
    "This effect allows you to completely delete a database, wiping all its associated collections.",
    "Be careful, this effect is highly destructive and is disabled by default to prevent accidents.",
    "To enable it, you must configure 'allow-drop.database' in the MongoSK configuration file."
})
@Examples({
    "set {mydatabase} to mongo database named \"exampleDatabase\" from {myserver}",
    "drop mongo database {mydatabase}"
})
@Since("2.0.3")
public class EffDropMongoDatabase extends Effect {

    private final static boolean CAN_DROP = MongoSK.getInstance().getConfig().getBoolean("allow-drop.database", false);

    public static void register(SyntaxRegistry registry) {
        if (CAN_DROP)
            registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffDropMongoDatabase.class)
                .addPatterns("drop [the] mongo[(sk|db)] database %mongoskdatabase%")
                .build());
    }

    private Expression<MongoSKDatabase> exprMongoSKDatabase;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprMongoSKDatabase = (Expression<MongoSKDatabase>) exprs[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event e) {
        MongoSKDatabase mongoSKDatabase = exprMongoSKDatabase.getSingle(e);
        if (mongoSKDatabase == null)
            return;
        SubscriberHelpers.ObservableSubscriber<Void> voidSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        mongoSKDatabase.getMongoDatabase().drop().subscribe(voidSubscriber);
        voidSubscriber.await();
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "drop the mongo database " + exprMongoSKDatabase.toString(e, debug);
    }
}
