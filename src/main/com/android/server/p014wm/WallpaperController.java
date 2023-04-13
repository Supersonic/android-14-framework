package com.android.server.p014wm;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.MathUtils;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.window.ScreenCapture;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.ToBooleanFunction;
import com.android.server.p014wm.WindowManagerService;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.WallpaperController */
/* loaded from: classes2.dex */
public class WallpaperController {
    public DisplayContent mDisplayContent;
    public final boolean mEnableSeparateLockScreenEngine;
    public long mLastWallpaperTimeoutTime;
    public final float mMaxWallpaperScale;
    public WindowManagerService mService;
    public boolean mShouldOffsetWallpaperCenter;
    public boolean mShouldUpdateZoom;
    public WindowState mWaitingOnWallpaper;
    public final ArrayList<WallpaperWindowToken> mWallpaperTokens = new ArrayList<>();
    public WindowState mWallpaperTarget = null;
    public WindowState mPrevWallpaperTarget = null;
    public float mLastWallpaperX = -1.0f;
    public float mLastWallpaperY = -1.0f;
    public float mLastWallpaperXStep = -1.0f;
    public float mLastWallpaperYStep = -1.0f;
    public float mLastWallpaperZoomOut = 0.0f;
    public int mLastWallpaperDisplayOffsetX = Integer.MIN_VALUE;
    public int mLastWallpaperDisplayOffsetY = Integer.MIN_VALUE;
    public boolean mLastFrozen = false;
    public int mWallpaperDrawState = 0;
    public Point mLargestDisplaySize = null;
    public final FindWallpaperTargetResult mFindResults = new FindWallpaperTargetResult();
    public final ToBooleanFunction<WindowState> mFindWallpaperTargetFunction = new ToBooleanFunction() { // from class: com.android.server.wm.WallpaperController$$ExternalSyntheticLambda1
        public final boolean apply(Object obj) {
            boolean lambda$new$0;
            lambda$new$0 = WallpaperController.this.lambda$new$0((WindowState) obj);
            return lambda$new$0;
        }
    };
    public Consumer<WindowState> mComputeMaxZoomOutFunction = new Consumer() { // from class: com.android.server.wm.WallpaperController$$ExternalSyntheticLambda2
        @Override // java.util.function.Consumer
        public final void accept(Object obj) {
            WallpaperController.this.lambda$new$1((WindowState) obj);
        }
    };

