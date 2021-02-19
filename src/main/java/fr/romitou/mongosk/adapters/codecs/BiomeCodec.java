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
        String name = document.getString("name");
        if (name == null)
            throw new StreamCorruptedException("Cannot retrieve name field from document!");
        return Biome.valueOf(name);
    }

    @Nonnull
    @Override
    public Document serialize(Biome biome) {
        Document document = new Document();
        document.put("name", biome.name());
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
