package fr.romitou.mongosk.adapters;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;

public interface MongoSKCodec<T> {

    @NotNull
    T deserialize(Document document) throws StreamCorruptedException;

    /**
     * @return the serialized document, or null if the object cannot be serialized
     */
    @Nullable
    Document serialize(T object);

    @NotNull
    Class<? extends T> getReturnType();

    @NotNull
    String getName();

}
