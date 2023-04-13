package com.android.server.p014wm;

import android.content.Context;
import android.graphics.Color;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.util.function.Function;
import java.util.function.Supplier;
/* renamed from: com.android.server.wm.LetterboxConfiguration */
/* loaded from: classes2.dex */
public final class LetterboxConfiguration {
    public final Context mContext;
    public float mDefaultMinAspectRatioForUnresizableApps;
    public int mDefaultPositionForHorizontalReachability;
    public int mDefaultPositionForVerticalReachability;
    public final LetterboxConfigurationDeviceConfig mDeviceConfig;
    public float mFixedOrientationLetterboxAspectRatio;
    public boolean mIsCameraCompatRefreshCycleThroughStopEnabled;
    public final boolean mIsCameraCompatTreatmentEnabled;
    public boolean mIsCameraCompatTreatmentRefreshEnabled;
    public boolean mIsCompatFakeFocusEnabled;
    public boolean mIsDisplayAspectRatioEnabledForFixedOrientationLetterbox;
    public final boolean mIsDisplayRotationImmersiveAppCompatPolicyEnabled;
    public boolean mIsEducationEnabled;
    public boolean mIsHorizontalReachabilityEnabled;
    public final boolean mIsPolicyForIgnoringRequestedOrientationEnabled;
    public boolean mIsSplitScreenAspectRatioForUnresizableAppsEnabled;
    public boolean mIsVerticalReachabilityEnabled;
    public int mLetterboxActivityCornersRadius;
    public Color mLetterboxBackgroundColorOverride;
    public Integer mLetterboxBackgroundColorResourceIdOverride;
    public int mLetterboxBackgroundType;
    public int mLetterboxBackgroundWallpaperBlurRadius;
    public float mLetterboxBackgroundWallpaperDarkScrimAlpha;
    public float mLetterboxBookModePositionMultiplier;
    public final LetterboxConfigurationPersister mLetterboxConfigurationPersister;
    public float mLetterboxHorizontalPositionMultiplier;
    public float mLetterboxTabletopModePositionMultiplier;
    public float mLetterboxVerticalPositionMultiplier;
    public boolean mTranslucentLetterboxingEnabled;
    public boolean mTranslucentLetterboxingOverrideEnabled;

    public LetterboxConfiguration(final Context context) {
        this(context, new LetterboxConfigurationPersister(context, new Supplier() { // from class: com.android.server.wm.LetterboxConfiguration$$ExternalSyntheticLambda4
            @Override // java.util.function.Supplier
            public final Object get() {
                Integer lambda$new$0;
                lambda$new$0 = LetterboxConfiguration.lambda$new$0(context);
                return lambda$new$0;
            }
        }, new Supplier() { // from class: com.android.server.wm.LetterboxConfiguration$$ExternalSyntheticLambda5
            @Override // java.util.function.Supplier
            public final Object get() {
                Integer lambda$new$1;
                lambda$new$1 = LetterboxConfiguration.lambda$new$1(context);
                return lambda$new$1;
            }
        }, new Supplier() { // from class: com.android.server.wm.LetterboxConfiguration$$ExternalSyntheticLambda6
            @Override // java.util.function.Supplier
            public final Object get() {
                Integer lambda$new$2;
                lambda$new$2 = LetterboxConfiguration.lambda$new$2(context);
                return lambda$new$2;
            }
        }, new Supplier() { // from class: com.android.server.wm.LetterboxConfiguration$$ExternalSyntheticLambda7
            @Override // java.util.function.Supplier
            public final Object get() {
                Integer lambda$new$3;
                lambda$new$3 = LetterboxConfiguration.lambda$new$3(context);
                return lambda$new$3;
            }
        }));
    }

    public static /* synthetic */ Integer lambda$new$0(Context context) {
        return Integer.valueOf(readLetterboxHorizontalReachabilityPositionFromConfig(context, false));
    }

    public static /* synthetic */ Integer lambda$new$1(Context context) {
        return Integer.valueOf(readLetterboxVerticalReachabilityPositionFromConfig(context, false));
    }

