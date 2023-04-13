package com.android.server.p006am;

import android.util.SparseArray;
import com.android.server.p006am.ActivityManagerService;
/* renamed from: com.android.server.am.PendingTempAllowlists */
/* loaded from: classes.dex */
public final class PendingTempAllowlists {
    public final SparseArray<ActivityManagerService.PendingTempAllowlist> mPendingTempAllowlist = new SparseArray<>();
    public ActivityManagerService mService;

    public PendingTempAllowlists(ActivityManagerService activityManagerService) {
        this.mService = activityManagerService;
    }

    public void put(int i, ActivityManagerService.PendingTempAllowlist pendingTempAllowlist) {
        synchronized (this.mPendingTempAllowlist) {
            this.mPendingTempAllowlist.put(i, pendingTempAllowlist);
        }
    }

    public void removeAt(int i) {
        synchronized (this.mPendingTempAllowlist) {
            this.mPendingTempAllowlist.removeAt(i);
        }
    }

    public ActivityManagerService.PendingTempAllowlist get(int i) {
        ActivityManagerService.PendingTempAllowlist pendingTempAllowlist;
        synchronized (this.mPendingTempAllowlist) {
            pendingTempAllowlist = this.mPendingTempAllowlist.get(i);
        }
        return pendingTempAllowlist;
    }

    public int size() {
        int size;
        synchronized (this.mPendingTempAllowlist) {
            size = this.mPendingTempAllowlist.size();
        }
        return size;
    }

    public ActivityManagerService.PendingTempAllowlist valueAt(int i) {
        ActivityManagerService.PendingTempAllowlist valueAt;
        synchronized (this.mPendingTempAllowlist) {
            valueAt = this.mPendingTempAllowlist.valueAt(i);
        }
        return valueAt;
    }

    public int indexOfKey(int i) {
        int indexOfKey;
        synchronized (this.mPendingTempAllowlist) {
            indexOfKey = this.mPendingTempAllowlist.indexOfKey(i);
        }
        return indexOfKey;
    }
}
