package com.android.server.sensorprivacy;

import android.os.Handler;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.sensorprivacy.SensorPrivacyStateController;
import java.util.function.BiConsumer;
/* loaded from: classes2.dex */
public abstract class SensorPrivacyStateController {
    public static SensorPrivacyStateController sInstance;
    public AllSensorStateController mAllSensorStateController = AllSensorStateController.getInstance();
    public final Object mLock = new Object();

    /* loaded from: classes2.dex */
    public interface AllSensorPrivacyListener {
        void onAllSensorPrivacyChanged(boolean z);
    }

    /* loaded from: classes2.dex */
    public interface SensorPrivacyListener {
        void onSensorPrivacyChanged(int i, int i2, int i3, SensorState sensorState);
    }

    /* loaded from: classes2.dex */
    public interface SensorPrivacyStateConsumer {
        void accept(int i, int i2, int i3, SensorState sensorState);
    }

    /* loaded from: classes2.dex */
    public interface SetStateResultCallback {
        void callback(boolean z);
    }

    @GuardedBy({"mLock"})
    public abstract void dumpLocked(DualDumpOutputStream dualDumpOutputStream);

    @GuardedBy({"mLock"})
    public abstract void forEachStateLocked(SensorPrivacyStateConsumer sensorPrivacyStateConsumer);

    @GuardedBy({"mLock"})
    public abstract SensorState getStateLocked(int i, int i2, int i3);

    @GuardedBy({"mLock"})
    public abstract void schedulePersistLocked();

    @GuardedBy({"mLock"})
    public abstract void setSensorPrivacyListenerLocked(Handler handler, SensorPrivacyListener sensorPrivacyListener);

    @GuardedBy({"mLock"})
    public abstract void setStateLocked(int i, int i2, int i3, boolean z, Handler handler, SetStateResultCallback setStateResultCallback);

    public static SensorPrivacyStateController getInstance() {
        if (sInstance == null) {
            sInstance = SensorPrivacyStateControllerImpl.getInstance();
        }
        return sInstance;
    }

    public SensorState getState(int i, int i2, int i3) {
        SensorState stateLocked;
        synchronized (this.mLock) {
            stateLocked = getStateLocked(i, i2, i3);
        }
        return stateLocked;
    }

    public void setState(int i, int i2, int i3, boolean z, Handler handler, SetStateResultCallback setStateResultCallback) {
        synchronized (this.mLock) {
            setStateLocked(i, i2, i3, z, handler, setStateResultCallback);
        }
    }

    public void setSensorPrivacyListener(Handler handler, SensorPrivacyListener sensorPrivacyListener) {
        synchronized (this.mLock) {
            setSensorPrivacyListenerLocked(handler, sensorPrivacyListener);
        }
    }

    public boolean getAllSensorState() {
        boolean allSensorStateLocked;
        synchronized (this.mLock) {
            allSensorStateLocked = this.mAllSensorStateController.getAllSensorStateLocked();
        }
        return allSensorStateLocked;
    }

    public void setAllSensorState(boolean z) {
        synchronized (this.mLock) {
            this.mAllSensorStateController.setAllSensorStateLocked(z);
        }
    }

    public void setAllSensorPrivacyListener(Handler handler, AllSensorPrivacyListener allSensorPrivacyListener) {
        synchronized (this.mLock) {
            this.mAllSensorStateController.setAllSensorPrivacyListenerLocked(handler, allSensorPrivacyListener);
        }
    }

    public void persistAll() {
        synchronized (this.mLock) {
            this.mAllSensorStateController.schedulePersistLocked();
            schedulePersistLocked();
        }
    }

    public void forEachState(SensorPrivacyStateConsumer sensorPrivacyStateConsumer) {
        synchronized (this.mLock) {
            forEachStateLocked(sensorPrivacyStateConsumer);
        }
    }

    public void dump(DualDumpOutputStream dualDumpOutputStream) {
        synchronized (this.mLock) {
            this.mAllSensorStateController.dumpLocked(dualDumpOutputStream);
            dumpLocked(dualDumpOutputStream);
        }
        dualDumpOutputStream.flush();
    }

    public void atomic(Runnable runnable) {
        synchronized (this.mLock) {
            runnable.run();
        }
    }

    public static void sendSetStateCallback(Handler handler, SetStateResultCallback setStateResultCallback, boolean z) {
        handler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.sensorprivacy.SensorPrivacyStateController$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((SensorPrivacyStateController.SetStateResultCallback) obj).callback(((Boolean) obj2).booleanValue());
            }
        }, setStateResultCallback, Boolean.valueOf(z)));
    }
}
