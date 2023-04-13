package com.android.server.app;

import android.app.ActivityManager;
import android.app.IActivityTaskManager;
import android.content.ComponentName;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.LruCache;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
/* loaded from: classes.dex */
public final class GameTaskInfoProvider {
    public final IActivityTaskManager mActivityTaskManager;
    public final GameClassifier mGameClassifier;
    public final UserHandle mUserHandle;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final LruCache<Integer, GameTaskInfo> mGameTaskInfoCache = new LruCache<>(50);

    public GameTaskInfoProvider(UserHandle userHandle, IActivityTaskManager iActivityTaskManager, GameClassifier gameClassifier) {
        this.mUserHandle = userHandle;
        this.mActivityTaskManager = iActivityTaskManager;
        this.mGameClassifier = gameClassifier;
    }

    public GameTaskInfo get(int i) {
        ComponentName componentName;
        synchronized (this.mLock) {
            GameTaskInfo gameTaskInfo = this.mGameTaskInfoCache.get(Integer.valueOf(i));
            if (gameTaskInfo != null) {
                return gameTaskInfo;
            }
            ActivityManager.RunningTaskInfo runningTaskInfo = getRunningTaskInfo(i);
            if (runningTaskInfo == null || (componentName = runningTaskInfo.baseActivity) == null) {
                return null;
            }
            return generateGameInfo(i, componentName);
        }
    }

    public GameTaskInfo get(int i, ComponentName componentName) {
        synchronized (this.mLock) {
            GameTaskInfo gameTaskInfo = this.mGameTaskInfoCache.get(Integer.valueOf(i));
            if (gameTaskInfo != null) {
                if (!gameTaskInfo.mComponentName.equals(componentName)) {
                    return gameTaskInfo;
                }
                Slog.w("GameTaskInfoProvider", "Found cached task info for taskId " + i + " but cached component name " + gameTaskInfo.mComponentName + " does not match " + componentName);
            }
            return generateGameInfo(i, componentName);
        }
    }

    public ActivityManager.RunningTaskInfo getRunningTaskInfo(int i) {
        try {
            for (ActivityManager.RunningTaskInfo runningTaskInfo : this.mActivityTaskManager.getTasks(Integer.MAX_VALUE, false, false, -1)) {
                if (runningTaskInfo.taskId == i) {
                    return runningTaskInfo;
                }
            }
            return null;
        } catch (RemoteException unused) {
            Slog.w("GameTaskInfoProvider", "Failed to fetch running tasks");
            return null;
        }
    }

    public final GameTaskInfo generateGameInfo(int i, ComponentName componentName) {
        GameTaskInfo gameTaskInfo = new GameTaskInfo(i, this.mGameClassifier.isGame(componentName.getPackageName(), this.mUserHandle), componentName);
        synchronized (this.mLock) {
            this.mGameTaskInfoCache.put(Integer.valueOf(i), gameTaskInfo);
        }
        return gameTaskInfo;
    }
}
