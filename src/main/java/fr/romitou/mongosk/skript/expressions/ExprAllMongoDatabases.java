package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKDatabase;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprAllMongoDatabases extends SimpleExpression<MongoSKDatabase> {

    static {
        Skript.registerExpression(
            ExprAllMongoDatabases.class,
            MongoSKDatabase.class,
            ExpressionType.PROPERTY,
            "[all] [the] mongo[(db|sk)] databases of %mongoskserver%",
            "[all] %mongoskserver%'[s] mongo[(db|sk)] databases"
        );
    }

    private Expression<MongoSKServer> exprMongoSKServer;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprMongoSKServer = (Expression<MongoSKServer>) exprs[0];
        return true;
    }

    @Override
    protected MongoSKDatabase[] get(@Nonnull final Event e) {
        MongoSKServer mongoSKServer = exprMongoSKServer.getSingle(e);
        if (mongoSKServer == null)
            return new MongoSKDatabase[0];
        SubscriberHelpers.ObservableSubscriber<Document> observableSubscriber = new SubscriberHelpers.OperationSubscriber<>();
        mongoSKServer.getMongoClient().listDatabases().subscribe(observableSubscriber);
        System.out.println(observableSubscriber.await().getReceived());
        return observableSubscriber.await()
            .getReceived()
            .stream()
            .map(doc -> doc.getString("name"))
            .map(name -> mongoSKServer.getMongoClient().getDatabase(name))
            .map(MongoSKDatabase::new)
            .toArray(MongoSKDatabase[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Nonnull
    @Override
    public Class<? extends MongoSKDatabase> getReturnType() {
        return MongoSKDatabase.class;
    }

    @Nonnull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "all mongosk databases of " + exprMongoSKServer.toString(e, debug);
    }
}
