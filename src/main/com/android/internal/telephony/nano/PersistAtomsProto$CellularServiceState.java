package com.android.internal.telephony.nano;

import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$CellularServiceState extends ExtendableMessageNano<PersistAtomsProto$CellularServiceState> {
    private static volatile PersistAtomsProto$CellularServiceState[] _emptyArray;
    public int carrierId;
    public int dataRat;
    public int dataRoamingType;
    public boolean isEmergencyOnly;
    public boolean isEndc;
    public boolean isInternetPdnUp;
    public boolean isMultiSim;
    public long lastUsedMillis;
    public int simSlotIndex;
    public long totalTimeMillis;
    public int voiceRat;
    public int voiceRoamingType;

    public static PersistAtomsProto$CellularServiceState[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$CellularServiceState[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$CellularServiceState() {
        clear();
    }

    public PersistAtomsProto$CellularServiceState clear() {
        this.voiceRat = 0;
        this.dataRat = 0;
        this.voiceRoamingType = 0;
        this.dataRoamingType = 0;
        this.isEndc = false;
        this.simSlotIndex = 0;
        this.isMultiSim = false;
        this.carrierId = 0;
        this.totalTimeMillis = 0L;
        this.isEmergencyOnly = false;
        this.isInternetPdnUp = false;
        this.lastUsedMillis = 0L;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.voiceRat;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.dataRat;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.voiceRoamingType;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        int i4 = this.dataRoamingType;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
        }
        boolean z = this.isEndc;
        if (z) {
            codedOutputByteBufferNano.writeBool(5, z);
        }
        int i5 = this.simSlotIndex;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i5);
        }
        boolean z2 = this.isMultiSim;
        if (z2) {
            codedOutputByteBufferNano.writeBool(7, z2);
        }
        int i6 = this.carrierId;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(8, i6);
        }
        long j = this.totalTimeMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(9, j);
        }
        boolean z3 = this.isEmergencyOnly;
        if (z3) {
            codedOutputByteBufferNano.writeBool(10, z3);
        }
        boolean z4 = this.isInternetPdnUp;
        if (z4) {
            codedOutputByteBufferNano.writeBool(11, z4);
        }
        long j2 = this.lastUsedMillis;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeInt64(10001, j2);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.voiceRat;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.dataRat;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.voiceRoamingType;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        int i4 = this.dataRoamingType;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
        }
        boolean z = this.isEndc;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z);
        }
        int i5 = this.simSlotIndex;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i5);
        }
        boolean z2 = this.isMultiSim;
        if (z2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z2);
        }
        int i6 = this.carrierId;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i6);
        }
        long j = this.totalTimeMillis;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(9, j);
        }
        boolean z3 = this.isEmergencyOnly;
        if (z3) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(10, z3);
        }
        boolean z4 = this.isInternetPdnUp;
        if (z4) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(11, z4);
        }
        long j2 = this.lastUsedMillis;
        return j2 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(10001, j2) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$CellularServiceState mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 8:
                    this.voiceRat = codedInputByteBufferNano.readInt32();
                    break;
                case 16:
                    this.dataRat = codedInputByteBufferNano.readInt32();
                    break;
                case 24:
                    this.voiceRoamingType = codedInputByteBufferNano.readInt32();
                    break;
                case 32:
                    this.dataRoamingType = codedInputByteBufferNano.readInt32();
                    break;
                case 40:
                    this.isEndc = codedInputByteBufferNano.readBool();
                    break;
                case 48:
                    this.simSlotIndex = codedInputByteBufferNano.readInt32();
                    break;
                case 56:
                    this.isMultiSim = codedInputByteBufferNano.readBool();
                    break;
                case 64:
                    this.carrierId = codedInputByteBufferNano.readInt32();
                    break;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    this.totalTimeMillis = codedInputByteBufferNano.readInt64();
                    break;
                case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                    this.isEmergencyOnly = codedInputByteBufferNano.readBool();
                    break;
                case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                    this.isInternetPdnUp = codedInputByteBufferNano.readBool();
                    break;
                case 80008:
                    this.lastUsedMillis = codedInputByteBufferNano.readInt64();
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

    public static PersistAtomsProto$CellularServiceState parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$CellularServiceState) MessageNano.mergeFrom(new PersistAtomsProto$CellularServiceState(), bArr);
    }

    public static PersistAtomsProto$CellularServiceState parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$CellularServiceState().mergeFrom(codedInputByteBufferNano);
    }
}
