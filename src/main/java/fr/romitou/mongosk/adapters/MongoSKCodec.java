package fr.romitou.mongosk.adapters;

import org.bson.Document;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public interface MongoSKCodec<T> {

    @Nonnull
    T deserialize(Document document) throws StreamCorruptedException;

    @Nonnull
    Document serialize(T object);

    @Nonnull
    Class<? extends T> getReturnType();

    @Nonnull
    String getName();

}
