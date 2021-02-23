package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.elements.MongoSKCollection;

import javax.annotation.Nonnull;

@Name("Mongo collection name")
@Description("Thanks to this expression, you can retrieve the name of your collection stored in Mongo.")
@Examples({"set {_collection} to mongo collection named \"romitou\" of {mydatabase}",
    "broadcast \"%{_collection}'s mongo name% should be 'romitou'!\""})
@Since("2.0.0")
public class ExprMongoCollectionName extends SimplePropertyExpression<MongoSKCollection, String> {

    static {
        register(
            ExprMongoCollectionName.class,
            String.class,
            "mongo[(db|sk)] name[s]",
            "mongoskcollections"
        );
    }

    @Nonnull
    @Override
    public String convert(MongoSKCollection collection) {
        return collection.getMongoCollection().getNamespace().getCollectionName();
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
