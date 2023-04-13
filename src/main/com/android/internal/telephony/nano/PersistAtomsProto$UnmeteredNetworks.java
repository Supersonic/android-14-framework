package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$UnmeteredNetworks extends ExtendableMessageNano<PersistAtomsProto$UnmeteredNetworks> {
    private static volatile PersistAtomsProto$UnmeteredNetworks[] _emptyArray;
    public int carrierId;
    public int phoneId;
    public long unmeteredNetworksBitmask;

    public static PersistAtomsProto$UnmeteredNetworks[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$UnmeteredNetworks[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$UnmeteredNetworks() {
        clear();
    }

    public PersistAtomsProto$UnmeteredNetworks clear() {
        this.phoneId = 0;
        this.carrierId = 0;
        this.unmeteredNetworksBitmask = 0L;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.phoneId;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.carrierId;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        long j = this.unmeteredNetworksBitmask;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(3, j);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.phoneId;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.carrierId;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        long j = this.unmeteredNetworksBitmask;
        return j != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(3, j) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$UnmeteredNetworks mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.phoneId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.carrierId = codedInputByteBufferNano.readInt32();
            } else if (readTag != 24) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.unmeteredNetworksBitmask = codedInputByteBufferNano.readInt64();
            }
        }
    }

    public static PersistAtomsProto$UnmeteredNetworks parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$UnmeteredNetworks) MessageNano.mergeFrom(new PersistAtomsProto$UnmeteredNetworks(), bArr);
    }

    public static PersistAtomsProto$UnmeteredNetworks parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$UnmeteredNetworks().mergeFrom(codedInputByteBufferNano);
    }
}
