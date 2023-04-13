package com.android.server.p014wm;

import android.window.TaskSnapshot;
import com.android.server.p014wm.AbsAppSnapshotCache;
/* renamed from: com.android.server.wm.TaskSnapshotCache */
/* loaded from: classes2.dex */
public class TaskSnapshotCache extends AbsAppSnapshotCache<Task> {
    public final AppSnapshotLoader mLoader;

    public TaskSnapshotCache(WindowManagerService windowManagerService, AppSnapshotLoader appSnapshotLoader) {
        super(windowManagerService, "Task");
        this.mLoader = appSnapshotLoader;
    }

    @Override // com.android.server.p014wm.AbsAppSnapshotCache
    public void putSnapshot(Task task, TaskSnapshot taskSnapshot) {
        AbsAppSnapshotCache.CacheEntry cacheEntry = this.mRunningCache.get(Integer.valueOf(task.mTaskId));
        if (cacheEntry != null) {
            this.mAppIdMap.remove(cacheEntry.topApp);
        }
        ActivityRecord topMostActivity = task.getTopMostActivity();
        this.mAppIdMap.put(topMostActivity, Integer.valueOf(task.mTaskId));
        this.mRunningCache.put(Integer.valueOf(task.mTaskId), new AbsAppSnapshotCache.CacheEntry(taskSnapshot, topMostActivity));
    }

    public TaskSnapshot getSnapshot(int i, int i2, boolean z, boolean z2) {
        TaskSnapshot snapshot = getSnapshot(Integer.valueOf(i));
        if (snapshot != null) {
            return snapshot;
        }
        if (z) {
            return tryRestoreFromDisk(i, i2, z2);
        }
        return null;
    }

    public final TaskSnapshot tryRestoreFromDisk(int i, int i2, boolean z) {
        return this.mLoader.loadTask(i, i2, z);
    }
}
