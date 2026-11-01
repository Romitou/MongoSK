package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;
import java.util.Locale;

public class PotionEffectTypeCodec implements MongoSKCodec<PotionEffectType> {

    /**
     * @return the type with the given name, which is its upper case key
     */
    @Nullable
    static PotionEffectType typeFromName(String name) {
        NamespacedKey key = NamespacedKey.fromString(name.toLowerCase(Locale.ROOT));
        return key == null ? null : Registry.EFFECT.get(key);
    }

    static String nameOf(PotionEffectType potionEffectType) {
        return potionEffectType.getKey().getKey().toUpperCase(Locale.ROOT);
    }

    @NotNull
    @Override
    public PotionEffectType deserialize(Document document) throws StreamCorruptedException {
        String name = document.getString("name");
        if (name == null)
            throw new StreamCorruptedException("Cannot retrieve name field from document!");
        PotionEffectType potionEffectType = typeFromName(name);
        if (potionEffectType == null)
            throw new StreamCorruptedException("Cannot parse given potion name!");
        return potionEffectType;
    }

    @NotNull
    @Override
    public Document serialize(PotionEffectType potionEffectType) {
        Document document = new Document();
        document.put("name", nameOf(potionEffectType));
        return document;
    }

    @NotNull
    @Override
    public Class<? extends PotionEffectType> getReturnType() {
        return PotionEffectType.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "potionEffectType";
    }
}
