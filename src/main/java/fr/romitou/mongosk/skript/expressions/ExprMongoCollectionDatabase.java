package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.elements.MongoSKCollection;

import javax.annotation.Nonnull;

@Name("Mongo database name of a collection")
@Description("Retrieves the name of the database to which the collection belongs. " +
    "You can then use the expression to retrieve a database by its name if you wish.")
@Examples({"set {_dbname} to {mycollection}'s database name",
    "set {_mydatabase} to mongo database named {_dbname} of {myserver}"})
@Since("2.0.0")
public class ExprMongoCollectionDatabase extends SimplePropertyExpression<MongoSKCollection, String> {

    static {
        register(
            ExprMongoCollectionDatabase.class,
            String.class,
            "mongo[(sk|db)] database name",
            "mongoskcollections"
        );
    }

    @Nonnull
    @Override
    public String convert(MongoSKCollection collection) {
        return collection.getMongoCollection().getNamespace().getDatabaseName();
    }

    @Nonnull
    @Override
    protected String getPropertyName() {
        return "mongo database name";
    }

    @Nonnull
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
