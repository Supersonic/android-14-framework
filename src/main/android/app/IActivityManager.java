package android.app;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.ApplicationErrorReport;
import android.app.IActivityController;
import android.app.IApplicationStartInfoCompleteListener;
import android.app.IApplicationThread;
import android.app.IForegroundServiceObserver;
import android.app.IInstrumentationWatcher;
import android.app.IProcessObserver;
import android.app.IServiceConnection;
import android.app.IStopUserCallback;
import android.app.ITaskStackListener;
import android.app.IUiAutomationConnection;
import android.app.IUidObserver;
import android.app.IUserSwitchObserver;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.LocusId;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.IPackageDataObserver;
import android.content.p001pm.ParceledListSlice;
import android.content.p001pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Debug;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.IProgressListener;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.StrictMode;
import android.p008os.WorkSource;
import android.text.TextUtils;
import com.android.internal.p028os.IResultReceiver;
import java.util.List;
/* loaded from: classes.dex */
public interface IActivityManager extends IInterface {
    void addInstrumentationResults(IApplicationThread iApplicationThread, Bundle bundle) throws RemoteException;

    void addPackageDependency(String str) throws RemoteException;

    void appNotResponding(String str) throws RemoteException;

    void appNotRespondingViaProvider(IBinder iBinder) throws RemoteException;

    void attachApplication(IApplicationThread iApplicationThread, long j) throws RemoteException;

    void backgroundAllowlistUid(int i) throws RemoteException;

    void backupAgentCreated(String str, IBinder iBinder, int i) throws RemoteException;

    boolean bindBackupAgent(String str, int i, int i2, int i3) throws RemoteException;

    int bindService(IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, String str, IServiceConnection iServiceConnection, long j, String str2, int i) throws RemoteException;

    int bindServiceInstance(IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, String str, IServiceConnection iServiceConnection, long j, String str2, String str3, int i) throws RemoteException;

    void bootAnimationComplete() throws RemoteException;

    @Deprecated
    int broadcastIntent(IApplicationThread iApplicationThread, Intent intent, String str, IIntentReceiver iIntentReceiver, int i, String str2, Bundle bundle, String[] strArr, int i2, Bundle bundle2, boolean z, boolean z2, int i3) throws RemoteException;

    int broadcastIntentWithFeature(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IIntentReceiver iIntentReceiver, int i, String str3, Bundle bundle, String[] strArr, String[] strArr2, String[] strArr3, int i2, Bundle bundle2, boolean z, boolean z2, int i3) throws RemoteException;

    void cancelIntentSender(IIntentSender iIntentSender) throws RemoteException;

    void cancelTaskWindowTransition(int i) throws RemoteException;

    int checkPermission(String str, int i, int i2) throws RemoteException;

    int checkUriPermission(Uri uri, int i, int i2, int i3, int i4, IBinder iBinder) throws RemoteException;

    int[] checkUriPermissions(List<Uri> list, int i, int i2, int i3, int i4, IBinder iBinder) throws RemoteException;

    boolean clearApplicationUserData(String str, boolean z, IPackageDataObserver iPackageDataObserver, int i) throws RemoteException;

    void closeSystemDialogs(String str) throws RemoteException;

    void crashApplicationWithType(int i, int i2, String str, int i3, String str2, boolean z, int i4) throws RemoteException;

    void crashApplicationWithTypeWithExtras(int i, int i2, String str, int i3, String str2, boolean z, int i4, Bundle bundle) throws RemoteException;

    boolean dumpHeap(String str, int i, boolean z, boolean z2, boolean z3, String str2, ParcelFileDescriptor parcelFileDescriptor, RemoteCallback remoteCallback) throws RemoteException;

    void dumpHeapFinished(String str) throws RemoteException;

    boolean enableAppFreezer(boolean z) throws RemoteException;

    boolean enableFgsNotificationRateLimit(boolean z) throws RemoteException;

    void enterSafeMode() throws RemoteException;

    boolean finishActivity(IBinder iBinder, int i, Intent intent, int i2) throws RemoteException;

    void finishAttachApplication(long j) throws RemoteException;

    void finishHeavyWeightApp() throws RemoteException;

    void finishInstrumentation(IApplicationThread iApplicationThread, int i, Bundle bundle) throws RemoteException;

    void finishReceiver(IBinder iBinder, int i, String str, Bundle bundle, boolean z, int i2) throws RemoteException;

    void forceDelayBroadcastDelivery(String str, long j) throws RemoteException;

    void forceStopPackage(String str, int i) throws RemoteException;

    List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() throws RemoteException;

    int getBackgroundRestrictionExemptionReason(int i) throws RemoteException;

    List<String> getBugreportWhitelistedPackages() throws RemoteException;

    Configuration getConfiguration() throws RemoteException;

    ContentProviderHolder getContentProvider(IApplicationThread iApplicationThread, String str, String str2, int i, boolean z) throws RemoteException;

    ContentProviderHolder getContentProviderExternal(String str, int i, IBinder iBinder, String str2) throws RemoteException;

    UserInfo getCurrentUser() throws RemoteException;

    int getCurrentUserId() throws RemoteException;

    List<String> getDelegatedShellPermissions() throws RemoteException;

    int[] getDisplayIdsForStartingVisibleBackgroundUsers() throws RemoteException;

    ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException;

    int getForegroundServiceType(ComponentName componentName, IBinder iBinder) throws RemoteException;

    ParceledListSlice<ApplicationExitInfo> getHistoricalProcessExitReasons(String str, int i, int i2, int i3) throws RemoteException;

    ParceledListSlice<ApplicationStartInfo> getHistoricalProcessStartReasons(String str, int i, int i2) throws RemoteException;

    ActivityManager.PendingIntentInfo getInfoForIntentSender(IIntentSender iIntentSender) throws RemoteException;

    Intent getIntentForIntentSender(IIntentSender iIntentSender) throws RemoteException;

    @Deprecated
    IIntentSender getIntentSender(int i, String str, IBinder iBinder, String str2, int i2, Intent[] intentArr, String[] strArr, int i3, Bundle bundle, int i4) throws RemoteException;

    IIntentSender getIntentSenderWithFeature(int i, String str, String str2, IBinder iBinder, String str3, int i2, Intent[] intentArr, String[] strArr, int i3, Bundle bundle, int i4) throws RemoteException;

    String getLaunchedFromPackage(IBinder iBinder) throws RemoteException;

    int getLaunchedFromUid(IBinder iBinder) throws RemoteException;

    ParcelFileDescriptor getLifeMonitor() throws RemoteException;

    int getLockTaskModeState() throws RemoteException;

    void getMemoryInfo(ActivityManager.MemoryInfo memoryInfo) throws RemoteException;

    int getMemoryTrimLevel() throws RemoteException;

    void getMimeTypeFilterAsync(Uri uri, int i, RemoteCallback remoteCallback) throws RemoteException;

    void getMyMemoryState(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) throws RemoteException;

    int getPackageProcessState(String str, String str2) throws RemoteException;

    int getProcessLimit() throws RemoteException;

    Debug.MemoryInfo[] getProcessMemoryInfo(int[] iArr) throws RemoteException;

    long[] getProcessPss(int[] iArr) throws RemoteException;

    List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState() throws RemoteException;

    ParceledListSlice getRecentTasks(int i, int i2, int i3) throws RemoteException;

    List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() throws RemoteException;

    List<ApplicationInfo> getRunningExternalApplications() throws RemoteException;

    PendingIntent getRunningServiceControlPanel(ComponentName componentName) throws RemoteException;

    int[] getRunningUserIds() throws RemoteException;

    List<ActivityManager.RunningServiceInfo> getServices(int i, int i2) throws RemoteException;

    String getSwitchingFromUserMessage() throws RemoteException;

    String getSwitchingToUserMessage() throws RemoteException;

    String getTagForIntentSender(IIntentSender iIntentSender, String str) throws RemoteException;

    Rect getTaskBounds(int i) throws RemoteException;

    int getTaskForActivity(IBinder iBinder, boolean z) throws RemoteException;

    List<ActivityManager.RunningTaskInfo> getTasks(int i) throws RemoteException;

    int getUidProcessCapabilities(int i, String str) throws RemoteException;

    int getUidProcessState(int i, String str) throws RemoteException;

    void grantUriPermission(IApplicationThread iApplicationThread, String str, Uri uri, int i, int i2) throws RemoteException;

    void handleApplicationCrash(IBinder iBinder, ApplicationErrorReport.ParcelableCrashInfo parcelableCrashInfo) throws RemoteException;

    void handleApplicationStrictModeViolation(IBinder iBinder, int i, StrictMode.ViolationInfo violationInfo) throws RemoteException;

    boolean handleApplicationWtf(IBinder iBinder, String str, boolean z, ApplicationErrorReport.ParcelableCrashInfo parcelableCrashInfo, int i) throws RemoteException;

    int handleIncomingUser(int i, int i2, int i3, boolean z, boolean z2, String str, String str2) throws RemoteException;

    void hang(IBinder iBinder, boolean z) throws RemoteException;

    void holdLock(IBinder iBinder, int i) throws RemoteException;

    boolean isAppFreezerEnabled() throws RemoteException;

    boolean isAppFreezerSupported() throws RemoteException;

    boolean isBackgroundRestricted(String str) throws RemoteException;

    boolean isInLockTaskMode() throws RemoteException;

    boolean isIntentSenderAnActivity(IIntentSender iIntentSender) throws RemoteException;

    boolean isIntentSenderTargetedToPackage(IIntentSender iIntentSender) throws RemoteException;

    boolean isModernBroadcastQueueEnabled() throws RemoteException;

    boolean isProcessFrozen(int i) throws RemoteException;

    boolean isTopActivityImmersive() throws RemoteException;

    boolean isTopOfTask(IBinder iBinder) throws RemoteException;

    boolean isUidActive(int i, String str) throws RemoteException;

    boolean isUserAMonkey() throws RemoteException;

    boolean isUserRunning(int i, int i2) throws RemoteException;

    boolean isVrModePackageEnabled(ComponentName componentName) throws RemoteException;

    void killAllBackgroundProcesses() throws RemoteException;

    void killApplication(String str, int i, int i2, String str2, int i3) throws RemoteException;

    void killApplicationProcess(String str, int i) throws RemoteException;

    void killBackgroundProcesses(String str, int i) throws RemoteException;

    void killPackageDependents(String str, int i) throws RemoteException;

    boolean killPids(int[] iArr, String str, boolean z) throws RemoteException;

    boolean killProcessesBelowForeground(String str) throws RemoteException;

    void killProcessesWhenImperceptible(int[] iArr, String str) throws RemoteException;

    void killUid(int i, int i2, String str) throws RemoteException;

    void killUidForPermissionChange(int i, int i2, String str) throws RemoteException;

    boolean launchBugReportHandlerApp() throws RemoteException;

    void logFgsApiBegin(int i, int i2, int i3) throws RemoteException;

    void logFgsApiEnd(int i, int i2, int i3) throws RemoteException;

    void logFgsApiStateChanged(int i, int i2, int i3, int i4) throws RemoteException;

    void makePackageIdle(String str, int i) throws RemoteException;

    boolean moveActivityTaskToBack(IBinder iBinder, boolean z) throws RemoteException;

    void moveTaskToFront(IApplicationThread iApplicationThread, String str, int i, int i2, Bundle bundle) throws RemoteException;

    void moveTaskToRootTask(int i, int i2, boolean z) throws RemoteException;

    void noteAlarmFinish(IIntentSender iIntentSender, WorkSource workSource, int i, String str) throws RemoteException;

    void noteAlarmStart(IIntentSender iIntentSender, WorkSource workSource, int i, String str) throws RemoteException;

    void noteWakeupAlarm(IIntentSender iIntentSender, WorkSource workSource, int i, String str, String str2) throws RemoteException;

    void notifyCleartextNetwork(int i, byte[] bArr) throws RemoteException;

    void notifyLockedProfile(int i) throws RemoteException;

    ParcelFileDescriptor openContentUri(String str) throws RemoteException;

    IBinder peekService(Intent intent, String str, String str2) throws RemoteException;

    void performIdleMaintenance() throws RemoteException;

    boolean profileControl(String str, int i, boolean z, ProfilerInfo profilerInfo, int i2) throws RemoteException;

    void publishContentProviders(IApplicationThread iApplicationThread, List<ContentProviderHolder> list) throws RemoteException;

    void publishService(IBinder iBinder, Intent intent, IBinder iBinder2) throws RemoteException;

    ParceledListSlice queryIntentComponentsForIntentSender(IIntentSender iIntentSender, int i) throws RemoteException;

    boolean refContentProvider(IBinder iBinder, int i, int i2) throws RemoteException;

    boolean registerForegroundServiceObserver(IForegroundServiceObserver iForegroundServiceObserver) throws RemoteException;

    boolean registerIntentSenderCancelListenerEx(IIntentSender iIntentSender, IResultReceiver iResultReceiver) throws RemoteException;

    void registerProcessObserver(IProcessObserver iProcessObserver) throws RemoteException;

    Intent registerReceiver(IApplicationThread iApplicationThread, String str, IIntentReceiver iIntentReceiver, IntentFilter intentFilter, String str2, int i, int i2) throws RemoteException;

    Intent registerReceiverWithFeature(IApplicationThread iApplicationThread, String str, String str2, String str3, IIntentReceiver iIntentReceiver, IntentFilter intentFilter, String str4, int i, int i2) throws RemoteException;

    void registerStrictModeCallback(IBinder iBinder) throws RemoteException;

    void registerTaskStackListener(ITaskStackListener iTaskStackListener) throws RemoteException;

    void registerUidObserver(IUidObserver iUidObserver, int i, int i2, String str) throws RemoteException;

