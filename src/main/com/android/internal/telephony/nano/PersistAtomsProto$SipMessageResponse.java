package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$SipMessageResponse extends ExtendableMessageNano<PersistAtomsProto$SipMessageResponse> {
    private static volatile PersistAtomsProto$SipMessageResponse[] _emptyArray;
    public int carrierId;
    public int count;
    public int messageError;
    public int sipMessageDirection;
    public int sipMessageMethod;
    public int sipMessageResponse;
    public int slotId;

    public static PersistAtomsProto$SipMessageResponse[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$SipMessageResponse[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$SipMessageResponse() {
        clear();
    }

    public PersistAtomsProto$SipMessageResponse clear() {
        this.carrierId = 0;
        this.slotId = 0;
        this.sipMessageMethod = 0;
        this.sipMessageResponse = 0;
        this.sipMessageDirection = 0;
        this.messageError = 0;
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
        int i3 = this.sipMessageMethod;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        int i4 = this.sipMessageResponse;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
        }
        int i5 = this.sipMessageDirection;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i5);
        }
        int i6 = this.messageError;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i6);
        }
        int i7 = this.count;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(7, i7);
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
        int i3 = this.sipMessageMethod;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        int i4 = this.sipMessageResponse;
        if (i4 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
        }
        int i5 = this.sipMessageDirection;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
        }
        int i6 = this.messageError;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i6);
        }
        int i7 = this.count;
        return i7 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(7, i7) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$SipMessageResponse mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.sipMessageMethod = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                this.sipMessageResponse = codedInputByteBufferNano.readInt32();
            } else if (readTag == 40) {
                this.sipMessageDirection = codedInputByteBufferNano.readInt32();
            } else if (readTag == 48) {
                this.messageError = codedInputByteBufferNano.readInt32();
            } else if (readTag != 56) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.count = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static PersistAtomsProto$SipMessageResponse parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$SipMessageResponse) MessageNano.mergeFrom(new PersistAtomsProto$SipMessageResponse(), bArr);
    }

    public static PersistAtomsProto$SipMessageResponse parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$SipMessageResponse().mergeFrom(codedInputByteBufferNano);
    }
}
