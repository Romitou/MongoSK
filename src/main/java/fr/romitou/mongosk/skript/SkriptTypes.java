package fr.romitou.mongosk.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import fr.romitou.mongosk.elements.*;

import javax.annotation.Nonnull;

public class SkriptTypes {
    static {
        Classes.registerClass(new ClassInfo<>(MongoSKServer.class, "mongoskserver")
            .user("mongo(db|sk)?( |-)?servers?")
            .name("MongoSK Server")
            .description("Represents a remote MongoDB server, with which you can interact. To create this type, use the Mongo Server expression.")
            .since("2.0.0")
            .parser(new Parser<MongoSKServer>() {
                @Override
                public boolean canParse(@Nonnull ParseContext context) {
                    return false;
                }

                @Override
                @Nonnull
                public String toString(@Nonnull MongoSKServer server, int flags) {
                    return server.getDisplayedName() + " server";
                }

                @Override
                @Nonnull
                public String toVariableNameString(@Nonnull MongoSKServer server) {
                    return "mongoskserver:" + server.getDisplayedName();
                }

                @Override
                @Nonnull
                public String getVariableNamePattern() {
                    return "mongoskserver:.+";
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKDatabase.class, "mongoskdatabase")
            .user("mongo(db|sk)?( |-)?databases?")
            .name("MongoSK Database")
            .description("Represents a Mongo database, which contains collections. If you want to create this type, look at the Mongo Database expression.")
            .since("2.0.0")
            .parser(new Parser<MongoSKDatabase>() {
                @Override
                public boolean canParse(@Nonnull ParseContext context) {
                    return false;
                }

                @Override
                @Nonnull
                public String toString(@Nonnull MongoSKDatabase database, int flags) {
                    return database.getMongoDatabase().getName() + " database";
                }

                @Override
                @Nonnull
                public String toVariableNameString(@Nonnull MongoSKDatabase database) {
                    return "mongoskdatabase:" + database.getMongoDatabase().getName();
                }

                @Override
                @Nonnull
                public String getVariableNamePattern() {
                    return "mongoskdatabase:.+";
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKCollection.class, "mongoskcollection")
            .user("mongo(db|sk)?( |-)?collections?")
            .name("MongoSK Collection")
            .description("Represents a Mongo collection, which contains documents. Use the Mongo Collection expression to create this type.")
            .since("2.0.0")
            .parser(new Parser<MongoSKCollection>() {
                @Override
                public boolean canParse(@Nonnull ParseContext context) {
                    return false;
                }

                @Override
                @Nonnull
                public String toString(@Nonnull MongoSKCollection collection, int flags) {
                    return collection.getMongoCollection().getNamespace().getCollectionName() + " collection";
                }

                @Override
                @Nonnull
                public String toVariableNameString(@Nonnull MongoSKCollection collection) {
                    return "mongoskcollection:" + collection.getMongoCollection().getNamespace().getCollectionName();
                }

                @Override
                @Nonnull
                public String getVariableNamePattern() {
                    return "mongoskcollection:.+";
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKDocument.class, "mongoskdocument")
            .user("mongo(db|sk)?( |-)?documents?")
            .name("MongoSK Document")
            .description("Represents a Mongo (BSON) document, which contains some fields and values. If you want to read fields values, use the Mongo Value expression. Otherwise, you can use the Mongo Query expression to query your Mongo collection for documents.")
            .since("2.0.0")
            .parser(new Parser<MongoSKDocument>() {
                @Override
                public boolean canParse(@Nonnull ParseContext context) {
                    return false;
                }

                @Override
                @Nonnull
                public String toString(@Nonnull MongoSKDocument document, int flags) {
                    return document.getBsonDocument().toJson() + " document";
                }

                @Override
                @Nonnull
                public String toVariableNameString(@Nonnull MongoSKDocument document) {
                    return "mongoskdocument:" + document.getBsonDocument().toJson();
                }

                @Override
                @Nonnull
                public String getVariableNamePattern() {
                    return "mongoskdocument:.+";
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(MongoSKFilter.class, "mongoskfilter")
            .user("mongo(db|sk)?( |-)?filters?")
            .name("MongoSK Filter")
            .description("Represents a filter, which can be used in your queries to target a certain type of data. You can create a filter using the Mongo Filter expression. ")
            .since("2.0.0")
            .parser(new Parser<MongoSKFilter>() {
                @Override
                public boolean canParse(@Nonnull ParseContext context) {
                    return false;
                }

                @Override
                @Nonnull
                public String toString(@Nonnull MongoSKFilter filter, int flags) {
                    return filter.toString();
                }

                @Override
                @Nonnull
                public String toVariableNameString(@Nonnull MongoSKFilter filter) {
                    return "mongoskfilter:" + filter.getFilter().toBsonDocument().toJson();
                }

                @Override
                @Nonnull
                public String getVariableNamePattern() {
                    return "mongoskfilter:.+";
                }
            })
        );

    }
}