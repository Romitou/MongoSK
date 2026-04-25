package fr.romitou.mongosk.utils;

import org.bson.types.Binary;
import org.junit.jupiter.api.Test;

import java.io.StreamCorruptedException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BinaryUtilsTest {

    @Test
    public void testBinaryDataWithByteArray() throws StreamCorruptedException {
        byte[] data = new byte[]{1, 2, 3};
        assertArrayEquals(data, BinaryUtils.getBinaryData(data));
    }

    @Test
    public void testBinaryDataWithBinaryObject() throws StreamCorruptedException {
        byte[] data = new byte[]{4, 5, 6};
        Binary binary = new Binary(data);
        assertArrayEquals(data, BinaryUtils.getBinaryData(binary));
    }

    @Test
    public void testBinaryDataWithInvalidInput() {
        assertThrows(StreamCorruptedException.class, () -> BinaryUtils.getBinaryData("invalid"));
        assertThrows(StreamCorruptedException.class, () -> BinaryUtils.getBinaryData(123));
    }

    @Test
    public void testBinaryDataWithNull() {
        assertThrows(StreamCorruptedException.class, () -> BinaryUtils.getBinaryData(null));
    }

}
