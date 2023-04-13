package com.android.server.criticalevents.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes5.dex */
public final class CriticalEventLogProto extends MessageNano {
    private static volatile CriticalEventLogProto[] _emptyArray;
    public int capacity;
    public CriticalEventProto[] events;
    public long timestampMs;
    public int windowMs;

    public static CriticalEventLogProto[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new CriticalEventLogProto[0];
                }
            }
        }
        return _emptyArray;
    }

    public CriticalEventLogProto() {
        clear();
    }

    public CriticalEventLogProto clear() {
        this.timestampMs = 0L;
        this.windowMs = 0;
        this.capacity = 0;
        this.events = CriticalEventProto.emptyArray();
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        long j = this.timestampMs;
        if (j != 0) {
            output.writeInt64(1, j);
        }
        int i = this.windowMs;
        if (i != 0) {
            output.writeInt32(2, i);
        }
        int i2 = this.capacity;
        if (i2 != 0) {
            output.writeInt32(3, i2);
        }
        CriticalEventProto[] criticalEventProtoArr = this.events;
        if (criticalEventProtoArr != null && criticalEventProtoArr.length > 0) {
            int i3 = 0;
            while (true) {
                CriticalEventProto[] criticalEventProtoArr2 = this.events;
                if (i3 >= criticalEventProtoArr2.length) {
                    break;
                }
                CriticalEventProto element = criticalEventProtoArr2[i3];
                if (element != null) {
                    output.writeMessage(4, element);
                }
                i3++;
            }
        }
        super.writeTo(output);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.framework.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int size = super.computeSerializedSize();
        long j = this.timestampMs;
        if (j != 0) {
            size += CodedOutputByteBufferNano.computeInt64Size(1, j);
        }
        int i = this.windowMs;
        if (i != 0) {
            size += CodedOutputByteBufferNano.computeInt32Size(2, i);
        }
        int i2 = this.capacity;
        if (i2 != 0) {
            size += CodedOutputByteBufferNano.computeInt32Size(3, i2);
        }
        CriticalEventProto[] criticalEventProtoArr = this.events;
        if (criticalEventProtoArr != null && criticalEventProtoArr.length > 0) {
            int i3 = 0;
            while (true) {
                CriticalEventProto[] criticalEventProtoArr2 = this.events;
                if (i3 >= criticalEventProtoArr2.length) {
                    break;
                }
                CriticalEventProto element = criticalEventProtoArr2[i3];
                if (element != null) {
                    size += CodedOutputByteBufferNano.computeMessageSize(4, element);
                }
                i3++;
            }
        }
        return size;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public CriticalEventLogProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            switch (tag) {
                case 0:
                    return this;
                case 8:
                    this.timestampMs = input.readInt64();
                    break;
                case 16:
                    this.windowMs = input.readInt32();
                    break;
                case 24:
                    int arrayLength = input.readInt32();
                    this.capacity = arrayLength;
                    break;
                case 34:
                    int arrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(input, 34);
                    CriticalEventProto[] criticalEventProtoArr = this.events;
                    int i = criticalEventProtoArr == null ? 0 : criticalEventProtoArr.length;
                    CriticalEventProto[] newArray = new CriticalEventProto[i + arrayLength2];
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

    public static CriticalEventLogProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (CriticalEventLogProto) MessageNano.mergeFrom(new CriticalEventLogProto(), data);
    }

    public static CriticalEventLogProto parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new CriticalEventLogProto().mergeFrom(input);
    }
}
