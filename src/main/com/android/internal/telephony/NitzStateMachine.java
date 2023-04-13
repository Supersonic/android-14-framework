package com.android.internal.telephony;

import android.content.ContentResolver;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public interface NitzStateMachine {

    /* loaded from: classes.dex */
    public interface DeviceState {
        long currentTimeMillis();

        long elapsedRealtimeMillis();

        boolean getIgnoreNitz();

        int getNitzNetworkDisconnectRetentionMillis();

        int getNitzUpdateDiffMillis();

        int getNitzUpdateSpacingMillis();
    }

    void dumpLogs(FileDescriptor fileDescriptor, IndentingPrintWriter indentingPrintWriter, String[] strArr);

    void dumpState(PrintWriter printWriter);

    void handleAirplaneModeChanged(boolean z);

    void handleCountryDetected(String str);

    void handleCountryUnavailable();

    void handleNetworkAvailable();

    void handleNetworkUnavailable();

    void handleNitzReceived(NitzSignal nitzSignal);

    /* loaded from: classes.dex */
    public static class DeviceStateImpl implements DeviceState {
        private final ContentResolver mCr;
        private final int mNitzUpdateSpacingMillis = SystemProperties.getInt("ro.nitz_update_spacing", (int) CarrierServicesSmsFilter.FILTER_COMPLETE_TIMEOUT_MS);
        private final int mNitzUpdateDiffMillis = SystemProperties.getInt("ro.nitz_update_diff", 2000);
        private final int mNitzNetworkDisconnectRetentionMillis = SystemProperties.getInt("ro.nitz_network_disconnect_retention", 300000);

        public DeviceStateImpl(Phone phone) {
            this.mCr = phone.getContext().getContentResolver();
        }

        @Override // com.android.internal.telephony.NitzStateMachine.DeviceState
        public int getNitzUpdateSpacingMillis() {
            return Settings.Global.getInt(this.mCr, "nitz_update_spacing", this.mNitzUpdateSpacingMillis);
        }

        @Override // com.android.internal.telephony.NitzStateMachine.DeviceState
        public int getNitzUpdateDiffMillis() {
            return Settings.Global.getInt(this.mCr, "nitz_update_diff", this.mNitzUpdateDiffMillis);
        }

        @Override // com.android.internal.telephony.NitzStateMachine.DeviceState
        public int getNitzNetworkDisconnectRetentionMillis() {
            return Settings.Global.getInt(this.mCr, "nitz_network_disconnect_retention", this.mNitzNetworkDisconnectRetentionMillis);
        }

        @Override // com.android.internal.telephony.NitzStateMachine.DeviceState
        public boolean getIgnoreNitz() {
            String str = SystemProperties.get("gsm.ignore-nitz");
            return str != null && str.equals("yes");
        }

        @Override // com.android.internal.telephony.NitzStateMachine.DeviceState
        public long elapsedRealtimeMillis() {
            return SystemClock.elapsedRealtime();
        }

        @Override // com.android.internal.telephony.NitzStateMachine.DeviceState
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }
}
