package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class TelephonyProto$Time extends ExtendableMessageNano<TelephonyProto$Time> {
    private static volatile TelephonyProto$Time[] _emptyArray;
    public long elapsedTimestampMillis;
    public long systemTimestampMillis;

    public static TelephonyProto$Time[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$Time[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$Time() {
        clear();
    }

    public TelephonyProto$Time clear() {
        this.systemTimestampMillis = 0L;
        this.elapsedTimestampMillis = 0L;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        long j = this.systemTimestampMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(1, j);
        }
        long j2 = this.elapsedTimestampMillis;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeInt64(2, j2);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        long j = this.systemTimestampMillis;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, j);
        }
        long j2 = this.elapsedTimestampMillis;
        return j2 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(2, j2) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$Time mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.systemTimestampMillis = codedInputByteBufferNano.readInt64();
            } else if (readTag != 16) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.elapsedTimestampMillis = codedInputByteBufferNano.readInt64();
            }
        }
    }

    public static TelephonyProto$Time parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$Time) MessageNano.mergeFrom(new TelephonyProto$Time(), bArr);
    }

    public static TelephonyProto$Time parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$Time().mergeFrom(codedInputByteBufferNano);
    }
}
