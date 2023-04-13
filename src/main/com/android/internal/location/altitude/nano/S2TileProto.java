package com.android.internal.location.altitude.nano;

import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import java.io.IOException;
import java.util.Arrays;
/* loaded from: classes4.dex */
public final class S2TileProto extends MessageNano {
    private static volatile S2TileProto[] _emptyArray;
    public byte[] byteBuffer;
    public byte[] byteJpeg;
    public byte[] bytePng;
    public String tileKey;

    public static S2TileProto[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new S2TileProto[0];
                }
            }
        }
        return _emptyArray;
    }

    public S2TileProto() {
        clear();
    }

    public S2TileProto clear() {
        this.tileKey = "";
        this.byteBuffer = WireFormatNano.EMPTY_BYTES;
        this.byteJpeg = WireFormatNano.EMPTY_BYTES;
        this.bytePng = WireFormatNano.EMPTY_BYTES;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (!this.tileKey.equals("")) {
            output.writeString(1, this.tileKey);
        }
        if (!Arrays.equals(this.byteBuffer, WireFormatNano.EMPTY_BYTES)) {
            output.writeBytes(2, this.byteBuffer);
        }
        if (!Arrays.equals(this.byteJpeg, WireFormatNano.EMPTY_BYTES)) {
            output.writeBytes(3, this.byteJpeg);
        }
        if (!Arrays.equals(this.bytePng, WireFormatNano.EMPTY_BYTES)) {
            output.writeBytes(4, this.bytePng);
        }
        super.writeTo(output);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.framework.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (!this.tileKey.equals("")) {
            size += CodedOutputByteBufferNano.computeStringSize(1, this.tileKey);
        }
        if (!Arrays.equals(this.byteBuffer, WireFormatNano.EMPTY_BYTES)) {
            size += CodedOutputByteBufferNano.computeBytesSize(2, this.byteBuffer);
        }
        if (!Arrays.equals(this.byteJpeg, WireFormatNano.EMPTY_BYTES)) {
            size += CodedOutputByteBufferNano.computeBytesSize(3, this.byteJpeg);
        }
        if (!Arrays.equals(this.bytePng, WireFormatNano.EMPTY_BYTES)) {
            return size + CodedOutputByteBufferNano.computeBytesSize(4, this.bytePng);
        }
        return size;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public S2TileProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            switch (tag) {
                case 0:
                    return this;
                case 10:
                    this.tileKey = input.readString();
                    break;
                case 18:
                    this.byteBuffer = input.readBytes();
                    break;
                case 26:
                    this.byteJpeg = input.readBytes();
                    break;
                case 34:
                    this.bytePng = input.readBytes();
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

    public static S2TileProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (S2TileProto) MessageNano.mergeFrom(new S2TileProto(), data);
    }

    public static S2TileProto parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new S2TileProto().mergeFrom(input);
    }
}
