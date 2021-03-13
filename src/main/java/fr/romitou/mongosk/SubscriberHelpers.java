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
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Subscriber helper implementations for the Quick Tour.
 * @author Romitou 27/02/2021
 */
public final class SubscriberHelpers {

    /**
     * A Subscriber that stores the publishers results and provides a latch so can block on completion.
     *
     * @param <T> The publishers result type
     */
    public static class ObservableSubscriber<T> implements Subscriber<T> {
        private final List<T> receivedData = new ArrayList<>();
        private final List<Throwable> errors = new ArrayList<>();
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
            return receivedData;
        }

        public Throwable getError() {
            if (errors.size() > 0)
                return errors.get(0);
            return null;
        }

        public List<T> get() {
            return await().getReceivedData();
        }

        public List<T> get(final long timeout, final TimeUnit unit) {
            return await(timeout, unit).getReceivedData();
        }

        public ObservableSubscriber<T> await() {
            return await(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }

        public ObservableSubscriber<T> await(final long timeout, final TimeUnit unit) {
            subscription.request(Integer.MAX_VALUE);
            try {
                if (!latch.await(timeout, unit))
                    throw new MongoTimeoutException("Publisher onComplete timed out");
                if (!errors.isEmpty())
                    throw errors.get(0);
            } catch (Throwable error) {
                Logger.severe("An error occurred during a Mongo request: " + error.getMessage());
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

    private SubscriberHelpers() {
    }
}
