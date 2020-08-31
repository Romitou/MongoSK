package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoClient;
import fr.romitou.mongosk.skript.MongoManager;
import org.bukkit.event.Event;

@Name("Close Mongo Client")
@Description("This effect allows you to close the connection of a Mongo client to the host. This means that no more queries can be made with this client.")
@Examples("close mongo connection of client named \"mongosk\"")
@Since("1.0.0")
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
