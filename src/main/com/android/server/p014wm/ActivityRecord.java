package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ICompatCameraControlCallback;
import android.app.IScreenCaptureObserver;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.ResultInfo;
import android.app.TaskInfo;
import android.app.WindowConfiguration;
import android.app.admin.DevicePolicyManager;
import android.app.assist.ActivityId;
import android.app.servertransaction.ActivityConfigurationChangeItem;
import android.app.servertransaction.ActivityLifecycleItem;
import android.app.servertransaction.ActivityRelaunchItem;
import android.app.servertransaction.ActivityResultItem;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.ClientTransactionItem;
import android.app.servertransaction.DestroyActivityItem;
import android.app.servertransaction.MoveToDisplayItem;
import android.app.servertransaction.NewIntentItem;
import android.app.servertransaction.PauseActivityItem;
import android.app.servertransaction.ResumeActivityItem;
import android.app.servertransaction.StartActivityItem;
import android.app.servertransaction.StopActivityItem;
import android.app.servertransaction.TopResumedActivityChangeItem;
import android.app.servertransaction.TransferSplashScreenViewStateItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.LocusId;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConstrainDisplayApisConfig;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.gui.DropInputMode;
import android.hardware.HardwareBuffer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.InputConstants;
import android.os.LocaleList;
import android.os.PersistableBundle;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.service.voice.IVoiceInteractionSession;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.MergedConfiguration;
import android.util.Pair;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.AppTransitionAnimationSpec;
import android.view.DisplayInfo;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.InputApplicationHandle;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationDefinition;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.window.ITaskFragmentOrganizer;
import android.window.RemoteTransition;
import android.window.SizeConfigurationBuckets;
import android.window.SplashScreenView;
import android.window.TaskSnapshot;
import android.window.TransitionInfo;
import android.window.WindowContainerToken;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ResolverActivity;
import com.android.internal.content.ReferrerIntent;
import com.android.internal.os.TimeoutRecord;
import com.android.internal.os.TransferPipe;
import com.android.internal.policy.AttributeCache;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.ToBooleanFunction;
import com.android.internal.util.jobs.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.LocalServices;
import com.android.server.SystemServerInitThreadPool$$ExternalSyntheticLambda0;
import com.android.server.contentcapture.ContentCaptureManagerInternal;
import com.android.server.display.color.ColorDisplayService;
import com.android.server.p006am.AppTimeTracker;
import com.android.server.p006am.PendingIntentRecord;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p014wm.ActivityMetricsLogger;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.DisplayPolicy;
import com.android.server.p014wm.RemoteAnimationController;
import com.android.server.p014wm.StartingSurfaceController;
import com.android.server.p014wm.WindowManagerService;
import com.android.server.p014wm.WindowState;
import com.android.server.p014wm.utils.InsetUtils;
import com.android.server.uri.NeededUriGrants;
import com.android.server.uri.UriPermissionOwner;
import com.google.android.collect.Sets;
import dalvik.annotation.optimization.NeverCompile;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.wm.ActivityRecord */
/* loaded from: classes2.dex */
public final class ActivityRecord extends WindowToken implements WindowManagerService.AppFreezeListener {
    public static ConstrainDisplayApisConfig sConstrainDisplayApisConfig;
    public boolean allDrawn;
    public WindowProcessController app;
    public AppTimeTracker appTimeTracker;
    public final Binder assistToken;
    public final boolean componentSpecified;
    public int configChangeFlags;
    public long createTime;
    public boolean deferRelaunchUntilPaused;
    public boolean delayedResume;
    public boolean finishing;
    public boolean firstWindowDrawn;
    public boolean forceNewConfig;
    public boolean frozenBeforeDestroy;
    public boolean hasBeenLaunched;
    public int icon;
    public boolean idle;
    public boolean immersive;
    public volatile boolean inHistory;
    public final ActivityInfo info;
    public final Intent intent;
    public boolean keysPaused;
    public int labelRes;
    public long lastLaunchTime;
    public long lastVisibleTime;
    public int launchCount;
    public boolean launchFailed;
    public int launchMode;
    public long launchTickTime;
    public final String launchedFromFeatureId;
    public final String launchedFromPackage;
    public final int launchedFromPid;
    public final int launchedFromUid;
    public int lockTaskLaunchMode;
    public final ComponentName mActivityComponent;
    public final ActivityRecordInputSink mActivityRecordInputSink;
    public final AddStartingWindow mAddStartingWindow;
    public boolean mAllowCrossUidActivitySwitchFromBelow;
    public int mAllowedTouchUid;
    public AnimatingActivityRegistry mAnimatingActivityRegistry;
    public final boolean mAppActivityEmbeddingSplitsEnabled;
    public boolean mAppStopped;
    public final ActivityTaskManagerService mAtmService;
    public boolean mCameraCompatControlClickedByUser;
    public final boolean mCameraCompatControlEnabled;
    public int mCameraCompatControlState;
    public RemoteCallbackList<IScreenCaptureObserver> mCaptureCallbacks;
    public boolean mClientVisibilityDeferred;
    public final ColorDisplayService.ColorTransformController mColorTransformController;
    public ICompatCameraControlCallback mCompatCameraControlCallback;
    public CompatDisplayInsets mCompatDisplayInsets;
    public int mConfigurationSeq;
    public boolean mCurrentLaunchCanTurnScreenOn;
    public CustomAppTransition mCustomCloseTransition;
    public CustomAppTransition mCustomOpenTransition;
    public boolean mDeferHidingClient;
    public final Runnable mDestroyTimeoutRunnable;
    public boolean mDismissKeyguard;
    public boolean mEnableRecentsScreenshot;
    public boolean mEnteringAnimation;
    public Drawable mEnterpriseThumbnailDrawable;
    public boolean mForceSendResultForMediaProjection;
    public boolean mFreezingScreen;
    public boolean mHandleExitSplashScreen;
    @VisibleForTesting
    int mHandoverLaunchDisplayId;
    @VisibleForTesting
    TaskDisplayArea mHandoverTaskDisplayArea;
    public final boolean mHasSceneTransition;
    public boolean mHaveState;
    public Bundle mIcicle;
    public boolean mImeInsetsFrozenUntilStartInput;
    public boolean mInSizeCompatModeForBounds;
    public boolean mInheritShownWhenLocked;
    public InputApplicationHandle mInputApplicationHandle;
    public long mInputDispatchingTimeoutMillis;
    public boolean mIsAspectRatioApplied;
    public boolean mIsEligibleForFixedOrientationLetterbox;
    public boolean mIsExiting;
    public boolean mIsInputDroppedForAnimation;
    public boolean mLastAllDrawn;
    public boolean mLastAllReadyAtSync;
    public AppSaturationInfo mLastAppSaturationInfo;
    public boolean mLastContainsDismissKeyguardWindow;
    public boolean mLastContainsShowWhenLockedWindow;
    public boolean mLastContainsTurnScreenOnWindow;
    public boolean mLastDeferHidingClient;
    @DropInputMode
    public int mLastDropInputMode;
    public boolean mLastImeShown;
    public Intent mLastNewIntent;
    public Task mLastParentBeforePip;
    public MergedConfiguration mLastReportedConfiguration;
    public int mLastReportedDisplayId;
    public boolean mLastReportedMultiWindowMode;
    public boolean mLastReportedPictureInPictureMode;
    public boolean mLastSurfaceShowing;
    public ITaskFragmentOrganizer mLastTaskFragmentOrganizerBeforePip;
    public long mLastTransactionSequence;
    public IBinder mLaunchCookie;
    public ActivityRecord mLaunchIntoPipHostActivity;
    public WindowContainerToken mLaunchRootTask;
    public final int mLaunchSourceType;
    public final Runnable mLaunchTickRunnable;
    public boolean mLaunchedFromBubble;
    public Rect mLetterboxBoundsForFixedOrientationAndAspectRatio;
    public final LetterboxUiController mLetterboxUiController;
    public LocusId mLocusId;
    public boolean mNeedsLetterboxedAnimation;
    public int mNumDrawnWindows;
    public int mNumInterestingWindows;
    public boolean mOccludesParent;
    public boolean mOverrideTaskTransition;
    public boolean mPauseSchedulePendingForPip;
    public final Runnable mPauseTimeoutRunnable;
    public ActivityOptions mPendingOptions;
    public int mPendingRelaunchCount;
    public RemoteAnimationAdapter mPendingRemoteAnimation;
    public RemoteTransition mPendingRemoteTransition;
    public PersistableBundle mPersistentState;
    public int mRelaunchReason;
    public long mRelaunchStartTime;
    public RemoteAnimationDefinition mRemoteAnimationDefinition;
    public boolean mRemovingFromDisplay;
    public boolean mReportedDrawn;
    public final WindowState.UpdateReportedVisibilityResults mReportedVisibilityResults;
    public boolean mRequestForceTransition;
    public IBinder mRequestedLaunchingTaskFragmentToken;
    public final RootWindowContainer mRootWindowContainer;
    public int mRotationAnimationHint;
    @GuardedBy({"this"})
    public ActivityServiceConnectionsHolder mServiceConnectionsHolder;
    public boolean mShareIdentity;
    public final boolean mShowForAllUsers;
    public boolean mShowWhenLocked;
    public Rect mSizeCompatBounds;
    public float mSizeCompatScale;
    public SizeConfigurationBuckets mSizeConfigurations;
    public boolean mSplashScreenStyleSolidColor;
    public StartingData mStartingData;
    public StartingSurfaceController.StartingSurface mStartingSurface;
    public WindowState mStartingWindow;
    public State mState;
    public final Runnable mStopTimeoutRunnable;
    public final boolean mStyleFillsParent;
    public int mTargetSdk;
    public boolean mTaskOverlay;
    public final ActivityTaskSupervisor mTaskSupervisor;
    public final Rect mTmpBounds;
    public final Configuration mTmpConfig;
    public final Runnable mTransferSplashScreenTimeoutRunnable;
    public int mTransferringSplashScreenState;
    public int mTransitionChangeFlags;
    public boolean mTurnScreenOn;
    public final int mUserId;
    public boolean mVisible;
    public volatile boolean mVisibleForServiceConnection;
    public boolean mVisibleSetFromTransferredStartingWindow;
    public boolean mVoiceInteraction;
    public boolean mWaitForEnteringPinnedMode;
    public boolean mWillCloseOrEnterPip;
    public ArrayList<ReferrerIntent> newIntents;
    @VisibleForTesting
    boolean noDisplay;
    public CharSequence nonLocalizedLabel;
    public boolean nowVisible;
    public final String packageName;
    public long pauseTime;
    public HashSet<WeakReference<PendingIntentRecord>> pendingResults;
    public boolean pendingVoiceInteractionStart;
    public PictureInPictureParams pictureInPictureArgs;
    public boolean preserveWindowOnDeferredRelaunch;
    public final String processName;
    public boolean reportedVisible;
    public final int requestCode;
    public ComponentName requestedVrComponent;
    public final String resolvedType;
    public ActivityRecord resultTo;
    public final String resultWho;
    public ArrayList<ResultInfo> results;
    public ActivityOptions returningOptions;
    public final boolean rootVoiceInteraction;
    public final Binder shareableActivityToken;
    public final String shortComponentName;
    public boolean shouldDockBigOverlays;
    public boolean startingMoved;
    public final boolean stateNotNeeded;
    public boolean supportsEnterPipOnTaskSwitch;
    public Task task;
    public final String taskAffinity;
    public ActivityManager.TaskDescription taskDescription;
    public int theme;
    public long topResumedStateLossTime;
    public UriPermissionOwner uriPermissions;
    public boolean visibleIgnoringKeyguard;
    public IVoiceInteractionSession voiceSession;

    /* renamed from: com.android.server.wm.ActivityRecord$CustomAppTransition */
    /* loaded from: classes2.dex */
    public static class CustomAppTransition {
        public int mBackgroundColor;
        public int mEnterAnim;
        public int mExitAnim;
    }

    /* renamed from: com.android.server.wm.ActivityRecord$State */
    /* loaded from: classes2.dex */
    public enum State {
        INITIALIZING,
        STARTED,
        RESUMED,
        PAUSING,
        PAUSED,
        STOPPING,
        STOPPED,
        FINISHING,
        DESTROYING,
        DESTROYED,
        RESTARTING_PROCESS
    }

    public static int getCenterOffset(int i, int i2) {
        return (int) (((i - i2) + 1) * 0.5f);
    }

    public static boolean hasResizeChange(int i) {
        return (i & 3456) != 0;
    }

