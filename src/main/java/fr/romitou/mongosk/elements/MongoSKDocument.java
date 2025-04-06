package fr.romitou.mongosk.elements;

import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.skript.expressions.ExprMongoEmbeddedValue;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.*;

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
        LoggerHelper.debug("Informations about this MongoSK document:",
            // "BSON document: " + this.bsonDocument,
            "JSON: " + this.bsonDocument.toJson(),
            "Base collection: " + this.baseCollection
        );
    }

    public Object getEmbeddedValue(final List<ExprMongoEmbeddedValue.MongoQueryElement> queryElements) {
        if (this.bsonDocument == null) {
            LoggerHelper.debug("The BSON document is null.",
                "Query elements: " + queryElements,
                "Document: null");
            return null;
        }
        Object current = this.bsonDocument;

        for (ExprMongoEmbeddedValue.MongoQueryElement element : queryElements) {
            if (element.getKey() != null) {
                if (!(current instanceof Document)) {
                    LoggerHelper.debug("Invalid path: a document was expected for the field '" + element.getKey() + "'.",
                        "Query elements: " + queryElements,
                        "Document: " + (this.bsonDocument != null ? this.bsonDocument.toJson() : "null"));
                    return null;
                }
                current = ((Document) current).get(element.getKey());
            } else if (element.getIndex() != null) {
                if (!(current instanceof List)) {
                    LoggerHelper.severe("Invalid path: a list was expected for the index " + element.getIndex() + ".",
                        "Query elements: " + queryElements,
                        "Document: " + this.bsonDocument.toJson());
                    return null;
                }
                List<Object> list = (List<Object>) current;
                int idx = element.getIndex();
                if (idx < 0 || idx >= list.size()) {
                    LoggerHelper.severe("Index " + idx + " out of range for the list.",
                        "Query elements: " + queryElements,
                        "Document: " + this.bsonDocument.toJson());
                    return null;
                }
                current = list.get(idx);
            }
        }
        return current;
    }

    public void setEmbeddedValue(final List<ExprMongoEmbeddedValue.MongoQueryElement> queryElements, Object value) {
        if (this.bsonDocument == null) {
            this.bsonDocument = new Document();
        }
        Object current = this.bsonDocument;

        for (int i = 0; i < queryElements.size(); i++) {
            ExprMongoEmbeddedValue.MongoQueryElement element = queryElements.get(i);
            boolean isLast = (i == queryElements.size() - 1);

            if (element.getKey() != null) {
                if (!(current instanceof Document)) {
                    LoggerHelper.severe("Invalid path: a document was expected for the field '" + element.getKey() + "'.",
                        "Query elements: " + queryElements,
                        "Document: " + this.bsonDocument.toJson());
                    return;
                }
                Document doc = (Document) current;
                if (isLast) {
                    if (value == null) {
                        doc.remove(element.getKey());
                    } else {
                        doc.put(element.getKey(), value);
                    }
                } else {
                    Object next = doc.get(element.getKey());
                    if (next == null) {
                        ExprMongoEmbeddedValue.MongoQueryElement nextElement = queryElements.get(i + 1);
                        next = (nextElement.getIndex() != null) ? new ArrayList<>() : new Document();
                        doc.put(element.getKey(), next);
                    }
                    current = next;
                }
            } else if (element.getIndex() != null) {
                if (!(current instanceof List)) {
                    LoggerHelper.severe("Invalid path: a list was expected for the index " + element.getIndex() + ".",
                        "Query elements: " + queryElements,
                        "Document: " + this.bsonDocument.toJson());
                    return;
                }
                List<Object> list = (List<Object>) current;
                int idx = element.getIndex();
                while (list.size() <= idx) {
                    list.add(null);
                }
                if (isLast) {
                    if (value == null) {
                        list.remove(idx);
                    } else {
                        list.set(idx, value);
                    }
                } else {
                    Object next = list.get(idx);
                    if (next == null) {
                        ExprMongoEmbeddedValue.MongoQueryElement nextElement = queryElements.get(i + 1);
                        next = (nextElement.getIndex() != null) ? new ArrayList<>() : new Document();
                        list.set(idx, next);
                    }
                    current = next;
                }
            }
        }
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
