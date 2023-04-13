package com.android.server.p014wm;

import android.util.ArrayMap;
import android.window.TaskSnapshot;
import com.android.server.p014wm.WindowContainer;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.AbsAppSnapshotCache */
/* loaded from: classes2.dex */
public abstract class AbsAppSnapshotCache<TYPE extends WindowContainer> {
    public final String mName;
    public final WindowManagerService mService;
    public final ArrayMap<ActivityRecord, Integer> mAppIdMap = new ArrayMap<>();
    public final ArrayMap<Integer, CacheEntry> mRunningCache = new ArrayMap<>();

    public abstract void putSnapshot(TYPE type, TaskSnapshot taskSnapshot);

    public AbsAppSnapshotCache(WindowManagerService windowManagerService, String str) {
        this.mService = windowManagerService;
        this.mName = str;
    }

    public void clearRunningCache() {
        this.mRunningCache.clear();
    }

    public final TaskSnapshot getSnapshot(Integer num) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                CacheEntry cacheEntry = this.mRunningCache.get(num);
                if (cacheEntry == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                TaskSnapshot taskSnapshot = cacheEntry.snapshot;
                WindowManagerService.resetPriorityAfterLockedSection();
                return taskSnapshot;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void onAppRemoved(ActivityRecord activityRecord) {
        Integer num = this.mAppIdMap.get(activityRecord);
        if (num != null) {
            removeRunningEntry(num);
        }
    }

    public void onAppDied(ActivityRecord activityRecord) {
        Integer num = this.mAppIdMap.get(activityRecord);
        if (num != null) {
            removeRunningEntry(num);
        }
    }

    public void onIdRemoved(Integer num) {
        removeRunningEntry(num);
    }

    public void removeRunningEntry(Integer num) {
        CacheEntry cacheEntry = this.mRunningCache.get(num);
        if (cacheEntry != null) {
            this.mAppIdMap.remove(cacheEntry.topApp);
            this.mRunningCache.remove(num);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        String str2 = str + "  ";
        String str3 = str2 + "  ";
        printWriter.println(str + "SnapshotCache " + this.mName);
        for (int size = this.mRunningCache.size() + (-1); size >= 0; size += -1) {
            CacheEntry valueAt = this.mRunningCache.valueAt(size);
            printWriter.println(str2 + "Entry token=" + this.mRunningCache.keyAt(size));
            printWriter.println(str3 + "topApp=" + valueAt.topApp);
            printWriter.println(str3 + "snapshot=" + valueAt.snapshot);
        }
    }

    /* renamed from: com.android.server.wm.AbsAppSnapshotCache$CacheEntry */
    /* loaded from: classes2.dex */
    public static final class CacheEntry {
        public final TaskSnapshot snapshot;
        public final ActivityRecord topApp;

        public CacheEntry(TaskSnapshot taskSnapshot, ActivityRecord activityRecord) {
            this.snapshot = taskSnapshot;
            this.topApp = activityRecord;
        }
    }
}
