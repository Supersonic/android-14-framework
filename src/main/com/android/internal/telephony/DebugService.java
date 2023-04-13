package com.android.internal.telephony;

import android.os.Build;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class DebugService {
    private static String TAG = "DebugService";

    public DebugService() {
        log("DebugService:");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0047, code lost:
        if (r0.equals("--metrics") == false) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr != null && strArr.length > 0) {
            char c = 0;
            String str = strArr[0];
            str.hashCode();
            switch (str.hashCode()) {
                case -1953159389:
                    break;
                case 513805138:
                    if (str.equals("--metricsprototext")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 674382917:
                    if (str.equals("--saveatoms")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 950313125:
                    if (str.equals("--metricsproto")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 1524367541:
                    if (str.equals("--clearatoms")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 3:
                    log("Collecting telephony metrics..");
                    TelephonyMetrics.getInstance().dump(fileDescriptor, printWriter, strArr);
                    return;
                case 2:
                    if (Build.IS_DEBUGGABLE) {
                        log("Saving atoms..");
                        PhoneFactory.getMetricsCollector().flushAtomsStorage();
                        return;
                    }
                    return;
                case 4:
                    if (Build.IS_DEBUGGABLE) {
                        log("Clearing atoms..");
                        PhoneFactory.getMetricsCollector().clearAtomsStorage();
                        return;
                    }
                    return;
            }
        }
        log("Dump telephony.");
        PhoneFactory.dump(fileDescriptor, printWriter, strArr);
    }

    private static void log(String str) {
        String str2 = TAG;
        Rlog.d(str2, "DebugService " + str);
    }
}
