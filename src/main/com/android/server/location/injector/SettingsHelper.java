package com.android.server.location.injector;

import android.os.PackageTagsList;
import android.util.IndentingPrintWriter;
import java.io.FileDescriptor;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class SettingsHelper {

    /* loaded from: classes.dex */
    public interface UserSettingChangedListener {
        void onSettingChanged(int i);
    }

    public abstract void addAdasAllowlistChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void addIgnoreSettingsAllowlistChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void addOnBackgroundThrottleIntervalChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void addOnBackgroundThrottlePackageWhitelistChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void addOnGnssMeasurementsFullTrackingEnabledChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void addOnLocationEnabledChangedListener(UserSettingChangedListener userSettingChangedListener);

    public abstract void addOnLocationPackageBlacklistChangedListener(UserSettingChangedListener userSettingChangedListener);

    public abstract void dump(FileDescriptor fileDescriptor, IndentingPrintWriter indentingPrintWriter, String[] strArr);

    public abstract PackageTagsList getAdasAllowlist();

    public abstract long getBackgroundThrottleIntervalMs();

    public abstract Set<String> getBackgroundThrottlePackageWhitelist();

    public abstract long getBackgroundThrottleProximityAlertIntervalMs();

    public abstract float getCoarseLocationAccuracyM();

    public abstract PackageTagsList getIgnoreSettingsAllowlist();

    public abstract boolean isGnssMeasurementsFullTrackingEnabled();

    public abstract boolean isLocationEnabled(int i);

    public abstract boolean isLocationPackageBlacklisted(int i, String str);

    public abstract void removeAdasAllowlistChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void removeIgnoreSettingsAllowlistChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void removeOnBackgroundThrottleIntervalChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void removeOnBackgroundThrottlePackageWhitelistChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void removeOnGnssMeasurementsFullTrackingEnabledChangedListener(GlobalSettingChangedListener globalSettingChangedListener);

    public abstract void removeOnLocationEnabledChangedListener(UserSettingChangedListener userSettingChangedListener);

    public abstract void removeOnLocationPackageBlacklistChangedListener(UserSettingChangedListener userSettingChangedListener);

    public abstract void setLocationEnabled(boolean z, int i);

    /* loaded from: classes.dex */
    public interface GlobalSettingChangedListener extends UserSettingChangedListener {
        void onSettingChanged();

        @Override // com.android.server.location.injector.SettingsHelper.UserSettingChangedListener
        default void onSettingChanged(int i) {
            onSettingChanged();
        }
    }
}
