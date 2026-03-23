package fr.romitou.mongosk.elements;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MongoSKDocumentTest {

    @Test
    public void testConstructorsAndGetters() {
        // Default constructor
        MongoSKDocument doc1 = new MongoSKDocument();
        assertNotNull(doc1.getBsonDocument());
        assertTrue(doc1.getBsonDocument().isEmpty());
        assertNull(doc1.getBaseCollection());

        // Constructor with Document
        Document bsonDoc = new Document("key", "value");
        MongoSKDocument doc2 = new MongoSKDocument(bsonDoc);
        assertEquals(bsonDoc, doc2.getBsonDocument());
        assertNull(doc2.getBaseCollection());

        // Constructor with Document and Collection
        MongoSKCollection collection = new MongoSKCollection(null);
        MongoSKDocument doc3 = new MongoSKDocument(bsonDoc, collection);
        assertEquals(bsonDoc, doc3.getBsonDocument());
        assertEquals(collection, doc3.getBaseCollection());
    }

    @Test
    public void testSetters() {
        MongoSKDocument doc = new MongoSKDocument();
        Document bsonDoc = new Document("a", 1);
        MongoSKCollection collection = new MongoSKCollection(null);

        doc.setBsonDocument(bsonDoc);
        assertEquals(bsonDoc, doc.getBsonDocument());

        doc.setBaseCollection(collection);
        assertEquals(collection, doc.getBaseCollection());
    }

    @Test
    public void testToString() {
        Document bsonDoc = new Document("key", "val");
        MongoSKCollection collection = new MongoSKCollection(null);
        MongoSKDocument doc = new MongoSKDocument(bsonDoc, collection);

        String str = doc.toString();
        assertNotNull(str);
        assertTrue(str.contains("MongoSKDocument"));
        assertTrue(str.contains("bsonDocument=" + bsonDoc));
        assertTrue(str.contains("baseCollection=" + collection));
    }

    @Test
    public void testEqualsAndHashCode() {
        Document docA = new Document("id", 1);
        Document docB = new Document("id", 2);

        MongoSKCollection colA = new MongoSKCollection(null);

        MongoSKDocument skDoc1 = new MongoSKDocument(docA, colA);
        MongoSKDocument skDoc2 = new MongoSKDocument(docA, colA);
        MongoSKDocument skDoc3 = new MongoSKDocument(docB, colA);
        MongoSKDocument skDoc4 = new MongoSKDocument(docA, null);

        // Reflexivity
        assertEquals(skDoc1, skDoc1);

        // Symmetry
        assertEquals(skDoc1, skDoc2);
        assertEquals(skDoc2, skDoc1);

        // HashCode consistency
        assertEquals(skDoc1.hashCode(), skDoc2.hashCode());

        // Inequality
        assertNotEquals(skDoc1, skDoc3); // Different BSON document
        assertNotEquals(skDoc1, skDoc4); // Different collection (one is null)
        assertNotEquals(skDoc1, null);
        assertNotEquals(skDoc1, "some string");

        // Test with both null BSON documents
        MongoSKDocument empty1 = new MongoSKDocument(null, null);
        MongoSKDocument empty2 = new MongoSKDocument(null, null);
        assertEquals(empty1, empty2);
        assertEquals(empty1.hashCode(), empty2.hashCode());
    }
}
