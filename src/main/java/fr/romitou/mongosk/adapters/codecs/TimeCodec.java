package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.Time;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class TimeCodec implements MongoSKCodec<Time> {
    @NotNull
    @Override
    public Time deserialize(Document document) throws StreamCorruptedException {
        Integer time = document.getInteger("time");
        if (time == null)
            throw new StreamCorruptedException("Cannot retrieve time field from document!");
        return new Time(time);
    }

    @NotNull
    @Override
    public Document serialize(Time time) {
        Document document = new Document();
        document.put("time", time.getTime());
        return document;
    }

    @NotNull
    @Override
    public Class<? extends Time> getReturnType() {
        return Time.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "time";
    }
}
