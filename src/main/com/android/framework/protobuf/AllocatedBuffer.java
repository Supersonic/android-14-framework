package com.android.framework.protobuf;

import java.nio.ByteBuffer;
@CheckReturnValue
/* loaded from: classes4.dex */
abstract class AllocatedBuffer {
    public abstract byte[] array();

    public abstract int arrayOffset();

    public abstract boolean hasArray();

    public abstract boolean hasNioBuffer();

    public abstract int limit();

    public abstract ByteBuffer nioBuffer();

    public abstract int position();

    public abstract AllocatedBuffer position(int i);

    public abstract int remaining();

    AllocatedBuffer() {
    }

    public static AllocatedBuffer wrap(byte[] bytes) {
        return wrapNoCheck(bytes, 0, bytes.length);
    }

    public static AllocatedBuffer wrap(byte[] bytes, int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > bytes.length) {
            throw new IndexOutOfBoundsException(String.format("bytes.length=%d, offset=%d, length=%d", Integer.valueOf(bytes.length), Integer.valueOf(offset), Integer.valueOf(length)));
        }
        return wrapNoCheck(bytes, offset, length);
    }

    public static AllocatedBuffer wrap(final ByteBuffer buffer) {
        Internal.checkNotNull(buffer, "buffer");
        return new AllocatedBuffer() { // from class: com.android.framework.protobuf.AllocatedBuffer.1
            @Override // com.android.framework.protobuf.AllocatedBuffer
            public boolean hasNioBuffer() {
                return true;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public ByteBuffer nioBuffer() {
                return buffer;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public boolean hasArray() {
                return buffer.hasArray();
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public byte[] array() {
                return buffer.array();
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public int arrayOffset() {
                return buffer.arrayOffset();
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public int position() {
                return buffer.position();
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public AllocatedBuffer position(int position) {
                buffer.position(position);
                return this;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public int limit() {
                return buffer.limit();
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public int remaining() {
                return buffer.remaining();
            }
        };
    }

    private static AllocatedBuffer wrapNoCheck(final byte[] bytes, final int offset, final int length) {
        return new AllocatedBuffer() { // from class: com.android.framework.protobuf.AllocatedBuffer.2
            private int position;

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public boolean hasNioBuffer() {
                return false;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public ByteBuffer nioBuffer() {
                throw new UnsupportedOperationException();
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public boolean hasArray() {
                return true;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public byte[] array() {
                return bytes;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public int arrayOffset() {
                return offset;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public int position() {
                return this.position;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public AllocatedBuffer position(int position) {
                if (position < 0 || position > length) {
                    throw new IllegalArgumentException("Invalid position: " + position);
                }
                this.position = position;
                return this;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public int limit() {
                return length;
            }

            @Override // com.android.framework.protobuf.AllocatedBuffer
            public int remaining() {
                return length - this.position;
            }
        };
    }
}
