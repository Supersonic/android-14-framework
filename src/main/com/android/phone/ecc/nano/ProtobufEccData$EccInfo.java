package com.android.phone.ecc.nano;

import com.android.internal.telephony.PhoneConfigurationManager;
import java.io.IOException;
/* loaded from: classes.dex */
public final class ProtobufEccData$EccInfo extends ExtendableMessageNano<ProtobufEccData$EccInfo> {
    private static volatile ProtobufEccData$EccInfo[] _emptyArray;
    public String[] normalRoutingMncs;
    public String phoneNumber;
    public int routing;
    public int[] types;

    public static ProtobufEccData$EccInfo[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new ProtobufEccData$EccInfo[0];
                }
            }
        }
        return _emptyArray;
    }

    public ProtobufEccData$EccInfo() {
        clear();
    }

    public ProtobufEccData$EccInfo clear() {
        this.phoneNumber = PhoneConfigurationManager.SSSS;
        this.types = WireFormatNano.EMPTY_INT_ARRAY;
        this.routing = 0;
        this.normalRoutingMncs = WireFormatNano.EMPTY_STRING_ARRAY;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.phone.ecc.nano.MessageNano
    public ProtobufEccData$EccInfo mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                this.phoneNumber = codedInputByteBufferNano.readString();
            } else if (readTag == 16) {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 16);
                int[] iArr = new int[repeatedFieldArrayLength];
                int i = 0;
                for (int i2 = 0; i2 < repeatedFieldArrayLength; i2++) {
                    if (i2 != 0) {
                        codedInputByteBufferNano.readTag();
                    }
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    switch (readInt32) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            iArr[i] = readInt32;
                            i++;
                            break;
                    }
                }
                if (i != 0) {
                    int[] iArr2 = this.types;
                    int length = iArr2 == null ? 0 : iArr2.length;
                    if (length == 0 && i == repeatedFieldArrayLength) {
                        this.types = iArr;
                    } else {
                        int[] iArr3 = new int[length + i];
                        if (length != 0) {
                            System.arraycopy(iArr2, 0, iArr3, 0, length);
                        }
                        System.arraycopy(iArr, 0, iArr3, length, i);
                        this.types = iArr3;
                    }
                }
            } else if (readTag == 18) {
                int pushLimit = codedInputByteBufferNano.pushLimit(codedInputByteBufferNano.readRawVarint32());
                int position = codedInputByteBufferNano.getPosition();
                int i3 = 0;
                while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                    switch (codedInputByteBufferNano.readInt32()) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            i3++;
                            break;
                    }
                }
                if (i3 != 0) {
                    codedInputByteBufferNano.rewindToPosition(position);
                    int[] iArr4 = this.types;
                    int length2 = iArr4 == null ? 0 : iArr4.length;
                    int[] iArr5 = new int[i3 + length2];
                    if (length2 != 0) {
                        System.arraycopy(iArr4, 0, iArr5, 0, length2);
                    }
                    while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                        int readInt322 = codedInputByteBufferNano.readInt32();
                        switch (readInt322) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                                iArr5[length2] = readInt322;
                                length2++;
                                break;
                        }
                    }
                    this.types = iArr5;
                }
                codedInputByteBufferNano.popLimit(pushLimit);
            } else if (readTag == 24) {
                int readInt323 = codedInputByteBufferNano.readInt32();
                if (readInt323 == 0 || readInt323 == 1 || readInt323 == 2) {
                    this.routing = readInt323;
                }
            } else if (readTag != 34) {
                if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            } else {
                int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                String[] strArr = this.normalRoutingMncs;
                int length3 = strArr == null ? 0 : strArr.length;
                int i4 = repeatedFieldArrayLength2 + length3;
                String[] strArr2 = new String[i4];
                if (length3 != 0) {
                    System.arraycopy(strArr, 0, strArr2, 0, length3);
                }
                while (length3 < i4 - 1) {
                    strArr2[length3] = codedInputByteBufferNano.readString();
                    codedInputByteBufferNano.readTag();
                    length3++;
                }
                strArr2[length3] = codedInputByteBufferNano.readString();
                this.normalRoutingMncs = strArr2;
            }
        }
    }
}
