package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.mongodb.client.MongoCollection;

public class ExprCollectionDatabaseName extends SimplePropertyExpression<MongoCollection, String> {

    static {
        register(ExprCollectionDatabaseName.class, String.class, "database name", "mongocollections");
    }

    @Override
    public String convert(MongoCollection mongoCollection) {
        return mongoCollection.getNamespace().getDatabaseName();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "database name";
    }

}