    public static /* synthetic */ boolean lambda$updateWallpaperWindowsTarget$2(WindowState windowState, WindowState windowState2) {
        return windowState2 == windowState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(WindowState windowState) {
        boolean z;
        ActivityRecord activityRecord;
        if (windowState.mAttrs.type == 2013) {
            FindWallpaperTargetResult findWallpaperTargetResult = this.mFindResults;
            if (findWallpaperTargetResult.topWallpaper == null || findWallpaperTargetResult.resetTopWallpaper) {
                WallpaperWindowToken asWallpaperToken = windowState.mToken.asWallpaperToken();
                if (asWallpaperToken == null) {
                    Slog.w(StartingSurfaceController.TAG, "Window " + windowState + " has wallpaper type but not wallpaper token");
                    return false;
                } else if (!asWallpaperToken.canShowWhenLocked() && this.mDisplayContent.isKeyguardLocked()) {
                    return false;
                } else {
                    this.mFindResults.setTopWallpaper(windowState);
                    this.mFindResults.resetTopWallpaper = false;
                }
            }
            return false;
        }
        this.mFindResults.resetTopWallpaper = true;
        if (!windowState.mTransitionController.isShellTransitionsEnabled()) {
            ActivityRecord activityRecord2 = windowState.mActivityRecord;
            if (activityRecord2 != null && !activityRecord2.isVisible() && !windowState.mActivityRecord.isAnimating(3)) {
                return false;
            }
        } else {
            ActivityRecord activityRecord3 = windowState.mActivityRecord;
            TransitionController transitionController = windowState.mTransitionController;
            if (activityRecord3 != null && !activityRecord3.isVisibleRequested() && !transitionController.inTransition(activityRecord3)) {
                return false;
            }
        }
        if (windowState.mWillReplaceWindow && this.mWallpaperTarget == null) {
            FindWallpaperTargetResult findWallpaperTargetResult2 = this.mFindResults;
            if (!findWallpaperTargetResult2.useTopWallpaperAsTarget) {
                findWallpaperTargetResult2.setUseTopWallpaperAsTarget(true);
            }
        }
        ActivityRecord activityRecord4 = windowState.mActivityRecord;
        WindowContainer animatingContainer = activityRecord4 != null ? activityRecord4.getAnimatingContainer() : null;
        boolean z2 = animatingContainer != null && animatingContainer.isAnimating(3) && AppTransition.isKeyguardGoingAwayTransitOld(animatingContainer.mTransit) && (animatingContainer.mTransitFlags & 4) != 0;
        if ((windowState.mAttrs.flags & 524288) != 0 && this.mService.mPolicy.isKeyguardLocked()) {
            TransitionController transitionController2 = windowState.mTransitionController;
            boolean z3 = transitionController2.isShellTransitionsEnabled() && transitionController2.inTransition(windowState);
            if ((this.mService.mPolicy.isKeyguardOccluded() || this.mService.mPolicy.isKeyguardUnoccluding() || z3) && (!isFullscreen(windowState.mAttrs) || ((activityRecord = windowState.mActivityRecord) != null && !activityRecord.fillsParent()))) {
                z = true;
                if (!z2 || z) {
                    this.mFindResults.setUseTopWallpaperAsTarget(true);
                }
                boolean z4 = !windowState.hasWallpaper() || (animatingContainer == null && animatingContainer.getAnimation() != null && animatingContainer.getAnimation().getShowWallpaper());
                if (!isRecentsTransitionTarget(windowState) || isBackNavigationTarget(windowState)) {
                    this.mFindResults.setWallpaperTarget(windowState);
                    return true;
                } else if (z4 && windowState.isOnScreen()) {
                    if (this.mWallpaperTarget == windowState || windowState.isDrawFinishedLw()) {
                        this.mFindResults.setWallpaperTarget(windowState);
                        if (windowState == this.mWallpaperTarget) {
                            windowState.isAnimating(3);
                        }
                        this.mFindResults.setIsWallpaperTargetForLetterbox(windowState.hasWallpaperForLetterboxBackground());
                        return windowState.mActivityRecord != null;
                    }
                    return false;
                } else {
                    return false;
                }
            }
        }
        z = false;
        if (!z2) {
        }
        this.mFindResults.setUseTopWallpaperAsTarget(true);
        if (windowState.hasWallpaper()) {
        }
        if (!isRecentsTransitionTarget(windowState)) {
        }
        this.mFindResults.setWallpaperTarget(windowState);
        return true;
    }

    public final boolean isRecentsTransitionTarget(WindowState windowState) {
        if (windowState.mTransitionController.isShellTransitionsEnabled()) {
            return windowState.mActivityRecord != null && windowState.mAttrs.type == 1 && this.mDisplayContent.isKeyguardLocked() && windowState.mTransitionController.isTransientHide(windowState.getTask());
        }
        RecentsAnimationController recentsAnimationController = this.mService.getRecentsAnimationController();
        return recentsAnimationController != null && recentsAnimationController.isWallpaperVisible(windowState);
    }

    public final boolean isBackNavigationTarget(WindowState windowState) {
        return this.mService.mAtmService.mBackNavigationController.isWallpaperVisible(windowState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(WindowState windowState) {
        if (windowState.mIsWallpaper || Float.compare(windowState.mWallpaperZoomOut, this.mLastWallpaperZoomOut) <= 0) {
            return;
        }
        this.mLastWallpaperZoomOut = windowState.mWallpaperZoomOut;
    }

    public WallpaperController(WindowManagerService windowManagerService, DisplayContent displayContent) {
        this.mService = windowManagerService;
        this.mDisplayContent = displayContent;
        Resources resources = windowManagerService.mContext.getResources();
        this.mMaxWallpaperScale = resources.getFloat(17105124);
        this.mShouldOffsetWallpaperCenter = resources.getBoolean(17891756);
        this.mEnableSeparateLockScreenEngine = resources.getBoolean(17891705);
    }

    public void resetLargestDisplay(Display display) {
        if (display == null || display.getType() != 1) {
            return;
        }
        this.mLargestDisplaySize = null;
    }

    @VisibleForTesting
    public void setShouldOffsetWallpaperCenter(boolean z) {
        this.mShouldOffsetWallpaperCenter = z;
    }

    public final Point findLargestDisplaySize() {
        if (this.mShouldOffsetWallpaperCenter) {
            Point point = new Point();
            List<DisplayInfo> possibleDisplayInfoLocked = this.mService.getPossibleDisplayInfoLocked(0);
            for (int i = 0; i < possibleDisplayInfoLocked.size(); i++) {
                DisplayInfo displayInfo = possibleDisplayInfoLocked.get(i);
                if (displayInfo.type == 1 && Math.max(displayInfo.logicalWidth, displayInfo.logicalHeight) > Math.max(point.x, point.y)) {
                    point.set(displayInfo.logicalWidth, displayInfo.logicalHeight);
                }
            }
            return point;
        }
        return null;
    }

    public WindowState getWallpaperTarget() {
        return this.mWallpaperTarget;
    }

    public boolean isWallpaperTarget(WindowState windowState) {
        return windowState == this.mWallpaperTarget;
    }

    public boolean isBelowWallpaperTarget(WindowState windowState) {
        WindowState windowState2 = this.mWallpaperTarget;
        return windowState2 != null && windowState2.mLayer >= windowState.mBaseLayer;
    }

    public boolean isWallpaperVisible() {
        for (int size = this.mWallpaperTokens.size() - 1; size >= 0; size--) {
            if (this.mWallpaperTokens.get(size).isVisible()) {
                return true;
            }
        }
        return false;
    }

    public final boolean shouldWallpaperBeVisible(WindowState windowState) {
        return (windowState == null && this.mPrevWallpaperTarget == null) ? false : true;
    }

    public boolean isWallpaperTargetAnimating() {
        ActivityRecord activityRecord;
        WindowState windowState = this.mWallpaperTarget;
        return windowState != null && windowState.isAnimating(3) && ((activityRecord = this.mWallpaperTarget.mActivityRecord) == null || !activityRecord.isWaitingForTransitionStart());
    }

    public void updateWallpaperVisibility() {
        boolean shouldWallpaperBeVisible = shouldWallpaperBeVisible(this.mWallpaperTarget);
        for (int size = this.mWallpaperTokens.size() - 1; size >= 0; size--) {
            this.mWallpaperTokens.get(size).setVisibility(shouldWallpaperBeVisible);
        }
    }

    public void hideDeferredWallpapersIfNeededLegacy() {
        for (int size = this.mWallpaperTokens.size() - 1; size >= 0; size--) {
            WallpaperWindowToken wallpaperWindowToken = this.mWallpaperTokens.get(size);
            if (!wallpaperWindowToken.isVisibleRequested()) {
                wallpaperWindowToken.commitVisibility(false);
            }
        }
    }

    public void hideWallpapers(WindowState windowState) {
        WindowState windowState2 = this.mWallpaperTarget;
        if ((windowState2 == null || (windowState2 == windowState && this.mPrevWallpaperTarget == null)) && !this.mFindResults.useTopWallpaperAsTarget) {
            for (int size = this.mWallpaperTokens.size() - 1; size >= 0; size--) {
                WallpaperWindowToken wallpaperWindowToken = this.mWallpaperTokens.get(size);
                wallpaperWindowToken.setVisibility(false);
                if (ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_WALLPAPER) && wallpaperWindowToken.isVisible() && ProtoLogCache.WM_DEBUG_WALLPAPER_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_WALLPAPER, 1984843251, 0, (String) null, new Object[]{String.valueOf(wallpaperWindowToken), String.valueOf(windowState), String.valueOf(this.mWallpaperTarget), String.valueOf(this.mPrevWallpaperTarget), String.valueOf(Debug.getCallers(5))});
                }
            }
        }
    }

    public boolean updateWallpaperOffset(WindowState windowState, boolean z) {
        boolean z2;
        Rect parentFrame = windowState.getParentFrame();
        Rect frame = windowState.getFrame();
        float f = windowState.isRtl() ? 1.0f : 0.0f;
        float f2 = this.mLastWallpaperX;
        if (f2 >= 0.0f) {
            f = f2;
        }
        float f3 = this.mLastWallpaperXStep;
        if (f3 < 0.0f) {
            f3 = -1.0f;
        }
        int width = frame.width() - parentFrame.width();
        int displayWidthOffset = getDisplayWidthOffset(width, parentFrame, windowState.isRtl());
        int i = width - displayWidthOffset;
        int i2 = i > 0 ? -((int) ((i * f) + 0.5f)) : 0;
        int i3 = this.mLastWallpaperDisplayOffsetX;
        if (i3 != Integer.MIN_VALUE) {
            i2 += i3;
        } else if (!windowState.isRtl()) {
            i2 -= displayWidthOffset;
        }
        boolean z3 = true;
        if (windowState.mWallpaperX == f && windowState.mWallpaperXStep == f3) {
            z2 = false;
        } else {
            windowState.mWallpaperX = f;
            windowState.mWallpaperXStep = f3;
            z2 = true;
        }
        float f4 = this.mLastWallpaperY;
        if (f4 < 0.0f) {
            f4 = 0.5f;
        }
        float f5 = this.mLastWallpaperYStep;
        float f6 = f5 >= 0.0f ? f5 : -1.0f;
        int height = (windowState.getFrame().bottom - windowState.getFrame().top) - parentFrame.height();
        int i4 = height > 0 ? -((int) ((height * f4) + 0.5f)) : 0;
        int i5 = this.mLastWallpaperDisplayOffsetY;
        if (i5 != Integer.MIN_VALUE) {
            i4 += i5;
        }
        if (windowState.mWallpaperY != f4 || windowState.mWallpaperYStep != f6) {
            windowState.mWallpaperY = f4;
            windowState.mWallpaperYStep = f6;
            z2 = true;
        }
        if (Float.compare(windowState.mWallpaperZoomOut, this.mLastWallpaperZoomOut) != 0) {
            windowState.mWallpaperZoomOut = this.mLastWallpaperZoomOut;
        } else {
            z3 = z2;
        }
        boolean wallpaperOffset = windowState.setWallpaperOffset(i2, i4, windowState.mShouldScaleWallpaper ? zoomOutToScale(windowState.mWallpaperZoomOut) : 1.0f);
        if (z3 && (windowState.mAttrs.privateFlags & 4) != 0) {
            if (z) {
                try {
                    this.mWaitingOnWallpaper = windowState;
                } catch (RemoteException unused) {
                }
            }
            windowState.mClient.dispatchWallpaperOffsets(windowState.mWallpaperX, windowState.mWallpaperY, windowState.mWallpaperXStep, windowState.mWallpaperYStep, windowState.mWallpaperZoomOut, z);
            if (z && this.mWaitingOnWallpaper != null) {
                long uptimeMillis = SystemClock.uptimeMillis();
                if (this.mLastWallpaperTimeoutTime + 10000 < uptimeMillis) {
                    try {
                        this.mService.mGlobalLock.wait(150L);
                    } catch (InterruptedException unused2) {
                    }
                    if (150 + uptimeMillis < SystemClock.uptimeMillis()) {
                        Slog.i(StartingSurfaceController.TAG, "Timeout waiting for wallpaper to offset: " + windowState);
                        this.mLastWallpaperTimeoutTime = uptimeMillis;
                    }
                }
                this.mWaitingOnWallpaper = null;
            }
        }
        return wallpaperOffset;
    }

    public final int getDisplayWidthOffset(int i, Rect rect, boolean z) {
        int width;
        if (this.mShouldOffsetWallpaperCenter) {
            if (this.mLargestDisplaySize == null) {
                this.mLargestDisplaySize = findLargestDisplaySize();
            }
            if (this.mLargestDisplaySize == null || this.mLargestDisplaySize.x == (width = rect.width()) || rect.width() >= rect.height()) {
                return 0;
            }
            Point point = this.mLargestDisplaySize;
            int round = Math.round(point.x * (rect.height() / point.y));
            if (z) {
                return round - ((width + round) / 2);
            }
            return Math.min(round - width, i) / 2;
        }
        return 0;
    }

    public void setWindowWallpaperPosition(WindowState windowState, float f, float f2, float f3, float f4) {
        if (windowState.mWallpaperX == f && windowState.mWallpaperY == f2) {
            return;
        }
        windowState.mWallpaperX = f;
        windowState.mWallpaperY = f2;
        windowState.mWallpaperXStep = f3;
        windowState.mWallpaperYStep = f4;
        updateWallpaperOffsetLocked(windowState, true);
    }

    public void setWallpaperZoomOut(WindowState windowState, float f) {
        if (Float.compare(windowState.mWallpaperZoomOut, f) != 0) {
            windowState.mWallpaperZoomOut = f;
            this.mShouldUpdateZoom = true;
            updateWallpaperOffsetLocked(windowState, false);
        }
    }

    public void setShouldZoomOutWallpaper(WindowState windowState, boolean z) {
        if (z != windowState.mShouldScaleWallpaper) {
            windowState.mShouldScaleWallpaper = z;
            updateWallpaperOffsetLocked(windowState, false);
        }
    }

    public void setWindowWallpaperDisplayOffset(WindowState windowState, int i, int i2) {
        if (windowState.mWallpaperDisplayOffsetX == i && windowState.mWallpaperDisplayOffsetY == i2) {
            return;
        }
        windowState.mWallpaperDisplayOffsetX = i;
        windowState.mWallpaperDisplayOffsetY = i2;
        updateWallpaperOffsetLocked(windowState, true);
    }

    public Bundle sendWindowWallpaperCommand(WindowState windowState, String str, int i, int i2, int i3, Bundle bundle, boolean z) {
        if (windowState == this.mWallpaperTarget || windowState == this.mPrevWallpaperTarget) {
            sendWindowWallpaperCommand(str, i, i2, i3, bundle, z);
            return null;
        }
        return null;
    }

    public final void sendWindowWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle, boolean z) {
        for (int size = this.mWallpaperTokens.size() - 1; size >= 0; size--) {
            this.mWallpaperTokens.get(size).sendWindowWallpaperCommand(str, i, i2, i3, bundle, z);
        }
    }

