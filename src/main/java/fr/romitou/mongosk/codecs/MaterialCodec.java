package fr.romitou.mongosk.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Material;

public class MaterialCodec implements Codec<Material> {
    @Override
    public Material decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final int ordinal = reader.readInt32();
        reader.readEndDocument();
        return Material.values()[ordinal];
    }

    @Override
    public void encode(BsonWriter writer, Material material, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeInt32(material.ordinal());
        writer.writeEndDocument();
    }

    @Override
    public Class<Material> getEncoderClass() {
        return Material.class;
    }
}
