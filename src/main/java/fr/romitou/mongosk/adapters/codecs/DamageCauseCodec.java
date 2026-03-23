package fr.romitou.mongosk.adapters.codecs;

import org.bukkit.event.entity.EntityDamageEvent;

public class DamageCauseCodec extends EnumCodec<EntityDamageEvent.DamageCause> {
    public DamageCauseCodec() {
        super(EntityDamageEvent.DamageCause.class, "damageCause");
    }
}
