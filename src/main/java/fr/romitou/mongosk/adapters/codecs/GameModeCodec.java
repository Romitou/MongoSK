package fr.romitou.mongosk.adapters.codecs;

import org.bukkit.GameMode;

public class GameModeCodec extends EnumCodec<GameMode> {
    public GameModeCodec() {
        super(GameMode.class, "gameMode");
    }
}
