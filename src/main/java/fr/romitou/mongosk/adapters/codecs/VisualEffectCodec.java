package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.VisualEffect;
import ch.njol.skript.variables.SerializedVariable;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bson.types.Binary;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

/**
 * This class uses binaries fields with Skript serializers and deserializers.
 * The reason is that this object has many fields and it is better to let Skript handle it.
 */
public class VisualEffectCodec implements MongoSKCodec<VisualEffect> {
    @Nonnull
    @Override
    public VisualEffect deserialize(Document document) throws StreamCorruptedException {
        Binary binary = (Binary) document.get("binary");
        ClassInfo<?> classInfo = Classes.getExactClassInfo(VisualEffect.class);
        if (binary == null || classInfo == null)
            throw new StreamCorruptedException("Cannot retrieve binary field from document or Skript's VisualEffect class info!");
        Object deserialized = Classes.deserialize(classInfo, binary.getData());
        if (!(deserialized instanceof VisualEffect))
            throw new StreamCorruptedException("Cannot parse given binary to get an VisualEffect!");
        return (VisualEffect) deserialized;
    }

    @Nonnull
    @Override
    public Document serialize(VisualEffect visualEffect) {
        Document document = new Document();
        SerializedVariable.Value serialized = Classes.serialize(visualEffect);
        if (serialized == null)
            return new Document();
        document.put("binary", serialized.data);
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends VisualEffect> getReturnType() {
        return VisualEffect.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "visualEffect";
    }
}
