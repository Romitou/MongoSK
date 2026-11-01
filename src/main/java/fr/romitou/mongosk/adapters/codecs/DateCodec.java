package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.Date;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class DateCodec implements MongoSKCodec<Date> {
    @NotNull
    @Override
    public Date deserialize(Document document) throws StreamCorruptedException {
        Long timestamp = document.getLong("timestamp");
        if (timestamp == null)
            throw new StreamCorruptedException("Cannot retrieve timestamp field from document!");
        return new Date(timestamp);
    }

    @NotNull
    @Override
    public Document serialize(Date date) {
        Document document = new Document();
        document.put("timestamp", date.getTime());
        return document;
    }

    @NotNull
    @Override
    public Class<? extends Date> getReturnType() {
        return Date.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "date";
    }
}
