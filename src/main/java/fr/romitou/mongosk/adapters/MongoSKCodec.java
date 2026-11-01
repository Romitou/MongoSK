package fr.romitou.mongosk.adapters;

import org.bson.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.StreamCorruptedException;

public interface MongoSKCodec<T> {

    @Nonnull
    T deserialize(Document document) throws StreamCorruptedException;

    /**
     * @return the serialized document, or null if the object cannot be serialized
     */
    @Nullable
    Document serialize(T object);

    @Nonnull
    Class<? extends T> getReturnType();

    @Nonnull
    String getName();

}
