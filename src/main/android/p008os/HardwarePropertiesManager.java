package android.p008os;

import android.content.Context;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.os.HardwarePropertiesManager */
/* loaded from: classes3.dex */
public class HardwarePropertiesManager {
    public static final int DEVICE_TEMPERATURE_BATTERY = 2;
    public static final int DEVICE_TEMPERATURE_CPU = 0;
    public static final int DEVICE_TEMPERATURE_GPU = 1;
    public static final int DEVICE_TEMPERATURE_SKIN = 3;
    private static final String TAG = HardwarePropertiesManager.class.getSimpleName();
    public static final int TEMPERATURE_CURRENT = 0;
    public static final int TEMPERATURE_SHUTDOWN = 2;
    public static final int TEMPERATURE_THROTTLING = 1;
    public static final int TEMPERATURE_THROTTLING_BELOW_VR_MIN = 3;
    public static final float UNDEFINED_TEMPERATURE = -3.4028235E38f;
    private final Context mContext;
    private final IHardwarePropertiesManager mService;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.HardwarePropertiesManager$DeviceTemperatureType */
    /* loaded from: classes3.dex */
    public @interface DeviceTemperatureType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.HardwarePropertiesManager$TemperatureSource */
    /* loaded from: classes3.dex */
    public @interface TemperatureSource {
    }

    public HardwarePropertiesManager(Context context, IHardwarePropertiesManager service) {
        this.mContext = context;
        this.mService = service;
    }

    public float[] getDeviceTemperatures(int type, int source) {
        switch (type) {
            case 0:
            case 1:
            case 2:
            case 3:
                switch (source) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        try {
                            return this.mService.getDeviceTemperatures(this.mContext.getOpPackageName(), type, source);
                        } catch (RemoteException e) {
                            throw e.rethrowFromSystemServer();
                        }
                    default:
                        Log.m104w(TAG, "Unknown device temperature source.");
                        return new float[0];
                }
            default:
                Log.m104w(TAG, "Unknown device temperature type.");
                return new float[0];
        }
    }

    public CpuUsageInfo[] getCpuUsages() {
        try {
            return this.mService.getCpuUsages(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public float[] getFanSpeeds() {
        try {
            return this.mService.getFanSpeeds(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
