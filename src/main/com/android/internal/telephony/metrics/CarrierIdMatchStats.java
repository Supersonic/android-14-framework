package com.android.internal.telephony.metrics;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.TelephonyStatsLog;
import com.android.internal.telephony.nano.PersistAtomsProto$CarrierIdMismatch;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class CarrierIdMatchStats {
    private static final String TAG = "CarrierIdMatchStats";

    private static String nullToEmpty(String str) {
        return str != null ? str : PhoneConfigurationManager.SSSS;
    }

    private CarrierIdMatchStats() {
    }

    public static void onCarrierIdMismatch(int i, String str, String str2, String str3, String str4) {
        PersistAtomsStorage atomsStorage = PhoneFactory.getMetricsCollector().getAtomsStorage();
        PersistAtomsProto$CarrierIdMismatch persistAtomsProto$CarrierIdMismatch = new PersistAtomsProto$CarrierIdMismatch();
        persistAtomsProto$CarrierIdMismatch.mccMnc = nullToEmpty(str);
        persistAtomsProto$CarrierIdMismatch.gid1 = nullToEmpty(str2);
        String nullToEmpty = nullToEmpty(str3);
        persistAtomsProto$CarrierIdMismatch.spn = nullToEmpty;
        persistAtomsProto$CarrierIdMismatch.pnn = nullToEmpty.isEmpty() ? nullToEmpty(str4) : PhoneConfigurationManager.SSSS;
        if (atomsStorage.addCarrierIdMismatch(persistAtomsProto$CarrierIdMismatch)) {
            String str5 = TAG;
            Rlog.d(str5, "New carrier ID mismatch event: " + persistAtomsProto$CarrierIdMismatch.toString());
            TelephonyStatsLog.write(313, i, str, str2, str3, str4);
        }
    }

    public static void sendCarrierIdTableVersion(int i) {
        if (PhoneFactory.getMetricsCollector().getAtomsStorage().setCarrierIdTableVersion(i)) {
            String str = TAG;
            Rlog.d(str, "New carrier ID table version: " + i);
            TelephonyStatsLog.write(314, i);
        }
    }
}
