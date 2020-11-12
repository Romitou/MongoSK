package fr.romitou.mongosk.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockCodec implements Codec<Block> {
    @Override
    public Block decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final World world = Bukkit.getWorld(reader.readString("world"));
        final int x = reader.readInt32("x"),
                y = reader.readInt32("y"),
                z = reader.readInt32("z");
        reader.readEndDocument();
        assert world != null;
        return world.getBlockAt(x, y, z);
    }

    @Override
    public void encode(BsonWriter writer, Block block, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("world", block.getWorld().getName());
        writer.writeInt32("x", block.getX());
        writer.writeInt32("y", block.getY());
        writer.writeInt32("z", block.getZ());
        writer.writeEndDocument();
    }

    @Override
    public Class<Block> getEncoderClass() {
        return Block.class;
    }
}
