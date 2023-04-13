package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.AppGlobals;
import android.app.IActivityController;
import android.app.PictureInPictureParams;
import android.app.TaskInfo;
import android.app.WindowConfiguration;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.service.voice.IVoiceInteractionSession;
import android.util.ArraySet;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayInfo;
import android.view.InsetsSource;
import android.view.InsetsState;
import android.view.RemoteAnimationAdapter;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.window.ITaskOrganizer;
import android.window.PictureInPictureSurfaceTransaction;
import android.window.StartingWindowInfo;
import android.window.TaskFragmentParentInfo;
import android.window.TaskSnapshot;
import android.window.WindowContainerToken;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.function.TriPredicate;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.internal.util.function.pooled.PooledPredicate;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.Watchdog;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p006am.AppTimeTracker;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.SurfaceAnimator;
import com.android.server.uri.NeededUriGrants;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.wm.Task */
/* loaded from: classes2.dex */
public class Task extends TaskFragment {
    public static Exception sTmpException;
    public String affinity;
    public Intent affinityIntent;
    public boolean askedCompatMode;
    public boolean autoRemoveRecents;
    public int effectiveUid;
    public boolean inRecents;
    public Intent intent;
    public boolean isAvailable;
    public boolean isPersistable;
    public long lastActiveTime;
    public CharSequence lastDescription;
    public int mAffiliatedTaskId;
    public boolean mAlignActivityLocaleWithTask;
    public final AnimatingActivityRegistry mAnimatingActivityRegistry;
    public String mCallingFeatureId;
    public String mCallingPackage;
    public int mCallingUid;
    public boolean mCanAffectSystemUiFlags;
    public ActivityRecord mChildPipActivity;
    public boolean mConfigWillChange;
    public int mCurrentUser;
    public boolean mDeferTaskAppear;
    public boolean mDragResizing;
    public final FindRootHelper mFindRootHelper;
    public int mForceHiddenFlags;
    public boolean mForceShowForAllUsers;
    public boolean mForceTranslucent;
    public final Handler mHandler;
    public boolean mHasBeenVisible;
    public boolean mInRemoveTask;
    public boolean mInResumeTopActivity;
    public boolean mIsEffectivelySystemApp;
    public Rect mLastNonFullscreenBounds;
    public SurfaceControl mLastRecentsAnimationOverlay;
    public PictureInPictureSurfaceTransaction mLastRecentsAnimationTransaction;
    public int mLastReportedRequestedOrientation;
    public int mLastRotationDisplayId;
    public boolean mLastSurfaceShowing;
    public final ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData mLastTaskSnapshotData;
    public long mLastTimeMoved;
    public IBinder mLaunchCookie;
    public int mLayerRank;
    public int mLockTaskAuth;
    public int mLockTaskUid;
    public int mMultiWindowRestoreWindowingMode;
    public boolean mNeverRelinquishIdentity;
    public Task mNextAffiliate;
    public int mNextAffiliateTaskId;
    public Task mPrevAffiliate;
    public int mPrevAffiliateTaskId;
    public int mPrevDisplayId;
    public boolean mRemoveWithTaskOrganizer;
    public boolean mRemoving;
    public boolean mReparentLeafTaskIfRelaunch;
    public String mRequiredDisplayCategory;
    public int mResizeMode;
    public boolean mReuseTask;
    public WindowProcessController mRootProcess;
    public int mRotation;
    public StartingData mSharedStartingData;
    public boolean mSupportsPictureInPicture;
    public boolean mTaskAppearedSent;
    public ActivityManager.TaskDescription mTaskDescription;
    public final int mTaskId;
    public ITaskOrganizer mTaskOrganizer;
    public final Rect mTmpDimBoundsRect;
    public Rect mTmpRect;
    public Rect mTmpRect2;
    public ActivityRecord mTranslucentActivityWaiting;
    public ArrayList<ActivityRecord> mUndrawnActivitiesBelowTopTranslucent;
    public int mUserId;
    public boolean mUserSetupComplete;
    public String mWindowLayoutAffinity;
    public int maxRecents;
    public ComponentName origActivity;
    public ComponentName realActivity;
    public boolean realActivitySuspended;
    public String rootAffinity;
    public boolean rootWasReset;
    public String stringName;
    public IVoiceInteractor voiceInteractor;
    public IVoiceInteractionSession voiceSession;
    public static final Rect sTmpBounds = new Rect();
    public static final ResetTargetTaskHelper sResetTargetTaskHelper = new ResetTargetTaskHelper();

