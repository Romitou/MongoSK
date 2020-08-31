package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.mongodb.client.MongoCollection;

@Name("Mongo Collection Database Name")
@Description("This expression allows you to retrieve the name of the database of a Mongo collection.")
@Examples({"set {_collection} to collection named \"example\" from database named \"mongosk\" with client named \"default\"" +
        "send {_collection}'s database name"})
@Since("1.0.0")
public class ExprCollectionDatabaseName extends SimplePropertyExpression<MongoCollection, String> {

    static {
        register(ExprCollectionDatabaseName.class, String.class, "[mongo[db]] database name", "mongocollections");
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