    public static boolean isResizeOnlyChange(int i) {
        return (i & (-3457)) == 0;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public ActivityRecord asActivityRecord() {
        return this;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean canCreateRemoteAnimationTarget() {
        return true;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean canCustomizeAppTransition() {
        return true;
    }

    @Override // com.android.server.p014wm.WindowToken, com.android.server.p014wm.WindowContainer
    public long getProtoFieldId() {
        return 1146756268038L;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean hasActivity() {
        return true;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean onChildVisibleRequestedChanged(WindowContainer windowContainer) {
        return false;
    }

    @Override // com.android.server.p014wm.WindowToken, com.android.server.p014wm.WindowContainer
    public void resetSurfacePositionForAnimationLeash(SurfaceControl.Transaction transaction) {
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean showSurfaceOnCreation() {
        return false;
    }

    public final void updateEnterpriseThumbnailDrawable(final Context context) {
        this.mEnterpriseThumbnailDrawable = ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getResources().getDrawable("WORK_PROFILE_ICON", "OUTLINE", "PROFILE_SWITCH_ANIMATION", new Supplier() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda35
            @Override // java.util.function.Supplier
            public final Object get() {
                Drawable drawable;
                drawable = context.getDrawable(17302404);
                return drawable;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(final float[] fArr, final float[] fArr2) {
        this.mWmService.f1164mH.post(new Runnable() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                ActivityRecord.this.lambda$new$1(fArr, fArr2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(float[] fArr, float[] fArr2) {
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mLastAppSaturationInfo == null) {
                    this.mLastAppSaturationInfo = new AppSaturationInfo();
                }
                this.mLastAppSaturationInfo.setSaturation(fArr, fArr2);
                updateColorTransform();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    @Override // com.android.server.p014wm.WindowToken, com.android.server.p014wm.WindowContainer
    @NeverCompile
    public void dump(PrintWriter printWriter, String str, boolean z) {
        ApplicationInfo applicationInfo;
        long uptimeMillis = SystemClock.uptimeMillis();
        printWriter.print(str);
        printWriter.print("packageName=");
        printWriter.print(this.packageName);
        printWriter.print(" processName=");
        printWriter.println(this.processName);
        printWriter.print(str);
        printWriter.print("launchedFromUid=");
        printWriter.print(this.launchedFromUid);
        printWriter.print(" launchedFromPackage=");
        printWriter.print(this.launchedFromPackage);
        printWriter.print(" launchedFromFeature=");
        printWriter.print(this.launchedFromFeatureId);
        printWriter.print(" userId=");
        printWriter.println(this.mUserId);
        printWriter.print(str);
        printWriter.print("app=");
        printWriter.println(this.app);
        printWriter.print(str);
        printWriter.println(this.intent.toInsecureString());
        printWriter.print(str);
        printWriter.print("rootOfTask=");
        printWriter.print(isRootOfTask());
        printWriter.print(" task=");
        printWriter.println(this.task);
        printWriter.print(str);
        printWriter.print("taskAffinity=");
        printWriter.println(this.taskAffinity);
        printWriter.print(str);
        printWriter.print("mActivityComponent=");
        printWriter.println(this.mActivityComponent.flattenToShortString());
        ActivityInfo activityInfo = this.info;
        if (activityInfo != null && (applicationInfo = activityInfo.applicationInfo) != null) {
            printWriter.print(str);
            printWriter.print("baseDir=");
            printWriter.println(applicationInfo.sourceDir);
            if (!Objects.equals(applicationInfo.sourceDir, applicationInfo.publicSourceDir)) {
                printWriter.print(str);
                printWriter.print("resDir=");
                printWriter.println(applicationInfo.publicSourceDir);
            }
            printWriter.print(str);
            printWriter.print("dataDir=");
            printWriter.println(applicationInfo.dataDir);
            if (applicationInfo.splitSourceDirs != null) {
                printWriter.print(str);
                printWriter.print("splitDir=");
                printWriter.println(Arrays.toString(applicationInfo.splitSourceDirs));
            }
        }
        printWriter.print(str);
        printWriter.print("stateNotNeeded=");
        printWriter.print(this.stateNotNeeded);
        printWriter.print(" componentSpecified=");
        printWriter.print(this.componentSpecified);
        printWriter.print(" mActivityType=");
        printWriter.println(WindowConfiguration.activityTypeToString(getActivityType()));
        if (this.rootVoiceInteraction) {
            printWriter.print(str);
            printWriter.print("rootVoiceInteraction=");
            printWriter.println(this.rootVoiceInteraction);
        }
        printWriter.print(str);
        printWriter.print("compat=");
        printWriter.print(this.mAtmService.compatibilityInfoForPackageLocked(this.info.applicationInfo));
        printWriter.print(" labelRes=0x");
        printWriter.print(Integer.toHexString(this.labelRes));
        printWriter.print(" icon=0x");
        printWriter.print(Integer.toHexString(this.icon));
        printWriter.print(" theme=0x");
        printWriter.println(Integer.toHexString(this.theme));
        printWriter.println(str + "mLastReportedConfigurations:");
        this.mLastReportedConfiguration.dump(printWriter, str + "  ");
        printWriter.print(str);
        printWriter.print("CurrentConfiguration=");
        printWriter.println(getConfiguration());
        if (!getRequestedOverrideConfiguration().equals(Configuration.EMPTY)) {
            printWriter.println(str + "RequestedOverrideConfiguration=" + getRequestedOverrideConfiguration());
        }
        if (!getResolvedOverrideConfiguration().equals(getRequestedOverrideConfiguration())) {
            printWriter.println(str + "ResolvedOverrideConfiguration=" + getResolvedOverrideConfiguration());
        }
        if (!matchParentBounds()) {
            printWriter.println(str + "bounds=" + getBounds());
        }
        if (this.resultTo != null || this.resultWho != null) {
            printWriter.print(str);
            printWriter.print("resultTo=");
            printWriter.print(this.resultTo);
            printWriter.print(" resultWho=");
            printWriter.print(this.resultWho);
            printWriter.print(" resultCode=");
            printWriter.println(this.requestCode);
        }
        ActivityManager.TaskDescription taskDescription = this.taskDescription;
        if (taskDescription != null && (taskDescription.getIconFilename() != null || this.taskDescription.getLabel() != null || this.taskDescription.getPrimaryColor() != 0)) {
            printWriter.print(str);
            printWriter.print("taskDescription:");
            printWriter.print(" label=\"");
            printWriter.print(this.taskDescription.getLabel());
            printWriter.print("\"");
            printWriter.print(" icon=");
            printWriter.print(this.taskDescription.getInMemoryIcon() != null ? this.taskDescription.getInMemoryIcon().getByteCount() + " bytes" : "null");
            printWriter.print(" iconResource=");
            printWriter.print(this.taskDescription.getIconResourcePackage());
            printWriter.print("/");
            printWriter.print(this.taskDescription.getIconResource());
            printWriter.print(" iconFilename=");
            printWriter.print(this.taskDescription.getIconFilename());
            printWriter.print(" primaryColor=");
            printWriter.println(Integer.toHexString(this.taskDescription.getPrimaryColor()));
            printWriter.print(str);
            printWriter.print("  backgroundColor=");
            printWriter.print(Integer.toHexString(this.taskDescription.getBackgroundColor()));
            printWriter.print(" statusBarColor=");
            printWriter.print(Integer.toHexString(this.taskDescription.getStatusBarColor()));
            printWriter.print(" navigationBarColor=");
            printWriter.println(Integer.toHexString(this.taskDescription.getNavigationBarColor()));
            printWriter.print(str);
            printWriter.print(" backgroundColorFloating=");
            printWriter.println(Integer.toHexString(this.taskDescription.getBackgroundColorFloating()));
        }
        if (this.results != null) {
            printWriter.print(str);
            printWriter.print("results=");
            printWriter.println(this.results);
        }
        HashSet<WeakReference<PendingIntentRecord>> hashSet = this.pendingResults;
        if (hashSet != null && hashSet.size() > 0) {
            printWriter.print(str);
            printWriter.println("Pending Results:");
            Iterator<WeakReference<PendingIntentRecord>> it = this.pendingResults.iterator();
            while (it.hasNext()) {
                WeakReference<PendingIntentRecord> next = it.next();
                PendingIntentRecord pendingIntentRecord = next != null ? next.get() : null;
                printWriter.print(str);
                printWriter.print("  - ");
                if (pendingIntentRecord == null) {
                    printWriter.println("null");
                } else {
                    printWriter.println(pendingIntentRecord);
                    pendingIntentRecord.dump(printWriter, str + "    ");
                }
            }
        }
        ArrayList<ReferrerIntent> arrayList = this.newIntents;
        if (arrayList != null && arrayList.size() > 0) {
            printWriter.print(str);
            printWriter.println("Pending New Intents:");
            for (int i = 0; i < this.newIntents.size(); i++) {
                Intent intent = this.newIntents.get(i);
                printWriter.print(str);
                printWriter.print("  - ");
                if (intent == null) {
                    printWriter.println("null");
                } else {
                    printWriter.println(intent.toShortString(false, true, false, false));
                }
            }
        }
        if (this.mPendingOptions != null) {
            printWriter.print(str);
            printWriter.print("pendingOptions=");
            printWriter.println(this.mPendingOptions);
        }
        if (this.mPendingRemoteAnimation != null) {
            printWriter.print(str);
            printWriter.print("pendingRemoteAnimationCallingPid=");
            printWriter.println(this.mPendingRemoteAnimation.getCallingPid());
        }
        if (this.mPendingRemoteTransition != null) {
            printWriter.print(str + " pendingRemoteTransition=" + this.mPendingRemoteTransition.getRemoteTransition());
        }
        AppTimeTracker appTimeTracker = this.appTimeTracker;
        if (appTimeTracker != null) {
            appTimeTracker.dumpWithHeader(printWriter, str, false);
        }
        UriPermissionOwner uriPermissionOwner = this.uriPermissions;
        if (uriPermissionOwner != null) {
            uriPermissionOwner.dump(printWriter, str);
        }
        printWriter.print(str);
        printWriter.print("launchFailed=");
        printWriter.print(this.launchFailed);
        printWriter.print(" launchCount=");
        printWriter.print(this.launchCount);
        printWriter.print(" lastLaunchTime=");
        long j = this.lastLaunchTime;
        if (j == 0) {
            printWriter.print("0");
        } else {
            TimeUtils.formatDuration(j, uptimeMillis, printWriter);
        }
        printWriter.println();
        if (this.mLaunchCookie != null) {
            printWriter.print(str);
            printWriter.print("launchCookie=");
            printWriter.println(this.mLaunchCookie);
        }
        if (this.mLaunchRootTask != null) {
            printWriter.print(str);
            printWriter.print("mLaunchRootTask=");
            printWriter.println(this.mLaunchRootTask);
        }
        printWriter.print(str);
        printWriter.print("mHaveState=");
        printWriter.print(this.mHaveState);
        printWriter.print(" mIcicle=");
        printWriter.println(this.mIcicle);
        printWriter.print(str);
        printWriter.print("state=");
        printWriter.print(this.mState);
        printWriter.print(" delayedResume=");
        printWriter.print(this.delayedResume);
        printWriter.print(" finishing=");
        printWriter.println(this.finishing);
        printWriter.print(str);
        printWriter.print("keysPaused=");
        printWriter.print(this.keysPaused);
        printWriter.print(" inHistory=");
        printWriter.print(this.inHistory);
        printWriter.print(" idle=");
        printWriter.println(this.idle);
        printWriter.print(str);
        printWriter.print("occludesParent=");
        printWriter.print(occludesParent());
        printWriter.print(" noDisplay=");
        printWriter.print(this.noDisplay);
        printWriter.print(" immersive=");
        printWriter.print(this.immersive);
        printWriter.print(" launchMode=");
        printWriter.println(this.launchMode);
        printWriter.print(str);
        printWriter.print("frozenBeforeDestroy=");
        printWriter.print(this.frozenBeforeDestroy);
        printWriter.print(" forceNewConfig=");
        printWriter.println(this.forceNewConfig);
        printWriter.print(str);
        printWriter.print("mActivityType=");
        printWriter.println(WindowConfiguration.activityTypeToString(getActivityType()));
        printWriter.print(str);
        printWriter.print("mImeInsetsFrozenUntilStartInput=");
        printWriter.println(this.mImeInsetsFrozenUntilStartInput);
        if (this.requestedVrComponent != null) {
            printWriter.print(str);
            printWriter.print("requestedVrComponent=");
            printWriter.println(this.requestedVrComponent);
        }
        super.dump(printWriter, str, z);
        if (this.mVoiceInteraction) {
            printWriter.println(str + "mVoiceInteraction=true");
        }
        printWriter.print(str);
        printWriter.print("mOccludesParent=");
        printWriter.println(this.mOccludesParent);
        printWriter.print(str);
        printWriter.print("overrideOrientation=");
        printWriter.println(ActivityInfo.screenOrientationToString(getOverrideOrientation()));
        printWriter.print(str);
        printWriter.print("requestedOrientation=");
        printWriter.println(ActivityInfo.screenOrientationToString(super.getOverrideOrientation()));
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("mVisibleRequested=");
        sb.append(this.mVisibleRequested);
        sb.append(" mVisible=");
        sb.append(this.mVisible);
        sb.append(" mClientVisible=");
        sb.append(isClientVisible());
        sb.append(this.mDeferHidingClient ? " mDeferHidingClient=" + this.mDeferHidingClient : "");
        sb.append(" reportedDrawn=");
        sb.append(this.mReportedDrawn);
        sb.append(" reportedVisible=");
        sb.append(this.reportedVisible);
        printWriter.println(sb.toString());
        if (this.paused) {
            printWriter.print(str);
            printWriter.print("paused=");
            printWriter.println(this.paused);
        }
        if (this.mAppStopped) {
            printWriter.print(str);
            printWriter.print("mAppStopped=");
            printWriter.println(this.mAppStopped);
        }
        if (this.mNumInterestingWindows != 0 || this.mNumDrawnWindows != 0 || this.allDrawn || this.mLastAllDrawn) {
            printWriter.print(str);
            printWriter.print("mNumInterestingWindows=");
            printWriter.print(this.mNumInterestingWindows);
            printWriter.print(" mNumDrawnWindows=");
            printWriter.print(this.mNumDrawnWindows);
            printWriter.print(" allDrawn=");
            printWriter.print(this.allDrawn);
            printWriter.print(" lastAllDrawn=");
            printWriter.print(this.mLastAllDrawn);
            printWriter.println(")");
        }
        if (this.mStartingData != null || this.firstWindowDrawn || this.mIsExiting) {
            printWriter.print(str);
            printWriter.print("startingData=");
            printWriter.print(this.mStartingData);
            printWriter.print(" firstWindowDrawn=");
            printWriter.print(this.firstWindowDrawn);
            printWriter.print(" mIsExiting=");
            printWriter.println(this.mIsExiting);
        }
        if (this.mStartingWindow != null || this.mStartingData != null || this.mStartingSurface != null || this.startingMoved || this.mVisibleSetFromTransferredStartingWindow) {
            printWriter.print(str);
            printWriter.print("startingWindow=");
            printWriter.print(this.mStartingWindow);
            printWriter.print(" startingSurface=");
            printWriter.print(this.mStartingSurface);
            printWriter.print(" startingDisplayed=");
            printWriter.print(isStartingWindowDisplayed());
            printWriter.print(" startingMoved=");
            printWriter.print(this.startingMoved);
            printWriter.println(" mVisibleSetFromTransferredStartingWindow=" + this.mVisibleSetFromTransferredStartingWindow);
        }
        if (this.mPendingRelaunchCount != 0) {
            printWriter.print(str);
            printWriter.print("mPendingRelaunchCount=");
            printWriter.println(this.mPendingRelaunchCount);
        }
        if (this.mSizeCompatScale != 1.0f || this.mSizeCompatBounds != null) {
            printWriter.println(str + "mSizeCompatScale=" + this.mSizeCompatScale + " mSizeCompatBounds=" + this.mSizeCompatBounds);
        }
        if (this.mRemovingFromDisplay) {
            printWriter.println(str + "mRemovingFromDisplay=" + this.mRemovingFromDisplay);
        }
        if (this.lastVisibleTime != 0 || this.nowVisible) {
            printWriter.print(str);
            printWriter.print("nowVisible=");
            printWriter.print(this.nowVisible);
            printWriter.print(" lastVisibleTime=");
            long j2 = this.lastVisibleTime;
            if (j2 == 0) {
                printWriter.print("0");
            } else {
                TimeUtils.formatDuration(j2, uptimeMillis, printWriter);
            }
            printWriter.println();
        }
        if (this.mDeferHidingClient) {
            printWriter.println(str + "mDeferHidingClient=" + this.mDeferHidingClient);
        }
        if (this.deferRelaunchUntilPaused || this.configChangeFlags != 0) {
            printWriter.print(str);
            printWriter.print("deferRelaunchUntilPaused=");
            printWriter.print(this.deferRelaunchUntilPaused);
            printWriter.print(" configChangeFlags=");
            printWriter.println(Integer.toHexString(this.configChangeFlags));
        }
        if (this.mServiceConnectionsHolder != null) {
            printWriter.print(str);
            printWriter.print("connections=");
            printWriter.println(this.mServiceConnectionsHolder);
        }
        if (this.info != null) {
            printWriter.println(str + "resizeMode=" + ActivityInfo.resizeModeToString(this.info.resizeMode));
            printWriter.println(str + "mLastReportedMultiWindowMode=" + this.mLastReportedMultiWindowMode + " mLastReportedPictureInPictureMode=" + this.mLastReportedPictureInPictureMode);
            if (this.info.supportsPictureInPicture()) {
                printWriter.println(str + "supportsPictureInPicture=" + this.info.supportsPictureInPicture());
                printWriter.println(str + "supportsEnterPipOnTaskSwitch: " + this.supportsEnterPipOnTaskSwitch);
            }
            if (getMaxAspectRatio() != 0.0f) {
                printWriter.println(str + "maxAspectRatio=" + getMaxAspectRatio());
            }
            float minAspectRatio = getMinAspectRatio();
            if (minAspectRatio != 0.0f) {
                printWriter.println(str + "minAspectRatio=" + minAspectRatio);
            }
            if (minAspectRatio != this.info.getManifestMinAspectRatio()) {
                printWriter.println(str + "manifestMinAspectRatio=" + this.info.getManifestMinAspectRatio());
            }
            printWriter.println(str + "supportsSizeChanges=" + ActivityInfo.sizeChangesSupportModeToString(this.info.supportsSizeChanges()));
            if (this.info.configChanges != 0) {
                printWriter.println(str + "configChanges=0x" + Integer.toHexString(this.info.configChanges));
            }
            printWriter.println(str + "neverSandboxDisplayApis=" + this.info.neverSandboxDisplayApis(sConstrainDisplayApisConfig));
            printWriter.println(str + "alwaysSandboxDisplayApis=" + this.info.alwaysSandboxDisplayApis(sConstrainDisplayApisConfig));
        }
        if (this.mLastParentBeforePip != null) {
            printWriter.println(str + "lastParentTaskIdBeforePip=" + this.mLastParentBeforePip.mTaskId);
        }
        if (this.mLaunchIntoPipHostActivity != null) {
            printWriter.println(str + "launchIntoPipHostActivity=" + this.mLaunchIntoPipHostActivity);
        }
        this.mLetterboxUiController.dump(printWriter, str);
        printWriter.println(str + "mCameraCompatControlState=" + TaskInfo.cameraCompatControlStateToString(this.mCameraCompatControlState));
        printWriter.println(str + "mCameraCompatControlEnabled=" + this.mCameraCompatControlEnabled);
    }

    public static boolean dumpActivity(FileDescriptor fileDescriptor, PrintWriter printWriter, int i, ActivityRecord activityRecord, String str, String str2, boolean z, boolean z2, boolean z3, String str3, boolean z4, Runnable runnable, Task task) {
        if (str3 == null || str3.equals(activityRecord.packageName)) {
            boolean z5 = !z2 && (z || !activityRecord.isInHistory());
            if (z4) {
                printWriter.println("");
            }
            if (runnable != null) {
                runnable.run();
            }
            String str4 = str + "  ";
            String[] strArr = new String[0];
            if (task != activityRecord.getTask()) {
                Task task2 = activityRecord.getTask();
                printWriter.print(str);
                printWriter.print(z5 ? "* " : "  ");
                printWriter.println(task2);
                if (z5) {
                    task2.dump(printWriter, str + "  ");
                } else if (z && task2.intent != null) {
                    printWriter.print(str);
                    printWriter.print("  ");
                    printWriter.println(task2.intent.toInsecureString());
                }
            }
            printWriter.print(str);
            printWriter.print(z5 ? "* " : "    ");
            printWriter.print(str2);
            printWriter.print(" #");
            printWriter.print(i);
            printWriter.print(": ");
            printWriter.println(activityRecord);
            if (z5) {
                activityRecord.dump(printWriter, str4, true);
            } else if (z) {
                printWriter.print(str4);
                printWriter.println(activityRecord.intent.toInsecureString());
                if (activityRecord.app != null) {
                    printWriter.print(str4);
                    printWriter.println(activityRecord.app);
                }
            }
            if (z3 && activityRecord.attachedToProcess()) {
                printWriter.flush();
                try {
                    TransferPipe transferPipe = new TransferPipe();
                    activityRecord.app.getThread().dumpActivity(transferPipe.getWriteFd(), activityRecord.token, str4, strArr);
                    transferPipe.go(fileDescriptor, 2000L);
                    transferPipe.kill();
                } catch (RemoteException unused) {
                    printWriter.println(str4 + "Got a RemoteException while dumping the activity");
                } catch (IOException e) {
                    printWriter.println(str4 + "Failure while dumping the activity: " + e);
                }
            }
            return true;
        }
        return false;
    }

    public void setSavedState(Bundle bundle) {
        this.mIcicle = bundle;
        this.mHaveState = bundle != null;
    }

    public Bundle getSavedState() {
        return this.mIcicle;
    }

    public boolean hasSavedState() {
        return this.mHaveState;
    }

    public PersistableBundle getPersistentSavedState() {
        return this.mPersistentState;
    }

    public void updateApplicationInfo(ApplicationInfo applicationInfo) {
        this.info.applicationInfo = applicationInfo;
    }

    public void setSizeConfigurations(SizeConfigurationBuckets sizeConfigurationBuckets) {
        this.mSizeConfigurations = sizeConfigurationBuckets;
    }

    public final void scheduleActivityMovedToDisplay(int i, Configuration configuration) {
        if (!attachedToProcess()) {
            if (ProtoLogCache.WM_DEBUG_SWITCH_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_SWITCH, -1495062622, 4, (String) null, new Object[]{String.valueOf(this), Long.valueOf(i)});
                return;
            }
            return;
        }
        try {
            if (ProtoLogCache.WM_DEBUG_SWITCH_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_SWITCH, 374506950, 4, (String) null, new Object[]{String.valueOf(this), Long.valueOf(i), String.valueOf(configuration)});
            }
            this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ClientTransactionItem) MoveToDisplayItem.obtain(i, configuration));
        } catch (RemoteException unused) {
        }
    }

    public final void scheduleConfigurationChanged(Configuration configuration) {
        if (!attachedToProcess()) {
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 1040675582, 0, (String) null, new Object[]{String.valueOf(this)});
                return;
            }
            return;
        }
        try {
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 969323241, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(configuration)});
            }
            this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ClientTransactionItem) ActivityConfigurationChangeItem.obtain(configuration));
        } catch (RemoteException unused) {
        }
    }

    public boolean scheduleTopResumedActivityChanged(boolean z) {
        if (!attachedToProcess()) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_STATES, -1193946201, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            return false;
        }
        if (z) {
            this.app.addToPendingTop();
        }
        try {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1305966693, 12, (String) null, new Object[]{String.valueOf(this), Boolean.valueOf(z)});
            }
            this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ClientTransactionItem) TopResumedActivityChangeItem.obtain(z));
            return true;
        } catch (RemoteException e) {
            Slog.w("ActivityTaskManager", "Failed to send top-resumed=" + z + " to " + this, e);
            return false;
        }
    }

    public void updateMultiWindowMode() {
        boolean inMultiWindowMode;
        Task task = this.task;
        if (task == null || task.getRootTask() == null || !attachedToProcess() || (inMultiWindowMode = inMultiWindowMode()) == this.mLastReportedMultiWindowMode) {
            return;
        }
        if (!inMultiWindowMode && this.mLastReportedPictureInPictureMode) {
            updatePictureInPictureMode(null, false);
            return;
        }
        this.mLastReportedMultiWindowMode = inMultiWindowMode;
        ensureActivityConfiguration(0, true);
    }

    public void updatePictureInPictureMode(Rect rect, boolean z) {
        Task task = this.task;
        if (task == null || task.getRootTask() == null || !attachedToProcess()) {
            return;
        }
        boolean z2 = inPinnedWindowingMode() && rect != null;
        if (z2 != this.mLastReportedPictureInPictureMode || z) {
            this.mLastReportedPictureInPictureMode = z2;
            this.mLastReportedMultiWindowMode = z2;
            ensureActivityConfiguration(0, true, true);
            if (z2 && findMainWindow() == null) {
                EventLog.writeEvent(1397638484, "265293293", -1, "");
                removeImmediately();
            }
        }
    }

    public Task getTask() {
        return this.task;
    }

    public TaskFragment getTaskFragment() {
        WindowContainer parent = getParent();
        if (parent != null) {
            return parent.asTaskFragment();
        }
        return null;
    }

    public final boolean shouldStartChangeTransition(TaskFragment taskFragment, TaskFragment taskFragment2) {
        return (taskFragment == null || taskFragment2 == null || !canStartChangeTransition() || !taskFragment.isOrganizedTaskFragment() || taskFragment.getBounds().equals(taskFragment2.getBounds())) ? false : true;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean canStartChangeTransition() {
        Task task = getTask();
        return (task == null || task.isDragResizing() || !super.canStartChangeTransition()) ? false : true;
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void onParentChanged(ConfigurationContainer configurationContainer, ConfigurationContainer configurationContainer2) {
        TaskFragment taskFragment = (TaskFragment) configurationContainer2;
        TaskFragment taskFragment2 = (TaskFragment) configurationContainer;
        Task task = taskFragment != null ? taskFragment.getTask() : null;
        Task task2 = taskFragment2 != null ? taskFragment2.getTask() : null;
        this.task = task2;
        if (shouldStartChangeTransition(taskFragment2, taskFragment)) {
            if (this.mTransitionController.isShellTransitionsEnabled()) {
                initializeChangeTransition(getBounds());
            } else {
                taskFragment2.initializeChangeTransition(getBounds(), getSurfaceControl());
            }
        }
        super.onParentChanged(taskFragment2, taskFragment);
        if (isPersistable()) {
            if (task != null) {
                this.mAtmService.notifyTaskPersisterLocked(task, false);
            }
            if (task2 != null) {
                this.mAtmService.notifyTaskPersisterLocked(task2, false);
            }
        }
        if (taskFragment == null && taskFragment2 != null) {
            this.mVoiceInteraction = task2.voiceSession != null;
            task2.updateOverrideConfigurationFromLaunchBounds();
            this.mLastReportedMultiWindowMode = inMultiWindowMode();
            this.mLastReportedPictureInPictureMode = inPinnedWindowingMode();
        }
        if (this.task == null && getDisplayContent() != null) {
            getDisplayContent().mClosingApps.remove(this);
        }
        Task rootTask = getRootTask();
        updateAnimatingActivityRegistry();
        Task task3 = this.task;
        if (task3 == this.mLastParentBeforePip && task3 != null) {
            this.mAtmService.mWindowOrganizerController.mTaskFragmentOrganizerController.onActivityReparentedToTask(this);
            clearLastParentBeforePip();
        }
        updateColorTransform();
        if (taskFragment != null) {
            taskFragment.cleanUpActivityReferences(this);
            this.mRequestedLaunchingTaskFragmentToken = null;
        }
        if (taskFragment2 != null) {
            if (isState(State.RESUMED)) {
                taskFragment2.setResumedActivity(this, "onParentChanged");
            }
            this.mLetterboxUiController.onActivityParentChanged(taskFragment2);
        }
        if (rootTask != null && rootTask.topRunningActivity() == this && this.firstWindowDrawn) {
            rootTask.setHasBeenVisible(true);
        }
        updateUntrustedEmbeddingInputProtection();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void setSurfaceControl(SurfaceControl surfaceControl) {
        super.setSurfaceControl(surfaceControl);
        if (surfaceControl != null) {
            this.mLastDropInputMode = 0;
            updateUntrustedEmbeddingInputProtection();
        }
    }

    public void setDropInputForAnimation(boolean z) {
        if (this.mIsInputDroppedForAnimation == z) {
            return;
        }
        this.mIsInputDroppedForAnimation = z;
        updateUntrustedEmbeddingInputProtection();
    }

    public final void updateUntrustedEmbeddingInputProtection() {
        if (getSurfaceControl() == null) {
            return;
        }
        if (this.mIsInputDroppedForAnimation) {
            setDropInputMode(1);
        } else if (isEmbeddedInUntrustedMode()) {
            setDropInputMode(2);
        } else {
            setDropInputMode(0);
        }
    }

    @VisibleForTesting
    public void setDropInputMode(@DropInputMode int i) {
        if (this.mLastDropInputMode != i) {
            this.mLastDropInputMode = i;
            this.mWmService.mTransactionFactory.get().setDropInputMode(getSurfaceControl(), i).apply();
        }
    }

    public final boolean isEmbeddedInUntrustedMode() {
        TaskFragment organizedTaskFragment = getOrganizedTaskFragment();
        if (organizedTaskFragment == null) {
            return false;
        }
        return !organizedTaskFragment.isAllowedToEmbedActivityInTrustedMode(this);
    }

    public void updateAnimatingActivityRegistry() {
        Task rootTask = getRootTask();
        AnimatingActivityRegistry animatingActivityRegistry = rootTask != null ? rootTask.getAnimatingActivityRegistry() : null;
        AnimatingActivityRegistry animatingActivityRegistry2 = this.mAnimatingActivityRegistry;
        if (animatingActivityRegistry2 != null && animatingActivityRegistry2 != animatingActivityRegistry) {
            animatingActivityRegistry2.notifyFinished(this);
        }
        this.mAnimatingActivityRegistry = animatingActivityRegistry;
    }

    public void setLastParentBeforePip(ActivityRecord activityRecord) {
        Task task;
        TaskFragment organizedTaskFragment;
        if (activityRecord == null) {
            task = getTask();
        } else {
            task = activityRecord.getTask();
        }
        this.mLastParentBeforePip = task;
        task.mChildPipActivity = this;
        this.mLaunchIntoPipHostActivity = activityRecord;
        if (activityRecord == null) {
            organizedTaskFragment = getOrganizedTaskFragment();
        } else {
            organizedTaskFragment = activityRecord.getOrganizedTaskFragment();
        }
        this.mLastTaskFragmentOrganizerBeforePip = organizedTaskFragment != null ? organizedTaskFragment.getTaskFragmentOrganizer() : null;
    }

    public final void clearLastParentBeforePip() {
        Task task = this.mLastParentBeforePip;
        if (task != null) {
            task.mChildPipActivity = null;
            this.mLastParentBeforePip = null;
        }
        this.mLaunchIntoPipHostActivity = null;
        this.mLastTaskFragmentOrganizerBeforePip = null;
    }

    public Task getLastParentBeforePip() {
        return this.mLastParentBeforePip;
    }

    public final void updateColorTransform() {
        if (this.mSurfaceControl == null || this.mLastAppSaturationInfo == null) {
            return;
        }
        SurfaceControl.Transaction pendingTransaction = getPendingTransaction();
        SurfaceControl surfaceControl = this.mSurfaceControl;
        AppSaturationInfo appSaturationInfo = this.mLastAppSaturationInfo;
        pendingTransaction.setColorTransform(surfaceControl, appSaturationInfo.mMatrix, appSaturationInfo.mTranslation);
        this.mWmService.scheduleAnimationLocked();
    }

    @Override // com.android.server.p014wm.WindowToken, com.android.server.p014wm.WindowContainer
    public void onDisplayChanged(DisplayContent displayContent) {
        DisplayContent displayContent2 = this.mDisplayContent;
        super.onDisplayChanged(displayContent);
        DisplayContent displayContent3 = this.mDisplayContent;
        if (displayContent2 == displayContent3) {
            return;
        }
        displayContent3.onRunningActivityChanged();
        if (displayContent2 == null) {
            return;
        }
        displayContent2.onRunningActivityChanged();
        this.mTransitionController.collect(this);
        if (displayContent2.mOpeningApps.remove(this)) {
            this.mDisplayContent.mOpeningApps.add(this);
            this.mDisplayContent.transferAppTransitionFrom(displayContent2);
            this.mDisplayContent.executeAppTransition();
        }
        displayContent2.mClosingApps.remove(this);
        displayContent2.getDisplayPolicy().removeRelaunchingApp(this);
        if (displayContent2.mFocusedApp == this) {
            displayContent2.setFocusedApp(null);
            if (displayContent.getTopMostActivity() == this) {
                displayContent.setFocusedApp(this);
            }
        }
        this.mLetterboxUiController.onMovedToDisplay(this.mDisplayContent.getDisplayId());
    }

    public void layoutLetterbox(WindowState windowState) {
        this.mLetterboxUiController.layoutLetterbox(windowState);
    }

    public boolean hasWallpaperBackgroundForLetterbox() {
        return this.mLetterboxUiController.hasWallpaperBackgroundForLetterbox();
    }

    public void updateLetterboxSurface(WindowState windowState, SurfaceControl.Transaction transaction) {
        this.mLetterboxUiController.updateLetterboxSurface(windowState, transaction);
    }

    public void updateLetterboxSurface(WindowState windowState) {
        this.mLetterboxUiController.updateLetterboxSurface(windowState);
    }

    public Rect getLetterboxInsets() {
        return this.mLetterboxUiController.getLetterboxInsets();
    }

    public void getLetterboxInnerBounds(Rect rect) {
        this.mLetterboxUiController.getLetterboxInnerBounds(rect);
    }

    public void updateCameraCompatState(boolean z, boolean z2, ICompatCameraControlCallback iCompatCameraControlCallback) {
        if (isCameraCompatControlEnabled()) {
            if (this.mCameraCompatControlClickedByUser && (z || this.mCameraCompatControlState == 3)) {
                return;
            }
            this.mCompatCameraControlCallback = iCompatCameraControlCallback;
            int i = !z ? 0 : z2 ? 2 : 1;
            if (setCameraCompatControlState(i)) {
                this.mTaskSupervisor.getActivityMetricsLogger().logCameraCompatControlAppearedEventReported(i, this.info.applicationInfo.uid);
                if (i == 0) {
                    this.mCameraCompatControlClickedByUser = false;
                    this.mCompatCameraControlCallback = null;
                }
                getTask().dispatchTaskInfoChangedIfNeeded(true);
                getDisplayContent().setLayoutNeeded();
                this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
            }
        }
    }

    public void updateCameraCompatStateFromUser(int i) {
        if (isCameraCompatControlEnabled()) {
            if (i == 0) {
                Slog.w("ActivityTaskManager", "Unexpected hidden state in updateCameraCompatState");
                return;
            }
            boolean cameraCompatControlState = setCameraCompatControlState(i);
            this.mCameraCompatControlClickedByUser = true;
            if (cameraCompatControlState) {
                this.mTaskSupervisor.getActivityMetricsLogger().logCameraCompatControlClickedEventReported(i, this.info.applicationInfo.uid);
                if (i == 3) {
                    this.mCompatCameraControlCallback = null;
                    return;
                }
                ICompatCameraControlCallback iCompatCameraControlCallback = this.mCompatCameraControlCallback;
                if (iCompatCameraControlCallback == null) {
                    Slog.w("ActivityTaskManager", "Callback for a camera compat control is null");
                    return;
                }
                try {
                    if (i == 2) {
                        iCompatCameraControlCallback.applyCameraCompatTreatment();
                    } else {
                        iCompatCameraControlCallback.revertCameraCompatTreatment();
                    }
                } catch (RemoteException e) {
                    Slog.e("ActivityTaskManager", "Unable to apply or revert camera compat treatment", e);
                }
            }
        }
    }

    public final boolean setCameraCompatControlState(int i) {
        if (isCameraCompatControlEnabled() && this.mCameraCompatControlState != i) {
            this.mCameraCompatControlState = i;
            return true;
        }
        return false;
    }

    public int getCameraCompatControlState() {
        return this.mCameraCompatControlState;
    }

    @VisibleForTesting
    public boolean isCameraCompatControlEnabled() {
        return this.mCameraCompatControlEnabled;
    }

    public boolean isFullyTransparentBarAllowed(Rect rect) {
        return this.mLetterboxUiController.isFullyTransparentBarAllowed(rect);
    }

    /* renamed from: com.android.server.wm.ActivityRecord$Token */
    /* loaded from: classes2.dex */
    public static class Token extends Binder {
        public WeakReference<ActivityRecord> mActivityRef;

        public Token() {
        }

        public String toString() {
            return "Token{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.mActivityRef.get() + "}";
        }
    }

    public static ActivityRecord forToken(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        try {
            return ((Token) iBinder).mActivityRef.get();
        } catch (ClassCastException e) {
            Slog.w("ActivityTaskManager", "Bad activity token: " + iBinder, e);
            return null;
        }
    }

    public static ActivityRecord forTokenLocked(IBinder iBinder) {
        ActivityRecord forToken = forToken(iBinder);
        if (forToken == null || forToken.getRootTask() == null) {
            return null;
        }
        return forToken;
    }

    public static boolean isResolverActivity(String str) {
        return ResolverActivity.class.getName().equals(str);
    }

    public boolean isResolverOrDelegateActivity() {
        return isResolverActivity(this.mActivityComponent.getClassName()) || Objects.equals(this.mActivityComponent, this.mAtmService.mTaskSupervisor.getSystemChooserActivity());
    }

    public boolean isResolverOrChildActivity() {
        if (PackageManagerShellCommandDataLoader.PACKAGE.equals(this.packageName)) {
            try {
                return ResolverActivity.class.isAssignableFrom(Object.class.getClassLoader().loadClass(this.mActivityComponent.getClassName()));
            } catch (ClassNotFoundException unused) {
                return false;
            }
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:117:0x03c6, code lost:
        if (r19.mContext.getPackageManager().getProperty("android.window.PROPERTY_ACTIVITY_EMBEDDING_SPLITS_ENABLED", r3).getBoolean() != false) goto L102;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ActivityRecord(ActivityTaskManagerService activityTaskManagerService, WindowProcessController windowProcessController, int i, int i2, String str, String str2, Intent intent, String str3, ActivityInfo activityInfo, Configuration configuration, ActivityRecord activityRecord, String str4, int i3, boolean z, boolean z2, ActivityTaskSupervisor activityTaskSupervisor, ActivityOptions activityOptions, ActivityRecord activityRecord2, PersistableBundle persistableBundle, ActivityManager.TaskDescription taskDescription, long j) {
        super(activityTaskManagerService.mWindowManager, new Token(), 2, true, null, false);
        boolean z3;
        int i4;
        String str5;
        int i5;
        this.mHandoverLaunchDisplayId = -1;
        this.createTime = System.currentTimeMillis();
        boolean z4 = true;
        this.mHaveState = true;
        this.pictureInPictureArgs = new PictureInPictureParams.Builder().build();
        this.mSplashScreenStyleSolidColor = false;
        this.mPauseSchedulePendingForPip = false;
        this.mTaskOverlay = false;
        this.mRelaunchReason = 0;
        this.mForceSendResultForMediaProjection = false;
        this.mRemovingFromDisplay = false;
        this.mReportedVisibilityResults = new WindowState.UpdateReportedVisibilityResults();
        this.mCurrentLaunchCanTurnScreenOn = true;
        this.mInputDispatchingTimeoutMillis = InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
        this.mLastTransactionSequence = Long.MIN_VALUE;
        this.mLastAllReadyAtSync = false;
        this.mSizeCompatScale = 1.0f;
        this.mInSizeCompatModeForBounds = false;
        this.mIsAspectRatioApplied = false;
        this.mCameraCompatControlState = 0;
        this.mEnableRecentsScreenshot = true;
        this.mLastDropInputMode = 0;
        this.mTransferringSplashScreenState = 0;
        this.mRotationAnimationHint = -1;
        ColorDisplayService.ColorTransformController colorTransformController = new ColorDisplayService.ColorTransformController() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda29
            @Override // com.android.server.display.color.ColorDisplayService.ColorTransformController
            public final void applyAppSaturation(float[] fArr, float[] fArr2) {
                ActivityRecord.this.lambda$new$2(fArr, fArr2);
            }
        };
        this.mColorTransformController = colorTransformController;
        this.mTmpConfig = new Configuration();
        this.mTmpBounds = new Rect();
        this.assistToken = new Binder();
        this.shareableActivityToken = new Binder();
        this.mPauseTimeoutRunnable = new Runnable() { // from class: com.android.server.wm.ActivityRecord.1
            @Override // java.lang.Runnable
            public void run() {
                Slog.w("ActivityTaskManager", "Activity pause timeout for " + ActivityRecord.this);
                synchronized (ActivityRecord.this.mAtmService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (!ActivityRecord.this.hasProcess()) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        ActivityRecord activityRecord3 = ActivityRecord.this;
                        ActivityTaskManagerService activityTaskManagerService2 = activityRecord3.mAtmService;
                        WindowProcessController windowProcessController2 = activityRecord3.app;
                        long j2 = activityRecord3.pauseTime;
                        activityTaskManagerService2.logAppTooSlow(windowProcessController2, j2, "pausing " + ActivityRecord.this);
                        ActivityRecord.this.activityPaused(true);
                        WindowManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        };
        this.mLaunchTickRunnable = new Runnable() { // from class: com.android.server.wm.ActivityRecord.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ActivityRecord.this.mAtmService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (ActivityRecord.this.continueLaunchTicking()) {
                            ActivityRecord activityRecord3 = ActivityRecord.this;
                            ActivityTaskManagerService activityTaskManagerService2 = activityRecord3.mAtmService;
                            WindowProcessController windowProcessController2 = activityRecord3.app;
                            long j2 = activityRecord3.launchTickTime;
                            activityTaskManagerService2.logAppTooSlow(windowProcessController2, j2, "launching " + ActivityRecord.this);
                        }
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        };
        this.mDestroyTimeoutRunnable = new Runnable() { // from class: com.android.server.wm.ActivityRecord.3
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ActivityRecord.this.mAtmService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        Slog.w("ActivityTaskManager", "Activity destroy timeout for " + ActivityRecord.this);
                        ActivityRecord.this.destroyed("destroyTimeout");
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        };
        this.mStopTimeoutRunnable = new Runnable() { // from class: com.android.server.wm.ActivityRecord.4
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ActivityRecord.this.mAtmService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        Slog.w("ActivityTaskManager", "Activity stop timeout for " + ActivityRecord.this);
                        if (ActivityRecord.this.isInHistory()) {
                            ActivityRecord.this.activityStopped(null, null, null);
                        }
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        };
        this.mAddStartingWindow = new AddStartingWindow();
        this.mTransferSplashScreenTimeoutRunnable = new Runnable() { // from class: com.android.server.wm.ActivityRecord.5
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ActivityRecord.this.mAtmService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        Slog.w("ActivityTaskManager", "Activity transferring splash screen timeout for " + ActivityRecord.this + " state " + ActivityRecord.this.mTransferringSplashScreenState);
                        if (ActivityRecord.this.isTransferringSplashScreen()) {
                            ActivityRecord activityRecord3 = ActivityRecord.this;
                            activityRecord3.mTransferringSplashScreenState = 3;
                            activityRecord3.removeStartingWindow();
                        }
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        };
        this.mAtmService = activityTaskManagerService;
        ((Token) this.token).mActivityRef = new WeakReference<>(this);
        this.info = activityInfo;
        int userId = UserHandle.getUserId(activityInfo.applicationInfo.uid);
        this.mUserId = userId;
        String str6 = activityInfo.applicationInfo.packageName;
        this.packageName = str6;
        this.intent = intent;
        String str7 = activityInfo.targetActivity;
        if (str7 == null || (str7.equals(intent.getComponent().getClassName()) && ((i5 = activityInfo.launchMode) == 0 || i5 == 1))) {
            this.mActivityComponent = intent.getComponent();
        } else {
            this.mActivityComponent = new ComponentName(activityInfo.packageName, activityInfo.targetActivity);
        }
        this.mLetterboxUiController = new LetterboxUiController(this.mWmService, this);
        this.mCameraCompatControlEnabled = this.mWmService.mContext.getResources().getBoolean(17891707);
        this.mTargetSdk = activityInfo.applicationInfo.targetSdkVersion;
        this.mShowForAllUsers = (activityInfo.flags & 1024) != 0;
        setOrientation(activityInfo.screenOrientation);
        this.mRotationAnimationHint = activityInfo.rotationAnimation;
        int i6 = activityInfo.flags;
        this.mShowWhenLocked = (i6 & 8388608) != 0;
        this.mInheritShownWhenLocked = (activityInfo.privateFlags & 1) != 0;
        this.mTurnScreenOn = (i6 & 16777216) != 0;
        int themeResource = activityInfo.getThemeResource();
        AttributeCache.Entry entry = AttributeCache.instance().get(str6, themeResource == 0 ? activityInfo.applicationInfo.targetSdkVersion < 11 ? 16973829 : 16973931 : themeResource, R.styleable.Window, userId);
        if (entry != null) {
            boolean z5 = !ActivityInfo.isTranslucentOrFloating(entry.array) || entry.array.getBoolean(14, false);
            this.mOccludesParent = z5;
            this.mStyleFillsParent = z5;
            this.noDisplay = entry.array.getBoolean(10, false);
        } else {
            this.mOccludesParent = true;
            this.mStyleFillsParent = true;
            this.noDisplay = false;
        }
        if (activityOptions != null) {
            this.mLaunchTaskBehind = activityOptions.getLaunchTaskBehind();
            int rotationAnimationHint = activityOptions.getRotationAnimationHint();
            if (rotationAnimationHint >= 0) {
                this.mRotationAnimationHint = rotationAnimationHint;
            }
            if (activityOptions.getLaunchIntoPipParams() != null) {
                this.pictureInPictureArgs = activityOptions.getLaunchIntoPipParams();
                if (activityRecord2 != null) {
                    adjustPictureInPictureParamsIfNeeded(activityRecord2.getBounds());
                }
            }
            this.mOverrideTaskTransition = activityOptions.getOverrideTaskTransition();
            this.mDismissKeyguard = activityOptions.getDismissKeyguard();
            this.mShareIdentity = activityOptions.isShareIdentityEnabled();
        }
        ((ColorDisplayService.ColorDisplayServiceInternal) LocalServices.getService(ColorDisplayService.ColorDisplayServiceInternal.class)).attachColorTransformController(str6, userId, new WeakReference<>(colorTransformController));
        this.mRootWindowContainer = activityTaskManagerService.mRootWindowContainer;
        this.launchedFromPid = i;
        this.launchedFromUid = i2;
        this.launchedFromPackage = str;
        this.launchedFromFeatureId = str2;
        this.mLaunchSourceType = determineLaunchSourceType(i2, windowProcessController);
        this.shortComponentName = intent.getComponent().flattenToShortString();
        this.resolvedType = str3;
        this.componentSpecified = z;
        this.rootVoiceInteraction = z2;
        this.mLastReportedConfiguration = new MergedConfiguration(configuration);
        this.resultTo = activityRecord;
        this.resultWho = str4;
        this.requestCode = i3;
        setState(State.INITIALIZING, "ActivityRecord ctor");
        this.launchFailed = false;
        this.delayedResume = false;
        this.finishing = false;
        this.deferRelaunchUntilPaused = false;
        this.keysPaused = false;
        this.inHistory = false;
        this.nowVisible = false;
        super.setClientVisible(true);
        this.idle = false;
        this.hasBeenLaunched = false;
        this.mTaskSupervisor = activityTaskSupervisor;
        String computeTaskAffinity = computeTaskAffinity(activityInfo.taskAffinity, activityInfo.applicationInfo.uid, this.launchMode);
        activityInfo.taskAffinity = computeTaskAffinity;
        this.taskAffinity = computeTaskAffinity;
        String num = Integer.toString(activityInfo.applicationInfo.uid);
        ActivityInfo.WindowLayout windowLayout = activityInfo.windowLayout;
        if (windowLayout != null && (str5 = windowLayout.windowLayoutAffinity) != null && !str5.startsWith(num)) {
            activityInfo.windowLayout.windowLayoutAffinity = num + XmlUtils.STRING_ARRAY_SEPARATOR + activityInfo.windowLayout.windowLayoutAffinity;
        }
        if (sConstrainDisplayApisConfig == null) {
            sConstrainDisplayApisConfig = new ConstrainDisplayApisConfig();
        }
        this.stateNotNeeded = (activityInfo.flags & 16) != 0;
        CharSequence charSequence = activityInfo.nonLocalizedLabel;
        this.nonLocalizedLabel = charSequence;
        int i7 = activityInfo.labelRes;
        this.labelRes = i7;
        if (charSequence == null && i7 == 0) {
            ApplicationInfo applicationInfo = activityInfo.applicationInfo;
            this.nonLocalizedLabel = applicationInfo.nonLocalizedLabel;
            this.labelRes = applicationInfo.labelRes;
        }
        this.icon = activityInfo.getIconResource();
        this.theme = activityInfo.getThemeResource();
        int i8 = activityInfo.flags;
        if ((i8 & 1) != 0 && windowProcessController != null && ((i4 = activityInfo.applicationInfo.uid) == 1000 || i4 == windowProcessController.mInfo.uid)) {
            this.processName = windowProcessController.mName;
        } else {
            this.processName = activityInfo.processName;
        }
        if ((i8 & 32) != 0) {
            intent.addFlags(8388608);
        }
        this.launchMode = activityInfo.launchMode;
        setActivityType(z, i2, intent, activityOptions, activityRecord2);
        this.immersive = (activityInfo.flags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
        String str8 = activityInfo.requestedVrComponent;
        this.requestedVrComponent = str8 == null ? null : ComponentName.unflattenFromString(str8);
        this.lockTaskLaunchMode = getLockTaskLaunchMode(activityInfo, activityOptions);
        if (activityOptions != null) {
            setOptions(activityOptions);
            this.mHasSceneTransition = activityOptions.getAnimationType() == 5 && activityOptions.getResultReceiver() != null;
            PendingIntent usageTimeReport = activityOptions.getUsageTimeReport();
            if (usageTimeReport != null) {
                this.appTimeTracker = new AppTimeTracker(usageTimeReport);
            }
            WindowContainerToken launchTaskDisplayArea = activityOptions.getLaunchTaskDisplayArea();
            this.mHandoverTaskDisplayArea = launchTaskDisplayArea != null ? (TaskDisplayArea) WindowContainer.fromBinder(launchTaskDisplayArea.asBinder()) : null;
            this.mHandoverLaunchDisplayId = activityOptions.getLaunchDisplayId();
            this.mLaunchCookie = activityOptions.getLaunchCookie();
            this.mLaunchRootTask = activityOptions.getLaunchRootTask();
        } else {
            this.mHasSceneTransition = false;
        }
        this.mPersistentState = persistableBundle;
        this.taskDescription = taskDescription;
        this.shouldDockBigOverlays = this.mWmService.mContext.getResources().getBoolean(17891617);
        if (j > 0) {
            this.createTime = j;
        }
        activityTaskManagerService.mPackageConfigPersister.updateConfigIfNeeded(this, userId, str6);
        this.mActivityRecordInputSink = new ActivityRecordInputSink(this, activityRecord2);
        updateEnterpriseThumbnailDrawable(activityTaskManagerService.getUiContext());
        try {
            if (WindowManager.hasWindowExtensionsEnabled()) {
            }
            z4 = false;
            z3 = z4;
        } catch (PackageManager.NameNotFoundException unused) {
            z3 = false;
        }
        this.mAppActivityEmbeddingSplitsEnabled = z3;
    }

    public static String computeTaskAffinity(String str, int i, int i2) {
        String num = Integer.toString(i);
        if (str == null || str.startsWith(num)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(num);
        sb.append(i2 == 3 ? "-si:" : XmlUtils.STRING_ARRAY_SEPARATOR);
        sb.append(str);
        return sb.toString();
    }

    public static int getLockTaskLaunchMode(ActivityInfo activityInfo, ActivityOptions activityOptions) {
        int i = activityInfo.lockTaskLaunchMode;
        if (!activityInfo.applicationInfo.isPrivilegedApp() && (i == 2 || i == 1)) {
            i = 0;
        }
        if (activityOptions != null && activityOptions.getLockTaskMode() && i == 0) {
            return 3;
        }
        return i;
    }

    public InputApplicationHandle getInputApplicationHandle(boolean z) {
        if (this.mInputApplicationHandle == null) {
            this.mInputApplicationHandle = new InputApplicationHandle(this.token, toString(), this.mInputDispatchingTimeoutMillis);
        } else if (z) {
            String activityRecord = toString();
            long j = this.mInputDispatchingTimeoutMillis;
            InputApplicationHandle inputApplicationHandle = this.mInputApplicationHandle;
            if (j != inputApplicationHandle.dispatchingTimeoutMillis || !activityRecord.equals(inputApplicationHandle.name)) {
                this.mInputApplicationHandle = new InputApplicationHandle(this.token, activityRecord, this.mInputDispatchingTimeoutMillis);
            }
        }
        return this.mInputApplicationHandle;
    }

    public void setProcess(WindowProcessController windowProcessController) {
        this.app = windowProcessController;
        Task task = this.task;
        if ((task != null ? task.getRootActivity() : null) == this) {
            this.task.setRootProcess(windowProcessController);
        }
        windowProcessController.addActivityIfNeeded(this);
        this.mInputDispatchingTimeoutMillis = ActivityTaskManagerService.getInputDispatchingTimeoutMillisLocked(this);
        TaskFragment taskFragment = getTaskFragment();
        if (taskFragment != null) {
            taskFragment.sendTaskFragmentInfoChanged();
        }
    }

    public boolean hasProcess() {
        return this.app != null;
    }

    public boolean attachedToProcess() {
        return hasProcess() && this.app.hasThread();
    }

    public final int evaluateStartingWindowTheme(ActivityRecord activityRecord, String str, int i, int i2) {
        if (validateStartingWindowTheme(activityRecord, str, i)) {
            return (i2 == 0 || !validateStartingWindowTheme(activityRecord, str, i2)) ? i : i2;
        }
        return 0;
    }

    public final boolean launchedFromSystemSurface() {
        int i = this.mLaunchSourceType;
        return i == 1 || i == 2 || i == 3;
    }

    public boolean isLaunchSourceType(int i) {
        return this.mLaunchSourceType == i;
    }

    public final int determineLaunchSourceType(int i, WindowProcessController windowProcessController) {
        if (i == 1000 || i == 0) {
            return 1;
        }
        if (windowProcessController != null) {
            if (windowProcessController.isHomeProcess()) {
                return 2;
            }
            return this.mAtmService.getSysUiServiceComponentLocked().getPackageName().equals(windowProcessController.mInfo.packageName) ? 3 : 4;
        }
        return 4;
    }

    public final boolean validateStartingWindowTheme(ActivityRecord activityRecord, String str, int i) {
        AttributeCache.Entry entry;
        if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -1782453012, 1, (String) null, new Object[]{Long.valueOf(i)});
        }
        if (i == 0 || (entry = AttributeCache.instance().get(str, i, R.styleable.Window, this.mWmService.mCurrentUserId)) == null) {
            return false;
        }
        boolean z = entry.array.getBoolean(5, false);
        boolean z2 = entry.array.getBoolean(4, false);
        boolean z3 = entry.array.getBoolean(14, false);
        boolean z4 = entry.array.getBoolean(12, false);
        if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -124316973, 0, (String) null, new Object[]{String.valueOf(z), String.valueOf(z2), String.valueOf(z3), String.valueOf(z4)});
        }
        if (z || z2) {
            return false;
        }
        if (!z3 || getDisplayContent().mWallpaperController.getWallpaperTarget() == null) {
            if (!z4 || launchedFromSystemSurface()) {
                return true;
            }
            if (activityRecord != null && activityRecord.getActivityType() == 1 && activityRecord.mTransferringSplashScreenState == 0) {
                if (activityRecord.mStartingData != null) {
                    return true;
                }
                if (activityRecord.mStartingWindow != null && activityRecord.mStartingSurface != null) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @VisibleForTesting
    public boolean addStartingWindow(String str, int i, ActivityRecord activityRecord, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7) {
        if (okToDisplay() && this.mStartingData == null) {
            WindowState findMainWindow = findMainWindow();
            if (findMainWindow == null || !findMainWindow.mWinAnimator.getShown()) {
                TaskSnapshotController taskSnapshotController = this.mWmService.mTaskSnapshotController;
                Task task = this.task;
                TaskSnapshot snapshot = taskSnapshotController.getSnapshot(task.mTaskId, task.mUserId, false, false);
                int startingWindowType = getStartingWindowType(z, z2, z3, z4, z5, z7, snapshot);
                int makeStartingWindowTypeParameter = StartingSurfaceController.makeStartingWindowTypeParameter(z, z2, z3, z4, z5, z6, startingWindowType == 2 && this.mWmService.mStartingSurfaceController.isExceptionApp(this.packageName, this.mTargetSdk, new Supplier() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda6
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        ApplicationInfo lambda$addStartingWindow$3;
                        lambda$addStartingWindow$3 = ActivityRecord.this.lambda$addStartingWindow$3();
                        return lambda$addStartingWindow$3;
                    }
                }), z7, startingWindowType, this.packageName, this.mUserId);
                if (startingWindowType == 1) {
                    if (isActivityTypeHome()) {
                        this.mWmService.mTaskSnapshotController.removeSnapshotCache(this.task.mTaskId);
                        if ((this.mDisplayContent.mAppTransition.getTransitFlags() & 2) == 0) {
                            return false;
                        }
                    }
                    return createSnapshot(snapshot, makeStartingWindowTypeParameter);
                } else if (i != 0 || this.theme == 0) {
                    if (activityRecord == null || !transferStartingWindow(activityRecord)) {
                        if (startingWindowType != 2) {
                            return false;
                        }
                        if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 2018852077, 0, (String) null, (Object[]) null);
                        }
                        this.mStartingData = new SplashScreenStartingData(this.mWmService, i, makeStartingWindowTypeParameter);
                        scheduleAddStartingWindow();
                        return true;
                    }
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ApplicationInfo lambda$addStartingWindow$3() {
        ActivityInfo resolveActivityInfo = this.intent.resolveActivityInfo(this.mAtmService.mContext.getPackageManager(), 128);
        if (resolveActivityInfo != null) {
            return resolveActivityInfo.applicationInfo;
        }
        return null;
    }

    public final boolean createSnapshot(TaskSnapshot taskSnapshot, int i) {
        if (taskSnapshot == null) {
            return false;
        }
        if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -1340540100, 0, (String) null, (Object[]) null);
        }
        this.mStartingData = new SnapshotStartingData(this.mWmService, taskSnapshot, i);
        if (this.task.forAllLeafTaskFragments(new ActivityRecord$$ExternalSyntheticLambda30())) {
            associateStartingDataWithTask();
        }
        scheduleAddStartingWindow();
        return true;
    }

    public void scheduleAddStartingWindow() {
        this.mAddStartingWindow.run();
    }

    /* renamed from: com.android.server.wm.ActivityRecord$AddStartingWindow */
    /* loaded from: classes2.dex */
    public class AddStartingWindow implements Runnable {
        public AddStartingWindow() {
        }

        @Override // java.lang.Runnable
        public void run() {
            StartingSurfaceController.StartingSurface startingSurface;
            boolean z;
            synchronized (ActivityRecord.this.mWmService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord activityRecord = ActivityRecord.this;
                    StartingData startingData = activityRecord.mStartingData;
                    if (startingData == null) {
                        if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 1836214582, 0, (String) null, new Object[]{String.valueOf(activityRecord)});
                        }
                        return;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 108170907, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(startingData)});
                    }
                    try {
                        startingSurface = startingData.createStartingSurface(ActivityRecord.this);
                    } catch (Exception e) {
                        Slog.w("ActivityTaskManager", "Exception when adding starting window", e);
                        startingSurface = null;
                    }
                    if (startingSurface != null) {
                        synchronized (ActivityRecord.this.mWmService.mGlobalLock) {
                            try {
                                WindowManagerService.boostPriorityForLockedSection();
                                ActivityRecord activityRecord2 = ActivityRecord.this;
                                if (activityRecord2.mStartingData == null) {
                                    if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 1822843721, 0, (String) null, new Object[]{String.valueOf(activityRecord2), String.valueOf(ActivityRecord.this.mStartingData)});
                                    }
                                    ActivityRecord activityRecord3 = ActivityRecord.this;
                                    activityRecord3.mStartingWindow = null;
                                    activityRecord3.mStartingData = null;
                                    z = true;
                                } else {
                                    activityRecord2.mStartingSurface = startingSurface;
                                    z = false;
                                }
                                if (!z && ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -1292329638, 0, (String) null, new Object[]{String.valueOf(ActivityRecord.this), String.valueOf(ActivityRecord.this.mStartingWindow), String.valueOf(ActivityRecord.this.mStartingSurface)});
                                }
                            } finally {
                            }
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        if (z) {
                            startingSurface.remove(false);
                        }
                    } else if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 1254403969, 0, (String) null, new Object[]{String.valueOf(ActivityRecord.this)});
                    }
                } finally {
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }
    }

    public final int getStartingWindowType(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, TaskSnapshot taskSnapshot) {
        Intent intent;
        ActivityRecord activity;
        if (!z && z2 && z3 && !z5 && (intent = this.task.intent) != null && this.mActivityComponent.equals(intent.getComponent()) && (activity = this.task.getActivity(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda27
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((ActivityRecord) obj).attachedToProcess();
            }
        })) != null) {
            return (activity.isSnapshotCompatible(taskSnapshot) && this.mDisplayContent.getDisplayRotation().rotationForOrientation(getOverrideOrientation(), this.mDisplayContent.getRotation()) == taskSnapshot.getRotation()) ? 1 : 0;
        }
        boolean isActivityTypeHome = isActivityTypeHome();
        if ((z || !z3 || (z2 && !z5)) && !isActivityTypeHome) {
            return 2;
        }
        if (z2) {
            if (z4) {
                if (isSnapshotCompatible(taskSnapshot)) {
                    return 1;
                }
                if (!isActivityTypeHome) {
                    return 2;
                }
            }
            if (!z6 && !isActivityTypeHome) {
                return 2;
            }
        }
        return 0;
    }

    @VisibleForTesting
    public boolean isSnapshotCompatible(TaskSnapshot taskSnapshot) {
        if (taskSnapshot != null && taskSnapshot.getTopActivityComponent().equals(this.mActivityComponent)) {
            int rotationForActivityInDifferentOrientation = this.mDisplayContent.rotationForActivityInDifferentOrientation(this);
            int rotation = this.task.getWindowConfiguration().getRotation();
            if (rotationForActivityInDifferentOrientation == -1) {
                rotationForActivityInDifferentOrientation = rotation;
            }
            if (taskSnapshot.getRotation() != rotationForActivityInDifferentOrientation) {
                return false;
            }
            Rect bounds = this.task.getBounds();
            int width = bounds.width();
            int height = bounds.height();
            Point taskSize = taskSnapshot.getTaskSize();
            if (Math.abs(rotation - rotationForActivityInDifferentOrientation) % 2 == 1) {
                width = height;
                height = width;
            }
            return Math.abs((((float) taskSize.x) / ((float) Math.max(taskSize.y, 1))) - (((float) width) / ((float) Math.max(height, 1)))) <= 0.01f;
        }
        return false;
    }

    public void setCustomizeSplashScreenExitAnimation(boolean z) {
        if (this.mHandleExitSplashScreen == z) {
            return;
        }
        this.mHandleExitSplashScreen = z;
    }

    public final void scheduleTransferSplashScreenTimeout() {
        this.mAtmService.f1161mH.postDelayed(this.mTransferSplashScreenTimeoutRunnable, 2000L);
    }

    public final void removeTransferSplashScreenTimeout() {
        this.mAtmService.f1161mH.removeCallbacks(this.mTransferSplashScreenTimeoutRunnable);
    }

    public final boolean transferSplashScreenIfNeeded() {
        if (this.finishing || !this.mHandleExitSplashScreen || this.mStartingSurface == null || this.mStartingWindow == null || this.mTransferringSplashScreenState == 3) {
            return false;
        }
        if (isTransferringSplashScreen()) {
            return true;
        }
        requestCopySplashScreen();
        return isTransferringSplashScreen();
    }

    public final boolean isTransferringSplashScreen() {
        int i = this.mTransferringSplashScreenState;
        return i == 2 || i == 1;
    }

    public final void requestCopySplashScreen() {
        this.mTransferringSplashScreenState = 1;
        if (!this.mAtmService.mTaskOrganizerController.copySplashScreenView(getTask())) {
            this.mTransferringSplashScreenState = 3;
            removeStartingWindow();
        }
        scheduleTransferSplashScreenTimeout();
    }

    public void onCopySplashScreenFinish(SplashScreenView.SplashScreenViewParcelable splashScreenViewParcelable) {
        WindowState windowState;
        removeTransferSplashScreenTimeout();
        if (splashScreenViewParcelable == null || this.mTransferringSplashScreenState != 1 || (windowState = this.mStartingWindow) == null || windowState.mRemoved || this.finishing) {
            if (splashScreenViewParcelable != null) {
                splashScreenViewParcelable.clearIfNeeded();
            }
            this.mTransferringSplashScreenState = 3;
            removeStartingWindow();
            return;
        }
        SurfaceControl applyStartingWindowAnimation = TaskOrganizerController.applyStartingWindowAnimation(windowState);
        try {
            this.mTransferringSplashScreenState = 2;
            this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ClientTransactionItem) TransferSplashScreenViewStateItem.obtain(splashScreenViewParcelable, applyStartingWindowAnimation));
            scheduleTransferSplashScreenTimeout();
        } catch (Exception unused) {
            Slog.w("ActivityTaskManager", "onCopySplashScreenComplete fail: " + this);
            this.mStartingWindow.cancelAnimation();
            splashScreenViewParcelable.clearIfNeeded();
            this.mTransferringSplashScreenState = 3;
        }
    }

    public final void onSplashScreenAttachComplete() {
        removeTransferSplashScreenTimeout();
        WindowState windowState = this.mStartingWindow;
        if (windowState != null) {
            windowState.cancelAnimation();
            this.mStartingWindow.hide(false, false);
        }
        this.mTransferringSplashScreenState = 3;
        removeStartingWindowAnimation(false);
    }

    public void cleanUpSplashScreen() {
        if (!this.mHandleExitSplashScreen || this.startingMoved) {
            return;
        }
        int i = this.mTransferringSplashScreenState;
        if (i == 3 || i == 0) {
            if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -1003678883, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            this.mAtmService.mTaskOrganizerController.onAppSplashScreenViewRemoved(getTask());
        }
    }

    public boolean isStartingWindowDisplayed() {
        StartingData startingData = this.mStartingData;
        if (startingData == null) {
            Task task = this.task;
            startingData = task != null ? task.mSharedStartingData : null;
        }
        return startingData != null && startingData.mIsDisplayed;
    }

    public void attachStartingWindow(WindowState windowState) {
        StartingData startingData = this.mStartingData;
        windowState.mStartingData = startingData;
        this.mStartingWindow = windowState;
        if (startingData != null) {
            if (startingData.mAssociatedTask != null) {
                attachStartingSurfaceToAssociatedTask();
            } else if (isEmbedded()) {
                associateStartingWindowWithTaskIfNeeded();
            }
        }
    }

    public final void attachStartingSurfaceToAssociatedTask() {
        WindowContainer.overrideConfigurationPropagation(this.mStartingWindow, this.mStartingData.mAssociatedTask);
        getSyncTransaction().reparent(this.mStartingWindow.mSurfaceControl, this.mStartingData.mAssociatedTask.mSurfaceControl);
    }

    public final void associateStartingDataWithTask() {
        StartingData startingData = this.mStartingData;
        Task task = this.task;
        startingData.mAssociatedTask = task;
        task.mSharedStartingData = startingData;
    }

    public void associateStartingWindowWithTaskIfNeeded() {
        StartingData startingData;
        if (this.mStartingWindow == null || (startingData = this.mStartingData) == null || startingData.mAssociatedTask != null) {
            return;
        }
        associateStartingDataWithTask();
        attachStartingSurfaceToAssociatedTask();
    }

    public void removeStartingWindow() {
        boolean isEligibleForLetterboxEducation = isEligibleForLetterboxEducation();
        if (transferSplashScreenIfNeeded()) {
            return;
        }
        removeStartingWindowAnimation(true);
        Task task = getTask();
        if (isEligibleForLetterboxEducation == isEligibleForLetterboxEducation() || task == null) {
            return;
        }
        task.dispatchTaskInfoChangedIfNeeded(true);
    }

    public void removeStartingWindowAnimation(boolean z) {
        this.mTransferringSplashScreenState = 0;
        Task task = this.task;
        if (task != null) {
            task.mSharedStartingData = null;
        }
        WindowState windowState = this.mStartingWindow;
        if (windowState == null) {
            if (this.mStartingData != null) {
                if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -2127842445, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                this.mStartingData = null;
                this.mStartingSurface = null;
                return;
            }
            return;
        }
        StartingData startingData = this.mStartingData;
        if (startingData != null) {
            boolean z2 = z && startingData.needRevealAnimation() && this.mStartingWindow.isVisibleByPolicy();
            if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -1237827119, 48, (String) null, new Object[]{String.valueOf(this), String.valueOf(this.mStartingWindow), Boolean.valueOf(z2), String.valueOf(Debug.getCallers(5))});
            }
            final StartingSurfaceController.StartingSurface startingSurface = this.mStartingSurface;
            this.mStartingData = null;
            this.mStartingSurface = null;
            this.mStartingWindow = null;
            if (startingSurface == null) {
                if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 45285419, 0, (String) null, (Object[]) null);
                }
            } else if (z2 && this.mTransitionController.inCollectingTransition(windowState) && windowState.cancelAndRedraw()) {
                windowState.mSyncTransaction.addTransactionCommittedListener(new SystemServerInitThreadPool$$ExternalSyntheticLambda0(), new SurfaceControl.TransactionCommittedListener() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda7
                    public final void onTransactionCommitted() {
                        ActivityRecord.this.lambda$removeStartingWindowAnimation$4(startingSurface);
                    }
                });
            } else {
                startingSurface.remove(z2);
            }
        } else if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 146871307, 0, (String) null, new Object[]{String.valueOf(this)});
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeStartingWindowAnimation$4(StartingSurfaceController.StartingSurface startingSurface) {
        synchronized (this.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                startingSurface.remove(true);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void reparent(TaskFragment taskFragment, int i, String str) {
        if (getParent() == null) {
            Slog.w("ActivityTaskManager", "reparent: Attempted to reparent non-existing app token: " + this.token);
        } else if (getTaskFragment() == taskFragment) {
            throw new IllegalArgumentException(str + ": task fragment =" + taskFragment + " is already the parent of r=" + this);
        } else {
            if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, 573582981, 20, (String) null, new Object[]{String.valueOf(this), Long.valueOf(this.task.mTaskId), Long.valueOf(i)});
            }
            reparent(taskFragment, i);
        }
    }

    public final boolean isHomeIntent(Intent intent) {
        return "android.intent.action.MAIN".equals(intent.getAction()) && (intent.hasCategory("android.intent.category.HOME") || intent.hasCategory("android.intent.category.SECONDARY_HOME")) && intent.getCategories().size() == 1 && intent.getData() == null && intent.getType() == null;
    }

    public static boolean isMainIntent(Intent intent) {
        return "android.intent.action.MAIN".equals(intent.getAction()) && intent.hasCategory("android.intent.category.LAUNCHER") && intent.getCategories().size() == 1 && intent.getData() == null && intent.getType() == null;
    }

    @VisibleForTesting
    public boolean canLaunchHomeActivity(int i, ActivityRecord activityRecord) {
        if (i == 1000 || i == 0) {
            return true;
        }
        RecentTasks recentTasks = this.mTaskSupervisor.mService.getRecentTasks();
        if (recentTasks == null || !recentTasks.isCallerRecents(i)) {
            return activityRecord != null && activityRecord.isResolverOrDelegateActivity();
        }
        return true;
    }

    public final boolean canLaunchAssistActivity(String str) {
        ComponentName componentName = this.mAtmService.mActiveVoiceInteractionServiceComponent;
        if (componentName != null) {
            return componentName.getPackageName().equals(str);
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x0067, code lost:
        if (android.service.dreams.DreamActivity.class.getName() == r2.info.name) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setActivityType(boolean z, int i, Intent intent, ActivityOptions activityOptions, ActivityRecord activityRecord) {
        int i2 = 4;
        if ((!z || canLaunchHomeActivity(i, activityRecord)) && isHomeIntent(intent) && !isResolverOrDelegateActivity()) {
            ActivityInfo activityInfo = this.info;
            int i3 = activityInfo.resizeMode;
            if (i3 == 4 || i3 == 1) {
                activityInfo.resizeMode = 0;
            }
            i2 = 2;
        } else if (this.mAtmService.getRecentTasks().isRecentsComponent(this.mActivityComponent, this.info.applicationInfo.uid)) {
            i2 = 3;
        } else if (activityOptions == null || activityOptions.getLaunchActivityType() != 4 || !canLaunchAssistActivity(this.launchedFromPackage)) {
            if (activityOptions != null) {
                i2 = 5;
                if (activityOptions.getLaunchActivityType() == 5) {
                    if (this.mAtmService.canLaunchDreamActivity(this.launchedFromPackage)) {
                    }
                }
            }
            i2 = 0;
        }
        setActivityType(i2);
    }

    public void setTaskToAffiliateWith(Task task) {
        int i = this.launchMode;
        if (i == 3 || i == 2) {
            return;
        }
        this.task.setTaskToAffiliateWith(task);
    }

    public Task getRootTask() {
        Task task = this.task;
        if (task != null) {
            return task.getRootTask();
        }
        return null;
    }

    public int getRootTaskId() {
        Task task = this.task;
        if (task != null) {
            return task.getRootTaskId();
        }
        return -1;
    }

    public Task getOrganizedTask() {
        Task task = this.task;
        if (task != null) {
            return task.getOrganizedTask();
        }
        return null;
    }

    public TaskFragment getOrganizedTaskFragment() {
        TaskFragment taskFragment = getTaskFragment();
        if (taskFragment != null) {
            return taskFragment.getOrganizedTaskFragment();
        }
        return null;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isEmbedded() {
        TaskFragment taskFragment = getTaskFragment();
        return taskFragment != null && taskFragment.isEmbedded();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public TaskDisplayArea getDisplayArea() {
        return (TaskDisplayArea) super.getDisplayArea();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean providesOrientation() {
        return this.mStyleFillsParent;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean fillsParent() {
        return occludesParent(true);
    }

    public boolean occludesParent() {
        return occludesParent(false);
    }

    @VisibleForTesting
    public boolean occludesParent(boolean z) {
        if (z || !this.finishing) {
            return this.mOccludesParent || showWallpaper();
        }
        return false;
    }

    public boolean setOccludesParent(boolean z) {
        boolean z2 = z != this.mOccludesParent;
        this.mOccludesParent = z;
        setMainWindowOpaque(z);
        this.mWmService.mWindowPlacerLocked.requestTraversal();
        if (z2 && this.task != null && !z) {
            getRootTask().convertActivityToTranslucent(this);
        }
        if (z2 || !z) {
            this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
        }
        return z2;
    }

    public void setMainWindowOpaque(boolean z) {
        WindowState findMainWindow = findMainWindow();
        if (findMainWindow == null) {
            return;
        }
        findMainWindow.mWinAnimator.setOpaqueLocked(z & (!PixelFormat.formatHasAlpha(findMainWindow.getAttrs().format)));
    }

    public void takeFromHistory() {
        if (this.inHistory) {
            this.inHistory = false;
            if (this.task != null && !this.finishing) {
                this.task = null;
            }
            abortAndClearOptionsAnimation();
        }
    }

    public boolean isInHistory() {
        return this.inHistory;
    }

    public boolean isInRootTaskLocked() {
        Task rootTask = getRootTask();
        return (rootTask == null || rootTask.isInTask(this) == null) ? false : true;
    }

    public boolean isPersistable() {
        Intent intent;
        int i = this.info.persistableMode;
        return (i == 0 || i == 2) && ((intent = this.intent) == null || (intent.getFlags() & 8388608) == 0);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isFocusable() {
        return super.isFocusable() && (canReceiveKeys() || isAlwaysFocusable());
    }

    public boolean canReceiveKeys() {
        Task task;
        return getWindowConfiguration().canReceiveKeys() && ((task = this.task) == null || task.getWindowConfiguration().canReceiveKeys());
    }

    public boolean isResizeable() {
        return isResizeable(true);
    }

    public boolean isResizeable(boolean z) {
        return this.mAtmService.mForceResizableActivities || ActivityInfo.isResizeableMode(this.info.resizeMode) || (this.info.supportsPictureInPicture() && z) || isEmbedded();
    }

    public boolean canForceResizeNonResizable(int i) {
        boolean supportsMultiWindow;
        int i2;
        if (i == 2 && this.info.supportsPictureInPicture()) {
            return false;
        }
        Task task = this.task;
        if (task != null) {
            supportsMultiWindow = task.supportsMultiWindow() || supportsMultiWindow();
        } else {
            supportsMultiWindow = supportsMultiWindow();
        }
        return ((WindowConfiguration.inMultiWindowMode(i) && supportsMultiWindow && !this.mAtmService.mForceResizableActivities) || (i2 = this.info.resizeMode) == 2 || i2 == 1) ? false : true;
    }

    public boolean supportsPictureInPicture() {
        return this.mAtmService.mSupportsPictureInPicture && isActivityTypeStandardOrUndefined() && this.info.supportsPictureInPicture();
    }

    public boolean supportsFreeform() {
        return supportsFreeformInDisplayArea(getDisplayArea());
    }

    public boolean supportsFreeformInDisplayArea(TaskDisplayArea taskDisplayArea) {
        return this.mAtmService.mSupportsFreeformWindowManagement && supportsMultiWindowInDisplayArea(taskDisplayArea);
    }

    public boolean supportsMultiWindow() {
        return supportsMultiWindowInDisplayArea(getDisplayArea());
    }

    public boolean supportsMultiWindowInDisplayArea(TaskDisplayArea taskDisplayArea) {
        if (isActivityTypeHome() || !this.mAtmService.mSupportsMultiWindow || taskDisplayArea == null) {
            return false;
        }
        if (isResizeable() || taskDisplayArea.supportsNonResizableMultiWindow()) {
            ActivityInfo activityInfo = this.info;
            ActivityInfo.WindowLayout windowLayout = activityInfo.windowLayout;
            return windowLayout == null || taskDisplayArea.supportsActivityMinWidthHeightMultiWindow(windowLayout.minWidth, windowLayout.minHeight, activityInfo);
        }
        return false;
    }

    public boolean canBeLaunchedOnDisplay(int i) {
        return this.mAtmService.mTaskSupervisor.canPlaceEntityOnDisplay(i, this.launchedFromPid, this.launchedFromUid, this.info);
    }

    public boolean checkEnterPictureInPictureState(String str, boolean z) {
        if (supportsPictureInPicture() && checkEnterPictureInPictureAppOpsState() && !this.mAtmService.shouldDisableNonVrUiLocked()) {
            DisplayContent displayContent = this.mDisplayContent;
            if (displayContent != null && !displayContent.mDwpcHelper.isEnteringPipAllowed(getUid())) {
                Slog.w("ActivityTaskManager", "Display " + this.mDisplayContent.getDisplayId() + " doesn't support enter picture-in-picture mode. caller = " + str);
                return false;
            }
            boolean z2 = this.mAtmService.getLockTaskModeState() != 0;
            TaskDisplayArea displayArea = getDisplayArea();
            boolean z3 = displayArea != null && displayArea.hasPinnedTask();
            boolean z4 = (isKeyguardLocked() || z2) ? false : true;
            if (z && z3) {
                return false;
            }
            int i = C18376.$SwitchMap$com$android$server$wm$ActivityRecord$State[this.mState.ordinal()];
            if (i != 1) {
                return (i == 2 || i == 3) ? z4 && !z3 && this.supportsEnterPipOnTaskSwitch : i == 4 && this.supportsEnterPipOnTaskSwitch && z4 && !z3;
            } else if (z2) {
                return false;
            } else {
                return this.supportsEnterPipOnTaskSwitch || !z;
            }
        }
        return false;
    }

    /* renamed from: com.android.server.wm.ActivityRecord$6 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class C18376 {
        public static final /* synthetic */ int[] $SwitchMap$com$android$server$wm$ActivityRecord$State;

        static {
            int[] iArr = new int[State.values().length];
            $SwitchMap$com$android$server$wm$ActivityRecord$State = iArr;
            try {
                iArr[State.RESUMED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityRecord$State[State.PAUSING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityRecord$State[State.PAUSED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityRecord$State[State.STOPPING.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityRecord$State[State.STARTED.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityRecord$State[State.STOPPED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityRecord$State[State.DESTROYED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityRecord$State[State.DESTROYING.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityRecord$State[State.INITIALIZING.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }

    public void setWillCloseOrEnterPip(boolean z) {
        this.mWillCloseOrEnterPip = z;
    }

    public boolean willCloseOrEnterPip() {
        return this.mWillCloseOrEnterPip;
    }

    public boolean checkEnterPictureInPictureAppOpsState() {
        return this.mAtmService.getAppOpsManager().checkOpNoThrow(67, this.info.applicationInfo.uid, this.packageName) == 0;
    }

    public final boolean isAlwaysFocusable() {
        return (this.info.flags & 262144) != 0;
    }

    public boolean windowsAreFocusable() {
        return windowsAreFocusable(false);
    }

    public boolean windowsAreFocusable(boolean z) {
        if (!z && this.mTargetSdk < 29) {
            ActivityRecord activityRecord = this.mWmService.mRoot.mTopFocusedAppByProcess.get(Integer.valueOf(getPid()));
            if (activityRecord != null && activityRecord != this) {
                return false;
            }
        }
        return (canReceiveKeys() || isAlwaysFocusable()) && isAttached();
    }

    public boolean moveFocusableActivityToTop(String str) {
        if (!isFocusable()) {
            if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_FOCUS, 240271590, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            return false;
        }
        Task rootTask = getRootTask();
        if (rootTask == null) {
            Slog.w("ActivityTaskManager", "moveFocusableActivityToTop: invalid root task: activity=" + this + " task=" + this.task);
            return false;
        }
        DisplayContent displayContent = this.mDisplayContent;
        ActivityRecord activityRecord = displayContent.mFocusedApp;
        if (activityRecord != null && activityRecord.task == this.task) {
            if (this.task == displayContent.getTask(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda10
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$moveFocusableActivityToTop$5;
                    lambda$moveFocusableActivityToTop$5 = ActivityRecord.lambda$moveFocusableActivityToTop$5((Task) obj);
                    return lambda$moveFocusableActivityToTop$5;
                }
            }, true)) {
                if (activityRecord == this) {
                    if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_FOCUS, 385237117, 0, (String) null, new Object[]{String.valueOf(this)});
                    }
                } else {
                    if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_FOCUS, 1239439010, 0, (String) null, new Object[]{String.valueOf(this)});
                    }
                    this.mDisplayContent.setFocusedApp(this);
                    this.mAtmService.mWindowManager.updateFocusedWindowLocked(0, true);
                }
                return !isState(State.RESUMED);
            }
        }
        if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_FOCUS, -50336993, 0, (String) null, new Object[]{String.valueOf(this)});
        }
        rootTask.moveToFront(str, this.task);
        if (this.mRootWindowContainer.getTopResumedActivity() == this) {
            this.mAtmService.setLastResumedActivityUncheckLocked(this, str);
        }
        return true;
    }

    public static /* synthetic */ boolean lambda$moveFocusableActivityToTop$5(Task task) {
        return task.isLeafTask() && task.isFocusable();
    }

    public void finishIfSubActivity(ActivityRecord activityRecord, String str, int i) {
        if (this.resultTo == activityRecord && this.requestCode == i && Objects.equals(this.resultWho, str)) {
            finishIfPossible("request-sub", false);
        }
    }

    public boolean finishIfSameAffinity(ActivityRecord activityRecord) {
        if (Objects.equals(activityRecord.taskAffinity, this.taskAffinity)) {
            activityRecord.finishIfPossible("request-affinity", true);
            return false;
        }
        return true;
    }

    public final void finishActivityResults(final int i, final Intent intent, final NeededUriGrants neededUriGrants) {
        ActivityRecord activityRecord = this.resultTo;
        if (activityRecord != null) {
            int i2 = activityRecord.mUserId;
            int i3 = this.mUserId;
            if (i2 != i3 && intent != null) {
                intent.prepareToLeaveUser(i3);
            }
            if (this.info.applicationInfo.uid > 0) {
                this.mAtmService.mUgmInternal.grantUriPermissionUncheckedFromIntent(neededUriGrants, this.resultTo.getUriPermissionsLocked());
            }
            if (this.mForceSendResultForMediaProjection || this.resultTo.isState(State.RESUMED)) {
                final ActivityRecord activityRecord2 = this.resultTo;
                this.mAtmService.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda14
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityRecord.this.lambda$finishActivityResults$6(activityRecord2, i, intent, neededUriGrants);
                    }
                });
            } else {
                this.resultTo.addResultLocked(this, this.resultWho, this.requestCode, i, intent);
            }
            this.resultTo = null;
        }
        this.results = null;
        this.pendingResults = null;
        this.newIntents = null;
        setSavedState(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishActivityResults$6(ActivityRecord activityRecord, int i, Intent intent, NeededUriGrants neededUriGrants) {
        synchronized (this.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                activityRecord.sendResult(getUid(), this.resultWho, this.requestCode, i, intent, neededUriGrants, this.mForceSendResultForMediaProjection);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public int finishIfPossible(String str, boolean z) {
        return finishIfPossible(0, null, null, str, z);
    }

    public int finishIfPossible(int i, Intent intent, NeededUriGrants neededUriGrants, String str, boolean z) {
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 1047769218, 4, (String) null, new Object[]{String.valueOf(this), Long.valueOf(i), String.valueOf(intent), String.valueOf(str)});
        }
        if (this.finishing) {
            Slog.w("ActivityTaskManager", "Duplicate finish request for r=" + this);
            return 0;
        } else if (!isInRootTaskLocked()) {
            Slog.w("ActivityTaskManager", "Finish request when not in root task for r=" + this);
            return 0;
        } else {
            Task rootTask = getRootTask();
            State state = State.RESUMED;
            int i2 = 1;
            boolean z2 = (isState(state) || rootTask.getTopResumedActivity() == null) && rootTask.isFocusedRootTaskOnDisplay() && !this.task.isClearingToReuseTask();
            boolean z3 = z2 && this.mRootWindowContainer.isTopDisplayFocusedRootTask(rootTask);
            this.mAtmService.deferWindowLayout();
            try {
                this.mTaskSupervisor.mNoHistoryActivities.remove(this);
                makeFinishingLocked();
                Task task = getTask();
                EventLogTags.writeWmFinishActivity(this.mUserId, System.identityHashCode(this), task.mTaskId, this.shortComponentName, str);
                ActivityRecord activityAbove = task.getActivityAbove(this);
                if (activityAbove != null && (this.intent.getFlags() & 524288) != 0) {
                    activityAbove.intent.addFlags(524288);
                }
                pauseKeyDispatchingLocked();
                if (z2 && task.topRunningActivity(true) == null) {
                    task.adjustFocusToNextFocusableTask("finish-top", false, z3);
                }
                finishActivityResults(i, intent, neededUriGrants);
                boolean z4 = task.getTopNonFinishingActivity() == null && !task.isClearingToReuseTask();
                this.mTransitionController.requestCloseTransitionIfNeeded(z4 ? task : this);
                if (isState(state)) {
                    if (z4) {
                        this.mAtmService.getTaskChangeNotificationController().notifyTaskRemovalStarted(task.getTaskInfo());
                    }
                    this.mDisplayContent.prepareAppTransition(2);
                    if (this.mAtmService.mWindowManager.mTaskSnapshotController != null && !task.isAnimatingByRecents() && !this.mTransitionController.inRecentsTransition(task)) {
                        ArraySet<Task> newArraySet = Sets.newArraySet(new Task[]{task});
                        this.mAtmService.mWindowManager.mTaskSnapshotController.snapshotTasks(newArraySet);
                        this.mAtmService.mWindowManager.mTaskSnapshotController.addSkipClosingAppSnapshotTasks(newArraySet);
                    }
                    setVisibility(false);
                    if (getTaskFragment().getPausingActivity() == null) {
                        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1003060523, 0, (String) null, new Object[]{String.valueOf(this)});
                        }
                        getTaskFragment().startPausing(false, false, null, "finish");
                    }
                    if (z4) {
                        this.mAtmService.getLockTaskController().clearLockedTask(task);
                        if (z2) {
                            this.mNeedsZBoost = true;
                            this.mDisplayContent.assignWindowLayers(false);
                        }
                    }
                } else if (!isState(State.PAUSING)) {
                    if (this.mVisibleRequested) {
                        prepareActivityHideTransitionAnimation();
                    }
                    boolean z5 = completeFinishing("finishIfPossible") == null;
                    if (z && isState(State.STOPPING)) {
                        this.mAtmService.updateOomAdj();
                    }
                    if (task.onlyHasTaskOverlayActivities(false)) {
                        task.forAllActivities(new Consumer() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda15
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                ((ActivityRecord) obj).prepareActivityHideTransitionAnimationIfOvarlay();
                            }
                        });
                    }
                    if (z5) {
                        i2 = 2;
                    }
                } else if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 1023413388, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                return i2;
            } finally {
                this.mAtmService.continueWindowLayout();
            }
        }
    }

    public void setForceSendResultForMediaProjection() {
        this.mForceSendResultForMediaProjection = true;
    }

    public final void prepareActivityHideTransitionAnimationIfOvarlay() {
        if (this.mTaskOverlay) {
            prepareActivityHideTransitionAnimation();
        }
    }

    public final void prepareActivityHideTransitionAnimation() {
        DisplayContent displayContent = this.mDisplayContent;
        displayContent.prepareAppTransition(2);
        setVisibility(false);
        displayContent.executeAppTransition();
    }

    public ActivityRecord completeFinishing(String str) {
        return completeFinishing(true, str);
    }

    /* JADX WARN: Removed duplicated region for block: B:62:0x00b8  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x00d4  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x00dd  */
    /* JADX WARN: Removed duplicated region for block: B:76:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ActivityRecord completeFinishing(boolean z, String str) {
        boolean z2;
        if (!this.finishing || isState(State.RESUMED)) {
            throw new IllegalArgumentException("Activity must be finishing and not resumed to complete, r=" + this + ", finishing=" + this.finishing + ", state=" + this.mState);
        } else if (isState(State.PAUSING)) {
            return this;
        } else {
            boolean z3 = true;
            boolean z4 = false;
            boolean z5 = this.mVisibleRequested || isState(State.PAUSED, State.STARTED);
            if (z && z5 && !this.task.isClearingToReuseTask()) {
                if (occludesParent(true) || (isKeyguardLocked() && this.mTaskSupervisor.getKeyguardController().topActivityOccludesKeyguard(this))) {
                    this.mDisplayContent.ensureActivitiesVisible(null, 0, false, true);
                }
            }
            ActivityRecord activityRecord = getDisplayArea().topRunningActivity(true);
            TaskFragment taskFragment = getTaskFragment();
            if (activityRecord != null && taskFragment != null && taskFragment.isEmbedded()) {
                TaskFragment organizedTaskFragment = taskFragment.getOrganizedTaskFragment();
                TaskFragment adjacentTaskFragment = organizedTaskFragment != null ? organizedTaskFragment.getAdjacentTaskFragment() : null;
                if (adjacentTaskFragment != null && activityRecord.isDescendantOf(adjacentTaskFragment) && organizedTaskFragment.topRunningActivity() == null) {
                    z2 = organizedTaskFragment.isDelayLastActivityRemoval();
                    if (activityRecord != null || (activityRecord.nowVisible && activityRecord.isVisibleRequested())) {
                        z3 = false;
                    }
                    if (z3 && this.mDisplayContent.isSleeping() && activityRecord == activityRecord.getTaskFragment().mLastPausedActivity) {
                        activityRecord.getTaskFragment().clearLastPausedActivity();
                    }
                    if (z5) {
                        addToFinishingAndWaitForIdle();
                        z4 = destroyIfPossible(str);
                    } else if (z3 || z2) {
                        addToStopping(false, false, "completeFinishing");
                        setState(State.STOPPING, "completeFinishing");
                    } else if (!addToFinishingAndWaitForIdle()) {
                        z4 = destroyIfPossible(str);
                    }
                    if (z4) {
                        return this;
                    }
                    return null;
                }
            }
            z2 = false;
            if (activityRecord != null) {
            }
            z3 = false;
            if (z3) {
                activityRecord.getTaskFragment().clearLastPausedActivity();
            }
            if (z5) {
            }
            if (z4) {
            }
        }
    }

    public boolean destroyIfPossible(String str) {
        setState(State.FINISHING, "destroyIfPossible");
        this.mTaskSupervisor.mStoppingActivities.remove(this);
        Task rootTask = getRootTask();
        TaskDisplayArea displayArea = getDisplayArea();
        ActivityRecord activityRecord = displayArea.topRunningActivity();
        if (activityRecord == null && rootTask.isFocusedRootTaskOnDisplay() && displayArea.getOrCreateRootHomeTask() != null) {
            addToFinishingAndWaitForIdle();
            return false;
        }
        makeFinishingLocked();
        boolean destroyImmediately = destroyImmediately("finish-imm:" + str);
        if (activityRecord == null) {
            this.mRootWindowContainer.ensureVisibilityAndConfig(activityRecord, getDisplayId(), false, true);
        }
        if (destroyImmediately) {
            this.mRootWindowContainer.resumeFocusedTasksTopActivities();
        }
        if (ProtoLogCache.WM_DEBUG_CONTAINERS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_CONTAINERS, -401282500, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(destroyImmediately)});
        }
        return destroyImmediately;
    }

    @VisibleForTesting
    public boolean addToFinishingAndWaitForIdle() {
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 1610646518, 0, (String) null, new Object[]{String.valueOf(this)});
        }
        setState(State.FINISHING, "addToFinishingAndWaitForIdle");
        if (!this.mTaskSupervisor.mFinishingActivities.contains(this)) {
            this.mTaskSupervisor.mFinishingActivities.add(this);
        }
        resumeKeyDispatchingLocked();
        return this.mRootWindowContainer.resumeFocusedTasksTopActivities();
    }

    public boolean destroyImmediately(String str) {
        boolean z;
        State state = State.DESTROYING;
        State state2 = State.DESTROYED;
        if (isState(state, state2)) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -21399771, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(str)});
            }
            return false;
        }
        EventLogTags.writeWmDestroyActivity(this.mUserId, System.identityHashCode(this), this.task.mTaskId, this.shortComponentName, str);
        cleanUp(false, false);
        boolean z2 = true;
        if (hasProcess()) {
            this.app.removeActivity(this, true);
            if (!this.app.hasActivities()) {
                this.mAtmService.clearHeavyWeightProcessIfEquals(this.app);
            }
            try {
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ActivityLifecycleItem) DestroyActivityItem.obtain(this.finishing, this.configChangeFlags));
            } catch (Exception unused) {
                if (this.finishing) {
                    removeFromHistory(str + " exceptionInScheduleDestroy");
                    z = true;
                }
            }
            z = false;
            z2 = false;
            this.nowVisible = false;
            if (this.finishing && !z2) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1432963966, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                setState(State.DESTROYING, "destroyActivityLocked. finishing and not skipping destroy");
                this.mAtmService.f1161mH.postDelayed(this.mDestroyTimeoutRunnable, 10000L);
            } else {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 726205185, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                setState(State.DESTROYED, "destroyActivityLocked. not finishing or skipping destroy");
                detachFromProcess();
            }
            z2 = z;
        } else if (this.finishing) {
            removeFromHistory(str + " hadNoApp");
        } else {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -729530161, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            setState(state2, "destroyActivityLocked. not finishing and had no app");
            z2 = false;
        }
        this.configChangeFlags = 0;
        return z2;
    }

    public void removeFromHistory(String str) {
        finishActivityResults(0, null, null);
        makeFinishingLocked();
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, 350168164, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(str), String.valueOf(Debug.getCallers(5))});
        }
        takeFromHistory();
        removeTimeouts();
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 579298675, 0, (String) null, new Object[]{String.valueOf(this)});
        }
        setState(State.DESTROYED, "removeFromHistory");
        detachFromProcess();
        resumeKeyDispatchingLocked();
        this.mDisplayContent.removeAppToken(this.token);
        cleanUpActivityServices();
        removeUriPermissionsLocked();
    }

    public void detachFromProcess() {
        WindowProcessController windowProcessController = this.app;
        if (windowProcessController != null) {
            windowProcessController.removeActivity(this, false);
        }
        this.app = null;
        this.mInputDispatchingTimeoutMillis = InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
    }

    public void makeFinishingLocked() {
        Task task;
        ActivityRecord activity;
        if (this.finishing) {
            return;
        }
        this.finishing = true;
        if (this.mLaunchCookie != null && this.mState != State.RESUMED && (task = this.task) != null && !task.mInRemoveTask && !task.isClearingToReuseTask() && (activity = this.task.getActivity(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda13
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$makeFinishingLocked$8;
                lambda$makeFinishingLocked$8 = ActivityRecord.this.lambda$makeFinishingLocked$8((ActivityRecord) obj);
                return lambda$makeFinishingLocked$8;
            }
        }, this, false, false)) != null) {
            activity.mLaunchCookie = this.mLaunchCookie;
            this.mLaunchCookie = null;
        }
        TaskFragment taskFragment = getTaskFragment();
        if (taskFragment != null) {
            Task task2 = taskFragment.getTask();
            if (task2 != null && task2.isClearingToReuseTask() && taskFragment.getTopNonFinishingActivity() == null) {
                taskFragment.mClearedTaskForReuse = true;
            }
            taskFragment.sendTaskFragmentInfoChanged();
        }
        if (this.mAppStopped) {
            abortAndClearOptionsAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$makeFinishingLocked$8(ActivityRecord activityRecord) {
        return activityRecord.mLaunchCookie == null && !activityRecord.finishing && activityRecord.isUid(getUid());
    }

    public boolean isFinishing() {
        return this.finishing;
    }

    public void destroyed(String str) {
        removeDestroyTimeout();
        if (ProtoLogCache.WM_DEBUG_CONTAINERS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_CONTAINERS, -1598452494, 0, (String) null, new Object[]{String.valueOf(this)});
        }
        if (!isState(State.DESTROYING, State.DESTROYED)) {
            throw new IllegalStateException("Reported destroyed for activity that is not destroying: r=" + this);
        }
        if (isInRootTaskLocked()) {
            cleanUp(true, false);
            removeFromHistory(str);
        }
        this.mRootWindowContainer.resumeFocusedTasksTopActivities();
    }

    public void cleanUp(boolean z, boolean z2) {
        HashSet<WeakReference<PendingIntentRecord>> hashSet;
        getTaskFragment().cleanUpActivityReferences(this);
        clearLastParentBeforePip();
        cleanUpSplashScreen();
        this.deferRelaunchUntilPaused = false;
        this.frozenBeforeDestroy = false;
        if (z2) {
            setState(State.DESTROYED, "cleanUp");
            detachFromProcess();
        }
        this.mTaskSupervisor.cleanupActivity(this);
        if (this.finishing && (hashSet = this.pendingResults) != null) {
            Iterator<WeakReference<PendingIntentRecord>> it = hashSet.iterator();
            while (it.hasNext()) {
                PendingIntentRecord pendingIntentRecord = it.next().get();
                if (pendingIntentRecord != null) {
                    this.mAtmService.mPendingIntentController.cancelIntentSender(pendingIntentRecord, false);
                }
            }
            this.pendingResults = null;
        }
        if (z) {
            cleanUpActivityServices();
        }
        removeTimeouts();
        clearRelaunching();
    }

    public boolean isRelaunching() {
        return this.mPendingRelaunchCount > 0;
    }

    @VisibleForTesting
    public void startRelaunching() {
        if (this.mPendingRelaunchCount == 0) {
            this.mRelaunchStartTime = SystemClock.elapsedRealtime();
            if (this.mVisibleRequested) {
                this.mDisplayContent.getDisplayPolicy().addRelaunchingApp(this);
            }
        }
        clearAllDrawn();
        this.mPendingRelaunchCount++;
    }

    public void finishRelaunching() {
        this.mLetterboxUiController.setRelauchingAfterRequestedOrientationChanged(false);
        this.mTaskSupervisor.getActivityMetricsLogger().notifyActivityRelaunched(this);
        int i = this.mPendingRelaunchCount;
        if (i > 0) {
            int i2 = i - 1;
            this.mPendingRelaunchCount = i2;
            if (i2 == 0 && !isClientVisible()) {
                finishOrAbortReplacingWindow();
            }
        } else {
            checkKeyguardFlagsChanged();
        }
        Task rootTask = getRootTask();
        if (rootTask == null || !rootTask.shouldSleepOrShutDownActivities()) {
            return;
        }
        rootTask.ensureActivitiesVisible(null, 0, false);
    }

    public void clearRelaunching() {
        if (this.mPendingRelaunchCount == 0) {
            return;
        }
        this.mPendingRelaunchCount = 0;
        finishOrAbortReplacingWindow();
    }

    public void finishOrAbortReplacingWindow() {
        this.mRelaunchStartTime = 0L;
        this.mDisplayContent.getDisplayPolicy().removeRelaunchingApp(this);
    }

    public ActivityServiceConnectionsHolder getOrCreateServiceConnectionsHolder() {
        ActivityServiceConnectionsHolder activityServiceConnectionsHolder;
        synchronized (this) {
            if (this.mServiceConnectionsHolder == null) {
                this.mServiceConnectionsHolder = new ActivityServiceConnectionsHolder(this);
            }
            activityServiceConnectionsHolder = this.mServiceConnectionsHolder;
        }
        return activityServiceConnectionsHolder;
    }

    public final void cleanUpActivityServices() {
        synchronized (this) {
            ActivityServiceConnectionsHolder activityServiceConnectionsHolder = this.mServiceConnectionsHolder;
            if (activityServiceConnectionsHolder == null) {
                return;
            }
            activityServiceConnectionsHolder.disconnectActivityFromServices();
            this.mServiceConnectionsHolder = null;
        }
    }

    public final void updateVisibleForServiceConnection() {
        State state;
        this.mVisibleForServiceConnection = this.mVisibleRequested || (state = this.mState) == State.RESUMED || state == State.PAUSING;
    }

    public void handleAppDied() {
        Task task;
        ActivityRecord activityRecord;
        Task task2;
        WindowProcessController windowProcessController;
        int i = this.mRelaunchReason;
        boolean z = false;
        if (((i != 1 && i != 2) || this.launchCount >= 3 || this.finishing) && ((!this.mHaveState && !this.stateNotNeeded && !isState(State.RESTARTING_PROCESS)) || this.finishing || (!this.mVisibleRequested && this.launchCount > 2 && this.lastLaunchTime > SystemClock.uptimeMillis() - 60000))) {
            z = true;
        }
        if (z) {
            if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, 1789603530, 204, (String) null, new Object[]{String.valueOf(this), Boolean.valueOf(this.mHaveState), String.valueOf(this.stateNotNeeded), Boolean.valueOf(this.finishing), String.valueOf(this.mState), String.valueOf(Debug.getCallers(5))});
            }
            if (!this.finishing || ((windowProcessController = this.app) != null && windowProcessController.isRemoved())) {
                Slog.w("ActivityTaskManager", "Force removing " + this + ": app died, no saved state");
                int i2 = this.mUserId;
                int identityHashCode = System.identityHashCode(this);
                Task task3 = this.task;
                EventLogTags.writeWmFinishActivity(i2, identityHashCode, task3 != null ? task3.mTaskId : -1, this.shortComponentName, "proc died without state saved");
            }
        } else {
            this.nowVisible = this.mVisibleRequested;
        }
        this.mTransitionController.requestCloseTransitionIfNeeded((z && (task2 = this.task) != null && task2.getChildCount() == 1) ? this.task : this);
        cleanUp(true, true);
        if (z) {
            if (this.mStartingData != null && this.mVisible && (task = this.task) != null && (activityRecord = task.topRunningActivity()) != null && !activityRecord.mVisible && activityRecord.shouldBeVisible()) {
                activityRecord.transferStartingWindow(this);
            }
            removeFromHistory("appDied");
        }
    }

    @Override // com.android.server.p014wm.WindowToken, com.android.server.p014wm.WindowContainer
    public void removeImmediately() {
        if (this.mState != State.DESTROYED) {
            Slog.w("ActivityTaskManager", "Force remove immediately " + this + " state=" + this.mState);
            destroyImmediately("removeImmediately");
            destroyed("removeImmediately");
        } else {
            onRemovedFromDisplay();
        }
        this.mActivityRecordInputSink.releaseSurfaceControl();
        super.removeImmediately();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void removeIfPossible() {
        this.mIsExiting = false;
        removeAllWindowsIfPossible();
        removeImmediately();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean handleCompleteDeferredRemoval() {
        if (this.mIsExiting) {
            removeIfPossible();
        }
        return super.handleCompleteDeferredRemoval();
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x007a, code lost:
        if (r10.mTransitionController.inTransition() != false) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onRemovedFromDisplay() {
        if (this.mRemovingFromDisplay) {
            return;
        }
        this.mRemovingFromDisplay = true;
        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, -1352076759, 0, (String) null, new Object[]{String.valueOf(this)});
        }
        getDisplayContent().mOpeningApps.remove(this);
        getDisplayContent().mUnknownAppVisibilityController.appRemovedOrHidden(this);
        this.mWmService.mTaskSnapshotController.onAppRemoved(this);
        this.mTaskSupervisor.getActivityMetricsLogger().notifyActivityRemoved(this);
        this.mTaskSupervisor.mStoppingActivities.remove(this);
        this.waitingToShow = false;
        boolean isAnimating = isAnimating(7, 17);
        if (!getDisplayContent().mClosingApps.contains(this)) {
            if (getDisplayContent().mAppTransition.isTransitionSet()) {
                getDisplayContent().mClosingApps.add(this);
            }
        }
        isAnimating = true;
        if (!isAnimating) {
            commitVisibility(false, true);
        } else {
            setVisibleRequested(false);
        }
        this.mTransitionController.collect(this);
        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, 1653210583, 204, (String) null, new Object[]{String.valueOf(this), Boolean.valueOf(isAnimating), String.valueOf(getAnimation()), Boolean.valueOf(isAnimating(3, 1))});
        }
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -1539974875, 12, (String) null, new Object[]{String.valueOf(this), Boolean.valueOf(isAnimating), String.valueOf(Debug.getCallers(4))});
        }
        if (this.mStartingData != null) {
            removeStartingWindow();
        }
        if (isAnimating(3, 1)) {
            getDisplayContent().mNoAnimationNotifyOnTransitionFinished.add(this.token);
        }
        if (isAnimating && !isEmpty()) {
            if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -2109936758, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            this.mIsExiting = true;
        } else {
            cancelAnimation();
            removeIfPossible();
        }
        stopFreezingScreen(true, true);
        DisplayContent displayContent = getDisplayContent();
        if (displayContent.mFocusedApp == this) {
            if (ProtoLogCache.WM_DEBUG_FOCUS_LIGHT_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT, -771177730, 4, (String) null, new Object[]{String.valueOf(this), Long.valueOf(displayContent.getDisplayId())});
            }
            displayContent.setFocusedApp(null);
            this.mWmService.updateFocusedWindowLocked(0, true);
        }
        this.mLetterboxUiController.destroy();
        if (!isAnimating) {
            updateReportedVisibilityLocked();
        }
        this.mDisplayContent.mPinnedTaskController.onActivityHidden(this.mActivityComponent);
        this.mDisplayContent.onRunningActivityChanged();
        this.mWmService.mEmbeddedWindowController.onActivityRemoved(this);
        this.mRemovingFromDisplay = false;
    }

    @Override // com.android.server.p014wm.WindowToken
    public boolean isFirstChildWindowGreaterThanSecond(WindowState windowState, WindowState windowState2) {
        int i = windowState.mAttrs.type;
        int i2 = windowState2.mAttrs.type;
        if (i != 1 || i2 == 1) {
            if (i == 1 || i2 != 1) {
                return (i == 3 && i2 != 3) || i == 3 || i2 != 3;
            }
            return true;
        }
        return false;
    }

    public boolean hasStartingWindow() {
        if (this.mStartingData != null) {
            return true;
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if (getChildAt(size).mAttrs.type == 3) {
                return true;
            }
        }
        return false;
    }

    public boolean isLastWindow(WindowState windowState) {
        return this.mChildren.size() == 1 && this.mChildren.get(0) == windowState;
    }

    @Override // com.android.server.p014wm.WindowToken
    public void addWindow(WindowState windowState) {
        super.addWindow(windowState);
        boolean z = false;
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            z |= ((WindowState) this.mChildren.get(size)).setReplacementWindowIfNeeded(windowState);
        }
        if (z) {
            this.mWmService.scheduleWindowReplacementTimeouts(this);
        }
        checkKeyguardFlagsChanged();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void removeChild(WindowState windowState) {
        if (this.mChildren.contains(windowState)) {
            super.removeChild((ActivityRecord) windowState);
            checkKeyguardFlagsChanged();
            updateLetterboxSurface(windowState);
        }
    }

    public void onWindowReplacementTimeout() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            ((WindowState) this.mChildren.get(size)).onWindowReplacementTimeout();
        }
    }

    public void setAppLayoutChanges(int i, String str) {
        if (this.mChildren.isEmpty()) {
            return;
        }
        DisplayContent displayContent = getDisplayContent();
        displayContent.pendingLayoutChanges = i | displayContent.pendingLayoutChanges;
    }

    public void removeReplacedWindowIfNeeded(WindowState windowState) {
        for (int size = this.mChildren.size() - 1; size >= 0 && !((WindowState) this.mChildren.get(size)).removeReplacedWindowIfNeeded(windowState); size--) {
        }
    }

    public final boolean transferStartingWindow(ActivityRecord activityRecord) {
        WindowState windowState = activityRecord.mStartingWindow;
        if (windowState != null && activityRecord.mStartingSurface != null) {
            if (windowState.getParent() == null) {
                return false;
            }
            if (activityRecord.mVisible) {
                this.mDisplayContent.mSkipAppTransitionAnimation = true;
            }
            if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -1938204785, 0, (String) null, new Object[]{String.valueOf(windowState), String.valueOf(activityRecord), String.valueOf(this)});
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (activityRecord.hasFixedRotationTransform()) {
                    this.mDisplayContent.handleTopActivityLaunchingInDifferentOrientation(this, false);
                }
                this.mStartingData = activityRecord.mStartingData;
                this.mStartingSurface = activityRecord.mStartingSurface;
                this.mStartingWindow = windowState;
                this.reportedVisible = activityRecord.reportedVisible;
                activityRecord.mStartingData = null;
                activityRecord.mStartingSurface = null;
                activityRecord.mStartingWindow = null;
                activityRecord.startingMoved = true;
                windowState.mToken = this;
                windowState.mActivityRecord = this;
                if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -1499134947, 0, (String) null, new Object[]{String.valueOf(windowState), String.valueOf(activityRecord)});
                }
                this.mTransitionController.collect(windowState);
                windowState.reparent(this, Integer.MAX_VALUE);
                windowState.clearFrozenInsetsState();
                if (activityRecord.allDrawn) {
                    this.allDrawn = true;
                }
                if (activityRecord.firstWindowDrawn) {
                    this.firstWindowDrawn = true;
                }
                if (activityRecord.isVisible()) {
                    setVisible(true);
                    setVisibleRequested(true);
                    this.mVisibleSetFromTransferredStartingWindow = true;
                }
                setClientVisible(activityRecord.isClientVisible());
                if (activityRecord.isAnimating()) {
                    transferAnimation(activityRecord);
                    this.mTransitionChangeFlags |= 8;
                } else if (this.mTransitionController.getTransitionPlayer() != null) {
                    this.mTransitionChangeFlags |= 8;
                }
                activityRecord.postWindowRemoveStartingWindowCleanup(windowState);
                activityRecord.mVisibleSetFromTransferredStartingWindow = false;
                this.mWmService.updateFocusedWindowLocked(3, true);
                getDisplayContent().setLayoutNeeded();
                this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
                return true;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        } else if (activityRecord.mStartingData != null) {
            if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -443173857, 0, (String) null, new Object[]{String.valueOf(activityRecord), String.valueOf(this)});
            }
            this.mStartingData = activityRecord.mStartingData;
            activityRecord.mStartingData = null;
            activityRecord.startingMoved = true;
            scheduleAddStartingWindow();
            return true;
        } else {
            return false;
        }
    }

    public void transferStartingWindowFromHiddenAboveTokenIfNeeded() {
        this.task.forAllActivities(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda26
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$transferStartingWindowFromHiddenAboveTokenIfNeeded$9;
                lambda$transferStartingWindowFromHiddenAboveTokenIfNeeded$9 = ActivityRecord.this.lambda$transferStartingWindowFromHiddenAboveTokenIfNeeded$9((ActivityRecord) obj);
                return lambda$transferStartingWindowFromHiddenAboveTokenIfNeeded$9;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$transferStartingWindowFromHiddenAboveTokenIfNeeded$9(ActivityRecord activityRecord) {
        if (activityRecord == this) {
            return true;
        }
        return !activityRecord.isVisibleRequested() && transferStartingWindow(activityRecord);
    }

    public boolean isKeyguardLocked() {
        DisplayContent displayContent = this.mDisplayContent;
        return displayContent != null ? displayContent.isKeyguardLocked() : this.mRootWindowContainer.getDefaultDisplay().isKeyguardLocked();
    }

    public void checkKeyguardFlagsChanged() {
        boolean containsDismissKeyguardWindow = containsDismissKeyguardWindow();
        boolean containsShowWhenLockedWindow = containsShowWhenLockedWindow();
        if (containsDismissKeyguardWindow != this.mLastContainsDismissKeyguardWindow || containsShowWhenLockedWindow != this.mLastContainsShowWhenLockedWindow) {
            this.mDisplayContent.notifyKeyguardFlagsChanged();
        }
        this.mLastContainsDismissKeyguardWindow = containsDismissKeyguardWindow;
        this.mLastContainsShowWhenLockedWindow = containsShowWhenLockedWindow;
        this.mLastContainsTurnScreenOnWindow = containsTurnScreenOnWindow();
    }

    public boolean containsDismissKeyguardWindow() {
        if (isRelaunching()) {
            return this.mLastContainsDismissKeyguardWindow;
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if ((((WindowState) this.mChildren.get(size)).mAttrs.flags & 4194304) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsShowWhenLockedWindow() {
        if (isRelaunching()) {
            return this.mLastContainsShowWhenLockedWindow;
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if ((((WindowState) this.mChildren.get(size)).mAttrs.flags & 524288) != 0) {
                return true;
            }
        }
        return false;
    }

    public void setShowWhenLocked(boolean z) {
        this.mShowWhenLocked = z;
        this.mAtmService.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
    }

    public void setInheritShowWhenLocked(boolean z) {
        this.mInheritShownWhenLocked = z;
        this.mAtmService.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
    }

    public static boolean canShowWhenLocked(ActivityRecord activityRecord) {
        ActivityRecord activityBelow;
        if (activityRecord == null || activityRecord.getTaskFragment() == null) {
            return false;
        }
        if (activityRecord.inPinnedWindowingMode() || !(activityRecord.mShowWhenLocked || activityRecord.containsShowWhenLockedWindow())) {
            if (!activityRecord.mInheritShownWhenLocked || (activityBelow = activityRecord.getTaskFragment().getActivityBelow(activityRecord)) == null || activityBelow.inPinnedWindowingMode()) {
                return false;
            }
            return activityBelow.mShowWhenLocked || activityBelow.containsShowWhenLockedWindow();
        }
        return true;
    }

    public boolean canShowWhenLocked() {
        TaskFragment taskFragment = getTaskFragment();
        if (taskFragment == null || taskFragment.getAdjacentTaskFragment() == null || !taskFragment.isEmbedded()) {
            return canShowWhenLocked(this);
        }
        return canShowWhenLocked(this) && canShowWhenLocked(taskFragment.getAdjacentTaskFragment().getTopNonFinishingActivity());
    }

    public boolean canShowWindows() {
        boolean z;
        boolean isAnimating;
        if (this.mTransitionController.isShellTransitionsEnabled()) {
            z = this.mSyncState != 1;
        } else {
            z = this.allDrawn;
        }
        if (this.mTransitionController.isShellTransitionsEnabled()) {
            isAnimating = this.mTransitionController.inPlayingTransition(this);
        } else {
            isAnimating = isAnimating(2, 1);
        }
        if (z) {
            return (isAnimating && hasNonDefaultColorWindow()) ? false : true;
        }
        return false;
    }

    public static /* synthetic */ boolean lambda$hasNonDefaultColorWindow$10(WindowState windowState) {
        return windowState.mAttrs.getColorMode() != 0;
    }

    public final boolean hasNonDefaultColorWindow() {
        return forAllWindows(new ToBooleanFunction() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda23
            public final boolean apply(Object obj) {
                boolean lambda$hasNonDefaultColorWindow$10;
                lambda$hasNonDefaultColorWindow$10 = ActivityRecord.lambda$hasNonDefaultColorWindow$10((WindowState) obj);
                return lambda$hasNonDefaultColorWindow$10;
            }
        }, true);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean forAllActivities(Predicate<ActivityRecord> predicate, boolean z) {
        return predicate.test(this);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void forAllActivities(Consumer<ActivityRecord> consumer, boolean z) {
        consumer.accept(this);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public ActivityRecord getActivity(Predicate<ActivityRecord> predicate, boolean z, ActivityRecord activityRecord) {
        if (predicate.test(this)) {
            return this;
        }
        return null;
    }

    public void logStartActivity(int i, Task task) {
        Uri data = this.intent.getData();
        EventLog.writeEvent(i, Integer.valueOf(this.mUserId), Integer.valueOf(System.identityHashCode(this)), Integer.valueOf(task.mTaskId), this.shortComponentName, this.intent.getAction(), this.intent.getType(), data != null ? data.toSafeString() : null, Integer.valueOf(this.intent.getFlags()));
    }

    public UriPermissionOwner getUriPermissionsLocked() {
        if (this.uriPermissions == null) {
            this.uriPermissions = new UriPermissionOwner(this.mAtmService.mUgmInternal, this);
        }
        return this.uriPermissions;
    }

    public void addResultLocked(ActivityRecord activityRecord, String str, int i, int i2, Intent intent) {
        ActivityResult activityResult = new ActivityResult(activityRecord, str, i, i2, intent);
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(activityResult);
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x002c  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0031 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void removeResultsLocked(ActivityRecord activityRecord, String str, int i) {
        ArrayList<ResultInfo> arrayList = this.results;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ActivityResult activityResult = (ActivityResult) this.results.get(size);
                if (activityResult.mFrom == activityRecord) {
                    String str2 = ((ResultInfo) activityResult).mResultWho;
                    if (str2 != null) {
                        if (!str2.equals(str)) {
                        }
                        if (((ResultInfo) activityResult).mRequestCode != i) {
                        }
                    } else {
                        if (str != null) {
                        }
                        if (((ResultInfo) activityResult).mRequestCode != i) {
                            this.results.remove(size);
                        }
                    }
                }
            }
        }
    }

    public void sendResult(int i, String str, int i2, int i3, Intent intent, NeededUriGrants neededUriGrants) {
        sendResult(i, str, i2, i3, intent, neededUriGrants, false);
    }

    public void sendResult(int i, String str, int i2, int i3, Intent intent, NeededUriGrants neededUriGrants, boolean z) {
        if (i > 0) {
            this.mAtmService.mUgmInternal.grantUriPermissionUncheckedFromIntent(neededUriGrants, getUriPermissionsLocked());
        }
        if (isState(State.RESUMED) && attachedToProcess()) {
            try {
                ArrayList arrayList = new ArrayList();
                arrayList.add(new ResultInfo(str, i2, i3, intent));
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ClientTransactionItem) ActivityResultItem.obtain(arrayList));
                return;
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Exception thrown sending result to " + this, e);
            }
        }
        if (z && attachedToProcess() && isState(State.STARTED, State.PAUSING, State.PAUSED, State.STOPPING, State.STOPPED)) {
            ClientTransaction obtain = ClientTransaction.obtain(this.app.getThread(), this.token);
            obtain.addCallback(ActivityResultItem.obtain(List.of(new ResultInfo(str, i2, i3, intent))));
            ActivityLifecycleItem lifecycleItemForCurrentStateForResult = getLifecycleItemForCurrentStateForResult();
            if (lifecycleItemForCurrentStateForResult != null) {
                obtain.setLifecycleStateRequest(lifecycleItemForCurrentStateForResult);
            } else {
                Slog.w("ActivityTaskManager", "Unable to get the lifecycle item for state " + this.mState + " so couldn't immediately send result");
            }
            try {
                this.mAtmService.getLifecycleManager().scheduleTransaction(obtain);
            } catch (RemoteException e2) {
                Slog.w("ActivityTaskManager", "Exception thrown sending result to " + this, e2);
            }
        }
        addResultLocked(null, str, i2, i3, intent);
    }

    public final ActivityLifecycleItem getLifecycleItemForCurrentStateForResult() {
        int i = C18376.$SwitchMap$com$android$server$wm$ActivityRecord$State[this.mState.ordinal()];
        if (i == 2 || i == 3) {
            return PauseActivityItem.obtain();
        }
        if (i != 4) {
            if (i == 5) {
                return StartActivityItem.obtain((ActivityOptions) null);
            }
            if (i != 6) {
                return null;
            }
        }
        return StopActivityItem.obtain(this.configChangeFlags);
    }

    public final void addNewIntentLocked(ReferrerIntent referrerIntent) {
        if (this.newIntents == null) {
            this.newIntents = new ArrayList<>();
        }
        this.newIntents.add(referrerIntent);
    }

    public final boolean isSleeping() {
        Task rootTask = getRootTask();
        return rootTask != null ? rootTask.shouldSleepActivities() : this.mAtmService.isSleepingLocked();
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void deliverNewIntentLocked(int i, Intent intent, NeededUriGrants neededUriGrants, String str) {
        this.mAtmService.mUgmInternal.grantUriPermissionUncheckedFromIntent(neededUriGrants, getUriPermissionsLocked());
        ReferrerIntent referrerIntent = new ReferrerIntent(intent, getFilteredReferrer(str));
        boolean z = false;
        boolean z2 = isTopRunningActivity() && isSleeping();
        State state = this.mState;
        State state2 = State.RESUMED;
        if ((state == state2 || state == State.PAUSED || z2) && attachedToProcess()) {
            try {
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(referrerIntent);
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ClientTransactionItem) NewIntentItem.obtain(arrayList, this.mState == state2));
            } catch (RemoteException e) {
                Slog.w("ActivityTaskManager", "Exception thrown sending new intent to " + this, e);
            } catch (NullPointerException e2) {
                Slog.w("ActivityTaskManager", "Exception thrown sending new intent to " + this, e2);
            }
            if (z) {
                return;
            }
            addNewIntentLocked(referrerIntent);
            return;
        }
        z = true;
        if (z) {
        }
    }

    public void updateOptionsLocked(ActivityOptions activityOptions) {
        if (activityOptions != null) {
            ActivityOptions activityOptions2 = this.mPendingOptions;
            if (activityOptions2 != null) {
                activityOptions2.abort();
            }
            setOptions(activityOptions);
        }
    }

    public boolean getLaunchedFromBubble() {
        return this.mLaunchedFromBubble;
    }

    public final void setOptions(ActivityOptions activityOptions) {
        this.mLaunchedFromBubble = activityOptions.getLaunchedFromBubble();
        this.mPendingOptions = activityOptions;
        if (activityOptions.getAnimationType() == 13) {
            this.mPendingRemoteAnimation = activityOptions.getRemoteAnimationAdapter();
        }
        this.mPendingRemoteTransition = activityOptions.getRemoteTransition();
    }

    public void applyOptionsAnimation() {
        RemoteAnimationAdapter remoteAnimationAdapter = this.mPendingRemoteAnimation;
        if (remoteAnimationAdapter != null) {
            this.mDisplayContent.mAppTransition.overridePendingAppTransitionRemote(remoteAnimationAdapter);
            this.mTransitionController.setStatusBarTransitionDelay(this.mPendingRemoteAnimation.getStatusBarTransitionDelay());
        } else {
            ActivityOptions activityOptions = this.mPendingOptions;
            if (activityOptions == null || activityOptions.getAnimationType() == 5) {
                return;
            }
            applyOptionsAnimation(this.mPendingOptions, this.intent);
        }
        clearOptionsAnimationForSiblings();
    }

    /* JADX WARN: Removed duplicated region for block: B:58:0x0214  */
    /* JADX WARN: Removed duplicated region for block: B:60:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void applyOptionsAnimation(ActivityOptions activityOptions, Intent intent) {
        IRemoteCallback iRemoteCallback;
        IRemoteCallback iRemoteCallback2;
        TransitionInfo.AnimationOptions makeScaleUpAnimOptions;
        int animationType = activityOptions.getAnimationType();
        DisplayContent displayContent = getDisplayContent();
        TransitionInfo.AnimationOptions animationOptions = null;
        if (animationType != -1 && animationType != 0) {
            if (animationType != 1) {
                if (animationType == 2) {
                    displayContent.mAppTransition.overridePendingAppTransitionScaleUp(activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getWidth(), activityOptions.getHeight());
                    makeScaleUpAnimOptions = TransitionInfo.AnimationOptions.makeScaleUpAnimOptions(activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getWidth(), activityOptions.getHeight());
                    if (intent.getSourceBounds() == null) {
                        intent.setSourceBounds(new Rect(activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getStartX() + activityOptions.getWidth(), activityOptions.getStartY() + activityOptions.getHeight()));
                    }
                } else if (animationType == 3 || animationType == 4) {
                    boolean z = animationType == 3;
                    HardwareBuffer thumbnail = activityOptions.getThumbnail();
                    displayContent.mAppTransition.overridePendingAppTransitionThumb(thumbnail, activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getAnimationStartedListener(), z);
                    TransitionInfo.AnimationOptions makeThumbnailAnimOptions = TransitionInfo.AnimationOptions.makeThumbnailAnimOptions(thumbnail, activityOptions.getStartX(), activityOptions.getStartY(), z);
                    IRemoteCallback animationStartedListener = activityOptions.getAnimationStartedListener();
                    if (intent.getSourceBounds() == null && thumbnail != null) {
                        intent.setSourceBounds(new Rect(activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getStartX() + thumbnail.getWidth(), activityOptions.getStartY() + thumbnail.getHeight()));
                    }
                    iRemoteCallback = animationStartedListener;
                    iRemoteCallback2 = null;
                    animationOptions = makeThumbnailAnimOptions;
                } else if (animationType == 8 || animationType == 9) {
                    AppTransitionAnimationSpec[] animSpecs = activityOptions.getAnimSpecs();
                    IAppTransitionAnimationSpecsFuture specsFuture = activityOptions.getSpecsFuture();
                    if (specsFuture != null) {
                        displayContent.mAppTransition.overridePendingAppTransitionMultiThumbFuture(specsFuture, activityOptions.getAnimationStartedListener(), animationType == 8);
                    } else if (animationType == 9 && animSpecs != null) {
                        displayContent.mAppTransition.overridePendingAppTransitionMultiThumb(animSpecs, activityOptions.getAnimationStartedListener(), activityOptions.getAnimationFinishedListener(), false);
                    } else {
                        displayContent.mAppTransition.overridePendingAppTransitionAspectScaledThumb(activityOptions.getThumbnail(), activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getWidth(), activityOptions.getHeight(), activityOptions.getAnimationStartedListener(), animationType == 8);
                        if (intent.getSourceBounds() == null) {
                            intent.setSourceBounds(new Rect(activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getStartX() + activityOptions.getWidth(), activityOptions.getStartY() + activityOptions.getHeight()));
                        }
                    }
                } else if (animationType == 11) {
                    displayContent.mAppTransition.overridePendingAppTransitionClipReveal(activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getWidth(), activityOptions.getHeight());
                    makeScaleUpAnimOptions = TransitionInfo.AnimationOptions.makeClipRevealAnimOptions(activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getWidth(), activityOptions.getHeight());
                    if (intent.getSourceBounds() == null) {
                        intent.setSourceBounds(new Rect(activityOptions.getStartX(), activityOptions.getStartY(), activityOptions.getStartX() + activityOptions.getWidth(), activityOptions.getStartY() + activityOptions.getHeight()));
                    }
                } else if (animationType == 12) {
                    displayContent.mAppTransition.overridePendingAppTransitionStartCrossProfileApps();
                    iRemoteCallback2 = null;
                    animationOptions = TransitionInfo.AnimationOptions.makeCrossProfileAnimOptions();
                    iRemoteCallback = null;
                } else {
                    Slog.e(StartingSurfaceController.TAG, "applyOptionsLocked: Unknown animationType=" + animationType);
                }
                iRemoteCallback = null;
                animationOptions = makeScaleUpAnimOptions;
                iRemoteCallback2 = iRemoteCallback;
            } else {
                displayContent.mAppTransition.overridePendingAppTransition(activityOptions.getPackageName(), activityOptions.getCustomEnterResId(), activityOptions.getCustomExitResId(), activityOptions.getCustomBackgroundColor(), activityOptions.getAnimationStartedListener(), activityOptions.getAnimationFinishedListener(), activityOptions.getOverrideTaskTransition());
                animationOptions = TransitionInfo.AnimationOptions.makeCustomAnimOptions(activityOptions.getPackageName(), activityOptions.getCustomEnterResId(), activityOptions.getCustomExitResId(), activityOptions.getCustomBackgroundColor(), activityOptions.getOverrideTaskTransition());
                iRemoteCallback = activityOptions.getAnimationStartedListener();
                iRemoteCallback2 = activityOptions.getAnimationFinishedListener();
            }
            if (animationOptions == null) {
                this.mTransitionController.setOverrideAnimation(animationOptions, iRemoteCallback, iRemoteCallback2);
                return;
            }
            return;
        }
        iRemoteCallback = null;
        iRemoteCallback2 = iRemoteCallback;
        if (animationOptions == null) {
        }
    }

    public void clearAllDrawn() {
        this.allDrawn = false;
        this.mLastAllDrawn = false;
    }

    public final boolean allDrawnStatesConsidered() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            WindowState windowState = (WindowState) this.mChildren.get(size);
            if (windowState.mightAffectAllDrawn() && !windowState.getDrawnStateEvaluated()) {
                return false;
            }
        }
        return true;
    }

    public void updateAllDrawn() {
        int i;
        if (this.allDrawn || (i = this.mNumInterestingWindows) <= 0 || !allDrawnStatesConsidered() || this.mNumDrawnWindows < i || isRelaunching()) {
            return;
        }
        this.allDrawn = true;
        DisplayContent displayContent = this.mDisplayContent;
        if (displayContent != null) {
            displayContent.setLayoutNeeded();
        }
        this.mWmService.f1164mH.obtainMessage(32, this).sendToTarget();
    }

    public void abortAndClearOptionsAnimation() {
        ActivityOptions activityOptions = this.mPendingOptions;
        if (activityOptions != null) {
            activityOptions.abort();
        }
        clearOptionsAnimation();
    }

    public void clearOptionsAnimation() {
        this.mPendingOptions = null;
        this.mPendingRemoteAnimation = null;
        this.mPendingRemoteTransition = null;
    }

    public void clearOptionsAnimationForSiblings() {
        Task task = this.task;
        if (task == null) {
            clearOptionsAnimation();
        } else {
            task.forAllActivities(new Consumer() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda20
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ActivityRecord) obj).clearOptionsAnimation();
                }
            });
        }
    }

    public ActivityOptions getOptions() {
        return this.mPendingOptions;
    }

    public ActivityOptions takeOptions() {
        ActivityOptions activityOptions = this.mPendingOptions;
        if (activityOptions == null) {
            return null;
        }
        this.mPendingOptions = null;
        activityOptions.setRemoteTransition(null);
        activityOptions.setRemoteAnimationAdapter(null);
        return activityOptions;
    }

    public RemoteTransition takeRemoteTransition() {
        RemoteTransition remoteTransition = this.mPendingRemoteTransition;
        this.mPendingRemoteTransition = null;
        return remoteTransition;
    }

    public boolean allowMoveToFront() {
        ActivityOptions activityOptions = this.mPendingOptions;
        return activityOptions == null || !activityOptions.getAvoidMoveToFront();
    }

    public void removeUriPermissionsLocked() {
        UriPermissionOwner uriPermissionOwner = this.uriPermissions;
        if (uriPermissionOwner != null) {
            uriPermissionOwner.removeUriPermissions();
            this.uriPermissions = null;
        }
    }

    public void pauseKeyDispatchingLocked() {
        if (this.keysPaused) {
            return;
        }
        this.keysPaused = true;
        if (getDisplayContent() != null) {
            getDisplayContent().getInputMonitor().pauseDispatchingLw(this);
        }
    }

    public void resumeKeyDispatchingLocked() {
        if (this.keysPaused) {
            this.keysPaused = false;
            if (getDisplayContent() != null) {
                getDisplayContent().getInputMonitor().resumeDispatchingLw(this);
            }
        }
    }

    public final void updateTaskDescription(CharSequence charSequence) {
        this.task.lastDescription = charSequence;
    }

    public void setDeferHidingClient(boolean z) {
        if (this.mDeferHidingClient == z) {
            return;
        }
        this.mDeferHidingClient = z;
        if (z || this.mVisibleRequested) {
            return;
        }
        setVisibility(false);
    }

    public boolean getDeferHidingClient() {
        return this.mDeferHidingClient;
    }

    public boolean canAffectSystemUiFlags() {
        Task task = this.task;
        return task != null && task.canAffectSystemUiFlags() && isVisible() && !inPinnedWindowingMode();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isVisible() {
        return this.mVisible;
    }

    public void setVisible(boolean z) {
        if (z != this.mVisible) {
            this.mVisible = z;
            WindowProcessController windowProcessController = this.app;
            if (windowProcessController != null) {
                this.mTaskSupervisor.onProcessActivityStateChanged(windowProcessController, false);
            }
            scheduleAnimation();
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean setVisibleRequested(boolean z) {
        if (super.setVisibleRequested(z)) {
            setInsetsFrozen(!z);
            updateVisibleForServiceConnection();
            WindowProcessController windowProcessController = this.app;
            if (windowProcessController != null) {
                this.mTaskSupervisor.onProcessActivityStateChanged(windowProcessController, false);
            }
            logAppCompatState();
            if (z) {
                return true;
            }
            finishOrAbortReplacingWindow();
            return true;
        }
        return false;
    }

    public void setVisibility(boolean z) {
        if (getParent() == null) {
            Slog.w(StartingSurfaceController.TAG, "Attempted to set visibility of non-existing app token: " + this.token);
            return;
        }
        if (z) {
            this.mDeferHidingClient = false;
        }
        setVisibility(z, this.mDeferHidingClient);
        this.mAtmService.addWindowLayoutReasons(2);
        this.mTaskSupervisor.getActivityMetricsLogger().notifyVisibilityChanged(this);
        this.mTaskSupervisor.mAppVisibilitiesChangedSinceLastPause = true;
    }

    public final void setVisibility(boolean z, boolean z2) {
        boolean z3;
        AppTransition appTransition = getDisplayContent().mAppTransition;
        if (!z && !this.mVisibleRequested) {
            if (z2 || !this.mLastDeferHidingClient) {
                return;
            }
            this.mLastDeferHidingClient = z2;
            setClientVisible(false);
            return;
        }
        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, -374767836, 972, (String) null, new Object[]{String.valueOf(this.token), Boolean.valueOf(z), String.valueOf(appTransition), Boolean.valueOf(isVisible()), Boolean.valueOf(this.mVisibleRequested), String.valueOf(Debug.getCallers(6))});
        }
        if (this.mTransitionController.isShellTransitionsEnabled()) {
            z3 = this.mTransitionController.isCollecting();
            if (z3) {
                this.mTransitionController.collect(this);
            } else {
                Slog.e("ActivityTaskManager", "setVisibility=" + z + " while transition is not collecting " + this + " caller=" + Debug.getCallers(8));
            }
        } else {
            z3 = false;
        }
        onChildVisibilityRequested(z);
        DisplayContent displayContent = getDisplayContent();
        displayContent.mOpeningApps.remove(this);
        displayContent.mClosingApps.remove(this);
        this.waitingToShow = false;
        setVisibleRequested(z);
        this.mLastDeferHidingClient = z2;
        if (!z) {
            removeDeadWindows();
            if (this.finishing || isState(State.STOPPED)) {
                displayContent.mUnknownAppVisibilityController.appRemovedOrHidden(this);
            }
        } else {
            if (!appTransition.isTransitionSet() && appTransition.isReady()) {
                displayContent.mOpeningApps.add(this);
            }
            this.startingMoved = false;
            if (!isVisible() || this.mAppStopped) {
                clearAllDrawn();
                if (!isVisible()) {
                    this.waitingToShow = true;
                    if (!isClientVisible()) {
                        forAllWindows(new Consumer() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda1
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                ActivityRecord.lambda$setVisibility$11((WindowState) obj);
                            }
                        }, true);
                    }
                }
            }
            setClientVisible(true);
            requestUpdateWallpaperIfNeeded();
            if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, 1224184681, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            this.mAppStopped = false;
            transferStartingWindowFromHiddenAboveTokenIfNeeded();
        }
        if (z3) {
            if (z || !this.mTransitionController.inPlayingTransition(this)) {
                return;
            }
            this.mTransitionChangeFlags |= 32768;
        } else if (deferCommitVisibilityChange(z)) {
        } else {
            commitVisibility(z, true);
            updateReportedVisibilityLocked();
        }
    }

    public static /* synthetic */ void lambda$setVisibility$11(WindowState windowState) {
        WindowStateAnimator windowStateAnimator = windowState.mWinAnimator;
        if (windowStateAnimator.mDrawState == 4) {
            windowStateAnimator.resetDrawState();
            windowState.forceReportingResized();
        }
    }

    public final boolean deferCommitVisibilityChange(boolean z) {
        WindowState findFocusedWindow;
        ActivityRecord activityRecord;
        if (this.mTransitionController.isShellTransitionsEnabled()) {
            return false;
        }
        if (this.mDisplayContent.mAppTransition.isTransitionSet() || (!isActivityTypeHome() && isAnimating(2, 8))) {
            if (this.mWaitForEnteringPinnedMode && this.mVisible == z) {
                return false;
            }
            if (okToAnimate(true, canTurnScreenOn() || this.mTaskSupervisor.getKeyguardController().isKeyguardGoingAway(this.mDisplayContent.mDisplayId))) {
                if (z) {
                    this.mDisplayContent.mOpeningApps.add(this);
                    this.mEnteringAnimation = true;
                } else if (this.mVisible) {
                    this.mDisplayContent.mClosingApps.add(this);
                    this.mEnteringAnimation = false;
                }
                if ((this.mDisplayContent.mAppTransition.getTransitFlags() & 32) != 0 && (findFocusedWindow = this.mDisplayContent.findFocusedWindow()) != null && (activityRecord = findFocusedWindow.mActivityRecord) != null) {
                    if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, 1810019902, 0, (String) null, new Object[]{String.valueOf(activityRecord)});
                    }
                    this.mDisplayContent.mOpeningApps.add(activityRecord);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean applyAnimation(WindowManager.LayoutParams layoutParams, int i, boolean z, boolean z2, ArrayList<WindowContainer> arrayList) {
        if ((this.mTransitionChangeFlags & 8) != 0) {
            return false;
        }
        this.mRequestForceTransition = false;
        return super.applyAnimation(layoutParams, i, z, z2, arrayList);
    }

    public void commitVisibility(boolean z, boolean z2, boolean z3) {
        this.mVisibleSetFromTransferredStartingWindow = false;
        if (z == isVisible()) {
            return;
        }
        int size = this.mChildren.size();
        boolean isAnimating = WindowManagerService.sEnableShellTransitions ? z : isAnimating(2, 1);
        for (int i = 0; i < size; i++) {
            ((WindowState) this.mChildren.get(i)).onAppVisibilityChanged(z, isAnimating);
        }
        setVisible(z);
        setVisibleRequested(z);
        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, -636553602, 1020, (String) null, new Object[]{String.valueOf(this), Boolean.valueOf(isVisible()), Boolean.valueOf(this.mVisibleRequested), Boolean.valueOf(isInTransition()), Boolean.valueOf(isAnimating), String.valueOf(Debug.getCallers(5))});
        }
        if (!z) {
            stopFreezingScreen(true, true);
        } else {
            WindowState windowState = this.mStartingWindow;
            if (windowState != null && !windowState.isDrawn() && (this.firstWindowDrawn || this.allDrawn)) {
                this.mStartingWindow.clearPolicyVisibilityFlag(1);
                this.mStartingWindow.mLegacyPolicyVisibilityAfterAnim = false;
            }
            final WindowManagerService windowManagerService = this.mWmService;
            Objects.requireNonNull(windowManagerService);
            forAllWindows(new Consumer() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda21
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WindowManagerService.this.makeWindowFreezingScreenIfNeededLocked((WindowState) obj);
                }
            }, true);
        }
        for (Task organizedTask = getOrganizedTask(); organizedTask != null; organizedTask = organizedTask.getParent().asTask()) {
            organizedTask.dispatchTaskInfoChangedIfNeeded(false);
        }
        DisplayContent displayContent = getDisplayContent();
        displayContent.getInputMonitor().setUpdateInputWindowsNeededLw();
        if (z2) {
            this.mWmService.updateFocusedWindowLocked(3, false);
            this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
        }
        displayContent.getInputMonitor().updateInputWindowsLw(false);
        this.mTransitionChangeFlags = 0;
        postApplyAnimation(z, z3);
    }

    public void commitVisibility(boolean z, boolean z2) {
        commitVisibility(z, z2, false);
    }

    public void setNeedsLetterboxedAnimation(boolean z) {
        this.mNeedsLetterboxedAnimation = z;
    }

    public boolean isNeedsLetterboxedAnimation() {
        return this.mNeedsLetterboxedAnimation;
    }

    public boolean isInLetterboxAnimation() {
        return this.mNeedsLetterboxedAnimation && isAnimating();
    }

    public final void postApplyAnimation(boolean z, boolean z2) {
        WindowState windowState;
        boolean isShellTransitionsEnabled = this.mTransitionController.isShellTransitionsEnabled();
        boolean z3 = false;
        boolean z4 = !isShellTransitionsEnabled && isAnimating(6, 25);
        if (!z4 && !isShellTransitionsEnabled) {
            onAnimationFinished(1, null);
            if (z) {
                this.mEnteringAnimation = true;
                this.mWmService.mActivityManagerAppTransitionNotifier.onAppTransitionFinishedLocked(this.token);
            }
        }
        if (z || isShellTransitionsEnabled || !isAnimating(2, 9)) {
            setClientVisible(z);
        }
        if (!z) {
            InputTarget imeInputTarget = this.mDisplayContent.getImeInputTarget();
            if (imeInputTarget != null && imeInputTarget.getWindowState() != null && imeInputTarget.getWindowState().mActivityRecord == this && (windowState = this.mDisplayContent.mInputMethodWindow) != null && windowState.isVisible()) {
                z3 = true;
            }
            this.mLastImeShown = z3;
            this.mImeInsetsFrozenUntilStartInput = true;
        }
        DisplayContent displayContent = getDisplayContent();
        if (!displayContent.mClosingApps.contains(this) && !displayContent.mOpeningApps.contains(this) && !z2) {
            this.mWmService.mTaskSnapshotController.notifyAppVisibilityChanged(this, z);
        }
        if (isShellTransitionsEnabled || isVisible() || z4 || displayContent.mAppTransition.isTransitionSet()) {
            return;
        }
        SurfaceControl.openTransaction();
        try {
            forAllWindows(new Consumer() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda28
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ActivityRecord.lambda$postApplyAnimation$12((WindowState) obj);
                }
            }, true);
        } finally {
            SurfaceControl.closeTransaction();
        }
    }

    public static /* synthetic */ void lambda$postApplyAnimation$12(WindowState windowState) {
        windowState.mWinAnimator.hide(SurfaceControl.getGlobalTransaction(), "immediately hidden");
    }

    public void commitFinishDrawing(SurfaceControl.Transaction transaction) {
        boolean z = false;
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            z |= ((WindowState) this.mChildren.get(size)).commitFinishDrawing(transaction);
        }
        if (z) {
            requestUpdateWallpaperIfNeeded();
        }
    }

    public boolean shouldApplyAnimation(boolean z) {
        if (isVisible() != z || this.mRequestForceTransition) {
            return true;
        }
        if (isVisible() || !this.mIsExiting) {
            return z && forAllWindows(new ToBooleanFunction() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda2
                public final boolean apply(Object obj) {
                    return ((WindowState) obj).waitingForReplacement();
                }
            }, true);
        }
        return true;
    }

    public void setRecentsScreenshotEnabled(boolean z) {
        this.mEnableRecentsScreenshot = z;
    }

    public boolean shouldUseAppThemeSnapshot() {
        return !this.mEnableRecentsScreenshot || forAllWindows(new ToBooleanFunction() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda22
            public final boolean apply(Object obj) {
                return ((WindowState) obj).isSecureLocked();
            }
        }, true);
    }

    public void setCurrentLaunchCanTurnScreenOn(boolean z) {
        this.mCurrentLaunchCanTurnScreenOn = z;
    }

    public boolean currentLaunchCanTurnScreenOn() {
        return this.mCurrentLaunchCanTurnScreenOn;
    }

    public void setState(State state, String str) {
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 1316533291, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(getState()), String.valueOf(state), String.valueOf(str)});
        }
        if (state == this.mState) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -926231510, 0, (String) null, new Object[]{String.valueOf(state)});
                return;
            }
            return;
        }
        this.mState = state;
        if (getTaskFragment() != null) {
            getTaskFragment().onActivityStateChanged(this, state, str);
        }
        if (state == State.STOPPING && !isSleeping() && getParent() == null) {
            Slog.w(StartingSurfaceController.TAG, "Attempted to notify stopping on non-existing app token: " + this.token);
            return;
        }
        updateVisibleForServiceConnection();
        WindowProcessController windowProcessController = this.app;
        if (windowProcessController != null) {
            this.mTaskSupervisor.onProcessActivityStateChanged(windowProcessController, false);
        }
        int i = C18376.$SwitchMap$com$android$server$wm$ActivityRecord$State[state.ordinal()];
        if (i == 1) {
            this.mAtmService.updateBatteryStats(this, true);
            this.mAtmService.updateActivityUsageStats(this, 1);
        } else if (i == 3) {
            this.mAtmService.updateBatteryStats(this, false);
            this.mAtmService.updateActivityUsageStats(this, 2);
            return;
        } else if (i != 5) {
            if (i == 6) {
                this.mAtmService.updateActivityUsageStats(this, 23);
                return;
            }
            if (i == 7) {
                if (this.app != null && (this.mVisible || this.mVisibleRequested)) {
                    this.mAtmService.updateBatteryStats(this, false);
                }
                this.mAtmService.updateActivityUsageStats(this, 24);
            } else if (i != 8) {
                return;
            }
            WindowProcessController windowProcessController2 = this.app;
            if (windowProcessController2 == null || windowProcessController2.hasActivities()) {
                return;
            }
            this.app.updateProcessInfo(true, false, true, false);
            return;
        }
        WindowProcessController windowProcessController3 = this.app;
        if (windowProcessController3 != null) {
            windowProcessController3.updateProcessInfo(false, true, true, true);
        }
        ContentCaptureManagerInternal contentCaptureManagerInternal = (ContentCaptureManagerInternal) LocalServices.getService(ContentCaptureManagerInternal.class);
        if (contentCaptureManagerInternal != null) {
            contentCaptureManagerInternal.notifyActivityEvent(this.mUserId, this.mActivityComponent, FrameworkStatsLog.WIFI_BYTES_TRANSFER, new ActivityId(getTask() != null ? getTask().mTaskId : -1, this.shareableActivityToken));
        }
    }

    public State getState() {
        return this.mState;
    }

    public boolean isState(State state) {
        return state == this.mState;
    }

    public boolean isState(State state, State state2) {
        State state3 = this.mState;
        return state == state3 || state2 == state3;
    }

    public boolean isState(State state, State state2, State state3, State state4) {
        State state5 = this.mState;
        return state == state5 || state2 == state5 || state3 == state5 || state4 == state5;
    }

    public boolean isState(State state, State state2, State state3, State state4, State state5) {
        State state6 = this.mState;
        return state == state6 || state2 == state6 || state3 == state6 || state4 == state6 || state5 == state6;
    }

    public void destroySurfaces() {
        destroySurfaces(false);
    }

    public final void destroySurfaces(boolean z) {
        ArrayList arrayList = new ArrayList(this.mChildren);
        boolean z2 = false;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            z2 |= ((WindowState) arrayList.get(size)).destroySurface(z, this.mAppStopped);
        }
        if (z2) {
            getDisplayContent().assignWindowLayers(true);
            updateLetterboxSurface(null);
        }
    }

    public void notifyAppResumed() {
        if (getParent() == null) {
            Slog.w(StartingSurfaceController.TAG, "Attempted to notify resumed of non-existing app token: " + this.token);
            return;
        }
        boolean z = this.mAppStopped;
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, 1364498663, 3, (String) null, new Object[]{Boolean.valueOf(z), String.valueOf(this)});
        }
        this.mAppStopped = false;
        if (this.mAtmService.getActivityStartController().isInExecution()) {
            setCurrentLaunchCanTurnScreenOn(true);
        }
        if (z) {
            return;
        }
        destroySurfaces(true);
    }

    public void notifyUnknownVisibilityLaunchedForKeyguardTransition() {
        if (this.noDisplay || !isKeyguardLocked()) {
            return;
        }
        this.mDisplayContent.mUnknownAppVisibilityController.notifyLaunched(this);
    }

    public final boolean shouldBeVisible(boolean z, boolean z2) {
        updateVisibilityIgnoringKeyguard(z);
        if (z2) {
            return this.visibleIgnoringKeyguard;
        }
        return shouldBeVisibleUnchecked();
    }

    public boolean shouldBeVisibleUnchecked() {
        Task rootTask = getRootTask();
        if (rootTask == null || !this.visibleIgnoringKeyguard) {
            return false;
        }
        if ((inPinnedWindowingMode() && rootTask.isForceHidden()) || hasOverlayOverUntrustedModeEmbedded()) {
            return false;
        }
        if (this.mDisplayContent.isSleeping()) {
            return canTurnScreenOn();
        }
        return this.mTaskSupervisor.getKeyguardController().checkKeyguardVisibility(this);
    }

    public boolean hasOverlayOverUntrustedModeEmbedded() {
        return (!isEmbeddedInUntrustedMode() || getTask() == null || getTask().getActivity(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$hasOverlayOverUntrustedModeEmbedded$13;
                lambda$hasOverlayOverUntrustedModeEmbedded$13 = ActivityRecord.this.lambda$hasOverlayOverUntrustedModeEmbedded$13((ActivityRecord) obj);
                return lambda$hasOverlayOverUntrustedModeEmbedded$13;
            }
        }, this, false, false) == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$hasOverlayOverUntrustedModeEmbedded$13(ActivityRecord activityRecord) {
        return (activityRecord.finishing || activityRecord.getUid() == getUid()) ? false : true;
    }

    public void updateVisibilityIgnoringKeyguard(boolean z) {
        this.visibleIgnoringKeyguard = (!z || this.mLaunchTaskBehind) && showToCurrentUser();
    }

    public boolean shouldBeVisible() {
        Task task = getTask();
        if (task == null) {
            return false;
        }
        return shouldBeVisible((task.shouldBeVisible(null) && task.getOccludingActivityAbove(this) == null) ? false : true, false);
    }

    public void makeVisibleIfNeeded(ActivityRecord activityRecord, boolean z) {
        if ((this.mState == State.RESUMED && this.mVisibleRequested) || this == activityRecord) {
            return;
        }
        Task rootTask = getRootTask();
        try {
            if (rootTask.mTranslucentActivityWaiting != null) {
                updateOptionsLocked(this.returningOptions);
                rootTask.mUndrawnActivitiesBelowTopTranslucent.add(this);
            }
            setVisibility(true);
            this.app.postPendingUiCleanMsg(true);
            if (z) {
                this.mClientVisibilityDeferred = false;
                makeActiveIfNeeded(activityRecord);
            } else {
                this.mClientVisibilityDeferred = true;
            }
            this.mTaskSupervisor.mStoppingActivities.remove(this);
        } catch (Exception e) {
            Slog.w("ActivityTaskManager", "Exception thrown making visible: " + this.intent.getComponent(), e);
        }
        handleAlreadyVisible();
    }

    public void makeInvisible() {
        if (this.mVisibleRequested) {
            try {
                boolean checkEnterPictureInPictureState = checkEnterPictureInPictureState("makeInvisible", true);
                setDeferHidingClient(checkEnterPictureInPictureState && !isState(State.STARTED, State.STOPPING, State.STOPPED, State.PAUSED));
                setVisibility(false);
                int i = C18376.$SwitchMap$com$android$server$wm$ActivityRecord$State[getState().ordinal()];
                if (i != 9) {
                    switch (i) {
                        case 1:
                        case 2:
                        case 3:
                        case 5:
                            break;
                        case 4:
                        case 6:
                            this.supportsEnterPipOnTaskSwitch = false;
                            return;
                        default:
                            return;
                    }
                }
                addToStopping(true, checkEnterPictureInPictureState, "makeInvisible");
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Exception thrown making hidden: " + this.intent.getComponent(), e);
            }
        }
    }

    public boolean makeActiveIfNeeded(ActivityRecord activityRecord) {
        if (shouldResumeActivity(activityRecord)) {
            return getRootTask().resumeTopActivityUncheckedLocked(activityRecord, null);
        }
        if (shouldPauseActivity(activityRecord)) {
            setState(State.PAUSING, "makeActiveIfNeeded");
            try {
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ActivityLifecycleItem) PauseActivityItem.obtain(this.finishing, false, this.configChangeFlags, false, false));
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Exception thrown sending pause: " + this.intent.getComponent(), e);
            }
        } else if (shouldStartActivity()) {
            setState(State.STARTED, "makeActiveIfNeeded");
            try {
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ActivityLifecycleItem) StartActivityItem.obtain(takeOptions()));
            } catch (Exception e2) {
                Slog.w("ActivityTaskManager", "Exception thrown sending start: " + this.intent.getComponent(), e2);
            }
            this.mTaskSupervisor.mStoppingActivities.remove(this);
        }
        return false;
    }

    @VisibleForTesting
    public boolean shouldPauseActivity(ActivityRecord activityRecord) {
        return shouldMakeActive(activityRecord) && !isFocusable() && !isState(State.PAUSING, State.PAUSED) && this.results == null;
    }

    @VisibleForTesting
    public boolean shouldResumeActivity(ActivityRecord activityRecord) {
        return shouldBeResumed(activityRecord) && !isState(State.RESUMED);
    }

    public final boolean shouldBeResumed(ActivityRecord activityRecord) {
        return shouldMakeActive(activityRecord) && isFocusable() && getTaskFragment().getVisibility(activityRecord) == 0 && canResumeByCompat();
    }

    public final boolean shouldStartActivity() {
        return this.mVisibleRequested && (isState(State.STOPPED) || isState(State.STOPPING));
    }

    @VisibleForTesting
    public boolean shouldMakeActive(ActivityRecord activityRecord) {
        if (isState(State.STARTED, State.RESUMED, State.PAUSED, State.STOPPED, State.STOPPING) && getRootTask().mTranslucentActivityWaiting == null && this != activityRecord && this.mTaskSupervisor.readyToResume() && !this.mLaunchTaskBehind) {
            if (this.task.hasChild(this)) {
                return getTaskFragment().topRunningActivity() == this;
            }
            throw new IllegalStateException("Activity not found in its task");
        }
        return false;
    }

    public void handleAlreadyVisible() {
        stopFreezingScreenLocked(false);
        try {
            if (this.returningOptions != null) {
                this.app.getThread().scheduleOnNewActivityOptions(this.token, this.returningOptions.toBundle());
            }
        } catch (RemoteException unused) {
        }
    }

    public static void activityResumedLocked(IBinder iBinder, boolean z) {
        ActivityRecord forTokenLocked = forTokenLocked(iBinder);
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_STATES, 1364126018, 0, (String) null, new Object[]{String.valueOf(forTokenLocked)});
        }
        if (forTokenLocked == null) {
            return;
        }
        forTokenLocked.setCustomizeSplashScreenExitAnimation(z);
        forTokenLocked.setSavedState(null);
        forTokenLocked.mDisplayContent.handleActivitySizeCompatModeIfNeeded(forTokenLocked);
        forTokenLocked.mDisplayContent.mUnknownAppVisibilityController.notifyAppResumedFinished(forTokenLocked);
    }

    public static void activityRefreshedLocked(IBinder iBinder) {
        DisplayRotationCompatPolicy displayRotationCompatPolicy;
        ActivityRecord forTokenLocked = forTokenLocked(iBinder);
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_STATES, -1480918485, 0, (String) null, new Object[]{String.valueOf(forTokenLocked)});
        }
        if (forTokenLocked == null || (displayRotationCompatPolicy = forTokenLocked.mDisplayContent.mDisplayRotationCompatPolicy) == null) {
            return;
        }
        displayRotationCompatPolicy.lambda$onActivityConfigurationChanging$0(forTokenLocked);
    }

    public static void splashScreenAttachedLocked(IBinder iBinder) {
        ActivityRecord forTokenLocked = forTokenLocked(iBinder);
        if (forTokenLocked == null) {
            Slog.w("ActivityTaskManager", "splashScreenTransferredLocked cannot find activity");
        } else {
            forTokenLocked.onSplashScreenAttachComplete();
        }
    }

    public void completeResumeLocked() {
        boolean z = this.mVisibleRequested;
        setVisibility(true);
        if (!z) {
            this.mTaskSupervisor.mAppVisibilitiesChangedSinceLastPause = true;
        }
        this.idle = false;
        this.results = null;
        ArrayList<ReferrerIntent> arrayList = this.newIntents;
        if (arrayList != null && arrayList.size() > 0) {
            ArrayList<ReferrerIntent> arrayList2 = this.newIntents;
            this.mLastNewIntent = arrayList2.get(arrayList2.size() - 1);
        }
        this.newIntents = null;
        if (isActivityTypeHome()) {
            this.mTaskSupervisor.updateHomeProcess(this.task.getBottomMostActivity().app);
        }
        if (this.nowVisible) {
            this.mTaskSupervisor.stopWaitingForActivityVisible(this);
        }
        this.mTaskSupervisor.scheduleIdleTimeout(this);
        this.mTaskSupervisor.reportResumedActivityLocked(this);
        resumeKeyDispatchingLocked();
        Task rootTask = getRootTask();
        this.mTaskSupervisor.mNoAnimActivities.clear();
        this.returningOptions = null;
        if (canTurnScreenOn()) {
            this.mTaskSupervisor.wakeUp("turnScreenOnFlag");
        } else {
            rootTask.checkReadyForSleep();
        }
    }

    public void activityPaused(boolean z) {
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 1068803972, 12, (String) null, new Object[]{String.valueOf(this.token), Boolean.valueOf(z)});
        }
        TaskFragment taskFragment = getTaskFragment();
        if (taskFragment != null) {
            removePauseTimeout();
            ActivityRecord pausingActivity = taskFragment.getPausingActivity();
            if (pausingActivity == this) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 397382873, 0, (String) null, new Object[]{String.valueOf(this), z ? "(due to timeout)" : " (pause complete)"});
                }
                this.mAtmService.deferWindowLayout();
                try {
                    taskFragment.completePause(true, null);
                    return;
                } finally {
                    this.mAtmService.continueWindowLayout();
                }
            }
            EventLogTags.writeWmFailedToPause(this.mUserId, System.identityHashCode(this), this.shortComponentName, pausingActivity != null ? pausingActivity.shortComponentName : "(none)");
            if (isState(State.PAUSING)) {
                setState(State.PAUSED, "activityPausedLocked");
                if (this.finishing) {
                    if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -937498525, 0, (String) null, new Object[]{String.valueOf(this)});
                    }
                    completeFinishing("activityPausedLocked");
                }
            }
        }
        this.mDisplayContent.handleActivitySizeCompatModeIfNeeded(this);
        this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
    }

    public void schedulePauseTimeout() {
        this.pauseTime = SystemClock.uptimeMillis();
        this.mAtmService.f1161mH.postDelayed(this.mPauseTimeoutRunnable, 500L);
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -705939410, 0, (String) null, (Object[]) null);
        }
    }

    public final void removePauseTimeout() {
        this.mAtmService.f1161mH.removeCallbacks(this.mPauseTimeoutRunnable);
    }

    public final void removeDestroyTimeout() {
        this.mAtmService.f1161mH.removeCallbacks(this.mDestroyTimeoutRunnable);
    }

    public final void removeStopTimeout() {
        this.mAtmService.f1161mH.removeCallbacks(this.mStopTimeoutRunnable);
    }

    public void removeTimeouts() {
        this.mTaskSupervisor.removeIdleTimeoutForActivity(this);
        removePauseTimeout();
        removeStopTimeout();
        removeDestroyTimeout();
        finishLaunchTickingLocked();
    }

    public void stopIfPossible() {
        Task rootTask = getRootTask();
        if (isNoHistory() && !this.finishing) {
            if (!rootTask.shouldSleepActivities()) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -1136139407, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                if (finishIfPossible("stop-no-history", false) != 0) {
                    resumeKeyDispatchingLocked();
                    return;
                }
            } else if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, 485170982, 0, (String) null, new Object[]{String.valueOf(this)});
            }
        }
        if (attachedToProcess()) {
            resumeKeyDispatchingLocked();
            try {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 189628502, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                setState(State.STOPPING, "stopIfPossible");
                EventLogTags.writeWmStopActivity(this.mUserId, System.identityHashCode(this), this.shortComponentName);
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ActivityLifecycleItem) StopActivityItem.obtain(this.configChangeFlags));
                this.mAtmService.f1161mH.postDelayed(this.mStopTimeoutRunnable, 11000L);
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Exception thrown during pause", e);
                this.mAppStopped = true;
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 306524472, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                setState(State.STOPPED, "stopIfPossible");
                if (this.deferRelaunchUntilPaused) {
                    destroyImmediately("stop-except");
                }
            }
        }
    }

    public void activityStopped(Bundle bundle, PersistableBundle persistableBundle, CharSequence charSequence) {
        removeStopTimeout();
        State state = this.mState;
        boolean z = state == State.STOPPING;
        if (!z && state != State.RESTARTING_PROCESS) {
            Slog.i("ActivityTaskManager", "Activity reported stop, but no longer stopping: " + this + " " + this.mState);
            return;
        }
        if (persistableBundle != null) {
            this.mPersistentState = persistableBundle;
            this.mAtmService.notifyTaskPersisterLocked(this.task, false);
        }
        if (bundle != null) {
            setSavedState(bundle);
            this.launchCount = 0;
            updateTaskDescription(charSequence);
        }
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_STATES, -172326720, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(this.mIcicle)});
        }
        if (z) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1305791032, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            setState(State.STOPPED, "activityStopped");
        }
        this.mAppStopped = true;
        this.firstWindowDrawn = false;
        Task task = this.task;
        if (task.mLastRecentsAnimationTransaction != null) {
            task.clearLastRecentsAnimationTransaction(true);
        }
        this.mDisplayContent.mPinnedTaskController.onActivityHidden(this.mActivityComponent);
        this.mDisplayContent.mUnknownAppVisibilityController.appRemovedOrHidden(this);
        if (isClientVisible()) {
            setClientVisible(false);
        }
        destroySurfaces();
        removeStartingWindow();
        if (this.finishing) {
            abortAndClearOptionsAnimation();
        } else if (this.deferRelaunchUntilPaused) {
            destroyImmediately("stop-config");
            this.mRootWindowContainer.resumeFocusedTasksTopActivities();
        } else {
            this.mAtmService.updatePreviousProcess(this);
        }
        this.mTaskSupervisor.checkReadyForSleepLocked(true);
    }

    public void addToStopping(boolean z, boolean z2, String str) {
        if (!this.mTaskSupervisor.mStoppingActivities.contains(this)) {
            EventLogTags.writeWmAddToStopping(this.mUserId, System.identityHashCode(this), this.shortComponentName, str);
            this.mTaskSupervisor.mStoppingActivities.add(this);
        }
        Task rootTask = getRootTask();
        boolean z3 = true;
        if (this.mTaskSupervisor.mStoppingActivities.size() <= 3 && (!isRootOfTask() || rootTask.getChildCount() > 1)) {
            z3 = false;
        }
        if (z || z3) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 1126328412, 15, (String) null, new Object[]{Boolean.valueOf(z3), Boolean.valueOf(!z2)});
            }
            if (!z2) {
                this.mTaskSupervisor.scheduleIdle();
                return;
            } else {
                this.mTaskSupervisor.scheduleIdleTimeout(this);
                return;
            }
        }
        rootTask.checkReadyForSleep();
    }

    public void startLaunchTickingLocked() {
        if (!Build.IS_USER && this.launchTickTime == 0) {
            this.launchTickTime = SystemClock.uptimeMillis();
            continueLaunchTicking();
        }
    }

    public final boolean continueLaunchTicking() {
        Task rootTask;
        if (this.launchTickTime == 0 || (rootTask = getRootTask()) == null) {
            return false;
        }
        rootTask.removeLaunchTickMessages();
        this.mAtmService.f1161mH.postDelayed(this.mLaunchTickRunnable, 500L);
        return true;
    }

    public void removeLaunchTickRunnable() {
        this.mAtmService.f1161mH.removeCallbacks(this.mLaunchTickRunnable);
    }

    public void finishLaunchTickingLocked() {
        this.launchTickTime = 0L;
        Task rootTask = getRootTask();
        if (rootTask == null) {
            return;
        }
        rootTask.removeLaunchTickMessages();
    }

    public boolean mayFreezeScreenLocked() {
        return mayFreezeScreenLocked(this.app);
    }

    public final boolean mayFreezeScreenLocked(WindowProcessController windowProcessController) {
        return (!hasProcess() || windowProcessController.isCrashing() || windowProcessController.isNotResponding()) ? false : true;
    }

    public void startFreezingScreenLocked(int i) {
        startFreezingScreenLocked(this.app, i);
    }

    public void startFreezingScreenLocked(WindowProcessController windowProcessController, int i) {
        if (mayFreezeScreenLocked(windowProcessController)) {
            if (getParent() == null) {
                Slog.w(StartingSurfaceController.TAG, "Attempted to freeze screen with non-existing app token: " + this.token);
            } else if (((-536870913) & i) == 0 && okToDisplay()) {
                if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, 1836306327, 0, (String) null, new Object[]{String.valueOf(this.token)});
                }
            } else {
                startFreezingScreen();
            }
        }
    }

    public void startFreezingScreen() {
        startFreezingScreen(-1);
    }

    public void startFreezingScreen(int i) {
        if (this.mTransitionController.isShellTransitionsEnabled()) {
            return;
        }
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ORIENTATION, 1746778201, 252, (String) null, new Object[]{String.valueOf(this.token), Boolean.valueOf(isVisible()), Boolean.valueOf(this.mFreezingScreen), Boolean.valueOf(this.mVisibleRequested), String.valueOf(new RuntimeException().fillInStackTrace())});
        }
        if (this.mVisibleRequested) {
            boolean z = i != -1;
            if (!this.mFreezingScreen) {
                this.mFreezingScreen = true;
                this.mWmService.registerAppFreezeListener(this);
                WindowManagerService windowManagerService = this.mWmService;
                int i2 = windowManagerService.mAppsFreezingScreen + 1;
                windowManagerService.mAppsFreezingScreen = i2;
                if (i2 == 1) {
                    if (z) {
                        this.mDisplayContent.getDisplayRotation().cancelSeamlessRotation();
                    }
                    this.mWmService.startFreezingDisplay(0, 0, this.mDisplayContent, i);
                    this.mWmService.f1164mH.removeMessages(17);
                    this.mWmService.f1164mH.sendEmptyMessageDelayed(17, 2000L);
                }
            }
            if (z) {
                return;
            }
            int size = this.mChildren.size();
            for (int i3 = 0; i3 < size; i3++) {
                ((WindowState) this.mChildren.get(i3)).onStartFreezingScreen();
            }
        }
    }

    public boolean isFreezingScreen() {
        return this.mFreezingScreen;
    }

    @Override // com.android.server.p014wm.WindowManagerService.AppFreezeListener
    public void onAppFreezeTimeout() {
        Slog.w(StartingSurfaceController.TAG, "Force clearing freeze: " + this);
        stopFreezingScreen(true, true);
    }

    public void stopFreezingScreenLocked(boolean z) {
        if (z || this.frozenBeforeDestroy) {
            this.frozenBeforeDestroy = false;
            if (getParent() == null) {
                return;
            }
            if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, 466506262, 60, (String) null, new Object[]{String.valueOf(this.token), Boolean.valueOf(isVisible()), Boolean.valueOf(isFreezingScreen())});
            }
            stopFreezingScreen(true, z);
        }
    }

    public void stopFreezingScreen(boolean z, boolean z2) {
        WindowManagerService windowManagerService;
        if (this.mFreezingScreen) {
            if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, 539077569, 12, (String) null, new Object[]{String.valueOf(this), Boolean.valueOf(z2)});
            }
            int size = this.mChildren.size();
            boolean z3 = false;
            for (int i = 0; i < size; i++) {
                z3 |= ((WindowState) this.mChildren.get(i)).onStopFreezingScreen();
            }
            if (z2 || z3) {
                if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -251259736, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                this.mFreezingScreen = false;
                this.mWmService.unregisterAppFreezeListener(this);
                windowManagerService.mAppsFreezingScreen--;
                this.mWmService.mLastFinishedFreezeSource = this;
            }
            if (z) {
                if (z3) {
                    this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
                }
                this.mWmService.stopFreezingDisplayLocked();
            }
        }
    }

    public void onFirstWindowDrawn(WindowState windowState) {
        ActivityRecord activityRecord;
        this.firstWindowDrawn = true;
        this.mSplashScreenStyleSolidColor = true;
        removeDeadWindows();
        if (this.mStartingWindow != null) {
            if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 1677260366, 0, (String) null, new Object[]{String.valueOf(windowState.mToken)});
            }
            windowState.cancelAnimation();
        }
        Task task = this.task;
        Task task2 = task.mSharedStartingData != null ? task : null;
        if (task2 == null) {
            removeStartingWindow();
        } else if (task2.getActivity(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda31
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onFirstWindowDrawn$14;
                lambda$onFirstWindowDrawn$14 = ActivityRecord.lambda$onFirstWindowDrawn$14((ActivityRecord) obj);
                return lambda$onFirstWindowDrawn$14;
            }
        }) == null && (activityRecord = task2.topActivityContainsStartingWindow()) != null) {
            activityRecord.removeStartingWindow();
        }
        updateReportedVisibilityLocked();
    }

    public static /* synthetic */ boolean lambda$onFirstWindowDrawn$14(ActivityRecord activityRecord) {
        return activityRecord.isVisibleRequested() && !activityRecord.firstWindowDrawn;
    }

    public final boolean setTaskHasBeenVisible() {
        if (this.task.getHasBeenVisible()) {
            return false;
        }
        if (inTransition()) {
            this.task.setDeferTaskAppear(true);
        }
        this.task.setHasBeenVisible(true);
        return true;
    }

    public void onStartingWindowDrawn() {
        boolean z;
        if (this.task != null) {
            this.mSplashScreenStyleSolidColor = true;
            z = !setTaskHasBeenVisible();
        } else {
            z = false;
        }
        if (z || this.mStartingData == null || this.finishing || this.mLaunchedFromBubble || !this.mVisibleRequested || this.mDisplayContent.mAppTransition.isReady() || this.mDisplayContent.mAppTransition.isRunning() || !this.mDisplayContent.isNextTransitionForward()) {
            return;
        }
        this.mStartingData.mIsTransitionForward = true;
        if (this != this.mDisplayContent.getLastOrientationSource()) {
            this.mDisplayContent.updateOrientation();
        }
        this.mDisplayContent.executeAppTransition();
    }

    public final void onWindowsDrawn() {
        ActivityMetricsLogger.TransitionInfoSnapshot notifyWindowsDrawn = this.mTaskSupervisor.getActivityMetricsLogger().notifyWindowsDrawn(this);
        boolean z = notifyWindowsDrawn != null;
        int i = z ? notifyWindowsDrawn.windowsDrawnDelayMs : -1;
        int launchState = z ? notifyWindowsDrawn.getLaunchState() : 0;
        if (z || this == getDisplayArea().topRunningActivity()) {
            this.mTaskSupervisor.reportActivityLaunched(false, this, i, launchState);
        }
        finishLaunchTickingLocked();
        if (this.task != null) {
            setTaskHasBeenVisible();
        }
        this.mLaunchRootTask = null;
    }

    public void onWindowsVisible() {
        this.mTaskSupervisor.stopWaitingForActivityVisible(this);
        if (this.nowVisible) {
            return;
        }
        this.nowVisible = true;
        this.lastVisibleTime = SystemClock.uptimeMillis();
        this.mAtmService.scheduleAppGcsLocked();
        this.mTaskSupervisor.scheduleProcessStoppingAndFinishingActivitiesIfNeeded();
        if (this.mImeInsetsFrozenUntilStartInput && getWindow(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onWindowsVisible$15;
                lambda$onWindowsVisible$15 = ActivityRecord.lambda$onWindowsVisible$15((WindowState) obj);
                return lambda$onWindowsVisible$15;
            }
        }) == null) {
            this.mImeInsetsFrozenUntilStartInput = false;
        }
    }

    public static /* synthetic */ boolean lambda$onWindowsVisible$15(WindowState windowState) {
        return WindowManager.LayoutParams.mayUseInputMethod(windowState.mAttrs.flags);
    }

    public void onWindowsGone() {
        this.nowVisible = false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void checkAppWindowsReadyToShow() {
        boolean z = this.allDrawn;
        if (z == this.mLastAllDrawn) {
            return;
        }
        this.mLastAllDrawn = z;
        if (z) {
            if (this.mFreezingScreen) {
                showAllWindowsLocked();
                stopFreezingScreen(false, true);
                if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                    ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ORIENTATION, 806891543, 20, (String) null, new Object[]{String.valueOf(this), Long.valueOf(this.mNumInterestingWindows), Long.valueOf(this.mNumDrawnWindows)});
                }
                setAppLayoutChanges(4, "checkAppWindowsReadyToShow: freezingScreen");
                return;
            }
            setAppLayoutChanges(8, "checkAppWindowsReadyToShow");
            if (getDisplayContent().mOpeningApps.contains(this) || !canShowWindows()) {
                return;
            }
            showAllWindowsLocked();
        }
    }

    public void showAllWindowsLocked() {
        forAllWindows(new Consumer() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WindowState) obj).performShowLocked();
            }
        }, false);
    }

    public void updateReportedVisibilityLocked() {
        int size = this.mChildren.size();
        this.mReportedVisibilityResults.reset();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            ((WindowState) this.mChildren.get(i)).updateReportedVisibility(this.mReportedVisibilityResults);
        }
        WindowState.UpdateReportedVisibilityResults updateReportedVisibilityResults = this.mReportedVisibilityResults;
        int i2 = updateReportedVisibilityResults.numInteresting;
        int i3 = updateReportedVisibilityResults.numVisible;
        int i4 = updateReportedVisibilityResults.numDrawn;
        boolean z2 = updateReportedVisibilityResults.nowGone;
        boolean z3 = i2 > 0 && i4 >= i2;
        if (i2 > 0 && i3 >= i2 && isVisible()) {
            z = true;
        }
        if (!z2) {
            if (!z3) {
                z3 = this.mReportedDrawn;
            }
            if (!z) {
                z = this.reportedVisible;
            }
        }
        if (z3 != this.mReportedDrawn) {
            if (z3) {
                onWindowsDrawn();
            }
            this.mReportedDrawn = z3;
        }
        if (z != this.reportedVisible) {
            this.reportedVisible = z;
            if (z) {
                onWindowsVisible();
            } else {
                onWindowsGone();
            }
        }
    }

    public boolean isReportedDrawn() {
        return this.mReportedDrawn;
    }

    @Override // com.android.server.p014wm.WindowToken
    public void setClientVisible(boolean z) {
        if (z || !this.mDeferHidingClient) {
            super.setClientVisible(z);
        }
    }

    public boolean updateDrawnWindowStates(WindowState windowState) {
        windowState.setDrawnStateEvaluated(true);
        if (!this.allDrawn || this.mFreezingScreen) {
            long j = this.mLastTransactionSequence;
            int i = this.mWmService.mTransactionSequence;
            if (j != i) {
                this.mLastTransactionSequence = i;
                this.mNumDrawnWindows = 0;
                this.mNumInterestingWindows = findMainWindow(false) != null ? 1 : 0;
            }
            WindowStateAnimator windowStateAnimator = windowState.mWinAnimator;
            if (!this.allDrawn && windowState.mightAffectAllDrawn()) {
                if (ProtoLogGroup.WM_DEBUG_ORIENTATION.isLogToLogcat()) {
                    boolean isAnimating = isAnimating(3, 1);
                    Slog.v("ActivityTaskManager", "Eval win " + windowState + ": isDrawn=" + windowState.isDrawn() + ", isAnimationSet=" + isAnimating);
                    if (!windowState.isDrawn()) {
                        Slog.v("ActivityTaskManager", "Not displayed: s=" + windowStateAnimator.mSurfaceController + " pv=" + windowState.isVisibleByPolicy() + " mDrawState=" + windowStateAnimator.drawStateToString() + " ph=" + windowState.isParentWindowHidden() + " th=" + this.mVisibleRequested + " a=" + isAnimating);
                    }
                }
                if (windowState != this.mStartingWindow) {
                    if (windowState.isInteresting()) {
                        if (findMainWindow(false) != windowState) {
                            this.mNumInterestingWindows++;
                        }
                        if (windowState.isDrawn()) {
                            this.mNumDrawnWindows++;
                            if (ProtoLogGroup.WM_DEBUG_ORIENTATION.isLogToLogcat()) {
                                Slog.v("ActivityTaskManager", "tokenMayBeDrawn: " + this + " w=" + windowState + " numInteresting=" + this.mNumInterestingWindows + " freezingScreen=" + this.mFreezingScreen + " mAppFreezing=" + windowState.mAppFreezing);
                                return true;
                            }
                            return true;
                        }
                    }
                } else if (this.mStartingData != null && windowState.isDrawn()) {
                    this.mStartingData.mIsDisplayed = true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean inputDispatchingTimedOut(TimeoutRecord timeoutRecord, int i) {
        ActivityRecord waitingHistoryRecordLocked;
        WindowProcessController windowProcessController;
        boolean z;
        try {
            Trace.traceBegin(64L, "ActivityRecord#inputDispatchingTimedOut()");
            timeoutRecord.mLatencyTracker.waitingOnGlobalLockStarted();
            synchronized (this.mAtmService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                timeoutRecord.mLatencyTracker.waitingOnGlobalLockEnded();
                waitingHistoryRecordLocked = getWaitingHistoryRecordLocked();
                windowProcessController = this.app;
                z = hasProcess() && (this.app.getPid() == i || i == -1);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (z) {
                return this.mAtmService.mAmInternal.inputDispatchingTimedOut(windowProcessController.mOwner, waitingHistoryRecordLocked.shortComponentName, waitingHistoryRecordLocked.info.applicationInfo, this.shortComponentName, this.app, false, timeoutRecord);
            }
            return this.mAtmService.mAmInternal.inputDispatchingTimedOut(i, false, timeoutRecord) <= 0;
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public final ActivityRecord getWaitingHistoryRecordLocked() {
        Task topDisplayFocusedRootTask;
        if (!this.mAppStopped || (topDisplayFocusedRootTask = this.mRootWindowContainer.getTopDisplayFocusedRootTask()) == null) {
            return this;
        }
        ActivityRecord topResumedActivity = topDisplayFocusedRootTask.getTopResumedActivity();
        if (topResumedActivity == null) {
            topResumedActivity = topDisplayFocusedRootTask.getTopPausingActivity();
        }
        return topResumedActivity != null ? topResumedActivity : this;
    }

    public boolean canBeTopRunning() {
        return !this.finishing && showToCurrentUser();
    }

    public boolean isInterestingToUserLocked() {
        State state;
        return this.mVisibleRequested || this.nowVisible || (state = this.mState) == State.PAUSING || state == State.RESUMED;
    }

    public static int getTaskForActivityLocked(IBinder iBinder, boolean z) {
        ActivityRecord forTokenLocked = forTokenLocked(iBinder);
        if (forTokenLocked == null || forTokenLocked.getParent() == null) {
            return -1;
        }
        return getTaskForActivityLocked(forTokenLocked, z);
    }

    public static int getTaskForActivityLocked(ActivityRecord activityRecord, boolean z) {
        Task task = activityRecord.task;
        if (!z || activityRecord.compareTo((WindowContainer) task.getRootActivity(false, true)) <= 0) {
            return task.mTaskId;
        }
        return -1;
    }

    public static ActivityRecord isInRootTaskLocked(IBinder iBinder) {
        ActivityRecord forTokenLocked = forTokenLocked(iBinder);
        if (forTokenLocked != null) {
            return forTokenLocked.getRootTask().isInTask(forTokenLocked);
        }
        return null;
    }

    public static Task getRootTask(IBinder iBinder) {
        ActivityRecord isInRootTaskLocked = isInRootTaskLocked(iBinder);
        if (isInRootTaskLocked != null) {
            return isInRootTaskLocked.getRootTask();
        }
        return null;
    }

    public static ActivityRecord isInAnyTask(IBinder iBinder) {
        ActivityRecord forTokenLocked = forTokenLocked(iBinder);
        if (forTokenLocked == null || !forTokenLocked.isAttached()) {
            return null;
        }
        return forTokenLocked;
    }

    public int getDisplayId() {
        DisplayContent displayContent;
        Task task = this.task;
        if (task == null || (displayContent = task.mDisplayContent) == null) {
            return -1;
        }
        return displayContent.mDisplayId;
    }

    public final boolean isDestroyable() {
        return (this.finishing || !hasProcess() || isState(State.RESUMED) || getRootTask() == null || this == getTaskFragment().getPausingActivity() || !this.mHaveState || !this.mAppStopped || this.mVisibleRequested) ? false : true;
    }

    public static String createImageFilename(long j, int i) {
        return String.valueOf(i) + "_activity_icon_" + j + ".png";
    }

    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
        Bitmap icon;
        if (taskDescription.getIconFilename() == null && (icon = taskDescription.getIcon()) != null) {
            String absolutePath = new File(TaskPersister.getUserImagesDir(this.task.mUserId), createImageFilename(this.createTime, this.task.mTaskId)).getAbsolutePath();
            this.mAtmService.getRecentTasks().saveImage(icon, absolutePath);
            taskDescription.setIconFilename(absolutePath);
        }
        this.taskDescription = taskDescription;
        getTask().updateTaskDescription();
    }

    public void setLocusId(LocusId locusId) {
        if (Objects.equals(locusId, this.mLocusId)) {
            return;
        }
        this.mLocusId = locusId;
        if (getTask() != null) {
            getTask().dispatchTaskInfoChangedIfNeeded(false);
        }
    }

    public LocusId getLocusId() {
        return this.mLocusId;
    }

    public void reportScreenCaptured() {
        RemoteCallbackList<IScreenCaptureObserver> remoteCallbackList = this.mCaptureCallbacks;
        if (remoteCallbackList != null) {
            int beginBroadcast = remoteCallbackList.beginBroadcast();
            for (int i = 0; i < beginBroadcast; i++) {
                try {
                    this.mCaptureCallbacks.getBroadcastItem(i).onScreenCaptured();
                } catch (RemoteException unused) {
                }
            }
            this.mCaptureCallbacks.finishBroadcast();
        }
    }

    public void registerCaptureObserver(IScreenCaptureObserver iScreenCaptureObserver) {
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mCaptureCallbacks == null) {
                    this.mCaptureCallbacks = new RemoteCallbackList<>();
                }
                this.mCaptureCallbacks.register(iScreenCaptureObserver);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void unregisterCaptureObserver(IScreenCaptureObserver iScreenCaptureObserver) {
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                RemoteCallbackList<IScreenCaptureObserver> remoteCallbackList = this.mCaptureCallbacks;
                if (remoteCallbackList != null) {
                    remoteCallbackList.unregister(iScreenCaptureObserver);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean isRegisteredForScreenCaptureCallback() {
        RemoteCallbackList<IScreenCaptureObserver> remoteCallbackList = this.mCaptureCallbacks;
        return remoteCallbackList != null && remoteCallbackList.getRegisteredCallbackCount() > 0;
    }

    public void setVoiceSessionLocked(IVoiceInteractionSession iVoiceInteractionSession) {
        this.voiceSession = iVoiceInteractionSession;
        this.pendingVoiceInteractionStart = false;
    }

    public void clearVoiceSessionLocked() {
        this.voiceSession = null;
        this.pendingVoiceInteractionStart = false;
    }

    public void showStartingWindow(boolean z) {
        showStartingWindow(null, false, z, false, null);
    }

    public final ActivityRecord searchCandidateLaunchingActivity() {
        ActivityRecord activityBelow = this.task.getActivityBelow(this);
        if (activityBelow == null) {
            activityBelow = this.task.getParent().getActivityBelow(this);
        }
        if (activityBelow == null || activityBelow.isActivityTypeHome()) {
            return null;
        }
        WindowProcessController windowProcessController = this.app;
        if (windowProcessController == null) {
            windowProcessController = (WindowProcessController) this.mAtmService.mProcessNames.get(this.processName, this.info.applicationInfo.uid);
        }
        WindowProcessController windowProcessController2 = activityBelow.app;
        if (windowProcessController2 == null) {
            windowProcessController2 = (WindowProcessController) this.mAtmService.mProcessNames.get(activityBelow.processName, activityBelow.info.applicationInfo.uid);
        }
        if (windowProcessController2 == windowProcessController || this.mActivityComponent.getPackageName().equals(activityBelow.mActivityComponent.getPackageName())) {
            return activityBelow;
        }
        return null;
    }

    public final boolean isIconStylePreferred(int i) {
        AttributeCache.Entry entry;
        return i != 0 && (entry = AttributeCache.instance().get(this.packageName, i, R.styleable.Window, this.mWmService.mCurrentUserId)) != null && entry.array.hasValue(61) && entry.array.getInt(61, 0) == 1;
    }

    public final boolean shouldUseSolidColorSplashScreen(ActivityRecord activityRecord, boolean z, ActivityOptions activityOptions, int i) {
        int i2;
        if (activityRecord != null || z || this.task.getActivityAbove(this) == null) {
            if (activityOptions != null) {
                int splashScreenStyle = activityOptions.getSplashScreenStyle();
                if (splashScreenStyle == 0) {
                    return true;
                }
                if (splashScreenStyle == 1 || isIconStylePreferred(i) || (i2 = this.mLaunchSourceType) == 2 || this.launchedFromUid == 2000) {
                    return false;
                }
                if (i2 == 3) {
                    return true;
                }
            } else if (isIconStylePreferred(i)) {
                return false;
            }
            if (activityRecord == null) {
                activityRecord = searchCandidateLaunchingActivity();
            }
            if (activityRecord == null || activityRecord.isActivityTypeHome()) {
                if (z) {
                    int i3 = this.mLaunchSourceType;
                    return (i3 == 1 || i3 == 2 || this.launchedFromUid == 2000) ? false : true;
                }
                return true;
            }
            return activityRecord.mSplashScreenStyleSolidColor;
        }
        return true;
    }

    public final int getSplashscreenTheme(ActivityOptions activityOptions) {
        String splashScreenThemeResName = activityOptions != null ? activityOptions.getSplashScreenThemeResName() : null;
        if (splashScreenThemeResName == null || splashScreenThemeResName.isEmpty()) {
            try {
                splashScreenThemeResName = this.mAtmService.getPackageManager().getSplashScreenTheme(this.packageName, this.mUserId);
            } catch (RemoteException unused) {
            }
        }
        if (splashScreenThemeResName == null || splashScreenThemeResName.isEmpty()) {
            return 0;
        }
        try {
            return this.mAtmService.mContext.createPackageContext(this.packageName, 0).getResources().getIdentifier(splashScreenThemeResName, null, null);
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException unused2) {
            return 0;
        }
    }

    public void showStartingWindow(ActivityRecord activityRecord, boolean z, boolean z2, boolean z3, ActivityRecord activityRecord2) {
        showStartingWindow(activityRecord, z, z2, isProcessRunning(), z3, activityRecord2, null);
    }

    public void showStartingWindow(ActivityRecord activityRecord, boolean z, boolean z2, boolean z3, boolean z4, ActivityRecord activityRecord2, ActivityOptions activityOptions) {
        if (this.mTaskOverlay) {
            return;
        }
        ActivityOptions activityOptions2 = activityOptions != null ? activityOptions : this.mPendingOptions;
        if (activityOptions2 == null || activityOptions2.getAnimationType() != 5) {
            int evaluateStartingWindowTheme = evaluateStartingWindowTheme(activityRecord, this.packageName, this.theme, z4 ? getSplashscreenTheme(activityOptions2) : 0);
            this.mSplashScreenStyleSolidColor = shouldUseSolidColorSplashScreen(activityRecord2, z4, activityOptions2, evaluateStartingWindowTheme);
            boolean z5 = true;
            boolean z6 = this.mState.ordinal() >= State.STARTED.ordinal() && this.mState.ordinal() <= State.STOPPED.ordinal();
            boolean z7 = (z || z6 || this.task.getActivity(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$showStartingWindow$17;
                    lambda$showStartingWindow$17 = ActivityRecord.this.lambda$showStartingWindow$17((ActivityRecord) obj);
                    return lambda$showStartingWindow$17;
                }
            }) != null) ? false : true;
            String str = this.packageName;
            if (!z && !z7) {
                z5 = false;
            }
            addStartingWindow(str, evaluateStartingWindowTheme, activityRecord, z5, z2, z3, allowTaskSnapshot(), z6, this.mSplashScreenStyleSolidColor, this.allDrawn);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$showStartingWindow$17(ActivityRecord activityRecord) {
        return (activityRecord.finishing || activityRecord == this) ? false : true;
    }

    public void cancelInitializing() {
        if (this.mStartingData != null) {
            removeStartingWindowAnimation(false);
        }
        if (this.mDisplayContent.mUnknownAppVisibilityController.allResolved()) {
            return;
        }
        this.mDisplayContent.mUnknownAppVisibilityController.appRemovedOrHidden(this);
    }

    public void postWindowRemoveStartingWindowCleanup(WindowState windowState) {
        if (this.mStartingWindow == windowState) {
            if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 1853793312, 0, (String) null, new Object[]{String.valueOf(windowState)});
            }
            removeStartingWindow();
        } else if (this.mChildren.size() == 0) {
            if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 1671994402, 0, (String) null, (Object[]) null);
            }
            this.mStartingData = null;
            if (this.mVisibleSetFromTransferredStartingWindow) {
                setVisible(false);
            }
        } else if (this.mChildren.size() != 1 || this.mStartingSurface == null || isRelaunching()) {
        } else {
            if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, -1715268616, 0, (String) null, new Object[]{String.valueOf(windowState)});
            }
            removeStartingWindow();
        }
    }

    public void removeDeadWindows() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            WindowState windowState = (WindowState) this.mChildren.get(size);
            if (windowState.mAppDied) {
                if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, 1515161239, 0, (String) null, new Object[]{String.valueOf(windowState)});
                }
                windowState.mDestroying = true;
                windowState.removeIfPossible();
            }
        }
    }

    public void setWillReplaceWindows(boolean z) {
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -1878839956, 0, (String) null, new Object[]{String.valueOf(this)});
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            ((WindowState) this.mChildren.get(size)).setWillReplaceWindow(z);
        }
    }

    public void setWillReplaceChildWindows() {
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -1471946192, 0, (String) null, new Object[]{String.valueOf(this)});
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            ((WindowState) this.mChildren.get(size)).setWillReplaceChildWindows();
        }
    }

    public void clearWillReplaceWindows() {
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -1698815688, 0, (String) null, new Object[]{String.valueOf(this)});
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            ((WindowState) this.mChildren.get(size)).clearWillReplaceWindow();
        }
    }

    public void requestUpdateWallpaperIfNeeded() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            ((WindowState) this.mChildren.get(size)).requestUpdateWallpaperIfNeeded();
        }
    }

    public WindowState getTopFullscreenOpaqueWindow() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            WindowState windowState = (WindowState) this.mChildren.get(size);
            if (windowState != null && windowState.mAttrs.isFullscreen() && !windowState.isFullyTransparent()) {
                return windowState;
            }
        }
        return null;
    }

    public WindowState findMainWindow() {
        return findMainWindow(true);
    }

    public WindowState findMainWindow(boolean z) {
        WindowState windowState = null;
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            WindowState windowState2 = (WindowState) this.mChildren.get(size);
            int i = windowState2.mAttrs.type;
            if (i == 1 || (z && i == 3)) {
                if (!windowState2.mAnimatingExit) {
                    return windowState2;
                }
                windowState = windowState2;
            }
        }
        return windowState;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean needsZBoost() {
        return this.mNeedsZBoost || super.needsZBoost();
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl getAnimationLeashParent() {
        if (inPinnedWindowingMode()) {
            return getRootTask().getSurfaceControl();
        }
        return super.getAnimationLeashParent();
    }

    @VisibleForTesting
    public boolean shouldAnimate() {
        Task task = this.task;
        return task == null || task.shouldAnimate();
    }

    public final SurfaceControl createAnimationBoundsLayer(SurfaceControl.Transaction transaction) {
        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, 1720229827, 0, (String) null, (Object[]) null);
        }
        SurfaceControl.Builder parent = makeAnimationLeash().setParent(getAnimationLeashParent());
        SurfaceControl.Builder callsite = parent.setName(getSurfaceControl() + " - animation-bounds").setCallsite("ActivityRecord.createAnimationBoundsLayer");
        if (this.mNeedsLetterboxedAnimation) {
            callsite.setEffectLayer();
        }
        SurfaceControl build = callsite.build();
        transaction.show(build);
        return build;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public boolean shouldDeferAnimationFinish(Runnable runnable) {
        AnimatingActivityRegistry animatingActivityRegistry = this.mAnimatingActivityRegistry;
        return animatingActivityRegistry != null && animatingActivityRegistry.notifyAboutToFinish(this, runnable);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isWaitingForTransitionStart() {
        DisplayContent displayContent = getDisplayContent();
        return displayContent != null && displayContent.mAppTransition.isTransitionSet() && (displayContent.mOpeningApps.contains(this) || displayContent.mClosingApps.contains(this) || displayContent.mChangingContainers.contains(this));
    }

    public boolean isTransitionForward() {
        StartingData startingData = this.mStartingData;
        return (startingData != null && startingData.mIsTransitionForward) || this.mDisplayContent.isNextTransitionForward();
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public void onLeashAnimationStarting(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        AnimatingActivityRegistry animatingActivityRegistry = this.mAnimatingActivityRegistry;
        if (animatingActivityRegistry != null) {
            animatingActivityRegistry.notifyStarting(this);
        }
        if (this.mNeedsLetterboxedAnimation) {
            updateLetterboxSurface(findMainWindow(), transaction);
            this.mNeedsAnimationBoundsLayer = true;
        }
        if (this.mNeedsAnimationBoundsLayer) {
            ((WindowContainer) this).mTmpRect.setEmpty();
            if (getDisplayContent().mAppTransitionController.isTransitWithinTask(getTransit(), this.task)) {
                this.task.getBounds(((WindowContainer) this).mTmpRect);
            } else {
                Task rootTask = getRootTask();
                if (rootTask == null) {
                    return;
                }
                rootTask.getBounds(((WindowContainer) this).mTmpRect);
            }
            this.mAnimationBoundsLayer = createAnimationBoundsLayer(transaction);
            transaction.setLayer(surfaceControl, 0);
            transaction.setLayer(this.mAnimationBoundsLayer, getLastLayer());
            if (this.mNeedsLetterboxedAnimation) {
                int roundedCornersRadius = this.mLetterboxUiController.getRoundedCornersRadius(findMainWindow());
                Rect rect = new Rect();
                getLetterboxInnerBounds(rect);
                transaction.setCornerRadius(this.mAnimationBoundsLayer, roundedCornersRadius).setCrop(this.mAnimationBoundsLayer, rect);
            }
            transaction.reparent(surfaceControl, this.mAnimationBoundsLayer);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void prepareSurfaces() {
        boolean z = isVisible() || isAnimating(2, 9);
        if (this.mSurfaceControl != null) {
            if (z && !this.mLastSurfaceShowing) {
                getSyncTransaction().show(this.mSurfaceControl);
            } else if (!z && this.mLastSurfaceShowing) {
                getSyncTransaction().hide(this.mSurfaceControl);
            }
            if (z && this.mSyncState == 0) {
                this.mActivityRecordInputSink.applyChangesToSurfaceIfChanged(getPendingTransaction());
            }
        }
        WindowContainerThumbnail windowContainerThumbnail = this.mThumbnail;
        if (windowContainerThumbnail != null) {
            windowContainerThumbnail.setShowing(getPendingTransaction(), z);
        }
        this.mLastSurfaceShowing = z;
        super.prepareSurfaces();
    }

    public boolean isSurfaceShowing() {
        return this.mLastSurfaceShowing;
    }

    public void attachThumbnailAnimation() {
        if (isAnimating(2, 1)) {
            HardwareBuffer appTransitionThumbnailHeader = getDisplayContent().mAppTransition.getAppTransitionThumbnailHeader(this.task);
            if (appTransitionThumbnailHeader == null) {
                if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, 1528528509, 0, (String) null, new Object[]{String.valueOf(this.task)});
                    return;
                }
                return;
            }
            clearThumbnail();
            SurfaceControl.Transaction pendingTransaction = getAnimatingContainer().getPendingTransaction();
            WindowContainerThumbnail windowContainerThumbnail = new WindowContainerThumbnail(pendingTransaction, getAnimatingContainer(), appTransitionThumbnailHeader);
            this.mThumbnail = windowContainerThumbnail;
            windowContainerThumbnail.startAnimation(pendingTransaction, loadThumbnailAnimation(appTransitionThumbnailHeader));
        }
    }

    public void attachCrossProfileAppsThumbnailAnimation() {
        Drawable drawable;
        if (isAnimating(2, 1)) {
            clearThumbnail();
            WindowState findMainWindow = findMainWindow();
            if (findMainWindow == null) {
                return;
            }
            Rect relativeFrame = findMainWindow.getRelativeFrame();
            if (this.task.mUserId == this.mWmService.mCurrentUserId) {
                drawable = this.mAtmService.getUiContext().getDrawable(17302312);
            } else {
                drawable = this.mEnterpriseThumbnailDrawable;
            }
            HardwareBuffer createCrossProfileAppsThumbnail = getDisplayContent().mAppTransition.createCrossProfileAppsThumbnail(drawable, relativeFrame);
            if (createCrossProfileAppsThumbnail == null) {
                return;
            }
            SurfaceControl.Transaction pendingTransaction = getPendingTransaction();
            this.mThumbnail = new WindowContainerThumbnail(pendingTransaction, getTask(), createCrossProfileAppsThumbnail);
            this.mThumbnail.startAnimation(pendingTransaction, getDisplayContent().mAppTransition.createCrossProfileAppsThumbnailAnimationLocked(relativeFrame), new Point(relativeFrame.left, relativeFrame.top));
        }
    }

    public final Animation loadThumbnailAnimation(HardwareBuffer hardwareBuffer) {
        Rect rect;
        Rect rect2;
        DisplayInfo displayInfo = this.mDisplayContent.getDisplayInfo();
        WindowState findMainWindow = findMainWindow();
        if (findMainWindow != null) {
            Rect rect3 = findMainWindow.getInsetsStateWithVisibilityOverride().calculateInsets(findMainWindow.getFrame(), WindowInsets.Type.systemBars(), false).toRect();
            Rect rect4 = new Rect(findMainWindow.getFrame());
            rect4.inset(rect3);
            rect = rect3;
            rect2 = rect4;
        } else {
            rect = null;
            rect2 = new Rect(0, 0, displayInfo.appWidth, displayInfo.appHeight);
        }
        return getDisplayContent().mAppTransition.createThumbnailAspectScaleAnimationLocked(rect2, rect, hardwareBuffer, this.task, this.mDisplayContent.getConfiguration().orientation);
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.SurfaceAnimator.Animatable
    public void onAnimationLeashLost(SurfaceControl.Transaction transaction) {
        super.onAnimationLeashLost(transaction);
        SurfaceControl surfaceControl = this.mAnimationBoundsLayer;
        if (surfaceControl != null) {
            transaction.remove(surfaceControl);
            this.mAnimationBoundsLayer = null;
        }
        this.mNeedsAnimationBoundsLayer = false;
        if (this.mNeedsLetterboxedAnimation) {
            this.mNeedsLetterboxedAnimation = false;
            updateLetterboxSurface(findMainWindow(), transaction);
        }
        AnimatingActivityRegistry animatingActivityRegistry = this.mAnimatingActivityRegistry;
        if (animatingActivityRegistry != null) {
            animatingActivityRegistry.notifyFinished(this);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void onAnimationFinished(int i, AnimationAdapter animationAdapter) {
        WindowState window;
        super.onAnimationFinished(i, animationAdapter);
        Trace.traceBegin(32L, "AR#onAnimationFinished");
        this.mTransit = -1;
        this.mTransitFlags = 0;
        setAppLayoutChanges(12, "ActivityRecord");
        clearThumbnail();
        setClientVisible(isVisible() || this.mVisibleRequested);
        getDisplayContent().computeImeTargetIfNeeded(this);
        if (ProtoLogCache.WM_DEBUG_ANIM_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ANIM, 2010476671, 1020, (String) null, new Object[]{String.valueOf(this), Boolean.valueOf(this.reportedVisible), Boolean.valueOf(okToDisplay()), Boolean.valueOf(okToAnimate()), Boolean.valueOf(isStartingWindowDisplayed())});
        }
        WindowContainerThumbnail windowContainerThumbnail = this.mThumbnail;
        if (windowContainerThumbnail != null) {
            windowContainerThumbnail.destroy();
            this.mThumbnail = null;
        }
        new ArrayList(this.mChildren).forEach(new Consumer() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WindowState) obj).onExitAnimationDone();
            }
        });
        Task task = this.task;
        if (task != null && this.startingMoved && (window = task.getWindow(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onAnimationFinished$18;
                lambda$onAnimationFinished$18 = ActivityRecord.lambda$onAnimationFinished$18((WindowState) obj);
                return lambda$onAnimationFinished$18;
            }
        })) != null && window.mAnimatingExit && !window.isSelfAnimating(0, 16)) {
            window.onExitAnimationDone();
        }
        getDisplayContent().mAppTransition.notifyAppTransitionFinishedLocked(this.token);
        scheduleAnimation();
        this.mTaskSupervisor.scheduleProcessStoppingAndFinishingActivitiesIfNeeded();
        Trace.traceEnd(32L);
    }

    public static /* synthetic */ boolean lambda$onAnimationFinished$18(WindowState windowState) {
        return windowState.mAttrs.type == 3;
    }

    public void clearAnimatingFlags() {
        boolean z = false;
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            z |= ((WindowState) this.mChildren.get(size)).clearAnimatingFlags();
        }
        if (z) {
            requestUpdateWallpaperIfNeeded();
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void cancelAnimation() {
        super.cancelAnimation();
        clearThumbnail();
    }

    public final void clearThumbnail() {
        WindowContainerThumbnail windowContainerThumbnail = this.mThumbnail;
        if (windowContainerThumbnail == null) {
            return;
        }
        windowContainerThumbnail.destroy();
        this.mThumbnail = null;
    }

    public int getTransit() {
        return this.mTransit;
    }

    public void registerRemoteAnimations(RemoteAnimationDefinition remoteAnimationDefinition) {
        this.mRemoteAnimationDefinition = remoteAnimationDefinition;
        if (remoteAnimationDefinition != null) {
            remoteAnimationDefinition.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda0
                @Override // android.os.IBinder.DeathRecipient
                public final void binderDied() {
                    ActivityRecord.this.unregisterRemoteAnimations();
                }
            });
        }
    }

    public void unregisterRemoteAnimations() {
        this.mRemoteAnimationDefinition = null;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public RemoteAnimationDefinition getRemoteAnimationDefinition() {
        return this.mRemoteAnimationDefinition;
    }

    @Override // com.android.server.p014wm.WindowToken
    public void applyFixedRotationTransform(DisplayInfo displayInfo, DisplayFrames displayFrames, Configuration configuration) {
        super.applyFixedRotationTransform(displayInfo, displayFrames, configuration);
        ensureActivityConfiguration(0, false);
    }

    @Override // com.android.server.p014wm.WindowContainer
    @Configuration.Orientation
    public int getRequestedConfigurationOrientation(boolean z) {
        ActivityRecord activity;
        if (this.mLetterboxUiController.hasInheritedOrientation()) {
            RootDisplayArea rootDisplayArea = getRootDisplayArea();
            if (z && rootDisplayArea != null && rootDisplayArea.isOrientationDifferentFromDisplay()) {
                return ActivityInfo.reverseOrientation(this.mLetterboxUiController.getInheritedOrientation());
            }
            return this.mLetterboxUiController.getInheritedOrientation();
        } else if (this.task != null && getOverrideOrientation() == 3 && (activity = this.task.getActivity(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda25
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean canDefineOrientationForActivitiesAbove;
                canDefineOrientationForActivitiesAbove = ((ActivityRecord) obj).canDefineOrientationForActivitiesAbove();
                return canDefineOrientationForActivitiesAbove;
            }
        }, this, false, true)) != null) {
            return activity.getRequestedConfigurationOrientation(z);
        } else {
            return super.getRequestedConfigurationOrientation(z);
        }
    }

    public boolean canDefineOrientationForActivitiesAbove() {
        int overrideOrientation;
        return (this.finishing || (overrideOrientation = getOverrideOrientation()) == -2 || overrideOrientation == 3) ? false : true;
    }

    @Override // com.android.server.p014wm.WindowToken
    public void onCancelFixedRotationTransform(int i) {
        if (this != this.mDisplayContent.getLastOrientationSource()) {
            return;
        }
        int requestedConfigurationOrientation = getRequestedConfigurationOrientation();
        if (requestedConfigurationOrientation == 0 || requestedConfigurationOrientation == this.mDisplayContent.getConfiguration().orientation) {
            this.mDisplayContent.mPinnedTaskController.onCancelFixedRotationTransform();
            startFreezingScreen(i);
            ensureActivityConfiguration(0, false);
            if (this.mTransitionController.isCollecting(this)) {
                this.task.resetSurfaceControlTransforms();
            }
        }
    }

    public void setRequestedOrientation(int i) {
        if (this.mLetterboxUiController.shouldIgnoreRequestedOrientation(i)) {
            return;
        }
        setOrientation(i, this);
        if (!getMergedOverrideConfiguration().equals(this.mLastReportedConfiguration.getMergedConfiguration())) {
            ensureActivityConfiguration(0, false, false, true);
        }
        this.mAtmService.getTaskChangeNotificationController().notifyActivityRequestedOrientationChanged(this.task.mTaskId, i);
    }

    public void reportDescendantOrientationChangeIfNeeded() {
        if (onDescendantOrientationChanged(this)) {
            this.task.dispatchTaskInfoChangedIfNeeded(true);
        }
    }

    @VisibleForTesting
    public boolean shouldIgnoreOrientationRequests() {
        return this.mAppActivityEmbeddingSplitsEnabled && ActivityInfo.isFixedOrientationPortrait(getOverrideOrientation()) && !this.task.inMultiWindowMode() && getTask().getConfiguration().smallestScreenWidthDp >= 600;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public int getOrientation(int i) {
        if (shouldIgnoreOrientationRequests()) {
            return -2;
        }
        if (i == 3) {
            return getOverrideOrientation();
        }
        if (getDisplayContent().mClosingApps.contains(this) || !(isVisibleRequested() || getDisplayContent().mOpeningApps.contains(this))) {
            return -2;
        }
        return getOverrideOrientation();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public int getOverrideOrientation() {
        return this.mLetterboxUiController.overrideOrientationIfNeeded(super.getOverrideOrientation());
    }

    public int getRequestedOrientation() {
        return super.getOverrideOrientation();
    }

    public void setLastReportedGlobalConfiguration(Configuration configuration) {
        this.mLastReportedConfiguration.setGlobalConfiguration(configuration);
    }

    public void setLastReportedConfiguration(MergedConfiguration mergedConfiguration) {
        setLastReportedConfiguration(mergedConfiguration.getGlobalConfiguration(), mergedConfiguration.getOverrideConfiguration());
    }

    public final void setLastReportedConfiguration(Configuration configuration, Configuration configuration2) {
        this.mLastReportedConfiguration.setConfiguration(configuration, configuration2);
    }

    public CompatDisplayInsets getCompatDisplayInsets() {
        if (this.mLetterboxUiController.hasInheritedLetterboxBehavior()) {
            return this.mLetterboxUiController.getInheritedCompatDisplayInsets();
        }
        return this.mCompatDisplayInsets;
    }

    public boolean hasCompatDisplayInsetsWithoutInheritance() {
        return this.mCompatDisplayInsets != null;
    }

    public boolean inSizeCompatMode() {
        WindowContainer parent;
        if (this.mInSizeCompatModeForBounds) {
            return true;
        }
        return (getCompatDisplayInsets() == null || !shouldCreateCompatDisplayInsets() || isFixedRotationTransforming() || getConfiguration().windowConfiguration.getAppBounds() == null || (parent = getParent()) == null || parent.getConfiguration().densityDpi == getConfiguration().densityDpi) ? false : true;
    }

    public boolean shouldCreateCompatDisplayInsets() {
        int supportsSizeChanges = this.info.supportsSizeChanges();
        if (supportsSizeChanges != 1) {
            if (supportsSizeChanges == 2 || supportsSizeChanges == 3) {
                return false;
            }
            if (inMultiWindowMode() || getWindowConfiguration().hasWindowDecorCaption()) {
                Task task = this.task;
                ActivityRecord rootActivity = task != null ? task.getRootActivity() : null;
                if (rootActivity != null && rootActivity != this && !rootActivity.shouldCreateCompatDisplayInsets()) {
                    return false;
                }
            }
            return !isResizeable() && (this.info.isFixedOrientation() || hasFixedAspectRatio()) && isActivityTypeStandardOrUndefined();
        }
        return true;
    }

    @Override // com.android.server.p014wm.WindowToken
    public boolean hasSizeCompatBounds() {
        return this.mSizeCompatBounds != null;
    }

    public final void updateCompatDisplayInsets() {
        if (getCompatDisplayInsets() == null && shouldCreateCompatDisplayInsets()) {
            Configuration requestedOverrideConfiguration = getRequestedOverrideConfiguration();
            Configuration configuration = getConfiguration();
            requestedOverrideConfiguration.colorMode = configuration.colorMode;
            requestedOverrideConfiguration.densityDpi = configuration.densityDpi;
            requestedOverrideConfiguration.smallestScreenWidthDp = configuration.smallestScreenWidthDp;
            if (ActivityInfo.isFixedOrientation(getOverrideOrientation())) {
                requestedOverrideConfiguration.windowConfiguration.setRotation(configuration.windowConfiguration.getRotation());
            }
            this.mCompatDisplayInsets = new CompatDisplayInsets(this.mDisplayContent, this, this.mLetterboxBoundsForFixedOrientationAndAspectRatio);
        }
    }

    @VisibleForTesting
    public void clearSizeCompatMode() {
        float f = this.mSizeCompatScale;
        this.mInSizeCompatModeForBounds = false;
        this.mSizeCompatScale = 1.0f;
        this.mSizeCompatBounds = null;
        this.mCompatDisplayInsets = null;
        if (1.0f != f) {
            forAllWindows((Consumer<WindowState>) new ActivityRecord$$ExternalSyntheticLambda19(), false);
        }
        int activityType = getActivityType();
        Configuration requestedOverrideConfiguration = getRequestedOverrideConfiguration();
        requestedOverrideConfiguration.unset();
        requestedOverrideConfiguration.windowConfiguration.setActivityType(activityType);
        onRequestedOverrideConfigurationChanged(requestedOverrideConfiguration);
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public boolean matchParentBounds() {
        WindowContainer parent;
        Rect resolvedOverrideBounds = getResolvedOverrideBounds();
        return resolvedOverrideBounds.isEmpty() || (parent = getParent()) == null || parent.getBounds().equals(resolvedOverrideBounds);
    }

    @Override // com.android.server.p014wm.WindowToken
    public float getCompatScale() {
        return hasSizeCompatBounds() ? this.mSizeCompatScale : super.getCompatScale();
    }

    @Override // com.android.server.p014wm.WindowToken, com.android.server.p014wm.ConfigurationContainer
    public void resolveOverrideConfiguration(Configuration configuration) {
        Configuration requestedOverrideConfiguration = getRequestedOverrideConfiguration();
        int i = requestedOverrideConfiguration.assetsSeq;
        boolean z = false;
        if (i != 0 && configuration.assetsSeq > i) {
            requestedOverrideConfiguration.assetsSeq = 0;
        }
        super.resolveOverrideConfiguration(configuration);
        Configuration resolvedOverrideConfiguration = getResolvedOverrideConfiguration();
        applyLocaleOverrideIfNeeded(resolvedOverrideConfiguration);
        if (isFixedRotationTransforming()) {
            this.mTmpConfig.setTo(configuration);
            this.mTmpConfig.updateFrom(resolvedOverrideConfiguration);
            configuration = this.mTmpConfig;
        }
        this.mIsAspectRatioApplied = false;
        int windowingMode = configuration.windowConfiguration.getWindowingMode();
        boolean z2 = windowingMode == 6 || windowingMode == 1 || (windowingMode == 2 && resolvedOverrideConfiguration.windowConfiguration.getWindowingMode() == 1);
        if (z2) {
            resolveFixedOrientationConfiguration(configuration);
        }
        CompatDisplayInsets compatDisplayInsets = getCompatDisplayInsets();
        if (compatDisplayInsets != null) {
            resolveSizeCompatModeConfiguration(configuration, compatDisplayInsets);
        } else if (inMultiWindowMode() && !z2) {
            resolvedOverrideConfiguration.orientation = 0;
            if (!matchParentBounds()) {
                getTaskFragment().computeConfigResourceOverrides(resolvedOverrideConfiguration, configuration);
            }
        } else if (!isLetterboxedForFixedOrientationAndAspectRatio()) {
            resolveAspectRatioRestriction(configuration);
        }
        if (z2 || compatDisplayInsets != null || !inMultiWindowMode()) {
            updateResolvedBoundsPosition(configuration);
        }
        DisplayContent displayContent = this.mDisplayContent;
        if (displayContent != null && displayContent.getIgnoreOrientationRequest()) {
            z = true;
        }
        if (compatDisplayInsets == null && (this.mLetterboxBoundsForFixedOrientationAndAspectRatio != null || (z && this.mIsAspectRatioApplied))) {
            resolvedOverrideConfiguration.smallestScreenWidthDp = Math.min(resolvedOverrideConfiguration.screenWidthDp, resolvedOverrideConfiguration.screenHeightDp);
        }
        int i2 = this.mConfigurationSeq + 1;
        this.mConfigurationSeq = i2;
        this.mConfigurationSeq = Math.max(i2, 1);
        getResolvedOverrideConfiguration().seq = this.mConfigurationSeq;
        if (providesMaxBounds()) {
            this.mTmpBounds.set(resolvedOverrideConfiguration.windowConfiguration.getBounds());
            if (this.mTmpBounds.isEmpty()) {
                this.mTmpBounds.set(configuration.windowConfiguration.getBounds());
            }
            resolvedOverrideConfiguration.windowConfiguration.setMaxBounds(this.mTmpBounds);
        }
        logAppCompatState();
    }

    @Configuration.Orientation
    public int getOrientationForReachability() {
        if (this.mLetterboxUiController.hasInheritedLetterboxBehavior()) {
            return this.mLetterboxUiController.getInheritedOrientation();
        }
        return getRequestedConfigurationOrientation();
    }

    public boolean areBoundsLetterboxed() {
        return getAppCompatState(true) != 2;
    }

    public final void logAppCompatState() {
        this.mTaskSupervisor.getActivityMetricsLogger().logAppCompatState(this);
    }

    public int getAppCompatState() {
        return getAppCompatState(false);
    }

    public final int getAppCompatState(boolean z) {
        if (z || this.mVisibleRequested) {
            if (this.mLetterboxUiController.hasInheritedLetterboxBehavior()) {
                return this.mLetterboxUiController.getInheritedAppCompatState();
            }
            if (this.mInSizeCompatModeForBounds) {
                return 3;
            }
            if (isLetterboxedForFixedOrientationAndAspectRatio()) {
                return 4;
            }
            return this.mIsAspectRatioApplied ? 5 : 2;
        }
        return 1;
    }

    public final void updateResolvedBoundsPosition(Configuration configuration) {
        int i;
        Configuration resolvedOverrideConfiguration = getResolvedOverrideConfiguration();
        Rect bounds = resolvedOverrideConfiguration.windowConfiguration.getBounds();
        Rect rect = this.mSizeCompatBounds;
        if (rect == null) {
            rect = bounds;
        }
        Rect appBounds = configuration.windowConfiguration.getAppBounds();
        Rect bounds2 = configuration.windowConfiguration.getBounds();
        if (bounds.isEmpty()) {
            return;
        }
        int i2 = 0;
        if (bounds2.width() == rect.width() || rect.width() > appBounds.width()) {
            i = 0;
        } else {
            i = Math.max(0, (((int) Math.ceil((appBounds.width() - rect.width()) * this.mLetterboxUiController.getHorizontalPositionMultiplier(configuration))) - rect.left) + appBounds.left);
        }
        if (bounds2.height() != rect.height() && rect.height() <= appBounds.height()) {
            i2 = Math.max(0, (((int) Math.ceil((appBounds.height() - rect.height()) * this.mLetterboxUiController.getVerticalPositionMultiplier(configuration))) - rect.top) + appBounds.top);
        }
        Rect rect2 = this.mSizeCompatBounds;
        if (rect2 != null) {
            rect2.offset(i, i2);
            Rect rect3 = this.mSizeCompatBounds;
            offsetBounds(resolvedOverrideConfiguration, rect3.left - bounds.left, rect3.top - bounds.top);
        } else {
            offsetBounds(resolvedOverrideConfiguration, i, i2);
        }
        if (resolvedOverrideConfiguration.windowConfiguration.getAppBounds().top == appBounds.top) {
            resolvedOverrideConfiguration.windowConfiguration.getBounds().top = bounds2.top;
            Rect rect4 = this.mSizeCompatBounds;
            if (rect4 != null) {
                rect4.top = bounds2.top;
            }
        }
        getTaskFragment().computeConfigResourceOverrides(resolvedOverrideConfiguration, configuration);
    }

    public void recomputeConfiguration() {
        if (this.mLetterboxUiController.applyOnOpaqueActivityBelow(new Consumer() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda24
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ActivityRecord) obj).recomputeConfiguration();
            }
        })) {
            return;
        }
        onRequestedOverrideConfigurationChanged(getRequestedOverrideConfiguration());
    }

    public boolean isInTransition() {
        return inTransitionSelfOrParent();
    }

    public boolean isLetterboxedForFixedOrientationAndAspectRatio() {
        return this.mLetterboxBoundsForFixedOrientationAndAspectRatio != null;
    }

    public boolean isAspectRatioApplied() {
        return this.mIsAspectRatioApplied;
    }

    public boolean isEligibleForLetterboxEducation() {
        return this.mWmService.mLetterboxConfiguration.getIsEducationEnabled() && this.mIsEligibleForFixedOrientationLetterbox && getWindowingMode() == 1 && getRequestedConfigurationOrientation() == 1 && this.mStartingWindow == null;
    }

    public final boolean orientationRespectedWithInsets(Rect rect, Rect rect2) {
        int requestedConfigurationOrientation;
        DisplayInfo displayInfo;
        rect2.setEmpty();
        boolean z = true;
        if (this.mDisplayContent == null || (requestedConfigurationOrientation = getRequestedConfigurationOrientation()) == 0) {
            return true;
        }
        int i = rect.height() >= rect.width() ? 1 : 2;
        if (isFixedRotationTransforming()) {
            displayInfo = getFixedRotationTransformDisplayInfo();
        } else {
            displayInfo = this.mDisplayContent.getDisplayInfo();
        }
        getTask().calculateInsetFrames(this.mTmpBounds, rect2, rect, displayInfo);
        int i2 = rect2.height() >= rect2.width() ? 1 : 2;
        if (i != i2 && i2 != requestedConfigurationOrientation) {
            z = false;
        }
        if (z) {
            rect2.setEmpty();
        }
        return z;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean handlesOrientationChangeFromDescendant(int i) {
        if (shouldIgnoreOrientationRequests()) {
            return false;
        }
        return super.handlesOrientationChangeFromDescendant(i);
    }

    public final void resolveFixedOrientationConfiguration(Configuration configuration) {
        this.mLetterboxBoundsForFixedOrientationAndAspectRatio = null;
        boolean z = false;
        this.mIsEligibleForFixedOrientationLetterbox = false;
        Rect bounds = configuration.windowConfiguration.getBounds();
        Rect rect = new Rect();
        boolean orientationRespectedWithInsets = orientationRespectedWithInsets(bounds, rect);
        if (orientationRespectedWithInsets && handlesOrientationChangeFromDescendant(getOverrideOrientation())) {
            return;
        }
        TaskFragment organizedTaskFragment = getOrganizedTaskFragment();
        if (organizedTaskFragment == null || organizedTaskFragment.fillsParent()) {
            Rect bounds2 = getResolvedOverrideConfiguration().windowConfiguration.getBounds();
            int i = configuration.orientation;
            int requestedConfigurationOrientation = getRequestedConfigurationOrientation();
            if (requestedConfigurationOrientation != 0 && requestedConfigurationOrientation != i) {
                z = true;
            }
            this.mIsEligibleForFixedOrientationLetterbox = z;
            if (z || !(requestedConfigurationOrientation == 0 || orientationRespectedWithInsets)) {
                CompatDisplayInsets compatDisplayInsets = getCompatDisplayInsets();
                if (compatDisplayInsets == null || compatDisplayInsets.mIsInFixedOrientationLetterbox) {
                    if (orientationRespectedWithInsets) {
                        rect = configuration.windowConfiguration.getAppBounds();
                    }
                    Rect rect2 = new Rect();
                    Rect rect3 = new Rect();
                    if (requestedConfigurationOrientation == 2) {
                        int min = Math.min((rect.top + bounds.width()) - 1, rect.bottom);
                        rect2.set(bounds.left, rect.top, bounds.right, min);
                        rect3.set(rect.left, rect.top, rect.right, min);
                    } else {
                        int min2 = Math.min(rect.left + bounds.height(), rect.right);
                        rect2.set(rect.left, bounds.top, min2, bounds.bottom);
                        rect3.set(rect.left, rect.top, min2, rect.bottom);
                    }
                    Rect rect4 = new Rect(bounds2);
                    bounds2.set(rect2);
                    float fixedOrientationLetterboxAspectRatio = this.mLetterboxUiController.getFixedOrientationLetterboxAspectRatio();
                    if (fixedOrientationLetterboxAspectRatio <= 1.0f) {
                        fixedOrientationLetterboxAspectRatio = computeAspectRatio(bounds);
                    }
                    this.mIsAspectRatioApplied = applyAspectRatio(bounds2, rect3, rect2, fixedOrientationLetterboxAspectRatio);
                    if (compatDisplayInsets != null) {
                        compatDisplayInsets.getBoundsByRotation(this.mTmpBounds, configuration.windowConfiguration.getRotation());
                        if (bounds2.width() != this.mTmpBounds.width() || bounds2.height() != this.mTmpBounds.height()) {
                            bounds2.set(rect4);
                            return;
                        }
                    }
                    getTaskFragment().computeConfigResourceOverrides(getResolvedOverrideConfiguration(), configuration, compatDisplayInsets);
                    this.mLetterboxBoundsForFixedOrientationAndAspectRatio = new Rect(bounds2);
                }
            }
        }
    }

    public final void resolveAspectRatioRestriction(Configuration configuration) {
        Configuration resolvedOverrideConfiguration = getResolvedOverrideConfiguration();
        Rect appBounds = configuration.windowConfiguration.getAppBounds();
        Rect bounds = configuration.windowConfiguration.getBounds();
        Rect bounds2 = resolvedOverrideConfiguration.windowConfiguration.getBounds();
        this.mTmpBounds.setEmpty();
        this.mIsAspectRatioApplied = applyAspectRatio(this.mTmpBounds, appBounds, bounds);
        if (!this.mTmpBounds.isEmpty()) {
            bounds2.set(this.mTmpBounds);
        }
        if (bounds2.isEmpty() || bounds2.equals(bounds)) {
            return;
        }
        getTaskFragment().computeConfigResourceOverrides(resolvedOverrideConfiguration, configuration, getFixedRotationTransformDisplayInfo());
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x00a1  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00c4  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00df  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00e1  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00f5  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x011a  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0128  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x012b  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x012f  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0132  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x013c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void resolveSizeCompatModeConfiguration(Configuration configuration, CompatDisplayInsets compatDisplayInsets) {
        Rect bounds;
        Rect appBounds;
        int i;
        Rect appBounds2;
        float f;
        int i2;
        int i3;
        int i4;
        Rect rect;
        Configuration resolvedOverrideConfiguration = getResolvedOverrideConfiguration();
        Rect bounds2 = resolvedOverrideConfiguration.windowConfiguration.getBounds();
        if (isLetterboxedForFixedOrientationAndAspectRatio()) {
            bounds = new Rect(bounds2);
        } else {
            bounds = configuration.windowConfiguration.getBounds();
        }
        Rect rect2 = bounds;
        if (isLetterboxedForFixedOrientationAndAspectRatio()) {
            appBounds = new Rect(getResolvedOverrideConfiguration().windowConfiguration.getAppBounds());
        } else {
            appBounds = configuration.windowConfiguration.getAppBounds();
        }
        Rect rect3 = appBounds;
        int requestedConfigurationOrientation = getRequestedConfigurationOrientation();
        boolean z = requestedConfigurationOrientation != 0;
        if (!z && (requestedConfigurationOrientation = compatDisplayInsets.mOriginalRequestedOrientation) == 0) {
            requestedConfigurationOrientation = configuration.orientation;
        }
        int i5 = requestedConfigurationOrientation;
        int rotation = configuration.windowConfiguration.getRotation();
        DisplayContent displayContent = this.mDisplayContent;
        boolean z2 = displayContent == null || displayContent.getDisplayRotation().isFixedToUserRotation();
        if (!z2 && !compatDisplayInsets.mIsFloating) {
            resolvedOverrideConfiguration.windowConfiguration.setRotation(rotation);
        } else {
            int rotation2 = resolvedOverrideConfiguration.windowConfiguration.getRotation();
            if (rotation2 != -1) {
                i = rotation2;
                Rect rect4 = new Rect();
                Rect rect5 = this.mTmpBounds;
                compatDisplayInsets.getContainerBounds(rect4, rect5, i, i5, z, z2);
                bounds2.set(rect5);
                if (!compatDisplayInsets.mIsFloating) {
                    this.mIsAspectRatioApplied = applyAspectRatio(bounds2, rect4, rect5);
                }
                getTaskFragment().computeConfigResourceOverrides(resolvedOverrideConfiguration, configuration, compatDisplayInsets);
                resolvedOverrideConfiguration.screenLayout = WindowContainer.computeScreenLayout(getConfiguration().screenLayout, resolvedOverrideConfiguration.screenWidthDp, resolvedOverrideConfiguration.screenHeightDp);
                if (resolvedOverrideConfiguration.screenWidthDp == resolvedOverrideConfiguration.screenHeightDp) {
                    resolvedOverrideConfiguration.orientation = configuration.orientation;
                }
                appBounds2 = resolvedOverrideConfiguration.windowConfiguration.getAppBounds();
                f = this.mSizeCompatScale;
                updateSizeCompatScale(appBounds2, rect3);
                i2 = rect3.top - rect2.top;
                boolean z3 = i2 == appBounds2.top - bounds2.top;
                if (this.mSizeCompatScale == 1.0f || z3) {
                    if (this.mSizeCompatBounds == null) {
                        this.mSizeCompatBounds = new Rect();
                    }
                    this.mSizeCompatBounds.set(appBounds2);
                    this.mSizeCompatBounds.offsetTo(0, 0);
                    this.mSizeCompatBounds.scale(this.mSizeCompatScale);
                    this.mSizeCompatBounds.bottom += i2;
                } else {
                    this.mSizeCompatBounds = null;
                }
                if (this.mSizeCompatScale != f) {
                    forAllWindows((Consumer<WindowState>) new ActivityRecord$$ExternalSyntheticLambda19(), false);
                }
                boolean equals = bounds2.equals(rect5);
                i3 = !equals ? rect2.left : rect3.left;
                i4 = !equals ? rect2.top : rect3.top;
                if (i3 == 0 || i4 != 0) {
                    rect = this.mSizeCompatBounds;
                    if (rect != null) {
                        rect.offset(i3, i4);
                    }
                    offsetBounds(resolvedOverrideConfiguration, i3 - bounds2.left, i4 - bounds2.top);
                }
                this.mInSizeCompatModeForBounds = isInSizeCompatModeForBounds(appBounds2, rect3);
            }
        }
        i = rotation;
        Rect rect42 = new Rect();
        Rect rect52 = this.mTmpBounds;
        compatDisplayInsets.getContainerBounds(rect42, rect52, i, i5, z, z2);
        bounds2.set(rect52);
        if (!compatDisplayInsets.mIsFloating) {
        }
        getTaskFragment().computeConfigResourceOverrides(resolvedOverrideConfiguration, configuration, compatDisplayInsets);
        resolvedOverrideConfiguration.screenLayout = WindowContainer.computeScreenLayout(getConfiguration().screenLayout, resolvedOverrideConfiguration.screenWidthDp, resolvedOverrideConfiguration.screenHeightDp);
        if (resolvedOverrideConfiguration.screenWidthDp == resolvedOverrideConfiguration.screenHeightDp) {
        }
        appBounds2 = resolvedOverrideConfiguration.windowConfiguration.getAppBounds();
        f = this.mSizeCompatScale;
        updateSizeCompatScale(appBounds2, rect3);
        i2 = rect3.top - rect2.top;
        if (i2 == appBounds2.top - bounds2.top) {
        }
        if (this.mSizeCompatScale == 1.0f) {
        }
        if (this.mSizeCompatBounds == null) {
        }
        this.mSizeCompatBounds.set(appBounds2);
        this.mSizeCompatBounds.offsetTo(0, 0);
        this.mSizeCompatBounds.scale(this.mSizeCompatScale);
        this.mSizeCompatBounds.bottom += i2;
        if (this.mSizeCompatScale != f) {
        }
        boolean equals2 = bounds2.equals(rect52);
        if (!equals2) {
        }
        if (!equals2) {
        }
        if (i3 == 0) {
        }
        rect = this.mSizeCompatBounds;
        if (rect != null) {
        }
        offsetBounds(resolvedOverrideConfiguration, i3 - bounds2.left, i4 - bounds2.top);
        this.mInSizeCompatModeForBounds = isInSizeCompatModeForBounds(appBounds2, rect3);
    }

    public void updateSizeCompatScale(final Rect rect, final Rect rect2) {
        this.mSizeCompatScale = ((Float) this.mLetterboxUiController.findOpaqueNotFinishingActivityBelow().map(new Function() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda32
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Float lambda$updateSizeCompatScale$20;
                lambda$updateSizeCompatScale$20 = ActivityRecord.lambda$updateSizeCompatScale$20((ActivityRecord) obj);
                return lambda$updateSizeCompatScale$20;
            }
        }).orElseGet(new Supplier() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda33
            @Override // java.util.function.Supplier
            public final Object get() {
                Float lambda$updateSizeCompatScale$21;
                lambda$updateSizeCompatScale$21 = ActivityRecord.lambda$updateSizeCompatScale$21(rect, rect2);
                return lambda$updateSizeCompatScale$21;
            }
        })).floatValue();
    }

    public static /* synthetic */ Float lambda$updateSizeCompatScale$20(ActivityRecord activityRecord) {
        return Float.valueOf(activityRecord.mSizeCompatScale);
    }

    public static /* synthetic */ Float lambda$updateSizeCompatScale$21(Rect rect, Rect rect2) {
        int width = rect.width();
        int height = rect.height();
        int width2 = rect2.width();
        int height2 = rect2.height();
        return Float.valueOf((width > width2 || height > height2) ? Math.min(width2 / width, height2 / height) : 1.0f);
    }

    public final boolean isInSizeCompatModeForBounds(Rect rect, Rect rect2) {
        if (this.mLetterboxUiController.hasInheritedLetterboxBehavior()) {
            return false;
        }
        int width = rect.width();
        int height = rect.height();
        int width2 = rect2.width();
        int height2 = rect2.height();
        if (width2 == width && height2 == height) {
            return false;
        }
        if ((width2 <= width || height2 <= height) && width2 >= width && height2 >= height) {
            float maxAspectRatio = getMaxAspectRatio();
            if (maxAspectRatio > 0.0f && (Math.max(width, height) + 0.5f) / Math.min(width, height) >= maxAspectRatio) {
                return false;
            }
            float minAspectRatio = getMinAspectRatio();
            if (minAspectRatio > 0.0f && (Math.max(width2, height2) + 0.5f) / Math.min(width2, height2) <= minAspectRatio) {
                return false;
            }
        }
        return true;
    }

    public static void offsetBounds(Configuration configuration, int i, int i2) {
        configuration.windowConfiguration.getBounds().offset(i, i2);
        configuration.windowConfiguration.getAppBounds().offset(i, i2);
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public Rect getBounds() {
        final Rect bounds = super.getBounds();
        return (Rect) this.mLetterboxUiController.findOpaqueNotFinishingActivityBelow().map(new Function() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ActivityRecord) obj).getBounds();
            }
        }).orElseGet(new Supplier() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda12
            @Override // java.util.function.Supplier
            public final Object get() {
                Rect lambda$getBounds$22;
                lambda$getBounds$22 = ActivityRecord.this.lambda$getBounds$22(bounds);
                return lambda$getBounds$22;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Rect lambda$getBounds$22(Rect rect) {
        Rect rect2 = this.mSizeCompatBounds;
        return rect2 != null ? rect2 : rect;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public boolean providesMaxBounds() {
        if (getUid() == 1000) {
            return false;
        }
        DisplayContent displayContent = this.mDisplayContent;
        if ((displayContent == null || displayContent.sandboxDisplayApis()) && !this.info.neverSandboxDisplayApis(sConstrainDisplayApisConfig)) {
            return this.info.alwaysSandboxDisplayApis(sConstrainDisplayApisConfig) || getCompatDisplayInsets() != null || shouldCreateCompatDisplayInsets();
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    @VisibleForTesting
    public Rect getAnimationBounds(int i) {
        TaskFragment taskFragment = getTaskFragment();
        return taskFragment != null ? taskFragment.getBounds() : getBounds();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void getAnimationPosition(Point point) {
        point.set(0, 0);
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void onConfigurationChanged(Configuration configuration) {
        Task task;
        int rotationForActivityInDifferentOrientation;
        int requestedOverrideWindowingMode;
        if (this.mTransitionController.isShellTransitionsEnabled() && isVisible() && isVisibleRequested()) {
            if (getRequestedOverrideWindowingMode() == 0) {
                requestedOverrideWindowingMode = configuration.windowConfiguration.getWindowingMode();
            } else {
                requestedOverrideWindowingMode = getRequestedOverrideWindowingMode();
            }
            if (getWindowingMode() != requestedOverrideWindowingMode) {
                this.mTransitionController.collect(this);
            }
        }
        boolean z = true;
        if (getCompatDisplayInsets() != null) {
            Configuration requestedOverrideConfiguration = getRequestedOverrideConfiguration();
            boolean z2 = requestedOverrideConfiguration.windowConfiguration.getRotation() != -1;
            int requestedConfigurationOrientation = getRequestedConfigurationOrientation();
            if (requestedConfigurationOrientation != 0 && requestedConfigurationOrientation != getConfiguration().orientation && requestedConfigurationOrientation == getParent().getConfiguration().orientation && requestedOverrideConfiguration.windowConfiguration.getRotation() != getParent().getWindowConfiguration().getRotation()) {
                requestedOverrideConfiguration.windowConfiguration.setRotation(getParent().getWindowConfiguration().getRotation());
                onRequestedOverrideConfigurationChanged(requestedOverrideConfiguration);
                return;
            } else if (z2 && requestedConfigurationOrientation == 0 && requestedOverrideConfiguration.windowConfiguration.getRotation() != -1) {
                requestedOverrideConfiguration.windowConfiguration.setRotation(-1);
                onRequestedOverrideConfigurationChanged(requestedOverrideConfiguration);
                return;
            }
        }
        boolean inPinnedWindowingMode = inPinnedWindowingMode();
        DisplayContent displayContent = this.mDisplayContent;
        if (inPinnedWindowingMode && attachedToProcess() && displayContent != null) {
            try {
                this.app.pauseConfigurationDispatch();
                super.onConfigurationChanged(configuration);
                if (this.mVisibleRequested && !inMultiWindowMode() && (rotationForActivityInDifferentOrientation = displayContent.rotationForActivityInDifferentOrientation(this)) != -1) {
                    this.app.resumeConfigurationDispatch();
                    displayContent.setFixedRotationLaunchingApp(this, rotationForActivityInDifferentOrientation);
                }
            } finally {
                if (this.app.resumeConfigurationDispatch()) {
                    WindowProcessController windowProcessController = this.app;
                    windowProcessController.dispatchConfiguration(windowProcessController.getConfiguration());
                }
            }
        } else {
            super.onConfigurationChanged(configuration);
        }
        if (getMergedOverrideConfiguration().seq != getResolvedOverrideConfiguration().seq) {
            onMergedOverrideConfigurationChanged();
        }
        if (!inPinnedWindowingMode && inPinnedWindowingMode() && (task = this.task) != null) {
            this.mWaitForEnteringPinnedMode = false;
            this.mTaskSupervisor.scheduleUpdatePictureInPictureModeIfNeeded(task, task.getBounds());
        }
        if (displayContent == null) {
            return;
        }
        if (this.mVisibleRequested) {
            displayContent.handleActivitySizeCompatModeIfNeeded(this);
        } else if (getCompatDisplayInsets() == null || this.visibleIgnoringKeyguard) {
        } else {
            WindowProcessController windowProcessController2 = this.app;
            if (windowProcessController2 == null || !windowProcessController2.hasVisibleActivities()) {
                int currentOverrideConfigurationChanges = displayContent.getCurrentOverrideConfigurationChanges();
                if (!hasResizeChange(currentOverrideConfigurationChanges) || (currentOverrideConfigurationChanges & 536872064) == 536872064) {
                    z = false;
                }
                if (z || (currentOverrideConfigurationChanges & IInstalld.FLAG_USE_QUOTA) != 0) {
                    restartProcessIfVisible();
                }
            }
        }
    }

    public final boolean applyAspectRatio(Rect rect, Rect rect2, Rect rect3) {
        return applyAspectRatio(rect, rect2, rect3, 0.0f);
    }

    public final boolean applyAspectRatio(Rect rect, Rect rect2, Rect rect3, float f) {
        int i;
        float f2;
        float f3;
        int i2;
        float maxAspectRatio = getMaxAspectRatio();
        Task rootTask = getRootTask();
        float minAspectRatio = getMinAspectRatio();
        TaskFragment organizedTaskFragment = getOrganizedTaskFragment();
        if (this.task == null || rootTask == null || ((maxAspectRatio < 1.0f && minAspectRatio < 1.0f && f < 1.0f) || isInVrUiMode(getConfiguration()) || !(organizedTaskFragment == null || organizedTaskFragment.fillsParent()))) {
            return false;
        }
        int width = rect2.width();
        int height = rect2.height();
        float computeAspectRatio = computeAspectRatio(rect2);
        if (f < 1.0f) {
            f = computeAspectRatio;
        }
        if (maxAspectRatio < 1.0f || f <= maxAspectRatio) {
            maxAspectRatio = (minAspectRatio < 1.0f || f >= minAspectRatio) ? f : minAspectRatio;
        }
        if (computeAspectRatio - maxAspectRatio > 0.005f) {
            if (width < height) {
                f2 = width * maxAspectRatio;
                i2 = (int) (f2 + 0.5f);
                i = width;
            } else {
                f3 = height * maxAspectRatio;
                i = (int) (f3 + 0.5f);
                i2 = height;
            }
        } else if (maxAspectRatio - computeAspectRatio > 0.005f) {
            int requestedConfigurationOrientation = getRequestedConfigurationOrientation();
            if (requestedConfigurationOrientation == 1 || (requestedConfigurationOrientation != 2 && width < height)) {
                f3 = height / maxAspectRatio;
                i = (int) (f3 + 0.5f);
                i2 = height;
            } else {
                f2 = width / maxAspectRatio;
                i2 = (int) (f2 + 0.5f);
                i = width;
            }
        } else {
            i = width;
            i2 = height;
        }
        if (width > i || height > i2) {
            int i3 = rect2.left;
            int i4 = i + i3;
            if (i4 >= rect2.right) {
                i4 = rect3.right;
                i3 = rect3.left;
            }
            int i5 = rect2.top;
            int i6 = i2 + i5;
            if (i6 >= rect2.bottom) {
                i6 = rect3.bottom;
                i5 = rect3.top;
            }
            rect.set(i3, i5, i4, i6);
            return true;
        }
        return false;
    }

    public float getMinAspectRatio() {
        if (this.mLetterboxUiController.hasInheritedLetterboxBehavior()) {
            return this.mLetterboxUiController.getInheritedMinAspectRatio();
        }
        ActivityInfo activityInfo = this.info;
        if (activityInfo.applicationInfo == null) {
            return activityInfo.getMinAspectRatio();
        }
        if (!activityInfo.isChangeEnabled(174042980L)) {
            return this.info.getMinAspectRatio();
        }
        if (this.info.isChangeEnabled(203647190L) && !ActivityInfo.isFixedOrientationPortrait(getOverrideOrientation())) {
            return this.info.getMinAspectRatio();
        }
        if (this.info.isChangeEnabled(218959984L) && isParentFullscreenPortrait()) {
            return this.info.getMinAspectRatio();
        }
        if (this.info.isChangeEnabled(208648326L)) {
            return Math.max(this.mLetterboxUiController.getSplitScreenAspectRatio(), this.info.getMinAspectRatio());
        }
        if (this.info.isChangeEnabled(180326787L)) {
            return Math.max(1.7777778f, this.info.getMinAspectRatio());
        }
        if (this.info.isChangeEnabled(180326845L)) {
            return Math.max(1.5f, this.info.getMinAspectRatio());
        }
        return this.info.getMinAspectRatio();
    }

    public final boolean isParentFullscreenPortrait() {
        WindowContainer parent = getParent();
        return parent != null && parent.getConfiguration().orientation == 1 && parent.getWindowConfiguration().getWindowingMode() == 1;
    }

    public float getMaxAspectRatio() {
        if (this.mLetterboxUiController.hasInheritedLetterboxBehavior()) {
            return this.mLetterboxUiController.getInheritedMaxAspectRatio();
        }
        return this.info.getMaxAspectRatio();
    }

    public final boolean hasFixedAspectRatio() {
        return (getMaxAspectRatio() == 0.0f && getMinAspectRatio() == 0.0f) ? false : true;
    }

    public static float computeAspectRatio(Rect rect) {
        int width = rect.width();
        int height = rect.height();
        if (width == 0 || height == 0) {
            return 0.0f;
        }
        return Math.max(width, height) / Math.min(width, height);
    }

    public boolean shouldUpdateConfigForDisplayChanged() {
        return this.mLastReportedDisplayId != getDisplayId();
    }

    public boolean ensureActivityConfiguration(int i, boolean z) {
        return ensureActivityConfiguration(i, z, false, false);
    }

    public boolean ensureActivityConfiguration(int i, boolean z, boolean z2) {
        return ensureActivityConfiguration(i, z, z2, false);
    }

    public boolean ensureActivityConfiguration(int i, boolean z, boolean z2, boolean z3) {
        State state;
        if (getRootTask().mConfigWillChange) {
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -804217032, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            return true;
        } else if (this.finishing) {
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -846078709, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            stopFreezingScreenLocked(false);
            return true;
        } else if (isState(State.DESTROYED)) {
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 1105210816, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            return true;
        } else if (!z2 && ((state = this.mState) == State.STOPPING || state == State.STOPPED || !shouldBeVisible())) {
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 1635062046, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            return true;
        } else {
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -1791031393, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            int displayId = getDisplayId();
            boolean z4 = this.mLastReportedDisplayId != displayId;
            if (z4) {
                this.mLastReportedDisplayId = displayId;
            }
            if (this.mVisibleRequested) {
                updateCompatDisplayInsets();
            }
            this.mTmpConfig.setTo(this.mLastReportedConfiguration.getMergedConfiguration());
            if (getConfiguration().equals(this.mTmpConfig) && !this.forceNewConfig && !z4) {
                if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -1115019498, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                return true;
            }
            int configurationChanges = getConfigurationChanges(this.mTmpConfig);
            Configuration mergedOverrideConfiguration = getMergedOverrideConfiguration();
            setLastReportedConfiguration(getProcessGlobalConfiguration(), mergedOverrideConfiguration);
            if (this.mState == State.INITIALIZING) {
                if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -235225312, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                return true;
            } else if (configurationChanges == 0 && !this.forceNewConfig) {
                if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -743431900, 0, (String) null, new Object[]{String.valueOf(this)});
                }
                if (z4) {
                    scheduleActivityMovedToDisplay(displayId, mergedOverrideConfiguration);
                } else {
                    scheduleConfigurationChanged(mergedOverrideConfiguration);
                }
                notifyDisplayCompatPolicyAboutConfigurationChange(this.mLastReportedConfiguration.getMergedConfiguration(), this.mTmpConfig);
                return true;
            } else {
                if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -929676529, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(Configuration.configurationDiffToString(configurationChanges))});
                }
                if (!attachedToProcess()) {
                    if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 1679569477, 0, (String) null, new Object[]{String.valueOf(this)});
                    }
                    stopFreezingScreenLocked(false);
                    this.forceNewConfig = false;
                    return true;
                }
                if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 1995093920, 0, (String) null, new Object[]{String.valueOf(this.info.name), String.valueOf(Integer.toHexString(configurationChanges)), String.valueOf(Integer.toHexString(this.info.getRealConfigChanged())), String.valueOf(this.mLastReportedConfiguration)});
                }
                if (shouldRelaunchLocked(configurationChanges, this.mTmpConfig) || this.forceNewConfig) {
                    this.configChangeFlags |= configurationChanges;
                    startFreezingScreenLocked(i);
                    this.forceNewConfig = false;
                    boolean z5 = z & (isResizeOnlyChange(configurationChanges) && !this.mFreezingScreen);
                    if (hasResizeChange((~this.info.getRealConfigChanged()) & configurationChanges)) {
                        this.mRelaunchReason = this.task.isDragResizing() ? 2 : 1;
                    } else {
                        this.mRelaunchReason = 0;
                    }
                    if (z3) {
                        this.mLetterboxUiController.setRelauchingAfterRequestedOrientationChanged(true);
                    }
                    if (this.mState == State.PAUSING) {
                        if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -90559682, 0, (String) null, new Object[]{String.valueOf(this)});
                        }
                        this.deferRelaunchUntilPaused = true;
                        this.preserveWindowOnDeferredRelaunch = z5;
                        return true;
                    }
                    if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 736692676, 0, (String) null, new Object[]{String.valueOf(this)});
                    }
                    if (!this.mVisibleRequested && ProtoLogCache.WM_DEBUG_STATES_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1558137010, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(Debug.getCallers(4))});
                    }
                    relaunchActivityLocked(z5);
                    return false;
                }
                if (z4) {
                    scheduleActivityMovedToDisplay(displayId, mergedOverrideConfiguration);
                } else {
                    scheduleConfigurationChanged(mergedOverrideConfiguration);
                }
                notifyDisplayCompatPolicyAboutConfigurationChange(this.mLastReportedConfiguration.getMergedConfiguration(), this.mTmpConfig);
                stopFreezingScreenLocked(false);
                return true;
            }
        }
    }

    public final void notifyDisplayCompatPolicyAboutConfigurationChange(Configuration configuration, Configuration configuration2) {
        if (this.mDisplayContent.mDisplayRotationCompatPolicy == null || !shouldBeResumed(null)) {
            return;
        }
        this.mDisplayContent.mDisplayRotationCompatPolicy.onActivityConfigurationChanging(this, configuration, configuration2);
    }

    public final Configuration getProcessGlobalConfiguration() {
        WindowProcessController windowProcessController = this.app;
        return windowProcessController != null ? windowProcessController.getConfiguration() : this.mAtmService.getGlobalConfiguration();
    }

    public final boolean shouldRelaunchLocked(int i, Configuration configuration) {
        int realConfigChanged = this.info.getRealConfigChanged();
        boolean onlyVrUiModeChanged = onlyVrUiModeChanged(i, configuration);
        if (this.info.applicationInfo.targetSdkVersion < 26 && this.requestedVrComponent != null && onlyVrUiModeChanged) {
            realConfigChanged |= 512;
        }
        return ((~realConfigChanged) & i) != 0;
    }

    public final boolean onlyVrUiModeChanged(int i, Configuration configuration) {
        return i == 512 && isInVrUiMode(getConfiguration()) != isInVrUiMode(configuration);
    }

    public final int getConfigurationChanges(Configuration configuration) {
        int filterDiff = SizeConfigurationBuckets.filterDiff(configuration.diff(getConfiguration()), configuration, getConfiguration(), this.mSizeConfigurations);
        return (536870912 & filterDiff) != 0 ? filterDiff & (-536870913) : filterDiff;
    }

    public void relaunchActivityLocked(boolean z) {
        ArrayList<ResultInfo> arrayList;
        ArrayList<ReferrerIntent> arrayList2;
        ResumeActivityItem obtain;
        WindowState windowState;
        if (this.mAtmService.mSuppressResizeConfigChanges && z) {
            this.configChangeFlags = 0;
            return;
        }
        if (!z) {
            InputTarget imeInputTarget = this.mDisplayContent.getImeInputTarget();
            this.mLastImeShown = (imeInputTarget == null || imeInputTarget.getWindowState() == null || imeInputTarget.getWindowState().mActivityRecord != this || (windowState = this.mDisplayContent.mInputMethodWindow) == null || !windowState.isVisible()) ? false : true;
        }
        Task rootTask = getRootTask();
        if (rootTask != null && rootTask.mTranslucentActivityWaiting == this) {
            rootTask.checkTranslucentActivityWaiting(null);
        }
        boolean shouldBeResumed = shouldBeResumed(null);
        if (shouldBeResumed) {
            arrayList = this.results;
            arrayList2 = this.newIntents;
        } else {
            arrayList = null;
            arrayList2 = null;
        }
        if (shouldBeResumed) {
            EventLogTags.writeWmRelaunchResumeActivity(this.mUserId, System.identityHashCode(this), this.task.mTaskId, this.shortComponentName, Integer.toHexString(this.configChangeFlags));
        } else {
            EventLogTags.writeWmRelaunchActivity(this.mUserId, System.identityHashCode(this), this.task.mTaskId, this.shortComponentName, Integer.toHexString(this.configChangeFlags));
        }
        startFreezingScreenLocked(0);
        try {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_STATES, -1016578046, 0, (String) null, new Object[]{shouldBeResumed ? "RESUMED" : "PAUSED", String.valueOf(this), String.valueOf(Debug.getCallers(6))});
            }
            this.forceNewConfig = false;
            startRelaunching();
            ActivityRelaunchItem obtain2 = ActivityRelaunchItem.obtain(arrayList, arrayList2, this.configChangeFlags, new MergedConfiguration(getProcessGlobalConfiguration(), getMergedOverrideConfiguration()), z);
            if (shouldBeResumed) {
                obtain = ResumeActivityItem.obtain(isTransitionForward(), shouldSendCompatFakeFocus());
            } else {
                obtain = PauseActivityItem.obtain();
            }
            ClientTransaction obtain3 = ClientTransaction.obtain(this.app.getThread(), this.token);
            obtain3.addCallback(obtain2);
            obtain3.setLifecycleStateRequest(obtain);
            this.mAtmService.getLifecycleManager().scheduleTransaction(obtain3);
        } catch (RemoteException e) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_STATES, -262984451, 0, (String) null, new Object[]{String.valueOf(e)});
            }
        }
        if (shouldBeResumed) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, 1270792394, 0, (String) null, new Object[]{String.valueOf(this)});
            }
            this.results = null;
            this.newIntents = null;
            this.mAtmService.getAppWarningsLocked().onResumeActivity(this);
        } else {
            removePauseTimeout();
            setState(State.PAUSED, "relaunchActivityLocked");
        }
        this.mTaskSupervisor.mStoppingActivities.remove(this);
        this.configChangeFlags = 0;
        this.deferRelaunchUntilPaused = false;
        this.preserveWindowOnDeferredRelaunch = false;
    }

    public void restartProcessIfVisible() {
        Slog.i("ActivityTaskManager", "Request to restart process of " + this);
        clearSizeCompatMode();
        if (attachedToProcess()) {
            setState(State.RESTARTING_PROCESS, "restartActivityProcess");
            if (!this.mVisibleRequested || this.mHaveState) {
                this.mAtmService.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda17
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityRecord.this.lambda$restartProcessIfVisible$23();
                    }
                });
                return;
            }
            if (getParent() != null) {
                startFreezingScreen();
            }
            try {
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), this.token, (ActivityLifecycleItem) StopActivityItem.obtain(0));
            } catch (RemoteException e) {
                Slog.w("ActivityTaskManager", "Exception thrown during restart " + this, e);
            }
            this.mTaskSupervisor.scheduleRestartTimeout(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$restartProcessIfVisible$23() {
        synchronized (this.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (hasProcess() && this.app.getReportedProcState() > 6) {
                    WindowProcessController windowProcessController = this.app;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    this.mAtmService.mAmInternal.killProcess(windowProcessController.mName, windowProcessController.mUid, "resetConfig");
                    return;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean isProcessRunning() {
        WindowProcessController windowProcessController = this.app;
        if (windowProcessController == null) {
            windowProcessController = (WindowProcessController) this.mAtmService.mProcessNames.get(this.processName, this.info.applicationInfo.uid);
        }
        return windowProcessController != null && windowProcessController.hasThread();
    }

    public final boolean allowTaskSnapshot() {
        ArrayList<ReferrerIntent> arrayList = this.newIntents;
        if (arrayList == null) {
            return true;
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            Intent intent = this.newIntents.get(size);
            if (intent != null && !isMainIntent(intent)) {
                Intent intent2 = this.mLastNewIntent;
                if (!(intent2 != null ? intent2.filterEquals(intent) : this.intent.filterEquals(intent)) || intent.getExtras() != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isNoHistory() {
        return ((this.intent.getFlags() & 1073741824) == 0 && (this.info.flags & 128) == 0) ? false : true;
    }

    public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException, XmlPullParserException {
        typedXmlSerializer.attributeLong((String) null, "id", this.createTime);
        typedXmlSerializer.attributeInt((String) null, "launched_from_uid", this.launchedFromUid);
        String str = this.launchedFromPackage;
        if (str != null) {
            typedXmlSerializer.attribute((String) null, "launched_from_package", str);
        }
        String str2 = this.launchedFromFeatureId;
        if (str2 != null) {
            typedXmlSerializer.attribute((String) null, "launched_from_feature", str2);
        }
        String str3 = this.resolvedType;
        if (str3 != null) {
            typedXmlSerializer.attribute((String) null, "resolved_type", str3);
        }
        typedXmlSerializer.attributeBoolean((String) null, "component_specified", this.componentSpecified);
        typedXmlSerializer.attributeInt((String) null, "user_id", this.mUserId);
        ActivityManager.TaskDescription taskDescription = this.taskDescription;
        if (taskDescription != null) {
            taskDescription.saveToXml(typedXmlSerializer);
        }
        typedXmlSerializer.startTag((String) null, "intent");
        this.intent.saveToXml(typedXmlSerializer);
        typedXmlSerializer.endTag((String) null, "intent");
        if (!isPersistable() || this.mPersistentState == null) {
            return;
        }
        typedXmlSerializer.startTag((String) null, "persistable_bundle");
        this.mPersistentState.saveToXml(typedXmlSerializer);
        typedXmlSerializer.endTag((String) null, "persistable_bundle");
    }

    public static ActivityRecord restoreFromXml(TypedXmlPullParser typedXmlPullParser, ActivityTaskSupervisor activityTaskSupervisor) throws IOException, XmlPullParserException {
        Intent intent = null;
        int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "launched_from_uid", 0);
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "launched_from_package");
        String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "launched_from_feature");
        String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "resolved_type");
        boolean attributeBoolean = typedXmlPullParser.getAttributeBoolean((String) null, "component_specified", false);
        int attributeInt2 = typedXmlPullParser.getAttributeInt((String) null, "user_id", 0);
        long attributeLong = typedXmlPullParser.getAttributeLong((String) null, "id", -1L);
        int depth = typedXmlPullParser.getDepth();
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription();
        taskDescription.restoreFromXml(typedXmlPullParser);
        PersistableBundle persistableBundle = null;
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() < depth)) {
                break;
            } else if (next == 2) {
                String name = typedXmlPullParser.getName();
                if ("intent".equals(name)) {
                    intent = Intent.restoreFromXml(typedXmlPullParser);
                } else if ("persistable_bundle".equals(name)) {
                    persistableBundle = PersistableBundle.restoreFromXml(typedXmlPullParser);
                } else {
                    Slog.w("ActivityTaskManager", "restoreActivity: unexpected name=" + name);
                    com.android.internal.util.XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
        if (intent == null) {
            throw new XmlPullParserException("restoreActivity error intent=" + intent);
        }
        ActivityTaskManagerService activityTaskManagerService = activityTaskSupervisor.mService;
        PersistableBundle persistableBundle2 = persistableBundle;
        ActivityInfo resolveActivity = activityTaskSupervisor.resolveActivity(intent, attributeValue3, 0, null, attributeInt2, Binder.getCallingUid(), 0);
        if (resolveActivity == null) {
            throw new XmlPullParserException("restoreActivity resolver error. Intent=" + intent + " resolvedType=" + attributeValue3);
        }
        return new Builder(activityTaskManagerService).setLaunchedFromUid(attributeInt).setLaunchedFromPackage(attributeValue).setLaunchedFromFeature(attributeValue2).setIntent(intent).setResolvedType(attributeValue3).setActivityInfo(resolveActivity).setComponentSpecified(attributeBoolean).setPersistentState(persistableBundle2).setTaskDescription(taskDescription).setCreateTime(attributeLong).build();
    }

    public static boolean isInVrUiMode(Configuration configuration) {
        return (configuration.uiMode & 15) == 7;
    }

    public String getProcessName() {
        return this.info.applicationInfo.processName;
    }

    public int getUid() {
        return this.info.applicationInfo.uid;
    }

    public boolean isUid(int i) {
        return this.info.applicationInfo.uid == i;
    }

    public int getPid() {
        WindowProcessController windowProcessController = this.app;
        if (windowProcessController != null) {
            return windowProcessController.getPid();
        }
        return 0;
    }

    public String getFilteredReferrer(String str) {
        if (str != null) {
            if (str.equals(this.packageName) || !this.mWmService.mPmInternal.filterAppAccess(str, this.info.applicationInfo.uid, this.mUserId)) {
                return str;
            }
            return null;
        }
        return null;
    }

    public boolean canTurnScreenOn() {
        if (getTurnScreenOnFlag()) {
            return this.mCurrentLaunchCanTurnScreenOn && getRootTask() != null && this.mTaskSupervisor.getKeyguardController().checkKeyguardVisibility(this);
        }
        return false;
    }

    public void setTurnScreenOn(boolean z) {
        this.mTurnScreenOn = z;
    }

    public void setAllowCrossUidActivitySwitchFromBelow(boolean z) {
        this.mAllowCrossUidActivitySwitchFromBelow = z;
    }

    public Pair<Boolean, Boolean> allowCrossUidActivitySwitchFromBelow(int i) {
        int i2 = this.info.applicationInfo.uid;
        if (i == i2) {
            Boolean bool = Boolean.TRUE;
            return new Pair<>(bool, bool);
        } else if (this.mAllowCrossUidActivitySwitchFromBelow) {
            Boolean bool2 = Boolean.TRUE;
            return new Pair<>(bool2, bool2);
        } else {
            return new Pair<>(Boolean.valueOf(!(ActivitySecurityModelFeatureFlags.shouldRestrictActivitySwitch(i2) && ActivitySecurityModelFeatureFlags.shouldRestrictActivitySwitch(i))), Boolean.FALSE);
        }
    }

    public boolean getTurnScreenOnFlag() {
        return this.mTurnScreenOn || containsTurnScreenOnWindow();
    }

    public final boolean containsTurnScreenOnWindow() {
        if (isRelaunching()) {
            return this.mLastContainsTurnScreenOnWindow;
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if ((((WindowState) this.mChildren.get(size)).mAttrs.flags & 2097152) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean canResumeByCompat() {
        WindowProcessController windowProcessController = this.app;
        return windowProcessController == null || windowProcessController.updateTopResumingActivityInProcessIfNeeded(this);
    }

    public boolean isTopRunningActivity() {
        return this.mRootWindowContainer.topRunningActivity() == this;
    }

    public boolean isFocusedActivityOnDisplay() {
        return this.mDisplayContent.forAllTaskDisplayAreas(new Predicate() { // from class: com.android.server.wm.ActivityRecord$$ExternalSyntheticLambda18
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isFocusedActivityOnDisplay$24;
                lambda$isFocusedActivityOnDisplay$24 = ActivityRecord.this.lambda$isFocusedActivityOnDisplay$24((TaskDisplayArea) obj);
                return lambda$isFocusedActivityOnDisplay$24;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isFocusedActivityOnDisplay$24(TaskDisplayArea taskDisplayArea) {
        return taskDisplayArea.getFocusedActivity() == this;
    }

    public boolean isRootOfTask() {
        Task task = this.task;
        return task != null && this == task.getRootActivity(true);
    }

    public void setTaskOverlay(boolean z) {
        this.mTaskOverlay = z;
        setAlwaysOnTop(z);
    }

    public boolean isTaskOverlay() {
        return this.mTaskOverlay;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public boolean isAlwaysOnTop() {
        return this.mTaskOverlay || super.isAlwaysOnTop();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean showToCurrentUser() {
        return this.mShowForAllUsers || this.mWmService.isUserVisible(this.mUserId);
    }

    @Override // com.android.server.p014wm.WindowToken
    public String toString() {
        if (this.stringName != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.stringName);
            sb.append(" t");
            Task task = this.task;
            sb.append(task == null ? -1 : task.mTaskId);
            sb.append(this.finishing ? " f}" : "");
            sb.append(this.mIsExiting ? " isExiting" : "");
            sb.append("}");
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder(128);
        sb2.append("ActivityRecord{");
        sb2.append(Integer.toHexString(System.identityHashCode(this)));
        sb2.append(" u");
        sb2.append(this.mUserId);
        sb2.append(' ');
        sb2.append(this.intent.getComponent().flattenToShortString());
        String sb3 = sb2.toString();
        this.stringName = sb3;
        return sb3;
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, int i) {
        writeNameToProto(protoOutputStream, 1138166333441L);
        super.dumpDebug(protoOutputStream, 1146756268034L, i);
        protoOutputStream.write(1133871366147L, this.mLastSurfaceShowing);
        protoOutputStream.write(1133871366148L, isWaitingForTransitionStart());
        protoOutputStream.write(1133871366149L, isAnimating(7, 17));
        WindowContainerThumbnail windowContainerThumbnail = this.mThumbnail;
        if (windowContainerThumbnail != null) {
            windowContainerThumbnail.dumpDebug(protoOutputStream, 1146756268038L);
        }
        protoOutputStream.write(1133871366151L, fillsParent());
        protoOutputStream.write(1133871366152L, this.mAppStopped);
        protoOutputStream.write(1133871366174L, !occludesParent());
        protoOutputStream.write(1133871366168L, this.mVisible);
        protoOutputStream.write(1133871366153L, this.mVisibleRequested);
        protoOutputStream.write(1133871366154L, isClientVisible());
        protoOutputStream.write(1133871366155L, this.mDeferHidingClient);
        protoOutputStream.write(1133871366156L, this.mReportedDrawn);
        protoOutputStream.write(1133871366157L, this.reportedVisible);
        protoOutputStream.write(1120986464270L, this.mNumInterestingWindows);
        protoOutputStream.write(1120986464271L, this.mNumDrawnWindows);
        protoOutputStream.write(1133871366160L, this.allDrawn);
        protoOutputStream.write(1133871366161L, this.mLastAllDrawn);
        WindowState windowState = this.mStartingWindow;
        if (windowState != null) {
            windowState.writeIdentifierToProto(protoOutputStream, 1146756268051L);
        }
        protoOutputStream.write(1133871366164L, isStartingWindowDisplayed());
        protoOutputStream.write(1133871366345L, this.startingMoved);
        protoOutputStream.write(1133871366166L, this.mVisibleSetFromTransferredStartingWindow);
        protoOutputStream.write(1138166333467L, this.mState.toString());
        protoOutputStream.write(1133871366172L, isRootOfTask());
        if (hasProcess()) {
            protoOutputStream.write(1120986464285L, this.app.getPid());
        }
        protoOutputStream.write(1133871366175L, this.pictureInPictureArgs.isAutoEnterEnabled());
        protoOutputStream.write(1133871366176L, inSizeCompatMode());
        protoOutputStream.write(1108101562401L, getMinAspectRatio());
        protoOutputStream.write(1133871366178L, providesMaxBounds());
        protoOutputStream.write(1133871366179L, this.mEnableRecentsScreenshot);
        protoOutputStream.write(1120986464292L, this.mLastDropInputMode);
    }

    @Override // com.android.server.p014wm.WindowToken, com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j, int i) {
        if (i != 2 || isVisible()) {
            long start = protoOutputStream.start(j);
            dumpDebug(protoOutputStream, i);
            protoOutputStream.end(start);
        }
    }

    public void writeNameToProto(ProtoOutputStream protoOutputStream, long j) {
        protoOutputStream.write(j, this.shortComponentName);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void writeIdentifierToProto(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, System.identityHashCode(this));
        protoOutputStream.write(1120986464258L, this.mUserId);
        protoOutputStream.write(1138166333443L, this.intent.getComponent().flattenToShortString());
        protoOutputStream.end(start);
    }

    /* renamed from: com.android.server.wm.ActivityRecord$CompatDisplayInsets */
    /* loaded from: classes2.dex */
    public static class CompatDisplayInsets {
        public final int mHeight;
        public final boolean mIsFloating;
        public final boolean mIsInFixedOrientationLetterbox;
        @Configuration.Orientation
        public final int mOriginalRequestedOrientation;
        public final int mOriginalRotation;
        public final int mWidth;
        public final Rect[] mNonDecorInsets = new Rect[4];
        public final Rect[] mStableInsets = new Rect[4];

        public CompatDisplayInsets(DisplayContent displayContent, ActivityRecord activityRecord, Rect rect) {
            int rotation;
            this.mOriginalRotation = displayContent.getRotation();
            boolean tasksAreFloating = activityRecord.getWindowConfiguration().tasksAreFloating();
            this.mIsFloating = tasksAreFloating;
            this.mOriginalRequestedOrientation = activityRecord.getRequestedConfigurationOrientation();
            if (tasksAreFloating) {
                Rect bounds = activityRecord.getWindowConfiguration().getBounds();
                this.mWidth = bounds.width();
                this.mHeight = bounds.height();
                Rect rect2 = new Rect();
                for (int i = 0; i < 4; i++) {
                    this.mNonDecorInsets[i] = rect2;
                    this.mStableInsets[i] = rect2;
                }
                this.mIsInFixedOrientationLetterbox = false;
                return;
            }
            Task task = activityRecord.getTask();
            boolean z = rect != null;
            this.mIsInFixedOrientationLetterbox = z;
            rect = z ? rect : task != null ? task.getBounds() : displayContent.getBounds();
            if (task != null) {
                rotation = task.getConfiguration().windowConfiguration.getRotation();
            } else {
                rotation = displayContent.getConfiguration().windowConfiguration.getRotation();
            }
            Point rotationZeroDimensions = getRotationZeroDimensions(rect, rotation);
            this.mWidth = rotationZeroDimensions.x;
            this.mHeight = rotationZeroDimensions.y;
            Rect rect3 = rect.equals(displayContent.getBounds()) ? null : new Rect();
            DisplayPolicy displayPolicy = displayContent.getDisplayPolicy();
            int i2 = 0;
            while (i2 < 4) {
                this.mNonDecorInsets[i2] = new Rect();
                this.mStableInsets[i2] = new Rect();
                boolean z2 = i2 == 1 || i2 == 3;
                int i3 = z2 ? displayContent.mBaseDisplayHeight : displayContent.mBaseDisplayWidth;
                int i4 = z2 ? displayContent.mBaseDisplayWidth : displayContent.mBaseDisplayHeight;
                DisplayPolicy.DecorInsets.Info decorInsetsInfo = displayPolicy.getDecorInsetsInfo(i2, i3, i4);
                this.mNonDecorInsets[i2].set(decorInsetsInfo.mNonDecorInsets);
                this.mStableInsets[i2].set(decorInsetsInfo.mConfigInsets);
                if (rect3 != null) {
                    rect3.set(rect);
                    displayContent.rotateBounds(rotation, i2, rect3);
                    updateInsetsForBounds(rect3, i3, i4, this.mNonDecorInsets[i2]);
                    updateInsetsForBounds(rect3, i3, i4, this.mStableInsets[i2]);
                }
                i2++;
            }
        }

        public static Point getRotationZeroDimensions(Rect rect, int i) {
            boolean z = true;
            if (i != 1 && i != 3) {
                z = false;
            }
            int width = rect.width();
            int height = rect.height();
            return z ? new Point(height, width) : new Point(width, height);
        }

        public static void updateInsetsForBounds(Rect rect, int i, int i2, Rect rect2) {
            rect2.left = Math.max(0, rect2.left - rect.left);
            rect2.top = Math.max(0, rect2.top - rect.top);
            rect2.right = Math.max(0, (rect.right - i) + rect2.right);
            rect2.bottom = Math.max(0, (rect.bottom - i2) + rect2.bottom);
        }

        public void getBoundsByRotation(Rect rect, int i) {
            boolean z = true;
            if (i != 1 && i != 3) {
                z = false;
            }
            rect.set(0, 0, z ? this.mHeight : this.mWidth, z ? this.mWidth : this.mHeight);
        }

        public void getFrameByOrientation(Rect rect, int i) {
            int max = Math.max(this.mWidth, this.mHeight);
            int min = Math.min(this.mWidth, this.mHeight);
            boolean z = i == 2;
            int i2 = z ? max : min;
            if (z) {
                max = min;
            }
            rect.set(0, 0, i2, max);
        }

        public void getContainerBounds(Rect rect, Rect rect2, int i, int i2, boolean z, boolean z2) {
            getFrameByOrientation(rect2, i2);
            if (this.mIsFloating) {
                rect.set(rect2);
                return;
            }
            getBoundsByRotation(rect, i);
            int width = rect.width();
            int height = rect.height();
            boolean z3 = (rect2.width() > rect2.height()) != (width > height);
            if (z3 && z2 && z) {
                if (i2 == 2) {
                    float f = width;
                    rect2.bottom = (int) ((f * f) / height);
                    rect2.right = width;
                } else {
                    rect2.bottom = height;
                    float f2 = height;
                    rect2.right = (int) ((f2 * f2) / width);
                }
                rect2.offset(ActivityRecord.getCenterOffset(this.mWidth, rect2.width()), 0);
            }
            rect.set(rect2);
            if (z3) {
                Rect rect3 = this.mNonDecorInsets[i];
                rect2.offset(rect3.left, rect3.top);
                rect.offset(rect3.left, rect3.top);
            } else if (i != -1) {
                TaskFragment.intersectWithInsetsIfFits(rect, rect2, this.mNonDecorInsets[i]);
            }
        }
    }

    /* renamed from: com.android.server.wm.ActivityRecord$AppSaturationInfo */
    /* loaded from: classes2.dex */
    public static class AppSaturationInfo {
        public float[] mMatrix;
        public float[] mTranslation;

        public AppSaturationInfo() {
            this.mMatrix = new float[9];
            this.mTranslation = new float[3];
        }

        public void setSaturation(float[] fArr, float[] fArr2) {
            float[] fArr3 = this.mMatrix;
            System.arraycopy(fArr, 0, fArr3, 0, fArr3.length);
            float[] fArr4 = this.mTranslation;
            System.arraycopy(fArr2, 0, fArr4, 0, fArr4.length);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public RemoteAnimationTarget createRemoteAnimationTarget(RemoteAnimationController.RemoteAnimationRecord remoteAnimationRecord) {
        WindowState findMainWindow = findMainWindow();
        if (this.task == null || findMainWindow == null) {
            return null;
        }
        boolean z = false;
        Rect rect = findMainWindow.getInsetsStateWithVisibilityOverride().calculateInsets(this.task.getBounds(), WindowInsets.Type.systemBars(), false).toRect();
        InsetUtils.addInsets(rect, getLetterboxInsets());
        int i = this.task.mTaskId;
        int mode = remoteAnimationRecord.getMode();
        SurfaceControl surfaceControl = remoteAnimationRecord.mAdapter.mCapturedLeash;
        boolean z2 = !fillsParent();
        Rect rect2 = new Rect();
        int prefixOrderIndex = getPrefixOrderIndex();
        RemoteAnimationController.RemoteAnimationAdapterWrapper remoteAnimationAdapterWrapper = remoteAnimationRecord.mAdapter;
        Point point = remoteAnimationAdapterWrapper.mPosition;
        Rect rect3 = remoteAnimationAdapterWrapper.mLocalBounds;
        Rect rect4 = remoteAnimationAdapterWrapper.mEndBounds;
        WindowConfiguration windowConfiguration = this.task.getWindowConfiguration();
        RemoteAnimationController.RemoteAnimationAdapterWrapper remoteAnimationAdapterWrapper2 = remoteAnimationRecord.mThumbnailAdapter;
        RemoteAnimationTarget remoteAnimationTarget = new RemoteAnimationTarget(i, mode, surfaceControl, z2, rect2, rect, prefixOrderIndex, point, rect3, rect4, windowConfiguration, false, remoteAnimationAdapterWrapper2 != null ? remoteAnimationAdapterWrapper2.mCapturedLeash : null, remoteAnimationRecord.mStartBounds, this.task.getTaskInfo(), checkEnterPictureInPictureAppOpsState());
        remoteAnimationTarget.setShowBackdrop(remoteAnimationRecord.mShowBackdrop);
        StartingData startingData = this.mStartingData;
        if (startingData != null && startingData.hasImeSurface()) {
            z = true;
        }
        remoteAnimationTarget.setWillShowImeOnTarget(z);
        remoteAnimationTarget.hasAnimatingParent = remoteAnimationRecord.hasAnimatingParent();
        return remoteAnimationTarget;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void getAnimationFrames(Rect rect, Rect rect2, Rect rect3, Rect rect4) {
        WindowState findMainWindow = findMainWindow();
        if (findMainWindow == null) {
            return;
        }
        findMainWindow.getAnimationFrames(rect, rect2, rect3, rect4);
    }

    public void setPictureInPictureParams(PictureInPictureParams pictureInPictureParams) {
        this.pictureInPictureArgs.copyOnlySet(pictureInPictureParams);
        adjustPictureInPictureParamsIfNeeded(getBounds());
        getTask().getRootTask().onPictureInPictureParamsChanged();
    }

    public void setShouldDockBigOverlays(boolean z) {
        this.shouldDockBigOverlays = z;
        getTask().getRootTask().onShouldDockBigOverlaysChanged();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isSyncFinished() {
        if (super.isSyncFinished()) {
            DisplayContent displayContent = this.mDisplayContent;
            if (displayContent == null || !displayContent.mUnknownAppVisibilityController.isVisibilityUnknown(this)) {
                if (isVisibleRequested()) {
                    if (isAttached()) {
                        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
                            if (((WindowState) this.mChildren.get(size)).isVisibleRequested()) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return false;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void finishSync(SurfaceControl.Transaction transaction, boolean z) {
        this.mLastAllReadyAtSync = allSyncFinished();
        super.finishSync(transaction, z);
    }

    public Point getMinDimensions() {
        ActivityInfo.WindowLayout windowLayout = this.info.windowLayout;
        if (windowLayout == null) {
            return null;
        }
        return new Point(windowLayout.minWidth, windowLayout.minHeight);
    }

    public final void adjustPictureInPictureParamsIfNeeded(Rect rect) {
        PictureInPictureParams pictureInPictureParams = this.pictureInPictureArgs;
        if (pictureInPictureParams == null || !pictureInPictureParams.hasSourceBoundsHint()) {
            return;
        }
        this.pictureInPictureArgs.getSourceRectHint().offset(rect.left, rect.top);
    }

    public final void applyLocaleOverrideIfNeeded(Configuration configuration) {
        LocaleList emptyLocaleList;
        LocaleList localeList;
        ComponentName componentName;
        Task task;
        boolean z = false;
        if (isEmbedded() || ((task = this.task) != null && task.mAlignActivityLocaleWithTask)) {
            Task task2 = this.task;
            if (task2 != null && (componentName = task2.realActivity) != null && !componentName.getPackageName().equals(this.packageName)) {
                z = true;
            }
            if (z) {
                ActivityTaskManagerInternal.PackageConfig findPackageConfiguration = this.mAtmService.mPackageConfigPersister.findPackageConfiguration(this.task.realActivity.getPackageName(), this.mUserId);
                if (findPackageConfiguration == null || (localeList = findPackageConfiguration.mLocales) == null || localeList.isEmpty()) {
                    emptyLocaleList = LocaleList.getEmptyLocaleList();
                } else {
                    emptyLocaleList = findPackageConfiguration.mLocales;
                }
                configuration.setLocales(emptyLocaleList);
            }
        }
    }

    public boolean shouldSendCompatFakeFocus() {
        return this.mLetterboxUiController.shouldSendFakeFocus() && inMultiWindowMode() && !inPinnedWindowingMode() && !inFreeformWindowingMode();
    }

    public void overrideCustomTransition(boolean z, int i, int i2, int i3) {
        CustomAppTransition customAnimation = getCustomAnimation(z);
        if (customAnimation == null) {
            customAnimation = new CustomAppTransition();
            if (z) {
                this.mCustomOpenTransition = customAnimation;
            } else {
                this.mCustomCloseTransition = customAnimation;
            }
        }
        customAnimation.mEnterAnim = i;
        customAnimation.mExitAnim = i2;
        customAnimation.mBackgroundColor = i3;
    }

    public void clearCustomTransition(boolean z) {
        if (z) {
            this.mCustomOpenTransition = null;
        } else {
            this.mCustomCloseTransition = null;
        }
    }

    public CustomAppTransition getCustomAnimation(boolean z) {
        return z ? this.mCustomOpenTransition : this.mCustomCloseTransition;
    }

    /* renamed from: com.android.server.wm.ActivityRecord$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        public ActivityInfo mActivityInfo;
        public final ActivityTaskManagerService mAtmService;
        public WindowProcessController mCallerApp;
        public boolean mComponentSpecified;
        public Configuration mConfiguration;
        public long mCreateTime;
        public Intent mIntent;
        public String mLaunchedFromFeature;
        public String mLaunchedFromPackage;
        public int mLaunchedFromPid;
        public int mLaunchedFromUid;
        public ActivityOptions mOptions;
        public PersistableBundle mPersistentState;
        public int mRequestCode;
        public String mResolvedType;
        public ActivityRecord mResultTo;
        public String mResultWho;
        public boolean mRootVoiceInteraction;
        public ActivityRecord mSourceRecord;
        public ActivityManager.TaskDescription mTaskDescription;

        public Builder(ActivityTaskManagerService activityTaskManagerService) {
            this.mAtmService = activityTaskManagerService;
        }

        public Builder setCaller(WindowProcessController windowProcessController) {
            this.mCallerApp = windowProcessController;
            return this;
        }

        public Builder setLaunchedFromPid(int i) {
            this.mLaunchedFromPid = i;
            return this;
        }

        public Builder setLaunchedFromUid(int i) {
            this.mLaunchedFromUid = i;
            return this;
        }

        public Builder setLaunchedFromPackage(String str) {
            this.mLaunchedFromPackage = str;
            return this;
        }

        public Builder setLaunchedFromFeature(String str) {
            this.mLaunchedFromFeature = str;
            return this;
        }

        public Builder setIntent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public Builder setResolvedType(String str) {
            this.mResolvedType = str;
            return this;
        }

        public Builder setActivityInfo(ActivityInfo activityInfo) {
            this.mActivityInfo = activityInfo;
            return this;
        }

        public Builder setResultTo(ActivityRecord activityRecord) {
            this.mResultTo = activityRecord;
            return this;
        }

        public Builder setResultWho(String str) {
            this.mResultWho = str;
            return this;
        }

        public Builder setRequestCode(int i) {
            this.mRequestCode = i;
            return this;
        }

        public Builder setComponentSpecified(boolean z) {
            this.mComponentSpecified = z;
            return this;
        }

        public Builder setRootVoiceInteraction(boolean z) {
            this.mRootVoiceInteraction = z;
            return this;
        }

        public Builder setActivityOptions(ActivityOptions activityOptions) {
            this.mOptions = activityOptions;
            return this;
        }

        public Builder setConfiguration(Configuration configuration) {
            this.mConfiguration = configuration;
            return this;
        }

        public Builder setSourceRecord(ActivityRecord activityRecord) {
            this.mSourceRecord = activityRecord;
            return this;
        }

        public final Builder setPersistentState(PersistableBundle persistableBundle) {
            this.mPersistentState = persistableBundle;
            return this;
        }

        public final Builder setTaskDescription(ActivityManager.TaskDescription taskDescription) {
            this.mTaskDescription = taskDescription;
            return this;
        }

        public final Builder setCreateTime(long j) {
            this.mCreateTime = j;
            return this;
        }

        public ActivityRecord build() {
            if (this.mConfiguration == null) {
                this.mConfiguration = this.mAtmService.getConfiguration();
            }
            ActivityTaskManagerService activityTaskManagerService = this.mAtmService;
            return new ActivityRecord(activityTaskManagerService, this.mCallerApp, this.mLaunchedFromPid, this.mLaunchedFromUid, this.mLaunchedFromPackage, this.mLaunchedFromFeature, this.mIntent, this.mResolvedType, this.mActivityInfo, this.mConfiguration, this.mResultTo, this.mResultWho, this.mRequestCode, this.mComponentSpecified, this.mRootVoiceInteraction, activityTaskManagerService.mTaskSupervisor, this.mOptions, this.mSourceRecord, this.mPersistentState, this.mTaskDescription, this.mCreateTime);
        }
    }
}
