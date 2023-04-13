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
public final class TelephonyProto$ActiveSubscriptionInfo extends ExtendableMessageNano<TelephonyProto$ActiveSubscriptionInfo> {
    private static volatile TelephonyProto$ActiveSubscriptionInfo[] _emptyArray;
    public int carrierId;
    public int isOpportunistic;
    public String simMccmnc;
    public int slotIndex;

    public static TelephonyProto$ActiveSubscriptionInfo[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$ActiveSubscriptionInfo[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$ActiveSubscriptionInfo() {
        clear();
    }

    public TelephonyProto$ActiveSubscriptionInfo clear() {
        this.slotIndex = 0;
        this.carrierId = 0;
        this.isOpportunistic = 0;
        this.simMccmnc = PhoneConfigurationManager.SSSS;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.slotIndex;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.carrierId;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.isOpportunistic;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        if (!this.simMccmnc.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(4, this.simMccmnc);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.slotIndex;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.carrierId;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.isOpportunistic;
        if (i3 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        return !this.simMccmnc.equals(PhoneConfigurationManager.SSSS) ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(4, this.simMccmnc) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$ActiveSubscriptionInfo mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.slotIndex = codedInputByteBufferNano.readInt32();
            } else if (readTag == 16) {
                this.carrierId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 24) {
                this.isOpportunistic = codedInputByteBufferNano.readInt32();
            } else if (readTag != 34) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.simMccmnc = codedInputByteBufferNano.readString();
            }
        }
    }

    public static TelephonyProto$ActiveSubscriptionInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$ActiveSubscriptionInfo) MessageNano.mergeFrom(new TelephonyProto$ActiveSubscriptionInfo(), bArr);
    }

    public static TelephonyProto$ActiveSubscriptionInfo parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$ActiveSubscriptionInfo().mergeFrom(codedInputByteBufferNano);
    }
}
