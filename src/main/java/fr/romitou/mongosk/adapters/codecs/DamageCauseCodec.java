package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class DamageCauseCodec implements MongoSKCodec<EntityDamageEvent.DamageCause> {
    @Nonnull
    @Override
    public EntityDamageEvent.DamageCause deserialize(Document document) throws StreamCorruptedException {
        String damageCauseName = document.getString("damageCauseName");
        if (damageCauseName == null)
            throw new StreamCorruptedException("Cannot retrieve damage cause name from document!");
        return EntityDamageEvent.DamageCause.valueOf(damageCauseName);
    }

    @Nonnull
    @Override
    public Document serialize(EntityDamageEvent.DamageCause damageCause) {
        Document document = new Document();
        document.put("damageCauseName", damageCause.name());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends EntityDamageEvent.DamageCause> getReturnType() {
        return EntityDamageEvent.DamageCause.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "damageCause";
    }
}
