package fr.romitou.mongosk.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import fr.romitou.mongosk.elements.*;

public class SkriptTypes {
    static {
        Classes.registerClass(new ClassInfo<>(MongoSKServer.class, "mongoskserver")
            .user("mongo(db|sk)?( |-)?servers?")
            .defaultExpression(new EventValueExpression<>(MongoSKServer.class))
            .name("MongoSK Server")
            .description("Represents a remote MongoDB server, with which you can interact. To create this type, use the Mongo Server expression.")
            .since("2.0.0")
            .parser(new Parser<MongoSKServer>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(MongoSKServer server, int flags) {
                    return server.getDisplayedName() + " server";
                }

                @Override
                public String toVariableNameString(MongoSKServer server) {
                    return "mongoskserver:" + server.getDisplayedName();
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKDatabase.class, "mongoskdatabase")
            .user("mongo(db|sk)?( |-)?databases?")
            .defaultExpression(new EventValueExpression<>(MongoSKDatabase.class))
            .name("MongoSK Database")
            .description("Represents a Mongo database, which contains collections. If you want to create this type, look at the Mongo Database expression.")
            .since("2.0.0")
            .parser(new Parser<MongoSKDatabase>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(MongoSKDatabase database, int flags) {
                    return database.getMongoDatabase().getName() + " database";
                }

                @Override
                public String toVariableNameString(MongoSKDatabase database) {
                    return "mongoskdatabase:" + database.getMongoDatabase().getName();
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKCollection.class, "mongoskcollection")
            .user("mongo(db|sk)?( |-)?collections?")
            .defaultExpression(new EventValueExpression<>(MongoSKCollection.class))
            .name("MongoSK Collection")
            .description("Represents a Mongo collection, which contains documents. Use the Mongo Collection expression to create this type.")
            .since("2.0.0")
            .parser(new Parser<MongoSKCollection>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(MongoSKCollection collection, int flags) {
                    return collection.getMongoCollection().getNamespace().getCollectionName() + " collection";
                }

                @Override
                public String toVariableNameString(MongoSKCollection collection) {
                    return "mongoskcollection:" + collection.getMongoCollection().getNamespace().getCollectionName();
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKDocument.class, "mongoskdocument")
            .user("mongo(db|sk)?( |-)?documents?")
            .defaultExpression(new EventValueExpression<>(MongoSKDocument.class))
            .name("MongoSK Document")
            .description("Represents a Mongo (BSON) document, which contains some fields and values. If you want to read fields values, use the Mongo Value expression. Otherwise, you can use the Mongo Query expression to query your Mongo collection for documents.")
            .since("2.0.0")
            .parser(new Parser<MongoSKDocument>() {

                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(MongoSKDocument document, int flags) {
                    return document.getBsonDocument().toJson() + " document";
                }

                @Override
                public String toVariableNameString(MongoSKDocument document) {
                    return "mongoskdocument:" + document.getBsonDocument().toJson();
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKFilter.class, "mongoskfilter")
            .user("mongo(db|sk)?( |-)?filters?")
            .defaultExpression(new EventValueExpression<>(MongoSKFilter.class))
            .name("MongoSK Filter")
            .description("Represents a filter, which can be used in your queries to target a certain type of data. You can create a filter using the Mongo Filter expression. ")
            .since("2.0.0")
            .parser(new Parser<MongoSKFilter>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(MongoSKFilter filter, int flags) {
                    return filter.getDisplay();
                }

                @Override
                public String toVariableNameString(MongoSKFilter filter) {
                    return "mongoskfilter:" + filter.getFilter().toBsonDocument().toJson();
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKQuery.class, "mongoskquery")
            .user("mongo(db|sk)?( |-)?quer(y|ies)")
            .defaultExpression(new EventValueExpression<>(MongoSKQuery.class))
            .name("MongoSK Query")
            .description("Allows you to build a complex query to target a very specific type of data with specific options.")
            .since("2.0.0")
            .parser(new Parser<MongoSKQuery>() {

                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(MongoSKQuery query, int flags) {
                    return query.getDisplay();
                }

                @Override
                public String toVariableNameString(MongoSKQuery query) {
                    return "mongoskquery:" + query;
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKSort.class, "mongosksort")
            .user("mongo(db|sk)?( |-)?sorts?")
            .defaultExpression(new EventValueExpression<>(MongoSKSort.class))
            .name("MongoSK Sort")
            .description("Create a sort to first retrieve certain documents according to the specified fields.")
            .since("2.0.0")
            .parser(new Parser<MongoSKSort>() {

                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(MongoSKSort sort, int flags) {
                    return sort.getDisplay();
                }

                @Override
                public String toVariableNameString(MongoSKSort sort) {
                    return "mongosksort:" + sort;
                }
            })
        );

    }
}
