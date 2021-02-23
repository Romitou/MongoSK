package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.elements.MongoSKDatabase;

import javax.annotation.Nonnull;

@Name("Mongo database name")
@Description("Thanks to this expression, you can retrieve the name of your database stored in Mongo.")
@Examples({"set {_database} to mongo database named \"example\" of {myserver}",
    "broadcast \"%{_database}'s mongo name% should be 'example'!\""})
@Since("2.0.0")
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
