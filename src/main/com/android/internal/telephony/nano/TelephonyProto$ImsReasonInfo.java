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
public final class TelephonyProto$ImsReasonInfo extends ExtendableMessageNano<TelephonyProto$ImsReasonInfo> {
    private static volatile TelephonyProto$ImsReasonInfo[] _emptyArray;
    public int extraCode;
    public String extraMessage;
    public int reasonCode;

    public static TelephonyProto$ImsReasonInfo[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$ImsReasonInfo[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$ImsReasonInfo() {
        clear();
    }

    public TelephonyProto$ImsReasonInfo clear() {
        this.reasonCode = 0;
        this.extraCode = 0;
        this.extraMessage = PhoneConfigurationManager.SSSS;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.reasonCode;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.extraCode;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        if (!this.extraMessage.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(3, this.extraMessage);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.reasonCode;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.extraCode;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        return !this.extraMessage.equals(PhoneConfigurationManager.SSSS) ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(3, this.extraMessage) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$ImsReasonInfo mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.reasonCode = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.extraCode = codedInputByteBufferNano.readInt32();
            } else if (readTag != 26) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.extraMessage = codedInputByteBufferNano.readString();
            }
        }
    }

    public static TelephonyProto$ImsReasonInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$ImsReasonInfo) MessageNano.mergeFrom(new TelephonyProto$ImsReasonInfo(), bArr);
    }

    public static TelephonyProto$ImsReasonInfo parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$ImsReasonInfo().mergeFrom(codedInputByteBufferNano);
    }
}
