package com.android.internal.telephony.metrics;

import com.android.internal.telephony.nano.TelephonyProto$EmergencyNumberInfo;
import com.android.internal.telephony.nano.TelephonyProto$ImsCapabilities;
import com.android.internal.telephony.nano.TelephonyProto$ImsConnectionState;
import com.android.internal.telephony.nano.TelephonyProto$ImsReasonInfo;
import com.android.internal.telephony.nano.TelephonyProto$RilDataCall;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyCallSession;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyServiceState;
import com.android.internal.telephony.nano.TelephonyProto$TelephonySettings;
/* loaded from: classes.dex */
public class CallSessionEventBuilder {
    private final TelephonyProto$TelephonyCallSession.Event mEvent;

    public TelephonyProto$TelephonyCallSession.Event build() {
        return this.mEvent;
    }

    public CallSessionEventBuilder(int i) {
        TelephonyProto$TelephonyCallSession.Event event = new TelephonyProto$TelephonyCallSession.Event();
        this.mEvent = event;
        event.type = i;
    }

    public CallSessionEventBuilder setDelay(int i) {
        this.mEvent.delay = i;
        return this;
    }

    public CallSessionEventBuilder setRilRequest(int i) {
        this.mEvent.rilRequest = i;
        return this;
    }

    public CallSessionEventBuilder setRilRequestId(int i) {
        this.mEvent.rilRequestId = i;
        return this;
    }

    public CallSessionEventBuilder setRilError(int i) {
        this.mEvent.error = i;
        return this;
    }

    public CallSessionEventBuilder setCallIndex(int i) {
        this.mEvent.callIndex = i;
        return this;
    }

    public CallSessionEventBuilder setCallState(int i) {
        this.mEvent.callState = i;
        return this;
    }

    public CallSessionEventBuilder setSrvccState(int i) {
        this.mEvent.srvccState = i;
        return this;
    }

    public CallSessionEventBuilder setImsCommand(int i) {
        this.mEvent.imsCommand = i;
        return this;
    }

    public CallSessionEventBuilder setImsReasonInfo(TelephonyProto$ImsReasonInfo telephonyProto$ImsReasonInfo) {
        this.mEvent.reasonInfo = telephonyProto$ImsReasonInfo;
        return this;
    }

    public CallSessionEventBuilder setSrcAccessTech(int i) {
        this.mEvent.srcAccessTech = i;
        return this;
    }

    public CallSessionEventBuilder setTargetAccessTech(int i) {
        this.mEvent.targetAccessTech = i;
        return this;
    }

    public CallSessionEventBuilder setSettings(TelephonyProto$TelephonySettings telephonyProto$TelephonySettings) {
        this.mEvent.settings = telephonyProto$TelephonySettings;
        return this;
    }

    public CallSessionEventBuilder setServiceState(TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState) {
        this.mEvent.serviceState = telephonyProto$TelephonyServiceState;
        return this;
    }

    public CallSessionEventBuilder setImsConnectionState(TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState) {
        this.mEvent.imsConnectionState = telephonyProto$ImsConnectionState;
        return this;
    }

    public CallSessionEventBuilder setImsCapabilities(TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities) {
        this.mEvent.imsCapabilities = telephonyProto$ImsCapabilities;
        return this;
    }

    public CallSessionEventBuilder setDataCalls(TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr) {
        this.mEvent.dataCalls = telephonyProto$RilDataCallArr;
        return this;
    }

    public CallSessionEventBuilder setPhoneState(int i) {
        this.mEvent.phoneState = i;
        return this;
    }

    public CallSessionEventBuilder setNITZ(long j) {
        this.mEvent.nitzTimestampMillis = j;
        return this;
    }

    public CallSessionEventBuilder setRilCalls(TelephonyProto$TelephonyCallSession.Event.RilCall[] rilCallArr) {
        this.mEvent.calls = rilCallArr;
        return this;
    }

    public CallSessionEventBuilder setAudioCodec(int i) {
        this.mEvent.audioCodec = i;
        return this;
    }

    public CallSessionEventBuilder setCallQuality(TelephonyProto$TelephonyCallSession.Event.CallQuality callQuality) {
        this.mEvent.callQuality = callQuality;
        return this;
    }

    public CallSessionEventBuilder setCallQualitySummaryDl(TelephonyProto$TelephonyCallSession.Event.CallQualitySummary callQualitySummary) {
        this.mEvent.callQualitySummaryDl = callQualitySummary;
        return this;
    }

    public CallSessionEventBuilder setCallQualitySummaryUl(TelephonyProto$TelephonyCallSession.Event.CallQualitySummary callQualitySummary) {
        this.mEvent.callQualitySummaryUl = callQualitySummary;
        return this;
    }

    public CallSessionEventBuilder setIsImsEmergencyCall(boolean z) {
        this.mEvent.isImsEmergencyCall = z;
        return this;
    }

    public CallSessionEventBuilder setEmergencyNumberDatabaseVersion(int i) {
        this.mEvent.emergencyNumberDatabaseVersion = i;
        return this;
    }

    public CallSessionEventBuilder setImsEmergencyNumberInfo(TelephonyProto$EmergencyNumberInfo telephonyProto$EmergencyNumberInfo) {
        this.mEvent.imsEmergencyNumberInfo = telephonyProto$EmergencyNumberInfo;
        return this;
    }
}
