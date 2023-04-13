package com.android.internal.telephony.nano;

import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class CarrierIdProto$CarrierList extends ExtendableMessageNano<CarrierIdProto$CarrierList> {
    private static volatile CarrierIdProto$CarrierList[] _emptyArray;
    public CarrierIdProto$CarrierId[] carrierId;
    public int version;

    public static CarrierIdProto$CarrierList[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new CarrierIdProto$CarrierList[0];
                }
            }
        }
        return _emptyArray;
    }

    public CarrierIdProto$CarrierList() {
        clear();
    }

    public CarrierIdProto$CarrierList clear() {
        this.carrierId = CarrierIdProto$CarrierId.emptyArray();
        this.version = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        CarrierIdProto$CarrierId[] carrierIdProto$CarrierIdArr = this.carrierId;
        if (carrierIdProto$CarrierIdArr != null && carrierIdProto$CarrierIdArr.length > 0) {
            int i = 0;
            while (true) {
                CarrierIdProto$CarrierId[] carrierIdProto$CarrierIdArr2 = this.carrierId;
                if (i >= carrierIdProto$CarrierIdArr2.length) {
                    break;
                }
                CarrierIdProto$CarrierId carrierIdProto$CarrierId = carrierIdProto$CarrierIdArr2[i];
                if (carrierIdProto$CarrierId != null) {
                    codedOutputByteBufferNano.writeMessage(1, carrierIdProto$CarrierId);
                }
                i++;
            }
        }
        int i2 = this.version;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        CarrierIdProto$CarrierId[] carrierIdProto$CarrierIdArr = this.carrierId;
        if (carrierIdProto$CarrierIdArr != null && carrierIdProto$CarrierIdArr.length > 0) {
            int i = 0;
            while (true) {
                CarrierIdProto$CarrierId[] carrierIdProto$CarrierIdArr2 = this.carrierId;
                if (i >= carrierIdProto$CarrierIdArr2.length) {
                    break;
                }
                CarrierIdProto$CarrierId carrierIdProto$CarrierId = carrierIdProto$CarrierIdArr2[i];
                if (carrierIdProto$CarrierId != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, carrierIdProto$CarrierId);
                }
                i++;
            }
        }
        int i2 = this.version;
        return i2 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public CarrierIdProto$CarrierList mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                CarrierIdProto$CarrierId[] carrierIdProto$CarrierIdArr = this.carrierId;
                int length = carrierIdProto$CarrierIdArr == null ? 0 : carrierIdProto$CarrierIdArr.length;
                int i = repeatedFieldArrayLength + length;
                CarrierIdProto$CarrierId[] carrierIdProto$CarrierIdArr2 = new CarrierIdProto$CarrierId[i];
                if (length != 0) {
                    System.arraycopy(carrierIdProto$CarrierIdArr, 0, carrierIdProto$CarrierIdArr2, 0, length);
                }
                while (length < i - 1) {
                    CarrierIdProto$CarrierId carrierIdProto$CarrierId = new CarrierIdProto$CarrierId();
                    carrierIdProto$CarrierIdArr2[length] = carrierIdProto$CarrierId;
                    codedInputByteBufferNano.readMessage(carrierIdProto$CarrierId);
                    codedInputByteBufferNano.readTag();
                    length++;
                }
                CarrierIdProto$CarrierId carrierIdProto$CarrierId2 = new CarrierIdProto$CarrierId();
                carrierIdProto$CarrierIdArr2[length] = carrierIdProto$CarrierId2;
                codedInputByteBufferNano.readMessage(carrierIdProto$CarrierId2);
                this.carrierId = carrierIdProto$CarrierIdArr2;
            } else if (readTag != 16) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.version = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static CarrierIdProto$CarrierList parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (CarrierIdProto$CarrierList) MessageNano.mergeFrom(new CarrierIdProto$CarrierList(), bArr);
    }

    public static CarrierIdProto$CarrierList parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new CarrierIdProto$CarrierList().mergeFrom(codedInputByteBufferNano);
    }
}
