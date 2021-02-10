package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
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

public class ExprMongoCollection extends SimpleExpression<MongoSKCollection> {

    static {
        Skript.registerExpression(
            ExprMongoCollection.class,
            MongoSKCollection.class,
            ExpressionType.COMBINED,
            "[the] mongo[(sk|db)] collection [named] %string% (of|from) %mongoskdatabase%"
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
        return "";
    }
}
