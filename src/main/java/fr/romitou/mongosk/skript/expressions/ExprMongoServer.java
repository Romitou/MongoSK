package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.elements.MongoSKServer;
import fr.romitou.mongosk.listeners.MongoCommandListener;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

@Name("Mongo server")
@Description("Create a connection to your remote MongoDB host using this expression." +
    "This syntax requires to specify a valid MongoDB connection string." +
    "Don't forget to include the 'appName' option in order to improve MongoSK reports in case of problems and to identify requests to your server monitoring." +
    "You can consult the attached examples or the official documentation: https://docs.mongodb.com/manual/reference/connection-string/.")
@Examples({"on script load:",
    "set {mongoclient} to a new mongosk client with connection string \"mongodb://romitou:mysupersecretpassword@127.0.0.1/?appName=myApp&retryWrites=true&w=majority\""})
@Since("2.0.0")
public class ExprMongoServer extends SimpleExpression<MongoSKServer> {

    static {
        Skript.registerExpression(
            ExprMongoServer.class,
            MongoSKServer.class,
            ExpressionType.COMBINED,
            "[[a] new] mongo[(sk|db)] (client|server) (with|from) [connection (string|uri|address)] %string%"
        );
    }

    private Expression<String> exprRawConnectionString;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprRawConnectionString = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected MongoSKServer[] get(@Nonnull final Event e) {
        String rawConnectionString = exprRawConnectionString.getSingle(e);
        if (rawConnectionString == null)
            return new MongoSKServer[0];

        // Validate the connection string.
        ConnectionString connectionString;
        try {
            connectionString = new ConnectionString(rawConnectionString);
        } catch (IllegalArgumentException exception) {
            Logger.severe("Your connection string is invalid: " + exception.getMessage().toLowerCase(Locale.ROOT));
            return new MongoSKServer[0];
        }

        // Create listeners.
        MongoCommandListener mongoCommandListener = new MongoCommandListener();

        // Build the Mongo client settings.
        MongoClientSettings.Builder settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .addCommandListener(mongoCommandListener);
        MongoClient mongoClient = MongoClients.create(settings.build());

        // Get the most pertinent displayed name for this client.
        String displayedName = connectionString.getApplicationName() == null
            ? connectionString.getHosts().get(0)
            : connectionString.getApplicationName();

        // Create the MongoSKServer object.
        MongoSKServer mongoSKServer = new MongoSKServer(displayedName, mongoClient);

        // Add this server to the listeners (we must do this because the client is only created after the builder).
        mongoCommandListener.setMongoSKServer(mongoSKServer);

        // Return the MongoSKServer linked with the listeners.
        return new MongoSKServer[]{mongoSKServer};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @Nonnull
    public Class<? extends MongoSKServer> getReturnType() {
        return MongoSKServer.class;
    }

    @Override
    @Nonnull
    public String toString(@Nullable Event e, boolean debug) {
        return "new mongosk server from connection string " + exprRawConnectionString.toString(e, debug);
    }
}
