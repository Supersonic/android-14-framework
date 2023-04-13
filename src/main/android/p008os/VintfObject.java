package android.p008os;

import android.util.Slog;
import java.util.Map;
/* renamed from: android.os.VintfObject */
/* loaded from: classes3.dex */
public class VintfObject {
    private static final String LOG_TAG = "VintfObject";

    public static native String[] getHalNamesAndVersions();

    public static native String getPlatformSepolicyVersion();

    public static native String getSepolicyVersion();

    public static native Long getTargetFrameworkCompatibilityMatrixVersion();

    public static native Map<String, String[]> getVndkSnapshots();

    public static native String[] report();

    public static native int verifyWithoutAvb();

    @Deprecated
    public static int verify(String[] packageInfo) {
        if (packageInfo == null || packageInfo.length <= 0) {
            Slog.m90w(LOG_TAG, "VintfObject.verify() is deprecated. Call verifyWithoutAvb() instead.");
            return verifyWithoutAvb();
        }
        Slog.m90w(LOG_TAG, "VintfObject.verify() with non-empty packageInfo is deprecated. Skipping compatibility checks for update package.");
        return 0;
    }

    private VintfObject() {
    }
}
