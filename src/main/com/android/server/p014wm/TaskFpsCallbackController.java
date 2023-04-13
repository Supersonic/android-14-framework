package com.android.server.p014wm;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.window.ITaskFpsCallback;
import java.util.HashMap;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: com.android.server.wm.TaskFpsCallbackController */
/* loaded from: classes2.dex */
public final class TaskFpsCallbackController {
    public final Context mContext;
    public final HashMap<ITaskFpsCallback, Long> mTaskFpsCallbacks = new HashMap<>();
    public final HashMap<ITaskFpsCallback, IBinder.DeathRecipient> mDeathRecipients = new HashMap<>();

    private static native long nativeRegister(ITaskFpsCallback iTaskFpsCallback, int i);

    private static native void nativeUnregister(long j);

    public TaskFpsCallbackController(Context context) {
        this.mContext = context;
    }

    public void registerListener(int i, final ITaskFpsCallback iTaskFpsCallback) {
        if (this.mTaskFpsCallbacks.containsKey(iTaskFpsCallback)) {
            return;
        }
        this.mTaskFpsCallbacks.put(iTaskFpsCallback, Long.valueOf(nativeRegister(iTaskFpsCallback, i)));
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.wm.TaskFpsCallbackController$$ExternalSyntheticLambda0
            @Override // android.os.IBinder.DeathRecipient
            public final void binderDied() {
                TaskFpsCallbackController.this.lambda$registerListener$0(iTaskFpsCallback);
            }
        };
        try {
            iTaskFpsCallback.asBinder().linkToDeath(deathRecipient, 0);
            this.mDeathRecipients.put(iTaskFpsCallback, deathRecipient);
        } catch (RemoteException unused) {
        }
    }

    /* renamed from: unregisterListener */
    public void lambda$registerListener$0(ITaskFpsCallback iTaskFpsCallback) {
        if (this.mTaskFpsCallbacks.containsKey(iTaskFpsCallback)) {
            iTaskFpsCallback.asBinder().unlinkToDeath(this.mDeathRecipients.get(iTaskFpsCallback), 0);
            this.mDeathRecipients.remove(iTaskFpsCallback);
            nativeUnregister(this.mTaskFpsCallbacks.get(iTaskFpsCallback).longValue());
            this.mTaskFpsCallbacks.remove(iTaskFpsCallback);
        }
    }
}
