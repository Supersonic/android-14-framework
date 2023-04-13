package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.net.Uri;
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
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.service.voice.IVoiceInteractionSession;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.window.PictureInPictureSurfaceTransaction;
import android.window.TaskFragmentAnimationParams;
import android.window.WindowContainerToken;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ResolverActivity;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.ToBooleanFunction;
import com.android.internal.util.function.QuintPredicate;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.internal.util.function.pooled.PooledPredicate;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.p006am.AppTimeTracker;
import com.android.server.p006am.UserState;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.ActivityTaskManagerService;
import com.android.server.p014wm.LaunchParamsController;
import com.android.server.p014wm.RootWindowContainer;
import com.android.server.p014wm.Task;
import com.android.server.policy.PermissionPolicyInternal;
import com.android.server.utils.Slogf;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.RootWindowContainer */
/* loaded from: classes2.dex */
public class RootWindowContainer extends WindowContainer<DisplayContent> implements DisplayManager.DisplayListener {
    public static final Consumer<WindowState> sRemoveReplacedWindowsConsumer = new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda2
        @Override // java.util.function.Consumer
        public final void accept(Object obj) {
            RootWindowContainer.lambda$static$1((WindowState) obj);
        }
    };
    public final AttachApplicationHelper mAttachApplicationHelper;
    public final Consumer<WindowState> mCloseSystemDialogsConsumer;
    public String mCloseSystemDialogsReason;
    public int mCurrentUser;
    public DisplayContent mDefaultDisplay;
    public int mDefaultMinSizeOfResizeableTaskDp;
    public String mDestroyAllActivitiesReason;
    public final Runnable mDestroyAllActivitiesRunnable;
    public final DeviceStateController mDeviceStateController;
    public final SparseArray<IntArray> mDisplayAccessUIDs;
    public DisplayManager mDisplayManager;
    public DisplayManagerInternal mDisplayManagerInternal;
    public final ActivityTaskManagerInternal.SleepTokenAcquirer mDisplayOffTokenAcquirer;
    public final DisplayRotationCoordinator mDisplayRotationCoordinator;
    public FinishDisabledPackageActivitiesHelper mFinishDisabledPackageActivitiesHelper;
    public final Handler mHandler;
    public Object mLastWindowFreezeSource;
    public boolean mObscureApplicationContentOnSecondaryDisplays;
    public boolean mOrientationChangeComplete;
    public final RankTaskLayersRunnable mRankTaskLayersRunnable;
    public float mScreenBrightnessOverride;
    public ActivityTaskManagerService mService;
    public final SparseArray<SleepToken> mSleepTokens;
    public boolean mSustainedPerformanceModeCurrent;
    public boolean mSustainedPerformanceModeEnabled;
    public boolean mTaskLayersChanged;
    public ActivityTaskSupervisor mTaskSupervisor;
    public final FindTaskResult mTmpFindTaskResult;
    public int mTmpTaskLayerRank;
    public final ArrayMap<Integer, ActivityRecord> mTopFocusedAppByProcess;
    public int mTopFocusedDisplayId;
    public boolean mUpdateRotation;
    public long mUserActivityTimeout;
    public SparseIntArray mUserRootTaskInFront;
    public boolean mWallpaperActionPending;
    public WindowManagerService mWindowManager;

    @Override // com.android.server.p014wm.ConfigurationContainer
    public String getName() {
        return "ROOT";
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isAttached() {
        return true;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isOnTop() {
        return true;
    }

    /* renamed from: com.android.server.wm.RootWindowContainer$1 */
    /* loaded from: classes2.dex */
    public class RunnableC18881 implements Runnable {
        public RunnableC18881() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (RootWindowContainer.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    RootWindowContainer.this.mTaskSupervisor.beginDeferResume();
                    RootWindowContainer.this.forAllActivities(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$1$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            RootWindowContainer.RunnableC18881.this.lambda$run$0((ActivityRecord) obj);
                        }
                    });
                    RootWindowContainer.this.mTaskSupervisor.endDeferResume();
                    RootWindowContainer.this.resumeFocusedTasksTopActivities();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0(ActivityRecord activityRecord) {
            if (activityRecord.finishing || !activityRecord.isDestroyable()) {
                return;
            }
            activityRecord.destroyImmediately(RootWindowContainer.this.mDestroyAllActivitiesReason);
        }
    }

    /* renamed from: com.android.server.wm.RootWindowContainer$FindTaskResult */
    /* loaded from: classes2.dex */
    public static class FindTaskResult implements Predicate<Task> {
        public ComponentName cls;
        public Uri documentData;
        public boolean isDocument;
        public int mActivityType;
        public ActivityRecord mCandidateRecord;
        public ActivityRecord mIdealRecord;
        public ActivityInfo mInfo;
        public Intent mIntent;
        public String mTaskAffinity;
        public int userId;

        public void init(int i, String str, Intent intent, ActivityInfo activityInfo) {
            this.mActivityType = i;
            this.mTaskAffinity = str;
            this.mIntent = intent;
            this.mInfo = activityInfo;
            this.mIdealRecord = null;
            this.mCandidateRecord = null;
        }

        public void process(WindowContainer windowContainer) {
            this.cls = this.mIntent.getComponent();
            if (this.mInfo.targetActivity != null) {
                ActivityInfo activityInfo = this.mInfo;
                this.cls = new ComponentName(activityInfo.packageName, activityInfo.targetActivity);
            }
            this.userId = UserHandle.getUserId(this.mInfo.applicationInfo.uid);
            Intent intent = this.mIntent;
            boolean isDocument = intent.isDocument() & (intent != null);
            this.isDocument = isDocument;
            this.documentData = isDocument ? this.mIntent.getData() : null;
            if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -814760297, 0, (String) null, new Object[]{String.valueOf(this.mInfo), String.valueOf(windowContainer)});
            }
            windowContainer.forAllLeafTasks(this);
        }

        /* JADX WARN: Removed duplicated region for block: B:101:0x019f  */
        /* JADX WARN: Removed duplicated region for block: B:48:0x00b5  */
        @Override // java.util.function.Predicate
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public boolean test(Task task) {
            Uri uri;
            boolean z;
            ComponentName componentName;
            String str;
            if (!ConfigurationContainer.isCompatibleActivityType(this.mActivityType, task.getActivityType())) {
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -373110070, 0, (String) null, new Object[]{String.valueOf(task)});
                }
                return false;
            } else if (task.voiceSession != null) {
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 51927339, 0, (String) null, new Object[]{String.valueOf(task)});
                }
                return false;
            } else if (task.mUserId != this.userId) {
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -399343789, 0, (String) null, new Object[]{String.valueOf(task)});
                }
                return false;
            } else {
                ActivityRecord topNonFinishingActivity = task.getTopNonFinishingActivity(false);
                if (topNonFinishingActivity == null || topNonFinishingActivity.finishing || topNonFinishingActivity.mUserId != this.userId || topNonFinishingActivity.launchMode == 3) {
                    if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1575977269, 0, (String) null, new Object[]{String.valueOf(task), String.valueOf(topNonFinishingActivity)});
                    }
                    return false;
                } else if (!ConfigurationContainer.isCompatibleActivityType(topNonFinishingActivity.getActivityType(), this.mActivityType)) {
                    if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1022146708, 0, (String) null, new Object[]{String.valueOf(task)});
                    }
                    return false;
                } else {
                    Intent intent = task.intent;
                    Intent intent2 = task.affinityIntent;
                    if (intent != null && intent.isDocument()) {
                        uri = intent.getData();
                    } else if (intent2 != null && intent2.isDocument()) {
                        uri = intent2.getData();
                    } else {
                        uri = null;
                        z = false;
                        if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                            ComponentName componentName2 = task.realActivity;
                            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 1192413464, 0, (String) null, new Object[]{String.valueOf(componentName2 != null ? componentName2.flattenToShortString() : ""), String.valueOf(task.rootAffinity), String.valueOf(this.mIntent.getComponent().flattenToShortString()), String.valueOf(this.mTaskAffinity)});
                        }
                        componentName = task.realActivity;
                        if (componentName == null && componentName.compareTo(this.cls) == 0 && Objects.equals(this.documentData, uri)) {
                            if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 1947936538, 0, (String) null, (Object[]) null);
                            }
                            if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 1557732761, 0, (String) null, new Object[]{String.valueOf(this.mIntent), String.valueOf(topNonFinishingActivity.intent)});
                            }
                            this.mIdealRecord = topNonFinishingActivity;
                            return true;
                        } else if (intent2 == null && intent2.getComponent() != null && intent2.getComponent().compareTo(this.cls) == 0 && Objects.equals(this.documentData, uri)) {
                            if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 1947936538, 0, (String) null, (Object[]) null);
                            }
                            if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 1557732761, 0, (String) null, new Object[]{String.valueOf(this.mIntent), String.valueOf(topNonFinishingActivity.intent)});
                            }
                            this.mIdealRecord = topNonFinishingActivity;
                            return true;
                        } else {
                            if (this.isDocument && !z && this.mIdealRecord == null && this.mCandidateRecord == null && (str = task.rootAffinity) != null) {
                                if (str.equals(this.mTaskAffinity) && task.isSameRequiredDisplayCategory(this.mInfo)) {
                                    if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 2039056415, 0, (String) null, (Object[]) null);
                                    }
                                    this.mCandidateRecord = topNonFinishingActivity;
                                }
                            } else if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -775004869, 0, (String) null, new Object[]{String.valueOf(task)});
                            }
                            return false;
                        }
                    }
                    z = true;
                    if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    }
                    componentName = task.realActivity;
                    if (componentName == null) {
                    }
                    if (intent2 == null) {
                    }
                    if (this.isDocument) {
                    }
                    if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    }
                    return false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(WindowState windowState) {
        if (windowState.mHasSurface) {
            try {
                windowState.mClient.closeSystemDialogs(this.mCloseSystemDialogsReason);
            } catch (RemoteException unused) {
            }
        }
    }

    public static /* synthetic */ void lambda$static$1(WindowState windowState) {
        ActivityRecord activityRecord = windowState.mActivityRecord;
        if (activityRecord != null) {
            activityRecord.removeReplacedWindowIfNeeded(windowState);
        }
    }

    public RootWindowContainer(WindowManagerService windowManagerService) {
        super(windowManagerService);
        this.mLastWindowFreezeSource = null;
        this.mScreenBrightnessOverride = Float.NaN;
        this.mUserActivityTimeout = -1L;
        this.mUpdateRotation = false;
        this.mObscureApplicationContentOnSecondaryDisplays = false;
        this.mSustainedPerformanceModeEnabled = false;
        this.mSustainedPerformanceModeCurrent = false;
        this.mOrientationChangeComplete = true;
        this.mWallpaperActionPending = false;
        this.mTopFocusedDisplayId = -1;
        this.mTopFocusedAppByProcess = new ArrayMap<>();
        this.mDisplayAccessUIDs = new SparseArray<>();
        this.mUserRootTaskInFront = new SparseIntArray(2);
        this.mSleepTokens = new SparseArray<>();
        this.mDefaultMinSizeOfResizeableTaskDp = -1;
        this.mTaskLayersChanged = true;
        this.mRankTaskLayersRunnable = new RankTaskLayersRunnable();
        this.mAttachApplicationHelper = new AttachApplicationHelper();
        this.mDestroyAllActivitiesRunnable = new RunnableC18881();
        this.mTmpFindTaskResult = new FindTaskResult();
        this.mCloseSystemDialogsConsumer = new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda45
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.this.lambda$new$0((WindowState) obj);
            }
        };
        this.mFinishDisabledPackageActivitiesHelper = new FinishDisabledPackageActivitiesHelper();
        this.mHandler = new MyHandler(windowManagerService.f1164mH.getLooper());
        ActivityTaskManagerService activityTaskManagerService = windowManagerService.mAtmService;
        this.mService = activityTaskManagerService;
        ActivityTaskSupervisor activityTaskSupervisor = activityTaskManagerService.mTaskSupervisor;
        this.mTaskSupervisor = activityTaskSupervisor;
        activityTaskSupervisor.mRootWindowContainer = this;
        Objects.requireNonNull(activityTaskManagerService);
        this.mDisplayOffTokenAcquirer = new ActivityTaskManagerService.SleepTokenAcquirerImpl("Display-off");
        this.mDeviceStateController = new DeviceStateController(windowManagerService.mContext, windowManagerService.f1164mH);
        this.mDisplayRotationCoordinator = new DisplayRotationCoordinator();
    }

    public boolean updateFocusedWindowLocked(int i, boolean z) {
        this.mTopFocusedAppByProcess.clear();
        boolean z2 = false;
        int i2 = -1;
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            DisplayContent displayContent = (DisplayContent) this.mChildren.get(size);
            z2 |= displayContent.updateFocusedWindowLocked(i, z, i2);
            WindowState windowState = displayContent.mCurrentFocus;
            if (windowState != null) {
                int i3 = windowState.mSession.mPid;
                if (this.mTopFocusedAppByProcess.get(Integer.valueOf(i3)) == null) {
                    this.mTopFocusedAppByProcess.put(Integer.valueOf(i3), windowState.mActivityRecord);
                }
                if (i2 == -1) {
                    i2 = displayContent.getDisplayId();
                }
            } else if (i2 == -1 && displayContent.mFocusedApp != null) {
                i2 = displayContent.getDisplayId();
            }
        }
        int i4 = i2 != -1 ? i2 : 0;
        if (this.mTopFocusedDisplayId != i4) {
            this.mTopFocusedDisplayId = i4;
            this.mWmService.mInputManager.setFocusedDisplay(i4);
            this.mWmService.mPolicy.setTopFocusedDisplay(i4);
            this.mWmService.mAccessibilityController.setFocusedDisplay(i4);
            if (ProtoLogCache.WM_DEBUG_FOCUS_LIGHT_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT, 312030608, 1, (String) null, new Object[]{Long.valueOf(i4)});
            }
        }
        return z2;
    }

    public DisplayContent getTopFocusedDisplayContent() {
        DisplayContent displayContent = getDisplayContent(this.mTopFocusedDisplayId);
        return displayContent != null ? displayContent : getDisplayContent(0);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void onChildPositionChanged(WindowContainer windowContainer) {
        WindowManagerService windowManagerService = this.mWmService;
        windowManagerService.updateFocusedWindowLocked(0, !windowManagerService.mPerDisplayFocusEnabled);
        this.mTaskSupervisor.updateTopResumedActivityIfNeeded("onChildPositionChanged");
    }

    public void onSettingsRetrieved() {
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            DisplayContent displayContent = (DisplayContent) this.mChildren.get(i);
            if (this.mWmService.mDisplayWindowSettings.updateSettingsForDisplay(displayContent)) {
                displayContent.reconfigureDisplayLocked();
                if (displayContent.isDefaultDisplay) {
                    this.mWmService.mAtmService.updateConfigurationLocked(this.mWmService.computeNewConfiguration(displayContent.getDisplayId()), null, false);
                }
            }
        }
    }

    public boolean isLayoutNeeded() {
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            if (((DisplayContent) this.mChildren.get(i)).isLayoutNeeded()) {
                return true;
            }
        }
        return false;
    }

    public void getWindowsByName(ArrayList<WindowState> arrayList, String str) {
        int i;
        try {
            i = Integer.parseInt(str, 16);
            str = null;
        } catch (RuntimeException unused) {
            i = 0;
        }
        getWindowsByName(arrayList, str, i);
    }

    public final void getWindowsByName(final ArrayList<WindowState> arrayList, final String str, final int i) {
        forAllWindows(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda51
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$getWindowsByName$2(str, arrayList, i, (WindowState) obj);
            }
        }, true);
    }

    public static /* synthetic */ void lambda$getWindowsByName$2(String str, ArrayList arrayList, int i, WindowState windowState) {
        if (str != null) {
            if (windowState.mAttrs.getTitle().toString().contains(str)) {
                arrayList.add(windowState);
            }
        } else if (System.identityHashCode(windowState) == i) {
            arrayList.add(windowState);
        }
    }

    public ActivityRecord getActivityRecord(IBinder iBinder) {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord = ((DisplayContent) this.mChildren.get(size)).getActivityRecord(iBinder);
            if (activityRecord != null) {
                return activityRecord;
            }
        }
        return null;
    }

    public WindowToken getWindowToken(IBinder iBinder) {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            WindowToken windowToken = ((DisplayContent) this.mChildren.get(size)).getWindowToken(iBinder);
            if (windowToken != null) {
                return windowToken;
            }
        }
        return null;
    }

    public DisplayContent getWindowTokenDisplay(WindowToken windowToken) {
        if (windowToken == null) {
            return null;
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            DisplayContent displayContent = (DisplayContent) this.mChildren.get(size);
            if (displayContent.getWindowToken(windowToken.token) == windowToken) {
                return displayContent;
            }
        }
        return null;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public void dispatchConfigurationToChild(DisplayContent displayContent, Configuration configuration) {
        if (displayContent.isDefaultDisplay) {
            displayContent.performDisplayOverrideConfigUpdate(configuration);
        } else {
            displayContent.onConfigurationChanged(configuration);
        }
    }

    public void refreshSecureSurfaceState() {
        forAllWindows(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda21
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$refreshSecureSurfaceState$3((WindowState) obj);
            }
        }, true);
    }

    public static /* synthetic */ void lambda$refreshSecureSurfaceState$3(WindowState windowState) {
        if (windowState.mHasSurface) {
            windowState.mWinAnimator.setSecureLocked(windowState.isSecureLocked());
        }
    }

    public void updateHiddenWhileSuspendedState(final ArraySet<String> arraySet, final boolean z) {
        forAllWindows(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda35
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$updateHiddenWhileSuspendedState$4(arraySet, z, (WindowState) obj);
            }
        }, false);
    }

    public static /* synthetic */ void lambda$updateHiddenWhileSuspendedState$4(ArraySet arraySet, boolean z, WindowState windowState) {
        if (arraySet.contains(windowState.getOwningPackage())) {
            windowState.setHiddenWhileSuspended(z);
        }
    }

    public void updateAppOpsState() {
        forAllWindows(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda50
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WindowState) obj).updateAppOpsState();
            }
        }, false);
    }

    public static /* synthetic */ boolean lambda$canShowStrictModeViolation$6(int i, WindowState windowState) {
        return windowState.mSession.mPid == i && windowState.isVisible();
    }

    public boolean canShowStrictModeViolation(final int i) {
        return getWindow(new Predicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda47
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$canShowStrictModeViolation$6;
                lambda$canShowStrictModeViolation$6 = RootWindowContainer.lambda$canShowStrictModeViolation$6(i, (WindowState) obj);
                return lambda$canShowStrictModeViolation$6;
            }
        }) != null;
    }

    public void closeSystemDialogs(String str) {
        this.mCloseSystemDialogsReason = str;
        forAllWindows(this.mCloseSystemDialogsConsumer, false);
    }

    /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.Object[], java.lang.String] */
    public void removeReplacedWindows() {
        if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, -1515151503, 0, (String) null, (Object[]) null);
        }
        this.mWmService.openSurfaceTransaction();
        try {
            forAllWindows(sRemoveReplacedWindowsConsumer, true);
        } finally {
            this.mWmService.closeSurfaceTransaction("removeReplacedWindows");
            if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, 1423592961, 0, (String) null, (Object[]) null);
            }
        }
    }

    public boolean hasPendingLayoutChanges(WindowAnimator windowAnimator) {
        int size = this.mChildren.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            int i2 = ((DisplayContent) this.mChildren.get(i)).pendingLayoutChanges;
            if ((i2 & 4) != 0) {
                windowAnimator.mBulkUpdateParams |= 2;
            }
            if (i2 != 0) {
                z = true;
            }
        }
        return z;
    }

    public boolean reclaimSomeSurfaceMemory(WindowStateAnimator windowStateAnimator, String str, boolean z) {
        boolean z2;
        WindowSurfaceController windowSurfaceController = windowStateAnimator.mSurfaceController;
        EventLogTags.writeWmNoSurfaceMemory(windowStateAnimator.mWin.toString(), windowStateAnimator.mSession.mPid, str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Slog.i(StartingSurfaceController.TAG, "Out of memory for surface!  Looking for leaks...");
            int size = this.mChildren.size();
            boolean z3 = false;
            for (int i = 0; i < size; i++) {
                z3 |= ((DisplayContent) this.mChildren.get(i)).destroyLeakedSurfaces();
            }
            if (z3) {
                z2 = false;
            } else {
                Slog.w(StartingSurfaceController.TAG, "No leaked surfaces; killing applications!");
                final SparseIntArray sparseIntArray = new SparseIntArray();
                z2 = false;
                for (int i2 = 0; i2 < size; i2++) {
                    ((DisplayContent) this.mChildren.get(i2)).forAllWindows(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda17
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            RootWindowContainer.this.lambda$reclaimSomeSurfaceMemory$7(sparseIntArray, (WindowState) obj);
                        }
                    }, false);
                    if (sparseIntArray.size() > 0) {
                        int size2 = sparseIntArray.size();
                        int[] iArr = new int[size2];
                        for (int i3 = 0; i3 < size2; i3++) {
                            iArr[i3] = sparseIntArray.keyAt(i3);
                        }
                        try {
                            try {
                                if (this.mWmService.mActivityManager.killPids(iArr, "Free memory", z)) {
                                    z2 = true;
                                }
                            } catch (RemoteException unused) {
                            }
                        } catch (RemoteException unused2) {
                        }
                    }
                }
            }
            if (z3 || z2) {
                Slog.w(StartingSurfaceController.TAG, "Looks like we have reclaimed some memory, clearing surface for retry.");
                if (windowSurfaceController != null) {
                    if (ProtoLogCache.WM_SHOW_SURFACE_ALLOC_enabled) {
                        ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_SURFACE_ALLOC, 399841913, 0, (String) null, new Object[]{String.valueOf(windowStateAnimator.mWin)});
                    }
                    SurfaceControl.Transaction transaction = this.mWmService.mTransactionFactory.get();
                    windowStateAnimator.destroySurface(transaction);
                    transaction.apply();
                    ActivityRecord activityRecord = windowStateAnimator.mWin.mActivityRecord;
                    if (activityRecord != null) {
                        activityRecord.removeStartingWindow();
                    }
                }
                try {
                    windowStateAnimator.mWin.mClient.dispatchGetNewSurface();
                } catch (RemoteException unused3) {
                }
            }
            return z3 || z2;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reclaimSomeSurfaceMemory$7(SparseIntArray sparseIntArray, WindowState windowState) {
        if (this.mWmService.mForceRemoves.contains(windowState)) {
            return;
        }
        WindowStateAnimator windowStateAnimator = windowState.mWinAnimator;
        if (windowStateAnimator.mSurfaceController != null) {
            int i = windowStateAnimator.mSession.mPid;
            sparseIntArray.append(i, i);
        }
    }

    public void performSurfacePlacement() {
        Trace.traceBegin(32L, "performSurfacePlacement");
        try {
            performSurfacePlacementNoTrace();
        } finally {
            Trace.traceEnd(32L);
        }
    }

    public void performSurfacePlacementNoTrace() {
        WindowManagerService windowManagerService = this.mWmService;
        if (windowManagerService.mFocusMayChange) {
            windowManagerService.mFocusMayChange = false;
            windowManagerService.updateFocusedWindowLocked(3, false);
        }
        float f = Float.NaN;
        this.mScreenBrightnessOverride = Float.NaN;
        this.mUserActivityTimeout = -1L;
        this.mObscureApplicationContentOnSecondaryDisplays = false;
        this.mSustainedPerformanceModeCurrent = false;
        WindowManagerService windowManagerService2 = this.mWmService;
        windowManagerService2.mTransactionSequence++;
        DisplayContent defaultDisplayContentLocked = windowManagerService2.getDefaultDisplayContentLocked();
        WindowSurfacePlacer windowSurfacePlacer = this.mWmService.mWindowPlacerLocked;
        Trace.traceBegin(32L, "applySurfaceChanges");
        this.mWmService.openSurfaceTransaction();
        try {
            try {
                applySurfaceChangesTransaction();
            } catch (RuntimeException e) {
                Slog.wtf(StartingSurfaceController.TAG, "Unhandled exception in Window Manager", e);
            }
            this.mWmService.mAtmService.mTaskOrganizerController.dispatchPendingEvents();
            this.mWmService.mAtmService.mTaskFragmentOrganizerController.dispatchPendingEvents();
            this.mWmService.mSyncEngine.onSurfacePlacement();
            this.mWmService.mAnimator.executeAfterPrepareSurfacesRunnables();
            checkAppTransitionReady(windowSurfacePlacer);
            RecentsAnimationController recentsAnimationController = this.mWmService.getRecentsAnimationController();
            if (recentsAnimationController != null) {
                recentsAnimationController.checkAnimationReady(defaultDisplayContentLocked.mWallpaperController);
            }
            this.mWmService.mAtmService.mBackNavigationController.checkAnimationReady(defaultDisplayContentLocked.mWallpaperController);
            for (int i = 0; i < this.mChildren.size(); i++) {
                DisplayContent displayContent = (DisplayContent) this.mChildren.get(i);
                if (displayContent.mWallpaperMayChange) {
                    if (ProtoLogCache.WM_DEBUG_WALLPAPER_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WALLPAPER, 535103992, 0, (String) null, (Object[]) null);
                    }
                    displayContent.pendingLayoutChanges |= 4;
                }
            }
            WindowManagerService windowManagerService3 = this.mWmService;
            if (windowManagerService3.mFocusMayChange) {
                windowManagerService3.mFocusMayChange = false;
                windowManagerService3.updateFocusedWindowLocked(2, false);
            }
            if (isLayoutNeeded()) {
                defaultDisplayContentLocked.pendingLayoutChanges |= 1;
            }
            handleResizingWindows();
            if (this.mWmService.mDisplayFrozen && ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -666510420, 3, (String) null, new Object[]{Boolean.valueOf(this.mOrientationChangeComplete)});
            }
            if (this.mOrientationChangeComplete) {
                WindowManagerService windowManagerService4 = this.mWmService;
                if (windowManagerService4.mWindowsFreezingScreen != 0) {
                    windowManagerService4.mWindowsFreezingScreen = 0;
                    windowManagerService4.mLastFinishedFreezeSource = this.mLastWindowFreezeSource;
                    windowManagerService4.f1164mH.removeMessages(11);
                }
                this.mWmService.stopFreezingDisplayLocked();
            }
            int size = this.mWmService.mDestroySurface.size();
            if (size > 0) {
                do {
                    size--;
                    WindowState windowState = this.mWmService.mDestroySurface.get(size);
                    windowState.mDestroying = false;
                    DisplayContent displayContent2 = windowState.getDisplayContent();
                    if (displayContent2.mInputMethodWindow == windowState) {
                        displayContent2.setInputMethodWindowLocked(null);
                    }
                    if (displayContent2.mWallpaperController.isWallpaperTarget(windowState)) {
                        displayContent2.pendingLayoutChanges |= 4;
                    }
                    windowState.destroySurfaceUnchecked();
                } while (size > 0);
                this.mWmService.mDestroySurface.clear();
            }
            for (int i2 = 0; i2 < this.mChildren.size(); i2++) {
                DisplayContent displayContent3 = (DisplayContent) this.mChildren.get(i2);
                if (displayContent3.pendingLayoutChanges != 0) {
                    displayContent3.setLayoutNeeded();
                }
            }
            if (!this.mWmService.mDisplayFrozen) {
                float f2 = this.mScreenBrightnessOverride;
                if (f2 >= 0.0f && f2 <= 1.0f) {
                    f = f2;
                }
                this.mHandler.obtainMessage(1, Float.floatToIntBits(f), 0).sendToTarget();
                this.mHandler.obtainMessage(2, Long.valueOf(this.mUserActivityTimeout)).sendToTarget();
            }
            boolean z = this.mSustainedPerformanceModeCurrent;
            if (z != this.mSustainedPerformanceModeEnabled) {
                this.mSustainedPerformanceModeEnabled = z;
                this.mWmService.mPowerManagerInternal.setPowerMode(2, z);
            }
            if (this.mUpdateRotation) {
                if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ORIENTATION, -1103115659, 0, (String) null, (Object[]) null);
                }
                this.mUpdateRotation = updateRotationUnchecked();
            }
            if (!this.mWmService.mWaitingForDrawnCallbacks.isEmpty() || (this.mOrientationChangeComplete && !isLayoutNeeded() && !this.mUpdateRotation)) {
                this.mWmService.checkDrawnWindowsLocked();
            }
            forAllDisplays(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda36
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    RootWindowContainer.lambda$performSurfacePlacementNoTrace$8((DisplayContent) obj);
                }
            });
            this.mWmService.enableScreenIfNeededLocked();
            this.mWmService.scheduleAnimationLocked();
        } finally {
            this.mWmService.closeSurfaceTransaction("performLayoutAndPlaceSurfaces");
            Trace.traceEnd(32L);
        }
    }

    public static /* synthetic */ void lambda$performSurfacePlacementNoTrace$8(DisplayContent displayContent) {
        displayContent.getInputMonitor().updateInputWindowsLw(true);
        displayContent.updateSystemGestureExclusion();
        displayContent.updateKeepClearAreas();
        displayContent.updateTouchExcludeRegion();
    }

    public final void checkAppTransitionReady(WindowSurfacePlacer windowSurfacePlacer) {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            DisplayContent displayContent = (DisplayContent) this.mChildren.get(size);
            if (displayContent.mAppTransition.isReady()) {
                displayContent.mAppTransitionController.handleAppTransitionReady();
            }
            if (displayContent.mAppTransition.isRunning() && !displayContent.isAppTransitioning()) {
                displayContent.handleAnimatingStoppedAndTransition();
            }
        }
    }

    public final void applySurfaceChangesTransaction() {
        DisplayContent displayContent = this.mDefaultDisplay;
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        int i = displayInfo.logicalWidth;
        int i2 = displayInfo.logicalHeight;
        SurfaceControl.Transaction syncTransaction = displayContent.getSyncTransaction();
        Watermark watermark = this.mWmService.mWatermark;
        if (watermark != null) {
            watermark.positionSurface(i, i2, syncTransaction);
        }
        StrictModeFlash strictModeFlash = this.mWmService.mStrictModeFlash;
        if (strictModeFlash != null) {
            strictModeFlash.positionSurface(i, i2, syncTransaction);
        }
        EmulatorDisplayOverlay emulatorDisplayOverlay = this.mWmService.mEmulatorDisplayOverlay;
        if (emulatorDisplayOverlay != null) {
            emulatorDisplayOverlay.positionSurface(i, i2, displayContent.getRotation(), syncTransaction);
        }
        int size = this.mChildren.size();
        for (int i3 = 0; i3 < size; i3++) {
            ((DisplayContent) this.mChildren.get(i3)).applySurfaceChangesTransaction();
        }
        this.mWmService.mDisplayManagerInternal.performTraversal(syncTransaction);
        if (syncTransaction != displayContent.mSyncTransaction) {
            SurfaceControl.mergeToGlobalTransaction(syncTransaction);
        }
    }

    public final void handleResizingWindows() {
        for (int size = this.mWmService.mResizingWindows.size() - 1; size >= 0; size--) {
            WindowState windowState = this.mWmService.mResizingWindows.get(size);
            if (!windowState.mAppFreezing && !windowState.getDisplayContent().mWaitingForConfig) {
                windowState.reportResized();
                this.mWmService.mResizingWindows.remove(size);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:39:0x00ba, code lost:
        if (r2 == 2009) goto L32;
     */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00c2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean handleNotObscuredLocked(WindowState windowState, boolean z, boolean z2) {
        WindowManager.LayoutParams layoutParams = windowState.mAttrs;
        int i = layoutParams.flags;
        boolean isOnScreen = windowState.isOnScreen();
        boolean isDisplayed = windowState.isDisplayed();
        int i2 = layoutParams.privateFlags;
        if (ProtoLogCache.WM_DEBUG_KEEP_SCREEN_ON_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_KEEP_SCREEN_ON, -481924678, 508, (String) null, new Object[]{String.valueOf(windowState), Boolean.valueOf(windowState.mHasSurface), Boolean.valueOf(isOnScreen), Boolean.valueOf(windowState.isDisplayed()), Long.valueOf(windowState.mAttrs.userActivityTimeout)});
        }
        if (windowState.mHasSurface && isOnScreen && !z2) {
            long j = windowState.mAttrs.userActivityTimeout;
            if (j >= 0 && this.mUserActivityTimeout < 0) {
                this.mUserActivityTimeout = j;
                if (ProtoLogCache.WM_DEBUG_KEEP_SCREEN_ON_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_KEEP_SCREEN_ON, 221540118, 1, (String) null, new Object[]{Long.valueOf(j)});
                }
            }
        }
        boolean z3 = false;
        if (windowState.mHasSurface && isDisplayed) {
            if (!z2 && windowState.mAttrs.screenBrightness >= 0.0f && Float.isNaN(this.mScreenBrightnessOverride)) {
                this.mScreenBrightnessOverride = windowState.mAttrs.screenBrightness;
            }
            int i3 = layoutParams.type;
            DisplayContent displayContent = windowState.getDisplayContent();
            if (displayContent == null || !displayContent.isDefaultDisplay) {
                if (displayContent != null) {
                    if (this.mObscureApplicationContentOnSecondaryDisplays) {
                        if (z) {
                        }
                    }
                }
                if ((262144 & i2) != 0) {
                    this.mSustainedPerformanceModeCurrent = true;
                }
            } else if (windowState.isDreamWindow() || this.mWmService.mPolicy.isKeyguardShowing()) {
                this.mObscureApplicationContentOnSecondaryDisplays = true;
            }
            z3 = true;
            if ((262144 & i2) != 0) {
            }
        }
        return z3;
    }

    public boolean updateRotationUnchecked() {
        boolean z = false;
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if (((DisplayContent) this.mChildren.get(size)).getDisplayRotation().updateRotationAndSendNewConfigIfChanged()) {
                z = true;
            }
        }
        return z;
    }

    public boolean copyAnimToLayoutParams() {
        boolean z;
        WindowManagerService windowManagerService = this.mWmService;
        WindowAnimator windowAnimator = windowManagerService.mAnimator;
        int i = windowAnimator.mBulkUpdateParams;
        if ((i & 1) != 0) {
            this.mUpdateRotation = true;
            z = true;
        } else {
            z = false;
        }
        if (this.mOrientationChangeComplete) {
            this.mLastWindowFreezeSource = windowAnimator.mLastWindowFreezeSource;
            if (windowManagerService.mWindowsFreezingScreen != 0) {
                z = true;
            }
        }
        if ((i & 2) != 0) {
            this.mWallpaperActionPending = true;
        }
        return z;
    }

    /* renamed from: com.android.server.wm.RootWindowContainer$MyHandler */
    /* loaded from: classes2.dex */
    public final class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                RootWindowContainer.this.mWmService.mPowerManagerInternal.setScreenBrightnessOverrideFromWindowManager(Float.intBitsToFloat(message.arg1));
            } else if (i != 2) {
            } else {
                RootWindowContainer.this.mWmService.mPowerManagerInternal.setUserActivityTimeoutOverrideFromWindowManager(((Long) message.obj).longValue());
            }
        }
    }

    public void dumpDisplayContents(PrintWriter printWriter) {
        printWriter.println("WINDOW MANAGER DISPLAY CONTENTS (dumpsys window displays)");
        if (this.mWmService.mDisplayReady) {
            int size = this.mChildren.size();
            for (int i = 0; i < size; i++) {
                ((DisplayContent) this.mChildren.get(i)).dump(printWriter, "  ", true);
            }
            return;
        }
        printWriter.println("  NO DISPLAY");
    }

    public void dumpTopFocusedDisplayId(PrintWriter printWriter) {
        printWriter.print("  mTopFocusedDisplayId=");
        printWriter.println(this.mTopFocusedDisplayId);
    }

    public void dumpLayoutNeededDisplayIds(PrintWriter printWriter) {
        if (isLayoutNeeded()) {
            printWriter.print("  mLayoutNeeded on displays=");
            int size = this.mChildren.size();
            for (int i = 0; i < size; i++) {
                DisplayContent displayContent = (DisplayContent) this.mChildren.get(i);
                if (displayContent.isLayoutNeeded()) {
                    printWriter.print(displayContent.getDisplayId());
                }
            }
            printWriter.println();
        }
    }

    public void dumpWindowsNoHeader(final PrintWriter printWriter, final boolean z, final ArrayList<WindowState> arrayList) {
        final int[] iArr = new int[1];
        forAllWindows(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda49
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$dumpWindowsNoHeader$9(arrayList, printWriter, iArr, z, (WindowState) obj);
            }
        }, true);
    }

    public static /* synthetic */ void lambda$dumpWindowsNoHeader$9(ArrayList arrayList, PrintWriter printWriter, int[] iArr, boolean z, WindowState windowState) {
        if (arrayList == null || arrayList.contains(windowState)) {
            printWriter.println("  Window #" + iArr[0] + " " + windowState + XmlUtils.STRING_ARRAY_SEPARATOR);
            windowState.dump(printWriter, "    ", z || arrayList != null);
            iArr[0] = iArr[0] + 1;
        }
    }

    public void dumpTokens(PrintWriter printWriter, boolean z) {
        printWriter.println("  All tokens:");
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            ((DisplayContent) this.mChildren.get(size)).dumpTokens(printWriter, z);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j, int i) {
        if (i != 2 || isVisible()) {
            long start = protoOutputStream.start(j);
            super.dumpDebug(protoOutputStream, 1146756268033L, i);
            this.mTaskSupervisor.getKeyguardController().dumpDebug(protoOutputStream, 1146756268037L);
            protoOutputStream.write(1133871366150L, this.mTaskSupervisor.mRecentTasks.isRecentsComponentHomeActivity(this.mCurrentUser));
            protoOutputStream.end(start);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void removeChild(DisplayContent displayContent) {
        super.removeChild((RootWindowContainer) displayContent);
        if (this.mTopFocusedDisplayId == displayContent.getDisplayId()) {
            this.mWmService.updateFocusedWindowLocked(0, true);
        }
    }

    public void forAllDisplays(Consumer<DisplayContent> consumer) {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            consumer.accept((DisplayContent) this.mChildren.get(size));
        }
    }

    public void forAllDisplayPolicies(Consumer<DisplayPolicy> consumer) {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            consumer.accept(((DisplayContent) this.mChildren.get(size)).getDisplayPolicy());
        }
    }

    public WindowState getCurrentInputMethodWindow() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            WindowState windowState = ((DisplayContent) this.mChildren.get(size)).mInputMethodWindow;
            if (windowState != null) {
                return windowState;
            }
        }
        return null;
    }

    public void getDisplayContextsWithNonToastVisibleWindows(final int i, List<Context> list) {
        if (list == null) {
            return;
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            DisplayContent displayContent = (DisplayContent) this.mChildren.get(size);
            if (displayContent.getWindow(new Predicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda46
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getDisplayContextsWithNonToastVisibleWindows$10;
                    lambda$getDisplayContextsWithNonToastVisibleWindows$10 = RootWindowContainer.lambda$getDisplayContextsWithNonToastVisibleWindows$10(i, (WindowState) obj);
                    return lambda$getDisplayContextsWithNonToastVisibleWindows$10;
                }
            }) != null) {
                list.add(displayContent.getDisplayUiContext());
            }
        }
    }

    public static /* synthetic */ boolean lambda$getDisplayContextsWithNonToastVisibleWindows$10(int i, WindowState windowState) {
        return i == windowState.mSession.mPid && windowState.isVisibleNow() && windowState.mAttrs.type != 2005;
    }

    public Context getDisplayUiContext(int i) {
        if (getDisplayContent(i) != null) {
            return getDisplayContent(i).getDisplayUiContext();
        }
        return null;
    }

    public void setWindowManager(WindowManagerService windowManagerService) {
        this.mWindowManager = windowManagerService;
        DisplayManager displayManager = (DisplayManager) this.mService.mContext.getSystemService(DisplayManager.class);
        this.mDisplayManager = displayManager;
        displayManager.registerDisplayListener(this, this.mService.mUiHandler);
        this.mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
        for (Display display : this.mDisplayManager.getDisplays()) {
            DisplayContent displayContent = new DisplayContent(display, this, this.mDeviceStateController);
            addChild((RootWindowContainer) displayContent, Integer.MIN_VALUE);
            if (displayContent.mDisplayId == 0) {
                this.mDefaultDisplay = displayContent;
            }
        }
        TaskDisplayArea defaultTaskDisplayArea = getDefaultTaskDisplayArea();
        defaultTaskDisplayArea.getOrCreateRootHomeTask(true);
        positionChildAt(Integer.MAX_VALUE, defaultTaskDisplayArea.mDisplayContent, false);
    }

    public DisplayContent getDefaultDisplay() {
        return this.mDefaultDisplay;
    }

    public DisplayRotationCoordinator getDisplayRotationCoordinator() {
        return this.mDisplayRotationCoordinator;
    }

    public TaskDisplayArea getDefaultTaskDisplayArea() {
        return this.mDefaultDisplay.getDefaultTaskDisplayArea();
    }

    public DisplayContent getDisplayContent(String str) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            DisplayContent childAt = getChildAt(childCount);
            if (childAt.mDisplay.isValid() && childAt.mDisplay.getUniqueId().equals(str)) {
                return childAt;
            }
        }
        return null;
    }

    public DisplayContent getDisplayContent(int i) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            DisplayContent childAt = getChildAt(childCount);
            if (childAt.mDisplayId == i) {
                return childAt;
            }
        }
        return null;
    }

    public DisplayContent getDisplayContentOrCreate(int i) {
        Display display;
        DisplayContent displayContent = getDisplayContent(i);
        if (displayContent != null) {
            return displayContent;
        }
        DisplayManager displayManager = this.mDisplayManager;
        if (displayManager == null || (display = displayManager.getDisplay(i)) == null) {
            return null;
        }
        DisplayContent displayContent2 = new DisplayContent(display, this, this.mDeviceStateController);
        addChild((RootWindowContainer) displayContent2, Integer.MIN_VALUE);
        return displayContent2;
    }

    public ActivityRecord getDefaultDisplayHomeActivityForUser(int i) {
        return getDefaultTaskDisplayArea().getHomeActivityForUser(i);
    }

    public boolean startHomeOnAllDisplays(int i, String str) {
        boolean z = false;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            z |= startHomeOnDisplay(i, str, getChildAt(childCount).mDisplayId);
        }
        return z;
    }

    public void startHomeOnEmptyDisplays(final String str) {
        forAllTaskDisplayAreas(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda43
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.this.lambda$startHomeOnEmptyDisplays$11(str, (TaskDisplayArea) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startHomeOnEmptyDisplays$11(String str, TaskDisplayArea taskDisplayArea) {
        if (taskDisplayArea.topRunningActivity() == null) {
            startHomeOnTaskDisplayArea(this.mCurrentUser, str, taskDisplayArea, false, false);
        }
    }

    public boolean startHomeOnDisplay(int i, String str, int i2) {
        return startHomeOnDisplay(i, str, i2, false, false);
    }

    public boolean startHomeOnDisplay(final int i, final String str, int i2, final boolean z, final boolean z2) {
        if (i2 == -1) {
            Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
            i2 = topDisplayFocusedRootTask != null ? topDisplayFocusedRootTask.getDisplayId() : 0;
        }
        return ((Boolean) getDisplayContent(i2).reduceOnAllTaskDisplayAreas(new BiFunction() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda7
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                Boolean lambda$startHomeOnDisplay$12;
                lambda$startHomeOnDisplay$12 = RootWindowContainer.this.lambda$startHomeOnDisplay$12(i, str, z, z2, (TaskDisplayArea) obj, (Boolean) obj2);
                return lambda$startHomeOnDisplay$12;
            }
        }, Boolean.FALSE)).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$startHomeOnDisplay$12(int i, String str, boolean z, boolean z2, TaskDisplayArea taskDisplayArea, Boolean bool) {
        return Boolean.valueOf(startHomeOnTaskDisplayArea(i, str, taskDisplayArea, z, z2) | bool.booleanValue());
    }

    public boolean startHomeOnTaskDisplayArea(int i, String str, TaskDisplayArea taskDisplayArea, boolean z, boolean z2) {
        Intent intent;
        ActivityInfo activityInfo;
        if (taskDisplayArea == null) {
            Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
            if (topDisplayFocusedRootTask != null) {
                taskDisplayArea = topDisplayFocusedRootTask.getDisplayArea();
            } else {
                taskDisplayArea = getDefaultTaskDisplayArea();
            }
        }
        if (taskDisplayArea == getDefaultTaskDisplayArea()) {
            intent = this.mService.getHomeIntent();
            activityInfo = resolveHomeActivity(i, intent);
        } else if (shouldPlaceSecondaryHomeOnDisplayArea(taskDisplayArea)) {
            Pair<ActivityInfo, Intent> resolveSecondaryHomeActivity = resolveSecondaryHomeActivity(i, taskDisplayArea);
            activityInfo = (ActivityInfo) resolveSecondaryHomeActivity.first;
            intent = (Intent) resolveSecondaryHomeActivity.second;
        } else {
            intent = null;
            activityInfo = null;
        }
        if (activityInfo == null || intent == null || !canStartHomeOnDisplayArea(activityInfo, taskDisplayArea, z)) {
            return false;
        }
        intent.setComponent(new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name));
        intent.setFlags(intent.getFlags() | 268435456);
        if (z2) {
            intent.putExtra("android.intent.extra.FROM_HOME_KEY", true);
            if (this.mWindowManager.getRecentsAnimationController() != null) {
                this.mWindowManager.getRecentsAnimationController().cancelAnimationForHomeStart();
            }
        }
        intent.putExtra("android.intent.extra.EXTRA_START_REASON", str);
        this.mService.getActivityStartController().startHomeActivity(intent, activityInfo, str + XmlUtils.STRING_ARRAY_SEPARATOR + i + XmlUtils.STRING_ARRAY_SEPARATOR + UserHandle.getUserId(activityInfo.applicationInfo.uid) + XmlUtils.STRING_ARRAY_SEPARATOR + taskDisplayArea.getDisplayId(), taskDisplayArea);
        return true;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:9:0x0035 -> B:10:0x0036). Please submit an issue!!! */
    @VisibleForTesting
    public ActivityInfo resolveHomeActivity(int i, Intent intent) {
        ActivityInfo activityInfo;
        ComponentName component = intent.getComponent();
        if (component != null) {
            activityInfo = AppGlobals.getPackageManager().getActivityInfo(component, 1024L, i);
        } else {
            ResolveInfo resolveIntent = this.mTaskSupervisor.resolveIntent(intent, intent.resolveTypeIfNeeded(this.mService.mContext.getContentResolver()), i, 1024, Binder.getCallingUid(), Binder.getCallingPid());
            if (resolveIntent != null) {
                activityInfo = resolveIntent.activityInfo;
            }
            activityInfo = null;
        }
        if (activityInfo == null) {
            Slogf.wtf(StartingSurfaceController.TAG, new Exception(), "No home screen found for %s and user %d", intent, Integer.valueOf(i));
            return null;
        }
        ActivityInfo activityInfo2 = new ActivityInfo(activityInfo);
        activityInfo2.applicationInfo = this.mService.getAppInfoForUser(activityInfo2.applicationInfo, i);
        return activityInfo2;
    }

    @VisibleForTesting
    public Pair<ActivityInfo, Intent> resolveSecondaryHomeActivity(int i, TaskDisplayArea taskDisplayArea) {
        if (taskDisplayArea == getDefaultTaskDisplayArea()) {
            throw new IllegalArgumentException("resolveSecondaryHomeActivity: Should not be default task container");
        }
        Intent homeIntent = this.mService.getHomeIntent();
        ActivityInfo resolveHomeActivity = resolveHomeActivity(i, homeIntent);
        if (resolveHomeActivity != null) {
            if (ResolverActivity.class.getName().equals(resolveHomeActivity.name)) {
                resolveHomeActivity = null;
            } else {
                homeIntent = this.mService.getSecondaryHomeIntent(resolveHomeActivity.applicationInfo.packageName);
                List<ResolveInfo> resolveActivities = resolveActivities(i, homeIntent);
                int size = resolveActivities.size();
                String str = resolveHomeActivity.name;
                int i2 = 0;
                while (true) {
                    if (i2 >= size) {
                        resolveHomeActivity = null;
                        break;
                    }
                    ResolveInfo resolveInfo = resolveActivities.get(i2);
                    if (resolveInfo.activityInfo.name.equals(str)) {
                        resolveHomeActivity = resolveInfo.activityInfo;
                        break;
                    }
                    i2++;
                }
                if (resolveHomeActivity == null && size > 0) {
                    resolveHomeActivity = resolveActivities.get(0).activityInfo;
                }
            }
        }
        if (resolveHomeActivity != null && !canStartHomeOnDisplayArea(resolveHomeActivity, taskDisplayArea, false)) {
            resolveHomeActivity = null;
        }
        if (resolveHomeActivity == null) {
            homeIntent = this.mService.getSecondaryHomeIntent(null);
            resolveHomeActivity = resolveHomeActivity(i, homeIntent);
        }
        return Pair.create(resolveHomeActivity, homeIntent);
    }

    @VisibleForTesting
    public List<ResolveInfo> resolveActivities(int i, Intent intent) {
        try {
            return AppGlobals.getPackageManager().queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mService.mContext.getContentResolver()), 1024L, i).getList();
        } catch (RemoteException unused) {
            return new ArrayList();
        }
    }

    public boolean resumeHomeActivity(ActivityRecord activityRecord, String str, TaskDisplayArea taskDisplayArea) {
        if (this.mService.isBooting() || this.mService.isBooted()) {
            if (taskDisplayArea == null) {
                taskDisplayArea = getDefaultTaskDisplayArea();
            }
            TaskDisplayArea taskDisplayArea2 = taskDisplayArea;
            ActivityRecord homeActivity = taskDisplayArea2.getHomeActivity();
            String str2 = str + " resumeHomeActivity";
            if (homeActivity != null && !homeActivity.finishing) {
                homeActivity.moveFocusableActivityToTop(str2);
                return resumeFocusedTasksTopActivities(homeActivity.getRootTask(), activityRecord, null);
            }
            return startHomeOnTaskDisplayArea(this.mCurrentUser, str2, taskDisplayArea2, false, false);
        }
        return false;
    }

    public boolean shouldPlaceSecondaryHomeOnDisplayArea(TaskDisplayArea taskDisplayArea) {
        DisplayContent displayContent;
        if (getDefaultTaskDisplayArea() == taskDisplayArea) {
            throw new IllegalArgumentException("shouldPlaceSecondaryHomeOnDisplay: Should not be on default task container");
        }
        if (taskDisplayArea != null && taskDisplayArea.canHostHomeTask()) {
            if (taskDisplayArea.getDisplayId() == 0 || this.mService.mSupportsMultiDisplay) {
                return (Settings.Global.getInt(this.mService.mContext.getContentResolver(), "device_provisioned", 0) != 0) && StorageManager.isUserKeyUnlocked(this.mCurrentUser) && (displayContent = taskDisplayArea.getDisplayContent()) != null && !displayContent.isRemoved() && displayContent.supportsSystemDecorations();
            }
            return false;
        }
        return false;
    }

    public boolean canStartHomeOnDisplayArea(ActivityInfo activityInfo, TaskDisplayArea taskDisplayArea, boolean z) {
        ActivityTaskManagerService activityTaskManagerService = this.mService;
        if (activityTaskManagerService.mFactoryTest == 1 && activityTaskManagerService.mTopAction == null) {
            return false;
        }
        WindowProcessController processController = activityTaskManagerService.getProcessController(activityInfo.processName, activityInfo.applicationInfo.uid);
        if (z || processController == null || !processController.isInstrumenting()) {
            int displayId = taskDisplayArea != null ? taskDisplayArea.getDisplayId() : -1;
            if (displayId != 0 && (displayId == -1 || displayId != this.mService.mVr2dDisplayId)) {
                if (!shouldPlaceSecondaryHomeOnDisplayArea(taskDisplayArea)) {
                    return false;
                }
                int i = activityInfo.launchMode;
                if (!((i == 2 || i == 3) ? false : true)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean ensureVisibilityAndConfig(ActivityRecord activityRecord, int i, boolean z, boolean z2) {
        ensureActivitiesVisible(null, 0, false, false);
        if (i == -1) {
            return true;
        }
        DisplayContent displayContent = getDisplayContent(i);
        Configuration updateOrientation = displayContent != null ? displayContent.updateOrientation(activityRecord, true) : null;
        if (activityRecord != null) {
            activityRecord.reportDescendantOrientationChangeIfNeeded();
        }
        if (activityRecord != null && z && updateOrientation != null) {
            activityRecord.frozenBeforeDestroy = true;
        }
        if (displayContent != null) {
            return displayContent.updateDisplayOverrideConfigurationLocked(updateOrientation, activityRecord, z2, null);
        }
        return true;
    }

    public List<ActivityAssistInfo> getTopVisibleActivities() {
        final ArrayList arrayList = new ArrayList();
        final Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
        forAllRootTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$getTopVisibleActivities$13(Task.this, arrayList, (Task) obj);
            }
        });
        return arrayList;
    }

    public static /* synthetic */ void lambda$getTopVisibleActivities$13(Task task, ArrayList arrayList, Task task2) {
        ActivityRecord topNonFinishingActivity;
        if (!task2.shouldBeVisible(null) || (topNonFinishingActivity = task2.getTopNonFinishingActivity()) == null) {
            return;
        }
        ActivityAssistInfo activityAssistInfo = new ActivityAssistInfo(topNonFinishingActivity);
        if (task2 == task) {
            arrayList.add(0, activityAssistInfo);
        } else {
            arrayList.add(activityAssistInfo);
        }
    }

    public Task getTopDisplayFocusedRootTask() {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            Task focusedRootTask = getChildAt(childCount).getFocusedRootTask();
            if (focusedRootTask != null) {
                return focusedRootTask;
            }
        }
        return null;
    }

    public ActivityRecord getTopResumedActivity() {
        Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
        if (topDisplayFocusedRootTask == null) {
            return null;
        }
        ActivityRecord topResumedActivity = topDisplayFocusedRootTask.getTopResumedActivity();
        return (topResumedActivity == null || topResumedActivity.app == null) ? (ActivityRecord) getItemFromTaskDisplayAreas(new Function() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((TaskDisplayArea) obj).getFocusedActivity();
            }
        }) : topResumedActivity;
    }

    public boolean isTopDisplayFocusedRootTask(Task task) {
        return task != null && task == getTopDisplayFocusedRootTask();
    }

    public boolean attachApplication(WindowProcessController windowProcessController) throws RemoteException {
        try {
            return this.mAttachApplicationHelper.process(windowProcessController);
        } finally {
            this.mAttachApplicationHelper.reset();
        }
    }

    public void ensureActivitiesVisible(ActivityRecord activityRecord, int i, boolean z) {
        ensureActivitiesVisible(activityRecord, i, z, true);
    }

    public void ensureActivitiesVisible(ActivityRecord activityRecord, int i, boolean z, boolean z2) {
        if (this.mTaskSupervisor.inActivityVisibilityUpdate() || this.mTaskSupervisor.isRootVisibilityUpdateDeferred()) {
            return;
        }
        try {
            this.mTaskSupervisor.beginActivityVisibilityUpdate();
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                getChildAt(childCount).ensureActivitiesVisible(activityRecord, i, z, z2);
            }
        } finally {
            this.mTaskSupervisor.endActivityVisibilityUpdate();
        }
    }

    public boolean switchUser(final int i, UserState userState) {
        Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
        int rootTaskId = topDisplayFocusedRootTask != null ? topDisplayFocusedRootTask.getRootTaskId() : -1;
        removeRootTasksInWindowingModes(2);
        this.mUserRootTaskInFront.put(this.mCurrentUser, rootTaskId);
        this.mCurrentUser = i;
        this.mTaskSupervisor.mStartingUsers.add(userState);
        forAllRootTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda28
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Task) obj).switchUser(i);
            }
        });
        Task rootTask = getRootTask(this.mUserRootTaskInFront.get(i));
        if (rootTask == null) {
            rootTask = getDefaultTaskDisplayArea().getOrCreateRootHomeTask();
        }
        boolean isActivityTypeHome = rootTask.isActivityTypeHome();
        if (rootTask.isOnHomeDisplay()) {
            rootTask.moveToFront("switchUserOnHomeDisplay");
        } else {
            resumeHomeActivity(null, "switchUserOnOtherDisplay", getDefaultTaskDisplayArea());
        }
        return isActivityTypeHome;
    }

    public void removeUser(int i) {
        this.mUserRootTaskInFront.delete(i);
    }

    public void updateUserRootTask(int i, Task task) {
        if (i != this.mCurrentUser) {
            if (task == null) {
                task = getDefaultTaskDisplayArea().getOrCreateRootHomeTask();
            }
            this.mUserRootTaskInFront.put(i, task.getRootTaskId());
        }
    }

    public void moveRootTaskToTaskDisplayArea(int i, TaskDisplayArea taskDisplayArea, boolean z) {
        Task rootTask = getRootTask(i);
        if (rootTask == null) {
            throw new IllegalArgumentException("moveRootTaskToTaskDisplayArea: Unknown rootTaskId=" + i);
        }
        TaskDisplayArea displayArea = rootTask.getDisplayArea();
        if (displayArea == null) {
            throw new IllegalStateException("moveRootTaskToTaskDisplayArea: rootTask=" + rootTask + " is not attached to any task display area.");
        } else if (taskDisplayArea == null) {
            throw new IllegalArgumentException("moveRootTaskToTaskDisplayArea: Unknown taskDisplayArea=" + taskDisplayArea);
        } else if (displayArea == taskDisplayArea) {
            throw new IllegalArgumentException("Trying to move rootTask=" + rootTask + " to its current taskDisplayArea=" + taskDisplayArea);
        } else {
            rootTask.reparent(taskDisplayArea, z);
            rootTask.resumeNextFocusAfterReparent();
        }
    }

    public void moveRootTaskToDisplay(int i, int i2, boolean z) {
        DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i2);
        if (displayContentOrCreate == null) {
            throw new IllegalArgumentException("moveRootTaskToDisplay: Unknown displayId=" + i2);
        }
        moveRootTaskToTaskDisplayArea(i, displayContentOrCreate.getDefaultTaskDisplayArea(), z);
    }

    public void moveActivityToPinnedRootTask(ActivityRecord activityRecord, ActivityRecord activityRecord2, String str) {
        moveActivityToPinnedRootTask(activityRecord, activityRecord2, str, null);
    }

    public void moveActivityToPinnedRootTask(ActivityRecord activityRecord, ActivityRecord activityRecord2, String str, Transition transition) {
        Task build;
        TaskDisplayArea displayArea = activityRecord.getDisplayArea();
        Task task = activityRecord.getTask();
        TransitionController transitionController = task.mTransitionController;
        if (transition == null && !transitionController.isCollecting() && transitionController.getTransitionPlayer() != null) {
            transition = transitionController.createTransition(10);
        }
        transitionController.deferTransitionReady();
        this.mService.deferWindowLayout();
        try {
            Task rootPinnedTask = displayArea.getRootPinnedTask();
            if (rootPinnedTask != null) {
                transitionController.collect(rootPinnedTask);
                removeRootTasksInWindowingModes(2);
            }
            activityRecord.getDisplayContent().prepareAppTransition(0);
            transitionController.collect(task);
            activityRecord.setWindowingMode(activityRecord.getWindowingMode());
            activityRecord.mWaitForEnteringPinnedMode = true;
            TaskFragment organizedTaskFragment = activityRecord.getOrganizedTaskFragment();
            if (task.getNonFinishingActivityCount() == 1) {
                task.maybeApplyLastRecentsAnimationTransaction();
                if (task.getParent() != displayArea) {
                    task.reparent(displayArea, true);
                }
                task.forAllTaskFragments(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda22
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        RootWindowContainer.lambda$moveActivityToPinnedRootTask$15((TaskFragment) obj);
                    }
                });
                build = task;
            } else {
                build = new Task.Builder(this.mService).setActivityType(activityRecord.getActivityType()).setOnTop(true).setActivityInfo(activityRecord.info).setParent(displayArea).setIntent(activityRecord.intent).setDeferTaskAppear(true).setHasBeenVisible(true).setWindowingMode(task.getRequestedOverrideWindowingMode()).build();
                activityRecord.setLastParentBeforePip(activityRecord2);
                build.setLastNonFullscreenBounds(task.mLastNonFullscreenBounds);
                build.setBoundsUnchecked(activityRecord.getTaskFragment().getBounds());
                PictureInPictureSurfaceTransaction pictureInPictureSurfaceTransaction = task.mLastRecentsAnimationTransaction;
                if (pictureInPictureSurfaceTransaction != null) {
                    build.setLastRecentsAnimationTransaction(pictureInPictureSurfaceTransaction, task.mLastRecentsAnimationOverlay);
                    task.clearLastRecentsAnimationTransaction(false);
                } else {
                    task.resetSurfaceControlTransforms();
                }
                if (organizedTaskFragment != null && organizedTaskFragment.getNonFinishingActivityCount() == 1 && organizedTaskFragment.getTopNonFinishingActivity() == activityRecord) {
                    organizedTaskFragment.mClearedTaskFragmentForPip = true;
                }
                transitionController.collect(build);
                if (transitionController.isShellTransitionsEnabled()) {
                    build.setWindowingMode(2);
                }
                activityRecord.reparent(build, Integer.MAX_VALUE, str);
                build.maybeApplyLastRecentsAnimationTransaction();
                ActivityRecord topMostActivity = task.getTopMostActivity();
                if (topMostActivity != null && topMostActivity.isState(ActivityRecord.State.STOPPED) && task.getDisplayContent().mAppTransition.containsTransitRequest(4)) {
                    task.getDisplayContent().mClosingApps.add(topMostActivity);
                    topMostActivity.mRequestForceTransition = true;
                }
            }
            build.setWindowingMode(2);
            if (activityRecord.getOptions() != null && activityRecord.getOptions().isLaunchIntoPip()) {
                this.mWindowManager.mTaskSnapshotController.recordSnapshot(task, false);
                build.setBounds(activityRecord.pictureInPictureArgs.getSourceRectHint());
            }
            build.setDeferTaskAppear(false);
            activityRecord.supportsEnterPipOnTaskSwitch = false;
            if (organizedTaskFragment != null && organizedTaskFragment.mClearedTaskFragmentForPip && organizedTaskFragment.isTaskVisibleRequested()) {
                this.mService.mTaskFragmentOrganizerController.dispatchPendingInfoChangedEvent(organizedTaskFragment);
            }
            this.mService.continueWindowLayout();
            try {
                ensureActivitiesVisible(null, 0, false);
                if (transition != null) {
                    transitionController.requestStartTransition(transition, build, null, null);
                    transition.setReady(build, true);
                }
                resumeFocusedTasksTopActivities();
                notifyActivityPipModeChanged(activityRecord.getTask(), activityRecord);
            } finally {
            }
        } catch (Throwable th) {
            this.mService.continueWindowLayout();
            try {
                ensureActivitiesVisible(null, 0, false);
                throw th;
            } finally {
            }
        }
    }

    public static /* synthetic */ void lambda$moveActivityToPinnedRootTask$15(TaskFragment taskFragment) {
        if (taskFragment.isOrganizedTaskFragment()) {
            taskFragment.resetAdjacentTaskFragment();
            taskFragment.setCompanionTaskFragment(null);
            taskFragment.setAnimationParams(TaskFragmentAnimationParams.DEFAULT);
            if (taskFragment.getTopNonFinishingActivity() != null) {
                taskFragment.setRelativeEmbeddedBounds(new Rect());
                taskFragment.updateRequestedOverrideConfiguration(Configuration.EMPTY);
            }
        }
    }

    public void notifyActivityPipModeChanged(Task task, ActivityRecord activityRecord) {
        boolean z = activityRecord != null;
        if (z) {
            this.mService.getTaskChangeNotificationController().notifyActivityPinned(activityRecord);
        } else {
            this.mService.getTaskChangeNotificationController().notifyActivityUnpinned();
        }
        this.mWindowManager.mPolicy.setPipVisibilityLw(z);
        this.mWmService.mTransactionFactory.get().setTrustedOverlay(task.getSurfaceControl(), z).apply();
    }

    public void executeAppTransitionForAllDisplay() {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            getChildAt(childCount).mDisplayContent.executeAppTransition();
        }
    }

    public ActivityRecord findTask(ActivityRecord activityRecord, TaskDisplayArea taskDisplayArea) {
        return findTask(activityRecord.getActivityType(), activityRecord.taskAffinity, activityRecord.intent, activityRecord.info, taskDisplayArea);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x003b, code lost:
        if (r10 != null) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ActivityRecord findTask(int i, String str, Intent intent, ActivityInfo activityInfo, final TaskDisplayArea taskDisplayArea) {
        ActivityRecord activityRecord;
        if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1559645910, 0, (String) null, new Object[]{String.valueOf(i), String.valueOf(str), String.valueOf(intent), String.valueOf(activityInfo), String.valueOf(taskDisplayArea)});
        }
        this.mTmpFindTaskResult.init(i, str, intent, activityInfo);
        if (taskDisplayArea != null) {
            this.mTmpFindTaskResult.process(taskDisplayArea);
            FindTaskResult findTaskResult = this.mTmpFindTaskResult;
            ActivityRecord activityRecord2 = findTaskResult.mIdealRecord;
            if (activityRecord2 != null) {
                return activityRecord2;
            }
            activityRecord = findTaskResult.mCandidateRecord;
        }
        activityRecord = null;
        ActivityRecord activityRecord3 = (ActivityRecord) getItemFromTaskDisplayAreas(new Function() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ActivityRecord lambda$findTask$16;
                lambda$findTask$16 = RootWindowContainer.this.lambda$findTask$16(taskDisplayArea, (TaskDisplayArea) obj);
                return lambda$findTask$16;
            }
        });
        if (activityRecord3 != null) {
            return activityRecord3;
        }
        if (ProtoLogGroup.WM_DEBUG_TASKS.isEnabled() && activityRecord == null && ProtoLogCache.WM_DEBUG_TASKS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1376035390, 0, (String) null, (Object[]) null);
        }
        return activityRecord;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ActivityRecord lambda$findTask$16(TaskDisplayArea taskDisplayArea, TaskDisplayArea taskDisplayArea2) {
        if (taskDisplayArea2 == taskDisplayArea) {
            return null;
        }
        this.mTmpFindTaskResult.process(taskDisplayArea2);
        ActivityRecord activityRecord = this.mTmpFindTaskResult.mIdealRecord;
        if (activityRecord != null) {
            return activityRecord;
        }
        return null;
    }

    public int finishTopCrashedActivities(final WindowProcessController windowProcessController, final String str) {
        final Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
        final Task[] taskArr = new Task[1];
        forAllTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda30
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$finishTopCrashedActivities$17(WindowProcessController.this, str, topDisplayFocusedRootTask, taskArr, (Task) obj);
            }
        });
        Task task = taskArr[0];
        if (task != null) {
            return task.mTaskId;
        }
        return -1;
    }

    public static /* synthetic */ void lambda$finishTopCrashedActivities$17(WindowProcessController windowProcessController, String str, Task task, Task[] taskArr, Task task2) {
        Task finishTopCrashedActivityLocked = task2.finishTopCrashedActivityLocked(windowProcessController, str);
        if (task2 == task || taskArr[0] == null) {
            taskArr[0] = finishTopCrashedActivityLocked;
        }
    }

    public boolean resumeFocusedTasksTopActivities() {
        return resumeFocusedTasksTopActivities(null, null, null);
    }

    public boolean resumeFocusedTasksTopActivities(Task task, ActivityRecord activityRecord, ActivityOptions activityOptions) {
        return resumeFocusedTasksTopActivities(task, activityRecord, activityOptions, false);
    }

    public boolean resumeFocusedTasksTopActivities(final Task task, final ActivityRecord activityRecord, final ActivityOptions activityOptions, boolean z) {
        boolean resumeHomeActivity;
        if (this.mTaskSupervisor.readyToResume()) {
            boolean resumeTopActivityUncheckedLocked = (task == null || !(task.isTopRootTaskInDisplayArea() || getTopDisplayFocusedRootTask() == task)) ? false : task.resumeTopActivityUncheckedLocked(activityRecord, activityOptions, z);
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                DisplayContent childAt = getChildAt(childCount);
                final boolean[] zArr = new boolean[1];
                final boolean z2 = resumeTopActivityUncheckedLocked;
                childAt.forAllRootTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda14
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        RootWindowContainer.lambda$resumeFocusedTasksTopActivities$18(Task.this, zArr, z2, activityOptions, activityRecord, (Task) obj);
                    }
                });
                boolean z3 = zArr[0];
                boolean z4 = resumeTopActivityUncheckedLocked | z3;
                if (!z3) {
                    Task focusedRootTask = childAt.getFocusedRootTask();
                    if (focusedRootTask != null) {
                        resumeHomeActivity = focusedRootTask.resumeTopActivityUncheckedLocked(activityRecord, activityOptions);
                    } else if (task == null) {
                        resumeHomeActivity = resumeHomeActivity(null, "no-focusable-task", childAt.getDefaultTaskDisplayArea());
                    }
                    resumeTopActivityUncheckedLocked = resumeHomeActivity | z4;
                }
                resumeTopActivityUncheckedLocked = z4;
            }
            return resumeTopActivityUncheckedLocked;
        }
        return false;
    }

    public static /* synthetic */ void lambda$resumeFocusedTasksTopActivities$18(Task task, boolean[] zArr, boolean z, ActivityOptions activityOptions, ActivityRecord activityRecord, Task task2) {
        ActivityRecord activityRecord2 = task2.topRunningActivity();
        if (!task2.isFocusableAndVisible() || activityRecord2 == null) {
            return;
        }
        if (task2 == task) {
            zArr[0] = zArr[0] | z;
        } else if (task2.getDisplayArea().isTopRootTask(task2) && activityRecord2.isState(ActivityRecord.State.RESUMED)) {
            task2.executeAppTransition(activityOptions);
        } else {
            zArr[0] = zArr[0] | activityRecord2.makeActiveIfNeeded(activityRecord);
        }
    }

    public void applySleepTokens(boolean z) {
        boolean z2 = false;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            final DisplayContent childAt = getChildAt(childCount);
            final boolean shouldSleep = childAt.shouldSleep();
            if (shouldSleep != childAt.isSleeping()) {
                childAt.setIsSleeping(shouldSleep);
                if (childAt.mTransitionController.isShellTransitionsEnabled() && !z2 && shouldSleep && !childAt.mAllSleepTokens.isEmpty()) {
                    final Transition transition = new Transition(12, 0, childAt.mTransitionController, this.mWmService.mSyncEngine);
                    Runnable runnable = new Runnable() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda11
                        @Override // java.lang.Runnable
                        public final void run() {
                            RootWindowContainer.lambda$applySleepTokens$19(DisplayContent.this, transition);
                        }
                    };
                    if (childAt.mTransitionController.isCollecting()) {
                        this.mWmService.mSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda12
                            @Override // java.lang.Runnable
                            public final void run() {
                                RootWindowContainer.lambda$applySleepTokens$20(DisplayContent.this, transition);
                            }
                        }, runnable);
                    } else {
                        childAt.mTransitionController.moveToCollecting(transition);
                        runnable.run();
                    }
                    z2 = true;
                }
                if (z) {
                    if (!shouldSleep && childAt.isDefaultDisplay && !childAt.getDisplayPolicy().isAwake() && childAt.mTransitionController.isShellTransitionsEnabled() && !childAt.mTransitionController.isCollecting()) {
                        childAt.mTransitionController.requestTransitionIfNeeded(11, 0, null, childAt);
                    }
                    childAt.forAllRootTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda13
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            RootWindowContainer.this.lambda$applySleepTokens$22(shouldSleep, childAt, (Task) obj);
                        }
                    });
                }
            }
        }
    }

    public static /* synthetic */ void lambda$applySleepTokens$19(DisplayContent displayContent, Transition transition) {
        displayContent.mTransitionController.requestStartTransition(transition, null, null, null);
        transition.playNow();
    }

    public static /* synthetic */ void lambda$applySleepTokens$20(DisplayContent displayContent, Transition transition) {
        displayContent.mTransitionController.moveToCollecting(transition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applySleepTokens$22(boolean z, DisplayContent displayContent, Task task) {
        if (z) {
            task.goToSleepIfPossible(false);
            return;
        }
        task.forAllLeafTasksAndLeafTaskFragments(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda31
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TaskFragment) obj).awakeFromSleeping();
            }
        }, true);
        if (task.isFocusedRootTaskOnDisplay() && !this.mTaskSupervisor.getKeyguardController().isKeyguardOrAodShowing(displayContent.mDisplayId)) {
            task.resumeTopActivityUncheckedLocked(null, null);
        }
        task.ensureActivitiesVisible(null, 0, false);
    }

    public Task getRootTask(int i) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            Task rootTask = getChildAt(childCount).getRootTask(i);
            if (rootTask != null) {
                return rootTask;
            }
        }
        return null;
    }

    public Task getRootTask(int i, int i2) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            Task rootTask = getChildAt(childCount).getRootTask(i, i2);
            if (rootTask != null) {
                return rootTask;
            }
        }
        return null;
    }

    public final Task getRootTask(int i, int i2, int i3) {
        DisplayContent displayContent = getDisplayContent(i3);
        if (displayContent == null) {
            return null;
        }
        return displayContent.getRootTask(i, i2);
    }

    public final ActivityTaskManager.RootTaskInfo getRootTaskInfo(final Task task) {
        final ActivityTaskManager.RootTaskInfo rootTaskInfo = new ActivityTaskManager.RootTaskInfo();
        task.fillTaskInfo(rootTaskInfo);
        DisplayContent displayContent = task.getDisplayContent();
        if (displayContent == null) {
            rootTaskInfo.position = -1;
        } else {
            final int[] iArr = new int[1];
            final boolean[] zArr = new boolean[1];
            displayContent.forAllRootTasks(new Predicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda18
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getRootTaskInfo$23;
                    lambda$getRootTaskInfo$23 = RootWindowContainer.lambda$getRootTaskInfo$23(Task.this, zArr, iArr, (Task) obj);
                    return lambda$getRootTaskInfo$23;
                }
            }, false);
            rootTaskInfo.position = zArr[0] ? iArr[0] : -1;
        }
        rootTaskInfo.visible = task.shouldBeVisible(null);
        task.getBounds(rootTaskInfo.bounds);
        int descendantTaskCount = task.getDescendantTaskCount();
        rootTaskInfo.childTaskIds = new int[descendantTaskCount];
        rootTaskInfo.childTaskNames = new String[descendantTaskCount];
        rootTaskInfo.childTaskBounds = new Rect[descendantTaskCount];
        rootTaskInfo.childTaskUserIds = new int[descendantTaskCount];
        final int[] iArr2 = {0};
        task.forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda19
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$getRootTaskInfo$24(iArr2, rootTaskInfo, (Task) obj);
            }
        }, false);
        ActivityRecord activityRecord = task.topRunningActivity();
        rootTaskInfo.topActivity = activityRecord != null ? activityRecord.intent.getComponent() : null;
        return rootTaskInfo;
    }

    public static /* synthetic */ boolean lambda$getRootTaskInfo$23(Task task, boolean[] zArr, int[] iArr, Task task2) {
        if (task == task2) {
            zArr[0] = true;
            return true;
        }
        iArr[0] = iArr[0] + 1;
        return false;
    }

    public static /* synthetic */ void lambda$getRootTaskInfo$24(int[] iArr, ActivityTaskManager.RootTaskInfo rootTaskInfo, Task task) {
        String str;
        int i = iArr[0];
        rootTaskInfo.childTaskIds[i] = task.mTaskId;
        String[] strArr = rootTaskInfo.childTaskNames;
        ComponentName componentName = task.origActivity;
        if (componentName != null) {
            str = componentName.flattenToString();
        } else {
            ComponentName componentName2 = task.realActivity;
            if (componentName2 != null) {
                str = componentName2.flattenToString();
            } else {
                str = task.getTopNonFinishingActivity() != null ? task.getTopNonFinishingActivity().packageName : "unknown";
            }
        }
        strArr[i] = str;
        rootTaskInfo.childTaskBounds[i] = task.mAtmService.getTaskBounds(task.mTaskId);
        rootTaskInfo.childTaskUserIds[i] = task.mUserId;
        iArr[0] = i + 1;
    }

    public ActivityTaskManager.RootTaskInfo getRootTaskInfo(int i) {
        Task rootTask = getRootTask(i);
        if (rootTask != null) {
            return getRootTaskInfo(rootTask);
        }
        return null;
    }

    public ActivityTaskManager.RootTaskInfo getRootTaskInfo(int i, int i2) {
        Task rootTask = getRootTask(i, i2);
        if (rootTask != null) {
            return getRootTaskInfo(rootTask);
        }
        return null;
    }

    public ActivityTaskManager.RootTaskInfo getRootTaskInfo(int i, int i2, int i3) {
        Task rootTask = getRootTask(i, i2, i3);
        if (rootTask != null) {
            return getRootTaskInfo(rootTask);
        }
        return null;
    }

    public ArrayList<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos(int i) {
        final ArrayList<ActivityTaskManager.RootTaskInfo> arrayList = new ArrayList<>();
        if (i == -1) {
            forAllRootTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda23
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    RootWindowContainer.this.lambda$getAllRootTaskInfos$25(arrayList, (Task) obj);
                }
            });
            return arrayList;
        }
        DisplayContent displayContent = getDisplayContent(i);
        if (displayContent == null) {
            return arrayList;
        }
        displayContent.forAllRootTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda24
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.this.lambda$getAllRootTaskInfos$26(arrayList, (Task) obj);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getAllRootTaskInfos$25(ArrayList arrayList, Task task) {
        arrayList.add(getRootTaskInfo(task));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getAllRootTaskInfos$26(ArrayList arrayList, Task task) {
        arrayList.add(getRootTaskInfo(task));
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayAdded(int i) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i);
                if (displayContentOrCreate == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                if (this.mService.isBooted() || this.mService.isBooting()) {
                    startSystemDecorations(displayContentOrCreate);
                }
                this.mWmService.mPossibleDisplayInfoMapper.removePossibleDisplayInfos(i);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void startSystemDecorations(DisplayContent displayContent) {
        startHomeOnDisplay(this.mCurrentUser, "displayAdded", displayContent.getDisplayId());
        displayContent.getDisplayPolicy().notifyDisplayReady();
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayRemoved(int i) {
        if (i == 0) {
            throw new IllegalArgumentException("Can't remove the primary display.");
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                DisplayContent displayContent = getDisplayContent(i);
                if (displayContent == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.remove();
                this.mWmService.mPossibleDisplayInfoMapper.removePossibleDisplayInfos(i);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayChanged(int i) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                DisplayContent displayContent = getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.onDisplayChanged();
                }
                this.mWmService.mPossibleDisplayInfoMapper.removePossibleDisplayInfos(i);
                updateDisplayImePolicyCache();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void updateDisplayImePolicyCache() {
        final ArrayMap arrayMap = new ArrayMap();
        forAllDisplays(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$updateDisplayImePolicyCache$27(arrayMap, (DisplayContent) obj);
            }
        });
        this.mWmService.mDisplayImePolicyCache = Collections.unmodifiableMap(arrayMap);
    }

    public static /* synthetic */ void lambda$updateDisplayImePolicyCache$27(ArrayMap arrayMap, DisplayContent displayContent) {
        arrayMap.put(Integer.valueOf(displayContent.getDisplayId()), Integer.valueOf(displayContent.getImePolicy()));
    }

    public void updateUIDsPresentOnDisplay() {
        this.mDisplayAccessUIDs.clear();
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            DisplayContent childAt = getChildAt(childCount);
            if (childAt.isPrivate()) {
                this.mDisplayAccessUIDs.append(childAt.mDisplayId, childAt.getPresentUIDs());
            }
        }
        this.mDisplayManagerInternal.setDisplayAccessUIDs(this.mDisplayAccessUIDs);
    }

    public void prepareForShutdown() {
        for (int i = 0; i < getChildCount(); i++) {
            createSleepToken("shutdown", getChildAt(i).mDisplayId);
        }
    }

    public SleepToken createSleepToken(String str, int i) {
        DisplayContent displayContent = getDisplayContent(i);
        if (displayContent == null) {
            throw new IllegalArgumentException("Invalid display: " + i);
        }
        int makeSleepTokenKey = makeSleepTokenKey(str, i);
        SleepToken sleepToken = this.mSleepTokens.get(makeSleepTokenKey);
        if (sleepToken == null) {
            SleepToken sleepToken2 = new SleepToken(str, i);
            this.mSleepTokens.put(makeSleepTokenKey, sleepToken2);
            displayContent.mAllSleepTokens.add(sleepToken2);
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -317761482, 4, (String) null, new Object[]{String.valueOf(str), Long.valueOf(i)});
            }
            return sleepToken2;
        }
        throw new RuntimeException("Create the same sleep token twice: " + sleepToken);
    }

    public void removeSleepToken(SleepToken sleepToken) {
        if (!this.mSleepTokens.contains(sleepToken.mHashKey)) {
            Slog.d(StartingSurfaceController.TAG, "Remove non-exist sleep token: " + sleepToken + " from " + Debug.getCallers(6));
        }
        this.mSleepTokens.remove(sleepToken.mHashKey);
        DisplayContent displayContent = getDisplayContent(sleepToken.mDisplayId);
        if (displayContent == null) {
            Slog.d(StartingSurfaceController.TAG, "Remove sleep token for non-existing display: " + sleepToken + " from " + Debug.getCallers(6));
            return;
        }
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -436553282, 4, (String) null, new Object[]{String.valueOf(sleepToken.mTag), Long.valueOf(sleepToken.mDisplayId)});
        }
        displayContent.mAllSleepTokens.remove(sleepToken);
        if (displayContent.mAllSleepTokens.isEmpty()) {
            this.mService.updateSleepIfNeededLocked();
            if ((this.mTaskSupervisor.getKeyguardController().isDisplayOccluded(displayContent.mDisplayId) || !sleepToken.mTag.equals("keyguard")) && !sleepToken.mTag.equals("Display-off")) {
                return;
            }
            displayContent.mSkipAppTransitionAnimation = true;
        }
    }

    public void addStartingWindowsForVisibleActivities() {
        final ArrayList arrayList = new ArrayList();
        forAllActivities(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda42
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$addStartingWindowsForVisibleActivities$28(arrayList, (ActivityRecord) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$addStartingWindowsForVisibleActivities$28(ArrayList arrayList, ActivityRecord activityRecord) {
        Task task = activityRecord.getTask();
        if (activityRecord.isVisibleRequested() && activityRecord.mStartingData == null && !arrayList.contains(task)) {
            activityRecord.showStartingWindow(true);
            arrayList.add(task);
        }
    }

    public void invalidateTaskLayers() {
        if (this.mTaskLayersChanged) {
            return;
        }
        this.mTaskLayersChanged = true;
        this.mService.f1161mH.post(this.mRankTaskLayersRunnable);
    }

    public void rankTaskLayers() {
        if (this.mTaskLayersChanged) {
            this.mTaskLayersChanged = false;
            this.mService.f1161mH.removeCallbacks(this.mRankTaskLayersRunnable);
        }
        this.mTmpTaskLayerRank = 0;
        forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda16
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.this.lambda$rankTaskLayers$30((Task) obj);
            }
        }, true);
        if (this.mTaskSupervisor.inActivityVisibilityUpdate()) {
            return;
        }
        this.mTaskSupervisor.computeProcessActivityStateBatch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$rankTaskLayers$30(Task task) {
        int i = task.mLayerRank;
        ActivityRecord activityRecord = task.topRunningActivityLocked();
        if (activityRecord != null && activityRecord.isVisibleRequested()) {
            int i2 = this.mTmpTaskLayerRank + 1;
            this.mTmpTaskLayerRank = i2;
            task.mLayerRank = i2;
        } else {
            task.mLayerRank = -1;
        }
        if (task.mLayerRank != i) {
            task.forAllActivities(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda33
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    RootWindowContainer.this.lambda$rankTaskLayers$29((ActivityRecord) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$rankTaskLayers$29(ActivityRecord activityRecord) {
        if (activityRecord.hasProcess()) {
            this.mTaskSupervisor.onProcessActivityStateChanged(activityRecord.app, true);
        }
    }

    public void clearOtherAppTimeTrackers(final AppTimeTracker appTimeTracker) {
        forAllActivities(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda9
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$clearOtherAppTimeTrackers$31(AppTimeTracker.this, (ActivityRecord) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$clearOtherAppTimeTrackers$31(AppTimeTracker appTimeTracker, ActivityRecord activityRecord) {
        if (activityRecord.appTimeTracker != appTimeTracker) {
            activityRecord.appTimeTracker = null;
        }
    }

    public void scheduleDestroyAllActivities(String str) {
        this.mDestroyAllActivitiesReason = str;
        this.mService.f1161mH.post(this.mDestroyAllActivitiesRunnable);
    }

    public boolean putTasksToSleep(final boolean z, final boolean z2) {
        final boolean[] zArr = {true};
        forAllRootTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda25
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$putTasksToSleep$32(z, zArr, z2, (Task) obj);
            }
        });
        return zArr[0];
    }

    public static /* synthetic */ void lambda$putTasksToSleep$32(boolean z, boolean[] zArr, boolean z2, Task task) {
        if (z) {
            zArr[0] = zArr[0] & task.goToSleepIfPossible(z2);
        } else {
            task.ensureActivitiesVisible(null, 0, false);
        }
    }

    public ActivityRecord findActivity(Intent intent, ActivityInfo activityInfo, boolean z) {
        ComponentName component = intent.getComponent();
        if (activityInfo.targetActivity != null) {
            component = new ComponentName(activityInfo.packageName, activityInfo.targetActivity);
        }
        int userId = UserHandle.getUserId(activityInfo.applicationInfo.uid);
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new QuintPredicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda26
            public final boolean test(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                boolean matchesActivity;
                matchesActivity = RootWindowContainer.matchesActivity((ActivityRecord) obj, ((Integer) obj2).intValue(), ((Boolean) obj3).booleanValue(), (Intent) obj4, (ComponentName) obj5);
                return matchesActivity;
            }
        }, PooledLambda.__(ActivityRecord.class), Integer.valueOf(userId), Boolean.valueOf(z), intent, component);
        ActivityRecord activity = getActivity(obtainPredicate);
        obtainPredicate.recycle();
        return activity;
    }

    public static boolean matchesActivity(ActivityRecord activityRecord, int i, boolean z, Intent intent, ComponentName componentName) {
        if (activityRecord.canBeTopRunning() && activityRecord.mUserId == i) {
            if (z) {
                if (activityRecord.intent.filterEquals(intent)) {
                    return true;
                }
            } else if (activityRecord.mActivityComponent.equals(componentName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAwakeDisplay() {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            if (!getChildAt(childCount).shouldSleep()) {
                return true;
            }
        }
        return false;
    }

    public Task getOrCreateRootTask(ActivityRecord activityRecord, ActivityOptions activityOptions, Task task, boolean z) {
        return getOrCreateRootTask(activityRecord, activityOptions, task, null, z, null, 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:65:0x00cc  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x00d1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Task getOrCreateRootTask(ActivityRecord activityRecord, ActivityOptions activityOptions, Task task, Task task2, boolean z, LaunchParamsController.LaunchParams launchParams, int i) {
        TaskDisplayArea taskDisplayArea;
        int launchDisplayId;
        DisplayContent displayContent;
        int launchTaskId;
        Task fromWindowContainerToken;
        if (activityOptions == null || (fromWindowContainerToken = Task.fromWindowContainerToken(activityOptions.getLaunchRootTask())) == null || !canLaunchOnDisplay(activityRecord, fromWindowContainerToken)) {
            if (activityOptions != null && (launchTaskId = activityOptions.getLaunchTaskId()) != -1) {
                activityOptions.setLaunchTaskId(-1);
                Task anyTaskForId = anyTaskForId(launchTaskId, 2, activityOptions, z);
                activityOptions.setLaunchTaskId(launchTaskId);
                if (canLaunchOnDisplay(activityRecord, anyTaskForId)) {
                    return anyTaskForId.getRootTask();
                }
            }
            TaskDisplayArea taskDisplayArea2 = null;
            if (launchParams == null || (taskDisplayArea = launchParams.mPreferredTaskDisplayArea) == null) {
                if (activityOptions != null) {
                    WindowContainerToken launchTaskDisplayArea = activityOptions.getLaunchTaskDisplayArea();
                    taskDisplayArea = launchTaskDisplayArea != null ? (TaskDisplayArea) WindowContainer.fromBinder(launchTaskDisplayArea.asBinder()) : null;
                    if (taskDisplayArea == null && (launchDisplayId = activityOptions.getLaunchDisplayId()) != -1 && (displayContent = getDisplayContent(launchDisplayId)) != null) {
                        taskDisplayArea = displayContent.getDefaultTaskDisplayArea();
                    }
                } else {
                    taskDisplayArea = null;
                }
            }
            int resolveActivityType = resolveActivityType(activityRecord, activityOptions, task);
            if (taskDisplayArea != null) {
                if (canLaunchOnDisplay(activityRecord, taskDisplayArea.getDisplayId())) {
                    return taskDisplayArea.getOrCreateRootTask(activityRecord, activityOptions, task, task2, launchParams, i, resolveActivityType, z);
                }
                taskDisplayArea = null;
            }
            Task rootTask = task != null ? task.getRootTask() : null;
            if (rootTask == null && activityRecord != null) {
                rootTask = activityRecord.getRootTask();
            }
            int i2 = launchParams != null ? launchParams.mWindowingMode : 0;
            if (rootTask != null) {
                taskDisplayArea = rootTask.getDisplayArea();
                if (taskDisplayArea != null && canLaunchOnDisplay(activityRecord, taskDisplayArea.mDisplayContent.mDisplayId)) {
                    if (i2 == 0) {
                        i2 = taskDisplayArea.resolveWindowingMode(activityRecord, activityOptions, task);
                    }
                    if (rootTask.isCompatible(i2, resolveActivityType) || rootTask.mCreatedByOrganizer) {
                        return rootTask;
                    }
                }
                return (taskDisplayArea2 != null ? getDefaultTaskDisplayArea() : taskDisplayArea2).getOrCreateRootTask(activityRecord, activityOptions, task, task2, launchParams, i, resolveActivityType, z);
            }
            taskDisplayArea2 = taskDisplayArea;
            return (taskDisplayArea2 != null ? getDefaultTaskDisplayArea() : taskDisplayArea2).getOrCreateRootTask(activityRecord, activityOptions, task, task2, launchParams, i, resolveActivityType, z);
        }
        return fromWindowContainerToken;
    }

    public final boolean canLaunchOnDisplay(ActivityRecord activityRecord, Task task) {
        if (task == null) {
            Slog.w(StartingSurfaceController.TAG, "canLaunchOnDisplay(), invalid task: " + task);
            return false;
        } else if (!task.isAttached()) {
            Slog.w(StartingSurfaceController.TAG, "canLaunchOnDisplay(), Task is not attached: " + task);
            return false;
        } else {
            return canLaunchOnDisplay(activityRecord, task.getTaskDisplayArea().getDisplayId());
        }
    }

    public final boolean canLaunchOnDisplay(ActivityRecord activityRecord, int i) {
        if (activityRecord == null || activityRecord.canBeLaunchedOnDisplay(i)) {
            return true;
        }
        Slog.w(StartingSurfaceController.TAG, "Not allow to launch " + activityRecord + " on display " + i);
        return false;
    }

    public int resolveActivityType(ActivityRecord activityRecord, ActivityOptions activityOptions, Task task) {
        int activityType = activityRecord != null ? activityRecord.getActivityType() : 0;
        if (activityType == 0 && task != null) {
            activityType = task.getActivityType();
        }
        if (activityType != 0) {
            return activityType;
        }
        if (activityOptions != null) {
            activityType = activityOptions.getLaunchActivityType();
        }
        if (activityType != 0) {
            return activityType;
        }
        return 1;
    }

    public Task getNextFocusableRootTask(Task task, boolean z) {
        Task nextFocusableRootTask;
        TaskDisplayArea displayArea = task.getDisplayArea();
        if (displayArea == null) {
            displayArea = getDisplayContent(task.mPrevDisplayId).getDefaultTaskDisplayArea();
        }
        Task nextFocusableRootTask2 = displayArea.getNextFocusableRootTask(task, z);
        if (nextFocusableRootTask2 != null) {
            return nextFocusableRootTask2;
        }
        if (displayArea.mDisplayContent.supportsSystemDecorations()) {
            return null;
        }
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            DisplayContent childAt = getChildAt(childCount);
            if (childAt != displayArea.mDisplayContent && (nextFocusableRootTask = childAt.getDefaultTaskDisplayArea().getNextFocusableRootTask(task, z)) != null) {
                return nextFocusableRootTask;
            }
        }
        return null;
    }

    public void closeSystemDialogActivities(final String str) {
        forAllActivities(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda20
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.this.lambda$closeSystemDialogActivities$33(str, (ActivityRecord) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closeSystemDialogActivities$33(String str, ActivityRecord activityRecord) {
        if ((activityRecord.info.flags & 256) != 0 || shouldCloseAssistant(activityRecord, str)) {
            activityRecord.finishIfPossible(str, true);
        }
    }

    public boolean hasVisibleWindowAboveButDoesNotOwnNotificationShade(final int i) {
        final boolean[] zArr = {false};
        return forAllWindows(new ToBooleanFunction() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda0
            public final boolean apply(Object obj) {
                boolean lambda$hasVisibleWindowAboveButDoesNotOwnNotificationShade$34;
                lambda$hasVisibleWindowAboveButDoesNotOwnNotificationShade$34 = RootWindowContainer.lambda$hasVisibleWindowAboveButDoesNotOwnNotificationShade$34(i, zArr, (WindowState) obj);
                return lambda$hasVisibleWindowAboveButDoesNotOwnNotificationShade$34;
            }
        }, true);
    }

    public static /* synthetic */ boolean lambda$hasVisibleWindowAboveButDoesNotOwnNotificationShade$34(int i, boolean[] zArr, WindowState windowState) {
        if (windowState.mOwnerUid == i && windowState.isVisible()) {
            zArr[0] = true;
        }
        if (windowState.mAttrs.type == 2040) {
            return zArr[0] && windowState.mOwnerUid != i;
        }
        return false;
    }

    public final boolean shouldCloseAssistant(ActivityRecord activityRecord, String str) {
        if (activityRecord.isActivityTypeAssistant() && str != "assist") {
            return this.mWmService.mAssistantOnTopOfDream;
        }
        return false;
    }

    /* renamed from: com.android.server.wm.RootWindowContainer$FinishDisabledPackageActivitiesHelper */
    /* loaded from: classes2.dex */
    public class FinishDisabledPackageActivitiesHelper implements Predicate<ActivityRecord> {
        public final ArrayList<ActivityRecord> mCollectedActivities = new ArrayList<>();
        public boolean mDoit;
        public boolean mEvenPersistent;
        public Set<String> mFilterByClasses;
        public Task mLastTask;
        public boolean mOnlyRemoveNoProcess;
        public String mPackageName;
        public int mUserId;

        public FinishDisabledPackageActivitiesHelper() {
        }

        public final void reset(String str, Set<String> set, boolean z, boolean z2, int i, boolean z3) {
            this.mPackageName = str;
            this.mFilterByClasses = set;
            this.mDoit = z;
            this.mEvenPersistent = z2;
            this.mUserId = i;
            this.mOnlyRemoveNoProcess = z3;
            this.mLastTask = null;
        }

        public boolean process(String str, Set<String> set, boolean z, boolean z2, int i, boolean z3) {
            reset(str, set, z, z2, i, z3);
            RootWindowContainer.this.forAllActivities(this);
            int size = this.mCollectedActivities.size();
            boolean z4 = false;
            for (int i2 = 0; i2 < size; i2++) {
                ActivityRecord activityRecord = this.mCollectedActivities.get(i2);
                if (this.mOnlyRemoveNoProcess) {
                    if (!activityRecord.hasProcess()) {
                        Slog.i(StartingSurfaceController.TAG, "  Force removing " + activityRecord);
                        activityRecord.cleanUp(false, false);
                        activityRecord.removeFromHistory("force-stop");
                    }
                } else {
                    Slog.i(StartingSurfaceController.TAG, "  Force finishing " + activityRecord);
                    activityRecord.finishIfPossible("force-stop", true);
                }
                z4 = true;
            }
            this.mCollectedActivities.clear();
            return z4;
        }

        @Override // java.util.function.Predicate
        public boolean test(ActivityRecord activityRecord) {
            Set<String> set;
            boolean z = (activityRecord.packageName.equals(this.mPackageName) && ((set = this.mFilterByClasses) == null || set.contains(activityRecord.mActivityComponent.getClassName()))) || (this.mPackageName == null && activityRecord.mUserId == this.mUserId);
            boolean z2 = !activityRecord.hasProcess();
            int i = this.mUserId;
            if ((i == -1 || activityRecord.mUserId == i) && ((z || activityRecord.getTask() == this.mLastTask) && (z2 || this.mEvenPersistent || !activityRecord.app.isPersistent()))) {
                if (!this.mDoit) {
                    return !activityRecord.finishing;
                }
                this.mCollectedActivities.add(activityRecord);
                this.mLastTask = activityRecord.getTask();
            }
            return false;
        }
    }

    public boolean finishDisabledPackageActivities(String str, Set<String> set, boolean z, boolean z2, int i, boolean z3) {
        return this.mFinishDisabledPackageActivitiesHelper.process(str, set, z, z2, i, z3);
    }

    public void updateActivityApplicationInfo(final ApplicationInfo applicationInfo) {
        final String str = applicationInfo.packageName;
        final int userId = UserHandle.getUserId(applicationInfo.uid);
        forAllActivities(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.lambda$updateActivityApplicationInfo$35(userId, str, applicationInfo, (ActivityRecord) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$updateActivityApplicationInfo$35(int i, String str, ApplicationInfo applicationInfo, ActivityRecord activityRecord) {
        if (activityRecord.mUserId == i && str.equals(activityRecord.packageName)) {
            activityRecord.updateApplicationInfo(applicationInfo);
        }
    }

    public void finishVoiceTask(IVoiceInteractionSession iVoiceInteractionSession) {
        final IBinder asBinder = iVoiceInteractionSession.asBinder();
        forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda10
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Task) obj).finishIfVoiceTask(asBinder);
            }
        }, true);
    }

    public void removeRootTasksInWindowingModes(int... iArr) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            getChildAt(childCount).removeRootTasksInWindowingModes(iArr);
        }
    }

    public void removeRootTasksWithActivityTypes(int... iArr) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            getChildAt(childCount).removeRootTasksWithActivityTypes(iArr);
        }
    }

    public ActivityRecord topRunningActivity() {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            ActivityRecord activityRecord = getChildAt(childCount).topRunningActivity();
            if (activityRecord != null) {
                return activityRecord;
            }
        }
        return null;
    }

    public boolean allResumedActivitiesIdle() {
        Task focusedRootTask;
        ActivityRecord topResumedActivity;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            DisplayContent childAt = getChildAt(childCount);
            if (!childAt.isSleeping() && (focusedRootTask = childAt.getFocusedRootTask()) != null && focusedRootTask.hasActivity() && ((topResumedActivity = focusedRootTask.getTopResumedActivity()) == null || !topResumedActivity.idle)) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -938271693, 1, (String) null, new Object[]{Long.valueOf(focusedRootTask.getRootTaskId()), String.valueOf(topResumedActivity)});
                    return false;
                }
                return false;
            }
        }
        this.mService.endLaunchPowerMode(1);
        return true;
    }

    public boolean allResumedActivitiesVisible() {
        final boolean[] zArr = {false};
        if (forAllRootTasks(new Predicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda32
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$allResumedActivitiesVisible$37;
                lambda$allResumedActivitiesVisible$37 = RootWindowContainer.lambda$allResumedActivitiesVisible$37(zArr, (Task) obj);
                return lambda$allResumedActivitiesVisible$37;
            }
        })) {
            return false;
        }
        return zArr[0];
    }

    public static /* synthetic */ boolean lambda$allResumedActivitiesVisible$37(boolean[] zArr, Task task) {
        ActivityRecord topResumedActivity = task.getTopResumedActivity();
        if (topResumedActivity != null) {
            if (!topResumedActivity.nowVisible) {
                return true;
            }
            zArr[0] = true;
        }
        return false;
    }

    public boolean allPausedActivitiesComplete() {
        final boolean[] zArr = {true};
        if (forAllLeafTasks(new Predicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda34
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$allPausedActivitiesComplete$38;
                lambda$allPausedActivitiesComplete$38 = RootWindowContainer.lambda$allPausedActivitiesComplete$38(zArr, (Task) obj);
                return lambda$allPausedActivitiesComplete$38;
            }
        })) {
            return false;
        }
        return zArr[0];
    }

    public static /* synthetic */ boolean lambda$allPausedActivitiesComplete$38(boolean[] zArr, Task task) {
        ActivityRecord topPausingActivity = task.getTopPausingActivity();
        if (topPausingActivity != null && !topPausingActivity.isState(ActivityRecord.State.PAUSED, ActivityRecord.State.STOPPED, ActivityRecord.State.STOPPING, ActivityRecord.State.FINISHING)) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, 895158150, 0, (String) null, new Object[]{String.valueOf(topPausingActivity), String.valueOf(topPausingActivity.getState())});
            }
            if (!ProtoLogGroup.WM_DEBUG_STATES.isEnabled()) {
                return true;
            }
            zArr[0] = false;
        }
        return false;
    }

    public void lockAllProfileTasks(final int i) {
        forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RootWindowContainer.this.lambda$lockAllProfileTasks$40(i, (Task) obj);
            }
        }, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$lockAllProfileTasks$40(final int i, Task task) {
        ActivityRecord activityRecord = task.topRunningActivity();
        if ((activityRecord == null || activityRecord.finishing || !"android.app.action.CONFIRM_DEVICE_CREDENTIAL_WITH_USER".equals(activityRecord.intent.getAction()) || !activityRecord.packageName.equals(this.mService.getSysUiServiceComponentLocked().getPackageName())) && task.getActivity(new Predicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda29
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$lockAllProfileTasks$39;
                lambda$lockAllProfileTasks$39 = RootWindowContainer.lambda$lockAllProfileTasks$39(i, (ActivityRecord) obj);
                return lambda$lockAllProfileTasks$39;
            }
        }) != null) {
            this.mService.getTaskChangeNotificationController().notifyTaskProfileLocked(task.getTaskInfo());
        }
    }

    public static /* synthetic */ boolean lambda$lockAllProfileTasks$39(int i, ActivityRecord activityRecord) {
        return !activityRecord.finishing && activityRecord.mUserId == i;
    }

    public Task anyTaskForId(int i) {
        return anyTaskForId(i, 2);
    }

    public Task anyTaskForId(int i, int i2) {
        return anyTaskForId(i, i2, null, false);
    }

    public Task anyTaskForId(int i, int i2, ActivityOptions activityOptions, boolean z) {
        Task task;
        Task orCreateRootTask;
        if (i2 != 2 && activityOptions != null) {
            throw new IllegalArgumentException("Should not specify activity options for non-restore lookup");
        }
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new AppTransition$$ExternalSyntheticLambda2(), PooledLambda.__(Task.class), Integer.valueOf(i));
        Task task2 = getTask(obtainPredicate);
        obtainPredicate.recycle();
        if (task2 != null) {
            if (activityOptions != null && (orCreateRootTask = getOrCreateRootTask(null, activityOptions, task2, z)) != null && task2.getRootTask() != orCreateRootTask) {
                task2.reparent(orCreateRootTask, z, z ? 0 : 2, true, true, "anyTaskForId");
            }
            return task2;
        } else if (i2 == 0 || (task = this.mTaskSupervisor.mRecentTasks.getTask(i)) == null) {
            return null;
        } else {
            if (i2 == 1 || this.mTaskSupervisor.restoreRecentTaskLocked(task, activityOptions, z)) {
                return task;
            }
            return null;
        }
    }

    @VisibleForTesting
    public void getRunningTasks(int i, List<ActivityManager.RunningTaskInfo> list, int i2, int i3, ArraySet<Integer> arraySet, int i4) {
        DisplayContent displayContent;
        if (i4 != -1) {
            DisplayContent displayContent2 = getDisplayContent(i4);
            if (displayContent2 == null) {
                return;
            }
            displayContent = displayContent2;
        } else {
            displayContent = this;
        }
        this.mTaskSupervisor.getRunningTasks().getTasks(i, list, i2, this.mService.getRecentTasks(), displayContent, i3, arraySet);
    }

    public void startPowerModeLaunchIfNeeded(boolean z, final ActivityRecord activityRecord) {
        ActivityOptions options;
        int i = 1;
        if (!z && activityRecord != null && activityRecord.app != null) {
            final boolean[] zArr = {true};
            final boolean[] zArr2 = {true};
            forAllTaskDisplayAreas(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda15
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    RootWindowContainer.lambda$startPowerModeLaunchIfNeeded$41(zArr, zArr2, activityRecord, (TaskDisplayArea) obj);
                }
            });
            if (!zArr[0] && !zArr2[0]) {
                return;
            }
        }
        if ((activityRecord != null ? activityRecord.isKeyguardLocked() : this.mDefaultDisplay.isKeyguardLocked()) && activityRecord != null && !activityRecord.isLaunchSourceType(3) && ((options = activityRecord.getOptions()) == null || options.getSourceInfo() == null || options.getSourceInfo().type != 3)) {
            i = 5;
        }
        this.mService.startLaunchPowerMode(i);
    }

    public static /* synthetic */ void lambda$startPowerModeLaunchIfNeeded$41(boolean[] zArr, boolean[] zArr2, ActivityRecord activityRecord, TaskDisplayArea taskDisplayArea) {
        ActivityRecord focusedActivity = taskDisplayArea.getFocusedActivity();
        WindowProcessController windowProcessController = focusedActivity == null ? null : focusedActivity.app;
        zArr[0] = zArr[0] & (windowProcessController == null);
        if (windowProcessController != null) {
            zArr2[0] = zArr2[0] & (!windowProcessController.equals(activityRecord.app));
        }
    }

    public int getTaskToShowPermissionDialogOn(final String str, final int i) {
        final PermissionPolicyInternal permissionPolicyInternal = this.mService.getPermissionPolicyInternal();
        if (permissionPolicyInternal == null) {
            return -1;
        }
        final int[] iArr = {-1};
        forAllLeafTaskFragments(new Predicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda27
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getTaskToShowPermissionDialogOn$43;
                lambda$getTaskToShowPermissionDialogOn$43 = RootWindowContainer.lambda$getTaskToShowPermissionDialogOn$43(PermissionPolicyInternal.this, i, str, iArr, (TaskFragment) obj);
                return lambda$getTaskToShowPermissionDialogOn$43;
            }
        });
        return iArr[0];
    }

    public static /* synthetic */ boolean lambda$getTaskToShowPermissionDialogOn$43(final PermissionPolicyInternal permissionPolicyInternal, int i, String str, int[] iArr, TaskFragment taskFragment) {
        ActivityRecord activity = taskFragment.getActivity(new Predicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda44
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getTaskToShowPermissionDialogOn$42;
                lambda$getTaskToShowPermissionDialogOn$42 = RootWindowContainer.lambda$getTaskToShowPermissionDialogOn$42(PermissionPolicyInternal.this, (ActivityRecord) obj);
                return lambda$getTaskToShowPermissionDialogOn$42;
            }
        });
        if (activity != null && activity.isUid(i) && Objects.equals(str, activity.packageName) && permissionPolicyInternal.shouldShowNotificationDialogForTask(activity.getTask().getTaskInfo(), str, activity.launchedFromPackage, activity.intent, activity.getName())) {
            iArr[0] = activity.getTask().mTaskId;
            return true;
        }
        return false;
    }

    public static /* synthetic */ boolean lambda$getTaskToShowPermissionDialogOn$42(PermissionPolicyInternal permissionPolicyInternal, ActivityRecord activityRecord) {
        return activityRecord.canBeTopRunning() && activityRecord.isVisibleRequested() && !permissionPolicyInternal.isIntentToPermissionDialog(activityRecord.intent);
    }

    public ArrayList<ActivityRecord> getDumpActivities(final String str, final boolean z, boolean z2, final int i) {
        if (z2) {
            Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
            if (topDisplayFocusedRootTask != null) {
                return topDisplayFocusedRootTask.getDumpActivitiesLocked(str, i);
            }
            return new ArrayList<>();
        }
        RecentTasks recentTasks = this.mWindowManager.mAtmService.getRecentTasks();
        final int recentsComponentUid = recentTasks != null ? recentTasks.getRecentsComponentUid() : -1;
        final ArrayList<ActivityRecord> arrayList = new ArrayList<>();
        forAllLeafTasks(new Predicate() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda37
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getDumpActivities$44;
                lambda$getDumpActivities$44 = RootWindowContainer.lambda$getDumpActivities$44(recentsComponentUid, z, arrayList, str, i, (Task) obj);
                return lambda$getDumpActivities$44;
            }
        });
        return arrayList;
    }

    public static /* synthetic */ boolean lambda$getDumpActivities$44(int i, boolean z, ArrayList arrayList, String str, int i2, Task task) {
        boolean z2 = task.effectiveUid == i;
        if (!z || task.shouldBeVisible(null) || z2) {
            arrayList.addAll(task.getDumpActivitiesLocked(str, i2));
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void dump(PrintWriter printWriter, String str, boolean z) {
        super.dump(printWriter, str, z);
        printWriter.print(str);
        printWriter.println("topDisplayFocusedRootTask=" + getTopDisplayFocusedRootTask());
        for (int childCount = getChildCount() + (-1); childCount >= 0; childCount--) {
            getChildAt(childCount).dump(printWriter, str, z);
        }
    }

    public void dumpDisplayConfigs(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.println("Display override configurations:");
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            DisplayContent childAt = getChildAt(i);
            printWriter.print(str);
            printWriter.print("  ");
            printWriter.print(childAt.mDisplayId);
            printWriter.print(": ");
            printWriter.println(childAt.getRequestedOverrideConfiguration());
        }
    }

    public boolean dumpActivities(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final boolean z, final boolean z2, final String str, int i) {
        final boolean[] zArr = {false};
        final boolean[] zArr2 = {false};
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            DisplayContent childAt = getChildAt(childCount);
            if (zArr[0]) {
                printWriter.println();
            }
            if (i == -1 || childAt.mDisplayId == i) {
                printWriter.print("Display #");
                printWriter.print(childAt.mDisplayId);
                printWriter.println(" (activities from top to bottom):");
                childAt.forAllRootTasks(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda38
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        RootWindowContainer.lambda$dumpActivities$45(zArr2, printWriter, fileDescriptor, z, z2, str, zArr, (Task) obj);
                    }
                });
                childAt.forAllTaskDisplayAreas(new Consumer() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda39
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        RootWindowContainer.lambda$dumpActivities$47(zArr, printWriter, str, zArr2, (TaskDisplayArea) obj);
                    }
                });
            }
        }
        boolean dumpHistoryList = zArr[0] | ActivityTaskSupervisor.dumpHistoryList(fileDescriptor, printWriter, this.mTaskSupervisor.mFinishingActivities, "  ", "Fin", false, !z, false, str, true, new Runnable() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                printWriter.println("  Activities waiting to finish:");
            }
        }, null);
        zArr[0] = dumpHistoryList;
        boolean dumpHistoryList2 = ActivityTaskSupervisor.dumpHistoryList(fileDescriptor, printWriter, this.mTaskSupervisor.mStoppingActivities, "  ", "Stop", false, !z, false, str, true, new Runnable() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                printWriter.println("  Activities waiting to stop:");
            }
        }, null) | dumpHistoryList;
        zArr[0] = dumpHistoryList2;
        return dumpHistoryList2;
    }

    public static /* synthetic */ void lambda$dumpActivities$45(boolean[] zArr, PrintWriter printWriter, FileDescriptor fileDescriptor, boolean z, boolean z2, String str, boolean[] zArr2, Task task) {
        if (zArr[0]) {
            printWriter.println();
        }
        boolean dump = task.dump(fileDescriptor, printWriter, z, z2, str, false);
        zArr[0] = dump;
        zArr2[0] = dump | zArr2[0];
    }

    public static /* synthetic */ void lambda$dumpActivities$47(boolean[] zArr, final PrintWriter printWriter, String str, boolean[] zArr2, TaskDisplayArea taskDisplayArea) {
        zArr[0] = ActivityTaskSupervisor.printThisActivity(printWriter, taskDisplayArea.getFocusedActivity(), str, zArr2[0], "    Resumed: ", new Runnable() { // from class: com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                printWriter.println("  Resumed activities in task display areas (from top to bottom):");
            }
        }) | zArr[0];
    }

    public static int makeSleepTokenKey(String str, int i) {
        return (str + i).hashCode();
    }

    /* renamed from: com.android.server.wm.RootWindowContainer$SleepToken */
    /* loaded from: classes2.dex */
    public static final class SleepToken {
        public final long mAcquireTime = SystemClock.uptimeMillis();
        public final int mDisplayId;
        public final int mHashKey;
        public final String mTag;

        public SleepToken(String str, int i) {
            this.mTag = str;
            this.mDisplayId = i;
            this.mHashKey = RootWindowContainer.makeSleepTokenKey(str, i);
        }

        public String toString() {
            return "{\"" + this.mTag + "\", display " + this.mDisplayId + ", acquire at " + TimeUtils.formatUptime(this.mAcquireTime) + "}";
        }

        public void writeTagToProto(ProtoOutputStream protoOutputStream, long j) {
            protoOutputStream.write(j, this.mTag);
        }
    }

    /* renamed from: com.android.server.wm.RootWindowContainer$RankTaskLayersRunnable */
    /* loaded from: classes2.dex */
    public class RankTaskLayersRunnable implements Runnable {
        public RankTaskLayersRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (RootWindowContainer.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (RootWindowContainer.this.mTaskLayersChanged) {
                        RootWindowContainer.this.mTaskLayersChanged = false;
                        RootWindowContainer.this.rankTaskLayers();
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* renamed from: com.android.server.wm.RootWindowContainer$AttachApplicationHelper */
    /* loaded from: classes2.dex */
    public class AttachApplicationHelper implements Consumer<Task>, Predicate<ActivityRecord> {
        public WindowProcessController mApp;
        public boolean mHasActivityStarted;
        public RemoteException mRemoteException;
        public ActivityRecord mTop;

        public AttachApplicationHelper() {
        }

        public void reset() {
            this.mHasActivityStarted = false;
            this.mRemoteException = null;
            this.mApp = null;
            this.mTop = null;
        }

        public boolean process(WindowProcessController windowProcessController) throws RemoteException {
            this.mApp = windowProcessController;
            for (int childCount = RootWindowContainer.this.getChildCount() - 1; childCount >= 0; childCount--) {
                RootWindowContainer.this.getChildAt(childCount).forAllRootTasks((Consumer<Task>) this);
                RemoteException remoteException = this.mRemoteException;
                if (remoteException != null) {
                    throw remoteException;
                }
            }
            if (!this.mHasActivityStarted) {
                RootWindowContainer.this.ensureActivitiesVisible(null, 0, false);
            }
            return this.mHasActivityStarted;
        }

        @Override // java.util.function.Consumer
        public void accept(Task task) {
            if (this.mRemoteException == null && task.getVisibility(null) != 2) {
                this.mTop = task.topRunningActivity();
                task.forAllActivities((Predicate<ActivityRecord>) this);
            }
        }

        @Override // java.util.function.Predicate
        public boolean test(ActivityRecord activityRecord) {
            if (!activityRecord.finishing && activityRecord.showToCurrentUser() && activityRecord.visibleIgnoringKeyguard && activityRecord.app == null) {
                WindowProcessController windowProcessController = this.mApp;
                if (windowProcessController.mUid == activityRecord.info.applicationInfo.uid && windowProcessController.mName.equals(activityRecord.processName)) {
                    try {
                        if (RootWindowContainer.this.mTaskSupervisor.realStartActivityLocked(activityRecord, this.mApp, this.mTop == activityRecord && activityRecord.getTask().canBeResumed(activityRecord), true)) {
                            this.mHasActivityStarted = true;
                        }
                        return false;
                    } catch (RemoteException e) {
                        Slog.w(StartingSurfaceController.TAG, "Exception in new application when starting activity " + this.mTop, e);
                        this.mRemoteException = e;
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
