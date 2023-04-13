package com.android.internal.telephony.metrics;

import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.SparseArray;
import com.android.internal.telephony.nano.TelephonyProto$ActiveSubscriptionInfo;
import com.android.internal.telephony.nano.TelephonyProto$EmergencyNumberInfo;
import com.android.internal.telephony.nano.TelephonyProto$ImsCapabilities;
import com.android.internal.telephony.nano.TelephonyProto$ImsConnectionState;
import com.android.internal.telephony.nano.TelephonyProto$RilDataCall;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyServiceState;
import com.android.internal.telephony.nano.TelephonyProto$TelephonySettings;
import java.util.Arrays;
/* loaded from: classes.dex */
public class TelephonyEventBuilder {
    private final TelephonyProto$TelephonyEvent mEvent;

    public TelephonyProto$TelephonyEvent build() {
        return this.mEvent;
    }

    public TelephonyEventBuilder() {
        this(-1);
    }

    public TelephonyEventBuilder(int i) {
        this(SystemClock.elapsedRealtime(), i);
    }

    public TelephonyEventBuilder(long j, int i) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = new TelephonyProto$TelephonyEvent();
        this.mEvent = telephonyProto$TelephonyEvent;
        telephonyProto$TelephonyEvent.timestampMillis = j;
        telephonyProto$TelephonyEvent.phoneId = i;
    }

    public TelephonyEventBuilder setSettings(TelephonyProto$TelephonySettings telephonyProto$TelephonySettings) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 1;
        telephonyProto$TelephonyEvent.settings = telephonyProto$TelephonySettings;
        return this;
    }

    public TelephonyEventBuilder setServiceState(TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 2;
        telephonyProto$TelephonyEvent.serviceState = telephonyProto$TelephonyServiceState;
        return this;
    }

    public TelephonyEventBuilder setImsConnectionState(TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 3;
        telephonyProto$TelephonyEvent.imsConnectionState = telephonyProto$ImsConnectionState;
        return this;
    }

    public TelephonyEventBuilder setImsCapabilities(TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 4;
        telephonyProto$TelephonyEvent.imsCapabilities = telephonyProto$ImsCapabilities;
        return this;
    }

    public TelephonyEventBuilder setDataStallRecoveryAction(int i) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 10;
        telephonyProto$TelephonyEvent.dataStallAction = i;
        return this;
    }

    public TelephonyEventBuilder setSignalStrength(int i) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 23;
        telephonyProto$TelephonyEvent.signalStrength = i;
        return this;
    }

    public TelephonyEventBuilder setSetupDataCall(TelephonyProto$TelephonyEvent.RilSetupDataCall rilSetupDataCall) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 5;
        telephonyProto$TelephonyEvent.setupDataCall = rilSetupDataCall;
        return this;
    }

    public TelephonyEventBuilder setSetupDataCallResponse(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse rilSetupDataCallResponse) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 6;
        telephonyProto$TelephonyEvent.setupDataCallResponse = rilSetupDataCallResponse;
        return this;
    }

    public TelephonyEventBuilder setDeactivateDataCall(TelephonyProto$TelephonyEvent.RilDeactivateDataCall rilDeactivateDataCall) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 8;
        telephonyProto$TelephonyEvent.deactivateDataCall = rilDeactivateDataCall;
        return this;
    }

    public TelephonyEventBuilder setDeactivateDataCallResponse(int i) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 9;
        telephonyProto$TelephonyEvent.error = i;
        return this;
    }

    public TelephonyEventBuilder setDataCalls(TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 7;
        telephonyProto$TelephonyEvent.dataCalls = telephonyProto$RilDataCallArr;
        return this;
    }

    public TelephonyEventBuilder setNITZ(long j) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 12;
        telephonyProto$TelephonyEvent.nitzTimestampMillis = j;
        return this;
    }

    public TelephonyEventBuilder setModemRestart(TelephonyProto$TelephonyEvent.ModemRestart modemRestart) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 11;
        telephonyProto$TelephonyEvent.modemRestart = modemRestart;
        return this;
    }

    public TelephonyEventBuilder setCarrierIdMatching(TelephonyProto$TelephonyEvent.CarrierIdMatching carrierIdMatching) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 13;
        telephonyProto$TelephonyEvent.carrierIdMatching = carrierIdMatching;
        return this;
    }

    public TelephonyEventBuilder setUpdatedEmergencyNumber(TelephonyProto$EmergencyNumberInfo telephonyProto$EmergencyNumberInfo, int i) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 21;
        telephonyProto$TelephonyEvent.updatedEmergencyNumber = telephonyProto$EmergencyNumberInfo;
        telephonyProto$TelephonyEvent.emergencyNumberDatabaseVersion = i;
        return this;
    }

    public TelephonyEventBuilder setCarrierKeyChange(TelephonyProto$TelephonyEvent.CarrierKeyChange carrierKeyChange) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 14;
        telephonyProto$TelephonyEvent.carrierKeyChange = carrierKeyChange;
        return this;
    }

    public TelephonyEventBuilder setSimStateChange(SparseArray<Integer> sparseArray) {
        int phoneCount = TelephonyManager.getDefault().getPhoneCount();
        int[] iArr = new int[phoneCount];
        this.mEvent.simState = iArr;
        Arrays.fill(iArr, 0);
        this.mEvent.type = 18;
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            if (keyAt >= 0 && keyAt < phoneCount) {
                this.mEvent.simState[keyAt] = sparseArray.get(keyAt).intValue();
            }
        }
        return this;
    }

    public TelephonyEventBuilder setActiveSubscriptionInfoChange(TelephonyProto$ActiveSubscriptionInfo telephonyProto$ActiveSubscriptionInfo) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 19;
        telephonyProto$TelephonyEvent.activeSubscriptionInfo = telephonyProto$ActiveSubscriptionInfo;
        return this;
    }

    public TelephonyEventBuilder setEnabledModemBitmap(int i) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 20;
        telephonyProto$TelephonyEvent.enabledModemBitmap = i;
        return this;
    }

    public TelephonyEventBuilder setDataSwitch(TelephonyProto$TelephonyEvent.DataSwitch dataSwitch) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 15;
        telephonyProto$TelephonyEvent.dataSwitch = dataSwitch;
        return this;
    }

    public TelephonyEventBuilder setNetworkValidate(int i) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 16;
        telephonyProto$TelephonyEvent.networkValidationState = i;
        return this;
    }

    public TelephonyEventBuilder setOnDemandDataSwitch(TelephonyProto$TelephonyEvent.OnDemandDataSwitch onDemandDataSwitch) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 17;
        telephonyProto$TelephonyEvent.onDemandDataSwitch = onDemandDataSwitch;
        return this;
    }

    public TelephonyEventBuilder setNetworkCapabilities(TelephonyProto$TelephonyEvent.NetworkCapabilitiesInfo networkCapabilitiesInfo) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 22;
        telephonyProto$TelephonyEvent.networkCapabilities = networkCapabilitiesInfo;
        return this;
    }

    public TelephonyEventBuilder setRadioState(int i) {
        TelephonyProto$TelephonyEvent telephonyProto$TelephonyEvent = this.mEvent;
        telephonyProto$TelephonyEvent.type = 24;
        telephonyProto$TelephonyEvent.radioState = i;
        return this;
    }
}
