package com.android.internal.telephony.nano;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class StoredPinProto$StoredPin extends ExtendableMessageNano<StoredPinProto$StoredPin> {
    private static volatile StoredPinProto$StoredPin[] _emptyArray;
    public int bootCount;
    public String iccid;
    public String pin;
    public int slotId;
    public int status;

    /* loaded from: classes.dex */
    public interface PinStatus {
        public static final int AVAILABLE = 1;
        public static final int REBOOT_READY = 2;
        public static final int VERIFICATION_READY = 3;
    }

    public static StoredPinProto$StoredPin[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new StoredPinProto$StoredPin[0];
                }
            }
        }
        return _emptyArray;
    }

    public StoredPinProto$StoredPin() {
        clear();
    }

    public StoredPinProto$StoredPin clear() {
        this.iccid = PhoneConfigurationManager.SSSS;
        this.pin = PhoneConfigurationManager.SSSS;
        this.slotId = 0;
        this.status = 1;
        this.bootCount = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (!this.iccid.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(1, this.iccid);
        }
        if (!this.pin.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(2, this.pin);
        }
        int i = this.slotId;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(3, i);
        }
        int i2 = this.status;
        if (i2 != 1) {
            codedOutputByteBufferNano.writeInt32(4, i2);
        }
        int i3 = this.bootCount;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(5, i3);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        if (!this.iccid.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.iccid);
        }
        if (!this.pin.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.pin);
        }
        int i = this.slotId;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i);
        }
        int i2 = this.status;
        if (i2 != 1) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i2);
        }
        int i3 = this.bootCount;
        return i3 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(5, i3) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public StoredPinProto$StoredPin mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                this.iccid = codedInputByteBufferNano.readString();
            } else if (readTag == 18) {
                this.pin = codedInputByteBufferNano.readString();
            } else if (readTag == 24) {
                this.slotId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 32) {
                int readInt32 = codedInputByteBufferNano.readInt32();
                if (readInt32 == 1 || readInt32 == 2 || readInt32 == 3) {
                    this.status = readInt32;
                }
            } else if (readTag != 40) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.bootCount = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static StoredPinProto$StoredPin parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (StoredPinProto$StoredPin) MessageNano.mergeFrom(new StoredPinProto$StoredPin(), bArr);
    }

    public static StoredPinProto$StoredPin parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new StoredPinProto$StoredPin().mergeFrom(codedInputByteBufferNano);
    }
}