    public static boolean replaceWindowsOnTaskMove(int i, int i2) {
        return i == 5 || i2 == 5;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public Task asTask() {
        return this;
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public long getProtoFieldId() {
        return 1146756268037L;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean showSurfaceOnCreation() {
        return false;
    }

    /* renamed from: com.android.server.wm.Task$ActivityTaskHandler */
    /* loaded from: classes2.dex */
    public class ActivityTaskHandler extends Handler {
        public ActivityTaskHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 101) {
                return;
            }
            synchronized (Task.this.mAtmService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Task.this.notifyActivityDrawnLocked(null);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* renamed from: com.android.server.wm.Task$FindRootHelper */
    /* loaded from: classes2.dex */
    public class FindRootHelper implements Predicate<ActivityRecord> {
        public boolean mIgnoreRelinquishIdentity;
        public ActivityRecord mRoot;
        public boolean mSetToBottomIfNone;

        public FindRootHelper() {
        }

        public ActivityRecord findRoot(boolean z, boolean z2) {
            this.mIgnoreRelinquishIdentity = z;
            this.mSetToBottomIfNone = z2;
            Task.this.forAllActivities((Predicate<ActivityRecord>) this, false);
            ActivityRecord activityRecord = this.mRoot;
            this.mRoot = null;
            return activityRecord;
        }

        @Override // java.util.function.Predicate
        public boolean test(ActivityRecord activityRecord) {
            if (this.mRoot == null && this.mSetToBottomIfNone) {
                this.mRoot = activityRecord;
            }
            if (activityRecord.finishing) {
                return false;
            }
            ActivityRecord activityRecord2 = this.mRoot;
            if (activityRecord2 == null || activityRecord2.finishing) {
                this.mRoot = activityRecord;
            }
            ActivityRecord activityRecord3 = this.mRoot;
            int i = activityRecord3 == activityRecord ? Task.this.effectiveUid : activityRecord.info.applicationInfo.uid;
            if (this.mIgnoreRelinquishIdentity) {
                return true;
            }
            ActivityInfo activityInfo = activityRecord3.info;
            if ((activityInfo.flags & IInstalld.FLAG_USE_QUOTA) != 0) {
                ApplicationInfo applicationInfo = activityInfo.applicationInfo;
                if (applicationInfo.uid == 1000 || applicationInfo.isSystemApp() || this.mRoot.info.applicationInfo.uid == i) {
                    this.mRoot = activityRecord;
                    return false;
                }
                return true;
            }
            return true;
        }
    }

    public Task(ActivityTaskManagerService activityTaskManagerService, int i, Intent intent, Intent intent2, String str, String str2, ComponentName componentName, ComponentName componentName2, boolean z, boolean z2, boolean z3, int i2, int i3, String str3, long j, boolean z4, ActivityManager.TaskDescription taskDescription, ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData persistedTaskSnapshotData, int i4, int i5, int i6, int i7, String str4, String str5, int i8, boolean z5, boolean z6, boolean z7, int i9, int i10, ActivityInfo activityInfo, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor, boolean z8, IBinder iBinder, boolean z9, boolean z10) {
        super(activityTaskManagerService, null, z8, false);
        this.mTranslucentActivityWaiting = null;
        this.mUndrawnActivitiesBelowTopTranslucent = new ArrayList<>();
        this.mInResumeTopActivity = false;
        this.mLockTaskAuth = 1;
        this.mLockTaskUid = -1;
        this.isPersistable = false;
        this.mNeverRelinquishIdentity = true;
        this.mReuseTask = false;
        this.mPrevAffiliateTaskId = -1;
        this.mNextAffiliateTaskId = -1;
        this.mLastNonFullscreenBounds = null;
        this.mLayerRank = -1;
        this.mPrevDisplayId = -1;
        this.mLastRotationDisplayId = -1;
        this.mMultiWindowRestoreWindowingMode = -1;
        this.mLastReportedRequestedOrientation = -1;
        this.mTmpRect = new Rect();
        this.mTmpRect2 = new Rect();
        this.mTmpDimBoundsRect = new Rect();
        this.mCanAffectSystemUiFlags = true;
        this.mForceHiddenFlags = 0;
        this.mForceTranslucent = false;
        this.mAnimatingActivityRegistry = new AnimatingActivityRegistry();
        this.mFindRootHelper = new FindRootHelper();
        this.mAlignActivityLocaleWithTask = false;
        this.mTaskId = i;
        this.mUserId = i2;
        this.mResizeMode = i8;
        this.mSupportsPictureInPicture = z5;
        this.mTaskDescription = taskDescription != null ? taskDescription : new ActivityManager.TaskDescription();
        this.mLastTaskSnapshotData = persistedTaskSnapshotData != null ? persistedTaskSnapshotData : new ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData();
        setOrientation(-2);
        this.affinityIntent = intent2;
        this.affinity = str;
        this.rootAffinity = str2;
        this.voiceSession = iVoiceInteractionSession;
        this.voiceInteractor = iVoiceInteractor;
        this.realActivity = componentName;
        this.realActivitySuspended = z6;
        this.origActivity = componentName2;
        this.rootWasReset = z;
        this.isAvailable = true;
        this.autoRemoveRecents = z2;
        this.askedCompatMode = z3;
        this.mUserSetupComplete = z7;
        this.effectiveUid = i3;
        touchActiveTime();
        this.lastDescription = str3;
        this.mLastTimeMoved = j;
        this.mNeverRelinquishIdentity = z4;
        this.mAffiliatedTaskId = i4;
        this.mPrevAffiliateTaskId = i5;
        this.mNextAffiliateTaskId = i6;
        this.mCallingUid = i7;
        this.mCallingPackage = str4;
        this.mCallingFeatureId = str5;
        this.mResizeMode = i8;
        if (activityInfo != null) {
            setIntent(intent, activityInfo);
            setMinDimensions(activityInfo);
        } else {
            this.intent = intent;
            this.mMinWidth = i9;
            this.mMinHeight = i10;
        }
        this.mAtmService.getTaskChangeNotificationController().notifyTaskCreated(i, this.realActivity);
        this.mHandler = new ActivityTaskHandler(this.mTaskSupervisor.mLooper);
        this.mCurrentUser = this.mAtmService.mAmInternal.getCurrentUserId();
        this.mLaunchCookie = iBinder;
        this.mDeferTaskAppear = z9;
        this.mRemoveWithTaskOrganizer = z10;
        EventLogTags.writeWmTaskCreated(i);
    }

    public static Task fromWindowContainerToken(WindowContainerToken windowContainerToken) {
        if (windowContainerToken == null) {
            return null;
        }
        return WindowContainer.fromBinder(windowContainerToken.asBinder()).asTask();
    }

    public Task reuseAsLeafTask(IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor, Intent intent, ActivityInfo activityInfo, ActivityRecord activityRecord) {
        this.voiceSession = iVoiceInteractionSession;
        this.voiceInteractor = iVoiceInteractor;
        setIntent(activityRecord, intent, activityInfo);
        setMinDimensions(activityInfo);
        this.mAtmService.getTaskChangeNotificationController().notifyTaskCreated(this.mTaskId, this.realActivity);
        return this;
    }

    public final void cleanUpResourcesForDestroy(WindowContainer<?> windowContainer) {
        if (hasChild()) {
            return;
        }
        saveLaunchingStateIfNeeded(windowContainer.getDisplayContent());
        IVoiceInteractionSession iVoiceInteractionSession = this.voiceSession;
        boolean z = iVoiceInteractionSession != null;
        if (z) {
            try {
                iVoiceInteractionSession.taskFinished(this.intent, this.mTaskId);
            } catch (RemoteException unused) {
            }
        }
        if (autoRemoveFromRecents(windowContainer.asTaskFragment()) || z) {
            this.mTaskSupervisor.mRecentTasks.remove(this);
        }
        removeIfPossible("cleanUpResourcesForDestroy");
    }

    @Override // com.android.server.p014wm.WindowContainer
    @VisibleForTesting
    public void removeIfPossible() {
        removeIfPossible("removeTaskIfPossible");
    }

    public void removeIfPossible(String str) {
        this.mAtmService.getLockTaskController().clearLockedTask(this);
        if (shouldDeferRemoval()) {
            return;
        }
        boolean isLeafTask = isLeafTask();
        removeImmediately(str);
        if (isLeafTask) {
            this.mAtmService.getTaskChangeNotificationController().notifyTaskRemoved(this.mTaskId);
            TaskDisplayArea displayArea = getDisplayArea();
            if (displayArea != null) {
                displayArea.onLeafTaskRemoved(this.mTaskId);
            }
        }
    }

    public void setResizeMode(int i) {
        if (this.mResizeMode == i) {
            return;
        }
        this.mResizeMode = i;
        this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
        this.mRootWindowContainer.resumeFocusedTasksTopActivities();
        updateTaskDescription();
    }

    public boolean resize(Rect rect, int i, boolean z) {
        ActivityRecord activityRecord;
        this.mAtmService.deferWindowLayout();
        boolean z2 = true;
        boolean z3 = (i & 2) != 0;
        try {
            if (getParent() == null) {
                setBounds(rect);
                if (!inFreeformWindowingMode()) {
                    this.mTaskSupervisor.restoreRecentTaskLocked(this, null, false);
                }
            } else if (!canResizeToBounds(rect)) {
                throw new IllegalArgumentException("resizeTask: Can not resize task=" + this + " to bounds=" + rect + " resizeMode=" + this.mResizeMode);
            } else {
                Trace.traceBegin(32L, "resizeTask_" + this.mTaskId);
                if (setBounds(rect, z3) != 0 && (activityRecord = topRunningActivityLocked()) != null) {
                    z2 = activityRecord.ensureActivityConfiguration(0, z);
                    this.mRootWindowContainer.ensureActivitiesVisible(activityRecord, 0, z);
                    if (!z2) {
                        this.mRootWindowContainer.resumeFocusedTasksTopActivities();
                    }
                }
                saveLaunchingStateIfNeeded();
                Trace.traceEnd(32L);
            }
            return z2;
        } finally {
            this.mAtmService.continueWindowLayout();
        }
    }

    public boolean reparent(Task task, boolean z, int i, boolean z2, boolean z3, String str) {
        return reparent(task, z ? Integer.MAX_VALUE : 0, i, z2, z3, true, str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:46:0x008e A[Catch: all -> 0x00e6, TryCatch #0 {all -> 0x00e6, blocks: (B:21:0x0056, B:23:0x005c, B:25:0x0062, B:30:0x006d, B:32:0x0073, B:44:0x0089, B:46:0x008e, B:49:0x0095, B:51:0x00a0, B:53:0x00a8, B:55:0x00af), top: B:71:0x0056 }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00af A[Catch: all -> 0x00e6, TRY_LEAVE, TryCatch #0 {all -> 0x00e6, blocks: (B:21:0x0056, B:23:0x005c, B:25:0x0062, B:30:0x006d, B:32:0x0073, B:44:0x0089, B:46:0x008e, B:49:0x0095, B:51:0x00a0, B:53:0x00a8, B:55:0x00af), top: B:71:0x0056 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00bd  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00c4  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x00c8  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x00e4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean reparent(Task task, int i, int i2, boolean z, boolean z2, boolean z3, String str) {
        boolean z4;
        int i3;
        ActivityTaskSupervisor activityTaskSupervisor = this.mTaskSupervisor;
        RootWindowContainer rootWindowContainer = this.mRootWindowContainer;
        WindowManagerService windowManagerService = this.mAtmService.mWindowManager;
        Task rootTask = getRootTask();
        Task reparentTargetRootTask = activityTaskSupervisor.getReparentTargetRootTask(this, task, i == Integer.MAX_VALUE);
        if (reparentTargetRootTask == rootTask || !canBeLaunchedOnDisplay(reparentTargetRootTask.getDisplayId())) {
            return false;
        }
        int windowingMode = reparentTargetRootTask.getWindowingMode();
        ActivityRecord topNonFinishingActivity = getTopNonFinishingActivity();
        boolean z5 = topNonFinishingActivity != null && replaceWindowsOnTaskMove(getWindowingMode(), windowingMode);
        if (z5) {
            windowManagerService.setWillReplaceWindow(topNonFinishingActivity.token, z);
        }
        this.mAtmService.deferWindowLayout();
        try {
            ActivityRecord activityRecord = topRunningActivityLocked();
            boolean z6 = activityRecord != null && rootWindowContainer.isTopDisplayFocusedRootTask(rootTask) && topRunningActivityLocked() == activityRecord;
            boolean z7 = activityRecord != null && rootTask.isTopRootTaskInDisplayArea() && rootTask.topRunningActivity() == activityRecord;
            if (i2 != 0 && (i2 != 1 || (!z6 && !z7))) {
                z4 = false;
                reparent(reparentTargetRootTask, i, z4, str);
                if (z3) {
                    activityTaskSupervisor.scheduleUpdatePictureInPictureModeIfNeeded(this, rootTask);
                }
                if (activityRecord != null && z4) {
                    reparentTargetRootTask.moveToFront(str);
                    if (activityRecord.isState(ActivityRecord.State.RESUMED) && activityRecord == this.mRootWindowContainer.getTopResumedActivity()) {
                        this.mAtmService.setLastResumedActivityUncheckLocked(activityRecord, str);
                    }
                }
                if (!z) {
                    this.mTaskSupervisor.mNoAnimActivities.add(topNonFinishingActivity);
                }
                if (z5) {
                    i3 = 0;
                } else {
                    i3 = 0;
                    windowManagerService.scheduleClearWillReplaceWindows(topNonFinishingActivity.token, false);
                }
                if (!z2) {
                    rootWindowContainer.ensureActivitiesVisible(null, i3, !z5);
                    rootWindowContainer.resumeFocusedTasksTopActivities();
                }
                activityTaskSupervisor.handleNonResizableTaskIfNeeded(this, task.getWindowingMode(), this.mRootWindowContainer.getDefaultTaskDisplayArea(), reparentTargetRootTask);
                if (task != reparentTargetRootTask) {
                    return true;
                }
                return i3;
            }
            z4 = true;
            reparent(reparentTargetRootTask, i, z4, str);
            if (z3) {
            }
            if (activityRecord != null) {
                reparentTargetRootTask.moveToFront(str);
                if (activityRecord.isState(ActivityRecord.State.RESUMED)) {
                    this.mAtmService.setLastResumedActivityUncheckLocked(activityRecord, str);
                }
            }
            if (!z) {
            }
            if (z5) {
            }
            if (!z2) {
            }
            activityTaskSupervisor.handleNonResizableTaskIfNeeded(this, task.getWindowingMode(), this.mRootWindowContainer.getDefaultTaskDisplayArea(), reparentTargetRootTask);
            if (task != reparentTargetRootTask) {
            }
        } finally {
            this.mAtmService.continueWindowLayout();
        }
    }

    public void touchActiveTime() {
        this.lastActiveTime = SystemClock.elapsedRealtime();
    }

    public long getInactiveDuration() {
        return SystemClock.elapsedRealtime() - this.lastActiveTime;
    }

    public void setIntent(ActivityRecord activityRecord) {
        setIntent(activityRecord, null, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x001f, code lost:
        if (r3 != r0.applicationInfo.uid) goto L14;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void setIntent(ActivityRecord activityRecord, Intent intent, ActivityInfo activityInfo) {
        boolean z = true;
        if (this.intent != null) {
            if (!this.mNeverRelinquishIdentity) {
                ActivityInfo activityInfo2 = activityInfo != null ? activityInfo : activityRecord.info;
                int i = this.effectiveUid;
                if (i != 1000) {
                    if (!this.mIsEffectivelySystemApp) {
                    }
                }
            }
            z = false;
        }
        if (z) {
            this.mCallingUid = activityRecord.launchedFromUid;
            this.mCallingPackage = activityRecord.launchedFromPackage;
            this.mCallingFeatureId = activityRecord.launchedFromFeatureId;
            if (intent == null) {
                intent = activityRecord.intent;
            }
            if (activityInfo == null) {
                activityInfo = activityRecord.info;
            }
            setIntent(intent, activityInfo);
        }
        setLockTaskAuth(activityRecord);
    }

    public final void setIntent(Intent intent, ActivityInfo activityInfo) {
        if (isLeafTask()) {
            this.mNeverRelinquishIdentity = (activityInfo.flags & IInstalld.FLAG_USE_QUOTA) == 0;
            String str = activityInfo.taskAffinity;
            this.affinity = str;
            if (this.intent == null) {
                this.rootAffinity = str;
                this.mRequiredDisplayCategory = activityInfo.requiredDisplayCategory;
            }
            ApplicationInfo applicationInfo = activityInfo.applicationInfo;
            this.effectiveUid = applicationInfo.uid;
            this.mIsEffectivelySystemApp = applicationInfo.isSystemApp();
            this.stringName = null;
            if (activityInfo.targetActivity == null) {
                if (intent != null && (intent.getSelector() != null || intent.getSourceBounds() != null)) {
                    Intent intent2 = new Intent(intent);
                    intent2.setSelector(null);
                    intent2.setSourceBounds(null);
                    intent = intent2;
                }
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_TASKS, -2054442123, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(intent)});
                }
                this.intent = intent;
                this.realActivity = intent != null ? intent.getComponent() : null;
                this.origActivity = null;
            } else {
                ComponentName componentName = new ComponentName(activityInfo.packageName, activityInfo.targetActivity);
                if (intent != null) {
                    Intent intent3 = new Intent(intent);
                    intent3.setSelector(null);
                    intent3.setSourceBounds(null);
                    if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_TASKS, 674932310, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(intent3)});
                    }
                    this.intent = intent3;
                    this.realActivity = componentName;
                    this.origActivity = intent.getComponent();
                } else {
                    this.intent = null;
                    this.realActivity = componentName;
                    this.origActivity = new ComponentName(activityInfo.packageName, activityInfo.name);
                }
            }
            ActivityInfo.WindowLayout windowLayout = activityInfo.windowLayout;
            this.mWindowLayoutAffinity = windowLayout != null ? windowLayout.windowLayoutAffinity : null;
            Intent intent4 = this.intent;
            int flags = intent4 == null ? 0 : intent4.getFlags();
            if ((2097152 & flags) != 0) {
                this.rootWasReset = true;
            }
            this.mUserId = UserHandle.getUserId(activityInfo.applicationInfo.uid);
            this.mUserSetupComplete = Settings.Secure.getIntForUser(this.mAtmService.mContext.getContentResolver(), "user_setup_complete", 0, this.mUserId) != 0;
            if ((activityInfo.flags & IInstalld.FLAG_FORCE) != 0) {
                this.autoRemoveRecents = true;
            } else if ((flags & 532480) == 524288) {
                if (activityInfo.documentLaunchMode != 0) {
                    this.autoRemoveRecents = false;
                } else {
                    this.autoRemoveRecents = true;
                }
            } else {
                this.autoRemoveRecents = false;
            }
            int i = this.mResizeMode;
            int i2 = activityInfo.resizeMode;
            if (i != i2) {
                this.mResizeMode = i2;
                updateTaskDescription();
            }
            this.mSupportsPictureInPicture = activityInfo.supportsPictureInPicture();
            if (this.inRecents) {
                this.mTaskSupervisor.mRecentTasks.remove(this);
                this.mTaskSupervisor.mRecentTasks.add(this);
            }
        }
    }

    public void setMinDimensions(ActivityInfo activityInfo) {
        ActivityInfo.WindowLayout windowLayout;
        if (activityInfo != null && (windowLayout = activityInfo.windowLayout) != null) {
            this.mMinWidth = windowLayout.minWidth;
            this.mMinHeight = windowLayout.minHeight;
            return;
        }
        this.mMinWidth = -1;
        this.mMinHeight = -1;
    }

    public boolean isSameIntentFilter(ActivityRecord activityRecord) {
        Intent intent;
        Intent intent2 = new Intent(activityRecord.intent);
        if (Objects.equals(this.realActivity, activityRecord.mActivityComponent) && (intent = this.intent) != null) {
            intent2.setComponent(intent.getComponent());
            intent2.setPackage(this.intent.getPackage());
        }
        return intent2.filterEquals(this.intent);
    }

    public boolean returnsToHomeRootTask() {
        if (inMultiWindowMode() || !hasChild()) {
            return false;
        }
        Intent intent = this.intent;
        if (intent != null) {
            if ((intent.getFlags() & 268451840) != 268451840) {
                return false;
            }
            Task rootHomeTask = getDisplayArea() != null ? getDisplayArea().getRootHomeTask() : null;
            return rootHomeTask == null || !this.mAtmService.getLockTaskController().isLockTaskModeViolation(rootHomeTask);
        }
        Task bottomMostTask = getBottomMostTask();
        return bottomMostTask != this && bottomMostTask.returnsToHomeRootTask();
    }

    public void setPrevAffiliate(Task task) {
        this.mPrevAffiliate = task;
        this.mPrevAffiliateTaskId = task == null ? -1 : task.mTaskId;
    }

    public void setNextAffiliate(Task task) {
        this.mNextAffiliate = task;
        this.mNextAffiliateTaskId = task == null ? -1 : task.mTaskId;
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void onParentChanged(ConfigurationContainer configurationContainer, ConfigurationContainer configurationContainer2) {
        WindowContainer windowContainer = (WindowContainer) configurationContainer;
        WindowContainer<?> windowContainer2 = (WindowContainer) configurationContainer2;
        DisplayContent displayContent = windowContainer != null ? windowContainer.getDisplayContent() : null;
        DisplayContent displayContent2 = windowContainer2 != null ? windowContainer2.getDisplayContent() : null;
        this.mPrevDisplayId = displayContent2 != null ? displayContent2.mDisplayId : -1;
        if (windowContainer2 != null && windowContainer == null) {
            cleanUpResourcesForDestroy(windowContainer2);
        }
        if (displayContent != null) {
            getConfiguration().windowConfiguration.setRotation(displayContent.getWindowConfiguration().getRotation());
        }
        super.onParentChanged(windowContainer, windowContainer2);
        updateTaskOrganizerState();
        if (getParent() == null && this.mDisplayContent != null) {
            this.mDisplayContent = null;
            this.mWmService.mWindowPlacerLocked.requestTraversal();
        }
        if (windowContainer2 != null) {
            final Task asTask = windowContainer2.asTask();
            if (asTask != null) {
                forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda23
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        Task.this.cleanUpActivityReferences((ActivityRecord) obj);
                    }
                });
            }
            if (windowContainer2.inPinnedWindowingMode() && (windowContainer == null || !windowContainer.inPinnedWindowingMode())) {
                this.mRootWindowContainer.notifyActivityPipModeChanged(this, null);
            }
        }
        if (windowContainer != null) {
            if (!this.mCreatedByOrganizer && !canBeOrganized()) {
                getSyncTransaction().show(this.mSurfaceControl);
            }
            IVoiceInteractionSession iVoiceInteractionSession = this.voiceSession;
            if (iVoiceInteractionSession != null) {
                try {
                    iVoiceInteractionSession.taskStarted(this.intent, this.mTaskId);
                } catch (RemoteException unused) {
                }
            }
        }
        if (windowContainer2 == null && windowContainer != null) {
            updateOverrideConfigurationFromLaunchBounds();
        }
        adjustBoundsForDisplayChangeIfNeeded(getDisplayContent());
        this.mRootWindowContainer.updateUIDsPresentOnDisplay();
        forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda24
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ActivityRecord) obj).updateAnimatingActivityRegistry();
            }
        });
    }

    @Override // com.android.server.p014wm.TaskFragment
    public ActivityRecord getTopResumedActivity() {
        if (!isLeafTask()) {
            for (int size = this.mChildren.size() - 1; size >= 0; size--) {
                ActivityRecord topResumedActivity = ((WindowContainer) this.mChildren.get(size)).asTask().getTopResumedActivity();
                if (topResumedActivity != null) {
                    return topResumedActivity;
                }
            }
        }
        ActivityRecord resumedActivity = getResumedActivity();
        ActivityRecord activityRecord = null;
        for (int size2 = this.mChildren.size() - 1; size2 >= 0; size2--) {
            WindowContainer windowContainer = (WindowContainer) this.mChildren.get(size2);
            if (windowContainer.asTaskFragment() != null) {
                activityRecord = windowContainer.asTaskFragment().getTopResumedActivity();
            } else if (resumedActivity != null && windowContainer.asActivityRecord() == resumedActivity) {
                activityRecord = resumedActivity;
            }
            if (activityRecord != null) {
                return activityRecord;
            }
        }
        return null;
    }

    @Override // com.android.server.p014wm.TaskFragment
    public ActivityRecord getTopPausingActivity() {
        if (!isLeafTask()) {
            for (int size = this.mChildren.size() - 1; size >= 0; size--) {
                ActivityRecord topPausingActivity = ((WindowContainer) this.mChildren.get(size)).asTask().getTopPausingActivity();
                if (topPausingActivity != null) {
                    return topPausingActivity;
                }
            }
        }
        ActivityRecord pausingActivity = getPausingActivity();
        ActivityRecord activityRecord = null;
        for (int size2 = this.mChildren.size() - 1; size2 >= 0; size2--) {
            WindowContainer windowContainer = (WindowContainer) this.mChildren.get(size2);
            if (windowContainer.asTaskFragment() != null) {
                activityRecord = windowContainer.asTaskFragment().getTopPausingActivity();
            } else if (pausingActivity != null && windowContainer.asActivityRecord() == pausingActivity) {
                activityRecord = pausingActivity;
            }
            if (activityRecord != null) {
                return activityRecord;
            }
        }
        return null;
    }

    public void updateTaskMovement(boolean z, boolean z2, int i) {
        EventLogTags.writeWmTaskMoved(this.mTaskId, getRootTaskId(), getDisplayId(), z ? 1 : 0, i);
        TaskDisplayArea displayArea = getDisplayArea();
        if (displayArea != null && isLeafTask()) {
            displayArea.onLeafTaskMoved(this, z, z2);
        }
        if (this.isPersistable) {
            this.mLastTimeMoved = System.currentTimeMillis();
        }
    }

    public final void closeRecentsChain() {
        Task task = this.mPrevAffiliate;
        if (task != null) {
            task.setNextAffiliate(this.mNextAffiliate);
        }
        Task task2 = this.mNextAffiliate;
        if (task2 != null) {
            task2.setPrevAffiliate(this.mPrevAffiliate);
        }
        setPrevAffiliate(null);
        setNextAffiliate(null);
    }

    public void removedFromRecents() {
        closeRecentsChain();
        if (this.inRecents) {
            this.inRecents = false;
            this.mAtmService.notifyTaskPersisterLocked(this, false);
        }
        clearRootProcess();
        this.mAtmService.mWindowManager.mTaskSnapshotController.notifyTaskRemovedFromRecents(this.mTaskId, this.mUserId);
    }

    public void setTaskToAffiliateWith(Task task) {
        closeRecentsChain();
        this.mAffiliatedTaskId = task.mAffiliatedTaskId;
        while (true) {
            Task task2 = task.mNextAffiliate;
            if (task2 == null) {
                break;
            } else if (task2.mAffiliatedTaskId != this.mAffiliatedTaskId) {
                Slog.e("ActivityTaskManager", "setTaskToAffiliateWith: nextRecents=" + task2 + " affilTaskId=" + task2.mAffiliatedTaskId + " should be " + this.mAffiliatedTaskId);
                if (task2.mPrevAffiliate == task) {
                    task2.setPrevAffiliate(null);
                }
                task.setNextAffiliate(null);
            } else {
                task = task2;
            }
        }
        task.setNextAffiliate(this);
        setPrevAffiliate(task);
        setNextAffiliate(null);
    }

    public Intent getBaseIntent() {
        Intent intent = this.intent;
        if (intent != null) {
            return intent;
        }
        Intent intent2 = this.affinityIntent;
        if (intent2 != null) {
            return intent2;
        }
        Task topMostTask = getTopMostTask();
        if (topMostTask == this || topMostTask == null) {
            return null;
        }
        return topMostTask.getBaseIntent();
    }

    public ActivityRecord getRootActivity() {
        return getRootActivity(true, false);
    }

    public ActivityRecord getRootActivity(boolean z) {
        return getRootActivity(false, z);
    }

    public ActivityRecord getRootActivity(boolean z, boolean z2) {
        return this.mFindRootHelper.findRoot(z, z2);
    }

    public ActivityRecord topRunningActivityLocked() {
        if (getParent() == null) {
            return null;
        }
        return getActivity(new Task$$ExternalSyntheticLambda14());
    }

    public boolean isUidPresent(int i) {
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new DisplayContent$$ExternalSyntheticLambda49(), PooledLambda.__(ActivityRecord.class), Integer.valueOf(i));
        boolean z = getActivity(obtainPredicate) != null;
        obtainPredicate.recycle();
        return z;
    }

    public ActivityRecord topActivityContainsStartingWindow() {
        if (getParent() == null) {
            return null;
        }
        return getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda40
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$topActivityContainsStartingWindow$1;
                lambda$topActivityContainsStartingWindow$1 = Task.lambda$topActivityContainsStartingWindow$1((ActivityRecord) obj);
                return lambda$topActivityContainsStartingWindow$1;
            }
        });
    }

    public static /* synthetic */ boolean lambda$topActivityContainsStartingWindow$1(ActivityRecord activityRecord) {
        return activityRecord.getWindow(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda45
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$topActivityContainsStartingWindow$0;
                lambda$topActivityContainsStartingWindow$0 = Task.lambda$topActivityContainsStartingWindow$0((WindowState) obj);
                return lambda$topActivityContainsStartingWindow$0;
            }
        }) != null;
    }

    public static /* synthetic */ boolean lambda$topActivityContainsStartingWindow$0(WindowState windowState) {
        return windowState.getBaseType() == 3;
    }

    public final boolean moveActivityToFront(ActivityRecord activityRecord) {
        boolean moveChildToFront;
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -463348344, 0, (String) null, new Object[]{String.valueOf(activityRecord), String.valueOf(Debug.getCallers(4))});
        }
        TaskFragment taskFragment = activityRecord.getTaskFragment();
        if (taskFragment != this) {
            moveChildToFront = true;
            if (taskFragment.isEmbedded() && taskFragment.getNonFinishingActivityCount() == 1) {
                taskFragment.mClearedForReorderActivityToFront = true;
            }
            activityRecord.reparent(this, Integer.MAX_VALUE);
            if (taskFragment.isEmbedded()) {
                this.mAtmService.mWindowOrganizerController.mTaskFragmentOrganizerController.onActivityReparentedToTask(activityRecord);
            }
        } else {
            moveChildToFront = moveChildToFront(activityRecord);
        }
        updateEffectiveIntent();
        return moveChildToFront;
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public void addChild(WindowContainer windowContainer, int i) {
        super.addChild(windowContainer, getAdjustedChildPosition(windowContainer, i));
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, 1330804250, 0, (String) null, new Object[]{String.valueOf(this)});
        }
        if (this.mTaskOrganizer != null && this.mCreatedByOrganizer && windowContainer.asTask() != null) {
            getDisplayArea().addRootTaskReferenceIfNeeded((Task) windowContainer);
        }
        this.mRootWindowContainer.updateUIDsPresentOnDisplay();
        TaskFragment asTaskFragment = windowContainer.asTaskFragment();
        if (asTaskFragment == null || asTaskFragment.asTask() != null) {
            return;
        }
        asTaskFragment.setMinDimensions(this.mMinWidth, this.mMinHeight);
        ActivityRecord topMostActivity = getTopMostActivity();
        if (topMostActivity != null) {
            topMostActivity.associateStartingWindowWithTaskIfNeeded();
        }
    }

    public void onDescendantActivityAdded(boolean z, int i, ActivityRecord activityRecord) {
        warnForNonLeafTask("onDescendantActivityAdded");
        if (!z) {
            if (activityRecord.getActivityType() == 0) {
                activityRecord.setActivityType(1);
            }
            setActivityType(activityRecord.getActivityType());
            this.isPersistable = activityRecord.isPersistable();
            this.mCallingUid = activityRecord.launchedFromUid;
            this.mCallingPackage = activityRecord.launchedFromPackage;
            this.mCallingFeatureId = activityRecord.launchedFromFeatureId;
            this.maxRecents = Math.min(Math.max(activityRecord.info.maxRecents, 1), ActivityTaskManager.getMaxAppRecentsLimitStatic());
        } else {
            activityRecord.setActivityType(i);
        }
        updateEffectiveIntent();
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public void removeChild(WindowContainer windowContainer) {
        removeChild(windowContainer, "removeChild");
    }

    public void removeChild(WindowContainer windowContainer, String str) {
        if (this.mCreatedByOrganizer && windowContainer.asTask() != null) {
            getDisplayArea().removeRootTaskReferenceIfNeeded((Task) windowContainer);
        }
        if (!this.mChildren.contains(windowContainer)) {
            Slog.e("ActivityTaskManager", "removeChild: r=" + windowContainer + " not found in t=" + this);
            return;
        }
        super.removeChild(windowContainer, false);
        if (inPinnedWindowingMode()) {
            this.mAtmService.getTaskChangeNotificationController().notifyTaskStackChanged();
        }
        if (hasChild()) {
            updateEffectiveIntent();
            if (onlyHasTaskOverlayActivities(true)) {
                this.mTaskSupervisor.removeTask(this, false, false, str);
            }
        } else if (this.mReuseTask || !shouldRemoveSelfOnLastChildRemoval()) {
        } else {
            removeIfPossible(str + ", last child = " + windowContainer + " in " + this);
        }
    }

    public boolean onlyHasTaskOverlayActivities(boolean z) {
        int i = 0;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            ActivityRecord asActivityRecord = getChildAt(childCount).asActivityRecord();
            if (asActivityRecord == null) {
                return false;
            }
            if (z || !asActivityRecord.finishing) {
                if (!asActivityRecord.isTaskOverlay()) {
                    return false;
                }
                i++;
            }
        }
        return i > 0;
    }

    public final boolean autoRemoveFromRecents(TaskFragment taskFragment) {
        return this.autoRemoveRecents || !(hasChild() || getHasBeenVisible()) || (taskFragment != null && taskFragment.isEmbedded());
    }

    public final void clearPinnedTaskIfNeed() {
        ActivityRecord activityRecord = this.mChildPipActivity;
        if (activityRecord == null || activityRecord.getTask() == null) {
            return;
        }
        this.mTaskSupervisor.removeRootTask(this.mChildPipActivity.getTask());
    }

    public void removeActivities(final String str, final boolean z) {
        clearPinnedTaskIfNeed();
        if (getRootTask() == null) {
            forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda11
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    Task.this.lambda$removeActivities$2(z, str, (ActivityRecord) obj);
                }
            });
            return;
        }
        final ArrayList arrayList = new ArrayList();
        forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda12
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Task.lambda$removeActivities$3(z, arrayList, (ActivityRecord) obj);
            }
        });
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord = (ActivityRecord) arrayList.get(size);
            if (activityRecord.isState(ActivityRecord.State.RESUMED) || (activityRecord.isVisible() && !this.mDisplayContent.mAppTransition.containsTransitRequest(2))) {
                activityRecord.finishIfPossible(str, false);
            } else {
                activityRecord.destroyIfPossible(str);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeActivities$2(boolean z, String str, ActivityRecord activityRecord) {
        if (activityRecord.finishing) {
            return;
        }
        if (z && activityRecord.isTaskOverlay()) {
            return;
        }
        activityRecord.takeFromHistory();
        removeChild(activityRecord, str);
    }

    public static /* synthetic */ void lambda$removeActivities$3(boolean z, ArrayList arrayList, ActivityRecord activityRecord) {
        if (activityRecord.finishing) {
            return;
        }
        if (z && activityRecord.isTaskOverlay()) {
            return;
        }
        arrayList.add(activityRecord);
    }

    public void performClearTaskForReuse(boolean z) {
        this.mReuseTask = true;
        this.mTaskSupervisor.beginDeferResume();
        try {
            removeActivities("clear-task-all", z);
        } finally {
            this.mTaskSupervisor.endDeferResume();
            this.mReuseTask = false;
        }
    }

    public ActivityRecord performClearTop(ActivityRecord activityRecord, int i, int[] iArr) {
        this.mReuseTask = true;
        this.mTaskSupervisor.beginDeferResume();
        try {
            return clearTopActivities(activityRecord, i, iArr);
        } finally {
            this.mTaskSupervisor.endDeferResume();
            this.mReuseTask = false;
        }
    }

    public final ActivityRecord clearTopActivities(ActivityRecord activityRecord, int i, final int[] iArr) {
        ActivityRecord findActivityInHistory = findActivityInHistory(activityRecord.mActivityComponent, activityRecord.mUserId);
        if (findActivityInHistory == null) {
            return null;
        }
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new BiPredicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda6
            @Override // java.util.function.BiPredicate
            public final boolean test(Object obj, Object obj2) {
                boolean finishActivityAbove;
                finishActivityAbove = Task.finishActivityAbove((ActivityRecord) obj, (ActivityRecord) obj2, iArr);
                return finishActivityAbove;
            }
        }, PooledLambda.__(ActivityRecord.class), findActivityInHistory);
        forAllActivities((Predicate<ActivityRecord>) obtainPredicate);
        obtainPredicate.recycle();
        if (findActivityInHistory.launchMode == 0 && (536870912 & i) == 0 && !ActivityStarter.isDocumentLaunchesIntoExisting(i) && !findActivityInHistory.finishing) {
            findActivityInHistory.finishIfPossible("clear-task-top", false);
        }
        return findActivityInHistory;
    }

    public static boolean finishActivityAbove(ActivityRecord activityRecord, ActivityRecord activityRecord2, int[] iArr) {
        if (activityRecord == activityRecord2) {
            return true;
        }
        if (!activityRecord.finishing && !activityRecord.isTaskOverlay()) {
            ActivityOptions options = activityRecord.getOptions();
            if (options != null) {
                activityRecord.clearOptionsAnimation();
                activityRecord2.updateOptionsLocked(options);
            }
            iArr[0] = iArr[0] + 1;
            activityRecord.finishIfPossible("clear-task-stack", false);
        }
        return false;
    }

    public String lockTaskAuthToString() {
        int i = this.mLockTaskAuth;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            return "unknown=" + this.mLockTaskAuth;
                        }
                        return "LOCK_TASK_AUTH_LAUNCHABLE_PRIV";
                    }
                    return "LOCK_TASK_AUTH_ALLOWLISTED";
                }
                return "LOCK_TASK_AUTH_LAUNCHABLE";
            }
            return "LOCK_TASK_AUTH_PINNABLE";
        }
        return "LOCK_TASK_AUTH_DONT_LOCK";
    }

    public void setLockTaskAuth() {
        setLockTaskAuth(getRootActivity());
    }

    public final void setLockTaskAuth(ActivityRecord activityRecord) {
        this.mLockTaskAuth = this.mAtmService.getLockTaskController().getLockTaskAuth(activityRecord, this);
        if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_LOCKTASK, 1824105730, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(lockTaskAuthToString())});
        }
    }

    public boolean supportsFreeformInDisplayArea(TaskDisplayArea taskDisplayArea) {
        return this.mAtmService.mSupportsFreeformWindowManagement && supportsMultiWindowInDisplayArea(taskDisplayArea);
    }

    public boolean canBeLaunchedOnDisplay(int i) {
        return this.mTaskSupervisor.canPlaceEntityOnDisplay(i, -1, -1, this);
    }

    public final boolean canResizeToBounds(Rect rect) {
        if (rect == null || !inFreeformWindowingMode()) {
            return true;
        }
        boolean z = rect.width() > rect.height();
        Rect requestedOverrideBounds = getRequestedOverrideBounds();
        int i = this.mResizeMode;
        if (i != 7) {
            return !(i == 6 && z) && (i != 5 || z);
        } else if (requestedOverrideBounds.isEmpty()) {
            return true;
        } else {
            return z == (requestedOverrideBounds.width() > requestedOverrideBounds.height());
        }
    }

    public boolean isClearingToReuseTask() {
        return this.mReuseTask;
    }

    public ActivityRecord findActivityInHistory(ComponentName componentName, int i) {
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new TriPredicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda0
            public final boolean test(Object obj, Object obj2, Object obj3) {
                boolean matchesActivityInHistory;
                matchesActivityInHistory = Task.matchesActivityInHistory((ActivityRecord) obj, (ComponentName) obj2, ((Integer) obj3).intValue());
                return matchesActivityInHistory;
            }
        }, PooledLambda.__(ActivityRecord.class), componentName, Integer.valueOf(i));
        ActivityRecord activity = getActivity(obtainPredicate);
        obtainPredicate.recycle();
        return activity;
    }

    public static boolean matchesActivityInHistory(ActivityRecord activityRecord, ComponentName componentName, int i) {
        return !activityRecord.finishing && activityRecord.mActivityComponent.equals(componentName) && activityRecord.mUserId == i;
    }

    public void updateTaskDescription() {
        Task asTask;
        ActivityRecord rootActivity = getRootActivity(true);
        if (rootActivity == null) {
            return;
        }
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription();
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new TriPredicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda18
            public final boolean test(Object obj, Object obj2, Object obj3) {
                boolean taskDescriptionFromActivityAboveRoot;
                taskDescriptionFromActivityAboveRoot = Task.setTaskDescriptionFromActivityAboveRoot((ActivityRecord) obj, (ActivityRecord) obj2, (ActivityManager.TaskDescription) obj3);
                return taskDescriptionFromActivityAboveRoot;
            }
        }, PooledLambda.__(ActivityRecord.class), rootActivity, taskDescription);
        forAllActivities((Predicate<ActivityRecord>) obtainPredicate);
        obtainPredicate.recycle();
        taskDescription.setResizeMode(this.mResizeMode);
        taskDescription.setMinWidth(this.mMinWidth);
        taskDescription.setMinHeight(this.mMinHeight);
        setTaskDescription(taskDescription);
        this.mAtmService.getTaskChangeNotificationController().notifyTaskDescriptionChanged(getTaskInfo());
        WindowContainer parent = getParent();
        if (parent != null && (asTask = parent.asTask()) != null) {
            asTask.updateTaskDescription();
        }
        dispatchTaskInfoChangedIfNeeded(false);
    }

    public static boolean setTaskDescriptionFromActivityAboveRoot(ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityManager.TaskDescription taskDescription) {
        ActivityManager.TaskDescription taskDescription2;
        if (!activityRecord.isTaskOverlay() && (taskDescription2 = activityRecord.taskDescription) != null) {
            if (taskDescription.getLabel() == null) {
                taskDescription.setLabel(taskDescription2.getLabel());
            }
            if (taskDescription.getRawIcon() == null) {
                taskDescription.setIcon(taskDescription2.getRawIcon());
            }
            if (taskDescription.getIconFilename() == null) {
                taskDescription.setIconFilename(taskDescription2.getIconFilename());
            }
            if (taskDescription.getPrimaryColor() == 0) {
                taskDescription.setPrimaryColor(taskDescription2.getPrimaryColor());
            }
            if (taskDescription.getBackgroundColor() == 0) {
                taskDescription.setBackgroundColor(taskDescription2.getBackgroundColor());
            }
            if (taskDescription.getStatusBarColor() == 0) {
                taskDescription.setStatusBarColor(taskDescription2.getStatusBarColor());
                taskDescription.setEnsureStatusBarContrastWhenTransparent(taskDescription2.getEnsureStatusBarContrastWhenTransparent());
            }
            if (taskDescription.getNavigationBarColor() == 0) {
                taskDescription.setNavigationBarColor(taskDescription2.getNavigationBarColor());
                taskDescription.setEnsureNavigationBarContrastWhenTransparent(taskDescription2.getEnsureNavigationBarContrastWhenTransparent());
            }
            if (taskDescription.getBackgroundColorFloating() == 0) {
                taskDescription.setBackgroundColorFloating(taskDescription2.getBackgroundColorFloating());
            }
        }
        return activityRecord == activityRecord2;
    }

    @VisibleForTesting
    public void updateEffectiveIntent() {
        ActivityRecord rootActivity = getRootActivity(true);
        if (rootActivity != null) {
            setIntent(rootActivity);
            updateTaskDescription();
        }
    }

    public void setLastNonFullscreenBounds(Rect rect) {
        Rect rect2 = this.mLastNonFullscreenBounds;
        if (rect2 == null) {
            this.mLastNonFullscreenBounds = new Rect(rect);
        } else {
            rect2.set(rect);
        }
    }

    public final void onConfigurationChangedInner(Configuration configuration) {
        ActivityRecord activityRecord;
        Rect rect;
        boolean persistTaskBounds = getWindowConfiguration().persistTaskBounds();
        boolean persistTaskBounds2 = getRequestedOverrideConfiguration().windowConfiguration.persistTaskBounds();
        if (getRequestedOverrideWindowingMode() == 0) {
            persistTaskBounds2 = configuration.windowConfiguration.persistTaskBounds();
        }
        boolean z = persistTaskBounds2 & (getRequestedOverrideConfiguration().windowConfiguration.getBounds() == null || getRequestedOverrideConfiguration().windowConfiguration.getBounds().isEmpty());
        if (!persistTaskBounds && z && (rect = this.mLastNonFullscreenBounds) != null && !rect.isEmpty()) {
            getRequestedOverrideConfiguration().windowConfiguration.setBounds(this.mLastNonFullscreenBounds);
        }
        int windowingMode = getWindowingMode();
        this.mTmpPrevBounds.set(getBounds());
        boolean inMultiWindowMode = inMultiWindowMode();
        boolean inPinnedWindowingMode = inPinnedWindowingMode();
        super.onConfigurationChanged(configuration);
        updateSurfaceSize(getSyncTransaction());
        boolean z2 = inPinnedWindowingMode != inPinnedWindowingMode();
        if (z2) {
            this.mTaskSupervisor.scheduleUpdatePictureInPictureModeIfNeeded(this, getRootTask());
        } else if (inMultiWindowMode != inMultiWindowMode()) {
            this.mTaskSupervisor.scheduleUpdateMultiWindowMode(this);
        }
        if (shouldStartChangeTransition(windowingMode, this.mTmpPrevBounds)) {
            initializeChangeTransition(this.mTmpPrevBounds);
        }
        if (getWindowConfiguration().persistTaskBounds()) {
            Rect requestedOverrideBounds = getRequestedOverrideBounds();
            if (!requestedOverrideBounds.isEmpty()) {
                setLastNonFullscreenBounds(requestedOverrideBounds);
            }
        }
        if (z2 && inPinnedWindowingMode && !this.mTransitionController.isShellTransitionsEnabled() && (activityRecord = topRunningActivity()) != null && this.mDisplayContent.isFixedRotationLaunchingApp(activityRecord)) {
            resetSurfaceControlTransforms();
        }
        saveLaunchingStateIfNeeded();
        boolean updateTaskOrganizerState = updateTaskOrganizerState();
        if (updateTaskOrganizerState) {
            updateSurfacePosition(getSyncTransaction());
            if (!isOrganized()) {
                updateSurfaceSize(getSyncTransaction());
            }
        }
        if (updateTaskOrganizerState) {
            return;
        }
        dispatchTaskInfoChangedIfNeeded(false);
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void onConfigurationChanged(Configuration configuration) {
        DisplayContent displayContent = this.mDisplayContent;
        if (displayContent == null || !displayContent.mPinnedTaskController.isFreezingTaskConfig(this)) {
            if (!isRootTask()) {
                onConfigurationChangedInner(configuration);
                return;
            }
            int windowingMode = getWindowingMode();
            boolean isAlwaysOnTop = isAlwaysOnTop();
            int rotation = getWindowConfiguration().getRotation();
            Rect rect = this.mTmpRect;
            getBounds(rect);
            onConfigurationChangedInner(configuration);
            TaskDisplayArea displayArea = getDisplayArea();
            if (displayArea == null) {
                return;
            }
            if (windowingMode != getWindowingMode()) {
                displayArea.onRootTaskWindowingModeChanged(this);
            }
            if (!isOrganized() && !getRequestedOverrideBounds().isEmpty() && this.mDisplayContent != null) {
                int rotation2 = getWindowConfiguration().getRotation();
                if (rotation != rotation2) {
                    this.mDisplayContent.rotateBounds(rotation, rotation2, rect);
                    setBounds(rect);
                }
            }
            if (isAlwaysOnTop != isAlwaysOnTop()) {
                displayArea.positionChildAt(Integer.MAX_VALUE, this, false);
            }
        }
    }

    public void resolveLeafTaskOnlyOverrideConfigs(Configuration configuration, Rect rect) {
        if (isLeafTask()) {
            int windowingMode = getResolvedOverrideConfiguration().windowConfiguration.getWindowingMode();
            if (windowingMode == 0) {
                windowingMode = configuration.windowConfiguration.getWindowingMode();
            }
            getConfiguration().windowConfiguration.setWindowingMode(windowingMode);
            Rect bounds = getResolvedOverrideConfiguration().windowConfiguration.getBounds();
            if (windowingMode == 1) {
                if (this.mCreatedByOrganizer) {
                    return;
                }
                bounds.setEmpty();
                return;
            }
            adjustForMinimalTaskDimensions(bounds, rect, configuration);
            if (windowingMode == 5) {
                computeFreeformBounds(bounds, configuration);
            }
        }
    }

    public void adjustForMinimalTaskDimensions(Rect rect, Rect rect2, Configuration configuration) {
        int i;
        int i2;
        int i3 = this.mMinWidth;
        int i4 = this.mMinHeight;
        if (!inPinnedWindowingMode()) {
            DisplayContent displayContent = this.mDisplayContent;
            int i5 = (int) ((displayContent == null ? 220 : displayContent.mMinSizeOfResizeableTaskDp) * (configuration.densityDpi / 160.0f));
            if (i3 == -1) {
                i3 = i5;
            }
            if (i4 == -1) {
                i4 = i5;
            }
        }
        if (rect.isEmpty()) {
            Rect bounds = configuration.windowConfiguration.getBounds();
            if (bounds.width() >= i3 && bounds.height() >= i4) {
                return;
            }
            rect.set(bounds);
        }
        boolean z = i3 > rect.width();
        boolean z2 = i4 > rect.height();
        if (z || z2) {
            if (z) {
                if (!rect2.isEmpty() && (i2 = rect.right) == rect2.right) {
                    rect.left = i2 - i3;
                } else {
                    rect.right = rect.left + i3;
                }
            }
            if (z2) {
                if (!rect2.isEmpty() && (i = rect.bottom) == rect2.bottom) {
                    rect.top = i - i4;
                } else {
                    rect.bottom = rect.top + i4;
                }
            }
        }
    }

    public final void computeFreeformBounds(Rect rect, Configuration configuration) {
        float f = configuration.densityDpi / 160.0f;
        Rect rect2 = new Rect(configuration.windowConfiguration.getBounds());
        DisplayContent displayContent = getDisplayContent();
        if (displayContent != null) {
            Rect rect3 = new Rect();
            displayContent.getStableRect(rect3);
            rect2.intersect(rect3);
        }
        fitWithinBounds(rect, rect2, (int) (48.0f * f), (int) (f * 32.0f));
        int i = rect2.top - rect.top;
        if (i > 0) {
            rect.offset(0, i);
        }
    }

    public static void fitWithinBounds(Rect rect, Rect rect2, int i, int i2) {
        int i3;
        if (rect2 == null || rect2.isEmpty() || rect2.contains(rect)) {
            return;
        }
        int min = Math.min(i, rect.width());
        int i4 = rect.right;
        int i5 = rect2.left;
        int i6 = 0;
        if (i4 < i5 + min) {
            i3 = min - (i4 - i5);
        } else {
            int i7 = rect.left;
            int i8 = rect2.right;
            i3 = i7 > i8 - min ? -(min - (i8 - i7)) : 0;
        }
        int min2 = Math.min(i2, rect.width());
        int i9 = rect.bottom;
        int i10 = rect2.top;
        if (i9 < i10 + min2) {
            i6 = min2 - (i9 - i10);
        } else {
            int i11 = rect.top;
            int i12 = rect2.bottom;
            if (i11 > i12 - min2) {
                i6 = -(min2 - (i12 - i11));
            }
        }
        rect.offset(i3, i6);
    }

    public final boolean shouldStartChangeTransition(int i, Rect rect) {
        if ((isLeafTask() || this.mCreatedByOrganizer) && canStartChangeTransition()) {
            int windowingMode = getWindowingMode();
            if (!this.mTransitionController.inTransition(this)) {
                return (i == 5) != (windowingMode == 5);
            }
            Rect bounds = getConfiguration().windowConfiguration.getBounds();
            return (i == windowingMode && rect.width() == bounds.width() && rect.height() == bounds.height()) ? false : true;
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void migrateToNewSurfaceControl(SurfaceControl.Transaction transaction) {
        super.migrateToNewSurfaceControl(transaction);
        Point point = this.mLastSurfaceSize;
        point.x = 0;
        point.y = 0;
        updateSurfaceSize(transaction);
    }

    public void updateSurfaceSize(SurfaceControl.Transaction transaction) {
        int i;
        int i2;
        if (this.mSurfaceControl == null || isOrganized()) {
            return;
        }
        if (isRootTask()) {
            Rect bounds = getBounds();
            i = bounds.width();
            i2 = bounds.height();
        } else {
            i = 0;
            i2 = 0;
        }
        Point point = this.mLastSurfaceSize;
        if (i == point.x && i2 == point.y) {
            return;
        }
        transaction.setWindowCrop(this.mSurfaceControl, i, i2);
        this.mLastSurfaceSize.set(i, i2);
    }

    @VisibleForTesting
    public Point getLastSurfaceSize() {
        return this.mLastSurfaceSize;
    }

    @VisibleForTesting
    public boolean isInChangeTransition() {
        return this.mSurfaceFreezer.hasLeash() || AppTransition.isChangeTransitOld(this.mTransit);
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.SurfaceFreezer.Freezable
    public SurfaceControl getFreezeSnapshotTarget() {
        if (this.mDisplayContent.mAppTransition.containsTransitRequest(6)) {
            ArraySet<Integer> arraySet = new ArraySet<>();
            arraySet.add(Integer.valueOf(getActivityType()));
            RemoteAnimationAdapter remoteAnimationOverride = this.mDisplayContent.mAppTransitionController.getRemoteAnimationOverride(this, 27, arraySet);
            if (remoteAnimationOverride == null || remoteAnimationOverride.getChangeNeedsSnapshot()) {
                return getSurfaceControl();
            }
            return null;
        }
        return null;
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public void writeIdentifierToProto(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, System.identityHashCode(this));
        protoOutputStream.write(1120986464258L, this.mUserId);
        Intent intent = this.intent;
        protoOutputStream.write(1138166333443L, (intent == null || intent.getComponent() == null) ? "Task" : this.intent.getComponent().flattenToShortString());
        protoOutputStream.end(start);
    }

    public final void saveLaunchingStateIfNeeded() {
        saveLaunchingStateIfNeeded(getDisplayContent());
    }

    public final void saveLaunchingStateIfNeeded(DisplayContent displayContent) {
        if (isLeafTask() && getHasBeenVisible()) {
            int windowingMode = getWindowingMode();
            if ((windowingMode == 1 || windowingMode == 5) && getWindowConfiguration().getDisplayWindowingMode() == 5) {
                this.mTaskSupervisor.mLaunchParamsPersister.saveTask(this, displayContent);
            }
        }
    }

    public Rect updateOverrideConfigurationFromLaunchBounds() {
        Task rootTask = getRootTask();
        Rect launchBounds = (rootTask == this || !rootTask.isOrganized()) ? getLaunchBounds() : null;
        setBounds(launchBounds);
        if (launchBounds != null && !launchBounds.isEmpty()) {
            launchBounds.set(getRequestedOverrideBounds());
        }
        return launchBounds;
    }

    public Rect getLaunchBounds() {
        Task rootTask = getRootTask();
        if (rootTask == null) {
            return null;
        }
        int windowingMode = getWindowingMode();
        if (!isActivityTypeStandardOrUndefined() || windowingMode == 1) {
            if (isResizeable()) {
                return rootTask.getRequestedOverrideBounds();
            }
            return null;
        } else if (!getWindowConfiguration().persistTaskBounds()) {
            return rootTask.getRequestedOverrideBounds();
        } else {
            return this.mLastNonFullscreenBounds;
        }
    }

    public void setRootProcess(WindowProcessController windowProcessController) {
        clearRootProcess();
        Intent intent = this.intent;
        if (intent == null || (intent.getFlags() & 8388608) != 0) {
            return;
        }
        this.mRootProcess = windowProcessController;
        windowProcessController.addRecentTask(this);
    }

    public void clearRootProcess() {
        WindowProcessController windowProcessController = this.mRootProcess;
        if (windowProcessController != null) {
            windowProcessController.removeRecentTask(this);
            this.mRootProcess = null;
        }
    }

    public int getRootTaskId() {
        return getRootTask().mTaskId;
    }

    public Task getOrganizedTask() {
        Task asTask;
        if (isOrganized()) {
            return this;
        }
        WindowContainer parent = getParent();
        if (parent == null || (asTask = parent.asTask()) == null) {
            return null;
        }
        return asTask.getOrganizedTask();
    }

    public Task getCreatedByOrganizerTask() {
        Task asTask;
        if (this.mCreatedByOrganizer) {
            return this;
        }
        WindowContainer parent = getParent();
        if (parent == null || (asTask = parent.asTask()) == null) {
            return null;
        }
        return asTask.getCreatedByOrganizerTask();
    }

    public boolean isRootTask() {
        return getRootTask() == this;
    }

    public boolean isLeafTask() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if (((WindowContainer) this.mChildren.get(size)).asTask() != null) {
                return false;
            }
        }
        return true;
    }

    public Task getTopLeafTask() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            Task asTask = ((WindowContainer) this.mChildren.get(size)).asTask();
            if (asTask != null) {
                return asTask.getTopLeafTask();
            }
        }
        return this;
    }

    public int getDescendantTaskCount() {
        final int[] iArr = {0};
        forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda31
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Task.lambda$getDescendantTaskCount$5(iArr, (Task) obj);
            }
        }, false);
        return iArr[0];
    }

    public static /* synthetic */ void lambda$getDescendantTaskCount$5(int[] iArr, Task task) {
        iArr[0] = iArr[0] + 1;
    }

    public Task adjustFocusToNextFocusableTask(String str) {
        return adjustFocusToNextFocusableTask(str, false, true);
    }

    public final Task getNextFocusableTask(final boolean z) {
        WindowContainer parent = getParent();
        if (parent == null) {
            return null;
        }
        Task task = parent.getTask(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda30
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getNextFocusableTask$6;
                lambda$getNextFocusableTask$6 = Task.this.lambda$getNextFocusableTask$6(z, obj);
                return lambda$getNextFocusableTask$6;
            }
        });
        return (task != null || parent.asTask() == null) ? task : parent.asTask().getNextFocusableTask(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getNextFocusableTask$6(boolean z, Object obj) {
        return (z || obj != this) && ((Task) obj).isFocusableAndVisible();
    }

    public Task adjustFocusToNextFocusableTask(String str, boolean z, boolean z2) {
        Task nextFocusableTask = getNextFocusableTask(z);
        if (nextFocusableTask == null) {
            nextFocusableTask = this.mRootWindowContainer.getNextFocusableRootTask(this, !z);
        }
        if (nextFocusableTask == null) {
            TaskDisplayArea displayArea = getDisplayArea();
            if (displayArea != null) {
                displayArea.clearPreferredTopFocusableRootTask();
                return null;
            }
            return null;
        }
        Task rootTask = nextFocusableTask.getRootTask();
        if (!z2) {
            WindowContainer parent = nextFocusableTask.getParent();
            do {
                Task task = nextFocusableTask;
                nextFocusableTask = parent;
                nextFocusableTask.positionChildAt(Integer.MAX_VALUE, task, false);
                parent = nextFocusableTask.getParent();
                if (nextFocusableTask.asTask() == null) {
                    break;
                }
            } while (parent != null);
            return rootTask;
        }
        String str2 = str + " adjustFocusToNextFocusableTask";
        ActivityRecord activityRecord = nextFocusableTask.topRunningActivity();
        if (nextFocusableTask.isActivityTypeHome() && (activityRecord == null || !activityRecord.isVisibleRequested())) {
            nextFocusableTask.getDisplayArea().moveHomeActivityToTop(str2);
            return rootTask;
        }
        nextFocusableTask.moveToFront(str2);
        if (rootTask.getTopResumedActivity() != null) {
            this.mTaskSupervisor.updateTopResumedActivityIfNeeded(str);
        }
        return rootTask;
    }

    public final int computeMinUserPosition(int i, int i2) {
        while (i < i2 && !((WindowContainer) this.mChildren.get(i)).showToCurrentUser()) {
            i++;
        }
        return i;
    }

    public final int computeMaxUserPosition(int i) {
        while (i > 0 && ((WindowContainer) this.mChildren.get(i)).showToCurrentUser()) {
            i--;
        }
        return i;
    }

    public final int getAdjustedChildPosition(WindowContainer windowContainer, int i) {
        int i2;
        boolean showToCurrentUser = windowContainer.showToCurrentUser();
        int size = this.mChildren.size();
        int computeMinUserPosition = showToCurrentUser ? computeMinUserPosition(0, size) : 0;
        if (size > 0) {
            i2 = showToCurrentUser ? size - 1 : computeMaxUserPosition(size - 1);
        } else {
            i2 = computeMinUserPosition;
        }
        if (!windowContainer.isAlwaysOnTop()) {
            while (i2 > computeMinUserPosition && ((WindowContainer) this.mChildren.get(i2)).isAlwaysOnTop()) {
                i2--;
            }
        }
        if (i == Integer.MIN_VALUE && computeMinUserPosition == 0) {
            return Integer.MIN_VALUE;
        }
        if (i != Integer.MAX_VALUE || i2 < size - 1) {
            if (!hasChild(windowContainer)) {
                i2++;
            }
            return Math.min(Math.max(i, computeMinUserPosition), i2);
        }
        return Integer.MAX_VALUE;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void positionChildAt(int i, WindowContainer windowContainer, boolean z) {
        boolean z2 = i >= this.mChildren.size() - 1;
        int adjustedChildPosition = getAdjustedChildPosition(windowContainer, i);
        super.positionChildAt(adjustedChildPosition, windowContainer, z);
        Task asTask = windowContainer.asTask();
        if (asTask != null) {
            asTask.updateTaskMovement(z2, adjustedChildPosition == Integer.MIN_VALUE, adjustedChildPosition);
        }
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public void removeImmediately() {
        removeImmediately("removeTask");
    }

    @Override // com.android.server.p014wm.TaskFragment
    public void removeImmediately(String str) {
        if (this.mRemoving) {
            return;
        }
        this.mRemoving = true;
        EventLogTags.writeWmTaskRemoved(this.mTaskId, getRootTaskId(), getDisplayId(), str);
        clearPinnedTaskIfNeed();
        setTaskOrganizer(null);
        super.removeImmediately();
        this.mRemoving = false;
    }

    public void reparent(Task task, int i, boolean z, String str) {
        int i2 = this.mTaskId;
        int rootTaskId = getRootTaskId();
        int displayId = getDisplayId();
        EventLogTags.writeWmTaskRemoved(i2, rootTaskId, displayId, "reParentTask:" + str);
        reparent(task, i);
        task.positionChildAt(i, this, z);
    }

    public int setBounds(Rect rect, boolean z) {
        int bounds = setBounds(rect);
        if (!z || (bounds & 2) == 2) {
            return bounds;
        }
        onResize();
        return bounds | 2;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public int setBounds(Rect rect) {
        if (isRootTask()) {
            return setBounds(getRequestedOverrideBounds(), rect);
        }
        DisplayContent displayContent = getRootTask() != null ? getRootTask().getDisplayContent() : null;
        int i = displayContent != null ? displayContent.getDisplayInfo().rotation : 0;
        int bounds = super.setBounds(rect);
        this.mRotation = i;
        updateSurfacePositionNonOrganized();
        return bounds;
    }

    public int setBoundsUnchecked(Rect rect) {
        int bounds = super.setBounds(rect);
        updateSurfaceBounds();
        return bounds;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public boolean isCompatible(int i, int i2) {
        if (i2 == 0) {
            i2 = 1;
        }
        return super.isCompatible(i, i2);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean onDescendantOrientationChanged(WindowContainer windowContainer) {
        if (super.onDescendantOrientationChanged(windowContainer)) {
            return true;
        }
        if (getParent() != null) {
            onConfigurationChanged(getParent().getConfiguration());
            return true;
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean handlesOrientationChangeFromDescendant(int i) {
        if (super.handlesOrientationChangeFromDescendant(i)) {
            if (isLeafTask()) {
                return canSpecifyOrientation() && getDisplayArea().canSpecifyOrientation(i);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void onDisplayChanged(DisplayContent displayContent) {
        if (!isRootTask() && !this.mCreatedByOrganizer) {
            adjustBoundsForDisplayChangeIfNeeded(displayContent);
        }
        super.onDisplayChanged(displayContent);
        if (isLeafTask()) {
            this.mWmService.mAtmService.getTaskChangeNotificationController().notifyTaskDisplayChanged(this.mTaskId, displayContent != null ? displayContent.getDisplayId() : -1);
        }
        if (isRootTask()) {
            updateSurfaceBounds();
        }
        sendTaskFragmentParentInfoChangedIfNeeded();
    }

    public boolean isResizeable() {
        return isResizeable(true);
    }

    public boolean isResizeable(boolean z) {
        return (this.mAtmService.mForceResizableActivities && getActivityType() == 1) || ActivityInfo.isResizeableMode(this.mResizeMode) || (this.mSupportsPictureInPicture && z);
    }

    public boolean preserveOrientationOnResize() {
        int i = this.mResizeMode;
        return i == 6 || i == 5 || i == 7;
    }

    public boolean cropWindowsToRootTaskBounds() {
        if (isActivityTypeHomeOrRecents()) {
            Task rootTask = getRootTask();
            if (rootTask.mCreatedByOrganizer) {
                rootTask = rootTask.getTopMostTask();
            }
            if (this == rootTask || isDescendantOf(rootTask)) {
                return false;
            }
        }
        return isResizeable();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void getAnimationFrames(Rect rect, Rect rect2, Rect rect3, Rect rect4) {
        if (getAdjacentTaskFragment() != null && getAdjacentTaskFragment().asTask() != null) {
            super.getAnimationFrames(rect, rect2, rect3, rect4);
            return;
        }
        WindowState topVisibleAppMainWindow = getTopVisibleAppMainWindow();
        if (topVisibleAppMainWindow != null) {
            topVisibleAppMainWindow.getAnimationFrames(rect, rect2, rect3, rect4);
        } else {
            super.getAnimationFrames(rect, rect2, rect3, rect4);
        }
    }

    public static void getMaxVisibleBounds(ActivityRecord activityRecord, Rect rect, boolean[] zArr) {
        WindowState findMainWindow;
        if (activityRecord.mIsExiting || !activityRecord.isClientVisible() || !activityRecord.isVisibleRequested() || (findMainWindow = activityRecord.findMainWindow()) == null) {
            return;
        }
        if (!zArr[0]) {
            zArr[0] = true;
            rect.setEmpty();
        }
        Rect rect2 = sTmpBounds;
        WindowManager.LayoutParams layoutParams = findMainWindow.mAttrs;
        rect2.set(findMainWindow.getFrame());
        rect2.inset(findMainWindow.getInsetsStateWithVisibilityOverride().calculateVisibleInsets(rect2, layoutParams.type, findMainWindow.getWindowingMode(), layoutParams.softInputMode, layoutParams.flags));
        rect.union(rect2);
    }

    public void getDimBounds(final Rect rect) {
        if (isRootTask()) {
            getBounds(rect);
            return;
        }
        Task rootTask = getRootTask();
        if (inFreeformWindowingMode()) {
            final boolean[] zArr = {false};
            forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    Task.getMaxVisibleBounds((ActivityRecord) obj, rect, zArr);
                }
            });
            if (zArr[0]) {
                return;
            }
        }
        if (!matchParentBounds()) {
            rootTask.getBounds(this.mTmpRect);
            this.mTmpRect.intersect(getBounds());
            rect.set(this.mTmpRect);
            return;
        }
        rect.set(getBounds());
    }

    public void adjustAnimationBoundsForTransition(Rect rect) {
        if (this.mWmService.mTaskTransitionSpec != null) {
            InsetsState rawInsetsState = getDisplayContent().getInsetsStateController().getRawInsetsState();
            for (int sourceSize = rawInsetsState.sourceSize() - 1; sourceSize >= 0; sourceSize--) {
                InsetsSource sourceAt = rawInsetsState.sourceAt(sourceSize);
                if (sourceAt.insetsRoundedCornerFrame()) {
                    rect.inset(sourceAt.calculateVisibleInsets(rect));
                }
            }
        }
    }

    public void setDragResizing(boolean z) {
        if (this.mDragResizing != z) {
            if (z && getRootTask().getWindowingMode() != 5) {
                throw new IllegalArgumentException("Drag resize not allow for root task id=" + getRootTaskId());
            }
            this.mDragResizing = z;
            resetDragResizingChangeReported();
        }
    }

    public boolean isDragResizing() {
        return this.mDragResizing;
    }

    public void adjustBoundsForDisplayChangeIfNeeded(DisplayContent displayContent) {
        if (displayContent == null || getRequestedOverrideBounds().isEmpty()) {
            return;
        }
        int displayId = displayContent.getDisplayId();
        int i = displayContent.getDisplayInfo().rotation;
        if (displayId != this.mLastRotationDisplayId) {
            this.mLastRotationDisplayId = displayId;
            this.mRotation = i;
        } else if (this.mRotation == i) {
        } else {
            this.mTmpRect2.set(getBounds());
            if (!getWindowConfiguration().canResizeTask()) {
                setBounds(this.mTmpRect2);
                return;
            }
            displayContent.rotateBounds(this.mRotation, i, this.mTmpRect2);
            if (setBounds(this.mTmpRect2) != 0) {
                this.mAtmService.resizeTask(this.mTaskId, getBounds(), 1);
            }
        }
    }

    public void cancelTaskWindowTransition() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            ((WindowContainer) this.mChildren.get(size)).cancelAnimation();
        }
    }

    public boolean showForAllUsers() {
        ActivityRecord topNonFinishingActivity;
        return (this.mChildren.isEmpty() || (topNonFinishingActivity = getTopNonFinishingActivity()) == null || !topNonFinishingActivity.mShowForAllUsers) ? false : true;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean showToCurrentUser() {
        return this.mForceShowForAllUsers || showForAllUsers() || this.mWmService.isUserVisible(getTopMostTask().mUserId);
    }

    public void setForceShowForAllUsers(boolean z) {
        this.mForceShowForAllUsers = z;
    }

    public ActivityRecord getOccludingActivityAbove(final ActivityRecord activityRecord) {
        ActivityRecord activity = getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda29
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getOccludingActivityAbove$8;
                lambda$getOccludingActivityAbove$8 = Task.lambda$getOccludingActivityAbove$8(ActivityRecord.this, (ActivityRecord) obj);
                return lambda$getOccludingActivityAbove$8;
            }
        });
        if (activity != activityRecord) {
            return activity;
        }
        return null;
    }

    public static /* synthetic */ boolean lambda$getOccludingActivityAbove$8(ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        if (activityRecord2 == activityRecord) {
            return true;
        }
        if (activityRecord2.occludesParent()) {
            TaskFragment taskFragment = activityRecord2.getTaskFragment();
            if (taskFragment == activityRecord.getTaskFragment()) {
                return true;
            }
            if (taskFragment == null || taskFragment.asTask() == null) {
                TaskFragment asTaskFragment = taskFragment.getParent().asTaskFragment();
                while (true) {
                    TaskFragment taskFragment2 = taskFragment;
                    taskFragment = asTaskFragment;
                    if (taskFragment == null || !taskFragment2.getBounds().equals(taskFragment.getBounds())) {
                        break;
                    } else if (taskFragment.asTask() != null) {
                        return true;
                    } else {
                        asTaskFragment = taskFragment.getParent().asTaskFragment();
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl.Builder makeAnimationLeash() {
        return super.makeAnimationLeash().setMetadata(3, this.mTaskId);
    }

    public boolean shouldAnimate() {
        if (isOrganized()) {
            return false;
        }
        RecentsAnimationController recentsAnimationController = this.mWmService.getRecentsAnimationController();
        return (recentsAnimationController != null && recentsAnimationController.isAnimatingTask(this) && recentsAnimationController.shouldDeferCancelUntilNextTransition()) ? false : true;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void setInitialSurfaceControlProperties(SurfaceControl.Builder builder) {
        builder.setEffectLayer().setMetadata(3, this.mTaskId);
        super.setInitialSurfaceControlProperties(builder);
    }

    public boolean isAnimatingByRecents() {
        return isAnimating(4, 8);
    }

    public WindowState getTopVisibleAppMainWindow() {
        ActivityRecord topVisibleActivity = getTopVisibleActivity();
        if (topVisibleActivity != null) {
            return topVisibleActivity.findMainWindow();
        }
        return null;
    }

    public ActivityRecord topRunningNonDelayedActivityLocked(ActivityRecord activityRecord) {
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new BiPredicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda19
            @Override // java.util.function.BiPredicate
            public final boolean test(Object obj, Object obj2) {
                boolean isTopRunningNonDelayed;
                isTopRunningNonDelayed = Task.isTopRunningNonDelayed((ActivityRecord) obj, (ActivityRecord) obj2);
                return isTopRunningNonDelayed;
            }
        }, PooledLambda.__(ActivityRecord.class), activityRecord);
        ActivityRecord activity = getActivity(obtainPredicate);
        obtainPredicate.recycle();
        return activity;
    }

    public static boolean isTopRunningNonDelayed(ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        return (activityRecord.delayedResume || activityRecord == activityRecord2 || !activityRecord.canBeTopRunning()) ? false : true;
    }

    public ActivityRecord topRunningActivity(IBinder iBinder, int i) {
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new TriPredicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda7
            public final boolean test(Object obj, Object obj2, Object obj3) {
                boolean isTopRunning;
                isTopRunning = Task.isTopRunning((ActivityRecord) obj, ((Integer) obj2).intValue(), (IBinder) obj3);
                return isTopRunning;
            }
        }, PooledLambda.__(ActivityRecord.class), Integer.valueOf(i), iBinder);
        ActivityRecord activity = getActivity(obtainPredicate);
        obtainPredicate.recycle();
        return activity;
    }

    public static boolean isTopRunning(ActivityRecord activityRecord, int i, IBinder iBinder) {
        return (activityRecord.getTask().mTaskId == i || activityRecord.token == iBinder || !activityRecord.canBeTopRunning()) ? false : true;
    }

    public ActivityRecord getTopFullscreenActivity() {
        return getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda27
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getTopFullscreenActivity$9;
                lambda$getTopFullscreenActivity$9 = Task.lambda$getTopFullscreenActivity$9((ActivityRecord) obj);
                return lambda$getTopFullscreenActivity$9;
            }
        });
    }

    public static /* synthetic */ boolean lambda$getTopFullscreenActivity$9(ActivityRecord activityRecord) {
        WindowState findMainWindow = activityRecord.findMainWindow();
        return findMainWindow != null && findMainWindow.mAttrs.isFullscreen();
    }

    public static /* synthetic */ boolean lambda$getTopVisibleActivity$10(ActivityRecord activityRecord) {
        return !activityRecord.mIsExiting && activityRecord.isClientVisible() && activityRecord.isVisibleRequested();
    }

    public ActivityRecord getTopVisibleActivity() {
        return getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda28
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getTopVisibleActivity$10;
                lambda$getTopVisibleActivity$10 = Task.lambda$getTopVisibleActivity$10((ActivityRecord) obj);
                return lambda$getTopVisibleActivity$10;
            }
        });
    }

    public static /* synthetic */ boolean lambda$getTopRealVisibleActivity$11(ActivityRecord activityRecord) {
        return !activityRecord.mIsExiting && activityRecord.isClientVisible() && activityRecord.isVisible();
    }

    public ActivityRecord getTopRealVisibleActivity() {
        return getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda43
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getTopRealVisibleActivity$11;
                lambda$getTopRealVisibleActivity$11 = Task.lambda$getTopRealVisibleActivity$11((ActivityRecord) obj);
                return lambda$getTopRealVisibleActivity$11;
            }
        });
    }

    public ActivityRecord getTopWaitSplashScreenActivity() {
        return getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda17
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getTopWaitSplashScreenActivity$12;
                lambda$getTopWaitSplashScreenActivity$12 = Task.lambda$getTopWaitSplashScreenActivity$12((ActivityRecord) obj);
                return lambda$getTopWaitSplashScreenActivity$12;
            }
        });
    }

    public static /* synthetic */ boolean lambda$getTopWaitSplashScreenActivity$12(ActivityRecord activityRecord) {
        return activityRecord.mHandleExitSplashScreen && activityRecord.mTransferringSplashScreenState == 1;
    }

    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
        this.mTaskDescription = taskDescription;
    }

    public void onSnapshotChanged(TaskSnapshot taskSnapshot) {
        this.mLastTaskSnapshotData.set(taskSnapshot);
        this.mAtmService.getTaskChangeNotificationController().notifyTaskSnapshotChanged(this.mTaskId, taskSnapshot);
    }

    public ActivityManager.TaskDescription getTaskDescription() {
        return this.mTaskDescription;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void forAllLeafTasks(Consumer<Task> consumer, boolean z) {
        int size = this.mChildren.size();
        boolean z2 = true;
        if (z) {
            for (int i = size - 1; i >= 0; i--) {
                Task asTask = ((WindowContainer) this.mChildren.get(i)).asTask();
                if (asTask != null) {
                    asTask.forAllLeafTasks(consumer, z);
                    z2 = false;
                }
            }
        } else {
            for (int i2 = 0; i2 < size; i2++) {
                Task asTask2 = ((WindowContainer) this.mChildren.get(i2)).asTask();
                if (asTask2 != null) {
                    asTask2.forAllLeafTasks(consumer, z);
                    z2 = false;
                }
            }
        }
        if (z2) {
            consumer.accept(this);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void forAllTasks(Consumer<Task> consumer, boolean z) {
        super.forAllTasks(consumer, z);
        consumer.accept(this);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void forAllRootTasks(Consumer<Task> consumer, boolean z) {
        if (isRootTask()) {
            consumer.accept(this);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean forAllTasks(Predicate<Task> predicate) {
        if (super.forAllTasks(predicate)) {
            return true;
        }
        return predicate.test(this);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean forAllLeafTasks(Predicate<Task> predicate) {
        boolean z = true;
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            Task asTask = ((WindowContainer) this.mChildren.get(size)).asTask();
            if (asTask != null) {
                if (asTask.forAllLeafTasks(predicate)) {
                    return true;
                }
                z = false;
            }
        }
        if (z) {
            return predicate.test(this);
        }
        return false;
    }

    public void forAllLeafTasksAndLeafTaskFragments(final Consumer<TaskFragment> consumer, final boolean z) {
        forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda37
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Task.lambda$forAllLeafTasksAndLeafTaskFragments$13(consumer, z, (Task) obj);
            }
        }, z);
    }

    public static /* synthetic */ void lambda$forAllLeafTasksAndLeafTaskFragments$13(Consumer consumer, boolean z, Task task) {
        if (task.isLeafTaskFragment()) {
            consumer.accept(task);
            return;
        }
        int i = 0;
        if (z) {
            for (int size = task.mChildren.size() - 1; size >= 0; size--) {
                WindowContainer windowContainer = (WindowContainer) task.mChildren.get(size);
                if (windowContainer.asTaskFragment() != null) {
                    windowContainer.forAllLeafTaskFragments(consumer, z);
                } else if (windowContainer.asActivityRecord() != null && i == 0) {
                    consumer.accept(task);
                    i = 1;
                }
            }
            return;
        }
        boolean z2 = false;
        while (i < task.mChildren.size()) {
            WindowContainer windowContainer2 = (WindowContainer) task.mChildren.get(i);
            if (windowContainer2.asTaskFragment() != null) {
                windowContainer2.forAllLeafTaskFragments(consumer, z);
            } else if (windowContainer2.asActivityRecord() != null && !z2) {
                consumer.accept(task);
                z2 = true;
            }
            i++;
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean forAllRootTasks(Predicate<Task> predicate, boolean z) {
        if (isRootTask()) {
            return predicate.test(this);
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public Task getTask(Predicate<Task> predicate, boolean z) {
        Task task = super.getTask(predicate, z);
        if (task != null) {
            return task;
        }
        if (predicate.test(this)) {
            return this;
        }
        return null;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public Task getRootTask(Predicate<Task> predicate, boolean z) {
        if (isRootTask() && predicate.test(this)) {
            return this;
        }
        return null;
    }

    public void setCanAffectSystemUiFlags(boolean z) {
        this.mCanAffectSystemUiFlags = z;
    }

    public boolean canAffectSystemUiFlags() {
        return this.mCanAffectSystemUiFlags;
    }

    public void dontAnimateDimExit() {
        this.mDimmer.dontAnimateExit();
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public String getName() {
        return "Task=" + this.mTaskId;
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public Dimmer getDimmer() {
        if (inMultiWindowMode()) {
            return this.mDimmer;
        }
        if (!isRootTask() || isTranslucent(null)) {
            return super.getDimmer();
        }
        return this.mDimmer;
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public void prepareSurfaces() {
        this.mDimmer.resetDimStates();
        super.prepareSurfaces();
        getDimBounds(this.mTmpDimBoundsRect);
        boolean z = false;
        if (inFreeformWindowingMode()) {
            getBounds(this.mTmpRect);
            Rect rect = this.mTmpDimBoundsRect;
            int i = rect.left;
            Rect rect2 = this.mTmpRect;
            rect.offsetTo(i - rect2.left, rect.top - rect2.top);
        } else {
            this.mTmpDimBoundsRect.offsetTo(0, 0);
        }
        SurfaceControl.Transaction syncTransaction = getSyncTransaction();
        if (this.mDimmer.updateDims(syncTransaction, this.mTmpDimBoundsRect)) {
            scheduleAnimation();
        }
        boolean isVisible = isVisible();
        z = (isVisible || isAnimating(7)) ? true : true;
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null && z != this.mLastSurfaceShowing) {
            syncTransaction.setVisibility(surfaceControl, z);
        }
        TrustedOverlayHost trustedOverlayHost = this.mOverlayHost;
        if (trustedOverlayHost != null) {
            trustedOverlayHost.setVisibility(syncTransaction, isVisible);
        }
        this.mLastSurfaceShowing = z;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void applyAnimationUnchecked(WindowManager.LayoutParams layoutParams, boolean z, int i, boolean z2, final ArrayList<WindowContainer> arrayList) {
        RecentsAnimationController recentsAnimationController = this.mWmService.getRecentsAnimationController();
        if (recentsAnimationController != null) {
            if (!z || isActivityTypeHomeOrRecents()) {
                return;
            }
            if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, 210750281, 0, (String) null, new Object[]{String.valueOf(recentsAnimationController), String.valueOf(asTask()), String.valueOf(AppTransition.appTransitionOldToString(i))});
            }
            final int size = arrayList != null ? arrayList.size() : 0;
            recentsAnimationController.addTaskToTargets(this, new SurfaceAnimator.OnAnimationFinishedCallback() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda15
                @Override // com.android.server.p014wm.SurfaceAnimator.OnAnimationFinishedCallback
                public final void onAnimationFinished(int i2, AnimationAdapter animationAdapter) {
                    Task.lambda$applyAnimationUnchecked$14(size, arrayList, i2, animationAdapter);
                }
            });
            return;
        }
        super.applyAnimationUnchecked(layoutParams, z, i, z2, arrayList);
    }

    public static /* synthetic */ void lambda$applyAnimationUnchecked$14(int i, ArrayList arrayList, int i2, AnimationAdapter animationAdapter) {
        for (int i3 = 0; i3 < i; i3++) {
            ((WindowContainer) arrayList.get(i3)).onAnimationFinished(i2, animationAdapter);
        }
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public void dump(PrintWriter printWriter, String str, boolean z) {
        super.dump(printWriter, str, z);
        this.mAnimatingActivityRegistry.dump(printWriter, "AnimatingApps:", str);
    }

    public void fillTaskInfo(TaskInfo taskInfo) {
        fillTaskInfo(taskInfo, true);
    }

    public void fillTaskInfo(TaskInfo taskInfo, boolean z) {
        fillTaskInfo(taskInfo, z, getDisplayArea());
    }

    public void fillTaskInfo(TaskInfo taskInfo, boolean z, TaskDisplayArea taskDisplayArea) {
        Intent cloneFilter;
        taskInfo.launchCookies.clear();
        taskInfo.addLaunchCookie(this.mLaunchCookie);
        ActivityRecord fillAndReturnTop = this.mTaskSupervisor.mTaskInfoHelper.fillAndReturnTop(this, taskInfo);
        taskInfo.userId = isLeafTask() ? this.mUserId : this.mCurrentUser;
        taskInfo.taskId = this.mTaskId;
        taskInfo.displayId = getDisplayId();
        int i = -1;
        taskInfo.displayAreaFeatureId = taskDisplayArea != null ? taskDisplayArea.mFeatureId : -1;
        Intent baseIntent = getBaseIntent();
        int flags = baseIntent == null ? 0 : baseIntent.getFlags();
        if (baseIntent == null) {
            cloneFilter = new Intent();
        } else {
            cloneFilter = z ? baseIntent.cloneFilter() : new Intent(baseIntent);
        }
        taskInfo.baseIntent = cloneFilter;
        cloneFilter.setFlags(flags);
        boolean z2 = true;
        taskInfo.isRunning = fillAndReturnTop != null;
        taskInfo.topActivity = fillAndReturnTop != null ? fillAndReturnTop.mActivityComponent : null;
        taskInfo.origActivity = this.origActivity;
        taskInfo.realActivity = this.realActivity;
        taskInfo.lastActiveTime = this.lastActiveTime;
        taskInfo.taskDescription = new ActivityManager.TaskDescription(getTaskDescription());
        taskInfo.supportsMultiWindow = supportsMultiWindowInDisplayArea(taskDisplayArea);
        taskInfo.configuration.setTo(getConfiguration());
        taskInfo.configuration.windowConfiguration.setActivityType(getActivityType());
        taskInfo.configuration.windowConfiguration.setWindowingMode(getWindowingMode());
        taskInfo.token = this.mRemoteToken.toWindowContainerToken();
        Task task = fillAndReturnTop != null ? fillAndReturnTop.getTask() : this;
        taskInfo.resizeMode = task.mResizeMode;
        taskInfo.topActivityType = task.getActivityType();
        taskInfo.displayCutoutInsets = task.getDisplayCutoutInsets();
        taskInfo.isResizeable = isResizeable();
        taskInfo.minWidth = this.mMinWidth;
        taskInfo.minHeight = this.mMinHeight;
        DisplayContent displayContent = this.mDisplayContent;
        taskInfo.defaultMinSize = displayContent == null ? 220 : displayContent.mMinSizeOfResizeableTaskDp;
        taskInfo.positionInParent = getRelativePosition();
        taskInfo.topActivityInfo = fillAndReturnTop != null ? fillAndReturnTop.info : null;
        PictureInPictureParams pictureInPictureParams = getPictureInPictureParams(fillAndReturnTop);
        taskInfo.pictureInPictureParams = pictureInPictureParams;
        taskInfo.launchIntoPipHostTaskId = (pictureInPictureParams == null || !pictureInPictureParams.isLaunchIntoPip() || fillAndReturnTop.getLastParentBeforePip() == null) ? -1 : fillAndReturnTop.getLastParentBeforePip().mTaskId;
        taskInfo.shouldDockBigOverlays = fillAndReturnTop != null && fillAndReturnTop.shouldDockBigOverlays;
        taskInfo.mTopActivityLocusId = fillAndReturnTop != null ? fillAndReturnTop.getLocusId() : null;
        boolean z3 = fillAndReturnTop != null && fillAndReturnTop.getOrganizedTask() == this && fillAndReturnTop.isState(ActivityRecord.State.RESUMED);
        boolean z4 = z3 && fillAndReturnTop.inSizeCompatMode();
        taskInfo.topActivityInSizeCompat = z4;
        if (z4 && this.mWmService.mLetterboxConfiguration.isTranslucentLetterboxingEnabled()) {
            taskInfo.topActivityInSizeCompat = fillAndReturnTop.fillsParent();
        }
        if (!z3 || !fillAndReturnTop.isEligibleForLetterboxEducation()) {
            z2 = false;
        }
        taskInfo.topActivityEligibleForLetterboxEducation = z2;
        taskInfo.cameraCompatControlState = z3 ? fillAndReturnTop.getCameraCompatControlState() : 0;
        Task asTask = getParent() != null ? getParent().asTask() : null;
        if (asTask != null && asTask.mCreatedByOrganizer) {
            i = asTask.mTaskId;
        }
        taskInfo.parentTaskId = i;
        taskInfo.isFocused = isFocused();
        taskInfo.isVisible = hasVisibleChildren();
        taskInfo.isSleeping = shouldSleepActivities();
    }

    public static /* synthetic */ boolean lambda$trimIneffectiveInfo$15(ActivityRecord activityRecord) {
        return !activityRecord.finishing;
    }

    public static void trimIneffectiveInfo(Task task, TaskInfo taskInfo) {
        ActivityRecord activity = task.getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda10
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$trimIneffectiveInfo$15;
                lambda$trimIneffectiveInfo$15 = Task.lambda$trimIneffectiveInfo$15((ActivityRecord) obj);
                return lambda$trimIneffectiveInfo$15;
            }
        }, false);
        int uid = activity != null ? activity.getUid() : task.effectiveUid;
        ActivityInfo activityInfo = taskInfo.topActivityInfo;
        if (activityInfo != null && task.effectiveUid != activityInfo.applicationInfo.uid) {
            ActivityInfo activityInfo2 = new ActivityInfo(taskInfo.topActivityInfo);
            taskInfo.topActivityInfo = activityInfo2;
            activityInfo2.applicationInfo = new ApplicationInfo(taskInfo.topActivityInfo.applicationInfo);
            taskInfo.topActivity = new ComponentName("", "");
            ActivityInfo activityInfo3 = taskInfo.topActivityInfo;
            activityInfo3.packageName = "";
            activityInfo3.taskAffinity = "";
            activityInfo3.processName = "";
            activityInfo3.name = "";
            activityInfo3.parentActivityName = "";
            activityInfo3.targetActivity = "";
            activityInfo3.splitName = "";
            ApplicationInfo applicationInfo = activityInfo3.applicationInfo;
            applicationInfo.className = "";
            applicationInfo.credentialProtectedDataDir = "";
            applicationInfo.dataDir = "";
            applicationInfo.deviceProtectedDataDir = "";
            applicationInfo.manageSpaceActivityName = "";
            applicationInfo.nativeLibraryDir = "";
            applicationInfo.nativeLibraryRootDir = "";
            applicationInfo.processName = "";
            applicationInfo.publicSourceDir = "";
            applicationInfo.scanPublicSourceDir = "";
            applicationInfo.scanSourceDir = "";
            applicationInfo.sourceDir = "";
            applicationInfo.taskAffinity = "";
            applicationInfo.name = "";
            applicationInfo.packageName = "";
        }
        if (task.effectiveUid != uid) {
            taskInfo.baseActivity = new ComponentName("", "");
        }
    }

    public PictureInPictureParams getPictureInPictureParams() {
        Task topMostTask = getTopMostTask();
        if (topMostTask == null) {
            return null;
        }
        return getPictureInPictureParams(topMostTask.getTopMostActivity());
    }

    public static PictureInPictureParams getPictureInPictureParams(ActivityRecord activityRecord) {
        if (activityRecord == null || activityRecord.pictureInPictureArgs.empty()) {
            return null;
        }
        return new PictureInPictureParams(activityRecord.pictureInPictureArgs);
    }

    public Rect getDisplayCutoutInsets() {
        if (this.mDisplayContent == null || getDisplayInfo().displayCutout == null) {
            return null;
        }
        WindowState topVisibleAppMainWindow = getTopVisibleAppMainWindow();
        int i = topVisibleAppMainWindow == null ? 0 : topVisibleAppMainWindow.getAttrs().layoutInDisplayCutoutMode;
        if (i == 3 || i == 1) {
            return null;
        }
        return getDisplayInfo().displayCutout.getSafeInsets();
    }

    public ActivityManager.RunningTaskInfo getTaskInfo() {
        ActivityManager.RunningTaskInfo runningTaskInfo = new ActivityManager.RunningTaskInfo();
        fillTaskInfo(runningTaskInfo);
        return runningTaskInfo;
    }

    public StartingWindowInfo getStartingWindowInfo(ActivityRecord activityRecord) {
        WindowState topFullscreenOpaqueWindow;
        WindowState window;
        StartingWindowInfo startingWindowInfo = new StartingWindowInfo();
        ActivityManager.RunningTaskInfo taskInfo = getTaskInfo();
        startingWindowInfo.taskInfo = taskInfo;
        ActivityInfo activityInfo = taskInfo.topActivityInfo;
        startingWindowInfo.targetActivityInfo = (activityInfo == null || (r2 = activityRecord.info) == activityInfo) ? null : null;
        startingWindowInfo.isKeyguardOccluded = this.mAtmService.mKeyguardController.isDisplayOccluded(0);
        StartingData startingData = activityRecord.mStartingData;
        int i = startingData != null ? startingData.mTypeParams : 272;
        startingWindowInfo.startingWindowTypeParameter = i;
        if ((i & 16) != 0 && (window = getWindow(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda41
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getStartingWindowInfo$16;
                lambda$getStartingWindowInfo$16 = Task.lambda$getStartingWindowInfo$16((WindowState) obj);
                return lambda$getStartingWindowInfo$16;
            }
        })) != null) {
            startingWindowInfo.mainWindowLayoutParams = window.getAttrs();
            startingWindowInfo.requestedVisibleTypes = window.getRequestedVisibleTypes();
        }
        startingWindowInfo.taskInfo.configuration.setTo(activityRecord.getConfiguration());
        ActivityRecord topFullscreenActivity = getTopFullscreenActivity();
        if (topFullscreenActivity != null && (topFullscreenOpaqueWindow = topFullscreenActivity.getTopFullscreenOpaqueWindow()) != null) {
            startingWindowInfo.topOpaqueWindowInsetsState = topFullscreenOpaqueWindow.getInsetsStateWithVisibilityOverride();
            startingWindowInfo.topOpaqueWindowLayoutParams = topFullscreenOpaqueWindow.getAttrs();
        }
        return startingWindowInfo;
    }

    public static /* synthetic */ boolean lambda$getStartingWindowInfo$16(WindowState windowState) {
        return windowState.mAttrs.type == 1;
    }

    public TaskFragmentParentInfo getTaskFragmentParentInfo() {
        return new TaskFragmentParentInfo(getConfiguration(), getDisplayId(), shouldBeVisible(null));
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public boolean onChildVisibleRequestedChanged(WindowContainer windowContainer) {
        if (super.onChildVisibleRequestedChanged(windowContainer)) {
            sendTaskFragmentParentInfoChangedIfNeeded();
            return true;
        }
        return false;
    }

    public void sendTaskFragmentParentInfoChangedIfNeeded() {
        TaskFragment taskFragment;
        if (isLeafTask() && (taskFragment = getTaskFragment(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((TaskFragment) obj).isOrganizedTaskFragment();
            }
        })) != null) {
            taskFragment.sendTaskFragmentParentInfoChanged();
        }
    }

    public boolean isTaskId(int i) {
        return this.mTaskId == i;
    }

    public ActivityRecord isInTask(ActivityRecord activityRecord) {
        if (activityRecord != null && activityRecord.isDescendantOf(this)) {
            return activityRecord;
        }
        return null;
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("userId=");
        printWriter.print(this.mUserId);
        printWriter.print(" effectiveUid=");
        UserHandle.formatUid(printWriter, this.effectiveUid);
        printWriter.print(" mCallingUid=");
        UserHandle.formatUid(printWriter, this.mCallingUid);
        printWriter.print(" mUserSetupComplete=");
        printWriter.print(this.mUserSetupComplete);
        printWriter.print(" mCallingPackage=");
        printWriter.print(this.mCallingPackage);
        printWriter.print(" mCallingFeatureId=");
        printWriter.println(this.mCallingFeatureId);
        if (this.affinity != null || this.rootAffinity != null) {
            printWriter.print(str);
            printWriter.print("affinity=");
            printWriter.print(this.affinity);
            String str2 = this.affinity;
            if (str2 == null || !str2.equals(this.rootAffinity)) {
                printWriter.print(" root=");
                printWriter.println(this.rootAffinity);
            } else {
                printWriter.println();
            }
        }
        if (this.mWindowLayoutAffinity != null) {
            printWriter.print(str);
            printWriter.print("windowLayoutAffinity=");
            printWriter.println(this.mWindowLayoutAffinity);
        }
        if (this.voiceSession != null || this.voiceInteractor != null) {
            printWriter.print(str);
            printWriter.print("VOICE: session=0x");
            printWriter.print(Integer.toHexString(System.identityHashCode(this.voiceSession)));
            printWriter.print(" interactor=0x");
            printWriter.println(Integer.toHexString(System.identityHashCode(this.voiceInteractor)));
        }
        if (this.intent != null) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(str);
            sb.append("intent={");
            this.intent.toShortString(sb, false, true, false, false);
            sb.append('}');
            printWriter.println(sb.toString());
        }
        if (this.affinityIntent != null) {
            StringBuilder sb2 = new StringBuilder(128);
            sb2.append(str);
            sb2.append("affinityIntent={");
            this.affinityIntent.toShortString(sb2, false, true, false, false);
            sb2.append('}');
            printWriter.println(sb2.toString());
        }
        if (this.origActivity != null) {
            printWriter.print(str);
            printWriter.print("origActivity=");
            printWriter.println(this.origActivity.flattenToShortString());
        }
        if (this.realActivity != null) {
            printWriter.print(str);
            printWriter.print("mActivityComponent=");
            printWriter.println(this.realActivity.flattenToShortString());
        }
        if (this.autoRemoveRecents || this.isPersistable || !isActivityTypeStandard()) {
            printWriter.print(str);
            printWriter.print("autoRemoveRecents=");
            printWriter.print(this.autoRemoveRecents);
            printWriter.print(" isPersistable=");
            printWriter.print(this.isPersistable);
            printWriter.print(" activityType=");
            printWriter.println(getActivityType());
        }
        if (this.rootWasReset || this.mNeverRelinquishIdentity || this.mReuseTask || this.mLockTaskAuth != 1) {
            printWriter.print(str);
            printWriter.print("rootWasReset=");
            printWriter.print(this.rootWasReset);
            printWriter.print(" mNeverRelinquishIdentity=");
            printWriter.print(this.mNeverRelinquishIdentity);
            printWriter.print(" mReuseTask=");
            printWriter.print(this.mReuseTask);
            printWriter.print(" mLockTaskAuth=");
            printWriter.println(lockTaskAuthToString());
        }
        if (this.mAffiliatedTaskId != this.mTaskId || this.mPrevAffiliateTaskId != -1 || this.mPrevAffiliate != null || this.mNextAffiliateTaskId != -1 || this.mNextAffiliate != null) {
            printWriter.print(str);
            printWriter.print("affiliation=");
            printWriter.print(this.mAffiliatedTaskId);
            printWriter.print(" prevAffiliation=");
            printWriter.print(this.mPrevAffiliateTaskId);
            printWriter.print(" (");
            Task task = this.mPrevAffiliate;
            if (task == null) {
                printWriter.print("null");
            } else {
                printWriter.print(Integer.toHexString(System.identityHashCode(task)));
            }
            printWriter.print(") nextAffiliation=");
            printWriter.print(this.mNextAffiliateTaskId);
            printWriter.print(" (");
            Task task2 = this.mNextAffiliate;
            if (task2 == null) {
                printWriter.print("null");
            } else {
                printWriter.print(Integer.toHexString(System.identityHashCode(task2)));
            }
            printWriter.println(")");
        }
        printWriter.print(str);
        printWriter.print("Activities=");
        printWriter.println(this.mChildren);
        if (!this.askedCompatMode || !this.inRecents || !this.isAvailable) {
            printWriter.print(str);
            printWriter.print("askedCompatMode=");
            printWriter.print(this.askedCompatMode);
            printWriter.print(" inRecents=");
            printWriter.print(this.inRecents);
            printWriter.print(" isAvailable=");
            printWriter.println(this.isAvailable);
        }
        if (this.lastDescription != null) {
            printWriter.print(str);
            printWriter.print("lastDescription=");
            printWriter.println(this.lastDescription);
        }
        if (this.mRootProcess != null) {
            printWriter.print(str);
            printWriter.print("mRootProcess=");
            printWriter.println(this.mRootProcess);
        }
        if (this.mSharedStartingData != null) {
            printWriter.println(str + "mSharedStartingData=" + this.mSharedStartingData);
        }
        printWriter.print(str);
        printWriter.print("taskId=" + this.mTaskId);
        printWriter.println(" rootTaskId=" + getRootTaskId());
        printWriter.print(str);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("hasChildPipActivity=");
        sb3.append(this.mChildPipActivity != null);
        printWriter.println(sb3.toString());
        printWriter.print(str);
        printWriter.print("mHasBeenVisible=");
        printWriter.println(getHasBeenVisible());
        printWriter.print(str);
        printWriter.print("mResizeMode=");
        printWriter.print(ActivityInfo.resizeModeToString(this.mResizeMode));
        printWriter.print(" mSupportsPictureInPicture=");
        printWriter.print(this.mSupportsPictureInPicture);
        printWriter.print(" isResizeable=");
        printWriter.println(isResizeable());
        printWriter.print(str);
        printWriter.print("lastActiveTime=");
        printWriter.print(this.lastActiveTime);
        printWriter.println(" (inactive for " + (getInactiveDuration() / 1000) + "s)");
    }

    @Override // com.android.server.p014wm.TaskFragment
    public String toFullString() {
        StringBuilder sb = new StringBuilder((int) FrameworkStatsLog.f392xcd34d435);
        sb.append(this);
        sb.setLength(sb.length() - 1);
        sb.append(" U=");
        sb.append(this.mUserId);
        Task rootTask = getRootTask();
        if (rootTask != this) {
            sb.append(" rootTaskId=");
            sb.append(rootTask.mTaskId);
        }
        sb.append(" visible=");
        sb.append(shouldBeVisible(null));
        sb.append(" visibleRequested=");
        sb.append(isVisibleRequested());
        sb.append(" mode=");
        sb.append(WindowConfiguration.windowingModeToString(getWindowingMode()));
        sb.append(" translucent=");
        sb.append(isTranslucent(null));
        sb.append(" sz=");
        sb.append(getChildCount());
        sb.append('}');
        return sb.toString();
    }

    @Override // com.android.server.p014wm.TaskFragment
    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("Task{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" #");
        sb.append(this.mTaskId);
        sb.append(" type=" + WindowConfiguration.activityTypeToString(getActivityType()));
        if (this.affinity != null) {
            sb.append(" A=");
            sb.append(this.affinity);
        } else {
            Intent intent = this.intent;
            if (intent != null && intent.getComponent() != null) {
                sb.append(" I=");
                sb.append(this.intent.getComponent().flattenToShortString());
            } else {
                Intent intent2 = this.affinityIntent;
                if (intent2 != null && intent2.getComponent() != null) {
                    sb.append(" aI=");
                    sb.append(this.affinityIntent.getComponent().flattenToShortString());
                }
            }
        }
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }

    public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws Exception {
        typedXmlSerializer.attributeInt((String) null, "task_id", this.mTaskId);
        ComponentName componentName = this.realActivity;
        if (componentName != null) {
            typedXmlSerializer.attribute((String) null, "real_activity", componentName.flattenToShortString());
        }
        typedXmlSerializer.attributeBoolean((String) null, "real_activity_suspended", this.realActivitySuspended);
        ComponentName componentName2 = this.origActivity;
        if (componentName2 != null) {
            typedXmlSerializer.attribute((String) null, "orig_activity", componentName2.flattenToShortString());
        }
        String str = this.affinity;
        if (str != null) {
            typedXmlSerializer.attribute((String) null, "affinity", str);
            if (!this.affinity.equals(this.rootAffinity)) {
                String str2 = this.rootAffinity;
                typedXmlSerializer.attribute((String) null, "root_affinity", str2 != null ? str2 : "@");
            }
        } else {
            String str3 = this.rootAffinity;
            if (str3 != null) {
                typedXmlSerializer.attribute((String) null, "root_affinity", str3 != null ? str3 : "@");
            }
        }
        String str4 = this.mWindowLayoutAffinity;
        if (str4 != null) {
            typedXmlSerializer.attribute((String) null, "window_layout_affinity", str4);
        }
        typedXmlSerializer.attributeBoolean((String) null, "root_has_reset", this.rootWasReset);
        typedXmlSerializer.attributeBoolean((String) null, "auto_remove_recents", this.autoRemoveRecents);
        typedXmlSerializer.attributeBoolean((String) null, "asked_compat_mode", this.askedCompatMode);
        typedXmlSerializer.attributeInt((String) null, "user_id", this.mUserId);
        typedXmlSerializer.attributeBoolean((String) null, "user_setup_complete", this.mUserSetupComplete);
        typedXmlSerializer.attributeInt((String) null, "effective_uid", this.effectiveUid);
        typedXmlSerializer.attributeLong((String) null, "last_time_moved", this.mLastTimeMoved);
        typedXmlSerializer.attributeBoolean((String) null, "never_relinquish_identity", this.mNeverRelinquishIdentity);
        CharSequence charSequence = this.lastDescription;
        if (charSequence != null) {
            typedXmlSerializer.attribute((String) null, "last_description", charSequence.toString());
        }
        if (getTaskDescription() != null) {
            getTaskDescription().saveToXml(typedXmlSerializer);
        }
        typedXmlSerializer.attributeInt((String) null, "task_affiliation", this.mAffiliatedTaskId);
        typedXmlSerializer.attributeInt((String) null, "prev_affiliation", this.mPrevAffiliateTaskId);
        typedXmlSerializer.attributeInt((String) null, "next_affiliation", this.mNextAffiliateTaskId);
        typedXmlSerializer.attributeInt((String) null, "calling_uid", this.mCallingUid);
        String str5 = this.mCallingPackage;
        if (str5 == null) {
            str5 = "";
        }
        typedXmlSerializer.attribute((String) null, "calling_package", str5);
        String str6 = this.mCallingFeatureId;
        typedXmlSerializer.attribute((String) null, "calling_feature_id", str6 != null ? str6 : "");
        typedXmlSerializer.attributeInt((String) null, "resize_mode", this.mResizeMode);
        typedXmlSerializer.attributeBoolean((String) null, "supports_picture_in_picture", this.mSupportsPictureInPicture);
        Rect rect = this.mLastNonFullscreenBounds;
        if (rect != null) {
            typedXmlSerializer.attribute((String) null, "non_fullscreen_bounds", rect.flattenToString());
        }
        typedXmlSerializer.attributeInt((String) null, "min_width", this.mMinWidth);
        typedXmlSerializer.attributeInt((String) null, "min_height", this.mMinHeight);
        typedXmlSerializer.attributeInt((String) null, "persist_task_version", 1);
        Point point = this.mLastTaskSnapshotData.taskSize;
        if (point != null) {
            typedXmlSerializer.attribute((String) null, "last_snapshot_task_size", point.flattenToString());
        }
        Rect rect2 = this.mLastTaskSnapshotData.contentInsets;
        if (rect2 != null) {
            typedXmlSerializer.attribute((String) null, "last_snapshot_content_insets", rect2.flattenToString());
        }
        Point point2 = this.mLastTaskSnapshotData.bufferSize;
        if (point2 != null) {
            typedXmlSerializer.attribute((String) null, "last_snapshot_buffer_size", point2.flattenToString());
        }
        if (this.affinityIntent != null) {
            typedXmlSerializer.startTag((String) null, "affinity_intent");
            this.affinityIntent.saveToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "affinity_intent");
        }
        if (this.intent != null) {
            typedXmlSerializer.startTag((String) null, "intent");
            this.intent.saveToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "intent");
        }
        sTmpException = null;
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new TriPredicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda44
            public final boolean test(Object obj, Object obj2, Object obj3) {
                boolean saveActivityToXml;
                saveActivityToXml = Task.saveActivityToXml((ActivityRecord) obj, (ActivityRecord) obj2, (TypedXmlSerializer) obj3);
                return saveActivityToXml;
            }
        }, PooledLambda.__(ActivityRecord.class), getBottomMostActivity(), typedXmlSerializer);
        forAllActivities((Predicate<ActivityRecord>) obtainPredicate);
        obtainPredicate.recycle();
        Exception exc = sTmpException;
        if (exc != null) {
            throw exc;
        }
    }

    public static boolean saveActivityToXml(ActivityRecord activityRecord, ActivityRecord activityRecord2, TypedXmlSerializer typedXmlSerializer) {
        if (activityRecord.info.persistableMode != 0 && activityRecord.isPersistable() && (((activityRecord.intent.getFlags() & 524288) | IInstalld.FLAG_FORCE) != 524288 || activityRecord == activityRecord2)) {
            try {
                typedXmlSerializer.startTag((String) null, "activity");
                activityRecord.saveToXml(typedXmlSerializer);
                typedXmlSerializer.endTag((String) null, "activity");
                return false;
            } catch (Exception e) {
                sTmpException = e;
            }
        }
        return true;
    }

    public static Task restoreFromXml(TypedXmlPullParser typedXmlPullParser, ActivityTaskSupervisor activityTaskSupervisor) throws IOException, XmlPullParserException {
        ArrayList arrayList;
        int i;
        int i2;
        int i3;
        boolean z;
        ApplicationInfo applicationInfo;
        String str;
        char c;
        long j;
        ArrayList arrayList2 = new ArrayList();
        int depth = typedXmlPullParser.getDepth();
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription();
        ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData persistedTaskSnapshotData = new ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData();
        long j2 = 0;
        String str2 = "";
        boolean z2 = true;
        boolean z3 = true;
        String str3 = null;
        ComponentName componentName = null;
        String str4 = null;
        int i4 = -1;
        int i5 = -1;
        boolean z4 = false;
        boolean z5 = false;
        ComponentName componentName2 = null;
        String str5 = null;
        boolean z6 = false;
        boolean z7 = false;
        boolean z8 = false;
        int i6 = 0;
        int i7 = 0;
        String str6 = null;
        int i8 = -1;
        int i9 = -1;
        int i10 = -1;
        int i11 = -1;
        String str7 = null;
        int i12 = 4;
        boolean z9 = false;
        Rect rect = null;
        int i13 = -1;
        int i14 = -1;
        int i15 = 0;
        for (int attributeCount = typedXmlPullParser.getAttributeCount() - 1; attributeCount >= 0; attributeCount--) {
            String attributeName = typedXmlPullParser.getAttributeName(attributeCount);
            String attributeValue = typedXmlPullParser.getAttributeValue(attributeCount);
            attributeName.hashCode();
            switch (attributeName.hashCode()) {
                case -2134816935:
                    str = str2;
                    if (attributeName.equals("asked_compat_mode")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1588736338:
                    str = str2;
                    if (attributeName.equals("last_snapshot_content_insets")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -1556983798:
                    str = str2;
                    if (attributeName.equals("last_time_moved")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -1537240555:
                    str = str2;
                    if (attributeName.equals("task_id")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -1494902876:
                    str = str2;
                    if (attributeName.equals("next_affiliation")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case -1138503444:
                    str = str2;
                    if (attributeName.equals("real_activity_suspended")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -1124927690:
                    str = str2;
                    if (attributeName.equals("task_affiliation")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case -974080081:
                    str = str2;
                    if (attributeName.equals("user_setup_complete")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case -929566280:
                    str = str2;
                    if (attributeName.equals("effective_uid")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case -865458610:
                    str = str2;
                    if (attributeName.equals("resize_mode")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case -826243148:
                    str = str2;
                    if (attributeName.equals("min_height")) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                case -801863159:
                    str = str2;
                    if (attributeName.equals("last_snapshot_task_size")) {
                        c = 11;
                        break;
                    }
                    c = 65535;
                    break;
                case -707249465:
                    str = str2;
                    if (attributeName.equals("non_fullscreen_bounds")) {
                        c = '\f';
                        break;
                    }
                    c = 65535;
                    break;
                case -705269939:
                    str = str2;
                    if (attributeName.equals("orig_activity")) {
                        c = '\r';
                        break;
                    }
                    c = 65535;
                    break;
                case -551322450:
                    str = str2;
                    if (attributeName.equals("last_snapshot_buffer_size")) {
                        c = 14;
                        break;
                    }
                    c = 65535;
                    break;
                case -502399667:
                    str = str2;
                    if (attributeName.equals("auto_remove_recents")) {
                        c = 15;
                        break;
                    }
                    c = 65535;
                    break;
                case -360792224:
                    str = str2;
                    if (attributeName.equals("supports_picture_in_picture")) {
                        c = 16;
                        break;
                    }
                    c = 65535;
                    break;
                case -162744347:
                    str = str2;
                    if (attributeName.equals("root_affinity")) {
                        c = 17;
                        break;
                    }
                    c = 65535;
                    break;
                case -147132913:
                    str = str2;
                    if (attributeName.equals("user_id")) {
                        c = 18;
                        break;
                    }
                    c = 65535;
                    break;
                case -132216235:
                    str = str2;
                    if (attributeName.equals("calling_uid")) {
                        c = 19;
                        break;
                    }
                    c = 65535;
                    break;
                case 180927924:
                    str = str2;
                    if (attributeName.equals("task_type")) {
                        c = 20;
                        break;
                    }
                    c = 65535;
                    break;
                case 331206372:
                    str = str2;
                    if (attributeName.equals("prev_affiliation")) {
                        c = 21;
                        break;
                    }
                    c = 65535;
                    break;
                case 394454367:
                    str = str2;
                    if (attributeName.equals("calling_feature_id")) {
                        c = 22;
                        break;
                    }
                    c = 65535;
                    break;
                case 541503897:
                    str = str2;
                    if (attributeName.equals("min_width")) {
                        c = 23;
                        break;
                    }
                    c = 65535;
                    break;
                case 605497640:
                    str = str2;
                    if (attributeName.equals("affinity")) {
                        c = 24;
                        break;
                    }
                    c = 65535;
                    break;
                case 869221331:
                    str = str2;
                    if (attributeName.equals("last_description")) {
                        c = 25;
                        break;
                    }
                    c = 65535;
                    break;
                case 1007873193:
                    str = str2;
                    if (attributeName.equals("persist_task_version")) {
                        c = 26;
                        break;
                    }
                    c = 65535;
                    break;
                case 1081438155:
                    str = str2;
                    if (attributeName.equals("calling_package")) {
                        c = 27;
                        break;
                    }
                    c = 65535;
                    break;
                case 1457608782:
                    str = str2;
                    if (attributeName.equals("never_relinquish_identity")) {
                        c = 28;
                        break;
                    }
                    c = 65535;
                    break;
                case 1539554448:
                    str = str2;
                    if (attributeName.equals("real_activity")) {
                        c = 29;
                        break;
                    }
                    c = 65535;
                    break;
                case 1999609934:
                    str = str2;
                    if (attributeName.equals("window_layout_affinity")) {
                        c = 30;
                        break;
                    }
                    c = 65535;
                    break;
                case 2023391309:
                    str = str2;
                    if (attributeName.equals("root_has_reset")) {
                        c = 31;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    str = str2;
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    z8 = Boolean.parseBoolean(attributeValue);
                    str2 = str;
                    break;
                case 1:
                    j = j2;
                    persistedTaskSnapshotData.contentInsets = Rect.unflattenFromString(attributeValue);
                    str2 = str;
                    j2 = j;
                    break;
                case 2:
                    j2 = Long.parseLong(attributeValue);
                    str2 = str;
                    break;
                case 3:
                    j = j2;
                    if (i4 == -1) {
                        i4 = Integer.parseInt(attributeValue);
                    }
                    str2 = str;
                    j2 = j;
                    break;
                case 4:
                    i10 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case 5:
                    z5 = Boolean.valueOf(attributeValue).booleanValue();
                    str2 = str;
                    break;
                case 6:
                    i8 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case 7:
                    z2 = Boolean.parseBoolean(attributeValue);
                    str2 = str;
                    break;
                case '\b':
                    i5 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case '\t':
                    i12 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case '\n':
                    i14 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case 11:
                    j = j2;
                    persistedTaskSnapshotData.taskSize = Point.unflattenFromString(attributeValue);
                    str2 = str;
                    j2 = j;
                    break;
                case '\f':
                    rect = Rect.unflattenFromString(attributeValue);
                    str2 = str;
                    break;
                case '\r':
                    componentName2 = ComponentName.unflattenFromString(attributeValue);
                    str2 = str;
                    break;
                case 14:
                    j = j2;
                    persistedTaskSnapshotData.bufferSize = Point.unflattenFromString(attributeValue);
                    str2 = str;
                    j2 = j;
                    break;
                case 15:
                    z7 = Boolean.parseBoolean(attributeValue);
                    str2 = str;
                    break;
                case 16:
                    z9 = Boolean.parseBoolean(attributeValue);
                    str2 = str;
                    break;
                case 17:
                    str4 = attributeValue;
                    str2 = str;
                    z4 = true;
                    break;
                case 18:
                    i7 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case 19:
                    i11 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case 20:
                    i6 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case 21:
                    i9 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case 22:
                    str7 = attributeValue;
                    str2 = str;
                    break;
                case 23:
                    i13 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case 24:
                    str3 = attributeValue;
                    str2 = str;
                    break;
                case 25:
                    str6 = attributeValue;
                    str2 = str;
                    break;
                case 26:
                    i15 = Integer.parseInt(attributeValue);
                    str2 = str;
                    break;
                case 27:
                    str2 = attributeValue;
                    break;
                case 28:
                    z3 = Boolean.parseBoolean(attributeValue);
                    str2 = str;
                    break;
                case 29:
                    componentName = ComponentName.unflattenFromString(attributeValue);
                    str2 = str;
                    break;
                case 30:
                    str5 = attributeValue;
                    str2 = str;
                    break;
                case 31:
                    z6 = Boolean.parseBoolean(attributeValue);
                    str2 = str;
                    break;
                default:
                    if (attributeName.startsWith("task_description_")) {
                        j = j2;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        j = j2;
                        sb.append("Task: Unknown attribute=");
                        sb.append(attributeName);
                        Slog.w("ActivityTaskManager", sb.toString());
                    }
                    str2 = str;
                    j2 = j;
                    break;
            }
        }
        long j3 = j2;
        String str8 = str2;
        taskDescription.restoreFromXml(typedXmlPullParser);
        Intent intent = null;
        Intent intent2 = null;
        while (true) {
            int next = typedXmlPullParser.next();
            if (next != 1 && (next != 3 || typedXmlPullParser.getDepth() >= depth)) {
                if (next == 2) {
                    String name = typedXmlPullParser.getName();
                    if ("affinity_intent".equals(name)) {
                        intent2 = Intent.restoreFromXml(typedXmlPullParser);
                    } else if ("intent".equals(name)) {
                        intent = Intent.restoreFromXml(typedXmlPullParser);
                    } else if ("activity".equals(name)) {
                        ActivityRecord restoreFromXml = ActivityRecord.restoreFromXml(typedXmlPullParser, activityTaskSupervisor);
                        if (restoreFromXml != null) {
                            arrayList2.add(restoreFromXml);
                        }
                    } else {
                        Slog.e("ActivityTaskManager", "restoreTask: Unexpected name=" + name);
                        XmlUtils.skipCurrentTag(typedXmlPullParser);
                    }
                }
            }
        }
        if (!z4) {
            str4 = str3;
        } else if ("@".equals(str4)) {
            str4 = null;
        }
        if (i5 <= 0) {
            Intent intent3 = intent != null ? intent : intent2;
            if (intent3 != null) {
                try {
                    arrayList = arrayList2;
                    i = i7;
                    try {
                        applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(intent3.getComponent().getPackageName(), 8704L, i);
                    } catch (RemoteException unused) {
                    }
                } catch (RemoteException unused2) {
                }
                if (applicationInfo != null) {
                    i2 = applicationInfo.uid;
                    Slog.w("ActivityTaskManager", "Updating task #" + i4 + " for " + intent3 + ": effectiveUid=" + i2);
                }
                i2 = 0;
                Slog.w("ActivityTaskManager", "Updating task #" + i4 + " for " + intent3 + ": effectiveUid=" + i2);
            }
            arrayList = arrayList2;
            i = i7;
            i2 = 0;
            Slog.w("ActivityTaskManager", "Updating task #" + i4 + " for " + intent3 + ": effectiveUid=" + i2);
        } else {
            arrayList = arrayList2;
            i = i7;
            i2 = i5;
        }
        if (i15 < 1) {
            if (i6 == 1) {
                i3 = i12;
                if (i3 == 2) {
                    z = z9;
                    i3 = 1;
                }
            } else {
                i3 = i12;
            }
            z = z9;
        } else {
            i3 = i12;
            if (i3 == 3) {
                i3 = 2;
                z = true;
            }
            z = z9;
        }
        Task buildInner = new Builder(activityTaskSupervisor.mService).setTaskId(i4).setIntent(intent).setAffinityIntent(intent2).setAffinity(str3).setRootAffinity(str4).setRealActivity(componentName).setOrigActivity(componentName2).setRootWasReset(z6).setAutoRemoveRecents(z7).setAskedCompatMode(z8).setUserId(i).setEffectiveUid(i2).setLastDescription(str6).setLastTimeMoved(j3).setNeverRelinquishIdentity(z3).setLastTaskDescription(taskDescription).setLastSnapshotData(persistedTaskSnapshotData).setTaskAffiliation(i8).setPrevAffiliateTaskId(i9).setNextAffiliateTaskId(i10).setCallingUid(i11).setCallingPackage(str8).setCallingFeatureId(str7).setResizeMode(i3).setSupportsPictureInPicture(z).setRealActivitySuspended(z5).setUserSetupComplete(z2).setMinWidth(i13).setMinHeight(i14).buildInner();
        Rect rect2 = rect;
        buildInner.mLastNonFullscreenBounds = rect2;
        buildInner.setBounds(rect2);
        buildInner.mWindowLayoutAffinity = str5;
        if (arrayList.size() > 0) {
            activityTaskSupervisor.mRootWindowContainer.getDisplayContent(0).getDefaultTaskDisplayArea().addChild(buildInner, Integer.MIN_VALUE);
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                buildInner.addChild((ActivityRecord) arrayList.get(size));
            }
        }
        return buildInner;
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public boolean isOrganized() {
        return this.mTaskOrganizer != null;
    }

    public final boolean canBeOrganized() {
        if (isRootTask() || this.mCreatedByOrganizer) {
            return true;
        }
        Task asTask = getParent().asTask();
        return asTask != null && asTask.mCreatedByOrganizer;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void reparentSurfaceControl(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        if (isOrganized() && isAlwaysOnTop()) {
            return;
        }
        super.reparentSurfaceControl(transaction, surfaceControl);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1, types: [com.android.server.wm.WindowContainer] */
    /* JADX WARN: Type inference failed for: r1v3 */
    public void setHasBeenVisible(boolean z) {
        Task asTask;
        this.mHasBeenVisible = z;
        if (!z || this.mDeferTaskAppear) {
            return;
        }
        sendTaskAppeared();
        ?? r1 = this;
        while (true) {
            WindowContainer parent = r1.getParent();
            if (parent == null || (asTask = parent.asTask()) == null) {
                return;
            }
            asTask.setHasBeenVisible(true);
            r1 = parent;
        }
    }

    public boolean getHasBeenVisible() {
        return this.mHasBeenVisible;
    }

    public void setDeferTaskAppear(boolean z) {
        boolean z2 = this.mDeferTaskAppear;
        this.mDeferTaskAppear = z;
        if (!z2 || z) {
            return;
        }
        sendTaskAppeared();
    }

    public boolean taskAppearedReady() {
        if (this.mTaskOrganizer == null || this.mDeferTaskAppear) {
            return false;
        }
        if (this.mCreatedByOrganizer) {
            return true;
        }
        return this.mSurfaceControl != null && getHasBeenVisible();
    }

    public final void sendTaskAppeared() {
        ITaskOrganizer iTaskOrganizer = this.mTaskOrganizer;
        if (iTaskOrganizer != null) {
            this.mAtmService.mTaskOrganizerController.onTaskAppeared(iTaskOrganizer, this);
        }
    }

    public final void sendTaskVanished(ITaskOrganizer iTaskOrganizer) {
        if (iTaskOrganizer != null) {
            this.mAtmService.mTaskOrganizerController.onTaskVanished(iTaskOrganizer, this);
        }
    }

    @VisibleForTesting
    public boolean setTaskOrganizer(ITaskOrganizer iTaskOrganizer) {
        return setTaskOrganizer(iTaskOrganizer, false);
    }

    @VisibleForTesting
    public boolean setTaskOrganizer(ITaskOrganizer iTaskOrganizer, boolean z) {
        ITaskOrganizer iTaskOrganizer2 = this.mTaskOrganizer;
        if (iTaskOrganizer2 == iTaskOrganizer) {
            return false;
        }
        this.mTaskOrganizer = iTaskOrganizer;
        sendTaskVanished(iTaskOrganizer2);
        if (this.mTaskOrganizer != null) {
            if (z) {
                return true;
            }
            sendTaskAppeared();
            return true;
        }
        TaskDisplayArea displayArea = getDisplayArea();
        if (displayArea != null) {
            displayArea.removeLaunchRootTask(this);
        }
        setForceHidden(2, false);
        if (this.mCreatedByOrganizer) {
            removeImmediately("setTaskOrganizer");
            return true;
        }
        return true;
    }

    public boolean updateTaskOrganizerState() {
        return updateTaskOrganizerState(false);
    }

    public boolean updateTaskOrganizerState(boolean z) {
        ITaskOrganizer iTaskOrganizer;
        if (getSurfaceControl() == null) {
            return false;
        }
        if (!canBeOrganized()) {
            return setTaskOrganizer(null);
        }
        ITaskOrganizer taskOrganizer = this.mWmService.mAtmService.mTaskOrganizerController.getTaskOrganizer();
        if (!this.mCreatedByOrganizer || (iTaskOrganizer = this.mTaskOrganizer) == null || taskOrganizer == null || iTaskOrganizer == taskOrganizer) {
            return setTaskOrganizer(taskOrganizer, z);
        }
        return false;
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public void setSurfaceControl(SurfaceControl surfaceControl) {
        super.setSurfaceControl(surfaceControl);
        sendTaskAppeared();
    }

    public boolean isFocused() {
        ActivityRecord activityRecord;
        DisplayContent displayContent = this.mDisplayContent;
        if (displayContent == null || (activityRecord = displayContent.mFocusedApp) == null) {
            return false;
        }
        Task task = activityRecord.getTask();
        return task == this || (task != null && task.getParent() == this);
    }

    public final boolean hasVisibleChildren() {
        return (!isAttached() || isForceHidden() || getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda20
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((ActivityRecord) obj).isVisible();
            }
        }) == null) ? false : true;
    }

    public void onAppFocusChanged(boolean z) {
        dispatchTaskInfoChangedIfNeeded(false);
        Task asTask = getParent().asTask();
        if (asTask != null) {
            asTask.dispatchTaskInfoChangedIfNeeded(false);
        }
        this.mAtmService.getTaskChangeNotificationController().notifyTaskFocusChanged(this.mTaskId, z);
    }

    public void onPictureInPictureParamsChanged() {
        if (inPinnedWindowingMode()) {
            dispatchTaskInfoChangedIfNeeded(true);
        }
    }

    public void onShouldDockBigOverlaysChanged() {
        dispatchTaskInfoChangedIfNeeded(true);
    }

    public void onSizeCompatActivityChanged() {
        dispatchTaskInfoChangedIfNeeded(true);
    }

    public void setMainWindowSizeChangeTransaction(SurfaceControl.Transaction transaction) {
        setMainWindowSizeChangeTransaction(transaction, this);
        forAllWindows(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda32
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WindowState) obj).requestRedrawForSync();
            }
        }, true);
    }

    public final void setMainWindowSizeChangeTransaction(final SurfaceControl.Transaction transaction, Task task) {
        ActivityRecord topNonFinishingActivity = getTopNonFinishingActivity();
        Task task2 = topNonFinishingActivity != null ? topNonFinishingActivity.getTask() : null;
        if (task2 == null) {
            return;
        }
        if (task2 != this) {
            task2.setMainWindowSizeChangeTransaction(transaction, task);
            return;
        }
        WindowState topVisibleAppMainWindow = getTopVisibleAppMainWindow();
        if (topVisibleAppMainWindow != null) {
            topVisibleAppMainWindow.applyWithNextDraw(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda42
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SurfaceControl.Transaction) obj).merge(transaction);
                }
            });
        } else {
            transaction.apply();
        }
    }

    public boolean setForceHidden(int i, boolean z) {
        int i2 = this.mForceHiddenFlags;
        int i3 = z ? i | i2 : (~i) & i2;
        if (i2 == i3) {
            return false;
        }
        boolean isForceHidden = isForceHidden();
        boolean isVisible = isVisible();
        this.mForceHiddenFlags = i3;
        boolean isForceHidden2 = isForceHidden();
        if (isForceHidden != isForceHidden2) {
            if (isVisible && isForceHidden2) {
                moveToBack("setForceHidden", null);
                return true;
            } else if (isAlwaysOnTop()) {
                moveToFront("setForceHidden");
                return true;
            } else {
                return true;
            }
        }
        return true;
    }

    public void setForceTranslucent(boolean z) {
        this.mForceTranslucent = z;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public boolean isAlwaysOnTop() {
        return !isForceHidden() && super.isAlwaysOnTop();
    }

    public boolean isAlwaysOnTopWhenVisible() {
        return super.isAlwaysOnTop();
    }

    @Override // com.android.server.p014wm.TaskFragment
    public boolean isForceHidden() {
        return this.mForceHiddenFlags != 0;
    }

    @Override // com.android.server.p014wm.TaskFragment
    public boolean isForceTranslucent() {
        return this.mForceTranslucent;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public void setWindowingMode(int i) {
        if (!isRootTask()) {
            super.setWindowingMode(i);
        } else {
            setWindowingMode(i, false);
        }
    }

    public void setWindowingMode(final int i, final boolean z) {
        this.mWmService.inSurfaceTransaction(new Runnable() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                Task.this.lambda$setWindowingMode$18(i, z);
            }
        });
    }

    /* renamed from: setWindowingModeInSurfaceTransaction */
    public final void lambda$setWindowingMode$18(int i, boolean z) {
        int i2;
        TaskDisplayArea displayArea = getDisplayArea();
        if (displayArea == null) {
            Slog.d("ActivityTaskManager", "taskDisplayArea is null, bail early");
            return;
        }
        int windowingMode = getWindowingMode();
        Task topMostTask = getTopMostTask();
        if (!z && !displayArea.isValidWindowingMode(i, null, topMostTask)) {
            i = 0;
        }
        if (windowingMode == i) {
            getRequestedOverrideConfiguration().windowConfiguration.setWindowingMode(i);
            return;
        }
        ActivityRecord topNonFinishingActivity = getTopNonFinishingActivity();
        if (i == 0) {
            WindowContainer parent = getParent();
            i2 = parent != null ? parent.getWindowingMode() : 1;
        } else {
            i2 = i;
        }
        if (windowingMode == 2) {
            setCanAffectSystemUiFlags(true);
            this.mTaskSupervisor.mUserLeaving = true;
            Task topDisplayFocusedRootTask = this.mRootWindowContainer.getTopDisplayFocusedRootTask();
            if (topDisplayFocusedRootTask != null) {
                ActivityRecord topResumedActivity = topDisplayFocusedRootTask.getTopResumedActivity();
                enableEnterPipOnTaskSwitch(topResumedActivity, null, topResumedActivity, null);
            }
            this.mRootWindowContainer.notifyActivityPipModeChanged(this, null);
        }
        if (i2 == 2 && displayArea.getRootPinnedTask() != null) {
            displayArea.getRootPinnedTask().dismissPip();
        }
        if (i2 != 1 && topNonFinishingActivity != 0 && !topNonFinishingActivity.noDisplay && topNonFinishingActivity.canForceResizeNonResizable(i2)) {
            this.mAtmService.getTaskChangeNotificationController().notifyActivityForcedResizable(topMostTask.mTaskId, 1, topNonFinishingActivity.info.applicationInfo.packageName);
        }
        this.mAtmService.deferWindowLayout();
        if (topNonFinishingActivity != null) {
            try {
                this.mTaskSupervisor.mNoAnimActivities.add(topNonFinishingActivity);
            } finally {
                this.mAtmService.continueWindowLayout();
            }
        }
        super.setWindowingMode(i);
        if (windowingMode == 2 && topNonFinishingActivity != null) {
            if (topNonFinishingActivity.getLastParentBeforePip() != null && !isForceHidden()) {
                Task lastParentBeforePip = topNonFinishingActivity.getLastParentBeforePip();
                if (lastParentBeforePip.isAttached()) {
                    topNonFinishingActivity.reparent(lastParentBeforePip, lastParentBeforePip.getChildCount(), "movePinnedActivityToOriginalTask");
                    lastParentBeforePip.moveToFront("movePinnedActivityToOriginalTask");
                }
            }
            if (topNonFinishingActivity.shouldBeVisible()) {
                this.mAtmService.resumeAppSwitches();
            }
        }
        if (z) {
            return;
        }
        if (topNonFinishingActivity != null && windowingMode == 1 && i == 2 && !this.mTransitionController.isShellTransitionsEnabled()) {
            this.mDisplayContent.mPinnedTaskController.deferOrientationChangeForEnteringPipFromFullScreenIfNeeded();
        }
        this.mAtmService.continueWindowLayout();
        if (this.mTaskSupervisor.isRootVisibilityUpdateDeferred()) {
            return;
        }
        this.mRootWindowContainer.ensureActivitiesVisible(null, 0, true);
        this.mRootWindowContainer.resumeFocusedTasksTopActivities();
    }

    public void abortPipEnter(ActivityRecord activityRecord) {
        if (inPinnedWindowingMode() && !activityRecord.inPinnedWindowingMode() && canMoveTaskToBack(this)) {
            Transition transition = new Transition(4, 0, this.mTransitionController, this.mWmService.mSyncEngine);
            this.mTransitionController.moveToCollecting(transition);
            this.mTransitionController.requestStartTransition(transition, this, null, null);
            if (activityRecord.getLastParentBeforePip() != null) {
                Task lastParentBeforePip = activityRecord.getLastParentBeforePip();
                if (lastParentBeforePip.isAttached()) {
                    activityRecord.reparent(lastParentBeforePip, lastParentBeforePip.getChildCount(), "movePinnedActivityToOriginalTask");
                }
            }
            if (isAttached()) {
                setWindowingMode(0);
                moveTaskToBackInner(this);
            }
            if (activityRecord.isAttached()) {
                activityRecord.setWindowingMode(0);
            }
        }
    }

    public void resumeNextFocusAfterReparent() {
        adjustFocusToNextFocusableTask("reparent", true, true);
        this.mRootWindowContainer.resumeFocusedTasksTopActivities();
        this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
    }

    public final boolean isOnHomeDisplay() {
        return getDisplayId() == 0;
    }

    public void moveToFront(String str) {
        moveToFront(str, null);
    }

    @VisibleForTesting
    public void moveToFront(String str, Task task) {
        if (isAttached()) {
            TaskDisplayArea displayArea = getDisplayArea();
            if (!isActivityTypeHome() && returnsToHomeRootTask()) {
                displayArea.moveHomeRootTaskToFront(str + " returnToHome");
            }
            Task focusedRootTask = isRootTask() ? displayArea.getFocusedRootTask() : null;
            if (task != null) {
                this = task;
            }
            this.getParent().positionChildAt(Integer.MAX_VALUE, this, true);
            displayArea.updateLastFocusedRootTask(focusedRootTask, str);
        }
    }

    public void moveToBack(String str, Task task) {
        if (isAttached()) {
            TaskDisplayArea displayArea = getDisplayArea();
            if (this.mCreatedByOrganizer) {
                if (task == null || task == this) {
                    return;
                }
                displayArea.positionTaskBehindHome(task);
                return;
            }
            WindowContainer parent = getParent();
            Task asTask = parent != null ? parent.asTask() : null;
            if (asTask != null) {
                asTask.moveToBack(str, this);
            } else {
                Task focusedRootTask = displayArea.getFocusedRootTask();
                displayArea.positionChildAt(Integer.MIN_VALUE, this, false);
                displayArea.updateLastFocusedRootTask(focusedRootTask, str);
            }
            if (task == null || task == this) {
                return;
            }
            positionChildAtBottom(task);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void switchUser(int i) {
        if (this.mCurrentUser == i) {
            return;
        }
        this.mCurrentUser = i;
        super.switchUser(i);
        if (isRootTask() || !showToCurrentUser()) {
            return;
        }
        getParent().positionChildAt(Integer.MAX_VALUE, this, false);
    }

    public void minimalResumeActivityLocked(ActivityRecord activityRecord) {
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1164930508, 0, (String) null, new Object[]{String.valueOf(activityRecord), String.valueOf(Debug.getCallers(5))});
        }
        activityRecord.setState(ActivityRecord.State.RESUMED, "minimalResumeActivityLocked");
        activityRecord.completeResumeLocked();
    }

    public void checkReadyForSleep() {
        if (shouldSleepActivities() && goToSleepIfPossible(false)) {
            this.mTaskSupervisor.checkReadyForSleepLocked(true);
        }
    }

    public boolean goToSleepIfPossible(final boolean z) {
        final int[] iArr = {0};
        forAllLeafTasksAndLeafTaskFragments(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Task.lambda$goToSleepIfPossible$19(z, iArr, (TaskFragment) obj);
            }
        }, true);
        return iArr[0] == 0;
    }

    public static /* synthetic */ void lambda$goToSleepIfPossible$19(boolean z, int[] iArr, TaskFragment taskFragment) {
        if (taskFragment.sleepIfPossible(z)) {
            return;
        }
        iArr[0] = iArr[0] + 1;
    }

    public boolean isTopRootTaskInDisplayArea() {
        TaskDisplayArea displayArea = getDisplayArea();
        return displayArea != null && displayArea.isTopRootTask(this);
    }

    public boolean isFocusedRootTaskOnDisplay() {
        DisplayContent displayContent = this.mDisplayContent;
        return displayContent != null && this == displayContent.getFocusedRootTask();
    }

    public void ensureActivitiesVisible(ActivityRecord activityRecord, int i, boolean z) {
        ensureActivitiesVisible(activityRecord, i, z, true);
    }

    public void ensureActivitiesVisible(final ActivityRecord activityRecord, final int i, final boolean z, final boolean z2) {
        this.mTaskSupervisor.beginActivityVisibilityUpdate();
        try {
            forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda16
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((Task) obj).updateActivityVisibilities(ActivityRecord.this, i, z, z2);
                }
            }, true);
            if (this.mTranslucentActivityWaiting != null && this.mUndrawnActivitiesBelowTopTranslucent.isEmpty()) {
                notifyActivityDrawnLocked(null);
            }
        } finally {
            this.mTaskSupervisor.endActivityVisibilityUpdate();
        }
    }

    public void checkTranslucentActivityWaiting(ActivityRecord activityRecord) {
        if (this.mTranslucentActivityWaiting != activityRecord) {
            this.mUndrawnActivitiesBelowTopTranslucent.clear();
            if (this.mTranslucentActivityWaiting != null) {
                notifyActivityDrawnLocked(null);
                this.mTranslucentActivityWaiting = null;
            }
            this.mHandler.removeMessages(101);
        }
    }

    public void convertActivityToTranslucent(ActivityRecord activityRecord) {
        this.mTranslucentActivityWaiting = activityRecord;
        this.mUndrawnActivitiesBelowTopTranslucent.clear();
        this.mHandler.sendEmptyMessageDelayed(101, 2000L);
    }

    public void notifyActivityDrawnLocked(ActivityRecord activityRecord) {
        if (activityRecord == null || (this.mUndrawnActivitiesBelowTopTranslucent.remove(activityRecord) && this.mUndrawnActivitiesBelowTopTranslucent.isEmpty())) {
            ActivityRecord activityRecord2 = this.mTranslucentActivityWaiting;
            this.mTranslucentActivityWaiting = null;
            this.mUndrawnActivitiesBelowTopTranslucent.clear();
            this.mHandler.removeMessages(101);
            if (activityRecord2 != null) {
                this.mWmService.setWindowOpaqueLocked(activityRecord2.token, false);
                if (activityRecord2.attachedToProcess()) {
                    try {
                        activityRecord2.app.getThread().scheduleTranslucentConversionComplete(activityRecord2.token, activityRecord != null);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    @GuardedBy({"mService"})
    public boolean resumeTopActivityUncheckedLocked(ActivityRecord activityRecord, ActivityOptions activityOptions, boolean z) {
        boolean z2;
        if (this.mInResumeTopActivity) {
            return false;
        }
        try {
            this.mInResumeTopActivity = true;
            if (isLeafTask()) {
                z2 = isFocusableAndVisible() ? resumeTopActivityInnerLocked(activityRecord, activityOptions, z) : false;
            } else {
                int size = this.mChildren.size() - 1;
                boolean z3 = false;
                while (size >= 0) {
                    int i = size - 1;
                    Task task = (Task) getChildAt(size);
                    if (task.isTopActivityFocusable()) {
                        if (task.getVisibility(null) != 0) {
                            if (task.topRunningActivity() != null) {
                                break;
                            }
                        } else {
                            z3 |= task.resumeTopActivityUncheckedLocked(activityRecord, activityOptions, z);
                            if (i >= this.mChildren.size()) {
                                size = this.mChildren.size() - 1;
                            }
                        }
                    }
                    size = i;
                }
                z2 = z3;
            }
            ActivityRecord activityRecord2 = topRunningActivity(true);
            if (activityRecord2 == null || !activityRecord2.canTurnScreenOn()) {
                checkReadyForSleep();
            }
            return z2;
        } finally {
            this.mInResumeTopActivity = false;
        }
    }

    @GuardedBy({"mService"})
    public boolean resumeTopActivityUncheckedLocked(ActivityRecord activityRecord, ActivityOptions activityOptions) {
        return resumeTopActivityUncheckedLocked(activityRecord, activityOptions, false);
    }

    @GuardedBy({"mService"})
    public final boolean resumeTopActivityInnerLocked(final ActivityRecord activityRecord, final ActivityOptions activityOptions, final boolean z) {
        if (this.mAtmService.isBooting() || this.mAtmService.isBooted()) {
            ActivityRecord activityRecord2 = topRunningActivity(true);
            if (activityRecord2 == null) {
                return resumeNextFocusableActivityWhenRootTaskIsEmpty(activityRecord, activityOptions);
            }
            final TaskFragment taskFragment = activityRecord2.getTaskFragment();
            final boolean[] zArr = {taskFragment.resumeTopActivity(activityRecord, activityOptions, z)};
            forAllLeafTaskFragments(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda38
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    Task.lambda$resumeTopActivityInnerLocked$21(TaskFragment.this, zArr, activityRecord, activityOptions, z, (TaskFragment) obj);
                }
            }, true);
            return zArr[0];
        }
        return false;
    }

    public static /* synthetic */ void lambda$resumeTopActivityInnerLocked$21(TaskFragment taskFragment, boolean[] zArr, ActivityRecord activityRecord, ActivityOptions activityOptions, boolean z, TaskFragment taskFragment2) {
        if (taskFragment != taskFragment2 && taskFragment2.canBeResumed(null)) {
            zArr[0] = taskFragment2.resumeTopActivity(activityRecord, activityOptions, z) | zArr[0];
        }
    }

    public final boolean resumeNextFocusableActivityWhenRootTaskIsEmpty(ActivityRecord activityRecord, ActivityOptions activityOptions) {
        Task adjustFocusToNextFocusableTask;
        if (!isActivityTypeHome() && (adjustFocusToNextFocusableTask = adjustFocusToNextFocusableTask("noMoreActivities")) != null) {
            return this.mRootWindowContainer.resumeFocusedTasksTopActivities(adjustFocusToNextFocusableTask, activityRecord, null);
        }
        ActivityOptions.abort(activityOptions);
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -143556958, 0, (String) null, new Object[]{"noMoreActivities"});
        }
        return this.mRootWindowContainer.resumeHomeActivity(activityRecord, "noMoreActivities", getDisplayArea());
    }

    /* JADX WARN: Code restructure failed: missing block: B:56:0x00d6, code lost:
        if (topRunningNonDelayedActivityLocked(null) == r10) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00e0, code lost:
        if (r14.getAnimationType() == 5) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00e2, code lost:
        r11 = false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void startActivityLocked(ActivityRecord activityRecord, Task task, boolean z, boolean z2, ActivityOptions activityOptions, ActivityRecord activityRecord2) {
        boolean z3;
        ActivityRecord findEnterPipOnTaskSwitchCandidate = findEnterPipOnTaskSwitchCandidate(task);
        Task task2 = activityRecord.getTask();
        boolean z4 = activityOptions == null || !activityOptions.getAvoidMoveToFront();
        boolean z5 = task2 == this || hasChild(task2);
        if (!activityRecord.mLaunchTaskBehind && z4 && (!z5 || z)) {
            positionChildAtTop(task2);
        }
        if (!z && z5 && !activityRecord.shouldBeVisible()) {
            ActivityOptions.abort(activityOptions);
            return;
        }
        Task task3 = activityRecord.getTask();
        if (task3 == null && this.mChildren.indexOf(null) != getChildCount() - 1) {
            this.mTaskSupervisor.mUserLeaving = false;
        }
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -1699018375, 0, (String) null, new Object[]{String.valueOf(activityRecord), String.valueOf(task3), String.valueOf(new RuntimeException("here").fillInStackTrace())});
        }
        if (isActivityTypeHomeOrRecents() && getActivityBelow(activityRecord) == null) {
            ActivityOptions.abort(activityOptions);
        } else if (!z4) {
            ActivityOptions.abort(activityOptions);
        } else {
            DisplayContent displayContent = this.mDisplayContent;
            if ((activityRecord.intent.getFlags() & 65536) != 0) {
                displayContent.prepareAppTransition(0);
                this.mTaskSupervisor.mNoAnimActivities.add(activityRecord);
                this.mTransitionController.setNoAnimation(activityRecord);
            } else {
                displayContent.prepareAppTransition(1);
                this.mTaskSupervisor.mNoAnimActivities.remove(activityRecord);
            }
            if (z && !activityRecord.mLaunchTaskBehind) {
                enableEnterPipOnTaskSwitch(findEnterPipOnTaskSwitchCandidate, null, activityRecord, activityOptions);
            }
            if (z) {
                if ((activityRecord.intent.getFlags() & 2097152) != 0) {
                    resetTaskIfNeeded(activityRecord, activityRecord);
                }
                z3 = true;
            } else {
                if (activityOptions != null) {
                }
                z3 = true;
            }
            if (activityOptions != null && activityOptions.getDisableStartingWindow()) {
                z3 = false;
            }
            if (activityRecord.mLaunchTaskBehind) {
                activityRecord.setVisibility(true);
                ensureActivitiesVisible(null, 0, false);
                this.mDisplayContent.executeAppTransition();
            } else if (z3) {
                this.mWmService.mStartingSurfaceController.showStartingWindow(activityRecord, activityRecord.getTask().getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$startActivityLocked$22;
                        lambda$startActivityLocked$22 = Task.lambda$startActivityLocked$22((ActivityRecord) obj);
                        return lambda$startActivityLocked$22;
                    }
                }), z, z2, activityRecord2);
            }
        }
    }

    public static /* synthetic */ boolean lambda$startActivityLocked$22(ActivityRecord activityRecord) {
        return activityRecord.mStartingData != null && activityRecord.showToCurrentUser();
    }

    public static ActivityRecord findEnterPipOnTaskSwitchCandidate(Task task) {
        if (task == null) {
            return null;
        }
        final ActivityRecord[] activityRecordArr = new ActivityRecord[1];
        task.forAllLeafTaskFragments(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda35
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$findEnterPipOnTaskSwitchCandidate$23;
                lambda$findEnterPipOnTaskSwitchCandidate$23 = Task.lambda$findEnterPipOnTaskSwitchCandidate$23(activityRecordArr, (TaskFragment) obj);
                return lambda$findEnterPipOnTaskSwitchCandidate$23;
            }
        });
        return activityRecordArr[0];
    }

    public static /* synthetic */ boolean lambda$findEnterPipOnTaskSwitchCandidate$23(ActivityRecord[] activityRecordArr, TaskFragment taskFragment) {
        ActivityRecord topNonFinishingActivity = taskFragment.getTopNonFinishingActivity();
        if (topNonFinishingActivity != null && topNonFinishingActivity.isState(ActivityRecord.State.RESUMED, ActivityRecord.State.PAUSING) && topNonFinishingActivity.supportsPictureInPicture()) {
            activityRecordArr[0] = topNonFinishingActivity;
            return true;
        }
        return false;
    }

    public static void enableEnterPipOnTaskSwitch(ActivityRecord activityRecord, Task task, ActivityRecord activityRecord2, ActivityOptions activityOptions) {
        if (activityRecord == null) {
            return;
        }
        if ((activityOptions == null || !activityOptions.disallowEnterPictureInPictureWhileLaunching()) && !activityRecord.inPinnedWindowingMode()) {
            boolean z = activityOptions != null && activityOptions.getTransientLaunch();
            Task rootTask = task != null ? task.getRootTask() : activityRecord2.getRootTask();
            if (rootTask == null || !(rootTask.isActivityTypeAssistant() || z)) {
                activityRecord.supportsEnterPipOnTaskSwitch = true;
            }
        }
    }

    public ActivityRecord resetTaskIfNeeded(ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        ActivityRecord topNonFinishingActivity;
        boolean z = (activityRecord2.info.flags & 4) != 0;
        Task task = activityRecord.getTask();
        this.mReuseTask = true;
        try {
            ActivityOptions process = sResetTargetTaskHelper.process(task, z);
            this.mReuseTask = false;
            if (this.mChildren.contains(task) && (topNonFinishingActivity = task.getTopNonFinishingActivity()) != null) {
                activityRecord = topNonFinishingActivity;
            }
            if (process != null) {
                activityRecord.updateOptionsLocked(process);
            }
            return activityRecord;
        } catch (Throwable th) {
            this.mReuseTask = false;
            throw th;
        }
    }

    public final Task finishTopCrashedActivityLocked(WindowProcessController windowProcessController, String str) {
        ActivityRecord activityRecord = topRunningActivity();
        if (activityRecord == null || activityRecord.app != windowProcessController) {
            return null;
        }
        if (activityRecord.isActivityTypeHome() && this.mAtmService.mHomeProcess == windowProcessController) {
            Slog.w("ActivityTaskManager", "  Not force finishing home activity " + activityRecord.intent.getComponent().flattenToShortString());
            return null;
        }
        Slog.w("ActivityTaskManager", "  Force finishing activity " + activityRecord.intent.getComponent().flattenToShortString());
        Task task = activityRecord.getTask();
        this.mDisplayContent.requestTransitionAndLegacyPrepare(2, 16);
        activityRecord.finishIfPossible(str, false);
        ActivityRecord activityBelow = getActivityBelow(activityRecord);
        if (activityBelow != null && activityBelow.isState(ActivityRecord.State.STARTED, ActivityRecord.State.RESUMED, ActivityRecord.State.PAUSING, ActivityRecord.State.PAUSED) && (!activityBelow.isActivityTypeHome() || this.mAtmService.mHomeProcess != activityBelow.app)) {
            Slog.w("ActivityTaskManager", "  Force finishing activity " + activityBelow.intent.getComponent().flattenToShortString());
            activityBelow.finishIfPossible(str, false);
        }
        return task;
    }

    public void finishIfVoiceTask(IBinder iBinder) {
        IVoiceInteractionSession iVoiceInteractionSession = this.voiceSession;
        if (iVoiceInteractionSession != null && iVoiceInteractionSession.asBinder() == iBinder) {
            forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda33
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    Task.this.lambda$finishIfVoiceTask$24((ActivityRecord) obj);
                }
            });
            return;
        }
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new BiPredicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda34
            @Override // java.util.function.BiPredicate
            public final boolean test(Object obj, Object obj2) {
                boolean finishIfVoiceActivity;
                finishIfVoiceActivity = Task.finishIfVoiceActivity((ActivityRecord) obj, (IBinder) obj2);
                return finishIfVoiceActivity;
            }
        }, PooledLambda.__(ActivityRecord.class), iBinder);
        forAllActivities((Predicate<ActivityRecord>) obtainPredicate);
        obtainPredicate.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishIfVoiceTask$24(ActivityRecord activityRecord) {
        if (activityRecord.finishing) {
            return;
        }
        activityRecord.finishIfPossible("finish-voice", false);
        this.mAtmService.updateOomAdj();
    }

    public static boolean finishIfVoiceActivity(ActivityRecord activityRecord, IBinder iBinder) {
        IVoiceInteractionSession iVoiceInteractionSession = activityRecord.voiceSession;
        if (iVoiceInteractionSession == null || iVoiceInteractionSession.asBinder() != iBinder) {
            return false;
        }
        activityRecord.clearVoiceSessionLocked();
        try {
            activityRecord.app.getThread().scheduleLocalVoiceInteractionStarted(activityRecord.token, (IVoiceInteractor) null);
        } catch (RemoteException unused) {
        }
        activityRecord.mAtmService.finishRunningVoiceLocked();
        return true;
    }

    public final boolean inFrontOfStandardRootTask() {
        TaskDisplayArea displayArea = getDisplayArea();
        if (displayArea == null) {
            return false;
        }
        final boolean[] zArr = new boolean[1];
        Task rootTask = displayArea.getRootTask(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda13
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$inFrontOfStandardRootTask$25;
                lambda$inFrontOfStandardRootTask$25 = Task.this.lambda$inFrontOfStandardRootTask$25(zArr, (Task) obj);
                return lambda$inFrontOfStandardRootTask$25;
            }
        });
        return rootTask != null && rootTask.isActivityTypeStandard();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$inFrontOfStandardRootTask$25(boolean[] zArr, Task task) {
        if (zArr[0]) {
            return true;
        }
        if (task == this) {
            zArr[0] = true;
        }
        return false;
    }

    public boolean shouldUpRecreateTaskLocked(ActivityRecord activityRecord, String str) {
        String computeTaskAffinity = ActivityRecord.computeTaskAffinity(str, activityRecord.getUid(), activityRecord.launchMode);
        if (activityRecord.getTask().affinity == null || !activityRecord.getTask().affinity.equals(computeTaskAffinity)) {
            return true;
        }
        Task task = activityRecord.getTask();
        if (activityRecord.isRootOfTask() && task.getBaseIntent() != null && task.getBaseIntent().isDocument()) {
            if (!inFrontOfStandardRootTask()) {
                return true;
            }
            Task taskBelow = getTaskBelow(task);
            if (taskBelow == null) {
                Slog.w("ActivityTaskManager", "shouldUpRecreateTask: task not in history for " + activityRecord);
                return false;
            } else if (!task.affinity.equals(taskBelow.affinity)) {
                return true;
            }
        }
        return false;
    }

    public boolean navigateUpTo(ActivityRecord activityRecord, Intent intent, String str, NeededUriGrants neededUriGrants, int i, Intent intent2, NeededUriGrants neededUriGrants2) {
        boolean z;
        ActivityRecord activityRecord2;
        boolean z2;
        ActivityRecord activity;
        if (activityRecord.attachedToProcess()) {
            Task task = activityRecord.getTask();
            if (activityRecord.isDescendantOf(this)) {
                final ActivityRecord activityBelow = task.getActivityBelow(activityRecord);
                final ComponentName component = intent.getComponent();
                if (task.getBottomMostActivity() == activityRecord || component == null || (activity = task.getActivity(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda4
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$navigateUpTo$26;
                        lambda$navigateUpTo$26 = Task.lambda$navigateUpTo$26(component, (ActivityRecord) obj);
                        return lambda$navigateUpTo$26;
                    }
                }, activityRecord, false, true)) == null) {
                    z = false;
                } else {
                    activityBelow = activity;
                    z = true;
                }
                IActivityController iActivityController = this.mAtmService.mController;
                if (iActivityController != null && (activityRecord2 = topRunningActivity(activityRecord.token, -1)) != null) {
                    try {
                        z2 = iActivityController.activityResuming(activityRecord2.packageName);
                    } catch (RemoteException unused) {
                        this.mAtmService.mController = null;
                        Watchdog.getInstance().setActivityController(null);
                        z2 = true;
                    }
                    if (!z2) {
                        return false;
                    }
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                final int[] iArr = {i};
                final Intent[] intentArr = {intent2};
                final NeededUriGrants[] neededUriGrantsArr = {neededUriGrants2};
                task.forAllActivities(new Predicate() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda5
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$navigateUpTo$27;
                        lambda$navigateUpTo$27 = Task.lambda$navigateUpTo$27(ActivityRecord.this, iArr, intentArr, neededUriGrantsArr, (ActivityRecord) obj);
                        return lambda$navigateUpTo$27;
                    }
                }, activityRecord, true, true);
                int i2 = iArr[0];
                Intent intent3 = intentArr[0];
                if (activityBelow != null && z) {
                    int i3 = activityRecord.info.applicationInfo.uid;
                    int execute = this.mAtmService.getActivityStartController().obtainStarter(intent, "navigateUpTo").setResolvedType(str).setUserId(activityRecord.mUserId).setCaller(activityRecord.app.getThread()).setResultTo(activityBelow.token).setIntentGrants(neededUriGrants).setCallingPid(-1).setCallingUid(i3).setCallingPackage(activityRecord.packageName).setCallingFeatureId(activityBelow.launchedFromFeatureId).setRealCallingPid(-1).setRealCallingUid(i3).setComponentSpecified(true).execute();
                    z = ActivityManager.isStartResultSuccessful(execute);
                    if (execute == 0) {
                        activityBelow.finishIfPossible(i2, intent3, neededUriGrants2, "navigate-top", true);
                    }
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return z;
            }
            return false;
        }
        return false;
    }

    public static /* synthetic */ boolean lambda$navigateUpTo$26(ComponentName componentName, ActivityRecord activityRecord) {
        return activityRecord.info.packageName.equals(componentName.getPackageName()) && activityRecord.info.name.equals(componentName.getClassName());
    }

    public static /* synthetic */ boolean lambda$navigateUpTo$27(ActivityRecord activityRecord, int[] iArr, Intent[] intentArr, NeededUriGrants[] neededUriGrantsArr, ActivityRecord activityRecord2) {
        if (activityRecord2 == activityRecord) {
            return true;
        }
        activityRecord2.finishIfPossible(iArr[0], intentArr[0], neededUriGrantsArr[0], "navigate-up", true);
        iArr[0] = 0;
        intentArr[0] = null;
        return false;
    }

    public void removeLaunchTickMessages() {
        forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ActivityRecord) obj).removeLaunchTickRunnable();
            }
        });
    }

    public final void updateTransitLocked(int i, ActivityOptions activityOptions) {
        if (activityOptions != null) {
            ActivityRecord activityRecord = topRunningActivity();
            if (activityRecord != null && !activityRecord.isState(ActivityRecord.State.RESUMED)) {
                activityRecord.updateOptionsLocked(activityOptions);
            } else {
                ActivityOptions.abort(activityOptions);
            }
        }
        this.mDisplayContent.prepareAppTransition(i);
    }

    public final void moveTaskToFront(Task task, boolean z, ActivityOptions activityOptions, AppTimeTracker appTimeTracker, String str) {
        moveTaskToFront(task, z, activityOptions, appTimeTracker, false, str);
    }

    public final void moveTaskToFront(Task task, boolean z, ActivityOptions activityOptions, final AppTimeTracker appTimeTracker, boolean z2, String str) {
        ActivityRecord findEnterPipOnTaskSwitchCandidate = findEnterPipOnTaskSwitchCandidate(getDisplayArea().getTopRootTask());
        if (task != this && !task.isDescendantOf(this)) {
            if (z) {
                ActivityOptions.abort(activityOptions);
                return;
            } else {
                updateTransitLocked(3, activityOptions);
                return;
            }
        }
        if (appTimeTracker != null) {
            task.forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda25
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ActivityRecord) obj).appTimeTracker = AppTimeTracker.this;
                }
            });
        }
        try {
            this.mDisplayContent.deferUpdateImeTarget();
            ActivityRecord topNonFinishingActivity = task.getTopNonFinishingActivity();
            if (topNonFinishingActivity != null && topNonFinishingActivity.showToCurrentUser()) {
                topNonFinishingActivity.moveFocusableActivityToTop(str);
                if (z) {
                    this.mDisplayContent.prepareAppTransition(0);
                    this.mTaskSupervisor.mNoAnimActivities.add(topNonFinishingActivity);
                    ActivityOptions.abort(activityOptions);
                } else {
                    updateTransitLocked(3, activityOptions);
                }
                enableEnterPipOnTaskSwitch(findEnterPipOnTaskSwitchCandidate, task, null, activityOptions);
                if (!z2) {
                    this.mRootWindowContainer.resumeFocusedTasksTopActivities();
                }
                return;
            }
            positionChildAtTop(task);
            if (topNonFinishingActivity != null) {
                this.mTaskSupervisor.mRecentTasks.add(topNonFinishingActivity.getTask());
            }
            ActivityOptions.abort(activityOptions);
        } finally {
            this.mDisplayContent.continueUpdateImeTarget();
        }
    }

    public final boolean canMoveTaskToBack(Task task) {
        boolean z;
        if (this.mAtmService.getLockTaskController().canMoveTaskToBack(task)) {
            if (isTopRootTaskInDisplayArea() && this.mAtmService.mController != null) {
                ActivityRecord activityRecord = topRunningActivity(null, task.mTaskId);
                if (activityRecord == null) {
                    activityRecord = topRunningActivity(null, -1);
                }
                if (activityRecord != null) {
                    try {
                        z = this.mAtmService.mController.activityResuming(activityRecord.packageName);
                    } catch (RemoteException unused) {
                        this.mAtmService.mController = null;
                        Watchdog.getInstance().setActivityController(null);
                        z = true;
                    }
                    if (!z) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean moveTaskToBack(final Task task) {
        Slog.i("ActivityTaskManager", "moveTaskToBack: " + task);
        if (canMoveTaskToBack(task)) {
            if (this.mTransitionController.isShellTransitionsEnabled()) {
                final Transition transition = new Transition(4, 0, this.mTransitionController, this.mWmService.mSyncEngine);
                if (this.mWmService.mSyncEngine.hasActiveSync()) {
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1941440781, 0, (String) null, new Object[]{String.valueOf(transition)});
                    }
                    this.mWmService.mSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda21
                        @Override // java.lang.Runnable
                        public final void run() {
                            Task.this.lambda$moveTaskToBack$29(transition);
                        }
                    }, new Runnable() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda22
                        @Override // java.lang.Runnable
                        public final void run() {
                            Task.this.lambda$moveTaskToBack$30(transition, task);
                        }
                    });
                    return true;
                }
                this.mTransitionController.moveToCollecting(transition);
                this.mTransitionController.requestStartTransition(transition, task, null, null);
                moveTaskToBackInner(task);
                return true;
            }
            if (!inPinnedWindowingMode()) {
                this.mDisplayContent.prepareAppTransition(4);
            }
            moveTaskToBackInner(task);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$moveTaskToBack$29(Transition transition) {
        this.mTransitionController.moveToCollecting(transition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$moveTaskToBack$30(Transition transition, Task task) {
        this.mTransitionController.requestStartTransition(transition, task, null, null);
        if (!canMoveTaskToBack(task)) {
            Slog.e("ActivityTaskManager", "Failed to move task to back after saying we could: " + task.mTaskId);
            transition.abort();
            return;
        }
        moveTaskToBackInner(task);
    }

    public final boolean moveTaskToBackInner(Task task) {
        moveToBack("moveTaskToBackInner", task);
        if (inPinnedWindowingMode()) {
            this.mTaskSupervisor.removeRootTask(this);
            return true;
        }
        this.mRootWindowContainer.ensureVisibilityAndConfig(null, this.mDisplayContent.mDisplayId, false, false);
        ActivityRecord activityRecord = getDisplayArea().topRunningActivity();
        Task rootTask = activityRecord.getRootTask();
        if (rootTask != null && rootTask != this && activityRecord.isState(ActivityRecord.State.RESUMED)) {
            this.mDisplayContent.executeAppTransition();
        } else {
            this.mRootWindowContainer.resumeFocusedTasksTopActivities();
        }
        return true;
    }

    public boolean willActivityBeVisible(IBinder iBinder) {
        ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
        if (forTokenLocked != null && forTokenLocked.shouldBeVisible()) {
            if (forTokenLocked.finishing) {
                Slog.e("ActivityTaskManager", "willActivityBeVisible: Returning false, would have returned true for r=" + forTokenLocked);
            }
            return !forTokenLocked.finishing;
        }
        return false;
    }

    public void unhandledBackLocked() {
        ActivityRecord topMostActivity = getTopMostActivity();
        if (topMostActivity != null) {
            topMostActivity.finishIfPossible("unhandled-back", true);
        }
    }

    public boolean dump(FileDescriptor fileDescriptor, PrintWriter printWriter, boolean z, boolean z2, String str, boolean z3) {
        return dump("  ", fileDescriptor, printWriter, z, z2, str, z3, null);
    }

    @Override // com.android.server.p014wm.TaskFragment
    public void dumpInner(String str, PrintWriter printWriter, boolean z, String str2) {
        super.dumpInner(str, printWriter, z, str2);
        if (this.mCreatedByOrganizer) {
            printWriter.println(str + "  mCreatedByOrganizer=true");
        }
        if (this.mLastNonFullscreenBounds != null) {
            printWriter.print(str);
            printWriter.print("  mLastNonFullscreenBounds=");
            printWriter.println(this.mLastNonFullscreenBounds);
        }
        if (isLeafTask()) {
            printWriter.println(str + "  isSleeping=" + shouldSleepActivities());
            ActivityRecord topPausingActivity = getTopPausingActivity();
            ActivityTaskSupervisor.printThisActivity(printWriter, topPausingActivity, str2, false, str + "  topPausingActivity=", null);
            ActivityRecord topResumedActivity = getTopResumedActivity();
            ActivityTaskSupervisor.printThisActivity(printWriter, topResumedActivity, str2, false, str + "  topResumedActivity=", null);
            if (this.mMinWidth == -1 && this.mMinHeight == -1) {
                return;
            }
            printWriter.print(str);
            printWriter.print("  mMinWidth=");
            printWriter.print(this.mMinWidth);
            printWriter.print(" mMinHeight=");
            printWriter.println(this.mMinHeight);
        }
    }

    public ArrayList<ActivityRecord> getDumpActivitiesLocked(String str, int i) {
        final ArrayList<ActivityRecord> arrayList = new ArrayList<>();
        if ("all".equals(str)) {
            forAllActivities(new Task$$ExternalSyntheticLambda46(arrayList));
        } else if ("top".equals(str)) {
            ActivityRecord topMostActivity = getTopMostActivity();
            if (topMostActivity != null) {
                arrayList.add(topMostActivity);
            }
        } else {
            final ActivityManagerService.ItemMatcher itemMatcher = new ActivityManagerService.ItemMatcher();
            itemMatcher.build(str);
            forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda47
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    Task.lambda$getDumpActivitiesLocked$31(ActivityManagerService.ItemMatcher.this, arrayList, (ActivityRecord) obj);
                }
            });
        }
        if (i != -1) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (arrayList.get(size).mUserId != i) {
                    arrayList.remove(size);
                }
            }
        }
        return arrayList;
    }

    public static /* synthetic */ void lambda$getDumpActivitiesLocked$31(ActivityManagerService.ItemMatcher itemMatcher, ArrayList arrayList, ActivityRecord activityRecord) {
        if (itemMatcher.match(activityRecord, activityRecord.intent.getComponent())) {
            arrayList.add(activityRecord);
        }
    }

    public ActivityRecord restartPackage(final String str) {
        final ActivityRecord activityRecord = topRunningActivity();
        forAllActivities(new Consumer() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda39
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Task.lambda$restartPackage$32(str, activityRecord, (ActivityRecord) obj);
            }
        });
        return activityRecord;
    }

    public static /* synthetic */ void lambda$restartPackage$32(String str, ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        if (activityRecord2.info.packageName.equals(str)) {
            activityRecord2.forceNewConfig = true;
            if (activityRecord != null && activityRecord2 == activityRecord && activityRecord2.isVisibleRequested()) {
                activityRecord2.startFreezingScreenLocked(256);
            }
        }
    }

    public Task reuseOrCreateTask(ActivityInfo activityInfo, Intent intent, boolean z) {
        return reuseOrCreateTask(activityInfo, intent, null, null, z, null, null, null);
    }

    public Task reuseOrCreateTask(ActivityInfo activityInfo, Intent intent, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor, boolean z, ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityOptions activityOptions) {
        int nextTaskIdForUser;
        Task build;
        if (canReuseAsLeafTask()) {
            build = reuseAsLeafTask(iVoiceInteractionSession, iVoiceInteractor, intent, activityInfo, activityRecord);
        } else {
            if (activityRecord != null) {
                nextTaskIdForUser = this.mTaskSupervisor.getNextTaskIdForUser(activityRecord.mUserId);
            } else {
                nextTaskIdForUser = this.mTaskSupervisor.getNextTaskIdForUser();
            }
            build = new Builder(this.mAtmService).setTaskId(nextTaskIdForUser).setActivityInfo(activityInfo).setActivityOptions(activityOptions).setIntent(intent).setVoiceSession(iVoiceInteractionSession).setVoiceInteractor(iVoiceInteractor).setOnTop(z).setParent(this).build();
        }
        int displayId = getDisplayId();
        if (displayId == -1) {
            displayId = 0;
        }
        boolean isKeyguardOrAodShowing = this.mAtmService.mTaskSupervisor.getKeyguardController().isKeyguardOrAodShowing(displayId);
        if (!this.mTaskSupervisor.getLaunchParamsController().layoutTask(build, activityInfo.windowLayout, activityRecord, activityRecord2, activityOptions) && !getRequestedOverrideBounds().isEmpty() && build.isResizeable() && !isKeyguardOrAodShowing) {
            build.setBounds(getRequestedOverrideBounds());
        }
        return build;
    }

    public final boolean canReuseAsLeafTask() {
        if (this.mCreatedByOrganizer || !isLeafTask()) {
            return false;
        }
        return DisplayContent.alwaysCreateRootTask(getWindowingMode(), getActivityType());
    }

    public void addChild(WindowContainer windowContainer, boolean z, boolean z2) {
        Task asTask = windowContainer.asTask();
        if (asTask != null) {
            try {
                asTask.setForceShowForAllUsers(z2);
            } catch (Throwable th) {
                if (asTask != null) {
                    asTask.setForceShowForAllUsers(false);
                }
                throw th;
            }
        }
        addChild(windowContainer, z ? Integer.MAX_VALUE : 0, z);
        if (asTask != null) {
            asTask.setForceShowForAllUsers(false);
        }
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public void setAlwaysOnTop(boolean z) {
        if (super.isAlwaysOnTop() == z) {
            return;
        }
        super.setAlwaysOnTop(z);
        if (isForceHidden()) {
            return;
        }
        getDisplayArea().positionChildAt(Integer.MAX_VALUE, this, false);
    }

    public void dismissPip() {
        if (!isActivityTypeStandardOrUndefined()) {
            throw new IllegalArgumentException("You can't move tasks from non-standard root tasks.");
        }
        if (getWindowingMode() != 2) {
            throw new IllegalArgumentException("Can't exit pinned mode if it's not pinned already.");
        }
        this.mWmService.inSurfaceTransaction(new Runnable() { // from class: com.android.server.wm.Task$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                Task.this.lambda$dismissPip$33();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dismissPip$33() {
        Task bottomMostTask = getBottomMostTask();
        setWindowingMode(0);
        if (isAttached()) {
            getDisplayArea().positionChildAt(Integer.MAX_VALUE, this, false);
        }
        this.mTaskSupervisor.scheduleUpdatePictureInPictureModeIfNeeded(bottomMostTask, this);
    }

    public final int setBounds(Rect rect, Rect rect2) {
        if (ConfigurationContainer.equivalentBounds(rect, rect2)) {
            return 0;
        }
        if (!inMultiWindowMode()) {
            rect2 = null;
        }
        return setBoundsUnchecked(rect2);
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public void getBounds(Rect rect) {
        rect.set(getBounds());
    }

    public final void addChild(WindowContainer windowContainer, int i, boolean z) {
        addChild((Task) windowContainer, (Comparator<Task>) null);
        positionChildAt(i, windowContainer, z);
    }

    public void positionChildAtTop(Task task) {
        if (task == null) {
            return;
        }
        if (task == this) {
            moveToFront("positionChildAtTop");
        } else {
            positionChildAt(Integer.MAX_VALUE, task, true);
        }
    }

    public void positionChildAtBottom(Task task) {
        positionChildAtBottom(task, getDisplayArea().getNextFocusableRootTask(task.getRootTask(), true) == null);
    }

    @VisibleForTesting
    public void positionChildAtBottom(Task task, boolean z) {
        if (task == null) {
            return;
        }
        positionChildAt(Integer.MIN_VALUE, task, z);
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer
    public void onChildPositionChanged(WindowContainer windowContainer) {
        dispatchTaskInfoChangedIfNeeded(false);
        if (this.mChildren.contains(windowContainer) && windowContainer.asTask() != null) {
            this.mRootWindowContainer.invalidateTaskLayers();
        }
    }

    public void reparent(TaskDisplayArea taskDisplayArea, boolean z) {
        if (taskDisplayArea == null) {
            throw new IllegalArgumentException("Task can't reparent to null " + this);
        } else if (getParent() == taskDisplayArea) {
            throw new IllegalArgumentException("Task=" + this + " already child of " + taskDisplayArea);
        } else if (canBeLaunchedOnDisplay(taskDisplayArea.getDisplayId())) {
            reparent(taskDisplayArea, z ? Integer.MAX_VALUE : Integer.MIN_VALUE);
            if (isLeafTask()) {
                taskDisplayArea.onLeafTaskMoved(this, z, !z);
            }
        } else {
            Slog.w("ActivityTaskManager", "Task=" + this + " can't reparent to " + taskDisplayArea);
        }
    }

    public void setLastRecentsAnimationTransaction(PictureInPictureSurfaceTransaction pictureInPictureSurfaceTransaction, SurfaceControl surfaceControl) {
        this.mLastRecentsAnimationTransaction = new PictureInPictureSurfaceTransaction(pictureInPictureSurfaceTransaction);
        this.mLastRecentsAnimationOverlay = surfaceControl;
    }

    public void clearLastRecentsAnimationTransaction(boolean z) {
        if (z && this.mLastRecentsAnimationOverlay != null) {
            getPendingTransaction().remove(this.mLastRecentsAnimationOverlay);
        }
        this.mLastRecentsAnimationTransaction = null;
        this.mLastRecentsAnimationOverlay = null;
        resetSurfaceControlTransforms();
    }

    public void resetSurfaceControlTransforms() {
        getSyncTransaction().setMatrix(this.mSurfaceControl, Matrix.IDENTITY_MATRIX, new float[9]).setWindowCrop(this.mSurfaceControl, null).setShadowRadius(this.mSurfaceControl, 0.0f).setCornerRadius(this.mSurfaceControl, 0.0f);
    }

    public void maybeApplyLastRecentsAnimationTransaction() {
        if (this.mLastRecentsAnimationTransaction != null) {
            SurfaceControl.Transaction pendingTransaction = getPendingTransaction();
            SurfaceControl surfaceControl = this.mLastRecentsAnimationOverlay;
            if (surfaceControl != null) {
                pendingTransaction.reparent(surfaceControl, this.mSurfaceControl);
            }
            PictureInPictureSurfaceTransaction.apply(this.mLastRecentsAnimationTransaction, this.mSurfaceControl, pendingTransaction);
            pendingTransaction.show(this.mSurfaceControl);
            this.mLastRecentsAnimationTransaction = null;
            this.mLastRecentsAnimationOverlay = null;
        }
    }

    public final void updateSurfaceBounds() {
        updateSurfaceSize(getSyncTransaction());
        updateSurfacePositionNonOrganized();
        scheduleAnimation();
    }

    public final Point getRelativePosition() {
        Point point = new Point();
        getRelativePosition(point);
        return point;
    }

    public boolean shouldIgnoreInput() {
        return this.mAtmService.mHasLeanbackFeature && inPinnedWindowingMode() && !isFocusedRootTaskOnDisplay();
    }

    public final void warnForNonLeafTask(String str) {
        if (isLeafTask()) {
            return;
        }
        Slog.w("ActivityTaskManager", str + " on non-leaf task " + this);
    }

    public DisplayInfo getDisplayInfo() {
        return this.mDisplayContent.getDisplayInfo();
    }

    public AnimatingActivityRegistry getAnimatingActivityRegistry() {
        return this.mAnimatingActivityRegistry;
    }

    @Override // com.android.server.p014wm.TaskFragment
    public void executeAppTransition(ActivityOptions activityOptions) {
        this.mDisplayContent.executeAppTransition();
        ActivityOptions.abort(activityOptions);
    }

    @Override // com.android.server.p014wm.TaskFragment
    public boolean shouldSleepActivities() {
        boolean isKeyguardGoingAway;
        DisplayContent displayContent = this.mDisplayContent;
        if (displayContent != null) {
            isKeyguardGoingAway = displayContent.isKeyguardGoingAway();
        } else {
            isKeyguardGoingAway = this.mRootWindowContainer.getDefaultDisplay().isKeyguardGoingAway();
        }
        if (isKeyguardGoingAway && isFocusedRootTaskOnDisplay() && displayContent.isDefaultDisplay) {
            return false;
        }
        return displayContent != null ? displayContent.isSleeping() : this.mAtmService.isSleepingLocked();
    }

    public final Rect getRawBounds() {
        return super.getBounds();
    }

    public void dispatchTaskInfoChangedIfNeeded(boolean z) {
        if (isOrganized()) {
            this.mAtmService.mTaskOrganizerController.onTaskInfoChanged(this, z);
        }
    }

    public void setReparentLeafTaskIfRelaunch(boolean z) {
        if (isOrganized()) {
            this.mReparentLeafTaskIfRelaunch = z;
        }
    }

    public boolean isSameRequiredDisplayCategory(ActivityInfo activityInfo) {
        String str = this.mRequiredDisplayCategory;
        return (str != null && str.equals(activityInfo.requiredDisplayCategory)) || (this.mRequiredDisplayCategory == null && activityInfo.requiredDisplayCategory == null);
    }

    @Override // com.android.server.p014wm.TaskFragment, com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j, int i) {
        if (i != 2 || isVisible()) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464258L, this.mTaskId);
            protoOutputStream.write(1120986464272L, getRootTaskId());
            if (getTopResumedActivity() != null) {
                getTopResumedActivity().writeIdentifierToProto(protoOutputStream, 1146756268044L);
            }
            ComponentName componentName = this.realActivity;
            if (componentName != null) {
                protoOutputStream.write(1138166333453L, componentName.flattenToShortString());
            }
            ComponentName componentName2 = this.origActivity;
            if (componentName2 != null) {
                protoOutputStream.write(1138166333454L, componentName2.flattenToShortString());
            }
            protoOutputStream.write(1120986464274L, this.mResizeMode);
            protoOutputStream.write(1133871366148L, matchParentBounds());
            getRawBounds().dumpDebug(protoOutputStream, 1146756268037L);
            Rect rect = this.mLastNonFullscreenBounds;
            if (rect != null) {
                rect.dumpDebug(protoOutputStream, 1146756268054L);
            }
            SurfaceControl surfaceControl = this.mSurfaceControl;
            if (surfaceControl != null) {
                protoOutputStream.write(1120986464264L, surfaceControl.getWidth());
                protoOutputStream.write(1120986464265L, this.mSurfaceControl.getHeight());
            }
            protoOutputStream.write(1133871366172L, this.mCreatedByOrganizer);
            protoOutputStream.write(1138166333469L, this.affinity);
            protoOutputStream.write(1133871366174L, this.mChildPipActivity != null);
            super.dumpDebug(protoOutputStream, 1146756268063L, i);
            protoOutputStream.end(start);
        }
    }

    /* renamed from: com.android.server.wm.Task$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        public ActivityInfo mActivityInfo;
        public ActivityOptions mActivityOptions;
        public int mActivityType;
        public String mAffinity;
        public Intent mAffinityIntent;
        public boolean mAskedCompatMode;
        public final ActivityTaskManagerService mAtmService;
        public boolean mAutoRemoveRecents;
        public String mCallingFeatureId;
        public String mCallingPackage;
        public int mCallingUid;
        public boolean mCreatedByOrganizer;
        public boolean mDeferTaskAppear;
        public int mEffectiveUid;
        public boolean mHasBeenVisible;
        public Intent mIntent;
        public String mLastDescription;
        public ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData mLastSnapshotData;
        public ActivityManager.TaskDescription mLastTaskDescription;
        public long mLastTimeMoved;
        public IBinder mLaunchCookie;
        public int mLaunchFlags;
        public boolean mNeverRelinquishIdentity;
        public boolean mOnTop;
        public ComponentName mOrigActivity;
        public WindowContainer mParent;
        public ComponentName mRealActivity;
        public boolean mRealActivitySuspended;
        public boolean mRemoveWithTaskOrganizer;
        public int mResizeMode;
        public String mRootAffinity;
        public boolean mRootWasReset;
        public Task mSourceTask;
        public boolean mSupportsPictureInPicture;
        public int mTaskAffiliation;
        public int mTaskId;
        public int mUserId;
        public boolean mUserSetupComplete;
        public IVoiceInteractor mVoiceInteractor;
        public IVoiceInteractionSession mVoiceSession;
        public int mPrevAffiliateTaskId = -1;
        public int mNextAffiliateTaskId = -1;
        public int mMinWidth = -1;
        public int mMinHeight = -1;
        public int mWindowingMode = 0;

        public Builder(ActivityTaskManagerService activityTaskManagerService) {
            this.mAtmService = activityTaskManagerService;
        }

        public Builder setParent(WindowContainer windowContainer) {
            this.mParent = windowContainer;
            return this;
        }

        public Builder setSourceTask(Task task) {
            this.mSourceTask = task;
            return this;
        }

        public Builder setLaunchFlags(int i) {
            this.mLaunchFlags = i;
            return this;
        }

        public Builder setTaskId(int i) {
            this.mTaskId = i;
            return this;
        }

        public Builder setIntent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public Builder setRealActivity(ComponentName componentName) {
            this.mRealActivity = componentName;
            return this;
        }

        public Builder setEffectiveUid(int i) {
            this.mEffectiveUid = i;
            return this;
        }

        public Builder setMinWidth(int i) {
            this.mMinWidth = i;
            return this;
        }

        public Builder setMinHeight(int i) {
            this.mMinHeight = i;
            return this;
        }

        public Builder setActivityInfo(ActivityInfo activityInfo) {
            this.mActivityInfo = activityInfo;
            return this;
        }

        public Builder setActivityOptions(ActivityOptions activityOptions) {
            this.mActivityOptions = activityOptions;
            return this;
        }

        public Builder setVoiceSession(IVoiceInteractionSession iVoiceInteractionSession) {
            this.mVoiceSession = iVoiceInteractionSession;
            return this;
        }

        public Builder setActivityType(int i) {
            this.mActivityType = i;
            return this;
        }

        public Builder setWindowingMode(int i) {
            this.mWindowingMode = i;
            return this;
        }

        public Builder setCreatedByOrganizer(boolean z) {
            this.mCreatedByOrganizer = z;
            return this;
        }

        public Builder setDeferTaskAppear(boolean z) {
            this.mDeferTaskAppear = z;
            return this;
        }

        public Builder setLaunchCookie(IBinder iBinder) {
            this.mLaunchCookie = iBinder;
            return this;
        }

        public Builder setOnTop(boolean z) {
            this.mOnTop = z;
            return this;
        }

        public Builder setHasBeenVisible(boolean z) {
            this.mHasBeenVisible = z;
            return this;
        }

        public Builder setRemoveWithTaskOrganizer(boolean z) {
            this.mRemoveWithTaskOrganizer = z;
            return this;
        }

        public final Builder setUserId(int i) {
            this.mUserId = i;
            return this;
        }

        public final Builder setLastTimeMoved(long j) {
            this.mLastTimeMoved = j;
            return this;
        }

        public final Builder setNeverRelinquishIdentity(boolean z) {
            this.mNeverRelinquishIdentity = z;
            return this;
        }

        public final Builder setCallingUid(int i) {
            this.mCallingUid = i;
            return this;
        }

        public final Builder setCallingPackage(String str) {
            this.mCallingPackage = str;
            return this;
        }

        public final Builder setResizeMode(int i) {
            this.mResizeMode = i;
            return this;
        }

        public final Builder setSupportsPictureInPicture(boolean z) {
            this.mSupportsPictureInPicture = z;
            return this;
        }

        public final Builder setUserSetupComplete(boolean z) {
            this.mUserSetupComplete = z;
            return this;
        }

        public final Builder setTaskAffiliation(int i) {
            this.mTaskAffiliation = i;
            return this;
        }

        public final Builder setPrevAffiliateTaskId(int i) {
            this.mPrevAffiliateTaskId = i;
            return this;
        }

        public final Builder setNextAffiliateTaskId(int i) {
            this.mNextAffiliateTaskId = i;
            return this;
        }

        public final Builder setCallingFeatureId(String str) {
            this.mCallingFeatureId = str;
            return this;
        }

        public final Builder setRealActivitySuspended(boolean z) {
            this.mRealActivitySuspended = z;
            return this;
        }

        public final Builder setLastDescription(String str) {
            this.mLastDescription = str;
            return this;
        }

        public final Builder setLastTaskDescription(ActivityManager.TaskDescription taskDescription) {
            this.mLastTaskDescription = taskDescription;
            return this;
        }

        public final Builder setLastSnapshotData(ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData persistedTaskSnapshotData) {
            this.mLastSnapshotData = persistedTaskSnapshotData;
            return this;
        }

        public final Builder setOrigActivity(ComponentName componentName) {
            this.mOrigActivity = componentName;
            return this;
        }

        public final Builder setRootWasReset(boolean z) {
            this.mRootWasReset = z;
            return this;
        }

        public final Builder setAutoRemoveRecents(boolean z) {
            this.mAutoRemoveRecents = z;
            return this;
        }

        public final Builder setAskedCompatMode(boolean z) {
            this.mAskedCompatMode = z;
            return this;
        }

        public final Builder setAffinityIntent(Intent intent) {
            this.mAffinityIntent = intent;
            return this;
        }

        public final Builder setAffinity(String str) {
            this.mAffinity = str;
            return this;
        }

        public final Builder setRootAffinity(String str) {
            this.mRootAffinity = str;
            return this;
        }

        public final Builder setVoiceInteractor(IVoiceInteractor iVoiceInteractor) {
            this.mVoiceInteractor = iVoiceInteractor;
            return this;
        }

        public final void validateRootTask(TaskDisplayArea taskDisplayArea) {
            Task rootTask;
            if (this.mActivityType == 0 && !this.mCreatedByOrganizer) {
                this.mActivityType = 1;
            }
            int i = this.mActivityType;
            if (i != 1 && i != 0 && (rootTask = taskDisplayArea.getRootTask(0, i)) != null) {
                throw new IllegalArgumentException("Root task=" + rootTask + " of activityType=" + this.mActivityType + " already on display=" + taskDisplayArea + ". Can't have multiple.");
            }
            int i2 = this.mWindowingMode;
            ActivityTaskManagerService activityTaskManagerService = this.mAtmService;
            if (!TaskDisplayArea.isWindowingModeSupported(i2, activityTaskManagerService.mSupportsMultiWindow, activityTaskManagerService.mSupportsFreeformWindowManagement, activityTaskManagerService.mSupportsPictureInPicture)) {
                throw new IllegalArgumentException("Can't create root task for unsupported windowingMode=" + this.mWindowingMode);
            }
            int i3 = this.mWindowingMode;
            if (i3 == 2 && this.mActivityType != 1) {
                throw new IllegalArgumentException("Root task with pinned windowing mode cannot with non-standard activity type.");
            }
            if (i3 == 2 && taskDisplayArea.getRootPinnedTask() != null) {
                taskDisplayArea.getRootPinnedTask().dismissPip();
            }
            Intent intent = this.mIntent;
            if (intent != null) {
                this.mLaunchFlags = intent.getFlags() | this.mLaunchFlags;
            }
            Task launchRootTask = this.mCreatedByOrganizer ? null : taskDisplayArea.getLaunchRootTask(this.mWindowingMode, this.mActivityType, this.mActivityOptions, this.mSourceTask, this.mLaunchFlags);
            if (launchRootTask != null) {
                this.mWindowingMode = 0;
                this.mParent = launchRootTask;
            }
            this.mTaskId = taskDisplayArea.getNextRootTaskId();
        }

        public Task build() {
            ActivityOptions activityOptions;
            WindowContainer windowContainer = this.mParent;
            if (windowContainer != null && (windowContainer instanceof TaskDisplayArea)) {
                validateRootTask((TaskDisplayArea) windowContainer);
            }
            if (this.mActivityInfo == null) {
                ActivityInfo activityInfo = new ActivityInfo();
                this.mActivityInfo = activityInfo;
                activityInfo.applicationInfo = new ApplicationInfo();
            }
            this.mUserId = UserHandle.getUserId(this.mActivityInfo.applicationInfo.uid);
            this.mTaskAffiliation = this.mTaskId;
            this.mLastTimeMoved = System.currentTimeMillis();
            this.mNeverRelinquishIdentity = true;
            ActivityInfo activityInfo2 = this.mActivityInfo;
            this.mCallingUid = activityInfo2.applicationInfo.uid;
            this.mCallingPackage = activityInfo2.packageName;
            this.mResizeMode = activityInfo2.resizeMode;
            this.mSupportsPictureInPicture = activityInfo2.supportsPictureInPicture();
            if (!this.mRemoveWithTaskOrganizer && (activityOptions = this.mActivityOptions) != null) {
                this.mRemoveWithTaskOrganizer = activityOptions.getRemoveWithTaskOranizer();
            }
            Task buildInner = buildInner();
            buildInner.mHasBeenVisible = this.mHasBeenVisible;
            int i = this.mActivityType;
            if (i != 0) {
                buildInner.setActivityType(i);
            }
            WindowContainer windowContainer2 = this.mParent;
            if (windowContainer2 != null) {
                if (windowContainer2 instanceof Task) {
                    ((Task) windowContainer2).addChild(buildInner, this.mOnTop ? Integer.MAX_VALUE : Integer.MIN_VALUE, (this.mActivityInfo.flags & 1024) != 0);
                } else {
                    windowContainer2.addChild(buildInner, this.mOnTop ? Integer.MAX_VALUE : Integer.MIN_VALUE);
                }
            }
            int i2 = this.mWindowingMode;
            if (i2 != 0) {
                buildInner.setWindowingMode(i2, true);
            }
            return buildInner;
        }

        @VisibleForTesting
        public Task buildInner() {
            return new Task(this.mAtmService, this.mTaskId, this.mIntent, this.mAffinityIntent, this.mAffinity, this.mRootAffinity, this.mRealActivity, this.mOrigActivity, this.mRootWasReset, this.mAutoRemoveRecents, this.mAskedCompatMode, this.mUserId, this.mEffectiveUid, this.mLastDescription, this.mLastTimeMoved, this.mNeverRelinquishIdentity, this.mLastTaskDescription, this.mLastSnapshotData, this.mTaskAffiliation, this.mPrevAffiliateTaskId, this.mNextAffiliateTaskId, this.mCallingUid, this.mCallingPackage, this.mCallingFeatureId, this.mResizeMode, this.mSupportsPictureInPicture, this.mRealActivitySuspended, this.mUserSetupComplete, this.mMinWidth, this.mMinHeight, this.mActivityInfo, this.mVoiceSession, this.mVoiceInteractor, this.mCreatedByOrganizer, this.mLaunchCookie, this.mDeferTaskAppear, this.mRemoveWithTaskOrganizer);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void updateOverlayInsetsState(WindowState windowState) {
        super.updateOverlayInsetsState(windowState);
        if (windowState == getTopVisibleAppMainWindow() && this.mOverlayHost != null) {
            InsetsState insetsState = windowState.getInsetsState(true);
            getBounds(this.mTmpRect);
            this.mOverlayHost.dispatchInsetsChanged(insetsState, this.mTmpRect);
        }
    }
}
