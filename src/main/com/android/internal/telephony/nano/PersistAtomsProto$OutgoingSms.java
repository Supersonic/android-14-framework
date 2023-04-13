package com.android.internal.telephony.nano;

import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.util.NetworkStackConstants;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$OutgoingSms extends ExtendableMessageNano<PersistAtomsProto$OutgoingSms> {
    private static volatile PersistAtomsProto$OutgoingSms[] _emptyArray;
    public int carrierId;
    public int count;
    public int errorCode;
    public int hashCode;
    public long intervalMillis;
    public boolean isEsim;
    public boolean isFromDefaultApp;
    public boolean isManagedProfile;
    public boolean isMultiSim;
    public boolean isRoaming;
    public long messageId;
    public int networkErrorCode;
    public int rat;
    public int retryId;
    public int sendErrorCode;
    public int sendResult;
    public int simSlotIndex;
    public int smsFormat;
    public int smsTech;

    public static PersistAtomsProto$OutgoingSms[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$OutgoingSms[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$OutgoingSms() {
        clear();
    }

    public PersistAtomsProto$OutgoingSms clear() {
        this.smsFormat = 0;
        this.smsTech = 0;
        this.rat = 0;
        this.sendResult = 0;
        this.errorCode = 0;
        this.isRoaming = false;
        this.isFromDefaultApp = false;
        this.simSlotIndex = 0;
        this.isMultiSim = false;
        this.isEsim = false;
        this.carrierId = 0;
        this.messageId = 0L;
        this.retryId = 0;
        this.intervalMillis = 0L;
        this.count = 0;
        this.sendErrorCode = 0;
        this.networkErrorCode = 0;
        this.isManagedProfile = false;
        this.hashCode = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.smsFormat;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.smsTech;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.rat;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        int i4 = this.sendResult;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
        }
        int i5 = this.errorCode;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i5);
        }
        boolean z = this.isRoaming;
        if (z) {
            codedOutputByteBufferNano.writeBool(6, z);
        }
        boolean z2 = this.isFromDefaultApp;
        if (z2) {
            codedOutputByteBufferNano.writeBool(7, z2);
        }
        int i6 = this.simSlotIndex;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(8, i6);
        }
        boolean z3 = this.isMultiSim;
        if (z3) {
            codedOutputByteBufferNano.writeBool(9, z3);
        }
        boolean z4 = this.isEsim;
        if (z4) {
            codedOutputByteBufferNano.writeBool(10, z4);
        }
        int i7 = this.carrierId;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(11, i7);
        }
        long j = this.messageId;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(12, j);
        }
        int i8 = this.retryId;
        if (i8 != 0) {
            codedOutputByteBufferNano.writeInt32(13, i8);
        }
        long j2 = this.intervalMillis;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeInt64(14, j2);
        }
        int i9 = this.count;
        if (i9 != 0) {
            codedOutputByteBufferNano.writeInt32(15, i9);
        }
        int i10 = this.sendErrorCode;
        if (i10 != 0) {
            codedOutputByteBufferNano.writeInt32(16, i10);
        }
        int i11 = this.networkErrorCode;
        if (i11 != 0) {
            codedOutputByteBufferNano.writeInt32(17, i11);
        }
        boolean z5 = this.isManagedProfile;
        if (z5) {
            codedOutputByteBufferNano.writeBool(18, z5);
        }
        int i12 = this.hashCode;
        if (i12 != 0) {
            codedOutputByteBufferNano.writeInt32(10001, i12);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.smsFormat;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.smsTech;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.rat;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        int i4 = this.sendResult;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
        }
        int i5 = this.errorCode;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
        }
        boolean z = this.isRoaming;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(6, z);
        }
        boolean z2 = this.isFromDefaultApp;
        if (z2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z2);
        }
        int i6 = this.simSlotIndex;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i6);
        }
        boolean z3 = this.isMultiSim;
        if (z3) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(9, z3);
        }
        boolean z4 = this.isEsim;
        if (z4) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(10, z4);
        }
        int i7 = this.carrierId;
        if (i7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i7);
        }
        long j = this.messageId;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(12, j);
        }
        int i8 = this.retryId;
        if (i8 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, i8);
        }
        long j2 = this.intervalMillis;
        if (j2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(14, j2);
        }
        int i9 = this.count;
        if (i9 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, i9);
        }
        int i10 = this.sendErrorCode;
        if (i10 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(16, i10);
        }
        int i11 = this.networkErrorCode;
        if (i11 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(17, i11);
        }
        boolean z5 = this.isManagedProfile;
        if (z5) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(18, z5);
        }
        int i12 = this.hashCode;
        return i12 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(10001, i12) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$OutgoingSms mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 8:
                    this.smsFormat = codedInputByteBufferNano.readInt32();
                    break;
                case 16:
                    this.smsTech = codedInputByteBufferNano.readInt32();
                    break;
                case 24:
                    this.rat = codedInputByteBufferNano.readInt32();
                    break;
                case 32:
                    this.sendResult = codedInputByteBufferNano.readInt32();
                    break;
                case 40:
                    this.errorCode = codedInputByteBufferNano.readInt32();
                    break;
                case 48:
                    this.isRoaming = codedInputByteBufferNano.readBool();
                    break;
                case 56:
                    this.isFromDefaultApp = codedInputByteBufferNano.readBool();
                    break;
                case 64:
                    this.simSlotIndex = codedInputByteBufferNano.readInt32();
                    break;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    this.isMultiSim = codedInputByteBufferNano.readBool();
                    break;
                case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                    this.isEsim = codedInputByteBufferNano.readBool();
                    break;
                case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                    this.carrierId = codedInputByteBufferNano.readInt32();
                    break;
                case 96:
                    this.messageId = codedInputByteBufferNano.readInt64();
                    break;
                case 104:
                    this.retryId = codedInputByteBufferNano.readInt32();
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                    this.intervalMillis = codedInputByteBufferNano.readInt64();
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                    this.count = codedInputByteBufferNano.readInt32();
                    break;
                case 128:
                    this.sendErrorCode = codedInputByteBufferNano.readInt32();
                    break;
                case NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT /* 136 */:
                    this.networkErrorCode = codedInputByteBufferNano.readInt32();
                    break;
                case 144:
                    this.isManagedProfile = codedInputByteBufferNano.readBool();
                    break;
                case 80008:
                    this.hashCode = codedInputByteBufferNano.readInt32();
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

    public static PersistAtomsProto$OutgoingSms parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$OutgoingSms) MessageNano.mergeFrom(new PersistAtomsProto$OutgoingSms(), bArr);
    }

    public static PersistAtomsProto$OutgoingSms parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$OutgoingSms().mergeFrom(codedInputByteBufferNano);
    }
}
