package fr.romitou.mongosk.adapters.codecs;

import org.bukkit.inventory.ItemStack;

/**
 * This class uses binaries fields with Skript serializers and deserializers.
 * The reason is that storing in binary will take up less space than storing all fields raw.
 */
public class ItemStackCodec extends BinaryCodec<ItemStack> {

    public ItemStackCodec() {
        super(ItemStack.class, "itemStack");
    }

}
