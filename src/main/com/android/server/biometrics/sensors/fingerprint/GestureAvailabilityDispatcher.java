package com.android.server.biometrics.sensors.fingerprint;

import android.hardware.fingerprint.IFingerprintClientActiveCallback;
import android.os.RemoteException;
import android.util.Slog;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public class GestureAvailabilityDispatcher {
    public boolean mIsActive;
    public final CopyOnWriteArrayList<IFingerprintClientActiveCallback> mClientActiveCallbacks = new CopyOnWriteArrayList<>();
    public final Map<Integer, Boolean> mActiveSensors = new HashMap();

    public boolean isAnySensorActive() {
        return this.mIsActive;
    }

    public void markSensorActive(int i, boolean z) {
        boolean z2;
        this.mActiveSensors.put(Integer.valueOf(i), Boolean.valueOf(z));
        boolean z3 = this.mIsActive;
        Iterator<Boolean> it = this.mActiveSensors.values().iterator();
        while (true) {
            if (!it.hasNext()) {
                z2 = false;
                break;
            } else if (it.next().booleanValue()) {
                z2 = true;
                break;
            }
        }
        if (z3 != z2) {
            Slog.d("GestureAvailabilityTracker", "Notifying gesture availability, active=" + this.mIsActive);
            this.mIsActive = z2;
            notifyClientActiveCallbacks(z2);
        }
    }

    public void registerCallback(IFingerprintClientActiveCallback iFingerprintClientActiveCallback) {
        this.mClientActiveCallbacks.add(iFingerprintClientActiveCallback);
    }

    public void removeCallback(IFingerprintClientActiveCallback iFingerprintClientActiveCallback) {
        this.mClientActiveCallbacks.remove(iFingerprintClientActiveCallback);
    }

    public final void notifyClientActiveCallbacks(boolean z) {
        Iterator<IFingerprintClientActiveCallback> it = this.mClientActiveCallbacks.iterator();
        while (it.hasNext()) {
            IFingerprintClientActiveCallback next = it.next();
            try {
                next.onClientActiveChanged(z);
            } catch (RemoteException unused) {
                this.mClientActiveCallbacks.remove(next);
            }
        }
    }
}
