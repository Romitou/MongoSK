package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.elements.MongoSKDatabase;

import javax.annotation.Nonnull;

public class ExprMongoDatabaseName extends SimplePropertyExpression<MongoSKDatabase, String> {

    static {
        register(
            ExprMongoDatabaseName.class,
            String.class,
            "mongo[(db|sk)] name",
            "mongoskdatabases"
        );
    }

    @Nonnull
    @Override
    public String convert(MongoSKDatabase database) {
        return database.getMongoDatabase().getName();
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
