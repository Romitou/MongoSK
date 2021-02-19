package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.SerializedVariable;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bson.types.Binary;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

/**
 * This class uses binaries fields with Skript serializers and deserializers.
 * The reason is that storing in binary will take up less space than storing all fields raw.
 */
public class ItemStackCodec implements MongoSKCodec<ItemStack> {

    @Nonnull
    @Override
    public ItemStack deserialize(Document document) throws StreamCorruptedException {
        Binary binary = (Binary) document.get("binary");
        ClassInfo<?> classInfo = Classes.getExactClassInfo(ItemStack.class);
        if (binary == null || classInfo == null)
            throw new StreamCorruptedException("Cannot retrieve binary field from document or Skript's ItemType class info!");
        Object deserialized = Classes.deserialize(classInfo, binary.getData());
        if (!(deserialized instanceof ItemStack))
            throw new StreamCorruptedException("Cannot parse given binary to get an ItemStack!");
        return (ItemStack) deserialized;
    }

    @Nonnull
    @Override
    public Document serialize(ItemStack itemStack) {
        Document document = new Document();
        SerializedVariable.Value serialized = Classes.serialize(itemStack);
        if (serialized == null)
            return new Document();
        document.put("binary", serialized.data);
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "itemStack";
    }
}
