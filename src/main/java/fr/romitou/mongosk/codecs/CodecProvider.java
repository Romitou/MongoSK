package fr.romitou.mongosk.codecs;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CodecProvider implements org.bson.codecs.configuration.CodecProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (Block.class.isAssignableFrom(clazz)) return (Codec<T>) new BlockCodec();
        if (Chunk.class.isAssignableFrom(clazz)) return (Codec<T>) new ChunkCodec();
        if (Enchantment.class.isAssignableFrom(clazz)) return (Codec<T>) new EnchantmentCodec();
        if (Entity.class.isAssignableFrom(clazz)) return (Codec<T>) new EntityCodec();
        if (Location.class.isAssignableFrom(clazz)) return (Codec<T>) new LocationCodec();
        if (Material.class.isAssignableFrom(clazz)) return (Codec<T>) new MaterialCodec();
        if (OfflinePlayer.class.isAssignableFrom(clazz)) return (Codec<T>) new PlayerCodec();
        if (Player.class.isAssignableFrom(clazz)) return (Codec<T>) new PlayerCodec();
        if (World.class.isAssignableFrom(clazz)) return (Codec<T>) new WorldCodec();
        return null;
    }
}
