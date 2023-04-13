package com.android.server.criticalevents.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes5.dex */
public final class CriticalEventLogStorageProto extends MessageNano {
    private static volatile CriticalEventLogStorageProto[] _emptyArray;
    public CriticalEventProto[] events;

    public static CriticalEventLogStorageProto[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new CriticalEventLogStorageProto[0];
                }
            }
        }
        return _emptyArray;
    }

    public CriticalEventLogStorageProto() {
        clear();
    }

    public CriticalEventLogStorageProto clear() {
        this.events = CriticalEventProto.emptyArray();
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        CriticalEventProto[] criticalEventProtoArr = this.events;
        if (criticalEventProtoArr != null && criticalEventProtoArr.length > 0) {
            int i = 0;
            while (true) {
                CriticalEventProto[] criticalEventProtoArr2 = this.events;
                if (i >= criticalEventProtoArr2.length) {
                    break;
                }
                CriticalEventProto element = criticalEventProtoArr2[i];
                if (element != null) {
                    output.writeMessage(1, element);
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
        CriticalEventProto[] criticalEventProtoArr = this.events;
        if (criticalEventProtoArr != null && criticalEventProtoArr.length > 0) {
            int i = 0;
            while (true) {
                CriticalEventProto[] criticalEventProtoArr2 = this.events;
                if (i >= criticalEventProtoArr2.length) {
                    break;
                }
                CriticalEventProto element = criticalEventProtoArr2[i];
                if (element != null) {
                    size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                }
                i++;
            }
        }
        return size;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public CriticalEventLogStorageProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            switch (tag) {
                case 0:
                    return this;
                case 10:
                    int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                    CriticalEventProto[] criticalEventProtoArr = this.events;
                    int i = criticalEventProtoArr == null ? 0 : criticalEventProtoArr.length;
                    CriticalEventProto[] newArray = new CriticalEventProto[i + arrayLength];
                    if (i != 0) {
                        System.arraycopy(criticalEventProtoArr, 0, newArray, 0, i);
                    }
                    while (i < newArray.length - 1) {
                        newArray[i] = new CriticalEventProto();
                        input.readMessage(newArray[i]);
                        input.readTag();
                        i++;
                    }
                    newArray[i] = new CriticalEventProto();
                    input.readMessage(newArray[i]);
                    this.events = newArray;
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

    public static CriticalEventLogStorageProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (CriticalEventLogStorageProto) MessageNano.mergeFrom(new CriticalEventLogStorageProto(), data);
    }

    public static CriticalEventLogStorageProto parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new CriticalEventLogStorageProto().mergeFrom(input);
    }
}
