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
public final class TelephonyProto$EmergencyNumberInfo extends ExtendableMessageNano<TelephonyProto$EmergencyNumberInfo> {
    private static volatile TelephonyProto$EmergencyNumberInfo[] _emptyArray;
    public String address;
    public String countryIso;
    public String mnc;
    public int numberSourcesBitmask;
    public int routing;
    public int serviceCategoriesBitmask;
    public String[] urns;

    public static TelephonyProto$EmergencyNumberInfo[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$EmergencyNumberInfo[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$EmergencyNumberInfo() {
        clear();
    }

    public TelephonyProto$EmergencyNumberInfo clear() {
        this.address = PhoneConfigurationManager.SSSS;
        this.countryIso = PhoneConfigurationManager.SSSS;
        this.mnc = PhoneConfigurationManager.SSSS;
        this.serviceCategoriesBitmask = 0;
        this.urns = WireFormatNano.EMPTY_STRING_ARRAY;
        this.numberSourcesBitmask = 0;
        this.routing = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (!this.address.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(1, this.address);
        }
        if (!this.countryIso.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(2, this.countryIso);
        }
        if (!this.mnc.equals(PhoneConfigurationManager.SSSS)) {
            codedOutputByteBufferNano.writeString(3, this.mnc);
        }
        int i = this.serviceCategoriesBitmask;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(4, i);
        }
        String[] strArr = this.urns;
        if (strArr != null && strArr.length > 0) {
            int i2 = 0;
            while (true) {
                String[] strArr2 = this.urns;
                if (i2 >= strArr2.length) {
                    break;
                }
                String str = strArr2[i2];
                if (str != null) {
                    codedOutputByteBufferNano.writeString(5, str);
                }
                i2++;
            }
        }
        int i3 = this.numberSourcesBitmask;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i3);
        }
        int i4 = this.routing;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(7, i4);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        if (!this.address.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.address);
        }
        if (!this.countryIso.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.countryIso);
        }
        if (!this.mnc.equals(PhoneConfigurationManager.SSSS)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.mnc);
        }
        int i = this.serviceCategoriesBitmask;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i);
        }
        String[] strArr = this.urns;
        if (strArr != null && strArr.length > 0) {
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            while (true) {
                String[] strArr2 = this.urns;
                if (i2 >= strArr2.length) {
                    break;
                }
                String str = strArr2[i2];
                if (str != null) {
                    i4++;
                    i3 += CodedOutputByteBufferNano.computeStringSizeNoTag(str);
                }
                i2++;
            }
            computeSerializedSize = computeSerializedSize + i3 + (i4 * 1);
        }
        int i5 = this.numberSourcesBitmask;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i5);
        }
        int i6 = this.routing;
        return i6 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(7, i6) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$EmergencyNumberInfo mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                this.address = codedInputByteBufferNano.readString();
            } else if (readTag == 18) {
                this.countryIso = codedInputByteBufferNano.readString();
            } else if (readTag == 26) {
                this.mnc = codedInputByteBufferNano.readString();
            } else if (readTag == 32) {
                this.serviceCategoriesBitmask = codedInputByteBufferNano.readInt32();
            } else if (readTag == 42) {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 42);
                String[] strArr = this.urns;
                int length = strArr == null ? 0 : strArr.length;
                int i = repeatedFieldArrayLength + length;
                String[] strArr2 = new String[i];
                if (length != 0) {
                    System.arraycopy(strArr, 0, strArr2, 0, length);
                }
                while (length < i - 1) {
                    strArr2[length] = codedInputByteBufferNano.readString();
                    codedInputByteBufferNano.readTag();
                    length++;
                }
                strArr2[length] = codedInputByteBufferNano.readString();
                this.urns = strArr2;
            } else if (readTag == 48) {
                this.numberSourcesBitmask = codedInputByteBufferNano.readInt32();
            } else if (readTag != 56) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.routing = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static TelephonyProto$EmergencyNumberInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$EmergencyNumberInfo) MessageNano.mergeFrom(new TelephonyProto$EmergencyNumberInfo(), bArr);
    }

    public static TelephonyProto$EmergencyNumberInfo parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$EmergencyNumberInfo().mergeFrom(codedInputByteBufferNano);
    }
}
