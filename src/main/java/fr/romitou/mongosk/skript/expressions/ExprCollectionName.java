package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.mongodb.client.MongoCollection;

@Name("Mongo Collection Name")
@Description("This expression allows you to retrieve the name of a Mongo collection.")
@Examples({"set {_collection} to collection named \"example\" from database named \"mongosk\" with client named \"default\"" +
        "send name of {_collection}"})
@Since("1.0.0")
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
