package fr.romitou.mongosk.objects;

import org.bson.Document;

import java.util.Objects;

public class MongoSKDocument {

    private Document bsonDocument;

    public MongoSKDocument() {
        this(new Document());
    }

    public MongoSKDocument(Document bsonDocument) {
        this.bsonDocument = bsonDocument;
    }

    public Document getBsonDocument() {
        return bsonDocument;
    }

    public void setBsonDocument(Document bsonDocument) {
        this.bsonDocument = bsonDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKDocument that = (MongoSKDocument) o;
        return Objects.equals(bsonDocument, that.bsonDocument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bsonDocument);
    }

    @Override
    public String toString() {
        return "MongoSKDocument{" +
                "bsonDocument=" + bsonDocument +
                '}';
    }
}
