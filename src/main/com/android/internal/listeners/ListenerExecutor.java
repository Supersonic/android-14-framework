package com.android.internal.listeners;

import java.util.concurrent.Executor;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public interface ListenerExecutor {

    /* loaded from: classes4.dex */
    public interface FailureCallback<TListenerOperation extends ListenerOperation<?>> {
        void onFailure(TListenerOperation tlisteneroperation, Exception exc);
    }

    /* loaded from: classes4.dex */
    public interface ListenerOperation<TListener> {
        void operate(TListener tlistener) throws Exception;

        default void onPreExecute() {
        }

        default void onFailure(Exception e) {
        }

        default void onPostExecute(boolean success) {
        }

        default void onComplete(boolean success) {
        }
    }

    default <TListener> void executeSafely(Executor executor, Supplier<TListener> listenerSupplier, ListenerOperation<TListener> operation) {
        executeSafely(executor, listenerSupplier, operation, null);
    }

    default <TListener, TListenerOperation extends ListenerOperation<TListener>> void executeSafely(Executor executor, final Supplier<TListener> listenerSupplier, final TListenerOperation operation, final FailureCallback<TListenerOperation> failureCallback) {
        final TListener listener;
        if (operation == null || (listener = listenerSupplier.get()) == null) {
            return;
        }
        boolean executing = false;
        boolean preexecute = false;
        try {
            operation.onPreExecute();
            preexecute = true;
            executor.execute(new Runnable() { // from class: com.android.internal.listeners.ListenerExecutor$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ListenerExecutor.lambda$executeSafely$0(listener, listenerSupplier, operation, failureCallback);
                }
            });
            executing = true;
        } finally {
            if (!executing) {
                if (preexecute) {
                    operation.onPostExecute(false);
                }
                operation.onComplete(false);
            }
        }
    }

    static /* synthetic */ void lambda$executeSafely$0(Object listener, Supplier listenerSupplier, ListenerOperation operation, FailureCallback failureCallback) {
        boolean success = false;
        try {
            try {
                if (listener == listenerSupplier.get()) {
                    operation.operate(listener);
                    success = true;
                }
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw ((RuntimeException) e);
                }
                operation.onFailure(e);
                if (failureCallback != null) {
                    failureCallback.onFailure(operation, e);
                }
            }
        } finally {
            operation.onPostExecute(false);
            operation.onComplete(false);
        }
    }
}
