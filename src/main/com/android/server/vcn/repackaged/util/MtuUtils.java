package com.android.server.vcn.repackaged.util;

import android.net.ipsec.ike.ChildSaProposal;
import android.util.ArrayMap;
import android.util.Pair;
import android.util.Slog;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/* loaded from: classes5.dex */
public class MtuUtils {
    private static final Map<Integer, Integer> AUTHCRYPT_ALGORITHM_OVERHEAD;
    private static final Map<Integer, Integer> AUTH_ALGORITHM_OVERHEAD;
    private static final Map<Integer, Integer> CRYPT_ALGORITHM_OVERHEAD;
    private static final int GENERIC_ESP_OVERHEAD_MAX_V4 = 78;
    private static final int GENERIC_ESP_OVERHEAD_MAX_V6 = 50;
    private static final String TAG = MtuUtils.class.getSimpleName();

    static {
        Map<Integer, Integer> map = new ArrayMap<>();
        map.put(0, 0);
        map.put(2, 12);
        map.put(5, 12);
        map.put(12, 32);
        map.put(13, 48);
        map.put(14, 64);
        map.put(8, 12);
        AUTH_ALGORITHM_OVERHEAD = Collections.unmodifiableMap(map);
        Map<Integer, Integer> map2 = new ArrayMap<>();
        map2.put(3, 15);
        map2.put(12, 31);
        map2.put(13, 11);
        CRYPT_ALGORITHM_OVERHEAD = Collections.unmodifiableMap(map2);
        Map<Integer, Integer> map3 = new ArrayMap<>();
        map3.put(18, 19);
        map3.put(19, 23);
        map3.put(20, 27);
        map3.put(28, 27);
        AUTHCRYPT_ALGORITHM_OVERHEAD = Collections.unmodifiableMap(map3);
    }

    public static int getMtu(List<ChildSaProposal> childProposals, int maxMtu, int underlyingMtu, boolean isIpv4) {
        if (underlyingMtu <= 0) {
            return 1280;
        }
        int maxAuthOverhead = 0;
        int maxCryptOverhead = 0;
        int maxAuthCryptOverhead = 0;
        for (ChildSaProposal proposal : childProposals) {
            for (Pair<Integer, Integer> encryptionAlgoPair : proposal.getEncryptionAlgorithms()) {
                int algo = encryptionAlgoPair.first.intValue();
                Map<Integer, Integer> map = AUTHCRYPT_ALGORITHM_OVERHEAD;
                if (map.containsKey(Integer.valueOf(algo))) {
                    maxAuthCryptOverhead = Math.max(maxAuthCryptOverhead, map.get(Integer.valueOf(algo)).intValue());
                } else {
                    Map<Integer, Integer> map2 = CRYPT_ALGORITHM_OVERHEAD;
                    if (map2.containsKey(Integer.valueOf(algo))) {
                        maxCryptOverhead = Math.max(maxCryptOverhead, map2.get(Integer.valueOf(algo)).intValue());
                    } else {
                        Slog.wtf(TAG, "Unknown encryption algorithm requested: " + algo);
                        return 1280;
                    }
                }
            }
            for (Integer num : proposal.getIntegrityAlgorithms()) {
                int algo2 = num.intValue();
                Map<Integer, Integer> map3 = AUTH_ALGORITHM_OVERHEAD;
                if (map3.containsKey(Integer.valueOf(algo2))) {
                    maxAuthOverhead = Math.max(maxAuthOverhead, map3.get(Integer.valueOf(algo2)).intValue());
                } else {
                    Slog.wtf(TAG, "Unknown integrity algorithm requested: " + algo2);
                    return 1280;
                }
            }
        }
        int genericEspOverheadMax = isIpv4 ? 78 : 50;
        int combinedModeMtu = (underlyingMtu - maxAuthCryptOverhead) - genericEspOverheadMax;
        int normalModeMtu = ((underlyingMtu - maxCryptOverhead) - maxAuthOverhead) - genericEspOverheadMax;
        return Math.min(Math.min(maxMtu, combinedModeMtu), normalModeMtu);
    }
}
