package com.android.phone.ecc.nano;

import com.android.internal.telephony.PhoneConfigurationManager;
import java.io.IOException;
/* loaded from: classes.dex */
public final class ProtobufEccData$CountryInfo extends ExtendableMessageNano<ProtobufEccData$CountryInfo> {
    private static volatile ProtobufEccData$CountryInfo[] _emptyArray;
    public String eccFallback;
    public ProtobufEccData$EccInfo[] eccs;
    public boolean ignoreModemConfig;
    public String isoCode;

    public static ProtobufEccData$CountryInfo[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new ProtobufEccData$CountryInfo[0];
                }
            }
        }
        return _emptyArray;
    }

    public ProtobufEccData$CountryInfo() {
        clear();
    }

    public ProtobufEccData$CountryInfo clear() {
        this.isoCode = PhoneConfigurationManager.SSSS;
        this.eccs = ProtobufEccData$EccInfo.emptyArray();
        this.eccFallback = PhoneConfigurationManager.SSSS;
        this.ignoreModemConfig = false;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.phone.ecc.nano.MessageNano
    public ProtobufEccData$CountryInfo mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                this.isoCode = codedInputByteBufferNano.readString();
            } else if (readTag == 18) {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                ProtobufEccData$EccInfo[] protobufEccData$EccInfoArr = this.eccs;
                int length = protobufEccData$EccInfoArr == null ? 0 : protobufEccData$EccInfoArr.length;
                int i = repeatedFieldArrayLength + length;
                ProtobufEccData$EccInfo[] protobufEccData$EccInfoArr2 = new ProtobufEccData$EccInfo[i];
                if (length != 0) {
                    System.arraycopy(protobufEccData$EccInfoArr, 0, protobufEccData$EccInfoArr2, 0, length);
                }
                while (length < i - 1) {
                    ProtobufEccData$EccInfo protobufEccData$EccInfo = new ProtobufEccData$EccInfo();
                    protobufEccData$EccInfoArr2[length] = protobufEccData$EccInfo;
                    codedInputByteBufferNano.readMessage(protobufEccData$EccInfo);
                    codedInputByteBufferNano.readTag();
                    length++;
                }
                ProtobufEccData$EccInfo protobufEccData$EccInfo2 = new ProtobufEccData$EccInfo();
                protobufEccData$EccInfoArr2[length] = protobufEccData$EccInfo2;
                codedInputByteBufferNano.readMessage(protobufEccData$EccInfo2);
                this.eccs = protobufEccData$EccInfoArr2;
            } else if (readTag == 26) {
                this.eccFallback = codedInputByteBufferNano.readString();
            } else if (readTag != 32) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                this.ignoreModemConfig = codedInputByteBufferNano.readBool();
            }
        }
    }
}
