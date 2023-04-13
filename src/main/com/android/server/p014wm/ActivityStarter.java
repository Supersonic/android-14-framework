package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.BackgroundStartPrivileges;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.app.WaitResult;
import android.content.Context;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.AuxiliaryResolveInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.p005os.IInstalld;
import android.service.voice.IVoiceInteractionSession;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Pools;
import android.util.Slog;
import android.view.RemoteAnimationAdapter;
import android.widget.Toast;
import android.window.RemoteTransition;
import android.window.WindowContainerToken;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.HeavyWeightSwitcherActivity;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.UiThread;
import com.android.server.p006am.PendingIntentRecord;
import com.android.server.p011pm.InstantAppResolver;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p014wm.ActivityMetricsLogger;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.LaunchParamsController;
import com.android.server.power.ShutdownCheckPoints;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.uri.NeededUriGrants;
import com.android.server.uri.UriGrantsManagerInternal;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.ActivityStarter */
/* loaded from: classes2.dex */
public class ActivityStarter {
    @VisibleForTesting
    boolean mAddingToTask;
    public TaskFragment mAddingToTaskFragment;
    public boolean mAvoidMoveToFront;
    public int mBalCode;
    public int mCallingUid;
    public final ActivityStartController mController;
    public boolean mDisplayLockAndOccluded;
    public boolean mDoResume;
    public boolean mFrozeTaskList;
    public Task mInTask;
    public TaskFragment mInTaskFragment;
    public Intent mIntent;
    public boolean mIntentDelivered;
    public final ActivityStartInterceptor mInterceptor;
    public boolean mIsTaskCleared;
    public ActivityRecord mLastStartActivityRecord;
    public int mLastStartActivityResult;
    public long mLastStartActivityTimeMs;
    public String mLastStartReason;
    public int mLaunchFlags;
    public int mLaunchMode;
    public boolean mLaunchTaskBehind;
    public boolean mMovedToFront;
    @VisibleForTesting
    ActivityRecord mMovedToTopActivity;
    public boolean mNoAnimation;
    public ActivityRecord mNotTop;
    public ActivityOptions mOptions;
    public TaskDisplayArea mPreferredTaskDisplayArea;
    public int mPreferredWindowingMode;
    public Task mPriorAboveTask;
    public int mRealCallingUid;
    public final RootWindowContainer mRootWindowContainer;
    public final ActivityTaskManagerService mService;
    public ActivityRecord mSourceRecord;
    public Task mSourceRootTask;
    @VisibleForTesting
    ActivityRecord mStartActivity;
    public int mStartFlags;
    public final ActivityTaskSupervisor mSupervisor;
    public Task mTargetRootTask;
    public Task mTargetTask;
    public boolean mTransientLaunch;
    public IVoiceInteractor mVoiceInteractor;
    public IVoiceInteractionSession mVoiceSession;
    public LaunchParamsController.LaunchParams mLaunchParams = new LaunchParamsController.LaunchParams();
    @VisibleForTesting
    Request mRequest = new Request();

    @VisibleForTesting
    /* renamed from: com.android.server.wm.ActivityStarter$Factory */
    /* loaded from: classes2.dex */
    public interface Factory {
        ActivityStarter obtain();

        void recycle(ActivityStarter activityStarter);

        void setController(ActivityStartController activityStartController);
    }

    public static int computeResolveFilterUid(int i, int i2, int i3) {
        return i3 != -10000 ? i3 : i >= 0 ? i : i2;
    }

    public static int getExternalResult(int i) {
        if (i != 102) {
            return i;
        }
        return 0;
    }

    public static boolean isDocumentLaunchesIntoExisting(int i) {
        return (524288 & i) != 0 && (i & 134217728) == 0;
    }

    /* renamed from: com.android.server.wm.ActivityStarter$DefaultFactory */
    /* loaded from: classes2.dex */
    public static class DefaultFactory implements Factory {
        public ActivityStartController mController;
        public ActivityStartInterceptor mInterceptor;
        public ActivityTaskManagerService mService;
        public ActivityTaskSupervisor mSupervisor;
        public final int MAX_STARTER_COUNT = 3;
        public Pools.SynchronizedPool<ActivityStarter> mStarterPool = new Pools.SynchronizedPool<>(3);

        public DefaultFactory(ActivityTaskManagerService activityTaskManagerService, ActivityTaskSupervisor activityTaskSupervisor, ActivityStartInterceptor activityStartInterceptor) {
            this.mService = activityTaskManagerService;
            this.mSupervisor = activityTaskSupervisor;
            this.mInterceptor = activityStartInterceptor;
        }

        @Override // com.android.server.p014wm.ActivityStarter.Factory
        public void setController(ActivityStartController activityStartController) {
            this.mController = activityStartController;
        }

        @Override // com.android.server.p014wm.ActivityStarter.Factory
        public ActivityStarter obtain() {
            ActivityStarter activityStarter = (ActivityStarter) this.mStarterPool.acquire();
            if (activityStarter == null) {
                ActivityTaskManagerService activityTaskManagerService = this.mService;
                if (activityTaskManagerService.mRootWindowContainer == null) {
                    throw new IllegalStateException("Too early to start activity.");
                }
                return new ActivityStarter(this.mController, activityTaskManagerService, this.mSupervisor, this.mInterceptor);
            }
            return activityStarter;
        }

