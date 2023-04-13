package com.android.server.app;

import android.content.ComponentName;
import java.util.Objects;
/* loaded from: classes.dex */
public final class GameTaskInfo {
    public final ComponentName mComponentName;
    public final boolean mIsGameTask;
    public final int mTaskId;

    public GameTaskInfo(int i, boolean z, ComponentName componentName) {
        this.mTaskId = i;
        this.mIsGameTask = z;
        this.mComponentName = componentName;
    }

    public String toString() {
        return "GameTaskInfo{mTaskId=" + this.mTaskId + ", mIsGameTask=" + this.mIsGameTask + ", mComponentName=" + this.mComponentName + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GameTaskInfo) {
            GameTaskInfo gameTaskInfo = (GameTaskInfo) obj;
            return this.mTaskId == gameTaskInfo.mTaskId && this.mIsGameTask == gameTaskInfo.mIsGameTask && this.mComponentName.equals(gameTaskInfo.mComponentName);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mTaskId), Boolean.valueOf(this.mIsGameTask), this.mComponentName);
    }
}
