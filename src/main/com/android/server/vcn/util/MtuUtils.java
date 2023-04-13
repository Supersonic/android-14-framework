package com.android.server.vcn.util;

import android.net.ipsec.ike.ChildSaProposal;
import android.util.ArrayMap;
import android.util.Pair;
import android.util.Slog;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public class MtuUtils {
    public static final Map<Integer, Integer> AUTHCRYPT_ALGORITHM_OVERHEAD;
    public static final Map<Integer, Integer> AUTH_ALGORITHM_OVERHEAD;
    public static final Map<Integer, Integer> CRYPT_ALGORITHM_OVERHEAD;
    public static final String TAG = "MtuUtils";

    static {
        ArrayMap arrayMap = new ArrayMap();
        arrayMap.put(0, 0);
        arrayMap.put(2, 12);
        arrayMap.put(5, 12);
        arrayMap.put(12, 32);
        arrayMap.put(13, 48);
        arrayMap.put(14, 64);
        arrayMap.put(8, 12);
        AUTH_ALGORITHM_OVERHEAD = Collections.unmodifiableMap(arrayMap);
        ArrayMap arrayMap2 = new ArrayMap();
        arrayMap2.put(3, 15);
        arrayMap2.put(12, 31);
        arrayMap2.put(13, 11);
        CRYPT_ALGORITHM_OVERHEAD = Collections.unmodifiableMap(arrayMap2);
        ArrayMap arrayMap3 = new ArrayMap();
        arrayMap3.put(18, 19);
        arrayMap3.put(19, 23);
        arrayMap3.put(20, 27);
        arrayMap3.put(28, 27);
        AUTHCRYPT_ALGORITHM_OVERHEAD = Collections.unmodifiableMap(arrayMap3);
    }

    public static int getMtu(List<ChildSaProposal> list, int i, int i2, boolean z) {
        if (i2 <= 0) {
            return 1280;
        }
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        for (ChildSaProposal childSaProposal : list) {
            for (Pair<Integer, Integer> pair : childSaProposal.getEncryptionAlgorithms()) {
                int intValue = ((Integer) pair.first).intValue();
                Map<Integer, Integer> map = AUTHCRYPT_ALGORITHM_OVERHEAD;
                if (map.containsKey(Integer.valueOf(intValue))) {
                    i3 = Math.max(i3, map.get(Integer.valueOf(intValue)).intValue());
                } else {
                    Map<Integer, Integer> map2 = CRYPT_ALGORITHM_OVERHEAD;
                    if (map2.containsKey(Integer.valueOf(intValue))) {
                        i4 = Math.max(i4, map2.get(Integer.valueOf(intValue)).intValue());
                    } else {
                        Slog.wtf(TAG, "Unknown encryption algorithm requested: " + intValue);
                        return 1280;
                    }
                }
            }
            for (Integer num : childSaProposal.getIntegrityAlgorithms()) {
                int intValue2 = num.intValue();
                Map<Integer, Integer> map3 = AUTH_ALGORITHM_OVERHEAD;
                if (map3.containsKey(Integer.valueOf(intValue2))) {
                    i5 = Math.max(i5, map3.get(Integer.valueOf(intValue2)).intValue());
                } else {
                    Slog.wtf(TAG, "Unknown integrity algorithm requested: " + intValue2);
                    return 1280;
                }
            }
        }
        int i6 = z ? 78 : 50;
        return Math.min(Math.min(i, (i2 - i3) - i6), ((i2 - i4) - i5) - i6);
    }
}
