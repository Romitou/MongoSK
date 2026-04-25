package fr.romitou.mongosk.utils;

import org.bson.types.Binary;

import java.io.StreamCorruptedException;

public class BinaryUtils {

    public static byte[] getBinaryData(Object unsafeBinary) throws StreamCorruptedException {
        if (unsafeBinary instanceof byte[])
            return (byte[]) unsafeBinary;
        else if (unsafeBinary instanceof Binary)
            return ((Binary) unsafeBinary).getData();
        throw new StreamCorruptedException("Cannot retrieve valid binary from document!");
    }

}
