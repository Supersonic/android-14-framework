package com.android.server.deviceidle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes5.dex */
public interface IDeviceIdleConstraint {
    public static final int ACTIVE = 0;
    public static final int SENSING_OR_ABOVE = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes5.dex */
    public @interface MinimumState {
    }

    void startMonitoring();

    void stopMonitoring();
}
