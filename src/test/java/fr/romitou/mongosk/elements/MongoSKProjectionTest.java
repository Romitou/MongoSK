package fr.romitou.mongosk.elements;

import com.mongodb.client.model.Projections;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MongoSKProjectionTest {

    @Test
    public void testGetters() {
        Bson bsonProj = Projections.include("field1", "field2");
        MongoSKProjection proj = new MongoSKProjection(bsonProj, "test display");

        assertEquals(bsonProj, proj.getProjection());
        assertEquals("test display", proj.getDisplay());
    }

    @Test
    public void testEqualsAndHashCode() {
        Bson bsonProj1 = Projections.include("field1");
        Bson bsonProj2 = Projections.exclude("field2");

        MongoSKProjection proj1 = new MongoSKProjection(bsonProj1, "display1");
        MongoSKProjection proj2 = new MongoSKProjection(bsonProj1, "display1");
        MongoSKProjection proj3 = new MongoSKProjection(bsonProj2, "display2");

        assertEquals(proj1, proj2);
        assertEquals(proj1.hashCode(), proj2.hashCode());

        assertNotEquals(proj1, proj3);
        assertNotEquals(proj1.hashCode(), proj3.hashCode());
    }

    @Test
    public void testToString() {
        Bson bsonProj = Projections.include("field1");
        MongoSKProjection proj = new MongoSKProjection(bsonProj, "test display");

        assertEquals("MongoSKProjection{projection=" + bsonProj + ", display='test display'}", proj.toString());
    }

    @Test
    public void testPrintDebug() {
        // Just verify it doesn't throw an exception when called
        Bson bsonProj = Projections.include("field1");
        MongoSKProjection proj = new MongoSKProjection(bsonProj, "test display");
        proj.printDebug();
    }
}
