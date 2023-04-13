package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.BackgroundStartPrivileges;
import android.app.IApplicationThread;
import android.app.ITaskStackListener;
import android.app.ProfilerInfo;
import android.content.ComponentName;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.RemoteException;
import android.service.voice.IVoiceInteractionSession;
import android.util.IntArray;
import android.util.proto.ProtoOutputStream;
import android.window.TaskSnapshot;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IVoiceInteractor;
import com.android.server.p006am.PendingIntentRecord;
import com.android.server.p006am.UserState;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
/* renamed from: com.android.server.wm.ActivityTaskManagerInternal */
/* loaded from: classes2.dex */
public abstract class ActivityTaskManagerInternal {

    /* renamed from: com.android.server.wm.ActivityTaskManagerInternal$PackageConfigurationUpdater */
    /* loaded from: classes2.dex */
    public interface PackageConfigurationUpdater {
        boolean commit();

        PackageConfigurationUpdater setGrammaticalGender(@Configuration.GrammaticalGender int i);

        PackageConfigurationUpdater setLocales(LocaleList localeList);

        PackageConfigurationUpdater setNightMode(int i);
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerInternal$ScreenObserver */
    /* loaded from: classes2.dex */
    public interface ScreenObserver {
        void onAwakeStateChanged(boolean z);

        void onKeyguardStateChanged(boolean z);
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerInternal$SleepTokenAcquirer */
    /* loaded from: classes2.dex */
    public interface SleepTokenAcquirer {
        void acquire(int i);

        void release(int i);
    }

    public abstract boolean attachApplication(WindowProcessController windowProcessController) throws RemoteException;

    public abstract boolean canCloseSystemDialogs(int i, int i2);

    public abstract boolean canGcNow();

    public abstract boolean canShowErrorDialogs();

    public abstract boolean checkCanCloseSystemDialogs(int i, int i2, String str);

    public abstract void cleanupDisabledPackageComponents(String str, Set<String> set, int i, boolean z);

    public abstract void cleanupRecentTasksForUser(int i);

    public abstract void clearHeavyWeightProcessIfEquals(WindowProcessController windowProcessController);

    public abstract void clearLockedTasks(String str);

    public abstract void clearPendingResultForActivity(IBinder iBinder, WeakReference<PendingIntentRecord> weakReference);

    public abstract void closeSystemDialogs(String str);

    public abstract CompatibilityInfo compatibilityInfoForPackage(ApplicationInfo applicationInfo);

    public abstract PackageConfigurationUpdater createPackageConfigurationUpdater();

    public abstract PackageConfigurationUpdater createPackageConfigurationUpdater(String str, int i);

    public abstract SleepTokenAcquirer createSleepTokenAcquirer(String str);

    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, boolean z2, String str2, int i2);

    public abstract boolean dumpActivity(FileDescriptor fileDescriptor, PrintWriter printWriter, String str, String[] strArr, int i, boolean z, boolean z2, boolean z3, boolean z4, int i2, int i3);

    public abstract void dumpForOom(PrintWriter printWriter);

    public abstract boolean dumpForProcesses(FileDescriptor fileDescriptor, PrintWriter printWriter, boolean z, String str, int i, boolean z2, boolean z3, int i2);

    public abstract void enableScreenAfterBoot(boolean z);

    public abstract void finishHeavyWeightApp();

    public abstract int finishTopCrashedActivities(WindowProcessController windowProcessController, String str);

    public abstract void flushRecentTasks();

    public abstract ComponentName getActivityName(IBinder iBinder);

    public abstract List<ActivityManager.AppTask> getAppTasks(String str, int i);

    public abstract PackageConfig getApplicationConfig(String str, int i);

    public abstract ActivityTokens getAttachedNonFinishingActivityForTask(int i, IBinder iBinder);

    public abstract ComponentName getHomeActivityForUser(int i);

    public abstract Intent getHomeIntent();

