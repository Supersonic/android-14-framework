package com.android.internal.telephony.metrics;

import com.android.internal.telephony.nano.TelephonyProto$ImsCapabilities;
import com.android.internal.telephony.nano.TelephonyProto$ImsConnectionState;
import com.android.internal.telephony.nano.TelephonyProto$RilDataCall;
import com.android.internal.telephony.nano.TelephonyProto$SmsSession;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyServiceState;
import com.android.internal.telephony.nano.TelephonyProto$TelephonySettings;
/* loaded from: classes.dex */
public class SmsSessionEventBuilder {
    TelephonyProto$SmsSession.Event mEvent;

    public TelephonyProto$SmsSession.Event build() {
        return this.mEvent;
    }

    public SmsSessionEventBuilder(int i) {
        TelephonyProto$SmsSession.Event event = new TelephonyProto$SmsSession.Event();
        this.mEvent = event;
        event.type = i;
    }

    public SmsSessionEventBuilder setDelay(int i) {
        this.mEvent.delay = i;
        return this;
    }

    public SmsSessionEventBuilder setTech(int i) {
        this.mEvent.tech = i;
        return this;
    }

    public SmsSessionEventBuilder setErrorCode(int i) {
        this.mEvent.errorCode = i;
        return this;
    }

    public SmsSessionEventBuilder setRilErrno(int i) {
        this.mEvent.error = i;
        return this;
    }

    public SmsSessionEventBuilder setImsServiceErrno(int i) {
        this.mEvent.imsError = i;
        return this;
    }

    public SmsSessionEventBuilder setSettings(TelephonyProto$TelephonySettings telephonyProto$TelephonySettings) {
        this.mEvent.settings = telephonyProto$TelephonySettings;
        return this;
    }

    public SmsSessionEventBuilder setServiceState(TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState) {
        this.mEvent.serviceState = telephonyProto$TelephonyServiceState;
        return this;
    }

    public SmsSessionEventBuilder setImsConnectionState(TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState) {
        this.mEvent.imsConnectionState = telephonyProto$ImsConnectionState;
        return this;
    }

    public SmsSessionEventBuilder setImsCapabilities(TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities) {
        this.mEvent.imsCapabilities = telephonyProto$ImsCapabilities;
        return this;
    }

    public SmsSessionEventBuilder setDataCalls(TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr) {
        this.mEvent.dataCalls = telephonyProto$RilDataCallArr;
        return this;
    }

    public SmsSessionEventBuilder setRilRequestId(int i) {
        this.mEvent.rilRequestId = i;
        return this;
    }

    public SmsSessionEventBuilder setFormat(int i) {
        this.mEvent.format = i;
        return this;
    }

    public SmsSessionEventBuilder setCellBroadcastMessage(TelephonyProto$SmsSession.Event.CBMessage cBMessage) {
        this.mEvent.cellBroadcastMessage = cBMessage;
        return this;
    }

    public SmsSessionEventBuilder setIncompleteSms(TelephonyProto$SmsSession.Event.IncompleteSms incompleteSms) {
        this.mEvent.incompleteSms = incompleteSms;
        return this;
    }

    public SmsSessionEventBuilder setBlocked(boolean z) {
        this.mEvent.blocked = z;
        return this;
    }

    public SmsSessionEventBuilder setSmsType(int i) {
        this.mEvent.smsType = i;
        return this;
    }

    public SmsSessionEventBuilder setMessageId(long j) {
        this.mEvent.messageId = j;
        return this;
    }
}