    public static /* synthetic */ Integer lambda$new$2(Context context) {
        return Integer.valueOf(readLetterboxHorizontalReachabilityPositionFromConfig(context, true));
    }

    public static /* synthetic */ Integer lambda$new$3(Context context) {
        return Integer.valueOf(readLetterboxVerticalReachabilityPositionFromConfig(context, true));
    }

    @VisibleForTesting
    public LetterboxConfiguration(Context context, LetterboxConfigurationPersister letterboxConfigurationPersister) {
        this.mIsCameraCompatTreatmentRefreshEnabled = true;
        this.mIsCameraCompatRefreshCycleThroughStopEnabled = true;
        this.mContext = context;
        LetterboxConfigurationDeviceConfig letterboxConfigurationDeviceConfig = new LetterboxConfigurationDeviceConfig(context.getMainExecutor());
        this.mDeviceConfig = letterboxConfigurationDeviceConfig;
        this.mFixedOrientationLetterboxAspectRatio = context.getResources().getFloat(17105076);
        this.mLetterboxActivityCornersRadius = context.getResources().getInteger(17694856);
        this.mLetterboxBackgroundType = readLetterboxBackgroundTypeFromConfig(context);
        this.mLetterboxBackgroundWallpaperBlurRadius = context.getResources().getDimensionPixelSize(17105082);
        this.mLetterboxBackgroundWallpaperDarkScrimAlpha = context.getResources().getFloat(17105081);
        this.mLetterboxHorizontalPositionMultiplier = context.getResources().getFloat(17105085);
        this.mLetterboxVerticalPositionMultiplier = context.getResources().getFloat(17105087);
        this.mLetterboxBookModePositionMultiplier = context.getResources().getFloat(17105083);
        this.mLetterboxTabletopModePositionMultiplier = context.getResources().getFloat(17105086);
        this.mIsHorizontalReachabilityEnabled = context.getResources().getBoolean(17891722);
        this.mIsVerticalReachabilityEnabled = context.getResources().getBoolean(17891725);
        this.mDefaultPositionForHorizontalReachability = readLetterboxHorizontalReachabilityPositionFromConfig(context, false);
        this.mDefaultPositionForVerticalReachability = readLetterboxVerticalReachabilityPositionFromConfig(context, false);
        this.mIsEducationEnabled = context.getResources().getBoolean(17891720);
        setDefaultMinAspectRatioForUnresizableApps(context.getResources().getFloat(17105084));
        this.mIsSplitScreenAspectRatioForUnresizableAppsEnabled = context.getResources().getBoolean(17891724);
        this.mIsDisplayAspectRatioEnabledForFixedOrientationLetterbox = context.getResources().getBoolean(17891718);
        this.mTranslucentLetterboxingEnabled = context.getResources().getBoolean(17891721);
        boolean z = context.getResources().getBoolean(17891711);
        this.mIsCameraCompatTreatmentEnabled = z;
        this.mIsCompatFakeFocusEnabled = context.getResources().getBoolean(17891708);
        this.mIsPolicyForIgnoringRequestedOrientationEnabled = context.getResources().getBoolean(17891723);
        boolean z2 = context.getResources().getBoolean(17891719);
        this.mIsDisplayRotationImmersiveAppCompatPolicyEnabled = z2;
        letterboxConfigurationDeviceConfig.updateFlagActiveStatus(z, "enable_compat_camera_treatment");
        letterboxConfigurationDeviceConfig.updateFlagActiveStatus(z2, "enable_display_rotation_immersive_app_compat_policy");
        letterboxConfigurationDeviceConfig.updateFlagActiveStatus(true, "allow_ignore_orientation_request");
        letterboxConfigurationDeviceConfig.updateFlagActiveStatus(this.mIsCompatFakeFocusEnabled, "enable_compat_fake_focus");
        letterboxConfigurationDeviceConfig.updateFlagActiveStatus(this.mTranslucentLetterboxingEnabled, "enable_letterbox_translucent_activity");
        this.mLetterboxConfigurationPersister = letterboxConfigurationPersister;
        letterboxConfigurationPersister.start();
    }

