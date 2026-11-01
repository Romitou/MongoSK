package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;
import java.util.Locale;

public class BiomeCodec implements MongoSKCodec<Biome> {
    @NotNull
    @Override
    public Biome deserialize(Document document) throws StreamCorruptedException {
        String name = document.getString("name");
        if (name == null)
            throw new StreamCorruptedException("Cannot retrieve name field from document!");
        Biome biome = Registry.BIOME.get(NamespacedKey.minecraft(name.toLowerCase(Locale.ROOT)));
        if (biome == null)
            throw new StreamCorruptedException("Cannot parse given biome name!");
        return biome;
    }

    @NotNull
    @Override
    public Document serialize(Biome biome) {
        Document document = new Document();
        document.put("name", biome.getKey().getKey());
        return document;
    }

    @NotNull
    @Override
    public Class<? extends Biome> getReturnType() {
        return Biome.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "biome";
    }
}
