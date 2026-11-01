package fr.romitou.mongosk.adapters;

import fr.romitou.mongosk.adapters.codecs.EntityCodec;
import fr.romitou.mongosk.adapters.codecs.PlayerCodec;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CodecsTest {

    @Test
    public void testEntityCodecStillHandlesPlayers() {
        // Players were always stored with the entity codec (it is registered before the player codec): this must stay
        // the case, otherwise filters on existing documents would no longer match.
        assertTrue(new EntityCodec().getReturnType().isAssignableFrom(Player.class));
        assertEquals(Entity.class, new EntityCodec().getReturnType());
        assertEquals(OfflinePlayer.class, new PlayerCodec().getReturnType());
    }
}