    public boolean isIgnoreOrientationRequestAllowed() {
        return this.mDeviceConfig.getFlag("allow_ignore_orientation_request");
    }

    public void setFixedOrientationLetterboxAspectRatio(float f) {
        this.mFixedOrientationLetterboxAspectRatio = f;
    }

    public void resetFixedOrientationLetterboxAspectRatio() {
        this.mFixedOrientationLetterboxAspectRatio = this.mContext.getResources().getFloat(17105076);
    }

    public float getFixedOrientationLetterboxAspectRatio() {
        return this.mFixedOrientationLetterboxAspectRatio;
    }

    public void resetDefaultMinAspectRatioForUnresizableApps() {
        setDefaultMinAspectRatioForUnresizableApps(this.mContext.getResources().getFloat(17105084));
    }

    public float getDefaultMinAspectRatioForUnresizableApps() {
        return this.mDefaultMinAspectRatioForUnresizableApps;
    }

    public void setDefaultMinAspectRatioForUnresizableApps(float f) {
        this.mDefaultMinAspectRatioForUnresizableApps = f;
    }

    public void setLetterboxActivityCornersRadius(int i) {
        this.mLetterboxActivityCornersRadius = i;
    }

    public void resetLetterboxActivityCornersRadius() {
        this.mLetterboxActivityCornersRadius = this.mContext.getResources().getInteger(17694856);
    }

    public boolean isLetterboxActivityCornersRounded() {
        return getLetterboxActivityCornersRadius() != 0;
    }

    public int getLetterboxActivityCornersRadius() {
        return this.mLetterboxActivityCornersRadius;
    }

    public Color getLetterboxBackgroundColor() {
        Color color = this.mLetterboxBackgroundColorOverride;
        if (color != null) {
            return color;
        }
        Integer num = this.mLetterboxBackgroundColorResourceIdOverride;
        return Color.valueOf(this.mContext.getResources().getColor(num != null ? num.intValue() : 17170817));
    }

    public void setLetterboxBackgroundColor(Color color) {
        this.mLetterboxBackgroundColorOverride = color;
    }

    public void setLetterboxBackgroundColorResourceId(int i) {
        this.mLetterboxBackgroundColorResourceIdOverride = Integer.valueOf(i);
    }

    public void resetLetterboxBackgroundColor() {
        this.mLetterboxBackgroundColorOverride = null;
        this.mLetterboxBackgroundColorResourceIdOverride = null;
    }

    public int getLetterboxBackgroundType() {
        return this.mLetterboxBackgroundType;
    }

    public void setLetterboxBackgroundType(int i) {
        this.mLetterboxBackgroundType = i;
    }

    public void resetLetterboxBackgroundType() {
        this.mLetterboxBackgroundType = readLetterboxBackgroundTypeFromConfig(this.mContext);
    }

    public static String letterboxBackgroundTypeToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "unknown=" + i;
                    }
                    return "LETTERBOX_BACKGROUND_WALLPAPER";
                }
                return "LETTERBOX_BACKGROUND_APP_COLOR_BACKGROUND_FLOATING";
            }
            return "LETTERBOX_BACKGROUND_APP_COLOR_BACKGROUND";
        }
        return "LETTERBOX_BACKGROUND_SOLID_COLOR";
    }

    public static int readLetterboxBackgroundTypeFromConfig(Context context) {
        int integer = context.getResources().getInteger(17694857);
        if (integer == 0 || integer == 1 || integer == 2 || integer == 3) {
            return integer;
        }
        return 0;
    }

    public void setLetterboxBackgroundWallpaperDarkScrimAlpha(float f) {
        this.mLetterboxBackgroundWallpaperDarkScrimAlpha = f;
    }

    public void resetLetterboxBackgroundWallpaperDarkScrimAlpha() {
        this.mLetterboxBackgroundWallpaperDarkScrimAlpha = this.mContext.getResources().getFloat(17105081);
    }

    public float getLetterboxBackgroundWallpaperDarkScrimAlpha() {
        return this.mLetterboxBackgroundWallpaperDarkScrimAlpha;
    }

    public void setLetterboxBackgroundWallpaperBlurRadius(int i) {
        this.mLetterboxBackgroundWallpaperBlurRadius = i;
    }

    public void resetLetterboxBackgroundWallpaperBlurRadius() {
        this.mLetterboxBackgroundWallpaperBlurRadius = this.mContext.getResources().getDimensionPixelSize(17105082);
    }

    public int getLetterboxBackgroundWallpaperBlurRadius() {
        return this.mLetterboxBackgroundWallpaperBlurRadius;
    }

    public float getLetterboxHorizontalPositionMultiplier(boolean z) {
        if (z) {
            float f = this.mLetterboxBookModePositionMultiplier;
            if (f < 0.0f || f > 1.0f) {
                Slog.w("ActivityTaskManager", "mLetterboxBookModePositionMultiplier out of bounds (isInBookMode=true): " + this.mLetterboxBookModePositionMultiplier);
                return 0.0f;
            }
            return f;
        }
        float f2 = this.mLetterboxHorizontalPositionMultiplier;
        if (f2 < 0.0f || f2 > 1.0f) {
            Slog.w("ActivityTaskManager", "mLetterboxBookModePositionMultiplier out of bounds (isInBookMode=false):" + this.mLetterboxBookModePositionMultiplier);
            return 0.5f;
        }
        return f2;
    }

    public float getLetterboxVerticalPositionMultiplier(boolean z) {
        if (z) {
            float f = this.mLetterboxTabletopModePositionMultiplier;
            if (f < 0.0f || f > 1.0f) {
                return 0.0f;
            }
            return f;
        }
        float f2 = this.mLetterboxVerticalPositionMultiplier;
        if (f2 < 0.0f || f2 > 1.0f) {
            return 0.5f;
        }
        return f2;
    }

    public void setLetterboxHorizontalPositionMultiplier(float f) {
        this.mLetterboxHorizontalPositionMultiplier = f;
    }

    public void setLetterboxVerticalPositionMultiplier(float f) {
        this.mLetterboxVerticalPositionMultiplier = f;
    }

    public void resetLetterboxHorizontalPositionMultiplier() {
        this.mLetterboxHorizontalPositionMultiplier = this.mContext.getResources().getFloat(17105085);
    }

    public void resetLetterboxVerticalPositionMultiplier() {
        this.mLetterboxVerticalPositionMultiplier = this.mContext.getResources().getFloat(17105087);
    }

    public boolean getIsHorizontalReachabilityEnabled() {
        return this.mIsHorizontalReachabilityEnabled;
    }

    public boolean getIsVerticalReachabilityEnabled() {
        return this.mIsVerticalReachabilityEnabled;
    }

    public void setIsHorizontalReachabilityEnabled(boolean z) {
        this.mIsHorizontalReachabilityEnabled = z;
    }

    public void setIsVerticalReachabilityEnabled(boolean z) {
        this.mIsVerticalReachabilityEnabled = z;
    }

    public void resetIsHorizontalReachabilityEnabled() {
        this.mIsHorizontalReachabilityEnabled = this.mContext.getResources().getBoolean(17891722);
    }

    public void resetIsVerticalReachabilityEnabled() {
        this.mIsVerticalReachabilityEnabled = this.mContext.getResources().getBoolean(17891725);
    }

    public int getDefaultPositionForHorizontalReachability() {
        return this.mDefaultPositionForHorizontalReachability;
    }

    public int getDefaultPositionForVerticalReachability() {
        return this.mDefaultPositionForVerticalReachability;
    }

    public void setDefaultPositionForHorizontalReachability(int i) {
        this.mDefaultPositionForHorizontalReachability = i;
    }

    public void setDefaultPositionForVerticalReachability(int i) {
        this.mDefaultPositionForVerticalReachability = i;
    }

    public void resetDefaultPositionForHorizontalReachability() {
        this.mDefaultPositionForHorizontalReachability = readLetterboxHorizontalReachabilityPositionFromConfig(this.mContext, false);
    }

    public void resetDefaultPositionForVerticalReachability() {
        this.mDefaultPositionForVerticalReachability = readLetterboxVerticalReachabilityPositionFromConfig(this.mContext, false);
    }

    public static int readLetterboxHorizontalReachabilityPositionFromConfig(Context context, boolean z) {
        int integer = context.getResources().getInteger(z ? 17694858 : 17694859);
        if (integer == 0 || integer == 1 || integer == 2) {
            return integer;
        }
        return 1;
    }

    public static int readLetterboxVerticalReachabilityPositionFromConfig(Context context, boolean z) {
        int integer = context.getResources().getInteger(z ? 17694860 : 17694861);
        if (integer == 0 || integer == 1 || integer == 2) {
            return integer;
        }
        return 1;
    }

    public float getHorizontalMultiplierForReachability(boolean z) {
        int letterboxPositionForHorizontalReachability = this.mLetterboxConfigurationPersister.getLetterboxPositionForHorizontalReachability(z);
        if (letterboxPositionForHorizontalReachability != 0) {
            if (letterboxPositionForHorizontalReachability != 1) {
                if (letterboxPositionForHorizontalReachability == 2) {
                    return 1.0f;
                }
                throw new AssertionError("Unexpected letterbox position type: " + letterboxPositionForHorizontalReachability);
            }
            return 0.5f;
        }
        return 0.0f;
    }

    public float getVerticalMultiplierForReachability(boolean z) {
        int letterboxPositionForVerticalReachability = this.mLetterboxConfigurationPersister.getLetterboxPositionForVerticalReachability(z);
        if (letterboxPositionForVerticalReachability != 0) {
            if (letterboxPositionForVerticalReachability != 1) {
                if (letterboxPositionForVerticalReachability == 2) {
                    return 1.0f;
                }
                throw new AssertionError("Unexpected letterbox position type: " + letterboxPositionForVerticalReachability);
            }
            return 0.5f;
        }
        return 0.0f;
    }

    public int getLetterboxPositionForHorizontalReachability(boolean z) {
        return this.mLetterboxConfigurationPersister.getLetterboxPositionForHorizontalReachability(z);
    }

    public int getLetterboxPositionForVerticalReachability(boolean z) {
        return this.mLetterboxConfigurationPersister.getLetterboxPositionForVerticalReachability(z);
    }

    public static String letterboxHorizontalReachabilityPositionToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return "LETTERBOX_HORIZONTAL_REACHABILITY_POSITION_RIGHT";
                }
                throw new AssertionError("Unexpected letterbox position type: " + i);
            }
            return "LETTERBOX_HORIZONTAL_REACHABILITY_POSITION_CENTER";
        }
        return "LETTERBOX_HORIZONTAL_REACHABILITY_POSITION_LEFT";
    }

    public static String letterboxVerticalReachabilityPositionToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return "LETTERBOX_VERTICAL_REACHABILITY_POSITION_BOTTOM";
                }
                throw new AssertionError("Unexpected letterbox position type: " + i);
            }
            return "LETTERBOX_VERTICAL_REACHABILITY_POSITION_CENTER";
        }
        return "LETTERBOX_VERTICAL_REACHABILITY_POSITION_TOP";
    }

    public void movePositionForHorizontalReachabilityToNextRightStop(final boolean z) {
        updatePositionForHorizontalReachability(z, new Function() { // from class: com.android.server.wm.LetterboxConfiguration$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$movePositionForHorizontalReachabilityToNextRightStop$4;
                lambda$movePositionForHorizontalReachabilityToNextRightStop$4 = LetterboxConfiguration.lambda$movePositionForHorizontalReachabilityToNextRightStop$4(z, (Integer) obj);
                return lambda$movePositionForHorizontalReachabilityToNextRightStop$4;
            }
        });
    }

    public static /* synthetic */ Integer lambda$movePositionForHorizontalReachabilityToNextRightStop$4(boolean z, Integer num) {
        return Integer.valueOf(Math.min(num.intValue() + (z ? 2 : 1), 2));
    }

    public void movePositionForHorizontalReachabilityToNextLeftStop(final boolean z) {
        updatePositionForHorizontalReachability(z, new Function() { // from class: com.android.server.wm.LetterboxConfiguration$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$movePositionForHorizontalReachabilityToNextLeftStop$5;
                lambda$movePositionForHorizontalReachabilityToNextLeftStop$5 = LetterboxConfiguration.lambda$movePositionForHorizontalReachabilityToNextLeftStop$5(z, (Integer) obj);
                return lambda$movePositionForHorizontalReachabilityToNextLeftStop$5;
            }
        });
    }

    public static /* synthetic */ Integer lambda$movePositionForHorizontalReachabilityToNextLeftStop$5(boolean z, Integer num) {
        return Integer.valueOf(Math.max(num.intValue() - (z ? 2 : 1), 0));
    }

    public void movePositionForVerticalReachabilityToNextBottomStop(final boolean z) {
        updatePositionForVerticalReachability(z, new Function() { // from class: com.android.server.wm.LetterboxConfiguration$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$movePositionForVerticalReachabilityToNextBottomStop$6;
                lambda$movePositionForVerticalReachabilityToNextBottomStop$6 = LetterboxConfiguration.lambda$movePositionForVerticalReachabilityToNextBottomStop$6(z, (Integer) obj);
                return lambda$movePositionForVerticalReachabilityToNextBottomStop$6;
            }
        });
    }

    public static /* synthetic */ Integer lambda$movePositionForVerticalReachabilityToNextBottomStop$6(boolean z, Integer num) {
        return Integer.valueOf(Math.min(num.intValue() + (z ? 2 : 1), 2));
    }

    public void movePositionForVerticalReachabilityToNextTopStop(final boolean z) {
        updatePositionForVerticalReachability(z, new Function() { // from class: com.android.server.wm.LetterboxConfiguration$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$movePositionForVerticalReachabilityToNextTopStop$7;
                lambda$movePositionForVerticalReachabilityToNextTopStop$7 = LetterboxConfiguration.lambda$movePositionForVerticalReachabilityToNextTopStop$7(z, (Integer) obj);
                return lambda$movePositionForVerticalReachabilityToNextTopStop$7;
            }
        });
    }

    public static /* synthetic */ Integer lambda$movePositionForVerticalReachabilityToNextTopStop$7(boolean z, Integer num) {
        return Integer.valueOf(Math.max(num.intValue() - (z ? 2 : 1), 0));
    }

    public boolean getIsEducationEnabled() {
        return this.mIsEducationEnabled;
    }

    public void setIsEducationEnabled(boolean z) {
        this.mIsEducationEnabled = z;
    }

    public void resetIsEducationEnabled() {
        this.mIsEducationEnabled = this.mContext.getResources().getBoolean(17891720);
    }

    public boolean getIsSplitScreenAspectRatioForUnresizableAppsEnabled() {
        return this.mIsSplitScreenAspectRatioForUnresizableAppsEnabled;
    }

    public boolean getIsDisplayAspectRatioEnabledForFixedOrientationLetterbox() {
        return this.mIsDisplayAspectRatioEnabledForFixedOrientationLetterbox;
    }

    public void setIsSplitScreenAspectRatioForUnresizableAppsEnabled(boolean z) {
        this.mIsSplitScreenAspectRatioForUnresizableAppsEnabled = z;
    }

    public void setIsDisplayAspectRatioEnabledForFixedOrientationLetterbox(boolean z) {
        this.mIsDisplayAspectRatioEnabledForFixedOrientationLetterbox = z;
    }

    public void resetIsSplitScreenAspectRatioForUnresizableAppsEnabled() {
        this.mIsSplitScreenAspectRatioForUnresizableAppsEnabled = this.mContext.getResources().getBoolean(17891724);
    }

    public void resetIsDisplayAspectRatioEnabledForFixedOrientationLetterbox() {
        this.mIsDisplayAspectRatioEnabledForFixedOrientationLetterbox = this.mContext.getResources().getBoolean(17891718);
    }

    public boolean isTranslucentLetterboxingEnabled() {
        return this.mTranslucentLetterboxingOverrideEnabled || (this.mTranslucentLetterboxingEnabled && this.mDeviceConfig.getFlag("enable_letterbox_translucent_activity"));
    }

    public void setTranslucentLetterboxingEnabled(boolean z) {
        this.mTranslucentLetterboxingEnabled = z;
    }

    public void setTranslucentLetterboxingOverrideEnabled(boolean z) {
        this.mTranslucentLetterboxingOverrideEnabled = z;
        setTranslucentLetterboxingEnabled(z);
    }

    public void resetTranslucentLetterboxingEnabled() {
        setTranslucentLetterboxingEnabled(this.mContext.getResources().getBoolean(17891721));
        setTranslucentLetterboxingOverrideEnabled(false);
    }

    public final void updatePositionForHorizontalReachability(boolean z, Function<Integer, Integer> function) {
        this.mLetterboxConfigurationPersister.setLetterboxPositionForHorizontalReachability(z, function.apply(Integer.valueOf(this.mLetterboxConfigurationPersister.getLetterboxPositionForHorizontalReachability(z))).intValue());
    }

    public final void updatePositionForVerticalReachability(boolean z, Function<Integer, Integer> function) {
        this.mLetterboxConfigurationPersister.setLetterboxPositionForVerticalReachability(z, function.apply(Integer.valueOf(this.mLetterboxConfigurationPersister.getLetterboxPositionForVerticalReachability(z))).intValue());
    }

    public boolean isCompatFakeFocusEnabled() {
        return this.mIsCompatFakeFocusEnabled && this.mDeviceConfig.getFlag("enable_compat_fake_focus");
    }

    @VisibleForTesting
    public void setIsCompatFakeFocusEnabled(boolean z) {
        this.mIsCompatFakeFocusEnabled = z;
    }

    public boolean isPolicyForIgnoringRequestedOrientationEnabled() {
        return this.mIsPolicyForIgnoringRequestedOrientationEnabled;
    }

    public boolean isCameraCompatTreatmentEnabled(boolean z) {
        return this.mIsCameraCompatTreatmentEnabled && (!z || this.mDeviceConfig.getFlag("enable_compat_camera_treatment"));
    }

    public boolean isCameraCompatRefreshEnabled() {
        return this.mIsCameraCompatTreatmentRefreshEnabled;
    }

    public void setCameraCompatRefreshEnabled(boolean z) {
        this.mIsCameraCompatTreatmentRefreshEnabled = z;
    }

    public void resetCameraCompatRefreshEnabled() {
        this.mIsCameraCompatTreatmentRefreshEnabled = true;
    }

    public boolean isCameraCompatRefreshCycleThroughStopEnabled() {
        return this.mIsCameraCompatRefreshCycleThroughStopEnabled;
    }

    public void setCameraCompatRefreshCycleThroughStopEnabled(boolean z) {
        this.mIsCameraCompatRefreshCycleThroughStopEnabled = z;
    }

    public void resetCameraCompatRefreshCycleThroughStopEnabled() {
        this.mIsCameraCompatRefreshCycleThroughStopEnabled = true;
    }

    public boolean isDisplayRotationImmersiveAppCompatPolicyEnabled(boolean z) {
        return this.mIsDisplayRotationImmersiveAppCompatPolicyEnabled && (!z || this.mDeviceConfig.getFlag("enable_display_rotation_immersive_app_compat_policy"));
    }
}
