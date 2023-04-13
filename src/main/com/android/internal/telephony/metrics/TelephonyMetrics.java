package com.android.internal.telephony.metrics;

import android.content.Context;
import android.net.NetworkCapabilities;
import android.os.BatteryStatsManager;
import android.os.Build;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.telephony.CallQuality;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyHistogram;
import android.telephony.TelephonyManager;
import android.telephony.data.DataCallResponse;
import android.telephony.emergency.EmergencyNumber;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsCallSession;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.feature.MmTelFeature;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.SparseArray;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.CarrierResolver;
import com.android.internal.telephony.GsmCdmaConnection;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.RIL;
import com.android.internal.telephony.SmsController;
import com.android.internal.telephony.SmsResponse;
import com.android.internal.telephony.UUSInfo;
import com.android.internal.telephony.data.LinkBandwidthEstimator;
import com.android.internal.telephony.emergency.EmergencyNumberTracker;
import com.android.internal.telephony.nano.TelephonyProto$ActiveSubscriptionInfo;
import com.android.internal.telephony.nano.TelephonyProto$BandwidthEstimatorStats;
import com.android.internal.telephony.nano.TelephonyProto$EmergencyNumberInfo;
import com.android.internal.telephony.nano.TelephonyProto$ImsCapabilities;
import com.android.internal.telephony.nano.TelephonyProto$ImsConnectionState;
import com.android.internal.telephony.nano.TelephonyProto$ImsReasonInfo;
import com.android.internal.telephony.nano.TelephonyProto$ModemPowerStats;
import com.android.internal.telephony.nano.TelephonyProto$RilDataCall;
import com.android.internal.telephony.nano.TelephonyProto$SmsSession;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyCallSession;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyHistogram;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyLog;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyServiceState;
import com.android.internal.telephony.nano.TelephonyProto$TelephonySettings;
import com.android.internal.telephony.nano.TelephonyProto$Time;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class TelephonyMetrics {
    private static final String TAG = "TelephonyMetrics";
    private static TelephonyMetrics sInstance;
    private Context mContext;
    private final Deque<TelephonyProto$TelephonyEvent> mTelephonyEvents = new ArrayDeque();
    private final SparseArray<InProgressCallSession> mInProgressCallSessions = new SparseArray<>();
    private final Deque<TelephonyProto$TelephonyCallSession> mCompletedCallSessions = new ArrayDeque();
    private final SparseArray<InProgressSmsSession> mInProgressSmsSessions = new SparseArray<>();
    private final Deque<TelephonyProto$SmsSession> mCompletedSmsSessions = new ArrayDeque();
    private final SparseArray<TelephonyProto$TelephonyServiceState> mLastServiceState = new SparseArray<>();
    private final SparseArray<TelephonyProto$ImsCapabilities> mLastImsCapabilities = new SparseArray<>();
    private final SparseArray<TelephonyProto$ImsConnectionState> mLastImsConnectionState = new SparseArray<>();
    private final SparseArray<TelephonyProto$TelephonySettings> mLastSettings = new SparseArray<>();
    private final SparseArray<Integer> mLastSimState = new SparseArray<>();
    private final SparseArray<Integer> mLastRadioState = new SparseArray<>();
    private final SparseArray<TelephonyProto$ActiveSubscriptionInfo> mLastActiveSubscriptionInfos = new SparseArray<>();
    private int mLastEnabledModemBitmap = (1 << TelephonyManager.getDefault().getPhoneCount()) - 1;
    private final SparseArray<TelephonyProto$TelephonyEvent.CarrierIdMatching> mLastCarrierId = new SparseArray<>();
    private final SparseArray<TelephonyProto$TelephonyEvent.NetworkCapabilitiesInfo> mLastNetworkCapabilitiesInfos = new SparseArray<>();
    private final SparseArray<SparseArray<TelephonyProto$RilDataCall>> mLastRilDataCallEvents = new SparseArray<>();
    private final List<Map<String, BwEstimationStats>> mBwEstStatsMapList = new ArrayList(Arrays.asList(new ArrayMap(), new ArrayMap()));
    private boolean mTelephonyEventsDropped = false;
    private long mStartSystemTimeMs = System.currentTimeMillis();
    private long mStartElapsedTimeMs = SystemClock.elapsedRealtime();

    private static int callQualityLevelToProtoEnum(int i) {
        if (i == 0) {
            return 1;
        }
        if (i == 1) {
            return 2;
        }
        if (i == 2) {
            return 3;
        }
        if (i == 3) {
            return 4;
        }
        if (i == 4) {
            return 5;
        }
        return i == 5 ? 6 : 0;
    }

    private int convertGsmCdmaCodec(int i) {
        switch (i) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 8;
            case 4:
                return 9;
            case 5:
                return 10;
            case 6:
                return 4;
            case 7:
                return 5;
            case 8:
                return 6;
            case 9:
                return 7;
            default:
                return 0;
        }
    }

    private static int convertImsCodec(int i) {
        switch (i) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
                return 12;
            case 13:
                return 13;
            case 14:
                return 14;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
            case 19:
                return 19;
            case 20:
                return 20;
            default:
                return 0;
        }
    }

    private static int convertRadioState(int i) {
        if (i != 0) {
            if (i != 1) {
                return i != 2 ? 0 : 3;
            }
            return 2;
        }
        return 1;
    }

    private int getSmsTech(int i, boolean z) {
        if (i == 1) {
            return 3;
        }
        if (i == 0) {
            return z ? 2 : 1;
        }
        return 0;
    }

    private void logv(String str) {
    }

    private static int mapSimStateToProto(int i) {
        if (i != 1) {
            return i != 10 ? 0 : 2;
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int toPrivacyFuzzedTimeInterval(long j, long j2) {
        long j3 = j2 - j;
        if (j3 < 0) {
            return 0;
        }
        if (j3 <= 10) {
            return 1;
        }
        if (j3 <= 20) {
            return 2;
        }
        if (j3 <= 50) {
            return 3;
        }
        if (j3 <= 100) {
            return 4;
        }
        if (j3 <= 200) {
            return 5;
        }
        if (j3 <= 500) {
            return 6;
        }
        if (j3 <= 1000) {
            return 7;
        }
        if (j3 <= 2000) {
            return 8;
        }
        if (j3 <= 5000) {
            return 9;
        }
        if (j3 <= 10000) {
            return 10;
        }
        if (j3 <= 30000) {
            return 11;
        }
        if (j3 <= 60000) {
            return 12;
        }
        if (j3 <= 180000) {
            return 13;
        }
        if (j3 <= 600000) {
            return 14;
        }
        if (j3 <= 1800000) {
            return 15;
        }
        if (j3 <= 3600000) {
            return 16;
        }
        if (j3 <= 7200000) {
            return 17;
        }
        return j3 <= 14400000 ? 18 : 19;
    }

    public void writeOnImsCallHeld(int i, ImsCallSession imsCallSession) {
    }

    public void writeOnImsCallHoldFailed(int i, ImsCallSession imsCallSession, ImsReasonInfo imsReasonInfo) {
    }

    public void writeOnImsCallHoldReceived(int i, ImsCallSession imsCallSession) {
    }

    public void writeOnImsCallInitiating(int i, ImsCallSession imsCallSession) {
    }

    public void writeOnImsCallProgressing(int i, ImsCallSession imsCallSession) {
    }

    public void writeOnImsCallResumeFailed(int i, ImsCallSession imsCallSession, ImsReasonInfo imsReasonInfo) {
    }

    public void writeOnImsCallResumeReceived(int i, ImsCallSession imsCallSession) {
    }

    public void writeOnImsCallResumed(int i, ImsCallSession imsCallSession) {
    }

    public void writeOnImsCallStartFailed(int i, ImsCallSession imsCallSession, ImsReasonInfo imsReasonInfo) {
    }

    public void writeOnImsCallStarted(int i, ImsCallSession imsCallSession) {
    }

    public void writeOnRilTimeoutResponse(int i, int i2, int i3) {
    }

    public static synchronized TelephonyMetrics getInstance() {
        TelephonyMetrics telephonyMetrics;
        synchronized (TelephonyMetrics.class) {
            if (sInstance == null) {
                sInstance = new TelephonyMetrics();
            }
            telephonyMetrics = sInstance;
        }
        return telephonyMetrics;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x004f  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0071 A[Catch: all -> 0x0075, TRY_LEAVE, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x0006, B:8:0x000b, B:12:0x0018, B:33:0x0054, B:34:0x0060, B:36:0x006d, B:37:0x0071, B:19:0x002f, B:22:0x0039, B:25:0x0043), top: B:44:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr != null) {
            if (strArr.length > 0) {
                char c = 0;
                boolean z = strArr.length <= 1 || !"--keep".equals(strArr[1]);
                String str = strArr[0];
                int hashCode = str.hashCode();
                if (hashCode == -1953159389) {
                    if (str.equals("--metrics")) {
                        if (c != 0) {
                        }
                    }
                    c = 65535;
                    if (c != 0) {
                    }
                } else if (hashCode != 513805138) {
                    if (hashCode == 950313125 && str.equals("--metricsproto")) {
                        c = 1;
                        if (c != 0) {
                            printAllMetrics(printWriter);
                        } else if (c == 1) {
                            printWriter.println(convertProtoToBase64String(buildProto()));
                            if (z) {
                                reset();
                            }
                        } else if (c == 2) {
                            printWriter.println(buildProto().toString());
                        }
                    }
                    c = 65535;
                    if (c != 0) {
                    }
                } else {
                    if (str.equals("--metricsprototext")) {
                        c = 2;
                        if (c != 0) {
                        }
                    }
                    c = 65535;
                    if (c != 0) {
                    }
                }
            }
        }
    }

    private static String telephonyEventToString(int i) {
        if (i != 21) {
            if (i != 22) {
                switch (i) {
                    case 0:
                        return "UNKNOWN";
                    case 1:
                        return "SETTINGS_CHANGED";
                    case 2:
                        return "RIL_SERVICE_STATE_CHANGED";
                    case 3:
                        return "IMS_CONNECTION_STATE_CHANGED";
                    case 4:
                        return "IMS_CAPABILITIES_CHANGED";
                    case 5:
                        return "DATA_CALL_SETUP";
                    case 6:
                        return "DATA_CALL_SETUP_RESPONSE";
                    case 7:
                        return "DATA_CALL_LIST_CHANGED";
                    case 8:
                        return "DATA_CALL_DEACTIVATE";
                    case 9:
                        return "DATA_CALL_DEACTIVATE_RESPONSE";
                    case 10:
                        return "DATA_STALL_ACTION";
                    case 11:
                        return "MODEM_RESTART";
                    case 12:
                        return "NITZ_TIME";
                    case 13:
                        return "CARRIER_ID_MATCHING";
                    default:
                        return Integer.toString(i);
                }
            }
            return "NETWORK_CAPABILITIES_CHANGED";
        }
        return "EMERGENCY_NUMBER_REPORT";
    }

    private static String callSessionEventToString(int i) {
        switch (i) {
            case 0:
                return "EVENT_UNKNOWN";
            case 1:
                return "SETTINGS_CHANGED";
            case 2:
                return "RIL_SERVICE_STATE_CHANGED";
            case 3:
                return "IMS_CONNECTION_STATE_CHANGED";
            case 4:
                return "IMS_CAPABILITIES_CHANGED";
            case 5:
                return "DATA_CALL_LIST_CHANGED";
            case 6:
                return "RIL_REQUEST";
            case 7:
                return "RIL_RESPONSE";
            case 8:
                return "RIL_CALL_RING";
            case 9:
                return "RIL_CALL_SRVCC";
            case 10:
                return "RIL_CALL_LIST_CHANGED";
            case 11:
                return "IMS_COMMAND";
            case 12:
                return "IMS_COMMAND_RECEIVED";
            case 13:
                return "IMS_COMMAND_FAILED";
            case 14:
                return "IMS_COMMAND_COMPLETE";
            case 15:
                return "IMS_CALL_RECEIVE";
            case 16:
                return "IMS_CALL_STATE_CHANGED";
            case 17:
                return "IMS_CALL_TERMINATED";
            case 18:
                return "IMS_CALL_HANDOVER";
            case 19:
                return "IMS_CALL_HANDOVER_FAILED";
            case 20:
                return "PHONE_STATE_CHANGED";
            case 21:
                return "NITZ_TIME";
            case 22:
                return "AUDIO_CODEC";
            default:
                return Integer.toString(i);
        }
    }

    private static String smsSessionEventToString(int i) {
        switch (i) {
            case 0:
                return "EVENT_UNKNOWN";
            case 1:
                return "SETTINGS_CHANGED";
            case 2:
                return "RIL_SERVICE_STATE_CHANGED";
            case 3:
                return "IMS_CONNECTION_STATE_CHANGED";
            case 4:
                return "IMS_CAPABILITIES_CHANGED";
            case 5:
                return "DATA_CALL_LIST_CHANGED";
            case 6:
                return "SMS_SEND";
            case 7:
                return "SMS_SEND_RESULT";
            case 8:
                return "SMS_RECEIVED";
            case 9:
            default:
                return Integer.toString(i);
            case 10:
                return "INCOMPLETE_SMS_RECEIVED";
        }
    }

    private synchronized void printAllMetrics(PrintWriter printWriter) {
        TelephonyProto$SmsSession.Event[] eventArr;
        TelephonyProto$TelephonyCallSession.Event[] eventArr2;
        TelephonyProto$TelephonyCallSession.Event.RilCall[] rilCallArr;
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("Telephony metrics proto:");
        indentingPrintWriter.println("------------------------------------------");
        indentingPrintWriter.println("Telephony events:");
        indentingPrintWriter.increaseIndent();
        Iterator<TelephonyProto$TelephonyEvent> it = this.mTelephonyEvents.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            TelephonyProto$TelephonyEvent next = it.next();
            indentingPrintWriter.print(next.timestampMillis);
            indentingPrintWriter.print(" [");
            indentingPrintWriter.print(next.phoneId);
            indentingPrintWriter.print("] ");
            indentingPrintWriter.print("T=");
            int i = next.type;
            if (i == 2) {
                indentingPrintWriter.print(telephonyEventToString(next.type) + "(Data RAT " + next.serviceState.dataRat + " Voice RAT " + next.serviceState.voiceRat + " Channel Number " + next.serviceState.channelNumber + " NR Frequency Range " + next.serviceState.nrFrequencyRange + " NR State " + next.serviceState.nrState + ")");
                for (int i2 = 0; i2 < next.serviceState.networkRegistrationInfo.length; i2++) {
                    indentingPrintWriter.print("reg info: domain=" + next.serviceState.networkRegistrationInfo[i2].domain + ", rat=" + next.serviceState.networkRegistrationInfo[i2].rat);
                }
            } else {
                indentingPrintWriter.print(telephonyEventToString(i));
            }
            indentingPrintWriter.println(PhoneConfigurationManager.SSSS);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Call sessions:");
        indentingPrintWriter.increaseIndent();
        for (TelephonyProto$TelephonyCallSession telephonyProto$TelephonyCallSession : this.mCompletedCallSessions) {
            indentingPrintWriter.print("Start time in minutes: " + telephonyProto$TelephonyCallSession.startTimeMinutes);
            indentingPrintWriter.print(", phone: " + telephonyProto$TelephonyCallSession.phoneId);
            if (telephonyProto$TelephonyCallSession.eventsDropped) {
                indentingPrintWriter.println(", events dropped: " + telephonyProto$TelephonyCallSession.eventsDropped);
            } else {
                indentingPrintWriter.println(PhoneConfigurationManager.SSSS);
            }
            indentingPrintWriter.println("Events: ");
            indentingPrintWriter.increaseIndent();
            for (TelephonyProto$TelephonyCallSession.Event event : telephonyProto$TelephonyCallSession.events) {
                indentingPrintWriter.print(event.delay);
                indentingPrintWriter.print(" T=");
                int i3 = event.type;
                if (i3 == 2) {
                    indentingPrintWriter.println(callSessionEventToString(event.type) + "(Data RAT " + event.serviceState.dataRat + " Voice RAT " + event.serviceState.voiceRat + " Channel Number " + event.serviceState.channelNumber + " NR Frequency Range " + event.serviceState.nrFrequencyRange + " NR State " + event.serviceState.nrState + ")");
                } else if (i3 == 10) {
                    indentingPrintWriter.println(callSessionEventToString(i3));
                    indentingPrintWriter.increaseIndent();
                    for (TelephonyProto$TelephonyCallSession.Event.RilCall rilCall : event.calls) {
                        indentingPrintWriter.println(rilCall.index + ". Type = " + rilCall.type + " State = " + rilCall.state + " End Reason " + rilCall.callEndReason + " Precise Disconnect Cause " + rilCall.preciseDisconnectCause + " isMultiparty = " + rilCall.isMultiparty);
                    }
                    indentingPrintWriter.decreaseIndent();
                } else if (i3 == 22) {
                    indentingPrintWriter.println(callSessionEventToString(event.type) + "(" + event.audioCodec + ")");
                } else {
                    indentingPrintWriter.println(callSessionEventToString(i3));
                }
            }
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Sms sessions:");
        indentingPrintWriter.increaseIndent();
        int i4 = 0;
        for (TelephonyProto$SmsSession telephonyProto$SmsSession : this.mCompletedSmsSessions) {
            i4++;
            indentingPrintWriter.print("[" + i4 + "] Start time in minutes: " + telephonyProto$SmsSession.startTimeMinutes);
            StringBuilder sb = new StringBuilder();
            sb.append(", phone: ");
            sb.append(telephonyProto$SmsSession.phoneId);
            indentingPrintWriter.print(sb.toString());
            if (telephonyProto$SmsSession.eventsDropped) {
                indentingPrintWriter.println(", events dropped: " + telephonyProto$SmsSession.eventsDropped);
            } else {
                indentingPrintWriter.println(PhoneConfigurationManager.SSSS);
            }
            indentingPrintWriter.println("Events: ");
            indentingPrintWriter.increaseIndent();
            for (TelephonyProto$SmsSession.Event event2 : telephonyProto$SmsSession.events) {
                indentingPrintWriter.print(event2.delay);
                indentingPrintWriter.print(" T=");
                int i5 = event2.type;
                if (i5 == 2) {
                    indentingPrintWriter.println(smsSessionEventToString(event2.type) + "(Data RAT " + event2.serviceState.dataRat + " Voice RAT " + event2.serviceState.voiceRat + " Channel Number " + event2.serviceState.channelNumber + " NR Frequency Range " + event2.serviceState.nrFrequencyRange + " NR State " + event2.serviceState.nrState + ")");
                } else if (i5 == 8) {
                    indentingPrintWriter.println(smsSessionEventToString(i5));
                    indentingPrintWriter.increaseIndent();
                    int i6 = event2.smsType;
                    if (i6 == 1) {
                        indentingPrintWriter.println("Type: SMS-PP");
                    } else if (i6 == 2) {
                        indentingPrintWriter.println("Type: Voicemail indication");
                    } else if (i6 == 3) {
                        indentingPrintWriter.println("Type: zero");
                    } else if (i6 == 4) {
                        indentingPrintWriter.println("Type: WAP PUSH");
                    }
                    if (event2.errorCode != 0) {
                        indentingPrintWriter.println("E=" + event2.errorCode);
                    }
                    indentingPrintWriter.decreaseIndent();
                } else {
                    if (i5 != 6 && i5 != 7) {
                        if (i5 == 10) {
                            indentingPrintWriter.println(smsSessionEventToString(i5));
                            indentingPrintWriter.increaseIndent();
                            indentingPrintWriter.println("Received: " + event2.incompleteSms.receivedParts + "/" + event2.incompleteSms.totalParts);
                            indentingPrintWriter.decreaseIndent();
                        }
                    }
                    indentingPrintWriter.println(smsSessionEventToString(i5));
                    indentingPrintWriter.increaseIndent();
                    indentingPrintWriter.println("ReqId=" + event2.rilRequestId);
                    indentingPrintWriter.println("E=" + event2.errorCode);
                    indentingPrintWriter.println("RilE=" + event2.error);
                    indentingPrintWriter.println("ImsE=" + event2.imsError);
                    indentingPrintWriter.decreaseIndent();
                }
            }
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Modem power stats:");
        indentingPrintWriter.increaseIndent();
        Context context = this.mContext;
        TelephonyProto$ModemPowerStats buildProto = new ModemPowerMetrics(context == null ? null : (BatteryStatsManager) context.getSystemService("batterystats")).buildProto();
        indentingPrintWriter.println("Power log duration (battery time) (ms): " + buildProto.loggingDurationMs);
        indentingPrintWriter.println("Energy consumed by modem (mAh): " + buildProto.energyConsumedMah);
        indentingPrintWriter.println("Number of packets sent (tx): " + buildProto.numPacketsTx);
        indentingPrintWriter.println("Number of bytes sent (tx): " + buildProto.numBytesTx);
        indentingPrintWriter.println("Number of packets received (rx): " + buildProto.numPacketsRx);
        indentingPrintWriter.println("Number of bytes received (rx): " + buildProto.numBytesRx);
        indentingPrintWriter.println("Amount of time kernel is active because of cellular data (ms): " + buildProto.cellularKernelActiveTimeMs);
        indentingPrintWriter.println("Amount of time spent in very poor rx signal level (ms): " + buildProto.timeInVeryPoorRxSignalLevelMs);
        indentingPrintWriter.println("Amount of time modem is in sleep (ms): " + buildProto.sleepTimeMs);
        indentingPrintWriter.println("Amount of time modem is in idle (ms): " + buildProto.idleTimeMs);
        indentingPrintWriter.println("Amount of time modem is in rx (ms): " + buildProto.rxTimeMs);
        indentingPrintWriter.println("Amount of time modem is in tx (ms): " + Arrays.toString(buildProto.txTimeMs));
        indentingPrintWriter.println("Amount of time phone spent in various Radio Access Technologies (ms): " + Arrays.toString(buildProto.timeInRatMs));
        indentingPrintWriter.println("Amount of time phone spent in various cellular rx signal strength levels (ms): " + Arrays.toString(buildProto.timeInRxSignalStrengthLevelMs));
        indentingPrintWriter.println("Energy consumed across measured modem rails (mAh): " + new DecimalFormat("#.##").format(buildProto.monitoredRailEnergyConsumedMah));
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Hardware Version: " + SystemProperties.get("ro.boot.revision", PhoneConfigurationManager.SSSS));
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("LinkBandwidthEstimator stats:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Tx");
        for (BwEstimationStats bwEstimationStats : this.mBwEstStatsMapList.get(0).values()) {
            indentingPrintWriter.println(bwEstimationStats.toString());
        }
        indentingPrintWriter.println("Rx");
        for (BwEstimationStats bwEstimationStats2 : this.mBwEstStatsMapList.get(1).values()) {
            indentingPrintWriter.println(bwEstimationStats2.toString());
        }
        RcsStats.getInstance().printAllMetrics(printWriter);
    }

    private static String convertProtoToBase64String(TelephonyProto$TelephonyLog telephonyProto$TelephonyLog) {
        return Base64.encodeToString(MessageNano.toByteArray(telephonyProto$TelephonyLog), 0);
    }

    private synchronized void reset() {
        this.mTelephonyEvents.clear();
        this.mCompletedCallSessions.clear();
        this.mCompletedSmsSessions.clear();
        this.mBwEstStatsMapList.get(0).clear();
        this.mBwEstStatsMapList.get(1).clear();
        this.mTelephonyEventsDropped = false;
        this.mStartSystemTimeMs = System.currentTimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mStartElapsedTimeMs = elapsedRealtime;
        addTelephonyEvent(new TelephonyEventBuilder(elapsedRealtime, -1).setSimStateChange(this.mLastSimState).build());
        addTelephonyEvent(new TelephonyEventBuilder(this.mStartElapsedTimeMs, -1).setEnabledModemBitmap(this.mLastEnabledModemBitmap).build());
        for (int i = 0; i < this.mLastActiveSubscriptionInfos.size(); i++) {
            int keyAt = this.mLastActiveSubscriptionInfos.keyAt(i);
            addTelephonyEvent(new TelephonyEventBuilder(this.mStartElapsedTimeMs, keyAt).setActiveSubscriptionInfoChange(this.mLastActiveSubscriptionInfos.get(keyAt)).build());
        }
        for (int i2 = 0; i2 < this.mLastServiceState.size(); i2++) {
            int keyAt2 = this.mLastServiceState.keyAt(i2);
            addTelephonyEvent(new TelephonyEventBuilder(this.mStartElapsedTimeMs, keyAt2).setServiceState(this.mLastServiceState.get(keyAt2)).build());
        }
        for (int i3 = 0; i3 < this.mLastImsCapabilities.size(); i3++) {
            int keyAt3 = this.mLastImsCapabilities.keyAt(i3);
            addTelephonyEvent(new TelephonyEventBuilder(this.mStartElapsedTimeMs, keyAt3).setImsCapabilities(this.mLastImsCapabilities.get(keyAt3)).build());
        }
        for (int i4 = 0; i4 < this.mLastImsConnectionState.size(); i4++) {
            int keyAt4 = this.mLastImsConnectionState.keyAt(i4);
            addTelephonyEvent(new TelephonyEventBuilder(this.mStartElapsedTimeMs, keyAt4).setImsConnectionState(this.mLastImsConnectionState.get(keyAt4)).build());
        }
        for (int i5 = 0; i5 < this.mLastCarrierId.size(); i5++) {
            int keyAt5 = this.mLastCarrierId.keyAt(i5);
            addTelephonyEvent(new TelephonyEventBuilder(this.mStartElapsedTimeMs, keyAt5).setCarrierIdMatching(this.mLastCarrierId.get(keyAt5)).build());
        }
        for (int i6 = 0; i6 < this.mLastNetworkCapabilitiesInfos.size(); i6++) {
            int keyAt6 = this.mLastNetworkCapabilitiesInfos.keyAt(i6);
            addTelephonyEvent(new TelephonyEventBuilder(this.mStartElapsedTimeMs, keyAt6).setNetworkCapabilities(this.mLastNetworkCapabilitiesInfos.get(keyAt6)).build());
        }
        for (int i7 = 0; i7 < this.mLastRilDataCallEvents.size(); i7++) {
            int keyAt7 = this.mLastRilDataCallEvents.keyAt(i7);
            for (int i8 = 0; i8 < this.mLastRilDataCallEvents.get(keyAt7).size(); i8++) {
                addTelephonyEvent(new TelephonyEventBuilder(this.mStartElapsedTimeMs, keyAt7).setDataCalls(new TelephonyProto$RilDataCall[]{this.mLastRilDataCallEvents.get(keyAt7).get(this.mLastRilDataCallEvents.get(keyAt7).keyAt(i8))}).build());
            }
        }
        for (int i9 = 0; i9 < this.mLastRadioState.size(); i9++) {
            int keyAt8 = this.mLastRadioState.keyAt(i9);
            addTelephonyEvent(new TelephonyEventBuilder(this.mStartElapsedTimeMs, keyAt8).setRadioState(this.mLastRadioState.get(keyAt8).intValue()).build());
        }
    }

    private synchronized TelephonyProto$TelephonyLog buildProto() {
        TelephonyProto$TelephonyLog telephonyProto$TelephonyLog;
        telephonyProto$TelephonyLog = new TelephonyProto$TelephonyLog();
        TelephonyProto$TelephonyEvent[] telephonyProto$TelephonyEventArr = new TelephonyProto$TelephonyEvent[this.mTelephonyEvents.size()];
        telephonyProto$TelephonyLog.events = telephonyProto$TelephonyEventArr;
        this.mTelephonyEvents.toArray(telephonyProto$TelephonyEventArr);
        telephonyProto$TelephonyLog.eventsDropped = this.mTelephonyEventsDropped;
        TelephonyProto$TelephonyCallSession[] telephonyProto$TelephonyCallSessionArr = new TelephonyProto$TelephonyCallSession[this.mCompletedCallSessions.size()];
        telephonyProto$TelephonyLog.callSessions = telephonyProto$TelephonyCallSessionArr;
        this.mCompletedCallSessions.toArray(telephonyProto$TelephonyCallSessionArr);
        TelephonyProto$SmsSession[] telephonyProto$SmsSessionArr = new TelephonyProto$SmsSession[this.mCompletedSmsSessions.size()];
        telephonyProto$TelephonyLog.smsSessions = telephonyProto$SmsSessionArr;
        this.mCompletedSmsSessions.toArray(telephonyProto$SmsSessionArr);
        List<TelephonyHistogram> telephonyRILTimingHistograms = RIL.getTelephonyRILTimingHistograms();
        telephonyProto$TelephonyLog.histograms = new TelephonyProto$TelephonyHistogram[telephonyRILTimingHistograms.size()];
        for (int i = 0; i < telephonyRILTimingHistograms.size(); i++) {
            telephonyProto$TelephonyLog.histograms[i] = new TelephonyProto$TelephonyHistogram();
            TelephonyHistogram telephonyHistogram = telephonyRILTimingHistograms.get(i);
            TelephonyProto$TelephonyHistogram telephonyProto$TelephonyHistogram = telephonyProto$TelephonyLog.histograms[i];
            telephonyProto$TelephonyHistogram.category = telephonyHistogram.getCategory();
            telephonyProto$TelephonyHistogram.f17id = telephonyHistogram.getId();
            telephonyProto$TelephonyHistogram.minTimeMillis = telephonyHistogram.getMinTime();
            telephonyProto$TelephonyHistogram.maxTimeMillis = telephonyHistogram.getMaxTime();
            telephonyProto$TelephonyHistogram.avgTimeMillis = telephonyHistogram.getAverageTime();
            telephonyProto$TelephonyHistogram.count = telephonyHistogram.getSampleCount();
            telephonyProto$TelephonyHistogram.bucketCount = telephonyHistogram.getBucketCount();
            telephonyProto$TelephonyHistogram.bucketEndPoints = telephonyHistogram.getBucketEndPoints();
            telephonyProto$TelephonyHistogram.bucketCounters = telephonyHistogram.getBucketCounters();
        }
        Context context = this.mContext;
        telephonyProto$TelephonyLog.modemPowerStats = new ModemPowerMetrics(context == null ? null : (BatteryStatsManager) context.getSystemService("batterystats")).buildProto();
        telephonyProto$TelephonyLog.hardwareRevision = SystemProperties.get("ro.boot.revision", PhoneConfigurationManager.SSSS);
        TelephonyProto$Time telephonyProto$Time = new TelephonyProto$Time();
        telephonyProto$TelephonyLog.startTime = telephonyProto$Time;
        telephonyProto$Time.systemTimestampMillis = this.mStartSystemTimeMs;
        telephonyProto$Time.elapsedTimestampMillis = this.mStartElapsedTimeMs;
        TelephonyProto$Time telephonyProto$Time2 = new TelephonyProto$Time();
        telephonyProto$TelephonyLog.endTime = telephonyProto$Time2;
        telephonyProto$Time2.systemTimestampMillis = System.currentTimeMillis();
        telephonyProto$TelephonyLog.endTime.elapsedTimestampMillis = SystemClock.elapsedRealtime();
        int phoneCount = TelephonyManager.getDefault().getPhoneCount();
        TelephonyProto$ActiveSubscriptionInfo[] telephonyProto$ActiveSubscriptionInfoArr = new TelephonyProto$ActiveSubscriptionInfo[phoneCount];
        for (int i2 = 0; i2 < this.mLastActiveSubscriptionInfos.size(); i2++) {
            int keyAt = this.mLastActiveSubscriptionInfos.keyAt(i2);
            telephonyProto$ActiveSubscriptionInfoArr[keyAt] = this.mLastActiveSubscriptionInfos.get(keyAt);
        }
        for (int i3 = 0; i3 < phoneCount; i3++) {
            if (telephonyProto$ActiveSubscriptionInfoArr[i3] == null) {
                telephonyProto$ActiveSubscriptionInfoArr[i3] = makeInvalidSubscriptionInfo(i3);
            }
        }
        telephonyProto$TelephonyLog.lastActiveSubscriptionInfo = telephonyProto$ActiveSubscriptionInfoArr;
        telephonyProto$TelephonyLog.bandwidthEstimatorStats = buildBandwidthEstimatorStats();
        return telephonyProto$TelephonyLog;
    }

    public void updateSimState(int i, int i2) {
        int mapSimStateToProto = mapSimStateToProto(i2);
        Integer num = this.mLastSimState.get(i);
        if (num == null || !num.equals(Integer.valueOf(mapSimStateToProto))) {
            this.mLastSimState.put(i, Integer.valueOf(mapSimStateToProto));
            addTelephonyEvent(new TelephonyEventBuilder(i).setSimStateChange(this.mLastSimState).build());
        }
    }

    public synchronized void updateActiveSubscriptionInfoList(List<SubscriptionInfo> list) {
        ArrayList<Integer> arrayList = new ArrayList();
        for (int i = 0; i < this.mLastActiveSubscriptionInfos.size(); i++) {
            arrayList.add(Integer.valueOf(this.mLastActiveSubscriptionInfos.keyAt(i)));
        }
        for (SubscriptionInfo subscriptionInfo : list) {
            final int simSlotIndex = subscriptionInfo.getSimSlotIndex();
            arrayList.removeIf(new Predicate() { // from class: com.android.internal.telephony.metrics.TelephonyMetrics$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateActiveSubscriptionInfoList$0;
                    lambda$updateActiveSubscriptionInfoList$0 = TelephonyMetrics.lambda$updateActiveSubscriptionInfoList$0(simSlotIndex, (Integer) obj);
                    return lambda$updateActiveSubscriptionInfoList$0;
                }
            });
            TelephonyProto$ActiveSubscriptionInfo telephonyProto$ActiveSubscriptionInfo = new TelephonyProto$ActiveSubscriptionInfo();
            telephonyProto$ActiveSubscriptionInfo.slotIndex = simSlotIndex;
            telephonyProto$ActiveSubscriptionInfo.isOpportunistic = subscriptionInfo.isOpportunistic() ? 1 : 0;
            telephonyProto$ActiveSubscriptionInfo.carrierId = subscriptionInfo.getCarrierId();
            if (subscriptionInfo.getMccString() != null && subscriptionInfo.getMncString() != null) {
                telephonyProto$ActiveSubscriptionInfo.simMccmnc = subscriptionInfo.getMccString() + subscriptionInfo.getMncString();
            }
            if (!MessageNano.messageNanoEquals(this.mLastActiveSubscriptionInfos.get(simSlotIndex), telephonyProto$ActiveSubscriptionInfo)) {
                addTelephonyEvent(new TelephonyEventBuilder(simSlotIndex).setActiveSubscriptionInfoChange(telephonyProto$ActiveSubscriptionInfo).build());
                this.mLastActiveSubscriptionInfos.put(simSlotIndex, telephonyProto$ActiveSubscriptionInfo);
            }
        }
        for (Integer num : arrayList) {
            int intValue = num.intValue();
            this.mLastActiveSubscriptionInfos.remove(intValue);
            addTelephonyEvent(new TelephonyEventBuilder(intValue).setActiveSubscriptionInfoChange(makeInvalidSubscriptionInfo(intValue)).build());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateActiveSubscriptionInfoList$0(int i, Integer num) {
        return num.equals(Integer.valueOf(i));
    }

    public void updateEnabledModemBitmap(int i) {
        if (this.mLastEnabledModemBitmap == i) {
            return;
        }
        this.mLastEnabledModemBitmap = i;
        addTelephonyEvent(new TelephonyEventBuilder().setEnabledModemBitmap(this.mLastEnabledModemBitmap).build());
    }

    private static TelephonyProto$ActiveSubscriptionInfo makeInvalidSubscriptionInfo(int i) {
        TelephonyProto$ActiveSubscriptionInfo telephonyProto$ActiveSubscriptionInfo = new TelephonyProto$ActiveSubscriptionInfo();
        telephonyProto$ActiveSubscriptionInfo.slotIndex = i;
        telephonyProto$ActiveSubscriptionInfo.carrierId = -1;
        telephonyProto$ActiveSubscriptionInfo.isOpportunistic = -1;
        return telephonyProto$ActiveSubscriptionInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int roundSessionStart(long j) {
        return (int) ((j / 300000) * 5);
    }

    public void writeCarrierKeyEvent(int i, int i2, boolean z) {
        TelephonyProto$TelephonyEvent.CarrierKeyChange carrierKeyChange = new TelephonyProto$TelephonyEvent.CarrierKeyChange();
        carrierKeyChange.keyType = i2;
        carrierKeyChange.isDownloadSuccessful = z;
        addTelephonyEvent(new TelephonyEventBuilder(i).setCarrierKeyChange(carrierKeyChange).build());
    }

    private TelephonyProto$TelephonyServiceState toServiceStateProto(ServiceState serviceState) {
        TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState = new TelephonyProto$TelephonyServiceState();
        telephonyProto$TelephonyServiceState.voiceRoamingType = serviceState.getVoiceRoamingType();
        telephonyProto$TelephonyServiceState.dataRoamingType = serviceState.getDataRoamingType();
        telephonyProto$TelephonyServiceState.voiceOperator = new TelephonyProto$TelephonyServiceState.TelephonyOperator();
        telephonyProto$TelephonyServiceState.dataOperator = new TelephonyProto$TelephonyServiceState.TelephonyOperator();
        if (serviceState.getOperatorAlphaLong() != null) {
            telephonyProto$TelephonyServiceState.voiceOperator.alphaLong = serviceState.getOperatorAlphaLong();
            telephonyProto$TelephonyServiceState.dataOperator.alphaLong = serviceState.getOperatorAlphaLong();
        }
        if (serviceState.getOperatorAlphaShort() != null) {
            telephonyProto$TelephonyServiceState.voiceOperator.alphaShort = serviceState.getOperatorAlphaShort();
            telephonyProto$TelephonyServiceState.dataOperator.alphaShort = serviceState.getOperatorAlphaShort();
        }
        if (serviceState.getOperatorNumeric() != null) {
            telephonyProto$TelephonyServiceState.voiceOperator.numeric = serviceState.getOperatorNumeric();
            telephonyProto$TelephonyServiceState.dataOperator.numeric = serviceState.getOperatorNumeric();
        }
        ArrayList arrayList = new ArrayList();
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 1);
        if (networkRegistrationInfo != null) {
            TelephonyProto$TelephonyServiceState.NetworkRegistrationInfo networkRegistrationInfo2 = new TelephonyProto$TelephonyServiceState.NetworkRegistrationInfo();
            networkRegistrationInfo2.domain = 2;
            networkRegistrationInfo2.transport = 1;
            networkRegistrationInfo2.rat = ServiceState.networkTypeToRilRadioTechnology(networkRegistrationInfo.getAccessNetworkTechnology());
            arrayList.add(networkRegistrationInfo2);
            TelephonyProto$TelephonyServiceState.NetworkRegistrationInfo[] networkRegistrationInfoArr = new TelephonyProto$TelephonyServiceState.NetworkRegistrationInfo[arrayList.size()];
            telephonyProto$TelephonyServiceState.networkRegistrationInfo = networkRegistrationInfoArr;
            arrayList.toArray(networkRegistrationInfoArr);
        }
        telephonyProto$TelephonyServiceState.voiceRat = serviceState.getRilVoiceRadioTechnology();
        telephonyProto$TelephonyServiceState.dataRat = serviceState.getRilDataRadioTechnology();
        telephonyProto$TelephonyServiceState.channelNumber = serviceState.getChannelNumber();
        telephonyProto$TelephonyServiceState.nrFrequencyRange = serviceState.getNrFrequencyRange();
        telephonyProto$TelephonyServiceState.nrState = serviceState.getNrState();
        return telephonyProto$TelephonyServiceState;
    }

    private synchronized void annotateInProgressCallSession(long j, int i, CallSessionEventBuilder callSessionEventBuilder) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession != null) {
            inProgressCallSession.addEvent(j, callSessionEventBuilder);
        }
    }

    private synchronized void annotateInProgressSmsSession(long j, int i, SmsSessionEventBuilder smsSessionEventBuilder) {
        InProgressSmsSession inProgressSmsSession = this.mInProgressSmsSessions.get(i);
        if (inProgressSmsSession != null) {
            inProgressSmsSession.addEvent(j, smsSessionEventBuilder);
        }
    }

    private synchronized InProgressCallSession startNewCallSessionIfNeeded(int i) {
        InProgressCallSession inProgressCallSession;
        inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            logv("Starting a new call session on phone " + i);
            inProgressCallSession = new InProgressCallSession(i);
            this.mInProgressCallSessions.append(i, inProgressCallSession);
            TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState = this.mLastServiceState.get(i);
            if (telephonyProto$TelephonyServiceState != null) {
                inProgressCallSession.addEvent(inProgressCallSession.startElapsedTimeMs, new CallSessionEventBuilder(2).setServiceState(telephonyProto$TelephonyServiceState));
            }
            TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities = this.mLastImsCapabilities.get(i);
            if (telephonyProto$ImsCapabilities != null) {
                inProgressCallSession.addEvent(inProgressCallSession.startElapsedTimeMs, new CallSessionEventBuilder(4).setImsCapabilities(telephonyProto$ImsCapabilities));
            }
            TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState = this.mLastImsConnectionState.get(i);
            if (telephonyProto$ImsConnectionState != null) {
                inProgressCallSession.addEvent(inProgressCallSession.startElapsedTimeMs, new CallSessionEventBuilder(3).setImsConnectionState(telephonyProto$ImsConnectionState));
            }
        }
        return inProgressCallSession;
    }

    private synchronized InProgressSmsSession startNewSmsSessionIfNeeded(int i) {
        InProgressSmsSession inProgressSmsSession;
        inProgressSmsSession = this.mInProgressSmsSessions.get(i);
        if (inProgressSmsSession == null) {
            logv("Starting a new sms session on phone " + i);
            inProgressSmsSession = startNewSmsSession(i);
            this.mInProgressSmsSessions.append(i, inProgressSmsSession);
        }
        return inProgressSmsSession;
    }

    private InProgressSmsSession startNewSmsSession(int i) {
        InProgressSmsSession inProgressSmsSession = new InProgressSmsSession(i);
        TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState = this.mLastServiceState.get(i);
        if (telephonyProto$TelephonyServiceState != null) {
            inProgressSmsSession.addEvent(inProgressSmsSession.startElapsedTimeMs, new SmsSessionEventBuilder(2).setServiceState(telephonyProto$TelephonyServiceState));
        }
        TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities = this.mLastImsCapabilities.get(i);
        if (telephonyProto$ImsCapabilities != null) {
            inProgressSmsSession.addEvent(inProgressSmsSession.startElapsedTimeMs, new SmsSessionEventBuilder(4).setImsCapabilities(telephonyProto$ImsCapabilities));
        }
        TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState = this.mLastImsConnectionState.get(i);
        if (telephonyProto$ImsConnectionState != null) {
            inProgressSmsSession.addEvent(inProgressSmsSession.startElapsedTimeMs, new SmsSessionEventBuilder(3).setImsConnectionState(telephonyProto$ImsConnectionState));
        }
        return inProgressSmsSession;
    }

    private synchronized void finishCallSession(InProgressCallSession inProgressCallSession) {
        TelephonyProto$TelephonyCallSession telephonyProto$TelephonyCallSession = new TelephonyProto$TelephonyCallSession();
        TelephonyProto$TelephonyCallSession.Event[] eventArr = new TelephonyProto$TelephonyCallSession.Event[inProgressCallSession.events.size()];
        telephonyProto$TelephonyCallSession.events = eventArr;
        inProgressCallSession.events.toArray(eventArr);
        telephonyProto$TelephonyCallSession.startTimeMinutes = inProgressCallSession.startSystemTimeMin;
        telephonyProto$TelephonyCallSession.phoneId = inProgressCallSession.phoneId;
        telephonyProto$TelephonyCallSession.eventsDropped = inProgressCallSession.isEventsDropped();
        if (this.mCompletedCallSessions.size() >= 50) {
            this.mCompletedCallSessions.removeFirst();
        }
        this.mCompletedCallSessions.add(telephonyProto$TelephonyCallSession);
        this.mInProgressCallSessions.remove(inProgressCallSession.phoneId);
        logv("Call session finished");
    }

    private synchronized void finishSmsSessionIfNeeded(InProgressSmsSession inProgressSmsSession) {
        if (inProgressSmsSession.getNumExpectedResponses() == 0) {
            finishSmsSession(inProgressSmsSession);
            this.mInProgressSmsSessions.remove(inProgressSmsSession.phoneId);
            logv("SMS session finished");
        }
    }

    private synchronized TelephonyProto$SmsSession finishSmsSession(InProgressSmsSession inProgressSmsSession) {
        TelephonyProto$SmsSession telephonyProto$SmsSession;
        telephonyProto$SmsSession = new TelephonyProto$SmsSession();
        TelephonyProto$SmsSession.Event[] eventArr = new TelephonyProto$SmsSession.Event[inProgressSmsSession.events.size()];
        telephonyProto$SmsSession.events = eventArr;
        inProgressSmsSession.events.toArray(eventArr);
        telephonyProto$SmsSession.startTimeMinutes = inProgressSmsSession.startSystemTimeMin;
        telephonyProto$SmsSession.phoneId = inProgressSmsSession.phoneId;
        telephonyProto$SmsSession.eventsDropped = inProgressSmsSession.isEventsDropped();
        if (this.mCompletedSmsSessions.size() >= 500) {
            this.mCompletedSmsSessions.removeFirst();
        }
        this.mCompletedSmsSessions.add(telephonyProto$SmsSession);
        return telephonyProto$SmsSession;
    }

    private synchronized void addTelephonyEvent(TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent) {
        if (this.mTelephonyEvents.size() >= 1000) {
            this.mTelephonyEvents.removeFirst();
            this.mTelephonyEventsDropped = true;
        }
        this.mTelephonyEvents.add(telephonyProto$TelephonyEvent);
    }

    public synchronized void writeServiceStateChanged(int i, ServiceState serviceState) {
        TelephonyProto$TelephonyEvent build = new TelephonyEventBuilder(i).setServiceState(toServiceStateProto(serviceState)).build();
        if (this.mLastServiceState.get(i) == null || !Arrays.equals(MessageNano.toByteArray(this.mLastServiceState.get(i)), MessageNano.toByteArray(build.serviceState))) {
            this.mLastServiceState.put(i, build.serviceState);
            addTelephonyEvent(build);
            annotateInProgressCallSession(build.timestampMillis, i, new CallSessionEventBuilder(2).setServiceState(build.serviceState));
            annotateInProgressSmsSession(build.timestampMillis, i, new SmsSessionEventBuilder(2).setServiceState(build.serviceState));
        }
    }

    public void writeDataStallEvent(int i, int i2) {
        addTelephonyEvent(new TelephonyEventBuilder(i).setDataStallRecoveryAction(i2).build());
    }

    public void writeSignalStrengthEvent(int i, int i2) {
        addTelephonyEvent(new TelephonyEventBuilder(i).setSignalStrength(i2).build());
    }

    private TelephonyProto$TelephonySettings cloneCurrentTelephonySettings(int i) {
        TelephonyProto$TelephonySettings telephonyProto$TelephonySettings = new TelephonyProto$TelephonySettings();
        TelephonyProto$TelephonySettings telephonyProto$TelephonySettings2 = this.mLastSettings.get(i);
        if (telephonyProto$TelephonySettings2 != null) {
            telephonyProto$TelephonySettings.preferredNetworkMode = telephonyProto$TelephonySettings2.preferredNetworkMode;
            telephonyProto$TelephonySettings.isEnhanced4GLteModeEnabled = telephonyProto$TelephonySettings2.isEnhanced4GLteModeEnabled;
            telephonyProto$TelephonySettings.isVtOverLteEnabled = telephonyProto$TelephonySettings2.isVtOverLteEnabled;
            telephonyProto$TelephonySettings.isWifiCallingEnabled = telephonyProto$TelephonySettings2.isWifiCallingEnabled;
            telephonyProto$TelephonySettings.isVtOverWifiEnabled = telephonyProto$TelephonySettings2.isVtOverWifiEnabled;
        }
        return telephonyProto$TelephonySettings;
    }

    public synchronized void writeImsSetFeatureValue(int i, int i2, int i3, int i4) {
        TelephonyProto$TelephonySettings cloneCurrentTelephonySettings = cloneCurrentTelephonySettings(i);
        if (i3 == 0) {
            if (i2 == 1) {
                cloneCurrentTelephonySettings.isEnhanced4GLteModeEnabled = i4 != 0;
            } else if (i2 == 2) {
                cloneCurrentTelephonySettings.isVtOverLteEnabled = i4 != 0;
            }
        } else if (i3 == 1) {
            if (i2 == 1) {
                cloneCurrentTelephonySettings.isWifiCallingEnabled = i4 != 0;
            } else if (i2 == 2) {
                cloneCurrentTelephonySettings.isVtOverWifiEnabled = i4 != 0;
            }
        }
        if (this.mLastSettings.get(i) == null || !Arrays.equals(MessageNano.toByteArray(this.mLastSettings.get(i)), MessageNano.toByteArray(cloneCurrentTelephonySettings))) {
            this.mLastSettings.put(i, cloneCurrentTelephonySettings);
            TelephonyProto$TelephonyEvent build = new TelephonyEventBuilder(i).setSettings(cloneCurrentTelephonySettings).build();
            addTelephonyEvent(build);
            annotateInProgressCallSession(build.timestampMillis, i, new CallSessionEventBuilder(1).setSettings(cloneCurrentTelephonySettings));
            annotateInProgressSmsSession(build.timestampMillis, i, new SmsSessionEventBuilder(1).setSettings(cloneCurrentTelephonySettings));
        }
    }

    public synchronized void writeSetPreferredNetworkType(int i, int i2) {
        TelephonyProto$TelephonySettings cloneCurrentTelephonySettings = cloneCurrentTelephonySettings(i);
        cloneCurrentTelephonySettings.preferredNetworkMode = i2 + 1;
        if (this.mLastSettings.get(i) == null || !Arrays.equals(MessageNano.toByteArray(this.mLastSettings.get(i)), MessageNano.toByteArray(cloneCurrentTelephonySettings))) {
            this.mLastSettings.put(i, cloneCurrentTelephonySettings);
            addTelephonyEvent(new TelephonyEventBuilder(i).setSettings(cloneCurrentTelephonySettings).build());
        }
    }

    public synchronized void writeOnImsConnectionState(int i, int i2, ImsReasonInfo imsReasonInfo) {
        TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState = new TelephonyProto$ImsConnectionState();
        telephonyProto$ImsConnectionState.state = i2;
        if (imsReasonInfo != null) {
            TelephonyProto$ImsReasonInfo telephonyProto$ImsReasonInfo = new TelephonyProto$ImsReasonInfo();
            telephonyProto$ImsReasonInfo.reasonCode = imsReasonInfo.getCode();
            telephonyProto$ImsReasonInfo.extraCode = imsReasonInfo.getExtraCode();
            String extraMessage = imsReasonInfo.getExtraMessage();
            if (extraMessage != null) {
                telephonyProto$ImsReasonInfo.extraMessage = extraMessage;
            }
            telephonyProto$ImsConnectionState.reasonInfo = telephonyProto$ImsReasonInfo;
        }
        if (this.mLastImsConnectionState.get(i) == null || !Arrays.equals(MessageNano.toByteArray(this.mLastImsConnectionState.get(i)), MessageNano.toByteArray(telephonyProto$ImsConnectionState))) {
            this.mLastImsConnectionState.put(i, telephonyProto$ImsConnectionState);
            TelephonyProto$TelephonyEvent build = new TelephonyEventBuilder(i).setImsConnectionState(telephonyProto$ImsConnectionState).build();
            addTelephonyEvent(build);
            annotateInProgressCallSession(build.timestampMillis, i, new CallSessionEventBuilder(3).setImsConnectionState(build.imsConnectionState));
            annotateInProgressSmsSession(build.timestampMillis, i, new SmsSessionEventBuilder(3).setImsConnectionState(build.imsConnectionState));
        }
    }

    public synchronized void writeOnImsCapabilities(int i, int i2, MmTelFeature.MmTelCapabilities mmTelCapabilities) {
        TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities = new TelephonyProto$ImsCapabilities();
        if (i2 == 0) {
            telephonyProto$ImsCapabilities.voiceOverLte = mmTelCapabilities.isCapable(1);
            telephonyProto$ImsCapabilities.videoOverLte = mmTelCapabilities.isCapable(2);
            telephonyProto$ImsCapabilities.utOverLte = mmTelCapabilities.isCapable(4);
        } else if (i2 == 1) {
            telephonyProto$ImsCapabilities.voiceOverWifi = mmTelCapabilities.isCapable(1);
            telephonyProto$ImsCapabilities.videoOverWifi = mmTelCapabilities.isCapable(2);
            telephonyProto$ImsCapabilities.utOverWifi = mmTelCapabilities.isCapable(4);
        }
        TelephonyProto$TelephonyEvent build = new TelephonyEventBuilder(i).setImsCapabilities(telephonyProto$ImsCapabilities).build();
        if (this.mLastImsCapabilities.get(i) == null || !Arrays.equals(MessageNano.toByteArray(this.mLastImsCapabilities.get(i)), MessageNano.toByteArray(telephonyProto$ImsCapabilities))) {
            this.mLastImsCapabilities.put(i, telephonyProto$ImsCapabilities);
            addTelephonyEvent(build);
            annotateInProgressCallSession(build.timestampMillis, i, new CallSessionEventBuilder(4).setImsCapabilities(build.imsCapabilities));
            annotateInProgressSmsSession(build.timestampMillis, i, new SmsSessionEventBuilder(4).setImsCapabilities(build.imsCapabilities));
        }
    }

    public void writeSetupDataCall(int i, int i2, int i3, String str, int i4) {
        TelephonyProto$TelephonyEvent.RilSetupDataCall rilSetupDataCall = new TelephonyProto$TelephonyEvent.RilSetupDataCall();
        rilSetupDataCall.rat = i2;
        rilSetupDataCall.dataProfile = i3 + 1;
        if (str != null) {
            rilSetupDataCall.apn = str;
        }
        rilSetupDataCall.type = i4 + 1;
        addTelephonyEvent(new TelephonyEventBuilder(i).setSetupDataCall(rilSetupDataCall).build());
    }

    public void writeRilDeactivateDataCall(int i, int i2, int i3, int i4) {
        TelephonyProto$TelephonyEvent.RilDeactivateDataCall rilDeactivateDataCall = new TelephonyProto$TelephonyEvent.RilDeactivateDataCall();
        rilDeactivateDataCall.cid = i3;
        if (i4 == 1) {
            rilDeactivateDataCall.reason = 1;
        } else if (i4 == 2) {
            rilDeactivateDataCall.reason = 2;
        } else if (i4 == 3) {
            rilDeactivateDataCall.reason = 4;
        } else {
            rilDeactivateDataCall.reason = 0;
        }
        addTelephonyEvent(new TelephonyEventBuilder(i).setDeactivateDataCall(rilDeactivateDataCall).build());
    }

    public void writeRilDataCallEvent(int i, int i2, int i3, int i4) {
        SparseArray<TelephonyProto$RilDataCall> sparseArray;
        TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr = {new TelephonyProto$RilDataCall()};
        TelephonyProto$RilDataCall telephonyProto$RilDataCall = telephonyProto$RilDataCallArr[0];
        telephonyProto$RilDataCall.cid = i2;
        telephonyProto$RilDataCall.apnTypeBitmask = i3;
        telephonyProto$RilDataCall.state = i4;
        if (this.mLastRilDataCallEvents.get(i) != null) {
            if (this.mLastRilDataCallEvents.get(i).get(i2) != null && Arrays.equals(MessageNano.toByteArray(this.mLastRilDataCallEvents.get(i).get(i2)), MessageNano.toByteArray(telephonyProto$RilDataCallArr[0]))) {
                return;
            }
            sparseArray = this.mLastRilDataCallEvents.get(i);
        } else {
            sparseArray = new SparseArray<>();
        }
        sparseArray.put(i2, telephonyProto$RilDataCallArr[0]);
        this.mLastRilDataCallEvents.put(i, sparseArray);
        addTelephonyEvent(new TelephonyEventBuilder(i).setDataCalls(telephonyProto$RilDataCallArr).build());
    }

    public void writeRilCallList(int i, ArrayList<GsmCdmaConnection> arrayList, String str) {
        logv("Logging CallList Changed Connections Size = " + arrayList.size());
        InProgressCallSession startNewCallSessionIfNeeded = startNewCallSessionIfNeeded(i);
        if (startNewCallSessionIfNeeded == null) {
            Rlog.e(TAG, "writeRilCallList: Call session is missing");
            return;
        }
        TelephonyProto$TelephonyCallSession.Event.RilCall[] convertConnectionsToRilCalls = convertConnectionsToRilCalls(arrayList, str);
        startNewCallSessionIfNeeded.addEvent(new CallSessionEventBuilder(10).setRilCalls(convertConnectionsToRilCalls));
        logv("Logged Call list changed");
        if (startNewCallSessionIfNeeded.isPhoneIdle() && disconnectReasonsKnown(convertConnectionsToRilCalls)) {
            finishCallSession(startNewCallSessionIfNeeded);
        }
    }

    private boolean disconnectReasonsKnown(TelephonyProto$TelephonyCallSession.Event.RilCall[] rilCallArr) {
        for (TelephonyProto$TelephonyCallSession.Event.RilCall rilCall : rilCallArr) {
            if (rilCall.callEndReason == 0) {
                return false;
            }
        }
        return true;
    }

    private TelephonyProto$TelephonyCallSession.Event.RilCall[] convertConnectionsToRilCalls(ArrayList<GsmCdmaConnection> arrayList, String str) {
        TelephonyProto$TelephonyCallSession.Event.RilCall[] rilCallArr = new TelephonyProto$TelephonyCallSession.Event.RilCall[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            TelephonyProto$TelephonyCallSession.Event.RilCall rilCall = new TelephonyProto$TelephonyCallSession.Event.RilCall();
            rilCallArr[i] = rilCall;
            rilCall.index = i;
            convertConnectionToRilCall(arrayList.get(i), rilCallArr[i], str);
        }
        return rilCallArr;
    }

    private TelephonyProto$EmergencyNumberInfo convertEmergencyNumberToEmergencyNumberInfo(EmergencyNumber emergencyNumber) {
        TelephonyProto$EmergencyNumberInfo telephonyProto$EmergencyNumberInfo = new TelephonyProto$EmergencyNumberInfo();
        telephonyProto$EmergencyNumberInfo.address = emergencyNumber.getNumber();
        telephonyProto$EmergencyNumberInfo.countryIso = emergencyNumber.getCountryIso();
        telephonyProto$EmergencyNumberInfo.mnc = emergencyNumber.getMnc();
        telephonyProto$EmergencyNumberInfo.serviceCategoriesBitmask = emergencyNumber.getEmergencyServiceCategoryBitmask();
        telephonyProto$EmergencyNumberInfo.urns = (String[]) emergencyNumber.getEmergencyUrns().stream().toArray(new IntFunction() { // from class: com.android.internal.telephony.metrics.TelephonyMetrics$$ExternalSyntheticLambda1
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                String[] lambda$convertEmergencyNumberToEmergencyNumberInfo$1;
                lambda$convertEmergencyNumberToEmergencyNumberInfo$1 = TelephonyMetrics.lambda$convertEmergencyNumberToEmergencyNumberInfo$1(i);
                return lambda$convertEmergencyNumberToEmergencyNumberInfo$1;
            }
        });
        telephonyProto$EmergencyNumberInfo.numberSourcesBitmask = emergencyNumber.getEmergencyNumberSourceBitmask();
        telephonyProto$EmergencyNumberInfo.routing = emergencyNumber.getEmergencyCallRouting();
        return telephonyProto$EmergencyNumberInfo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String[] lambda$convertEmergencyNumberToEmergencyNumberInfo$1(int i) {
        return new String[i];
    }

    private void convertConnectionToRilCall(GsmCdmaConnection gsmCdmaConnection, TelephonyProto$TelephonyCallSession.Event.RilCall rilCall, String str) {
        if (gsmCdmaConnection.isIncoming()) {
            rilCall.type = 2;
        } else {
            rilCall.type = 1;
        }
        switch (C02651.$SwitchMap$com$android$internal$telephony$Call$State[gsmCdmaConnection.getState().ordinal()]) {
            case 1:
                rilCall.state = 1;
                break;
            case 2:
                rilCall.state = 2;
                break;
            case 3:
                rilCall.state = 3;
                break;
            case 4:
                rilCall.state = 4;
                break;
            case 5:
                rilCall.state = 5;
                break;
            case 6:
                rilCall.state = 6;
                break;
            case 7:
                rilCall.state = 7;
                break;
            case 8:
                rilCall.state = 8;
                break;
            case 9:
                rilCall.state = 9;
                break;
            default:
                rilCall.state = 0;
                break;
        }
        rilCall.callEndReason = gsmCdmaConnection.getDisconnectCause();
        rilCall.isMultiparty = gsmCdmaConnection.isMultiparty();
        rilCall.preciseDisconnectCause = gsmCdmaConnection.getPreciseDisconnectCause();
        if (gsmCdmaConnection.getDisconnectCause() == 0 || !gsmCdmaConnection.isEmergencyCall() || gsmCdmaConnection.getEmergencyNumberInfo() == null || ThreadLocalRandom.current().nextDouble(0.0d, 100.0d) >= getSamplePercentageForEmergencyCall(str)) {
            return;
        }
        rilCall.isEmergencyCall = gsmCdmaConnection.isEmergencyCall();
        rilCall.emergencyNumberInfo = convertEmergencyNumberToEmergencyNumberInfo(gsmCdmaConnection.getEmergencyNumberInfo());
        EmergencyNumberTracker emergencyNumberTracker = gsmCdmaConnection.getEmergencyNumberTracker();
        rilCall.emergencyNumberDatabaseVersion = emergencyNumberTracker != null ? emergencyNumberTracker.getEmergencyNumberDbVersion() : -1;
    }

    public void writeRilDial(int i, GsmCdmaConnection gsmCdmaConnection, int i2, UUSInfo uUSInfo) {
        InProgressCallSession startNewCallSessionIfNeeded = startNewCallSessionIfNeeded(i);
        logv("Logging Dial Connection = " + gsmCdmaConnection);
        if (startNewCallSessionIfNeeded == null) {
            Rlog.e(TAG, "writeRilDial: Call session is missing");
            return;
        }
        TelephonyProto$TelephonyCallSession.Event.RilCall[] rilCallArr = {new TelephonyProto$TelephonyCallSession.Event.RilCall()};
        TelephonyProto$TelephonyCallSession.Event.RilCall rilCall = rilCallArr[0];
        rilCall.index = -1;
        convertConnectionToRilCall(gsmCdmaConnection, rilCall, PhoneConfigurationManager.SSSS);
        startNewCallSessionIfNeeded.addEvent(startNewCallSessionIfNeeded.startElapsedTimeMs, new CallSessionEventBuilder(6).setRilRequest(1).setRilCalls(rilCallArr));
        logv("Logged Dial event");
    }

    public void writeRilCallRing(int i, char[] cArr) {
        InProgressCallSession startNewCallSessionIfNeeded = startNewCallSessionIfNeeded(i);
        startNewCallSessionIfNeeded.addEvent(startNewCallSessionIfNeeded.startElapsedTimeMs, new CallSessionEventBuilder(8));
    }

    public void writeRilHangup(int i, GsmCdmaConnection gsmCdmaConnection, int i2, String str) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "writeRilHangup: Call session is missing");
            return;
        }
        TelephonyProto$TelephonyCallSession.Event.RilCall[] rilCallArr = {new TelephonyProto$TelephonyCallSession.Event.RilCall()};
        TelephonyProto$TelephonyCallSession.Event.RilCall rilCall = rilCallArr[0];
        rilCall.index = i2;
        convertConnectionToRilCall(gsmCdmaConnection, rilCall, str);
        inProgressCallSession.addEvent(new CallSessionEventBuilder(6).setRilRequest(3).setRilCalls(rilCallArr));
        logv("Logged Hangup event");
    }

    public void writeRilAnswer(int i, int i2) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "writeRilAnswer: Call session is missing");
        } else {
            inProgressCallSession.addEvent(new CallSessionEventBuilder(6).setRilRequest(2).setRilRequestId(i2));
        }
    }

    public void writeRilSrvcc(int i, int i2) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "writeRilSrvcc: Call session is missing");
        } else {
            inProgressCallSession.addEvent(new CallSessionEventBuilder(9).setSrvccState(i2 + 1));
        }
    }

    private int toCallSessionRilRequest(int i) {
        if (i != 10) {
            if (i != 36) {
                if (i != 40) {
                    if (i != 84) {
                        switch (i) {
                            case 12:
                            case 13:
                            case 14:
                                return 3;
                            case 15:
                                return 5;
                            case 16:
                                return 7;
                            default:
                                String str = TAG;
                                Rlog.e(str, "Unknown RIL request: " + i);
                                return 0;
                        }
                    }
                    return 6;
                }
                return 2;
            }
            return 4;
        }
        return 1;
    }

    private void writeOnSetupDataCallResponse(int i, int i2, int i3, int i4, DataCallResponse dataCallResponse) {
        TelephonyProto$TelephonyEvent.RilSetupDataCallResponse rilSetupDataCallResponse = new TelephonyProto$TelephonyEvent.RilSetupDataCallResponse();
        TelephonyProto$RilDataCall telephonyProto$RilDataCall = new TelephonyProto$RilDataCall();
        if (dataCallResponse != null) {
            rilSetupDataCallResponse.status = dataCallResponse.getCause() == 0 ? 1 : dataCallResponse.getCause();
            rilSetupDataCallResponse.suggestedRetryTimeMillis = dataCallResponse.getSuggestedRetryTime();
            telephonyProto$RilDataCall.cid = dataCallResponse.getId();
            telephonyProto$RilDataCall.type = dataCallResponse.getProtocolType() + 1;
            if (!TextUtils.isEmpty(dataCallResponse.getInterfaceName())) {
                telephonyProto$RilDataCall.ifname = dataCallResponse.getInterfaceName();
            }
        }
        rilSetupDataCallResponse.call = telephonyProto$RilDataCall;
        addTelephonyEvent(new TelephonyEventBuilder(i).setSetupDataCallResponse(rilSetupDataCallResponse).build());
    }

    private void writeOnCallSolicitedResponse(int i, int i2, int i3, int i4) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "writeOnCallSolicitedResponse: Call session is missing");
        } else {
            inProgressCallSession.addEvent(new CallSessionEventBuilder(7).setRilRequest(toCallSessionRilRequest(i4)).setRilRequestId(i2).setRilError(i3 + 1));
        }
    }

    private synchronized void writeOnSmsSolicitedResponse(int i, int i2, int i3, SmsResponse smsResponse) {
        int i4;
        long j;
        InProgressSmsSession inProgressSmsSession = this.mInProgressSmsSessions.get(i);
        if (inProgressSmsSession == null) {
            Rlog.e(TAG, "SMS session is missing");
        } else {
            if (smsResponse != null) {
                i4 = smsResponse.mErrorCode;
                j = smsResponse.mMessageId;
            } else {
                i4 = -1;
                j = 0;
            }
            inProgressSmsSession.addEvent(new SmsSessionEventBuilder(7).setErrorCode(i4).setRilErrno(i3 + 1).setRilRequestId(i2).setMessageId(j));
            inProgressSmsSession.decreaseExpectedResponse();
            finishSmsSessionIfNeeded(inProgressSmsSession);
        }
    }

    public synchronized void writeOnImsServiceSmsSolicitedResponse(int i, int i2, int i3, long j) {
        InProgressSmsSession inProgressSmsSession = this.mInProgressSmsSessions.get(i);
        if (inProgressSmsSession == null) {
            Rlog.e(TAG, "SMS session is missing");
        } else {
            inProgressSmsSession.addEvent(new SmsSessionEventBuilder(7).setImsServiceErrno(i2).setErrorCode(i3).setMessageId(j));
            inProgressSmsSession.decreaseExpectedResponse();
            finishSmsSessionIfNeeded(inProgressSmsSession);
        }
    }

    private void writeOnDeactivateDataCallResponse(int i, int i2) {
        addTelephonyEvent(new TelephonyEventBuilder(i).setDeactivateDataCallResponse(i2 + 1).build());
    }

    public void writeOnRilSolicitedResponse(int i, int i2, int i3, int i4, Object obj) {
        if (i4 != 10) {
            if (i4 != 87 && i4 != 113) {
                if (i4 != 40) {
                    if (i4 != 41) {
                        switch (i4) {
                            case 12:
                            case 13:
                            case 14:
                                break;
                            default:
                                switch (i4) {
                                    case 25:
                                    case 26:
                                        break;
                                    case 27:
                                        writeOnSetupDataCallResponse(i, i2, i3, i4, (DataCallResponse) obj);
                                        return;
                                    default:
                                        return;
                                }
                        }
                    } else {
                        writeOnDeactivateDataCallResponse(i, i3);
                        return;
                    }
                }
            }
            writeOnSmsSolicitedResponse(i, i2, i3, (SmsResponse) obj);
            return;
        }
        writeOnCallSolicitedResponse(i, i2, i3, i4);
    }

    public void writeNetworkValidate(int i) {
        addTelephonyEvent(new TelephonyEventBuilder().setNetworkValidate(i).build());
    }

    public void writeDataSwitch(int i, TelephonyProto$TelephonyEvent.DataSwitch dataSwitch) {
        addTelephonyEvent(new TelephonyEventBuilder(SubscriptionManager.getPhoneId(i)).setDataSwitch(dataSwitch).build());
    }

    public void writeOnDemandDataSwitch(TelephonyProto$TelephonyEvent.OnDemandDataSwitch onDemandDataSwitch) {
        addTelephonyEvent(new TelephonyEventBuilder().setOnDemandDataSwitch(onDemandDataSwitch).build());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.metrics.TelephonyMetrics$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C02651 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$Call$State;
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$PhoneConstants$State;

        static {
            int[] iArr = new int[PhoneConstants.State.values().length];
            $SwitchMap$com$android$internal$telephony$PhoneConstants$State = iArr;
            try {
                iArr[PhoneConstants.State.IDLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$PhoneConstants$State[PhoneConstants.State.RINGING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$PhoneConstants$State[PhoneConstants.State.OFFHOOK.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            int[] iArr2 = new int[Call.State.values().length];
            $SwitchMap$com$android$internal$telephony$Call$State = iArr2;
            try {
                iArr2[Call.State.IDLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.ACTIVE.ordinal()] = 2;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.HOLDING.ordinal()] = 3;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.DIALING.ordinal()] = 4;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.ALERTING.ordinal()] = 5;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.INCOMING.ordinal()] = 6;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.WAITING.ordinal()] = 7;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.DISCONNECTED.ordinal()] = 8;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.DISCONNECTING.ordinal()] = 9;
            } catch (NoSuchFieldError unused12) {
            }
        }
    }

    public void writePhoneState(int i, PhoneConstants.State state) {
        int i2;
        int i3 = C02651.$SwitchMap$com$android$internal$telephony$PhoneConstants$State[state.ordinal()];
        if (i3 != 1) {
            i2 = 2;
            if (i3 != 2) {
                i2 = 3;
                if (i3 != 3) {
                    i2 = 0;
                }
            }
        } else {
            i2 = 1;
        }
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "writePhoneState: Call session is missing");
            return;
        }
        inProgressCallSession.setLastKnownPhoneState(i2);
        if (i2 == 1 && !inProgressCallSession.containsCsCalls()) {
            finishCallSession(inProgressCallSession);
        }
        inProgressCallSession.addEvent(new CallSessionEventBuilder(20).setPhoneState(i2));
    }

    private int getCallId(ImsCallSession imsCallSession) {
        if (imsCallSession == null) {
            return -1;
        }
        try {
            return Integer.parseInt(imsCallSession.getCallId());
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    public void writeImsCallState(int i, ImsCallSession imsCallSession, Call.State state) {
        int i2;
        switch (C02651.$SwitchMap$com$android$internal$telephony$Call$State[state.ordinal()]) {
            case 1:
                i2 = 1;
                break;
            case 2:
                i2 = 2;
                break;
            case 3:
                i2 = 3;
                break;
            case 4:
                i2 = 4;
                break;
            case 5:
                i2 = 5;
                break;
            case 6:
                i2 = 6;
                break;
            case 7:
                i2 = 7;
                break;
            case 8:
                i2 = 8;
                break;
            case 9:
                i2 = 9;
                break;
            default:
                i2 = 0;
                break;
        }
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "Call session is missing");
        } else {
            inProgressCallSession.addEvent(new CallSessionEventBuilder(16).setCallIndex(getCallId(imsCallSession)).setCallState(i2));
        }
    }

    public void writeOnImsCallStart(int i, ImsCallSession imsCallSession) {
        startNewCallSessionIfNeeded(i).addEvent(new CallSessionEventBuilder(11).setCallIndex(getCallId(imsCallSession)).setImsCommand(1));
    }

    public void writeOnImsCallReceive(int i, ImsCallSession imsCallSession) {
        startNewCallSessionIfNeeded(i).addEvent(new CallSessionEventBuilder(15).setCallIndex(getCallId(imsCallSession)));
    }

    public void writeOnImsCommand(int i, ImsCallSession imsCallSession, int i2) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "Call session is missing");
        } else {
            inProgressCallSession.addEvent(new CallSessionEventBuilder(11).setCallIndex(getCallId(imsCallSession)).setImsCommand(i2));
        }
    }

    private TelephonyProto$ImsReasonInfo toImsReasonInfoProto(ImsReasonInfo imsReasonInfo) {
        TelephonyProto$ImsReasonInfo telephonyProto$ImsReasonInfo = new TelephonyProto$ImsReasonInfo();
        if (imsReasonInfo != null) {
            telephonyProto$ImsReasonInfo.reasonCode = imsReasonInfo.getCode();
            telephonyProto$ImsReasonInfo.extraCode = imsReasonInfo.getExtraCode();
            String extraMessage = imsReasonInfo.getExtraMessage();
            if (extraMessage != null) {
                telephonyProto$ImsReasonInfo.extraMessage = extraMessage;
            }
        }
        return telephonyProto$ImsReasonInfo;
    }

    public static TelephonyProto$TelephonyCallSession.Event.CallQuality toCallQualityProto(CallQuality callQuality) {
        TelephonyProto$TelephonyCallSession.Event.CallQuality callQuality2 = new TelephonyProto$TelephonyCallSession.Event.CallQuality();
        if (callQuality != null) {
            callQuality2.downlinkLevel = callQualityLevelToProtoEnum(callQuality.getDownlinkCallQualityLevel());
            callQuality2.uplinkLevel = callQualityLevelToProtoEnum(callQuality.getUplinkCallQualityLevel());
            callQuality2.durationInSeconds = callQuality.getCallDuration() / 1000;
            callQuality2.rtpPacketsTransmitted = callQuality.getNumRtpPacketsTransmitted();
            callQuality2.rtpPacketsReceived = callQuality.getNumRtpPacketsReceived();
            callQuality2.rtpPacketsTransmittedLost = callQuality.getNumRtpPacketsTransmittedLost();
            callQuality2.rtpPacketsNotReceived = callQuality.getNumRtpPacketsNotReceived();
            callQuality2.averageRelativeJitterMillis = callQuality.getAverageRelativeJitter();
            callQuality2.maxRelativeJitterMillis = callQuality.getMaxRelativeJitter();
            callQuality2.codecType = convertImsCodec(callQuality.getCodecType());
            callQuality2.rtpInactivityDetected = callQuality.isRtpInactivityDetected();
            callQuality2.rxSilenceDetected = callQuality.isIncomingSilenceDetectedAtCallSetup();
            callQuality2.txSilenceDetected = callQuality.isOutgoingSilenceDetectedAtCallSetup();
            callQuality2.voiceFrames = callQuality.getNumVoiceFrames();
            callQuality2.noDataFrames = callQuality.getNumNoDataFrames();
            callQuality2.rtpDroppedPackets = callQuality.getNumDroppedRtpPackets();
            callQuality2.minPlayoutDelayMillis = callQuality.getMinPlayoutDelayMillis();
            callQuality2.maxPlayoutDelayMillis = callQuality.getMaxPlayoutDelayMillis();
            callQuality2.rxRtpSidPackets = callQuality.getNumRtpSidPacketsReceived();
            callQuality2.rtpDuplicatePackets = callQuality.getNumRtpDuplicatePackets();
        }
        return callQuality2;
    }

    public void writeOnImsCallTerminated(int i, ImsCallSession imsCallSession, ImsReasonInfo imsReasonInfo, CallQualityMetrics callQualityMetrics, EmergencyNumber emergencyNumber, String str, int i2) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "Call session is missing");
            return;
        }
        CallSessionEventBuilder callSessionEventBuilder = new CallSessionEventBuilder(17);
        callSessionEventBuilder.setCallIndex(getCallId(imsCallSession));
        callSessionEventBuilder.setImsReasonInfo(toImsReasonInfoProto(imsReasonInfo));
        if (callQualityMetrics != null) {
            callSessionEventBuilder.setCallQualitySummaryDl(callQualityMetrics.getCallQualitySummaryDl()).setCallQualitySummaryUl(callQualityMetrics.getCallQualitySummaryUl());
        }
        if (emergencyNumber != null && ThreadLocalRandom.current().nextDouble(0.0d, 100.0d) < getSamplePercentageForEmergencyCall(str)) {
            callSessionEventBuilder.setIsImsEmergencyCall(true);
            callSessionEventBuilder.setImsEmergencyNumberInfo(convertEmergencyNumberToEmergencyNumberInfo(emergencyNumber));
            callSessionEventBuilder.setEmergencyNumberDatabaseVersion(i2);
        }
        inProgressCallSession.addEvent(callSessionEventBuilder);
    }

    public void writeOnImsCallHandoverEvent(int i, int i2, ImsCallSession imsCallSession, int i3, int i4, ImsReasonInfo imsReasonInfo) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "Call session is missing");
        } else {
            inProgressCallSession.addEvent(new CallSessionEventBuilder(i2).setCallIndex(getCallId(imsCallSession)).setSrcAccessTech(i3).setTargetAccessTech(i4).setImsReasonInfo(toImsReasonInfoProto(imsReasonInfo)));
        }
    }

    public synchronized void writeRilSendSms(int i, int i2, int i3, int i4, long j) {
        InProgressSmsSession startNewSmsSessionIfNeeded = startNewSmsSessionIfNeeded(i);
        startNewSmsSessionIfNeeded.addEvent(new SmsSessionEventBuilder(6).setTech(i3).setRilRequestId(i2).setFormat(i4).setMessageId(j));
        startNewSmsSessionIfNeeded.increaseExpectedResponse();
    }

    public synchronized void writeImsServiceSendSms(int i, String str, int i2, long j) {
        InProgressSmsSession startNewSmsSessionIfNeeded = startNewSmsSessionIfNeeded(i);
        startNewSmsSessionIfNeeded.addEvent(new SmsSessionEventBuilder(6).setTech(3).setImsServiceErrno(i2).setFormat(convertSmsFormat(str)).setMessageId(j));
        startNewSmsSessionIfNeeded.increaseExpectedResponse();
    }

    public synchronized void writeNewCBSms(int i, int i2, int i3, boolean z, boolean z2, int i4, int i5, long j) {
        InProgressSmsSession startNewSmsSessionIfNeeded = startNewSmsSessionIfNeeded(i);
        int i6 = z ? 2 : z2 ? 1 : 3;
        TelephonyProto$SmsSession.Event.CBMessage cBMessage = new TelephonyProto$SmsSession.Event.CBMessage();
        cBMessage.msgFormat = i2;
        cBMessage.msgPriority = i3 + 1;
        cBMessage.msgType = i6;
        cBMessage.serviceCategory = i4;
        cBMessage.serialNumber = i5;
        cBMessage.deliveredTimestampMillis = j;
        startNewSmsSessionIfNeeded.addEvent(new SmsSessionEventBuilder(9).setCellBroadcastMessage(cBMessage));
        finishSmsSessionIfNeeded(startNewSmsSessionIfNeeded);
    }

    public void writeDroppedIncomingMultipartSms(int i, String str, int i2, int i3) {
        logv("Logged dropped multipart SMS: received " + i2 + " out of " + i3);
        TelephonyProto$SmsSession.Event.IncompleteSms incompleteSms = new TelephonyProto$SmsSession.Event.IncompleteSms();
        incompleteSms.receivedParts = i2;
        incompleteSms.totalParts = i3;
        InProgressSmsSession startNewSmsSession = startNewSmsSession(i);
        startNewSmsSession.addEvent(new SmsSessionEventBuilder(10).setFormat(convertSmsFormat(str)).setIncompleteSms(incompleteSms));
        finishSmsSession(startNewSmsSession);
    }

    private void writeIncomingSmsWithType(int i, int i2, String str, boolean z) {
        InProgressSmsSession startNewSmsSession = startNewSmsSession(i);
        startNewSmsSession.addEvent(new SmsSessionEventBuilder(8).setFormat(convertSmsFormat(str)).setSmsType(i2).setErrorCode(!z ? 1 : 0));
        finishSmsSession(startNewSmsSession);
    }

    public void writeIncomingSMSPP(int i, String str, boolean z) {
        logv("Logged SMS-PP session. Result = " + z);
        writeIncomingSmsWithType(i, 1, str, z);
    }

    public void writeIncomingVoiceMailSms(int i, String str) {
        logv("Logged VoiceMail message.");
        writeIncomingSmsWithType(i, 2, str, true);
    }

    public void writeIncomingSmsTypeZero(int i, String str) {
        logv("Logged Type-0 SMS message.");
        writeIncomingSmsWithType(i, 3, str, true);
    }

    private void writeIncomingSmsSessionWithType(int i, int i2, int i3, String str, long[] jArr, boolean z, boolean z2, long j) {
        logv("Logged SMS session consisting of " + jArr.length + " parts, source = " + i3 + " blocked = " + z + " type = " + i2 + " " + SmsController.formatCrossStackMessageId(j));
        int convertSmsFormat = convertSmsFormat(str);
        int i4 = !z2 ? 1 : 0;
        int i5 = 0;
        int smsTech = getSmsTech(i3, convertSmsFormat == 2);
        InProgressSmsSession startNewSmsSession = startNewSmsSession(i);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        while (i5 < jArr.length) {
            startNewSmsSession.addEvent((i5 > 0 ? jArr[i5] - jArr[i5 - 1] : 0L) + elapsedRealtime, new SmsSessionEventBuilder(8).setFormat(convertSmsFormat).setTech(smsTech).setErrorCode(i4).setSmsType(i2).setBlocked(z).setMessageId(j));
            i5++;
        }
        finishSmsSession(startNewSmsSession);
    }

    public void writeIncomingWapPush(int i, int i2, String str, long[] jArr, boolean z, long j) {
        writeIncomingSmsSessionWithType(i, 4, i2, str, jArr, false, z, j);
    }

    public void writeIncomingSmsSession(int i, int i2, String str, long[] jArr, boolean z, long j) {
        writeIncomingSmsSessionWithType(i, 0, i2, str, jArr, z, true, j);
    }

    public void writeIncomingSmsError(int i, boolean z, int i2, int i3) {
        logv("Incoming SMS error = " + i3);
        if (i3 != 1) {
            int i4 = i3 != 3 ? i3 != 4 ? 1 : 24 : 13;
            InProgressSmsSession startNewSmsSession = startNewSmsSession(i);
            startNewSmsSession.addEvent(new SmsSessionEventBuilder(8).setFormat(z ? 2 : 1).setTech(getSmsTech(i2, z)).setErrorCode(i4));
            finishSmsSession(startNewSmsSession);
        }
    }

    public void writeNITZEvent(int i, long j) {
        TelephonyProto$TelephonyEvent build = new TelephonyEventBuilder(i).setNITZ(j).build();
        addTelephonyEvent(build);
        annotateInProgressCallSession(build.timestampMillis, i, new CallSessionEventBuilder(21).setNITZ(j));
    }

    public void writeModemRestartEvent(int i, String str) {
        TelephonyProto$TelephonyEvent.ModemRestart modemRestart = new TelephonyProto$TelephonyEvent.ModemRestart();
        String radioVersion = Build.getRadioVersion();
        if (radioVersion != null) {
            modemRestart.basebandVersion = radioVersion;
        }
        if (str != null) {
            modemRestart.reason = str;
        }
        addTelephonyEvent(new TelephonyEventBuilder(i).setModemRestart(modemRestart).build());
    }

    public void writeCarrierIdMatchingEvent(int i, int i2, int i3, String str, String str2, CarrierResolver.CarrierMatchingRule carrierMatchingRule) {
        TelephonyProto$TelephonyEvent.CarrierIdMatching carrierIdMatching = new TelephonyProto$TelephonyEvent.CarrierIdMatching();
        TelephonyProto$TelephonyEvent.CarrierIdMatchingResult carrierIdMatchingResult = new TelephonyProto$TelephonyEvent.CarrierIdMatchingResult();
        if (i3 != -1) {
            carrierIdMatchingResult.carrierId = i3;
            if (str2 != null) {
                carrierIdMatchingResult.unknownMccmnc = str;
                carrierIdMatchingResult.unknownGid1 = str2;
            }
        } else if (str != null) {
            carrierIdMatchingResult.unknownMccmnc = str;
        }
        carrierIdMatchingResult.mccmnc = TelephonyUtils.emptyIfNull(carrierMatchingRule.mccMnc);
        carrierIdMatchingResult.spn = TelephonyUtils.emptyIfNull(carrierMatchingRule.spn);
        carrierIdMatchingResult.pnn = TelephonyUtils.emptyIfNull(carrierMatchingRule.plmn);
        carrierIdMatchingResult.gid1 = TelephonyUtils.emptyIfNull(carrierMatchingRule.gid1);
        carrierIdMatchingResult.gid2 = TelephonyUtils.emptyIfNull(carrierMatchingRule.gid2);
        carrierIdMatchingResult.imsiPrefix = TelephonyUtils.emptyIfNull(carrierMatchingRule.imsiPrefixPattern);
        carrierIdMatchingResult.iccidPrefix = TelephonyUtils.emptyIfNull(carrierMatchingRule.iccidPrefix);
        carrierIdMatchingResult.preferApn = TelephonyUtils.emptyIfNull(carrierMatchingRule.apn);
        List<String> list = carrierMatchingRule.privilegeAccessRule;
        if (list != null) {
            carrierIdMatchingResult.privilegeAccessRule = (String[]) list.stream().toArray(new IntFunction() { // from class: com.android.internal.telephony.metrics.TelephonyMetrics$$ExternalSyntheticLambda2
                @Override // java.util.function.IntFunction
                public final Object apply(int i4) {
                    String[] lambda$writeCarrierIdMatchingEvent$2;
                    lambda$writeCarrierIdMatchingEvent$2 = TelephonyMetrics.lambda$writeCarrierIdMatchingEvent$2(i4);
                    return lambda$writeCarrierIdMatchingEvent$2;
                }
            });
        }
        carrierIdMatching.cidTableVersion = i2;
        carrierIdMatching.result = carrierIdMatchingResult;
        TelephonyProto$TelephonyEvent build = new TelephonyEventBuilder(i).setCarrierIdMatching(carrierIdMatching).build();
        this.mLastCarrierId.put(i, carrierIdMatching);
        addTelephonyEvent(build);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String[] lambda$writeCarrierIdMatchingEvent$2(int i) {
        return new String[i];
    }

    public void writeEmergencyNumberUpdateEvent(int i, EmergencyNumber emergencyNumber, int i2) {
        if (emergencyNumber == null) {
            return;
        }
        addTelephonyEvent(new TelephonyEventBuilder(i).setUpdatedEmergencyNumber(convertEmergencyNumberToEmergencyNumberInfo(emergencyNumber), i2).build());
    }

    public void writeNetworkCapabilitiesChangedEvent(int i, NetworkCapabilities networkCapabilities) {
        TelephonyProto$TelephonyEvent.NetworkCapabilitiesInfo networkCapabilitiesInfo = new TelephonyProto$TelephonyEvent.NetworkCapabilitiesInfo();
        networkCapabilitiesInfo.isNetworkUnmetered = networkCapabilities.hasCapability(25);
        TelephonyProto$TelephonyEvent build = new TelephonyEventBuilder(i).setNetworkCapabilities(networkCapabilitiesInfo).build();
        this.mLastNetworkCapabilitiesInfos.put(i, networkCapabilitiesInfo);
        addTelephonyEvent(build);
    }

    public void writeRadioState(int i, int i2) {
        int convertRadioState = convertRadioState(i2);
        TelephonyProto$TelephonyEvent build = new TelephonyEventBuilder(i).setRadioState(convertRadioState).build();
        this.mLastRadioState.put(i, Integer.valueOf(convertRadioState));
        addTelephonyEvent(build);
    }

    private int convertSmsFormat(String str) {
        str.hashCode();
        if (str.equals("3gpp")) {
            return 1;
        }
        return !str.equals("3gpp2") ? 0 : 2;
    }

    public void writeAudioCodecIms(int i, ImsCallSession imsCallSession) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "Call session is missing");
            return;
        }
        ImsCallProfile localCallProfile = imsCallSession.getLocalCallProfile();
        if (localCallProfile != null) {
            int convertImsCodec = convertImsCodec(localCallProfile.mMediaProfile.mAudioQuality);
            inProgressCallSession.addEvent(new CallSessionEventBuilder(22).setCallIndex(getCallId(imsCallSession)).setAudioCodec(convertImsCodec));
            logv("Logged Audio Codec event. Value: " + convertImsCodec);
        }
    }

    public void writeAudioCodecGsmCdma(int i, int i2) {
        InProgressCallSession inProgressCallSession = this.mInProgressCallSessions.get(i);
        if (inProgressCallSession == null) {
            Rlog.e(TAG, "Call session is missing");
            return;
        }
        int convertGsmCdmaCodec = convertGsmCdmaCodec(i2);
        inProgressCallSession.addEvent(new CallSessionEventBuilder(22).setAudioCodec(convertGsmCdmaCodec));
        logv("Logged Audio Codec event. Value: " + convertGsmCdmaCodec);
    }

    private double getSamplePercentageForEmergencyCall(String str) {
        if ("cn,in".contains(str)) {
            return 1.0d;
        }
        if ("us,id,br,pk,ng,bd,ru,mx,jp,et,ph,eg,vn,cd,tr,ir,de".contains(str)) {
            return 5.0d;
        }
        if ("th,gb,fr,tz,it,za,mm,ke,kr,co,es,ug,ar,ua,dz,sd,iq".contains(str)) {
            return 15.0d;
        }
        if ("pl,ca,af,ma,sa,pe,uz,ve,my,ao,mz,gh,np,ye,mg,kp,cm".contains(str)) {
            return 25.0d;
        }
        if ("au,tw,ne,lk,bf,mw,ml,ro,kz,sy,cl,zm,gt,zw,nl,ec,sn".contains(str)) {
            return 35.0d;
        }
        return "kh,td,so,gn,ss,rw,bj,tn,bi,be,cu,bo,ht,gr,do,cz,pt".contains(str) ? 45.0d : 50.0d;
    }

    public synchronized void writeBandwidthStats(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        BwEstimationStats lookupEstimationStats = lookupEstimationStats(i, i2, i3);
        long[] jArr = lookupEstimationStats.mBwEstErrorAcc;
        jArr[i4] = jArr[i4] + Math.abs(i5);
        long[] jArr2 = lookupEstimationStats.mStaticBwErrorAcc;
        jArr2[i4] = jArr2[i4] + Math.abs(i6);
        long[] jArr3 = lookupEstimationStats.mBwAccKbps;
        jArr3[i4] = jArr3[i4] + i7;
        int[] iArr = lookupEstimationStats.mCount;
        iArr[i4] = iArr[i4] + 1;
    }

    private BwEstimationStats lookupEstimationStats(int i, int i2, int i3) {
        String dataRatName = LinkBandwidthEstimator.getDataRatName(i2, i3);
        BwEstimationStats bwEstimationStats = this.mBwEstStatsMapList.get(i).get(dataRatName);
        if (bwEstimationStats == null) {
            BwEstimationStats bwEstimationStats2 = new BwEstimationStats(i2, i3);
            this.mBwEstStatsMapList.get(i).put(dataRatName, bwEstimationStats2);
            return bwEstimationStats2;
        }
        return bwEstimationStats;
    }

    private TelephonyProto$BandwidthEstimatorStats buildBandwidthEstimatorStats() {
        TelephonyProto$BandwidthEstimatorStats telephonyProto$BandwidthEstimatorStats = new TelephonyProto$BandwidthEstimatorStats();
        telephonyProto$BandwidthEstimatorStats.perRatTx = (TelephonyProto$BandwidthEstimatorStats.PerRat[]) writeBandwidthEstimatorStatsRatList(this.mBwEstStatsMapList.get(0)).toArray(new TelephonyProto$BandwidthEstimatorStats.PerRat[0]);
        telephonyProto$BandwidthEstimatorStats.perRatRx = (TelephonyProto$BandwidthEstimatorStats.PerRat[]) writeBandwidthEstimatorStatsRatList(this.mBwEstStatsMapList.get(1)).toArray(new TelephonyProto$BandwidthEstimatorStats.PerRat[0]);
        return telephonyProto$BandwidthEstimatorStats;
    }

    private List<TelephonyProto$BandwidthEstimatorStats.PerRat> writeBandwidthEstimatorStatsRatList(Map<String, BwEstimationStats> map) {
        ArrayList arrayList = new ArrayList();
        for (BwEstimationStats bwEstimationStats : map.values()) {
            arrayList.add(bwEstimationStats.writeBandwidthStats());
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BwEstimationStats {
        final int mNrMode;
        final int mRadioTechnology;
        final long[] mBwEstErrorAcc = new long[5];
        final long[] mStaticBwErrorAcc = new long[5];
        final long[] mBwAccKbps = new long[5];
        final int[] mCount = new int[5];

        BwEstimationStats(int i, int i2) {
            this.mRadioTechnology = i;
            this.mNrMode = i2;
        }

        public String toString() {
            return LinkBandwidthEstimator.getDataRatName(this.mRadioTechnology, this.mNrMode) + "\n Count\n" + printValues(this.mCount) + "\n AvgKbps\n" + printAvgValues(this.mBwAccKbps, this.mCount) + "\n BwEst Error\n" + printAvgValues(this.mBwEstErrorAcc, this.mCount) + "\n StaticBw Error\n" + printAvgValues(this.mStaticBwErrorAcc, this.mCount);
        }

        private String printValues(int[] iArr) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                sb.append(" " + iArr[i]);
            }
            return sb.toString();
        }

        private String printAvgValues(long[] jArr, int[] iArr) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                int calculateAvg = calculateAvg(jArr[i], iArr[i]);
                sb.append(" " + calculateAvg);
            }
            return sb.toString();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public TelephonyProto$BandwidthEstimatorStats.PerRat writeBandwidthStats() {
            TelephonyProto$BandwidthEstimatorStats.PerRat perRat = new TelephonyProto$BandwidthEstimatorStats.PerRat();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < 5; i++) {
                TelephonyProto$BandwidthEstimatorStats.PerLevel writeBandwidthStatsPerLevel = writeBandwidthStatsPerLevel(i);
                if (writeBandwidthStatsPerLevel != null) {
                    arrayList.add(writeBandwidthStatsPerLevel);
                }
            }
            perRat.rat = this.mRadioTechnology;
            perRat.perLevel = (TelephonyProto$BandwidthEstimatorStats.PerLevel[]) arrayList.toArray(new TelephonyProto$BandwidthEstimatorStats.PerLevel[0]);
            perRat.nrMode = this.mNrMode;
            return perRat;
        }

        private TelephonyProto$BandwidthEstimatorStats.PerLevel writeBandwidthStatsPerLevel(int i) {
            int i2 = this.mCount[i];
            if (i2 > 0) {
                TelephonyProto$BandwidthEstimatorStats.PerLevel perLevel = new TelephonyProto$BandwidthEstimatorStats.PerLevel();
                perLevel.signalLevel = i;
                perLevel.count = i2;
                perLevel.avgBwKbps = calculateAvg(this.mBwAccKbps[i], i2);
                perLevel.staticBwErrorPercent = calculateAvg(this.mStaticBwErrorAcc[i], i2);
                perLevel.bwEstErrorPercent = calculateAvg(this.mBwEstErrorAcc[i], i2);
                return perLevel;
            }
            return null;
        }

        private int calculateAvg(long j, int i) {
            if (i > 0) {
                return (int) (j / i);
            }
            return 0;
        }
    }
}
