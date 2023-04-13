package com.android.server.biometrics;

import android.hardware.biometrics.BiometricManager;
import com.android.internal.util.jobs.XmlUtils;
/* loaded from: classes.dex */
public class SensorConfig {

    /* renamed from: id */
    public final int f1134id;
    public final int modality;
    @BiometricManager.Authenticators.Types
    public final int strength;

    public SensorConfig(String str) {
        String[] split = str.split(XmlUtils.STRING_ARRAY_SEPARATOR);
        this.f1134id = Integer.parseInt(split[0]);
        this.modality = Integer.parseInt(split[1]);
        this.strength = Integer.parseInt(split[2]);
    }
}
