package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.util.WeatherType;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class WeatherTypeCodec implements MongoSKCodec<WeatherType> {
    @NotNull
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

    @NotNull
    @Override
    public Document serialize(WeatherType weatherType) {
        Document document = new Document();
        document.put("name", weatherType.name());
        return document;
    }

    @NotNull
    @Override
    public Class<? extends WeatherType> getReturnType() {
        return WeatherType.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "weatherType";
    }
}
