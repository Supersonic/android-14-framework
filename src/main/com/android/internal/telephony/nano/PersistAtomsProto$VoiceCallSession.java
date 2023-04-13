package com.android.internal.telephony.nano;

import android.telephony.gsm.SmsMessage;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.cat.BerTlv;
import com.android.internal.telephony.imsphone.ImsRttTextHandler;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.util.DnsPacket;
import com.android.internal.telephony.util.NetworkStackConstants;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$VoiceCallSession extends ExtendableMessageNano<PersistAtomsProto$VoiceCallSession> {
    private static volatile PersistAtomsProto$VoiceCallSession[] _emptyArray;
    public int bandAtEnd;
    public int bearerAtEnd;
    public int bearerAtStart;
    public int callDuration;
    public int carrierId;
    public long codecBitmask;
    public int concurrentCallCountAtEnd;
    public int concurrentCallCountAtStart;
    public int direction;
    public int disconnectExtraCode;
    public String disconnectExtraMessage;
    public int disconnectReasonCode;
    public boolean isEmergency;
    public boolean isEsim;
    public boolean isMultiSim;
    public boolean isMultiparty;
    public boolean isRoaming;
    public int lastKnownRat;
    public int mainCodecQuality;
    public int ratAtConnected;
    public int ratAtEnd;
    public int ratAtStart;
    public long ratSwitchCount;
    public boolean rttEnabled;
    public long setupBeginMillis;
    public int setupDurationMillis;
    public boolean setupFailed;
    public int signalStrengthAtEnd;
    public int simSlotIndex;
    public long srvccCancellationCount;
    public boolean srvccCompleted;
    public long srvccFailureCount;
    public boolean videoEnabled;

    public static PersistAtomsProto$VoiceCallSession[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$VoiceCallSession[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$VoiceCallSession() {
        clear();
    }

    public PersistAtomsProto$VoiceCallSession clear() {
        this.bearerAtStart = 0;
        this.bearerAtEnd = 0;
        this.direction = 0;
        this.setupFailed = false;
        this.disconnectReasonCode = 0;
        this.disconnectExtraCode = 0;
        this.disconnectExtraMessage = PhoneConfigurationManager.SSSS;
        this.ratAtStart = 0;
        this.ratAtEnd = 0;
        this.ratSwitchCount = 0L;
        this.codecBitmask = 0L;
        this.concurrentCallCountAtStart = 0;
        this.concurrentCallCountAtEnd = 0;
        this.simSlotIndex = 0;
        this.isMultiSim = false;
        this.isEsim = false;
        this.carrierId = 0;
        this.srvccCompleted = false;
        this.srvccFailureCount = 0L;
        this.srvccCancellationCount = 0L;
        this.rttEnabled = false;
        this.isEmergency = false;
        this.isRoaming = false;
        this.signalStrengthAtEnd = 0;
        this.bandAtEnd = 0;
        this.setupDurationMillis = 0;
        this.mainCodecQuality = 0;
        this.videoEnabled = false;
        this.ratAtConnected = 0;
        this.isMultiparty = false;
        this.callDuration = 0;
        this.lastKnownRat = 0;
        this.setupBeginMillis = 0L;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.bearerAtStart;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.bearerAtEnd;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.direction;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        boolean z = this.setupFailed;
        if (z) {
            codedOutputByteBufferNano.writeBool(5, z);
        }
        int i4 = this.disconnectReasonCode;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i4);
        }
        int i5 = this.disconnectExtraCode;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(7, i5);
        }
        if (!this.disconnectExtraMessage.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(8, this.disconnectExtraMessage);
        }
        int i6 = this.ratAtStart;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(9, i6);
        }
        int i7 = this.ratAtEnd;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(10, i7);
        }
        long j = this.ratSwitchCount;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(11, j);
        }
        long j2 = this.codecBitmask;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeInt64(12, j2);
        }
        int i8 = this.concurrentCallCountAtStart;
        if (i8 != 0) {
            codedOutputByteBufferNano.writeInt32(13, i8);
        }
        int i9 = this.concurrentCallCountAtEnd;
        if (i9 != 0) {
            codedOutputByteBufferNano.writeInt32(14, i9);
        }
        int i10 = this.simSlotIndex;
        if (i10 != 0) {
            codedOutputByteBufferNano.writeInt32(15, i10);
        }
        boolean z2 = this.isMultiSim;
        if (z2) {
            codedOutputByteBufferNano.writeBool(16, z2);
        }
        boolean z3 = this.isEsim;
        if (z3) {
            codedOutputByteBufferNano.writeBool(17, z3);
        }
        int i11 = this.carrierId;
        if (i11 != 0) {
            codedOutputByteBufferNano.writeInt32(18, i11);
        }
        boolean z4 = this.srvccCompleted;
        if (z4) {
            codedOutputByteBufferNano.writeBool(19, z4);
        }
        long j3 = this.srvccFailureCount;
        if (j3 != 0) {
            codedOutputByteBufferNano.writeInt64(20, j3);
        }
        long j4 = this.srvccCancellationCount;
        if (j4 != 0) {
            codedOutputByteBufferNano.writeInt64(21, j4);
        }
        boolean z5 = this.rttEnabled;
        if (z5) {
            codedOutputByteBufferNano.writeBool(22, z5);
        }
        boolean z6 = this.isEmergency;
        if (z6) {
            codedOutputByteBufferNano.writeBool(23, z6);
        }
        boolean z7 = this.isRoaming;
        if (z7) {
            codedOutputByteBufferNano.writeBool(24, z7);
        }
        int i12 = this.signalStrengthAtEnd;
        if (i12 != 0) {
            codedOutputByteBufferNano.writeInt32(25, i12);
        }
        int i13 = this.bandAtEnd;
        if (i13 != 0) {
            codedOutputByteBufferNano.writeInt32(26, i13);
        }
        int i14 = this.setupDurationMillis;
        if (i14 != 0) {
            codedOutputByteBufferNano.writeInt32(27, i14);
        }
        int i15 = this.mainCodecQuality;
        if (i15 != 0) {
            codedOutputByteBufferNano.writeInt32(28, i15);
        }
        boolean z8 = this.videoEnabled;
        if (z8) {
            codedOutputByteBufferNano.writeBool(29, z8);
        }
        int i16 = this.ratAtConnected;
        if (i16 != 0) {
            codedOutputByteBufferNano.writeInt32(30, i16);
        }
        boolean z9 = this.isMultiparty;
        if (z9) {
            codedOutputByteBufferNano.writeBool(31, z9);
        }
        int i17 = this.callDuration;
        if (i17 != 0) {
            codedOutputByteBufferNano.writeInt32(32, i17);
        }
        int i18 = this.lastKnownRat;
        if (i18 != 0) {
            codedOutputByteBufferNano.writeInt32(33, i18);
        }
        long j5 = this.setupBeginMillis;
        if (j5 != 0) {
            codedOutputByteBufferNano.writeInt64(10001, j5);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.bearerAtStart;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.bearerAtEnd;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.direction;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        boolean z = this.setupFailed;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z);
        }
        int i4 = this.disconnectReasonCode;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i4);
        }
        int i5 = this.disconnectExtraCode;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i5);
        }
        if (!this.disconnectExtraMessage.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(8, this.disconnectExtraMessage);
        }
        int i6 = this.ratAtStart;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i6);
        }
        int i7 = this.ratAtEnd;
        if (i7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i7);
        }
        long j = this.ratSwitchCount;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(11, j);
        }
        long j2 = this.codecBitmask;
        if (j2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(12, j2);
        }
        int i8 = this.concurrentCallCountAtStart;
        if (i8 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, i8);
        }
        int i9 = this.concurrentCallCountAtEnd;
        if (i9 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(14, i9);
        }
        int i10 = this.simSlotIndex;
        if (i10 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, i10);
        }
        boolean z2 = this.isMultiSim;
        if (z2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(16, z2);
        }
        boolean z3 = this.isEsim;
        if (z3) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(17, z3);
        }
        int i11 = this.carrierId;
        if (i11 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(18, i11);
        }
        boolean z4 = this.srvccCompleted;
        if (z4) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(19, z4);
        }
        long j3 = this.srvccFailureCount;
        if (j3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(20, j3);
        }
        long j4 = this.srvccCancellationCount;
        if (j4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(21, j4);
        }
        boolean z5 = this.rttEnabled;
        if (z5) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(22, z5);
        }
        boolean z6 = this.isEmergency;
        if (z6) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(23, z6);
        }
        boolean z7 = this.isRoaming;
        if (z7) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(24, z7);
        }
        int i12 = this.signalStrengthAtEnd;
        if (i12 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(25, i12);
        }
        int i13 = this.bandAtEnd;
        if (i13 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(26, i13);
        }
        int i14 = this.setupDurationMillis;
        if (i14 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(27, i14);
        }
        int i15 = this.mainCodecQuality;
        if (i15 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(28, i15);
        }
        boolean z8 = this.videoEnabled;
        if (z8) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(29, z8);
        }
        int i16 = this.ratAtConnected;
        if (i16 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(30, i16);
        }
        boolean z9 = this.isMultiparty;
        if (z9) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(31, z9);
        }
        int i17 = this.callDuration;
        if (i17 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(32, i17);
        }
        int i18 = this.lastKnownRat;
        if (i18 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(33, i18);
        }
        long j5 = this.setupBeginMillis;
        return j5 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(10001, j5) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$VoiceCallSession mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 8:
                    this.bearerAtStart = codedInputByteBufferNano.readInt32();
                    break;
                case 16:
                    this.bearerAtEnd = codedInputByteBufferNano.readInt32();
                    break;
                case 24:
                    this.direction = codedInputByteBufferNano.readInt32();
                    break;
                case 40:
                    this.setupFailed = codedInputByteBufferNano.readBool();
                    break;
                case 48:
                    this.disconnectReasonCode = codedInputByteBufferNano.readInt32();
                    break;
                case 56:
                    this.disconnectExtraCode = codedInputByteBufferNano.readInt32();
                    break;
                case 66:
                    this.disconnectExtraMessage = codedInputByteBufferNano.readString();
                    break;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    this.ratAtStart = codedInputByteBufferNano.readInt32();
                    break;
                case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                    this.ratAtEnd = codedInputByteBufferNano.readInt32();
                    break;
                case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                    this.ratSwitchCount = codedInputByteBufferNano.readInt64();
                    break;
                case 96:
                    this.codecBitmask = codedInputByteBufferNano.readInt64();
                    break;
                case 104:
                    this.concurrentCallCountAtStart = codedInputByteBufferNano.readInt32();
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                    this.concurrentCallCountAtEnd = codedInputByteBufferNano.readInt32();
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                    this.simSlotIndex = codedInputByteBufferNano.readInt32();
                    break;
                case 128:
                    this.isMultiSim = codedInputByteBufferNano.readBool();
                    break;
                case NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT /* 136 */:
                    this.isEsim = codedInputByteBufferNano.readBool();
                    break;
                case 144:
                    this.carrierId = codedInputByteBufferNano.readInt32();
                    break;
                case 152:
                    this.srvccCompleted = codedInputByteBufferNano.readBool();
                    break;
                case SmsMessage.MAX_USER_DATA_SEPTETS /* 160 */:
                    this.srvccFailureCount = codedInputByteBufferNano.readInt64();
                    break;
                case 168:
                    this.srvccCancellationCount = codedInputByteBufferNano.readInt64();
                    break;
                case 176:
                    this.rttEnabled = codedInputByteBufferNano.readBool();
                    break;
                case 184:
                    this.isEmergency = codedInputByteBufferNano.readBool();
                    break;
                case DnsPacket.DnsRecord.NAME_COMPRESSION /* 192 */:
                    this.isRoaming = codedInputByteBufferNano.readBool();
                    break;
                case ImsRttTextHandler.MAX_BUFFERING_DELAY_MILLIS /* 200 */:
                    this.signalStrengthAtEnd = codedInputByteBufferNano.readInt32();
                    break;
                case BerTlv.BER_PROACTIVE_COMMAND_TAG /* 208 */:
                    this.bandAtEnd = codedInputByteBufferNano.readInt32();
                    break;
                case 216:
                    this.setupDurationMillis = codedInputByteBufferNano.readInt32();
                    break;
                case 224:
                    this.mainCodecQuality = codedInputByteBufferNano.readInt32();
                    break;
                case 232:
                    this.videoEnabled = codedInputByteBufferNano.readBool();
                    break;
                case 240:
                    this.ratAtConnected = codedInputByteBufferNano.readInt32();
                    break;
                case 248:
                    this.isMultiparty = codedInputByteBufferNano.readBool();
                    break;
                case CallFailCause.RADIO_UPLINK_FAILURE /* 256 */:
                    this.callDuration = codedInputByteBufferNano.readInt32();
                    break;
                case 264:
                    this.lastKnownRat = codedInputByteBufferNano.readInt32();
                    break;
                case 80008:
                    this.setupBeginMillis = codedInputByteBufferNano.readInt64();
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

    public static PersistAtomsProto$VoiceCallSession parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$VoiceCallSession) MessageNano.mergeFrom(new PersistAtomsProto$VoiceCallSession(), bArr);
    }

    public static PersistAtomsProto$VoiceCallSession parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$VoiceCallSession().mergeFrom(codedInputByteBufferNano);
    }
}