    public final void updateWallpaperOffsetLocked(WindowState windowState, boolean z) {
        WindowState windowState2 = this.mWallpaperTarget;
        if (windowState2 == null && windowState.mToken.isVisible() && windowState.mTransitionController.inTransition()) {
            windowState2 = windowState;
        }
        if (windowState2 != null) {
            float f = windowState2.mWallpaperX;
            if (f >= 0.0f) {
                this.mLastWallpaperX = f;
            } else {
                float f2 = windowState.mWallpaperX;
                if (f2 >= 0.0f) {
                    this.mLastWallpaperX = f2;
                }
            }
            float f3 = windowState2.mWallpaperY;
            if (f3 >= 0.0f) {
                this.mLastWallpaperY = f3;
            } else {
                float f4 = windowState.mWallpaperY;
                if (f4 >= 0.0f) {
                    this.mLastWallpaperY = f4;
                }
            }
            computeLastWallpaperZoomOut();
            int i = windowState2.mWallpaperDisplayOffsetX;
            if (i != Integer.MIN_VALUE) {
                this.mLastWallpaperDisplayOffsetX = i;
            } else {
                int i2 = windowState.mWallpaperDisplayOffsetX;
                if (i2 != Integer.MIN_VALUE) {
                    this.mLastWallpaperDisplayOffsetX = i2;
                }
            }
            int i3 = windowState2.mWallpaperDisplayOffsetY;
            if (i3 != Integer.MIN_VALUE) {
                this.mLastWallpaperDisplayOffsetY = i3;
            } else {
                int i4 = windowState.mWallpaperDisplayOffsetY;
                if (i4 != Integer.MIN_VALUE) {
                    this.mLastWallpaperDisplayOffsetY = i4;
                }
            }
            float f5 = windowState2.mWallpaperXStep;
            if (f5 >= 0.0f) {
                this.mLastWallpaperXStep = f5;
            } else {
                float f6 = windowState.mWallpaperXStep;
                if (f6 >= 0.0f) {
                    this.mLastWallpaperXStep = f6;
                }
            }
            float f7 = windowState2.mWallpaperYStep;
            if (f7 >= 0.0f) {
                this.mLastWallpaperYStep = f7;
            } else {
                float f8 = windowState.mWallpaperYStep;
                if (f8 >= 0.0f) {
                    this.mLastWallpaperYStep = f8;
                }
            }
        }
        for (int size = this.mWallpaperTokens.size() - 1; size >= 0; size--) {
            this.mWallpaperTokens.get(size).updateWallpaperOffset(z);
        }
    }

