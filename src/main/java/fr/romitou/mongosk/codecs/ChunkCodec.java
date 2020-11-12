package fr.romitou.mongosk.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkCodec implements Codec<Chunk> {
    @Override
    public Chunk decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final World world = Bukkit.getWorld(reader.readString("world"));
        final int x = reader.readInt32("x"),
                z = reader.readInt32("z");
        reader.readEndDocument();
        assert world != null;
        return world.getChunkAt(x, z);
    }

    @Override
    public void encode(BsonWriter writer, Chunk chunk, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("world", chunk.getWorld().getName());
        writer.writeInt32("x", chunk.getX());
        writer.writeInt32("z", chunk.getZ());
        writer.writeEndDocument();
    }

    @Override
    public Class<Chunk> getEncoderClass() {
        return Chunk.class;
    }
}
