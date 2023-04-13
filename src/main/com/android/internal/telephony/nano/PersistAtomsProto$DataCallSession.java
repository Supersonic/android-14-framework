package com.android.internal.telephony.nano;

import android.telephony.gsm.SmsMessage;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.protobuf.nano.WireFormatNano;
import com.android.internal.telephony.util.NetworkStackConstants;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$DataCallSession extends ExtendableMessageNano<PersistAtomsProto$DataCallSession> {
    private static volatile PersistAtomsProto$DataCallSession[] _emptyArray;
    public int apnTypeBitmask;
    public int bandAtEnd;
    public int carrierId;
    public int deactivateReason;
    public int dimension;
    public long durationMinutes;
    public int failureCause;
    public int[] handoverFailureCauses;
    public int[] handoverFailureRat;
    public int ipType;
    public boolean isEsim;
    public boolean isMultiSim;
    public boolean isNonDds;
    public boolean isOpportunistic;
    public boolean isRoaming;
    public boolean ongoing;
    public boolean oosAtEnd;
    public int ratAtEnd;
    public long ratSwitchCount;
    public boolean setupFailed;
    public int suggestedRetryMillis;

    public static PersistAtomsProto$DataCallSession[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$DataCallSession[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$DataCallSession() {
        clear();
    }

    public PersistAtomsProto$DataCallSession clear() {
        this.dimension = 0;
        this.isMultiSim = false;
        this.isEsim = false;
        this.apnTypeBitmask = 0;
        this.carrierId = 0;
        this.isRoaming = false;
        this.ratAtEnd = 0;
        this.oosAtEnd = false;
        this.ratSwitchCount = 0L;
        this.isOpportunistic = false;
        this.ipType = 0;
        this.setupFailed = false;
        this.failureCause = 0;
        this.suggestedRetryMillis = 0;
        this.deactivateReason = 0;
        this.durationMinutes = 0L;
        this.ongoing = false;
        this.bandAtEnd = 0;
        int[] iArr = WireFormatNano.EMPTY_INT_ARRAY;
        this.handoverFailureCauses = iArr;
        this.handoverFailureRat = iArr;
        this.isNonDds = false;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.dimension;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        boolean z = this.isMultiSim;
        if (z) {
            codedOutputByteBufferNano.writeBool(2, z);
        }
        boolean z2 = this.isEsim;
        if (z2) {
            codedOutputByteBufferNano.writeBool(3, z2);
        }
        int i2 = this.apnTypeBitmask;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i2);
        }
        int i3 = this.carrierId;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i3);
        }
        boolean z3 = this.isRoaming;
        if (z3) {
            codedOutputByteBufferNano.writeBool(7, z3);
        }
        int i4 = this.ratAtEnd;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(8, i4);
        }
        boolean z4 = this.oosAtEnd;
        if (z4) {
            codedOutputByteBufferNano.writeBool(9, z4);
        }
        long j = this.ratSwitchCount;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(10, j);
        }
        boolean z5 = this.isOpportunistic;
        if (z5) {
            codedOutputByteBufferNano.writeBool(11, z5);
        }
        int i5 = this.ipType;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(12, i5);
        }
        boolean z6 = this.setupFailed;
        if (z6) {
            codedOutputByteBufferNano.writeBool(13, z6);
        }
        int i6 = this.failureCause;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(14, i6);
        }
        int i7 = this.suggestedRetryMillis;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(15, i7);
        }
        int i8 = this.deactivateReason;
        if (i8 != 0) {
            codedOutputByteBufferNano.writeInt32(16, i8);
        }
        long j2 = this.durationMinutes;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeInt64(17, j2);
        }
        boolean z7 = this.ongoing;
        if (z7) {
            codedOutputByteBufferNano.writeBool(18, z7);
        }
        int i9 = this.bandAtEnd;
        if (i9 != 0) {
            codedOutputByteBufferNano.writeInt32(19, i9);
        }
        int[] iArr = this.handoverFailureCauses;
        int i10 = 0;
        if (iArr != null && iArr.length > 0) {
            int i11 = 0;
            while (true) {
                int[] iArr2 = this.handoverFailureCauses;
                if (i11 >= iArr2.length) {
                    break;
                }
                codedOutputByteBufferNano.writeInt32(20, iArr2[i11]);
                i11++;
            }
        }
        int[] iArr3 = this.handoverFailureRat;
        if (iArr3 != null && iArr3.length > 0) {
            while (true) {
                int[] iArr4 = this.handoverFailureRat;
                if (i10 >= iArr4.length) {
                    break;
                }
                codedOutputByteBufferNano.writeInt32(21, iArr4[i10]);
                i10++;
            }
        }
        boolean z8 = this.isNonDds;
        if (z8) {
            codedOutputByteBufferNano.writeBool(22, z8);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int[] iArr;
        int[] iArr2;
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.dimension;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        boolean z = this.isMultiSim;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, z);
        }
        boolean z2 = this.isEsim;
        if (z2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, z2);
        }
        int i2 = this.apnTypeBitmask;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i2);
        }
        int i3 = this.carrierId;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i3);
        }
        boolean z3 = this.isRoaming;
        if (z3) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z3);
        }
        int i4 = this.ratAtEnd;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i4);
        }
        boolean z4 = this.oosAtEnd;
        if (z4) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(9, z4);
        }
        long j = this.ratSwitchCount;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(10, j);
        }
        boolean z5 = this.isOpportunistic;
        if (z5) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(11, z5);
        }
        int i5 = this.ipType;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(12, i5);
        }
        boolean z6 = this.setupFailed;
        if (z6) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(13, z6);
        }
        int i6 = this.failureCause;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(14, i6);
        }
        int i7 = this.suggestedRetryMillis;
        if (i7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, i7);
        }
        int i8 = this.deactivateReason;
        if (i8 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(16, i8);
        }
        long j2 = this.durationMinutes;
        if (j2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(17, j2);
        }
        boolean z7 = this.ongoing;
        if (z7) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(18, z7);
        }
        int i9 = this.bandAtEnd;
        if (i9 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(19, i9);
        }
        int[] iArr3 = this.handoverFailureCauses;
        int i10 = 0;
        if (iArr3 != null && iArr3.length > 0) {
            int i11 = 0;
            int i12 = 0;
            while (true) {
                iArr2 = this.handoverFailureCauses;
                if (i11 >= iArr2.length) {
                    break;
                }
                i12 += CodedOutputByteBufferNano.computeInt32SizeNoTag(iArr2[i11]);
                i11++;
            }
            computeSerializedSize = computeSerializedSize + i12 + (iArr2.length * 2);
        }
        int[] iArr4 = this.handoverFailureRat;
        if (iArr4 != null && iArr4.length > 0) {
            int i13 = 0;
            while (true) {
                iArr = this.handoverFailureRat;
                if (i10 >= iArr.length) {
                    break;
                }
                i13 += CodedOutputByteBufferNano.computeInt32SizeNoTag(iArr[i10]);
                i10++;
            }
            computeSerializedSize = computeSerializedSize + i13 + (iArr.length * 2);
        }
        boolean z8 = this.isNonDds;
        return z8 ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(22, z8) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$DataCallSession mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 8:
                    this.dimension = codedInputByteBufferNano.readInt32();
                    break;
                case 16:
                    this.isMultiSim = codedInputByteBufferNano.readBool();
                    break;
                case 24:
                    this.isEsim = codedInputByteBufferNano.readBool();
                    break;
                case 40:
                    this.apnTypeBitmask = codedInputByteBufferNano.readInt32();
                    break;
                case 48:
                    this.carrierId = codedInputByteBufferNano.readInt32();
                    break;
                case 56:
                    this.isRoaming = codedInputByteBufferNano.readBool();
                    break;
                case 64:
                    this.ratAtEnd = codedInputByteBufferNano.readInt32();
                    break;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    this.oosAtEnd = codedInputByteBufferNano.readBool();
                    break;
                case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                    this.ratSwitchCount = codedInputByteBufferNano.readInt64();
                    break;
                case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                    this.isOpportunistic = codedInputByteBufferNano.readBool();
                    break;
                case 96:
                    this.ipType = codedInputByteBufferNano.readInt32();
                    break;
                case 104:
                    this.setupFailed = codedInputByteBufferNano.readBool();
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                    this.failureCause = codedInputByteBufferNano.readInt32();
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                    this.suggestedRetryMillis = codedInputByteBufferNano.readInt32();
                    break;
                case 128:
                    this.deactivateReason = codedInputByteBufferNano.readInt32();
                    break;
                case NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT /* 136 */:
                    this.durationMinutes = codedInputByteBufferNano.readInt64();
                    break;
                case 144:
                    this.ongoing = codedInputByteBufferNano.readBool();
                    break;
                case 152:
                    this.bandAtEnd = codedInputByteBufferNano.readInt32();
                    break;
                case SmsMessage.MAX_USER_DATA_SEPTETS /* 160 */:
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, SmsMessage.MAX_USER_DATA_SEPTETS);
                    int[] iArr = this.handoverFailureCauses;
                    int length = iArr == null ? 0 : iArr.length;
                    int i = repeatedFieldArrayLength + length;
                    int[] iArr2 = new int[i];
                    if (length != 0) {
                        System.arraycopy(iArr, 0, iArr2, 0, length);
                    }
                    while (length < i - 1) {
                        iArr2[length] = codedInputByteBufferNano.readInt32();
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    iArr2[length] = codedInputByteBufferNano.readInt32();
                    this.handoverFailureCauses = iArr2;
                    break;
                case 162:
                    int pushLimit = codedInputByteBufferNano.pushLimit(codedInputByteBufferNano.readRawVarint32());
                    int position = codedInputByteBufferNano.getPosition();
                    int i2 = 0;
                    while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                        codedInputByteBufferNano.readInt32();
                        i2++;
                    }
                    codedInputByteBufferNano.rewindToPosition(position);
                    int[] iArr3 = this.handoverFailureCauses;
                    int length2 = iArr3 == null ? 0 : iArr3.length;
                    int i3 = i2 + length2;
                    int[] iArr4 = new int[i3];
                    if (length2 != 0) {
                        System.arraycopy(iArr3, 0, iArr4, 0, length2);
                    }
                    while (length2 < i3) {
                        iArr4[length2] = codedInputByteBufferNano.readInt32();
                        length2++;
                    }
                    this.handoverFailureCauses = iArr4;
                    codedInputByteBufferNano.popLimit(pushLimit);
                    break;
                case 168:
                    int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 168);
                    int[] iArr5 = this.handoverFailureRat;
                    int length3 = iArr5 == null ? 0 : iArr5.length;
                    int i4 = repeatedFieldArrayLength2 + length3;
                    int[] iArr6 = new int[i4];
                    if (length3 != 0) {
                        System.arraycopy(iArr5, 0, iArr6, 0, length3);
                    }
                    while (length3 < i4 - 1) {
                        iArr6[length3] = codedInputByteBufferNano.readInt32();
                        codedInputByteBufferNano.readTag();
                        length3++;
                    }
                    iArr6[length3] = codedInputByteBufferNano.readInt32();
                    this.handoverFailureRat = iArr6;
                    break;
                case 170:
                    int pushLimit2 = codedInputByteBufferNano.pushLimit(codedInputByteBufferNano.readRawVarint32());
                    int position2 = codedInputByteBufferNano.getPosition();
                    int i5 = 0;
                    while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                        codedInputByteBufferNano.readInt32();
                        i5++;
                    }
                    codedInputByteBufferNano.rewindToPosition(position2);
                    int[] iArr7 = this.handoverFailureRat;
                    int length4 = iArr7 == null ? 0 : iArr7.length;
                    int i6 = i5 + length4;
                    int[] iArr8 = new int[i6];
                    if (length4 != 0) {
                        System.arraycopy(iArr7, 0, iArr8, 0, length4);
                    }
                    while (length4 < i6) {
                        iArr8[length4] = codedInputByteBufferNano.readInt32();
                        length4++;
                    }
                    this.handoverFailureRat = iArr8;
                    codedInputByteBufferNano.popLimit(pushLimit2);
                    break;
                case 176:
                    this.isNonDds = codedInputByteBufferNano.readBool();
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

    public static PersistAtomsProto$DataCallSession parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$DataCallSession) MessageNano.mergeFrom(new PersistAtomsProto$DataCallSession(), bArr);
    }

    public static PersistAtomsProto$DataCallSession parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$DataCallSession().mergeFrom(codedInputByteBufferNano);
    }
}
