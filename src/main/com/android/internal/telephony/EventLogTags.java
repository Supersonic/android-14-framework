package com.android.internal.telephony;

import android.util.EventLog;
/* loaded from: classes.dex */
public class EventLogTags {
    public static final int BAD_IP_ADDRESS = 50117;
    public static final int CALL_DROP = 50106;
    public static final int CDMA_DATA_DROP = 50111;
    public static final int CDMA_DATA_SETUP_FAILED = 50110;
    public static final int CDMA_DATA_STATE_CHANGE = 50115;
    public static final int CDMA_SERVICE_STATE_CHANGE = 50116;
    public static final int DATA_NETWORK_REGISTRATION_FAIL = 50107;
    public static final int DATA_NETWORK_STATUS_ON_RADIO_OFF = 50108;
    public static final int DATA_STALL_RECOVERY_CLEANUP = 50119;
    public static final int DATA_STALL_RECOVERY_GET_DATA_CALL_LIST = 50118;
    public static final int DATA_STALL_RECOVERY_RADIO_RESTART = 50121;
    public static final int DATA_STALL_RECOVERY_RADIO_RESTART_WITH_PROP = 50122;
    public static final int DATA_STALL_RECOVERY_REREGISTER = 50120;
    public static final int EXP_DET_SMS_DENIED_BY_USER = 50125;
    public static final int EXP_DET_SMS_SENT_BY_USER = 50128;
    public static final int GSM_DATA_STATE_CHANGE = 50113;
    public static final int GSM_RAT_SWITCHED = 50112;
    public static final int GSM_RAT_SWITCHED_NEW = 50123;
    public static final int GSM_SERVICE_STATE_CHANGE = 50114;
    public static final int PDP_BAD_DNS_ADDRESS = 50100;
    public static final int PDP_CONTEXT_RESET = 50103;
    public static final int PDP_NETWORK_DROP = 50109;
    public static final int PDP_RADIO_RESET = 50102;
    public static final int PDP_RADIO_RESET_COUNTDOWN_TRIGGERED = 50101;
    public static final int PDP_REREGISTER_NETWORK = 50104;
    public static final int PDP_SETUP_FAIL = 50105;

    private EventLogTags() {
    }

    public static void writePdpBadDnsAddress(String str) {
        EventLog.writeEvent((int) PDP_BAD_DNS_ADDRESS, str);
    }

    public static void writePdpRadioResetCountdownTriggered(int i) {
        EventLog.writeEvent((int) PDP_RADIO_RESET_COUNTDOWN_TRIGGERED, i);
    }

    public static void writePdpRadioReset(int i) {
        EventLog.writeEvent((int) PDP_RADIO_RESET, i);
    }

    public static void writePdpContextReset(int i) {
        EventLog.writeEvent((int) PDP_CONTEXT_RESET, i);
    }

    public static void writePdpReregisterNetwork(int i) {
        EventLog.writeEvent((int) PDP_REREGISTER_NETWORK, i);
    }

    public static void writePdpSetupFail(int i, int i2, int i3) {
        EventLog.writeEvent((int) PDP_SETUP_FAIL, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCallDrop(int i, int i2, int i3) {
        EventLog.writeEvent((int) CALL_DROP, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeDataNetworkRegistrationFail(int i, int i2) {
        EventLog.writeEvent((int) DATA_NETWORK_REGISTRATION_FAIL, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeDataNetworkStatusOnRadioOff(String str, int i) {
        EventLog.writeEvent((int) DATA_NETWORK_STATUS_ON_RADIO_OFF, str, Integer.valueOf(i));
    }

    public static void writePdpNetworkDrop(int i, int i2) {
        EventLog.writeEvent((int) PDP_NETWORK_DROP, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCdmaDataSetupFailed(int i, int i2, int i3) {
        EventLog.writeEvent((int) CDMA_DATA_SETUP_FAILED, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCdmaDataDrop(int i, int i2) {
        EventLog.writeEvent((int) CDMA_DATA_DROP, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeGsmRatSwitched(int i, int i2, int i3) {
        EventLog.writeEvent((int) GSM_RAT_SWITCHED, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeGsmDataStateChange(String str, String str2) {
        EventLog.writeEvent((int) GSM_DATA_STATE_CHANGE, str, str2);
    }

    public static void writeGsmServiceStateChange(int i, int i2, int i3, int i4) {
        EventLog.writeEvent((int) GSM_SERVICE_STATE_CHANGE, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    public static void writeCdmaDataStateChange(String str, String str2) {
        EventLog.writeEvent((int) CDMA_DATA_STATE_CHANGE, str, str2);
    }

    public static void writeCdmaServiceStateChange(int i, int i2, int i3, int i4) {
        EventLog.writeEvent((int) CDMA_SERVICE_STATE_CHANGE, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    public static void writeBadIpAddress(String str) {
        EventLog.writeEvent((int) BAD_IP_ADDRESS, str);
    }

    public static void writeDataStallRecoveryGetDataCallList(int i) {
        EventLog.writeEvent((int) DATA_STALL_RECOVERY_GET_DATA_CALL_LIST, i);
    }

    public static void writeDataStallRecoveryCleanup(int i) {
        EventLog.writeEvent((int) DATA_STALL_RECOVERY_CLEANUP, i);
    }

    public static void writeDataStallRecoveryReregister(int i) {
        EventLog.writeEvent((int) DATA_STALL_RECOVERY_REREGISTER, i);
    }

    public static void writeDataStallRecoveryRadioRestart(int i) {
        EventLog.writeEvent((int) DATA_STALL_RECOVERY_RADIO_RESTART, i);
    }

    public static void writeDataStallRecoveryRadioRestartWithProp(int i) {
        EventLog.writeEvent((int) DATA_STALL_RECOVERY_RADIO_RESTART_WITH_PROP, i);
    }

    public static void writeGsmRatSwitchedNew(int i, int i2, int i3) {
        EventLog.writeEvent((int) GSM_RAT_SWITCHED_NEW, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeExpDetSmsDeniedByUser(String str) {
        EventLog.writeEvent((int) EXP_DET_SMS_DENIED_BY_USER, str);
    }

    public static void writeExpDetSmsSentByUser(String str) {
        EventLog.writeEvent((int) EXP_DET_SMS_SENT_BY_USER, str);
    }
}
