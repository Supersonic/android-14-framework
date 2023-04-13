package com.android.server.appop;

import android.util.SparseArray;
import com.android.internal.util.FrameworkStatsLog;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public interface AppOpsUidStateTracker {

    /* loaded from: classes.dex */
    public interface UidStateChangedCallback {
        void onUidStateChanged(int i, int i2, boolean z);
    }

    static int processStateToUidState(int i) {
        if (i == -1) {
            return 700;
        }
        if (i <= 1) {
            return 100;
        }
        if (i <= 2) {
            return 200;
        }
        if (i <= 3) {
            return 500;
        }
        if (i <= 4) {
            return FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND;
        }
        if (i <= 5) {
            return 500;
        }
        return i <= 11 ? 600 : 700;
    }

    void addUidStateChangedCallback(Executor executor, UidStateChangedCallback uidStateChangedCallback);

    void dumpEvents(PrintWriter printWriter);

    void dumpUidState(PrintWriter printWriter, int i, long j);

    int evalMode(int i, int i2, int i3);

    int getUidState(int i);

    boolean isUidInForeground(int i);

    void updateAppWidgetVisibility(SparseArray<String> sparseArray, boolean z);

    void updateUidProcState(int i, int i2, int i3);
}
