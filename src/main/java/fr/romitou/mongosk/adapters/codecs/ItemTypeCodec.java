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
 * This class uses binaries fields with Skript serializers and deserializers in order to take up less database space.
 * Serialising fields in plain text like other codecs would be useless and counterproductive.
 */
public class ItemTypeCodec implements MongoSKCodec<ItemType> {
    @Nonnull
    @Override
    public ItemType deserialize(Document document) throws StreamCorruptedException {
        Binary binary = (Binary) document.get("binary");
        ClassInfo<?> classInfo = Classes.getExactClassInfo(ItemType.class);
        if (binary == null || classInfo == null)
            throw new StreamCorruptedException("Cannot retrieve binary field or ItemType class info!");
        Object deserialized = Classes.deserialize(classInfo, binary.getData());
        if (!(deserialized instanceof ItemType))
            throw new StreamCorruptedException("Deserialized object is not an ItemType!");
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
