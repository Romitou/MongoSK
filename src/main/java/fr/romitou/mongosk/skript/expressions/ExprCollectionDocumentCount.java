package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.mongodb.client.MongoCollection;

public class ExprCollectionDocumentCount extends SimplePropertyExpression<MongoCollection, Integer> {

    static {
        register(ExprCollectionDocumentCount.class, Integer.class, "document (count|size)", "mongocollections");
    }

    @Override
    public Integer convert(MongoCollection collection) {
        return (int) collection.countDocuments();
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    protected String getPropertyName() {
        return "document count";
    }

}
