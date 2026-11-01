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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Close Mongo client")
@Description({
    "Closes the connection to a Mongo server and releases its resources.",
    "The client cannot be used anymore once closed: create a new one with the Mongo server expression if needed."
})
@Examples({
    "on script unload:",
    "\tclose mongo client {mongoclient}"
})
@Since("3.0.0")
public class EffCloseMongoServer extends Effect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffCloseMongoServer.class)
            .addPatterns("close [the] mongo[(sk|db)] (client|server|connection)[s] %mongoskservers%")
            .build());
    }

    private Expression<MongoSKServer> exprMongoSKServer;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprMongoSKServer = (Expression<MongoSKServer>) exprs[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event e) {
        for (MongoSKServer mongoSKServer : exprMongoSKServer.getArray(e))
            mongoSKServer.close();
    }

    @NotNull
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "close mongo client " + exprMongoSKServer.toString(e, debug);
    }
}