    void registerUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver, String str) throws RemoteException;

    void removeApplicationStartInfoCompleteListener(int i) throws RemoteException;

    void removeContentProvider(IBinder iBinder, boolean z) throws RemoteException;

    @Deprecated
    void removeContentProviderExternal(String str, IBinder iBinder) throws RemoteException;

    void removeContentProviderExternalAsUser(String str, IBinder iBinder, int i) throws RemoteException;

    boolean removeTask(int i) throws RemoteException;

    void requestBugReport(int i) throws RemoteException;

    void requestBugReportWithDescription(String str, String str2, int i) throws RemoteException;

    void requestFullBugReport() throws RemoteException;

    void requestInteractiveBugReport() throws RemoteException;

    void requestInteractiveBugReportWithDescription(String str, String str2) throws RemoteException;

    void requestRemoteBugReport(long j) throws RemoteException;

    void requestSystemServerHeapDump() throws RemoteException;

    void requestTelephonyBugReport(String str, String str2) throws RemoteException;

    void requestWifiBugReport(String str, String str2) throws RemoteException;

    void resetAppErrors() throws RemoteException;

    void resizeTask(int i, Rect rect, int i2) throws RemoteException;

    void restart() throws RemoteException;

    int restartUserInBackground(int i) throws RemoteException;

    void resumeAppSwitches() throws RemoteException;

    void revokeUriPermission(IApplicationThread iApplicationThread, String str, Uri uri, int i, int i2) throws RemoteException;

    void scheduleApplicationInfoChanged(List<String> list, int i) throws RemoteException;

    void sendIdleJobTrigger() throws RemoteException;

    int sendIntentSender(IApplicationThread iApplicationThread, IIntentSender iIntentSender, IBinder iBinder, int i, Intent intent, String str, IIntentReceiver iIntentReceiver, String str2, Bundle bundle) throws RemoteException;

    void serviceDoneExecuting(IBinder iBinder, int i, int i2, int i3) throws RemoteException;

    void setActivityController(IActivityController iActivityController, boolean z) throws RemoteException;

    void setActivityLocusContext(ComponentName componentName, LocusId locusId, IBinder iBinder) throws RemoteException;

    void setAgentApp(String str, String str2) throws RemoteException;

    void setAlwaysFinish(boolean z) throws RemoteException;

    void setApplicationStartInfoCompleteListener(IApplicationStartInfoCompleteListener iApplicationStartInfoCompleteListener, int i) throws RemoteException;

    void setDebugApp(String str, boolean z, boolean z2) throws RemoteException;

    void setDumpHeapDebugLimit(String str, int i, long j, String str2) throws RemoteException;

    void setFocusedRootTask(int i) throws RemoteException;

    void setHasTopUi(boolean z) throws RemoteException;

    void setPackageScreenCompatMode(String str, int i) throws RemoteException;

    void setPersistentVrThread(int i) throws RemoteException;

    void setProcessImportant(IBinder iBinder, int i, boolean z, String str) throws RemoteException;

    void setProcessLimit(int i) throws RemoteException;

    boolean setProcessMemoryTrimLevel(String str, int i, int i2) throws RemoteException;

    void setProcessStateSummary(byte[] bArr) throws RemoteException;

    void setRenderThread(int i) throws RemoteException;

    void setRequestedOrientation(IBinder iBinder, int i) throws RemoteException;

    void setServiceForeground(ComponentName componentName, IBinder iBinder, int i, Notification notification, int i2, int i3) throws RemoteException;

    void setStopUserOnSwitch(int i) throws RemoteException;

    void setTaskResizeable(int i, int i2) throws RemoteException;

    void setUserIsMonkey(boolean z) throws RemoteException;

    boolean shouldServiceTimeOut(ComponentName componentName, IBinder iBinder) throws RemoteException;

    void showBootMessage(CharSequence charSequence, boolean z) throws RemoteException;

    void showWaitingForDebugger(IApplicationThread iApplicationThread, boolean z) throws RemoteException;

    boolean shutdown(int i) throws RemoteException;

    void signalPersistentProcesses(int i) throws RemoteException;

    @Deprecated
    int startActivity(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle) throws RemoteException;

    @Deprecated
    int startActivityAsUser(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3) throws RemoteException;

    int startActivityAsUserWithFeature(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3) throws RemoteException;

    int startActivityFromRecents(int i, Bundle bundle) throws RemoteException;

    int startActivityWithFeature(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle) throws RemoteException;

    boolean startBinderTracking() throws RemoteException;

    void startConfirmDeviceCredentialIntent(Intent intent, Bundle bundle) throws RemoteException;

    void startDelegateShellPermissionIdentity(int i, String[] strArr) throws RemoteException;

    boolean startInstrumentation(ComponentName componentName, String str, int i, Bundle bundle, IInstrumentationWatcher iInstrumentationWatcher, IUiAutomationConnection iUiAutomationConnection, int i2, String str2) throws RemoteException;

    boolean startProfile(int i) throws RemoteException;

    boolean startProfileWithListener(int i, IProgressListener iProgressListener) throws RemoteException;

    ComponentName startService(IApplicationThread iApplicationThread, Intent intent, String str, boolean z, String str2, String str3, int i) throws RemoteException;

    void startSystemLockTaskMode(int i) throws RemoteException;

    boolean startUserInBackground(int i) throws RemoteException;

    boolean startUserInBackgroundVisibleOnDisplay(int i, int i2, IProgressListener iProgressListener) throws RemoteException;

    boolean startUserInBackgroundWithListener(int i, IProgressListener iProgressListener) throws RemoteException;

    boolean startUserInForegroundWithListener(int i, IProgressListener iProgressListener) throws RemoteException;

    void stopAppForUser(String str, int i) throws RemoteException;

    void stopAppSwitches() throws RemoteException;

    boolean stopBinderTrackingAndDump(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void stopDelegateShellPermissionIdentity() throws RemoteException;

    boolean stopProfile(int i) throws RemoteException;

    int stopService(IApplicationThread iApplicationThread, Intent intent, String str, int i) throws RemoteException;

    boolean stopServiceToken(ComponentName componentName, IBinder iBinder, int i) throws RemoteException;

    int stopUser(int i, boolean z, IStopUserCallback iStopUserCallback) throws RemoteException;

    int stopUserWithDelayedLocking(int i, boolean z, IStopUserCallback iStopUserCallback) throws RemoteException;

    void suppressResizeConfigChanges(boolean z) throws RemoteException;

    boolean switchUser(int i) throws RemoteException;

    void unbindBackupAgent(ApplicationInfo applicationInfo) throws RemoteException;

    void unbindFinished(IBinder iBinder, Intent intent, boolean z) throws RemoteException;

    boolean unbindService(IServiceConnection iServiceConnection) throws RemoteException;

    void unbroadcastIntent(IApplicationThread iApplicationThread, Intent intent, int i) throws RemoteException;

    void unhandledBack() throws RemoteException;

    @Deprecated
    boolean unlockUser(int i, byte[] bArr, byte[] bArr2, IProgressListener iProgressListener) throws RemoteException;

    boolean unlockUser2(int i, IProgressListener iProgressListener) throws RemoteException;

    void unregisterIntentSenderCancelListener(IIntentSender iIntentSender, IResultReceiver iResultReceiver) throws RemoteException;

    void unregisterProcessObserver(IProcessObserver iProcessObserver) throws RemoteException;

    void unregisterReceiver(IIntentReceiver iIntentReceiver) throws RemoteException;

    void unregisterTaskStackListener(ITaskStackListener iTaskStackListener) throws RemoteException;

    void unregisterUidObserver(IUidObserver iUidObserver) throws RemoteException;

    void unregisterUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver) throws RemoteException;

    void unstableProviderDied(IBinder iBinder) throws RemoteException;

    boolean updateConfiguration(Configuration configuration) throws RemoteException;

    void updateLockTaskPackages(int i, String[] strArr) throws RemoteException;

    boolean updateMccMncConfiguration(String str, String str2) throws RemoteException;

    void updatePersistentConfiguration(Configuration configuration) throws RemoteException;

    void updatePersistentConfigurationWithAttribution(Configuration configuration, String str, String str2) throws RemoteException;

    void updateServiceGroup(IServiceConnection iServiceConnection, int i, int i2) throws RemoteException;

    void waitForBroadcastBarrier() throws RemoteException;

    void waitForBroadcastIdle() throws RemoteException;

    void waitForNetworkStateUpdate(long j) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IActivityManager {
        @Override // android.app.IActivityManager
        public ParcelFileDescriptor openContentUri(String uriString) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void registerUidObserver(IUidObserver observer, int which, int cutpoint, String callingPackage) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void unregisterUidObserver(IUidObserver observer) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean isUidActive(int uid, String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public int getUidProcessState(int uid, String callingPackage) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int checkPermission(String permission, int pid, int uid) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void handleApplicationCrash(IBinder app, ApplicationErrorReport.ParcelableCrashInfo crashInfo) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int startActivity(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int startActivityWithFeature(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void unhandledBack() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean finishActivity(IBinder token, int code, Intent data, int finishTask) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public Intent registerReceiver(IApplicationThread caller, String callerPackage, IIntentReceiver receiver, IntentFilter filter, String requiredPermission, int userId, int flags) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public Intent registerReceiverWithFeature(IApplicationThread caller, String callerPackage, String callingFeatureId, String receiverId, IIntentReceiver receiver, IntentFilter filter, String requiredPermission, int userId, int flags) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void unregisterReceiver(IIntentReceiver receiver) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int broadcastIntent(IApplicationThread caller, Intent intent, String resolvedType, IIntentReceiver resultTo, int resultCode, String resultData, Bundle map, String[] requiredPermissions, int appOp, Bundle options, boolean serialized, boolean sticky, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int broadcastIntentWithFeature(IApplicationThread caller, String callingFeatureId, Intent intent, String resolvedType, IIntentReceiver resultTo, int resultCode, String resultData, Bundle map, String[] requiredPermissions, String[] excludePermissions, String[] excludePackages, int appOp, Bundle options, boolean serialized, boolean sticky, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void unbroadcastIntent(IApplicationThread caller, Intent intent, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void finishReceiver(IBinder who, int resultCode, String resultData, Bundle map, boolean abortBroadcast, int flags) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void attachApplication(IApplicationThread app, long startSeq) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void finishAttachApplication(long startSeq) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void moveTaskToFront(IApplicationThread caller, String callingPackage, int task, int flags, Bundle options) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int getTaskForActivity(IBinder token, boolean onlyRoot) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public ContentProviderHolder getContentProvider(IApplicationThread caller, String callingPackage, String name, int userId, boolean stable) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void publishContentProviders(IApplicationThread caller, List<ContentProviderHolder> providers) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean refContentProvider(IBinder connection, int stableDelta, int unstableDelta) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public PendingIntent getRunningServiceControlPanel(ComponentName service) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public ComponentName startService(IApplicationThread caller, Intent service, String resolvedType, boolean requireForeground, String callingPackage, String callingFeatureId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public int stopService(IApplicationThread caller, Intent service, String resolvedType, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int bindService(IApplicationThread caller, IBinder token, Intent service, String resolvedType, IServiceConnection connection, long flags, String callingPackage, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int bindServiceInstance(IApplicationThread caller, IBinder token, Intent service, String resolvedType, IServiceConnection connection, long flags, String instanceName, String callingPackage, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void updateServiceGroup(IServiceConnection connection, int group, int importance) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean unbindService(IServiceConnection connection) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void publishService(IBinder token, Intent intent, IBinder service) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setDebugApp(String packageName, boolean waitForDebugger, boolean persistent) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setAgentApp(String packageName, String agent) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setAlwaysFinish(boolean enabled) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean startInstrumentation(ComponentName className, String profileFile, int flags, Bundle arguments, IInstrumentationWatcher watcher, IUiAutomationConnection connection, int userId, String abiOverride) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void addInstrumentationResults(IApplicationThread target, Bundle results) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void finishInstrumentation(IApplicationThread target, int resultCode, Bundle results) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public Configuration getConfiguration() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public boolean updateConfiguration(Configuration values) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean updateMccMncConfiguration(String mcc, String mnc) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean stopServiceToken(ComponentName className, IBinder token, int startId) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void setProcessLimit(int max) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int getProcessLimit() throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int checkUriPermission(Uri uri, int pid, int uid, int mode, int userId, IBinder callerToken) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int[] checkUriPermissions(List<Uri> uris, int pid, int uid, int mode, int userId, IBinder callerToken) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void grantUriPermission(IApplicationThread caller, String targetPkg, Uri uri, int mode, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void revokeUriPermission(IApplicationThread caller, String targetPkg, Uri uri, int mode, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setActivityController(IActivityController watcher, boolean imAMonkey) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void showWaitingForDebugger(IApplicationThread who, boolean waiting) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void signalPersistentProcesses(int signal) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public ParceledListSlice getRecentTasks(int maxNum, int flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void serviceDoneExecuting(IBinder token, int type, int startId, int res) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public IIntentSender getIntentSender(int type, String packageName, IBinder token, String resultWho, int requestCode, Intent[] intents, String[] resolvedTypes, int flags, Bundle options, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public IIntentSender getIntentSenderWithFeature(int type, String packageName, String featureId, IBinder token, String resultWho, int requestCode, Intent[] intents, String[] resolvedTypes, int flags, Bundle options, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void cancelIntentSender(IIntentSender sender) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public ActivityManager.PendingIntentInfo getInfoForIntentSender(IIntentSender sender) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public boolean registerIntentSenderCancelListenerEx(IIntentSender sender, IResultReceiver receiver) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void unregisterIntentSenderCancelListener(IIntentSender sender, IResultReceiver receiver) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void enterSafeMode() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void noteWakeupAlarm(IIntentSender sender, WorkSource workSource, int sourceUid, String sourcePkg, String tag) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void removeContentProvider(IBinder connection, boolean stable) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setRequestedOrientation(IBinder token, int requestedOrientation) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void unbindFinished(IBinder token, Intent service, boolean doRebind) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setProcessImportant(IBinder token, int pid, boolean isForeground, String reason) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setServiceForeground(ComponentName className, IBinder token, int id, Notification notification, int flags, int foregroundServiceType) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int getForegroundServiceType(ComponentName className, IBinder token) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public boolean moveActivityTaskToBack(IBinder token, boolean nonRoot) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void getMemoryInfo(ActivityManager.MemoryInfo outInfo) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public boolean clearApplicationUserData(String packageName, boolean keepState, IPackageDataObserver observer, int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void stopAppForUser(String packageName, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean registerForegroundServiceObserver(IForegroundServiceObserver callback) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void forceStopPackage(String packageName, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean killPids(int[] pids, String reason, boolean secure) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public List<ActivityManager.RunningServiceInfo> getServices(int maxNum, int flags) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public IBinder peekService(Intent service, String resolvedType, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public boolean profileControl(String process, int userId, boolean start, ProfilerInfo profilerInfo, int profileType) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean shutdown(int timeout) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void stopAppSwitches() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void resumeAppSwitches() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean bindBackupAgent(String packageName, int backupRestoreMode, int targetUserId, int backupDestination) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void backupAgentCreated(String packageName, IBinder agent, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void unbindBackupAgent(ApplicationInfo appInfo) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int handleIncomingUser(int callingPid, int callingUid, int userId, boolean allowAll, boolean requireFull, String name, String callerPackage) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void addPackageDependency(String packageName) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void killApplication(String pkg, int appId, int userId, String reason, int exitInfoReason) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void closeSystemDialogs(String reason) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public Debug.MemoryInfo[] getProcessMemoryInfo(int[] pids) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void killApplicationProcess(String processName, int uid) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean handleApplicationWtf(IBinder app, String tag, boolean system, ApplicationErrorReport.ParcelableCrashInfo crashInfo, int immediateCallerPid) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void killBackgroundProcesses(String packageName, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean isUserAMonkey() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public List<ApplicationInfo> getRunningExternalApplications() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void finishHeavyWeightApp() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void handleApplicationStrictModeViolation(IBinder app, int penaltyMask, StrictMode.ViolationInfo crashInfo) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void registerStrictModeCallback(IBinder binder) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean isTopActivityImmersive() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void crashApplicationWithType(int uid, int initialPid, String packageName, int userId, String message, boolean force, int exceptionTypeId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void crashApplicationWithTypeWithExtras(int uid, int initialPid, String packageName, int userId, String message, boolean force, int exceptionTypeId, Bundle extras) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void getMimeTypeFilterAsync(Uri uri, int userId, RemoteCallback resultCallback) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean dumpHeap(String process, int userId, boolean managed, boolean mallocInfo, boolean runGc, String path, ParcelFileDescriptor fd, RemoteCallback finishCallback) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean isUserRunning(int userid, int flags) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void setPackageScreenCompatMode(String packageName, int mode) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean switchUser(int userid) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public String getSwitchingFromUserMessage() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public String getSwitchingToUserMessage() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void setStopUserOnSwitch(int value) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean removeTask(int taskId) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void registerProcessObserver(IProcessObserver observer) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void unregisterProcessObserver(IProcessObserver observer) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean isIntentSenderTargetedToPackage(IIntentSender sender) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void updatePersistentConfiguration(Configuration values) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void updatePersistentConfigurationWithAttribution(Configuration values, String callingPackageName, String callingAttributionTag) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public long[] getProcessPss(int[] pids) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void showBootMessage(CharSequence msg, boolean always) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void killAllBackgroundProcesses() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public ContentProviderHolder getContentProviderExternal(String name, int userId, IBinder token, String tag) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void removeContentProviderExternal(String name, IBinder token) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void removeContentProviderExternalAsUser(String name, IBinder token, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void getMyMemoryState(ActivityManager.RunningAppProcessInfo outInfo) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean killProcessesBelowForeground(String reason) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public UserInfo getCurrentUser() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public int getCurrentUserId() throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int getLaunchedFromUid(IBinder activityToken) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void unstableProviderDied(IBinder connection) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean isIntentSenderAnActivity(IIntentSender sender) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public int startActivityAsUser(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int startActivityAsUserWithFeature(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int stopUser(int userid, boolean force, IStopUserCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public int stopUserWithDelayedLocking(int userid, boolean force, IStopUserCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void registerUserSwitchObserver(IUserSwitchObserver observer, String name) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void unregisterUserSwitchObserver(IUserSwitchObserver observer) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int[] getRunningUserIds() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void requestSystemServerHeapDump() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void requestBugReport(int bugreportType) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void requestBugReportWithDescription(String shareTitle, String shareDescription, int bugreportType) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void requestTelephonyBugReport(String shareTitle, String shareDescription) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void requestWifiBugReport(String shareTitle, String shareDescription) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void requestInteractiveBugReportWithDescription(String shareTitle, String shareDescription) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void requestInteractiveBugReport() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void requestFullBugReport() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void requestRemoteBugReport(long nonce) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean launchBugReportHandlerApp() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public List<String> getBugreportWhitelistedPackages() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public Intent getIntentForIntentSender(IIntentSender sender) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public String getLaunchedFromPackage(IBinder activityToken) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void killUid(int appId, int userId, String reason) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setUserIsMonkey(boolean monkey) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void hang(IBinder who, boolean allowRestart) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void moveTaskToRootTask(int taskId, int rootTaskId, boolean toTop) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setFocusedRootTask(int taskId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void restart() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void performIdleMaintenance() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void appNotRespondingViaProvider(IBinder connection) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public Rect getTaskBounds(int taskId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public boolean setProcessMemoryTrimLevel(String process, int userId, int level) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public String getTagForIntentSender(IIntentSender sender, String prefix) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public boolean startUserInBackground(int userid) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean isInLockTaskMode() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public int startActivityFromRecents(int taskId, Bundle options) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void startSystemLockTaskMode(int taskId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean isTopOfTask(IBinder token) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void bootAnimationComplete() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void registerTaskStackListener(ITaskStackListener listener) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void unregisterTaskStackListener(ITaskStackListener listener) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void notifyCleartextNetwork(int uid, byte[] firstPacket) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setTaskResizeable(int taskId, int resizeableMode) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void resizeTask(int taskId, Rect bounds, int resizeMode) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int getLockTaskModeState() throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void setDumpHeapDebugLimit(String processName, int uid, long maxMemSize, String reportPackage) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void dumpHeapFinished(String path) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void updateLockTaskPackages(int userId, String[] packages) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void noteAlarmStart(IIntentSender sender, WorkSource workSource, int sourceUid, String tag) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void noteAlarmFinish(IIntentSender sender, WorkSource workSource, int sourceUid, String tag) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int getPackageProcessState(String packageName, String callingPackage) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public boolean startBinderTracking() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean stopBinderTrackingAndDump(ParcelFileDescriptor fd) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void suppressResizeConfigChanges(boolean suppress) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean unlockUser(int userid, byte[] token, byte[] secret, IProgressListener listener) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean unlockUser2(int userId, IProgressListener listener) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void killPackageDependents(String packageName, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void makePackageIdle(String packageName, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int getMemoryTrimLevel() throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public boolean isVrModePackageEnabled(ComponentName packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void notifyLockedProfile(int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void startConfirmDeviceCredentialIntent(Intent intent, Bundle options) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void sendIdleJobTrigger() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int sendIntentSender(IApplicationThread caller, IIntentSender target, IBinder whitelistToken, int code, Intent intent, String resolvedType, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public boolean isBackgroundRestricted(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void setRenderThread(int tid) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setHasTopUi(boolean hasTopUi) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public int restartUserInBackground(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void cancelTaskWindowTransition(int taskId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void scheduleApplicationInfoChanged(List<String> packageNames, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setPersistentVrThread(int tid) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void waitForNetworkStateUpdate(long procStateSeq) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void backgroundAllowlistUid(int uid) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean startUserInBackgroundWithListener(int userid, IProgressListener unlockProgressListener) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void startDelegateShellPermissionIdentity(int uid, String[] permissions) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void stopDelegateShellPermissionIdentity() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public List<String> getDelegatedShellPermissions() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public ParcelFileDescriptor getLifeMonitor() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public boolean startUserInForegroundWithListener(int userid, IProgressListener unlockProgressListener) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void appNotResponding(String reason) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public ParceledListSlice<ApplicationStartInfo> getHistoricalProcessStartReasons(String packageName, int maxNum, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void setApplicationStartInfoCompleteListener(IApplicationStartInfoCompleteListener listener, int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void removeApplicationStartInfoCompleteListener(int userId) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public ParceledListSlice<ApplicationExitInfo> getHistoricalProcessExitReasons(String packageName, int pid, int maxNum, int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public void killProcessesWhenImperceptible(int[] pids, String reason) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setActivityLocusContext(ComponentName activity, LocusId locusId, IBinder appToken) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void setProcessStateSummary(byte[] state) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean isAppFreezerSupported() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean isAppFreezerEnabled() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void killUidForPermissionChange(int appId, int userId, String reason) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void resetAppErrors() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean enableAppFreezer(boolean enable) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean enableFgsNotificationRateLimit(boolean enable) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void holdLock(IBinder token, int durationMs) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean startProfile(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean stopProfile(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public ParceledListSlice queryIntentComponentsForIntentSender(IIntentSender sender, int matchFlags) throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public int getUidProcessCapabilities(int uid, String callingPackage) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public void waitForBroadcastIdle() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void waitForBroadcastBarrier() throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void forceDelayBroadcastDelivery(String targetPackage, long delayedDurationMs) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public boolean isModernBroadcastQueueEnabled() throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean isProcessFrozen(int pid) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public int getBackgroundRestrictionExemptionReason(int uid) throws RemoteException {
            return 0;
        }

        @Override // android.app.IActivityManager
        public boolean startUserInBackgroundVisibleOnDisplay(int userid, int displayId, IProgressListener unlockProgressListener) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public boolean startProfileWithListener(int userid, IProgressListener unlockProgressListener) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public int[] getDisplayIdsForStartingVisibleBackgroundUsers() throws RemoteException {
            return null;
        }

        @Override // android.app.IActivityManager
        public boolean shouldServiceTimeOut(ComponentName className, IBinder token) throws RemoteException {
            return false;
        }

        @Override // android.app.IActivityManager
        public void logFgsApiBegin(int apiType, int appUid, int appPid) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void logFgsApiEnd(int apiType, int appUid, int appPid) throws RemoteException {
        }

        @Override // android.app.IActivityManager
        public void logFgsApiStateChanged(int apiType, int state, int appUid, int appPid) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IActivityManager {
        public static final String DESCRIPTOR = "android.app.IActivityManager";
        static final int TRANSACTION_addInstrumentationResults = 39;
        static final int TRANSACTION_addPackageDependency = 89;
        static final int TRANSACTION_appNotResponding = 210;
        static final int TRANSACTION_appNotRespondingViaProvider = 160;
        static final int TRANSACTION_attachApplication = 19;
        static final int TRANSACTION_backgroundAllowlistUid = 203;
        static final int TRANSACTION_backupAgentCreated = 86;
        static final int TRANSACTION_bindBackupAgent = 85;
        static final int TRANSACTION_bindService = 30;
        static final int TRANSACTION_bindServiceInstance = 31;
        static final int TRANSACTION_bootAnimationComplete = 169;
        static final int TRANSACTION_broadcastIntent = 15;
        static final int TRANSACTION_broadcastIntentWithFeature = 16;
        static final int TRANSACTION_cancelIntentSender = 58;
        static final int TRANSACTION_cancelTaskWindowTransition = 199;
        static final int TRANSACTION_checkPermission = 6;
        static final int TRANSACTION_checkUriPermission = 47;
        static final int TRANSACTION_checkUriPermissions = 48;
        static final int TRANSACTION_clearApplicationUserData = 73;
        static final int TRANSACTION_closeSystemDialogs = 91;
        static final int TRANSACTION_crashApplicationWithType = 102;
        static final int TRANSACTION_crashApplicationWithTypeWithExtras = 103;
        static final int TRANSACTION_dumpHeap = 105;
        static final int TRANSACTION_dumpHeapFinished = 177;
        static final int TRANSACTION_enableAppFreezer = 222;
        static final int TRANSACTION_enableFgsNotificationRateLimit = 223;
        static final int TRANSACTION_enterSafeMode = 62;
        static final int TRANSACTION_finishActivity = 11;
        static final int TRANSACTION_finishAttachApplication = 20;
        static final int TRANSACTION_finishHeavyWeightApp = 98;
        static final int TRANSACTION_finishInstrumentation = 40;
        static final int TRANSACTION_finishReceiver = 18;
        static final int TRANSACTION_forceDelayBroadcastDelivery = 231;
        static final int TRANSACTION_forceStopPackage = 76;
        static final int TRANSACTION_getAllRootTaskInfos = 154;
        static final int TRANSACTION_getBackgroundRestrictionExemptionReason = 234;
        static final int TRANSACTION_getBugreportWhitelistedPackages = 148;
        static final int TRANSACTION_getConfiguration = 41;
        static final int TRANSACTION_getContentProvider = 24;
        static final int TRANSACTION_getContentProviderExternal = 121;
        static final int TRANSACTION_getCurrentUser = 126;
        static final int TRANSACTION_getCurrentUserId = 127;
        static final int TRANSACTION_getDelegatedShellPermissions = 207;
        static final int TRANSACTION_getDisplayIdsForStartingVisibleBackgroundUsers = 237;
        static final int TRANSACTION_getFocusedRootTaskInfo = 157;
        static final int TRANSACTION_getForegroundServiceType = 69;
        static final int TRANSACTION_getHistoricalProcessExitReasons = 214;
        static final int TRANSACTION_getHistoricalProcessStartReasons = 211;
        static final int TRANSACTION_getInfoForIntentSender = 59;
        static final int TRANSACTION_getIntentForIntentSender = 149;
        static final int TRANSACTION_getIntentSender = 56;
        static final int TRANSACTION_getIntentSenderWithFeature = 57;
        static final int TRANSACTION_getLaunchedFromPackage = 150;
        static final int TRANSACTION_getLaunchedFromUid = 128;
        static final int TRANSACTION_getLifeMonitor = 208;
        static final int TRANSACTION_getLockTaskModeState = 175;
        static final int TRANSACTION_getMemoryInfo = 71;
        static final int TRANSACTION_getMemoryTrimLevel = 189;
        static final int TRANSACTION_getMimeTypeFilterAsync = 104;
        static final int TRANSACTION_getMyMemoryState = 124;
        static final int TRANSACTION_getPackageProcessState = 181;
        static final int TRANSACTION_getProcessLimit = 46;
        static final int TRANSACTION_getProcessMemoryInfo = 92;
        static final int TRANSACTION_getProcessPss = 118;
        static final int TRANSACTION_getProcessesInErrorState = 72;
        static final int TRANSACTION_getRecentTasks = 54;
        static final int TRANSACTION_getRunningAppProcesses = 79;
        static final int TRANSACTION_getRunningExternalApplications = 97;
        static final int TRANSACTION_getRunningServiceControlPanel = 27;
        static final int TRANSACTION_getRunningUserIds = 137;
        static final int TRANSACTION_getServices = 78;
        static final int TRANSACTION_getSwitchingFromUserMessage = 109;
        static final int TRANSACTION_getSwitchingToUserMessage = 110;
        static final int TRANSACTION_getTagForIntentSender = 163;
        static final int TRANSACTION_getTaskBounds = 161;
        static final int TRANSACTION_getTaskForActivity = 23;
        static final int TRANSACTION_getTasks = 21;
        static final int TRANSACTION_getUidProcessCapabilities = 228;
        static final int TRANSACTION_getUidProcessState = 5;
        static final int TRANSACTION_grantUriPermission = 49;
        static final int TRANSACTION_handleApplicationCrash = 7;
        static final int TRANSACTION_handleApplicationStrictModeViolation = 99;
        static final int TRANSACTION_handleApplicationWtf = 94;
        static final int TRANSACTION_handleIncomingUser = 88;
        static final int TRANSACTION_hang = 153;
        static final int TRANSACTION_holdLock = 224;
        static final int TRANSACTION_isAppFreezerEnabled = 219;
        static final int TRANSACTION_isAppFreezerSupported = 218;
        static final int TRANSACTION_isBackgroundRestricted = 195;
        static final int TRANSACTION_isInLockTaskMode = 165;
        static final int TRANSACTION_isIntentSenderAnActivity = 130;
        static final int TRANSACTION_isIntentSenderTargetedToPackage = 115;
        static final int TRANSACTION_isModernBroadcastQueueEnabled = 232;
        static final int TRANSACTION_isProcessFrozen = 233;
        static final int TRANSACTION_isTopActivityImmersive = 101;
        static final int TRANSACTION_isTopOfTask = 168;
        static final int TRANSACTION_isUidActive = 4;
        static final int TRANSACTION_isUserAMonkey = 96;
        static final int TRANSACTION_isUserRunning = 106;
        static final int TRANSACTION_isVrModePackageEnabled = 190;
        static final int TRANSACTION_killAllBackgroundProcesses = 120;
        static final int TRANSACTION_killApplication = 90;
        static final int TRANSACTION_killApplicationProcess = 93;
        static final int TRANSACTION_killBackgroundProcesses = 95;
        static final int TRANSACTION_killPackageDependents = 187;
        static final int TRANSACTION_killPids = 77;
        static final int TRANSACTION_killProcessesBelowForeground = 125;
        static final int TRANSACTION_killProcessesWhenImperceptible = 215;
        static final int TRANSACTION_killUid = 151;
        static final int TRANSACTION_killUidForPermissionChange = 220;
        static final int TRANSACTION_launchBugReportHandlerApp = 147;
        static final int TRANSACTION_logFgsApiBegin = 239;
        static final int TRANSACTION_logFgsApiEnd = 240;
        static final int TRANSACTION_logFgsApiStateChanged = 241;
        static final int TRANSACTION_makePackageIdle = 188;
        static final int TRANSACTION_moveActivityTaskToBack = 70;
        static final int TRANSACTION_moveTaskToFront = 22;
        static final int TRANSACTION_moveTaskToRootTask = 155;
        static final int TRANSACTION_noteAlarmFinish = 180;
        static final int TRANSACTION_noteAlarmStart = 179;
        static final int TRANSACTION_noteWakeupAlarm = 63;
        static final int TRANSACTION_notifyCleartextNetwork = 172;
        static final int TRANSACTION_notifyLockedProfile = 191;
        static final int TRANSACTION_openContentUri = 1;
        static final int TRANSACTION_peekService = 80;
        static final int TRANSACTION_performIdleMaintenance = 159;
        static final int TRANSACTION_profileControl = 81;
        static final int TRANSACTION_publishContentProviders = 25;
        static final int TRANSACTION_publishService = 34;
        static final int TRANSACTION_queryIntentComponentsForIntentSender = 227;
        static final int TRANSACTION_refContentProvider = 26;
        static final int TRANSACTION_registerForegroundServiceObserver = 75;
        static final int TRANSACTION_registerIntentSenderCancelListenerEx = 60;
        static final int TRANSACTION_registerProcessObserver = 113;
        static final int TRANSACTION_registerReceiver = 12;
        static final int TRANSACTION_registerReceiverWithFeature = 13;
        static final int TRANSACTION_registerStrictModeCallback = 100;
        static final int TRANSACTION_registerTaskStackListener = 170;
        static final int TRANSACTION_registerUidObserver = 2;
        static final int TRANSACTION_registerUserSwitchObserver = 135;
        static final int TRANSACTION_removeApplicationStartInfoCompleteListener = 213;
        static final int TRANSACTION_removeContentProvider = 64;
        static final int TRANSACTION_removeContentProviderExternal = 122;
        static final int TRANSACTION_removeContentProviderExternalAsUser = 123;
        static final int TRANSACTION_removeTask = 112;
        static final int TRANSACTION_requestBugReport = 139;
        static final int TRANSACTION_requestBugReportWithDescription = 140;
        static final int TRANSACTION_requestFullBugReport = 145;
        static final int TRANSACTION_requestInteractiveBugReport = 144;
        static final int TRANSACTION_requestInteractiveBugReportWithDescription = 143;
        static final int TRANSACTION_requestRemoteBugReport = 146;
        static final int TRANSACTION_requestSystemServerHeapDump = 138;
        static final int TRANSACTION_requestTelephonyBugReport = 141;
        static final int TRANSACTION_requestWifiBugReport = 142;
        static final int TRANSACTION_resetAppErrors = 221;
        static final int TRANSACTION_resizeTask = 174;
        static final int TRANSACTION_restart = 158;
        static final int TRANSACTION_restartUserInBackground = 198;
        static final int TRANSACTION_resumeAppSwitches = 84;
        static final int TRANSACTION_revokeUriPermission = 50;
        static final int TRANSACTION_scheduleApplicationInfoChanged = 200;
        static final int TRANSACTION_sendIdleJobTrigger = 193;
        static final int TRANSACTION_sendIntentSender = 194;
        static final int TRANSACTION_serviceDoneExecuting = 55;
        static final int TRANSACTION_setActivityController = 51;
        static final int TRANSACTION_setActivityLocusContext = 216;
        static final int TRANSACTION_setAgentApp = 36;
        static final int TRANSACTION_setAlwaysFinish = 37;
        static final int TRANSACTION_setApplicationStartInfoCompleteListener = 212;
        static final int TRANSACTION_setDebugApp = 35;
        static final int TRANSACTION_setDumpHeapDebugLimit = 176;
        static final int TRANSACTION_setFocusedRootTask = 156;
        static final int TRANSACTION_setHasTopUi = 197;
        static final int TRANSACTION_setPackageScreenCompatMode = 107;
        static final int TRANSACTION_setPersistentVrThread = 201;
        static final int TRANSACTION_setProcessImportant = 67;
        static final int TRANSACTION_setProcessLimit = 45;
        static final int TRANSACTION_setProcessMemoryTrimLevel = 162;
        static final int TRANSACTION_setProcessStateSummary = 217;
        static final int TRANSACTION_setRenderThread = 196;
        static final int TRANSACTION_setRequestedOrientation = 65;
        static final int TRANSACTION_setServiceForeground = 68;
        static final int TRANSACTION_setStopUserOnSwitch = 111;
        static final int TRANSACTION_setTaskResizeable = 173;
        static final int TRANSACTION_setUserIsMonkey = 152;
        static final int TRANSACTION_shouldServiceTimeOut = 238;
        static final int TRANSACTION_showBootMessage = 119;
        static final int TRANSACTION_showWaitingForDebugger = 52;
        static final int TRANSACTION_shutdown = 82;
        static final int TRANSACTION_signalPersistentProcesses = 53;
        static final int TRANSACTION_startActivity = 8;
        static final int TRANSACTION_startActivityAsUser = 131;
        static final int TRANSACTION_startActivityAsUserWithFeature = 132;
        static final int TRANSACTION_startActivityFromRecents = 166;
        static final int TRANSACTION_startActivityWithFeature = 9;
        static final int TRANSACTION_startBinderTracking = 182;
        static final int TRANSACTION_startConfirmDeviceCredentialIntent = 192;
        static final int TRANSACTION_startDelegateShellPermissionIdentity = 205;
        static final int TRANSACTION_startInstrumentation = 38;
        static final int TRANSACTION_startProfile = 225;
        static final int TRANSACTION_startProfileWithListener = 236;
        static final int TRANSACTION_startService = 28;
        static final int TRANSACTION_startSystemLockTaskMode = 167;
        static final int TRANSACTION_startUserInBackground = 164;
        static final int TRANSACTION_startUserInBackgroundVisibleOnDisplay = 235;
        static final int TRANSACTION_startUserInBackgroundWithListener = 204;
        static final int TRANSACTION_startUserInForegroundWithListener = 209;
        static final int TRANSACTION_stopAppForUser = 74;
        static final int TRANSACTION_stopAppSwitches = 83;
        static final int TRANSACTION_stopBinderTrackingAndDump = 183;
        static final int TRANSACTION_stopDelegateShellPermissionIdentity = 206;
        static final int TRANSACTION_stopProfile = 226;
        static final int TRANSACTION_stopService = 29;
        static final int TRANSACTION_stopServiceToken = 44;
        static final int TRANSACTION_stopUser = 133;
        static final int TRANSACTION_stopUserWithDelayedLocking = 134;
        static final int TRANSACTION_suppressResizeConfigChanges = 184;
        static final int TRANSACTION_switchUser = 108;
        static final int TRANSACTION_unbindBackupAgent = 87;
        static final int TRANSACTION_unbindFinished = 66;
        static final int TRANSACTION_unbindService = 33;
        static final int TRANSACTION_unbroadcastIntent = 17;
        static final int TRANSACTION_unhandledBack = 10;
        static final int TRANSACTION_unlockUser = 185;
        static final int TRANSACTION_unlockUser2 = 186;
        static final int TRANSACTION_unregisterIntentSenderCancelListener = 61;
        static final int TRANSACTION_unregisterProcessObserver = 114;
        static final int TRANSACTION_unregisterReceiver = 14;
        static final int TRANSACTION_unregisterTaskStackListener = 171;
        static final int TRANSACTION_unregisterUidObserver = 3;
        static final int TRANSACTION_unregisterUserSwitchObserver = 136;
        static final int TRANSACTION_unstableProviderDied = 129;
        static final int TRANSACTION_updateConfiguration = 42;
        static final int TRANSACTION_updateLockTaskPackages = 178;
        static final int TRANSACTION_updateMccMncConfiguration = 43;
        static final int TRANSACTION_updatePersistentConfiguration = 116;
        static final int TRANSACTION_updatePersistentConfigurationWithAttribution = 117;
        static final int TRANSACTION_updateServiceGroup = 32;
        static final int TRANSACTION_waitForBroadcastBarrier = 230;
        static final int TRANSACTION_waitForBroadcastIdle = 229;
        static final int TRANSACTION_waitForNetworkStateUpdate = 202;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IActivityManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IActivityManager)) {
                return (IActivityManager) iin;
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
                    return "openContentUri";
                case 2:
                    return "registerUidObserver";
                case 3:
                    return "unregisterUidObserver";
                case 4:
                    return "isUidActive";
                case 5:
                    return "getUidProcessState";
                case 6:
                    return "checkPermission";
                case 7:
                    return "handleApplicationCrash";
                case 8:
                    return "startActivity";
                case 9:
                    return "startActivityWithFeature";
                case 10:
                    return "unhandledBack";
                case 11:
                    return "finishActivity";
                case 12:
                    return "registerReceiver";
                case 13:
                    return "registerReceiverWithFeature";
                case 14:
                    return "unregisterReceiver";
                case 15:
                    return "broadcastIntent";
                case 16:
                    return "broadcastIntentWithFeature";
                case 17:
                    return "unbroadcastIntent";
                case 18:
                    return "finishReceiver";
                case 19:
                    return "attachApplication";
                case 20:
                    return "finishAttachApplication";
                case 21:
                    return "getTasks";
                case 22:
                    return "moveTaskToFront";
                case 23:
                    return "getTaskForActivity";
                case 24:
                    return "getContentProvider";
                case 25:
                    return "publishContentProviders";
                case 26:
                    return "refContentProvider";
                case 27:
                    return "getRunningServiceControlPanel";
                case 28:
                    return "startService";
                case 29:
                    return "stopService";
                case 30:
                    return "bindService";
                case 31:
                    return "bindServiceInstance";
                case 32:
                    return "updateServiceGroup";
                case 33:
                    return "unbindService";
                case 34:
                    return "publishService";
                case 35:
                    return "setDebugApp";
                case 36:
                    return "setAgentApp";
                case 37:
                    return "setAlwaysFinish";
                case 38:
                    return "startInstrumentation";
                case 39:
                    return "addInstrumentationResults";
                case 40:
                    return "finishInstrumentation";
                case 41:
                    return "getConfiguration";
                case 42:
                    return "updateConfiguration";
                case 43:
                    return "updateMccMncConfiguration";
                case 44:
                    return "stopServiceToken";
                case 45:
                    return "setProcessLimit";
                case 46:
                    return "getProcessLimit";
                case 47:
                    return "checkUriPermission";
                case 48:
                    return "checkUriPermissions";
                case 49:
                    return "grantUriPermission";
                case 50:
                    return "revokeUriPermission";
                case 51:
                    return "setActivityController";
                case 52:
                    return "showWaitingForDebugger";
                case 53:
                    return "signalPersistentProcesses";
                case 54:
                    return "getRecentTasks";
                case 55:
                    return "serviceDoneExecuting";
                case 56:
                    return "getIntentSender";
                case 57:
                    return "getIntentSenderWithFeature";
                case 58:
                    return "cancelIntentSender";
                case 59:
                    return "getInfoForIntentSender";
                case 60:
                    return "registerIntentSenderCancelListenerEx";
                case 61:
                    return "unregisterIntentSenderCancelListener";
                case 62:
                    return "enterSafeMode";
                case 63:
                    return "noteWakeupAlarm";
                case 64:
                    return "removeContentProvider";
                case 65:
                    return "setRequestedOrientation";
                case 66:
                    return "unbindFinished";
                case 67:
                    return "setProcessImportant";
                case 68:
                    return "setServiceForeground";
                case 69:
                    return "getForegroundServiceType";
                case 70:
                    return "moveActivityTaskToBack";
                case 71:
                    return "getMemoryInfo";
                case 72:
                    return "getProcessesInErrorState";
                case 73:
                    return "clearApplicationUserData";
                case 74:
                    return "stopAppForUser";
                case 75:
                    return "registerForegroundServiceObserver";
                case 76:
                    return "forceStopPackage";
                case 77:
                    return "killPids";
                case 78:
                    return "getServices";
                case 79:
                    return "getRunningAppProcesses";
                case 80:
                    return "peekService";
                case 81:
                    return "profileControl";
                case 82:
                    return "shutdown";
                case 83:
                    return "stopAppSwitches";
                case 84:
                    return "resumeAppSwitches";
                case 85:
                    return "bindBackupAgent";
                case 86:
                    return "backupAgentCreated";
                case 87:
                    return "unbindBackupAgent";
                case 88:
                    return "handleIncomingUser";
                case 89:
                    return "addPackageDependency";
                case 90:
                    return "killApplication";
                case 91:
                    return "closeSystemDialogs";
                case 92:
                    return "getProcessMemoryInfo";
                case 93:
                    return "killApplicationProcess";
                case 94:
                    return "handleApplicationWtf";
                case 95:
                    return "killBackgroundProcesses";
                case 96:
                    return "isUserAMonkey";
                case 97:
                    return "getRunningExternalApplications";
                case 98:
                    return "finishHeavyWeightApp";
                case 99:
                    return "handleApplicationStrictModeViolation";
                case 100:
                    return "registerStrictModeCallback";
                case 101:
                    return "isTopActivityImmersive";
                case 102:
                    return "crashApplicationWithType";
                case 103:
                    return "crashApplicationWithTypeWithExtras";
                case 104:
                    return "getMimeTypeFilterAsync";
                case 105:
                    return "dumpHeap";
                case 106:
                    return "isUserRunning";
                case 107:
                    return "setPackageScreenCompatMode";
                case 108:
                    return "switchUser";
                case 109:
                    return "getSwitchingFromUserMessage";
                case 110:
                    return "getSwitchingToUserMessage";
                case 111:
                    return "setStopUserOnSwitch";
                case 112:
                    return "removeTask";
                case 113:
                    return "registerProcessObserver";
                case 114:
                    return "unregisterProcessObserver";
                case 115:
                    return "isIntentSenderTargetedToPackage";
                case 116:
                    return "updatePersistentConfiguration";
                case 117:
                    return "updatePersistentConfigurationWithAttribution";
                case 118:
                    return "getProcessPss";
                case 119:
                    return "showBootMessage";
                case 120:
                    return "killAllBackgroundProcesses";
                case 121:
                    return "getContentProviderExternal";
                case 122:
                    return "removeContentProviderExternal";
                case 123:
                    return "removeContentProviderExternalAsUser";
                case 124:
                    return "getMyMemoryState";
                case 125:
                    return "killProcessesBelowForeground";
                case 126:
                    return "getCurrentUser";
                case 127:
                    return "getCurrentUserId";
                case 128:
                    return "getLaunchedFromUid";
                case 129:
                    return "unstableProviderDied";
                case 130:
                    return "isIntentSenderAnActivity";
                case 131:
                    return "startActivityAsUser";
                case 132:
                    return "startActivityAsUserWithFeature";
                case 133:
                    return "stopUser";
                case 134:
                    return "stopUserWithDelayedLocking";
                case 135:
                    return "registerUserSwitchObserver";
                case 136:
                    return "unregisterUserSwitchObserver";
                case 137:
                    return "getRunningUserIds";
                case 138:
                    return "requestSystemServerHeapDump";
                case 139:
                    return "requestBugReport";
                case 140:
                    return "requestBugReportWithDescription";
                case 141:
                    return "requestTelephonyBugReport";
                case 142:
                    return "requestWifiBugReport";
                case 143:
                    return "requestInteractiveBugReportWithDescription";
                case 144:
                    return "requestInteractiveBugReport";
                case 145:
                    return "requestFullBugReport";
                case 146:
                    return "requestRemoteBugReport";
                case 147:
                    return "launchBugReportHandlerApp";
                case 148:
                    return "getBugreportWhitelistedPackages";
                case 149:
                    return "getIntentForIntentSender";
                case 150:
                    return "getLaunchedFromPackage";
                case 151:
                    return "killUid";
                case 152:
                    return "setUserIsMonkey";
                case 153:
                    return "hang";
                case 154:
                    return "getAllRootTaskInfos";
                case 155:
                    return "moveTaskToRootTask";
                case 156:
                    return "setFocusedRootTask";
                case 157:
                    return "getFocusedRootTaskInfo";
                case 158:
                    return "restart";
                case 159:
                    return "performIdleMaintenance";
                case 160:
                    return "appNotRespondingViaProvider";
                case 161:
                    return "getTaskBounds";
                case 162:
                    return "setProcessMemoryTrimLevel";
                case 163:
                    return "getTagForIntentSender";
                case 164:
                    return "startUserInBackground";
                case 165:
                    return "isInLockTaskMode";
                case 166:
                    return "startActivityFromRecents";
                case 167:
                    return "startSystemLockTaskMode";
                case 168:
                    return "isTopOfTask";
                case 169:
                    return "bootAnimationComplete";
                case 170:
                    return "registerTaskStackListener";
                case 171:
                    return "unregisterTaskStackListener";
                case 172:
                    return "notifyCleartextNetwork";
                case 173:
                    return "setTaskResizeable";
                case 174:
                    return "resizeTask";
                case 175:
                    return "getLockTaskModeState";
                case 176:
                    return "setDumpHeapDebugLimit";
                case 177:
                    return "dumpHeapFinished";
                case 178:
                    return "updateLockTaskPackages";
                case 179:
                    return "noteAlarmStart";
                case 180:
                    return "noteAlarmFinish";
                case 181:
                    return "getPackageProcessState";
                case 182:
                    return "startBinderTracking";
                case 183:
                    return "stopBinderTrackingAndDump";
                case 184:
                    return "suppressResizeConfigChanges";
                case 185:
                    return "unlockUser";
                case 186:
                    return "unlockUser2";
                case 187:
                    return "killPackageDependents";
                case 188:
                    return "makePackageIdle";
                case 189:
                    return "getMemoryTrimLevel";
                case 190:
                    return "isVrModePackageEnabled";
                case 191:
                    return "notifyLockedProfile";
                case 192:
                    return "startConfirmDeviceCredentialIntent";
                case 193:
                    return "sendIdleJobTrigger";
                case 194:
                    return "sendIntentSender";
                case 195:
                    return "isBackgroundRestricted";
                case 196:
                    return "setRenderThread";
                case 197:
                    return "setHasTopUi";
                case 198:
                    return "restartUserInBackground";
                case 199:
                    return "cancelTaskWindowTransition";
                case 200:
                    return "scheduleApplicationInfoChanged";
                case 201:
                    return "setPersistentVrThread";
                case 202:
                    return "waitForNetworkStateUpdate";
                case 203:
                    return "backgroundAllowlistUid";
                case 204:
                    return "startUserInBackgroundWithListener";
                case 205:
                    return "startDelegateShellPermissionIdentity";
                case 206:
                    return "stopDelegateShellPermissionIdentity";
                case 207:
                    return "getDelegatedShellPermissions";
                case 208:
                    return "getLifeMonitor";
                case 209:
                    return "startUserInForegroundWithListener";
                case 210:
                    return "appNotResponding";
                case 211:
                    return "getHistoricalProcessStartReasons";
                case 212:
                    return "setApplicationStartInfoCompleteListener";
                case 213:
                    return "removeApplicationStartInfoCompleteListener";
                case 214:
                    return "getHistoricalProcessExitReasons";
                case 215:
                    return "killProcessesWhenImperceptible";
                case 216:
                    return "setActivityLocusContext";
                case 217:
                    return "setProcessStateSummary";
                case 218:
                    return "isAppFreezerSupported";
                case 219:
                    return "isAppFreezerEnabled";
                case 220:
                    return "killUidForPermissionChange";
                case 221:
                    return "resetAppErrors";
                case 222:
                    return "enableAppFreezer";
                case 223:
                    return "enableFgsNotificationRateLimit";
                case 224:
                    return "holdLock";
                case 225:
                    return "startProfile";
                case 226:
                    return "stopProfile";
                case 227:
                    return "queryIntentComponentsForIntentSender";
                case 228:
                    return "getUidProcessCapabilities";
                case 229:
                    return "waitForBroadcastIdle";
                case 230:
                    return "waitForBroadcastBarrier";
                case 231:
                    return "forceDelayBroadcastDelivery";
                case 232:
                    return "isModernBroadcastQueueEnabled";
                case 233:
                    return "isProcessFrozen";
                case 234:
                    return "getBackgroundRestrictionExemptionReason";
                case 235:
                    return "startUserInBackgroundVisibleOnDisplay";
                case 236:
                    return "startProfileWithListener";
                case 237:
                    return "getDisplayIdsForStartingVisibleBackgroundUsers";
                case 238:
                    return "shouldServiceTimeOut";
                case 239:
                    return "logFgsApiBegin";
                case 240:
                    return "logFgsApiEnd";
                case 241:
                    return "logFgsApiStateChanged";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result = openContentUri(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            return true;
                        case 2:
                            IUidObserver _arg02 = IUidObserver.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            registerUidObserver(_arg02, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            return true;
                        case 3:
                            IUidObserver _arg03 = IUidObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterUidObserver(_arg03);
                            reply.writeNoException();
                            return true;
                        case 4:
                            int _arg04 = data.readInt();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result2 = isUidActive(_arg04, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            return true;
                        case 5:
                            int _arg05 = data.readInt();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            int _result3 = getUidProcessState(_arg05, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            return true;
                        case 6:
                            String _arg06 = data.readString();
                            int _arg14 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result4 = checkPermission(_arg06, _arg14, _arg22);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            return true;
                        case 7:
                            IBinder _arg07 = data.readStrongBinder();
                            ApplicationErrorReport.ParcelableCrashInfo _arg15 = (ApplicationErrorReport.ParcelableCrashInfo) data.readTypedObject(ApplicationErrorReport.ParcelableCrashInfo.CREATOR);
                            data.enforceNoDataAvail();
                            handleApplicationCrash(_arg07, _arg15);
                            reply.writeNoException();
                            return true;
                        case 8:
                            IApplicationThread _arg08 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg16 = data.readString();
                            Intent _arg23 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg32 = data.readString();
                            IBinder _arg4 = data.readStrongBinder();
                            String _arg5 = data.readString();
                            int _arg6 = data.readInt();
                            int _arg7 = data.readInt();
                            ProfilerInfo _arg8 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            Bundle _arg9 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result5 = startActivity(_arg08, _arg16, _arg23, _arg32, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            return true;
                        case 9:
                            IApplicationThread _arg09 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg17 = data.readString();
                            String _arg24 = data.readString();
                            Intent _arg33 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg42 = data.readString();
                            IBinder _arg52 = data.readStrongBinder();
                            String _arg62 = data.readString();
                            int _arg72 = data.readInt();
                            int _arg82 = data.readInt();
                            ProfilerInfo _arg92 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            Bundle _arg10 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result6 = startActivityWithFeature(_arg09, _arg17, _arg24, _arg33, _arg42, _arg52, _arg62, _arg72, _arg82, _arg92, _arg10);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            return true;
                        case 10:
                            unhandledBack();
                            reply.writeNoException();
                            return true;
                        case 11:
                            IBinder _arg010 = data.readStrongBinder();
                            int _arg18 = data.readInt();
                            Intent _arg25 = (Intent) data.readTypedObject(Intent.CREATOR);
                            int _arg34 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = finishActivity(_arg010, _arg18, _arg25, _arg34);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            return true;
                        case 12:
                            IApplicationThread _arg011 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg19 = data.readString();
                            IIntentReceiver _arg26 = IIntentReceiver.Stub.asInterface(data.readStrongBinder());
                            IntentFilter _arg35 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            String _arg43 = data.readString();
                            int _arg53 = data.readInt();
                            int _arg63 = data.readInt();
                            data.enforceNoDataAvail();
                            Intent _result8 = registerReceiver(_arg011, _arg19, _arg26, _arg35, _arg43, _arg53, _arg63);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            return true;
                        case 13:
                            IApplicationThread _arg012 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg110 = data.readString();
                            String _arg27 = data.readString();
                            String _arg36 = data.readString();
                            IIntentReceiver _arg44 = IIntentReceiver.Stub.asInterface(data.readStrongBinder());
                            IntentFilter _arg54 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            String _arg64 = data.readString();
                            int _arg73 = data.readInt();
                            int _arg83 = data.readInt();
                            data.enforceNoDataAvail();
                            Intent _result9 = registerReceiverWithFeature(_arg012, _arg110, _arg27, _arg36, _arg44, _arg54, _arg64, _arg73, _arg83);
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            return true;
                        case 14:
                            IIntentReceiver _arg013 = IIntentReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterReceiver(_arg013);
                            reply.writeNoException();
                            return true;
                        case 15:
                            IApplicationThread _arg014 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            Intent _arg111 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg28 = data.readString();
                            IIntentReceiver _arg37 = IIntentReceiver.Stub.asInterface(data.readStrongBinder());
                            int _arg45 = data.readInt();
                            String _arg55 = data.readString();
                            Bundle _arg65 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            String[] _arg74 = data.createStringArray();
                            int _arg84 = data.readInt();
                            Bundle _arg93 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            boolean _arg102 = data.readBoolean();
                            boolean _arg11 = data.readBoolean();
                            int _arg122 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result10 = broadcastIntent(_arg014, _arg111, _arg28, _arg37, _arg45, _arg55, _arg65, _arg74, _arg84, _arg93, _arg102, _arg11, _arg122);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            return true;
                        case 16:
                            IApplicationThread _arg015 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg112 = data.readString();
                            Intent _arg29 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg38 = data.readString();
                            IIntentReceiver _arg46 = IIntentReceiver.Stub.asInterface(data.readStrongBinder());
                            int _arg56 = data.readInt();
                            String _arg66 = data.readString();
                            Bundle _arg75 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            String[] _arg85 = data.createStringArray();
                            String[] _arg94 = data.createStringArray();
                            String[] _arg103 = data.createStringArray();
                            int _arg113 = data.readInt();
                            Bundle _arg123 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            boolean _arg132 = data.readBoolean();
                            boolean _arg142 = data.readBoolean();
                            int _arg152 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result11 = broadcastIntentWithFeature(_arg015, _arg112, _arg29, _arg38, _arg46, _arg56, _arg66, _arg75, _arg85, _arg94, _arg103, _arg113, _arg123, _arg132, _arg142, _arg152);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            return true;
                        case 17:
                            IApplicationThread _arg016 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            Intent _arg114 = (Intent) data.readTypedObject(Intent.CREATOR);
                            int _arg210 = data.readInt();
                            data.enforceNoDataAvail();
                            unbroadcastIntent(_arg016, _arg114, _arg210);
                            reply.writeNoException();
                            return true;
                        case 18:
                            IBinder _arg017 = data.readStrongBinder();
                            int _arg115 = data.readInt();
                            String _arg211 = data.readString();
                            Bundle _arg39 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            boolean _arg47 = data.readBoolean();
                            int _arg57 = data.readInt();
                            data.enforceNoDataAvail();
                            finishReceiver(_arg017, _arg115, _arg211, _arg39, _arg47, _arg57);
                            return true;
                        case 19:
                            IApplicationThread _arg018 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            long _arg116 = data.readLong();
                            data.enforceNoDataAvail();
                            attachApplication(_arg018, _arg116);
                            reply.writeNoException();
                            return true;
                        case 20:
                            long _arg019 = data.readLong();
                            data.enforceNoDataAvail();
                            finishAttachApplication(_arg019);
                            reply.writeNoException();
                            return true;
                        case 21:
                            int _arg020 = data.readInt();
                            data.enforceNoDataAvail();
                            List<ActivityManager.RunningTaskInfo> _result12 = getTasks(_arg020);
                            reply.writeNoException();
                            reply.writeTypedList(_result12, 1);
                            return true;
                        case 22:
                            IApplicationThread _arg021 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg117 = data.readString();
                            int _arg212 = data.readInt();
                            int _arg310 = data.readInt();
                            Bundle _arg48 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            moveTaskToFront(_arg021, _arg117, _arg212, _arg310, _arg48);
                            reply.writeNoException();
                            return true;
                        case 23:
                            IBinder _arg022 = data.readStrongBinder();
                            boolean _arg118 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result13 = getTaskForActivity(_arg022, _arg118);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            return true;
                        case 24:
                            IApplicationThread _arg023 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg119 = data.readString();
                            String _arg213 = data.readString();
                            int _arg311 = data.readInt();
                            boolean _arg49 = data.readBoolean();
                            data.enforceNoDataAvail();
                            ContentProviderHolder _result14 = getContentProvider(_arg023, _arg119, _arg213, _arg311, _arg49);
                            reply.writeNoException();
                            reply.writeTypedObject(_result14, 1);
                            return true;
                        case 25:
                            IApplicationThread _arg024 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            List<ContentProviderHolder> _arg120 = data.createTypedArrayList(ContentProviderHolder.CREATOR);
                            data.enforceNoDataAvail();
                            publishContentProviders(_arg024, _arg120);
                            reply.writeNoException();
                            return true;
                        case 26:
                            IBinder _arg025 = data.readStrongBinder();
                            int _arg121 = data.readInt();
                            int _arg214 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result15 = refContentProvider(_arg025, _arg121, _arg214);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            return true;
                        case 27:
                            ComponentName _arg026 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            PendingIntent _result16 = getRunningServiceControlPanel(_arg026);
                            reply.writeNoException();
                            reply.writeTypedObject(_result16, 1);
                            return true;
                        case 28:
                            IApplicationThread _arg027 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            Intent _arg124 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg215 = data.readString();
                            boolean _arg312 = data.readBoolean();
                            String _arg410 = data.readString();
                            String _arg58 = data.readString();
                            int _arg67 = data.readInt();
                            data.enforceNoDataAvail();
                            ComponentName _result17 = startService(_arg027, _arg124, _arg215, _arg312, _arg410, _arg58, _arg67);
                            reply.writeNoException();
                            reply.writeTypedObject(_result17, 1);
                            return true;
                        case 29:
                            IApplicationThread _arg028 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            Intent _arg125 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg216 = data.readString();
                            int _arg313 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result18 = stopService(_arg028, _arg125, _arg216, _arg313);
                            reply.writeNoException();
                            reply.writeInt(_result18);
                            return true;
                        case 30:
                            IApplicationThread _arg029 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg126 = data.readStrongBinder();
                            Intent _arg217 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg314 = data.readString();
                            IServiceConnection _arg411 = IServiceConnection.Stub.asInterface(data.readStrongBinder());
                            long _arg59 = data.readLong();
                            String _arg68 = data.readString();
                            int _arg76 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result19 = bindService(_arg029, _arg126, _arg217, _arg314, _arg411, _arg59, _arg68, _arg76);
                            reply.writeNoException();
                            reply.writeInt(_result19);
                            return true;
                        case 31:
                            IApplicationThread _arg030 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg127 = data.readStrongBinder();
                            Intent _arg218 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg315 = data.readString();
                            IServiceConnection _arg412 = IServiceConnection.Stub.asInterface(data.readStrongBinder());
                            long _arg510 = data.readLong();
                            String _arg69 = data.readString();
                            String _arg77 = data.readString();
                            int _arg86 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result20 = bindServiceInstance(_arg030, _arg127, _arg218, _arg315, _arg412, _arg510, _arg69, _arg77, _arg86);
                            reply.writeNoException();
                            reply.writeInt(_result20);
                            return true;
                        case 32:
                            IServiceConnection _arg031 = IServiceConnection.Stub.asInterface(data.readStrongBinder());
                            int _arg128 = data.readInt();
                            int _arg219 = data.readInt();
                            data.enforceNoDataAvail();
                            updateServiceGroup(_arg031, _arg128, _arg219);
                            reply.writeNoException();
                            return true;
                        case 33:
                            IServiceConnection _arg032 = IServiceConnection.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result21 = unbindService(_arg032);
                            reply.writeNoException();
                            reply.writeBoolean(_result21);
                            return true;
                        case 34:
                            IBinder _arg033 = data.readStrongBinder();
                            Intent _arg129 = (Intent) data.readTypedObject(Intent.CREATOR);
                            IBinder _arg220 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            publishService(_arg033, _arg129, _arg220);
                            reply.writeNoException();
                            return true;
                        case 35:
                            String _arg034 = data.readString();
                            boolean _arg130 = data.readBoolean();
                            boolean _arg221 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setDebugApp(_arg034, _arg130, _arg221);
                            reply.writeNoException();
                            return true;
                        case 36:
                            String _arg035 = data.readString();
                            String _arg131 = data.readString();
                            data.enforceNoDataAvail();
                            setAgentApp(_arg035, _arg131);
                            reply.writeNoException();
                            return true;
                        case 37:
                            boolean _arg036 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setAlwaysFinish(_arg036);
                            reply.writeNoException();
                            return true;
                        case 38:
                            ComponentName _arg037 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg133 = data.readString();
                            int _arg222 = data.readInt();
                            Bundle _arg316 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            IInstrumentationWatcher _arg413 = IInstrumentationWatcher.Stub.asInterface(data.readStrongBinder());
                            IUiAutomationConnection _arg511 = IUiAutomationConnection.Stub.asInterface(data.readStrongBinder());
                            int _arg610 = data.readInt();
                            String _arg78 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result22 = startInstrumentation(_arg037, _arg133, _arg222, _arg316, _arg413, _arg511, _arg610, _arg78);
                            reply.writeNoException();
                            reply.writeBoolean(_result22);
                            return true;
                        case 39:
                            IApplicationThread _arg038 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg134 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            addInstrumentationResults(_arg038, _arg134);
                            reply.writeNoException();
                            return true;
                        case 40:
                            IApplicationThread _arg039 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            int _arg135 = data.readInt();
                            Bundle _arg223 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            finishInstrumentation(_arg039, _arg135, _arg223);
                            reply.writeNoException();
                            return true;
                        case 41:
                            Configuration _result23 = getConfiguration();
                            reply.writeNoException();
                            reply.writeTypedObject(_result23, 1);
                            return true;
                        case 42:
                            Configuration _arg040 = (Configuration) data.readTypedObject(Configuration.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result24 = updateConfiguration(_arg040);
                            reply.writeNoException();
                            reply.writeBoolean(_result24);
                            return true;
                        case 43:
                            String _arg041 = data.readString();
                            String _arg136 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result25 = updateMccMncConfiguration(_arg041, _arg136);
                            reply.writeNoException();
                            reply.writeBoolean(_result25);
                            return true;
                        case 44:
                            ComponentName _arg042 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            IBinder _arg137 = data.readStrongBinder();
                            int _arg224 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result26 = stopServiceToken(_arg042, _arg137, _arg224);
                            reply.writeNoException();
                            reply.writeBoolean(_result26);
                            return true;
                        case 45:
                            int _arg043 = data.readInt();
                            data.enforceNoDataAvail();
                            setProcessLimit(_arg043);
                            reply.writeNoException();
                            return true;
                        case 46:
                            int _result27 = getProcessLimit();
                            reply.writeNoException();
                            reply.writeInt(_result27);
                            return true;
                        case 47:
                            Uri _arg044 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg138 = data.readInt();
                            int _arg225 = data.readInt();
                            int _arg317 = data.readInt();
                            int _arg414 = data.readInt();
                            IBinder _arg512 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int _result28 = checkUriPermission(_arg044, _arg138, _arg225, _arg317, _arg414, _arg512);
                            reply.writeNoException();
                            reply.writeInt(_result28);
                            return true;
                        case 48:
                            List<Uri> _arg045 = data.createTypedArrayList(Uri.CREATOR);
                            int _arg139 = data.readInt();
                            int _arg226 = data.readInt();
                            int _arg318 = data.readInt();
                            int _arg415 = data.readInt();
                            IBinder _arg513 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int[] _result29 = checkUriPermissions(_arg045, _arg139, _arg226, _arg318, _arg415, _arg513);
                            reply.writeNoException();
                            reply.writeIntArray(_result29);
                            return true;
                        case 49:
                            IApplicationThread _arg046 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg140 = data.readString();
                            Uri _arg227 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg319 = data.readInt();
                            int _arg416 = data.readInt();
                            data.enforceNoDataAvail();
                            grantUriPermission(_arg046, _arg140, _arg227, _arg319, _arg416);
                            reply.writeNoException();
                            return true;
                        case 50:
                            IApplicationThread _arg047 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg141 = data.readString();
                            Uri _arg228 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg320 = data.readInt();
                            int _arg417 = data.readInt();
                            data.enforceNoDataAvail();
                            revokeUriPermission(_arg047, _arg141, _arg228, _arg320, _arg417);
                            reply.writeNoException();
                            return true;
                        case 51:
                            IActivityController _arg048 = IActivityController.Stub.asInterface(data.readStrongBinder());
                            boolean _arg143 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setActivityController(_arg048, _arg143);
                            reply.writeNoException();
                            return true;
                        case 52:
                            IApplicationThread _arg049 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            boolean _arg144 = data.readBoolean();
                            data.enforceNoDataAvail();
                            showWaitingForDebugger(_arg049, _arg144);
                            reply.writeNoException();
                            return true;
                        case 53:
                            int _arg050 = data.readInt();
                            data.enforceNoDataAvail();
                            signalPersistentProcesses(_arg050);
                            reply.writeNoException();
                            return true;
                        case 54:
                            int _arg051 = data.readInt();
                            int _arg145 = data.readInt();
                            int _arg229 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result30 = getRecentTasks(_arg051, _arg145, _arg229);
                            reply.writeNoException();
                            reply.writeTypedObject(_result30, 1);
                            return true;
                        case 55:
                            IBinder _arg052 = data.readStrongBinder();
                            int _arg146 = data.readInt();
                            int _arg230 = data.readInt();
                            int _arg321 = data.readInt();
                            data.enforceNoDataAvail();
                            serviceDoneExecuting(_arg052, _arg146, _arg230, _arg321);
                            return true;
                        case 56:
                            int _arg053 = data.readInt();
                            String _arg147 = data.readString();
                            IBinder _arg231 = data.readStrongBinder();
                            String _arg322 = data.readString();
                            int _arg418 = data.readInt();
                            Intent[] _arg514 = (Intent[]) data.createTypedArray(Intent.CREATOR);
                            String[] _arg611 = data.createStringArray();
                            int _arg79 = data.readInt();
                            Bundle _arg87 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg95 = data.readInt();
                            data.enforceNoDataAvail();
                            IIntentSender _result31 = getIntentSender(_arg053, _arg147, _arg231, _arg322, _arg418, _arg514, _arg611, _arg79, _arg87, _arg95);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result31);
                            return true;
                        case 57:
                            int _arg054 = data.readInt();
                            String _arg148 = data.readString();
                            String _arg232 = data.readString();
                            IBinder _arg323 = data.readStrongBinder();
                            String _arg419 = data.readString();
                            int _arg515 = data.readInt();
                            Intent[] _arg612 = (Intent[]) data.createTypedArray(Intent.CREATOR);
                            String[] _arg710 = data.createStringArray();
                            int _arg88 = data.readInt();
                            Bundle _arg96 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg104 = data.readInt();
                            data.enforceNoDataAvail();
                            IIntentSender _result32 = getIntentSenderWithFeature(_arg054, _arg148, _arg232, _arg323, _arg419, _arg515, _arg612, _arg710, _arg88, _arg96, _arg104);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result32);
                            return true;
                        case 58:
                            IIntentSender _arg055 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            cancelIntentSender(_arg055);
                            reply.writeNoException();
                            return true;
                        case 59:
                            IIntentSender _arg056 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ActivityManager.PendingIntentInfo _result33 = getInfoForIntentSender(_arg056);
                            reply.writeNoException();
                            reply.writeTypedObject(_result33, 1);
                            return true;
                        case 60:
                            IIntentSender _arg057 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            IResultReceiver _arg149 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result34 = registerIntentSenderCancelListenerEx(_arg057, _arg149);
                            reply.writeNoException();
                            reply.writeBoolean(_result34);
                            return true;
                        case 61:
                            IIntentSender _arg058 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            IResultReceiver _arg150 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterIntentSenderCancelListener(_arg058, _arg150);
                            reply.writeNoException();
                            return true;
                        case 62:
                            enterSafeMode();
                            reply.writeNoException();
                            return true;
                        case 63:
                            IIntentSender _arg059 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            WorkSource _arg151 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            int _arg233 = data.readInt();
                            String _arg324 = data.readString();
                            String _arg420 = data.readString();
                            data.enforceNoDataAvail();
                            noteWakeupAlarm(_arg059, _arg151, _arg233, _arg324, _arg420);
                            reply.writeNoException();
                            return true;
                        case 64:
                            IBinder _arg060 = data.readStrongBinder();
                            boolean _arg153 = data.readBoolean();
                            data.enforceNoDataAvail();
                            removeContentProvider(_arg060, _arg153);
                            return true;
                        case 65:
                            IBinder _arg061 = data.readStrongBinder();
                            int _arg154 = data.readInt();
                            data.enforceNoDataAvail();
                            setRequestedOrientation(_arg061, _arg154);
                            reply.writeNoException();
                            return true;
                        case 66:
                            IBinder _arg062 = data.readStrongBinder();
                            Intent _arg155 = (Intent) data.readTypedObject(Intent.CREATOR);
                            boolean _arg234 = data.readBoolean();
                            data.enforceNoDataAvail();
                            unbindFinished(_arg062, _arg155, _arg234);
                            reply.writeNoException();
                            return true;
                        case 67:
                            IBinder _arg063 = data.readStrongBinder();
                            int _arg156 = data.readInt();
                            boolean _arg235 = data.readBoolean();
                            String _arg325 = data.readString();
                            data.enforceNoDataAvail();
                            setProcessImportant(_arg063, _arg156, _arg235, _arg325);
                            reply.writeNoException();
                            return true;
                        case 68:
                            ComponentName _arg064 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            IBinder _arg157 = data.readStrongBinder();
                            int _arg236 = data.readInt();
                            Notification _arg326 = (Notification) data.readTypedObject(Notification.CREATOR);
                            int _arg421 = data.readInt();
                            int _arg516 = data.readInt();
                            data.enforceNoDataAvail();
                            setServiceForeground(_arg064, _arg157, _arg236, _arg326, _arg421, _arg516);
                            reply.writeNoException();
                            return true;
                        case 69:
                            ComponentName _arg065 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            IBinder _arg158 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int _result35 = getForegroundServiceType(_arg065, _arg158);
                            reply.writeNoException();
                            reply.writeInt(_result35);
                            return true;
                        case 70:
                            IBinder _arg066 = data.readStrongBinder();
                            boolean _arg159 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result36 = moveActivityTaskToBack(_arg066, _arg159);
                            reply.writeNoException();
                            reply.writeBoolean(_result36);
                            return true;
                        case 71:
                            ActivityManager.MemoryInfo _arg067 = new ActivityManager.MemoryInfo();
                            data.enforceNoDataAvail();
                            getMemoryInfo(_arg067);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg067, 1);
                            return true;
                        case 72:
                            List<ActivityManager.ProcessErrorStateInfo> _result37 = getProcessesInErrorState();
                            reply.writeNoException();
                            reply.writeTypedList(_result37, 1);
                            return true;
                        case 73:
                            String _arg068 = data.readString();
                            boolean _arg160 = data.readBoolean();
                            IPackageDataObserver _arg237 = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
                            int _arg327 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result38 = clearApplicationUserData(_arg068, _arg160, _arg237, _arg327);
                            reply.writeNoException();
                            reply.writeBoolean(_result38);
                            return true;
                        case 74:
                            String _arg069 = data.readString();
                            int _arg161 = data.readInt();
                            data.enforceNoDataAvail();
                            stopAppForUser(_arg069, _arg161);
                            reply.writeNoException();
                            return true;
                        case 75:
                            IForegroundServiceObserver _arg070 = IForegroundServiceObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result39 = registerForegroundServiceObserver(_arg070);
                            reply.writeNoException();
                            reply.writeBoolean(_result39);
                            return true;
                        case 76:
                            String _arg071 = data.readString();
                            int _arg162 = data.readInt();
                            data.enforceNoDataAvail();
                            forceStopPackage(_arg071, _arg162);
                            reply.writeNoException();
                            return true;
                        case 77:
                            int[] _arg072 = data.createIntArray();
                            String _arg163 = data.readString();
                            boolean _arg238 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result40 = killPids(_arg072, _arg163, _arg238);
                            reply.writeNoException();
                            reply.writeBoolean(_result40);
                            return true;
                        case 78:
                            int _arg073 = data.readInt();
                            int _arg164 = data.readInt();
                            data.enforceNoDataAvail();
                            List<ActivityManager.RunningServiceInfo> _result41 = getServices(_arg073, _arg164);
                            reply.writeNoException();
                            reply.writeTypedList(_result41, 1);
                            return true;
                        case 79:
                            List<ActivityManager.RunningAppProcessInfo> _result42 = getRunningAppProcesses();
                            reply.writeNoException();
                            reply.writeTypedList(_result42, 1);
                            return true;
                        case 80:
                            Intent _arg074 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg165 = data.readString();
                            String _arg239 = data.readString();
                            data.enforceNoDataAvail();
                            IBinder _result43 = peekService(_arg074, _arg165, _arg239);
                            reply.writeNoException();
                            reply.writeStrongBinder(_result43);
                            return true;
                        case 81:
                            String _arg075 = data.readString();
                            int _arg166 = data.readInt();
                            boolean _arg240 = data.readBoolean();
                            ProfilerInfo _arg328 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            int _arg422 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result44 = profileControl(_arg075, _arg166, _arg240, _arg328, _arg422);
                            reply.writeNoException();
                            reply.writeBoolean(_result44);
                            return true;
                        case 82:
                            int _arg076 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result45 = shutdown(_arg076);
                            reply.writeNoException();
                            reply.writeBoolean(_result45);
                            return true;
                        case 83:
                            stopAppSwitches();
                            reply.writeNoException();
                            return true;
                        case 84:
                            resumeAppSwitches();
                            reply.writeNoException();
                            return true;
                        case 85:
                            String _arg077 = data.readString();
                            int _arg167 = data.readInt();
                            int _arg241 = data.readInt();
                            int _arg329 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result46 = bindBackupAgent(_arg077, _arg167, _arg241, _arg329);
                            reply.writeNoException();
                            reply.writeBoolean(_result46);
                            return true;
                        case 86:
                            String _arg078 = data.readString();
                            IBinder _arg168 = data.readStrongBinder();
                            int _arg242 = data.readInt();
                            data.enforceNoDataAvail();
                            backupAgentCreated(_arg078, _arg168, _arg242);
                            reply.writeNoException();
                            return true;
                        case 87:
                            ApplicationInfo _arg079 = (ApplicationInfo) data.readTypedObject(ApplicationInfo.CREATOR);
                            data.enforceNoDataAvail();
                            unbindBackupAgent(_arg079);
                            reply.writeNoException();
                            return true;
                        case 88:
                            int _arg080 = data.readInt();
                            int _arg169 = data.readInt();
                            int _arg243 = data.readInt();
                            boolean _arg330 = data.readBoolean();
                            boolean _arg423 = data.readBoolean();
                            String _arg517 = data.readString();
                            String _arg613 = data.readString();
                            data.enforceNoDataAvail();
                            int _result47 = handleIncomingUser(_arg080, _arg169, _arg243, _arg330, _arg423, _arg517, _arg613);
                            reply.writeNoException();
                            reply.writeInt(_result47);
                            return true;
                        case 89:
                            String _arg081 = data.readString();
                            data.enforceNoDataAvail();
                            addPackageDependency(_arg081);
                            reply.writeNoException();
                            return true;
                        case 90:
                            String _arg082 = data.readString();
                            int _arg170 = data.readInt();
                            int _arg244 = data.readInt();
                            String _arg331 = data.readString();
                            int _arg424 = data.readInt();
                            data.enforceNoDataAvail();
                            killApplication(_arg082, _arg170, _arg244, _arg331, _arg424);
                            reply.writeNoException();
                            return true;
                        case 91:
                            String _arg083 = data.readString();
                            data.enforceNoDataAvail();
                            closeSystemDialogs(_arg083);
                            reply.writeNoException();
                            return true;
                        case 92:
                            int[] _arg084 = data.createIntArray();
                            data.enforceNoDataAvail();
                            Debug.MemoryInfo[] _result48 = getProcessMemoryInfo(_arg084);
                            reply.writeNoException();
                            reply.writeTypedArray(_result48, 1);
                            return true;
                        case 93:
                            String _arg085 = data.readString();
                            int _arg171 = data.readInt();
                            data.enforceNoDataAvail();
                            killApplicationProcess(_arg085, _arg171);
                            reply.writeNoException();
                            return true;
                        case 94:
                            IBinder _arg086 = data.readStrongBinder();
                            String _arg172 = data.readString();
                            boolean _arg245 = data.readBoolean();
                            ApplicationErrorReport.ParcelableCrashInfo _arg332 = (ApplicationErrorReport.ParcelableCrashInfo) data.readTypedObject(ApplicationErrorReport.ParcelableCrashInfo.CREATOR);
                            int _arg425 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result49 = handleApplicationWtf(_arg086, _arg172, _arg245, _arg332, _arg425);
                            reply.writeNoException();
                            reply.writeBoolean(_result49);
                            return true;
                        case 95:
                            String _arg087 = data.readString();
                            int _arg173 = data.readInt();
                            data.enforceNoDataAvail();
                            killBackgroundProcesses(_arg087, _arg173);
                            reply.writeNoException();
                            return true;
                        case 96:
                            boolean _result50 = isUserAMonkey();
                            reply.writeNoException();
                            reply.writeBoolean(_result50);
                            return true;
                        case 97:
                            List<ApplicationInfo> _result51 = getRunningExternalApplications();
                            reply.writeNoException();
                            reply.writeTypedList(_result51, 1);
                            return true;
                        case 98:
                            finishHeavyWeightApp();
                            reply.writeNoException();
                            return true;
                        case 99:
                            IBinder _arg088 = data.readStrongBinder();
                            int _arg174 = data.readInt();
                            StrictMode.ViolationInfo _arg246 = (StrictMode.ViolationInfo) data.readTypedObject(StrictMode.ViolationInfo.CREATOR);
                            data.enforceNoDataAvail();
                            handleApplicationStrictModeViolation(_arg088, _arg174, _arg246);
                            reply.writeNoException();
                            return true;
                        case 100:
                            IBinder _arg089 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            registerStrictModeCallback(_arg089);
                            reply.writeNoException();
                            return true;
                        case 101:
                            boolean _result52 = isTopActivityImmersive();
                            reply.writeNoException();
                            reply.writeBoolean(_result52);
                            return true;
                        case 102:
                            int _arg090 = data.readInt();
                            int _arg175 = data.readInt();
                            String _arg247 = data.readString();
                            int _arg333 = data.readInt();
                            String _arg426 = data.readString();
                            boolean _arg518 = data.readBoolean();
                            int _arg614 = data.readInt();
                            data.enforceNoDataAvail();
                            crashApplicationWithType(_arg090, _arg175, _arg247, _arg333, _arg426, _arg518, _arg614);
                            reply.writeNoException();
                            return true;
                        case 103:
                            int _arg091 = data.readInt();
                            int _arg176 = data.readInt();
                            String _arg248 = data.readString();
                            int _arg334 = data.readInt();
                            String _arg427 = data.readString();
                            boolean _arg519 = data.readBoolean();
                            int _arg615 = data.readInt();
                            Bundle _arg711 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            crashApplicationWithTypeWithExtras(_arg091, _arg176, _arg248, _arg334, _arg427, _arg519, _arg615, _arg711);
                            reply.writeNoException();
                            return true;
                        case 104:
                            Uri _arg092 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg177 = data.readInt();
                            RemoteCallback _arg249 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            getMimeTypeFilterAsync(_arg092, _arg177, _arg249);
                            return true;
                        case 105:
                            String _arg093 = data.readString();
                            int _arg178 = data.readInt();
                            boolean _arg250 = data.readBoolean();
                            boolean _arg335 = data.readBoolean();
                            boolean _arg428 = data.readBoolean();
                            String _arg520 = data.readString();
                            ParcelFileDescriptor _arg616 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            RemoteCallback _arg712 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result53 = dumpHeap(_arg093, _arg178, _arg250, _arg335, _arg428, _arg520, _arg616, _arg712);
                            reply.writeNoException();
                            reply.writeBoolean(_result53);
                            return true;
                        case 106:
                            int _arg094 = data.readInt();
                            int _arg179 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result54 = isUserRunning(_arg094, _arg179);
                            reply.writeNoException();
                            reply.writeBoolean(_result54);
                            return true;
                        case 107:
                            String _arg095 = data.readString();
                            int _arg180 = data.readInt();
                            data.enforceNoDataAvail();
                            setPackageScreenCompatMode(_arg095, _arg180);
                            reply.writeNoException();
                            return true;
                        case 108:
                            int _arg096 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result55 = switchUser(_arg096);
                            reply.writeNoException();
                            reply.writeBoolean(_result55);
                            return true;
                        case 109:
                            String _result56 = getSwitchingFromUserMessage();
                            reply.writeNoException();
                            reply.writeString(_result56);
                            return true;
                        case 110:
                            String _result57 = getSwitchingToUserMessage();
                            reply.writeNoException();
                            reply.writeString(_result57);
                            return true;
                        case 111:
                            int _arg097 = data.readInt();
                            data.enforceNoDataAvail();
                            setStopUserOnSwitch(_arg097);
                            reply.writeNoException();
                            return true;
                        case 112:
                            int _arg098 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result58 = removeTask(_arg098);
                            reply.writeNoException();
                            reply.writeBoolean(_result58);
                            return true;
                        case 113:
                            IProcessObserver _arg099 = IProcessObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerProcessObserver(_arg099);
                            reply.writeNoException();
                            return true;
                        case 114:
                            IProcessObserver _arg0100 = IProcessObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterProcessObserver(_arg0100);
                            reply.writeNoException();
                            return true;
                        case 115:
                            IIntentSender _arg0101 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result59 = isIntentSenderTargetedToPackage(_arg0101);
                            reply.writeNoException();
                            reply.writeBoolean(_result59);
                            return true;
                        case 116:
                            Configuration _arg0102 = (Configuration) data.readTypedObject(Configuration.CREATOR);
                            data.enforceNoDataAvail();
                            updatePersistentConfiguration(_arg0102);
                            reply.writeNoException();
                            return true;
                        case 117:
                            Configuration _arg0103 = (Configuration) data.readTypedObject(Configuration.CREATOR);
                            String _arg181 = data.readString();
                            String _arg251 = data.readString();
                            data.enforceNoDataAvail();
                            updatePersistentConfigurationWithAttribution(_arg0103, _arg181, _arg251);
                            reply.writeNoException();
                            return true;
                        case 118:
                            int[] _arg0104 = data.createIntArray();
                            data.enforceNoDataAvail();
                            long[] _result60 = getProcessPss(_arg0104);
                            reply.writeNoException();
                            reply.writeLongArray(_result60);
                            return true;
                        case 119:
                            CharSequence _arg0105 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            boolean _arg182 = data.readBoolean();
                            data.enforceNoDataAvail();
                            showBootMessage(_arg0105, _arg182);
                            reply.writeNoException();
                            return true;
                        case 120:
                            killAllBackgroundProcesses();
                            reply.writeNoException();
                            return true;
                        case 121:
                            String _arg0106 = data.readString();
                            int _arg183 = data.readInt();
                            IBinder _arg252 = data.readStrongBinder();
                            String _arg336 = data.readString();
                            data.enforceNoDataAvail();
                            ContentProviderHolder _result61 = getContentProviderExternal(_arg0106, _arg183, _arg252, _arg336);
                            reply.writeNoException();
                            reply.writeTypedObject(_result61, 1);
                            return true;
                        case 122:
                            String _arg0107 = data.readString();
                            IBinder _arg184 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            removeContentProviderExternal(_arg0107, _arg184);
                            reply.writeNoException();
                            return true;
                        case 123:
                            String _arg0108 = data.readString();
                            IBinder _arg185 = data.readStrongBinder();
                            int _arg253 = data.readInt();
                            data.enforceNoDataAvail();
                            removeContentProviderExternalAsUser(_arg0108, _arg185, _arg253);
                            reply.writeNoException();
                            return true;
                        case 124:
                            ActivityManager.RunningAppProcessInfo _arg0109 = new ActivityManager.RunningAppProcessInfo();
                            data.enforceNoDataAvail();
                            getMyMemoryState(_arg0109);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg0109, 1);
                            return true;
                        case 125:
                            String _arg0110 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result62 = killProcessesBelowForeground(_arg0110);
                            reply.writeNoException();
                            reply.writeBoolean(_result62);
                            return true;
                        case 126:
                            UserInfo _result63 = getCurrentUser();
                            reply.writeNoException();
                            reply.writeTypedObject(_result63, 1);
                            return true;
                        case 127:
                            int _result64 = getCurrentUserId();
                            reply.writeNoException();
                            reply.writeInt(_result64);
                            return true;
                        case 128:
                            IBinder _arg0111 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int _result65 = getLaunchedFromUid(_arg0111);
                            reply.writeNoException();
                            reply.writeInt(_result65);
                            return true;
                        case 129:
                            IBinder _arg0112 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            unstableProviderDied(_arg0112);
                            reply.writeNoException();
                            return true;
                        case 130:
                            IIntentSender _arg0113 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result66 = isIntentSenderAnActivity(_arg0113);
                            reply.writeNoException();
                            reply.writeBoolean(_result66);
                            return true;
                        case 131:
                            IApplicationThread _arg0114 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg186 = data.readString();
                            Intent _arg254 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg337 = data.readString();
                            IBinder _arg429 = data.readStrongBinder();
                            String _arg521 = data.readString();
                            int _arg617 = data.readInt();
                            int _arg713 = data.readInt();
                            ProfilerInfo _arg89 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            Bundle _arg97 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg105 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result67 = startActivityAsUser(_arg0114, _arg186, _arg254, _arg337, _arg429, _arg521, _arg617, _arg713, _arg89, _arg97, _arg105);
                            reply.writeNoException();
                            reply.writeInt(_result67);
                            return true;
                        case 132:
                            IApplicationThread _arg0115 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg187 = data.readString();
                            String _arg255 = data.readString();
                            Intent _arg338 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg430 = data.readString();
                            IBinder _arg522 = data.readStrongBinder();
                            String _arg618 = data.readString();
                            int _arg714 = data.readInt();
                            int _arg810 = data.readInt();
                            ProfilerInfo _arg98 = (ProfilerInfo) data.readTypedObject(ProfilerInfo.CREATOR);
                            Bundle _arg106 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg1110 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result68 = startActivityAsUserWithFeature(_arg0115, _arg187, _arg255, _arg338, _arg430, _arg522, _arg618, _arg714, _arg810, _arg98, _arg106, _arg1110);
                            reply.writeNoException();
                            reply.writeInt(_result68);
                            return true;
                        case 133:
                            int _arg0116 = data.readInt();
                            boolean _arg188 = data.readBoolean();
                            IStopUserCallback _arg256 = IStopUserCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result69 = stopUser(_arg0116, _arg188, _arg256);
                            reply.writeNoException();
                            reply.writeInt(_result69);
                            return true;
                        case 134:
                            int _arg0117 = data.readInt();
                            boolean _arg189 = data.readBoolean();
                            IStopUserCallback _arg257 = IStopUserCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result70 = stopUserWithDelayedLocking(_arg0117, _arg189, _arg257);
                            reply.writeNoException();
                            reply.writeInt(_result70);
                            return true;
                        case 135:
                            IUserSwitchObserver _arg0118 = IUserSwitchObserver.Stub.asInterface(data.readStrongBinder());
                            String _arg190 = data.readString();
                            data.enforceNoDataAvail();
                            registerUserSwitchObserver(_arg0118, _arg190);
                            reply.writeNoException();
                            return true;
                        case 136:
                            IUserSwitchObserver _arg0119 = IUserSwitchObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterUserSwitchObserver(_arg0119);
                            reply.writeNoException();
                            return true;
                        case 137:
                            int[] _result71 = getRunningUserIds();
                            reply.writeNoException();
                            reply.writeIntArray(_result71);
                            return true;
                        case 138:
                            requestSystemServerHeapDump();
                            reply.writeNoException();
                            return true;
                        case 139:
                            int _arg0120 = data.readInt();
                            data.enforceNoDataAvail();
                            requestBugReport(_arg0120);
                            reply.writeNoException();
                            return true;
                        case 140:
                            String _arg0121 = data.readString();
                            String _arg191 = data.readString();
                            int _arg258 = data.readInt();
                            data.enforceNoDataAvail();
                            requestBugReportWithDescription(_arg0121, _arg191, _arg258);
                            reply.writeNoException();
                            return true;
                        case 141:
                            String _arg0122 = data.readString();
                            String _arg192 = data.readString();
                            data.enforceNoDataAvail();
                            requestTelephonyBugReport(_arg0122, _arg192);
                            reply.writeNoException();
                            return true;
                        case 142:
                            String _arg0123 = data.readString();
                            String _arg193 = data.readString();
                            data.enforceNoDataAvail();
                            requestWifiBugReport(_arg0123, _arg193);
                            reply.writeNoException();
                            return true;
                        case 143:
                            String _arg0124 = data.readString();
                            String _arg194 = data.readString();
                            data.enforceNoDataAvail();
                            requestInteractiveBugReportWithDescription(_arg0124, _arg194);
                            reply.writeNoException();
                            return true;
                        case 144:
                            requestInteractiveBugReport();
                            reply.writeNoException();
                            return true;
                        case 145:
                            requestFullBugReport();
                            reply.writeNoException();
                            return true;
                        case 146:
                            long _arg0125 = data.readLong();
                            data.enforceNoDataAvail();
                            requestRemoteBugReport(_arg0125);
                            reply.writeNoException();
                            return true;
                        case 147:
                            boolean _result72 = launchBugReportHandlerApp();
                            reply.writeNoException();
                            reply.writeBoolean(_result72);
                            return true;
                        case 148:
                            List<String> _result73 = getBugreportWhitelistedPackages();
                            reply.writeNoException();
                            reply.writeStringList(_result73);
                            return true;
                        case 149:
                            IBinder _arg0126 = data.readStrongBinder();
                            IIntentSender _arg0127 = IIntentSender.Stub.asInterface(_arg0126);
                            data.enforceNoDataAvail();
                            Intent _result74 = getIntentForIntentSender(_arg0127);
                            reply.writeNoException();
                            reply.writeTypedObject(_result74, 1);
                            return true;
                        case 150:
                            IBinder _arg0128 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            String _result75 = getLaunchedFromPackage(_arg0128);
                            reply.writeNoException();
                            reply.writeString(_result75);
                            return true;
                        case 151:
                            int _arg0129 = data.readInt();
                            int _arg195 = data.readInt();
                            String _arg259 = data.readString();
                            data.enforceNoDataAvail();
                            killUid(_arg0129, _arg195, _arg259);
                            reply.writeNoException();
                            return true;
                        case 152:
                            boolean _arg0130 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setUserIsMonkey(_arg0130);
                            reply.writeNoException();
                            return true;
                        case 153:
                            IBinder _arg0131 = data.readStrongBinder();
                            boolean _arg196 = data.readBoolean();
                            data.enforceNoDataAvail();
                            hang(_arg0131, _arg196);
                            reply.writeNoException();
                            return true;
                        case 154:
                            List<ActivityTaskManager.RootTaskInfo> _result76 = getAllRootTaskInfos();
                            reply.writeNoException();
                            reply.writeTypedList(_result76, 1);
                            return true;
                        case 155:
                            int _arg0132 = data.readInt();
                            int _arg197 = data.readInt();
                            boolean _arg260 = data.readBoolean();
                            data.enforceNoDataAvail();
                            moveTaskToRootTask(_arg0132, _arg197, _arg260);
                            reply.writeNoException();
                            return true;
                        case 156:
                            int _arg0133 = data.readInt();
                            data.enforceNoDataAvail();
                            setFocusedRootTask(_arg0133);
                            reply.writeNoException();
                            return true;
                        case 157:
                            ActivityTaskManager.RootTaskInfo _result77 = getFocusedRootTaskInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result77, 1);
                            return true;
                        case 158:
                            restart();
                            reply.writeNoException();
                            return true;
                        case 159:
                            performIdleMaintenance();
                            reply.writeNoException();
                            return true;
                        case 160:
                            IBinder _arg0134 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            appNotRespondingViaProvider(_arg0134);
                            reply.writeNoException();
                            return true;
                        case 161:
                            int _arg0135 = data.readInt();
                            data.enforceNoDataAvail();
                            Rect _result78 = getTaskBounds(_arg0135);
                            reply.writeNoException();
                            reply.writeTypedObject(_result78, 1);
                            return true;
                        case 162:
                            String _arg0136 = data.readString();
                            int _arg198 = data.readInt();
                            int _arg261 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result79 = setProcessMemoryTrimLevel(_arg0136, _arg198, _arg261);
                            reply.writeNoException();
                            reply.writeBoolean(_result79);
                            return true;
                        case 163:
                            IIntentSender _arg0137 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            String _arg199 = data.readString();
                            data.enforceNoDataAvail();
                            String _result80 = getTagForIntentSender(_arg0137, _arg199);
                            reply.writeNoException();
                            reply.writeString(_result80);
                            return true;
                        case 164:
                            int _arg0138 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result81 = startUserInBackground(_arg0138);
                            reply.writeNoException();
                            reply.writeBoolean(_result81);
                            return true;
                        case 165:
                            boolean _result82 = isInLockTaskMode();
                            reply.writeNoException();
                            reply.writeBoolean(_result82);
                            return true;
                        case 166:
                            int _arg0139 = data.readInt();
                            Bundle _arg1100 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result83 = startActivityFromRecents(_arg0139, _arg1100);
                            reply.writeNoException();
                            reply.writeInt(_result83);
                            return true;
                        case 167:
                            int _arg0140 = data.readInt();
                            data.enforceNoDataAvail();
                            startSystemLockTaskMode(_arg0140);
                            reply.writeNoException();
                            return true;
                        case 168:
                            IBinder _arg0141 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            boolean _result84 = isTopOfTask(_arg0141);
                            reply.writeNoException();
                            reply.writeBoolean(_result84);
                            return true;
                        case 169:
                            bootAnimationComplete();
                            reply.writeNoException();
                            return true;
                        case 170:
                            ITaskStackListener _arg0142 = ITaskStackListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerTaskStackListener(_arg0142);
                            reply.writeNoException();
                            return true;
                        case 171:
                            ITaskStackListener _arg0143 = ITaskStackListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterTaskStackListener(_arg0143);
                            reply.writeNoException();
                            return true;
                        case 172:
                            int _arg0144 = data.readInt();
                            byte[] _arg1101 = data.createByteArray();
                            data.enforceNoDataAvail();
                            notifyCleartextNetwork(_arg0144, _arg1101);
                            reply.writeNoException();
                            return true;
                        case 173:
                            int _arg0145 = data.readInt();
                            int _arg1102 = data.readInt();
                            data.enforceNoDataAvail();
                            setTaskResizeable(_arg0145, _arg1102);
                            reply.writeNoException();
                            return true;
                        case 174:
                            int _arg0146 = data.readInt();
                            Rect _arg1103 = (Rect) data.readTypedObject(Rect.CREATOR);
                            int _arg262 = data.readInt();
                            data.enforceNoDataAvail();
                            resizeTask(_arg0146, _arg1103, _arg262);
                            reply.writeNoException();
                            return true;
                        case 175:
                            int _result85 = getLockTaskModeState();
                            reply.writeNoException();
                            reply.writeInt(_result85);
                            return true;
                        case 176:
                            String _arg0147 = data.readString();
                            int _arg1104 = data.readInt();
                            long _arg263 = data.readLong();
                            String _arg339 = data.readString();
                            data.enforceNoDataAvail();
                            setDumpHeapDebugLimit(_arg0147, _arg1104, _arg263, _arg339);
                            reply.writeNoException();
                            return true;
                        case 177:
                            String _arg0148 = data.readString();
                            data.enforceNoDataAvail();
                            dumpHeapFinished(_arg0148);
                            reply.writeNoException();
                            return true;
                        case 178:
                            int _arg0149 = data.readInt();
                            String[] _arg1105 = data.createStringArray();
                            data.enforceNoDataAvail();
                            updateLockTaskPackages(_arg0149, _arg1105);
                            reply.writeNoException();
                            return true;
                        case 179:
                            IIntentSender _arg0150 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            WorkSource _arg1106 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            int _arg264 = data.readInt();
                            String _arg340 = data.readString();
                            data.enforceNoDataAvail();
                            noteAlarmStart(_arg0150, _arg1106, _arg264, _arg340);
                            reply.writeNoException();
                            return true;
                        case 180:
                            IIntentSender _arg0151 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            WorkSource _arg1107 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            int _arg265 = data.readInt();
                            String _arg341 = data.readString();
                            data.enforceNoDataAvail();
                            noteAlarmFinish(_arg0151, _arg1107, _arg265, _arg341);
                            reply.writeNoException();
                            return true;
                        case 181:
                            String _arg0152 = data.readString();
                            String _arg1108 = data.readString();
                            data.enforceNoDataAvail();
                            int _result86 = getPackageProcessState(_arg0152, _arg1108);
                            reply.writeNoException();
                            reply.writeInt(_result86);
                            return true;
                        case 182:
                            boolean _result87 = startBinderTracking();
                            reply.writeNoException();
                            reply.writeBoolean(_result87);
                            return true;
                        case 183:
                            ParcelFileDescriptor _arg0153 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result88 = stopBinderTrackingAndDump(_arg0153);
                            reply.writeNoException();
                            reply.writeBoolean(_result88);
                            return true;
                        case 184:
                            boolean _arg0154 = data.readBoolean();
                            data.enforceNoDataAvail();
                            suppressResizeConfigChanges(_arg0154);
                            reply.writeNoException();
                            return true;
                        case 185:
                            int _arg0155 = data.readInt();
                            byte[] _arg1109 = data.createByteArray();
                            byte[] _arg266 = data.createByteArray();
                            IProgressListener _arg342 = IProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result89 = unlockUser(_arg0155, _arg1109, _arg266, _arg342);
                            reply.writeNoException();
                            reply.writeBoolean(_result89);
                            return true;
                        case 186:
                            int _arg0156 = data.readInt();
                            IProgressListener _arg1111 = IProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result90 = unlockUser2(_arg0156, _arg1111);
                            reply.writeNoException();
                            reply.writeBoolean(_result90);
                            return true;
                        case 187:
                            String _arg0157 = data.readString();
                            int _arg1112 = data.readInt();
                            data.enforceNoDataAvail();
                            killPackageDependents(_arg0157, _arg1112);
                            reply.writeNoException();
                            return true;
                        case 188:
                            String _arg0158 = data.readString();
                            int _arg1113 = data.readInt();
                            data.enforceNoDataAvail();
                            makePackageIdle(_arg0158, _arg1113);
                            reply.writeNoException();
                            return true;
                        case 189:
                            int _result91 = getMemoryTrimLevel();
                            reply.writeNoException();
                            reply.writeInt(_result91);
                            return true;
                        case 190:
                            ComponentName _arg0159 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result92 = isVrModePackageEnabled(_arg0159);
                            reply.writeNoException();
                            reply.writeBoolean(_result92);
                            return true;
                        case 191:
                            int _arg0160 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyLockedProfile(_arg0160);
                            reply.writeNoException();
                            return true;
                        case 192:
                            Intent _arg0161 = (Intent) data.readTypedObject(Intent.CREATOR);
                            Bundle _arg1114 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            startConfirmDeviceCredentialIntent(_arg0161, _arg1114);
                            reply.writeNoException();
                            return true;
                        case 193:
                            sendIdleJobTrigger();
                            reply.writeNoException();
                            return true;
                        case 194:
                            IApplicationThread _arg0162 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            IIntentSender _arg1115 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg267 = data.readStrongBinder();
                            int _arg343 = data.readInt();
                            Intent _arg431 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg523 = data.readString();
                            IIntentReceiver _arg619 = IIntentReceiver.Stub.asInterface(data.readStrongBinder());
                            String _arg715 = data.readString();
                            Bundle _arg811 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result93 = sendIntentSender(_arg0162, _arg1115, _arg267, _arg343, _arg431, _arg523, _arg619, _arg715, _arg811);
                            reply.writeNoException();
                            reply.writeInt(_result93);
                            return true;
                        case 195:
                            String _arg0163 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result94 = isBackgroundRestricted(_arg0163);
                            reply.writeNoException();
                            reply.writeBoolean(_result94);
                            return true;
                        case 196:
                            int _arg0164 = data.readInt();
                            data.enforceNoDataAvail();
                            setRenderThread(_arg0164);
                            reply.writeNoException();
                            return true;
                        case 197:
                            boolean _arg0165 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setHasTopUi(_arg0165);
                            reply.writeNoException();
                            return true;
                        case 198:
                            int _arg0166 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result95 = restartUserInBackground(_arg0166);
                            reply.writeNoException();
                            reply.writeInt(_result95);
                            return true;
                        case 199:
                            int _arg0167 = data.readInt();
                            data.enforceNoDataAvail();
                            cancelTaskWindowTransition(_arg0167);
                            reply.writeNoException();
                            return true;
                        case 200:
                            List<String> _arg0168 = data.createStringArrayList();
                            int _arg1116 = data.readInt();
                            data.enforceNoDataAvail();
                            scheduleApplicationInfoChanged(_arg0168, _arg1116);
                            reply.writeNoException();
                            return true;
                        case 201:
                            int _arg0169 = data.readInt();
                            data.enforceNoDataAvail();
                            setPersistentVrThread(_arg0169);
                            reply.writeNoException();
                            return true;
                        case 202:
                            long _arg0170 = data.readLong();
                            data.enforceNoDataAvail();
                            waitForNetworkStateUpdate(_arg0170);
                            reply.writeNoException();
                            return true;
                        case 203:
                            int _arg0171 = data.readInt();
                            data.enforceNoDataAvail();
                            backgroundAllowlistUid(_arg0171);
                            reply.writeNoException();
                            return true;
                        case 204:
                            int _arg0172 = data.readInt();
                            IProgressListener _arg1117 = IProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result96 = startUserInBackgroundWithListener(_arg0172, _arg1117);
                            reply.writeNoException();
                            reply.writeBoolean(_result96);
                            return true;
                        case 205:
                            int _arg0173 = data.readInt();
                            String[] _arg1118 = data.createStringArray();
                            data.enforceNoDataAvail();
                            startDelegateShellPermissionIdentity(_arg0173, _arg1118);
                            reply.writeNoException();
                            return true;
                        case 206:
                            stopDelegateShellPermissionIdentity();
                            reply.writeNoException();
                            return true;
                        case 207:
                            List<String> _result97 = getDelegatedShellPermissions();
                            reply.writeNoException();
                            reply.writeStringList(_result97);
                            return true;
                        case 208:
                            ParcelFileDescriptor _result98 = getLifeMonitor();
                            reply.writeNoException();
                            reply.writeTypedObject(_result98, 1);
                            return true;
                        case 209:
                            int _arg0174 = data.readInt();
                            IProgressListener _arg1119 = IProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result99 = startUserInForegroundWithListener(_arg0174, _arg1119);
                            reply.writeNoException();
                            reply.writeBoolean(_result99);
                            return true;
                        case 210:
                            String _arg0175 = data.readString();
                            data.enforceNoDataAvail();
                            appNotResponding(_arg0175);
                            reply.writeNoException();
                            return true;
                        case 211:
                            String _arg0176 = data.readString();
                            int _arg1120 = data.readInt();
                            int _arg268 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice<ApplicationStartInfo> _result100 = getHistoricalProcessStartReasons(_arg0176, _arg1120, _arg268);
                            reply.writeNoException();
                            reply.writeTypedObject(_result100, 1);
                            return true;
                        case 212:
                            IApplicationStartInfoCompleteListener _arg0177 = IApplicationStartInfoCompleteListener.Stub.asInterface(data.readStrongBinder());
                            int _arg1121 = data.readInt();
                            data.enforceNoDataAvail();
                            setApplicationStartInfoCompleteListener(_arg0177, _arg1121);
                            reply.writeNoException();
                            return true;
                        case 213:
                            int _arg0178 = data.readInt();
                            data.enforceNoDataAvail();
                            removeApplicationStartInfoCompleteListener(_arg0178);
                            reply.writeNoException();
                            return true;
                        case 214:
                            String _arg0179 = data.readString();
                            int _arg1122 = data.readInt();
                            int _arg269 = data.readInt();
                            int _arg344 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice<ApplicationExitInfo> _result101 = getHistoricalProcessExitReasons(_arg0179, _arg1122, _arg269, _arg344);
                            reply.writeNoException();
                            reply.writeTypedObject(_result101, 1);
                            return true;
                        case 215:
                            int[] _arg0180 = data.createIntArray();
                            String _arg1123 = data.readString();
                            data.enforceNoDataAvail();
                            killProcessesWhenImperceptible(_arg0180, _arg1123);
                            reply.writeNoException();
                            return true;
                        case 216:
                            ComponentName _arg0181 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            LocusId _arg1124 = (LocusId) data.readTypedObject(LocusId.CREATOR);
                            IBinder _arg270 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            setActivityLocusContext(_arg0181, _arg1124, _arg270);
                            reply.writeNoException();
                            return true;
                        case 217:
                            byte[] _arg0182 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setProcessStateSummary(_arg0182);
                            reply.writeNoException();
                            return true;
                        case 218:
                            boolean _result102 = isAppFreezerSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result102);
                            return true;
                        case 219:
                            boolean _result103 = isAppFreezerEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result103);
                            return true;
                        case 220:
                            int _arg0183 = data.readInt();
                            int _arg1125 = data.readInt();
                            String _arg271 = data.readString();
                            data.enforceNoDataAvail();
                            killUidForPermissionChange(_arg0183, _arg1125, _arg271);
                            reply.writeNoException();
                            return true;
                        case 221:
                            resetAppErrors();
                            reply.writeNoException();
                            return true;
                        case 222:
                            boolean _arg0184 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result104 = enableAppFreezer(_arg0184);
                            reply.writeNoException();
                            reply.writeBoolean(_result104);
                            return true;
                        case 223:
                            boolean _arg0185 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result105 = enableFgsNotificationRateLimit(_arg0185);
                            reply.writeNoException();
                            reply.writeBoolean(_result105);
                            return true;
                        case 224:
                            IBinder _arg0186 = data.readStrongBinder();
                            int _arg1126 = data.readInt();
                            data.enforceNoDataAvail();
                            holdLock(_arg0186, _arg1126);
                            reply.writeNoException();
                            return true;
                        case 225:
                            int _arg0187 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result106 = startProfile(_arg0187);
                            reply.writeNoException();
                            reply.writeBoolean(_result106);
                            return true;
                        case 226:
                            int _arg0188 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result107 = stopProfile(_arg0188);
                            reply.writeNoException();
                            reply.writeBoolean(_result107);
                            return true;
                        case 227:
                            IIntentSender _arg0189 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                            int _arg1127 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result108 = queryIntentComponentsForIntentSender(_arg0189, _arg1127);
                            reply.writeNoException();
                            reply.writeTypedObject(_result108, 1);
                            return true;
                        case 228:
                            int _arg0190 = data.readInt();
                            String _arg1128 = data.readString();
                            data.enforceNoDataAvail();
                            int _result109 = getUidProcessCapabilities(_arg0190, _arg1128);
                            reply.writeNoException();
                            reply.writeInt(_result109);
                            return true;
                        case 229:
                            waitForBroadcastIdle();
                            reply.writeNoException();
                            return true;
                        case 230:
                            waitForBroadcastBarrier();
                            reply.writeNoException();
                            return true;
                        case 231:
                            String _arg0191 = data.readString();
                            long _arg1129 = data.readLong();
                            data.enforceNoDataAvail();
                            forceDelayBroadcastDelivery(_arg0191, _arg1129);
                            reply.writeNoException();
                            return true;
                        case 232:
                            boolean _result110 = isModernBroadcastQueueEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result110);
                            return true;
                        case 233:
                            int _arg0192 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result111 = isProcessFrozen(_arg0192);
                            reply.writeNoException();
                            reply.writeBoolean(_result111);
                            return true;
                        case 234:
                            int _arg0193 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result112 = getBackgroundRestrictionExemptionReason(_arg0193);
                            reply.writeNoException();
                            reply.writeInt(_result112);
                            return true;
                        case 235:
                            int _arg0194 = data.readInt();
                            int _arg1130 = data.readInt();
                            IProgressListener _arg272 = IProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result113 = startUserInBackgroundVisibleOnDisplay(_arg0194, _arg1130, _arg272);
                            reply.writeNoException();
                            reply.writeBoolean(_result113);
                            return true;
                        case 236:
                            int _arg0195 = data.readInt();
                            IProgressListener _arg1131 = IProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result114 = startProfileWithListener(_arg0195, _arg1131);
                            reply.writeNoException();
                            reply.writeBoolean(_result114);
                            return true;
                        case 237:
                            int[] _result115 = getDisplayIdsForStartingVisibleBackgroundUsers();
                            reply.writeNoException();
                            reply.writeIntArray(_result115);
                            return true;
                        case 238:
                            ComponentName _arg0196 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            IBinder _arg1132 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            boolean _result116 = shouldServiceTimeOut(_arg0196, _arg1132);
                            reply.writeNoException();
                            reply.writeBoolean(_result116);
                            return true;
                        case 239:
                            int _arg0197 = data.readInt();
                            int _arg1133 = data.readInt();
                            int _arg273 = data.readInt();
                            data.enforceNoDataAvail();
                            logFgsApiBegin(_arg0197, _arg1133, _arg273);
                            return true;
                        case 240:
                            int _arg0198 = data.readInt();
                            int _arg1134 = data.readInt();
                            int _arg274 = data.readInt();
                            data.enforceNoDataAvail();
                            logFgsApiEnd(_arg0198, _arg1134, _arg274);
                            return true;
                        case 241:
                            int _arg0199 = data.readInt();
                            int _arg1135 = data.readInt();
                            int _arg275 = data.readInt();
                            int _arg345 = data.readInt();
                            data.enforceNoDataAvail();
                            logFgsApiStateChanged(_arg0199, _arg1135, _arg275, _arg345);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IActivityManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.app.IActivityManager
            public ParcelFileDescriptor openContentUri(String uriString) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uriString);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void registerUidObserver(IUidObserver observer, int which, int cutpoint, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(which);
                    _data.writeInt(cutpoint);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void unregisterUidObserver(IUidObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isUidActive(int uid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getUidProcessState(int uid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int checkPermission(String permission, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permission);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void handleApplicationCrash(IBinder app, ApplicationErrorReport.ParcelableCrashInfo crashInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(app);
                    _data.writeTypedObject(crashInfo, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int startActivity(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeStrongBinder(resultTo);
                    _data.writeString(resultWho);
                    _data.writeInt(requestCode);
                    _data.writeInt(flags);
                    _data.writeTypedObject(profilerInfo, 0);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int startActivityWithFeature(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                                        this.mRemote.transact(9, _data, _reply, 0);
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

            @Override // android.app.IActivityManager
            public void unhandledBack() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean finishActivity(IBinder token, int code, Intent data, int finishTask) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(code);
                    _data.writeTypedObject(data, 0);
                    _data.writeInt(finishTask);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public Intent registerReceiver(IApplicationThread caller, String callerPackage, IIntentReceiver receiver, IntentFilter filter, String requiredPermission, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callerPackage);
                    _data.writeStrongInterface(receiver);
                    _data.writeTypedObject(filter, 0);
                    _data.writeString(requiredPermission);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    Intent _result = (Intent) _reply.readTypedObject(Intent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public Intent registerReceiverWithFeature(IApplicationThread caller, String callerPackage, String callingFeatureId, String receiverId, IIntentReceiver receiver, IntentFilter filter, String requiredPermission, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callerPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeString(receiverId);
                    _data.writeStrongInterface(receiver);
                    _data.writeTypedObject(filter, 0);
                    _data.writeString(requiredPermission);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    Intent _result = (Intent) _reply.readTypedObject(Intent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void unregisterReceiver(IIntentReceiver receiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(receiver);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int broadcastIntent(IApplicationThread caller, Intent intent, String resolvedType, IIntentReceiver resultTo, int resultCode, String resultData, Bundle map, String[] requiredPermissions, int appOp, Bundle options, boolean serialized, boolean sticky, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeTypedObject(intent, 0);
                    try {
                        _data.writeString(resolvedType);
                        try {
                            _data.writeStrongInterface(resultTo);
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
                }
                try {
                    _data.writeInt(resultCode);
                    try {
                        _data.writeString(resultData);
                        try {
                            _data.writeTypedObject(map, 0);
                            try {
                                _data.writeStringArray(requiredPermissions);
                                try {
                                    _data.writeInt(appOp);
                                    try {
                                        _data.writeTypedObject(options, 0);
                                        try {
                                            _data.writeBoolean(serialized);
                                            try {
                                                _data.writeBoolean(sticky);
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
                                } catch (Throwable th7) {
                                    th = th7;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
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
                    try {
                        _data.writeInt(userId);
                        this.mRemote.transact(15, _data, _reply, 0);
                        _reply.readException();
                        int _result = _reply.readInt();
                        _reply.recycle();
                        _data.recycle();
                        return _result;
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

            @Override // android.app.IActivityManager
            public int broadcastIntentWithFeature(IApplicationThread caller, String callingFeatureId, Intent intent, String resolvedType, IIntentReceiver resultTo, int resultCode, String resultData, Bundle map, String[] requiredPermissions, String[] excludePermissions, String[] excludePackages, int appOp, Bundle options, boolean serialized, boolean sticky, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
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
                }
                try {
                    _data.writeString(resolvedType);
                    try {
                        _data.writeStrongInterface(resultTo);
                        try {
                            _data.writeInt(resultCode);
                            try {
                                _data.writeString(resultData);
                                try {
                                    _data.writeTypedObject(map, 0);
                                    try {
                                        _data.writeStringArray(requiredPermissions);
                                        try {
                                            _data.writeStringArray(excludePermissions);
                                            try {
                                                _data.writeStringArray(excludePackages);
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
                    try {
                        _data.writeInt(appOp);
                        try {
                            _data.writeTypedObject(options, 0);
                            _data.writeBoolean(serialized);
                            _data.writeBoolean(sticky);
                            _data.writeInt(userId);
                            this.mRemote.transact(16, _data, _reply, 0);
                            _reply.readException();
                            int _result = _reply.readInt();
                            _reply.recycle();
                            _data.recycle();
                            return _result;
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

            @Override // android.app.IActivityManager
            public void unbroadcastIntent(IApplicationThread caller, Intent intent, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeTypedObject(intent, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void finishReceiver(IBinder who, int resultCode, String resultData, Bundle map, boolean abortBroadcast, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(who);
                    _data.writeInt(resultCode);
                    _data.writeString(resultData);
                    _data.writeTypedObject(map, 0);
                    _data.writeBoolean(abortBroadcast);
                    _data.writeInt(flags);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void attachApplication(IApplicationThread app, long startSeq) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(app);
                    _data.writeLong(startSeq);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void finishAttachApplication(long startSeq) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(startSeq);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(maxNum);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityManager.RunningTaskInfo> _result = _reply.createTypedArrayList(ActivityManager.RunningTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void moveTaskToFront(IApplicationThread caller, String callingPackage, int task, int flags, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeInt(task);
                    _data.writeInt(flags);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getTaskForActivity(IBinder token, boolean onlyRoot) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeBoolean(onlyRoot);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ContentProviderHolder getContentProvider(IApplicationThread caller, String callingPackage, String name, int userId, boolean stable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeString(name);
                    _data.writeInt(userId);
                    _data.writeBoolean(stable);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    ContentProviderHolder _result = (ContentProviderHolder) _reply.readTypedObject(ContentProviderHolder.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void publishContentProviders(IApplicationThread caller, List<ContentProviderHolder> providers) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeTypedList(providers, 0);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean refContentProvider(IBinder connection, int stableDelta, int unstableDelta) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(connection);
                    _data.writeInt(stableDelta);
                    _data.writeInt(unstableDelta);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public PendingIntent getRunningServiceControlPanel(ComponentName service) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(service, 0);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    PendingIntent _result = (PendingIntent) _reply.readTypedObject(PendingIntent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ComponentName startService(IApplicationThread caller, Intent service, String resolvedType, boolean requireForeground, String callingPackage, String callingFeatureId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeTypedObject(service, 0);
                    _data.writeString(resolvedType);
                    _data.writeBoolean(requireForeground);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeInt(userId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int stopService(IApplicationThread caller, Intent service, String resolvedType, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeTypedObject(service, 0);
                    _data.writeString(resolvedType);
                    _data.writeInt(userId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int bindService(IApplicationThread caller, IBinder token, Intent service, String resolvedType, IServiceConnection connection, long flags, String callingPackage, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(service, 0);
                    _data.writeString(resolvedType);
                    _data.writeStrongInterface(connection);
                    _data.writeLong(flags);
                    _data.writeString(callingPackage);
                    _data.writeInt(userId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int bindServiceInstance(IApplicationThread caller, IBinder token, Intent service, String resolvedType, IServiceConnection connection, long flags, String instanceName, String callingPackage, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(service, 0);
                    _data.writeString(resolvedType);
                    _data.writeStrongInterface(connection);
                    _data.writeLong(flags);
                    _data.writeString(instanceName);
                    _data.writeString(callingPackage);
                    _data.writeInt(userId);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void updateServiceGroup(IServiceConnection connection, int group, int importance) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(connection);
                    _data.writeInt(group);
                    _data.writeInt(importance);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean unbindService(IServiceConnection connection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(connection);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void publishService(IBinder token, Intent intent, IBinder service) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(intent, 0);
                    _data.writeStrongBinder(service);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setDebugApp(String packageName, boolean waitForDebugger, boolean persistent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(waitForDebugger);
                    _data.writeBoolean(persistent);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setAgentApp(String packageName, String agent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(agent);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setAlwaysFinish(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean startInstrumentation(ComponentName className, String profileFile, int flags, Bundle arguments, IInstrumentationWatcher watcher, IUiAutomationConnection connection, int userId, String abiOverride) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeString(profileFile);
                    _data.writeInt(flags);
                    _data.writeTypedObject(arguments, 0);
                    _data.writeStrongInterface(watcher);
                    _data.writeStrongInterface(connection);
                    _data.writeInt(userId);
                    _data.writeString(abiOverride);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void addInstrumentationResults(IApplicationThread target, Bundle results) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(target);
                    _data.writeTypedObject(results, 0);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void finishInstrumentation(IApplicationThread target, int resultCode, Bundle results) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(target);
                    _data.writeInt(resultCode);
                    _data.writeTypedObject(results, 0);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public Configuration getConfiguration() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    Configuration _result = (Configuration) _reply.readTypedObject(Configuration.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean updateConfiguration(Configuration values) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(values, 0);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean updateMccMncConfiguration(String mcc, String mnc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(mcc);
                    _data.writeString(mnc);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean stopServiceToken(ComponentName className, IBinder token, int startId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeStrongBinder(token);
                    _data.writeInt(startId);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setProcessLimit(int max) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(max);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getProcessLimit() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int checkUriPermission(Uri uri, int pid, int uid, int mode, int userId, IBinder callerToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uri, 0);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeInt(mode);
                    _data.writeInt(userId);
                    _data.writeStrongBinder(callerToken);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int[] checkUriPermissions(List<Uri> uris, int pid, int uid, int mode, int userId, IBinder callerToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(uris, 0);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeInt(mode);
                    _data.writeInt(userId);
                    _data.writeStrongBinder(callerToken);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void grantUriPermission(IApplicationThread caller, String targetPkg, Uri uri, int mode, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(targetPkg);
                    _data.writeTypedObject(uri, 0);
                    _data.writeInt(mode);
                    _data.writeInt(userId);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void revokeUriPermission(IApplicationThread caller, String targetPkg, Uri uri, int mode, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(targetPkg);
                    _data.writeTypedObject(uri, 0);
                    _data.writeInt(mode);
                    _data.writeInt(userId);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setActivityController(IActivityController watcher, boolean imAMonkey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(watcher);
                    _data.writeBoolean(imAMonkey);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void showWaitingForDebugger(IApplicationThread who, boolean waiting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(who);
                    _data.writeBoolean(waiting);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void signalPersistentProcesses(int signal) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(signal);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ParceledListSlice getRecentTasks(int maxNum, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(maxNum);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void serviceDoneExecuting(IBinder token, int type, int startId, int res) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(type);
                    _data.writeInt(startId);
                    _data.writeInt(res);
                    this.mRemote.transact(55, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public IIntentSender getIntentSender(int type, String packageName, IBinder token, String resultWho, int requestCode, Intent[] intents, String[] resolvedTypes, int flags, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(token);
                    _data.writeString(resultWho);
                    _data.writeInt(requestCode);
                    _data.writeTypedArray(intents, 0);
                    _data.writeStringArray(resolvedTypes);
                    _data.writeInt(flags);
                    _data.writeTypedObject(options, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    IIntentSender _result = IIntentSender.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public IIntentSender getIntentSenderWithFeature(int type, String packageName, String featureId, IBinder token, String resultWho, int requestCode, Intent[] intents, String[] resolvedTypes, int flags, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeString(packageName);
                    try {
                        _data.writeString(featureId);
                        try {
                            _data.writeStrongBinder(token);
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
                            _data.writeTypedArray(intents, 0);
                            try {
                                _data.writeStringArray(resolvedTypes);
                                try {
                                    _data.writeInt(flags);
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
                                    this.mRemote.transact(57, _data, _reply, 0);
                                    _reply.readException();
                                    IIntentSender _result = IIntentSender.Stub.asInterface(_reply.readStrongBinder());
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

            @Override // android.app.IActivityManager
            public void cancelIntentSender(IIntentSender sender) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ActivityManager.PendingIntentInfo getInfoForIntentSender(IIntentSender sender) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    ActivityManager.PendingIntentInfo _result = (ActivityManager.PendingIntentInfo) _reply.readTypedObject(ActivityManager.PendingIntentInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean registerIntentSenderCancelListenerEx(IIntentSender sender, IResultReceiver receiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    _data.writeStrongInterface(receiver);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void unregisterIntentSenderCancelListener(IIntentSender sender, IResultReceiver receiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    _data.writeStrongInterface(receiver);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void enterSafeMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void noteWakeupAlarm(IIntentSender sender, WorkSource workSource, int sourceUid, String sourcePkg, String tag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    _data.writeTypedObject(workSource, 0);
                    _data.writeInt(sourceUid);
                    _data.writeString(sourcePkg);
                    _data.writeString(tag);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void removeContentProvider(IBinder connection, boolean stable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(connection);
                    _data.writeBoolean(stable);
                    this.mRemote.transact(64, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setRequestedOrientation(IBinder token, int requestedOrientation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(requestedOrientation);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void unbindFinished(IBinder token, Intent service, boolean doRebind) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(service, 0);
                    _data.writeBoolean(doRebind);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setProcessImportant(IBinder token, int pid, boolean isForeground, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(pid);
                    _data.writeBoolean(isForeground);
                    _data.writeString(reason);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setServiceForeground(ComponentName className, IBinder token, int id, Notification notification, int flags, int foregroundServiceType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeStrongBinder(token);
                    _data.writeInt(id);
                    _data.writeTypedObject(notification, 0);
                    _data.writeInt(flags);
                    _data.writeInt(foregroundServiceType);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getForegroundServiceType(ComponentName className, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean moveActivityTaskToBack(IBinder token, boolean nonRoot) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeBoolean(nonRoot);
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void getMemoryInfo(ActivityManager.MemoryInfo outInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        outInfo.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityManager.ProcessErrorStateInfo> _result = _reply.createTypedArrayList(ActivityManager.ProcessErrorStateInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean clearApplicationUserData(String packageName, boolean keepState, IPackageDataObserver observer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(keepState);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(userId);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void stopAppForUser(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean registerForegroundServiceObserver(IForegroundServiceObserver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void forceStopPackage(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean killPids(int[] pids, String reason, boolean secure) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(pids);
                    _data.writeString(reason);
                    _data.writeBoolean(secure);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public List<ActivityManager.RunningServiceInfo> getServices(int maxNum, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(maxNum);
                    _data.writeInt(flags);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityManager.RunningServiceInfo> _result = _reply.createTypedArrayList(ActivityManager.RunningServiceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityManager.RunningAppProcessInfo> _result = _reply.createTypedArrayList(ActivityManager.RunningAppProcessInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public IBinder peekService(Intent service, String resolvedType, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(service, 0);
                    _data.writeString(resolvedType);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean profileControl(String process, int userId, boolean start, ProfilerInfo profilerInfo, int profileType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(process);
                    _data.writeInt(userId);
                    _data.writeBoolean(start);
                    _data.writeTypedObject(profilerInfo, 0);
                    _data.writeInt(profileType);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean shutdown(int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(timeout);
                    this.mRemote.transact(82, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void stopAppSwitches() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void resumeAppSwitches() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean bindBackupAgent(String packageName, int backupRestoreMode, int targetUserId, int backupDestination) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(backupRestoreMode);
                    _data.writeInt(targetUserId);
                    _data.writeInt(backupDestination);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void backupAgentCreated(String packageName, IBinder agent, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(agent);
                    _data.writeInt(userId);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void unbindBackupAgent(ApplicationInfo appInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(appInfo, 0);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int handleIncomingUser(int callingPid, int callingUid, int userId, boolean allowAll, boolean requireFull, String name, String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(callingPid);
                    _data.writeInt(callingUid);
                    _data.writeInt(userId);
                    _data.writeBoolean(allowAll);
                    _data.writeBoolean(requireFull);
                    _data.writeString(name);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void addPackageDependency(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void killApplication(String pkg, int appId, int userId, String reason, int exitInfoReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeInt(appId);
                    _data.writeInt(userId);
                    _data.writeString(reason);
                    _data.writeInt(exitInfoReason);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void closeSystemDialogs(String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public Debug.MemoryInfo[] getProcessMemoryInfo(int[] pids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(pids);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                    Debug.MemoryInfo[] _result = (Debug.MemoryInfo[]) _reply.createTypedArray(Debug.MemoryInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void killApplicationProcess(String processName, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(processName);
                    _data.writeInt(uid);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean handleApplicationWtf(IBinder app, String tag, boolean system, ApplicationErrorReport.ParcelableCrashInfo crashInfo, int immediateCallerPid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(app);
                    _data.writeString(tag);
                    _data.writeBoolean(system);
                    _data.writeTypedObject(crashInfo, 0);
                    _data.writeInt(immediateCallerPid);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void killBackgroundProcesses(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(95, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isUserAMonkey() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public List<ApplicationInfo> getRunningExternalApplications() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(97, _data, _reply, 0);
                    _reply.readException();
                    List<ApplicationInfo> _result = _reply.createTypedArrayList(ApplicationInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void finishHeavyWeightApp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(98, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void handleApplicationStrictModeViolation(IBinder app, int penaltyMask, StrictMode.ViolationInfo crashInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(app);
                    _data.writeInt(penaltyMask);
                    _data.writeTypedObject(crashInfo, 0);
                    this.mRemote.transact(99, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void registerStrictModeCallback(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(100, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isTopActivityImmersive() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(101, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void crashApplicationWithType(int uid, int initialPid, String packageName, int userId, String message, boolean force, int exceptionTypeId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(initialPid);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(message);
                    _data.writeBoolean(force);
                    _data.writeInt(exceptionTypeId);
                    this.mRemote.transact(102, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void crashApplicationWithTypeWithExtras(int uid, int initialPid, String packageName, int userId, String message, boolean force, int exceptionTypeId, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(initialPid);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(message);
                    _data.writeBoolean(force);
                    _data.writeInt(exceptionTypeId);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(103, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void getMimeTypeFilterAsync(Uri uri, int userId, RemoteCallback resultCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uri, 0);
                    _data.writeInt(userId);
                    _data.writeTypedObject(resultCallback, 0);
                    this.mRemote.transact(104, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean dumpHeap(String process, int userId, boolean managed, boolean mallocInfo, boolean runGc, String path, ParcelFileDescriptor fd, RemoteCallback finishCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(process);
                    _data.writeInt(userId);
                    _data.writeBoolean(managed);
                    _data.writeBoolean(mallocInfo);
                    _data.writeBoolean(runGc);
                    _data.writeString(path);
                    _data.writeTypedObject(fd, 0);
                    _data.writeTypedObject(finishCallback, 0);
                    this.mRemote.transact(105, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isUserRunning(int userid, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    _data.writeInt(flags);
                    this.mRemote.transact(106, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setPackageScreenCompatMode(String packageName, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(mode);
                    this.mRemote.transact(107, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean switchUser(int userid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    this.mRemote.transact(108, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public String getSwitchingFromUserMessage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(109, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public String getSwitchingToUserMessage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(110, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setStopUserOnSwitch(int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(value);
                    this.mRemote.transact(111, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean removeTask(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(112, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void registerProcessObserver(IProcessObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(113, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void unregisterProcessObserver(IProcessObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(114, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isIntentSenderTargetedToPackage(IIntentSender sender) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    this.mRemote.transact(115, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void updatePersistentConfiguration(Configuration values) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(values, 0);
                    this.mRemote.transact(116, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void updatePersistentConfigurationWithAttribution(Configuration values, String callingPackageName, String callingAttributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(values, 0);
                    _data.writeString(callingPackageName);
                    _data.writeString(callingAttributionTag);
                    this.mRemote.transact(117, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public long[] getProcessPss(int[] pids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(pids);
                    this.mRemote.transact(118, _data, _reply, 0);
                    _reply.readException();
                    long[] _result = _reply.createLongArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void showBootMessage(CharSequence msg, boolean always) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (msg != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(msg, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeBoolean(always);
                    this.mRemote.transact(119, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void killAllBackgroundProcesses() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(120, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ContentProviderHolder getContentProviderExternal(String name, int userId, IBinder token, String tag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(userId);
                    _data.writeStrongBinder(token);
                    _data.writeString(tag);
                    this.mRemote.transact(121, _data, _reply, 0);
                    _reply.readException();
                    ContentProviderHolder _result = (ContentProviderHolder) _reply.readTypedObject(ContentProviderHolder.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void removeContentProviderExternal(String name, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(122, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void removeContentProviderExternalAsUser(String name, IBinder token, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongBinder(token);
                    _data.writeInt(userId);
                    this.mRemote.transact(123, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void getMyMemoryState(ActivityManager.RunningAppProcessInfo outInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(124, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        outInfo.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean killProcessesBelowForeground(String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    this.mRemote.transact(125, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public UserInfo getCurrentUser() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(126, _data, _reply, 0);
                    _reply.readException();
                    UserInfo _result = (UserInfo) _reply.readTypedObject(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getCurrentUserId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(127, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getLaunchedFromUid(IBinder activityToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(activityToken);
                    this.mRemote.transact(128, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void unstableProviderDied(IBinder connection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(connection);
                    this.mRemote.transact(129, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isIntentSenderAnActivity(IIntentSender sender) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    this.mRemote.transact(130, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int startActivityAsUser(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    try {
                        _data.writeString(callingPackage);
                        try {
                            _data.writeTypedObject(intent, 0);
                            try {
                                _data.writeString(resolvedType);
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
                        _data.writeStrongBinder(resultTo);
                        try {
                            _data.writeString(resultWho);
                            try {
                                _data.writeInt(requestCode);
                                try {
                                    _data.writeInt(flags);
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
                            _data.writeTypedObject(profilerInfo, 0);
                            try {
                                _data.writeTypedObject(options, 0);
                                try {
                                    _data.writeInt(userId);
                                    try {
                                        this.mRemote.transact(131, _data, _reply, 0);
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

            @Override // android.app.IActivityManager
            public int startActivityAsUserWithFeature(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                                    this.mRemote.transact(132, _data, _reply, 0);
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

            @Override // android.app.IActivityManager
            public int stopUser(int userid, boolean force, IStopUserCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    _data.writeBoolean(force);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(133, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int stopUserWithDelayedLocking(int userid, boolean force, IStopUserCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    _data.writeBoolean(force);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(134, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void registerUserSwitchObserver(IUserSwitchObserver observer, String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    _data.writeString(name);
                    this.mRemote.transact(135, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void unregisterUserSwitchObserver(IUserSwitchObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(136, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int[] getRunningUserIds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(137, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void requestSystemServerHeapDump() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(138, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void requestBugReport(int bugreportType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(bugreportType);
                    this.mRemote.transact(139, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void requestBugReportWithDescription(String shareTitle, String shareDescription, int bugreportType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(shareTitle);
                    _data.writeString(shareDescription);
                    _data.writeInt(bugreportType);
                    this.mRemote.transact(140, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void requestTelephonyBugReport(String shareTitle, String shareDescription) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(shareTitle);
                    _data.writeString(shareDescription);
                    this.mRemote.transact(141, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void requestWifiBugReport(String shareTitle, String shareDescription) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(shareTitle);
                    _data.writeString(shareDescription);
                    this.mRemote.transact(142, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void requestInteractiveBugReportWithDescription(String shareTitle, String shareDescription) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(shareTitle);
                    _data.writeString(shareDescription);
                    this.mRemote.transact(143, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void requestInteractiveBugReport() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(144, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void requestFullBugReport() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(145, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void requestRemoteBugReport(long nonce) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(nonce);
                    this.mRemote.transact(146, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean launchBugReportHandlerApp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(147, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public List<String> getBugreportWhitelistedPackages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(148, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public Intent getIntentForIntentSender(IIntentSender sender) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    this.mRemote.transact(149, _data, _reply, 0);
                    _reply.readException();
                    Intent _result = (Intent) _reply.readTypedObject(Intent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public String getLaunchedFromPackage(IBinder activityToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(activityToken);
                    this.mRemote.transact(150, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void killUid(int appId, int userId, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appId);
                    _data.writeInt(userId);
                    _data.writeString(reason);
                    this.mRemote.transact(151, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setUserIsMonkey(boolean monkey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(monkey);
                    this.mRemote.transact(152, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void hang(IBinder who, boolean allowRestart) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(who);
                    _data.writeBoolean(allowRestart);
                    this.mRemote.transact(153, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(154, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityTaskManager.RootTaskInfo> _result = _reply.createTypedArrayList(ActivityTaskManager.RootTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void moveTaskToRootTask(int taskId, int rootTaskId, boolean toTop) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(rootTaskId);
                    _data.writeBoolean(toTop);
                    this.mRemote.transact(155, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setFocusedRootTask(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(156, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(157, _data, _reply, 0);
                    _reply.readException();
                    ActivityTaskManager.RootTaskInfo _result = (ActivityTaskManager.RootTaskInfo) _reply.readTypedObject(ActivityTaskManager.RootTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void restart() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(158, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void performIdleMaintenance() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(159, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void appNotRespondingViaProvider(IBinder connection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(connection);
                    this.mRemote.transact(160, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public Rect getTaskBounds(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(161, _data, _reply, 0);
                    _reply.readException();
                    Rect _result = (Rect) _reply.readTypedObject(Rect.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean setProcessMemoryTrimLevel(String process, int userId, int level) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(process);
                    _data.writeInt(userId);
                    _data.writeInt(level);
                    this.mRemote.transact(162, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public String getTagForIntentSender(IIntentSender sender, String prefix) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    _data.writeString(prefix);
                    this.mRemote.transact(163, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean startUserInBackground(int userid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    this.mRemote.transact(164, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isInLockTaskMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(165, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int startActivityFromRecents(int taskId, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(166, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void startSystemLockTaskMode(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(167, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isTopOfTask(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(168, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void bootAnimationComplete() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(169, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void registerTaskStackListener(ITaskStackListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(170, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void unregisterTaskStackListener(ITaskStackListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(171, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void notifyCleartextNetwork(int uid, byte[] firstPacket) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeByteArray(firstPacket);
                    this.mRemote.transact(172, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setTaskResizeable(int taskId, int resizeableMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(resizeableMode);
                    this.mRemote.transact(173, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void resizeTask(int taskId, Rect bounds, int resizeMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(bounds, 0);
                    _data.writeInt(resizeMode);
                    this.mRemote.transact(174, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getLockTaskModeState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(175, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setDumpHeapDebugLimit(String processName, int uid, long maxMemSize, String reportPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(processName);
                    _data.writeInt(uid);
                    _data.writeLong(maxMemSize);
                    _data.writeString(reportPackage);
                    this.mRemote.transact(176, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void dumpHeapFinished(String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    this.mRemote.transact(177, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void updateLockTaskPackages(int userId, String[] packages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeStringArray(packages);
                    this.mRemote.transact(178, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void noteAlarmStart(IIntentSender sender, WorkSource workSource, int sourceUid, String tag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    _data.writeTypedObject(workSource, 0);
                    _data.writeInt(sourceUid);
                    _data.writeString(tag);
                    this.mRemote.transact(179, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void noteAlarmFinish(IIntentSender sender, WorkSource workSource, int sourceUid, String tag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    _data.writeTypedObject(workSource, 0);
                    _data.writeInt(sourceUid);
                    _data.writeString(tag);
                    this.mRemote.transact(180, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getPackageProcessState(String packageName, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(181, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean startBinderTracking() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(182, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean stopBinderTrackingAndDump(ParcelFileDescriptor fd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(fd, 0);
                    this.mRemote.transact(183, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void suppressResizeConfigChanges(boolean suppress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(suppress);
                    this.mRemote.transact(184, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean unlockUser(int userid, byte[] token, byte[] secret, IProgressListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    _data.writeByteArray(token);
                    _data.writeByteArray(secret);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(185, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean unlockUser2(int userId, IProgressListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(186, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void killPackageDependents(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(187, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void makePackageIdle(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(188, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getMemoryTrimLevel() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(189, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isVrModePackageEnabled(ComponentName packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(packageName, 0);
                    this.mRemote.transact(190, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void notifyLockedProfile(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(191, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void startConfirmDeviceCredentialIntent(Intent intent, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(192, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void sendIdleJobTrigger() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(193, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int sendIntentSender(IApplicationThread caller, IIntentSender target, IBinder whitelistToken, int code, Intent intent, String resolvedType, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeStrongInterface(target);
                    _data.writeStrongBinder(whitelistToken);
                    _data.writeInt(code);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeStrongInterface(finishedReceiver);
                    _data.writeString(requiredPermission);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(194, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isBackgroundRestricted(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(195, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setRenderThread(int tid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(tid);
                    this.mRemote.transact(196, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setHasTopUi(boolean hasTopUi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(hasTopUi);
                    this.mRemote.transact(197, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int restartUserInBackground(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(198, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void cancelTaskWindowTransition(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(199, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void scheduleApplicationInfoChanged(List<String> packageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(packageNames);
                    _data.writeInt(userId);
                    this.mRemote.transact(200, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setPersistentVrThread(int tid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(tid);
                    this.mRemote.transact(201, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void waitForNetworkStateUpdate(long procStateSeq) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(procStateSeq);
                    this.mRemote.transact(202, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void backgroundAllowlistUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(203, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean startUserInBackgroundWithListener(int userid, IProgressListener unlockProgressListener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    _data.writeStrongInterface(unlockProgressListener);
                    this.mRemote.transact(204, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void startDelegateShellPermissionIdentity(int uid, String[] permissions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeStringArray(permissions);
                    this.mRemote.transact(205, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void stopDelegateShellPermissionIdentity() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(206, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public List<String> getDelegatedShellPermissions() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(207, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ParcelFileDescriptor getLifeMonitor() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(208, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean startUserInForegroundWithListener(int userid, IProgressListener unlockProgressListener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    _data.writeStrongInterface(unlockProgressListener);
                    this.mRemote.transact(209, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void appNotResponding(String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    this.mRemote.transact(210, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ParceledListSlice<ApplicationStartInfo> getHistoricalProcessStartReasons(String packageName, int maxNum, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(maxNum);
                    _data.writeInt(userId);
                    this.mRemote.transact(211, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice<ApplicationStartInfo> _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setApplicationStartInfoCompleteListener(IApplicationStartInfoCompleteListener listener, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(userId);
                    this.mRemote.transact(212, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void removeApplicationStartInfoCompleteListener(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(213, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ParceledListSlice<ApplicationExitInfo> getHistoricalProcessExitReasons(String packageName, int pid, int maxNum, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(maxNum);
                    _data.writeInt(userId);
                    this.mRemote.transact(214, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice<ApplicationExitInfo> _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void killProcessesWhenImperceptible(int[] pids, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(pids);
                    _data.writeString(reason);
                    this.mRemote.transact(215, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setActivityLocusContext(ComponentName activity, LocusId locusId, IBinder appToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(activity, 0);
                    _data.writeTypedObject(locusId, 0);
                    _data.writeStrongBinder(appToken);
                    this.mRemote.transact(216, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void setProcessStateSummary(byte[] state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(state);
                    this.mRemote.transact(217, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isAppFreezerSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(218, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isAppFreezerEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(219, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void killUidForPermissionChange(int appId, int userId, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appId);
                    _data.writeInt(userId);
                    _data.writeString(reason);
                    this.mRemote.transact(220, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void resetAppErrors() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(221, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean enableAppFreezer(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(222, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean enableFgsNotificationRateLimit(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(223, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void holdLock(IBinder token, int durationMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(durationMs);
                    this.mRemote.transact(224, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean startProfile(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(225, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean stopProfile(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(226, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public ParceledListSlice queryIntentComponentsForIntentSender(IIntentSender sender, int matchFlags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(sender);
                    _data.writeInt(matchFlags);
                    this.mRemote.transact(227, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getUidProcessCapabilities(int uid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(228, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void waitForBroadcastIdle() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(229, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void waitForBroadcastBarrier() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(230, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void forceDelayBroadcastDelivery(String targetPackage, long delayedDurationMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPackage);
                    _data.writeLong(delayedDurationMs);
                    this.mRemote.transact(231, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isModernBroadcastQueueEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(232, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean isProcessFrozen(int pid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pid);
                    this.mRemote.transact(233, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int getBackgroundRestrictionExemptionReason(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(234, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean startUserInBackgroundVisibleOnDisplay(int userid, int displayId, IProgressListener unlockProgressListener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    _data.writeInt(displayId);
                    _data.writeStrongInterface(unlockProgressListener);
                    this.mRemote.transact(235, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean startProfileWithListener(int userid, IProgressListener unlockProgressListener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userid);
                    _data.writeStrongInterface(unlockProgressListener);
                    this.mRemote.transact(236, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public int[] getDisplayIdsForStartingVisibleBackgroundUsers() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(237, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public boolean shouldServiceTimeOut(ComponentName className, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(238, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void logFgsApiBegin(int apiType, int appUid, int appPid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(apiType);
                    _data.writeInt(appUid);
                    _data.writeInt(appPid);
                    this.mRemote.transact(239, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void logFgsApiEnd(int apiType, int appUid, int appPid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(apiType);
                    _data.writeInt(appUid);
                    _data.writeInt(appPid);
                    this.mRemote.transact(240, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IActivityManager
            public void logFgsApiStateChanged(int apiType, int state, int appUid, int appPid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(apiType);
                    _data.writeInt(state);
                    _data.writeInt(appUid);
                    _data.writeInt(appPid);
                    this.mRemote.transact(241, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 240;
        }
    }
}
