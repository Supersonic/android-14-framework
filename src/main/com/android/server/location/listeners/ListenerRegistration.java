package com.android.server.location.listeners;

import com.android.internal.listeners.ListenerExecutor;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class ListenerRegistration<TListener> implements ListenerExecutor {
    public boolean mActive;
    public final Executor mExecutor;
    public volatile TListener mListener;

    public final boolean equals(Object obj) {
        return this == obj;
    }

    public String getTag() {
        return "ListenerRegistration";
    }

    public void onActive() {
    }

    public void onInactive() {
    }

    public void onListenerUnregister() {
    }

    public void onRegister(Object obj) {
    }

    public void onUnregister() {
    }

    public String toString() {
        return "[]";
    }

    public ListenerRegistration(Executor executor, TListener tlistener) {
        Objects.requireNonNull(executor);
        this.mExecutor = executor;
        this.mActive = false;
        Objects.requireNonNull(tlistener);
        this.mListener = tlistener;
    }

    public final Executor getExecutor() {
        return this.mExecutor;
    }

    public final boolean isActive() {
        return this.mActive;
    }

    public final boolean setActive(boolean z) {
        if (z != this.mActive) {
            this.mActive = z;
            return true;
        }
        return false;
    }

    public final boolean isRegistered() {
        return this.mListener != null;
    }

    public final void unregisterInternal() {
        this.mListener = null;
        onListenerUnregister();
    }

    public void onOperationFailure(ListenerExecutor.ListenerOperation<TListener> listenerOperation, Exception exc) {
        throw new AssertionError(exc);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$executeOperation$0() {
        return this.mListener;
    }

    public final void executeOperation(ListenerExecutor.ListenerOperation<TListener> listenerOperation) {
        executeSafely(this.mExecutor, new Supplier() { // from class: com.android.server.location.listeners.ListenerRegistration$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                Object lambda$executeOperation$0;
                lambda$executeOperation$0 = ListenerRegistration.this.lambda$executeOperation$0();
                return lambda$executeOperation$0;
            }
        }, listenerOperation, new ListenerExecutor.FailureCallback() { // from class: com.android.server.location.listeners.ListenerRegistration$$ExternalSyntheticLambda1
            public final void onFailure(ListenerExecutor.ListenerOperation listenerOperation2, Exception exc) {
                ListenerRegistration.this.onOperationFailure(listenerOperation2, exc);
            }
        });
    }

    public final int hashCode() {
        return super.hashCode();
    }
}
