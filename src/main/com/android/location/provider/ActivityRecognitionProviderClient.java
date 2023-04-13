package com.android.location.provider;

import android.hardware.location.IActivityRecognitionHardware;
import android.hardware.location.IActivityRecognitionHardwareClient;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
/* loaded from: classes.dex */
public abstract class ActivityRecognitionProviderClient {
    private static final String TAG = "ArProviderClient";
    private IActivityRecognitionHardwareClient.Stub mClient = new IActivityRecognitionHardwareClient.Stub() { // from class: com.android.location.provider.ActivityRecognitionProviderClient.1
        public void onAvailabilityChanged(boolean isSupported, IActivityRecognitionHardware instance) {
            ActivityRecognitionProvider provider;
            int callingUid = Binder.getCallingUid();
            if (callingUid != 1000) {
                Log.d(ActivityRecognitionProviderClient.TAG, "Ignoring calls from non-system server. Uid: " + callingUid);
                return;
            }
            if (!isSupported) {
                provider = null;
            } else {
                try {
                    provider = new ActivityRecognitionProvider(instance);
                } catch (RemoteException e) {
                    Log.e(ActivityRecognitionProviderClient.TAG, "Error creating Hardware Activity-Recognition Provider.", e);
                    return;
                }
            }
            ActivityRecognitionProviderClient.this.onProviderChanged(isSupported, provider);
        }
    };

    public abstract void onProviderChanged(boolean z, ActivityRecognitionProvider activityRecognitionProvider);

    protected ActivityRecognitionProviderClient() {
    }

    public IBinder getBinder() {
        return this.mClient;
    }
}
