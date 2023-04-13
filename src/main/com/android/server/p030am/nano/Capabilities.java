package com.android.server.p030am.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* renamed from: com.android.server.am.nano.Capabilities */
/* loaded from: classes5.dex */
public final class Capabilities extends MessageNano {
    private static volatile Capabilities[] _emptyArray;
    public Capability[] values;

    public static Capabilities[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new Capabilities[0];
                }
            }
        }
        return _emptyArray;
    }

    public Capabilities() {
        clear();
    }

    public Capabilities clear() {
        this.values = Capability.emptyArray();
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        Capability[] capabilityArr = this.values;
        if (capabilityArr != null && capabilityArr.length > 0) {
            int i = 0;
            while (true) {
                Capability[] capabilityArr2 = this.values;
                if (i >= capabilityArr2.length) {
                    break;
                }
                Capability element = capabilityArr2[i];
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
        Capability[] capabilityArr = this.values;
        if (capabilityArr != null && capabilityArr.length > 0) {
            int i = 0;
            while (true) {
                Capability[] capabilityArr2 = this.values;
                if (i >= capabilityArr2.length) {
                    break;
                }
                Capability element = capabilityArr2[i];
                if (element != null) {
                    size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                }
                i++;
            }
        }
        return size;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public Capabilities mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            switch (tag) {
                case 0:
                    return this;
                case 10:
                    int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                    Capability[] capabilityArr = this.values;
                    int i = capabilityArr == null ? 0 : capabilityArr.length;
                    Capability[] newArray = new Capability[i + arrayLength];
                    if (i != 0) {
                        System.arraycopy(capabilityArr, 0, newArray, 0, i);
                    }
                    while (i < newArray.length - 1) {
                        newArray[i] = new Capability();
                        input.readMessage(newArray[i]);
                        input.readTag();
                        i++;
                    }
                    newArray[i] = new Capability();
                    input.readMessage(newArray[i]);
                    this.values = newArray;
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

    public static Capabilities parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (Capabilities) MessageNano.mergeFrom(new Capabilities(), data);
    }

    public static Capabilities parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new Capabilities().mergeFrom(input);
    }
}
