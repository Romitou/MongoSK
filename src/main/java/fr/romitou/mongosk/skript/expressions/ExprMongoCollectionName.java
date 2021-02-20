package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.elements.MongoSKCollection;

import javax.annotation.Nonnull;

public class ExprMongoCollectionName extends SimplePropertyExpression<MongoSKCollection, String> {

    static {
        register(ExprMongoCollectionName.class, String.class, "mongo[(db|sk)] name", "mongoskcollections");
    }

    @Nonnull
    @Override
    public String convert(MongoSKCollection collection) {
        return collection.getMongoCollection().getNamespace().getCollectionName();
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo name";
    }

    @Nonnull
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
