package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$ImsDedicatedBearerEvent extends ExtendableMessageNano<PersistAtomsProto$ImsDedicatedBearerEvent> {
    private static volatile PersistAtomsProto$ImsDedicatedBearerEvent[] _emptyArray;
    public int bearerState;
    public int carrierId;
    public int count;
    public boolean hasListeners;
    public boolean localConnectionInfoReceived;
    public int qci;
    public int ratAtEnd;
    public boolean remoteConnectionInfoReceived;
    public int slotId;

    public static PersistAtomsProto$ImsDedicatedBearerEvent[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$ImsDedicatedBearerEvent[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$ImsDedicatedBearerEvent() {
        clear();
    }

    public PersistAtomsProto$ImsDedicatedBearerEvent clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.ratAtEnd = 0;
        this.qci = 0;
        this.bearerState = 0;
        this.localConnectionInfoReceived = false;
        this.remoteConnectionInfoReceived = false;
        this.hasListeners = false;
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
        int i3 = this.ratAtEnd;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        int i4 = this.qci;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
        }
        int i5 = this.bearerState;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i5);
        }
        boolean z = this.localConnectionInfoReceived;
        if (z) {
            codedOutputByteBufferNano.writeBool(6, z);
        }
        boolean z2 = this.remoteConnectionInfoReceived;
        if (z2) {
            codedOutputByteBufferNano.writeBool(7, z2);
        }
        boolean z3 = this.hasListeners;
        if (z3) {
            codedOutputByteBufferNano.writeBool(8, z3);
        }
        int i6 = this.count;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(9, i6);
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
        int i3 = this.ratAtEnd;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        int i4 = this.qci;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
        }
        int i5 = this.bearerState;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
        }
        boolean z = this.localConnectionInfoReceived;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(6, z);
        }
        boolean z2 = this.remoteConnectionInfoReceived;
        if (z2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z2);
        }
        boolean z3 = this.hasListeners;
        if (z3) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(8, z3);
        }
        int i6 = this.count;
        return i6 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(9, i6) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$ImsDedicatedBearerEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.ratAtEnd = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                this.qci = codedInputByteBufferNano.readInt32();
            } else if (readTag == 40) {
                this.bearerState = codedInputByteBufferNano.readInt32();
            } else if (readTag == 48) {
                this.localConnectionInfoReceived = codedInputByteBufferNano.readBool();
            } else if (readTag == 56) {
                this.remoteConnectionInfoReceived = codedInputByteBufferNano.readBool();
            } else if (readTag == 64) {
                this.hasListeners = codedInputByteBufferNano.readBool();
            } else if (readTag != 72) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.count = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static PersistAtomsProto$ImsDedicatedBearerEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$ImsDedicatedBearerEvent) MessageNano.mergeFrom(new PersistAtomsProto$ImsDedicatedBearerEvent(), bArr);
    }

    public static PersistAtomsProto$ImsDedicatedBearerEvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$ImsDedicatedBearerEvent().mergeFrom(codedInputByteBufferNano);
    }
}
