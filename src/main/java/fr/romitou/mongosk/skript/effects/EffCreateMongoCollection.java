package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Create Mongo collection")
@Description({
    "Quickly create a collection with a specific name within a database.",
    "It is recommended to verify that the collection does not already exist before creating it."
})
@Examples({
    "set {mydatabase} to mongo database named \"exampleDatabase\" from {myserver}",
    "create a mongo collection named \"playerData\" in {mydatabase}"
})
@Since("2.0.3")
public class EffCreateMongoCollection extends MongoEffect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffCreateMongoCollection.class)
            .addPatterns("create [a] mongo[(sk|db)] collection [named] %string% in %mongoskdatabase%")
            .build());
    }

    private Expression<String> exprCollectionName;
    private Expression<MongoSKDatabase> exprMongoSKDatabase;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult) {
        exprCollectionName = (Expression<String>) exprs[0];
        exprMongoSKDatabase = (Expression<MongoSKDatabase>) exprs[1];
        return true;
    }

    @Override
    protected @Nullable Request<?> prepare(Event e) {
        String collectionName = exprCollectionName.getSingle(e);
        MongoSKDatabase mongoSKDatabase = exprMongoSKDatabase.getSingle(e);
        if (collectionName == null || mongoSKDatabase == null)
            return null;
        return Request.withoutResult(mongoSKDatabase.getMongoDatabase().createCollection(collectionName));
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "create mongo collection named " + exprCollectionName.toString(e, debug) + " in " + exprMongoSKDatabase.toString(e, debug);
    }
}
