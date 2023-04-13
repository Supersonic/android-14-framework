package com.android.framework.protobuf.nano;

import java.io.IOException;
import java.util.Arrays;
/* loaded from: classes4.dex */
final class UnknownFieldData {
    final byte[] bytes;
    final int tag;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UnknownFieldData(int tag, byte[] bytes) {
        this.tag = tag;
        this.bytes = bytes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int computeSerializedSize() {
        int size = 0 + CodedOutputByteBufferNano.computeRawVarint32Size(this.tag);
        return size + this.bytes.length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        output.writeRawVarint32(this.tag);
        output.writeRawBytes(this.bytes);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof UnknownFieldData) {
            UnknownFieldData other = (UnknownFieldData) o;
            return this.tag == other.tag && Arrays.equals(this.bytes, other.bytes);
        }
        return false;
    }

    public int hashCode() {
        int result = (17 * 31) + this.tag;
        return (result * 31) + Arrays.hashCode(this.bytes);
    }
}
