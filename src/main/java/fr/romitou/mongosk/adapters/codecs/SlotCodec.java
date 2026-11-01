package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.slot.Slot;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;

/**
 * This class uses binaries fields with Skript serializers and deserializers.
 * The reason is that storing in binary will take up less space than storing all fields raw.
 */
public class SlotCodec implements MongoSKCodec<Object> {
    /**
     * A slot is stored as its item, so it is read back as an item stack.
     */
    @NotNull
    @Override
    public Object deserialize(Document document) throws StreamCorruptedException {
        MongoSKCodec<ItemStack> codec = MongoSKAdapter.getCodecByClass(ItemStack.class);
        if (codec == null)
            throw new StreamCorruptedException("The item stack codec is required to read a slot!");
        return codec.deserialize(document);
    }

    @Nullable
    @Override
    public Document serialize(Object slot) {
        MongoSKCodec<ItemStack> codec = MongoSKAdapter.getCodecByClass(ItemStack.class);
        if (codec == null)
            return null;
        return codec.serialize(((Slot) slot).getItem());
    }

    @NotNull
    @Override
    public Class<?> getReturnType() {
        return Slot.class;
    }

    @NotNull
    @Override
    public String getName() {
        // Tell MongoSK to deserialize this Slot as an ItemStack.
        return "itemStack";
    }
}
