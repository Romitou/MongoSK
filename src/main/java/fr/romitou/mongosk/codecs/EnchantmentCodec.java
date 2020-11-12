package fr.romitou.mongosk.codecs;

import ch.njol.skript.bukkitutil.EnchantmentUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentCodec implements Codec<Enchantment> {
    @Override
    public Enchantment decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final String enchantment = reader.readString();
        reader.readEndDocument();
        assert enchantment != null;
        return EnchantmentUtils.getByKey(enchantment);
    }

    @Override
    public void encode(BsonWriter writer, Enchantment enchantment, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString(EnchantmentUtils.getKey(enchantment));
        writer.writeEndDocument();
    }

    @Override
    public Class<Enchantment> getEncoderClass() {
        return Enchantment.class;
    }
}
