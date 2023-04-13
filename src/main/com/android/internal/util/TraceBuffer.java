package com.android.internal.util;

import android.util.proto.ProtoOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes3.dex */
public class TraceBuffer<P, S extends P, T extends P> {
    private final Queue<T> mBuffer;
    private int mBufferCapacity;
    private final Object mBufferLock;
    private int mBufferUsedSize;
    private final Consumer mProtoDequeuedCallback;
    private final ProtoProvider<P, S, T> mProtoProvider;

    /* loaded from: classes3.dex */
    public interface ProtoProvider<P, S extends P, T extends P> {
        byte[] getBytes(P p);

        int getItemSize(P p);

        void write(S s, Queue<T> queue, OutputStream outputStream) throws IOException;
    }

    /* loaded from: classes3.dex */
    private static class ProtoOutputStreamProvider implements ProtoProvider<ProtoOutputStream, ProtoOutputStream, ProtoOutputStream> {
        private ProtoOutputStreamProvider() {
        }

        @Override // com.android.internal.util.TraceBuffer.ProtoProvider
        public int getItemSize(ProtoOutputStream proto) {
            return proto.getRawSize();
        }

        @Override // com.android.internal.util.TraceBuffer.ProtoProvider
        public byte[] getBytes(ProtoOutputStream proto) {
            return proto.getBytes();
        }

        @Override // com.android.internal.util.TraceBuffer.ProtoProvider
        public void write(ProtoOutputStream encapsulatingProto, Queue<ProtoOutputStream> buffer, OutputStream os) throws IOException {
            os.write(encapsulatingProto.getBytes());
            for (ProtoOutputStream protoOutputStream : buffer) {
                byte[] protoBytes = protoOutputStream.getBytes();
                os.write(protoBytes);
            }
        }
    }

    public TraceBuffer(int bufferCapacity) {
        this(bufferCapacity, new ProtoOutputStreamProvider(), null);
    }

    public TraceBuffer(int bufferCapacity, ProtoProvider protoProvider, Consumer<T> protoDequeuedCallback) {
        this.mBufferLock = new Object();
        this.mBuffer = new ArrayDeque();
        this.mBufferCapacity = bufferCapacity;
        this.mProtoProvider = protoProvider;
        this.mProtoDequeuedCallback = protoDequeuedCallback;
        resetBuffer();
    }

    public int getAvailableSpace() {
        return this.mBufferCapacity - this.mBufferUsedSize;
    }

    public int size() {
        return this.mBuffer.size();
    }

    public void setCapacity(int capacity) {
        this.mBufferCapacity = capacity;
    }

    public void add(T proto) {
        int protoLength = this.mProtoProvider.getItemSize(proto);
        if (protoLength > this.mBufferCapacity) {
            throw new IllegalStateException("Trace object too large for the buffer. Buffer size:" + this.mBufferCapacity + " Object size: " + protoLength);
        }
        synchronized (this.mBufferLock) {
            discardOldest(protoLength);
            this.mBuffer.add(proto);
            this.mBufferUsedSize += protoLength;
            this.mBufferLock.notify();
        }
    }

    public boolean contains(final byte[] other) {
        return this.mBuffer.stream().anyMatch(new Predicate() { // from class: com.android.internal.util.TraceBuffer$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$contains$0;
                lambda$contains$0 = TraceBuffer.this.lambda$contains$0(other, obj);
                return lambda$contains$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$contains$0(byte[] other, Object p) {
        return Arrays.equals(this.mProtoProvider.getBytes(p), other);
    }

    public void writeTraceToFile(File traceFile, S encapsulatingProto) throws IOException {
        synchronized (this.mBufferLock) {
            traceFile.delete();
            OutputStream os = new FileOutputStream(traceFile);
            traceFile.setReadable(true, false);
            this.mProtoProvider.write(encapsulatingProto, this.mBuffer, os);
            os.flush();
            os.close();
        }
    }

    private void discardOldest(int protoLength) {
        long availableSpace = getAvailableSpace();
        while (availableSpace < protoLength) {
            T poll = this.mBuffer.poll();
            if (poll == null) {
                throw new IllegalStateException("No element to discard from buffer");
            }
            this.mBufferUsedSize -= this.mProtoProvider.getItemSize(poll);
            availableSpace = getAvailableSpace();
            Consumer consumer = this.mProtoDequeuedCallback;
            if (consumer != null) {
                consumer.accept(poll);
            }
        }
    }

    public void resetBuffer() {
        synchronized (this.mBufferLock) {
            if (this.mProtoDequeuedCallback != null) {
                for (T item : this.mBuffer) {
                    this.mProtoDequeuedCallback.accept(item);
                }
            }
            this.mBuffer.clear();
            this.mBufferUsedSize = 0;
        }
    }

    public int getBufferSize() {
        return this.mBufferUsedSize;
    }

    public String getStatus() {
        String str;
        synchronized (this.mBufferLock) {
            str = "Buffer size: " + this.mBufferCapacity + " bytes\nBuffer usage: " + this.mBufferUsedSize + " bytes\nElements in the buffer: " + this.mBuffer.size();
        }
        return str;
    }
}
