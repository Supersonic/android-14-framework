package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class PersistAtomsProto$OutgoingShortCodeSms extends ExtendableMessageNano<PersistAtomsProto$OutgoingShortCodeSms> {
    private static volatile PersistAtomsProto$OutgoingShortCodeSms[] _emptyArray;
    public int category;
    public int shortCodeSmsCount;
    public int xmlVersion;

    public static PersistAtomsProto$OutgoingShortCodeSms[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new PersistAtomsProto$OutgoingShortCodeSms[0];
                }
            }
        }
        return _emptyArray;
    }

    public PersistAtomsProto$OutgoingShortCodeSms() {
        clear();
    }

    public PersistAtomsProto$OutgoingShortCodeSms clear() {
        this.category = 0;
        this.xmlVersion = 0;
        this.shortCodeSmsCount = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.category;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.xmlVersion;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.shortCodeSmsCount;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.category;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.xmlVersion;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.shortCodeSmsCount;
        return i3 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i3) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public PersistAtomsProto$OutgoingShortCodeSms mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.category = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.xmlVersion = codedInputByteBufferNano.readInt32();
            } else if (readTag != 24) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.shortCodeSmsCount = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static PersistAtomsProto$OutgoingShortCodeSms parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (PersistAtomsProto$OutgoingShortCodeSms) MessageNano.mergeFrom(new PersistAtomsProto$OutgoingShortCodeSms(), bArr);
    }

    public static PersistAtomsProto$OutgoingShortCodeSms parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new PersistAtomsProto$OutgoingShortCodeSms().mergeFrom(codedInputByteBufferNano);
    }
}
