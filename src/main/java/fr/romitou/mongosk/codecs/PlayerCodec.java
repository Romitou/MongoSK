package fr.romitou.mongosk.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerCodec implements Codec<OfflinePlayer> {
    @Override
    public OfflinePlayer decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(reader.readString()));
        reader.readEndDocument();
        return player;
    }

    @Override
    public void encode(BsonWriter writer, OfflinePlayer player, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString(player.getUniqueId().toString());
        writer.writeEndDocument();
    }

    @Override
    public Class<OfflinePlayer> getEncoderClass() {
        return OfflinePlayer.class;
    }
}
