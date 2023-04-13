package com.android.server.p014wm;

import android.util.ArraySet;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.ActivityServiceConnectionsHolder */
/* loaded from: classes2.dex */
public class ActivityServiceConnectionsHolder<T> {
    public final ActivityRecord mActivity;
    @GuardedBy({"mActivity"})
    public ArraySet<T> mConnections;
    public volatile boolean mIsDisconnecting;

    public ActivityServiceConnectionsHolder(ActivityRecord activityRecord) {
        this.mActivity = activityRecord;
    }

    public void addConnection(T t) {
        synchronized (this.mActivity) {
            if (this.mIsDisconnecting) {
                return;
            }
            if (this.mConnections == null) {
                this.mConnections = new ArraySet<>();
            }
            this.mConnections.add(t);
        }
    }

    public void removeConnection(T t) {
        synchronized (this.mActivity) {
            ArraySet<T> arraySet = this.mConnections;
            if (arraySet == null) {
                return;
            }
            arraySet.remove(t);
        }
    }

    public boolean isActivityVisible() {
        return this.mActivity.mVisibleForServiceConnection;
    }

    public int getActivityPid() {
        WindowProcessController windowProcessController = this.mActivity.app;
        if (windowProcessController != null) {
            return windowProcessController.getPid();
        }
        return -1;
    }

    public void forEachConnection(Consumer<T> consumer) {
        synchronized (this.mActivity) {
            ArraySet<T> arraySet = this.mConnections;
            if (arraySet != null && !arraySet.isEmpty()) {
                for (int size = this.mConnections.size() - 1; size >= 0; size--) {
                    consumer.accept(this.mConnections.valueAt(size));
                }
            }
        }
    }

    @GuardedBy({"mActivity"})
    public void disconnectActivityFromServices() {
        ArraySet<T> arraySet = this.mConnections;
        if (arraySet == null || arraySet.isEmpty() || this.mIsDisconnecting) {
            return;
        }
        this.mIsDisconnecting = true;
        this.mActivity.mAtmService.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityServiceConnectionsHolder$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ActivityServiceConnectionsHolder.this.lambda$disconnectActivityFromServices$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$disconnectActivityFromServices$0() {
        this.mActivity.mAtmService.mAmInternal.disconnectActivityFromServices(this);
        this.mIsDisconnecting = false;
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println(str + "activity=" + this.mActivity);
    }

    public String toString() {
        return String.valueOf(this.mConnections);
    }
}
