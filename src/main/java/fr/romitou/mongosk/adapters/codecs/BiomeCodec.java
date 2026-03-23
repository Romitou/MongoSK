package fr.romitou.mongosk.adapters.codecs;

import org.bukkit.block.Biome;

public class BiomeCodec extends EnumCodec<Biome> {
    public BiomeCodec() {
        super(Biome.class, "biome");
    }
}
