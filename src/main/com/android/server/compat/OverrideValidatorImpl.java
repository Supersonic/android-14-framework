package com.android.server.compat;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.compat.AndroidBuildClassifier;
import com.android.internal.compat.IOverrideValidator;
import com.android.internal.compat.OverrideAllowedState;
/* loaded from: classes.dex */
public class OverrideValidatorImpl extends IOverrideValidator.Stub {
    public AndroidBuildClassifier mAndroidBuildClassifier;
    public CompatConfig mCompatConfig;
    public Context mContext;
    public boolean mForceNonDebuggableFinalBuild = false;

    /* loaded from: classes.dex */
    public class SettingsObserver extends ContentObserver {
        public SettingsObserver() {
            super(new Handler());
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            OverrideValidatorImpl overrideValidatorImpl = OverrideValidatorImpl.this;
            overrideValidatorImpl.mForceNonDebuggableFinalBuild = Settings.Global.getInt(overrideValidatorImpl.mContext.getContentResolver(), "force_non_debuggable_final_build_for_compat", 0) == 1;
        }
    }

    @VisibleForTesting
    public OverrideValidatorImpl(AndroidBuildClassifier androidBuildClassifier, Context context, CompatConfig compatConfig) {
        this.mAndroidBuildClassifier = androidBuildClassifier;
        this.mContext = context;
        this.mCompatConfig = compatConfig;
    }

    public OverrideAllowedState getOverrideAllowedStateForRecheck(long j, String str) {
        return getOverrideAllowedStateInternal(j, str, true);
    }

    public OverrideAllowedState getOverrideAllowedState(long j, String str) {
        return getOverrideAllowedStateInternal(j, str, false);
    }

    public final OverrideAllowedState getOverrideAllowedStateInternal(long j, String str, boolean z) {
        if (this.mCompatConfig.isLoggingOnly(j)) {
            return new OverrideAllowedState(5, -1, -1);
        }
        boolean z2 = this.mAndroidBuildClassifier.isDebuggableBuild() && !this.mForceNonDebuggableFinalBuild;
        boolean z3 = this.mAndroidBuildClassifier.isFinalBuild() || this.mForceNonDebuggableFinalBuild;
        int maxTargetSdkForChangeIdOptIn = this.mCompatConfig.maxTargetSdkForChangeIdOptIn(j);
        boolean isDisabled = this.mCompatConfig.isDisabled(j);
        if (z2) {
            return new OverrideAllowedState(0, -1, -1);
        }
        if (maxTargetSdkForChangeIdOptIn >= this.mAndroidBuildClassifier.platformTargetSdk()) {
            return new OverrideAllowedState(6, -1, maxTargetSdkForChangeIdOptIn);
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        if (packageManager == null) {
            throw new IllegalStateException("No PackageManager!");
        }
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 4194304);
            if (this.mCompatConfig.isOverridable(j) && (z || this.mContext.checkCallingOrSelfPermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG_ON_RELEASE_BUILD") == 0)) {
                return new OverrideAllowedState(0, -1, -1);
            }
            int i = applicationInfo.targetSdkVersion;
            if ((applicationInfo.flags & 2) == 0) {
                return new OverrideAllowedState(1, -1, -1);
            }
            if (z3) {
                if (maxTargetSdkForChangeIdOptIn != -1 || isDisabled) {
                    if (isDisabled || i <= maxTargetSdkForChangeIdOptIn) {
                        return new OverrideAllowedState(0, i, maxTargetSdkForChangeIdOptIn);
                    }
                    return new OverrideAllowedState(3, i, maxTargetSdkForChangeIdOptIn);
                }
                return new OverrideAllowedState(2, i, maxTargetSdkForChangeIdOptIn);
            }
            return new OverrideAllowedState(0, i, maxTargetSdkForChangeIdOptIn);
        } catch (PackageManager.NameNotFoundException unused) {
            return new OverrideAllowedState(4, -1, -1);
        }
    }

    public void registerContentObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("force_non_debuggable_final_build_for_compat"), false, new SettingsObserver());
    }

    public void forceNonDebuggableFinalForTest(boolean z) {
        this.mForceNonDebuggableFinalBuild = z;
    }
}
