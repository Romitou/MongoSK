package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.Experience;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class ExperienceCodec implements MongoSKCodec<Experience> {
    @NotNull
    @Override
    public Experience deserialize(Document document) throws StreamCorruptedException {
        Integer xp = document.getInteger("xp");
        if (xp == null)
            throw new StreamCorruptedException("Cannot retrieve XP field from document!");
        return new Experience(xp);
    }

    @NotNull
    @Override
    public Document serialize(Experience experience) {
        Document document = new Document();
        document.put("xp", experience.getXP());
        return document;
    }

    @NotNull
    @Override
    public Class<? extends Experience> getReturnType() {
        return Experience.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "experience";
    }
}
