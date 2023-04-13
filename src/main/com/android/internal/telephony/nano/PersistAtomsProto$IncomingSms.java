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
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$IncomingSms extends ExtendableMessageNano<PersistAtomsProto$IncomingSms> {
    private static volatile PersistAtomsProto$IncomingSms[] _emptyArray;
    public boolean blocked;
    public int carrierId;
    public int count;
    public int error;
    public int hashCode;
    public boolean isEsim;
    public boolean isManagedProfile;
    public boolean isMultiSim;
    public boolean isRoaming;
    public long messageId;
    public int rat;
    public int receivedParts;
    public int simSlotIndex;
    public int smsFormat;
    public int smsTech;
    public int smsType;
    public int totalParts;

    public static PersistAtomsProto$IncomingSms[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$IncomingSms[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$IncomingSms() {
        clear();
    }

    public PersistAtomsProto$IncomingSms clear() {
        this.smsFormat = 0;
        this.smsTech = 0;
        this.rat = 0;
        this.smsType = 0;
        this.totalParts = 0;
        this.receivedParts = 0;
        this.blocked = false;
        this.error = 0;
        this.isRoaming = false;
        this.simSlotIndex = 0;
        this.isMultiSim = false;
        this.isEsim = false;
        this.carrierId = 0;
        this.messageId = 0L;
        this.count = 0;
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
        int i4 = this.smsType;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
        }
        int i5 = this.totalParts;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i5);
        }
        int i6 = this.receivedParts;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i6);
        }
        boolean z = this.blocked;
        if (z) {
            codedOutputByteBufferNano.writeBool(7, z);
        }
        int i7 = this.error;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(8, i7);
        }
        boolean z2 = this.isRoaming;
        if (z2) {
            codedOutputByteBufferNano.writeBool(9, z2);
        }
        int i8 = this.simSlotIndex;
        if (i8 != 0) {
            codedOutputByteBufferNano.writeInt32(10, i8);
        }
        boolean z3 = this.isMultiSim;
        if (z3) {
            codedOutputByteBufferNano.writeBool(11, z3);
        }
        boolean z4 = this.isEsim;
        if (z4) {
            codedOutputByteBufferNano.writeBool(12, z4);
        }
        int i9 = this.carrierId;
        if (i9 != 0) {
            codedOutputByteBufferNano.writeInt32(13, i9);
        }
        long j = this.messageId;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(14, j);
        }
        int i10 = this.count;
        if (i10 != 0) {
            codedOutputByteBufferNano.writeInt32(15, i10);
        }
        boolean z5 = this.isManagedProfile;
        if (z5) {
            codedOutputByteBufferNano.writeBool(16, z5);
        }
        int i11 = this.hashCode;
        if (i11 != 0) {
            codedOutputByteBufferNano.writeInt32(10001, i11);
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
        int i4 = this.smsType;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
        }
        int i5 = this.totalParts;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
        }
        int i6 = this.receivedParts;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i6);
        }
        boolean z = this.blocked;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z);
        }
        int i7 = this.error;
        if (i7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i7);
        }
        boolean z2 = this.isRoaming;
        if (z2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(9, z2);
        }
        int i8 = this.simSlotIndex;
        if (i8 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i8);
        }
        boolean z3 = this.isMultiSim;
        if (z3) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(11, z3);
        }
        boolean z4 = this.isEsim;
        if (z4) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(12, z4);
        }
        int i9 = this.carrierId;
        if (i9 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, i9);
        }
        long j = this.messageId;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(14, j);
        }
        int i10 = this.count;
        if (i10 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, i10);
        }
        boolean z5 = this.isManagedProfile;
        if (z5) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(16, z5);
        }
        int i11 = this.hashCode;
        return i11 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(10001, i11) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$IncomingSms mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                    this.smsType = codedInputByteBufferNano.readInt32();
                    break;
                case 40:
                    this.totalParts = codedInputByteBufferNano.readInt32();
                    break;
                case 48:
                    this.receivedParts = codedInputByteBufferNano.readInt32();
                    break;
                case 56:
                    this.blocked = codedInputByteBufferNano.readBool();
                    break;
                case 64:
                    this.error = codedInputByteBufferNano.readInt32();
                    break;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    this.isRoaming = codedInputByteBufferNano.readBool();
                    break;
                case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                    this.simSlotIndex = codedInputByteBufferNano.readInt32();
                    break;
                case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                    this.isMultiSim = codedInputByteBufferNano.readBool();
                    break;
                case 96:
                    this.isEsim = codedInputByteBufferNano.readBool();
                    break;
                case 104:
                    this.carrierId = codedInputByteBufferNano.readInt32();
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                    this.messageId = codedInputByteBufferNano.readInt64();
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                    this.count = codedInputByteBufferNano.readInt32();
                    break;
                case 128:
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

    public static PersistAtomsProto$IncomingSms parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$IncomingSms) MessageNano.mergeFrom(new PersistAtomsProto$IncomingSms(), bArr);
    }

    public static PersistAtomsProto$IncomingSms parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$IncomingSms().mergeFrom(codedInputByteBufferNano);
    }
}
