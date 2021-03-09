package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.slot.Slot;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * This class uses binaries fields with Skript serializers and deserializers.
 * The reason is that storing in binary will take up less space than storing all fields raw.
 */
public class SlotCodec implements MongoSKCodec<Slot> {
    @Nonnull
    @Override
    public Slot deserialize(Document document) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Slot cannot be deserialized!");
    }

    @Nonnull
    @Override
    public Document serialize(Slot slot) {
        MongoSKCodec<ItemStack> codec = MongoSKAdapter.getCodecByClass(ItemStack.class);
        if (codec == null)
            return new Document();
        return codec.serialize(slot.getItem());
    }

    @Nonnull
    @Override
    public Class<? extends Slot> getReturnType() {
        return Slot.class;
    }

    @Nonnull
    @Override
    public String getName() {
        // Tell MongoSK to deserialize this Slot as an ItemStack.
        return "itemStack";
    }
}
