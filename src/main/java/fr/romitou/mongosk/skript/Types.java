package fr.romitou.mongosk.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Types {
    static {
        Classes.registerClass(new ClassInfo<>(MongoDatabase.class, "mongodatabase")
                .user("(mongo[db]( |-)?)?database")
                .name("MongoDB Database")
                .description("Represents a MongoDB database.")
                .since("1.0.0")
                .parser(new Parser<MongoDatabase>() {

                    @Override
                    public String toString(MongoDatabase o, int flags) {
                        return o.getName() + " database";
                    }

                    @Override
                    public String toVariableNameString(MongoDatabase o) {
                        return o.getName();
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return ".+";
                    }

                    @Override
                    public MongoDatabase parse(String s, ParseContext context) {
                        return null;
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(MongoCollection.class, "mongocollection")
                .user("(mongo[db]( |-)?)?collection")
                .name("MongoDB Collection")
                .description("Represents a MongoDB collection.")
                .since("1.0.0")
                .parser(new Parser<MongoCollection>() {
                    @Override
                    public String toString(MongoCollection o, int flags) {
                        return o.getNamespace().getCollectionName() + " collection";
                    }

                    @Override
                    public String toVariableNameString(MongoCollection o) {
                        return o.getNamespace().getCollectionName();
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return ".+";
                    }

                    @Override
                    public MongoCollection parse(String s, ParseContext context) {
                        return null;
                    }

                })
        );

        Classes.registerClass(new ClassInfo<>(Document.class, "mongodocument")
                .user("(mongo[db]( |-)?)?document")
                .name("MongoDB Document")
                .description("Represents a MongoDB document.")
                .since("1.0.0")
                .parser(new Parser<Document>() {

                    @Override
                    public String toString(Document o, int flags) {
                        return o.toString() + " document";
                    }

                    @Override
                    public String toVariableNameString(Document o) {
                        return o.toString();
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return ".+";
                    }

                    @Override
                    public Document parse(String s, ParseContext context) {
                        return null;
                    }

                })
        );

    }
}
