package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.elements.MongoSKCollection;

import javax.annotation.Nonnull;

public class ExprMongoCollectionDatabase extends SimplePropertyExpression<MongoSKCollection, String> {

    static {
        register(
            ExprMongoCollectionDatabase.class,
            String.class,
            "mongo[(sk|db)] database name",
            "mongoskcollections"
        );
    }

    @Nonnull
    @Override
    public String convert(MongoSKCollection collection) {
        System.out.println(collection.getMongoCollection().getNamespace().getDatabaseName());
        return collection.getMongoCollection().getNamespace().getDatabaseName();
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo database name";
    }

    @Nonnull
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
