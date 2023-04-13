package com.android.server.biometrics.sensors;

import android.content.Context;
import android.hardware.biometrics.IBiometricServiceLockoutResetCallback;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class LockoutResetDispatcher implements IBinder.DeathRecipient {
    @VisibleForTesting
    final List<ClientCallback> mClientCallbacks = new ArrayList();
    public final Context mContext;

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
    }

    /* loaded from: classes.dex */
    public static class ClientCallback {
        public final IBiometricServiceLockoutResetCallback mCallback;
        public final String mOpPackageName;
        public final PowerManager.WakeLock mWakeLock;

        public ClientCallback(Context context, IBiometricServiceLockoutResetCallback iBiometricServiceLockoutResetCallback, String str) {
            this.mOpPackageName = str;
            this.mCallback = iBiometricServiceLockoutResetCallback;
            this.mWakeLock = ((PowerManager) context.getSystemService(PowerManager.class)).newWakeLock(1, "LockoutResetMonitor:SendLockoutReset");
        }

        public void sendLockoutReset(int i) {
            if (this.mCallback != null) {
                try {
                    this.mWakeLock.acquire(2000L);
                    this.mCallback.onLockoutReset(i, new IRemoteCallback.Stub() { // from class: com.android.server.biometrics.sensors.LockoutResetDispatcher.ClientCallback.1
                        public void sendResult(Bundle bundle) {
                            ClientCallback.this.releaseWakelock();
                        }
                    });
                } catch (RemoteException e) {
                    Slog.w("LockoutResetTracker", "Failed to invoke onLockoutReset: ", e);
                    releaseWakelock();
                }
            }
        }

        public final void releaseWakelock() {
            if (this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
            }
        }
    }

    public LockoutResetDispatcher(Context context) {
        this.mContext = context;
    }

    public void addCallback(IBiometricServiceLockoutResetCallback iBiometricServiceLockoutResetCallback, String str) {
        if (iBiometricServiceLockoutResetCallback == null) {
            Slog.w("LockoutResetTracker", "Callback from : " + str + " is null");
            return;
        }
        this.mClientCallbacks.add(new ClientCallback(this.mContext, iBiometricServiceLockoutResetCallback, str));
        try {
            iBiometricServiceLockoutResetCallback.asBinder().linkToDeath(this, 0);
        } catch (RemoteException e) {
            Slog.e("LockoutResetTracker", "Failed to link to death", e);
        }
    }

    public void binderDied(IBinder iBinder) {
        Slog.e("LockoutResetTracker", "Callback binder died: " + iBinder);
        Iterator<ClientCallback> it = this.mClientCallbacks.iterator();
        while (it.hasNext()) {
            ClientCallback next = it.next();
            if (next.mCallback.asBinder().equals(iBinder)) {
                Slog.e("LockoutResetTracker", "Removing dead callback for: " + next.mOpPackageName);
                next.releaseWakelock();
                it.remove();
            }
        }
    }

    public void notifyLockoutResetCallbacks(int i) {
        for (ClientCallback clientCallback : this.mClientCallbacks) {
            clientCallback.sendLockoutReset(i);
        }
    }
}