    public abstract IIntentSender getIntentSender(int i, String str, String str2, int i2, int i3, IBinder iBinder, String str3, int i4, Intent[] intentArr, String[] strArr, int i5, Bundle bundle);

    public abstract ActivityMetricsLaunchObserverRegistry getLaunchObserverRegistry();

    public abstract ActivityManager.RecentTaskInfo getMostRecentTaskFromBackground();

    public abstract ActivityServiceConnectionsHolder getServiceConnectionsHolder(IBinder iBinder);

    public abstract TaskSnapshot getTaskSnapshotBlocking(int i, boolean z);

    public abstract int getTaskToShowPermissionDialogOn(String str, int i);

    public abstract WindowProcessController getTopApp();

    public abstract int getTopProcessState();

    public abstract List<ActivityAssistInfo> getTopVisibleActivities();

    public abstract IBinder getUriPermissionOwnerForActivity(IBinder iBinder);

    public abstract boolean handleAppCrashInActivityController(String str, int i, String str2, String str3, long j, String str4, Runnable runnable);

    public abstract void handleAppDied(WindowProcessController windowProcessController, boolean z, Runnable runnable);

    public abstract boolean hasResumedActivity(int i);

    public abstract boolean hasSystemAlertWindowPermission(int i, int i2, String str);

    public abstract boolean isBaseOfLockedTask(String str);

    public abstract boolean isCallerRecents(int i);

    public abstract boolean isGetTasksAllowed(String str, int i, int i2);

    public abstract boolean isShuttingDown();

    public abstract boolean isSleeping();

    public abstract boolean isUidForeground(int i);

    public abstract void loadRecentTasksForUser(int i);

    public abstract void notifyActiveDreamChanged(ComponentName componentName);

    public abstract void notifyActiveVoiceInteractionServiceChanged(ComponentName componentName);

    public abstract void notifyLockedProfile(int i, int i2);

    public abstract void onCleanUpApplicationRecord(WindowProcessController windowProcessController);

    public abstract boolean onForceStopPackage(String str, boolean z, boolean z2, int i);

    public abstract void onHandleAppCrash(WindowProcessController windowProcessController);

    public abstract void onLocalVoiceInteractionStarted(IBinder iBinder, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor);

    public abstract void onPackageAdded(String str, boolean z);

    public abstract void onPackageDataCleared(String str, int i);

    public abstract void onPackageReplaced(ApplicationInfo applicationInfo);

    public abstract void onPackageUninstalled(String str, int i);

    public abstract void onPackagesSuspendedChanged(String[] strArr, boolean z, int i);

    public abstract void onProcessAdded(WindowProcessController windowProcessController);

    public abstract void onProcessMapped(int i, WindowProcessController windowProcessController);

    public abstract void onProcessRemoved(String str, int i);

    public abstract void onProcessUnMapped(int i);

    public abstract void onUidActive(int i, int i2);

    public abstract void onUidInactive(int i);

    public abstract void onUidProcStateChanged(int i, int i2);

    public abstract void onUserStopped(int i);

    public abstract void preBindApplication(WindowProcessController windowProcessController);

    public abstract void registerActivityStartInterceptor(int i, ActivityInterceptorCallback activityInterceptorCallback);

    public abstract void registerScreenObserver(ScreenObserver screenObserver);

    public abstract void registerTaskStackListener(ITaskStackListener iTaskStackListener);

    public abstract void removeRecentTasksByPackageName(String str, int i);

    public abstract void removeUser(int i);

    public abstract void restartTaskActivityProcessIfVisible(int i, String str);

    public abstract void resumeTopActivities(boolean z);

    public abstract void scheduleDestroyAllActivities(String str);

    public abstract void sendActivityResult(int i, IBinder iBinder, String str, int i2, int i3, Intent intent);

    public abstract void setAccessibilityServiceUids(IntArray intArray);

