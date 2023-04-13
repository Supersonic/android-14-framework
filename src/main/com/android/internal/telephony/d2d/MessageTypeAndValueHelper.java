package com.android.internal.telephony.d2d;

import com.android.internal.telephony.BiMap;
/* loaded from: classes.dex */
public class MessageTypeAndValueHelper {
    public static final BiMap<Integer, Integer> BATTERY_STATE_TO_DC_BATTERY_STATE;
    public static final BiMap<Integer, Integer> CODEC_TO_DC_CODEC;
    public static final BiMap<Integer, Integer> COVERAGE_TO_DC_COVERAGE;
    public static final BiMap<Integer, Integer> MSG_TYPE_TO_DC_MSG_TYPE;
    public static final BiMap<Integer, Integer> RAT_TYPE_TO_DC_NETWORK_TYPE;

    static {
        BiMap<Integer, Integer> biMap = new BiMap<>();
        MSG_TYPE_TO_DC_MSG_TYPE = biMap;
        BiMap<Integer, Integer> biMap2 = new BiMap<>();
        RAT_TYPE_TO_DC_NETWORK_TYPE = biMap2;
        BiMap<Integer, Integer> biMap3 = new BiMap<>();
        CODEC_TO_DC_CODEC = biMap3;
        BiMap<Integer, Integer> biMap4 = new BiMap<>();
        BATTERY_STATE_TO_DC_BATTERY_STATE = biMap4;
        BiMap<Integer, Integer> biMap5 = new BiMap<>();
        COVERAGE_TO_DC_COVERAGE = biMap5;
        biMap.put(1, 1);
        biMap.put(2, 2);
        biMap.put(3, 3);
        biMap.put(4, 4);
        biMap2.put(1, 13);
        biMap2.put(2, 18);
        biMap2.put(3, 20);
        biMap3.put(1, 18);
        biMap3.put(2, 2);
        biMap3.put(3, 1);
        biMap4.put(1, 1);
        biMap4.put(2, 2);
        biMap4.put(3, 3);
        biMap5.put(1, 1);
        biMap5.put(2, 2);
    }
}