        @Override // com.android.server.p014wm.ActivityStarter.Factory
        public void recycle(ActivityStarter activityStarter) {
            activityStarter.reset(true);
            this.mStarterPool.release(activityStarter);
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.wm.ActivityStarter$Request */
    /* loaded from: classes2.dex */
    public static class Request {
        public ActivityInfo activityInfo;
        public SafeActivityOptions activityOptions;
        public boolean allowPendingRemoteAnimationRegistryLookup;
        public boolean avoidMoveToFront;
        public BackgroundStartPrivileges backgroundStartPrivileges;
        public IApplicationThread caller;
        public String callingFeatureId;
        public String callingPackage;
        public boolean componentSpecified;
        public Intent ephemeralIntent;
        public IBinder errorCallbackToken;
        public int filterCallingUid;
        public Configuration globalConfig;
        public boolean ignoreTargetSecurity;
        public Task inTask;
        public TaskFragment inTaskFragment;
        public Intent intent;
        public NeededUriGrants intentGrants;
        public PendingIntentRecord originatingPendingIntent;
        public ActivityRecord[] outActivity;
        public ProfilerInfo profilerInfo;
        public String reason;
        public int requestCode;
        public ResolveInfo resolveInfo;
        public String resolvedType;
        public IBinder resultTo;
        public String resultWho;
        public int startFlags;
        public int userId;
        public IVoiceInteractor voiceInteractor;
        public IVoiceInteractionSession voiceSession;
        public WaitResult waitResult;
        public int callingPid = 0;
        public int callingUid = -1;
        public int realCallingPid = 0;
        public int realCallingUid = -1;

        public Request() {
            reset();
        }

        public void reset() {
            this.caller = null;
            this.intent = null;
            this.intentGrants = null;
            this.ephemeralIntent = null;
            this.resolvedType = null;
            this.activityInfo = null;
            this.resolveInfo = null;
            this.voiceSession = null;
            this.voiceInteractor = null;
            this.resultTo = null;
            this.resultWho = null;
            this.requestCode = 0;
            this.callingPid = 0;
            this.callingUid = -1;
            this.callingPackage = null;
            this.callingFeatureId = null;
            this.realCallingPid = 0;
            this.realCallingUid = -1;
            this.startFlags = 0;
            this.activityOptions = null;
            this.ignoreTargetSecurity = false;
            this.componentSpecified = false;
            this.outActivity = null;
            this.inTask = null;
            this.inTaskFragment = null;
            this.reason = null;
            this.profilerInfo = null;
            this.globalConfig = null;
            this.userId = 0;
            this.waitResult = null;
            this.avoidMoveToFront = false;
            this.allowPendingRemoteAnimationRegistryLookup = true;
            this.filterCallingUid = -10000;
            this.originatingPendingIntent = null;
            this.backgroundStartPrivileges = BackgroundStartPrivileges.NONE;
            this.errorCallbackToken = null;
        }

        public void set(Request request) {
            this.caller = request.caller;
            this.intent = request.intent;
            this.intentGrants = request.intentGrants;
            this.ephemeralIntent = request.ephemeralIntent;
            this.resolvedType = request.resolvedType;
            this.activityInfo = request.activityInfo;
            this.resolveInfo = request.resolveInfo;
            this.voiceSession = request.voiceSession;
            this.voiceInteractor = request.voiceInteractor;
            this.resultTo = request.resultTo;
            this.resultWho = request.resultWho;
            this.requestCode = request.requestCode;
            this.callingPid = request.callingPid;
            this.callingUid = request.callingUid;
            this.callingPackage = request.callingPackage;
            this.callingFeatureId = request.callingFeatureId;
            this.realCallingPid = request.realCallingPid;
            this.realCallingUid = request.realCallingUid;
            this.startFlags = request.startFlags;
            this.activityOptions = request.activityOptions;
            this.ignoreTargetSecurity = request.ignoreTargetSecurity;
            this.componentSpecified = request.componentSpecified;
            this.outActivity = request.outActivity;
            this.inTask = request.inTask;
            this.inTaskFragment = request.inTaskFragment;
            this.reason = request.reason;
            this.profilerInfo = request.profilerInfo;
            this.globalConfig = request.globalConfig;
            this.userId = request.userId;
            this.waitResult = request.waitResult;
            this.avoidMoveToFront = request.avoidMoveToFront;
            this.allowPendingRemoteAnimationRegistryLookup = request.allowPendingRemoteAnimationRegistryLookup;
            this.filterCallingUid = request.filterCallingUid;
            this.originatingPendingIntent = request.originatingPendingIntent;
            this.backgroundStartPrivileges = request.backgroundStartPrivileges;
            this.errorCallbackToken = request.errorCallbackToken;
        }

        /* JADX WARN: Removed duplicated region for block: B:58:0x010c  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void resolveActivity(ActivityTaskSupervisor activityTaskSupervisor) {
            UserInfo userInfo;
            boolean z;
            if (this.realCallingPid == 0) {
                this.realCallingPid = Binder.getCallingPid();
            }
            if (this.realCallingUid == -1) {
                this.realCallingUid = Binder.getCallingUid();
            }
            if (this.callingUid >= 0) {
                this.callingPid = -1;
            } else if (this.caller == null) {
                this.callingPid = this.realCallingPid;
                this.callingUid = this.realCallingUid;
            } else {
                this.callingUid = -1;
                this.callingPid = -1;
            }
            int i = this.callingUid;
            if (this.caller != null) {
                synchronized (activityTaskSupervisor.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        WindowProcessController processController = activityTaskSupervisor.mService.getProcessController(this.caller);
                        if (processController != null) {
                            i = processController.mInfo.uid;
                        }
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
            this.ephemeralIntent = new Intent(this.intent);
            Intent intent = new Intent(this.intent);
            this.intent = intent;
            if (intent.getComponent() != null && ((!"android.intent.action.VIEW".equals(this.intent.getAction()) || this.intent.getData() != null) && !"android.intent.action.INSTALL_INSTANT_APP_PACKAGE".equals(this.intent.getAction()) && !"android.intent.action.RESOLVE_INSTANT_APP_PACKAGE".equals(this.intent.getAction()) && activityTaskSupervisor.mService.getPackageManagerInternalLocked().isInstantAppInstallerComponent(this.intent.getComponent()))) {
                this.intent.setComponent(null);
            }
            ResolveInfo resolveIntent = activityTaskSupervisor.resolveIntent(this.intent, this.resolvedType, this.userId, 0, ActivityStarter.computeResolveFilterUid(this.callingUid, this.realCallingUid, this.filterCallingUid), this.realCallingPid);
            this.resolveInfo = resolveIntent;
            if (resolveIntent == null && (userInfo = activityTaskSupervisor.getUserInfo(this.userId)) != null && userInfo.isManagedProfile()) {
                UserManager userManager = UserManager.get(activityTaskSupervisor.mService.mContext);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    UserInfo profileParent = userManager.getProfileParent(this.userId);
                    if (profileParent != null && userManager.isUserUnlockingOrUnlocked(profileParent.id)) {
                        if (!userManager.isUserUnlockingOrUnlocked(this.userId)) {
                            z = true;
                            if (z) {
                                this.resolveInfo = activityTaskSupervisor.resolveIntent(this.intent, this.resolvedType, this.userId, 786432, ActivityStarter.computeResolveFilterUid(this.callingUid, this.realCallingUid, this.filterCallingUid), this.realCallingPid);
                            }
                        }
                    }
                    z = false;
                    if (z) {
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            ActivityInfo resolveActivity = activityTaskSupervisor.resolveActivity(this.intent, this.resolveInfo, this.startFlags, this.profilerInfo);
            this.activityInfo = resolveActivity;
            if (resolveActivity != null) {
                UriGrantsManagerInternal uriGrantsManagerInternal = activityTaskSupervisor.mService.mUgmInternal;
                Intent intent2 = this.intent;
                ApplicationInfo applicationInfo = resolveActivity.applicationInfo;
                this.intentGrants = uriGrantsManagerInternal.checkGrantUriPermissionFromIntent(intent2, i, applicationInfo.packageName, UserHandle.getUserId(applicationInfo.uid));
            }
        }
    }

    public ActivityStarter(ActivityStartController activityStartController, ActivityTaskManagerService activityTaskManagerService, ActivityTaskSupervisor activityTaskSupervisor, ActivityStartInterceptor activityStartInterceptor) {
        this.mController = activityStartController;
        this.mService = activityTaskManagerService;
        this.mRootWindowContainer = activityTaskManagerService.mRootWindowContainer;
        this.mSupervisor = activityTaskSupervisor;
        this.mInterceptor = activityStartInterceptor;
        reset(true);
    }

    public void set(ActivityStarter activityStarter) {
        this.mStartActivity = activityStarter.mStartActivity;
        this.mIntent = activityStarter.mIntent;
        this.mCallingUid = activityStarter.mCallingUid;
        this.mRealCallingUid = activityStarter.mRealCallingUid;
        this.mOptions = activityStarter.mOptions;
        this.mBalCode = activityStarter.mBalCode;
        this.mLaunchTaskBehind = activityStarter.mLaunchTaskBehind;
        this.mLaunchFlags = activityStarter.mLaunchFlags;
        this.mLaunchMode = activityStarter.mLaunchMode;
        this.mLaunchParams.set(activityStarter.mLaunchParams);
        this.mNotTop = activityStarter.mNotTop;
        this.mDoResume = activityStarter.mDoResume;
        this.mStartFlags = activityStarter.mStartFlags;
        this.mSourceRecord = activityStarter.mSourceRecord;
        this.mPreferredTaskDisplayArea = activityStarter.mPreferredTaskDisplayArea;
        this.mPreferredWindowingMode = activityStarter.mPreferredWindowingMode;
        this.mInTask = activityStarter.mInTask;
        this.mInTaskFragment = activityStarter.mInTaskFragment;
        this.mAddingToTask = activityStarter.mAddingToTask;
        this.mSourceRootTask = activityStarter.mSourceRootTask;
        this.mTargetTask = activityStarter.mTargetTask;
        this.mTargetRootTask = activityStarter.mTargetRootTask;
        this.mIsTaskCleared = activityStarter.mIsTaskCleared;
        this.mMovedToFront = activityStarter.mMovedToFront;
        this.mNoAnimation = activityStarter.mNoAnimation;
        this.mAvoidMoveToFront = activityStarter.mAvoidMoveToFront;
        this.mFrozeTaskList = activityStarter.mFrozeTaskList;
        this.mVoiceSession = activityStarter.mVoiceSession;
        this.mVoiceInteractor = activityStarter.mVoiceInteractor;
        this.mIntentDelivered = activityStarter.mIntentDelivered;
        this.mLastStartActivityResult = activityStarter.mLastStartActivityResult;
        this.mLastStartActivityTimeMs = activityStarter.mLastStartActivityTimeMs;
        this.mLastStartReason = activityStarter.mLastStartReason;
        this.mRequest.set(activityStarter.mRequest);
    }

    public boolean relatedToPackage(String str) {
        ActivityRecord activityRecord;
        ActivityRecord activityRecord2 = this.mLastStartActivityRecord;
        return (activityRecord2 != null && str.equals(activityRecord2.packageName)) || ((activityRecord = this.mStartActivity) != null && str.equals(activityRecord.packageName));
    }

    public int execute() {
        ActivityMetricsLogger.LaunchingState notifyActivityLaunching;
        try {
            onExecutionStarted();
            Intent intent = this.mRequest.intent;
            if (intent != null && intent.hasFileDescriptors()) {
                throw new IllegalArgumentException("File descriptors passed in Intent");
            }
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(this.mRequest.resultTo);
                int i = this.mRequest.realCallingUid;
                if (i == -1) {
                    i = Binder.getCallingUid();
                }
                notifyActivityLaunching = this.mSupervisor.getActivityMetricsLogger().notifyActivityLaunching(this.mRequest.intent, forTokenLocked, i);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Request request = this.mRequest;
            if (request.activityInfo == null) {
                request.resolveActivity(this.mSupervisor);
            }
            Intent intent2 = this.mRequest.intent;
            if (intent2 != null) {
                String action = intent2.getAction();
                String str = this.mRequest.callingPackage;
                if (action != null && str != null && ("com.android.internal.intent.action.REQUEST_SHUTDOWN".equals(action) || "android.intent.action.ACTION_SHUTDOWN".equals(action) || "android.intent.action.REBOOT".equals(action))) {
                    ShutdownCheckPoints.recordCheckPoint(action, str, null);
                }
            }
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                boolean z = (this.mRequest.globalConfig == null || this.mService.getGlobalConfiguration().diff(this.mRequest.globalConfig) == 0) ? false : true;
                Task topDisplayFocusedRootTask = this.mRootWindowContainer.getTopDisplayFocusedRootTask();
                if (topDisplayFocusedRootTask != null) {
                    topDisplayFocusedRootTask.mConfigWillChange = z;
                }
                if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -1492881555, 3, (String) null, new Object[]{Boolean.valueOf(z)});
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                int resolveToHeavyWeightSwitcherIfNeeded = resolveToHeavyWeightSwitcherIfNeeded();
                if (resolveToHeavyWeightSwitcherIfNeeded == 0) {
                    int executeRequest = executeRequest(this.mRequest);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    if (z) {
                        this.mService.mAmInternal.enforceCallingPermission("android.permission.CHANGE_CONFIGURATION", "updateConfiguration()");
                        if (topDisplayFocusedRootTask != null) {
                            topDisplayFocusedRootTask.mConfigWillChange = false;
                        }
                        if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -1868048288, 0, (String) null, (Object[]) null);
                        }
                        this.mService.updateConfigurationLocked(this.mRequest.globalConfig, null, false);
                    }
                    SafeActivityOptions safeActivityOptions = this.mRequest.activityOptions;
                    ActivityOptions originalOptions = safeActivityOptions != null ? safeActivityOptions.getOriginalOptions() : null;
                    ActivityRecord activityRecord = this.mDoResume ? this.mLastStartActivityRecord : null;
                    this.mSupervisor.getActivityMetricsLogger().notifyActivityLaunched(notifyActivityLaunching, executeRequest, this.mStartActivity == activityRecord, activityRecord, originalOptions);
                    WaitResult waitResult = this.mRequest.waitResult;
                    if (waitResult != null) {
                        waitResult.result = executeRequest;
                        executeRequest = waitResultIfNeeded(waitResult, this.mLastStartActivityRecord, notifyActivityLaunching);
                    }
                    int externalResult = getExternalResult(executeRequest);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return externalResult;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return resolveToHeavyWeightSwitcherIfNeeded;
            }
        } finally {
            onExecutionComplete();
        }
    }

    public final int resolveToHeavyWeightSwitcherIfNeeded() {
        WindowProcessController windowProcessController;
        ActivityInfo activityInfo = this.mRequest.activityInfo;
        if (activityInfo != null && this.mService.mHasHeavyWeightFeature) {
            ApplicationInfo applicationInfo = activityInfo.applicationInfo;
            if ((applicationInfo.privateFlags & 2) != 0 && activityInfo.processName.equals(applicationInfo.packageName) && (windowProcessController = this.mService.mHeavyWeightProcess) != null) {
                int i = windowProcessController.mInfo.uid;
                ActivityInfo activityInfo2 = this.mRequest.activityInfo;
                if (i != activityInfo2.applicationInfo.uid || !windowProcessController.mName.equals(activityInfo2.processName)) {
                    Request request = this.mRequest;
                    int i2 = request.callingUid;
                    IApplicationThread iApplicationThread = request.caller;
                    if (iApplicationThread != null) {
                        WindowProcessController processController = this.mService.getProcessController(iApplicationThread);
                        if (processController != null) {
                            i2 = processController.mInfo.uid;
                        } else {
                            Slog.w("ActivityTaskManager", "Unable to find app for caller " + this.mRequest.caller + " (pid=" + this.mRequest.callingPid + ") when starting: " + this.mRequest.intent.toString());
                            SafeActivityOptions.abort(this.mRequest.activityOptions);
                            return -94;
                        }
                    }
                    ActivityTaskManagerService activityTaskManagerService = this.mService;
                    Request request2 = this.mRequest;
                    IIntentSender intentSenderLocked = activityTaskManagerService.getIntentSenderLocked(2, PackageManagerShellCommandDataLoader.PACKAGE, null, i2, request2.userId, null, null, 0, new Intent[]{request2.intent}, new String[]{request2.resolvedType}, 1342177280, null);
                    Intent intent = new Intent();
                    if (this.mRequest.requestCode >= 0) {
                        intent.putExtra("has_result", true);
                    }
                    intent.putExtra("intent", new IntentSender(intentSenderLocked));
                    windowProcessController.updateIntentForHeavyWeightActivity(intent);
                    intent.putExtra("new_app", this.mRequest.activityInfo.packageName);
                    intent.setFlags(this.mRequest.intent.getFlags());
                    intent.setClassName(PackageManagerShellCommandDataLoader.PACKAGE, HeavyWeightSwitcherActivity.class.getName());
                    Request request3 = this.mRequest;
                    request3.intent = intent;
                    request3.resolvedType = null;
                    request3.caller = null;
                    request3.callingUid = Binder.getCallingUid();
                    this.mRequest.callingPid = Binder.getCallingPid();
                    Request request4 = this.mRequest;
                    request4.componentSpecified = true;
                    request4.resolveInfo = this.mSupervisor.resolveIntent(request4.intent, null, request4.userId, 0, computeResolveFilterUid(request4.callingUid, request4.realCallingUid, request4.filterCallingUid), this.mRequest.realCallingPid);
                    Request request5 = this.mRequest;
                    ResolveInfo resolveInfo = request5.resolveInfo;
                    ActivityInfo activityInfo3 = resolveInfo != null ? resolveInfo.activityInfo : null;
                    request5.activityInfo = activityInfo3;
                    if (activityInfo3 != null) {
                        request5.activityInfo = this.mService.mAmInternal.getActivityInfoForUser(activityInfo3, request5.userId);
                    }
                }
            }
        }
        return 0;
    }

    public final int waitResultIfNeeded(WaitResult waitResult, ActivityRecord activityRecord, ActivityMetricsLogger.LaunchingState launchingState) {
        int i = waitResult.result;
        if (i == 3 || (i == 2 && activityRecord.nowVisible && activityRecord.isState(ActivityRecord.State.RESUMED))) {
            waitResult.timeout = false;
            waitResult.who = activityRecord.mActivityComponent;
            waitResult.totalTime = 0L;
            return i;
        }
        this.mSupervisor.waitActivityVisibleOrLaunched(waitResult, activityRecord, launchingState);
        if (i == 0 && waitResult.result == 2) {
            return 2;
        }
        return i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:156:0x0421  */
    /* JADX WARN: Removed duplicated region for block: B:160:0x0470  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x0487 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:170:0x04e2  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x04e5  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0501 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:178:0x050d  */
    /* JADX WARN: Removed duplicated region for block: B:183:0x051c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:187:0x053f  */
    /* JADX WARN: Removed duplicated region for block: B:223:0x0239 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x01de A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0217  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x021a  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x0222  */
    /* JADX WARN: Type inference failed for: r18v11 */
    /* JADX WARN: Type inference failed for: r18v16 */
    /* JADX WARN: Type inference failed for: r18v8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int executeRequest(Request request) {
        String str;
        Bundle bundle;
        int i;
        int i2;
        WindowProcessController windowProcessController;
        int i3;
        int i4;
        IVoiceInteractionSession iVoiceInteractionSession;
        Task task;
        ActivityRecord activityRecord;
        ActivityRecord activityRecord2;
        ActivityRecord activityRecord3;
        int i5;
        String str2;
        String str3;
        String str4;
        String str5;
        int i6;
        int i7;
        int i8;
        Request request2;
        int i9;
        String str6;
        int i10;
        int i11;
        String str7;
        int i12;
        ActivityOptions activityOptions;
        ActivityInfo activityInfo;
        Task task2;
        int i13;
        int i14;
        int i15;
        boolean z;
        Intent intent;
        String str8;
        ResolveInfo resolveInfo;
        int i16;
        boolean z2;
        ActivityOptions activityOptions2;
        Task task3;
        int i17;
        TaskFragment taskFragment;
        String str9;
        boolean z3;
        int i18;
        Request request3;
        String str10;
        int i19;
        ?? r18;
        ActivityRecord build;
        ActivityRecord[] activityRecordArr;
        ApplicationInfo applicationInfo;
        if (TextUtils.isEmpty(request.reason)) {
            throw new IllegalArgumentException("Need to specify a reason.");
        }
        this.mLastStartReason = request.reason;
        this.mLastStartActivityTimeMs = System.currentTimeMillis();
        this.mLastStartActivityRecord = null;
        IApplicationThread iApplicationThread = request.caller;
        Intent intent2 = request.intent;
        NeededUriGrants neededUriGrants = request.intentGrants;
        String str11 = request.resolvedType;
        ActivityInfo activityInfo2 = request.activityInfo;
        ResolveInfo resolveInfo2 = request.resolveInfo;
        IVoiceInteractionSession iVoiceInteractionSession2 = request.voiceSession;
        IBinder iBinder = request.resultTo;
        String str12 = request.resultWho;
        int i20 = request.requestCode;
        int i21 = request.callingPid;
        int i22 = request.callingUid;
        String str13 = request.callingPackage;
        String str14 = request.callingFeatureId;
        int i23 = request.realCallingPid;
        NeededUriGrants neededUriGrants2 = neededUriGrants;
        int i24 = request.realCallingUid;
        ResolveInfo resolveInfo3 = resolveInfo2;
        int i25 = request.startFlags;
        SafeActivityOptions safeActivityOptions = request.activityOptions;
        Task task4 = request.inTask;
        TaskFragment taskFragment2 = request.inTaskFragment;
        if (safeActivityOptions != null) {
            bundle = safeActivityOptions.popAppVerificationBundle();
            str = str12;
        } else {
            str = str12;
            bundle = null;
        }
        if (iApplicationThread != null) {
            WindowProcessController processController = this.mService.getProcessController(iApplicationThread);
            if (processController != null) {
                int pid = processController.getPid();
                i22 = processController.mInfo.uid;
                i2 = 0;
                i = pid;
                windowProcessController = processController;
            } else {
                Slog.w("ActivityTaskManager", "Unable to find app for caller " + iApplicationThread + " (pid=" + i21 + ") when starting: " + intent2.toString());
                i2 = -94;
                i = i21;
                i22 = i22;
                windowProcessController = processController;
            }
        } else {
            i = i21;
            i2 = 0;
            windowProcessController = null;
        }
        int userId = (activityInfo2 == null || (applicationInfo = activityInfo2.applicationInfo) == null) ? 0 : UserHandle.getUserId(applicationInfo.uid);
        WindowProcessController windowProcessController2 = windowProcessController;
        if (activityInfo2 != null) {
            i4 = activityInfo2.launchMode;
            i3 = i;
        } else {
            i3 = i;
            i4 = 0;
        }
        if (i2 == 0) {
            StringBuilder sb = new StringBuilder();
            task = task4;
            sb.append("START u");
            sb.append(userId);
            sb.append(" {");
            iVoiceInteractionSession = iVoiceInteractionSession2;
            sb.append(intent2.toShortString(true, true, true, false));
            sb.append("} with ");
            sb.append(ActivityInfo.launchModeToString(i4));
            sb.append(" from uid ");
            sb.append(i22);
            Slog.i("ActivityTaskManager", sb.toString());
        } else {
            iVoiceInteractionSession = iVoiceInteractionSession2;
            task = task4;
        }
        if (iBinder != null) {
            activityRecord = ActivityRecord.isInAnyTask(iBinder);
            if (activityRecord == null || i20 < 0 || activityRecord.finishing) {
                activityRecord2 = activityRecord;
                activityRecord = null;
            } else {
                activityRecord2 = activityRecord;
            }
        } else {
            activityRecord = null;
            activityRecord2 = null;
        }
        int flags = intent2.getFlags();
        if ((33554432 & flags) == 0 || activityRecord2 == null) {
            activityRecord3 = activityRecord;
            i5 = i20;
            str2 = str14;
            str3 = str;
        } else if (i20 >= 0) {
            SafeActivityOptions.abort(safeActivityOptions);
            return -93;
        } else {
            ActivityRecord activityRecord4 = activityRecord2.resultTo;
            if (activityRecord4 != null && !activityRecord4.isInRootTaskLocked()) {
                activityRecord4 = null;
            }
            str3 = activityRecord2.resultWho;
            int i26 = activityRecord2.requestCode;
            activityRecord2.resultTo = null;
            if (activityRecord4 != null) {
                activityRecord4.removeResultsLocked(activityRecord2, str3, i26);
            }
            if (activityRecord2.launchedFromUid == i22) {
                activityRecord3 = activityRecord4;
                i5 = i26;
                str4 = activityRecord2.launchedFromPackage;
                str2 = activityRecord2.launchedFromFeatureId;
                if (i2 == 0 && intent2.getComponent() == null) {
                    i2 = -91;
                }
                if (i2 == 0 || activityInfo2 != null) {
                    str5 = str2;
                } else {
                    str5 = str2;
                    i2 = -92;
                }
                int i27 = -97;
                if (i2 == 0 || activityRecord2 == null) {
                    i6 = i2;
                } else {
                    i6 = i2;
                    if (activityRecord2.getTask().voiceSession != null && (268435456 & flags) == 0 && activityRecord2.info.applicationInfo.uid != activityInfo2.applicationInfo.uid) {
                        try {
                            intent2.addCategory("android.intent.category.VOICE");
                        } catch (RemoteException e) {
                            Slog.w("ActivityTaskManager", "Failure checking voice capabilities", e);
                        }
                        if (!this.mService.getPackageManager().activitySupportsIntentAsUser(intent2.getComponent(), intent2, str11, userId)) {
                            Slog.w("ActivityTaskManager", "Activity being started in current voice task does not support voice: " + intent2);
                            i7 = -97;
                            if (i7 == 0 || iVoiceInteractionSession == null) {
                                i8 = i7;
                            } else {
                                try {
                                    int i28 = i7;
                                    if (this.mService.getPackageManager().activitySupportsIntentAsUser(intent2.getComponent(), intent2, str11, userId)) {
                                        i27 = i28;
                                    } else {
                                        Slog.w("ActivityTaskManager", "Activity being started in new voice task does not support: " + intent2);
                                    }
                                } catch (RemoteException e2) {
                                    Slog.w("ActivityTaskManager", "Failure checking voice capabilities", e2);
                                }
                                i8 = i27;
                            }
                            Task rootTask = activityRecord3 == null ? null : activityRecord3.getRootTask();
                            if (i8 != 0) {
                                if (activityRecord3 != null) {
                                    activityRecord3.sendResult(-1, str3, i5, 0, null, null);
                                }
                                SafeActivityOptions.abort(safeActivityOptions);
                                return i8;
                            }
                            try {
                                try {
                                    int i29 = userId;
                                    try {
                                        int i30 = i5;
                                        String str15 = str3;
                                        ActivityRecord activityRecord5 = activityRecord2;
                                        ActivityRecord activityRecord6 = activityRecord3;
                                        int i31 = i22;
                                        String str16 = str4;
                                        boolean z4 = (!this.mSupervisor.checkStartAnyActivityPermission(intent2, activityInfo2, str3, i5, i3, i22, str4, str5, request.ignoreTargetSecurity, task != null, windowProcessController2, activityRecord3, rootTask)) | (!this.mService.mIntentFirewall.checkStartActivity(intent2, i22, i3, str11, activityInfo2.applicationInfo)) | (!this.mService.getPermissionPolicyInternal().checkStartActivity(intent2, i31, str16));
                                        ActivityOptions options = safeActivityOptions != null ? safeActivityOptions.getOptions(intent2, activityInfo2, windowProcessController2, this.mSupervisor) : null;
                                        if (z4) {
                                            i10 = 1;
                                        } else {
                                            try {
                                                Trace.traceBegin(32L, "shouldAbortBackgroundActivityStart");
                                                int checkBackgroundActivityStart = this.mController.getBackgroundActivityLaunchController().checkBackgroundActivityStart(i31, i3, str16, i24, i23, windowProcessController2, request.originatingPendingIntent, request.backgroundStartPrivileges, intent2, options);
                                                Trace.traceEnd(32L);
                                                i10 = checkBackgroundActivityStart;
                                            } catch (Throwable th) {
                                                Trace.traceEnd(32L);
                                                throw th;
                                            }
                                        }
                                        if (request.allowPendingRemoteAnimationRegistryLookup) {
                                            options = this.mService.getActivityStartController().getPendingRemoteAnimationRegistry().overrideOptionsIfNeeded(str16, options);
                                        }
                                        if (this.mService.mController != null) {
                                            try {
                                                z4 |= !this.mService.mController.activityStarting(intent2.cloneFilter(), activityInfo2.applicationInfo.packageName);
                                            } catch (RemoteException unused) {
                                                this.mService.mController = null;
                                            }
                                        }
                                        this.mInterceptor.setStates(i29, i23, i24, i25, str16, str5);
                                        if (this.mInterceptor.intercept(intent2, resolveInfo3, activityInfo2, str11, task, taskFragment2, i3, i31, options)) {
                                            ActivityStartInterceptor activityStartInterceptor = this.mInterceptor;
                                            intent2 = activityStartInterceptor.mIntent;
                                            ResolveInfo resolveInfo4 = activityStartInterceptor.mRInfo;
                                            ActivityInfo activityInfo3 = activityStartInterceptor.mAInfo;
                                            str7 = activityStartInterceptor.mResolvedType;
                                            Task task5 = activityStartInterceptor.mInTask;
                                            i12 = activityStartInterceptor.mCallingPid;
                                            int i32 = activityStartInterceptor.mCallingUid;
                                            activityOptions = activityStartInterceptor.mActivityOptions;
                                            resolveInfo3 = resolveInfo4;
                                            i11 = i32;
                                            activityInfo = activityInfo3;
                                            neededUriGrants2 = null;
                                            task2 = task5;
                                        } else {
                                            i11 = i31;
                                            str7 = str11;
                                            i12 = i3;
                                            activityOptions = options;
                                            activityInfo = activityInfo2;
                                            task2 = task;
                                        }
                                        if (z4) {
                                            if (activityRecord6 != null) {
                                                activityRecord6.sendResult(-1, str15, i30, 0, null, null);
                                            }
                                            ActivityOptions.abort(activityOptions);
                                            return 102;
                                        }
                                        if (activityInfo != null) {
                                            i15 = i29;
                                            if (this.mService.getPackageManagerInternalLocked().isPermissionsReviewRequired(activityInfo.packageName, i15)) {
                                                IIntentSender intentSenderLocked = this.mService.getIntentSenderLocked(2, str16, str5, i11, i15, null, null, 0, new Intent[]{intent2}, new String[]{str7}, 1342177280, null);
                                                Intent intent3 = new Intent("android.intent.action.REVIEW_PERMISSIONS");
                                                int flags2 = intent2.getFlags() | 8388608;
                                                if ((268959744 & flags2) != 0) {
                                                    flags2 |= 134217728;
                                                }
                                                intent3.setFlags(flags2);
                                                intent3.putExtra("android.intent.extra.PACKAGE_NAME", activityInfo.packageName);
                                                intent3.putExtra("android.intent.extra.INTENT", new IntentSender(intentSenderLocked));
                                                if (activityRecord6 != null) {
                                                    z = true;
                                                    intent3.putExtra("android.intent.extra.RESULT_NEEDED", true);
                                                } else {
                                                    z = true;
                                                }
                                                i14 = i24;
                                                ResolveInfo resolveIntent = this.mSupervisor.resolveIntent(intent3, null, i15, 0, computeResolveFilterUid(i14, i14, request.filterCallingUid), i23);
                                                ActivityInfo resolveActivity = this.mSupervisor.resolveActivity(intent3, resolveIntent, i25, null);
                                                str8 = null;
                                                intent = intent3;
                                                i11 = i14;
                                                i13 = i25;
                                                i12 = i23;
                                                neededUriGrants2 = null;
                                                resolveInfo = resolveIntent;
                                                activityInfo = resolveActivity;
                                                if (resolveInfo == null) {
                                                    AuxiliaryResolveInfo auxiliaryResolveInfo = resolveInfo.auxiliaryInfo;
                                                    if (auxiliaryResolveInfo != null) {
                                                        ResolveInfo resolveInfo5 = resolveInfo;
                                                        i16 = i14;
                                                        task3 = task2;
                                                        i18 = i13;
                                                        activityOptions2 = activityOptions;
                                                        i17 = i10;
                                                        z2 = true;
                                                        taskFragment = taskFragment2;
                                                        str9 = str5;
                                                        r18 = 0;
                                                        request3 = request;
                                                        Intent createLaunchIntent = createLaunchIntent(auxiliaryResolveInfo, request.ephemeralIntent, str16, str5, bundle, str8, i15);
                                                        str10 = null;
                                                        neededUriGrants2 = null;
                                                        i19 = i16;
                                                        i12 = i23;
                                                        activityInfo = this.mSupervisor.resolveActivity(createLaunchIntent, resolveInfo5, i18, null);
                                                        intent = createLaunchIntent;
                                                        if (windowProcessController2 == null || i23 <= 0 || (r4 = this.mService.mProcessMap.getProcess(i23)) == null) {
                                                            WindowProcessController windowProcessController3 = windowProcessController2;
                                                        }
                                                        ActivityOptions activityOptions3 = activityOptions2;
                                                        build = new ActivityRecord.Builder(this.mService).setCaller(windowProcessController3).setLaunchedFromPid(i12).setLaunchedFromUid(i19).setLaunchedFromPackage(str16).setLaunchedFromFeature(str9).setIntent(intent).setResolvedType(str10).setActivityInfo(activityInfo).setConfiguration(this.mService.getGlobalConfiguration()).setResultTo(activityRecord6).setResultWho(str15).setRequestCode(i30).setComponentSpecified(request3.componentSpecified).setRootVoiceInteraction(iVoiceInteractionSession != null ? z2 : r18).setActivityOptions(activityOptions3).setSourceRecord(activityRecord5).build();
                                                        this.mLastStartActivityRecord = build;
                                                        if (build.appTimeTracker == null && activityRecord5 != null) {
                                                            build.appTimeTracker = activityRecord5.appTimeTracker;
                                                        }
                                                        WindowProcessController windowProcessController4 = this.mService.mHomeProcess;
                                                        boolean z5 = (windowProcessController4 == null && activityInfo.applicationInfo.uid == windowProcessController4.mUid) ? z2 : r18;
                                                        if (i17 != 0 && !z5) {
                                                            this.mService.resumeAppSwitches();
                                                        }
                                                        int startActivityUnchecked = startActivityUnchecked(build, activityRecord5, iVoiceInteractionSession, request3.voiceInteractor, i18, activityOptions3, task3, taskFragment, i17, neededUriGrants2, i16);
                                                        this.mLastStartActivityResult = startActivityUnchecked;
                                                        activityRecordArr = request3.outActivity;
                                                        if (activityRecordArr != null) {
                                                            activityRecordArr[r18] = this.mLastStartActivityRecord;
                                                        }
                                                        return startActivityUnchecked;
                                                    }
                                                    i16 = i14;
                                                    activityOptions2 = activityOptions;
                                                    task3 = task2;
                                                    i17 = i10;
                                                    taskFragment = taskFragment2;
                                                    str9 = str5;
                                                    z3 = false;
                                                    z2 = true;
                                                } else {
                                                    i16 = i14;
                                                    z2 = z;
                                                    activityOptions2 = activityOptions;
                                                    task3 = task2;
                                                    i17 = i10;
                                                    taskFragment = taskFragment2;
                                                    str9 = str5;
                                                    z3 = false;
                                                }
                                                i18 = i13;
                                                request3 = request;
                                                str10 = str8;
                                                i19 = i11;
                                                r18 = z3;
                                                if (windowProcessController2 == null) {
                                                }
                                                WindowProcessController windowProcessController32 = windowProcessController2;
                                                ActivityOptions activityOptions32 = activityOptions2;
                                                build = new ActivityRecord.Builder(this.mService).setCaller(windowProcessController32).setLaunchedFromPid(i12).setLaunchedFromUid(i19).setLaunchedFromPackage(str16).setLaunchedFromFeature(str9).setIntent(intent).setResolvedType(str10).setActivityInfo(activityInfo).setConfiguration(this.mService.getGlobalConfiguration()).setResultTo(activityRecord6).setResultWho(str15).setRequestCode(i30).setComponentSpecified(request3.componentSpecified).setRootVoiceInteraction(iVoiceInteractionSession != null ? z2 : r18).setActivityOptions(activityOptions32).setSourceRecord(activityRecord5).build();
                                                this.mLastStartActivityRecord = build;
                                                if (build.appTimeTracker == null) {
                                                    build.appTimeTracker = activityRecord5.appTimeTracker;
                                                }
                                                WindowProcessController windowProcessController42 = this.mService.mHomeProcess;
                                                if (windowProcessController42 == null) {
                                                }
                                                if (i17 != 0) {
                                                    this.mService.resumeAppSwitches();
                                                }
                                                int startActivityUnchecked2 = startActivityUnchecked(build, activityRecord5, iVoiceInteractionSession, request3.voiceInteractor, i18, activityOptions32, task3, taskFragment, i17, neededUriGrants2, i16);
                                                this.mLastStartActivityResult = startActivityUnchecked2;
                                                activityRecordArr = request3.outActivity;
                                                if (activityRecordArr != null) {
                                                }
                                                return startActivityUnchecked2;
                                            }
                                            i13 = i25;
                                            i14 = i24;
                                        } else {
                                            i13 = i25;
                                            i14 = i24;
                                            i15 = i29;
                                        }
                                        z = true;
                                        intent = intent2;
                                        str8 = str7;
                                        resolveInfo = resolveInfo3;
                                        if (resolveInfo == null) {
                                        }
                                        i18 = i13;
                                        request3 = request;
                                        str10 = str8;
                                        i19 = i11;
                                        r18 = z3;
                                        if (windowProcessController2 == null) {
                                        }
                                        WindowProcessController windowProcessController322 = windowProcessController2;
                                        ActivityOptions activityOptions322 = activityOptions2;
                                        build = new ActivityRecord.Builder(this.mService).setCaller(windowProcessController322).setLaunchedFromPid(i12).setLaunchedFromUid(i19).setLaunchedFromPackage(str16).setLaunchedFromFeature(str9).setIntent(intent).setResolvedType(str10).setActivityInfo(activityInfo).setConfiguration(this.mService.getGlobalConfiguration()).setResultTo(activityRecord6).setResultWho(str15).setRequestCode(i30).setComponentSpecified(request3.componentSpecified).setRootVoiceInteraction(iVoiceInteractionSession != null ? z2 : r18).setActivityOptions(activityOptions322).setSourceRecord(activityRecord5).build();
                                        this.mLastStartActivityRecord = build;
                                        if (build.appTimeTracker == null) {
                                        }
                                        WindowProcessController windowProcessController422 = this.mService.mHomeProcess;
                                        if (windowProcessController422 == null) {
                                        }
                                        if (i17 != 0) {
                                        }
                                        int startActivityUnchecked22 = startActivityUnchecked(build, activityRecord5, iVoiceInteractionSession, request3.voiceInteractor, i18, activityOptions322, task3, taskFragment, i17, neededUriGrants2, i16);
                                        this.mLastStartActivityResult = startActivityUnchecked22;
                                        activityRecordArr = request3.outActivity;
                                        if (activityRecordArr != null) {
                                        }
                                        return startActivityUnchecked22;
                                    } catch (SecurityException e3) {
                                        e = e3;
                                        i9 = i5;
                                        userId = i29;
                                        request2 = request;
                                        ActivityRecord activityRecord7 = activityRecord3;
                                        int i33 = i22;
                                        Intent intent4 = request2.ephemeralIntent;
                                        if (intent4 != null && (intent4.getComponent() != null || intent4.getPackage() != null)) {
                                            if (intent4.getComponent() != null) {
                                                str6 = intent4.getComponent().getPackageName();
                                            } else {
                                                str6 = intent4.getPackage();
                                            }
                                            if (this.mService.getPackageManagerInternalLocked().filterAppAccess(str6, i33, userId)) {
                                                if (activityRecord7 != null) {
                                                    activityRecord7.sendResult(-1, str3, i9, 0, null, null);
                                                }
                                                SafeActivityOptions.abort(safeActivityOptions);
                                                return -92;
                                            }
                                        }
                                        throw e;
                                    }
                                } catch (SecurityException e4) {
                                    e = e4;
                                    i9 = i5;
                                }
                            } catch (SecurityException e5) {
                                e = e5;
                                request2 = request;
                                i9 = i5;
                            }
                        }
                    }
                }
                i7 = i6;
                if (i7 == 0) {
                }
                i8 = i7;
                if (activityRecord3 == null) {
                }
                if (i8 != 0) {
                }
            } else {
                activityRecord3 = activityRecord4;
                i5 = i26;
                str2 = str14;
            }
        }
        str4 = str13;
        if (i2 == 0) {
            i2 = -91;
        }
        if (i2 == 0) {
        }
        str5 = str2;
        int i272 = -97;
        if (i2 == 0) {
        }
        i6 = i2;
        i7 = i6;
        if (i7 == 0) {
        }
        i8 = i7;
        if (activityRecord3 == null) {
        }
        if (i8 != 0) {
        }
    }

    public final boolean handleBackgroundActivityAbort(ActivityRecord activityRecord) {
        if (!this.mService.isBackgroundActivityStartsEnabled()) {
            ActivityRecord activityRecord2 = activityRecord.resultTo;
            String str = activityRecord.resultWho;
            int i = activityRecord.requestCode;
            if (activityRecord2 != null) {
                activityRecord2.sendResult(-1, str, i, 0, null, null);
            }
            ActivityOptions.abort(activityRecord.getOptions());
            return true;
        }
        return false;
    }

    public final void onExecutionComplete() {
        this.mController.onExecutionComplete(this);
    }

    public final void onExecutionStarted() {
        this.mController.onExecutionStarted();
    }

    public final Intent createLaunchIntent(AuxiliaryResolveInfo auxiliaryResolveInfo, Intent intent, String str, String str2, Bundle bundle, String str3, int i) {
        if (auxiliaryResolveInfo != null && auxiliaryResolveInfo.needsPhaseTwo) {
            PackageManagerInternal packageManagerInternalLocked = this.mService.getPackageManagerInternalLocked();
            packageManagerInternalLocked.requestInstantAppResolutionPhaseTwo(auxiliaryResolveInfo, intent, str3, str, str2, packageManagerInternalLocked.isInstantApp(str, i), bundle, i);
        }
        return InstantAppResolver.buildEphemeralInstallerIntent(intent, InstantAppResolver.sanitizeIntent(intent), auxiliaryResolveInfo == null ? null : auxiliaryResolveInfo.failureIntent, str, str2, bundle, str3, i, auxiliaryResolveInfo == null ? null : auxiliaryResolveInfo.installFailureActivity, auxiliaryResolveInfo == null ? null : auxiliaryResolveInfo.token, auxiliaryResolveInfo != null && auxiliaryResolveInfo.needsPhaseTwo, auxiliaryResolveInfo != null ? auxiliaryResolveInfo.filters : null);
    }

    public void postStartActivityProcessing(ActivityRecord activityRecord, int i, Task task) {
        Task task2;
        if (!ActivityManager.isStartResultSuccessful(i) && this.mFrozeTaskList) {
            this.mSupervisor.mRecentTasks.resetFreezeTaskListReorderingOnTimeout();
        }
        if (ActivityManager.isStartResultFatalError(i)) {
            return;
        }
        this.mSupervisor.reportWaitingActivityLaunchedIfNeeded(activityRecord, i);
        if (activityRecord.getTask() != null) {
            task2 = activityRecord.getTask();
        } else {
            task2 = this.mTargetTask;
        }
        if (task == null || task2 == null || !task2.isAttached()) {
            return;
        }
        if (i == 2 || i == 3) {
            Task rootHomeTask = task2.getDisplayArea().getRootHomeTask();
            boolean z = true;
            boolean z2 = rootHomeTask != null && rootHomeTask.shouldBeVisible(null);
            ActivityRecord topNonFinishingActivity = task2.getTopNonFinishingActivity();
            if (topNonFinishingActivity == null || !topNonFinishingActivity.isVisible()) {
                z = false;
            }
            this.mService.getTaskChangeNotificationController().notifyActivityRestartAttempt(task2.getTaskInfo(), z2, this.mIsTaskCleared, z);
        }
        if (ActivityManager.isStartResultSuccessful(i)) {
            this.mInterceptor.onActivityLaunched(task2.getTaskInfo(), activityRecord);
        }
    }

    public final int startActivityUnchecked(ActivityRecord activityRecord, ActivityRecord activityRecord2, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor, int i, ActivityOptions activityOptions, Task task, TaskFragment taskFragment, int i2, NeededUriGrants neededUriGrants, int i3) {
        TransitionController transitionController = activityRecord.mTransitionController;
        Transition createTransition = (transitionController.isCollecting() || transitionController.getTransitionPlayer() == null) ? null : transitionController.createTransition(1);
        RemoteTransition takeRemoteTransition = activityRecord.takeRemoteTransition();
        try {
            this.mService.deferWindowLayout();
            transitionController.collect(activityRecord);
            Trace.traceBegin(32L, "startActivityInner");
            int startActivityInner = startActivityInner(activityRecord, activityRecord2, iVoiceInteractionSession, iVoiceInteractor, i, activityOptions, task, taskFragment, i2, neededUriGrants, i3);
            Trace.traceEnd(32L);
            Task handleStartResult = handleStartResult(activityRecord, activityOptions, startActivityInner, createTransition, takeRemoteTransition);
            this.mService.continueWindowLayout();
            postStartActivityProcessing(activityRecord, startActivityInner, handleStartResult);
            return startActivityInner;
        } catch (Throwable th) {
            this.mService.continueWindowLayout();
            throw th;
        }
    }

    public final Task handleStartResult(ActivityRecord activityRecord, ActivityOptions activityOptions, int i, Transition transition, RemoteTransition remoteTransition) {
        StatusBarManagerInternal statusBarManagerInternal;
        ActivityTaskSupervisor activityTaskSupervisor = this.mSupervisor;
        boolean z = activityTaskSupervisor.mUserLeaving;
        activityTaskSupervisor.mUserLeaving = false;
        Task rootTask = activityRecord.getRootTask();
        if (rootTask == null) {
            rootTask = this.mTargetRootTask;
        }
        if (!ActivityManager.isStartResultSuccessful(i) || rootTask == null) {
            if (this.mStartActivity.getTask() != null) {
                this.mStartActivity.finishIfPossible("startActivity", true);
            } else if (this.mStartActivity.getParent() != null) {
                this.mStartActivity.getParent().removeChild(this.mStartActivity);
            }
            if (rootTask != null && rootTask.isAttached() && !rootTask.hasActivity() && !rootTask.isActivityTypeHome() && !rootTask.mCreatedByOrganizer) {
                rootTask.removeIfPossible("handleStartResult");
            }
            if (transition != null) {
                transition.abort();
            }
            return null;
        }
        if (activityOptions != null && activityOptions.getTaskAlwaysOnTop()) {
            rootTask.setAlwaysOnTop(true);
        }
        ActivityRecord activityRecord2 = rootTask.topRunningActivity();
        if (activityRecord2 != null && activityRecord2.shouldUpdateConfigForDisplayChanged()) {
            this.mRootWindowContainer.ensureVisibilityAndConfig(activityRecord2, activityRecord2.getDisplayId(), true, false);
        }
        if (!this.mAvoidMoveToFront && this.mDoResume && this.mRootWindowContainer.hasVisibleWindowAboveButDoesNotOwnNotificationShade(activityRecord.launchedFromUid) && (statusBarManagerInternal = this.mService.getStatusBarManagerInternal()) != null) {
            statusBarManagerInternal.collapsePanels();
        }
        TransitionController transitionController = activityRecord.mTransitionController;
        boolean z2 = i == 0 || i == 2;
        boolean z3 = activityOptions != null && activityOptions.getTransientLaunch();
        boolean z4 = z3 && this.mPriorAboveTask != null && this.mDisplayLockAndOccluded;
        if (z2) {
            transitionController.collectExistenceChange(activityRecord);
        } else if (i == 3 && transition != null && this.mMovedToTopActivity == null && !z4) {
            transition.abort();
            transition = null;
        }
        if (z3) {
            if (z4 && transition != null) {
                transition.collect(this.mLastStartActivityRecord);
                transition.collect(this.mPriorAboveTask);
            }
            transitionController.setTransientLaunch(this.mLastStartActivityRecord, this.mPriorAboveTask);
            if (z4 && transition != null) {
                DisplayContent displayContent = this.mLastStartActivityRecord.getDisplayContent();
                displayContent.mWallpaperController.adjustWallpaperWindows();
                transition.setReady(displayContent, true);
            }
        }
        if (!z) {
            transitionController.setCanPipOnFinish(false);
        }
        if (transition != null) {
            Task task = this.mTargetTask;
            if (task == null) {
                task = activityRecord.getTask();
            }
            transitionController.requestStartTransition(transition, task, remoteTransition, null);
        } else if (z2) {
            transitionController.setReady(activityRecord, false);
        }
        return rootTask;
    }

    @VisibleForTesting
    public int startActivityInner(ActivityRecord activityRecord, ActivityRecord activityRecord2, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor, int i, ActivityOptions activityOptions, Task task, TaskFragment taskFragment, int i2, NeededUriGrants neededUriGrants, int i3) {
        boolean z;
        ActivityRecord activityRecord3;
        int deliverToCurrentTopIfNeeded;
        ActivityRecord activityRecord4;
        ActivityRecord findActivity;
        setInitialState(activityRecord, activityOptions, task, taskFragment, i, activityRecord2, iVoiceInteractionSession, iVoiceInteractor, i2, i3);
        computeLaunchingTaskFlags();
        this.mIntent.setFlags(this.mLaunchFlags);
        Iterator<ActivityRecord> it = this.mSupervisor.mStoppingActivities.iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            } else if (it.next().getActivityType() == 5) {
                z = true;
                break;
            }
        }
        Task focusedRootTask = this.mPreferredTaskDisplayArea.getFocusedRootTask();
        Task topLeafTask = focusedRootTask != null ? focusedRootTask.getTopLeafTask() : null;
        Task reusableTask = getReusableTask();
        ActivityOptions activityOptions2 = this.mOptions;
        if (activityOptions2 != null && activityOptions2.freezeRecentTasksReordering() && this.mSupervisor.mRecentTasks.isCallerRecents(activityRecord.launchedFromUid) && !this.mSupervisor.mRecentTasks.isFreezeTaskListReorderingSet()) {
            this.mFrozeTaskList = true;
            this.mSupervisor.mRecentTasks.setFreezeTaskListReordering();
        }
        Task computeTargetTask = reusableTask != null ? reusableTask : computeTargetTask();
        boolean z2 = computeTargetTask == null;
        this.mTargetTask = computeTargetTask;
        computeLaunchParams(activityRecord, activityRecord2, computeTargetTask);
        int isAllowedToStart = isAllowedToStart(activityRecord, z2, computeTargetTask);
        if (isAllowedToStart != 0) {
            ActivityRecord activityRecord5 = activityRecord.resultTo;
            if (activityRecord5 != null) {
                activityRecord5.sendResult(-1, activityRecord.resultWho, activityRecord.requestCode, 0, null, null);
            }
            return isAllowedToStart;
        }
        if (computeTargetTask != null) {
            if (computeTargetTask.getTreeWeight() > 300) {
                Slog.e("ActivityTaskManager", "Remove " + computeTargetTask + " because it has contained too many activities or windows (abort starting " + activityRecord + " from uid=" + this.mCallingUid);
                computeTargetTask.removeImmediately("bulky-task");
                return 102;
            }
            this.mPriorAboveTask = TaskDisplayArea.getRootTaskAbove(computeTargetTask.getRootTask());
        }
        ActivityRecord topNonFinishingActivity = z2 ? null : computeTargetTask.getTopNonFinishingActivity();
        if (topNonFinishingActivity != null) {
            if (3 == this.mLaunchMode && (activityRecord4 = this.mSourceRecord) != null && computeTargetTask == activityRecord4.getTask() && (findActivity = this.mRootWindowContainer.findActivity(this.mIntent, this.mStartActivity.info, false)) != null && findActivity.getTask() != computeTargetTask) {
                findActivity.destroyIfPossible("Removes redundant singleInstance");
            }
            int recycleTask = recycleTask(computeTargetTask, topNonFinishingActivity, reusableTask, neededUriGrants);
            if (recycleTask != 0) {
                return recycleTask;
            }
        } else {
            this.mAddingToTask = true;
        }
        Task focusedRootTask2 = this.mPreferredTaskDisplayArea.getFocusedRootTask();
        if (focusedRootTask2 == null || (deliverToCurrentTopIfNeeded = deliverToCurrentTopIfNeeded(focusedRootTask2, neededUriGrants)) == 0) {
            if (this.mTargetRootTask == null) {
                this.mTargetRootTask = getOrCreateRootTask(this.mStartActivity, this.mLaunchFlags, computeTargetTask, this.mOptions);
            }
            if (z2) {
                setNewTask((!this.mLaunchTaskBehind || (activityRecord3 = this.mSourceRecord) == null) ? null : activityRecord3.getTask());
            } else if (this.mAddingToTask) {
                addOrReparentStartingActivity(computeTargetTask, "adding to task");
            }
            if (!this.mAvoidMoveToFront && this.mDoResume) {
                this.mTargetRootTask.getRootTask().moveToFront("reuseOrNewTask", computeTargetTask);
                if (!this.mTargetRootTask.isTopRootTaskInDisplayArea() && this.mService.isDreaming() && !z) {
                    this.mLaunchTaskBehind = true;
                    activityRecord.mLaunchTaskBehind = true;
                }
            }
            this.mService.mUgmInternal.grantUriPermissionUncheckedFromIntent(neededUriGrants, this.mStartActivity.getUriPermissionsLocked());
            ActivityRecord activityRecord6 = this.mStartActivity;
            ActivityRecord activityRecord7 = activityRecord6.resultTo;
            if (activityRecord7 != null && activityRecord7.info != null) {
                PackageManagerInternal packageManagerInternalLocked = this.mService.getPackageManagerInternalLocked();
                ActivityRecord activityRecord8 = this.mStartActivity;
                int packageUid = packageManagerInternalLocked.getPackageUid(activityRecord8.resultTo.info.packageName, 0L, activityRecord8.mUserId);
                ActivityRecord activityRecord9 = this.mStartActivity;
                packageManagerInternalLocked.grantImplicitAccess(activityRecord9.mUserId, this.mIntent, UserHandle.getAppId(activityRecord9.info.applicationInfo.uid), packageUid, true);
            } else if (activityRecord6.mShareIdentity) {
                PackageManagerInternal packageManagerInternalLocked2 = this.mService.getPackageManagerInternalLocked();
                ActivityRecord activityRecord10 = this.mStartActivity;
                packageManagerInternalLocked2.grantImplicitAccess(activityRecord10.mUserId, this.mIntent, UserHandle.getAppId(activityRecord10.info.applicationInfo.uid), activityRecord.launchedFromUid, true);
            }
            Task task2 = this.mStartActivity.getTask();
            if (z2) {
                EventLogTags.writeWmCreateTask(this.mStartActivity.mUserId, task2.mTaskId, task2.getRootTaskId(), task2.getDisplayId());
            }
            this.mStartActivity.logStartActivity(30005, task2);
            this.mStartActivity.getTaskFragment().clearLastPausedActivity();
            this.mRootWindowContainer.startPowerModeLaunchIfNeeded(false, this.mStartActivity);
            this.mTargetRootTask.startActivityLocked(this.mStartActivity, focusedRootTask2, z2, task2 != topLeafTask, this.mOptions, activityRecord2);
            if (this.mDoResume) {
                ActivityRecord activityRecord11 = task2.topRunningActivityLocked();
                if (!this.mTargetRootTask.isTopActivityFocusable() || (activityRecord11 != null && activityRecord11.isTaskOverlay() && this.mStartActivity != activityRecord11)) {
                    this.mTargetRootTask.ensureActivitiesVisible(null, 0, false);
                    this.mTargetRootTask.mDisplayContent.executeAppTransition();
                } else {
                    if (this.mTargetRootTask.isTopActivityFocusable() && !this.mRootWindowContainer.isTopDisplayFocusedRootTask(this.mTargetRootTask)) {
                        this.mTargetRootTask.moveToFront("startActivityInner");
                    }
                    this.mRootWindowContainer.resumeFocusedTasksTopActivities(this.mTargetRootTask, this.mStartActivity, this.mOptions, this.mTransientLaunch);
                }
            }
            this.mRootWindowContainer.updateUserRootTask(this.mStartActivity.mUserId, this.mTargetRootTask);
            this.mSupervisor.mRecentTasks.add(task2);
            this.mSupervisor.handleNonResizableTaskIfNeeded(task2, this.mPreferredWindowingMode, this.mPreferredTaskDisplayArea, this.mTargetRootTask);
            ActivityOptions activityOptions3 = this.mOptions;
            if (activityOptions3 != null && activityOptions3.isLaunchIntoPip() && activityRecord2 != null && activityRecord2.getTask() == this.mStartActivity.getTask()) {
                this.mRootWindowContainer.moveActivityToPinnedRootTask(this.mStartActivity, activityRecord2, "launch-into-pip");
            }
            return 0;
        }
        return deliverToCurrentTopIfNeeded;
    }

    public final Task computeTargetTask() {
        ActivityRecord activityRecord = this.mStartActivity;
        if (activityRecord.resultTo != null || this.mInTask != null || this.mAddingToTask || (this.mLaunchFlags & 268435456) == 0) {
            ActivityRecord activityRecord2 = this.mSourceRecord;
            if (activityRecord2 != null) {
                return activityRecord2.getTask();
            }
            Task task = this.mInTask;
            if (task != null) {
                if (!task.isAttached()) {
                    getOrCreateRootTask(this.mStartActivity, this.mLaunchFlags, this.mInTask, this.mOptions);
                }
                return this.mInTask;
            }
            Task orCreateRootTask = getOrCreateRootTask(activityRecord, this.mLaunchFlags, null, this.mOptions);
            ActivityRecord topNonFinishingActivity = orCreateRootTask.getTopNonFinishingActivity();
            if (topNonFinishingActivity != null) {
                return topNonFinishingActivity.getTask();
            }
            orCreateRootTask.removeIfPossible("computeTargetTask");
            return null;
        }
        return null;
    }

    public final void computeLaunchParams(ActivityRecord activityRecord, ActivityRecord activityRecord2, Task task) {
        TaskDisplayArea defaultTaskDisplayArea;
        this.mSupervisor.getLaunchParamsController().calculate(task, activityRecord.info.windowLayout, activityRecord, activityRecord2, this.mOptions, this.mRequest, 3, this.mLaunchParams);
        if (this.mLaunchParams.hasPreferredTaskDisplayArea()) {
            defaultTaskDisplayArea = this.mLaunchParams.mPreferredTaskDisplayArea;
        } else {
            defaultTaskDisplayArea = this.mRootWindowContainer.getDefaultTaskDisplayArea();
        }
        this.mPreferredTaskDisplayArea = defaultTaskDisplayArea;
        this.mPreferredWindowingMode = this.mLaunchParams.mWindowingMode;
    }

    @VisibleForTesting
    public int isAllowedToStart(ActivityRecord activityRecord, boolean z, Task task) {
        DisplayContent displayContentOrCreate;
        if (activityRecord.packageName == null) {
            ActivityOptions.abort(this.mOptions);
            return -92;
        }
        if (activityRecord.isActivityTypeHome() && !this.mRootWindowContainer.canStartHomeOnDisplayArea(activityRecord.info, this.mPreferredTaskDisplayArea, true)) {
            Slog.w("ActivityTaskManager", "Cannot launch home on display area " + this.mPreferredTaskDisplayArea);
            return -96;
        }
        boolean z2 = z || !task.isUidPresent(this.mCallingUid) || (3 == this.mLaunchMode && task.inPinnedWindowingMode());
        if (this.mBalCode == 0 && z2 && handleBackgroundActivityAbort(activityRecord)) {
            Slog.e("ActivityTaskManager", "Abort background activity starts from " + this.mCallingUid);
            return 102;
        }
        boolean z3 = (this.mLaunchFlags & 268468224) == 268468224;
        if (!z) {
            if (this.mService.getLockTaskController().isLockTaskModeViolation(task, z3)) {
                Slog.e("ActivityTaskManager", "Attempted Lock Task Mode violation r=" + activityRecord);
                return 101;
            }
        } else if (this.mService.getLockTaskController().isNewTaskLockTaskModeViolation(activityRecord)) {
            Slog.e("ActivityTaskManager", "Attempted Lock Task Mode violation r=" + activityRecord);
            return 101;
        }
        TaskDisplayArea taskDisplayArea = this.mPreferredTaskDisplayArea;
        if (taskDisplayArea != null && (displayContentOrCreate = this.mRootWindowContainer.getDisplayContentOrCreate(taskDisplayArea.getDisplayId())) != null) {
            int windowingMode = task != null ? task.getWindowingMode() : displayContentOrCreate.getWindowingMode();
            ActivityRecord activityRecord2 = this.mSourceRecord;
            if (!displayContentOrCreate.mDwpcHelper.canActivityBeLaunched(activityRecord.info, activityRecord.intent, windowingMode, activityRecord2 != null ? activityRecord2.getDisplayId() : 0, z)) {
                Slog.w("ActivityTaskManager", "Abort to launch " + activityRecord.info.getComponentName() + " on display area " + this.mPreferredTaskDisplayArea);
                return 102;
            }
        }
        return !checkActivitySecurityModel(activityRecord, z, task) ? 102 : 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v11, types: [com.android.server.wm.ActivityRecord] */
    public final boolean checkActivitySecurityModel(ActivityRecord activityRecord, boolean z, Task task) {
        boolean z2;
        int i;
        ActivityRecord activityRecord2;
        ActivityRecord activityRecord3;
        ActivityRecord activityRecord4;
        boolean z3;
        int i2 = this.mBalCode;
        if (i2 == 2) {
            return true;
        }
        boolean z4 = z || (this.mLaunchFlags & 268435456) == 268435456;
        if (z4 && (i2 == 3 || i2 == 6 || i2 == 5 || i2 == 7 || i2 == 4)) {
            return true;
        }
        ActivityRecord activityRecord5 = this.mSourceRecord;
        if (activityRecord5 != null) {
            Task task2 = activityRecord5.getTask();
            if (!z4 || (task2 != null && (task2.isVisible() || task2 == task))) {
                if (!z4) {
                    task2 = task;
                }
                Pair<Boolean, Boolean> doesTopActivityMatchingUidExistForAsm = ActivityTaskSupervisor.doesTopActivityMatchingUidExistForAsm(task2, this.mSourceRecord.getUid(), this.mSourceRecord);
                z2 = !((Boolean) doesTopActivityMatchingUidExistForAsm.first).booleanValue();
                z3 = !((Boolean) doesTopActivityMatchingUidExistForAsm.second).booleanValue();
            } else {
                z3 = true;
                z2 = true;
            }
            if (!z3) {
                return true;
            }
        } else {
            z2 = true;
        }
        ActivityRecord activity = task == null ? null : task.getActivity(new Predicate() { // from class: com.android.server.wm.ActivityStarter$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$checkActivitySecurityModel$0;
                lambda$checkActivitySecurityModel$0 = ActivityStarter.lambda$checkActivitySecurityModel$0((ActivityRecord) obj);
                return lambda$checkActivitySecurityModel$0;
            }
        });
        if (z || (activityRecord4 = this.mSourceRecord) == null) {
            i = 3;
        } else {
            i = activityRecord4.getTask().equals(task) ? 1 : 2;
        }
        ActivityRecord activityRecord6 = this.mSourceRecord;
        int uid = activityRecord6 != null ? activityRecord6.getUid() : this.mCallingUid;
        ActivityRecord activityRecord7 = this.mSourceRecord;
        FrameworkStatsLog.write((int) FrameworkStatsLog.ACTIVITY_ACTION_BLOCKED, uid, activityRecord7 != null ? activityRecord7.info.name : null, activity != null ? activity.getUid() : -1, activity != null ? activity.info.name : null, z || (activityRecord3 = this.mSourceRecord) == null || task == null || !task.equals(activityRecord3.getTask()), activityRecord.getUid(), activityRecord.info.name, activityRecord.intent.getAction(), this.mLaunchFlags, i, 7, (task == null || (activityRecord2 = this.mSourceRecord) == null || task.equals(activityRecord2.getTask()) || !task.isVisible()) ? false : true, this.mBalCode);
        boolean z5 = ActivitySecurityModelFeatureFlags.shouldRestrictActivitySwitch(this.mCallingUid) && z2;
        String str = activityRecord.launchedFromPackage;
        if (ActivitySecurityModelFeatureFlags.shouldShowToast(this.mCallingUid)) {
            StringBuilder sb = new StringBuilder();
            sb.append("go/android-asm");
            sb.append(z5 ? " blocked " : " would block ");
            sb.append((Object) ActivityTaskSupervisor.getApplicationLabel(this.mService.mContext.getPackageManager(), str));
            final String sb2 = sb.toString();
            UiThread.getHandler().post(new Runnable() { // from class: com.android.server.wm.ActivityStarter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityStarter.this.lambda$checkActivitySecurityModel$1(sb2);
                }
            });
            logDebugInfoForActivitySecurity("Launch", activityRecord, task, activity, z5, z4);
        }
        if (z5) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("[ASM] Abort Launching r: ");
            sb3.append(activityRecord);
            sb3.append(" as source: ");
            ?? r1 = this.mSourceRecord;
            if (r1 != 0) {
                str = r1;
            }
            sb3.append((Object) str);
            sb3.append(" is in background. New task: ");
            sb3.append(z);
            sb3.append(". Top activity: ");
            sb3.append(activity);
            sb3.append(". BAL Code: ");
            sb3.append(BackgroundActivityStartController.balCodeToString(this.mBalCode));
            Slog.e("ActivityTaskManager", sb3.toString());
            return false;
        }
        return true;
    }

