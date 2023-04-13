package android.app;

import android.app.ActivityManager;
import android.app.IActivityTaskManager;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.p008os.Build;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.DisplayMetrics;
import android.util.Singleton;
import android.view.RemoteAnimationDefinition;
import android.window.SplashScreenView;
import com.android.internal.C4057R;
import java.util.List;
/* loaded from: classes.dex */
public class ActivityTaskManager {
    public static final int DEFAULT_MINIMAL_SPLIT_SCREEN_DISPLAY_SIZE_DP = 440;
    public static final String EXTRA_IGNORE_TARGET_SECURITY = "android.app.extra.EXTRA_IGNORE_TARGET_SECURITY";
    public static final String EXTRA_OPTIONS = "android.app.extra.OPTIONS";
    public static final int INVALID_STACK_ID = -1;
    public static final int INVALID_TASK_ID = -1;
    public static final int INVALID_WINDOWING_MODE = -1;
    public static final int RESIZE_MODE_FORCED = 2;
    public static final int RESIZE_MODE_PRESERVE_WINDOW = 1;
    public static final int RESIZE_MODE_SYSTEM = 0;
    public static final int RESIZE_MODE_SYSTEM_SCREEN_ROTATION = 1;
    public static final int RESIZE_MODE_USER = 1;
    public static final int RESIZE_MODE_USER_FORCED = 3;
    private static int sMaxRecentTasks = -1;
    private static final Singleton<ActivityTaskManager> sInstance = new Singleton<ActivityTaskManager>() { // from class: android.app.ActivityTaskManager.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public ActivityTaskManager create() {
            return new ActivityTaskManager();
        }
    };
    private static final Singleton<IActivityTaskManager> IActivityTaskManagerSingleton = new Singleton<IActivityTaskManager>() { // from class: android.app.ActivityTaskManager.2
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public IActivityTaskManager create() {
            IBinder b = ServiceManager.getService(Context.ACTIVITY_TASK_SERVICE);
            return IActivityTaskManager.Stub.asInterface(b);
        }
    };

    private ActivityTaskManager() {
    }

    public static ActivityTaskManager getInstance() {
        return sInstance.get();
    }

    public static IActivityTaskManager getService() {
        return IActivityTaskManagerSingleton.get();
    }

    public void removeRootTasksInWindowingModes(int[] windowingModes) {
        try {
            getService().removeRootTasksInWindowingModes(windowingModes);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeRootTasksWithActivityTypes(int[] activityTypes) {
        try {
            getService().removeRootTasksWithActivityTypes(activityTypes);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeAllVisibleRecentTasks() {
        try {
            getService().removeAllVisibleRecentTasks();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static int getMaxRecentTasksStatic() {
        int i = sMaxRecentTasks;
        if (i < 0) {
            int i2 = ActivityManager.isLowRamDeviceStatic() ? 36 : 48;
            sMaxRecentTasks = i2;
            return i2;
        }
        return i;
    }

    public void onSplashScreenViewCopyFinished(int taskId, SplashScreenView.SplashScreenViewParcelable parcelable) {
        try {
            getService().onSplashScreenViewCopyFinished(taskId, parcelable);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static int getDefaultAppRecentsLimitStatic() {
        return getMaxRecentTasksStatic() / 6;
    }

    public static int getMaxAppRecentsLimitStatic() {
        return getMaxRecentTasksStatic() / 2;
    }

    public static boolean supportsMultiWindow(Context context) {
        boolean isWatch = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WATCH);
        return (!ActivityManager.isLowRamDeviceStatic() || isWatch) && Resources.getSystem().getBoolean(C4057R.bool.config_supportsMultiWindow);
    }

    public static boolean supportsSplitScreenMultiWindow(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getDisplay().getRealMetrics(dm);
        int widthDp = (int) (dm.widthPixels / dm.density);
        int heightDp = (int) (dm.heightPixels / dm.density);
        return Math.max(widthDp, heightDp) >= 440 && supportsMultiWindow(context) && Resources.getSystem().getBoolean(C4057R.bool.config_supportsSplitScreenMultiWindow);
    }

    public void startSystemLockTaskMode(int taskId) {
        try {
            getService().startSystemLockTaskMode(taskId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void stopSystemLockTaskMode() {
        try {
            getService().stopSystemLockTaskMode();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void moveTaskToRootTask(int taskId, int rootTaskId, boolean toTop) {
        try {
            getService().moveTaskToRootTask(taskId, rootTaskId, toTop);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void resizeTask(int taskId, Rect bounds) {
        try {
            getService().resizeTask(taskId, bounds, 0);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clearLaunchParamsForPackages(List<String> packageNames) {
        try {
            getService().clearLaunchParamsForPackages(packageNames);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public static boolean currentUiModeSupportsErrorDialogs(Configuration config) {
        int modeType = config.uiMode & 15;
        return (modeType == 3 || (modeType == 6 && Build.IS_USER) || modeType == 4 || modeType == 7) ? false : true;
    }

    public static boolean currentUiModeSupportsErrorDialogs(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return currentUiModeSupportsErrorDialogs(config);
    }

    public static int getMaxNumPictureInPictureActions(Context context) {
        return context.getResources().getInteger(C4057R.integer.config_pictureInPictureMaxNumberOfActions);
    }

    public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum) {
        return getTasks(maxNum, false, false, -1);
    }

    public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum, boolean filterOnlyVisibleRecents) {
        return getTasks(maxNum, filterOnlyVisibleRecents, false, -1);
    }

    public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum, boolean filterOnlyVisibleRecents, boolean keepIntentExtra) {
        return getTasks(maxNum, filterOnlyVisibleRecents, keepIntentExtra, -1);
    }

    public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum, boolean filterOnlyVisibleRecents, boolean keepIntentExtra, int displayId) {
        try {
            return getService().getTasks(maxNum, filterOnlyVisibleRecents, keepIntentExtra, displayId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<ActivityManager.RecentTaskInfo> getRecentTasks(int maxNum, int flags, int userId) {
        try {
            return getService().getRecentTasks(maxNum, flags, userId).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void registerTaskStackListener(TaskStackListener listener) {
        try {
            getService().registerTaskStackListener(listener);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unregisterTaskStackListener(TaskStackListener listener) {
        try {
            getService().unregisterTaskStackListener(listener);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Rect getTaskBounds(int taskId) {
        try {
            return getService().getTaskBounds(taskId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void registerRemoteAnimationsForDisplay(int displayId, RemoteAnimationDefinition definition) {
        try {
            getService().registerRemoteAnimationsForDisplay(displayId, definition);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isInLockTaskMode() {
        try {
            return getService().isInLockTaskMode();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean removeTask(int taskId) {
        try {
            return getService().removeTask(taskId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void detachNavigationBarFromApp(IBinder transition) {
        try {
            getService().detachNavigationBarFromApp(transition);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void updateLockTaskPackages(Context context, String[] packages) {
        try {
            getService().updateLockTaskPackages(context.getUserId(), packages);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes.dex */
    public static class RootTaskInfo extends TaskInfo implements Parcelable {
        public static final Parcelable.Creator<RootTaskInfo> CREATOR = new Parcelable.Creator<RootTaskInfo>() { // from class: android.app.ActivityTaskManager.RootTaskInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RootTaskInfo createFromParcel(Parcel source) {
                return new RootTaskInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RootTaskInfo[] newArray(int size) {
                return new RootTaskInfo[size];
            }
        };
        public Rect bounds;
        public Rect[] childTaskBounds;
        public int[] childTaskIds;
        public String[] childTaskNames;
        public int[] childTaskUserIds;
        public int position;
        public boolean visible;

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.app.TaskInfo, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedObject(this.bounds, flags);
            dest.writeIntArray(this.childTaskIds);
            dest.writeStringArray(this.childTaskNames);
            dest.writeTypedArray(this.childTaskBounds, flags);
            dest.writeIntArray(this.childTaskUserIds);
            dest.writeInt(this.visible ? 1 : 0);
            dest.writeInt(this.position);
            super.writeToParcel(dest, flags);
        }

        @Override // android.app.TaskInfo
        void readFromParcel(Parcel source) {
            this.bounds = (Rect) source.readTypedObject(Rect.CREATOR);
            this.childTaskIds = source.createIntArray();
            this.childTaskNames = source.createStringArray();
            this.childTaskBounds = (Rect[]) source.createTypedArray(Rect.CREATOR);
            this.childTaskUserIds = source.createIntArray();
            this.visible = source.readInt() > 0;
            this.position = source.readInt();
            super.readFromParcel(source);
        }

        public RootTaskInfo() {
            this.bounds = new Rect();
        }

        private RootTaskInfo(Parcel source) {
            this.bounds = new Rect();
            readFromParcel(source);
        }

        @Override // android.app.TaskInfo
        public String toString() {
            StringBuilder sb = new StringBuilder(256);
            sb.append("RootTask id=");
            sb.append(this.taskId);
            sb.append(" bounds=");
            sb.append(this.bounds.toShortString());
            sb.append(" displayId=");
            sb.append(this.displayId);
            sb.append(" userId=");
            sb.append(this.userId);
            sb.append("\n");
            sb.append(" configuration=");
            sb.append(this.configuration);
            sb.append("\n");
            for (int i = 0; i < this.childTaskIds.length; i++) {
                sb.append("  taskId=");
                sb.append(this.childTaskIds[i]);
                sb.append(": ");
                sb.append(this.childTaskNames[i]);
                if (this.childTaskBounds != null) {
                    sb.append(" bounds=");
                    sb.append(this.childTaskBounds[i].toShortString());
                }
                sb.append(" userId=").append(this.childTaskUserIds[i]);
                sb.append(" visible=").append(this.visible);
                if (this.topActivity != null) {
                    sb.append(" topActivity=").append(this.topActivity);
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }
}
