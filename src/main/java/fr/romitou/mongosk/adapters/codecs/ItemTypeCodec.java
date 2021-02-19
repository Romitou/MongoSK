package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.aliases.ItemType;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class ItemTypeCodec implements MongoSKCodec<ItemType> {
    @Nonnull
    @Override
    public ItemType deserialize(Document document) throws StreamCorruptedException {
        // Possible errors are always thrown by ItemStackCodec.
        return new ItemType(new ItemStackCodec().deserialize(document));
    }

    @Nonnull
    @Override
    public Document serialize(ItemType itemType) {
        ItemStack randomItemStack = itemType.getRandom();
        if (randomItemStack == null)
            return new Document();
        return new ItemStackCodec().serialize(randomItemStack);
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