    public void clearLastWallpaperTimeoutTime() {
        this.mLastWallpaperTimeoutTime = 0L;
    }

    public void wallpaperCommandComplete(IBinder iBinder) {
        WindowState windowState = this.mWaitingOnWallpaper;
        if (windowState == null || windowState.mClient.asBinder() != iBinder) {
            return;
        }
        this.mWaitingOnWallpaper = null;
        this.mService.mGlobalLock.notifyAll();
    }

    public void wallpaperOffsetsComplete(IBinder iBinder) {
        WindowState windowState = this.mWaitingOnWallpaper;
        if (windowState == null || windowState.mClient.asBinder() != iBinder) {
            return;
        }
        this.mWaitingOnWallpaper = null;
        this.mService.mGlobalLock.notifyAll();
    }

    public final void findWallpaperTarget() {
        this.mFindResults.reset();
        if (this.mDisplayContent.getDefaultTaskDisplayArea().isRootTaskVisible(5)) {
            this.mFindResults.setUseTopWallpaperAsTarget(true);
        }
        this.mDisplayContent.forAllWindows(this.mFindWallpaperTargetFunction, true);
        FindWallpaperTargetResult findWallpaperTargetResult = this.mFindResults;
        if (findWallpaperTargetResult.wallpaperTarget == null && findWallpaperTargetResult.useTopWallpaperAsTarget) {
            findWallpaperTargetResult.setWallpaperTarget(findWallpaperTargetResult.topWallpaper);
        }
    }

