package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.mongodb.client.MongoCollection;

@Name("Mongo Collection Document Count")
@Description("This expression allows you to retrieve the number of documents in a Mongo collection.")
@Examples({"set {_collection} to collection named \"example\" from database named \"mongosk\" with client named \"default\"",
        "send \"%{_collection}'s document count%\""})
@Since("1.0.0")
public class ExprCollectionDocumentCount extends SimplePropertyExpression<MongoCollection, Integer> {

    static {
        register(ExprCollectionDocumentCount.class, Integer.class, "[mongo[db]] document (count|size)", "mongocollections");
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
