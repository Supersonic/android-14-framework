package android.media.p007tv.tuner;

import android.annotation.SystemApi;
import android.media.MediaMetrics;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.TunerVersionChecker */
/* loaded from: classes2.dex */
public final class TunerVersionChecker {
    private static final String TAG = "TunerVersionChecker";
    public static final int TUNER_VERSION_1_0 = 65536;
    public static final int TUNER_VERSION_1_1 = 65537;
    public static final int TUNER_VERSION_2_0 = 131072;
    public static final int TUNER_VERSION_3_0 = 196608;
    public static final int TUNER_VERSION_UNKNOWN = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.TunerVersionChecker$TunerVersion */
    /* loaded from: classes2.dex */
    public @interface TunerVersion {
    }

    private TunerVersionChecker() {
    }

    public static int getTunerVersion() {
        return Tuner.getTunerVersion();
    }

    public static boolean supportTunerVersion(int version) {
        int currentVersion = Tuner.getTunerVersion();
        return isHigherOrEqualVersionTo(version) && getMajorVersion(version) == getMajorVersion(currentVersion);
    }

    public static boolean isHigherOrEqualVersionTo(int version) {
        int currentVersion = Tuner.getTunerVersion();
        return currentVersion >= version;
    }

    public static int getMajorVersion(int version) {
        return ((-65536) & version) >>> 16;
    }

    public static int getMinorVersion(int version) {
        return 65535 & version;
    }

    public static boolean checkHigherOrEqualVersionTo(int version, String methodName) {
        if (!isHigherOrEqualVersionTo(version)) {
            Log.m110e(TAG, "Current Tuner version " + getMajorVersion(Tuner.getTunerVersion()) + MediaMetrics.SEPARATOR + getMinorVersion(Tuner.getTunerVersion()) + " does not support " + methodName + MediaMetrics.SEPARATOR);
            return false;
        }
        return true;
    }

    public static boolean checkSupportVersion(int version, String methodName) {
        if (!supportTunerVersion(version)) {
            Log.m110e(TAG, "Current Tuner version " + getMajorVersion(Tuner.getTunerVersion()) + MediaMetrics.SEPARATOR + getMinorVersion(Tuner.getTunerVersion()) + " does not support " + methodName + MediaMetrics.SEPARATOR);
            return false;
        }
        return true;
    }
}
