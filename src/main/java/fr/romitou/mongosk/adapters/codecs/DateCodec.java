package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.Date;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class DateCodec implements MongoSKCodec<Date> {
    @Nonnull
    @Override
    public Date deserialize(Document document) throws StreamCorruptedException {
        Long timestamp = document.getLong("timestamp");
        if (timestamp == null)
            throw new StreamCorruptedException("Cannot retrieve timestamp field from document!");
        return new Date(timestamp);
    }

    @Nonnull
    @Override
    public Document serialize(Date date) {
        Document document = new Document();
        document.put("timestamp", date.getTimestamp());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends Date> getReturnType() {
        return Date.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "date";
    }
}
