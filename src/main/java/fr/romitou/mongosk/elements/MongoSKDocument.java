package fr.romitou.mongosk.elements;

import org.bson.Document;

import javax.annotation.Nullable;
import java.util.Objects;

public class MongoSKDocument {

    private final Document bsonDocument;
    private final MongoSKCollection baseCollection;

    public MongoSKDocument(Document bsonDocument, @Nullable MongoSKCollection baseCollection) {
        this.bsonDocument = bsonDocument;
        this.baseCollection = baseCollection;
    }

    public Document getBsonDocument() {
        return bsonDocument;
    }

    public MongoSKCollection getBaseCollection() {
        return baseCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKDocument that = (MongoSKDocument) o;
        return Objects.equals(bsonDocument, that.bsonDocument) && Objects.equals(baseCollection, that.baseCollection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bsonDocument, baseCollection);
    }

    @Override
    public String
    toString() {
        return "MongoSKDocument{" +
            "bsonDocument=" + bsonDocument +
            ", baseCollection=" + baseCollection +
            '}';
    }
}
