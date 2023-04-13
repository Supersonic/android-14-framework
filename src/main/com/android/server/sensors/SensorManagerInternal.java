package com.android.server.sensors;

import android.os.ParcelFileDescriptor;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public abstract class SensorManagerInternal {

    /* loaded from: classes2.dex */
    public interface ProximityActiveListener {
        void onProximityActive(boolean z);
    }

    /* loaded from: classes2.dex */
    public interface RuntimeSensorCallback {
        int onConfigurationChanged(int i, boolean z, int i2, int i3);

        int onDirectChannelConfigured(int i, int i2, int i3);

        int onDirectChannelCreated(ParcelFileDescriptor parcelFileDescriptor);

        void onDirectChannelDestroyed(int i);
    }

    public abstract void addProximityActiveListener(Executor executor, ProximityActiveListener proximityActiveListener);

    public abstract int createRuntimeSensor(int i, int i2, String str, String str2, int i3, RuntimeSensorCallback runtimeSensorCallback);

    public abstract void removeRuntimeSensor(int i);

    public abstract boolean sendSensorEvent(int i, int i2, long j, float[] fArr);
}
