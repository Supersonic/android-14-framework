package android.window;

import android.app.ActivityManager;
import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.view.SurfaceControl;
import android.window.ITaskOrganizer;
import android.window.TaskOrganizer;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes4.dex */
public class TaskOrganizer extends WindowOrganizer {
    private final Executor mExecutor;
    private final ITaskOrganizer mInterface;
    private final ITaskOrganizerController mTaskOrganizerController;

    public TaskOrganizer() {
        this(null, null);
    }

    public TaskOrganizer(ITaskOrganizerController taskOrganizerController, Executor executor) {
        this.mInterface = new BinderC39691();
        this.mExecutor = executor != null ? executor : new PendingIntent$$ExternalSyntheticLambda1();
        this.mTaskOrganizerController = taskOrganizerController != null ? taskOrganizerController : getController();
    }

    public List<TaskAppearedInfo> registerOrganizer() {
        try {
            return this.mTaskOrganizerController.registerTaskOrganizer(this.mInterface).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unregisterOrganizer() {
        try {
            this.mTaskOrganizerController.unregisterTaskOrganizer(this.mInterface);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addStartingWindow(StartingWindowInfo info) {
    }

    public void removeStartingWindow(StartingWindowRemovalInfo removalInfo) {
    }

    public void copySplashScreenView(int taskId) {
    }

    public void onAppSplashScreenViewRemoved(int taskId) {
    }

    public void onTaskAppeared(ActivityManager.RunningTaskInfo taskInfo, SurfaceControl leash) {
    }

    public void onTaskVanished(ActivityManager.RunningTaskInfo taskInfo) {
    }

    public void onTaskInfoChanged(ActivityManager.RunningTaskInfo taskInfo) {
    }

    public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo) {
    }

    public void onImeDrawnOnTask(int taskId) {
    }

    public void createRootTask(int displayId, int windowingMode, IBinder launchCookie, boolean removeWithTaskOrganizer) {
        try {
            this.mTaskOrganizerController.createRootTask(displayId, windowingMode, launchCookie, removeWithTaskOrganizer);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void createRootTask(int displayId, int windowingMode, IBinder launchCookie) {
        createRootTask(displayId, windowingMode, launchCookie, false);
    }

    public boolean deleteRootTask(WindowContainerToken task) {
        try {
            return this.mTaskOrganizerController.deleteRootTask(task);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<ActivityManager.RunningTaskInfo> getChildTasks(WindowContainerToken parent, int[] activityTypes) {
        try {
            return this.mTaskOrganizerController.getChildTasks(parent, activityTypes);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<ActivityManager.RunningTaskInfo> getRootTasks(int displayId, int[] activityTypes) {
        try {
            return this.mTaskOrganizerController.getRootTasks(displayId, activityTypes);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public WindowContainerToken getImeTarget(int display) {
        try {
            return this.mTaskOrganizerController.getImeTarget(display);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setInterceptBackPressedOnTaskRoot(WindowContainerToken task, boolean interceptBackPressed) {
        try {
            this.mTaskOrganizerController.setInterceptBackPressedOnTaskRoot(task, interceptBackPressed);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void restartTaskTopActivityProcessIfVisible(WindowContainerToken task) {
        try {
            this.mTaskOrganizerController.restartTaskTopActivityProcessIfVisible(task);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void updateCameraCompatControlState(WindowContainerToken task, int state) {
        try {
            this.mTaskOrganizerController.updateCameraCompatControlState(task, state);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setIsIgnoreOrientationRequestDisabled(boolean isDisabled) {
        try {
            this.mTaskOrganizerController.setIsIgnoreOrientationRequestDisabled(isDisabled);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Executor getExecutor() {
        return this.mExecutor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.window.TaskOrganizer$1 */
    /* loaded from: classes4.dex */
    public class BinderC39691 extends ITaskOrganizer.Stub {
        BinderC39691() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addStartingWindow$0(StartingWindowInfo windowInfo) {
            TaskOrganizer.this.addStartingWindow(windowInfo);
        }

        @Override // android.window.ITaskOrganizer
        public void addStartingWindow(final StartingWindowInfo windowInfo) {
            TaskOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskOrganizer$1$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizer.BinderC39691.this.lambda$addStartingWindow$0(windowInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeStartingWindow$1(StartingWindowRemovalInfo removalInfo) {
            TaskOrganizer.this.removeStartingWindow(removalInfo);
        }

        @Override // android.window.ITaskOrganizer
        public void removeStartingWindow(final StartingWindowRemovalInfo removalInfo) {
            TaskOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskOrganizer$1$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizer.BinderC39691.this.lambda$removeStartingWindow$1(removalInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$copySplashScreenView$2(int taskId) {
            TaskOrganizer.this.copySplashScreenView(taskId);
        }

        @Override // android.window.ITaskOrganizer
        public void copySplashScreenView(final int taskId) {
            TaskOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskOrganizer$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizer.BinderC39691.this.lambda$copySplashScreenView$2(taskId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAppSplashScreenViewRemoved$3(int taskId) {
            TaskOrganizer.this.onAppSplashScreenViewRemoved(taskId);
        }

        @Override // android.window.ITaskOrganizer
        public void onAppSplashScreenViewRemoved(final int taskId) {
            TaskOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskOrganizer$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizer.BinderC39691.this.lambda$onAppSplashScreenViewRemoved$3(taskId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTaskAppeared$4(ActivityManager.RunningTaskInfo taskInfo, SurfaceControl leash) {
            TaskOrganizer.this.onTaskAppeared(taskInfo, leash);
        }

        @Override // android.window.ITaskOrganizer
        public void onTaskAppeared(final ActivityManager.RunningTaskInfo taskInfo, final SurfaceControl leash) {
            TaskOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskOrganizer$1$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizer.BinderC39691.this.lambda$onTaskAppeared$4(taskInfo, leash);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTaskVanished$5(ActivityManager.RunningTaskInfo taskInfo) {
            TaskOrganizer.this.onTaskVanished(taskInfo);
        }

        @Override // android.window.ITaskOrganizer
        public void onTaskVanished(final ActivityManager.RunningTaskInfo taskInfo) {
            TaskOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskOrganizer$1$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizer.BinderC39691.this.lambda$onTaskVanished$5(taskInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTaskInfoChanged$6(ActivityManager.RunningTaskInfo info) {
            TaskOrganizer.this.onTaskInfoChanged(info);
        }

        @Override // android.window.ITaskOrganizer
        public void onTaskInfoChanged(final ActivityManager.RunningTaskInfo info) {
            TaskOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskOrganizer$1$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizer.BinderC39691.this.lambda$onTaskInfoChanged$6(info);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackPressedOnTaskRoot$7(ActivityManager.RunningTaskInfo info) {
            TaskOrganizer.this.onBackPressedOnTaskRoot(info);
        }

        @Override // android.window.ITaskOrganizer
        public void onBackPressedOnTaskRoot(final ActivityManager.RunningTaskInfo info) {
            TaskOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskOrganizer$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizer.BinderC39691.this.lambda$onBackPressedOnTaskRoot$7(info);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onImeDrawnOnTask$8(int taskId) {
            TaskOrganizer.this.onImeDrawnOnTask(taskId);
        }

        @Override // android.window.ITaskOrganizer
        public void onImeDrawnOnTask(final int taskId) {
            TaskOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskOrganizer$1$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizer.BinderC39691.this.lambda$onImeDrawnOnTask$8(taskId);
                }
            });
        }
    }

    private ITaskOrganizerController getController() {
        try {
            return getWindowOrganizerController().getTaskOrganizerController();
        } catch (RemoteException e) {
            return null;
        }
    }
}
