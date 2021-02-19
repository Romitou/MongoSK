package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.block.Biome;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class BiomeCodec implements MongoSKCodec<Biome> {
    @Nonnull
    @Override
    public Biome deserialize(Document document) throws StreamCorruptedException {
        String biomeName = document.getString("biomeName");
        if (biomeName == null)
            throw new StreamCorruptedException("Cannot retrieve biome name from document!");
        return Biome.valueOf(biomeName);
    }

    @Nonnull
    @Override
    public Document serialize(Biome biome) {
        Document document = new Document();
        document.put("biomeName", biome.name());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends Biome> getReturnType() {
        return Biome.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "biome";
    }
}