    public final boolean isFullscreen(WindowManager.LayoutParams layoutParams) {
        return layoutParams.x == 0 && layoutParams.y == 0 && layoutParams.width == -1 && layoutParams.height == -1;
    }

    public final void updateWallpaperWindowsTarget(FindWallpaperTargetResult findWallpaperTargetResult) {
        WindowState windowState;
        WindowState windowState2 = findWallpaperTargetResult.wallpaperTarget;
        if (this.mWallpaperTarget == windowState2 || ((windowState = this.mPrevWallpaperTarget) != null && windowState == windowState2)) {
            WindowState windowState3 = this.mPrevWallpaperTarget;
            if (windowState3 == null || windowState3.isAnimatingLw()) {
                return;
            }
            if (ProtoLogCache.WM_DEBUG_WALLPAPER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WALLPAPER, -1478175541, 0, (String) null, (Object[]) null);
            }
            this.mPrevWallpaperTarget = null;
            this.mWallpaperTarget = windowState2;
            return;
        }
        if (ProtoLogCache.WM_DEBUG_WALLPAPER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WALLPAPER, 114070759, 0, (String) null, new Object[]{String.valueOf(windowState2), String.valueOf(this.mWallpaperTarget), String.valueOf(Debug.getCallers(5))});
        }
        this.mPrevWallpaperTarget = null;
        final WindowState windowState4 = this.mWallpaperTarget;
        this.mWallpaperTarget = windowState2;
        if (windowState4 == null && windowState2 != null) {
            updateWallpaperOffsetLocked(windowState2, false);
        }
        if (windowState2 == null || windowState4 == null) {
            return;
        }
        boolean isAnimatingLw = windowState4.isAnimatingLw();
        boolean isAnimatingLw2 = windowState2.isAnimatingLw();
        if (ProtoLogCache.WM_DEBUG_WALLPAPER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WALLPAPER, -275077723, 0, (String) null, new Object[]{String.valueOf(isAnimatingLw2), String.valueOf(isAnimatingLw)});
        }
        if (isAnimatingLw2 && isAnimatingLw && this.mDisplayContent.getWindow(new Predicate() { // from class: com.android.server.wm.WallpaperController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateWallpaperWindowsTarget$2;
                lambda$updateWallpaperWindowsTarget$2 = WallpaperController.lambda$updateWallpaperWindowsTarget$2(WindowState.this, (WindowState) obj);
                return lambda$updateWallpaperWindowsTarget$2;
            }
        }) != null) {
            ActivityRecord activityRecord = windowState2.mActivityRecord;
            boolean z = true;
            boolean z2 = (activityRecord == null || activityRecord.isVisibleRequested()) ? false : true;
            ActivityRecord activityRecord2 = windowState4.mActivityRecord;
            if (activityRecord2 == null || activityRecord2.isVisibleRequested()) {
                z = false;
            }
            if (ProtoLogCache.WM_DEBUG_WALLPAPER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WALLPAPER, -360208282, 204, (String) null, new Object[]{String.valueOf(windowState4), Boolean.valueOf(z), String.valueOf(windowState2), Boolean.valueOf(z2)});
            }
            this.mPrevWallpaperTarget = windowState4;
            if (z2 && !z) {
                if (ProtoLogCache.WM_DEBUG_WALLPAPER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WALLPAPER, 1178653181, 0, (String) null, (Object[]) null);
                }
                this.mWallpaperTarget = windowState4;
            } else if (z2 == z && !this.mDisplayContent.mOpeningApps.contains(windowState2.mActivityRecord) && (this.mDisplayContent.mOpeningApps.contains(windowState4.mActivityRecord) || this.mDisplayContent.mClosingApps.contains(windowState4.mActivityRecord))) {
                this.mWallpaperTarget = windowState4;
            }
            findWallpaperTargetResult.setWallpaperTarget(windowState2);
        }
    }

    public final void updateWallpaperTokens(boolean z, boolean z2) {
        for (int size = this.mWallpaperTokens.size() - 1; size >= 0; size--) {
            WallpaperWindowToken wallpaperWindowToken = this.mWallpaperTokens.get(size);
            wallpaperWindowToken.updateWallpaperWindows(z && (!z2 || wallpaperWindowToken.canShowWhenLocked()));
        }
    }

    public void adjustWallpaperWindows() {
        this.mDisplayContent.mWallpaperMayChange = false;
        findWallpaperTarget();
        updateWallpaperWindowsTarget(this.mFindResults);
        WindowState windowState = this.mWallpaperTarget;
        boolean z = windowState != null;
        if (z) {
            float f = windowState.mWallpaperX;
            if (f >= 0.0f) {
                this.mLastWallpaperX = f;
                this.mLastWallpaperXStep = windowState.mWallpaperXStep;
            }
            computeLastWallpaperZoomOut();
            WindowState windowState2 = this.mWallpaperTarget;
            float f2 = windowState2.mWallpaperY;
            if (f2 >= 0.0f) {
                this.mLastWallpaperY = f2;
                this.mLastWallpaperYStep = windowState2.mWallpaperYStep;
            }
            int i = windowState2.mWallpaperDisplayOffsetX;
            if (i != Integer.MIN_VALUE) {
                this.mLastWallpaperDisplayOffsetX = i;
            }
            int i2 = windowState2.mWallpaperDisplayOffsetY;
            if (i2 != Integer.MIN_VALUE) {
                this.mLastWallpaperDisplayOffsetY = i2;
            }
        }
        updateWallpaperTokens(z, this.mDisplayContent.isKeyguardLocked());
        if (z) {
            boolean z2 = this.mLastFrozen;
            boolean z3 = this.mFindResults.isWallpaperTargetForLetterbox;
            if (z2 != z3) {
                this.mLastFrozen = z3;
                sendWindowWallpaperCommand(z3 ? "android.wallpaper.freeze" : "android.wallpaper.unfreeze", 0, 0, 0, null, false);
            }
        }
        if (ProtoLogCache.WM_DEBUG_WALLPAPER_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_WALLPAPER, -304728471, 0, (String) null, new Object[]{String.valueOf(this.mWallpaperTarget), String.valueOf(this.mPrevWallpaperTarget)});
        }
    }

    public boolean processWallpaperDrawPendingTimeout() {
        if (this.mWallpaperDrawState == 1) {
            this.mWallpaperDrawState = 2;
            if (this.mService.getRecentsAnimationController() != null) {
                this.mService.getRecentsAnimationController().startAnimation();
            }
            this.mService.mAtmService.mBackNavigationController.startAnimation();
            return true;
        }
        return false;
    }

    public boolean wallpaperTransitionReady() {
        boolean z;
        boolean z2 = true;
        int size = this.mWallpaperTokens.size() - 1;
        while (true) {
            if (size < 0) {
                z = true;
                break;
            } else if (this.mWallpaperTokens.get(size).hasVisibleNotDrawnWallpaper()) {
                int i = this.mWallpaperDrawState;
                z = i == 2;
                if (i == 0) {
                    this.mWallpaperDrawState = 1;
                    this.mService.f1164mH.removeMessages(39, this);
                    WindowManagerService.HandlerC1915H handlerC1915H = this.mService.f1164mH;
                    handlerC1915H.sendMessageDelayed(handlerC1915H.obtainMessage(39, this), 500L);
                }
                z2 = false;
            } else {
                size--;
            }
        }
        if (z2) {
            this.mWallpaperDrawState = 0;
            this.mService.f1164mH.removeMessages(39, this);
        }
        return z;
    }

    public void adjustWallpaperWindowsForAppTransitionIfNeeded(ArraySet<ActivityRecord> arraySet) {
        boolean z = true;
        if ((this.mDisplayContent.pendingLayoutChanges & 4) == 0) {
            int size = arraySet.size() - 1;
            while (true) {
                if (size < 0) {
                    z = false;
                    break;
                } else if (arraySet.valueAt(size).windowsCanBeWallpaperTarget()) {
                    break;
                } else {
                    size--;
                }
            }
        }
        if (z) {
            adjustWallpaperWindows();
        }
    }

    public void addWallpaperToken(WallpaperWindowToken wallpaperWindowToken) {
        this.mWallpaperTokens.add(wallpaperWindowToken);
    }

    public void removeWallpaperToken(WallpaperWindowToken wallpaperWindowToken) {
        this.mWallpaperTokens.remove(wallpaperWindowToken);
    }

    @VisibleForTesting
    public boolean canScreenshotWallpaper() {
        return canScreenshotWallpaper(getTopVisibleWallpaper());
    }

    public final boolean canScreenshotWallpaper(WindowState windowState) {
        return this.mService.mPolicy.isScreenOn() && windowState != null;
    }

    public Bitmap screenshotWallpaperLocked() {
        WindowState topVisibleWallpaper = getTopVisibleWallpaper();
        if (canScreenshotWallpaper(topVisibleWallpaper)) {
            Rect bounds = topVisibleWallpaper.getBounds();
            bounds.offsetTo(0, 0);
            ScreenCapture.ScreenshotHardwareBuffer captureLayers = ScreenCapture.captureLayers(topVisibleWallpaper.getSurfaceControl(), bounds, 1.0f);
            if (captureLayers == null) {
                Slog.w(StartingSurfaceController.TAG, "Failed to screenshot wallpaper");
                return null;
            }
            return Bitmap.wrapHardwareBuffer(captureLayers.getHardwareBuffer(), captureLayers.getColorSpace());
        }
        return null;
    }

    public SurfaceControl mirrorWallpaperSurface() {
        WindowState topVisibleWallpaper = getTopVisibleWallpaper();
        if (topVisibleWallpaper != null) {
            return SurfaceControl.mirrorSurface(topVisibleWallpaper.getSurfaceControl());
        }
        return null;
    }

    public WindowState getTopVisibleWallpaper() {
        for (int size = this.mWallpaperTokens.size() - 1; size >= 0; size--) {
            WallpaperWindowToken wallpaperWindowToken = this.mWallpaperTokens.get(size);
            for (int childCount = wallpaperWindowToken.getChildCount() - 1; childCount >= 0; childCount--) {
                WindowState childAt = wallpaperWindowToken.getChildAt(childCount);
                if (childAt.mWinAnimator.getShown() && childAt.mWinAnimator.mLastAlpha > 0.0f) {
                    return childAt;
                }
            }
        }
        return null;
    }

    public final void computeLastWallpaperZoomOut() {
        if (this.mShouldUpdateZoom) {
            this.mLastWallpaperZoomOut = 0.0f;
            this.mDisplayContent.forAllWindows(this.mComputeMaxZoomOutFunction, true);
            this.mShouldUpdateZoom = false;
        }
    }

    public final float zoomOutToScale(float f) {
        return MathUtils.lerp(1.0f, this.mMaxWallpaperScale, 1.0f - f);
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("displayId=");
        printWriter.println(this.mDisplayContent.getDisplayId());
        printWriter.print(str);
        printWriter.print("mWallpaperTarget=");
        printWriter.println(this.mWallpaperTarget);
        if (this.mPrevWallpaperTarget != null) {
            printWriter.print(str);
            printWriter.print("mPrevWallpaperTarget=");
            printWriter.println(this.mPrevWallpaperTarget);
        }
        printWriter.print(str);
        printWriter.print("mLastWallpaperX=");
        printWriter.print(this.mLastWallpaperX);
        printWriter.print(" mLastWallpaperY=");
        printWriter.println(this.mLastWallpaperY);
        if (this.mLastWallpaperDisplayOffsetX == Integer.MIN_VALUE && this.mLastWallpaperDisplayOffsetY == Integer.MIN_VALUE) {
            return;
        }
        printWriter.print(str);
        printWriter.print("mLastWallpaperDisplayOffsetX=");
        printWriter.print(this.mLastWallpaperDisplayOffsetX);
        printWriter.print(" mLastWallpaperDisplayOffsetY=");
        printWriter.println(this.mLastWallpaperDisplayOffsetY);
    }

    /* renamed from: com.android.server.wm.WallpaperController$FindWallpaperTargetResult */
    /* loaded from: classes2.dex */
    public static final class FindWallpaperTargetResult {
        public boolean isWallpaperTargetForLetterbox;
        public boolean resetTopWallpaper;
        public WindowState topWallpaper;
        public boolean useTopWallpaperAsTarget;
        public WindowState wallpaperTarget;

        public FindWallpaperTargetResult() {
            this.topWallpaper = null;
            this.useTopWallpaperAsTarget = false;
            this.wallpaperTarget = null;
            this.resetTopWallpaper = false;
            this.isWallpaperTargetForLetterbox = false;
        }

        public void setTopWallpaper(WindowState windowState) {
            this.topWallpaper = windowState;
        }

        public void setWallpaperTarget(WindowState windowState) {
            this.wallpaperTarget = windowState;
        }

        public void setUseTopWallpaperAsTarget(boolean z) {
            this.useTopWallpaperAsTarget = z;
        }

        public void setIsWallpaperTargetForLetterbox(boolean z) {
            this.isWallpaperTargetForLetterbox = z;
        }

        public void reset() {
            this.topWallpaper = null;
            this.wallpaperTarget = null;
            this.useTopWallpaperAsTarget = false;
            this.resetTopWallpaper = false;
            this.isWallpaperTargetForLetterbox = false;
        }
    }
}
