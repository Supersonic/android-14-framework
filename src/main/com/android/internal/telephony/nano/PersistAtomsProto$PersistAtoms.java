package com.android.internal.telephony.nano;

import android.telephony.gsm.SmsMessage;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.imsphone.ImsRttTextHandler;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$PersistAtoms extends ExtendableMessageNano<PersistAtomsProto$PersistAtoms> {
    private static volatile PersistAtomsProto$PersistAtoms[] _emptyArray;
    public int autoDataSwitchToggleCount;
    public String buildFingerprint;
    public PersistAtomsProto$CarrierIdMismatch[] carrierIdMismatch;
    public int carrierIdTableVersion;
    public PersistAtomsProto$CellularDataServiceSwitch[] cellularDataServiceSwitch;
    public long cellularDataServiceSwitchPullTimestampMillis;
    public PersistAtomsProto$CellularServiceState[] cellularServiceState;
    public long cellularServiceStatePullTimestampMillis;
    public PersistAtomsProto$DataCallSession[] dataCallSession;
    public long dataCallSessionPullTimestampMillis;
    public PersistAtomsProto$GbaEvent[] gbaEvent;
    public long gbaEventPullTimestampMillis;
    public PersistAtomsProto$ImsDedicatedBearerEvent[] imsDedicatedBearerEvent;
    public long imsDedicatedBearerEventPullTimestampMillis;
    public PersistAtomsProto$ImsDedicatedBearerListenerEvent[] imsDedicatedBearerListenerEvent;
    public long imsDedicatedBearerListenerEventPullTimestampMillis;
    public PersistAtomsProto$ImsRegistrationFeatureTagStats[] imsRegistrationFeatureTagStats;
    public long imsRegistrationFeatureTagStatsPullTimestampMillis;
    public PersistAtomsProto$ImsRegistrationServiceDescStats[] imsRegistrationServiceDescStats;
    public long imsRegistrationServiceDescStatsPullTimestampMillis;
    public PersistAtomsProto$ImsRegistrationStats[] imsRegistrationStats;
    public long imsRegistrationStatsPullTimestampMillis;
    public PersistAtomsProto$ImsRegistrationTermination[] imsRegistrationTermination;
    public long imsRegistrationTerminationPullTimestampMillis;
    public PersistAtomsProto$IncomingSms[] incomingSms;
    public long incomingSmsPullTimestampMillis;
    public PersistAtomsProto$NetworkRequests[] networkRequests;
    public long networkRequestsPullTimestampMillis;
    public PersistAtomsProto$NetworkRequestsV2[] networkRequestsV2;
    public long networkRequestsV2PullTimestampMillis;
    public PersistAtomsProto$OutgoingShortCodeSms[] outgoingShortCodeSms;
    public long outgoingShortCodeSmsPullTimestampMillis;
    public PersistAtomsProto$OutgoingSms[] outgoingSms;
    public long outgoingSmsPullTimestampMillis;
    public PersistAtomsProto$PresenceNotifyEvent[] presenceNotifyEvent;
    public long presenceNotifyEventPullTimestampMillis;
    public PersistAtomsProto$RcsAcsProvisioningStats[] rcsAcsProvisioningStats;
    public long rcsAcsProvisioningStatsPullTimestampMillis;
    public PersistAtomsProto$RcsClientProvisioningStats[] rcsClientProvisioningStats;
    public long rcsClientProvisioningStatsPullTimestampMillis;
    public PersistAtomsProto$SipDelegateStats[] sipDelegateStats;
    public long sipDelegateStatsPullTimestampMillis;
    public PersistAtomsProto$SipMessageResponse[] sipMessageResponse;
    public long sipMessageResponsePullTimestampMillis;
    public PersistAtomsProto$SipTransportFeatureTagStats[] sipTransportFeatureTagStats;
    public long sipTransportFeatureTagStatsPullTimestampMillis;
    public PersistAtomsProto$SipTransportSession[] sipTransportSession;
    public long sipTransportSessionPullTimestampMillis;
    public PersistAtomsProto$UceEventStats[] uceEventStats;
    public long uceEventStatsPullTimestampMillis;
    public PersistAtomsProto$UnmeteredNetworks[] unmeteredNetworks;
    public PersistAtomsProto$VoiceCallRatUsage[] voiceCallRatUsage;
    public long voiceCallRatUsagePullTimestampMillis;
    public PersistAtomsProto$VoiceCallSession[] voiceCallSession;
    public long voiceCallSessionPullTimestampMillis;

    public static PersistAtomsProto$PersistAtoms[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$PersistAtoms[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$PersistAtoms() {
        clear();
    }

    public PersistAtomsProto$PersistAtoms clear() {
        this.voiceCallRatUsage = PersistAtomsProto$VoiceCallRatUsage.emptyArray();
        this.voiceCallRatUsagePullTimestampMillis = 0L;
        this.voiceCallSession = PersistAtomsProto$VoiceCallSession.emptyArray();
        this.voiceCallSessionPullTimestampMillis = 0L;
        this.incomingSms = PersistAtomsProto$IncomingSms.emptyArray();
        this.incomingSmsPullTimestampMillis = 0L;
        this.outgoingSms = PersistAtomsProto$OutgoingSms.emptyArray();
        this.outgoingSmsPullTimestampMillis = 0L;
        this.carrierIdMismatch = PersistAtomsProto$CarrierIdMismatch.emptyArray();
        this.carrierIdTableVersion = 0;
        this.dataCallSession = PersistAtomsProto$DataCallSession.emptyArray();
        this.dataCallSessionPullTimestampMillis = 0L;
        this.cellularServiceState = PersistAtomsProto$CellularServiceState.emptyArray();
        this.cellularServiceStatePullTimestampMillis = 0L;
        this.cellularDataServiceSwitch = PersistAtomsProto$CellularDataServiceSwitch.emptyArray();
        this.cellularDataServiceSwitchPullTimestampMillis = 0L;
        this.imsRegistrationTermination = PersistAtomsProto$ImsRegistrationTermination.emptyArray();
        this.imsRegistrationTerminationPullTimestampMillis = 0L;
        this.imsRegistrationStats = PersistAtomsProto$ImsRegistrationStats.emptyArray();
        this.imsRegistrationStatsPullTimestampMillis = 0L;
        this.buildFingerprint = PhoneConfigurationManager.SSSS;
        this.networkRequests = PersistAtomsProto$NetworkRequests.emptyArray();
        this.networkRequestsPullTimestampMillis = 0L;
        this.imsRegistrationFeatureTagStats = PersistAtomsProto$ImsRegistrationFeatureTagStats.emptyArray();
        this.imsRegistrationFeatureTagStatsPullTimestampMillis = 0L;
        this.rcsClientProvisioningStats = PersistAtomsProto$RcsClientProvisioningStats.emptyArray();
        this.rcsClientProvisioningStatsPullTimestampMillis = 0L;
        this.rcsAcsProvisioningStats = PersistAtomsProto$RcsAcsProvisioningStats.emptyArray();
        this.rcsAcsProvisioningStatsPullTimestampMillis = 0L;
        this.sipDelegateStats = PersistAtomsProto$SipDelegateStats.emptyArray();
        this.sipDelegateStatsPullTimestampMillis = 0L;
        this.sipTransportFeatureTagStats = PersistAtomsProto$SipTransportFeatureTagStats.emptyArray();
        this.sipTransportFeatureTagStatsPullTimestampMillis = 0L;
        this.sipMessageResponse = PersistAtomsProto$SipMessageResponse.emptyArray();
        this.sipMessageResponsePullTimestampMillis = 0L;
        this.sipTransportSession = PersistAtomsProto$SipTransportSession.emptyArray();
        this.sipTransportSessionPullTimestampMillis = 0L;
        this.imsDedicatedBearerListenerEvent = PersistAtomsProto$ImsDedicatedBearerListenerEvent.emptyArray();
        this.imsDedicatedBearerListenerEventPullTimestampMillis = 0L;
        this.imsDedicatedBearerEvent = PersistAtomsProto$ImsDedicatedBearerEvent.emptyArray();
        this.imsDedicatedBearerEventPullTimestampMillis = 0L;
        this.imsRegistrationServiceDescStats = PersistAtomsProto$ImsRegistrationServiceDescStats.emptyArray();
        this.imsRegistrationServiceDescStatsPullTimestampMillis = 0L;
        this.uceEventStats = PersistAtomsProto$UceEventStats.emptyArray();
        this.uceEventStatsPullTimestampMillis = 0L;
        this.presenceNotifyEvent = PersistAtomsProto$PresenceNotifyEvent.emptyArray();
        this.presenceNotifyEventPullTimestampMillis = 0L;
        this.gbaEvent = PersistAtomsProto$GbaEvent.emptyArray();
        this.gbaEventPullTimestampMillis = 0L;
        this.networkRequestsV2 = PersistAtomsProto$NetworkRequestsV2.emptyArray();
        this.networkRequestsV2PullTimestampMillis = 0L;
        this.unmeteredNetworks = PersistAtomsProto$UnmeteredNetworks.emptyArray();
        this.outgoingShortCodeSms = PersistAtomsProto$OutgoingShortCodeSms.emptyArray();
        this.outgoingShortCodeSmsPullTimestampMillis = 0L;
        this.autoDataSwitchToggleCount = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        PersistAtomsProto$VoiceCallRatUsage[] persistAtomsProto$VoiceCallRatUsageArr = this.voiceCallRatUsage;
        int i = 0;
        if (persistAtomsProto$VoiceCallRatUsageArr != null && persistAtomsProto$VoiceCallRatUsageArr.length > 0) {
            int i2 = 0;
            while (true) {
                PersistAtomsProto$VoiceCallRatUsage[] persistAtomsProto$VoiceCallRatUsageArr2 = this.voiceCallRatUsage;
                if (i2 >= persistAtomsProto$VoiceCallRatUsageArr2.length) {
                    break;
                }
                PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage = persistAtomsProto$VoiceCallRatUsageArr2[i2];
                if (persistAtomsProto$VoiceCallRatUsage != null) {
                    codedOutputByteBufferNano.writeMessage(1, persistAtomsProto$VoiceCallRatUsage);
                }
                i2++;
            }
        }
        long j = this.voiceCallRatUsagePullTimestampMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(2, j);
        }
        PersistAtomsProto$VoiceCallSession[] persistAtomsProto$VoiceCallSessionArr = this.voiceCallSession;
        if (persistAtomsProto$VoiceCallSessionArr != null && persistAtomsProto$VoiceCallSessionArr.length > 0) {
            int i3 = 0;
            while (true) {
                PersistAtomsProto$VoiceCallSession[] persistAtomsProto$VoiceCallSessionArr2 = this.voiceCallSession;
                if (i3 >= persistAtomsProto$VoiceCallSessionArr2.length) {
                    break;
                }
                PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = persistAtomsProto$VoiceCallSessionArr2[i3];
                if (persistAtomsProto$VoiceCallSession != null) {
                    codedOutputByteBufferNano.writeMessage(3, persistAtomsProto$VoiceCallSession);
                }
                i3++;
            }
        }
        long j2 = this.voiceCallSessionPullTimestampMillis;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeInt64(4, j2);
        }
        PersistAtomsProto$IncomingSms[] persistAtomsProto$IncomingSmsArr = this.incomingSms;
        if (persistAtomsProto$IncomingSmsArr != null && persistAtomsProto$IncomingSmsArr.length > 0) {
            int i4 = 0;
            while (true) {
                PersistAtomsProto$IncomingSms[] persistAtomsProto$IncomingSmsArr2 = this.incomingSms;
                if (i4 >= persistAtomsProto$IncomingSmsArr2.length) {
                    break;
                }
                PersistAtomsProto$IncomingSms persistAtomsProto$IncomingSms = persistAtomsProto$IncomingSmsArr2[i4];
                if (persistAtomsProto$IncomingSms != null) {
                    codedOutputByteBufferNano.writeMessage(5, persistAtomsProto$IncomingSms);
                }
                i4++;
            }
        }
        long j3 = this.incomingSmsPullTimestampMillis;
        if (j3 != 0) {
            codedOutputByteBufferNano.writeInt64(6, j3);
        }
        PersistAtomsProto$OutgoingSms[] persistAtomsProto$OutgoingSmsArr = this.outgoingSms;
        if (persistAtomsProto$OutgoingSmsArr != null && persistAtomsProto$OutgoingSmsArr.length > 0) {
            int i5 = 0;
            while (true) {
                PersistAtomsProto$OutgoingSms[] persistAtomsProto$OutgoingSmsArr2 = this.outgoingSms;
                if (i5 >= persistAtomsProto$OutgoingSmsArr2.length) {
                    break;
                }
                PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms = persistAtomsProto$OutgoingSmsArr2[i5];
                if (persistAtomsProto$OutgoingSms != null) {
                    codedOutputByteBufferNano.writeMessage(7, persistAtomsProto$OutgoingSms);
                }
                i5++;
            }
        }
        long j4 = this.outgoingSmsPullTimestampMillis;
        if (j4 != 0) {
            codedOutputByteBufferNano.writeInt64(8, j4);
        }
        PersistAtomsProto$CarrierIdMismatch[] persistAtomsProto$CarrierIdMismatchArr = this.carrierIdMismatch;
        if (persistAtomsProto$CarrierIdMismatchArr != null && persistAtomsProto$CarrierIdMismatchArr.length > 0) {
            int i6 = 0;
            while (true) {
                PersistAtomsProto$CarrierIdMismatch[] persistAtomsProto$CarrierIdMismatchArr2 = this.carrierIdMismatch;
                if (i6 >= persistAtomsProto$CarrierIdMismatchArr2.length) {
                    break;
                }
                PersistAtomsProto$CarrierIdMismatch persistAtomsProto$CarrierIdMismatch = persistAtomsProto$CarrierIdMismatchArr2[i6];
                if (persistAtomsProto$CarrierIdMismatch != null) {
                    codedOutputByteBufferNano.writeMessage(9, persistAtomsProto$CarrierIdMismatch);
                }
                i6++;
            }
        }
        int i7 = this.carrierIdTableVersion;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(10, i7);
        }
        PersistAtomsProto$DataCallSession[] persistAtomsProto$DataCallSessionArr = this.dataCallSession;
        if (persistAtomsProto$DataCallSessionArr != null && persistAtomsProto$DataCallSessionArr.length > 0) {
            int i8 = 0;
            while (true) {
                PersistAtomsProto$DataCallSession[] persistAtomsProto$DataCallSessionArr2 = this.dataCallSession;
                if (i8 >= persistAtomsProto$DataCallSessionArr2.length) {
                    break;
                }
                PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = persistAtomsProto$DataCallSessionArr2[i8];
                if (persistAtomsProto$DataCallSession != null) {
                    codedOutputByteBufferNano.writeMessage(11, persistAtomsProto$DataCallSession);
                }
                i8++;
            }
        }
        long j5 = this.dataCallSessionPullTimestampMillis;
        if (j5 != 0) {
            codedOutputByteBufferNano.writeInt64(12, j5);
        }
        PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr = this.cellularServiceState;
        if (persistAtomsProto$CellularServiceStateArr != null && persistAtomsProto$CellularServiceStateArr.length > 0) {
            int i9 = 0;
            while (true) {
                PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr2 = this.cellularServiceState;
                if (i9 >= persistAtomsProto$CellularServiceStateArr2.length) {
                    break;
                }
                PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState = persistAtomsProto$CellularServiceStateArr2[i9];
                if (persistAtomsProto$CellularServiceState != null) {
                    codedOutputByteBufferNano.writeMessage(13, persistAtomsProto$CellularServiceState);
                }
                i9++;
            }
        }
        long j6 = this.cellularServiceStatePullTimestampMillis;
        if (j6 != 0) {
            codedOutputByteBufferNano.writeInt64(14, j6);
        }
        PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr = this.cellularDataServiceSwitch;
        if (persistAtomsProto$CellularDataServiceSwitchArr != null && persistAtomsProto$CellularDataServiceSwitchArr.length > 0) {
            int i10 = 0;
            while (true) {
                PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr2 = this.cellularDataServiceSwitch;
                if (i10 >= persistAtomsProto$CellularDataServiceSwitchArr2.length) {
                    break;
                }
                PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch = persistAtomsProto$CellularDataServiceSwitchArr2[i10];
                if (persistAtomsProto$CellularDataServiceSwitch != null) {
                    codedOutputByteBufferNano.writeMessage(15, persistAtomsProto$CellularDataServiceSwitch);
                }
                i10++;
            }
        }
        long j7 = this.cellularDataServiceSwitchPullTimestampMillis;
        if (j7 != 0) {
            codedOutputByteBufferNano.writeInt64(16, j7);
        }
        PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr = this.imsRegistrationTermination;
        if (persistAtomsProto$ImsRegistrationTerminationArr != null && persistAtomsProto$ImsRegistrationTerminationArr.length > 0) {
            int i11 = 0;
            while (true) {
                PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr2 = this.imsRegistrationTermination;
                if (i11 >= persistAtomsProto$ImsRegistrationTerminationArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination = persistAtomsProto$ImsRegistrationTerminationArr2[i11];
                if (persistAtomsProto$ImsRegistrationTermination != null) {
                    codedOutputByteBufferNano.writeMessage(17, persistAtomsProto$ImsRegistrationTermination);
                }
                i11++;
            }
        }
        long j8 = this.imsRegistrationTerminationPullTimestampMillis;
        if (j8 != 0) {
            codedOutputByteBufferNano.writeInt64(18, j8);
        }
        PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr = this.imsRegistrationStats;
        if (persistAtomsProto$ImsRegistrationStatsArr != null && persistAtomsProto$ImsRegistrationStatsArr.length > 0) {
            int i12 = 0;
            while (true) {
                PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr2 = this.imsRegistrationStats;
                if (i12 >= persistAtomsProto$ImsRegistrationStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats = persistAtomsProto$ImsRegistrationStatsArr2[i12];
                if (persistAtomsProto$ImsRegistrationStats != null) {
                    codedOutputByteBufferNano.writeMessage(19, persistAtomsProto$ImsRegistrationStats);
                }
                i12++;
            }
        }
        long j9 = this.imsRegistrationStatsPullTimestampMillis;
        if (j9 != 0) {
            codedOutputByteBufferNano.writeInt64(20, j9);
        }
        if (!this.buildFingerprint.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(21, this.buildFingerprint);
        }
        PersistAtomsProto$NetworkRequests[] persistAtomsProto$NetworkRequestsArr = this.networkRequests;
        if (persistAtomsProto$NetworkRequestsArr != null && persistAtomsProto$NetworkRequestsArr.length > 0) {
            int i13 = 0;
            while (true) {
                PersistAtomsProto$NetworkRequests[] persistAtomsProto$NetworkRequestsArr2 = this.networkRequests;
                if (i13 >= persistAtomsProto$NetworkRequestsArr2.length) {
                    break;
                }
                PersistAtomsProto$NetworkRequests persistAtomsProto$NetworkRequests = persistAtomsProto$NetworkRequestsArr2[i13];
                if (persistAtomsProto$NetworkRequests != null) {
                    codedOutputByteBufferNano.writeMessage(22, persistAtomsProto$NetworkRequests);
                }
                i13++;
            }
        }
        long j10 = this.networkRequestsPullTimestampMillis;
        if (j10 != 0) {
            codedOutputByteBufferNano.writeInt64(23, j10);
        }
        PersistAtomsProto$ImsRegistrationFeatureTagStats[] persistAtomsProto$ImsRegistrationFeatureTagStatsArr = this.imsRegistrationFeatureTagStats;
        if (persistAtomsProto$ImsRegistrationFeatureTagStatsArr != null && persistAtomsProto$ImsRegistrationFeatureTagStatsArr.length > 0) {
            int i14 = 0;
            while (true) {
                PersistAtomsProto$ImsRegistrationFeatureTagStats[] persistAtomsProto$ImsRegistrationFeatureTagStatsArr2 = this.imsRegistrationFeatureTagStats;
                if (i14 >= persistAtomsProto$ImsRegistrationFeatureTagStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats = persistAtomsProto$ImsRegistrationFeatureTagStatsArr2[i14];
                if (persistAtomsProto$ImsRegistrationFeatureTagStats != null) {
                    codedOutputByteBufferNano.writeMessage(24, persistAtomsProto$ImsRegistrationFeatureTagStats);
                }
                i14++;
            }
        }
        long j11 = this.imsRegistrationFeatureTagStatsPullTimestampMillis;
        if (j11 != 0) {
            codedOutputByteBufferNano.writeInt64(25, j11);
        }
        PersistAtomsProto$RcsClientProvisioningStats[] persistAtomsProto$RcsClientProvisioningStatsArr = this.rcsClientProvisioningStats;
        if (persistAtomsProto$RcsClientProvisioningStatsArr != null && persistAtomsProto$RcsClientProvisioningStatsArr.length > 0) {
            int i15 = 0;
            while (true) {
                PersistAtomsProto$RcsClientProvisioningStats[] persistAtomsProto$RcsClientProvisioningStatsArr2 = this.rcsClientProvisioningStats;
                if (i15 >= persistAtomsProto$RcsClientProvisioningStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats = persistAtomsProto$RcsClientProvisioningStatsArr2[i15];
                if (persistAtomsProto$RcsClientProvisioningStats != null) {
                    codedOutputByteBufferNano.writeMessage(26, persistAtomsProto$RcsClientProvisioningStats);
                }
                i15++;
            }
        }
        long j12 = this.rcsClientProvisioningStatsPullTimestampMillis;
        if (j12 != 0) {
            codedOutputByteBufferNano.writeInt64(27, j12);
        }
        PersistAtomsProto$RcsAcsProvisioningStats[] persistAtomsProto$RcsAcsProvisioningStatsArr = this.rcsAcsProvisioningStats;
        if (persistAtomsProto$RcsAcsProvisioningStatsArr != null && persistAtomsProto$RcsAcsProvisioningStatsArr.length > 0) {
            int i16 = 0;
            while (true) {
                PersistAtomsProto$RcsAcsProvisioningStats[] persistAtomsProto$RcsAcsProvisioningStatsArr2 = this.rcsAcsProvisioningStats;
                if (i16 >= persistAtomsProto$RcsAcsProvisioningStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats = persistAtomsProto$RcsAcsProvisioningStatsArr2[i16];
                if (persistAtomsProto$RcsAcsProvisioningStats != null) {
                    codedOutputByteBufferNano.writeMessage(28, persistAtomsProto$RcsAcsProvisioningStats);
                }
                i16++;
            }
        }
        long j13 = this.rcsAcsProvisioningStatsPullTimestampMillis;
        if (j13 != 0) {
            codedOutputByteBufferNano.writeInt64(29, j13);
        }
        PersistAtomsProto$SipDelegateStats[] persistAtomsProto$SipDelegateStatsArr = this.sipDelegateStats;
        if (persistAtomsProto$SipDelegateStatsArr != null && persistAtomsProto$SipDelegateStatsArr.length > 0) {
            int i17 = 0;
            while (true) {
                PersistAtomsProto$SipDelegateStats[] persistAtomsProto$SipDelegateStatsArr2 = this.sipDelegateStats;
                if (i17 >= persistAtomsProto$SipDelegateStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats = persistAtomsProto$SipDelegateStatsArr2[i17];
                if (persistAtomsProto$SipDelegateStats != null) {
                    codedOutputByteBufferNano.writeMessage(30, persistAtomsProto$SipDelegateStats);
                }
                i17++;
            }
        }
        long j14 = this.sipDelegateStatsPullTimestampMillis;
        if (j14 != 0) {
            codedOutputByteBufferNano.writeInt64(31, j14);
        }
        PersistAtomsProto$SipTransportFeatureTagStats[] persistAtomsProto$SipTransportFeatureTagStatsArr = this.sipTransportFeatureTagStats;
        if (persistAtomsProto$SipTransportFeatureTagStatsArr != null && persistAtomsProto$SipTransportFeatureTagStatsArr.length > 0) {
            int i18 = 0;
            while (true) {
                PersistAtomsProto$SipTransportFeatureTagStats[] persistAtomsProto$SipTransportFeatureTagStatsArr2 = this.sipTransportFeatureTagStats;
                if (i18 >= persistAtomsProto$SipTransportFeatureTagStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats = persistAtomsProto$SipTransportFeatureTagStatsArr2[i18];
                if (persistAtomsProto$SipTransportFeatureTagStats != null) {
                    codedOutputByteBufferNano.writeMessage(32, persistAtomsProto$SipTransportFeatureTagStats);
                }
                i18++;
            }
        }
        long j15 = this.sipTransportFeatureTagStatsPullTimestampMillis;
        if (j15 != 0) {
            codedOutputByteBufferNano.writeInt64(33, j15);
        }
        PersistAtomsProto$SipMessageResponse[] persistAtomsProto$SipMessageResponseArr = this.sipMessageResponse;
        if (persistAtomsProto$SipMessageResponseArr != null && persistAtomsProto$SipMessageResponseArr.length > 0) {
            int i19 = 0;
            while (true) {
                PersistAtomsProto$SipMessageResponse[] persistAtomsProto$SipMessageResponseArr2 = this.sipMessageResponse;
                if (i19 >= persistAtomsProto$SipMessageResponseArr2.length) {
                    break;
                }
                PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse = persistAtomsProto$SipMessageResponseArr2[i19];
                if (persistAtomsProto$SipMessageResponse != null) {
                    codedOutputByteBufferNano.writeMessage(34, persistAtomsProto$SipMessageResponse);
                }
                i19++;
            }
        }
        long j16 = this.sipMessageResponsePullTimestampMillis;
        if (j16 != 0) {
            codedOutputByteBufferNano.writeInt64(35, j16);
        }
        PersistAtomsProto$SipTransportSession[] persistAtomsProto$SipTransportSessionArr = this.sipTransportSession;
        if (persistAtomsProto$SipTransportSessionArr != null && persistAtomsProto$SipTransportSessionArr.length > 0) {
            int i20 = 0;
            while (true) {
                PersistAtomsProto$SipTransportSession[] persistAtomsProto$SipTransportSessionArr2 = this.sipTransportSession;
                if (i20 >= persistAtomsProto$SipTransportSessionArr2.length) {
                    break;
                }
                PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession = persistAtomsProto$SipTransportSessionArr2[i20];
                if (persistAtomsProto$SipTransportSession != null) {
                    codedOutputByteBufferNano.writeMessage(36, persistAtomsProto$SipTransportSession);
                }
                i20++;
            }
        }
        long j17 = this.sipTransportSessionPullTimestampMillis;
        if (j17 != 0) {
            codedOutputByteBufferNano.writeInt64(37, j17);
        }
        PersistAtomsProto$ImsDedicatedBearerListenerEvent[] persistAtomsProto$ImsDedicatedBearerListenerEventArr = this.imsDedicatedBearerListenerEvent;
        if (persistAtomsProto$ImsDedicatedBearerListenerEventArr != null && persistAtomsProto$ImsDedicatedBearerListenerEventArr.length > 0) {
            int i21 = 0;
            while (true) {
                PersistAtomsProto$ImsDedicatedBearerListenerEvent[] persistAtomsProto$ImsDedicatedBearerListenerEventArr2 = this.imsDedicatedBearerListenerEvent;
                if (i21 >= persistAtomsProto$ImsDedicatedBearerListenerEventArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent = persistAtomsProto$ImsDedicatedBearerListenerEventArr2[i21];
                if (persistAtomsProto$ImsDedicatedBearerListenerEvent != null) {
                    codedOutputByteBufferNano.writeMessage(38, persistAtomsProto$ImsDedicatedBearerListenerEvent);
                }
                i21++;
            }
        }
        long j18 = this.imsDedicatedBearerListenerEventPullTimestampMillis;
        if (j18 != 0) {
            codedOutputByteBufferNano.writeInt64(39, j18);
        }
        PersistAtomsProto$ImsDedicatedBearerEvent[] persistAtomsProto$ImsDedicatedBearerEventArr = this.imsDedicatedBearerEvent;
        if (persistAtomsProto$ImsDedicatedBearerEventArr != null && persistAtomsProto$ImsDedicatedBearerEventArr.length > 0) {
            int i22 = 0;
            while (true) {
                PersistAtomsProto$ImsDedicatedBearerEvent[] persistAtomsProto$ImsDedicatedBearerEventArr2 = this.imsDedicatedBearerEvent;
                if (i22 >= persistAtomsProto$ImsDedicatedBearerEventArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent = persistAtomsProto$ImsDedicatedBearerEventArr2[i22];
                if (persistAtomsProto$ImsDedicatedBearerEvent != null) {
                    codedOutputByteBufferNano.writeMessage(40, persistAtomsProto$ImsDedicatedBearerEvent);
                }
                i22++;
            }
        }
        long j19 = this.imsDedicatedBearerEventPullTimestampMillis;
        if (j19 != 0) {
            codedOutputByteBufferNano.writeInt64(41, j19);
        }
        PersistAtomsProto$ImsRegistrationServiceDescStats[] persistAtomsProto$ImsRegistrationServiceDescStatsArr = this.imsRegistrationServiceDescStats;
        if (persistAtomsProto$ImsRegistrationServiceDescStatsArr != null && persistAtomsProto$ImsRegistrationServiceDescStatsArr.length > 0) {
            int i23 = 0;
            while (true) {
                PersistAtomsProto$ImsRegistrationServiceDescStats[] persistAtomsProto$ImsRegistrationServiceDescStatsArr2 = this.imsRegistrationServiceDescStats;
                if (i23 >= persistAtomsProto$ImsRegistrationServiceDescStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats = persistAtomsProto$ImsRegistrationServiceDescStatsArr2[i23];
                if (persistAtomsProto$ImsRegistrationServiceDescStats != null) {
                    codedOutputByteBufferNano.writeMessage(42, persistAtomsProto$ImsRegistrationServiceDescStats);
                }
                i23++;
            }
        }
        long j20 = this.imsRegistrationServiceDescStatsPullTimestampMillis;
        if (j20 != 0) {
            codedOutputByteBufferNano.writeInt64(43, j20);
        }
        PersistAtomsProto$UceEventStats[] persistAtomsProto$UceEventStatsArr = this.uceEventStats;
        if (persistAtomsProto$UceEventStatsArr != null && persistAtomsProto$UceEventStatsArr.length > 0) {
            int i24 = 0;
            while (true) {
                PersistAtomsProto$UceEventStats[] persistAtomsProto$UceEventStatsArr2 = this.uceEventStats;
                if (i24 >= persistAtomsProto$UceEventStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats = persistAtomsProto$UceEventStatsArr2[i24];
                if (persistAtomsProto$UceEventStats != null) {
                    codedOutputByteBufferNano.writeMessage(44, persistAtomsProto$UceEventStats);
                }
                i24++;
            }
        }
        long j21 = this.uceEventStatsPullTimestampMillis;
        if (j21 != 0) {
            codedOutputByteBufferNano.writeInt64(45, j21);
        }
        PersistAtomsProto$PresenceNotifyEvent[] persistAtomsProto$PresenceNotifyEventArr = this.presenceNotifyEvent;
        if (persistAtomsProto$PresenceNotifyEventArr != null && persistAtomsProto$PresenceNotifyEventArr.length > 0) {
            int i25 = 0;
            while (true) {
                PersistAtomsProto$PresenceNotifyEvent[] persistAtomsProto$PresenceNotifyEventArr2 = this.presenceNotifyEvent;
                if (i25 >= persistAtomsProto$PresenceNotifyEventArr2.length) {
                    break;
                }
                PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent = persistAtomsProto$PresenceNotifyEventArr2[i25];
                if (persistAtomsProto$PresenceNotifyEvent != null) {
                    codedOutputByteBufferNano.writeMessage(46, persistAtomsProto$PresenceNotifyEvent);
                }
                i25++;
            }
        }
        long j22 = this.presenceNotifyEventPullTimestampMillis;
        if (j22 != 0) {
            codedOutputByteBufferNano.writeInt64(47, j22);
        }
        PersistAtomsProto$GbaEvent[] persistAtomsProto$GbaEventArr = this.gbaEvent;
        if (persistAtomsProto$GbaEventArr != null && persistAtomsProto$GbaEventArr.length > 0) {
            int i26 = 0;
            while (true) {
                PersistAtomsProto$GbaEvent[] persistAtomsProto$GbaEventArr2 = this.gbaEvent;
                if (i26 >= persistAtomsProto$GbaEventArr2.length) {
                    break;
                }
                PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent = persistAtomsProto$GbaEventArr2[i26];
                if (persistAtomsProto$GbaEvent != null) {
                    codedOutputByteBufferNano.writeMessage(48, persistAtomsProto$GbaEvent);
                }
                i26++;
            }
        }
        long j23 = this.gbaEventPullTimestampMillis;
        if (j23 != 0) {
            codedOutputByteBufferNano.writeInt64(49, j23);
        }
        PersistAtomsProto$NetworkRequestsV2[] persistAtomsProto$NetworkRequestsV2Arr = this.networkRequestsV2;
        if (persistAtomsProto$NetworkRequestsV2Arr != null && persistAtomsProto$NetworkRequestsV2Arr.length > 0) {
            int i27 = 0;
            while (true) {
                PersistAtomsProto$NetworkRequestsV2[] persistAtomsProto$NetworkRequestsV2Arr2 = this.networkRequestsV2;
                if (i27 >= persistAtomsProto$NetworkRequestsV2Arr2.length) {
                    break;
                }
                PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV2 = persistAtomsProto$NetworkRequestsV2Arr2[i27];
                if (persistAtomsProto$NetworkRequestsV2 != null) {
                    codedOutputByteBufferNano.writeMessage(50, persistAtomsProto$NetworkRequestsV2);
                }
                i27++;
            }
        }
        long j24 = this.networkRequestsV2PullTimestampMillis;
        if (j24 != 0) {
            codedOutputByteBufferNano.writeInt64(51, j24);
        }
        PersistAtomsProto$UnmeteredNetworks[] persistAtomsProto$UnmeteredNetworksArr = this.unmeteredNetworks;
        if (persistAtomsProto$UnmeteredNetworksArr != null && persistAtomsProto$UnmeteredNetworksArr.length > 0) {
            int i28 = 0;
            while (true) {
                PersistAtomsProto$UnmeteredNetworks[] persistAtomsProto$UnmeteredNetworksArr2 = this.unmeteredNetworks;
                if (i28 >= persistAtomsProto$UnmeteredNetworksArr2.length) {
                    break;
                }
                PersistAtomsProto$UnmeteredNetworks persistAtomsProto$UnmeteredNetworks = persistAtomsProto$UnmeteredNetworksArr2[i28];
                if (persistAtomsProto$UnmeteredNetworks != null) {
                    codedOutputByteBufferNano.writeMessage(52, persistAtomsProto$UnmeteredNetworks);
                }
                i28++;
            }
        }
        PersistAtomsProto$OutgoingShortCodeSms[] persistAtomsProto$OutgoingShortCodeSmsArr = this.outgoingShortCodeSms;
        if (persistAtomsProto$OutgoingShortCodeSmsArr != null && persistAtomsProto$OutgoingShortCodeSmsArr.length > 0) {
            while (true) {
                PersistAtomsProto$OutgoingShortCodeSms[] persistAtomsProto$OutgoingShortCodeSmsArr2 = this.outgoingShortCodeSms;
                if (i >= persistAtomsProto$OutgoingShortCodeSmsArr2.length) {
                    break;
                }
                PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms = persistAtomsProto$OutgoingShortCodeSmsArr2[i];
                if (persistAtomsProto$OutgoingShortCodeSms != null) {
                    codedOutputByteBufferNano.writeMessage(53, persistAtomsProto$OutgoingShortCodeSms);
                }
                i++;
            }
        }
        long j25 = this.outgoingShortCodeSmsPullTimestampMillis;
        if (j25 != 0) {
            codedOutputByteBufferNano.writeInt64(54, j25);
        }
        int i29 = this.autoDataSwitchToggleCount;
        if (i29 != 0) {
            codedOutputByteBufferNano.writeInt32(55, i29);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        PersistAtomsProto$VoiceCallRatUsage[] persistAtomsProto$VoiceCallRatUsageArr = this.voiceCallRatUsage;
        int i = 0;
        if (persistAtomsProto$VoiceCallRatUsageArr != null && persistAtomsProto$VoiceCallRatUsageArr.length > 0) {
            int i2 = 0;
            while (true) {
                PersistAtomsProto$VoiceCallRatUsage[] persistAtomsProto$VoiceCallRatUsageArr2 = this.voiceCallRatUsage;
                if (i2 >= persistAtomsProto$VoiceCallRatUsageArr2.length) {
                    break;
                }
                PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage = persistAtomsProto$VoiceCallRatUsageArr2[i2];
                if (persistAtomsProto$VoiceCallRatUsage != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, persistAtomsProto$VoiceCallRatUsage);
                }
                i2++;
            }
        }
        long j = this.voiceCallRatUsagePullTimestampMillis;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, j);
        }
        PersistAtomsProto$VoiceCallSession[] persistAtomsProto$VoiceCallSessionArr = this.voiceCallSession;
        if (persistAtomsProto$VoiceCallSessionArr != null && persistAtomsProto$VoiceCallSessionArr.length > 0) {
            int i3 = 0;
            while (true) {
                PersistAtomsProto$VoiceCallSession[] persistAtomsProto$VoiceCallSessionArr2 = this.voiceCallSession;
                if (i3 >= persistAtomsProto$VoiceCallSessionArr2.length) {
                    break;
                }
                PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = persistAtomsProto$VoiceCallSessionArr2[i3];
                if (persistAtomsProto$VoiceCallSession != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, persistAtomsProto$VoiceCallSession);
                }
                i3++;
            }
        }
        long j2 = this.voiceCallSessionPullTimestampMillis;
        if (j2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(4, j2);
        }
        PersistAtomsProto$IncomingSms[] persistAtomsProto$IncomingSmsArr = this.incomingSms;
        if (persistAtomsProto$IncomingSmsArr != null && persistAtomsProto$IncomingSmsArr.length > 0) {
            int i4 = 0;
            while (true) {
                PersistAtomsProto$IncomingSms[] persistAtomsProto$IncomingSmsArr2 = this.incomingSms;
                if (i4 >= persistAtomsProto$IncomingSmsArr2.length) {
                    break;
                }
                PersistAtomsProto$IncomingSms persistAtomsProto$IncomingSms = persistAtomsProto$IncomingSmsArr2[i4];
                if (persistAtomsProto$IncomingSms != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, persistAtomsProto$IncomingSms);
                }
                i4++;
            }
        }
        long j3 = this.incomingSmsPullTimestampMillis;
        if (j3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(6, j3);
        }
        PersistAtomsProto$OutgoingSms[] persistAtomsProto$OutgoingSmsArr = this.outgoingSms;
        if (persistAtomsProto$OutgoingSmsArr != null && persistAtomsProto$OutgoingSmsArr.length > 0) {
            int i5 = 0;
            while (true) {
                PersistAtomsProto$OutgoingSms[] persistAtomsProto$OutgoingSmsArr2 = this.outgoingSms;
                if (i5 >= persistAtomsProto$OutgoingSmsArr2.length) {
                    break;
                }
                PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms = persistAtomsProto$OutgoingSmsArr2[i5];
                if (persistAtomsProto$OutgoingSms != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, persistAtomsProto$OutgoingSms);
                }
                i5++;
            }
        }
        long j4 = this.outgoingSmsPullTimestampMillis;
        if (j4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(8, j4);
        }
        PersistAtomsProto$CarrierIdMismatch[] persistAtomsProto$CarrierIdMismatchArr = this.carrierIdMismatch;
        if (persistAtomsProto$CarrierIdMismatchArr != null && persistAtomsProto$CarrierIdMismatchArr.length > 0) {
            int i6 = 0;
            while (true) {
                PersistAtomsProto$CarrierIdMismatch[] persistAtomsProto$CarrierIdMismatchArr2 = this.carrierIdMismatch;
                if (i6 >= persistAtomsProto$CarrierIdMismatchArr2.length) {
                    break;
                }
                PersistAtomsProto$CarrierIdMismatch persistAtomsProto$CarrierIdMismatch = persistAtomsProto$CarrierIdMismatchArr2[i6];
                if (persistAtomsProto$CarrierIdMismatch != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(9, persistAtomsProto$CarrierIdMismatch);
                }
                i6++;
            }
        }
        int i7 = this.carrierIdTableVersion;
        if (i7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i7);
        }
        PersistAtomsProto$DataCallSession[] persistAtomsProto$DataCallSessionArr = this.dataCallSession;
        if (persistAtomsProto$DataCallSessionArr != null && persistAtomsProto$DataCallSessionArr.length > 0) {
            int i8 = 0;
            while (true) {
                PersistAtomsProto$DataCallSession[] persistAtomsProto$DataCallSessionArr2 = this.dataCallSession;
                if (i8 >= persistAtomsProto$DataCallSessionArr2.length) {
                    break;
                }
                PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = persistAtomsProto$DataCallSessionArr2[i8];
                if (persistAtomsProto$DataCallSession != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(11, persistAtomsProto$DataCallSession);
                }
                i8++;
            }
        }
        long j5 = this.dataCallSessionPullTimestampMillis;
        if (j5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(12, j5);
        }
        PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr = this.cellularServiceState;
        if (persistAtomsProto$CellularServiceStateArr != null && persistAtomsProto$CellularServiceStateArr.length > 0) {
            int i9 = 0;
            while (true) {
                PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr2 = this.cellularServiceState;
                if (i9 >= persistAtomsProto$CellularServiceStateArr2.length) {
                    break;
                }
                PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState = persistAtomsProto$CellularServiceStateArr2[i9];
                if (persistAtomsProto$CellularServiceState != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(13, persistAtomsProto$CellularServiceState);
                }
                i9++;
            }
        }
        long j6 = this.cellularServiceStatePullTimestampMillis;
        if (j6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(14, j6);
        }
        PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr = this.cellularDataServiceSwitch;
        if (persistAtomsProto$CellularDataServiceSwitchArr != null && persistAtomsProto$CellularDataServiceSwitchArr.length > 0) {
            int i10 = 0;
            while (true) {
                PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr2 = this.cellularDataServiceSwitch;
                if (i10 >= persistAtomsProto$CellularDataServiceSwitchArr2.length) {
                    break;
                }
                PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch = persistAtomsProto$CellularDataServiceSwitchArr2[i10];
                if (persistAtomsProto$CellularDataServiceSwitch != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(15, persistAtomsProto$CellularDataServiceSwitch);
                }
                i10++;
            }
        }
        long j7 = this.cellularDataServiceSwitchPullTimestampMillis;
        if (j7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(16, j7);
        }
        PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr = this.imsRegistrationTermination;
        if (persistAtomsProto$ImsRegistrationTerminationArr != null && persistAtomsProto$ImsRegistrationTerminationArr.length > 0) {
            int i11 = 0;
            while (true) {
                PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr2 = this.imsRegistrationTermination;
                if (i11 >= persistAtomsProto$ImsRegistrationTerminationArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination = persistAtomsProto$ImsRegistrationTerminationArr2[i11];
                if (persistAtomsProto$ImsRegistrationTermination != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(17, persistAtomsProto$ImsRegistrationTermination);
                }
                i11++;
            }
        }
        long j8 = this.imsRegistrationTerminationPullTimestampMillis;
        if (j8 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(18, j8);
        }
        PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr = this.imsRegistrationStats;
        if (persistAtomsProto$ImsRegistrationStatsArr != null && persistAtomsProto$ImsRegistrationStatsArr.length > 0) {
            int i12 = 0;
            while (true) {
                PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr2 = this.imsRegistrationStats;
                if (i12 >= persistAtomsProto$ImsRegistrationStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats = persistAtomsProto$ImsRegistrationStatsArr2[i12];
                if (persistAtomsProto$ImsRegistrationStats != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(19, persistAtomsProto$ImsRegistrationStats);
                }
                i12++;
            }
        }
        long j9 = this.imsRegistrationStatsPullTimestampMillis;
        if (j9 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(20, j9);
        }
        if (!this.buildFingerprint.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(21, this.buildFingerprint);
        }
        PersistAtomsProto$NetworkRequests[] persistAtomsProto$NetworkRequestsArr = this.networkRequests;
        if (persistAtomsProto$NetworkRequestsArr != null && persistAtomsProto$NetworkRequestsArr.length > 0) {
            int i13 = 0;
            while (true) {
                PersistAtomsProto$NetworkRequests[] persistAtomsProto$NetworkRequestsArr2 = this.networkRequests;
                if (i13 >= persistAtomsProto$NetworkRequestsArr2.length) {
                    break;
                }
                PersistAtomsProto$NetworkRequests persistAtomsProto$NetworkRequests = persistAtomsProto$NetworkRequestsArr2[i13];
                if (persistAtomsProto$NetworkRequests != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(22, persistAtomsProto$NetworkRequests);
                }
                i13++;
            }
        }
        long j10 = this.networkRequestsPullTimestampMillis;
        if (j10 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(23, j10);
        }
        PersistAtomsProto$ImsRegistrationFeatureTagStats[] persistAtomsProto$ImsRegistrationFeatureTagStatsArr = this.imsRegistrationFeatureTagStats;
        if (persistAtomsProto$ImsRegistrationFeatureTagStatsArr != null && persistAtomsProto$ImsRegistrationFeatureTagStatsArr.length > 0) {
            int i14 = 0;
            while (true) {
                PersistAtomsProto$ImsRegistrationFeatureTagStats[] persistAtomsProto$ImsRegistrationFeatureTagStatsArr2 = this.imsRegistrationFeatureTagStats;
                if (i14 >= persistAtomsProto$ImsRegistrationFeatureTagStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats = persistAtomsProto$ImsRegistrationFeatureTagStatsArr2[i14];
                if (persistAtomsProto$ImsRegistrationFeatureTagStats != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(24, persistAtomsProto$ImsRegistrationFeatureTagStats);
                }
                i14++;
            }
        }
        long j11 = this.imsRegistrationFeatureTagStatsPullTimestampMillis;
        if (j11 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(25, j11);
        }
        PersistAtomsProto$RcsClientProvisioningStats[] persistAtomsProto$RcsClientProvisioningStatsArr = this.rcsClientProvisioningStats;
        if (persistAtomsProto$RcsClientProvisioningStatsArr != null && persistAtomsProto$RcsClientProvisioningStatsArr.length > 0) {
            int i15 = 0;
            while (true) {
                PersistAtomsProto$RcsClientProvisioningStats[] persistAtomsProto$RcsClientProvisioningStatsArr2 = this.rcsClientProvisioningStats;
                if (i15 >= persistAtomsProto$RcsClientProvisioningStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats = persistAtomsProto$RcsClientProvisioningStatsArr2[i15];
                if (persistAtomsProto$RcsClientProvisioningStats != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(26, persistAtomsProto$RcsClientProvisioningStats);
                }
                i15++;
            }
        }
        long j12 = this.rcsClientProvisioningStatsPullTimestampMillis;
        if (j12 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(27, j12);
        }
        PersistAtomsProto$RcsAcsProvisioningStats[] persistAtomsProto$RcsAcsProvisioningStatsArr = this.rcsAcsProvisioningStats;
        if (persistAtomsProto$RcsAcsProvisioningStatsArr != null && persistAtomsProto$RcsAcsProvisioningStatsArr.length > 0) {
            int i16 = 0;
            while (true) {
                PersistAtomsProto$RcsAcsProvisioningStats[] persistAtomsProto$RcsAcsProvisioningStatsArr2 = this.rcsAcsProvisioningStats;
                if (i16 >= persistAtomsProto$RcsAcsProvisioningStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats = persistAtomsProto$RcsAcsProvisioningStatsArr2[i16];
                if (persistAtomsProto$RcsAcsProvisioningStats != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(28, persistAtomsProto$RcsAcsProvisioningStats);
                }
                i16++;
            }
        }
        long j13 = this.rcsAcsProvisioningStatsPullTimestampMillis;
        if (j13 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(29, j13);
        }
        PersistAtomsProto$SipDelegateStats[] persistAtomsProto$SipDelegateStatsArr = this.sipDelegateStats;
        if (persistAtomsProto$SipDelegateStatsArr != null && persistAtomsProto$SipDelegateStatsArr.length > 0) {
            int i17 = 0;
            while (true) {
                PersistAtomsProto$SipDelegateStats[] persistAtomsProto$SipDelegateStatsArr2 = this.sipDelegateStats;
                if (i17 >= persistAtomsProto$SipDelegateStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats = persistAtomsProto$SipDelegateStatsArr2[i17];
                if (persistAtomsProto$SipDelegateStats != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(30, persistAtomsProto$SipDelegateStats);
                }
                i17++;
            }
        }
        long j14 = this.sipDelegateStatsPullTimestampMillis;
        if (j14 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(31, j14);
        }
        PersistAtomsProto$SipTransportFeatureTagStats[] persistAtomsProto$SipTransportFeatureTagStatsArr = this.sipTransportFeatureTagStats;
        if (persistAtomsProto$SipTransportFeatureTagStatsArr != null && persistAtomsProto$SipTransportFeatureTagStatsArr.length > 0) {
            int i18 = 0;
            while (true) {
                PersistAtomsProto$SipTransportFeatureTagStats[] persistAtomsProto$SipTransportFeatureTagStatsArr2 = this.sipTransportFeatureTagStats;
                if (i18 >= persistAtomsProto$SipTransportFeatureTagStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats = persistAtomsProto$SipTransportFeatureTagStatsArr2[i18];
                if (persistAtomsProto$SipTransportFeatureTagStats != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(32, persistAtomsProto$SipTransportFeatureTagStats);
                }
                i18++;
            }
        }
        long j15 = this.sipTransportFeatureTagStatsPullTimestampMillis;
        if (j15 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(33, j15);
        }
        PersistAtomsProto$SipMessageResponse[] persistAtomsProto$SipMessageResponseArr = this.sipMessageResponse;
        if (persistAtomsProto$SipMessageResponseArr != null && persistAtomsProto$SipMessageResponseArr.length > 0) {
            int i19 = 0;
            while (true) {
                PersistAtomsProto$SipMessageResponse[] persistAtomsProto$SipMessageResponseArr2 = this.sipMessageResponse;
                if (i19 >= persistAtomsProto$SipMessageResponseArr2.length) {
                    break;
                }
                PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse = persistAtomsProto$SipMessageResponseArr2[i19];
                if (persistAtomsProto$SipMessageResponse != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(34, persistAtomsProto$SipMessageResponse);
                }
                i19++;
            }
        }
        long j16 = this.sipMessageResponsePullTimestampMillis;
        if (j16 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(35, j16);
        }
        PersistAtomsProto$SipTransportSession[] persistAtomsProto$SipTransportSessionArr = this.sipTransportSession;
        if (persistAtomsProto$SipTransportSessionArr != null && persistAtomsProto$SipTransportSessionArr.length > 0) {
            int i20 = 0;
            while (true) {
                PersistAtomsProto$SipTransportSession[] persistAtomsProto$SipTransportSessionArr2 = this.sipTransportSession;
                if (i20 >= persistAtomsProto$SipTransportSessionArr2.length) {
                    break;
                }
                PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession = persistAtomsProto$SipTransportSessionArr2[i20];
                if (persistAtomsProto$SipTransportSession != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(36, persistAtomsProto$SipTransportSession);
                }
                i20++;
            }
        }
        long j17 = this.sipTransportSessionPullTimestampMillis;
        if (j17 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(37, j17);
        }
        PersistAtomsProto$ImsDedicatedBearerListenerEvent[] persistAtomsProto$ImsDedicatedBearerListenerEventArr = this.imsDedicatedBearerListenerEvent;
        if (persistAtomsProto$ImsDedicatedBearerListenerEventArr != null && persistAtomsProto$ImsDedicatedBearerListenerEventArr.length > 0) {
            int i21 = 0;
            while (true) {
                PersistAtomsProto$ImsDedicatedBearerListenerEvent[] persistAtomsProto$ImsDedicatedBearerListenerEventArr2 = this.imsDedicatedBearerListenerEvent;
                if (i21 >= persistAtomsProto$ImsDedicatedBearerListenerEventArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent = persistAtomsProto$ImsDedicatedBearerListenerEventArr2[i21];
                if (persistAtomsProto$ImsDedicatedBearerListenerEvent != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(38, persistAtomsProto$ImsDedicatedBearerListenerEvent);
                }
                i21++;
            }
        }
        long j18 = this.imsDedicatedBearerListenerEventPullTimestampMillis;
        if (j18 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(39, j18);
        }
        PersistAtomsProto$ImsDedicatedBearerEvent[] persistAtomsProto$ImsDedicatedBearerEventArr = this.imsDedicatedBearerEvent;
        if (persistAtomsProto$ImsDedicatedBearerEventArr != null && persistAtomsProto$ImsDedicatedBearerEventArr.length > 0) {
            int i22 = 0;
            while (true) {
                PersistAtomsProto$ImsDedicatedBearerEvent[] persistAtomsProto$ImsDedicatedBearerEventArr2 = this.imsDedicatedBearerEvent;
                if (i22 >= persistAtomsProto$ImsDedicatedBearerEventArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent = persistAtomsProto$ImsDedicatedBearerEventArr2[i22];
                if (persistAtomsProto$ImsDedicatedBearerEvent != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(40, persistAtomsProto$ImsDedicatedBearerEvent);
                }
                i22++;
            }
        }
        long j19 = this.imsDedicatedBearerEventPullTimestampMillis;
        if (j19 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(41, j19);
        }
        PersistAtomsProto$ImsRegistrationServiceDescStats[] persistAtomsProto$ImsRegistrationServiceDescStatsArr = this.imsRegistrationServiceDescStats;
        if (persistAtomsProto$ImsRegistrationServiceDescStatsArr != null && persistAtomsProto$ImsRegistrationServiceDescStatsArr.length > 0) {
            int i23 = 0;
            while (true) {
                PersistAtomsProto$ImsRegistrationServiceDescStats[] persistAtomsProto$ImsRegistrationServiceDescStatsArr2 = this.imsRegistrationServiceDescStats;
                if (i23 >= persistAtomsProto$ImsRegistrationServiceDescStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats = persistAtomsProto$ImsRegistrationServiceDescStatsArr2[i23];
                if (persistAtomsProto$ImsRegistrationServiceDescStats != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(42, persistAtomsProto$ImsRegistrationServiceDescStats);
                }
                i23++;
            }
        }
        long j20 = this.imsRegistrationServiceDescStatsPullTimestampMillis;
        if (j20 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(43, j20);
        }
        PersistAtomsProto$UceEventStats[] persistAtomsProto$UceEventStatsArr = this.uceEventStats;
        if (persistAtomsProto$UceEventStatsArr != null && persistAtomsProto$UceEventStatsArr.length > 0) {
            int i24 = 0;
            while (true) {
                PersistAtomsProto$UceEventStats[] persistAtomsProto$UceEventStatsArr2 = this.uceEventStats;
                if (i24 >= persistAtomsProto$UceEventStatsArr2.length) {
                    break;
                }
                PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats = persistAtomsProto$UceEventStatsArr2[i24];
                if (persistAtomsProto$UceEventStats != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(44, persistAtomsProto$UceEventStats);
                }
                i24++;
            }
        }
        long j21 = this.uceEventStatsPullTimestampMillis;
        if (j21 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(45, j21);
        }
        PersistAtomsProto$PresenceNotifyEvent[] persistAtomsProto$PresenceNotifyEventArr = this.presenceNotifyEvent;
        if (persistAtomsProto$PresenceNotifyEventArr != null && persistAtomsProto$PresenceNotifyEventArr.length > 0) {
            int i25 = 0;
            while (true) {
                PersistAtomsProto$PresenceNotifyEvent[] persistAtomsProto$PresenceNotifyEventArr2 = this.presenceNotifyEvent;
                if (i25 >= persistAtomsProto$PresenceNotifyEventArr2.length) {
                    break;
                }
                PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent = persistAtomsProto$PresenceNotifyEventArr2[i25];
                if (persistAtomsProto$PresenceNotifyEvent != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(46, persistAtomsProto$PresenceNotifyEvent);
                }
                i25++;
            }
        }
        long j22 = this.presenceNotifyEventPullTimestampMillis;
        if (j22 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(47, j22);
        }
        PersistAtomsProto$GbaEvent[] persistAtomsProto$GbaEventArr = this.gbaEvent;
        if (persistAtomsProto$GbaEventArr != null && persistAtomsProto$GbaEventArr.length > 0) {
            int i26 = 0;
            while (true) {
                PersistAtomsProto$GbaEvent[] persistAtomsProto$GbaEventArr2 = this.gbaEvent;
                if (i26 >= persistAtomsProto$GbaEventArr2.length) {
                    break;
                }
                PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent = persistAtomsProto$GbaEventArr2[i26];
                if (persistAtomsProto$GbaEvent != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(48, persistAtomsProto$GbaEvent);
                }
                i26++;
            }
        }
        long j23 = this.gbaEventPullTimestampMillis;
        if (j23 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(49, j23);
        }
        PersistAtomsProto$NetworkRequestsV2[] persistAtomsProto$NetworkRequestsV2Arr = this.networkRequestsV2;
        if (persistAtomsProto$NetworkRequestsV2Arr != null && persistAtomsProto$NetworkRequestsV2Arr.length > 0) {
            int i27 = 0;
            while (true) {
                PersistAtomsProto$NetworkRequestsV2[] persistAtomsProto$NetworkRequestsV2Arr2 = this.networkRequestsV2;
                if (i27 >= persistAtomsProto$NetworkRequestsV2Arr2.length) {
                    break;
                }
                PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV2 = persistAtomsProto$NetworkRequestsV2Arr2[i27];
                if (persistAtomsProto$NetworkRequestsV2 != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(50, persistAtomsProto$NetworkRequestsV2);
                }
                i27++;
            }
        }
        long j24 = this.networkRequestsV2PullTimestampMillis;
        if (j24 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(51, j24);
        }
        PersistAtomsProto$UnmeteredNetworks[] persistAtomsProto$UnmeteredNetworksArr = this.unmeteredNetworks;
        if (persistAtomsProto$UnmeteredNetworksArr != null && persistAtomsProto$UnmeteredNetworksArr.length > 0) {
            int i28 = 0;
            while (true) {
                PersistAtomsProto$UnmeteredNetworks[] persistAtomsProto$UnmeteredNetworksArr2 = this.unmeteredNetworks;
                if (i28 >= persistAtomsProto$UnmeteredNetworksArr2.length) {
                    break;
                }
                PersistAtomsProto$UnmeteredNetworks persistAtomsProto$UnmeteredNetworks = persistAtomsProto$UnmeteredNetworksArr2[i28];
                if (persistAtomsProto$UnmeteredNetworks != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(52, persistAtomsProto$UnmeteredNetworks);
                }
                i28++;
            }
        }
        PersistAtomsProto$OutgoingShortCodeSms[] persistAtomsProto$OutgoingShortCodeSmsArr = this.outgoingShortCodeSms;
        if (persistAtomsProto$OutgoingShortCodeSmsArr != null && persistAtomsProto$OutgoingShortCodeSmsArr.length > 0) {
            while (true) {
                PersistAtomsProto$OutgoingShortCodeSms[] persistAtomsProto$OutgoingShortCodeSmsArr2 = this.outgoingShortCodeSms;
                if (i >= persistAtomsProto$OutgoingShortCodeSmsArr2.length) {
                    break;
                }
                PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms = persistAtomsProto$OutgoingShortCodeSmsArr2[i];
                if (persistAtomsProto$OutgoingShortCodeSms != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(53, persistAtomsProto$OutgoingShortCodeSms);
                }
                i++;
            }
        }
        long j25 = this.outgoingShortCodeSmsPullTimestampMillis;
        if (j25 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(54, j25);
        }
        int i29 = this.autoDataSwitchToggleCount;
        return i29 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(55, i29) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$PersistAtoms mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 10:
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                    PersistAtomsProto$VoiceCallRatUsage[] persistAtomsProto$VoiceCallRatUsageArr = this.voiceCallRatUsage;
                    int length = persistAtomsProto$VoiceCallRatUsageArr == null ? 0 : persistAtomsProto$VoiceCallRatUsageArr.length;
                    int i = repeatedFieldArrayLength + length;
                    PersistAtomsProto$VoiceCallRatUsage[] persistAtomsProto$VoiceCallRatUsageArr2 = new PersistAtomsProto$VoiceCallRatUsage[i];
                    if (length != 0) {
                        System.arraycopy(persistAtomsProto$VoiceCallRatUsageArr, 0, persistAtomsProto$VoiceCallRatUsageArr2, 0, length);
                    }
                    while (length < i - 1) {
                        PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage = new PersistAtomsProto$VoiceCallRatUsage();
                        persistAtomsProto$VoiceCallRatUsageArr2[length] = persistAtomsProto$VoiceCallRatUsage;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$VoiceCallRatUsage);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage2 = new PersistAtomsProto$VoiceCallRatUsage();
                    persistAtomsProto$VoiceCallRatUsageArr2[length] = persistAtomsProto$VoiceCallRatUsage2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$VoiceCallRatUsage2);
                    this.voiceCallRatUsage = persistAtomsProto$VoiceCallRatUsageArr2;
                    break;
                case 16:
                    this.voiceCallRatUsagePullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 26:
                    int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                    PersistAtomsProto$VoiceCallSession[] persistAtomsProto$VoiceCallSessionArr = this.voiceCallSession;
                    int length2 = persistAtomsProto$VoiceCallSessionArr == null ? 0 : persistAtomsProto$VoiceCallSessionArr.length;
                    int i2 = repeatedFieldArrayLength2 + length2;
                    PersistAtomsProto$VoiceCallSession[] persistAtomsProto$VoiceCallSessionArr2 = new PersistAtomsProto$VoiceCallSession[i2];
                    if (length2 != 0) {
                        System.arraycopy(persistAtomsProto$VoiceCallSessionArr, 0, persistAtomsProto$VoiceCallSessionArr2, 0, length2);
                    }
                    while (length2 < i2 - 1) {
                        PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = new PersistAtomsProto$VoiceCallSession();
                        persistAtomsProto$VoiceCallSessionArr2[length2] = persistAtomsProto$VoiceCallSession;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$VoiceCallSession);
                        codedInputByteBufferNano.readTag();
                        length2++;
                    }
                    PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession2 = new PersistAtomsProto$VoiceCallSession();
                    persistAtomsProto$VoiceCallSessionArr2[length2] = persistAtomsProto$VoiceCallSession2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$VoiceCallSession2);
                    this.voiceCallSession = persistAtomsProto$VoiceCallSessionArr2;
                    break;
                case 32:
                    this.voiceCallSessionPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 42:
                    int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 42);
                    PersistAtomsProto$IncomingSms[] persistAtomsProto$IncomingSmsArr = this.incomingSms;
                    int length3 = persistAtomsProto$IncomingSmsArr == null ? 0 : persistAtomsProto$IncomingSmsArr.length;
                    int i3 = repeatedFieldArrayLength3 + length3;
                    PersistAtomsProto$IncomingSms[] persistAtomsProto$IncomingSmsArr2 = new PersistAtomsProto$IncomingSms[i3];
                    if (length3 != 0) {
                        System.arraycopy(persistAtomsProto$IncomingSmsArr, 0, persistAtomsProto$IncomingSmsArr2, 0, length3);
                    }
                    while (length3 < i3 - 1) {
                        PersistAtomsProto$IncomingSms persistAtomsProto$IncomingSms = new PersistAtomsProto$IncomingSms();
                        persistAtomsProto$IncomingSmsArr2[length3] = persistAtomsProto$IncomingSms;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$IncomingSms);
                        codedInputByteBufferNano.readTag();
                        length3++;
                    }
                    PersistAtomsProto$IncomingSms persistAtomsProto$IncomingSms2 = new PersistAtomsProto$IncomingSms();
                    persistAtomsProto$IncomingSmsArr2[length3] = persistAtomsProto$IncomingSms2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$IncomingSms2);
                    this.incomingSms = persistAtomsProto$IncomingSmsArr2;
                    break;
                case 48:
                    this.incomingSmsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 58:
                    int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 58);
                    PersistAtomsProto$OutgoingSms[] persistAtomsProto$OutgoingSmsArr = this.outgoingSms;
                    int length4 = persistAtomsProto$OutgoingSmsArr == null ? 0 : persistAtomsProto$OutgoingSmsArr.length;
                    int i4 = repeatedFieldArrayLength4 + length4;
                    PersistAtomsProto$OutgoingSms[] persistAtomsProto$OutgoingSmsArr2 = new PersistAtomsProto$OutgoingSms[i4];
                    if (length4 != 0) {
                        System.arraycopy(persistAtomsProto$OutgoingSmsArr, 0, persistAtomsProto$OutgoingSmsArr2, 0, length4);
                    }
                    while (length4 < i4 - 1) {
                        PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms = new PersistAtomsProto$OutgoingSms();
                        persistAtomsProto$OutgoingSmsArr2[length4] = persistAtomsProto$OutgoingSms;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$OutgoingSms);
                        codedInputByteBufferNano.readTag();
                        length4++;
                    }
                    PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms2 = new PersistAtomsProto$OutgoingSms();
                    persistAtomsProto$OutgoingSmsArr2[length4] = persistAtomsProto$OutgoingSms2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$OutgoingSms2);
                    this.outgoingSms = persistAtomsProto$OutgoingSmsArr2;
                    break;
                case 64:
                    this.outgoingSmsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case RadioNVItems.RIL_NV_LTE_SCAN_PRIORITY_25 /* 74 */:
                    int repeatedFieldArrayLength5 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 74);
                    PersistAtomsProto$CarrierIdMismatch[] persistAtomsProto$CarrierIdMismatchArr = this.carrierIdMismatch;
                    int length5 = persistAtomsProto$CarrierIdMismatchArr == null ? 0 : persistAtomsProto$CarrierIdMismatchArr.length;
                    int i5 = repeatedFieldArrayLength5 + length5;
                    PersistAtomsProto$CarrierIdMismatch[] persistAtomsProto$CarrierIdMismatchArr2 = new PersistAtomsProto$CarrierIdMismatch[i5];
                    if (length5 != 0) {
                        System.arraycopy(persistAtomsProto$CarrierIdMismatchArr, 0, persistAtomsProto$CarrierIdMismatchArr2, 0, length5);
                    }
                    while (length5 < i5 - 1) {
                        PersistAtomsProto$CarrierIdMismatch persistAtomsProto$CarrierIdMismatch = new PersistAtomsProto$CarrierIdMismatch();
                        persistAtomsProto$CarrierIdMismatchArr2[length5] = persistAtomsProto$CarrierIdMismatch;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$CarrierIdMismatch);
                        codedInputByteBufferNano.readTag();
                        length5++;
                    }
                    PersistAtomsProto$CarrierIdMismatch persistAtomsProto$CarrierIdMismatch2 = new PersistAtomsProto$CarrierIdMismatch();
                    persistAtomsProto$CarrierIdMismatchArr2[length5] = persistAtomsProto$CarrierIdMismatch2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$CarrierIdMismatch2);
                    this.carrierIdMismatch = persistAtomsProto$CarrierIdMismatchArr2;
                    break;
                case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                    this.carrierIdTableVersion = codedInputByteBufferNano.readInt32();
                    break;
                case 90:
                    int repeatedFieldArrayLength6 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 90);
                    PersistAtomsProto$DataCallSession[] persistAtomsProto$DataCallSessionArr = this.dataCallSession;
                    int length6 = persistAtomsProto$DataCallSessionArr == null ? 0 : persistAtomsProto$DataCallSessionArr.length;
                    int i6 = repeatedFieldArrayLength6 + length6;
                    PersistAtomsProto$DataCallSession[] persistAtomsProto$DataCallSessionArr2 = new PersistAtomsProto$DataCallSession[i6];
                    if (length6 != 0) {
                        System.arraycopy(persistAtomsProto$DataCallSessionArr, 0, persistAtomsProto$DataCallSessionArr2, 0, length6);
                    }
                    while (length6 < i6 - 1) {
                        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = new PersistAtomsProto$DataCallSession();
                        persistAtomsProto$DataCallSessionArr2[length6] = persistAtomsProto$DataCallSession;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$DataCallSession);
                        codedInputByteBufferNano.readTag();
                        length6++;
                    }
                    PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession2 = new PersistAtomsProto$DataCallSession();
                    persistAtomsProto$DataCallSessionArr2[length6] = persistAtomsProto$DataCallSession2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$DataCallSession2);
                    this.dataCallSession = persistAtomsProto$DataCallSessionArr2;
                    break;
                case 96:
                    this.dataCallSessionPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 106:
                    int repeatedFieldArrayLength7 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 106);
                    PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr = this.cellularServiceState;
                    int length7 = persistAtomsProto$CellularServiceStateArr == null ? 0 : persistAtomsProto$CellularServiceStateArr.length;
                    int i7 = repeatedFieldArrayLength7 + length7;
                    PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr2 = new PersistAtomsProto$CellularServiceState[i7];
                    if (length7 != 0) {
                        System.arraycopy(persistAtomsProto$CellularServiceStateArr, 0, persistAtomsProto$CellularServiceStateArr2, 0, length7);
                    }
                    while (length7 < i7 - 1) {
                        PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState = new PersistAtomsProto$CellularServiceState();
                        persistAtomsProto$CellularServiceStateArr2[length7] = persistAtomsProto$CellularServiceState;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$CellularServiceState);
                        codedInputByteBufferNano.readTag();
                        length7++;
                    }
                    PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState2 = new PersistAtomsProto$CellularServiceState();
                    persistAtomsProto$CellularServiceStateArr2[length7] = persistAtomsProto$CellularServiceState2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$CellularServiceState2);
                    this.cellularServiceState = persistAtomsProto$CellularServiceStateArr2;
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                    this.cellularServiceStatePullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL /* 122 */:
                    int repeatedFieldArrayLength8 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL);
                    PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr = this.cellularDataServiceSwitch;
                    int length8 = persistAtomsProto$CellularDataServiceSwitchArr == null ? 0 : persistAtomsProto$CellularDataServiceSwitchArr.length;
                    int i8 = repeatedFieldArrayLength8 + length8;
                    PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr2 = new PersistAtomsProto$CellularDataServiceSwitch[i8];
                    if (length8 != 0) {
                        System.arraycopy(persistAtomsProto$CellularDataServiceSwitchArr, 0, persistAtomsProto$CellularDataServiceSwitchArr2, 0, length8);
                    }
                    while (length8 < i8 - 1) {
                        PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch = new PersistAtomsProto$CellularDataServiceSwitch();
                        persistAtomsProto$CellularDataServiceSwitchArr2[length8] = persistAtomsProto$CellularDataServiceSwitch;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$CellularDataServiceSwitch);
                        codedInputByteBufferNano.readTag();
                        length8++;
                    }
                    PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch2 = new PersistAtomsProto$CellularDataServiceSwitch();
                    persistAtomsProto$CellularDataServiceSwitchArr2[length8] = persistAtomsProto$CellularDataServiceSwitch2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$CellularDataServiceSwitch2);
                    this.cellularDataServiceSwitch = persistAtomsProto$CellularDataServiceSwitchArr2;
                    break;
                case 128:
                    this.cellularDataServiceSwitchPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 138:
                    int repeatedFieldArrayLength9 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 138);
                    PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr = this.imsRegistrationTermination;
                    int length9 = persistAtomsProto$ImsRegistrationTerminationArr == null ? 0 : persistAtomsProto$ImsRegistrationTerminationArr.length;
                    int i9 = repeatedFieldArrayLength9 + length9;
                    PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr2 = new PersistAtomsProto$ImsRegistrationTermination[i9];
                    if (length9 != 0) {
                        System.arraycopy(persistAtomsProto$ImsRegistrationTerminationArr, 0, persistAtomsProto$ImsRegistrationTerminationArr2, 0, length9);
                    }
                    while (length9 < i9 - 1) {
                        PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination = new PersistAtomsProto$ImsRegistrationTermination();
                        persistAtomsProto$ImsRegistrationTerminationArr2[length9] = persistAtomsProto$ImsRegistrationTermination;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$ImsRegistrationTermination);
                        codedInputByteBufferNano.readTag();
                        length9++;
                    }
                    PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination2 = new PersistAtomsProto$ImsRegistrationTermination();
                    persistAtomsProto$ImsRegistrationTerminationArr2[length9] = persistAtomsProto$ImsRegistrationTermination2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$ImsRegistrationTermination2);
                    this.imsRegistrationTermination = persistAtomsProto$ImsRegistrationTerminationArr2;
                    break;
                case 144:
                    this.imsRegistrationTerminationPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 154:
                    int repeatedFieldArrayLength10 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 154);
                    PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr = this.imsRegistrationStats;
                    int length10 = persistAtomsProto$ImsRegistrationStatsArr == null ? 0 : persistAtomsProto$ImsRegistrationStatsArr.length;
                    int i10 = repeatedFieldArrayLength10 + length10;
                    PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr2 = new PersistAtomsProto$ImsRegistrationStats[i10];
                    if (length10 != 0) {
                        System.arraycopy(persistAtomsProto$ImsRegistrationStatsArr, 0, persistAtomsProto$ImsRegistrationStatsArr2, 0, length10);
                    }
                    while (length10 < i10 - 1) {
                        PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats = new PersistAtomsProto$ImsRegistrationStats();
                        persistAtomsProto$ImsRegistrationStatsArr2[length10] = persistAtomsProto$ImsRegistrationStats;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$ImsRegistrationStats);
                        codedInputByteBufferNano.readTag();
                        length10++;
                    }
                    PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats2 = new PersistAtomsProto$ImsRegistrationStats();
                    persistAtomsProto$ImsRegistrationStatsArr2[length10] = persistAtomsProto$ImsRegistrationStats2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$ImsRegistrationStats2);
                    this.imsRegistrationStats = persistAtomsProto$ImsRegistrationStatsArr2;
                    break;
                case SmsMessage.MAX_USER_DATA_SEPTETS /* 160 */:
                    this.imsRegistrationStatsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 170:
                    this.buildFingerprint = codedInputByteBufferNano.readString();
                    break;
                case 178:
                    int repeatedFieldArrayLength11 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 178);
                    PersistAtomsProto$NetworkRequests[] persistAtomsProto$NetworkRequestsArr = this.networkRequests;
                    int length11 = persistAtomsProto$NetworkRequestsArr == null ? 0 : persistAtomsProto$NetworkRequestsArr.length;
                    int i11 = repeatedFieldArrayLength11 + length11;
                    PersistAtomsProto$NetworkRequests[] persistAtomsProto$NetworkRequestsArr2 = new PersistAtomsProto$NetworkRequests[i11];
                    if (length11 != 0) {
                        System.arraycopy(persistAtomsProto$NetworkRequestsArr, 0, persistAtomsProto$NetworkRequestsArr2, 0, length11);
                    }
                    while (length11 < i11 - 1) {
                        PersistAtomsProto$NetworkRequests persistAtomsProto$NetworkRequests = new PersistAtomsProto$NetworkRequests();
                        persistAtomsProto$NetworkRequestsArr2[length11] = persistAtomsProto$NetworkRequests;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$NetworkRequests);
                        codedInputByteBufferNano.readTag();
                        length11++;
                    }
                    PersistAtomsProto$NetworkRequests persistAtomsProto$NetworkRequests2 = new PersistAtomsProto$NetworkRequests();
                    persistAtomsProto$NetworkRequestsArr2[length11] = persistAtomsProto$NetworkRequests2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$NetworkRequests2);
                    this.networkRequests = persistAtomsProto$NetworkRequestsArr2;
                    break;
                case 184:
                    this.networkRequestsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 194:
                    int repeatedFieldArrayLength12 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 194);
                    PersistAtomsProto$ImsRegistrationFeatureTagStats[] persistAtomsProto$ImsRegistrationFeatureTagStatsArr = this.imsRegistrationFeatureTagStats;
                    int length12 = persistAtomsProto$ImsRegistrationFeatureTagStatsArr == null ? 0 : persistAtomsProto$ImsRegistrationFeatureTagStatsArr.length;
                    int i12 = repeatedFieldArrayLength12 + length12;
                    PersistAtomsProto$ImsRegistrationFeatureTagStats[] persistAtomsProto$ImsRegistrationFeatureTagStatsArr2 = new PersistAtomsProto$ImsRegistrationFeatureTagStats[i12];
                    if (length12 != 0) {
                        System.arraycopy(persistAtomsProto$ImsRegistrationFeatureTagStatsArr, 0, persistAtomsProto$ImsRegistrationFeatureTagStatsArr2, 0, length12);
                    }
                    while (length12 < i12 - 1) {
                        PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats = new PersistAtomsProto$ImsRegistrationFeatureTagStats();
                        persistAtomsProto$ImsRegistrationFeatureTagStatsArr2[length12] = persistAtomsProto$ImsRegistrationFeatureTagStats;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$ImsRegistrationFeatureTagStats);
                        codedInputByteBufferNano.readTag();
                        length12++;
                    }
                    PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats2 = new PersistAtomsProto$ImsRegistrationFeatureTagStats();
                    persistAtomsProto$ImsRegistrationFeatureTagStatsArr2[length12] = persistAtomsProto$ImsRegistrationFeatureTagStats2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$ImsRegistrationFeatureTagStats2);
                    this.imsRegistrationFeatureTagStats = persistAtomsProto$ImsRegistrationFeatureTagStatsArr2;
                    break;
                case ImsRttTextHandler.MAX_BUFFERING_DELAY_MILLIS /* 200 */:
                    this.imsRegistrationFeatureTagStatsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 210:
                    int repeatedFieldArrayLength13 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 210);
                    PersistAtomsProto$RcsClientProvisioningStats[] persistAtomsProto$RcsClientProvisioningStatsArr = this.rcsClientProvisioningStats;
                    int length13 = persistAtomsProto$RcsClientProvisioningStatsArr == null ? 0 : persistAtomsProto$RcsClientProvisioningStatsArr.length;
                    int i13 = repeatedFieldArrayLength13 + length13;
                    PersistAtomsProto$RcsClientProvisioningStats[] persistAtomsProto$RcsClientProvisioningStatsArr2 = new PersistAtomsProto$RcsClientProvisioningStats[i13];
                    if (length13 != 0) {
                        System.arraycopy(persistAtomsProto$RcsClientProvisioningStatsArr, 0, persistAtomsProto$RcsClientProvisioningStatsArr2, 0, length13);
                    }
                    while (length13 < i13 - 1) {
                        PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats = new PersistAtomsProto$RcsClientProvisioningStats();
                        persistAtomsProto$RcsClientProvisioningStatsArr2[length13] = persistAtomsProto$RcsClientProvisioningStats;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$RcsClientProvisioningStats);
                        codedInputByteBufferNano.readTag();
                        length13++;
                    }
                    PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats2 = new PersistAtomsProto$RcsClientProvisioningStats();
                    persistAtomsProto$RcsClientProvisioningStatsArr2[length13] = persistAtomsProto$RcsClientProvisioningStats2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$RcsClientProvisioningStats2);
                    this.rcsClientProvisioningStats = persistAtomsProto$RcsClientProvisioningStatsArr2;
                    break;
                case 216:
                    this.rcsClientProvisioningStatsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 226:
                    int repeatedFieldArrayLength14 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 226);
                    PersistAtomsProto$RcsAcsProvisioningStats[] persistAtomsProto$RcsAcsProvisioningStatsArr = this.rcsAcsProvisioningStats;
                    int length14 = persistAtomsProto$RcsAcsProvisioningStatsArr == null ? 0 : persistAtomsProto$RcsAcsProvisioningStatsArr.length;
                    int i14 = repeatedFieldArrayLength14 + length14;
                    PersistAtomsProto$RcsAcsProvisioningStats[] persistAtomsProto$RcsAcsProvisioningStatsArr2 = new PersistAtomsProto$RcsAcsProvisioningStats[i14];
                    if (length14 != 0) {
                        System.arraycopy(persistAtomsProto$RcsAcsProvisioningStatsArr, 0, persistAtomsProto$RcsAcsProvisioningStatsArr2, 0, length14);
                    }
                    while (length14 < i14 - 1) {
                        PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats = new PersistAtomsProto$RcsAcsProvisioningStats();
                        persistAtomsProto$RcsAcsProvisioningStatsArr2[length14] = persistAtomsProto$RcsAcsProvisioningStats;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$RcsAcsProvisioningStats);
                        codedInputByteBufferNano.readTag();
                        length14++;
                    }
                    PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats2 = new PersistAtomsProto$RcsAcsProvisioningStats();
                    persistAtomsProto$RcsAcsProvisioningStatsArr2[length14] = persistAtomsProto$RcsAcsProvisioningStats2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$RcsAcsProvisioningStats2);
                    this.rcsAcsProvisioningStats = persistAtomsProto$RcsAcsProvisioningStatsArr2;
                    break;
                case 232:
                    this.rcsAcsProvisioningStatsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 242:
                    int repeatedFieldArrayLength15 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 242);
                    PersistAtomsProto$SipDelegateStats[] persistAtomsProto$SipDelegateStatsArr = this.sipDelegateStats;
                    int length15 = persistAtomsProto$SipDelegateStatsArr == null ? 0 : persistAtomsProto$SipDelegateStatsArr.length;
                    int i15 = repeatedFieldArrayLength15 + length15;
                    PersistAtomsProto$SipDelegateStats[] persistAtomsProto$SipDelegateStatsArr2 = new PersistAtomsProto$SipDelegateStats[i15];
                    if (length15 != 0) {
                        System.arraycopy(persistAtomsProto$SipDelegateStatsArr, 0, persistAtomsProto$SipDelegateStatsArr2, 0, length15);
                    }
                    while (length15 < i15 - 1) {
                        PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats = new PersistAtomsProto$SipDelegateStats();
                        persistAtomsProto$SipDelegateStatsArr2[length15] = persistAtomsProto$SipDelegateStats;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$SipDelegateStats);
                        codedInputByteBufferNano.readTag();
                        length15++;
                    }
                    PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats2 = new PersistAtomsProto$SipDelegateStats();
                    persistAtomsProto$SipDelegateStatsArr2[length15] = persistAtomsProto$SipDelegateStats2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$SipDelegateStats2);
                    this.sipDelegateStats = persistAtomsProto$SipDelegateStatsArr2;
                    break;
                case 248:
                    this.sipDelegateStatsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case CallFailCause.RADIO_RELEASE_NORMAL /* 258 */:
                    int repeatedFieldArrayLength16 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, CallFailCause.RADIO_RELEASE_NORMAL);
                    PersistAtomsProto$SipTransportFeatureTagStats[] persistAtomsProto$SipTransportFeatureTagStatsArr = this.sipTransportFeatureTagStats;
                    int length16 = persistAtomsProto$SipTransportFeatureTagStatsArr == null ? 0 : persistAtomsProto$SipTransportFeatureTagStatsArr.length;
                    int i16 = repeatedFieldArrayLength16 + length16;
                    PersistAtomsProto$SipTransportFeatureTagStats[] persistAtomsProto$SipTransportFeatureTagStatsArr2 = new PersistAtomsProto$SipTransportFeatureTagStats[i16];
                    if (length16 != 0) {
                        System.arraycopy(persistAtomsProto$SipTransportFeatureTagStatsArr, 0, persistAtomsProto$SipTransportFeatureTagStatsArr2, 0, length16);
                    }
                    while (length16 < i16 - 1) {
                        PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats = new PersistAtomsProto$SipTransportFeatureTagStats();
                        persistAtomsProto$SipTransportFeatureTagStatsArr2[length16] = persistAtomsProto$SipTransportFeatureTagStats;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$SipTransportFeatureTagStats);
                        codedInputByteBufferNano.readTag();
                        length16++;
                    }
                    PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats2 = new PersistAtomsProto$SipTransportFeatureTagStats();
                    persistAtomsProto$SipTransportFeatureTagStatsArr2[length16] = persistAtomsProto$SipTransportFeatureTagStats2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$SipTransportFeatureTagStats2);
                    this.sipTransportFeatureTagStats = persistAtomsProto$SipTransportFeatureTagStatsArr2;
                    break;
                case 264:
                    this.sipTransportFeatureTagStatsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 274:
                    int repeatedFieldArrayLength17 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 274);
                    PersistAtomsProto$SipMessageResponse[] persistAtomsProto$SipMessageResponseArr = this.sipMessageResponse;
                    int length17 = persistAtomsProto$SipMessageResponseArr == null ? 0 : persistAtomsProto$SipMessageResponseArr.length;
                    int i17 = repeatedFieldArrayLength17 + length17;
                    PersistAtomsProto$SipMessageResponse[] persistAtomsProto$SipMessageResponseArr2 = new PersistAtomsProto$SipMessageResponse[i17];
                    if (length17 != 0) {
                        System.arraycopy(persistAtomsProto$SipMessageResponseArr, 0, persistAtomsProto$SipMessageResponseArr2, 0, length17);
                    }
                    while (length17 < i17 - 1) {
                        PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse = new PersistAtomsProto$SipMessageResponse();
                        persistAtomsProto$SipMessageResponseArr2[length17] = persistAtomsProto$SipMessageResponse;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$SipMessageResponse);
                        codedInputByteBufferNano.readTag();
                        length17++;
                    }
                    PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse2 = new PersistAtomsProto$SipMessageResponse();
                    persistAtomsProto$SipMessageResponseArr2[length17] = persistAtomsProto$SipMessageResponse2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$SipMessageResponse2);
                    this.sipMessageResponse = persistAtomsProto$SipMessageResponseArr2;
                    break;
                case 280:
                    this.sipMessageResponsePullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 290:
                    int repeatedFieldArrayLength18 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 290);
                    PersistAtomsProto$SipTransportSession[] persistAtomsProto$SipTransportSessionArr = this.sipTransportSession;
                    int length18 = persistAtomsProto$SipTransportSessionArr == null ? 0 : persistAtomsProto$SipTransportSessionArr.length;
                    int i18 = repeatedFieldArrayLength18 + length18;
                    PersistAtomsProto$SipTransportSession[] persistAtomsProto$SipTransportSessionArr2 = new PersistAtomsProto$SipTransportSession[i18];
                    if (length18 != 0) {
                        System.arraycopy(persistAtomsProto$SipTransportSessionArr, 0, persistAtomsProto$SipTransportSessionArr2, 0, length18);
                    }
                    while (length18 < i18 - 1) {
                        PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession = new PersistAtomsProto$SipTransportSession();
                        persistAtomsProto$SipTransportSessionArr2[length18] = persistAtomsProto$SipTransportSession;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$SipTransportSession);
                        codedInputByteBufferNano.readTag();
                        length18++;
                    }
                    PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession2 = new PersistAtomsProto$SipTransportSession();
                    persistAtomsProto$SipTransportSessionArr2[length18] = persistAtomsProto$SipTransportSession2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$SipTransportSession2);
                    this.sipTransportSession = persistAtomsProto$SipTransportSessionArr2;
                    break;
                case 296:
                    this.sipTransportSessionPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 306:
                    int repeatedFieldArrayLength19 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 306);
                    PersistAtomsProto$ImsDedicatedBearerListenerEvent[] persistAtomsProto$ImsDedicatedBearerListenerEventArr = this.imsDedicatedBearerListenerEvent;
                    int length19 = persistAtomsProto$ImsDedicatedBearerListenerEventArr == null ? 0 : persistAtomsProto$ImsDedicatedBearerListenerEventArr.length;
                    int i19 = repeatedFieldArrayLength19 + length19;
                    PersistAtomsProto$ImsDedicatedBearerListenerEvent[] persistAtomsProto$ImsDedicatedBearerListenerEventArr2 = new PersistAtomsProto$ImsDedicatedBearerListenerEvent[i19];
                    if (length19 != 0) {
                        System.arraycopy(persistAtomsProto$ImsDedicatedBearerListenerEventArr, 0, persistAtomsProto$ImsDedicatedBearerListenerEventArr2, 0, length19);
                    }
                    while (length19 < i19 - 1) {
                        PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent = new PersistAtomsProto$ImsDedicatedBearerListenerEvent();
                        persistAtomsProto$ImsDedicatedBearerListenerEventArr2[length19] = persistAtomsProto$ImsDedicatedBearerListenerEvent;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$ImsDedicatedBearerListenerEvent);
                        codedInputByteBufferNano.readTag();
                        length19++;
                    }
                    PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent2 = new PersistAtomsProto$ImsDedicatedBearerListenerEvent();
                    persistAtomsProto$ImsDedicatedBearerListenerEventArr2[length19] = persistAtomsProto$ImsDedicatedBearerListenerEvent2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$ImsDedicatedBearerListenerEvent2);
                    this.imsDedicatedBearerListenerEvent = persistAtomsProto$ImsDedicatedBearerListenerEventArr2;
                    break;
                case 312:
                    this.imsDedicatedBearerListenerEventPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 322:
                    int repeatedFieldArrayLength20 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 322);
                    PersistAtomsProto$ImsDedicatedBearerEvent[] persistAtomsProto$ImsDedicatedBearerEventArr = this.imsDedicatedBearerEvent;
                    int length20 = persistAtomsProto$ImsDedicatedBearerEventArr == null ? 0 : persistAtomsProto$ImsDedicatedBearerEventArr.length;
                    int i20 = repeatedFieldArrayLength20 + length20;
                    PersistAtomsProto$ImsDedicatedBearerEvent[] persistAtomsProto$ImsDedicatedBearerEventArr2 = new PersistAtomsProto$ImsDedicatedBearerEvent[i20];
                    if (length20 != 0) {
                        System.arraycopy(persistAtomsProto$ImsDedicatedBearerEventArr, 0, persistAtomsProto$ImsDedicatedBearerEventArr2, 0, length20);
                    }
                    while (length20 < i20 - 1) {
                        PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent = new PersistAtomsProto$ImsDedicatedBearerEvent();
                        persistAtomsProto$ImsDedicatedBearerEventArr2[length20] = persistAtomsProto$ImsDedicatedBearerEvent;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$ImsDedicatedBearerEvent);
                        codedInputByteBufferNano.readTag();
                        length20++;
                    }
                    PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent2 = new PersistAtomsProto$ImsDedicatedBearerEvent();
                    persistAtomsProto$ImsDedicatedBearerEventArr2[length20] = persistAtomsProto$ImsDedicatedBearerEvent2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$ImsDedicatedBearerEvent2);
                    this.imsDedicatedBearerEvent = persistAtomsProto$ImsDedicatedBearerEventArr2;
                    break;
                case 328:
                    this.imsDedicatedBearerEventPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 338:
                    int repeatedFieldArrayLength21 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 338);
                    PersistAtomsProto$ImsRegistrationServiceDescStats[] persistAtomsProto$ImsRegistrationServiceDescStatsArr = this.imsRegistrationServiceDescStats;
                    int length21 = persistAtomsProto$ImsRegistrationServiceDescStatsArr == null ? 0 : persistAtomsProto$ImsRegistrationServiceDescStatsArr.length;
                    int i21 = repeatedFieldArrayLength21 + length21;
                    PersistAtomsProto$ImsRegistrationServiceDescStats[] persistAtomsProto$ImsRegistrationServiceDescStatsArr2 = new PersistAtomsProto$ImsRegistrationServiceDescStats[i21];
                    if (length21 != 0) {
                        System.arraycopy(persistAtomsProto$ImsRegistrationServiceDescStatsArr, 0, persistAtomsProto$ImsRegistrationServiceDescStatsArr2, 0, length21);
                    }
                    while (length21 < i21 - 1) {
                        PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats = new PersistAtomsProto$ImsRegistrationServiceDescStats();
                        persistAtomsProto$ImsRegistrationServiceDescStatsArr2[length21] = persistAtomsProto$ImsRegistrationServiceDescStats;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$ImsRegistrationServiceDescStats);
                        codedInputByteBufferNano.readTag();
                        length21++;
                    }
                    PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats2 = new PersistAtomsProto$ImsRegistrationServiceDescStats();
                    persistAtomsProto$ImsRegistrationServiceDescStatsArr2[length21] = persistAtomsProto$ImsRegistrationServiceDescStats2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$ImsRegistrationServiceDescStats2);
                    this.imsRegistrationServiceDescStats = persistAtomsProto$ImsRegistrationServiceDescStatsArr2;
                    break;
                case 344:
                    this.imsRegistrationServiceDescStatsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 354:
                    int repeatedFieldArrayLength22 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 354);
                    PersistAtomsProto$UceEventStats[] persistAtomsProto$UceEventStatsArr = this.uceEventStats;
                    int length22 = persistAtomsProto$UceEventStatsArr == null ? 0 : persistAtomsProto$UceEventStatsArr.length;
                    int i22 = repeatedFieldArrayLength22 + length22;
                    PersistAtomsProto$UceEventStats[] persistAtomsProto$UceEventStatsArr2 = new PersistAtomsProto$UceEventStats[i22];
                    if (length22 != 0) {
                        System.arraycopy(persistAtomsProto$UceEventStatsArr, 0, persistAtomsProto$UceEventStatsArr2, 0, length22);
                    }
                    while (length22 < i22 - 1) {
                        PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats = new PersistAtomsProto$UceEventStats();
                        persistAtomsProto$UceEventStatsArr2[length22] = persistAtomsProto$UceEventStats;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$UceEventStats);
                        codedInputByteBufferNano.readTag();
                        length22++;
                    }
                    PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats2 = new PersistAtomsProto$UceEventStats();
                    persistAtomsProto$UceEventStatsArr2[length22] = persistAtomsProto$UceEventStats2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$UceEventStats2);
                    this.uceEventStats = persistAtomsProto$UceEventStatsArr2;
                    break;
                case 360:
                    this.uceEventStatsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 370:
                    int repeatedFieldArrayLength23 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 370);
                    PersistAtomsProto$PresenceNotifyEvent[] persistAtomsProto$PresenceNotifyEventArr = this.presenceNotifyEvent;
                    int length23 = persistAtomsProto$PresenceNotifyEventArr == null ? 0 : persistAtomsProto$PresenceNotifyEventArr.length;
                    int i23 = repeatedFieldArrayLength23 + length23;
                    PersistAtomsProto$PresenceNotifyEvent[] persistAtomsProto$PresenceNotifyEventArr2 = new PersistAtomsProto$PresenceNotifyEvent[i23];
                    if (length23 != 0) {
                        System.arraycopy(persistAtomsProto$PresenceNotifyEventArr, 0, persistAtomsProto$PresenceNotifyEventArr2, 0, length23);
                    }
                    while (length23 < i23 - 1) {
                        PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent = new PersistAtomsProto$PresenceNotifyEvent();
                        persistAtomsProto$PresenceNotifyEventArr2[length23] = persistAtomsProto$PresenceNotifyEvent;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$PresenceNotifyEvent);
                        codedInputByteBufferNano.readTag();
                        length23++;
                    }
                    PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent2 = new PersistAtomsProto$PresenceNotifyEvent();
                    persistAtomsProto$PresenceNotifyEventArr2[length23] = persistAtomsProto$PresenceNotifyEvent2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$PresenceNotifyEvent2);
                    this.presenceNotifyEvent = persistAtomsProto$PresenceNotifyEventArr2;
                    break;
                case 376:
                    this.presenceNotifyEventPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 386:
                    int repeatedFieldArrayLength24 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 386);
                    PersistAtomsProto$GbaEvent[] persistAtomsProto$GbaEventArr = this.gbaEvent;
                    int length24 = persistAtomsProto$GbaEventArr == null ? 0 : persistAtomsProto$GbaEventArr.length;
                    int i24 = repeatedFieldArrayLength24 + length24;
                    PersistAtomsProto$GbaEvent[] persistAtomsProto$GbaEventArr2 = new PersistAtomsProto$GbaEvent[i24];
                    if (length24 != 0) {
                        System.arraycopy(persistAtomsProto$GbaEventArr, 0, persistAtomsProto$GbaEventArr2, 0, length24);
                    }
                    while (length24 < i24 - 1) {
                        PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent = new PersistAtomsProto$GbaEvent();
                        persistAtomsProto$GbaEventArr2[length24] = persistAtomsProto$GbaEvent;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$GbaEvent);
                        codedInputByteBufferNano.readTag();
                        length24++;
                    }
                    PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent2 = new PersistAtomsProto$GbaEvent();
                    persistAtomsProto$GbaEventArr2[length24] = persistAtomsProto$GbaEvent2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$GbaEvent2);
                    this.gbaEvent = persistAtomsProto$GbaEventArr2;
                    break;
                case 392:
                    this.gbaEventPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 402:
                    int repeatedFieldArrayLength25 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 402);
                    PersistAtomsProto$NetworkRequestsV2[] persistAtomsProto$NetworkRequestsV2Arr = this.networkRequestsV2;
                    int length25 = persistAtomsProto$NetworkRequestsV2Arr == null ? 0 : persistAtomsProto$NetworkRequestsV2Arr.length;
                    int i25 = repeatedFieldArrayLength25 + length25;
                    PersistAtomsProto$NetworkRequestsV2[] persistAtomsProto$NetworkRequestsV2Arr2 = new PersistAtomsProto$NetworkRequestsV2[i25];
                    if (length25 != 0) {
                        System.arraycopy(persistAtomsProto$NetworkRequestsV2Arr, 0, persistAtomsProto$NetworkRequestsV2Arr2, 0, length25);
                    }
                    while (length25 < i25 - 1) {
                        PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV2 = new PersistAtomsProto$NetworkRequestsV2();
                        persistAtomsProto$NetworkRequestsV2Arr2[length25] = persistAtomsProto$NetworkRequestsV2;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$NetworkRequestsV2);
                        codedInputByteBufferNano.readTag();
                        length25++;
                    }
                    PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV22 = new PersistAtomsProto$NetworkRequestsV2();
                    persistAtomsProto$NetworkRequestsV2Arr2[length25] = persistAtomsProto$NetworkRequestsV22;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$NetworkRequestsV22);
                    this.networkRequestsV2 = persistAtomsProto$NetworkRequestsV2Arr2;
                    break;
                case 408:
                    this.networkRequestsV2PullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 418:
                    int repeatedFieldArrayLength26 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 418);
                    PersistAtomsProto$UnmeteredNetworks[] persistAtomsProto$UnmeteredNetworksArr = this.unmeteredNetworks;
                    int length26 = persistAtomsProto$UnmeteredNetworksArr == null ? 0 : persistAtomsProto$UnmeteredNetworksArr.length;
                    int i26 = repeatedFieldArrayLength26 + length26;
                    PersistAtomsProto$UnmeteredNetworks[] persistAtomsProto$UnmeteredNetworksArr2 = new PersistAtomsProto$UnmeteredNetworks[i26];
                    if (length26 != 0) {
                        System.arraycopy(persistAtomsProto$UnmeteredNetworksArr, 0, persistAtomsProto$UnmeteredNetworksArr2, 0, length26);
                    }
                    while (length26 < i26 - 1) {
                        PersistAtomsProto$UnmeteredNetworks persistAtomsProto$UnmeteredNetworks = new PersistAtomsProto$UnmeteredNetworks();
                        persistAtomsProto$UnmeteredNetworksArr2[length26] = persistAtomsProto$UnmeteredNetworks;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$UnmeteredNetworks);
                        codedInputByteBufferNano.readTag();
                        length26++;
                    }
                    PersistAtomsProto$UnmeteredNetworks persistAtomsProto$UnmeteredNetworks2 = new PersistAtomsProto$UnmeteredNetworks();
                    persistAtomsProto$UnmeteredNetworksArr2[length26] = persistAtomsProto$UnmeteredNetworks2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$UnmeteredNetworks2);
                    this.unmeteredNetworks = persistAtomsProto$UnmeteredNetworksArr2;
                    break;
                case 426:
                    int repeatedFieldArrayLength27 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 426);
                    PersistAtomsProto$OutgoingShortCodeSms[] persistAtomsProto$OutgoingShortCodeSmsArr = this.outgoingShortCodeSms;
                    int length27 = persistAtomsProto$OutgoingShortCodeSmsArr == null ? 0 : persistAtomsProto$OutgoingShortCodeSmsArr.length;
                    int i27 = repeatedFieldArrayLength27 + length27;
                    PersistAtomsProto$OutgoingShortCodeSms[] persistAtomsProto$OutgoingShortCodeSmsArr2 = new PersistAtomsProto$OutgoingShortCodeSms[i27];
                    if (length27 != 0) {
                        System.arraycopy(persistAtomsProto$OutgoingShortCodeSmsArr, 0, persistAtomsProto$OutgoingShortCodeSmsArr2, 0, length27);
                    }
                    while (length27 < i27 - 1) {
                        PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms = new PersistAtomsProto$OutgoingShortCodeSms();
                        persistAtomsProto$OutgoingShortCodeSmsArr2[length27] = persistAtomsProto$OutgoingShortCodeSms;
                        codedInputByteBufferNano.readMessage(persistAtomsProto$OutgoingShortCodeSms);
                        codedInputByteBufferNano.readTag();
                        length27++;
                    }
                    PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms2 = new PersistAtomsProto$OutgoingShortCodeSms();
                    persistAtomsProto$OutgoingShortCodeSmsArr2[length27] = persistAtomsProto$OutgoingShortCodeSms2;
                    codedInputByteBufferNano.readMessage(persistAtomsProto$OutgoingShortCodeSms2);
                    this.outgoingShortCodeSms = persistAtomsProto$OutgoingShortCodeSmsArr2;
                    break;
                case 432:
                    this.outgoingShortCodeSmsPullTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 440:
                    this.autoDataSwitchToggleCount = codedInputByteBufferNano.readInt32();
                    break;
                default:
                    if (storeUnknownField(codedInputByteBufferNano, readTag)) {
                        break;
                    } else {
                        return this;
                    }
            }
        }
    }

    public static PersistAtomsProto$PersistAtoms parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$PersistAtoms) MessageNano.mergeFrom(new PersistAtomsProto$PersistAtoms(), bArr);
    }

    public static PersistAtomsProto$PersistAtoms parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$PersistAtoms().mergeFrom(codedInputByteBufferNano);
    }
}
