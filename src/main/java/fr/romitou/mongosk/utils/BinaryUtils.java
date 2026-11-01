package fr.romitou.mongosk.utils;

import org.bson.types.Binary;

import java.io.StreamCorruptedException;

public class BinaryUtils {

    public static byte[] getBinaryData(Object unsafeBinary) throws StreamCorruptedException {
        if (unsafeBinary instanceof byte[] bytes)
            return bytes;
        if (unsafeBinary instanceof Binary binary)
            return binary.getData();
        throw new StreamCorruptedException("Cannot retrieve valid binary from document!");
    }

}
