package fr.romitou.mongosk.elements;

import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.skript.expressions.ExprMongoEmbeddedValue;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
        LoggerHelper.debug("Informations about this MongoSK document:",
            // "BSON document: " + this.bsonDocument,
            "JSON: " + this.bsonDocument.toJson(),
            "Base collection: " + this.baseCollection
        );
    }

    public Object getEmbeddedValue(final ExprMongoEmbeddedValue.MongoQueryElement[] queryElements) {
        Object value = this.bsonDocument;
        Iterator<ExprMongoEmbeddedValue.MongoQueryElement> keyIterator = Arrays.stream(queryElements).iterator();
        while (keyIterator.hasNext()) {
            ExprMongoEmbeddedValue.MongoQueryElement queryElement = keyIterator.next();
            if (queryElement.path != null) {
                if (value instanceof Document) {
                    value = ((Document) value).get(queryElement.path);
                } else {
                    if (value == null) {
                        LoggerHelper.debug("Expected a document, but got null instead at path '" + queryElement.path + "'.",
                            "Query elements: " + Arrays.toString(queryElements),
                            "Document: " + this.bsonDocument.toJson()
                        );
                    } else {
                        LoggerHelper.debug("Expected a document, but got a " + value.getClass().getSimpleName() + " instead at path '" + queryElement.path + "'.",
                            "Query elements: " + Arrays.toString(queryElements),
                            "Document: " + this.bsonDocument.toJson()
                        );
                    }
                    return null;
                }
            } else if (queryElement.index != null) {
                if (value instanceof List) {
                    value = ((List<Object>) value).get(queryElement.index);
                } else {
                    if (value == null) {
                        LoggerHelper.severe("Expected a list, but got null instead at index " + queryElement.index + ".",
                            "Query elements: " + Arrays.toString(queryElements),
                            "Document: " + this.bsonDocument.toJson()
                        );
                    } else {
                        LoggerHelper.severe("Expected a list, but got a " + value.getClass().getSimpleName() + " instead at index " + queryElement.index + ".",
                            "Query elements: " + Arrays.toString(queryElements),
                            "Document: " + this.bsonDocument.toJson()
                        );
                    }
                    return null;
                }
            }
        }
        return value;
    }

    public void setEmbeddedValue(final ExprMongoEmbeddedValue.MongoQueryElement[] queryElements, Object value) {
        Object currentValue = this.bsonDocument;
        if (currentValue == null) {
            currentValue = new Document();
        }
        Iterator<ExprMongoEmbeddedValue.MongoQueryElement> keyIterator = Arrays.stream(queryElements).iterator();
        while (keyIterator.hasNext()) {
            ExprMongoEmbeddedValue.MongoQueryElement queryElement = keyIterator.next();
            if (queryElement.path != null) {
                if (currentValue instanceof Document) {
                    if (keyIterator.hasNext()) {
                        Object foundValue = ((Document) currentValue).get(queryElement.path);
                        if (foundValue == null) {
                            ((Document) currentValue).put(queryElement.path, new Document());
                        } else {
                            currentValue = foundValue;
                        }
                    } else {
                        ((Document) currentValue).put(queryElement.path, value);
                    }
                } else {
                    if (currentValue == null) {
                        LoggerHelper.debug("Expected a document, but got null instead at path '" + queryElement.path + "'.",
                            "Query elements: " + Arrays.toString(queryElements),
                            "Document: " + this.bsonDocument.toJson()
                        );
                    } else {
                        LoggerHelper.debug("Expected a document, but got a " + currentValue.getClass().getSimpleName() + " instead at path '" + queryElement.path + "'.",
                            "Query elements: " + Arrays.toString(queryElements),
                            "Document: " + this.bsonDocument.toJson()
                        );
                    }
                }
            } else if (queryElement.index != null) {
                if (currentValue instanceof List) {
                    if (keyIterator.hasNext()) {
                        currentValue = ((List<Object>) currentValue).get(queryElement.index);
                    } else {
                        ((List<Object>) currentValue).set(queryElement.index, value);
                    }
                } else {
                    if (currentValue == null) {
                        LoggerHelper.severe("Expected a list, but got null instead at index " + queryElement.index + ".",
                            "Query elements: " + Arrays.toString(queryElements),
                            "Document: " + this.bsonDocument.toJson()
                        );
                    } else {
                        LoggerHelper.severe("Expected a list, but got a " + currentValue.getClass().getSimpleName() + " instead at index " + queryElement.index + ".",
                            "Query elements: " + Arrays.toString(queryElements),
                            "Document: " + this.bsonDocument.toJson()
                        );
                    }
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
