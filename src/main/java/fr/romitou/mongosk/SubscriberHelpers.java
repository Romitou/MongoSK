/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.romitou.mongosk;

import com.mongodb.MongoTimeoutException;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Subscriber helper implementations for the Quick Tour.
 *
 * @author Romitou 27/02/2021
 */
public final class SubscriberHelpers {

    // The error of the last Mongo request, or null if it succeeded. Requests are awaited sequentially by the scripts.
    private static volatile String lastError;
    // Maximum time to wait for a request, so that a request that never completes cannot freeze the server.
    private static final long REQUEST_TIMEOUT = getRequestTimeout();

    private SubscriberHelpers() {
    }

    @Nullable
    public static String getLastError() {
        return lastError;
    }

    public static void setLastError(@Nullable String error) {
        lastError = error;
    }

    private static long getRequestTimeout() {
        try {
            return MongoSK.getInstance().getConfig().getLong("timeouts.request", 30000);
        } catch (Throwable t) {
            // MongoSK not available or failed to load
            return 30000;
        }
    }

    /**
     * A Subscriber that stores the publishers results and provides a latch so can block on completion.
     *
     * @param <T> The publishers result type
     */
    public static class ObservableSubscriber<T> implements Subscriber<T> {
        // Filled by the threads of the driver while the requests are awaited by the server thread.
        private final List<T> receivedData = Collections.synchronizedList(new ArrayList<>());
        private final List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());
        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile Subscription subscription;

        ObservableSubscriber() {
        }

        @Override
        public void onSubscribe(final Subscription s) {
            subscription = s;
        }

        @Override
        public void onNext(final T t) {
            receivedData.add(t);
        }

        @Override
        public void onError(final Throwable t) {
            errors.add(t);
            onComplete();
        }

        @Override
        public void onComplete() {
            latch.countDown();
        }

        public Subscription getSubscription() {
            return subscription;
        }

        public List<T> getReceivedData() {
            synchronized (receivedData) {
                return new ArrayList<>(receivedData);
            }
        }

        public Throwable getError() {
            synchronized (errors) {
                return errors.isEmpty() ? null : errors.get(0);
            }
        }

        public List<T> get() {
            return await().getReceivedData();
        }

        public List<T> get(final long timeout, final TimeUnit unit) {
            return await(timeout, unit).getReceivedData();
        }

        public ObservableSubscriber<T> await() {
            return await(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        public ObservableSubscriber<T> await(final long timeout, final TimeUnit unit) {
            setLastError(null);
            subscription.request(Integer.MAX_VALUE);
            try {
                if (!latch.await(timeout, unit))
                    throw new MongoTimeoutException("Publisher onComplete timed out");
                Throwable error = getError();
                if (error != null)
                    throw error;
            } catch (Throwable error) {
                setLastError(error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage());
                LoggerHelper.severe("An error occurred during a Mongo request: " + error.getMessage());
            }
            return this;
        }
    }

    /**
     * A Subscriber that immediately requests Integer.MAX_VALUE onSubscribe
     *
     * @param <T> The publishers result type
     */
    public static class OperationSubscriber<T> extends ObservableSubscriber<T> {

        @Override
        public void onSubscribe(final Subscription s) {
            super.onSubscribe(s);
            s.request(Integer.MAX_VALUE);
        }
    }
}
