package android.content.p001pm;

import android.content.ComponentName;
/* renamed from: android.content.pm.ActivityPresentationInfo */
/* loaded from: classes.dex */
public final class ActivityPresentationInfo {
    public final ComponentName componentName;
    public final int displayId;
    public final int taskId;

    public ActivityPresentationInfo(int taskId, int displayId, ComponentName componentName) {
        this.taskId = taskId;
        this.displayId = displayId;
        this.componentName = componentName;
    }
}
