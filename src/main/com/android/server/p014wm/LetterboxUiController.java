package com.android.server.p014wm;

import android.app.ActivityManager;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Slog;
import android.view.InsetsSource;
import android.view.InsetsState;
import android.view.RoundedCorner;
import android.view.SurfaceControl;
import android.view.WindowInsets;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.LetterboxDetails;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.DeviceStateController;
import com.android.server.p014wm.WindowContainer;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
/* renamed from: com.android.server.wm.LetterboxUiController */
/* loaded from: classes2.dex */
public final class LetterboxUiController {
    public static final Predicate<ActivityRecord> FIRST_OPAQUE_NOT_FINISHING_ACTIVITY_PREDICATE = new Predicate() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda0
        @Override // java.util.function.Predicate
        public final boolean test(Object obj) {
            boolean lambda$static$0;
            lambda$static$0 = LetterboxUiController.lambda$static$0((ActivityRecord) obj);
            return lambda$static$0;
        }
    };
    public final ActivityRecord mActivityRecord;
    public final Boolean mBooleanPropertyAllowDisplayOrientationOverride;
    public final Boolean mBooleanPropertyAllowOrientationOverride;
    public final Boolean mBooleanPropertyCameraCompatAllowForceRotation;
    public final Boolean mBooleanPropertyCameraCompatAllowRefresh;
    public final Boolean mBooleanPropertyCameraCompatEnableRefreshViaPause;
    public final Boolean mBooleanPropertyFakeFocus;
    public final Boolean mBooleanPropertyIgnoreRequestedOrientation;
    public ActivityRecord.CompatDisplayInsets mInheritedCompatDisplayInsets;
    public final boolean mIsOverrideAnyOrientationEnabled;
    public final boolean mIsOverrideCameraCompatDisableForceRotationEnabled;
    public final boolean mIsOverrideCameraCompatDisableRefreshEnabled;
    public final boolean mIsOverrideCameraCompatEnableRefreshViaPauseEnabled;
    public final boolean mIsOverrideEnableCompatFakeFocusEnabled;
    public final boolean mIsOverrideEnableCompatIgnoreRequestedOrientationEnabled;
    public final boolean mIsOverrideOrientationOnlyForCameraEnabled;
    public final boolean mIsOverrideRespectRequestedOrientationEnabled;
    public final boolean mIsOverrideToNosensorOrientationEnabled;
    public final boolean mIsOverrideToPortraitOrientationEnabled;
    public final boolean mIsOverrideToReverseLandscapeOrientationEnabled;
    public final boolean mIsOverrideUseDisplayLandscapeNaturalOrientationEnabled;
    public boolean mIsRefreshAfterRotationRequested;
    public boolean mIsRelauchingAfterRequestedOrientationChanged;
    public Letterbox mLetterbox;
    public WindowContainerListener mLetterboxConfigListener;
    public final LetterboxConfiguration mLetterboxConfiguration;
    public boolean mShowWallpaperForLetterboxBackground;
    public final Point mTmpPoint = new Point();
    public float mInheritedMinAspectRatio = 0.0f;
    public float mInheritedMaxAspectRatio = 0.0f;
    @Configuration.Orientation
    public int mInheritedOrientation = 0;
    public int mInheritedAppCompatState = 0;

    public static /* synthetic */ boolean lambda$static$0(ActivityRecord activityRecord) {
        return activityRecord.fillsParent() && !activityRecord.isFinishing();
    }

    public LetterboxUiController(WindowManagerService windowManagerService, ActivityRecord activityRecord) {
        LetterboxConfiguration letterboxConfiguration = windowManagerService.mLetterboxConfiguration;
        this.mLetterboxConfiguration = letterboxConfiguration;
        this.mActivityRecord = activityRecord;
        PackageManager packageManager = windowManagerService.mContext.getPackageManager();
        String str = activityRecord.packageName;
        Objects.requireNonNull(letterboxConfiguration);
        this.mBooleanPropertyIgnoreRequestedOrientation = readComponentProperty(packageManager, str, new LetterboxUiController$$ExternalSyntheticLambda2(letterboxConfiguration), "android.window.PROPERTY_COMPAT_IGNORE_REQUESTED_ORIENTATION");
        String str2 = activityRecord.packageName;
        Objects.requireNonNull(letterboxConfiguration);
        this.mBooleanPropertyFakeFocus = readComponentProperty(packageManager, str2, new LetterboxUiController$$ExternalSyntheticLambda4(letterboxConfiguration), "android.window.PROPERTY_COMPAT_ENABLE_FAKE_FOCUS");
        this.mBooleanPropertyCameraCompatAllowForceRotation = readComponentProperty(packageManager, activityRecord.packageName, new BooleanSupplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda18
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$new$1;
                lambda$new$1 = LetterboxUiController.this.lambda$new$1();
                return lambda$new$1;
            }
        }, "android.window.PROPERTY_CAMERA_COMPAT_ALLOW_FORCE_ROTATION");
        this.mBooleanPropertyCameraCompatAllowRefresh = readComponentProperty(packageManager, activityRecord.packageName, new BooleanSupplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda19
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$new$2;
                lambda$new$2 = LetterboxUiController.this.lambda$new$2();
                return lambda$new$2;
            }
        }, "android.window.PROPERTY_CAMERA_COMPAT_ALLOW_REFRESH");
        this.mBooleanPropertyCameraCompatEnableRefreshViaPause = readComponentProperty(packageManager, activityRecord.packageName, new BooleanSupplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda20
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$new$3;
                lambda$new$3 = LetterboxUiController.this.lambda$new$3();
                return lambda$new$3;
            }
        }, "android.window.PROPERTY_CAMERA_COMPAT_ENABLE_REFRESH_VIA_PAUSE");
        this.mBooleanPropertyAllowOrientationOverride = readComponentProperty(packageManager, activityRecord.packageName, null, "android.window.PROPERTY_COMPAT_ALLOW_ORIENTATION_OVERRIDE");
        this.mBooleanPropertyAllowDisplayOrientationOverride = readComponentProperty(packageManager, activityRecord.packageName, null, "android.window.PROPERTY_COMPAT_ALLOW_DISPLAY_ORIENTATION_OVERRIDE");
        this.mIsOverrideAnyOrientationEnabled = isCompatChangeEnabled(265464455L);
        this.mIsOverrideToPortraitOrientationEnabled = isCompatChangeEnabled(265452344L);
        this.mIsOverrideToReverseLandscapeOrientationEnabled = isCompatChangeEnabled(266124927L);
        this.mIsOverrideToNosensorOrientationEnabled = isCompatChangeEnabled(265451093L);
        this.mIsOverrideOrientationOnlyForCameraEnabled = isCompatChangeEnabled(265456536L);
        this.mIsOverrideUseDisplayLandscapeNaturalOrientationEnabled = isCompatChangeEnabled(255940284L);
        this.mIsOverrideRespectRequestedOrientationEnabled = isCompatChangeEnabled(236283604L);
        this.mIsOverrideCameraCompatDisableForceRotationEnabled = isCompatChangeEnabled(263959004L);
        this.mIsOverrideCameraCompatDisableRefreshEnabled = isCompatChangeEnabled(264304459L);
        this.mIsOverrideCameraCompatEnableRefreshViaPauseEnabled = isCompatChangeEnabled(264301586L);
        this.mIsOverrideEnableCompatIgnoreRequestedOrientationEnabled = isCompatChangeEnabled(254631730L);
        this.mIsOverrideEnableCompatFakeFocusEnabled = isCompatChangeEnabled(263259275L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1() {
        return this.mLetterboxConfiguration.isCameraCompatTreatmentEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$2() {
        return this.mLetterboxConfiguration.isCameraCompatTreatmentEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$3() {
        return this.mLetterboxConfiguration.isCameraCompatTreatmentEnabled(true);
    }

    public static Boolean readComponentProperty(PackageManager packageManager, String str, BooleanSupplier booleanSupplier, String str2) {
        if (booleanSupplier == null || booleanSupplier.getAsBoolean()) {
            try {
                return Boolean.valueOf(packageManager.getProperty(str2, str).getBoolean());
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }
        return null;
    }

    public void destroy() {
        Letterbox letterbox = this.mLetterbox;
        if (letterbox != null) {
            letterbox.destroy();
            this.mLetterbox = null;
        }
    }

    public void onMovedToDisplay(int i) {
        Letterbox letterbox = this.mLetterbox;
        if (letterbox != null) {
            letterbox.onMovedToDisplay(i);
        }
    }

    public boolean shouldIgnoreRequestedOrientation(int i) {
        DisplayRotationCompatPolicy displayRotationCompatPolicy;
        LetterboxConfiguration letterboxConfiguration = this.mLetterboxConfiguration;
        Objects.requireNonNull(letterboxConfiguration);
        if (shouldEnableWithOverrideAndProperty(new LetterboxUiController$$ExternalSyntheticLambda2(letterboxConfiguration), this.mIsOverrideEnableCompatIgnoreRequestedOrientationEnabled, this.mBooleanPropertyIgnoreRequestedOrientation)) {
            if (this.mIsRelauchingAfterRequestedOrientationChanged) {
                Slog.w("ActivityTaskManager", "Ignoring orientation update to " + ActivityInfo.screenOrientationToString(i) + " due to relaunching after setRequestedOrientation for " + this.mActivityRecord);
                return true;
            }
            ActivityRecord activityRecord = this.mActivityRecord;
            DisplayContent displayContent = activityRecord.mDisplayContent;
            if (displayContent == null || (displayRotationCompatPolicy = displayContent.mDisplayRotationCompatPolicy) == null || !displayRotationCompatPolicy.isTreatmentEnabledForActivity(activityRecord)) {
                return false;
            }
            Slog.w("ActivityTaskManager", "Ignoring orientation update to " + ActivityInfo.screenOrientationToString(i) + " due to camera compat treatment for " + this.mActivityRecord);
            return true;
        }
        return false;
    }

    public boolean shouldSendFakeFocus() {
        LetterboxConfiguration letterboxConfiguration = this.mLetterboxConfiguration;
        Objects.requireNonNull(letterboxConfiguration);
        return shouldEnableWithOverrideAndProperty(new LetterboxUiController$$ExternalSyntheticLambda4(letterboxConfiguration), this.mIsOverrideEnableCompatFakeFocusEnabled, this.mBooleanPropertyFakeFocus);
    }

    public void setRelauchingAfterRequestedOrientationChanged(boolean z) {
        this.mIsRelauchingAfterRequestedOrientationChanged = z;
    }

    public boolean isRefreshAfterRotationRequested() {
        return this.mIsRefreshAfterRotationRequested;
    }

    public void setIsRefreshAfterRotationRequested(boolean z) {
        this.mIsRefreshAfterRotationRequested = z;
    }

    public boolean isOverrideRespectRequestedOrientationEnabled() {
        return this.mIsOverrideRespectRequestedOrientationEnabled;
    }

    public boolean shouldUseDisplayLandscapeNaturalOrientation() {
        return shouldEnableWithOptInOverrideAndOptOutProperty(new BooleanSupplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda1
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$shouldUseDisplayLandscapeNaturalOrientation$4;
                lambda$shouldUseDisplayLandscapeNaturalOrientation$4 = LetterboxUiController.this.lambda$shouldUseDisplayLandscapeNaturalOrientation$4();
                return lambda$shouldUseDisplayLandscapeNaturalOrientation$4;
            }
        }, this.mIsOverrideUseDisplayLandscapeNaturalOrientationEnabled, this.mBooleanPropertyAllowDisplayOrientationOverride);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$shouldUseDisplayLandscapeNaturalOrientation$4() {
        ActivityRecord activityRecord = this.mActivityRecord;
        return (activityRecord.mDisplayContent == null || activityRecord.getTask() == null || !this.mActivityRecord.mDisplayContent.getIgnoreOrientationRequest() || this.mActivityRecord.getTask().inMultiWindowMode() || this.mActivityRecord.mDisplayContent.getNaturalOrientation() != 2) ? false : true;
    }

    public int overrideOrientationIfNeeded(int i) {
        DisplayRotationCompatPolicy displayRotationCompatPolicy;
        if (Boolean.FALSE.equals(this.mBooleanPropertyAllowOrientationOverride)) {
            return i;
        }
        ActivityRecord activityRecord = this.mActivityRecord;
        DisplayContent displayContent = activityRecord.mDisplayContent;
        if (!this.mIsOverrideOrientationOnlyForCameraEnabled || displayContent == null || ((displayRotationCompatPolicy = displayContent.mDisplayRotationCompatPolicy) != null && displayRotationCompatPolicy.isActivityEligibleForOrientationOverride(activityRecord))) {
            if (this.mIsOverrideToReverseLandscapeOrientationEnabled && (ActivityInfo.isFixedOrientationLandscape(i) || this.mIsOverrideAnyOrientationEnabled)) {
                Slog.w("ActivityTaskManager", "Requested orientation  " + ActivityInfo.screenOrientationToString(i) + " for " + this.mActivityRecord + " is overridden to " + ActivityInfo.screenOrientationToString(8));
                return 8;
            } else if (this.mIsOverrideAnyOrientationEnabled || !ActivityInfo.isFixedOrientation(i)) {
                if (this.mIsOverrideToPortraitOrientationEnabled) {
                    Slog.w("ActivityTaskManager", "Requested orientation  " + ActivityInfo.screenOrientationToString(i) + " for " + this.mActivityRecord + " is overridden to " + ActivityInfo.screenOrientationToString(1));
                    return 1;
                } else if (this.mIsOverrideToNosensorOrientationEnabled) {
                    Slog.w("ActivityTaskManager", "Requested orientation  " + ActivityInfo.screenOrientationToString(i) + " for " + this.mActivityRecord + " is overridden to " + ActivityInfo.screenOrientationToString(5));
                    return 5;
                } else {
                    return i;
                }
            } else {
                return i;
            }
        }
        return i;
    }

    public boolean isOverrideOrientationOnlyForCameraEnabled() {
        return this.mIsOverrideOrientationOnlyForCameraEnabled;
    }

    public boolean shouldRefreshActivityForCameraCompat() {
        return shouldEnableWithOptOutOverrideAndProperty(new BooleanSupplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda16
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$shouldRefreshActivityForCameraCompat$5;
                lambda$shouldRefreshActivityForCameraCompat$5 = LetterboxUiController.this.lambda$shouldRefreshActivityForCameraCompat$5();
                return lambda$shouldRefreshActivityForCameraCompat$5;
            }
        }, this.mIsOverrideCameraCompatDisableRefreshEnabled, this.mBooleanPropertyCameraCompatAllowRefresh);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$shouldRefreshActivityForCameraCompat$5() {
        return this.mLetterboxConfiguration.isCameraCompatTreatmentEnabled(true);
    }

    public boolean shouldRefreshActivityViaPauseForCameraCompat() {
        return shouldEnableWithOverrideAndProperty(new BooleanSupplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda15
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$shouldRefreshActivityViaPauseForCameraCompat$6;
                lambda$shouldRefreshActivityViaPauseForCameraCompat$6 = LetterboxUiController.this.lambda$shouldRefreshActivityViaPauseForCameraCompat$6();
                return lambda$shouldRefreshActivityViaPauseForCameraCompat$6;
            }
        }, this.mIsOverrideCameraCompatEnableRefreshViaPauseEnabled, this.mBooleanPropertyCameraCompatEnableRefreshViaPause);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$shouldRefreshActivityViaPauseForCameraCompat$6() {
        return this.mLetterboxConfiguration.isCameraCompatTreatmentEnabled(true);
    }

    public boolean shouldForceRotateForCameraCompat() {
        return shouldEnableWithOptOutOverrideAndProperty(new BooleanSupplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda14
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$shouldForceRotateForCameraCompat$7;
                lambda$shouldForceRotateForCameraCompat$7 = LetterboxUiController.this.lambda$shouldForceRotateForCameraCompat$7();
                return lambda$shouldForceRotateForCameraCompat$7;
            }
        }, this.mIsOverrideCameraCompatDisableForceRotationEnabled, this.mBooleanPropertyCameraCompatAllowForceRotation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$shouldForceRotateForCameraCompat$7() {
        return this.mLetterboxConfiguration.isCameraCompatTreatmentEnabled(true);
    }

    public final boolean isCompatChangeEnabled(long j) {
        return this.mActivityRecord.info.isChangeEnabled(j);
    }

    public final boolean shouldEnableWithOptOutOverrideAndProperty(BooleanSupplier booleanSupplier, boolean z, Boolean bool) {
        return (!booleanSupplier.getAsBoolean() || Boolean.FALSE.equals(bool) || z) ? false : true;
    }

    public final boolean shouldEnableWithOptInOverrideAndOptOutProperty(BooleanSupplier booleanSupplier, boolean z, Boolean bool) {
        return booleanSupplier.getAsBoolean() && !Boolean.FALSE.equals(bool) && z;
    }

    public final boolean shouldEnableWithOverrideAndProperty(BooleanSupplier booleanSupplier, boolean z, Boolean bool) {
        if (booleanSupplier.getAsBoolean() && !Boolean.FALSE.equals(bool)) {
            return Boolean.TRUE.equals(bool) || z;
        }
        return false;
    }

    public boolean hasWallpaperBackgroundForLetterbox() {
        return this.mShowWallpaperForLetterboxBackground;
    }

    public Rect getLetterboxInsets() {
        Letterbox letterbox = this.mLetterbox;
        if (letterbox != null) {
            return letterbox.getInsets();
        }
        return new Rect();
    }

    public void getLetterboxInnerBounds(Rect rect) {
        Letterbox letterbox = this.mLetterbox;
        if (letterbox != null) {
            rect.set(letterbox.getInnerFrame());
            WindowState findMainWindow = this.mActivityRecord.findMainWindow();
            if (findMainWindow != null) {
                adjustBoundsForTaskbar(findMainWindow, rect);
                return;
            }
            return;
        }
        rect.setEmpty();
    }

    public final void getLetterboxOuterBounds(Rect rect) {
        Letterbox letterbox = this.mLetterbox;
        if (letterbox != null) {
            rect.set(letterbox.getOuterFrame());
        } else {
            rect.setEmpty();
        }
    }

    public boolean isFullyTransparentBarAllowed(Rect rect) {
        Letterbox letterbox = this.mLetterbox;
        return letterbox == null || letterbox.notIntersectsOrFullyContains(rect);
    }

    public void updateLetterboxSurface(WindowState windowState) {
        updateLetterboxSurface(windowState, this.mActivityRecord.getSyncTransaction());
    }

    public void updateLetterboxSurface(WindowState windowState, SurfaceControl.Transaction transaction) {
        WindowState findMainWindow = this.mActivityRecord.findMainWindow();
        if (findMainWindow == windowState || windowState == null || findMainWindow == null) {
            layoutLetterbox(windowState);
            Letterbox letterbox = this.mLetterbox;
            if (letterbox == null || !letterbox.needsApplySurfaceChanges()) {
                return;
            }
            this.mLetterbox.applySurfaceChanges(transaction);
        }
    }

    public void layoutLetterbox(WindowState windowState) {
        WindowState findMainWindow = this.mActivityRecord.findMainWindow();
        if (findMainWindow != null) {
            if (windowState == null || findMainWindow == windowState) {
                updateRoundedCornersIfNeeded(findMainWindow);
                WindowState findMainWindow2 = this.mActivityRecord.findMainWindow(false);
                if (findMainWindow2 != null && findMainWindow2 != findMainWindow) {
                    updateRoundedCornersIfNeeded(findMainWindow2);
                }
                updateWallpaperForLetterbox(findMainWindow);
                if (shouldShowLetterboxUi(findMainWindow)) {
                    if (this.mLetterbox == null) {
                        Letterbox letterbox = new Letterbox(new Supplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda5
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                SurfaceControl.Builder lambda$layoutLetterbox$8;
                                lambda$layoutLetterbox$8 = LetterboxUiController.this.lambda$layoutLetterbox$8();
                                return lambda$layoutLetterbox$8;
                            }
                        }, this.mActivityRecord.mWmService.mTransactionFactory, new Supplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda6
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                boolean shouldLetterboxHaveRoundedCorners;
                                shouldLetterboxHaveRoundedCorners = LetterboxUiController.this.shouldLetterboxHaveRoundedCorners();
                                return Boolean.valueOf(shouldLetterboxHaveRoundedCorners);
                            }
                        }, new Supplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda7
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                Color letterboxBackgroundColor;
                                letterboxBackgroundColor = LetterboxUiController.this.getLetterboxBackgroundColor();
                                return letterboxBackgroundColor;
                            }
                        }, new Supplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda8
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                return Boolean.valueOf(LetterboxUiController.this.hasWallpaperBackgroundForLetterbox());
                            }
                        }, new Supplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda9
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                int letterboxWallpaperBlurRadius;
                                letterboxWallpaperBlurRadius = LetterboxUiController.this.getLetterboxWallpaperBlurRadius();
                                return Integer.valueOf(letterboxWallpaperBlurRadius);
                            }
                        }, new Supplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda10
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                float letterboxWallpaperDarkScrimAlpha;
                                letterboxWallpaperDarkScrimAlpha = LetterboxUiController.this.getLetterboxWallpaperDarkScrimAlpha();
                                return Float.valueOf(letterboxWallpaperDarkScrimAlpha);
                            }
                        }, new IntConsumer() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda11
                            @Override // java.util.function.IntConsumer
                            public final void accept(int i) {
                                LetterboxUiController.this.handleHorizontalDoubleTap(i);
                            }
                        }, new IntConsumer() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda12
                            @Override // java.util.function.IntConsumer
                            public final void accept(int i) {
                                LetterboxUiController.this.handleVerticalDoubleTap(i);
                            }
                        }, new Supplier() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda13
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                return LetterboxUiController.this.getLetterboxParentSurface();
                            }
                        });
                        this.mLetterbox = letterbox;
                        letterbox.attachInput(findMainWindow);
                    }
                    if (this.mActivityRecord.isInLetterboxAnimation()) {
                        this.mActivityRecord.getTask().getPosition(this.mTmpPoint);
                    } else {
                        this.mActivityRecord.getPosition(this.mTmpPoint);
                    }
                    Rect fixedRotationTransformDisplayBounds = this.mActivityRecord.getFixedRotationTransformDisplayBounds();
                    if (fixedRotationTransformDisplayBounds == null) {
                        if (this.mActivityRecord.inMultiWindowMode()) {
                            fixedRotationTransformDisplayBounds = this.mActivityRecord.getTask().getBounds();
                        } else {
                            fixedRotationTransformDisplayBounds = this.mActivityRecord.getRootTask().getParent().getBounds();
                        }
                    }
                    this.mLetterbox.layout(fixedRotationTransformDisplayBounds, hasInheritedLetterboxBehavior() ? this.mActivityRecord.getBounds() : findMainWindow.getFrame(), this.mTmpPoint);
                    return;
                }
                Letterbox letterbox2 = this.mLetterbox;
                if (letterbox2 != null) {
                    letterbox2.hide();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ SurfaceControl.Builder lambda$layoutLetterbox$8() {
        return this.mActivityRecord.makeChildSurface(null);
    }

    public SurfaceControl getLetterboxParentSurface() {
        if (this.mActivityRecord.isInLetterboxAnimation()) {
            return this.mActivityRecord.getTask().getSurfaceControl();
        }
        return this.mActivityRecord.getSurfaceControl();
    }

    public final boolean shouldLetterboxHaveRoundedCorners() {
        return this.mLetterboxConfiguration.isLetterboxActivityCornersRounded() && this.mActivityRecord.fillsParent();
    }

    public final boolean isDisplayFullScreenAndInPosture(DeviceStateController.DeviceState deviceState, boolean z) {
        Task task = this.mActivityRecord.getTask();
        DisplayContent displayContent = this.mActivityRecord.mDisplayContent;
        return displayContent != null && displayContent.getDisplayRotation().isDeviceInPosture(deviceState, z) && task != null && task.getWindowingMode() == 1;
    }

    public final boolean isDisplayFullScreenAndSeparatingHinge() {
        Task task = this.mActivityRecord.getTask();
        DisplayContent displayContent = this.mActivityRecord.mDisplayContent;
        return displayContent != null && displayContent.getDisplayRotation().isDisplaySeparatingHinge() && task != null && task.getWindowingMode() == 1;
    }

    public float getHorizontalPositionMultiplier(Configuration configuration) {
        boolean isDisplayFullScreenAndInPosture = isDisplayFullScreenAndInPosture(DeviceStateController.DeviceState.HALF_FOLDED, false);
        if (isHorizontalReachabilityEnabled(configuration)) {
            return this.mLetterboxConfiguration.getHorizontalMultiplierForReachability(isDisplayFullScreenAndInPosture);
        }
        return this.mLetterboxConfiguration.getLetterboxHorizontalPositionMultiplier(isDisplayFullScreenAndInPosture);
    }

    public float getVerticalPositionMultiplier(Configuration configuration) {
        boolean isDisplayFullScreenAndInPosture = isDisplayFullScreenAndInPosture(DeviceStateController.DeviceState.HALF_FOLDED, true);
        if (isVerticalReachabilityEnabled(configuration)) {
            return this.mLetterboxConfiguration.getVerticalMultiplierForReachability(isDisplayFullScreenAndInPosture);
        }
        return this.mLetterboxConfiguration.getLetterboxVerticalPositionMultiplier(isDisplayFullScreenAndInPosture);
    }

    public float getFixedOrientationLetterboxAspectRatio() {
        if (isDisplayFullScreenAndSeparatingHinge()) {
            return getSplitScreenAspectRatio();
        }
        if (this.mActivityRecord.shouldCreateCompatDisplayInsets()) {
            return getDefaultMinAspectRatioForUnresizableApps();
        }
        return getDefaultMinAspectRatio();
    }

    public final float getDefaultMinAspectRatioForUnresizableApps() {
        if (!this.mLetterboxConfiguration.getIsSplitScreenAspectRatioForUnresizableAppsEnabled() || this.mActivityRecord.getDisplayContent() == null) {
            if (this.mLetterboxConfiguration.getDefaultMinAspectRatioForUnresizableApps() > 1.0f) {
                return this.mLetterboxConfiguration.getDefaultMinAspectRatioForUnresizableApps();
            }
            return getDefaultMinAspectRatio();
        }
        return getSplitScreenAspectRatio();
    }

    public float getSplitScreenAspectRatio() {
        DisplayContent displayContent = this.mActivityRecord.getDisplayContent();
        if (displayContent == null) {
            return getDefaultMinAspectRatioForUnresizableApps();
        }
        int dimensionPixelSize = getResources().getDimensionPixelSize(17105206) - (getResources().getDimensionPixelSize(17105205) * 2);
        Rect rect = new Rect(displayContent.getBounds());
        if (rect.width() >= rect.height()) {
            rect.inset(dimensionPixelSize / 2, 0);
            rect.right = rect.centerX();
        } else {
            rect.inset(0, dimensionPixelSize / 2);
            rect.bottom = rect.centerY();
        }
        return ActivityRecord.computeAspectRatio(rect);
    }

    public final float getDefaultMinAspectRatio() {
        DisplayContent displayContent = this.mActivityRecord.getDisplayContent();
        if (displayContent == null || !this.mLetterboxConfiguration.getIsDisplayAspectRatioEnabledForFixedOrientationLetterbox()) {
            return this.mLetterboxConfiguration.getFixedOrientationLetterboxAspectRatio();
        }
        return ActivityRecord.computeAspectRatio(new Rect(displayContent.getBounds()));
    }

    public Resources getResources() {
        return this.mActivityRecord.mWmService.mContext.getResources();
    }

    @VisibleForTesting
    public void handleHorizontalDoubleTap(int i) {
        if (!isHorizontalReachabilityEnabled() || this.mActivityRecord.isInTransition()) {
            return;
        }
        if (this.mLetterbox.getInnerFrame().left > i || this.mLetterbox.getInnerFrame().right < i) {
            boolean isDisplayFullScreenAndSeparatingHinge = isDisplayFullScreenAndSeparatingHinge();
            int letterboxPositionForHorizontalReachability = this.mLetterboxConfiguration.getLetterboxPositionForHorizontalReachability(isDisplayFullScreenAndSeparatingHinge);
            if (this.mLetterbox.getInnerFrame().left > i) {
                this.mLetterboxConfiguration.movePositionForHorizontalReachabilityToNextLeftStop(isDisplayFullScreenAndSeparatingHinge);
                logLetterboxPositionChange(letterboxPositionForHorizontalReachability != 1 ? 4 : 1);
            } else if (this.mLetterbox.getInnerFrame().right < i) {
                this.mLetterboxConfiguration.movePositionForHorizontalReachabilityToNextRightStop(isDisplayFullScreenAndSeparatingHinge);
                logLetterboxPositionChange(letterboxPositionForHorizontalReachability == 1 ? 3 : 2);
            }
            this.mActivityRecord.recomputeConfiguration();
        }
    }

    @VisibleForTesting
    public void handleVerticalDoubleTap(int i) {
        if (!isVerticalReachabilityEnabled() || this.mActivityRecord.isInTransition()) {
            return;
        }
        if (this.mLetterbox.getInnerFrame().top > i || this.mLetterbox.getInnerFrame().bottom < i) {
            boolean isDisplayFullScreenAndSeparatingHinge = isDisplayFullScreenAndSeparatingHinge();
            int letterboxPositionForVerticalReachability = this.mLetterboxConfiguration.getLetterboxPositionForVerticalReachability(isDisplayFullScreenAndSeparatingHinge);
            if (this.mLetterbox.getInnerFrame().top > i) {
                this.mLetterboxConfiguration.movePositionForVerticalReachabilityToNextTopStop(isDisplayFullScreenAndSeparatingHinge);
                logLetterboxPositionChange(letterboxPositionForVerticalReachability == 1 ? 5 : 8);
            } else if (this.mLetterbox.getInnerFrame().bottom < i) {
                this.mLetterboxConfiguration.movePositionForVerticalReachabilityToNextBottomStop(isDisplayFullScreenAndSeparatingHinge);
                logLetterboxPositionChange(letterboxPositionForVerticalReachability == 1 ? 7 : 6);
            }
            this.mActivityRecord.recomputeConfiguration();
        }
    }

    public final boolean isHorizontalReachabilityEnabled(Configuration configuration) {
        return this.mLetterboxConfiguration.getIsHorizontalReachabilityEnabled() && configuration.windowConfiguration.getWindowingMode() == 1 && configuration.orientation == 2 && this.mActivityRecord.getOrientationForReachability() == 1 && configuration.windowConfiguration.getAppBounds().height() <= this.mActivityRecord.getBounds().height();
    }

    @VisibleForTesting
    public boolean isHorizontalReachabilityEnabled() {
        return isHorizontalReachabilityEnabled(this.mActivityRecord.getParent().getConfiguration());
    }

    public final boolean isVerticalReachabilityEnabled(Configuration configuration) {
        return this.mLetterboxConfiguration.getIsVerticalReachabilityEnabled() && configuration.windowConfiguration.getWindowingMode() == 1 && configuration.orientation == 1 && this.mActivityRecord.getOrientationForReachability() == 2 && configuration.windowConfiguration.getBounds().width() == this.mActivityRecord.getBounds().width();
    }

    @VisibleForTesting
    public boolean isVerticalReachabilityEnabled() {
        return isVerticalReachabilityEnabled(this.mActivityRecord.getParent().getConfiguration());
    }

    @VisibleForTesting
    public boolean shouldShowLetterboxUi(WindowState windowState) {
        return isSurfaceVisible(windowState) && windowState.areAppWindowBoundsLetterboxed() && (windowState.getAttrs().flags & 1048576) == 0;
    }

    @VisibleForTesting
    public boolean isSurfaceVisible(WindowState windowState) {
        return windowState.isOnScreen() && (this.mActivityRecord.isVisible() || this.mActivityRecord.isVisibleRequested());
    }

    public final Color getLetterboxBackgroundColor() {
        WindowState findMainWindow = this.mActivityRecord.findMainWindow();
        if (findMainWindow == null || findMainWindow.isLetterboxedForDisplayCutout()) {
            return Color.valueOf(-16777216);
        }
        int letterboxBackgroundType = this.mLetterboxConfiguration.getLetterboxBackgroundType();
        ActivityManager.TaskDescription taskDescription = this.mActivityRecord.taskDescription;
        if (letterboxBackgroundType == 0) {
            return this.mLetterboxConfiguration.getLetterboxBackgroundColor();
        }
        if (letterboxBackgroundType != 1) {
            if (letterboxBackgroundType != 2) {
                if (letterboxBackgroundType == 3) {
                    if (hasWallpaperBackgroundForLetterbox()) {
                        return Color.valueOf(-16777216);
                    }
                    Slog.w("ActivityTaskManager", "Wallpaper option is selected for letterbox background but blur is not supported by a device or not supported in the current window configuration or both alpha scrim and blur radius aren't provided so using solid color background");
                } else {
                    throw new AssertionError("Unexpected letterbox background type: " + letterboxBackgroundType);
                }
            } else if (taskDescription != null && taskDescription.getBackgroundColorFloating() != 0) {
                return Color.valueOf(taskDescription.getBackgroundColorFloating());
            }
        } else if (taskDescription != null && taskDescription.getBackgroundColor() != 0) {
            return Color.valueOf(taskDescription.getBackgroundColor());
        }
        return this.mLetterboxConfiguration.getLetterboxBackgroundColor();
    }

    public final void updateRoundedCornersIfNeeded(WindowState windowState) {
        SurfaceControl surfaceControl = windowState.getSurfaceControl();
        if (surfaceControl == null || !surfaceControl.isValid()) {
            return;
        }
        this.mActivityRecord.getSyncTransaction().setCrop(surfaceControl, getCropBoundsIfNeeded(windowState)).setCornerRadius(surfaceControl, getRoundedCornersRadius(windowState));
    }

    @VisibleForTesting
    public Rect getCropBoundsIfNeeded(WindowState windowState) {
        if (!requiresRoundedCorners(windowState) || this.mActivityRecord.isInLetterboxAnimation()) {
            return null;
        }
        Rect rect = new Rect(this.mActivityRecord.getBounds());
        adjustBoundsForTaskbar(windowState, rect);
        float f = windowState.mInvGlobalScale;
        if (f != 1.0f && f > 0.0f) {
            rect.scale(f);
        }
        rect.offsetTo(0, 0);
        return rect;
    }

    public final boolean requiresRoundedCorners(WindowState windowState) {
        return isLetterboxedNotForDisplayCutout(windowState) && this.mLetterboxConfiguration.isLetterboxActivityCornersRounded();
    }

    public int getRoundedCornersRadius(WindowState windowState) {
        int min;
        if (!requiresRoundedCorners(windowState) || this.mActivityRecord.isInLetterboxAnimation()) {
            return 0;
        }
        if (this.mLetterboxConfiguration.getLetterboxActivityCornersRadius() >= 0) {
            min = this.mLetterboxConfiguration.getLetterboxActivityCornersRadius();
        } else {
            InsetsState insetsState = windowState.getInsetsState();
            min = Math.min(getInsetsStateCornerRadius(insetsState, 3), getInsetsStateCornerRadius(insetsState, 2));
        }
        float f = windowState.mInvGlobalScale;
        return (f == 1.0f || f <= 0.0f) ? min : (int) (f * min);
    }

    @VisibleForTesting
    public InsetsSource getExpandedTaskbarOrNull(WindowState windowState) {
        InsetsState insetsState = windowState.getInsetsState();
        for (int sourceSize = insetsState.sourceSize() - 1; sourceSize >= 0; sourceSize--) {
            InsetsSource sourceAt = insetsState.sourceAt(sourceSize);
            if (sourceAt.getType() == WindowInsets.Type.navigationBars() && sourceAt.insetsRoundedCornerFrame() && sourceAt.isVisible()) {
                return sourceAt;
            }
        }
        return null;
    }

    public final void adjustBoundsForTaskbar(WindowState windowState, Rect rect) {
        InsetsSource expandedTaskbarOrNull = getExpandedTaskbarOrNull(windowState);
        if (expandedTaskbarOrNull != null) {
            rect.bottom = Math.min(rect.bottom, expandedTaskbarOrNull.getFrame().top);
        }
    }

    public final int getInsetsStateCornerRadius(InsetsState insetsState, int i) {
        RoundedCorner roundedCorner = insetsState.getRoundedCorners().getRoundedCorner(i);
        if (roundedCorner == null) {
            return 0;
        }
        return roundedCorner.getRadius();
    }

    public final boolean isLetterboxedNotForDisplayCutout(WindowState windowState) {
        return shouldShowLetterboxUi(windowState) && !windowState.isLetterboxedForDisplayCutout();
    }

    public final void updateWallpaperForLetterbox(WindowState windowState) {
        boolean z = this.mLetterboxConfiguration.getLetterboxBackgroundType() == 3 && isLetterboxedNotForDisplayCutout(windowState) && (getLetterboxWallpaperBlurRadius() > 0 || getLetterboxWallpaperDarkScrimAlpha() > 0.0f) && (getLetterboxWallpaperBlurRadius() <= 0 || isLetterboxWallpaperBlurSupported());
        if (this.mShowWallpaperForLetterboxBackground != z) {
            this.mShowWallpaperForLetterboxBackground = z;
            this.mActivityRecord.requestUpdateWallpaperIfNeeded();
        }
    }

    public final int getLetterboxWallpaperBlurRadius() {
        int letterboxBackgroundWallpaperBlurRadius = this.mLetterboxConfiguration.getLetterboxBackgroundWallpaperBlurRadius();
        if (letterboxBackgroundWallpaperBlurRadius < 0) {
            return 0;
        }
        return letterboxBackgroundWallpaperBlurRadius;
    }

    public final float getLetterboxWallpaperDarkScrimAlpha() {
        float letterboxBackgroundWallpaperDarkScrimAlpha = this.mLetterboxConfiguration.getLetterboxBackgroundWallpaperDarkScrimAlpha();
        if (letterboxBackgroundWallpaperDarkScrimAlpha < 0.0f || letterboxBackgroundWallpaperDarkScrimAlpha >= 1.0f) {
            return 0.0f;
        }
        return letterboxBackgroundWallpaperDarkScrimAlpha;
    }

    public final boolean isLetterboxWallpaperBlurSupported() {
        return ((WindowManager) this.mLetterboxConfiguration.mContext.getSystemService(WindowManager.class)).isCrossWindowBlurEnabled();
    }

    public void dump(PrintWriter printWriter, String str) {
        WindowState findMainWindow = this.mActivityRecord.findMainWindow();
        if (findMainWindow == null) {
            return;
        }
        boolean areAppWindowBoundsLetterboxed = findMainWindow.areAppWindowBoundsLetterboxed();
        printWriter.println(str + "areBoundsLetterboxed=" + areAppWindowBoundsLetterboxed);
        if (areAppWindowBoundsLetterboxed) {
            printWriter.println(str + "  letterboxReason=" + getLetterboxReasonString(findMainWindow));
            printWriter.println(str + "  activityAspectRatio=" + ActivityRecord.computeAspectRatio(this.mActivityRecord.getBounds()));
            boolean shouldShowLetterboxUi = shouldShowLetterboxUi(findMainWindow);
            printWriter.println(str + "shouldShowLetterboxUi=" + shouldShowLetterboxUi);
            if (shouldShowLetterboxUi) {
                printWriter.println(str + "  letterboxBackgroundColor=" + Integer.toHexString(getLetterboxBackgroundColor().toArgb()));
                printWriter.println(str + "  letterboxBackgroundType=" + LetterboxConfiguration.letterboxBackgroundTypeToString(this.mLetterboxConfiguration.getLetterboxBackgroundType()));
                printWriter.println(str + "  letterboxCornerRadius=" + getRoundedCornersRadius(findMainWindow));
                if (this.mLetterboxConfiguration.getLetterboxBackgroundType() == 3) {
                    printWriter.println(str + "  isLetterboxWallpaperBlurSupported=" + isLetterboxWallpaperBlurSupported());
                    printWriter.println(str + "  letterboxBackgroundWallpaperDarkScrimAlpha=" + getLetterboxWallpaperDarkScrimAlpha());
                    printWriter.println(str + "  letterboxBackgroundWallpaperBlurRadius=" + getLetterboxWallpaperBlurRadius());
                }
                printWriter.println(str + "  isHorizontalReachabilityEnabled=" + isHorizontalReachabilityEnabled());
                printWriter.println(str + "  isVerticalReachabilityEnabled=" + isVerticalReachabilityEnabled());
                printWriter.println(str + "  letterboxHorizontalPositionMultiplier=" + getHorizontalPositionMultiplier(this.mActivityRecord.getParent().getConfiguration()));
                printWriter.println(str + "  letterboxVerticalPositionMultiplier=" + getVerticalPositionMultiplier(this.mActivityRecord.getParent().getConfiguration()));
                printWriter.println(str + "  letterboxPositionForHorizontalReachability=" + LetterboxConfiguration.letterboxHorizontalReachabilityPositionToString(this.mLetterboxConfiguration.getLetterboxPositionForHorizontalReachability(false)));
                printWriter.println(str + "  letterboxPositionForVerticalReachability=" + LetterboxConfiguration.letterboxVerticalReachabilityPositionToString(this.mLetterboxConfiguration.getLetterboxPositionForVerticalReachability(false)));
                printWriter.println(str + "  fixedOrientationLetterboxAspectRatio=" + this.mLetterboxConfiguration.getFixedOrientationLetterboxAspectRatio());
                printWriter.println(str + "  defaultMinAspectRatioForUnresizableApps=" + this.mLetterboxConfiguration.getDefaultMinAspectRatioForUnresizableApps());
                printWriter.println(str + "  isSplitScreenAspectRatioForUnresizableAppsEnabled=" + this.mLetterboxConfiguration.getIsSplitScreenAspectRatioForUnresizableAppsEnabled());
                printWriter.println(str + "  isDisplayAspectRatioEnabledForFixedOrientationLetterbox=" + this.mLetterboxConfiguration.getIsDisplayAspectRatioEnabledForFixedOrientationLetterbox());
            }
        }
    }

    public final String getLetterboxReasonString(WindowState windowState) {
        return this.mActivityRecord.inSizeCompatMode() ? "SIZE_COMPAT_MODE" : this.mActivityRecord.isLetterboxedForFixedOrientationAndAspectRatio() ? "FIXED_ORIENTATION" : windowState.isLetterboxedForDisplayCutout() ? "DISPLAY_CUTOUT" : this.mActivityRecord.isAspectRatioApplied() ? "ASPECT_RATIO" : "UNKNOWN_REASON";
    }

    public final int letterboxHorizontalReachabilityPositionToLetterboxPosition(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return 4;
                }
                throw new AssertionError("Unexpected letterbox horizontal reachability position type: " + i);
            }
            return 2;
        }
        return 3;
    }

    public final int letterboxVerticalReachabilityPositionToLetterboxPosition(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return 6;
                }
                throw new AssertionError("Unexpected letterbox vertical reachability position type: " + i);
            }
            return 2;
        }
        return 5;
    }

    public int getLetterboxPositionForLogging() {
        if (isHorizontalReachabilityEnabled()) {
            return letterboxHorizontalReachabilityPositionToLetterboxPosition(getLetterboxConfiguration().getLetterboxPositionForHorizontalReachability(isDisplayFullScreenAndInPosture(DeviceStateController.DeviceState.HALF_FOLDED, false)));
        }
        if (isVerticalReachabilityEnabled()) {
            return letterboxVerticalReachabilityPositionToLetterboxPosition(getLetterboxConfiguration().getLetterboxPositionForVerticalReachability(isDisplayFullScreenAndInPosture(DeviceStateController.DeviceState.HALF_FOLDED, true)));
        }
        return 0;
    }

    public final LetterboxConfiguration getLetterboxConfiguration() {
        return this.mLetterboxConfiguration;
    }

    public final void logLetterboxPositionChange(int i) {
        this.mActivityRecord.mTaskSupervisor.getActivityMetricsLogger().logLetterboxPositionChange(this.mActivityRecord, i);
    }

    public LetterboxDetails getLetterboxDetails() {
        WindowState findMainWindow = this.mActivityRecord.findMainWindow();
        if (this.mLetterbox != null && findMainWindow != null && !findMainWindow.isLetterboxedForDisplayCutout()) {
            Rect rect = new Rect();
            Rect rect2 = new Rect();
            getLetterboxInnerBounds(rect);
            getLetterboxOuterBounds(rect2);
            if (!rect.isEmpty() && !rect2.isEmpty()) {
                return new LetterboxDetails(rect, rect2, findMainWindow.mAttrs.insetsFlags.appearance);
            }
        }
        return null;
    }

    public void onActivityParentChanged(final WindowContainer<?> windowContainer) {
        final ActivityRecord activity;
        if (this.mLetterboxConfiguration.isTranslucentLetterboxingEnabled()) {
            WindowContainerListener windowContainerListener = this.mLetterboxConfigListener;
            if (windowContainerListener != null) {
                windowContainerListener.onRemoved();
                clearInheritedConfig();
            }
            if (this.mActivityRecord.getTask() == null || this.mActivityRecord.fillsParent() || this.mActivityRecord.hasCompatDisplayInsetsWithoutInheritance() || (activity = this.mActivityRecord.getTask().getActivity(FIRST_OPAQUE_NOT_FINISHING_ACTIVITY_PREDICATE, this.mActivityRecord, false, true)) == null) {
                return;
            }
            inheritConfiguration(activity);
            this.mLetterboxConfigListener = WindowContainer.overrideConfigurationPropagation(this.mActivityRecord, activity, new WindowContainer.ConfigurationMerger() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda3
                @Override // com.android.server.p014wm.WindowContainer.ConfigurationMerger
                public final Configuration merge(Configuration configuration, Configuration configuration2) {
                    Configuration lambda$onActivityParentChanged$9;
                    lambda$onActivityParentChanged$9 = LetterboxUiController.this.lambda$onActivityParentChanged$9(windowContainer, activity, configuration, configuration2);
                    return lambda$onActivityParentChanged$9;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Configuration lambda$onActivityParentChanged$9(WindowContainer windowContainer, ActivityRecord activityRecord, Configuration configuration, Configuration configuration2) {
        Configuration configuration3 = new Configuration();
        Rect bounds = windowContainer.getWindowConfiguration().getBounds();
        Rect bounds2 = configuration3.windowConfiguration.getBounds();
        Rect bounds3 = configuration.windowConfiguration.getBounds();
        int i = bounds.left;
        bounds2.set(i, bounds.top, bounds3.width() + i, bounds.top + bounds3.height());
        configuration3.windowConfiguration.setAppBounds(new Rect());
        inheritConfiguration(activityRecord);
        return configuration3;
    }

    public boolean hasInheritedLetterboxBehavior() {
        return this.mLetterboxConfigListener != null;
    }

    public boolean hasInheritedOrientation() {
        return hasInheritedLetterboxBehavior() && this.mActivityRecord.getOverrideOrientation() != -1;
    }

    public float getInheritedMinAspectRatio() {
        return this.mInheritedMinAspectRatio;
    }

    public float getInheritedMaxAspectRatio() {
        return this.mInheritedMaxAspectRatio;
    }

    public int getInheritedAppCompatState() {
        return this.mInheritedAppCompatState;
    }

    @Configuration.Orientation
    public int getInheritedOrientation() {
        return this.mInheritedOrientation;
    }

    public ActivityRecord.CompatDisplayInsets getInheritedCompatDisplayInsets() {
        return this.mInheritedCompatDisplayInsets;
    }

    public boolean applyOnOpaqueActivityBelow(final Consumer<ActivityRecord> consumer) {
        return ((Boolean) findOpaqueNotFinishingActivityBelow().map(new Function() { // from class: com.android.server.wm.LetterboxUiController$$ExternalSyntheticLambda17
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean lambda$applyOnOpaqueActivityBelow$10;
                lambda$applyOnOpaqueActivityBelow$10 = LetterboxUiController.lambda$applyOnOpaqueActivityBelow$10(consumer, (ActivityRecord) obj);
                return lambda$applyOnOpaqueActivityBelow$10;
            }
        }).orElse(Boolean.FALSE)).booleanValue();
    }

    public static /* synthetic */ Boolean lambda$applyOnOpaqueActivityBelow$10(Consumer consumer, ActivityRecord activityRecord) {
        consumer.accept(activityRecord);
        return Boolean.TRUE;
    }

    public Optional<ActivityRecord> findOpaqueNotFinishingActivityBelow() {
        if (!hasInheritedLetterboxBehavior() || this.mActivityRecord.getTask() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.mActivityRecord.getTask().getActivity(FIRST_OPAQUE_NOT_FINISHING_ACTIVITY_PREDICATE, this.mActivityRecord, false, true));
    }

    public final void inheritConfiguration(ActivityRecord activityRecord) {
        if (this.mActivityRecord.getMinAspectRatio() != 0.0f) {
            this.mInheritedMinAspectRatio = activityRecord.getMinAspectRatio();
        }
        if (this.mActivityRecord.getMaxAspectRatio() != 0.0f) {
            this.mInheritedMaxAspectRatio = activityRecord.getMaxAspectRatio();
        }
        this.mInheritedOrientation = activityRecord.getRequestedConfigurationOrientation();
        this.mInheritedAppCompatState = activityRecord.getAppCompatState();
        this.mInheritedCompatDisplayInsets = activityRecord.getCompatDisplayInsets();
    }

    public final void clearInheritedConfig() {
        this.mLetterboxConfigListener = null;
        this.mInheritedMinAspectRatio = 0.0f;
        this.mInheritedMaxAspectRatio = 0.0f;
        this.mInheritedOrientation = 0;
        this.mInheritedAppCompatState = 0;
        this.mInheritedCompatDisplayInsets = null;
    }
}
