package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.elements.MongoSKCollection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Drop Mongo collection")
@Description({
    "This effect allows you to completely delete a collection, erasing all its associated documents.",
    "Due to its destructive nature, this effect is disabled by default.",
    "To enable it, you must configure 'allow-drop.collection' in the MongoSK configuration file."
})
@Examples({
    "set {mycollection} to mongo collection named \"myCollection\" of {mydatabase}",
    "drop mongo collection {mycollection}"
})
@Since("2.0.3")
public class EffDropMongoCollection extends MongoEffect {

    private final static boolean CAN_DROP = MongoSK.getInstance().getConfig().getBoolean("allow-drop.collection", false);

    public static void register(SyntaxRegistry registry) {
        if (CAN_DROP)
            registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffDropMongoCollection.class)
                .addPatterns("drop [the] mongo[(sk|db)] collection %mongoskcollection%")
                .build());
    }

    private Expression<MongoSKCollection> exprMongoSKCollection;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult) {
        exprMongoSKCollection = (Expression<MongoSKCollection>) exprs[0];
        return true;
    }

    @Override
    protected @Nullable Request<?> prepare(Event e) {
        MongoSKCollection mongoSKCollection = exprMongoSKCollection.getSingle(e);
        if (mongoSKCollection == null)
            return null;
        return Request.withoutResult(mongoSKCollection.getMongoCollection().drop());
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "drop the mongo collection " + exprMongoSKCollection.toString(e, debug);
    }
}
