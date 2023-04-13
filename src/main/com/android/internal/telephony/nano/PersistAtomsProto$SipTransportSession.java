package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$SipTransportSession extends ExtendableMessageNano<PersistAtomsProto$SipTransportSession> {
    private static volatile PersistAtomsProto$SipTransportSession[] _emptyArray;
    public int carrierId;
    public int endedGracefullyCount;
    public boolean isEndedGracefully;
    public int sessionCount;
    public int sessionMethod;
    public int sipMessageDirection;
    public int sipResponse;
    public int slotId;

    public static PersistAtomsProto$SipTransportSession[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$SipTransportSession[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$SipTransportSession() {
        clear();
    }

    public PersistAtomsProto$SipTransportSession clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.sessionMethod = 0;
        this.sipMessageDirection = 0;
        this.sipResponse = 0;
        this.sessionCount = 0;
        this.endedGracefullyCount = 0;
        this.isEndedGracefully = false;
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
        int i3 = this.sessionMethod;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        int i4 = this.sipMessageDirection;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
        }
        int i5 = this.sipResponse;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i5);
        }
        int i6 = this.sessionCount;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i6);
        }
        int i7 = this.endedGracefullyCount;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(7, i7);
        }
        boolean z = this.isEndedGracefully;
        if (z) {
            codedOutputByteBufferNano.writeBool(10001, z);
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
        int i3 = this.sessionMethod;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        int i4 = this.sipMessageDirection;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
        }
        int i5 = this.sipResponse;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
        }
        int i6 = this.sessionCount;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i6);
        }
        int i7 = this.endedGracefullyCount;
        if (i7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i7);
        }
        boolean z = this.isEndedGracefully;
        return z ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(10001, z) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$SipTransportSession mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.sessionMethod = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                this.sipMessageDirection = codedInputByteBufferNano.readInt32();
            } else if (readTag == 40) {
                this.sipResponse = codedInputByteBufferNano.readInt32();
            } else if (readTag == 48) {
                this.sessionCount = codedInputByteBufferNano.readInt32();
            } else if (readTag == 56) {
                this.endedGracefullyCount = codedInputByteBufferNano.readInt32();
            } else if (readTag != 80008) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.isEndedGracefully = codedInputByteBufferNano.readBool();
            }
        }
    }

    public static PersistAtomsProto$SipTransportSession parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$SipTransportSession) MessageNano.mergeFrom(new PersistAtomsProto$SipTransportSession(), bArr);
    }

    public static PersistAtomsProto$SipTransportSession parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$SipTransportSession().mergeFrom(codedInputByteBufferNano);
    }
}
