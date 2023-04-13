package android.graphics;
/* loaded from: classes.dex */
public final class Compatibility {
    private static int sTargetSdkVersion = 0;

    private Compatibility() {
    }

    public static void setTargetSdkVersion(int targetSdkVersion) {
        sTargetSdkVersion = targetSdkVersion;
        Canvas.setCompatibilityVersion(targetSdkVersion);
    }

    public static int getTargetSdkVersion() {
        return sTargetSdkVersion;
    }
}
