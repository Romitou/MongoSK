package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.Timespan;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class TimespanCodec implements MongoSKCodec<Timespan> {
    @Nonnull
    @Override
    public Timespan deserialize(Document document) throws StreamCorruptedException {
        Long millis = document.getLong("millis");
        if (millis == null)
            throw new StreamCorruptedException("Cannot retrieve millis field from document!");
        return new Timespan(millis);
    }

    @Nonnull
    @Override
    public Document serialize(Timespan timespan) {
        Document document = new Document();
        document.put("millis", timespan.getMilliSeconds());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "timespan";
    }
}
