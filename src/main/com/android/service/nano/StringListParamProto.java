package com.android.service.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes5.dex */
public final class StringListParamProto extends MessageNano {
    private static volatile StringListParamProto[] _emptyArray;
    public String[] element;

    public static StringListParamProto[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new StringListParamProto[0];
                }
            }
        }
        return _emptyArray;
    }

    public StringListParamProto() {
        clear();
    }

    public StringListParamProto clear() {
        this.element = WireFormatNano.EMPTY_STRING_ARRAY;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        String[] strArr = this.element;
        if (strArr != null && strArr.length > 0) {
            int i = 0;
            while (true) {
                String[] strArr2 = this.element;
                if (i >= strArr2.length) {
                    break;
                }
                String element = strArr2[i];
                if (element != null) {
                    output.writeString(1, element);
                }
                i++;
            }
        }
        super.writeTo(output);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.framework.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int size = super.computeSerializedSize();
        String[] strArr = this.element;
        if (strArr != null && strArr.length > 0) {
            int dataCount = 0;
            int dataSize = 0;
            int i = 0;
            while (true) {
                String[] strArr2 = this.element;
                if (i < strArr2.length) {
                    String element = strArr2[i];
                    if (element != null) {
                        dataCount++;
                        dataSize += CodedOutputByteBufferNano.computeStringSizeNoTag(element);
                    }
                    i++;
                } else {
                    return size + dataSize + (dataCount * 1);
                }
            }
        } else {
            return size;
        }
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public StringListParamProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            switch (tag) {
                case 0:
                    return this;
                case 10:
                    int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                    String[] strArr = this.element;
                    int i = strArr == null ? 0 : strArr.length;
                    String[] newArray = new String[i + arrayLength];
                    if (i != 0) {
                        System.arraycopy(strArr, 0, newArray, 0, i);
                    }
                    while (i < newArray.length - 1) {
                        newArray[i] = input.readString();
                        input.readTag();
                        i++;
                    }
                    newArray[i] = input.readString();
                    this.element = newArray;
                    break;
                default:
                    if (WireFormatNano.parseUnknownField(input, tag)) {
                        break;
                    } else {
                        return this;
                    }
            }
        }
    }

    public static StringListParamProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (StringListParamProto) MessageNano.mergeFrom(new StringListParamProto(), data);
    }

    public static StringListParamProto parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new StringListParamProto().mergeFrom(input);
    }
}
