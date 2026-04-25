package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bson.BSONException;
import org.bson.Document;
import org.bson.json.JsonParseException;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("Mongo command result")
@Description({
    "Sends a raw command to a MongoDB database and returns the resulting documents.",
    "This frees you from the specific features implemented in MongoSK and allows you to execute arbitrary MongoDB commands.",
    "For more information on possible commands, see: https://docs.mongodb.com/manual/reference/command/."
})
@Examples({
    "set {_docs::*} to results of mongo command \"{ \"serverStatus\": 1 }\" of database {database}",
    "set {_version} to mongo value \"version\" of {_docs::1}",
    "broadcast \"Mongo host version: %{_version}%\""
})
@Since("2.0.3")
public class ExprMongoCommandResult extends SimpleExpression<MongoSKDocument> {

    static {
        Skript.registerExpression(
            ExprMongoCommandResult.class,
            MongoSKDocument.class,
            ExpressionType.COMBINED,
            "(results|documents) of mongo[(sk|db)] command %string% (of|in) database %mongoskdatabase%"
        );
    }

    private Expression<String> exprJsonString;
    private Expression<MongoSKDatabase> exprMongoSKDatabase;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprJsonString = (Expression<String>) exprs[0];
        exprMongoSKDatabase = (Expression<MongoSKDatabase>) exprs[1];
        return true;
    }

    @Override
    protected MongoSKDocument[] get(@Nonnull Event e) {
        String jsonString = exprJsonString.getSingle(e);
        MongoSKDatabase mongoSKDatabase = exprMongoSKDatabase.getSingle(e);
        if (mongoSKDatabase == null)
            return new MongoSKDocument[0];
        SubscriberHelpers.ObservableSubscriber<Document> documentObservableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        try {
            mongoSKDatabase.getMongoDatabase()
                .runCommand(Document.parse(jsonString).toBsonDocument())
                .subscribe(documentObservableSubscriber);
        } catch (BSONException | JsonParseException ex) {
            LoggerHelper.severe("An error occurred when changing the document's JSON: " + ex.getMessage(),
                "Provided JSON: " + jsonString,
                "Please check its validity on https://jsonlint.com."
            );
        }
        // Nobody has subscribed. :(
        if (documentObservableSubscriber.getSubscription() == null)
            return new MongoSKDocument[0];
        return documentObservableSubscriber.get()
            .stream()
            .map(MongoSKDocument::new)
            .toArray(MongoSKDocument[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKDocument> getReturnType() {
        return MongoSKDocument.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "result of mongo command " + exprJsonString.toString(e, debug) + " of database " + exprMongoSKDatabase.toString(e, debug);
    }
}
