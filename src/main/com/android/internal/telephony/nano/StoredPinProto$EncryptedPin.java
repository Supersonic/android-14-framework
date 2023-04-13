package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.protobuf.nano.WireFormatNano;
import java.io.IOException;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class StoredPinProto$EncryptedPin extends ExtendableMessageNano<StoredPinProto$EncryptedPin> {
    private static volatile StoredPinProto$EncryptedPin[] _emptyArray;
    public byte[] encryptedStoredPin;

    /* renamed from: iv */
    public byte[] f13iv;

    public static StoredPinProto$EncryptedPin[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new StoredPinProto$EncryptedPin[0];
                }
            }
        }
        return _emptyArray;
    }

    public StoredPinProto$EncryptedPin() {
        clear();
    }

    public StoredPinProto$EncryptedPin clear() {
        byte[] bArr = WireFormatNano.EMPTY_BYTES;
        this.encryptedStoredPin = bArr;
        this.f13iv = bArr;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        byte[] bArr = this.encryptedStoredPin;
        byte[] bArr2 = WireFormatNano.EMPTY_BYTES;
        if (!Arrays.equals(bArr, bArr2)) {
            codedOutputByteBufferNano.writeBytes(1, this.encryptedStoredPin);
        }
        if (!Arrays.equals(this.f13iv, bArr2)) {
            codedOutputByteBufferNano.writeBytes(2, this.f13iv);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        byte[] bArr = this.encryptedStoredPin;
        byte[] bArr2 = WireFormatNano.EMPTY_BYTES;
        if (!Arrays.equals(bArr, bArr2)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBytesSize(1, this.encryptedStoredPin);
        }
        return !Arrays.equals(this.f13iv, bArr2) ? computeSerializedSize + CodedOutputByteBufferNano.computeBytesSize(2, this.f13iv) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public StoredPinProto$EncryptedPin mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                this.encryptedStoredPin = codedInputByteBufferNano.readBytes();
            } else if (readTag != 18) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.f13iv = codedInputByteBufferNano.readBytes();
            }
        }
    }

    public static StoredPinProto$EncryptedPin parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (StoredPinProto$EncryptedPin) MessageNano.mergeFrom(new StoredPinProto$EncryptedPin(), bArr);
    }

    public static StoredPinProto$EncryptedPin parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new StoredPinProto$EncryptedPin().mergeFrom(codedInputByteBufferNano);
    }
}
