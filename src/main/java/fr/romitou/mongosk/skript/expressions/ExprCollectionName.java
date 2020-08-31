package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.mongodb.client.MongoCollection;

public class ExprCollectionName extends SimplePropertyExpression<MongoCollection, String> {

    static {
        register(ExprDatabaseName.class, String.class, "[mongo[db]] name", "mongocollections");
    }

    @Override
    public String convert(MongoCollection mongoCollection) {
        return mongoCollection.getNamespace().getCollectionName();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "mongo name";
    }

}
