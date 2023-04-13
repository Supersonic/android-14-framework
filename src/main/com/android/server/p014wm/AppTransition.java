package com.android.server.p014wm;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.HardwareBuffer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.view.AppTransitionAnimationSpec;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.RemoteAnimationAdapter;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.TransitionAnimation;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.AppTransition */
/* loaded from: classes2.dex */
public class AppTransition implements DumpUtils.Dump {
    public static final ArrayList<Pair<Integer, String>> sFlagToString;
    public IRemoteCallback mAnimationFinishedCallback;
    public final Context mContext;
    public AppTransitionAnimationSpec mDefaultNextAppTransitionAnimationSpec;
    public final int mDefaultWindowAnimationStyleResId;
    public final DisplayContent mDisplayContent;
    public final Handler mHandler;
    public String mLastChangingApp;
    public String mLastClosingApp;
    public String mLastOpeningApp;
    public IAppTransitionAnimationSpecsFuture mNextAppTransitionAnimationsSpecsFuture;
    public boolean mNextAppTransitionAnimationsSpecsPending;
    public int mNextAppTransitionBackgroundColor;
    public IRemoteCallback mNextAppTransitionCallback;
    public int mNextAppTransitionEnter;
    public int mNextAppTransitionExit;
    public IRemoteCallback mNextAppTransitionFutureCallback;
    public int mNextAppTransitionInPlace;
    public boolean mNextAppTransitionIsSync;
    public boolean mNextAppTransitionOverrideRequested;
    public String mNextAppTransitionPackage;
    public boolean mNextAppTransitionScaleUp;
    public boolean mOverrideTaskTransition;
    public RemoteAnimationController mRemoteAnimationController;
    public final WindowManagerService mService;
    @VisibleForTesting
    final TransitionAnimation mTransitionAnimation;
    public int mNextAppTransitionFlags = 0;
    public final ArrayList<Integer> mNextAppTransitionRequests = new ArrayList<>();
    public int mLastUsedAppTransition = -1;
    public int mNextAppTransitionType = 0;
    public final SparseArray<AppTransitionAnimationSpec> mNextAppTransitionAnimationsSpecs = new SparseArray<>();
    public final Rect mTmpRect = new Rect();
    public int mAppTransitionState = 0;
    public final ArrayList<WindowManagerInternal.AppTransitionListener> mListeners = new ArrayList<>();
    public final ExecutorService mDefaultExecutor = Executors.newSingleThreadExecutor();
    public final Runnable mHandleAppTransitionTimeoutRunnable = new Runnable() { // from class: com.android.server.wm.AppTransition$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            AppTransition.this.lambda$new$0();
        }
    };
    public final boolean mGridLayoutRecentsEnabled = SystemProperties.getBoolean("ro.recents.grid", false);

    public static boolean isActivityTransitOld(int i) {
        return i == 6 || i == 7 || i == 18;
    }

    public static boolean isChangeTransitOld(int i) {
        return i == 27 || i == 30;
    }

    public static boolean isClosingTransitOld(int i) {
        return i == 7 || i == 9 || i == 12 || i == 15 || i == 25 || i == 26;
    }

    public static boolean isKeyguardGoingAwayTransitOld(int i) {
        return i == 20 || i == 21;
    }

    public static boolean isKeyguardOccludeTransitOld(int i) {
        return i == 22 || i == 33 || i == 23;
    }

    public static boolean isKeyguardTransit(int i) {
        return i == 7 || i == 8 || i == 9;
    }

    public static boolean isNormalTransit(int i) {
        return i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static boolean isTaskCloseTransitOld(int i) {
        return i == 9 || i == 11;
    }

    public static boolean isTaskFragmentTransitOld(int i) {
        return i == 28 || i == 29 || i == 30;
    }

    public static boolean isTaskOpenTransitOld(int i) {
        return i == 8 || i == 16 || i == 10;
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x0076, code lost:
        if (r10 != false) goto L55;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0079, code lost:
        if (r10 != false) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x007c, code lost:
        if (r10 != false) goto L55;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x007f, code lost:
        r3 = 7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0082, code lost:
        if (r10 != false) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0085, code lost:
        r0 = 5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0087, code lost:
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:?, code lost:
        return r3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int mapOpenCloseTransitTypes(int i, boolean z) {
        int i2;
        int i3 = 4;
        if (i != 24) {
            int i4 = 6;
            if (i != 25) {
                if (i != 28) {
                    if (i != 29) {
                        if (i == 31) {
                            return z ? 28 : 29;
                        }
                        if (i != 32) {
                            switch (i) {
                                case 6:
                                    break;
                                case 7:
                                    break;
                                case 8:
                                    if (!z) {
                                        i2 = 9;
                                        break;
                                    } else {
                                        i2 = 8;
                                        break;
                                    }
                                case 9:
                                    if (!z) {
                                        i2 = 11;
                                        break;
                                    } else {
                                        i2 = 10;
                                        break;
                                    }
                                case 10:
                                    if (!z) {
                                        i2 = 13;
                                        break;
                                    } else {
                                        i2 = 12;
                                        break;
                                    }
                                case 11:
                                    if (!z) {
                                        i2 = 15;
                                        break;
                                    } else {
                                        i2 = 14;
                                        break;
                                    }
                                case 12:
                                    if (!z) {
                                        i2 = 19;
                                        break;
                                    } else {
                                        i2 = 18;
                                        break;
                                    }
                                case 13:
                                    if (!z) {
                                        i2 = 17;
                                        break;
                                    } else {
                                        i2 = 16;
                                        break;
                                    }
                                case 14:
                                    if (!z) {
                                        i2 = 21;
                                        break;
                                    } else {
                                        i2 = 20;
                                        break;
                                    }
                                case 15:
                                    if (!z) {
                                        i2 = 23;
                                        break;
                                    } else {
                                        i2 = 22;
                                        break;
                                    }
                                case 16:
                                    return z ? 25 : 24;
                                default:
                                    return 0;
                            }
                        } else if (z) {
                            return 0;
                        } else {
                            i2 = 27;
                        }
                        return i2;
                    }
                }
            }
        }
    }

    public void registerKeygaurdExitAnimationStartListener(WindowManagerInternal.KeyguardExitAnimationStartListener keyguardExitAnimationStartListener) {
    }

    public AppTransition(Context context, WindowManagerService windowManagerService, DisplayContent displayContent) {
        this.mContext = context;
        this.mService = windowManagerService;
        this.mHandler = new Handler(windowManagerService.f1164mH.getLooper());
        this.mDisplayContent = displayContent;
        this.mTransitionAnimation = new TransitionAnimation(context, ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_ANIM), StartingSurfaceController.TAG);
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(R.styleable.Window);
        this.mDefaultWindowAnimationStyleResId = obtainStyledAttributes.getResourceId(8, 0);
        obtainStyledAttributes.recycle();
    }

    public boolean isTransitionSet() {
        return !this.mNextAppTransitionRequests.isEmpty();
    }

    public boolean isUnoccluding() {
        return this.mNextAppTransitionRequests.contains(9);
    }

    public boolean transferFrom(AppTransition appTransition) {
        this.mNextAppTransitionRequests.addAll(appTransition.mNextAppTransitionRequests);
        return prepare();
    }

    public void setLastAppTransition(int i, ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityRecord activityRecord3) {
        this.mLastUsedAppTransition = i;
        this.mLastOpeningApp = "" + activityRecord;
        this.mLastClosingApp = "" + activityRecord2;
        this.mLastChangingApp = "" + activityRecord3;
    }

    public boolean isReady() {
        int i = this.mAppTransitionState;
        return i == 1 || i == 3;
    }

    public void setReady() {
        setAppTransitionState(1);
        fetchAppTransitionSpecsFromFuture();
    }

    public boolean isRunning() {
        return this.mAppTransitionState == 2;
    }

    public void setIdle() {
        setAppTransitionState(0);
    }

    public boolean isIdle() {
        return this.mAppTransitionState == 0;
    }

    public boolean isTimeout() {
        return this.mAppTransitionState == 3;
    }

    public void setTimeout() {
        setAppTransitionState(3);
    }

    public Animation getNextAppRequestedAnimation(boolean z) {
        Animation loadAppTransitionAnimation = this.mTransitionAnimation.loadAppTransitionAnimation(this.mNextAppTransitionPackage, z ? this.mNextAppTransitionEnter : this.mNextAppTransitionExit);
        int i = this.mNextAppTransitionBackgroundColor;
        if (i != 0 && loadAppTransitionAnimation != null) {
            loadAppTransitionAnimation.setBackdropColor(i);
        }
        return loadAppTransitionAnimation;
    }

    public int getNextAppTransitionBackgroundColor() {
        return this.mNextAppTransitionBackgroundColor;
    }

    @VisibleForTesting
    public boolean isNextAppTransitionOverrideRequested() {
        return this.mNextAppTransitionOverrideRequested;
    }

    public HardwareBuffer getAppTransitionThumbnailHeader(WindowContainer windowContainer) {
        AppTransitionAnimationSpec appTransitionAnimationSpec = this.mNextAppTransitionAnimationsSpecs.get(windowContainer.hashCode());
        if (appTransitionAnimationSpec == null) {
            appTransitionAnimationSpec = this.mDefaultNextAppTransitionAnimationSpec;
        }
        if (appTransitionAnimationSpec != null) {
            return appTransitionAnimationSpec.buffer;
        }
        return null;
    }

    public boolean isNextAppTransitionThumbnailUp() {
        int i = this.mNextAppTransitionType;
        return i == 3 || i == 5;
    }

    public boolean isNextAppTransitionThumbnailDown() {
        int i = this.mNextAppTransitionType;
        return i == 4 || i == 6;
    }

    public boolean isNextAppTransitionOpenCrossProfileApps() {
        return this.mNextAppTransitionType == 9;
    }

    public boolean isFetchingAppTransitionsSpecs() {
        return this.mNextAppTransitionAnimationsSpecsPending;
    }

    public final boolean prepare() {
        if (isRunning()) {
            return false;
        }
        setAppTransitionState(0);
        notifyAppTransitionPendingLocked();
        return true;
    }

    public int goodToGo(int i, ActivityRecord activityRecord) {
        long uptimeMillis;
        this.mNextAppTransitionFlags = 0;
        this.mNextAppTransitionRequests.clear();
        setAppTransitionState(2);
        WindowContainer animatingContainer = activityRecord != null ? activityRecord.getAnimatingContainer() : null;
        AnimationAdapter animation = animatingContainer != null ? animatingContainer.getAnimation() : null;
        if (animation != null) {
            uptimeMillis = animation.getStatusBarTransitionsStartTime();
        } else {
            uptimeMillis = SystemClock.uptimeMillis();
        }
        int notifyAppTransitionStartingLocked = notifyAppTransitionStartingLocked(uptimeMillis, 120L);
        RemoteAnimationController remoteAnimationController = this.mRemoteAnimationController;
        if (remoteAnimationController != null) {
            remoteAnimationController.goodToGo(i);
        } else if ((isTaskOpenTransitOld(i) || i == 12) && animation != null && this.mDisplayContent.getDisplayPolicy().shouldAttachNavBarToAppDuringTransition() && this.mService.getRecentsAnimationController() == null) {
            new NavBarFadeAnimationController(this.mDisplayContent).fadeOutAndInSequentially(animation.getDurationHint(), null, activityRecord.getSurfaceControl());
        }
        return notifyAppTransitionStartingLocked;
    }

    public void clear() {
        clear(true);
    }

    public final void clear(boolean z) {
        this.mNextAppTransitionType = 0;
        this.mNextAppTransitionOverrideRequested = false;
        this.mNextAppTransitionAnimationsSpecs.clear();
        this.mRemoteAnimationController = null;
        this.mNextAppTransitionAnimationsSpecsFuture = null;
        this.mDefaultNextAppTransitionAnimationSpec = null;
        this.mAnimationFinishedCallback = null;
        this.mOverrideTaskTransition = false;
        this.mNextAppTransitionIsSync = false;
        if (z) {
            this.mNextAppTransitionPackage = null;
            this.mNextAppTransitionEnter = 0;
            this.mNextAppTransitionExit = 0;
            this.mNextAppTransitionBackgroundColor = 0;
        }
    }

    public void freeze() {
        boolean contains = this.mNextAppTransitionRequests.contains(7);
        RemoteAnimationController remoteAnimationController = this.mRemoteAnimationController;
        if (remoteAnimationController != null) {
            remoteAnimationController.cancelAnimation("freeze");
        }
        this.mNextAppTransitionRequests.clear();
        clear();
        setReady();
        notifyAppTransitionCancelledLocked(contains);
    }

    public final void setAppTransitionState(int i) {
        this.mAppTransitionState = i;
        updateBooster();
    }

    public void updateBooster() {
        WindowManagerService.sThreadPriorityBooster.setAppTransitionRunning(needsBoosting());
    }

    public final boolean needsBoosting() {
        int i;
        return !this.mNextAppTransitionRequests.isEmpty() || (i = this.mAppTransitionState) == 1 || i == 2 || (this.mService.getRecentsAnimationController() != null);
    }

    public void registerListenerLocked(WindowManagerInternal.AppTransitionListener appTransitionListener) {
        this.mListeners.add(appTransitionListener);
    }

    public void unregisterListener(WindowManagerInternal.AppTransitionListener appTransitionListener) {
        this.mListeners.remove(appTransitionListener);
    }

    public void notifyAppTransitionFinishedLocked(IBinder iBinder) {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onAppTransitionFinishedLocked(iBinder);
        }
    }

    public final void notifyAppTransitionPendingLocked() {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onAppTransitionPendingLocked();
        }
    }

    public final void notifyAppTransitionCancelledLocked(boolean z) {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onAppTransitionCancelledLocked(z);
        }
    }

    public final void notifyAppTransitionTimeoutLocked() {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onAppTransitionTimeoutLocked();
        }
    }

    public final int notifyAppTransitionStartingLocked(long j, long j2) {
        int i = 0;
        for (int i2 = 0; i2 < this.mListeners.size(); i2++) {
            i |= this.mListeners.get(i2).onAppTransitionStartingLocked(j, j2);
        }
        return i;
    }

    @VisibleForTesting
    public int getDefaultWindowAnimationStyleResId() {
        return this.mDefaultWindowAnimationStyleResId;
    }

    @VisibleForTesting
    public int getAnimationStyleResId(WindowManager.LayoutParams layoutParams) {
        return this.mTransitionAnimation.getAnimationStyleResId(layoutParams);
    }

    @VisibleForTesting
    public Animation loadAnimationSafely(Context context, int i) {
        return TransitionAnimation.loadAnimationSafely(context, i, StartingSurfaceController.TAG);
    }

    public Animation loadAnimationAttr(WindowManager.LayoutParams layoutParams, int i, int i2) {
        return this.mTransitionAnimation.loadAnimationAttr(layoutParams, i, i2);
    }

    public final void getDefaultNextAppTransitionStartRect(Rect rect) {
        Rect rect2;
        AppTransitionAnimationSpec appTransitionAnimationSpec = this.mDefaultNextAppTransitionAnimationSpec;
        if (appTransitionAnimationSpec == null || (rect2 = appTransitionAnimationSpec.rect) == null) {
            Slog.e(StartingSurfaceController.TAG, "Starting rect for app requested, but none available", new Throwable());
            rect.setEmpty();
            return;
        }
        rect.set(rect2);
    }

    public final void putDefaultNextAppTransitionCoordinates(int i, int i2, int i3, int i4, HardwareBuffer hardwareBuffer) {
        this.mDefaultNextAppTransitionAnimationSpec = new AppTransitionAnimationSpec(-1, hardwareBuffer, new Rect(i, i2, i3 + i, i4 + i2));
    }

    public HardwareBuffer createCrossProfileAppsThumbnail(Drawable drawable, Rect rect) {
        return this.mTransitionAnimation.createCrossProfileAppsThumbnail(drawable, rect);
    }

    public Animation createCrossProfileAppsThumbnailAnimationLocked(Rect rect) {
        return this.mTransitionAnimation.createCrossProfileAppsThumbnailAnimationLocked(rect);
    }

    public Animation createThumbnailAspectScaleAnimationLocked(Rect rect, Rect rect2, HardwareBuffer hardwareBuffer, WindowContainer windowContainer, int i) {
        AppTransitionAnimationSpec appTransitionAnimationSpec = this.mNextAppTransitionAnimationsSpecs.get(windowContainer.hashCode());
        TransitionAnimation transitionAnimation = this.mTransitionAnimation;
        Rect rect3 = appTransitionAnimationSpec != null ? appTransitionAnimationSpec.rect : null;
        AppTransitionAnimationSpec appTransitionAnimationSpec2 = this.mDefaultNextAppTransitionAnimationSpec;
        return transitionAnimation.createThumbnailAspectScaleAnimationLocked(rect, rect2, hardwareBuffer, i, rect3, appTransitionAnimationSpec2 != null ? appTransitionAnimationSpec2.rect : null, this.mNextAppTransitionScaleUp);
    }

    public boolean canSkipFirstFrame() {
        int i = this.mNextAppTransitionType;
        return (i == 1 || this.mNextAppTransitionOverrideRequested || i == 7 || i == 8 || this.mNextAppTransitionRequests.contains(7)) ? false : true;
    }

    public RemoteAnimationController getRemoteAnimationController() {
        return this.mRemoteAnimationController;
    }

    public Animation loadAnimation(WindowManager.LayoutParams layoutParams, int i, boolean z, int i2, int i3, Rect rect, Rect rect2, Rect rect3, Rect rect4, Rect rect5, boolean z2, boolean z3, WindowContainer windowContainer) {
        Animation createThumbnailEnterExitAnimationLockedCompat;
        Rect rect6;
        Rect rect7;
        boolean canCustomizeAppTransition = windowContainer.canCustomizeAppTransition();
        Animation animation = null;
        if (this.mNextAppTransitionOverrideRequested) {
            if (canCustomizeAppTransition || this.mOverrideTaskTransition) {
                this.mNextAppTransitionType = 1;
            } else if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                ProtoLogImpl.e(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, 2079410261, 0, (String) null, (Object[]) null);
            }
        }
        if (isKeyguardGoingAwayTransitOld(i) && z) {
            animation = this.mTransitionAnimation.loadKeyguardExitAnimation(this.mNextAppTransitionFlags, i == 21);
        } else if (i != 22 && i != 33) {
            if (i == 23 && !z) {
                animation = this.mTransitionAnimation.loadKeyguardUnoccludeAnimation();
            } else if (i != 26) {
                if (z2 && (i == 6 || i == 8 || i == 10)) {
                    createThumbnailEnterExitAnimationLockedCompat = this.mTransitionAnimation.loadVoiceActivityOpenAnimation(z);
                    if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, 508887531, 48, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), String.valueOf(appTransitionOldToString(i)), Boolean.valueOf(z), String.valueOf(Debug.getCallers(3))});
                    }
                } else if (z2 && (i == 7 || i == 9 || i == 11)) {
                    createThumbnailEnterExitAnimationLockedCompat = this.mTransitionAnimation.loadVoiceActivityExitAnimation(z);
                    if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, 508887531, 48, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), String.valueOf(appTransitionOldToString(i)), Boolean.valueOf(z), String.valueOf(Debug.getCallers(3))});
                    }
                } else if (i == 18) {
                    TransitionAnimation transitionAnimation = this.mTransitionAnimation;
                    AppTransitionAnimationSpec appTransitionAnimationSpec = this.mDefaultNextAppTransitionAnimationSpec;
                    if (appTransitionAnimationSpec != null) {
                        rect7 = appTransitionAnimationSpec.rect;
                        rect6 = rect3;
                    } else {
                        rect6 = rect3;
                        rect7 = null;
                    }
                    createThumbnailEnterExitAnimationLockedCompat = transitionAnimation.createRelaunchAnimation(rect, rect6, rect7);
                    if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, -1800899273, 0, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), String.valueOf(appTransitionOldToString(i)), String.valueOf(Debug.getCallers(3))});
                    }
                } else {
                    int i4 = this.mNextAppTransitionType;
                    if (i4 == 1) {
                        createThumbnailEnterExitAnimationLockedCompat = getNextAppRequestedAnimation(z);
                        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, -519504830, 48, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), String.valueOf(appTransitionOldToString(i)), Boolean.valueOf(z), String.valueOf(Debug.getCallers(3))});
                        }
                    } else if (i4 == 7) {
                        createThumbnailEnterExitAnimationLockedCompat = this.mTransitionAnimation.loadAppTransitionAnimation(this.mNextAppTransitionPackage, this.mNextAppTransitionInPlace);
                        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, 1457990604, 0, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), String.valueOf(appTransitionOldToString(i)), String.valueOf(Debug.getCallers(3))});
                        }
                    } else if (i4 == 8) {
                        TransitionAnimation transitionAnimation2 = this.mTransitionAnimation;
                        AppTransitionAnimationSpec appTransitionAnimationSpec2 = this.mDefaultNextAppTransitionAnimationSpec;
                        createThumbnailEnterExitAnimationLockedCompat = transitionAnimation2.createClipRevealAnimationLockedCompat(i, z, rect, rect2, appTransitionAnimationSpec2 != null ? appTransitionAnimationSpec2.rect : null);
                        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, 274773837, 0, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), String.valueOf(appTransitionOldToString(i)), String.valueOf(Debug.getCallers(3))});
                        }
                    } else if (i4 == 2) {
                        TransitionAnimation transitionAnimation3 = this.mTransitionAnimation;
                        AppTransitionAnimationSpec appTransitionAnimationSpec3 = this.mDefaultNextAppTransitionAnimationSpec;
                        createThumbnailEnterExitAnimationLockedCompat = transitionAnimation3.createScaleUpAnimationLockedCompat(i, z, rect, appTransitionAnimationSpec3 != null ? appTransitionAnimationSpec3.rect : null);
                        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, 2028163120, 0, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), String.valueOf(appTransitionOldToString(i)), String.valueOf(z), String.valueOf(Debug.getCallers(3))});
                        }
                    } else if (i4 == 3 || i4 == 4) {
                        this.mNextAppTransitionScaleUp = i4 == 3;
                        HardwareBuffer appTransitionThumbnailHeader = getAppTransitionThumbnailHeader(windowContainer);
                        TransitionAnimation transitionAnimation4 = this.mTransitionAnimation;
                        boolean z4 = this.mNextAppTransitionScaleUp;
                        AppTransitionAnimationSpec appTransitionAnimationSpec4 = this.mDefaultNextAppTransitionAnimationSpec;
                        createThumbnailEnterExitAnimationLockedCompat = transitionAnimation4.createThumbnailEnterExitAnimationLockedCompat(z, z4, rect, i, appTransitionThumbnailHeader, appTransitionAnimationSpec4 != null ? appTransitionAnimationSpec4.rect : null);
                        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, -1872288685, (int) FrameworkStatsLog.f392xcd34d435, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), this.mNextAppTransitionScaleUp ? "ANIM_THUMBNAIL_SCALE_UP" : "ANIM_THUMBNAIL_SCALE_DOWN", String.valueOf(appTransitionOldToString(i)), Boolean.valueOf(z), String.valueOf(Debug.getCallers(3))});
                        }
                    } else if (i4 == 5 || i4 == 6) {
                        this.mNextAppTransitionScaleUp = i4 == 5;
                        AppTransitionAnimationSpec appTransitionAnimationSpec5 = this.mNextAppTransitionAnimationsSpecs.get(windowContainer.hashCode());
                        TransitionAnimation transitionAnimation5 = this.mTransitionAnimation;
                        boolean z5 = this.mNextAppTransitionScaleUp;
                        Rect rect8 = appTransitionAnimationSpec5 != null ? appTransitionAnimationSpec5.rect : null;
                        AppTransitionAnimationSpec appTransitionAnimationSpec6 = this.mDefaultNextAppTransitionAnimationSpec;
                        createThumbnailEnterExitAnimationLockedCompat = transitionAnimation5.createAspectScaledThumbnailEnterExitAnimationLocked(z, z5, i3, i, rect, rect3, rect4, rect5, z3, rect8, appTransitionAnimationSpec6 != null ? appTransitionAnimationSpec6.rect : null);
                        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, -1872288685, (int) FrameworkStatsLog.f392xcd34d435, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), this.mNextAppTransitionScaleUp ? "ANIM_THUMBNAIL_ASPECT_SCALE_UP" : "ANIM_THUMBNAIL_ASPECT_SCALE_DOWN", String.valueOf(appTransitionOldToString(i)), Boolean.valueOf(z), String.valueOf(Debug.getCallers(3))});
                        }
                    } else if (i4 == 9 && z) {
                        createThumbnailEnterExitAnimationLockedCompat = this.mTransitionAnimation.loadCrossProfileAppEnterAnimation();
                        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, 1589610525, 0, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), String.valueOf(appTransitionOldToString(i)), String.valueOf(Debug.getCallers(3))});
                        }
                    } else if (isChangeTransitOld(i)) {
                        createThumbnailEnterExitAnimationLockedCompat = new AlphaAnimation(1.0f, 1.0f);
                        createThumbnailEnterExitAnimationLockedCompat.setDuration(336L);
                        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, -1862269827, 48, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), String.valueOf(appTransitionOldToString(i)), Boolean.valueOf(z), String.valueOf(Debug.getCallers(3))});
                        }
                    } else {
                        int mapOpenCloseTransitTypes = mapOpenCloseTransitTypes(i, z);
                        if (mapOpenCloseTransitTypes != 0) {
                            ActivityRecord.CustomAppTransition customAppTransition = getCustomAppTransition(mapOpenCloseTransitTypes, windowContainer);
                            if (customAppTransition != null) {
                                createThumbnailEnterExitAnimationLockedCompat = loadCustomActivityAnimation(customAppTransition, z, windowContainer);
                            } else if (canCustomizeAppTransition) {
                                createThumbnailEnterExitAnimationLockedCompat = loadAnimationAttr(layoutParams, mapOpenCloseTransitTypes, i);
                            } else {
                                createThumbnailEnterExitAnimationLockedCompat = this.mTransitionAnimation.loadDefaultAnimationAttr(mapOpenCloseTransitTypes, i);
                            }
                        } else {
                            createThumbnailEnterExitAnimationLockedCompat = null;
                        }
                        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_ANIM_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM, -57572004, 964, (String) null, new Object[]{String.valueOf(createThumbnailEnterExitAnimationLockedCompat), Long.valueOf(mapOpenCloseTransitTypes), String.valueOf(appTransitionOldToString(i)), Boolean.valueOf(z), Boolean.valueOf(canCustomizeAppTransition), String.valueOf(Debug.getCallers(3))});
                        }
                    }
                }
                animation = createThumbnailEnterExitAnimationLockedCompat;
            }
        }
        setAppTransitionFinishedCallbackIfNeeded(animation);
        return animation;
    }

    public ActivityRecord.CustomAppTransition getCustomAppTransition(int i, WindowContainer windowContainer) {
        ActivityRecord asActivityRecord = windowContainer.asActivityRecord();
        if (asActivityRecord == null) {
            return null;
        }
        if ((i == 5 || i == 6) && (asActivityRecord = asActivityRecord.getTask().getActivityAbove(asActivityRecord)) == null) {
            return null;
        }
        if (i == 4 || i == 5) {
            return asActivityRecord.getCustomAnimation(true);
        }
        if (i == 6 || i == 7) {
            return asActivityRecord.getCustomAnimation(false);
        }
        return null;
    }

    public final Animation loadCustomActivityAnimation(ActivityRecord.CustomAppTransition customAppTransition, boolean z, WindowContainer windowContainer) {
        int i;
        Animation loadAppTransitionAnimation = this.mTransitionAnimation.loadAppTransitionAnimation(windowContainer.asActivityRecord().packageName, z ? customAppTransition.mEnterAnim : customAppTransition.mExitAnim);
        if (loadAppTransitionAnimation != null && (i = customAppTransition.mBackgroundColor) != 0) {
            loadAppTransitionAnimation.setBackdropColor(i);
            loadAppTransitionAnimation.setShowBackdrop(true);
        }
        return loadAppTransitionAnimation;
    }

    public int getAppRootTaskClipMode() {
        return (this.mNextAppTransitionRequests.contains(5) || this.mNextAppTransitionRequests.contains(7) || this.mNextAppTransitionType == 8) ? 1 : 0;
    }

    public int getTransitFlags() {
        return this.mNextAppTransitionFlags;
    }

    public void postAnimationCallback() {
        if (this.mNextAppTransitionCallback != null) {
            this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.wm.AppTransition$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AppTransition.doAnimationCallback((IRemoteCallback) obj);
                }
            }, this.mNextAppTransitionCallback));
            this.mNextAppTransitionCallback = null;
        }
    }

    public void overridePendingAppTransition(String str, int i, int i2, int i3, IRemoteCallback iRemoteCallback, IRemoteCallback iRemoteCallback2, boolean z) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionOverrideRequested = true;
            this.mNextAppTransitionPackage = str;
            this.mNextAppTransitionEnter = i;
            this.mNextAppTransitionExit = i2;
            this.mNextAppTransitionBackgroundColor = i3;
            postAnimationCallback();
            this.mNextAppTransitionCallback = iRemoteCallback;
            this.mAnimationFinishedCallback = iRemoteCallback2;
            this.mOverrideTaskTransition = z;
        }
    }

    public void overridePendingAppTransitionScaleUp(int i, int i2, int i3, int i4) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = 2;
            putDefaultNextAppTransitionCoordinates(i, i2, i3, i4, null);
            postAnimationCallback();
        }
    }

    public void overridePendingAppTransitionClipReveal(int i, int i2, int i3, int i4) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = 8;
            putDefaultNextAppTransitionCoordinates(i, i2, i3, i4, null);
            postAnimationCallback();
        }
    }

    public void overridePendingAppTransitionThumb(HardwareBuffer hardwareBuffer, int i, int i2, IRemoteCallback iRemoteCallback, boolean z) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = z ? 3 : 4;
            this.mNextAppTransitionScaleUp = z;
            putDefaultNextAppTransitionCoordinates(i, i2, 0, 0, hardwareBuffer);
            postAnimationCallback();
            this.mNextAppTransitionCallback = iRemoteCallback;
        }
    }

    public void overridePendingAppTransitionAspectScaledThumb(HardwareBuffer hardwareBuffer, int i, int i2, int i3, int i4, IRemoteCallback iRemoteCallback, boolean z) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = z ? 5 : 6;
            this.mNextAppTransitionScaleUp = z;
            putDefaultNextAppTransitionCoordinates(i, i2, i3, i4, hardwareBuffer);
            postAnimationCallback();
            this.mNextAppTransitionCallback = iRemoteCallback;
        }
    }

    public void overridePendingAppTransitionMultiThumb(AppTransitionAnimationSpec[] appTransitionAnimationSpecArr, IRemoteCallback iRemoteCallback, IRemoteCallback iRemoteCallback2, boolean z) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = z ? 5 : 6;
            this.mNextAppTransitionScaleUp = z;
            if (appTransitionAnimationSpecArr != null) {
                for (int i = 0; i < appTransitionAnimationSpecArr.length; i++) {
                    AppTransitionAnimationSpec appTransitionAnimationSpec = appTransitionAnimationSpecArr[i];
                    if (appTransitionAnimationSpec != null) {
                        Predicate<Task> obtainPredicate = PooledLambda.obtainPredicate(new AppTransition$$ExternalSyntheticLambda2(), PooledLambda.__(Task.class), Integer.valueOf(appTransitionAnimationSpec.taskId));
                        Task task = this.mDisplayContent.getTask(obtainPredicate);
                        obtainPredicate.recycle();
                        if (task != null) {
                            this.mNextAppTransitionAnimationsSpecs.put(task.hashCode(), appTransitionAnimationSpec);
                            if (i == 0) {
                                Rect rect = appTransitionAnimationSpec.rect;
                                putDefaultNextAppTransitionCoordinates(rect.left, rect.top, rect.width(), rect.height(), appTransitionAnimationSpec.buffer);
                            }
                        }
                    }
                }
            }
            postAnimationCallback();
            this.mNextAppTransitionCallback = iRemoteCallback;
            this.mAnimationFinishedCallback = iRemoteCallback2;
        }
    }

    public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture iAppTransitionAnimationSpecsFuture, IRemoteCallback iRemoteCallback, boolean z) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = z ? 5 : 6;
            this.mNextAppTransitionAnimationsSpecsFuture = iAppTransitionAnimationSpecsFuture;
            this.mNextAppTransitionScaleUp = z;
            this.mNextAppTransitionFutureCallback = iRemoteCallback;
            if (isReady()) {
                fetchAppTransitionSpecsFromFuture();
            }
        }
    }

    public void overridePendingAppTransitionRemote(RemoteAnimationAdapter remoteAnimationAdapter) {
        overridePendingAppTransitionRemote(remoteAnimationAdapter, false, false);
    }

    public void overridePendingAppTransitionRemote(RemoteAnimationAdapter remoteAnimationAdapter, boolean z, boolean z2) {
        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
            boolean isTransitionSet = isTransitionSet();
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, 1448683958, 3, (String) null, new Object[]{Boolean.valueOf(isTransitionSet), String.valueOf(remoteAnimationAdapter)});
        }
        if (!isTransitionSet() || this.mNextAppTransitionIsSync) {
            return;
        }
        clear(!z2);
        this.mNextAppTransitionType = 10;
        this.mRemoteAnimationController = new RemoteAnimationController(this.mService, this.mDisplayContent, remoteAnimationAdapter, this.mHandler, z2);
        this.mNextAppTransitionIsSync = z;
    }

    public void overridePendingAppTransitionStartCrossProfileApps() {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = 9;
            postAnimationCallback();
        }
    }

    public final boolean canOverridePendingAppTransition() {
        return isTransitionSet() && this.mNextAppTransitionType != 10;
    }

    public final void fetchAppTransitionSpecsFromFuture() {
        final IAppTransitionAnimationSpecsFuture iAppTransitionAnimationSpecsFuture = this.mNextAppTransitionAnimationsSpecsFuture;
        if (iAppTransitionAnimationSpecsFuture != null) {
            this.mNextAppTransitionAnimationsSpecsPending = true;
            this.mNextAppTransitionAnimationsSpecsFuture = null;
            this.mDefaultExecutor.execute(new Runnable() { // from class: com.android.server.wm.AppTransition$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AppTransition.this.lambda$fetchAppTransitionSpecsFromFuture$1(iAppTransitionAnimationSpecsFuture);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchAppTransitionSpecsFromFuture$1(IAppTransitionAnimationSpecsFuture iAppTransitionAnimationSpecsFuture) {
        AppTransitionAnimationSpec[] appTransitionAnimationSpecArr;
        try {
            Binder.allowBlocking(iAppTransitionAnimationSpecsFuture.asBinder());
            appTransitionAnimationSpecArr = iAppTransitionAnimationSpecsFuture.get();
        } catch (RemoteException e) {
            Slog.w(StartingSurfaceController.TAG, "Failed to fetch app transition specs: " + e);
            appTransitionAnimationSpecArr = null;
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mNextAppTransitionAnimationsSpecsPending = false;
                overridePendingAppTransitionMultiThumb(appTransitionAnimationSpecArr, this.mNextAppTransitionFutureCallback, null, this.mNextAppTransitionScaleUp);
                this.mNextAppTransitionFutureCallback = null;
                this.mService.requestTraversal();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("mNextAppTransitionRequests=[");
        Iterator<Integer> it = this.mNextAppTransitionRequests.iterator();
        boolean z = false;
        while (it.hasNext()) {
            Integer next = it.next();
            if (z) {
                sb.append(", ");
            }
            sb.append(appTransitionToString(next.intValue()));
            z = true;
        }
        sb.append("]");
        sb.append(", mNextAppTransitionFlags=" + appTransitionFlagsToString(this.mNextAppTransitionFlags));
        return sb.toString();
    }

    public static String appTransitionOldToString(int i) {
        switch (i) {
            case -1:
                return "TRANSIT_OLD_UNSET";
            case 0:
                return "TRANSIT_OLD_NONE";
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 17:
            case 19:
            case 27:
            default:
                return "<UNKNOWN: " + i + ">";
            case 6:
                return "TRANSIT_OLD_ACTIVITY_OPEN";
            case 7:
                return "TRANSIT_OLD_ACTIVITY_CLOSE";
            case 8:
                return "TRANSIT_OLD_TASK_OPEN";
            case 9:
                return "TRANSIT_OLD_TASK_CLOSE";
            case 10:
                return "TRANSIT_OLD_TASK_TO_FRONT";
            case 11:
                return "TRANSIT_OLD_TASK_TO_BACK";
            case 12:
                return "TRANSIT_OLD_WALLPAPER_CLOSE";
            case 13:
                return "TRANSIT_OLD_WALLPAPER_OPEN";
            case 14:
                return "TRANSIT_OLD_WALLPAPER_INTRA_OPEN";
            case 15:
                return "TRANSIT_OLD_WALLPAPER_INTRA_CLOSE";
            case 16:
                return "TRANSIT_OLD_TASK_OPEN_BEHIND";
            case 18:
                return "TRANSIT_OLD_ACTIVITY_RELAUNCH";
            case 20:
                return "TRANSIT_OLD_KEYGUARD_GOING_AWAY";
            case 21:
                return "TRANSIT_OLD_KEYGUARD_GOING_AWAY_ON_WALLPAPER";
            case 22:
                return "TRANSIT_OLD_KEYGUARD_OCCLUDE";
            case 23:
                return "TRANSIT_OLD_KEYGUARD_UNOCCLUDE";
            case 24:
                return "TRANSIT_OLD_TRANSLUCENT_ACTIVITY_OPEN";
            case 25:
                return "TRANSIT_OLD_TRANSLUCENT_ACTIVITY_CLOSE";
            case 26:
                return "TRANSIT_OLD_CRASHING_ACTIVITY_CLOSE";
            case 28:
                return "TRANSIT_OLD_TASK_FRAGMENT_OPEN";
            case 29:
                return "TRANSIT_OLD_TASK_FRAGMENT_CLOSE";
            case 30:
                return "TRANSIT_OLD_TASK_FRAGMENT_CHANGE";
            case 31:
                return "TRANSIT_OLD_DREAM_ACTIVITY_OPEN";
            case 32:
                return "TRANSIT_OLD_DREAM_ACTIVITY_CLOSE";
            case 33:
                return "TRANSIT_OLD_KEYGUARD_OCCLUDE_BY_DREAM";
        }
    }

    public static String appTransitionToString(int i) {
        switch (i) {
            case 0:
                return "TRANSIT_NONE";
            case 1:
                return "TRANSIT_OPEN";
            case 2:
                return "TRANSIT_CLOSE";
            case 3:
                return "TRANSIT_TO_FRONT";
            case 4:
                return "TRANSIT_TO_BACK";
            case 5:
                return "TRANSIT_RELAUNCH";
            case 6:
                return "TRANSIT_CHANGE";
            case 7:
                return "TRANSIT_KEYGUARD_GOING_AWAY";
            case 8:
                return "TRANSIT_KEYGUARD_OCCLUDE";
            case 9:
                return "TRANSIT_KEYGUARD_UNOCCLUDE";
            default:
                return "<UNKNOWN: " + i + ">";
        }
    }

    public final String appStateToString() {
        int i = this.mAppTransitionState;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "unknown state=" + this.mAppTransitionState;
                    }
                    return "APP_STATE_TIMEOUT";
                }
                return "APP_STATE_RUNNING";
            }
            return "APP_STATE_READY";
        }
        return "APP_STATE_IDLE";
    }

    public final String transitTypeToString() {
        switch (this.mNextAppTransitionType) {
            case 0:
                return "NEXT_TRANSIT_TYPE_NONE";
            case 1:
                return "NEXT_TRANSIT_TYPE_CUSTOM";
            case 2:
                return "NEXT_TRANSIT_TYPE_SCALE_UP";
            case 3:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_UP";
            case 4:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_DOWN";
            case 5:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_UP";
            case 6:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_DOWN";
            case 7:
                return "NEXT_TRANSIT_TYPE_CUSTOM_IN_PLACE";
            case 8:
            default:
                return "unknown type=" + this.mNextAppTransitionType;
            case 9:
                return "NEXT_TRANSIT_TYPE_OPEN_CROSS_PROFILE_APPS";
        }
    }

    static {
        ArrayList<Pair<Integer, String>> arrayList = new ArrayList<>();
        sFlagToString = arrayList;
        arrayList.add(new Pair<>(1, "TRANSIT_FLAG_KEYGUARD_GOING_AWAY_TO_SHADE"));
        arrayList.add(new Pair<>(2, "TRANSIT_FLAG_KEYGUARD_GOING_AWAY_NO_ANIMATION"));
        arrayList.add(new Pair<>(4, "TRANSIT_FLAG_KEYGUARD_GOING_AWAY_WITH_WALLPAPER"));
        arrayList.add(new Pair<>(8, "TRANSIT_FLAG_KEYGUARD_GOING_AWAY_SUBTLE_ANIMATION"));
        arrayList.add(new Pair<>(22, "TRANSIT_FLAG_KEYGUARD_GOING_AWAY_TO_LAUNCHER_WITH_IN_WINDOW_ANIMATIONS"));
        arrayList.add(new Pair<>(16, "TRANSIT_FLAG_APP_CRASHED"));
        arrayList.add(new Pair<>(32, "TRANSIT_FLAG_OPEN_BEHIND"));
    }

    public static String appTransitionFlagsToString(int i) {
        StringBuilder sb = new StringBuilder();
        Iterator<Pair<Integer, String>> it = sFlagToString.iterator();
        String str = "";
        while (it.hasNext()) {
            Pair<Integer, String> next = it.next();
            if ((((Integer) next.first).intValue() & i) != 0) {
                sb.append(str);
                sb.append((String) next.second);
                str = " | ";
            }
        }
        return sb.toString();
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1159641169921L, this.mAppTransitionState);
        protoOutputStream.write(1159641169922L, this.mLastUsedAppTransition);
        protoOutputStream.end(start);
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.println(this);
        printWriter.print(str);
        printWriter.print("mAppTransitionState=");
        printWriter.println(appStateToString());
        if (this.mNextAppTransitionType != 0) {
            printWriter.print(str);
            printWriter.print("mNextAppTransitionType=");
            printWriter.println(transitTypeToString());
        }
        if (this.mNextAppTransitionOverrideRequested || this.mNextAppTransitionType == 1) {
            printWriter.print(str);
            printWriter.print("mNextAppTransitionPackage=");
            printWriter.println(this.mNextAppTransitionPackage);
            printWriter.print(str);
            printWriter.print("mNextAppTransitionEnter=0x");
            printWriter.print(Integer.toHexString(this.mNextAppTransitionEnter));
            printWriter.print(" mNextAppTransitionExit=0x");
            printWriter.println(Integer.toHexString(this.mNextAppTransitionExit));
            printWriter.print(" mNextAppTransitionBackgroundColor=0x");
            printWriter.println(Integer.toHexString(this.mNextAppTransitionBackgroundColor));
        }
        switch (this.mNextAppTransitionType) {
            case 2:
                getDefaultNextAppTransitionStartRect(this.mTmpRect);
                printWriter.print(str);
                printWriter.print("mNextAppTransitionStartX=");
                printWriter.print(this.mTmpRect.left);
                printWriter.print(" mNextAppTransitionStartY=");
                printWriter.println(this.mTmpRect.top);
                printWriter.print(str);
                printWriter.print("mNextAppTransitionStartWidth=");
                printWriter.print(this.mTmpRect.width());
                printWriter.print(" mNextAppTransitionStartHeight=");
                printWriter.println(this.mTmpRect.height());
                break;
            case 3:
            case 4:
            case 5:
            case 6:
                printWriter.print(str);
                printWriter.print("mDefaultNextAppTransitionAnimationSpec=");
                printWriter.println(this.mDefaultNextAppTransitionAnimationSpec);
                printWriter.print(str);
                printWriter.print("mNextAppTransitionAnimationsSpecs=");
                printWriter.println(this.mNextAppTransitionAnimationsSpecs);
                printWriter.print(str);
                printWriter.print("mNextAppTransitionScaleUp=");
                printWriter.println(this.mNextAppTransitionScaleUp);
                break;
            case 7:
                printWriter.print(str);
                printWriter.print("mNextAppTransitionPackage=");
                printWriter.println(this.mNextAppTransitionPackage);
                printWriter.print(str);
                printWriter.print("mNextAppTransitionInPlace=0x");
                printWriter.print(Integer.toHexString(this.mNextAppTransitionInPlace));
                break;
        }
        if (this.mNextAppTransitionCallback != null) {
            printWriter.print(str);
            printWriter.print("mNextAppTransitionCallback=");
            printWriter.println(this.mNextAppTransitionCallback);
        }
        if (this.mLastUsedAppTransition != 0) {
            printWriter.print(str);
            printWriter.print("mLastUsedAppTransition=");
            printWriter.println(appTransitionOldToString(this.mLastUsedAppTransition));
            printWriter.print(str);
            printWriter.print("mLastOpeningApp=");
            printWriter.println(this.mLastOpeningApp);
            printWriter.print(str);
            printWriter.print("mLastClosingApp=");
            printWriter.println(this.mLastClosingApp);
            printWriter.print(str);
            printWriter.print("mLastChangingApp=");
            printWriter.println(this.mLastChangingApp);
        }
    }

    public boolean prepareAppTransition(int i, int i2) {
        if (this.mDisplayContent.mTransitionController.isShellTransitionsEnabled()) {
            return false;
        }
        this.mNextAppTransitionRequests.add(Integer.valueOf(i));
        this.mNextAppTransitionFlags |= i2;
        updateBooster();
        removeAppTransitionTimeoutCallbacks();
        this.mHandler.postDelayed(this.mHandleAppTransitionTimeoutRunnable, 5000L);
        return prepare();
    }

    public static boolean isTaskTransitOld(int i) {
        return isTaskOpenTransitOld(i) || isTaskCloseTransitOld(i);
    }

    public int getKeyguardTransition() {
        if (this.mNextAppTransitionRequests.indexOf(7) != -1) {
            return 7;
        }
        int indexOf = this.mNextAppTransitionRequests.indexOf(9);
        int indexOf2 = this.mNextAppTransitionRequests.indexOf(8);
        if (indexOf == -1 && indexOf2 == -1) {
            return 0;
        }
        if (indexOf == -1 || indexOf >= indexOf2) {
            return indexOf != -1 ? 9 : 8;
        }
        return 0;
    }

    public int getFirstAppTransition() {
        for (int i = 0; i < this.mNextAppTransitionRequests.size(); i++) {
            int intValue = this.mNextAppTransitionRequests.get(i).intValue();
            if (intValue != 0 && !isKeyguardTransit(intValue)) {
                return intValue;
            }
        }
        return 0;
    }

    public boolean containsTransitRequest(int i) {
        return this.mNextAppTransitionRequests.contains(Integer.valueOf(i));
    }

    /* renamed from: handleAppTransitionTimeout */
    public final void lambda$new$0() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                DisplayContent displayContent = this.mDisplayContent;
                if (displayContent == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                notifyAppTransitionTimeoutLocked();
                if (isTransitionSet() || !displayContent.mOpeningApps.isEmpty() || !displayContent.mClosingApps.isEmpty() || !displayContent.mChangingContainers.isEmpty()) {
                    if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, 344795667, 349, (String) null, new Object[]{Long.valueOf(displayContent.getDisplayId()), Boolean.valueOf(displayContent.mAppTransition.isTransitionSet()), Long.valueOf(displayContent.mOpeningApps.size()), Long.valueOf(displayContent.mClosingApps.size()), Long.valueOf(displayContent.mChangingContainers.size())});
                    }
                    setTimeout();
                    this.mService.mWindowPlacerLocked.performSurfacePlacement();
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public static void doAnimationCallback(IRemoteCallback iRemoteCallback) {
        try {
            iRemoteCallback.sendResult((Bundle) null);
        } catch (RemoteException unused) {
        }
    }

    public final void setAppTransitionFinishedCallbackIfNeeded(Animation animation) {
        IRemoteCallback iRemoteCallback = this.mAnimationFinishedCallback;
        if (iRemoteCallback == null || animation == null) {
            return;
        }
        animation.setAnimationListener(new animationAnimation$AnimationListenerC18471(iRemoteCallback));
    }

    /* renamed from: com.android.server.wm.AppTransition$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class animationAnimation$AnimationListenerC18471 implements Animation.AnimationListener {
        public final /* synthetic */ IRemoteCallback val$callback;

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }

        public animationAnimation$AnimationListenerC18471(IRemoteCallback iRemoteCallback) {
            this.val$callback = iRemoteCallback;
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            AppTransition.this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.wm.AppTransition$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AppTransition.doAnimationCallback((IRemoteCallback) obj);
                }
            }, this.val$callback));
        }
    }

    public void removeAppTransitionTimeoutCallbacks() {
        this.mHandler.removeCallbacks(this.mHandleAppTransitionTimeoutRunnable);
    }
}
