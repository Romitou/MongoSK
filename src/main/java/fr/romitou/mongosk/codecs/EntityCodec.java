package fr.romitou.mongosk.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class EntityCodec implements Codec<Entity> {
    @Override
    public Entity decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        Entity entity = Bukkit.getEntity(UUID.fromString(reader.readString("uuid")));
        reader.readEndDocument();
        assert entity != null;
        return entity;
    }

    @Override
    public void encode(BsonWriter writer, Entity entity, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("uuid", entity.getUniqueId().toString());
        writer.writeEndDocument();
    }

    @Override
    public Class<Entity> getEncoderClass() {
        return Entity.class;
    }
}
