package com.android.server.appop;

import android.util.ArraySet;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public interface AppOpsCheckingServiceInterface {
    void clearAllModes();

    boolean dumpListeners(int i, int i2, String str, PrintWriter printWriter);

    SparseBooleanArray evalForegroundPackageOps(String str, SparseBooleanArray sparseBooleanArray, int i);

    SparseBooleanArray evalForegroundUidOps(int i, SparseBooleanArray sparseBooleanArray);

    SparseIntArray getNonDefaultPackageModes(String str, int i);

    SparseIntArray getNonDefaultUidModes(int i);

    ArraySet<OnOpModeChangedListener> getOpModeChangedListeners(int i);

    int getPackageMode(String str, int i, int i2);

    ArraySet<OnOpModeChangedListener> getPackageModeChangedListeners(String str);

    int getUidMode(int i, int i2);

    void notifyOpChanged(OnOpModeChangedListener onOpModeChangedListener, int i, int i2, String str);

    void notifyOpChangedForAllPkgsInUid(int i, int i2, boolean z, OnOpModeChangedListener onOpModeChangedListener);

    void notifyWatchersOfChange(int i, int i2);

    void readState();

    void removeListener(OnOpModeChangedListener onOpModeChangedListener);

    boolean removePackage(String str, int i);

    void removeUid(int i);

    void setPackageMode(String str, int i, int i2, int i3);

    boolean setUidMode(int i, int i2, int i3);

    void shutdown();

    void startWatchingOpModeChanged(OnOpModeChangedListener onOpModeChangedListener, int i);

    void startWatchingPackageModeChanged(OnOpModeChangedListener onOpModeChangedListener, String str);

    void systemReady();

    @VisibleForTesting
    void writeState();
}
