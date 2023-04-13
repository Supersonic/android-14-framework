package com.android.server.vcn.repackaged.util;

import android.p008os.ParcelUuid;
import com.android.internal.util.HexDump;
/* loaded from: classes5.dex */
public class LogUtils {
    public static String getHashedSubscriptionGroup(ParcelUuid uuid) {
        if (uuid == null) {
            return null;
        }
        return HexDump.toHexString(uuid.hashCode());
    }
}
