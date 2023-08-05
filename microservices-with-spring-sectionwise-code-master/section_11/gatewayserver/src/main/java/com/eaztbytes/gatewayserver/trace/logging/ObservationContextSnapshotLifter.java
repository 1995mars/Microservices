package com.eaztbytes.gatewayserver.trace.logging;

import java.util.function.BiFunction;

import org.reactivestreams.Subscription;
import org.springframework.lang.Nullable;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ThreadLocalAccessor;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.util.context.Context;

public class ObservationContextSnapshotLifter<T> implements CoreSubscriber<T> {

    public static <T> BiFunction<Scannable, CoreSubscriber<? super T>, CoreSubscriber<? super T>> lifter() {
        return (scannable, coreSubscriber) -> new ObservationContextSnapshotLifter<>(coreSubscriber);
    }

    private final CoreSubscriber<? super T> delegate;

    @Nullable
    private final ObservationThreadLocalAccessor observationThreadLocalAccessor;

    private ObservationContextSnapshotLifter(CoreSubscriber<? super T> delegate) {
        this.delegate = delegate;
        observationThreadLocalAccessor = findObservationThreadLocalAccessor();
    }

    @Nullable
    private static ObservationThreadLocalAccessor findObservationThreadLocalAccessor() {
        for (ThreadLocalAccessor<?> threadLocalAccessor : ContextRegistry.getInstance().getThreadLocalAccessors()) {
            if (ObservationThreadLocalAccessor.KEY.equals(threadLocalAccessor.key())
                    && threadLocalAccessor instanceof ObservationThreadLocalAccessor observationThreadLocalAccessor) {
                return observationThreadLocalAccessor;
            }
        }
        return null;
    }

    @Override
    public Context currentContext() {
        return delegate.currentContext();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        delegate.onSubscribe(subscription);
    }

    @Override
    public void onNext(T t) {
        if (isObservationThreadLocalUnset()) {
            try (ContextSnapshot.Scope scope = ContextSnapshot.setThreadLocalsFrom(currentContext(), ObservationThreadLocalAccessor.KEY)) {
                delegate.onNext(t);
            }
        } else {
            delegate.onNext(t);
        }
    }

    @Override
    public void onError(Throwable t) {
        if (isObservationThreadLocalUnset()) {
            try (ContextSnapshot.Scope scope = ContextSnapshot.setThreadLocalsFrom(currentContext(), ObservationThreadLocalAccessor.KEY)) {
                delegate.onError(t);
            }
        } else {
            delegate.onError(t);
        }
    }

    @Override
    public void onComplete() {
        if (isObservationThreadLocalUnset()) {
            try (ContextSnapshot.Scope scope = ContextSnapshot.setThreadLocalsFrom(currentContext(), ObservationThreadLocalAccessor.KEY)) {
                delegate.onComplete();
            }
        } else {
            delegate.onComplete();
        }
    }

    private boolean isObservationThreadLocalUnset() {
        return observationThreadLocalAccessor != null && observationThreadLocalAccessor.getValue() == null;
    }
}
