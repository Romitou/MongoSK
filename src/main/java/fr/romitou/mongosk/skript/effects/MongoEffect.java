/**
 * Note: authored by Romitou with AI coding assistance from Gemini.
 */

package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.test.runner.TestMode;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.MongoRequest;
import fr.romitou.mongosk.MongoSK;
import org.bukkit.event.Event;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * An effect which sends a request to MongoDB. Like the wait effect, it suspends the script until the response is
 * received, without blocking the server.
 * <p>
 * An instance of an effect is shared by all the executions of a script, some of them being suspended at the same time:
 * the state of an execution must not be stored in its fields.
 */
public abstract class MongoEffect extends Effect {

    protected static final String STORE_PATTERN = " and store [the] (result|document|value)[s] in %~objects%";

    private static final Map<Event, String> LAST_ERRORS = Collections.synchronizedMap(new WeakHashMap<>());

    // The test runner of Skript forbids assertions after a delay and stops the server before delayed tests complete,
    // so the requests are awaited during the tests.
    private static final boolean SUSPEND_SCRIPTS = !TestMode.ENABLED;

    private final long timeout = MongoSK.getInstance().getConfig().getLong("timeouts.request", 30000);

    private @Nullable Expression<?> storedIn;

    /**
     * @return the error of the last Mongo request of this event, or null if it succeeded
     */
    public static @Nullable String getLastError(Event event) {
        return LAST_ERRORS.get(event);
    }

    public static void setLastError(Event event, String error) {
        LAST_ERRORS.put(event, error);
    }

    /**
     * Initializes the effect, like {@link Effect#init}.
     */
    protected abstract boolean load(Expression<?>[] exprs, int matchedPattern, SkriptParser.ParseResult parseResult);

    /**
     * Prepares the request on the thread of the script, where its expressions can safely be evaluated.
     *
     * @return the request to send, or null if there is nothing to do
     */
    protected abstract @Nullable Request<?> prepare(Event event);

    /**
     * Stores the results of the requests of this effect in the given expression.
     *
     * @return whether the results can be stored in this expression
     */
    protected final boolean storeIn(Expression<?> expression) {
        if (!Changer.ChangerUtils.acceptsChange(expression, Changer.ChangeMode.SET, Object.class)) {
            Skript.error(expression + " cannot be set");
            return false;
        }
        storedIn = expression;
        return true;
    }

    /**
     * @return whether only one result can be stored
     */
    protected final boolean storesSingleResult() {
        return storedIn != null && storedIn.isSingle();
    }

    @Override
    public final boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        if (!load(exprs, matchedPattern, parseResult))
            return false;
        if (SUSPEND_SCRIPTS)
            getParser().setHasDelayBefore(Kleenean.TRUE);
        return true;
    }

    @Override
    protected final void execute(@NotNull Event event) {
        // The effect is executed by walk, as it can suspend the script.
    }

    @Override
    protected @Nullable TriggerItem walk(@NotNull Event event) {
        debug(event, true);
        LAST_ERRORS.remove(event);
        Request<?> request;
        try {
            request = prepare(event);
        } catch (RuntimeException e) {
            fail(event, e);
            return getNext();
        }
        return request == null ? getNext() : send(event, request);
    }

    private <T> @Nullable TriggerItem send(Event event, Request<T> request) {
        CompletableFuture<MongoRequest.Response<T>> response = MongoRequest.send(request.publisher(), timeout);
        // The scheduler is not available anymore when the server is stopping.
        if (!SUSPEND_SCRIPTS || !MongoSK.getInstance().isEnabled()) {
            complete(event, request, response.join());
            return getNext();
        }

        Object localVariables = Variables.removeLocals(event);
        response.thenAccept(result -> resumeOnServerThread(() -> {
            Delay.addDelayedEvent(event);
            if (localVariables != null)
                Variables.setLocalVariables(event, localVariables);
            complete(event, request, result);
            TriggerItem next = getNext();
            if (next != null)
                TriggerItem.walk(next, event);
            Variables.removeLocals(event); // The script may be finished.
        }));
        return null;
    }

    private static void resumeOnServerThread(Runnable resume) {
        try {
            MongoSK instance = MongoSK.getInstance();
            instance.getServer().getScheduler().runTask(instance, resume);
        } catch (IllegalPluginAccessException e) {
            LoggerHelper.debug("A script was not resumed after a Mongo request, as the server is stopping.");
        }
    }

    private <T> void complete(Event event, Request<T> request, MongoRequest.Response<T> response) {
        if (response.error() != null) {
            fail(event, response.error());
            store(event, new Object[0]);
            return;
        }
        store(event, request.mapper().apply(response.results()));
    }

    private void store(Event event, Object[] results) {
        if (storedIn == null)
            return;
        if (results.length == 0)
            storedIn.change(event, null, Changer.ChangeMode.DELETE);
        else
            storedIn.change(event, storedIn.isSingle() ? new Object[]{results[0]} : results, Changer.ChangeMode.SET);
    }

    private static void fail(Event event, Throwable error) {
        setLastError(event, error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage());
        LoggerHelper.severe("An error occurred during a Mongo request: " + error.getMessage());
    }

    /**
     * @param publisher the request, sent when the effect subscribes to it
     * @param mapper    converts the results on the server thread to the values stored by the effect
     */
    protected record Request<T>(Publisher<T> publisher, Function<List<T>, Object[]> mapper) {

        /**
         * @return a request whose results are ignored
         */
        protected static <T> Request<T> withoutResult(Publisher<T> publisher) {
            return new Request<>(publisher, results -> new Object[0]);
        }
    }
}
