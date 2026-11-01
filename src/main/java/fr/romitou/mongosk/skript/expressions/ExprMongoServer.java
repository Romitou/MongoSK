package fr.romitou.mongosk.skript.expressions;

import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import ch.njol.skript.lang.ExpressionType;
import org.skriptlang.skript.registration.SyntaxRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientException;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.SubscriberHelpers;
import fr.romitou.mongosk.elements.MongoSKServer;
import fr.romitou.mongosk.listeners.MongoCommandListener;
import org.bson.UuidRepresentation;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

@Name("Mongo server")
@Description({
    "Create a connection to your remote MongoDB host using this expression.",
    "This syntax requires you to specify a valid MongoDB connection string.",
    "A client is reused as long as it is open: evaluating this expression again with the same connection string returns the same client.",
    "Use the 'close mongo client' effect when you no longer need it, for example on script unload.",
    "Don't forget to include the 'appName' option in order to improve MongoSK reports in case of problems and to identify requests in your server monitoring.",
    "For more information, consult the official documentation: https://docs.mongodb.com/manual/reference/connection-string/."
})
@Examples({
    "on script load:",
    "	set {mongoclient} to a new mongosk client with connection string \"mongodb://<username>:<password>@127.0.0.1/?appName=myApp&retryWrites=true&w=majority\""
})
@Since("2.0.0")
public class ExprMongoServer extends SimpleExpression<MongoSKServer> {

    private final int CONNECT_TIMEOUT = MongoSK.getInstance().getConfig().getInt("timeouts.connect", 10000);
    private final int READ_TIMEOUT = MongoSK.getInstance().getConfig().getInt("timeouts.read", 10000);

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(SyntaxRegistry.EXPRESSION, DefaultSyntaxInfos.Expression.builder(ExprMongoServer.class, MongoSKServer.class).addPatterns("[[a] new] mongo[(sk|db)] (client|server) (with|from) [connection (string|uri|address)] %string%").priority(ExpressionType.SIMPLE.priority()).build());
    }

    private Expression<?> exprRawConnectionString;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprRawConnectionString = exprs[0];
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MongoSKServer[] get(@Nonnull final Event e) {
        String rawConnectionString = (exprRawConnectionString instanceof VariableString)
            ? ((VariableString) exprRawConnectionString).getSingle(e)
            : ((Expression<String>) exprRawConnectionString).getSingle(e);

        if (rawConnectionString == null)
            return new MongoSKServer[0];

        // Synchronized so that two concurrent evaluations cannot open two clients for the same connection string.
        synchronized (ExprMongoServer.class) {
            // Reuse the client already opened with this connection string, instead of opening a new connection pool.
            MongoSKServer openServer = MongoSK.getOpenServer(rawConnectionString);
            if (openServer != null)
                return new MongoSKServer[]{openServer};
            MongoSKServer mongoSKServer = createServer(rawConnectionString);
            return mongoSKServer == null ? new MongoSKServer[0] : new MongoSKServer[]{mongoSKServer};
        }
    }

    @Nullable
    private MongoSKServer createServer(String rawConnectionString) {
        // Validate the connection string.
        ConnectionString connectionString;
        try {
            connectionString = new ConnectionString(rawConnectionString);
        } catch (IllegalArgumentException ex) {
            SubscriberHelpers.setLastError(ex.getMessage());
            LoggerHelper.severe("Your connection string is invalid: " + ex.getMessage().toLowerCase());
            return null;
        } catch (MongoClientException ex) {
            SubscriberHelpers.setLastError(ex.getMessage());
            LoggerHelper.severe("An error occurred while creating the client: " + ex.getMessage().toLowerCase());
            return null;
        }

        // Create listeners.
        MongoCommandListener mongoCommandListener = new MongoCommandListener();

        // Build the Mongo client settings.
        MongoClientSettings.Builder settings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(connectionString)
            .applyToSocketSettings(builder -> builder
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS))
            .applicationName(connectionString.getApplicationName() == null
                ? "MongoSK/" + MongoSK.getInstance().getDescription().getVersion()
                : connectionString.getApplicationName())
            .addCommandListener(mongoCommandListener);
        MongoClient mongoClient = MongoClients.create(settings.build());

        // Get the most pertinent displayed name for this client.
        String displayedName = connectionString.getApplicationName() == null
            ? connectionString.getHosts().get(0)
            : connectionString.getApplicationName();

        // Create the MongoSKServer object.
        MongoSKServer mongoSKServer = new MongoSKServer(displayedName, rawConnectionString, mongoClient);

        // Add this server to the listeners (we must do this because the client is only created after the builder).
        mongoCommandListener.setMongoSKServer(mongoSKServer);

        // Return the MongoSKServer linked with the listeners.
        return mongoSKServer;
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
