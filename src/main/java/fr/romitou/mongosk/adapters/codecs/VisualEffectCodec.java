package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.visual.VisualEffect;

/**
 * This class uses binary fields with Skript serializers and deserializers.
 * The reason is that this object has many fields and it is better to let Skript handle it.
 */
public class VisualEffectCodec extends BinaryCodec<VisualEffect> {

    public VisualEffectCodec() {
        super(VisualEffect.class, "visualEffect");
    }

}
