package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class PotionEffectCodec implements MongoSKCodec<PotionEffect> {
    @Nonnull
    @Override
    public PotionEffect deserialize(Document document) throws StreamCorruptedException {
        String type = document.getString("type");
        Integer duration = document.getInteger("duration"),
            amplifier = document.getInteger("amplifier");
        Boolean isAmbient = document.getBoolean("isAmbient"),
            hasParticles = document.getBoolean("hasParticles"),
            hasIcon = document.getBoolean("hasIcon");
        if (type == null || duration == null || amplifier == null || isAmbient == null || hasParticles == null || hasIcon == null)
            throw new StreamCorruptedException("Cannot retrieve type, duration, amplifier, isAmbient, hasParticles or hasIcon fields!");
        PotionEffectType potionEffectType = PotionEffectType.getByName(type);
        if (potionEffectType == null)
            throw new StreamCorruptedException("Cannot parse given potion type!");
        return new PotionEffect(potionEffectType, duration, amplifier, isAmbient, hasParticles, hasIcon);
    }

    @Nonnull
    @Override
    public Document serialize(PotionEffect potionEffect) {
        Document document = new Document();
        document.put("type", potionEffect.getType().getName());
        document.put("duration", potionEffect.getDuration());
        document.put("amplifier", potionEffect.getAmplifier());
        document.put("isAmbient", potionEffect.isAmbient());
        document.put("hasParticles", potionEffect.hasParticles());
        document.put("hasIcon", potionEffect.hasIcon());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends PotionEffect> getReturnType() {
        return PotionEffect.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "potionEffect";
    }
}
