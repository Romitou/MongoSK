package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class PotionEffectTypeCodec implements MongoSKCodec<PotionEffectType> {
    @Nonnull
    @Override
    public PotionEffectType deserialize(Document document) throws StreamCorruptedException {
        String name = document.getString("name");
        if (name == null)
            throw new StreamCorruptedException("Cannot retrieve name field from document!");
        PotionEffectType potionEffectType = PotionEffectType.getByName(name);
        if (potionEffectType == null)
            throw new StreamCorruptedException("Cannot parse given potion name!");
        return potionEffectType;
    }

    @Nonnull
    @Override
    public Document serialize(PotionEffectType potionEffectType) {
        Document document = new Document();
        document.put("name", potionEffectType.getName());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends PotionEffectType> getReturnType() {
        return PotionEffectType.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "potionEffectType";
    }
}
