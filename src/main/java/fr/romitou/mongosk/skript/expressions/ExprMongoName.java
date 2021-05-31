package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKDatabase;

import javax.annotation.Nonnull;

@Name("Mongo name")
@Description("Retrieve the names of different Mongo elements. " +
    "You can use this expression to retrieve the name of collections or databases.")
@Examples({"set {_name} to {mongodatabase}'s mongo name",
    "broadcast mongo name of {mongocollection}"})
@Since("2.0.0")
public class ExprMongoName extends SimplePropertyExpression<Object, String> {

    static {
        register(
            ExprMongoName.class,
            String.class,
            "mongo[(db|sk)] name",
            "mongoskdatabase/mongoskcollection"
        );
    }

    @Override
    public String convert(Object ctx) {
        if (ctx instanceof MongoSKCollection)
            return ((MongoSKCollection) ctx).getMongoCollection().getNamespace().getCollectionName();
        else if (ctx instanceof MongoSKDatabase)
            return ((MongoSKDatabase) ctx).getMongoDatabase().getName();
        return null;
    }

    @Override
    @Nonnull
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    @Nonnull
    protected String getPropertyName() {
        return "mongo name";
    }

}
