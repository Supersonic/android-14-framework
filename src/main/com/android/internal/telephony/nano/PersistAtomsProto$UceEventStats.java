package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$UceEventStats extends ExtendableMessageNano<PersistAtomsProto$UceEventStats> {
    private static volatile PersistAtomsProto$UceEventStats[] _emptyArray;
    public int carrierId;
    public int commandCode;
    public int count;
    public int networkResponse;
    public int slotId;
    public boolean successful;
    public int type;

    public static PersistAtomsProto$UceEventStats[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$UceEventStats[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$UceEventStats() {
        clear();
    }

    public PersistAtomsProto$UceEventStats clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.type = 0;
        this.successful = false;
        this.commandCode = 0;
        this.networkResponse = 0;
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
        int i3 = this.type;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        boolean z = this.successful;
        if (z) {
            codedOutputByteBufferNano.writeBool(4, z);
        }
        int i4 = this.commandCode;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i4);
        }
        int i5 = this.networkResponse;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i5);
        }
        int i6 = this.count;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(7, i6);
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
        int i3 = this.type;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        boolean z = this.successful;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, z);
        }
        int i4 = this.commandCode;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i4);
        }
        int i5 = this.networkResponse;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i5);
        }
        int i6 = this.count;
        return i6 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(7, i6) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$UceEventStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.type = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                this.successful = codedInputByteBufferNano.readBool();
            } else if (readTag == 40) {
                this.commandCode = codedInputByteBufferNano.readInt32();
            } else if (readTag == 48) {
                this.networkResponse = codedInputByteBufferNano.readInt32();
            } else if (readTag != 56) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.count = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static PersistAtomsProto$UceEventStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$UceEventStats) MessageNano.mergeFrom(new PersistAtomsProto$UceEventStats(), bArr);
    }

    public static PersistAtomsProto$UceEventStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$UceEventStats().mergeFrom(codedInputByteBufferNano);
    }
}
