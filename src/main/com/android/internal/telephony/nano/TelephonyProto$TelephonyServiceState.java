package com.android.internal.telephony.nano;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class TelephonyProto$TelephonyServiceState extends ExtendableMessageNano<TelephonyProto$TelephonyServiceState> {
    private static volatile TelephonyProto$TelephonyServiceState[] _emptyArray;
    public int channelNumber;
    public TelephonyOperator dataOperator;
    public int dataRat;
    public int dataRoamingType;
    public NetworkRegistrationInfo[] networkRegistrationInfo;
    public int nrFrequencyRange;
    public int nrState;
    public TelephonyOperator voiceOperator;
    public int voiceRat;
    public int voiceRoamingType;

    /* loaded from: classes.dex */
    public interface Domain {
        public static final int DOMAIN_CS = 1;
        public static final int DOMAIN_PS = 2;
        public static final int DOMAIN_UNKNOWN = 0;
    }

    /* loaded from: classes.dex */
    public interface FrequencyRange {
        public static final int FREQUENCY_RANGE_HIGH = 3;
        public static final int FREQUENCY_RANGE_LOW = 1;
        public static final int FREQUENCY_RANGE_MID = 2;
        public static final int FREQUENCY_RANGE_MMWAVE = 4;
        public static final int FREQUENCY_RANGE_UNKNOWN = 0;
    }

    /* loaded from: classes.dex */
    public interface NrState {
        public static final int NR_STATE_CONNECTED = 3;
        public static final int NR_STATE_NONE = 0;
        public static final int NR_STATE_NOT_RESTRICTED = 2;
        public static final int NR_STATE_RESTRICTED = 1;
    }

    /* loaded from: classes.dex */
    public interface RoamingType {
        public static final int ROAMING_TYPE_DOMESTIC = 2;
        public static final int ROAMING_TYPE_INTERNATIONAL = 3;
        public static final int ROAMING_TYPE_NOT_ROAMING = 0;
        public static final int ROAMING_TYPE_UNKNOWN = 1;
        public static final int UNKNOWN = -1;
    }

    /* loaded from: classes.dex */
    public interface Transport {
        public static final int TRANSPORT_UNKNOWN = 0;
        public static final int TRANSPORT_WLAN = 2;
        public static final int TRANSPORT_WWAN = 1;
    }

    /* loaded from: classes.dex */
    public static final class TelephonyOperator extends ExtendableMessageNano<TelephonyOperator> {
        private static volatile TelephonyOperator[] _emptyArray;
        public String alphaLong;
        public String alphaShort;
        public String numeric;

        public static TelephonyOperator[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new TelephonyOperator[0];
                    }
                }
            }
            return _emptyArray;
        }

        public TelephonyOperator() {
            clear();
        }

        public TelephonyOperator clear() {
            this.alphaLong = PhoneConfigurationManager.SSSS;
            this.alphaShort = PhoneConfigurationManager.SSSS;
            this.numeric = PhoneConfigurationManager.SSSS;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (!this.alphaLong.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(1, this.alphaLong);
            }
            if (!this.alphaShort.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(2, this.alphaShort);
            }
            if (!this.numeric.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(3, this.numeric);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (!this.alphaLong.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.alphaLong);
            }
            if (!this.alphaShort.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.alphaShort);
            }
            return !this.numeric.equals(PhoneConfigurationManager.SSSS) ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(3, this.numeric) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public TelephonyOperator mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    this.alphaLong = codedInputByteBufferNano.readString();
                } else if (readTag == 18) {
                    this.alphaShort = codedInputByteBufferNano.readString();
                } else if (readTag != 26) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    this.numeric = codedInputByteBufferNano.readString();
                }
            }
        }

        public static TelephonyOperator parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (TelephonyOperator) MessageNano.mergeFrom(new TelephonyOperator(), bArr);
        }

        public static TelephonyOperator parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new TelephonyOperator().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class NetworkRegistrationInfo extends ExtendableMessageNano<NetworkRegistrationInfo> {
        private static volatile NetworkRegistrationInfo[] _emptyArray;
        public int domain;
        public int rat;
        public int transport;

        public static NetworkRegistrationInfo[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new NetworkRegistrationInfo[0];
                    }
                }
            }
            return _emptyArray;
        }

        public NetworkRegistrationInfo() {
            clear();
        }

        public NetworkRegistrationInfo clear() {
            this.domain = 0;
            this.transport = 0;
            this.rat = -1;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.domain;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.transport;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.rat;
            if (i3 != -1) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.domain;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.transport;
            if (i2 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.rat;
            return i3 != -1 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i3) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public NetworkRegistrationInfo mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    if (readInt32 == 0 || readInt32 == 1 || readInt32 == 2) {
                        this.domain = readInt32;
                    }
                } else if (readTag == 16) {
                    int readInt322 = codedInputByteBufferNano.readInt32();
                    if (readInt322 == 0 || readInt322 == 1 || readInt322 == 2) {
                        this.transport = readInt322;
                    }
                } else if (readTag != 24) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    int readInt323 = codedInputByteBufferNano.readInt32();
                    switch (readInt323) {
                        case -1:
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                            this.rat = readInt323;
                            continue;
                    }
                }
            }
        }

        public static NetworkRegistrationInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (NetworkRegistrationInfo) MessageNano.mergeFrom(new NetworkRegistrationInfo(), bArr);
        }

        public static NetworkRegistrationInfo parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new NetworkRegistrationInfo().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static TelephonyProto$TelephonyServiceState[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$TelephonyServiceState[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$TelephonyServiceState() {
        clear();
    }

    public TelephonyProto$TelephonyServiceState clear() {
        this.voiceOperator = null;
        this.dataOperator = null;
        this.voiceRoamingType = -1;
        this.dataRoamingType = -1;
        this.voiceRat = -1;
        this.dataRat = -1;
        this.channelNumber = 0;
        this.nrFrequencyRange = 0;
        this.nrState = 0;
        this.networkRegistrationInfo = NetworkRegistrationInfo.emptyArray();
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        TelephonyOperator telephonyOperator = this.voiceOperator;
        if (telephonyOperator != null) {
            codedOutputByteBufferNano.writeMessage(1, telephonyOperator);
        }
        TelephonyOperator telephonyOperator2 = this.dataOperator;
        if (telephonyOperator2 != null) {
            codedOutputByteBufferNano.writeMessage(2, telephonyOperator2);
        }
        int i = this.voiceRoamingType;
        if (i != -1) {
            codedOutputByteBufferNano.writeInt32(3, i);
        }
        int i2 = this.dataRoamingType;
        if (i2 != -1) {
            codedOutputByteBufferNano.writeInt32(4, i2);
        }
        int i3 = this.voiceRat;
        if (i3 != -1) {
            codedOutputByteBufferNano.writeInt32(5, i3);
        }
        int i4 = this.dataRat;
        if (i4 != -1) {
            codedOutputByteBufferNano.writeInt32(6, i4);
        }
        int i5 = this.channelNumber;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(7, i5);
        }
        int i6 = this.nrFrequencyRange;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(8, i6);
        }
        int i7 = this.nrState;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(9, i7);
        }
        NetworkRegistrationInfo[] networkRegistrationInfoArr = this.networkRegistrationInfo;
        if (networkRegistrationInfoArr != null && networkRegistrationInfoArr.length > 0) {
            int i8 = 0;
            while (true) {
                NetworkRegistrationInfo[] networkRegistrationInfoArr2 = this.networkRegistrationInfo;
                if (i8 >= networkRegistrationInfoArr2.length) {
                    break;
                }
                NetworkRegistrationInfo networkRegistrationInfo = networkRegistrationInfoArr2[i8];
                if (networkRegistrationInfo != null) {
                    codedOutputByteBufferNano.writeMessage(10, networkRegistrationInfo);
                }
                i8++;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        TelephonyOperator telephonyOperator = this.voiceOperator;
        if (telephonyOperator != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, telephonyOperator);
        }
        TelephonyOperator telephonyOperator2 = this.dataOperator;
        if (telephonyOperator2 != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, telephonyOperator2);
        }
        int i = this.voiceRoamingType;
        if (i != -1) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i);
        }
        int i2 = this.dataRoamingType;
        if (i2 != -1) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i2);
        }
        int i3 = this.voiceRat;
        if (i3 != -1) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i3);
        }
        int i4 = this.dataRat;
        if (i4 != -1) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i4);
        }
        int i5 = this.channelNumber;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i5);
        }
        int i6 = this.nrFrequencyRange;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i6);
        }
        int i7 = this.nrState;
        if (i7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i7);
        }
        NetworkRegistrationInfo[] networkRegistrationInfoArr = this.networkRegistrationInfo;
        if (networkRegistrationInfoArr != null && networkRegistrationInfoArr.length > 0) {
            int i8 = 0;
            while (true) {
                NetworkRegistrationInfo[] networkRegistrationInfoArr2 = this.networkRegistrationInfo;
                if (i8 >= networkRegistrationInfoArr2.length) {
                    break;
                }
                NetworkRegistrationInfo networkRegistrationInfo = networkRegistrationInfoArr2[i8];
                if (networkRegistrationInfo != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(10, networkRegistrationInfo);
                }
                i8++;
            }
        }
        return computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$TelephonyServiceState mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 10:
                    if (this.voiceOperator == null) {
                        this.voiceOperator = new TelephonyOperator();
                    }
                    codedInputByteBufferNano.readMessage(this.voiceOperator);
                    break;
                case 18:
                    if (this.dataOperator == null) {
                        this.dataOperator = new TelephonyOperator();
                    }
                    codedInputByteBufferNano.readMessage(this.dataOperator);
                    break;
                case 24:
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    if (readInt32 != -1 && readInt32 != 0 && readInt32 != 1 && readInt32 != 2 && readInt32 != 3) {
                        break;
                    } else {
                        this.voiceRoamingType = readInt32;
                        break;
                    }
                case 32:
                    int readInt322 = codedInputByteBufferNano.readInt32();
                    if (readInt322 != -1 && readInt322 != 0 && readInt322 != 1 && readInt322 != 2 && readInt322 != 3) {
                        break;
                    } else {
                        this.dataRoamingType = readInt322;
                        break;
                    }
                case 40:
                    int readInt323 = codedInputByteBufferNano.readInt32();
                    switch (readInt323) {
                        case -1:
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                            this.voiceRat = readInt323;
                            continue;
                    }
                case 48:
                    int readInt324 = codedInputByteBufferNano.readInt32();
                    switch (readInt324) {
                        case -1:
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                            this.dataRat = readInt324;
                            continue;
                    }
                case 56:
                    this.channelNumber = codedInputByteBufferNano.readInt32();
                    break;
                case 64:
                    int readInt325 = codedInputByteBufferNano.readInt32();
                    if (readInt325 != 0 && readInt325 != 1 && readInt325 != 2 && readInt325 != 3 && readInt325 != 4) {
                        break;
                    } else {
                        this.nrFrequencyRange = readInt325;
                        break;
                    }
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    int readInt326 = codedInputByteBufferNano.readInt32();
                    if (readInt326 != 0 && readInt326 != 1 && readInt326 != 2 && readInt326 != 3) {
                        break;
                    } else {
                        this.nrState = readInt326;
                        break;
                    }
                case RadioNVItems.RIL_NV_LTE_BSR_MAX_TIME /* 82 */:
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 82);
                    NetworkRegistrationInfo[] networkRegistrationInfoArr = this.networkRegistrationInfo;
                    int length = networkRegistrationInfoArr == null ? 0 : networkRegistrationInfoArr.length;
                    int i = repeatedFieldArrayLength + length;
                    NetworkRegistrationInfo[] networkRegistrationInfoArr2 = new NetworkRegistrationInfo[i];
                    if (length != 0) {
                        System.arraycopy(networkRegistrationInfoArr, 0, networkRegistrationInfoArr2, 0, length);
                    }
                    while (length < i - 1) {
                        NetworkRegistrationInfo networkRegistrationInfo = new NetworkRegistrationInfo();
                        networkRegistrationInfoArr2[length] = networkRegistrationInfo;
                        codedInputByteBufferNano.readMessage(networkRegistrationInfo);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    NetworkRegistrationInfo networkRegistrationInfo2 = new NetworkRegistrationInfo();
                    networkRegistrationInfoArr2[length] = networkRegistrationInfo2;
                    codedInputByteBufferNano.readMessage(networkRegistrationInfo2);
                    this.networkRegistrationInfo = networkRegistrationInfoArr2;
                    break;
                default:
                    if (storeUnknownField(codedInputByteBufferNano, readTag)) {
                        break;
                    } else {
                        return this;
                    }
            }
        }
    }

    public static TelephonyProto$TelephonyServiceState parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$TelephonyServiceState) MessageNano.mergeFrom(new TelephonyProto$TelephonyServiceState(), bArr);
    }

    public static TelephonyProto$TelephonyServiceState parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$TelephonyServiceState().mergeFrom(codedInputByteBufferNano);
    }
}
