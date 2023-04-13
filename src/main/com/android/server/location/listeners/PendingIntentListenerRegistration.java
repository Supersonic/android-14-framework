package com.android.server.location.listeners;

import android.app.PendingIntent;
import android.util.Log;
import com.android.internal.listeners.ListenerExecutor;
import com.android.internal.util.ConcurrentUtils;
/* loaded from: classes.dex */
public abstract class PendingIntentListenerRegistration<TKey, TListener> extends RemovableListenerRegistration<TKey, TListener> implements PendingIntent.CancelListener {
    public abstract PendingIntent getPendingIntentFromKey(TKey tkey);

    public PendingIntentListenerRegistration(TListener tlistener) {
        super(ConcurrentUtils.DIRECT_EXECUTOR, tlistener);
    }

    @Override // com.android.server.location.listeners.RemovableListenerRegistration
    public void onRegister() {
        super.onRegister();
        if (getPendingIntentFromKey(getKey()).addCancelListener(ConcurrentUtils.DIRECT_EXECUTOR, this)) {
            return;
        }
        remove();
    }

    @Override // com.android.server.location.listeners.RemovableListenerRegistration, com.android.server.location.listeners.ListenerRegistration
    public void onUnregister() {
        getPendingIntentFromKey(getKey()).removeCancelListener(this);
        super.onUnregister();
    }

    @Override // com.android.server.location.listeners.ListenerRegistration
    public void onOperationFailure(ListenerExecutor.ListenerOperation<TListener> listenerOperation, Exception exc) {
        if (exc instanceof PendingIntent.CanceledException) {
            String tag = getTag();
            Log.w(tag, "registration " + this + " removed", exc);
            remove();
            return;
        }
        super.onOperationFailure(listenerOperation, exc);
    }

    public void onCanceled(PendingIntent pendingIntent) {
        if (Log.isLoggable(getTag(), 3)) {
            String tag = getTag();
            Log.d(tag, "pending intent registration " + this + " canceled");
        }
        remove();
    }
}