    public static /* synthetic */ boolean lambda$checkActivitySecurityModel$0(ActivityRecord activityRecord) {
        return (activityRecord.finishing || activityRecord.isAlwaysOnTop()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkActivitySecurityModel$1(String str) {
        Toast.makeText(this.mService.mContext, str, 1).show();
    }

    public final void logDebugInfoForActivitySecurity(String str, final ActivityRecord activityRecord, Task task, final ActivityRecord activityRecord2, boolean z, boolean z2) {
        ActivityRecord activityRecord3;
        final Function function = new Function() { // from class: com.android.server.wm.ActivityStarter$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$logDebugInfoForActivitySecurity$2;
                lambda$logDebugInfoForActivitySecurity$2 = ActivityStarter.this.lambda$logDebugInfoForActivitySecurity$2(activityRecord2, activityRecord, (ActivityRecord) obj);
                return lambda$logDebugInfoForActivitySecurity$2;
            }
        };
        final StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add("[ASM] ------ Activity Security " + str + " Debug Logging Start ------");
        StringBuilder sb = new StringBuilder();
        sb.append("[ASM] Block Enabled: ");
        sb.append(z);
        stringJoiner.add(sb.toString());
        stringJoiner.add("[ASM] ASM Version: 7");
        boolean z3 = (task == null || (activityRecord3 = this.mSourceRecord) == null || activityRecord3.getTask() != task) ? false : true;
        if (this.mSourceRecord == null) {
            stringJoiner.add("[ASM] Source Package: " + activityRecord.launchedFromPackage);
            String nameForUid = this.mService.mContext.getPackageManager().getNameForUid(this.mRealCallingUid);
            stringJoiner.add("[ASM] Real Calling Uid Package: " + nameForUid);
        } else {
            stringJoiner.add("[ASM] Source Record: " + ((String) function.apply(this.mSourceRecord)));
            if (z3) {
                stringJoiner.add("[ASM] Source/Target Task: " + this.mSourceRecord.getTask());
                stringJoiner.add("[ASM] Source/Target Task Stack: ");
            } else {
                stringJoiner.add("[ASM] Source Task: " + this.mSourceRecord.getTask());
                stringJoiner.add("[ASM] Source Task Stack: ");
            }
            this.mSourceRecord.getTask().forAllActivities(new Consumer() { // from class: com.android.server.wm.ActivityStarter$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ActivityStarter.lambda$logDebugInfoForActivitySecurity$3(stringJoiner, function, (ActivityRecord) obj);
                }
            });
        }
        stringJoiner.add("[ASM] Target Task Top: " + ((String) function.apply(activityRecord2)));
        if (!z3) {
            stringJoiner.add("[ASM] Target Task: " + task);
            if (task != null) {
                stringJoiner.add("[ASM] Target Task Stack: ");
                task.forAllActivities(new Consumer() { // from class: com.android.server.wm.ActivityStarter$$ExternalSyntheticLambda6
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityStarter.lambda$logDebugInfoForActivitySecurity$4(stringJoiner, function, (ActivityRecord) obj);
                    }
                });
            }
        }
        stringJoiner.add("[ASM] Target Record: " + ((String) function.apply(activityRecord)));
        stringJoiner.add("[ASM] Intent: " + this.mIntent);
        stringJoiner.add("[ASM] TaskToFront: " + z2);
        stringJoiner.add("[ASM] BalCode: " + BackgroundActivityStartController.balCodeToString(this.mBalCode));
        stringJoiner.add("[ASM] ------ Activity Security " + str + " Debug Logging End ------");
        Slog.i("ActivityTaskManager", stringJoiner.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$logDebugInfoForActivitySecurity$2(ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityRecord activityRecord3) {
        if (activityRecord3 == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(activityRecord3 == this.mSourceRecord ? " [source]=> " : activityRecord3 == activityRecord ? " [ top  ]=> " : activityRecord3 == activityRecord2 ? " [target]=> " : "         => ");
        sb.append(activityRecord3);
        sb.append(" :: visible=");
        sb.append(activityRecord3.isVisible());
        sb.append(", finishing=");
        sb.append(activityRecord3.isFinishing());
        sb.append(", alwaysOnTop=");
        sb.append(activityRecord3.isAlwaysOnTop());
        sb.append(", taskFragment=");
        sb.append(activityRecord3.getTaskFragment());
        return sb.toString();
    }

    public static /* synthetic */ void lambda$logDebugInfoForActivitySecurity$3(StringJoiner stringJoiner, Function function, ActivityRecord activityRecord) {
        stringJoiner.add("[ASM] " + ((String) function.apply(activityRecord)));
    }

    public static /* synthetic */ void lambda$logDebugInfoForActivitySecurity$4(StringJoiner stringJoiner, Function function, ActivityRecord activityRecord) {
        stringJoiner.add("[ASM] " + ((String) function.apply(activityRecord)));
    }

    @VisibleForTesting
    public static int canEmbedActivity(TaskFragment taskFragment, ActivityRecord activityRecord, Task task) {
        Task task2 = taskFragment.getTask();
        if (task2 == null || task != task2) {
            return 3;
        }
        return taskFragment.isAllowedToEmbedActivity(activityRecord);
    }

    @VisibleForTesting
    public int recycleTask(Task task, ActivityRecord activityRecord, Task task2, NeededUriGrants neededUriGrants) {
        int i = task.mUserId;
        ActivityRecord activityRecord2 = this.mStartActivity;
        if (i != activityRecord2.mUserId) {
            this.mTargetRootTask = task.getRootTask();
            this.mAddingToTask = true;
            return 0;
        }
        if (task2 != null) {
            if (task.intent == null) {
                task.setIntent(activityRecord2);
            } else {
                if ((activityRecord2.intent.getFlags() & 16384) != 0) {
                    task.intent.addFlags(16384);
                } else {
                    task.intent.removeFlags(16384);
                }
            }
        }
        this.mRootWindowContainer.startPowerModeLaunchIfNeeded(false, activityRecord);
        setTargetRootTaskIfNeeded(activityRecord);
        ActivityRecord activityRecord3 = this.mLastStartActivityRecord;
        if (activityRecord3 != null && (activityRecord3.finishing || activityRecord3.noDisplay)) {
            this.mLastStartActivityRecord = activityRecord;
        }
        if ((this.mStartFlags & 1) != 0) {
            if (!this.mMovedToFront && this.mDoResume) {
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1585311008, 0, (String) null, new Object[]{String.valueOf(this.mTargetRootTask), String.valueOf(activityRecord)});
                }
                this.mTargetRootTask.moveToFront("intentActivityFound");
            }
            resumeTargetRootTaskIfNeeded();
            return 1;
        }
        complyActivityFlags(task, task2 != null ? task2.getTopNonFinishingActivity() : null, neededUriGrants);
        if (this.mAddingToTask) {
            clearTopIfNeeded(task, this.mCallingUid, this.mRealCallingUid, this.mStartActivity.getUid(), this.mLaunchFlags);
            return 0;
        }
        if (activityRecord.finishing) {
            activityRecord = task.getTopNonFinishingActivity();
        }
        if (this.mMovedToFront) {
            activityRecord.showStartingWindow(true);
        } else if (this.mDoResume) {
            this.mTargetRootTask.moveToFront("intentActivityFound");
        }
        resumeTargetRootTaskIfNeeded();
        if (this.mService.isDreaming() && activityRecord.canTurnScreenOn()) {
            activityRecord.mTaskSupervisor.wakeUp("recycleTask#turnScreenOnFlag");
        }
        this.mLastStartActivityRecord = activityRecord;
        return this.mMovedToFront ? 2 : 3;
    }

