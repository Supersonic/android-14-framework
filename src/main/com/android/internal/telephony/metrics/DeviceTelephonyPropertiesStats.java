package com.android.internal.telephony.metrics;

import com.android.internal.telephony.PhoneFactory;
/* loaded from: classes.dex */
public class DeviceTelephonyPropertiesStats {
    public static void recordAutoDataSwitchFeatureToggle() {
        PhoneFactory.getMetricsCollector().getAtomsStorage().recordToggledAutoDataSwitch();
    }
}