    public abstract void setAllowAppSwitches(String str, int i, int i2);

    public abstract void setBackgroundActivityStartCallback(BackgroundActivityStartCallback backgroundActivityStartCallback);

    public abstract void setCompanionAppUids(int i, Set<Integer> set);

    public abstract void setDeviceOwnerUid(int i);

    public abstract void setFocusedActivity(IBinder iBinder);

    public abstract void setProfileApp(String str);

    public abstract void setProfileProc(WindowProcessController windowProcessController);

    public abstract void setProfilerInfo(ProfilerInfo profilerInfo);

    public abstract void setVr2dDisplayId(int i);

    public abstract boolean showStrictModeViolationDialog();

    public abstract void showSystemReadyErrorDialogsIfNeeded();

    public abstract boolean shuttingDown(boolean z, int i);

    public abstract int startActivitiesAsPackage(String str, String str2, int i, Intent[] intentArr, Bundle bundle);

    public abstract int startActivitiesInPackage(int i, int i2, int i3, String str, String str2, Intent[] intentArr, String[] strArr, IBinder iBinder, SafeActivityOptions safeActivityOptions, int i4, boolean z, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges);

    public abstract int startActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, Intent intent, IBinder iBinder, int i, Bundle bundle, int i2);

    public abstract int startActivityInPackage(int i, int i2, int i3, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i4, int i5, SafeActivityOptions safeActivityOptions, int i6, Task task, String str5, boolean z, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges);

    public abstract void startConfirmDeviceCredentialIntent(Intent intent, Bundle bundle);

    public abstract boolean startHomeActivity(int i, String str);

    public abstract boolean startHomeOnAllDisplays(int i, String str);

    public abstract boolean startHomeOnDisplay(int i, String str, int i2, boolean z, boolean z2);

    public abstract boolean switchUser(int i, UserState userState);

    public abstract void unregisterActivityStartInterceptor(int i);

    public abstract void updateTopComponentForFactoryTest();

    public abstract void updateUserConfiguration();

    public abstract boolean useTopSchedGroupForTopProcess();

    public abstract void writeActivitiesToProto(ProtoOutputStream protoOutputStream);

    public abstract void writeProcessesToProto(ProtoOutputStream protoOutputStream, String str, int i, boolean z);

    /* renamed from: com.android.server.wm.ActivityTaskManagerInternal$ActivityTokens */
    /* loaded from: classes2.dex */
    public final class ActivityTokens {
        public final IBinder mActivityToken;
        public final IApplicationThread mAppThread;
        public final IBinder mAssistToken;
        public final IBinder mShareableActivityToken;
        public final int mUid;

        public ActivityTokens(IBinder iBinder, IBinder iBinder2, IApplicationThread iApplicationThread, IBinder iBinder3, int i) {
            this.mActivityToken = iBinder;
            this.mAssistToken = iBinder2;
            this.mAppThread = iApplicationThread;
            this.mShareableActivityToken = iBinder3;
            this.mUid = i;
        }

        public IBinder getActivityToken() {
            return this.mActivityToken;
        }

        public IBinder getAssistToken() {
            return this.mAssistToken;
        }

        public IApplicationThread getApplicationThread() {
            return this.mAppThread;
        }

        public int getUid() {
            return this.mUid;
        }
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerInternal$PackageConfig */
    /* loaded from: classes2.dex */
    public static class PackageConfig {
        @Configuration.GrammaticalGender
        public final Integer mGrammaticalGender;
        public final LocaleList mLocales;
        public final Integer mNightMode;

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
        public PackageConfig(Integer num, LocaleList localeList, @Configuration.GrammaticalGender Integer num2) {
            this.mNightMode = num;
            this.mLocales = localeList;
            this.mGrammaticalGender = num2;
        }

        public String toString() {
            return "PackageConfig: nightMode " + this.mNightMode + " locales " + this.mLocales;
        }
    }
}
