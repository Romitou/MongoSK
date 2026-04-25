package fr.romitou.mongosk.utils;

import fr.romitou.mongosk.elements.MongoQueryElement;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MongoPathParserTest {

    @Test
    public void testSimplePath() {
        ArrayList<MongoQueryElement> elements = MongoPathParser.parse("foo.bar");
        assertEquals(2, elements.size());
        assertEquals(new MongoQueryElement("foo"), elements.get(0));
        assertEquals(new MongoQueryElement("bar"), elements.get(1));
    }

    @Test
    public void testPathWithIndex() {
        ArrayList<MongoQueryElement> elements = MongoPathParser.parse("foo[0].bar");
        assertEquals(3, elements.size());
        assertEquals(new MongoQueryElement("foo"), elements.get(0));
        assertEquals(new MongoQueryElement(0), elements.get(1));
        assertEquals(new MongoQueryElement("bar"), elements.get(2));
    }

    @Test
    public void testPathWithMultipleIndices() {
        ArrayList<MongoQueryElement> elements = MongoPathParser.parse("foo[0][1].bar");
        assertEquals(4, elements.size());
        assertEquals(new MongoQueryElement("foo"), elements.get(0));
        assertEquals(new MongoQueryElement(0), elements.get(1));
        assertEquals(new MongoQueryElement(1), elements.get(2));
        assertEquals(new MongoQueryElement("bar"), elements.get(3));
    }

    @Test
    public void testPathWithEscapedDot() {
        ArrayList<MongoQueryElement> elements = MongoPathParser.parse("foo\\.bar.baz");
        assertEquals(2, elements.size());
        assertEquals(new MongoQueryElement("foo.bar"), elements.get(0));
        assertEquals(new MongoQueryElement("baz"), elements.get(1));
    }

    @Test
    public void testEmptyPath() {
        ArrayList<MongoQueryElement> elements = MongoPathParser.parse("");
        assertTrue(elements.isEmpty());
    }

    @Test
    public void testPathWithEmptySegment() {
        // "foo..bar" -> empty segment logged, but not added.
        ArrayList<MongoQueryElement> elements = MongoPathParser.parse("foo..bar");
        assertEquals(2, elements.size());
        assertEquals(new MongoQueryElement("foo"), elements.get(0));
        assertEquals(new MongoQueryElement("bar"), elements.get(1));
    }

    @Test
    public void testUnclosedBracket() {
         // "foo[0" -> unclosed bracket logged.
         // "foo" is added, "[0" is skipped/logged.
         ArrayList<MongoQueryElement> elements = MongoPathParser.parse("foo[0");
         assertEquals(1, elements.size());
         assertEquals(new MongoQueryElement("foo"), elements.get(0));
    }

    @Test
    public void testInvalidIndex() {
        // "foo[bar]" -> invalid integer logged.
        ArrayList<MongoQueryElement> elements = MongoPathParser.parse("foo[bar]");
        assertEquals(1, elements.size());
        assertEquals(new MongoQueryElement("foo"), elements.get(0));
    }

    @Test
    public void testUnexpectedClosingBracket() {
        // "foo]" -> unexpected closing bracket logged.
        ArrayList<MongoQueryElement> elements = MongoPathParser.parse("foo]");
        assertEquals(1, elements.size());
        assertEquals(new MongoQueryElement("foo"), elements.get(0));
    }

    @Test
    public void testBracketAtStart() {
        ArrayList<MongoQueryElement> elements = MongoPathParser.parse("[0].foo");
        assertEquals(2, elements.size());
        assertEquals(new MongoQueryElement(0), elements.get(0));
        assertEquals(new MongoQueryElement("foo"), elements.get(1));
    }
}
