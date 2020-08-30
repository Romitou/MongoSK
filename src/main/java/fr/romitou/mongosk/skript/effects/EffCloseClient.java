package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoClient;
import fr.romitou.mongosk.skript.MongoManager;
import org.bukkit.event.Event;

public class EffCloseClient extends Effect {

    static {
        Skript.registerEffect(EffCloseClient.class, "close [mongo[db]] connection [of] %mongoclient%");
    }

    private Expression<MongoClient> exprClient;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprClient = (Expression<MongoClient>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event e) {
        MongoClient client = exprClient.getSingle(e);
        if (client == null)
            return;
        client.close();
        MongoManager.removeClient(client);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "close " + exprClient.toString(e, debug);
    }

}
