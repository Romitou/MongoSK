package fr.romitou.mongosk.skript.expressions;

import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import ch.njol.skript.lang.ExpressionType;
import org.skriptlang.skript.registration.SyntaxRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.reactivestreams.client.MongoCollection;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("Mongo collection")
@Description({
    "Retrieve a specific collection from a Mongo database.",
    "You must specify the database where the collection is located."
})
@Examples({
    "set {mycollection} to mongo collection named \"players\" of {mydatabase}",
    "broadcast \"Retrieved collection: %{mycollection}'s mongo name%\""
})
@Since("2.0.0")
public class ExprMongoCollection extends SimpleExpression<MongoSKCollection> {

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(SyntaxRegistry.EXPRESSION, DefaultSyntaxInfos.Expression.builder(ExprMongoCollection.class, MongoSKCollection.class).addPatterns("[the] mongo[(sk|db)] collection [named] %string% (of|from) %mongoskdatabase%").priority(ExpressionType.SIMPLE.priority()).build());
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
    protected MongoSKCollection[] get(@Nonnull final Event e) {
        String collectionName = exprCollectionName.getSingle(e);
        MongoSKDatabase mongoSKDatabase = exprMongoSKDatabase.getSingle(e);
        if (collectionName == null || mongoSKDatabase == null)
            return new MongoSKCollection[0];
        MongoCollection<Document> mongoCollection = mongoSKDatabase.getMongoDatabase().getCollection(collectionName);
        if (mongoCollection == null)
            return new MongoSKCollection[0];
        return new MongoSKCollection[]{new MongoSKCollection(mongoCollection)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @Nonnull
    public Class<? extends MongoSKCollection> getReturnType() {
        return MongoSKCollection.class;
    }

    @Override
    @Nonnull
    public String toString(@Nullable Event e, boolean debug) {
        return "mongosk collection named " + exprCollectionName.toString(e, debug) + " from " + exprMongoSKDatabase.toString(e, debug);
    }
}
