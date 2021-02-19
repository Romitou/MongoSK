package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
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
public class ItemTypeCodec implements MongoSKCodec<ItemType> {
    @Nonnull
    @Override
    public ItemType deserialize(Document document) throws StreamCorruptedException {
        Binary binary = (Binary) document.get("binary");
        ClassInfo<?> classInfo = Classes.getExactClassInfo(ItemType.class);
        if (binary == null || classInfo == null)
            throw new StreamCorruptedException("Cannot retrieve binary field from document or Skript's ItemType class info!");
        Object deserialized = Classes.deserialize(classInfo, binary.getData());
        if (!(deserialized instanceof ItemType))
            throw new StreamCorruptedException("Cannot parse given binary to get an ItemType!");
        return (ItemType) deserialized;
    }

    @Nonnull
    @Override
    public Document serialize(ItemType itemType) {
        Document document = new Document();
        SerializedVariable.Value serialized = Classes.serialize(itemType);
        if (serialized == null)
            return new Document();
        document.put("binary", serialized.data);
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "itemType";
    }
}
