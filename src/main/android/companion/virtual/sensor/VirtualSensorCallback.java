package android.companion.virtual.sensor;

import android.annotation.SystemApi;
import android.p008os.SharedMemory;
import java.time.Duration;
@SystemApi
/* loaded from: classes.dex */
public interface VirtualSensorCallback {
    void onConfigurationChanged(VirtualSensor virtualSensor, boolean z, Duration duration, Duration duration2);

    default void onDirectChannelCreated(int channelHandle, SharedMemory sharedMemory) {
    }

    default void onDirectChannelDestroyed(int channelHandle) {
    }

    default void onDirectChannelConfigured(int channelHandle, VirtualSensor sensor, int rateLevel, int reportToken) {
    }
}
