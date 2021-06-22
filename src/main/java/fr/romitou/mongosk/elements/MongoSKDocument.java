package fr.romitou.mongosk.elements;

import fr.romitou.mongosk.Logger;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.Objects;

public class MongoSKDocument {

    private Document bsonDocument;
    private MongoSKCollection baseCollection;

    public MongoSKDocument() {
        this(new Document(), null);
    }

    public MongoSKDocument(Document bsonDocument) {
        this(bsonDocument, null);
    }

    public MongoSKDocument(Document bsonDocument, @Nullable MongoSKCollection baseCollection) {
        this.bsonDocument = bsonDocument;
        this.baseCollection = baseCollection;
    }

    public Document getBsonDocument() {
        return bsonDocument;
    }

    public void setBsonDocument(Document bsonDocument) {
        this.bsonDocument = bsonDocument;
    }

    public MongoSKCollection getBaseCollection() {
        return baseCollection;
    }

    public void setBaseCollection(MongoSKCollection baseCollection) {
        this.baseCollection = baseCollection;
    }

    public void printDebug() {
        Logger.debug("Informations about this MongoSK document:",
            // "BSON document: " + this.bsonDocument,
            "JSON: " + this.bsonDocument.toJson(),
            "Base collection: " + this.baseCollection
        );
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
    public String toString() {
        return "MongoSKDocument{" +
            "bsonDocument=" + bsonDocument +
            ", baseCollection=" + baseCollection +
            '}';
    }
}
