package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKServer;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("Close Mongo client")
@Description({
    "Closes the connection to a Mongo server and releases its resources.",
    "The client cannot be used anymore once closed: create a new one with the Mongo server expression if needed."
})
@Examples({
    "on script unload:",
    "\tclose mongo client {mongoclient}"
})
@Since("2.5.0")
public class EffCloseMongoServer extends Effect {

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(org.skriptlang.skript.registration.SyntaxRegistry.EFFECT, org.skriptlang.skript.registration.SyntaxInfo.builder(EffCloseMongoServer.class).addPatterns("close [the] mongo[(sk|db)] (client|server|connection)[s] %mongoskservers%").build());
    }

    private Expression<MongoSKServer> exprMongoSKServer;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprMongoSKServer = (Expression<MongoSKServer>) exprs[0];
        return true;
    }

    @Override
    protected void execute(@Nonnull Event e) {
        for (MongoSKServer mongoSKServer : exprMongoSKServer.getArray(e))
            mongoSKServer.close();
    }

    @Nonnull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "close mongo client " + exprMongoSKServer.toString(e, debug);
    }
}
