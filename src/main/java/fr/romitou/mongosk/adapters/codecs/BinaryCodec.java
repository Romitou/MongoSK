package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.SerializedVariable;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;

/**
 * Abstract class for codecs that use binary fields with Skript serializers and deserializers.
 * The reason is that storing in binary will take up less space than storing all fields raw.
 */
public abstract class BinaryCodec<T> implements MongoSKCodec<T> {

    private final Class<T> clazz;
    private final String name;

    protected BinaryCodec(Class<T> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @NotNull
    @Override
    public T deserialize(Document document) throws StreamCorruptedException {
        Object unsafeObject = document.get("binary");
        byte[] byteData = MongoSKAdapter.getBinaryData(unsafeObject);
        ClassInfo<?> classInfo = Classes.getExactClassInfo(clazz);
        if (byteData == null || classInfo == null)
            throw new StreamCorruptedException("Cannot retrieve binary field from document or Skript's " + clazz.getSimpleName() + " class info!");
        Object deserialized = Classes.deserialize(classInfo, byteData);
        if (!clazz.isInstance(deserialized))
            throw new StreamCorruptedException("Cannot parse given binary to get an " + clazz.getSimpleName() + "!");

        return processDeserialized(clazz.cast(deserialized));
    }

    protected T processDeserialized(T deserialized) {
        return deserialized;
    }

    @Nullable
    @Override
    public Document serialize(T object) {
        Document document = new Document();
        SerializedVariable.Value serialized = Classes.serialize(object);
        if (serialized == null)
            return null;
        document.put("binary", serialized.data);
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
