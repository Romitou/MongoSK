package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.Time;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class TimeCodec implements MongoSKCodec<Time> {
    @Nonnull
    @Override
    public Time deserialize(Document document) throws StreamCorruptedException {
        Integer time = document.getInteger("time");
        if (time == null)
            throw new StreamCorruptedException("Cannot retrieve time field from document!");
        return new Time(time);
    }

    @Nonnull
    @Override
    public Document serialize(Time time) {
        Document document = new Document();
        document.put("time", time.getTime());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends Time> getReturnType() {
        return Time.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "time";
    }
}
