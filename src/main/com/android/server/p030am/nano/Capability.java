package com.android.server.p030am.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* renamed from: com.android.server.am.nano.Capability */
/* loaded from: classes5.dex */
public final class Capability extends MessageNano {
    private static volatile Capability[] _emptyArray;
    public String name;

    public static Capability[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new Capability[0];
                }
            }
        }
        return _emptyArray;
    }

    public Capability() {
        clear();
    }

    public Capability clear() {
        this.name = "";
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (!this.name.equals("")) {
            output.writeString(1, this.name);
        }
        super.writeTo(output);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.framework.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (!this.name.equals("")) {
            return size + CodedOutputByteBufferNano.computeStringSize(1, this.name);
        }
        return size;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public Capability mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            switch (tag) {
                case 0:
                    return this;
                case 10:
                    this.name = input.readString();
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

    public static Capability parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (Capability) MessageNano.mergeFrom(new Capability(), data);
    }

    public static Capability parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new Capability().mergeFrom(input);
    }
}
