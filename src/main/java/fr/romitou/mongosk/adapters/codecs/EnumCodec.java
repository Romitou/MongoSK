package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public abstract class EnumCodec<T extends Enum<T>> implements MongoSKCodec<T> {

    private final Class<T> clazz;
    private final String name;

    protected EnumCodec(Class<T> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Nonnull
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

    @Nonnull
    @Override
    public Document serialize(T object) {
        Document document = new Document();
        document.put("name", object.name());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends T> getReturnType() {
        return clazz;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }
}
