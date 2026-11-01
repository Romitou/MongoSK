package fr.romitou.mongosk.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import fr.romitou.mongosk.elements.*;
import org.bson.types.Binary;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public final class SkriptTypes {

    private SkriptTypes() {
    }

    public static void register() {
        register(MongoSKServer.class, "mongoskserver", "mongo(db|sk)?( |-)?servers?", "MongoSK Server", "2.0.0",
            "Represents a remote MongoDB server, with which you can interact. To create this type, use the Mongo Server expression.",
            server -> server.getDisplayedName() + " server",
            MongoSKServer::getDisplayedName);

        register(MongoSKDatabase.class, "mongoskdatabase", "mongo(db|sk)?( |-)?databases?", "MongoSK Database", "2.0.0",
            "Represents a Mongo database, which contains collections. If you want to create this type, look at the Mongo Database expression.",
            database -> database.getMongoDatabase().getName() + " database",
            database -> database.getMongoDatabase().getName());

        register(MongoSKCollection.class, "mongoskcollection", "mongo(db|sk)?( |-)?collections?", "MongoSK Collection", "2.0.0",
            "Represents a Mongo collection, which contains documents. Use the Mongo Collection expression to create this type.",
            collection -> collection.getMongoCollection().getNamespace().getCollectionName() + " collection",
            collection -> collection.getMongoCollection().getNamespace().getCollectionName());

        register(MongoSKDocument.class, "mongoskdocument", "mongo(db|sk)?( |-)?documents?", "MongoSK Document", "2.0.0",
            "Represents a Mongo (BSON) document, which contains some fields and values. If you want to read fields values, use the Mongo Value expression. Otherwise, you can use the Mongo Query expression to query your Mongo collection for documents.",
            document -> document.getBsonDocument().toJson() + " document",
            document -> document.getBsonDocument().toJson());

        register(MongoSKFilter.class, "mongoskfilter", "mongo(db|sk)?( |-)?filters?", "MongoSK Filter", "2.0.0",
            "Represents a filter, which can be used in your queries to target a certain type of data. You can create a filter using the Mongo Filter expression. ",
            MongoSKFilter::getDisplay,
            filter -> filter.getFilter().toBsonDocument().toJson());

        register(MongoSKQuery.class, "mongoskquery", "mongo(db|sk)?( |-)?quer(y|ies)", "MongoSK Query", "2.0.0",
            "Allows you to build a complex query to target a very specific type of data with specific options.",
            MongoSKQuery::getDisplay,
            MongoSKQuery::toString);

        register(MongoSKSort.class, "mongosksort", "mongo(db|sk)?( |-)?sorts?", "MongoSK Sort", "2.0.0",
            "Create a sort to first retrieve certain documents according to the specified fields.",
            MongoSKSort::getDisplay,
            MongoSKSort::toString);

        register(Binary.class, "mongobinary", "mongo(db|sk)?( |-)?binar(y|ies)", "MongoSK Binary", "2.3.3",
            "Represents a binary data, which can be used to store large data in a more efficient way.",
            binary -> new String(binary.getData(), StandardCharsets.UTF_8),
            binary -> new String(binary.getData(), StandardCharsets.UTF_8));

        register(MongoSKProjection.class, "mongoskprojection", "mongo(db|sk)?( |-)?projections?", "MongoSK Projection", "3.0.0",
            "Create a projection to include or exclude certain fields when retrieving documents.",
            MongoSKProjection::getDisplay,
            MongoSKProjection::toString);
    }

    /**
     * Registers a type which cannot be parsed from a script.
     *
     * @param variableId the unique part of the variable name, prefixed with the code name of the type
     */
    private static <T> void register(Class<T> type, String codeName, String user, String name, String since, String description,
                                     Function<T, String> display, Function<T, String> variableId) {
        Classes.registerClass(new ClassInfo<>(type, codeName)
            .user(user)
            .defaultExpression(new EventValueExpression<>(type))
            .name(name)
            .description(description)
            .since(since)
            .parser(new Parser<T>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(T object, int flags) {
                    return display.apply(object);
                }

                @Override
                public String toVariableNameString(T object) {
                    return codeName + ":" + variableId.apply(object);
                }
            })
        );
    }
}
