package com.android.server.tare;

import com.android.internal.annotations.VisibleForTesting;
import java.time.Clock;
/* loaded from: classes2.dex */
public class TareUtils {
    @VisibleForTesting
    static Clock sSystemClock = Clock.systemUTC();

    public static long getCurrentTimeMillis() {
        return sSystemClock.millis();
    }

    public static int cakeToArc(long j) {
        return (int) (j / 1000000000);
    }

    public static String cakeToString(long j) {
        if (j == 0) {
            return "0 ARCs";
        }
        long j2 = j % 1000000000;
        long cakeToArc = cakeToArc(j);
        if (cakeToArc == 0) {
            if (j2 == 1) {
                return j2 + " cake";
            }
            return j2 + " cakes";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(cakeToArc);
        int i = (j2 > 0L ? 1 : (j2 == 0L ? 0 : -1));
        if (i != 0) {
            sb.append(".");
            sb.append(String.format("%03d", Long.valueOf(Math.abs(j2) / 1000000)));
        }
        sb.append(" ARC");
        if (cakeToArc != 1 || i != 0) {
            sb.append("s");
        }
        return sb.toString();
    }

    public static String appToString(int i, String str) {
        return "<" + i + ">" + str;
    }
}
