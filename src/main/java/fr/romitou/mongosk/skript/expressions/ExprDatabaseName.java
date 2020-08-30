package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.mongodb.client.MongoDatabase;

public class ExprDatabaseName extends SimplePropertyExpression<MongoDatabase, String> {

    static {
        register(ExprDatabaseName.class, String.class, "name", "mongodatabases");
    }

    @Override
    public String convert(MongoDatabase mongoDatabase) {
        return mongoDatabase.getName();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "name";
    }

}
