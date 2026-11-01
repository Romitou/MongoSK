package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.Timeperiod;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class TimePeriodCodec implements MongoSKCodec<Timeperiod> {
    @NotNull
    @Override
    public Timeperiod deserialize(Document document) throws StreamCorruptedException {
        Integer start = document.getInteger("start"),
            end = document.getInteger("end");
        if (start == null || end == null)
            throw new StreamCorruptedException("Cannot retrieve start field or end field from document!");
        return new Timeperiod(start, end);
    }

    @NotNull
    @Override
    public Document serialize(Timeperiod timeperiod) {
        Document document = new Document();
        document.put("start", timeperiod.start);
        document.put("end", timeperiod.end);
        return document;
    }

    @NotNull
    @Override
    public Class<? extends Timeperiod> getReturnType() {
        return Timeperiod.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "timePeriod";
    }
}
