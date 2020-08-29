package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import fr.romitou.mongosk.skript.MongoManager;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.event.Event;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class EffCreateClient extends Effect {

    static {
        Skript.registerEffect(EffCreateClient.class,
                "connect to [the] mongo[db] [(server|host)] %string% (named|with name|as) %string%",
                "create [a] [new] mongo[db] client to [(server|host)] %string% (named|with name|as) %string%");
    }

    private Expression<String> exprUri, exprName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprUri = (Expression<String>) exprs[0];
        exprName = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event e) {
        String uri = exprUri.getSingle(e);
        String name = exprName.getSingle(e);
        if (uri == null || name == null)
            return;
        try {
            MongoManager.addClient(MongoClients.create(
                    MongoClientSettings
                            .builder()
                            .codecRegistry(
                                    fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                                            fromProviders(PojoCodecProvider.builder().automatic(true).build())))
                            .applyConnectionString(new ConnectionString(uri))
                            .build()
            ), name);
        } catch (IllegalArgumentException ex) {
            Skript.error("Something went wrong. " + ex.getMessage());
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "create a new mongo client to server " + exprUri.toString(e, debug) + " with name " + exprName.toString(e, debug);
    }

}
