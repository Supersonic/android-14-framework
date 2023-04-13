package android.internal.modules.utils.build;

import android.hardware.gnss.GnssSignalType;
import android.p008os.Build;
import android.util.SparseArray;
import java.util.Set;
/* loaded from: classes2.dex */
public final class UnboundedSdkLevel {
    private static final SparseArray<Set<String>> PREVIOUS_CODENAMES;
    private static final UnboundedSdkLevel sInstance;
    private final String mCodename;
    private final boolean mIsReleaseBuild;
    private final Set<String> mKnownCodenames;
    private final int mSdkInt;

    public static boolean isAtLeast(String version) {
        return sInstance.isAtLeastInternal(version);
    }

    public static boolean isAtMost(String version) {
        return sInstance.isAtMostInternal(version);
    }

    static {
        Set<String> set;
        SparseArray<Set<String>> sparseArray = new SparseArray<>(4);
        PREVIOUS_CODENAMES = sparseArray;
        sparseArray.put(29, Set.of(GnssSignalType.CODE_TYPE_Q));
        sparseArray.put(30, Set.of(GnssSignalType.CODE_TYPE_Q, "R"));
        sparseArray.put(31, Set.of(GnssSignalType.CODE_TYPE_Q, "R", GnssSignalType.CODE_TYPE_S));
        sparseArray.put(32, Set.of(GnssSignalType.CODE_TYPE_Q, "R", GnssSignalType.CODE_TYPE_S, "Sv2"));
        int i = Build.VERSION.SDK_INT;
        String str = Build.VERSION.CODENAME;
        if (SdkLevel.isAtLeastT()) {
            set = Build.VERSION.KNOWN_CODENAMES;
        } else {
            set = sparseArray.get(Build.VERSION.SDK_INT);
        }
        sInstance = new UnboundedSdkLevel(i, str, set);
    }

    UnboundedSdkLevel(int sdkInt, String codename, Set<String> knownCodenames) {
        this.mSdkInt = sdkInt;
        this.mCodename = codename;
        this.mIsReleaseBuild = "REL".equals(codename);
        this.mKnownCodenames = knownCodenames;
    }

    boolean isAtLeastInternal(String version) {
        String version2 = removeFingerprint(version);
        if (this.mIsReleaseBuild) {
            if (!isCodename(version2)) {
                return this.mSdkInt >= Integer.parseInt(version2);
            } else if (this.mKnownCodenames.contains(version2)) {
                throw new IllegalArgumentException("Artifact with a known codename " + version2 + " must be recompiled with a finalized integer version.");
            } else {
                return false;
            }
        } else if (isCodename(version2)) {
            return this.mKnownCodenames.contains(version2);
        } else {
            return this.mSdkInt >= Integer.parseInt(version2);
        }
    }

    boolean isAtMostInternal(String version) {
        String version2 = removeFingerprint(version);
        if (!this.mIsReleaseBuild) {
            return isCodename(version2) ? !this.mKnownCodenames.contains(version2) || this.mCodename.equals(version2) : this.mSdkInt < Integer.parseInt(version2);
        } else if (!isCodename(version2)) {
            return this.mSdkInt <= Integer.parseInt(version2);
        } else if (this.mKnownCodenames.contains(version2)) {
            throw new IllegalArgumentException("Artifact with a known codename " + version2 + " must be recompiled with a finalized integer version.");
        } else {
            return true;
        }
    }

    String removeFingerprint(String version) {
        int index;
        if (isCodename(version) && (index = version.indexOf(46)) != -1) {
            return version.substring(0, index);
        }
        return version;
    }

    private boolean isCodename(String version) {
        if (version.length() == 0) {
            throw new IllegalArgumentException();
        }
        return Character.isUpperCase(version.charAt(0));
    }
}
