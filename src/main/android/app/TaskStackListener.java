package android.app;

import android.app.ActivityManager;
import android.app.ITaskStackListener;
import android.content.ComponentName;
import android.p008os.RemoteException;
import android.window.TaskSnapshot;
/* loaded from: classes.dex */
public abstract class TaskStackListener extends ITaskStackListener.Stub {
    private boolean mIsRemote = true;

    public void setIsLocal() {
        this.mIsRemote = false;
    }

    @Override // android.app.ITaskStackListener
    public void onTaskStackChanged() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityPinned(String packageName, int userId, int taskId, int rootTaskId) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityUnpinned() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo task, boolean homeTaskVisible, boolean clearedTask, boolean wasVisible) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityForcedResizable(String packageName, int taskId, int reason) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityDismissingDockedTask() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityLaunchOnSecondaryDisplayFailed(ActivityManager.RunningTaskInfo taskInfo, int requestedDisplayId) throws RemoteException {
        onActivityLaunchOnSecondaryDisplayFailed();
    }

    @Deprecated
    public void onActivityLaunchOnSecondaryDisplayFailed() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityLaunchOnSecondaryDisplayRerouted(ActivityManager.RunningTaskInfo taskInfo, int requestedDisplayId) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskCreated(int taskId, ComponentName componentName) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskRemoved(int taskId) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        onTaskMovedToFront(taskInfo.taskId);
    }

    @Deprecated
    public void onTaskMovedToFront(int taskId) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskRemovalStarted(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        onTaskRemovalStarted(taskInfo.taskId);
    }

    @Deprecated
    public void onTaskRemovalStarted(int taskId) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskDescriptionChanged(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        onTaskDescriptionChanged(taskInfo.taskId, taskInfo.taskDescription);
    }

    @Deprecated
    public void onTaskDescriptionChanged(int taskId, ActivityManager.TaskDescription td) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityRequestedOrientationChanged(int taskId, int requestedOrientation) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskProfileLocked(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskSnapshotChanged(int taskId, TaskSnapshot snapshot) throws RemoteException {
        if (this.mIsRemote && snapshot != null && snapshot.getHardwareBuffer() != null) {
            snapshot.getHardwareBuffer().close();
        }
    }

    @Override // android.app.ITaskStackListener
    public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskDisplayChanged(int taskId, int newDisplayId) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onRecentTaskListUpdated() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onRecentTaskListFrozenChanged(boolean frozen) {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskFocusChanged(int taskId, boolean focused) {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskRequestedOrientationChanged(int taskId, int requestedOrientation) {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityRotation(int displayId) {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskMovedToBack(ActivityManager.RunningTaskInfo taskInfo) {
    }

    @Override // android.app.ITaskStackListener
    public void onLockTaskModeChanged(int mode) {
    }
}
