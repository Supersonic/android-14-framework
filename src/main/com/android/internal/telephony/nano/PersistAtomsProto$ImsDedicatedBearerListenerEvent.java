package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$ImsDedicatedBearerListenerEvent extends ExtendableMessageNano<PersistAtomsProto$ImsDedicatedBearerListenerEvent> {
    private static volatile PersistAtomsProto$ImsDedicatedBearerListenerEvent[] _emptyArray;
    public int carrierId;
    public boolean dedicatedBearerEstablished;
    public int eventCount;
    public int qci;
    public int ratAtEnd;
    public int slotId;

    public static PersistAtomsProto$ImsDedicatedBearerListenerEvent[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$ImsDedicatedBearerListenerEvent[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$ImsDedicatedBearerListenerEvent() {
        clear();
    }

    public PersistAtomsProto$ImsDedicatedBearerListenerEvent clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.ratAtEnd = 0;
        this.qci = 0;
        this.dedicatedBearerEstablished = false;
        this.eventCount = 0;
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
        boolean z = this.dedicatedBearerEstablished;
        if (z) {
            codedOutputByteBufferNano.writeBool(5, z);
        }
        int i5 = this.eventCount;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i5);
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
        boolean z = this.dedicatedBearerEstablished;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z);
        }
        int i5 = this.eventCount;
        return i5 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(6, i5) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$ImsDedicatedBearerListenerEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.dedicatedBearerEstablished = codedInputByteBufferNano.readBool();
            } else if (readTag != 48) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.eventCount = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static PersistAtomsProto$ImsDedicatedBearerListenerEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$ImsDedicatedBearerListenerEvent) MessageNano.mergeFrom(new PersistAtomsProto$ImsDedicatedBearerListenerEvent(), bArr);
    }

    public static PersistAtomsProto$ImsDedicatedBearerListenerEvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$ImsDedicatedBearerListenerEvent().mergeFrom(codedInputByteBufferNano);
    }
}
