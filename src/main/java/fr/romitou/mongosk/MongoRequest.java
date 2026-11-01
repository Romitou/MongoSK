package fr.romitou.mongosk;

import com.mongodb.MongoTimeoutException;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Sends a request to MongoDB without blocking the calling thread, and collects its results.
 *
 * @param <T> the type of the results
 */
public final class MongoRequest<T> implements Subscriber<T> {

    private static final Set<CompletableFuture<?>> PENDING = ConcurrentHashMap.newKeySet();

    private final List<T> results = new ArrayList<>();
    private final CompletableFuture<Response<T>> response = new CompletableFuture<>();
    private volatile Subscription subscription;

    private MongoRequest() {
    }

    /**
     * @param timeout the maximum time to wait for the results in milliseconds, after which the request is cancelled
     * @return the response of the request, which is always completed normally: failures are held by the response
     */
    public static <T> CompletableFuture<Response<T>> send(Publisher<T> publisher, long timeout) {
        MongoRequest<T> request = new MongoRequest<>();
        CompletableFuture<Response<T>> response = request.response;
        PENDING.add(response);
        response.whenComplete((result, ignored) -> {
            PENDING.remove(response);
            if (result.error() instanceof MongoTimeoutException)
                request.cancel();
        });
        response.completeOnTimeout(
            new Response<>(List.of(), new MongoTimeoutException("The request did not complete in " + timeout + "ms")),
            timeout, TimeUnit.MILLISECONDS
        );
        try {
            publisher.subscribe(request);
        } catch (RuntimeException e) {
            request.onError(e);
        }
        return response;
    }

    /**
     * Waits for the pending requests, each of them being limited by its own timeout.
     */
    public static void awaitPending() {
        CompletableFuture.allOf(PENDING.toArray(new CompletableFuture<?>[0])).join();
    }

    private void cancel() {
        Subscription current = subscription;
        if (current != null)
            current.cancel();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T result) {
        results.add(result);
    }

    @Override
    public void onError(Throwable error) {
        response.complete(new Response<>(List.of(), error));
    }

    @Override
    public void onComplete() {
        response.complete(new Response<>(results, null));
    }

    /**
     * @param results the results of the request, empty if it failed
     * @param error   the reason of the failure, or null if the request succeeded
     */
    public record Response<T>(List<T> results, @Nullable Throwable error) {
    }
}
