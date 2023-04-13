package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$NetworkRequestsV2 extends ExtendableMessageNano<PersistAtomsProto$NetworkRequestsV2> {
    private static volatile PersistAtomsProto$NetworkRequestsV2[] _emptyArray;
    public int capability;
    public int carrierId;
    public int requestCount;

    /* loaded from: classes.dex */
    public interface NetworkCapability {
        public static final int CBS = 3;
        public static final int ENTERPRISE = 4;
        public static final int PRIORITIZE_BANDWIDTH = 2;
        public static final int PRIORITIZE_LATENCY = 1;
        public static final int UNKNOWN = 0;
    }

    public static PersistAtomsProto$NetworkRequestsV2[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$NetworkRequestsV2[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$NetworkRequestsV2() {
        clear();
    }

    public PersistAtomsProto$NetworkRequestsV2 clear() {
        this.carrierId = 0;
        this.capability = 0;
        this.requestCount = 0;
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
        int i2 = this.capability;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.requestCount;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
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
        int i2 = this.capability;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.requestCount;
        return i3 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i3) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$NetworkRequestsV2 mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.carrierId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                int readInt32 = codedInputByteBufferNano.readInt32();
                if (readInt32 == 0 || readInt32 == 1 || readInt32 == 2 || readInt32 == 3 || readInt32 == 4) {
                    this.capability = readInt32;
                }
            } else if (readTag != 24) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.requestCount = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static PersistAtomsProto$NetworkRequestsV2 parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$NetworkRequestsV2) MessageNano.mergeFrom(new PersistAtomsProto$NetworkRequestsV2(), bArr);
    }

    public static PersistAtomsProto$NetworkRequestsV2 parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$NetworkRequestsV2().mergeFrom(codedInputByteBufferNano);
    }
}
