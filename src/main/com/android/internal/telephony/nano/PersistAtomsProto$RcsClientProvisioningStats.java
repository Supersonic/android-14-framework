package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$RcsClientProvisioningStats extends ExtendableMessageNano<PersistAtomsProto$RcsClientProvisioningStats> {
    private static volatile PersistAtomsProto$RcsClientProvisioningStats[] _emptyArray;
    public int carrierId;
    public int count;
    public int event;
    public int slotId;

    public static PersistAtomsProto$RcsClientProvisioningStats[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$RcsClientProvisioningStats[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$RcsClientProvisioningStats() {
        clear();
    }

    public PersistAtomsProto$RcsClientProvisioningStats clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.event = 0;
        this.count = 0;
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
        int i3 = this.event;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        int i4 = this.count;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
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
        int i3 = this.event;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        int i4 = this.count;
        return i4 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(4, i4) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$RcsClientProvisioningStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.event = codedInputByteBufferNano.readInt32();
            } else if (readTag != 32) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.count = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static PersistAtomsProto$RcsClientProvisioningStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$RcsClientProvisioningStats) MessageNano.mergeFrom(new PersistAtomsProto$RcsClientProvisioningStats(), bArr);
    }

    public static PersistAtomsProto$RcsClientProvisioningStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$RcsClientProvisioningStats().mergeFrom(codedInputByteBufferNano);
    }
}
