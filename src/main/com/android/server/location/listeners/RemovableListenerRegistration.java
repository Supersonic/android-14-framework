package com.android.server.location.listeners;

import com.android.internal.listeners.ListenerExecutor;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public abstract class RemovableListenerRegistration<TKey, TListener> extends ListenerRegistration<TListener> {
    public volatile TKey mKey;
    public final AtomicBoolean mRemoved;

    public abstract ListenerMultiplexer<TKey, ? super TListener, ?, ?> getOwner();

    public void onRegister() {
    }

    public void onRemove(boolean z) {
    }

    public RemovableListenerRegistration(Executor executor, TListener tlistener) {
        super(executor, tlistener);
        this.mRemoved = new AtomicBoolean(false);
    }

    public final TKey getKey() {
        TKey tkey = this.mKey;
        Objects.requireNonNull(tkey);
        return tkey;
    }

    public final void remove() {
        remove(true);
    }

    public final void remove(boolean z) {
        final TKey tkey = this.mKey;
        if (tkey == null || this.mRemoved.getAndSet(true)) {
            return;
        }
        onRemove(z);
        if (z) {
            getOwner().removeRegistration(tkey, this);
        } else {
            executeOperation(new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.listeners.RemovableListenerRegistration$$ExternalSyntheticLambda0
                public final void operate(Object obj) {
                    RemovableListenerRegistration.this.lambda$remove$0(tkey, obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$remove$0(Object obj, Object obj2) throws Exception {
        getOwner().removeRegistration(obj, this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.location.listeners.ListenerRegistration
    public final void onRegister(Object obj) {
        super.onRegister(obj);
        Objects.requireNonNull(obj);
        this.mKey = obj;
        onRegister();
    }

    @Override // com.android.server.location.listeners.ListenerRegistration
    public void onUnregister() {
        this.mKey = null;
        super.onUnregister();
    }
}
