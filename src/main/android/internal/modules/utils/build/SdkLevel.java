package android.internal.modules.utils.build;

import android.p008os.Build;
/* loaded from: classes2.dex */
public final class SdkLevel {
    private SdkLevel() {
    }

    public static boolean isAtLeastR() {
        return true;
    }

    public static boolean isAtLeastS() {
        return true;
    }

    public static boolean isAtLeastSv2() {
        return true;
    }

    public static boolean isAtLeastT() {
        return true;
    }

    public static boolean isAtLeastU() {
        return isAtLeastPreReleaseCodename("UpsideDownCake");
    }

    private static boolean isAtLeastPreReleaseCodename(String codename) {
        return !"REL".equals(Build.VERSION.CODENAME) && Build.VERSION.CODENAME.compareTo(codename) >= 0;
    }
}
