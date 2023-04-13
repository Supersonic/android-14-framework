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
public final class PersistAtomsProto$ImsRegistrationStats extends ExtendableMessageNano<PersistAtomsProto$ImsRegistrationStats> {
    private static volatile PersistAtomsProto$ImsRegistrationStats[] _emptyArray;
    public int carrierId;
    public long lastUsedMillis;
    public int rat;
    public long registeredMillis;
    public int simSlotIndex;
    public long smsAvailableMillis;
    public long smsCapableMillis;
    public long utAvailableMillis;
    public long utCapableMillis;
    public long videoAvailableMillis;
    public long videoCapableMillis;
    public long voiceAvailableMillis;
    public long voiceCapableMillis;

    public static PersistAtomsProto$ImsRegistrationStats[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$ImsRegistrationStats[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$ImsRegistrationStats() {
        clear();
    }

    public PersistAtomsProto$ImsRegistrationStats clear() {
        this.carrierId = 0;
        this.simSlotIndex = 0;
        this.rat = 0;
        this.registeredMillis = 0L;
        this.voiceCapableMillis = 0L;
        this.voiceAvailableMillis = 0L;
        this.smsCapableMillis = 0L;
        this.smsAvailableMillis = 0L;
        this.videoCapableMillis = 0L;
        this.videoAvailableMillis = 0L;
        this.utCapableMillis = 0L;
        this.utAvailableMillis = 0L;
        this.lastUsedMillis = 0L;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.carrierId;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.simSlotIndex;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.rat;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        long j = this.registeredMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(4, j);
        }
        long j2 = this.voiceCapableMillis;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeInt64(5, j2);
        }
        long j3 = this.voiceAvailableMillis;
        if (j3 != 0) {
            codedOutputByteBufferNano.writeInt64(6, j3);
        }
        long j4 = this.smsCapableMillis;
        if (j4 != 0) {
            codedOutputByteBufferNano.writeInt64(7, j4);
        }
        long j5 = this.smsAvailableMillis;
        if (j5 != 0) {
            codedOutputByteBufferNano.writeInt64(8, j5);
        }
        long j6 = this.videoCapableMillis;
        if (j6 != 0) {
            codedOutputByteBufferNano.writeInt64(9, j6);
        }
        long j7 = this.videoAvailableMillis;
        if (j7 != 0) {
            codedOutputByteBufferNano.writeInt64(10, j7);
        }
        long j8 = this.utCapableMillis;
        if (j8 != 0) {
            codedOutputByteBufferNano.writeInt64(11, j8);
        }
        long j9 = this.utAvailableMillis;
        if (j9 != 0) {
            codedOutputByteBufferNano.writeInt64(12, j9);
        }
        long j10 = this.lastUsedMillis;
        if (j10 != 0) {
            codedOutputByteBufferNano.writeInt64(10001, j10);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.carrierId;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.simSlotIndex;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.rat;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        long j = this.registeredMillis;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(4, j);
        }
        long j2 = this.voiceCapableMillis;
        if (j2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(5, j2);
        }
        long j3 = this.voiceAvailableMillis;
        if (j3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(6, j3);
        }
        long j4 = this.smsCapableMillis;
        if (j4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(7, j4);
        }
        long j5 = this.smsAvailableMillis;
        if (j5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(8, j5);
        }
        long j6 = this.videoCapableMillis;
        if (j6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(9, j6);
        }
        long j7 = this.videoAvailableMillis;
        if (j7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(10, j7);
        }
        long j8 = this.utCapableMillis;
        if (j8 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(11, j8);
        }
        long j9 = this.utAvailableMillis;
        if (j9 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(12, j9);
        }
        long j10 = this.lastUsedMillis;
        return j10 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(10001, j10) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$ImsRegistrationStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 8:
                    this.carrierId = codedInputByteBufferNano.readInt32();
                    break;
                case 16:
                    this.simSlotIndex = codedInputByteBufferNano.readInt32();
                    break;
                case 24:
                    this.rat = codedInputByteBufferNano.readInt32();
                    break;
                case 32:
                    this.registeredMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 40:
                    this.voiceCapableMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 48:
                    this.voiceAvailableMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 56:
                    this.smsCapableMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 64:
                    this.smsAvailableMillis = codedInputByteBufferNano.readInt64();
                    break;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    this.videoCapableMillis = codedInputByteBufferNano.readInt64();
                    break;
                case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                    this.videoAvailableMillis = codedInputByteBufferNano.readInt64();
                    break;
                case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                    this.utCapableMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 96:
                    this.utAvailableMillis = codedInputByteBufferNano.readInt64();
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

    public static PersistAtomsProto$ImsRegistrationStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$ImsRegistrationStats) MessageNano.mergeFrom(new PersistAtomsProto$ImsRegistrationStats(), bArr);
    }

    public static PersistAtomsProto$ImsRegistrationStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$ImsRegistrationStats().mergeFrom(codedInputByteBufferNano);
    }
}
