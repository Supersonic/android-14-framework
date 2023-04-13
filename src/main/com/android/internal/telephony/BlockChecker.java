package com.android.internal.telephony;

import android.content.Context;
import android.os.Bundle;
import android.provider.BlockedNumberContract;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class BlockChecker {
    @Deprecated
    public static boolean isBlocked(Context context, String str) {
        return isBlocked(context, str, null);
    }

    public static boolean isBlocked(Context context, String str, Bundle bundle) {
        return getBlockStatus(context, str, bundle) != 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0048  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int getBlockStatus(Context context, String str, Bundle bundle) {
        int i;
        int nanoTime;
        long nanoTime2 = System.nanoTime();
        try {
            i = BlockedNumberContract.SystemContract.shouldSystemBlockNumber(context, str, bundle);
            if (i != 0) {
                try {
                    Rlog.d("BlockChecker", str + " is blocked.");
                } catch (Exception e) {
                    e = e;
                    Rlog.e("BlockChecker", "Exception checking for blocked number: " + e);
                    nanoTime = (int) ((System.nanoTime() - nanoTime2) / TimeUtils.NANOS_PER_MS);
                    if (nanoTime > 500) {
                    }
                    return i;
                }
            }
        } catch (Exception e2) {
            e = e2;
            i = 0;
        }
        nanoTime = (int) ((System.nanoTime() - nanoTime2) / TimeUtils.NANOS_PER_MS);
        if (nanoTime > 500) {
            Rlog.d("BlockChecker", "Blocked number lookup took: " + nanoTime + " ms.");
        }
        return i;
    }
}
