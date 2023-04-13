package com.android.server.p006am;

import android.provider.DeviceConfig;
import com.android.server.p006am.BaseAppStateTracker;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.BaseAppStatePolicy */
/* loaded from: classes.dex */
public abstract class BaseAppStatePolicy<T extends BaseAppStateTracker> {
    public final boolean mDefaultTrackerEnabled;
    public final BaseAppStateTracker.Injector<?> mInjector;
    public final String mKeyTrackerEnabled;
    public final T mTracker;
    public volatile boolean mTrackerEnabled;

    public int getProposedRestrictionLevel(String str, int i, int i2) {
        return 0;
    }

    public abstract void onTrackerEnabled(boolean z);

    public BaseAppStatePolicy(BaseAppStateTracker.Injector<?> injector, T t, String str, boolean z) {
        this.mInjector = injector;
        this.mTracker = t;
        this.mKeyTrackerEnabled = str;
        this.mDefaultTrackerEnabled = z;
    }

    public void updateTrackerEnabled() {
        boolean z = DeviceConfig.getBoolean("activity_manager", this.mKeyTrackerEnabled, this.mDefaultTrackerEnabled);
        if (z != this.mTrackerEnabled) {
            this.mTrackerEnabled = z;
            onTrackerEnabled(z);
        }
    }

    public void onPropertiesChanged(String str) {
        if (this.mKeyTrackerEnabled.equals(str)) {
            updateTrackerEnabled();
        }
    }

    public void onSystemReady() {
        updateTrackerEnabled();
    }

    public boolean isEnabled() {
        return this.mTrackerEnabled;
    }

    public int shouldExemptUid(int i) {
        return this.mTracker.mAppRestrictionController.getBackgroundRestrictionExemptionReason(i);
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print(this.mKeyTrackerEnabled);
        printWriter.print('=');
        printWriter.println(this.mTrackerEnabled);
    }
}
