package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$RcsAcsProvisioningStats extends ExtendableMessageNano<PersistAtomsProto$RcsAcsProvisioningStats> {
    private static volatile PersistAtomsProto$RcsAcsProvisioningStats[] _emptyArray;
    public int carrierId;
    public int count;
    public boolean isSingleRegistrationEnabled;
    public int responseCode;
    public int responseType;
    public int slotId;
    public long stateTimerMillis;

    public static PersistAtomsProto$RcsAcsProvisioningStats[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$RcsAcsProvisioningStats[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$RcsAcsProvisioningStats() {
        clear();
    }

    public PersistAtomsProto$RcsAcsProvisioningStats clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.responseCode = 0;
        this.responseType = 0;
        this.isSingleRegistrationEnabled = false;
        this.count = 0;
        this.stateTimerMillis = 0L;
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
        int i2 = this.slotId;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.responseCode;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        int i4 = this.responseType;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
        }
        boolean z = this.isSingleRegistrationEnabled;
        if (z) {
            codedOutputByteBufferNano.writeBool(5, z);
        }
        int i5 = this.count;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i5);
        }
        long j = this.stateTimerMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(7, j);
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
        int i2 = this.slotId;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.responseCode;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        int i4 = this.responseType;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
        }
        boolean z = this.isSingleRegistrationEnabled;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z);
        }
        int i5 = this.count;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i5);
        }
        long j = this.stateTimerMillis;
        return j != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(7, j) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$RcsAcsProvisioningStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.carrierId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.slotId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 24) {
                this.responseCode = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                this.responseType = codedInputByteBufferNano.readInt32();
            } else if (readTag == 40) {
                this.isSingleRegistrationEnabled = codedInputByteBufferNano.readBool();
            } else if (readTag == 48) {
                this.count = codedInputByteBufferNano.readInt32();
            } else if (readTag != 56) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.stateTimerMillis = codedInputByteBufferNano.readInt64();
            }
        }
    }

    public static PersistAtomsProto$RcsAcsProvisioningStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$RcsAcsProvisioningStats) MessageNano.mergeFrom(new PersistAtomsProto$RcsAcsProvisioningStats(), bArr);
    }

    public static PersistAtomsProto$RcsAcsProvisioningStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$RcsAcsProvisioningStats().mergeFrom(codedInputByteBufferNano);
    }
}
