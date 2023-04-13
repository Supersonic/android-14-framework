package com.android.modules.utils.build;

import android.os.Build;
import android.util.SparseArray;
import androidx.annotation.NonNull;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Set;
/* loaded from: classes.dex */
public final class UnboundedSdkLevel {
    public static final SparseArray<Set<String>> PREVIOUS_CODENAMES;
    public static final UnboundedSdkLevel sInstance;
    public final String mCodename;
    public final boolean mIsReleaseBuild;
    public final Set<String> mKnownCodenames;
    public final int mSdkInt;

    public static boolean isAtLeast(@NonNull String str) {
        return sInstance.isAtLeastInternal(str);
    }

    public static boolean isAtMost(@NonNull String str) {
        return sInstance.isAtMostInternal(str);
    }

    static {
        Set<String> set;
        SparseArray<Set<String>> sparseArray = new SparseArray<>(4);
        PREVIOUS_CODENAMES = sparseArray;
        sparseArray.put(29, Set.of("Q"));
        sparseArray.put(30, Set.of("Q", "R"));
        sparseArray.put(31, Set.of("Q", "R", "S"));
        sparseArray.put(32, Set.of("Q", "R", "S", "Sv2"));
        int i = Build.VERSION.SDK_INT;
        String str = Build.VERSION.CODENAME;
        if (SdkLevel.isAtLeastT()) {
            set = Build.VERSION.KNOWN_CODENAMES;
        } else {
            set = sparseArray.get(i);
        }
        sInstance = new UnboundedSdkLevel(i, str, set);
    }

    @VisibleForTesting
    public UnboundedSdkLevel(int i, String str, Set<String> set) {
        this.mSdkInt = i;
        this.mCodename = str;
        this.mIsReleaseBuild = "REL".equals(str);
        this.mKnownCodenames = set;
    }

    @VisibleForTesting
    public boolean isAtLeastInternal(@NonNull String str) {
        String removeFingerprint = removeFingerprint(str);
        if (this.mIsReleaseBuild) {
            if (!isCodename(removeFingerprint)) {
                return this.mSdkInt >= Integer.parseInt(removeFingerprint);
            } else if (this.mKnownCodenames.contains(removeFingerprint)) {
                throw new IllegalArgumentException("Artifact with a known codename " + removeFingerprint + " must be recompiled with a finalized integer version.");
            } else {
                return false;
            }
        } else if (isCodename(removeFingerprint)) {
            return this.mKnownCodenames.contains(removeFingerprint);
        } else {
            return this.mSdkInt >= Integer.parseInt(removeFingerprint);
        }
    }

    @VisibleForTesting
    public boolean isAtMostInternal(@NonNull String str) {
        String removeFingerprint = removeFingerprint(str);
        if (!this.mIsReleaseBuild) {
            return isCodename(removeFingerprint) ? !this.mKnownCodenames.contains(removeFingerprint) || this.mCodename.equals(removeFingerprint) : this.mSdkInt < Integer.parseInt(removeFingerprint);
        } else if (!isCodename(removeFingerprint)) {
            return this.mSdkInt <= Integer.parseInt(removeFingerprint);
        } else if (this.mKnownCodenames.contains(removeFingerprint)) {
            throw new IllegalArgumentException("Artifact with a known codename " + removeFingerprint + " must be recompiled with a finalized integer version.");
        } else {
            return true;
        }
    }

    @VisibleForTesting
    public String removeFingerprint(@NonNull String str) {
        int indexOf;
        return (!isCodename(str) || (indexOf = str.indexOf(46)) == -1) ? str : str.substring(0, indexOf);
    }

    public final boolean isCodename(String str) {
        if (str.length() == 0) {
            throw new IllegalArgumentException();
        }
        return Character.isUpperCase(str.charAt(0));
    }
}
