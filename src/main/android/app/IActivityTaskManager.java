package android.app;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityClientController;
import android.app.IActivityController;
import android.app.IApplicationThread;
import android.app.IAssistDataReceiver;
import android.app.IScreenCaptureObserver;
import android.app.ITaskStackListener;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.p001pm.ConfigurationInfo;
import android.content.p001pm.ParceledListSlice;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.service.voice.IVoiceInteractionSession;
import android.view.IRecentsAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationDefinition;
import android.window.BackAnimationAdapter;
import android.window.BackNavigationInfo;
import android.window.IWindowOrganizerController;
import android.window.SplashScreenView;
import android.window.TaskSnapshot;
import com.android.internal.app.IVoiceInteractor;
import java.util.List;
/* loaded from: classes.dex */
public interface IActivityTaskManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.IActivityTaskManager";

    int addAppTask(IBinder iBinder, Intent intent, ActivityManager.TaskDescription taskDescription, Bitmap bitmap) throws RemoteException;

    void alwaysShowUnsupportedCompileSdkWarning(ComponentName componentName) throws RemoteException;

    void cancelRecentsAnimation(boolean z) throws RemoteException;

    void cancelTaskWindowTransition(int i) throws RemoteException;

    void clearLaunchParamsForPackages(List<String> list) throws RemoteException;

    void detachNavigationBarFromApp(IBinder iBinder) throws RemoteException;

    void finishVoiceTask(IVoiceInteractionSession iVoiceInteractionSession) throws RemoteException;

    IActivityClientController getActivityClientController() throws RemoteException;

    List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() throws RemoteException;

    List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfosOnDisplay(int i) throws RemoteException;

    Point getAppTaskThumbnailSize() throws RemoteException;

    List<IBinder> getAppTasks(String str) throws RemoteException;

    Bundle getAssistContextExtras(int i) throws RemoteException;

    ConfigurationInfo getDeviceConfigurationInfo() throws RemoteException;

    ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException;

    int getFrontActivityScreenCompatMode() throws RemoteException;

    int getLastResumedActivityUserId() throws RemoteException;

    int getLockTaskModeState() throws RemoteException;

    boolean getPackageAskScreenCompat(String str) throws RemoteException;

    int getPackageScreenCompatMode(String str) throws RemoteException;

    ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int i, int i2, int i3) throws RemoteException;

    ActivityTaskManager.RootTaskInfo getRootTaskInfo(int i, int i2) throws RemoteException;

    ActivityTaskManager.RootTaskInfo getRootTaskInfoOnDisplay(int i, int i2, int i3) throws RemoteException;

    Rect getTaskBounds(int i) throws RemoteException;

    ActivityManager.TaskDescription getTaskDescription(int i) throws RemoteException;

    Bitmap getTaskDescriptionIcon(String str, int i) throws RemoteException;

    TaskSnapshot getTaskSnapshot(int i, boolean z, boolean z2) throws RemoteException;

    List<ActivityManager.RunningTaskInfo> getTasks(int i, boolean z, boolean z2, int i2) throws RemoteException;

    String getVoiceInteractorPackageName(IBinder iBinder) throws RemoteException;

    IWindowOrganizerController getWindowOrganizerController() throws RemoteException;

    boolean isActivityStartAllowedOnDisplay(int i, Intent intent, String str, int i2) throws RemoteException;

    boolean isAssistDataAllowedOnCurrentActivity() throws RemoteException;

    boolean isInLockTaskMode() throws RemoteException;

    boolean isTopActivityImmersive() throws RemoteException;

    void keyguardGoingAway(int i) throws RemoteException;

    void moveRootTaskToDisplay(int i, int i2) throws RemoteException;

    void moveTaskToFront(IApplicationThread iApplicationThread, String str, int i, int i2, Bundle bundle) throws RemoteException;

    void moveTaskToRootTask(int i, int i2, boolean z) throws RemoteException;

    void onPictureInPictureStateChanged(PictureInPictureUiState pictureInPictureUiState) throws RemoteException;

    void onSplashScreenViewCopyFinished(int i, SplashScreenView.SplashScreenViewParcelable splashScreenViewParcelable) throws RemoteException;

    void registerRemoteAnimationForNextActivityStart(String str, RemoteAnimationAdapter remoteAnimationAdapter, IBinder iBinder) throws RemoteException;

    void registerRemoteAnimationsForDisplay(int i, RemoteAnimationDefinition remoteAnimationDefinition) throws RemoteException;

    void registerScreenCaptureObserver(IBinder iBinder, IScreenCaptureObserver iScreenCaptureObserver) throws RemoteException;

    void registerTaskStackListener(ITaskStackListener iTaskStackListener) throws RemoteException;

    void releaseSomeActivities(IApplicationThread iApplicationThread) throws RemoteException;

    void removeAllVisibleRecentTasks() throws RemoteException;

    void removeRootTasksInWindowingModes(int[] iArr) throws RemoteException;

    void removeRootTasksWithActivityTypes(int[] iArr) throws RemoteException;

    boolean removeTask(int i) throws RemoteException;

    void reportAssistContextExtras(IBinder iBinder, Bundle bundle, AssistStructure assistStructure, AssistContent assistContent, Uri uri) throws RemoteException;

    boolean requestAssistContextExtras(int i, IAssistDataReceiver iAssistDataReceiver, Bundle bundle, IBinder iBinder, boolean z, boolean z2) throws RemoteException;

    boolean requestAssistDataForTask(IAssistDataReceiver iAssistDataReceiver, int i, String str, String str2) throws RemoteException;

    boolean requestAutofillData(IAssistDataReceiver iAssistDataReceiver, Bundle bundle, IBinder iBinder, int i) throws RemoteException;

    void resizeTask(int i, Rect rect, int i2) throws RemoteException;

    void resumeAppSwitches() throws RemoteException;

    void setActivityController(IActivityController iActivityController, boolean z) throws RemoteException;

    void setFocusedRootTask(int i) throws RemoteException;

    void setFocusedTask(int i) throws RemoteException;

    void setFrontActivityScreenCompatMode(int i) throws RemoteException;

    void setLockScreenShown(boolean z, boolean z2) throws RemoteException;

    void setPackageAskScreenCompat(String str, boolean z) throws RemoteException;

    void setPackageScreenCompatMode(String str, int i) throws RemoteException;

    void setPersistentVrThread(int i) throws RemoteException;

    void setRunningRemoteTransitionDelegate(IApplicationThread iApplicationThread) throws RemoteException;

    void setTaskResizeable(int i, int i2) throws RemoteException;

    void setVoiceKeepAwake(IVoiceInteractionSession iVoiceInteractionSession, boolean z) throws RemoteException;

    void setVrThread(int i) throws RemoteException;

    int startActivities(IApplicationThread iApplicationThread, String str, String str2, Intent[] intentArr, String[] strArr, IBinder iBinder, Bundle bundle, int i) throws RemoteException;

    int startActivity(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle) throws RemoteException;

    WaitResult startActivityAndWait(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3) throws RemoteException;

    int startActivityAsCaller(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, boolean z, int i3) throws RemoteException;

    int startActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3) throws RemoteException;

    int startActivityFromGameSession(IApplicationThread iApplicationThread, String str, String str2, int i, int i2, Intent intent, int i3, int i4) throws RemoteException;

    int startActivityFromRecents(int i, Bundle bundle) throws RemoteException;

    int startActivityIntentSender(IApplicationThread iApplicationThread, IIntentSender iIntentSender, IBinder iBinder, Intent intent, String str, IBinder iBinder2, String str2, int i, int i2, int i3, Bundle bundle) throws RemoteException;

    int startActivityWithConfig(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, Configuration configuration, Bundle bundle, int i3) throws RemoteException;

    int startAssistantActivity(String str, String str2, int i, int i2, Intent intent, String str3, Bundle bundle, int i3) throws RemoteException;

    BackNavigationInfo startBackNavigation(RemoteCallback remoteCallback, BackAnimationAdapter backAnimationAdapter) throws RemoteException;

    boolean startDreamActivity(Intent intent) throws RemoteException;

    boolean startNextMatchingActivity(IBinder iBinder, Intent intent, Bundle bundle) throws RemoteException;

    void startRecentsActivity(Intent intent, long j, IRecentsAnimationRunner iRecentsAnimationRunner) throws RemoteException;

    void startSystemLockTaskMode(int i) throws RemoteException;

    int startVoiceActivity(String str, String str2, int i, int i2, Intent intent, String str3, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor, int i3, ProfilerInfo profilerInfo, Bundle bundle, int i4) throws RemoteException;

    void stopAppSwitches() throws RemoteException;

    void stopSystemLockTaskMode() throws RemoteException;

    boolean supportsLocalVoiceInteraction() throws RemoteException;

    void suppressResizeConfigChanges(boolean z) throws RemoteException;

    TaskSnapshot takeTaskSnapshot(int i) throws RemoteException;

    void unhandledBack() throws RemoteException;

    void unregisterScreenCaptureObserver(IBinder iBinder, IScreenCaptureObserver iScreenCaptureObserver) throws RemoteException;

    void unregisterTaskStackListener(ITaskStackListener iTaskStackListener) throws RemoteException;

    boolean updateConfiguration(Configuration configuration) throws RemoteException;

    void updateLockTaskFeatures(int i, int i2) throws RemoteException;

    void updateLockTaskPackages(int i, String[] strArr) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IActivityTaskManager {
        @Override // android.app.IActivityTaskManager
        public int startActivity(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public int startActivities(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent[] intents, String[] resolvedTypes, IBinder resultTo, Bundle options, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public int startActivityAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public boolean startNextMatchingActivity(IBinder callingActivity, Intent intent, Bundle options) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public boolean startDreamActivity(Intent intent) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public int startActivityIntentSender(IApplicationThread caller, IIntentSender target, IBinder whitelistToken, Intent fillInIntent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flagsMask, int flagsValues, Bundle options) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public WaitResult startActivityAndWait(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public int startActivityWithConfig(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, Configuration newConfig, Bundle options, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public int startVoiceActivity(String callingPackage, String callingFeatureId, int callingPid, int callingUid, Intent intent, String resolvedType, IVoiceInteractionSession session, IVoiceInteractor interactor, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public String getVoiceInteractorPackageName(IBinder callingVoiceInteractor) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public int startAssistantActivity(String callingPackage, String callingFeatureId, int callingPid, int callingUid, Intent intent, String resolvedType, Bundle options, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public int startActivityFromGameSession(IApplicationThread caller, String callingPackage, String callingFeatureId, int callingPid, int callingUid, Intent intent, int taskId, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public void startRecentsActivity(Intent intent, long eventTime, IRecentsAnimationRunner recentsAnimationRunner) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public int startActivityFromRecents(int taskId, Bundle options) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public int startActivityAsCaller(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, boolean ignoreTargetSecurity, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public boolean isActivityStartAllowedOnDisplay(int displayId, Intent intent, String resolvedType, int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public void unhandledBack() throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public IActivityClientController getActivityClientController() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public int getFrontActivityScreenCompatMode() throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public void setFrontActivityScreenCompatMode(int mode) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void setFocusedTask(int taskId) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public boolean removeTask(int taskId) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public void removeAllVisibleRecentTasks() throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum, boolean filterOnlyVisibleRecents, boolean keepIntentExtra, int displayId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public void moveTaskToFront(IApplicationThread app, String callingPackage, int task, int flags, Bundle options) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int maxNum, int flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public boolean isTopActivityImmersive() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public ActivityManager.TaskDescription getTaskDescription(int taskId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public void reportAssistContextExtras(IBinder assistToken, Bundle extras, AssistStructure structure, AssistContent content, Uri referrer) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void setFocusedRootTask(int taskId) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public Rect getTaskBounds(int taskId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public void cancelRecentsAnimation(boolean restoreHomeRootTaskPosition) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void updateLockTaskPackages(int userId, String[] packages) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public boolean isInLockTaskMode() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public int getLockTaskModeState() throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public List<IBinder> getAppTasks(String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public void startSystemLockTaskMode(int taskId) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void stopSystemLockTaskMode() throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void finishVoiceTask(IVoiceInteractionSession session) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public int addAppTask(IBinder activityToken, Intent intent, ActivityManager.TaskDescription description, Bitmap thumbnail) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public Point getAppTaskThumbnailSize() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public void releaseSomeActivities(IApplicationThread app) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public Bitmap getTaskDescriptionIcon(String filename, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public void registerTaskStackListener(ITaskStackListener listener) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void unregisterTaskStackListener(ITaskStackListener listener) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void setTaskResizeable(int taskId, int resizeableMode) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void resizeTask(int taskId, Rect bounds, int resizeMode) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void moveRootTaskToDisplay(int taskId, int displayId) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void moveTaskToRootTask(int taskId, int rootTaskId, boolean toTop) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void removeRootTasksInWindowingModes(int[] windowingModes) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void removeRootTasksWithActivityTypes(int[] activityTypes) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public ActivityTaskManager.RootTaskInfo getRootTaskInfo(int windowingMode, int activityType) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfosOnDisplay(int displayId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public ActivityTaskManager.RootTaskInfo getRootTaskInfoOnDisplay(int windowingMode, int activityType, int displayId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public void setLockScreenShown(boolean showingKeyguard, boolean showingAod) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public Bundle getAssistContextExtras(int requestType) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public boolean requestAssistContextExtras(int requestType, IAssistDataReceiver receiver, Bundle receiverExtras, IBinder activityToken, boolean focused, boolean newSessionId) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public boolean requestAutofillData(IAssistDataReceiver receiver, Bundle receiverExtras, IBinder activityToken, int flags) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public boolean isAssistDataAllowedOnCurrentActivity() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public boolean requestAssistDataForTask(IAssistDataReceiver receiver, int taskId, String callingPackageName, String callingAttributionTag) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public void keyguardGoingAway(int flags) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void suppressResizeConfigChanges(boolean suppress) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public IWindowOrganizerController getWindowOrganizerController() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public boolean supportsLocalVoiceInteraction() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public ConfigurationInfo getDeviceConfigurationInfo() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public void cancelTaskWindowTransition(int taskId) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public TaskSnapshot getTaskSnapshot(int taskId, boolean isLowResolution, boolean takeSnapshotIfNeeded) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public TaskSnapshot takeTaskSnapshot(int taskId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public int getLastResumedActivityUserId() throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public boolean updateConfiguration(Configuration values) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public void updateLockTaskFeatures(int userId, int flags) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void registerRemoteAnimationForNextActivityStart(String packageName, RemoteAnimationAdapter adapter, IBinder launchCookie) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void registerRemoteAnimationsForDisplay(int displayId, RemoteAnimationDefinition definition) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void alwaysShowUnsupportedCompileSdkWarning(ComponentName activity) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void setVrThread(int tid) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void setPersistentVrThread(int tid) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void stopAppSwitches() throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void resumeAppSwitches() throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void setActivityController(IActivityController watcher, boolean imAMonkey) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void setVoiceKeepAwake(IVoiceInteractionSession session, boolean keepAwake) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public int getPackageScreenCompatMode(String packageName) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityTaskManager
        public void setPackageScreenCompatMode(String packageName, int mode) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public boolean getPackageAskScreenCompat(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityTaskManager
        public void setPackageAskScreenCompat(String packageName, boolean ask) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void clearLaunchParamsForPackages(List<String> packageNames) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void onSplashScreenViewCopyFinished(int taskId, SplashScreenView.SplashScreenViewParcelable material) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void onPictureInPictureStateChanged(PictureInPictureUiState pipState) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void detachNavigationBarFromApp(IBinder transition) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void setRunningRemoteTransitionDelegate(IApplicationThread caller) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public BackNavigationInfo startBackNavigation(RemoteCallback navigationObserver, BackAnimationAdapter adaptor) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityTaskManager
        public void registerScreenCaptureObserver(IBinder activityToken, IScreenCaptureObserver observer) throws RemoteException {
        }

        @Override // android.app.IActivityTaskManager
        public void unregisterScreenCaptureObserver(IBinder activityToken, IScreenCaptureObserver observer) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IActivityTaskManager {
        static final int TRANSACTION_addAppTask = 41;
        static final int TRANSACTION_alwaysShowUnsupportedCompileSdkWarning = 76;
        static final int TRANSACTION_cancelRecentsAnimation = 33;
        static final int TRANSACTION_cancelTaskWindowTransition = 68;
        static final int TRANSACTION_clearLaunchParamsForPackages = 87;
        static final int TRANSACTION_detachNavigationBarFromApp = 90;
        static final int TRANSACTION_finishVoiceTask = 40;
        static final int TRANSACTION_getActivityClientController = 18;
        static final int TRANSACTION_getAllRootTaskInfos = 53;
        static final int TRANSACTION_getAllRootTaskInfosOnDisplay = 55;
        static final int TRANSACTION_getAppTaskThumbnailSize = 42;
        static final int TRANSACTION_getAppTasks = 37;
        static final int TRANSACTION_getAssistContextExtras = 58;
        static final int TRANSACTION_getDeviceConfigurationInfo = 67;
        static final int TRANSACTION_getFocusedRootTaskInfo = 31;
        static final int TRANSACTION_getFrontActivityScreenCompatMode = 19;
        static final int TRANSACTION_getLastResumedActivityUserId = 71;
        static final int TRANSACTION_getLockTaskModeState = 36;
        static final int TRANSACTION_getPackageAskScreenCompat = 85;
        static final int TRANSACTION_getPackageScreenCompatMode = 83;
        static final int TRANSACTION_getRecentTasks = 26;
        static final int TRANSACTION_getRootTaskInfo = 54;
        static final int TRANSACTION_getRootTaskInfoOnDisplay = 56;
        static final int TRANSACTION_getTaskBounds = 32;
        static final int TRANSACTION_getTaskDescription = 28;
        static final int TRANSACTION_getTaskDescriptionIcon = 44;
        static final int TRANSACTION_getTaskSnapshot = 69;
        static final int TRANSACTION_getTasks = 24;
        static final int TRANSACTION_getVoiceInteractorPackageName = 10;
        static final int TRANSACTION_getWindowOrganizerController = 65;
        static final int TRANSACTION_isActivityStartAllowedOnDisplay = 16;
        static final int TRANSACTION_isAssistDataAllowedOnCurrentActivity = 61;
        static final int TRANSACTION_isInLockTaskMode = 35;
        static final int TRANSACTION_isTopActivityImmersive = 27;
        static final int TRANSACTION_keyguardGoingAway = 63;
        static final int TRANSACTION_moveRootTaskToDisplay = 49;
        static final int TRANSACTION_moveTaskToFront = 25;
        static final int TRANSACTION_moveTaskToRootTask = 50;
        static final int TRANSACTION_onPictureInPictureStateChanged = 89;
        static final int TRANSACTION_onSplashScreenViewCopyFinished = 88;
        static final int TRANSACTION_registerRemoteAnimationForNextActivityStart = 74;
        static final int TRANSACTION_registerRemoteAnimationsForDisplay = 75;
        static final int TRANSACTION_registerScreenCaptureObserver = 93;
        static final int TRANSACTION_registerTaskStackListener = 45;
        static final int TRANSACTION_releaseSomeActivities = 43;
        static final int TRANSACTION_removeAllVisibleRecentTasks = 23;
        static final int TRANSACTION_removeRootTasksInWindowingModes = 51;
        static final int TRANSACTION_removeRootTasksWithActivityTypes = 52;
        static final int TRANSACTION_removeTask = 22;
        static final int TRANSACTION_reportAssistContextExtras = 29;
        static final int TRANSACTION_requestAssistContextExtras = 59;
        static final int TRANSACTION_requestAssistDataForTask = 62;
        static final int TRANSACTION_requestAutofillData = 60;
        static final int TRANSACTION_resizeTask = 48;
        static final int TRANSACTION_resumeAppSwitches = 80;
        static final int TRANSACTION_setActivityController = 81;
        static final int TRANSACTION_setFocusedRootTask = 30;
        static final int TRANSACTION_setFocusedTask = 21;
        static final int TRANSACTION_setFrontActivityScreenCompatMode = 20;
        static final int TRANSACTION_setLockScreenShown = 57;
        static final int TRANSACTION_setPackageAskScreenCompat = 86;
        static final int TRANSACTION_setPackageScreenCompatMode = 84;
        static final int TRANSACTION_setPersistentVrThread = 78;
        static final int TRANSACTION_setRunningRemoteTransitionDelegate = 91;
        static final int TRANSACTION_setTaskResizeable = 47;
        static final int TRANSACTION_setVoiceKeepAwake = 82;
        static final int TRANSACTION_setVrThread = 77;
        static final int TRANSACTION_startActivities = 2;
        static final int TRANSACTION_startActivity = 1;
        static final int TRANSACTION_startActivityAndWait = 7;
        static final int TRANSACTION_startActivityAsCaller = 15;
        static final int TRANSACTION_startActivityAsUser = 3;
        static final int TRANSACTION_startActivityFromGameSession = 12;
        static final int TRANSACTION_startActivityFromRecents = 14;
        static final int TRANSACTION_startActivityIntentSender = 6;
        static final int TRANSACTION_startActivityWithConfig = 8;
        static final int TRANSACTION_startAssistantActivity = 11;
        static final int TRANSACTION_startBackNavigation = 92;
        static final int TRANSACTION_startDreamActivity = 5;
        static final int TRANSACTION_startNextMatchingActivity = 4;
        static final int TRANSACTION_startRecentsActivity = 13;
        static final int TRANSACTION_startSystemLockTaskMode = 38;
        static final int TRANSACTION_startVoiceActivity = 9;
        static final int TRANSACTION_stopAppSwitches = 79;
        static final int TRANSACTION_stopSystemLockTaskMode = 39;
        static final int TRANSACTION_supportsLocalVoiceInteraction = 66;
        static final int TRANSACTION_suppressResizeConfigChanges = 64;
        static final int TRANSACTION_takeTaskSnapshot = 70;
        static final int TRANSACTION_unhandledBack = 17;
        static final int TRANSACTION_unregisterScreenCaptureObserver = 94;
        static final int TRANSACTION_unregisterTaskStackListener = 46;
        static final int TRANSACTION_updateConfiguration = 72;
        static final int TRANSACTION_updateLockTaskFeatures = 73;
        static final int TRANSACTION_updateLockTaskPackages = 34;

        public Stub() {
            attachInterface(this, IActivityTaskManager.DESCRIPTOR);
        }

        public static IActivityTaskManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IActivityTaskManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IActivityTaskManager)) {
                return (IActivityTaskManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "startActivity";
                case 2:
                    return "startActivities";
                case 3:
                    return "startActivityAsUser";
                case 4:
                    return "startNextMatchingActivity";
                case 5:
                    return "startDreamActivity";
                case 6:
                    return "startActivityIntentSender";
                case 7:
                    return "startActivityAndWait";
                case 8:
                    return "startActivityWithConfig";
                case 9:
                    return "startVoiceActivity";
                case 10:
                    return "getVoiceInteractorPackageName";
                case 11:
                    return "startAssistantActivity";
                case 12:
                    return "startActivityFromGameSession";
                case 13:
                    return "startRecentsActivity";
                case 14:
                    return "startActivityFromRecents";
                case 15:
                    return "startActivityAsCaller";
                case 16:
                    return "isActivityStartAllowedOnDisplay";
                case 17:
                    return "unhandledBack";
                case 18:
                    return "getActivityClientController";
                case 19:
                    return "getFrontActivityScreenCompatMode";
                case 20:
                    return "setFrontActivityScreenCompatMode";
                case 21:
                    return "setFocusedTask";
                case 22:
                    return "removeTask";
                case 23:
                    return "removeAllVisibleRecentTasks";
                case 24:
                    return "getTasks";
                case 25:
                    return "moveTaskToFront";
                case 26:
                    return "getRecentTasks";
                case 27:
                    return "isTopActivityImmersive";
                case 28:
                    return "getTaskDescription";
                case 29:
                    return "reportAssistContextExtras";
                case 30:
                    return "setFocusedRootTask";
                case 31:
                    return "getFocusedRootTaskInfo";
                case 32:
                    return "getTaskBounds";
                case 33:
                    return "cancelRecentsAnimation";
                case 34:
                    return "updateLockTaskPackages";
                case 35:
                    return "isInLockTaskMode";
                case 36:
                    return "getLockTaskModeState";
                case 37:
                    return "getAppTasks";
                case 38:
                    return "startSystemLockTaskMode";
                case 39:
                    return "stopSystemLockTaskMode";
                case 40:
                    return "finishVoiceTask";
                case 41:
                    return "addAppTask";
                case 42:
                    return "getAppTaskThumbnailSize";
                case 43:
                    return "releaseSomeActivities";
                case 44:
                    return "getTaskDescriptionIcon";
                case 45:
                    return "registerTaskStackListener";
                case 46:
                    return "unregisterTaskStackListener";
                case 47:
                    return "setTaskResizeable";
                case 48:
                    return "resizeTask";
                case 49:
                    return "moveRootTaskToDisplay";
                case 50:
                    return "moveTaskToRootTask";
                case 51:
                    return "removeRootTasksInWindowingModes";
                case 52:
                    return "removeRootTasksWithActivityTypes";
                case 53:
                    return "getAllRootTaskInfos";
                case 54:
                    return "getRootTaskInfo";
                case 55:
                    return "getAllRootTaskInfosOnDisplay";
                case 56:
                    return "getRootTaskInfoOnDisplay";
                case 57:
                    return "setLockScreenShown";
                case 58:
                    return "getAssistContextExtras";
                case 59:
                    return "requestAssistContextExtras";
                case 60:
                    return "requestAutofillData";
                case 61:
                    return "isAssistDataAllowedOnCurrentActivity";
                case 62:
                    return "requestAssistDataForTask";
                case 63:
                    return "keyguardGoingAway";
                case 64:
                    return "suppressResizeConfigChanges";
                case 65:
                    return "getWindowOrganizerController";
                case 66:
                    return "supportsLocalVoiceInteraction";
                case 67:
                    return "getDeviceConfigurationInfo";
                case 68:
                    return "cancelTaskWindowTransition";
                case 69:
                    return "getTaskSnapshot";
                case 70:
                    return "takeTaskSnapshot";
                case 71:
                    return "getLastResumedActivityUserId";
                case 72:
                    return "updateConfiguration";
                case 73:
                    return "updateLockTaskFeatures";
                case 74:
                    return "registerRemoteAnimationForNextActivityStart";
                case 75:
                    return "registerRemoteAnimationsForDisplay";
                case 76:
                    return "alwaysShowUnsupportedCompileSdkWarning";
                case 77:
                    return "setVrThread";
                case 78:
                    return "setPersistentVrThread";
                case 79:
                    return "stopAppSwitches";
                case 80:
                    return "resumeAppSwitches";
                case 81:
                    return "setActivityController";
                case 82:
                    return "setVoiceKeepAwake";
                case 83:
                    return "getPackageScreenCompatMode";
                case 84:
                    return "setPackageScreenCompatMode";
                case 85:
                    return "getPackageAskScreenCompat";
                case 86:
                    return "setPackageAskScreenCompat";
                case 87:
                    return "clearLaunchParamsForPackages";
                case 88:
                    return "onSplashScreenViewCopyFinished";
                case 89:
                    return "onPictureInPictureStateChanged";
                case 90:
                    return "detachNavigationBarFromApp";
                case 91:
                    return "setRunningRemoteTransitionDelegate";
                case 92:
                    return "startBackNavigation";
                case 93:
                    return "registerScreenCaptureObserver";
                case 94:
                    return "unregisterScreenCaptureObserver";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IActivityTaskManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IActivityTaskManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IApplicationThread _arg0 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            Intent _arg3 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg4 = data.readString();
                            IBinder _arg5 = data.readStrongBinder();
                            String _arg6 = data.readString();
                            int _arg7 = data.readInt();
                            int _arg8 = data.readInt();
                            ProfilerInfo _arg9 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            Bundle _arg10 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result = startActivity(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9, _arg10);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            return true;
                        case 2:
                            IApplicationThread _arg02 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            Intent[] _arg32 = (Intent[]) data.createTypedArray(Intent.CREATOR);
                            String[] _arg42 = data.createStringArray();
                            IBinder _arg52 = data.readStrongBinder();
                            Bundle _arg62 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg72 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result2 = startActivities(_arg02, _arg12, _arg22, _arg32, _arg42, _arg52, _arg62, _arg72);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            return true;
                        case 3:
                            IApplicationThread _arg03 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg13 = data.readString();
                            String _arg23 = data.readString();
                            Intent _arg33 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg43 = data.readString();
                            IBinder _arg53 = data.readStrongBinder();
                            String _arg63 = data.readString();
                            int _arg73 = data.readInt();
                            int _arg82 = data.readInt();
                            ProfilerInfo _arg92 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            Bundle _arg102 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg11 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result3 = startActivityAsUser(_arg03, _arg13, _arg23, _arg33, _arg43, _arg53, _arg63, _arg73, _arg82, _arg92, _arg102, _arg11);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            return true;
                        case 4:
                            IBinder _arg04 = data.readStrongBinder();
                            Intent _arg14 = (Intent) data.readTypedObject(Intent.CREATOR);
                            Bundle _arg24 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result4 = startNextMatchingActivity(_arg04, _arg14, _arg24);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            return true;
                        case 5:
                            Intent _arg05 = (Intent) data.readTypedObject(Intent.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result5 = startDreamActivity(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            return true;
                        case 6:
                            IApplicationThread _arg06 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            IIntentSender _arg15 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg25 = data.readStrongBinder();
                            Intent _arg34 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg44 = data.readString();
                            IBinder _arg54 = data.readStrongBinder();
                            String _arg64 = data.readString();
                            int _arg74 = data.readInt();
                            int _arg83 = data.readInt();
                            int _arg93 = data.readInt();
                            Bundle _arg103 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result6 = startActivityIntentSender(_arg06, _arg15, _arg25, _arg34, _arg44, _arg54, _arg64, _arg74, _arg83, _arg93, _arg103);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            return true;
                        case 7:
                            IApplicationThread _arg07 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg16 = data.readString();
                            String _arg26 = data.readString();
                            Intent _arg35 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg45 = data.readString();
                            IBinder _arg55 = data.readStrongBinder();
                            String _arg65 = data.readString();
                            int _arg75 = data.readInt();
                            int _arg84 = data.readInt();
                            ProfilerInfo _arg94 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            Bundle _arg104 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            WaitResult _result7 = startActivityAndWait(_arg07, _arg16, _arg26, _arg35, _arg45, _arg55, _arg65, _arg75, _arg84, _arg94, _arg104, _arg112);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            return true;
                        case 8:
                            IApplicationThread _arg08 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg17 = data.readString();
                            String _arg27 = data.readString();
                            Intent _arg36 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg46 = data.readString();
                            IBinder _arg56 = data.readStrongBinder();
                            String _arg66 = data.readString();
                            int _arg76 = data.readInt();
                            int _arg85 = data.readInt();
                            Configuration _arg95 = (Configuration) data.readTypedObject(Configuration.CREATOR);
                            Bundle _arg105 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg113 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result8 = startActivityWithConfig(_arg08, _arg17, _arg27, _arg36, _arg46, _arg56, _arg66, _arg76, _arg85, _arg95, _arg105, _arg113);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            return true;
                        case 9:
                            String _arg09 = data.readString();
                            String _arg18 = data.readString();
                            int _arg28 = data.readInt();
                            int _arg37 = data.readInt();
                            Intent _arg47 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg57 = data.readString();
                            IVoiceInteractionSession _arg67 = IVoiceInteractionSession.Stub.asInterface(data.readStrongBinder());
                            IVoiceInteractor _arg77 = IVoiceInteractor.Stub.asInterface(data.readStrongBinder());
                            int _arg86 = data.readInt();
                            ProfilerInfo _arg96 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            Bundle _arg106 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result9 = startVoiceActivity(_arg09, _arg18, _arg28, _arg37, _arg47, _arg57, _arg67, _arg77, _arg86, _arg96, _arg106, _arg114);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            return true;
                        case 10:
                            IBinder _arg010 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            String _result10 = getVoiceInteractorPackageName(_arg010);
                            reply.writeNoException();
                            reply.writeString(_result10);
                            return true;
                        case 11:
                            String _arg011 = data.readString();
                            String _arg19 = data.readString();
                            int _arg29 = data.readInt();
                            int _arg38 = data.readInt();
                            Intent _arg48 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg58 = data.readString();
                            Bundle _arg68 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg78 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result11 = startAssistantActivity(_arg011, _arg19, _arg29, _arg38, _arg48, _arg58, _arg68, _arg78);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            return true;
                        case 12:
                            IApplicationThread _arg012 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg110 = data.readString();
                            String _arg210 = data.readString();
                            int _arg39 = data.readInt();
                            int _arg49 = data.readInt();
                            Intent _arg59 = (Intent) data.readTypedObject(Intent.CREATOR);
                            int _arg69 = data.readInt();
                            int _arg79 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result12 = startActivityFromGameSession(_arg012, _arg110, _arg210, _arg39, _arg49, _arg59, _arg69, _arg79);
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            return true;
                        case 13:
                            Intent _arg013 = (Intent) data.readTypedObject(Intent.CREATOR);
                            long _arg111 = data.readLong();
                            IRecentsAnimationRunner _arg211 = IRecentsAnimationRunner.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startRecentsActivity(_arg013, _arg111, _arg211);
                            reply.writeNoException();
                            return true;
                        case 14:
                            int _arg014 = data.readInt();
                            Bundle _arg115 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result13 = startActivityFromRecents(_arg014, _arg115);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            return true;
                        case 15:
                            IApplicationThread _arg015 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg116 = data.readString();
                            Intent _arg212 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg310 = data.readString();
                            IBinder _arg410 = data.readStrongBinder();
                            String _arg510 = data.readString();
                            int _arg610 = data.readInt();
                            int _arg710 = data.readInt();
                            ProfilerInfo _arg87 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            Bundle _arg97 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            boolean _arg107 = data.readBoolean();
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result14 = startActivityAsCaller(_arg015, _arg116, _arg212, _arg310, _arg410, _arg510, _arg610, _arg710, _arg87, _arg97, _arg107, _arg117);
                            reply.writeNoException();
                            reply.writeInt(_result14);
                            return true;
                        case 16:
                            int _arg016 = data.readInt();
                            Intent _arg118 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg213 = data.readString();
                            int _arg311 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result15 = isActivityStartAllowedOnDisplay(_arg016, _arg118, _arg213, _arg311);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            return true;
                        case 17:
                            unhandledBack();
                            reply.writeNoException();
                            return true;
                        case 18:
                            IActivityClientController _result16 = getActivityClientController();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result16);
                            return true;
                        case 19:
                            int _result17 = getFrontActivityScreenCompatMode();
                            reply.writeNoException();
                            reply.writeInt(_result17);
                            return true;
                        case 20:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            setFrontActivityScreenCompatMode(_arg017);
                            reply.writeNoException();
                            return true;
                        case 21:
                            int _arg018 = data.readInt();
                            data.enforceNoDataAvail();
                            setFocusedTask(_arg018);
                            reply.writeNoException();
                            return true;
                        case 22:
                            int _arg019 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result18 = removeTask(_arg019);
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            return true;
                        case 23:
                            removeAllVisibleRecentTasks();
                            reply.writeNoException();
                            return true;
                        case 24:
                            int _arg020 = data.readInt();
                            boolean _arg119 = data.readBoolean();
                            boolean _arg214 = data.readBoolean();
                            int _arg312 = data.readInt();
                            data.enforceNoDataAvail();
                            List<ActivityManager.RunningTaskInfo> _result19 = getTasks(_arg020, _arg119, _arg214, _arg312);
                            reply.writeNoException();
                            reply.writeTypedList(_result19, 1);
                            return true;
                        case 25:
                            IApplicationThread _arg021 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg120 = data.readString();
                            int _arg215 = data.readInt();
                            int _arg313 = data.readInt();
                            Bundle _arg411 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            moveTaskToFront(_arg021, _arg120, _arg215, _arg313, _arg411);
                            reply.writeNoException();
                            return true;
                        case 26:
                            int _arg022 = data.readInt();
                            int _arg121 = data.readInt();
                            int _arg216 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice<ActivityManager.RecentTaskInfo> _result20 = getRecentTasks(_arg022, _arg121, _arg216);
                            reply.writeNoException();
                            reply.writeTypedObject(_result20, 1);
                            return true;
                        case 27:
                            boolean _result21 = isTopActivityImmersive();
                            reply.writeNoException();
                            reply.writeBoolean(_result21);
                            return true;
                        case 28:
                            int _arg023 = data.readInt();
                            data.enforceNoDataAvail();
                            ActivityManager.TaskDescription _result22 = getTaskDescription(_arg023);
                            reply.writeNoException();
                            reply.writeTypedObject(_result22, 1);
                            return true;
                        case 29:
                            IBinder _arg024 = data.readStrongBinder();
                            Bundle _arg122 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            AssistStructure _arg217 = (AssistStructure) data.readTypedObject(AssistStructure.CREATOR);
                            AssistContent _arg314 = (AssistContent) data.readTypedObject(AssistContent.CREATOR);
                            Uri _arg412 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            reportAssistContextExtras(_arg024, _arg122, _arg217, _arg314, _arg412);
                            reply.writeNoException();
                            return true;
                        case 30:
                            int _arg025 = data.readInt();
                            data.enforceNoDataAvail();
                            setFocusedRootTask(_arg025);
                            reply.writeNoException();
                            return true;
                        case 31:
                            ActivityTaskManager.RootTaskInfo _result23 = getFocusedRootTaskInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result23, 1);
                            return true;
                        case 32:
                            int _arg026 = data.readInt();
                            data.enforceNoDataAvail();
                            Rect _result24 = getTaskBounds(_arg026);
                            reply.writeNoException();
                            reply.writeTypedObject(_result24, 1);
                            return true;
                        case 33:
                            boolean _arg027 = data.readBoolean();
                            data.enforceNoDataAvail();
                            cancelRecentsAnimation(_arg027);
                            reply.writeNoException();
                            return true;
                        case 34:
                            int _arg028 = data.readInt();
                            String[] _arg123 = data.createStringArray();
                            data.enforceNoDataAvail();
                            updateLockTaskPackages(_arg028, _arg123);
                            reply.writeNoException();
                            return true;
                        case 35:
                            boolean _result25 = isInLockTaskMode();
                            reply.writeNoException();
                            reply.writeBoolean(_result25);
                            return true;
                        case 36:
                            int _result26 = getLockTaskModeState();
                            reply.writeNoException();
                            reply.writeInt(_result26);
                            return true;
                        case 37:
                            String _arg029 = data.readString();
                            data.enforceNoDataAvail();
                            List<IBinder> _result27 = getAppTasks(_arg029);
                            reply.writeNoException();
                            reply.writeBinderList(_result27);
                            return true;
                        case 38:
                            int _arg030 = data.readInt();
                            data.enforceNoDataAvail();
                            startSystemLockTaskMode(_arg030);
                            reply.writeNoException();
                            return true;
                        case 39:
                            stopSystemLockTaskMode();
                            reply.writeNoException();
                            return true;
                        case 40:
                            IBinder _arg031 = data.readStrongBinder();
                            IVoiceInteractionSession _arg032 = IVoiceInteractionSession.Stub.asInterface(_arg031);
                            data.enforceNoDataAvail();
                            finishVoiceTask(_arg032);
                            reply.writeNoException();
                            return true;
                        case 41:
                            IBinder _arg033 = data.readStrongBinder();
                            Intent _arg124 = (Intent) data.readTypedObject(Intent.CREATOR);
                            ActivityManager.TaskDescription _arg218 = (ActivityManager.TaskDescription) data.readTypedObject(ActivityManager.TaskDescription.CREATOR);
                            Bitmap _arg315 = (Bitmap) data.readTypedObject(Bitmap.CREATOR);
                            data.enforceNoDataAvail();
                            int _result28 = addAppTask(_arg033, _arg124, _arg218, _arg315);
                            reply.writeNoException();
                            reply.writeInt(_result28);
                            return true;
                        case 42:
                            Point _result29 = getAppTaskThumbnailSize();
                            reply.writeNoException();
                            reply.writeTypedObject(_result29, 1);
                            return true;
                        case 43:
                            IApplicationThread _arg034 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            releaseSomeActivities(_arg034);
                            return true;
                        case 44:
                            String _arg035 = data.readString();
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            Bitmap _result30 = getTaskDescriptionIcon(_arg035, _arg125);
                            reply.writeNoException();
                            reply.writeTypedObject(_result30, 1);
                            return true;
                        case 45:
                            ITaskStackListener _arg036 = ITaskStackListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerTaskStackListener(_arg036);
                            reply.writeNoException();
                            return true;
                        case 46:
                            ITaskStackListener _arg037 = ITaskStackListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterTaskStackListener(_arg037);
                            reply.writeNoException();
                            return true;
                        case 47:
                            int _arg038 = data.readInt();
                            int _arg126 = data.readInt();
                            data.enforceNoDataAvail();
                            setTaskResizeable(_arg038, _arg126);
                            reply.writeNoException();
                            return true;
                        case 48:
                            int _arg039 = data.readInt();
                            Rect _arg127 = (Rect) data.readTypedObject(Rect.CREATOR);
                            int _arg219 = data.readInt();
                            data.enforceNoDataAvail();
                            resizeTask(_arg039, _arg127, _arg219);
                            reply.writeNoException();
                            return true;
                        case 49:
                            int _arg040 = data.readInt();
                            int _arg128 = data.readInt();
                            data.enforceNoDataAvail();
                            moveRootTaskToDisplay(_arg040, _arg128);
                            reply.writeNoException();
                            return true;
                        case 50:
                            int _arg041 = data.readInt();
                            int _arg129 = data.readInt();
                            boolean _arg220 = data.readBoolean();
                            data.enforceNoDataAvail();
                            moveTaskToRootTask(_arg041, _arg129, _arg220);
                            reply.writeNoException();
                            return true;
                        case 51:
                            int[] _arg042 = data.createIntArray();
                            data.enforceNoDataAvail();
                            removeRootTasksInWindowingModes(_arg042);
                            reply.writeNoException();
                            return true;
                        case 52:
                            int[] _arg043 = data.createIntArray();
                            data.enforceNoDataAvail();
                            removeRootTasksWithActivityTypes(_arg043);
                            reply.writeNoException();
                            return true;
                        case 53:
                            List<ActivityTaskManager.RootTaskInfo> _result31 = getAllRootTaskInfos();
                            reply.writeNoException();
                            reply.writeTypedList(_result31, 1);
                            return true;
                        case 54:
                            int _arg044 = data.readInt();
                            int _arg130 = data.readInt();
                            data.enforceNoDataAvail();
                            ActivityTaskManager.RootTaskInfo _result32 = getRootTaskInfo(_arg044, _arg130);
                            reply.writeNoException();
                            reply.writeTypedObject(_result32, 1);
                            return true;
                        case 55:
                            int _arg045 = data.readInt();
                            data.enforceNoDataAvail();
                            List<ActivityTaskManager.RootTaskInfo> _result33 = getAllRootTaskInfosOnDisplay(_arg045);
                            reply.writeNoException();
                            reply.writeTypedList(_result33, 1);
                            return true;
                        case 56:
                            int _arg046 = data.readInt();
                            int _arg131 = data.readInt();
                            int _arg221 = data.readInt();
                            data.enforceNoDataAvail();
                            ActivityTaskManager.RootTaskInfo _result34 = getRootTaskInfoOnDisplay(_arg046, _arg131, _arg221);
                            reply.writeNoException();
                            reply.writeTypedObject(_result34, 1);
                            return true;
                        case 57:
                            boolean _arg047 = data.readBoolean();
                            boolean _arg132 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setLockScreenShown(_arg047, _arg132);
                            reply.writeNoException();
                            return true;
                        case 58:
                            int _arg048 = data.readInt();
                            data.enforceNoDataAvail();
                            Bundle _result35 = getAssistContextExtras(_arg048);
                            reply.writeNoException();
                            reply.writeTypedObject(_result35, 1);
                            return true;
                        case 59:
                            int _arg049 = data.readInt();
                            IAssistDataReceiver _arg133 = IAssistDataReceiver.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg222 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            IBinder _arg316 = data.readStrongBinder();
                            boolean _arg413 = data.readBoolean();
                            boolean _arg511 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result36 = requestAssistContextExtras(_arg049, _arg133, _arg222, _arg316, _arg413, _arg511);
                            reply.writeNoException();
                            reply.writeBoolean(_result36);
                            return true;
                        case 60:
                            IAssistDataReceiver _arg050 = IAssistDataReceiver.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg134 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            IBinder _arg223 = data.readStrongBinder();
                            int _arg317 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result37 = requestAutofillData(_arg050, _arg134, _arg223, _arg317);
                            reply.writeNoException();
                            reply.writeBoolean(_result37);
                            return true;
                        case 61:
                            boolean _result38 = isAssistDataAllowedOnCurrentActivity();
                            reply.writeNoException();
                            reply.writeBoolean(_result38);
                            return true;
                        case 62:
                            IAssistDataReceiver _arg051 = IAssistDataReceiver.Stub.asInterface(data.readStrongBinder());
                            int _arg135 = data.readInt();
                            String _arg224 = data.readString();
                            String _arg318 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result39 = requestAssistDataForTask(_arg051, _arg135, _arg224, _arg318);
                            reply.writeNoException();
                            reply.writeBoolean(_result39);
                            return true;
                        case 63:
                            int _arg052 = data.readInt();
                            data.enforceNoDataAvail();
                            keyguardGoingAway(_arg052);
                            reply.writeNoException();
                            return true;
                        case 64:
                            boolean _arg053 = data.readBoolean();
                            data.enforceNoDataAvail();
                            suppressResizeConfigChanges(_arg053);
                            reply.writeNoException();
                            return true;
                        case 65:
                            IWindowOrganizerController _result40 = getWindowOrganizerController();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result40);
                            return true;
                        case 66:
                            boolean _result41 = supportsLocalVoiceInteraction();
                            reply.writeNoException();
                            reply.writeBoolean(_result41);
                            return true;
                        case 67:
                            ConfigurationInfo _result42 = getDeviceConfigurationInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result42, 1);
                            return true;
                        case 68:
                            int _arg054 = data.readInt();
                            data.enforceNoDataAvail();
                            cancelTaskWindowTransition(_arg054);
                            reply.writeNoException();
                            return true;
                        case 69:
                            int _arg055 = data.readInt();
                            boolean _arg136 = data.readBoolean();
                            boolean _arg225 = data.readBoolean();
                            data.enforceNoDataAvail();
                            TaskSnapshot _result43 = getTaskSnapshot(_arg055, _arg136, _arg225);
                            reply.writeNoException();
                            reply.writeTypedObject(_result43, 1);
                            return true;
                        case 70:
                            int _arg056 = data.readInt();
                            data.enforceNoDataAvail();
                            TaskSnapshot _result44 = takeTaskSnapshot(_arg056);
                            reply.writeNoException();
                            reply.writeTypedObject(_result44, 1);
                            return true;
                        case 71:
                            int _result45 = getLastResumedActivityUserId();
                            reply.writeNoException();
                            reply.writeInt(_result45);
                            return true;
                        case 72:
                            Configuration _arg057 = (Configuration) data.readTypedObject(Configuration.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result46 = updateConfiguration(_arg057);
                            reply.writeNoException();
                            reply.writeBoolean(_result46);
                            return true;
                        case 73:
                            int _arg058 = data.readInt();
                            int _arg137 = data.readInt();
                            data.enforceNoDataAvail();
                            updateLockTaskFeatures(_arg058, _arg137);
                            reply.writeNoException();
                            return true;
                        case 74:
                            String _arg059 = data.readString();
                            RemoteAnimationAdapter _arg138 = (RemoteAnimationAdapter) data.readTypedObject(RemoteAnimationAdapter.CREATOR);
                            IBinder _arg226 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            registerRemoteAnimationForNextActivityStart(_arg059, _arg138, _arg226);
                            reply.writeNoException();
                            return true;
                        case 75:
                            int _arg060 = data.readInt();
                            RemoteAnimationDefinition _arg139 = (RemoteAnimationDefinition) data.readTypedObject(RemoteAnimationDefinition.CREATOR);
                            data.enforceNoDataAvail();
                            registerRemoteAnimationsForDisplay(_arg060, _arg139);
                            reply.writeNoException();
                            return true;
                        case 76:
                            ComponentName _arg061 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            alwaysShowUnsupportedCompileSdkWarning(_arg061);
                            reply.writeNoException();
                            return true;
                        case 77:
                            int _arg062 = data.readInt();
                            data.enforceNoDataAvail();
                            setVrThread(_arg062);
                            reply.writeNoException();
                            return true;
                        case 78:
                            int _arg063 = data.readInt();
                            data.enforceNoDataAvail();
                            setPersistentVrThread(_arg063);
                            reply.writeNoException();
                            return true;
                        case 79:
                            stopAppSwitches();
                            reply.writeNoException();
                            return true;
                        case 80:
                            resumeAppSwitches();
                            reply.writeNoException();
                            return true;
                        case 81:
                            IActivityController _arg064 = IActivityController.Stub.asInterface(data.readStrongBinder());
                            boolean _arg140 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setActivityController(_arg064, _arg140);
                            reply.writeNoException();
                            return true;
                        case 82:
                            IVoiceInteractionSession _arg065 = IVoiceInteractionSession.Stub.asInterface(data.readStrongBinder());
                            boolean _arg141 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setVoiceKeepAwake(_arg065, _arg141);
                            reply.writeNoException();
                            return true;
                        case 83:
                            String _arg066 = data.readString();
                            data.enforceNoDataAvail();
                            int _result47 = getPackageScreenCompatMode(_arg066);
                            reply.writeNoException();
                            reply.writeInt(_result47);
                            return true;
                        case 84:
                            String _arg067 = data.readString();
                            int _arg142 = data.readInt();
                            data.enforceNoDataAvail();
                            setPackageScreenCompatMode(_arg067, _arg142);
                            reply.writeNoException();
                            return true;
                        case 85:
                            String _arg068 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result48 = getPackageAskScreenCompat(_arg068);
                            reply.writeNoException();
                            reply.writeBoolean(_result48);
                            return true;
                        case 86:
                            String _arg069 = data.readString();
                            boolean _arg143 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPackageAskScreenCompat(_arg069, _arg143);
                            reply.writeNoException();
                            return true;
                        case 87:
                            List<String> _arg070 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            clearLaunchParamsForPackages(_arg070);
                            reply.writeNoException();
                            return true;
                        case 88:
                            int _arg071 = data.readInt();
                            SplashScreenView.SplashScreenViewParcelable _arg144 = (SplashScreenView.SplashScreenViewParcelable) data.readTypedObject(SplashScreenView.SplashScreenViewParcelable.CREATOR);
                            data.enforceNoDataAvail();
                            onSplashScreenViewCopyFinished(_arg071, _arg144);
                            reply.writeNoException();
                            return true;
                        case 89:
                            PictureInPictureUiState _arg072 = (PictureInPictureUiState) data.readTypedObject(PictureInPictureUiState.CREATOR);
                            data.enforceNoDataAvail();
                            onPictureInPictureStateChanged(_arg072);
                            reply.writeNoException();
                            return true;
                        case 90:
                            IBinder _arg073 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            detachNavigationBarFromApp(_arg073);
                            reply.writeNoException();
                            return true;
                        case 91:
                            IApplicationThread _arg074 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setRunningRemoteTransitionDelegate(_arg074);
                            reply.writeNoException();
                            return true;
                        case 92:
                            RemoteCallback _arg075 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            BackAnimationAdapter _arg145 = (BackAnimationAdapter) data.readTypedObject(BackAnimationAdapter.CREATOR);
                            data.enforceNoDataAvail();
                            BackNavigationInfo _result49 = startBackNavigation(_arg075, _arg145);
                            reply.writeNoException();
                            reply.writeTypedObject(_result49, 1);
                            return true;
                        case 93:
                            IBinder _arg076 = data.readStrongBinder();
                            IScreenCaptureObserver _arg146 = IScreenCaptureObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerScreenCaptureObserver(_arg076, _arg146);
                            reply.writeNoException();
                            return true;
                        case 94:
                            IBinder _arg077 = data.readStrongBinder();
                            IScreenCaptureObserver _arg147 = IScreenCaptureObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterScreenCaptureObserver(_arg077, _arg147);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IActivityTaskManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IActivityTaskManager.DESCRIPTOR;
            }

            @Override // android.app.IActivityTaskManager
            public int startActivity(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    try {
                        _data.writeString(callingPackage);
                        try {
                            _data.writeString(callingFeatureId);
                            try {
                                _data.writeTypedObject(intent, 0);
                            } catch (Throwable th) {
                                th = th;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(resolvedType);
                        try {
                            _data.writeStrongBinder(resultTo);
                            try {
                                _data.writeString(resultWho);
                                try {
                                    _data.writeInt(requestCode);
                                } catch (Throwable th4) {
                                    th = th4;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(flags);
                            try {
                                _data.writeTypedObject(profilerInfo, 0);
                                try {
                                    _data.writeTypedObject(options, 0);
                                    try {
                                        this.mRemote.transact(1, _data, _reply, 0);
                                        _reply.readException();
                                        int _result = _reply.readInt();
                                        _reply.recycle();
                                        _data.recycle();
                                        return _result;
                                    } catch (Throwable th7) {
                                        th = th7;
                                        _reply.recycle();
                                        _data.recycle();
                                        throw th;
                                    }
                                } catch (Throwable th8) {
                                    th = th8;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                }
            }

            @Override // android.app.IActivityTaskManager
            public int startActivities(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent[] intents, String[] resolvedTypes, IBinder resultTo, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeTypedArray(intents, 0);
                    _data.writeStringArray(resolvedTypes);
                    _data.writeStrongBinder(resultTo);
                    _data.writeTypedObject(options, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int startActivityAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeString(callingFeatureId);
                    try {
                        _data.writeTypedObject(intent, 0);
                        try {
                            _data.writeString(resolvedType);
                            try {
                                _data.writeStrongBinder(resultTo);
                            } catch (Throwable th2) {
                                th = th2;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(resultWho);
                        try {
                            _data.writeInt(requestCode);
                            try {
                                _data.writeInt(flags);
                                try {
                                    _data.writeTypedObject(profilerInfo, 0);
                                } catch (Throwable th5) {
                                    th = th5;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th6) {
                                th = th6;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeTypedObject(options, 0);
                            try {
                                _data.writeInt(userId);
                                try {
                                    this.mRemote.transact(3, _data, _reply, 0);
                                    _reply.readException();
                                    int _result = _reply.readInt();
                                    _reply.recycle();
                                    _data.recycle();
                                    return _result;
                                } catch (Throwable th8) {
                                    th = th8;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean startNextMatchingActivity(IBinder callingActivity, Intent intent, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongBinder(callingActivity);
                    _data.writeTypedObject(intent, 0);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean startDreamActivity(Intent intent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int startActivityIntentSender(IApplicationThread caller, IIntentSender target, IBinder whitelistToken, Intent fillInIntent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flagsMask, int flagsValues, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    try {
                        _data.writeStrongInterface(target);
                        try {
                            _data.writeStrongBinder(whitelistToken);
                            try {
                                _data.writeTypedObject(fillInIntent, 0);
                            } catch (Throwable th) {
                                th = th;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(resolvedType);
                        try {
                            _data.writeStrongBinder(resultTo);
                            try {
                                _data.writeString(resultWho);
                                try {
                                    _data.writeInt(requestCode);
                                } catch (Throwable th4) {
                                    th = th4;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(flagsMask);
                            try {
                                _data.writeInt(flagsValues);
                                try {
                                    _data.writeTypedObject(options, 0);
                                    try {
                                        this.mRemote.transact(6, _data, _reply, 0);
                                        _reply.readException();
                                        int _result = _reply.readInt();
                                        _reply.recycle();
                                        _data.recycle();
                                        return _result;
                                    } catch (Throwable th7) {
                                        th = th7;
                                        _reply.recycle();
                                        _data.recycle();
                                        throw th;
                                    }
                                } catch (Throwable th8) {
                                    th = th8;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                }
            }

            @Override // android.app.IActivityTaskManager
            public WaitResult startActivityAndWait(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    try {
                        _data.writeString(callingFeatureId);
                    } catch (Throwable th) {
                        th = th;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    _data.writeTypedObject(intent, 0);
                    try {
                        _data.writeString(resolvedType);
                        try {
                            _data.writeStrongBinder(resultTo);
                            try {
                                _data.writeString(resultWho);
                            } catch (Throwable th3) {
                                th = th3;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(requestCode);
                        try {
                            _data.writeInt(flags);
                            try {
                                _data.writeTypedObject(profilerInfo, 0);
                                try {
                                    _data.writeTypedObject(options, 0);
                                    try {
                                        _data.writeInt(userId);
                                        try {
                                            this.mRemote.transact(7, _data, _reply, 0);
                                            _reply.readException();
                                            WaitResult _result = (WaitResult) _reply.readTypedObject(WaitResult.CREATOR);
                                            _reply.recycle();
                                            _data.recycle();
                                            return _result;
                                        } catch (Throwable th6) {
                                            th = th6;
                                            _reply.recycle();
                                            _data.recycle();
                                            throw th;
                                        }
                                    } catch (Throwable th7) {
                                        th = th7;
                                    }
                                } catch (Throwable th8) {
                                    th = th8;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.IActivityTaskManager
            public int startActivityWithConfig(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, Configuration newConfig, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeString(callingFeatureId);
                    try {
                        _data.writeTypedObject(intent, 0);
                        try {
                            _data.writeString(resolvedType);
                            try {
                                _data.writeStrongBinder(resultTo);
                            } catch (Throwable th2) {
                                th = th2;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(resultWho);
                        try {
                            _data.writeInt(requestCode);
                            try {
                                _data.writeInt(startFlags);
                                try {
                                    _data.writeTypedObject(newConfig, 0);
                                } catch (Throwable th5) {
                                    th = th5;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th6) {
                                th = th6;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeTypedObject(options, 0);
                            try {
                                _data.writeInt(userId);
                                try {
                                    this.mRemote.transact(8, _data, _reply, 0);
                                    _reply.readException();
                                    int _result = _reply.readInt();
                                    _reply.recycle();
                                    _data.recycle();
                                    return _result;
                                } catch (Throwable th8) {
                                    th = th8;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.IActivityTaskManager
            public int startVoiceActivity(String callingPackage, String callingFeatureId, int callingPid, int callingUid, Intent intent, String resolvedType, IVoiceInteractionSession session, IVoiceInteractor interactor, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeInt(callingPid);
                    try {
                        _data.writeInt(callingUid);
                        try {
                            _data.writeTypedObject(intent, 0);
                            try {
                                _data.writeString(resolvedType);
                            } catch (Throwable th2) {
                                th = th2;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeStrongInterface(session);
                        try {
                            _data.writeStrongInterface(interactor);
                            try {
                                _data.writeInt(flags);
                                try {
                                    _data.writeTypedObject(profilerInfo, 0);
                                } catch (Throwable th5) {
                                    th = th5;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th6) {
                                th = th6;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeTypedObject(options, 0);
                            try {
                                _data.writeInt(userId);
                                try {
                                    this.mRemote.transact(9, _data, _reply, 0);
                                    _reply.readException();
                                    int _result = _reply.readInt();
                                    _reply.recycle();
                                    _data.recycle();
                                    return _result;
                                } catch (Throwable th8) {
                                    th = th8;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.IActivityTaskManager
            public String getVoiceInteractorPackageName(IBinder callingVoiceInteractor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongBinder(callingVoiceInteractor);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int startAssistantActivity(String callingPackage, String callingFeatureId, int callingPid, int callingUid, Intent intent, String resolvedType, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeInt(callingPid);
                    _data.writeInt(callingUid);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeTypedObject(options, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int startActivityFromGameSession(IApplicationThread caller, String callingPackage, String callingFeatureId, int callingPid, int callingUid, Intent intent, int taskId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeInt(callingPid);
                    _data.writeInt(callingUid);
                    _data.writeTypedObject(intent, 0);
                    _data.writeInt(taskId);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void startRecentsActivity(Intent intent, long eventTime, IRecentsAnimationRunner recentsAnimationRunner) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeLong(eventTime);
                    _data.writeStrongInterface(recentsAnimationRunner);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int startActivityFromRecents(int taskId, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int startActivityAsCaller(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, boolean ignoreTargetSecurity, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeTypedObject(intent, 0);
                    try {
                        _data.writeString(resolvedType);
                        try {
                            _data.writeStrongBinder(resultTo);
                            try {
                                _data.writeString(resultWho);
                            } catch (Throwable th2) {
                                th = th2;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(requestCode);
                        try {
                            _data.writeInt(flags);
                            try {
                                _data.writeTypedObject(profilerInfo, 0);
                                try {
                                    _data.writeTypedObject(options, 0);
                                } catch (Throwable th5) {
                                    th = th5;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th6) {
                                th = th6;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeBoolean(ignoreTargetSecurity);
                            try {
                                _data.writeInt(userId);
                                try {
                                    this.mRemote.transact(15, _data, _reply, 0);
                                    _reply.readException();
                                    int _result = _reply.readInt();
                                    _reply.recycle();
                                    _data.recycle();
                                    return _result;
                                } catch (Throwable th8) {
                                    th = th8;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean isActivityStartAllowedOnDisplay(int displayId, Intent intent, String resolvedType, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeInt(userId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void unhandledBack() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public IActivityClientController getActivityClientController() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    IActivityClientController _result = IActivityClientController.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int getFrontActivityScreenCompatMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setFrontActivityScreenCompatMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setFocusedTask(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean removeTask(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void removeAllVisibleRecentTasks() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum, boolean filterOnlyVisibleRecents, boolean keepIntentExtra, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(maxNum);
                    _data.writeBoolean(filterOnlyVisibleRecents);
                    _data.writeBoolean(keepIntentExtra);
                    _data.writeInt(displayId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityManager.RunningTaskInfo> _result = _reply.createTypedArrayList(ActivityManager.RunningTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void moveTaskToFront(IApplicationThread app, String callingPackage, int task, int flags, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(app);
                    _data.writeString(callingPackage);
                    _data.writeInt(task);
                    _data.writeInt(flags);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int maxNum, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(maxNum);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice<ActivityManager.RecentTaskInfo> _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean isTopActivityImmersive() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public ActivityManager.TaskDescription getTaskDescription(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    ActivityManager.TaskDescription _result = (ActivityManager.TaskDescription) _reply.readTypedObject(ActivityManager.TaskDescription.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void reportAssistContextExtras(IBinder assistToken, Bundle extras, AssistStructure structure, AssistContent content, Uri referrer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongBinder(assistToken);
                    _data.writeTypedObject(extras, 0);
                    _data.writeTypedObject(structure, 0);
                    _data.writeTypedObject(content, 0);
                    _data.writeTypedObject(referrer, 0);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setFocusedRootTask(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    ActivityTaskManager.RootTaskInfo _result = (ActivityTaskManager.RootTaskInfo) _reply.readTypedObject(ActivityTaskManager.RootTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public Rect getTaskBounds(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    Rect _result = (Rect) _reply.readTypedObject(Rect.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void cancelRecentsAnimation(boolean restoreHomeRootTaskPosition) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeBoolean(restoreHomeRootTaskPosition);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void updateLockTaskPackages(int userId, String[] packages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeStringArray(packages);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean isInLockTaskMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int getLockTaskModeState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public List<IBinder> getAppTasks(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    List<IBinder> _result = _reply.createBinderArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void startSystemLockTaskMode(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void stopSystemLockTaskMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void finishVoiceTask(IVoiceInteractionSession session) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int addAppTask(IBinder activityToken, Intent intent, ActivityManager.TaskDescription description, Bitmap thumbnail) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongBinder(activityToken);
                    _data.writeTypedObject(intent, 0);
                    _data.writeTypedObject(description, 0);
                    _data.writeTypedObject(thumbnail, 0);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public Point getAppTaskThumbnailSize() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    Point _result = (Point) _reply.readTypedObject(Point.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void releaseSomeActivities(IApplicationThread app) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(app);
                    this.mRemote.transact(43, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public Bitmap getTaskDescriptionIcon(String filename, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeString(filename);
                    _data.writeInt(userId);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    Bitmap _result = (Bitmap) _reply.readTypedObject(Bitmap.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void registerTaskStackListener(ITaskStackListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void unregisterTaskStackListener(ITaskStackListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setTaskResizeable(int taskId, int resizeableMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(resizeableMode);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void resizeTask(int taskId, Rect bounds, int resizeMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(bounds, 0);
                    _data.writeInt(resizeMode);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void moveRootTaskToDisplay(int taskId, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(displayId);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void moveTaskToRootTask(int taskId, int rootTaskId, boolean toTop) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(rootTaskId);
                    _data.writeBoolean(toTop);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void removeRootTasksInWindowingModes(int[] windowingModes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeIntArray(windowingModes);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void removeRootTasksWithActivityTypes(int[] activityTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeIntArray(activityTypes);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityTaskManager.RootTaskInfo> _result = _reply.createTypedArrayList(ActivityTaskManager.RootTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public ActivityTaskManager.RootTaskInfo getRootTaskInfo(int windowingMode, int activityType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(windowingMode);
                    _data.writeInt(activityType);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    ActivityTaskManager.RootTaskInfo _result = (ActivityTaskManager.RootTaskInfo) _reply.readTypedObject(ActivityTaskManager.RootTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfosOnDisplay(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityTaskManager.RootTaskInfo> _result = _reply.createTypedArrayList(ActivityTaskManager.RootTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public ActivityTaskManager.RootTaskInfo getRootTaskInfoOnDisplay(int windowingMode, int activityType, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(windowingMode);
                    _data.writeInt(activityType);
                    _data.writeInt(displayId);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    ActivityTaskManager.RootTaskInfo _result = (ActivityTaskManager.RootTaskInfo) _reply.readTypedObject(ActivityTaskManager.RootTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setLockScreenShown(boolean showingKeyguard, boolean showingAod) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeBoolean(showingKeyguard);
                    _data.writeBoolean(showingAod);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public Bundle getAssistContextExtras(int requestType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(requestType);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean requestAssistContextExtras(int requestType, IAssistDataReceiver receiver, Bundle receiverExtras, IBinder activityToken, boolean focused, boolean newSessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(requestType);
                    _data.writeStrongInterface(receiver);
                    _data.writeTypedObject(receiverExtras, 0);
                    _data.writeStrongBinder(activityToken);
                    _data.writeBoolean(focused);
                    _data.writeBoolean(newSessionId);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean requestAutofillData(IAssistDataReceiver receiver, Bundle receiverExtras, IBinder activityToken, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(receiver);
                    _data.writeTypedObject(receiverExtras, 0);
                    _data.writeStrongBinder(activityToken);
                    _data.writeInt(flags);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean isAssistDataAllowedOnCurrentActivity() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean requestAssistDataForTask(IAssistDataReceiver receiver, int taskId, String callingPackageName, String callingAttributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(receiver);
                    _data.writeInt(taskId);
                    _data.writeString(callingPackageName);
                    _data.writeString(callingAttributionTag);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void keyguardGoingAway(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void suppressResizeConfigChanges(boolean suppress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeBoolean(suppress);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public IWindowOrganizerController getWindowOrganizerController() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                    IWindowOrganizerController _result = IWindowOrganizerController.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean supportsLocalVoiceInteraction() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public ConfigurationInfo getDeviceConfigurationInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                    ConfigurationInfo _result = (ConfigurationInfo) _reply.readTypedObject(ConfigurationInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void cancelTaskWindowTransition(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public TaskSnapshot getTaskSnapshot(int taskId, boolean isLowResolution, boolean takeSnapshotIfNeeded) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeBoolean(isLowResolution);
                    _data.writeBoolean(takeSnapshotIfNeeded);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                    TaskSnapshot _result = (TaskSnapshot) _reply.readTypedObject(TaskSnapshot.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public TaskSnapshot takeTaskSnapshot(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                    TaskSnapshot _result = (TaskSnapshot) _reply.readTypedObject(TaskSnapshot.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int getLastResumedActivityUserId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean updateConfiguration(Configuration values) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeTypedObject(values, 0);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void updateLockTaskFeatures(int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void registerRemoteAnimationForNextActivityStart(String packageName, RemoteAnimationAdapter adapter, IBinder launchCookie) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(adapter, 0);
                    _data.writeStrongBinder(launchCookie);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void registerRemoteAnimationsForDisplay(int displayId, RemoteAnimationDefinition definition) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(definition, 0);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void alwaysShowUnsupportedCompileSdkWarning(ComponentName activity) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeTypedObject(activity, 0);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setVrThread(int tid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(tid);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setPersistentVrThread(int tid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(tid);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void stopAppSwitches() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void resumeAppSwitches() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setActivityController(IActivityController watcher, boolean imAMonkey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(watcher);
                    _data.writeBoolean(imAMonkey);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setVoiceKeepAwake(IVoiceInteractionSession session, boolean keepAwake) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeBoolean(keepAwake);
                    this.mRemote.transact(82, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public int getPackageScreenCompatMode(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setPackageScreenCompatMode(String packageName, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(mode);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public boolean getPackageAskScreenCompat(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setPackageAskScreenCompat(String packageName, boolean ask) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(ask);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void clearLaunchParamsForPackages(List<String> packageNames) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStringList(packageNames);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void onSplashScreenViewCopyFinished(int taskId, SplashScreenView.SplashScreenViewParcelable material) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(material, 0);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void onPictureInPictureStateChanged(PictureInPictureUiState pipState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeTypedObject(pipState, 0);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void detachNavigationBarFromApp(IBinder transition) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongBinder(transition);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void setRunningRemoteTransitionDelegate(IApplicationThread caller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public BackNavigationInfo startBackNavigation(RemoteCallback navigationObserver, BackAnimationAdapter adaptor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeTypedObject(navigationObserver, 0);
                    _data.writeTypedObject(adaptor, 0);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                    BackNavigationInfo _result = (BackNavigationInfo) _reply.readTypedObject(BackNavigationInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void registerScreenCaptureObserver(IBinder activityToken, IScreenCaptureObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongBinder(activityToken);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityTaskManager
            public void unregisterScreenCaptureObserver(IBinder activityToken, IScreenCaptureObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IActivityTaskManager.DESCRIPTOR);
                    _data.writeStrongBinder(activityToken);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 93;
        }
    }
}
