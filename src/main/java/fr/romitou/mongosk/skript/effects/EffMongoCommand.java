package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bson.Document;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Execute Mongo command")
@Description({
    "Sends a raw command to a MongoDB database without blocking the server, and stores the resulting documents. The script waits for the result like with the wait effect.",
    "This frees you from the specific features implemented in MongoSK and allows you to execute arbitrary MongoDB commands.",
    "For more information on possible commands, see: https://docs.mongodb.com/manual/reference/command/."
})
@Examples({
    "execute mongo command \"{ \"\"serverStatus\"\": 1 }\" in database {mydatabase} and store the result in {_status}",
    "broadcast \"Mongo host version: %mongo value \"version\" of {_status}%\""
})
@Since("3.0.0")
public class EffMongoCommand extends MongoEffect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffMongoCommand.class)
            .addPatterns("execute mongo[(sk|db)] command %string% (of|in) database %mongoskdatabase%" + STORE_PATTERN)
            .build());
    }

    private Expression<String> exprJsonString;
    private Expression<MongoSKDatabase> exprMongoSKDatabase;
    private Expression<?> exprStoredIn;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult) {
        exprJsonString = (Expression<String>) exprs[0];
        exprMongoSKDatabase = (Expression<MongoSKDatabase>) exprs[1];
        exprStoredIn = exprs[2];
        return storeIn(exprStoredIn);
    }

    @Override
    protected @Nullable Request<?> prepare(Event event) {
        String jsonString = exprJsonString.getSingle(event);
        MongoSKDatabase database = exprMongoSKDatabase.getSingle(event);
        if (jsonString == null || database == null)
            return null;
        return new Request<>(
            database.getMongoDatabase().runCommand(Document.parse(jsonString).toBsonDocument()),
            documents -> documents.stream().map(MongoSKDocument::new).toArray()
        );
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "execute mongo command " + exprJsonString.toString(e, debug) + " in database " + exprMongoSKDatabase.toString(e, debug)
            + " and store the result in " + exprStoredIn.toString(e, debug);
    }
}
