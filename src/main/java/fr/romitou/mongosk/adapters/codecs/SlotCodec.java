package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.slot.Slot;
import ch.njol.skript.variables.SerializedVariable;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bson.types.Binary;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

/**
 * This class uses binaries fields with Skript serializers and deserializers.
 * The reason is that storing in binary will take up less space than storing all fields raw.
 */
public class SlotCodec implements MongoSKCodec<Slot> {
    @Nonnull
    @Override
    public Slot deserialize(Document document) throws StreamCorruptedException {
        Binary binary = (Binary) document.get("binary");
        ClassInfo<?> classInfo = Classes.getExactClassInfo(Slot.class);
        if (binary == null || classInfo == null)
            throw new StreamCorruptedException("Cannot retrieve binary field from document or Skript's Slot class info!");
        Object deserialized = Classes.deserialize(classInfo, binary.getData());
        if (!(deserialized instanceof Slot))
            throw new StreamCorruptedException("Cannot parse given binary to get an Slot!");
        return (Slot) deserialized;
    }

    @Nonnull
    @Override
    public Document serialize(Slot slot) {
        Document document = new Document();
        SerializedVariable.Value serialized = Classes.serialize(slot);
        if (serialized == null)
            return new Document();
        document.put("binary", serialized.data);
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends Slot> getReturnType() {
        return Slot.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "slot";
    }
}
