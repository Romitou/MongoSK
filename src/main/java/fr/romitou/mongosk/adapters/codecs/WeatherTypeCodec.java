package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.WeatherType;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class WeatherTypeCodec implements MongoSKCodec<WeatherType> {
    @Nonnull
    @Override
    public WeatherType deserialize(Document document) throws StreamCorruptedException {
        String name = document.getString("name");
        if (name == null)
            throw new StreamCorruptedException("Cannot retrieve name field from document!");
        WeatherType weatherType = WeatherType.parse(name);
        if (weatherType == null)
            throw new StreamCorruptedException("Cannot parse given weather name!");
        return weatherType;
    }

    @Nonnull
    @Override
    public Document serialize(WeatherType weatherType) {
        Document document = new Document();
        document.put("name", weatherType.name());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends WeatherType> getReturnType() {
        return WeatherType.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "weatherType";
    }
}
