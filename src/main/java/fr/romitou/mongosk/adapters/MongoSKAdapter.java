package fr.romitou.mongosk.adapters;

import fr.romitou.mongosk.adapters.codecs.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MongoSKAdapter {

    public static List<MongoSKCodec<?>> codecs = new ArrayList<>();

    public static List<String> loadCodecs() {
        codecs.add(new BiomeCodec());
        codecs.add(new BlockCodec());
        codecs.add(new DamageCauseCodec());
        codecs.add(new EntityCodec());
        codecs.add(new ItemTypeCodec());
        codecs.add(new ItemStackCodec());
        codecs.add(new MaterialCodec());
        codecs.add(new PlayerCodec());
        return getCodecNames();
    }

    public static List<String> getCodecNames() {
        return codecs.stream()
            .map(MongoSKCodec::getName)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> MongoSKCodec<T> getCodecByName(String name) {
        return (MongoSKCodec<T>) codecs.stream()
            .filter(codec -> codec.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> MongoSKCodec<T> getCodecByClass(Class<? extends T> clazz) {
        return (MongoSKCodec<T>) codecs.stream()
            .filter(codec -> codec.getReturnType().isAssignableFrom(clazz))
            .findFirst()
            .orElse(null);
    }

}
