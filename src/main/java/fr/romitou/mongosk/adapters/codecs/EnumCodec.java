package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public abstract class EnumCodec<T extends Enum<T>> implements MongoSKCodec<T> {

    private final Class<T> clazz;
    private final String name;

    protected EnumCodec(Class<T> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @NotNull
    @Override
    public T deserialize(Document document) throws StreamCorruptedException {
        String enumName = document.getString("name");
        if (enumName == null)
            throw new StreamCorruptedException("Cannot retrieve name field from document!");
        try {
            return Enum.valueOf(clazz, enumName);
        } catch (IllegalArgumentException e) {
            throw new StreamCorruptedException("Cannot parse given name to get an " + clazz.getSimpleName() + "!");
        }
    }

    @NotNull
    @Override
    public Document serialize(T object) {
        Document document = new Document();
        document.put("name", object.name());
        return document;
    }

    @NotNull
    @Override
    public Class<? extends T> getReturnType() {
        return clazz;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }
}