    public final void clearTopIfNeeded(Task task, final int i, final int i2, final int i3, int i4) {
        if ((i4 & 268435456) != 268435456 || this.mBalCode == 2) {
            return;
        }
        Predicate<ActivityRecord> predicate = new Predicate() { // from class: com.android.server.wm.ActivityStarter$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$clearTopIfNeeded$5;
                lambda$clearTopIfNeeded$5 = ActivityStarter.lambda$clearTopIfNeeded$5(i3, i, i2, (ActivityRecord) obj);
                return lambda$clearTopIfNeeded$5;
            }
        };
        ActivityRecord topMostActivity = task.getTopMostActivity();
        if (topMostActivity == null || predicate.test(topMostActivity)) {
            return;
        }
        final boolean shouldRestrictActivitySwitch = ActivitySecurityModelFeatureFlags.shouldRestrictActivitySwitch(i);
        int[] iArr = new int[0];
        if (shouldRestrictActivitySwitch) {
            ActivityRecord activity = task.getActivity(predicate);
            if (activity == null) {
                activity = this.mStartActivity;
            }
            int[] iArr2 = new int[1];
            task.performClearTop(activity, i4, iArr2);
            if (iArr2[0] > 0) {
                Slog.w("ActivityTaskManager", "Cleared top n: " + iArr2[0] + " activities from task t: " + task + " not matching top uid: " + i);
            }
            iArr = iArr2;
        }
        if (ActivitySecurityModelFeatureFlags.shouldShowToast(i)) {
            if (!shouldRestrictActivitySwitch || iArr[0] > 0) {
                UiThread.getHandler().post(new Runnable() { // from class: com.android.server.wm.ActivityStarter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityStarter.this.lambda$clearTopIfNeeded$6(shouldRestrictActivitySwitch);
                    }
                });
                logDebugInfoForActivitySecurity("Clear Top", this.mStartActivity, task, topMostActivity, shouldRestrictActivitySwitch, true);
            }
        }
    }

    public static /* synthetic */ boolean lambda$clearTopIfNeeded$5(int i, int i2, int i3, ActivityRecord activityRecord) {
        return activityRecord.isUid(i) || activityRecord.isUid(i2) || activityRecord.isUid(i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearTopIfNeeded$6(boolean z) {
        Context context = this.mService.mContext;
        StringBuilder sb = new StringBuilder();
        sb.append(z ? "Top activities cleared by " : "Top activities would be cleared by ");
        sb.append("go/android-asm");
        Toast.makeText(context, sb.toString(), 1).show();
    }

    public final int deliverToCurrentTopIfNeeded(Task task, NeededUriGrants neededUriGrants) {
        ActivityRecord activityRecord = task.topRunningNonDelayedActivityLocked(this.mNotTop);
        if (activityRecord != null && activityRecord.mActivityComponent.equals(this.mStartActivity.mActivityComponent) && activityRecord.mUserId == this.mStartActivity.mUserId && activityRecord.attachedToProcess() && ((this.mLaunchFlags & 536870912) != 0 || 1 == this.mLaunchMode) && (!activityRecord.isActivityTypeHome() || activityRecord.getDisplayArea() == this.mPreferredTaskDisplayArea)) {
            activityRecord.getTaskFragment().clearLastPausedActivity();
            if (this.mDoResume) {
                this.mRootWindowContainer.resumeFocusedTasksTopActivities();
            }
            ActivityOptions.abort(this.mOptions);
            if ((this.mStartFlags & 1) != 0) {
                return 1;
            }
            ActivityRecord activityRecord2 = this.mStartActivity;
            ActivityRecord activityRecord3 = activityRecord2.resultTo;
            if (activityRecord3 != null) {
                activityRecord3.sendResult(-1, activityRecord2.resultWho, activityRecord2.requestCode, 0, null, null);
                this.mStartActivity.resultTo = null;
            }
            deliverNewIntent(activityRecord, neededUriGrants);
            this.mSupervisor.handleNonResizableTaskIfNeeded(activityRecord.getTask(), this.mLaunchParams.mWindowingMode, this.mPreferredTaskDisplayArea, task);
            return 3;
        }
        return 0;
    }

    public final void complyActivityFlags(Task task, ActivityRecord activityRecord, NeededUriGrants neededUriGrants) {
        ActivityRecord topNonFinishingActivity = task.getTopNonFinishingActivity();
        boolean z = (activityRecord == null || (this.mLaunchFlags & 2097152) == 0) ? false : true;
        if (z) {
            topNonFinishingActivity = this.mTargetRootTask.resetTaskIfNeeded(topNonFinishingActivity, this.mStartActivity);
        }
        int i = this.mLaunchFlags;
        if ((i & 268468224) == 268468224) {
            task.performClearTaskForReuse(true);
            task.setIntent(this.mStartActivity);
            this.mAddingToTask = true;
            this.mIsTaskCleared = true;
        } else if ((i & 67108864) != 0 || isDocumentLaunchesIntoExisting(i) || isLaunchModeOneOf(3, 2, 4)) {
            int[] iArr = new int[1];
            ActivityRecord performClearTop = task.performClearTop(this.mStartActivity, this.mLaunchFlags, iArr);
            if (performClearTop != null && !performClearTop.finishing) {
                if (iArr[0] > 0) {
                    this.mMovedToTopActivity = performClearTop;
                }
                if (performClearTop.isRootOfTask()) {
                    performClearTop.getTask().setIntent(this.mStartActivity);
                }
                deliverNewIntent(performClearTop, neededUriGrants);
                return;
            }
            this.mAddingToTask = true;
            if (performClearTop != null && performClearTop.getTaskFragment() != null && performClearTop.getTaskFragment().isEmbedded()) {
                this.mAddingToTaskFragment = performClearTop.getTaskFragment();
            }
            if (task.getRootTask() == null) {
                Task orCreateRootTask = getOrCreateRootTask(this.mStartActivity, this.mLaunchFlags, null, this.mOptions);
                this.mTargetRootTask = orCreateRootTask;
                orCreateRootTask.addChild(task, !this.mLaunchTaskBehind, (this.mStartActivity.info.flags & 1024) != 0);
            }
        } else {
            int i2 = this.mLaunchFlags;
            if ((67108864 & i2) == 0 && !this.mAddingToTask && (i2 & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0) {
                ActivityRecord activityRecord2 = this.mStartActivity;
                ActivityRecord findActivityInHistory = task.findActivityInHistory(activityRecord2.mActivityComponent, activityRecord2.mUserId);
                if (findActivityInHistory != null) {
                    if (findActivityInHistory.getTask().moveActivityToFront(findActivityInHistory)) {
                        this.mMovedToTopActivity = findActivityInHistory;
                        if (this.mNoAnimation) {
                            findActivityInHistory.mDisplayContent.prepareAppTransition(0);
                        } else {
                            findActivityInHistory.mDisplayContent.prepareAppTransition(3);
                        }
                    }
                    findActivityInHistory.updateOptionsLocked(this.mOptions);
                    deliverNewIntent(findActivityInHistory, neededUriGrants);
                    findActivityInHistory.getTaskFragment().clearLastPausedActivity();
                    return;
                }
                this.mAddingToTask = true;
            } else if (!this.mStartActivity.mActivityComponent.equals(task.realActivity)) {
                if (!z) {
                    this.mAddingToTask = true;
                } else if (task.rootWasReset) {
                } else {
                    task.setIntent(this.mStartActivity);
                }
            } else if (task == this.mInTask) {
            } else {
                if (((this.mLaunchFlags & 536870912) != 0 || 1 == this.mLaunchMode) && topNonFinishingActivity.mActivityComponent.equals(this.mStartActivity.mActivityComponent) && this.mStartActivity.resultTo == null) {
                    if (topNonFinishingActivity.isRootOfTask()) {
                        topNonFinishingActivity.getTask().setIntent(this.mStartActivity);
                    }
                    deliverNewIntent(topNonFinishingActivity, neededUriGrants);
                } else if (!task.isSameIntentFilter(this.mStartActivity)) {
                    this.mAddingToTask = true;
                } else if (activityRecord == null) {
                    this.mAddingToTask = true;
                }
            }
        }
    }

    public void reset(boolean z) {
        this.mStartActivity = null;
        this.mIntent = null;
        this.mCallingUid = -1;
        this.mRealCallingUid = -1;
        this.mOptions = null;
        this.mBalCode = 1;
        this.mLaunchTaskBehind = false;
        this.mLaunchFlags = 0;
        this.mLaunchMode = -1;
        this.mLaunchParams.reset();
        this.mNotTop = null;
        this.mDoResume = false;
        this.mStartFlags = 0;
        this.mSourceRecord = null;
        this.mPreferredTaskDisplayArea = null;
        this.mPreferredWindowingMode = 0;
        this.mInTask = null;
        this.mInTaskFragment = null;
        this.mAddingToTaskFragment = null;
        this.mAddingToTask = false;
        this.mSourceRootTask = null;
        this.mTargetRootTask = null;
        this.mTargetTask = null;
        this.mIsTaskCleared = false;
        this.mMovedToFront = false;
        this.mNoAnimation = false;
        this.mAvoidMoveToFront = false;
        this.mFrozeTaskList = false;
        this.mTransientLaunch = false;
        this.mDisplayLockAndOccluded = false;
        this.mVoiceSession = null;
        this.mVoiceInteractor = null;
        this.mIntentDelivered = false;
        if (z) {
            this.mRequest.reset();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x01db  */
    /* JADX WARN: Removed duplicated region for block: B:101:0x01dd  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x022a  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x0254  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0256  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setInitialState(ActivityRecord activityRecord, ActivityOptions activityOptions, Task task, TaskFragment taskFragment, int i, ActivityRecord activityRecord2, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor, int i2, int i3) {
        TaskDisplayArea defaultTaskDisplayArea;
        TaskFragment taskFragment2;
        Task task2;
        Task topDisplayFocusedRootTask;
        ActivityRecord activityRecord3;
        ActivityRecord activityRecord4 = activityRecord2;
        reset(false);
        this.mStartActivity = activityRecord;
        this.mIntent = activityRecord.intent;
        this.mOptions = activityOptions;
        this.mCallingUid = activityRecord.launchedFromUid;
        this.mRealCallingUid = i3;
        this.mSourceRecord = activityRecord4;
        this.mSourceRootTask = activityRecord4 != null ? activityRecord2.getRootTask() : null;
        this.mVoiceSession = iVoiceInteractionSession;
        this.mVoiceInteractor = iVoiceInteractor;
        this.mBalCode = i2;
        this.mLaunchParams.reset();
        this.mSupervisor.getLaunchParamsController().calculate(task, activityRecord.info.windowLayout, activityRecord, activityRecord2, activityOptions, this.mRequest, 0, this.mLaunchParams);
        if (this.mLaunchParams.hasPreferredTaskDisplayArea()) {
            defaultTaskDisplayArea = this.mLaunchParams.mPreferredTaskDisplayArea;
        } else {
            defaultTaskDisplayArea = this.mRootWindowContainer.getDefaultTaskDisplayArea();
        }
        this.mPreferredTaskDisplayArea = defaultTaskDisplayArea;
        this.mPreferredWindowingMode = this.mLaunchParams.mWindowingMode;
        int i4 = activityRecord.launchMode;
        this.mLaunchMode = i4;
        this.mLaunchFlags = adjustLaunchFlagsToDocumentMode(activityRecord, 3 == i4, 2 == i4, this.mIntent.getFlags());
        this.mLaunchTaskBehind = (!activityRecord.mLaunchTaskBehind || isLaunchModeOneOf(2, 3) || (this.mLaunchFlags & 524288) == 0) ? false : true;
        if (this.mLaunchMode == 4) {
            this.mLaunchFlags |= 268435456;
        }
        String str = activityRecord.info.requiredDisplayCategory;
        if (str != null && (activityRecord3 = this.mSourceRecord) != null && !str.equals(activityRecord3.info.requiredDisplayCategory)) {
            this.mLaunchFlags |= 268435456;
        }
        sendNewTaskResultRequestIfNeeded();
        int i5 = this.mLaunchFlags;
        if ((i5 & 524288) != 0 && activityRecord.resultTo == null) {
            this.mLaunchFlags = i5 | 268435456;
        }
        int i6 = this.mLaunchFlags;
        if ((i6 & 268435456) != 0 && (this.mLaunchTaskBehind || activityRecord.info.documentLaunchMode == 2)) {
            this.mLaunchFlags = i6 | 134217728;
        }
        this.mSupervisor.mUserLeaving = (this.mLaunchFlags & 262144) == 0;
        if (!activityRecord.showToCurrentUser() || this.mLaunchTaskBehind) {
            activityRecord.delayedResume = true;
            this.mDoResume = false;
        } else {
            this.mDoResume = true;
        }
        ActivityOptions activityOptions2 = this.mOptions;
        if (activityOptions2 != null) {
            if (activityOptions2.getLaunchTaskId() != -1 && this.mOptions.getTaskOverlay()) {
                activityRecord.setTaskOverlay(true);
                if (!this.mOptions.canTaskOverlayResume()) {
                    Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(this.mOptions.getLaunchTaskId());
                    ActivityRecord topNonFinishingActivity = anyTaskForId != null ? anyTaskForId.getTopNonFinishingActivity() : null;
                    if (topNonFinishingActivity != null && !topNonFinishingActivity.isState(ActivityRecord.State.RESUMED)) {
                        this.mDoResume = false;
                        this.mAvoidMoveToFront = true;
                    }
                }
            } else if (this.mOptions.getAvoidMoveToFront()) {
                this.mDoResume = false;
                this.mAvoidMoveToFront = true;
            }
            this.mTransientLaunch = this.mOptions.getTransientLaunch();
            KeyguardController keyguardController = this.mSupervisor.getKeyguardController();
            int displayId = this.mPreferredTaskDisplayArea.getDisplayId();
            boolean z = keyguardController.isKeyguardLocked(displayId) && keyguardController.isDisplayOccluded(displayId);
            this.mDisplayLockAndOccluded = z;
            if (this.mTransientLaunch && z && this.mService.getTransitionController().isShellTransitionsEnabled()) {
                this.mDoResume = false;
                this.mAvoidMoveToFront = true;
            }
            this.mTargetRootTask = Task.fromWindowContainerToken(this.mOptions.getLaunchRootTask());
            if (taskFragment == null) {
                taskFragment2 = TaskFragment.fromTaskFragmentToken(this.mOptions.getLaunchTaskFragmentToken(), this.mService);
                if (taskFragment2 != null && taskFragment2.isEmbeddedTaskFragmentInPip()) {
                    Slog.w("ActivityTaskManager", "Can not start activity in TaskFragment in PIP: " + taskFragment2);
                    taskFragment2 = null;
                }
                this.mNotTop = (this.mLaunchFlags & 16777216) == 0 ? activityRecord4 : null;
                this.mInTask = task;
                if (task != null && !task.inRecents) {
                    Slog.w("ActivityTaskManager", "Starting activity in task not in recents: " + task);
                    this.mInTask = null;
                }
                task2 = this.mInTask;
                if (task2 != null && !task2.isSameRequiredDisplayCategory(activityRecord.info)) {
                    Slog.w("ActivityTaskManager", "Starting activity in task with different display category: " + this.mInTask);
                    this.mInTask = null;
                }
                this.mInTaskFragment = taskFragment2;
                this.mStartFlags = i;
                if ((i & 1) != 0) {
                    if (activityRecord4 == null && (topDisplayFocusedRootTask = this.mRootWindowContainer.getTopDisplayFocusedRootTask()) != null) {
                        activityRecord4 = topDisplayFocusedRootTask.topRunningNonDelayedActivityLocked(this.mNotTop);
                    }
                    if (activityRecord4 == null || !activityRecord4.mActivityComponent.equals(activityRecord.mActivityComponent)) {
                        this.mStartFlags &= -2;
                    }
                }
                this.mNoAnimation = (this.mLaunchFlags & 65536) == 0;
                if (this.mBalCode == 0 || this.mService.isBackgroundActivityStartsEnabled()) {
                }
                this.mAvoidMoveToFront = true;
                this.mDoResume = false;
                return;
            }
        }
        taskFragment2 = taskFragment;
        this.mNotTop = (this.mLaunchFlags & 16777216) == 0 ? activityRecord4 : null;
        this.mInTask = task;
        if (task != null) {
            Slog.w("ActivityTaskManager", "Starting activity in task not in recents: " + task);
            this.mInTask = null;
        }
        task2 = this.mInTask;
        if (task2 != null) {
            Slog.w("ActivityTaskManager", "Starting activity in task with different display category: " + this.mInTask);
            this.mInTask = null;
        }
        this.mInTaskFragment = taskFragment2;
        this.mStartFlags = i;
        if ((i & 1) != 0) {
        }
        this.mNoAnimation = (this.mLaunchFlags & 65536) == 0;
        if (this.mBalCode == 0) {
        }
    }

    public final void sendNewTaskResultRequestIfNeeded() {
        if (this.mStartActivity.resultTo == null || (this.mLaunchFlags & 268435456) == 0) {
            return;
        }
        Slog.w("ActivityTaskManager", "Activity is launching as a new task, so cancelling activity result.");
        ActivityRecord activityRecord = this.mStartActivity;
        activityRecord.resultTo.sendResult(-1, activityRecord.resultWho, activityRecord.requestCode, 0, null, null);
        this.mStartActivity.resultTo = null;
    }

    public final void computeLaunchingTaskFlags() {
        ActivityRecord activityRecord;
        Task task;
        if (this.mSourceRecord == null && (task = this.mInTask) != null && task.getRootTask() != null) {
            Intent baseIntent = this.mInTask.getBaseIntent();
            ActivityRecord rootActivity = this.mInTask.getRootActivity();
            if (baseIntent == null) {
                ActivityOptions.abort(this.mOptions);
                throw new IllegalArgumentException("Launching into task without base intent: " + this.mInTask);
            }
            if (isLaunchModeOneOf(3, 2)) {
                if (!baseIntent.getComponent().equals(this.mStartActivity.intent.getComponent())) {
                    ActivityOptions.abort(this.mOptions);
                    throw new IllegalArgumentException("Trying to launch singleInstance/Task " + this.mStartActivity + " into different task " + this.mInTask);
                } else if (rootActivity != null) {
                    ActivityOptions.abort(this.mOptions);
                    throw new IllegalArgumentException("Caller with mInTask " + this.mInTask + " has root " + rootActivity + " but target is singleInstance/Task");
                }
            }
            if (rootActivity == null) {
                int flags = (baseIntent.getFlags() & 403185664) | (this.mLaunchFlags & (-403185665));
                this.mLaunchFlags = flags;
                this.mIntent.setFlags(flags);
                this.mInTask.setIntent(this.mStartActivity);
                this.mAddingToTask = true;
            } else if ((this.mLaunchFlags & 268435456) != 0) {
                this.mAddingToTask = false;
            } else {
                this.mAddingToTask = true;
            }
        } else {
            this.mInTask = null;
            if ((this.mStartActivity.isResolverOrDelegateActivity() || this.mStartActivity.noDisplay) && (activityRecord = this.mSourceRecord) != null && activityRecord.inFreeformWindowingMode()) {
                this.mAddingToTask = true;
            }
        }
        Task task2 = this.mInTask;
        if (task2 == null) {
            ActivityRecord activityRecord2 = this.mSourceRecord;
            if (activityRecord2 == null) {
                if ((this.mLaunchFlags & 268435456) == 0 && task2 == null) {
                    Slog.w("ActivityTaskManager", "startActivity called from non-Activity context; forcing Intent.FLAG_ACTIVITY_NEW_TASK for: " + this.mIntent);
                    this.mLaunchFlags = this.mLaunchFlags | 268435456;
                }
            } else if (activityRecord2.launchMode == 3) {
                this.mLaunchFlags |= 268435456;
            } else if (isLaunchModeOneOf(3, 2)) {
                this.mLaunchFlags |= 268435456;
            }
        }
        int i = this.mLaunchFlags;
        if ((i & IInstalld.FLAG_USE_QUOTA) != 0) {
            if ((i & 268435456) == 0 || this.mSourceRecord == null) {
                this.mLaunchFlags = i & (-4097);
            }
        }
    }

    public final Task getReusableTask() {
        ActivityRecord activityRecord;
        ActivityOptions activityOptions = this.mOptions;
        if (activityOptions != null && activityOptions.getLaunchTaskId() != -1) {
            Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(this.mOptions.getLaunchTaskId());
            if (anyTaskForId != null) {
                return anyTaskForId;
            }
            return null;
        }
        int i = this.mLaunchFlags;
        if ((((268435456 & i) != 0 && (i & 134217728) == 0) || isLaunchModeOneOf(3, 2)) && (this.mInTask == null && this.mStartActivity.resultTo == null)) {
            int i2 = this.mLaunchMode;
            if (3 == i2) {
                RootWindowContainer rootWindowContainer = this.mRootWindowContainer;
                Intent intent = this.mIntent;
                ActivityRecord activityRecord2 = this.mStartActivity;
                activityRecord = rootWindowContainer.findActivity(intent, activityRecord2.info, activityRecord2.isActivityTypeHome());
            } else if ((this.mLaunchFlags & IInstalld.FLAG_USE_QUOTA) != 0) {
                activityRecord = this.mRootWindowContainer.findActivity(this.mIntent, this.mStartActivity.info, 2 != i2);
            } else {
                activityRecord = this.mRootWindowContainer.findTask(this.mStartActivity, this.mPreferredTaskDisplayArea);
            }
        } else {
            activityRecord = null;
        }
        if (activityRecord != null && this.mLaunchMode == 4 && !activityRecord.getTask().getRootActivity().mActivityComponent.equals(this.mStartActivity.mActivityComponent)) {
            activityRecord = null;
        }
        if (activityRecord != null && ((this.mStartActivity.isActivityTypeHome() || activityRecord.isActivityTypeHome()) && activityRecord.getDisplayArea() != this.mPreferredTaskDisplayArea)) {
            activityRecord = null;
        }
        if (activityRecord != null) {
            return activityRecord.getTask();
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x00ad  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x00fe  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x011c  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0122  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setTargetRootTaskIfNeeded(ActivityRecord activityRecord) {
        boolean z;
        IBinder iBinder;
        RemoteAnimationAdapter remoteAnimationAdapter;
        ActivityRecord activityRecord2;
        ActivityRecord activityRecord3;
        WindowContainerToken windowContainerToken;
        activityRecord.getTaskFragment().clearLastPausedActivity();
        Task task = activityRecord.getTask();
        if (this.mTargetRootTask == null) {
            ActivityRecord activityRecord4 = this.mSourceRecord;
            if (activityRecord4 != null && (windowContainerToken = activityRecord4.mLaunchRootTask) != null) {
                this.mTargetRootTask = Task.fromWindowContainerToken(windowContainerToken);
            } else {
                this.mTargetRootTask = getOrCreateRootTask(this.mStartActivity, this.mLaunchFlags, task, this.mOptions);
            }
        }
        Task asTask = this.mTargetRootTask.getAdjacentTaskFragment() != null ? this.mTargetRootTask.getAdjacentTaskFragment().asTask() : null;
        if (asTask != null && activityRecord.isDescendantOf(asTask)) {
            this.mTargetRootTask = asTask;
        }
        if (this.mTargetRootTask.getDisplayArea() == this.mPreferredTaskDisplayArea) {
            Task focusedRootTask = this.mTargetRootTask.mDisplayContent.getFocusedRootTask();
            ActivityRecord activityRecord5 = focusedRootTask == null ? null : focusedRootTask.topRunningNonDelayedActivityLocked(this.mNotTop);
            Task task2 = activityRecord5 != null ? activityRecord5.getTask() : null;
            if (task2 == task && (focusedRootTask == null || task2 == focusedRootTask.getTopMostTask())) {
                z = false;
                if (z && !this.mAvoidMoveToFront) {
                    this.mStartActivity.intent.addFlags(4194304);
                    activityRecord2 = this.mSourceRecord;
                    if (activityRecord2 != null || inTopNonFinishingTask(activityRecord2)) {
                        if (this.mLaunchTaskBehind && (activityRecord3 = this.mSourceRecord) != null) {
                            activityRecord.setTaskToAffiliateWith(activityRecord3.getTask());
                        }
                        if (!activityRecord.isDescendantOf(this.mTargetRootTask)) {
                            Task task3 = this.mTargetRootTask;
                            if (task3 != task && task3 != task.getParent().asTask()) {
                                task.getParent().positionChildAt(Integer.MAX_VALUE, task, false);
                                task = task.getParent().asTaskFragment().getTask();
                            }
                            boolean z2 = activityRecord.isVisibleRequested() && activityRecord.inMultiWindowMode() && activityRecord == this.mTargetRootTask.topRunningActivity();
                            this.mTargetRootTask.moveTaskToFront(task, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, true, "bringingFoundTaskToFront");
                            this.mMovedToFront = !z2;
                        } else if (activityRecord.getWindowingMode() != 2) {
                            task.reparent(this.mTargetRootTask, true, 0, true, true, "reparentToTargetRootTask");
                            this.mMovedToFront = true;
                        }
                        this.mOptions = null;
                    }
                }
                ActivityRecord activityRecord6 = this.mStartActivity;
                iBinder = activityRecord6.mLaunchCookie;
                if (iBinder != null) {
                    activityRecord.mLaunchCookie = iBinder;
                }
                remoteAnimationAdapter = activityRecord6.mPendingRemoteAnimation;
                if (remoteAnimationAdapter != null) {
                    activityRecord.mPendingRemoteAnimation = remoteAnimationAdapter;
                }
                this.mTargetRootTask = activityRecord.getRootTask();
                this.mSupervisor.handleNonResizableTaskIfNeeded(task, 0, this.mRootWindowContainer.getDefaultTaskDisplayArea(), this.mTargetRootTask);
            }
        }
        z = true;
        if (z) {
            this.mStartActivity.intent.addFlags(4194304);
            activityRecord2 = this.mSourceRecord;
            if (activityRecord2 != null) {
            }
            if (this.mLaunchTaskBehind) {
                activityRecord.setTaskToAffiliateWith(activityRecord3.getTask());
            }
            if (!activityRecord.isDescendantOf(this.mTargetRootTask)) {
            }
            this.mOptions = null;
        }
        ActivityRecord activityRecord62 = this.mStartActivity;
        iBinder = activityRecord62.mLaunchCookie;
        if (iBinder != null) {
        }
        remoteAnimationAdapter = activityRecord62.mPendingRemoteAnimation;
        if (remoteAnimationAdapter != null) {
        }
        this.mTargetRootTask = activityRecord.getRootTask();
        this.mSupervisor.handleNonResizableTaskIfNeeded(task, 0, this.mRootWindowContainer.getDefaultTaskDisplayArea(), this.mTargetRootTask);
    }

    public final boolean inTopNonFinishingTask(ActivityRecord activityRecord) {
        if (activityRecord == null || activityRecord.getTask() == null) {
            return false;
        }
        Task task = activityRecord.getTask();
        Task createdByOrganizerTask = task.getCreatedByOrganizerTask() != null ? task.getCreatedByOrganizerTask() : activityRecord.getRootTask();
        ActivityRecord topNonFinishingActivity = createdByOrganizerTask != null ? createdByOrganizerTask.getTopNonFinishingActivity() : null;
        return topNonFinishingActivity != null && topNonFinishingActivity.getTask() == task;
    }

    public final void resumeTargetRootTaskIfNeeded() {
        if (this.mDoResume) {
            ActivityRecord activityRecord = this.mTargetRootTask.topRunningActivity(true);
            if (activityRecord != null) {
                activityRecord.setCurrentLaunchCanTurnScreenOn(true);
            }
            if (this.mTargetRootTask.isFocusable()) {
                this.mRootWindowContainer.resumeFocusedTasksTopActivities(this.mTargetRootTask, null, this.mOptions, this.mTransientLaunch);
            } else {
                this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
            }
        } else {
            ActivityOptions.abort(this.mOptions);
        }
        this.mRootWindowContainer.updateUserRootTask(this.mStartActivity.mUserId, this.mTargetRootTask);
    }

    public final void setNewTask(Task task) {
        boolean z = (this.mLaunchTaskBehind || this.mAvoidMoveToFront) ? false : true;
        Task task2 = this.mTargetRootTask;
        ActivityRecord activityRecord = this.mStartActivity;
        Task reuseOrCreateTask = task2.reuseOrCreateTask(activityRecord.info, this.mIntent, this.mVoiceSession, this.mVoiceInteractor, z, activityRecord, this.mSourceRecord, this.mOptions);
        reuseOrCreateTask.mTransitionController.collectExistenceChange(reuseOrCreateTask);
        addOrReparentStartingActivity(reuseOrCreateTask, "setTaskFromReuseOrCreateNewTask");
        if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_TASKS, -1304806505, 0, (String) null, new Object[]{String.valueOf(this.mStartActivity), String.valueOf(this.mStartActivity.getTask())});
        }
        if (task != null) {
            this.mStartActivity.setTaskToAffiliateWith(task);
        }
    }

    public final void deliverNewIntent(ActivityRecord activityRecord, NeededUriGrants neededUriGrants) {
        if (this.mIntentDelivered) {
            return;
        }
        activityRecord.logStartActivity(30003, activityRecord.getTask());
        int i = this.mCallingUid;
        ActivityRecord activityRecord2 = this.mStartActivity;
        activityRecord.deliverNewIntentLocked(i, activityRecord2.intent, neededUriGrants, activityRecord2.launchedFromPackage);
        this.mIntentDelivered = true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v2, types: [com.android.server.wm.ActivityRecord] */
    /* JADX WARN: Type inference failed for: r3v0, types: [com.android.server.wm.Task, com.android.server.wm.TaskFragment] */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v2, types: [com.android.server.wm.WindowContainer, com.android.server.wm.TaskFragment] */
    /* JADX WARN: Type inference failed for: r3v3, types: [com.android.server.wm.TaskFragment] */
    public final void addOrReparentStartingActivity(Task task, String str) {
        ActivityRecord activityRecord;
        TaskFragment taskFragment = this.mInTaskFragment;
        if (taskFragment != null) {
            int canEmbedActivity = canEmbedActivity(taskFragment, this.mStartActivity, task);
            if (canEmbedActivity == 0) {
                task = this.mInTaskFragment;
                this.mStartActivity.mRequestedLaunchingTaskFragmentToken = task.getFragmentToken();
            } else {
                sendCanNotEmbedActivityError(this.mInTaskFragment, canEmbedActivity);
            }
        } else {
            TaskFragment taskFragment2 = this.mAddingToTaskFragment;
            if (taskFragment2 == null) {
                taskFragment2 = null;
            }
            if (taskFragment2 == null && (activityRecord = task.topRunningActivity(false)) != null) {
                taskFragment2 = activityRecord.getTaskFragment();
            }
            if (taskFragment2 != null && taskFragment2.isEmbedded() && canEmbedActivity(taskFragment2, this.mStartActivity, task) == 0) {
                task = taskFragment2;
            }
        }
        if (this.mStartActivity.getTaskFragment() == null || this.mStartActivity.getTaskFragment() == task) {
            task.addChild(this.mStartActivity, Integer.MAX_VALUE);
        } else {
            this.mStartActivity.reparent(task, task.getChildCount(), str);
        }
    }

    public final void sendCanNotEmbedActivityError(TaskFragment taskFragment, int i) {
        String str;
        if (i == 1) {
            str = "The app:" + this.mCallingUid + "is not trusted to " + this.mStartActivity;
        } else if (i == 2) {
            str = "Cannot embed " + this.mStartActivity + ". TaskFragment's bounds:" + taskFragment.getBounds() + ", minimum dimensions:" + this.mStartActivity.getMinDimensions();
        } else if (i == 3) {
            str = "Cannot embed " + this.mStartActivity + " that launched on another task,mLaunchMode=" + ActivityInfo.launchModeToString(this.mLaunchMode) + ",mLaunchFlag=" + Integer.toHexString(this.mLaunchFlags);
        } else {
            str = "Unhandled embed result:" + i;
        }
        if (taskFragment.isOrganized()) {
            this.mService.mWindowOrganizerController.sendTaskFragmentOperationFailure(taskFragment.getTaskFragmentOrganizer(), this.mRequest.errorCallbackToken, taskFragment, 2, new SecurityException(str));
        } else {
            Slog.w("ActivityTaskManager", str);
        }
    }

    public final int adjustLaunchFlagsToDocumentMode(ActivityRecord activityRecord, boolean z, boolean z2, int i) {
        int i2 = i & 524288;
        if (i2 != 0 && (z || z2)) {
            Slog.i("ActivityTaskManager", "Ignoring FLAG_ACTIVITY_NEW_DOCUMENT, launchMode is \"singleInstance\" or \"singleTask\"");
        } else {
            int i3 = activityRecord.info.documentLaunchMode;
            if (i3 == 1 || i3 == 2) {
                return i | 524288;
            }
            if (i3 != 3) {
                return i;
            }
            if (this.mLaunchMode == 4 && i2 == 0) {
                return i;
            }
        }
        return i & (-134742017);
    }

    public final Task getOrCreateRootTask(ActivityRecord activityRecord, int i, Task task, ActivityOptions activityOptions) {
        boolean z = (activityOptions == null || !activityOptions.getAvoidMoveToFront()) && !this.mLaunchTaskBehind;
        ActivityRecord activityRecord2 = this.mSourceRecord;
        return this.mRootWindowContainer.getOrCreateRootTask(activityRecord, activityOptions, task, activityRecord2 != null ? activityRecord2.getTask() : null, z, this.mLaunchParams, i);
    }

    public final boolean isLaunchModeOneOf(int i, int i2) {
        int i3 = this.mLaunchMode;
        return i == i3 || i2 == i3;
    }

    public final boolean isLaunchModeOneOf(int i, int i2, int i3) {
        int i4 = this.mLaunchMode;
        return i == i4 || i2 == i4 || i3 == i4;
    }

    public ActivityStarter setIntent(Intent intent) {
        this.mRequest.intent = intent;
        return this;
    }

    public Intent getIntent() {
        return this.mRequest.intent;
    }

    public ActivityStarter setIntentGrants(NeededUriGrants neededUriGrants) {
        this.mRequest.intentGrants = neededUriGrants;
        return this;
    }

    public ActivityStarter setReason(String str) {
        this.mRequest.reason = str;
        return this;
    }

    public ActivityStarter setCaller(IApplicationThread iApplicationThread) {
        this.mRequest.caller = iApplicationThread;
        return this;
    }

    public ActivityStarter setResolvedType(String str) {
        this.mRequest.resolvedType = str;
        return this;
    }

    public ActivityStarter setActivityInfo(ActivityInfo activityInfo) {
        this.mRequest.activityInfo = activityInfo;
        return this;
    }

    public ActivityStarter setVoiceSession(IVoiceInteractionSession iVoiceInteractionSession) {
        this.mRequest.voiceSession = iVoiceInteractionSession;
        return this;
    }

    public ActivityStarter setVoiceInteractor(IVoiceInteractor iVoiceInteractor) {
        this.mRequest.voiceInteractor = iVoiceInteractor;
        return this;
    }

    public ActivityStarter setResultTo(IBinder iBinder) {
        this.mRequest.resultTo = iBinder;
        return this;
    }

    public ActivityStarter setResultWho(String str) {
        this.mRequest.resultWho = str;
        return this;
    }

    public ActivityStarter setRequestCode(int i) {
        this.mRequest.requestCode = i;
        return this;
    }

    public ActivityStarter setCallingPid(int i) {
        this.mRequest.callingPid = i;
        return this;
    }

    public ActivityStarter setCallingUid(int i) {
        this.mRequest.callingUid = i;
        return this;
    }

    public ActivityStarter setCallingPackage(String str) {
        this.mRequest.callingPackage = str;
        return this;
    }

    public ActivityStarter setCallingFeatureId(String str) {
        this.mRequest.callingFeatureId = str;
        return this;
    }

    public ActivityStarter setRealCallingPid(int i) {
        this.mRequest.realCallingPid = i;
        return this;
    }

    public ActivityStarter setRealCallingUid(int i) {
        this.mRequest.realCallingUid = i;
        return this;
    }

    public ActivityStarter setStartFlags(int i) {
        this.mRequest.startFlags = i;
        return this;
    }

    public ActivityStarter setActivityOptions(SafeActivityOptions safeActivityOptions) {
        this.mRequest.activityOptions = safeActivityOptions;
        return this;
    }

    public ActivityStarter setActivityOptions(Bundle bundle) {
        return setActivityOptions(SafeActivityOptions.fromBundle(bundle));
    }

    public ActivityStarter setIgnoreTargetSecurity(boolean z) {
        this.mRequest.ignoreTargetSecurity = z;
        return this;
    }

    public ActivityStarter setFilterCallingUid(int i) {
        this.mRequest.filterCallingUid = i;
        return this;
    }

    public ActivityStarter setComponentSpecified(boolean z) {
        this.mRequest.componentSpecified = z;
        return this;
    }

    public ActivityStarter setOutActivity(ActivityRecord[] activityRecordArr) {
        this.mRequest.outActivity = activityRecordArr;
        return this;
    }

    public ActivityStarter setInTask(Task task) {
        this.mRequest.inTask = task;
        return this;
    }

    public ActivityStarter setInTaskFragment(TaskFragment taskFragment) {
        this.mRequest.inTaskFragment = taskFragment;
        return this;
    }

    public ActivityStarter setWaitResult(WaitResult waitResult) {
        this.mRequest.waitResult = waitResult;
        return this;
    }

    public ActivityStarter setProfilerInfo(ProfilerInfo profilerInfo) {
        this.mRequest.profilerInfo = profilerInfo;
        return this;
    }

    public ActivityStarter setGlobalConfiguration(Configuration configuration) {
        this.mRequest.globalConfig = configuration;
        return this;
    }

    public ActivityStarter setUserId(int i) {
        this.mRequest.userId = i;
        return this;
    }

    public ActivityStarter setAllowPendingRemoteAnimationRegistryLookup(boolean z) {
        this.mRequest.allowPendingRemoteAnimationRegistryLookup = z;
        return this;
    }

    public ActivityStarter setOriginatingPendingIntent(PendingIntentRecord pendingIntentRecord) {
        this.mRequest.originatingPendingIntent = pendingIntentRecord;
        return this;
    }

    public ActivityStarter setBackgroundStartPrivileges(BackgroundStartPrivileges backgroundStartPrivileges) {
        this.mRequest.backgroundStartPrivileges = backgroundStartPrivileges;
        return this;
    }

    public ActivityStarter setErrorCallbackToken(IBinder iBinder) {
        this.mRequest.errorCallbackToken = iBinder;
        return this;
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("mCurrentUser=");
        printWriter.println(this.mRootWindowContainer.mCurrentUser);
        printWriter.print(str);
        printWriter.print("mLastStartReason=");
        printWriter.println(this.mLastStartReason);
        printWriter.print(str);
        printWriter.print("mLastStartActivityTimeMs=");
        printWriter.println(DateFormat.getDateTimeInstance().format(new Date(this.mLastStartActivityTimeMs)));
        printWriter.print(str);
        printWriter.print("mLastStartActivityResult=");
        printWriter.println(this.mLastStartActivityResult);
        if (this.mLastStartActivityRecord != null) {
            printWriter.print(str);
            printWriter.println("mLastStartActivityRecord:");
            ActivityRecord activityRecord = this.mLastStartActivityRecord;
            activityRecord.dump(printWriter, str + "  ", true);
        }
        if (this.mStartActivity != null) {
            printWriter.print(str);
            printWriter.println("mStartActivity:");
            ActivityRecord activityRecord2 = this.mStartActivity;
            activityRecord2.dump(printWriter, str + "  ", true);
        }
        if (this.mIntent != null) {
            printWriter.print(str);
            printWriter.print("mIntent=");
            printWriter.println(this.mIntent);
        }
        if (this.mOptions != null) {
            printWriter.print(str);
            printWriter.print("mOptions=");
            printWriter.println(this.mOptions);
        }
        printWriter.print(str);
        printWriter.print("mLaunchMode=");
        printWriter.print(ActivityInfo.launchModeToString(this.mLaunchMode));
        printWriter.print(str);
        printWriter.print("mLaunchFlags=0x");
        printWriter.print(Integer.toHexString(this.mLaunchFlags));
        printWriter.print(" mDoResume=");
        printWriter.print(this.mDoResume);
        printWriter.print(" mAddingToTask=");
        printWriter.print(this.mAddingToTask);
        printWriter.print(" mInTaskFragment=");
        printWriter.println(this.mInTaskFragment);
    }
}
