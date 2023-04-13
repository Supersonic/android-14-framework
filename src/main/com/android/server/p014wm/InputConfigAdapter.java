package com.android.server.p014wm;

import android.p005os.IInstalld;
import java.util.List;
/* renamed from: com.android.server.wm.InputConfigAdapter */
/* loaded from: classes2.dex */
public class InputConfigAdapter {
    public static final List<FlagMapping> INPUT_FEATURE_TO_CONFIG_MAP;
    public static final int INPUT_FEATURE_TO_CONFIG_MASK;
    public static final List<FlagMapping> LAYOUT_PARAM_FLAG_TO_CONFIG_MAP;
    public static final int LAYOUT_PARAM_FLAG_TO_CONFIG_MASK;

    /* renamed from: com.android.server.wm.InputConfigAdapter$FlagMapping */
    /* loaded from: classes2.dex */
    public static class FlagMapping {
        public final int mFlag;
        public final int mInputConfig;
        public final boolean mInverted;

        public FlagMapping(int i, int i2, boolean z) {
            this.mFlag = i;
            this.mInputConfig = i2;
            this.mInverted = z;
        }
    }

    static {
        List<FlagMapping> of = List.of(new FlagMapping(1, 1, false), new FlagMapping(2, IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES, false), new FlagMapping(4, 16384, false));
        INPUT_FEATURE_TO_CONFIG_MAP = of;
        INPUT_FEATURE_TO_CONFIG_MASK = computeMask(of);
        List<FlagMapping> of2 = List.of(new FlagMapping(16, 8, false), new FlagMapping(8388608, 16, true), new FlagMapping(262144, 512, false), new FlagMapping(536870912, 1024, false));
        LAYOUT_PARAM_FLAG_TO_CONFIG_MAP = of2;
        LAYOUT_PARAM_FLAG_TO_CONFIG_MASK = computeMask(of2);
    }

    public static int getMask() {
        return LAYOUT_PARAM_FLAG_TO_CONFIG_MASK | INPUT_FEATURE_TO_CONFIG_MASK | 64;
    }

    public static int getInputConfigFromWindowParams(int i, int i2, int i3) {
        return (i == 2013 ? 64 : 0) | applyMapping(i2, LAYOUT_PARAM_FLAG_TO_CONFIG_MAP) | applyMapping(i3, INPUT_FEATURE_TO_CONFIG_MAP);
    }

    public static int applyMapping(int i, List<FlagMapping> list) {
        int i2 = 0;
        for (FlagMapping flagMapping : list) {
            if (((flagMapping.mFlag & i) != 0) != flagMapping.mInverted) {
                i2 |= flagMapping.mInputConfig;
            }
        }
        return i2;
    }

    public static int computeMask(List<FlagMapping> list) {
        int i = 0;
        for (FlagMapping flagMapping : list) {
            i |= flagMapping.mInputConfig;
        }
        return i;
    }
}
