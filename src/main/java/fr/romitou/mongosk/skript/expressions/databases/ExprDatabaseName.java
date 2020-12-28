package fr.romitou.mongosk.skript.expressions.databases;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.mongodb.client.MongoDatabase;

@Name("Mongo Database Name")
@Description("This expression allows you to retrieve the name of a Mongo database.")
@Examples({"set {_database} to database named \"mongosk\" with client named \"default\"",
        "send mongo name of {_database}"})
@Since("1.0.0")
public class ExprDatabaseName extends SimplePropertyExpression<MongoDatabase, String> {

    static {
        register(ExprDatabaseName.class, String.class, "mongo[db] name", "mongodatabases");
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
        return "mongo name";
    }

}
