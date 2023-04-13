package com.android.internal.telephony.nano;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class CarrierIdProto$CarrierId extends ExtendableMessageNano<CarrierIdProto$CarrierId> {
    private static volatile CarrierIdProto$CarrierId[] _emptyArray;
    public int canonicalId;
    public CarrierIdProto$CarrierAttribute[] carrierAttribute;
    public String carrierName;
    public int parentCanonicalId;

    public static CarrierIdProto$CarrierId[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new CarrierIdProto$CarrierId[0];
                }
            }
        }
        return _emptyArray;
    }

    public CarrierIdProto$CarrierId() {
        clear();
    }

    public CarrierIdProto$CarrierId clear() {
        this.canonicalId = 0;
        this.carrierName = PhoneConfigurationManager.SSSS;
        this.carrierAttribute = CarrierIdProto$CarrierAttribute.emptyArray();
        this.parentCanonicalId = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.canonicalId;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        if (!this.carrierName.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(2, this.carrierName);
        }
        CarrierIdProto$CarrierAttribute[] carrierIdProto$CarrierAttributeArr = this.carrierAttribute;
        if (carrierIdProto$CarrierAttributeArr != null && carrierIdProto$CarrierAttributeArr.length > 0) {
            int i2 = 0;
            while (true) {
                CarrierIdProto$CarrierAttribute[] carrierIdProto$CarrierAttributeArr2 = this.carrierAttribute;
                if (i2 >= carrierIdProto$CarrierAttributeArr2.length) {
                    break;
                }
                CarrierIdProto$CarrierAttribute carrierIdProto$CarrierAttribute = carrierIdProto$CarrierAttributeArr2[i2];
                if (carrierIdProto$CarrierAttribute != null) {
                    codedOutputByteBufferNano.writeMessage(3, carrierIdProto$CarrierAttribute);
                }
                i2++;
            }
        }
        int i3 = this.parentCanonicalId;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i3);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.canonicalId;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        if (!this.carrierName.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.carrierName);
        }
        CarrierIdProto$CarrierAttribute[] carrierIdProto$CarrierAttributeArr = this.carrierAttribute;
        if (carrierIdProto$CarrierAttributeArr != null && carrierIdProto$CarrierAttributeArr.length > 0) {
            int i2 = 0;
            while (true) {
                CarrierIdProto$CarrierAttribute[] carrierIdProto$CarrierAttributeArr2 = this.carrierAttribute;
                if (i2 >= carrierIdProto$CarrierAttributeArr2.length) {
                    break;
                }
                CarrierIdProto$CarrierAttribute carrierIdProto$CarrierAttribute = carrierIdProto$CarrierAttributeArr2[i2];
                if (carrierIdProto$CarrierAttribute != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, carrierIdProto$CarrierAttribute);
                }
                i2++;
            }
        }
        int i3 = this.parentCanonicalId;
        return i3 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(4, i3) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public CarrierIdProto$CarrierId mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.canonicalId = codedInputByteBufferNano.readInt32();
            } else if (readTag == 18) {
                this.carrierName = codedInputByteBufferNano.readString();
            } else if (readTag == 26) {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                CarrierIdProto$CarrierAttribute[] carrierIdProto$CarrierAttributeArr = this.carrierAttribute;
                int length = carrierIdProto$CarrierAttributeArr == null ? 0 : carrierIdProto$CarrierAttributeArr.length;
                int i = repeatedFieldArrayLength + length;
                CarrierIdProto$CarrierAttribute[] carrierIdProto$CarrierAttributeArr2 = new CarrierIdProto$CarrierAttribute[i];
                if (length != 0) {
                    System.arraycopy(carrierIdProto$CarrierAttributeArr, 0, carrierIdProto$CarrierAttributeArr2, 0, length);
                }
                while (length < i - 1) {
                    CarrierIdProto$CarrierAttribute carrierIdProto$CarrierAttribute = new CarrierIdProto$CarrierAttribute();
                    carrierIdProto$CarrierAttributeArr2[length] = carrierIdProto$CarrierAttribute;
                    codedInputByteBufferNano.readMessage(carrierIdProto$CarrierAttribute);
                    codedInputByteBufferNano.readTag();
                    length++;
                }
                CarrierIdProto$CarrierAttribute carrierIdProto$CarrierAttribute2 = new CarrierIdProto$CarrierAttribute();
                carrierIdProto$CarrierAttributeArr2[length] = carrierIdProto$CarrierAttribute2;
                codedInputByteBufferNano.readMessage(carrierIdProto$CarrierAttribute2);
                this.carrierAttribute = carrierIdProto$CarrierAttributeArr2;
            } else if (readTag != 32) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.parentCanonicalId = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static CarrierIdProto$CarrierId parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (CarrierIdProto$CarrierId) MessageNano.mergeFrom(new CarrierIdProto$CarrierId(), bArr);
    }

    public static CarrierIdProto$CarrierId parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new CarrierIdProto$CarrierId().mergeFrom(codedInputByteBufferNano);
    }
}
