package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.GameMode;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class GameModeCodec implements MongoSKCodec<GameMode> {
    @Nonnull
    @Override
    public GameMode deserialize(Document document) throws StreamCorruptedException {
        String name = document.getString("name");
        if (name == null)
            throw new StreamCorruptedException("Cannot retrieve name field from document!");
        return GameMode.valueOf(name);
    }

    @Nonnull
    @Override
    public Document serialize(GameMode gameMode) {
        Document document = new Document();
        document.put("name", gameMode.name());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends GameMode> getReturnType() {
        return GameMode.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "gameMode";
    }
}
