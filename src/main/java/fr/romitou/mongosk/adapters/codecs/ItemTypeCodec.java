package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.aliases.ItemType;

/**
 * This class uses binaries fields with Skript serializers and deserializers.
 * The reason is that storing in binary will take up less space than storing all fields raw.
 */
public class ItemTypeCodec extends BinaryCodec<ItemType> {

    public ItemTypeCodec() {
        super(ItemType.class, "itemType");
    }

    @Override
    protected ItemType processDeserialized(ItemType deserialized) {
        return deserialized.getItem();
    }
}
