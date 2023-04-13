package com.android.framework.protobuf;

import com.android.framework.protobuf.AbstractMessageLite;
import com.android.framework.protobuf.AbstractMessageLite.Builder;
import com.android.framework.protobuf.ByteString;
import com.android.framework.protobuf.MessageLite;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/* loaded from: classes4.dex */
public abstract class AbstractMessageLite<MessageType extends AbstractMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> implements MessageLite {
    protected int memoizedHashCode = 0;

    /* loaded from: classes4.dex */
    protected interface InternalOneOfEnum {
        int getNumber();
    }

    @Override // com.android.framework.protobuf.MessageLite
    public ByteString toByteString() {
        try {
            ByteString.CodedBuilder out = ByteString.newCodedBuilder(getSerializedSize());
            writeTo(out.getCodedOutput());
            return out.build();
        } catch (IOException e) {
            throw new RuntimeException(getSerializingExceptionMessage("ByteString"), e);
        }
    }

    @Override // com.android.framework.protobuf.MessageLite
    public byte[] toByteArray() {
        try {
            byte[] result = new byte[getSerializedSize()];
            CodedOutputStream output = CodedOutputStream.newInstance(result);
            writeTo(output);
            output.checkNoSpaceLeft();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(getSerializingExceptionMessage("byte array"), e);
        }
    }

    @Override // com.android.framework.protobuf.MessageLite
    public void writeTo(OutputStream output) throws IOException {
        int bufferSize = CodedOutputStream.computePreferredBufferSize(getSerializedSize());
        CodedOutputStream codedOutput = CodedOutputStream.newInstance(output, bufferSize);
        writeTo(codedOutput);
        codedOutput.flush();
    }

    @Override // com.android.framework.protobuf.MessageLite
    public void writeDelimitedTo(OutputStream output) throws IOException {
        int serialized = getSerializedSize();
        int bufferSize = CodedOutputStream.computePreferredBufferSize(CodedOutputStream.computeUInt32SizeNoTag(serialized) + serialized);
        CodedOutputStream codedOutput = CodedOutputStream.newInstance(output, bufferSize);
        codedOutput.writeUInt32NoTag(serialized);
        writeTo(codedOutput);
        codedOutput.flush();
    }

    int getMemoizedSerializedSize() {
        throw new UnsupportedOperationException();
    }

    void setMemoizedSerializedSize(int size) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSerializedSize(Schema schema) {
        int memoizedSerializedSize = getMemoizedSerializedSize();
        if (memoizedSerializedSize == -1) {
            int memoizedSerializedSize2 = schema.getSerializedSize(this);
            setMemoizedSerializedSize(memoizedSerializedSize2);
            return memoizedSerializedSize2;
        }
        return memoizedSerializedSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UninitializedMessageException newUninitializedMessageException() {
        return new UninitializedMessageException(this);
    }

    private String getSerializingExceptionMessage(String target) {
        return "Serializing " + getClass().getName() + " to a " + target + " threw an IOException (should never happen).";
    }

    protected static void checkByteStringIsUtf8(ByteString byteString) throws IllegalArgumentException {
        if (!byteString.isValidUtf8()) {
            throw new IllegalArgumentException("Byte string is not UTF-8.");
        }
    }

    @Deprecated
    protected static <T> void addAll(Iterable<T> values, Collection<? super T> list) {
        Builder.addAll((Iterable) values, (List) list);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static <T> void addAll(Iterable<T> values, List<? super T> list) {
        Builder.addAll((Iterable) values, (List) list);
    }

    /* loaded from: classes4.dex */
    public static abstract class Builder<MessageType extends AbstractMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> implements MessageLite.Builder {
        @Override // 
        /* renamed from: clone */
        public abstract BuilderType mo6504clone();

        protected abstract BuilderType internalMergeFrom(MessageType messagetype);

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public abstract BuilderType mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException;

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(CodedInputStream input) throws IOException {
            return mergeFrom(input, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(ByteString data) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = data.newCodedInput();
                mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            } catch (InvalidProtocolBufferException e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException(getReadingExceptionMessage("ByteString"), e2);
            }
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = data.newCodedInput();
                mergeFrom(input, extensionRegistry);
                input.checkLastTagWas(0);
                return this;
            } catch (InvalidProtocolBufferException e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException(getReadingExceptionMessage("ByteString"), e2);
            }
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(byte[] data) throws InvalidProtocolBufferException {
            return mergeFrom(data, 0, data.length);
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = CodedInputStream.newInstance(data, off, len);
                mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            } catch (InvalidProtocolBufferException e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException(getReadingExceptionMessage("byte array"), e2);
            }
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return mergeFrom(data, 0, data.length, extensionRegistry);
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = CodedInputStream.newInstance(data, off, len);
                mergeFrom(input, extensionRegistry);
                input.checkLastTagWas(0);
                return this;
            } catch (InvalidProtocolBufferException e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException(getReadingExceptionMessage("byte array"), e2);
            }
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(InputStream input) throws IOException {
            CodedInputStream codedInput = CodedInputStream.newInstance(input);
            mergeFrom(codedInput);
            codedInput.checkLastTagWas(0);
            return this;
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            CodedInputStream codedInput = CodedInputStream.newInstance(input);
            mergeFrom(codedInput, extensionRegistry);
            codedInput.checkLastTagWas(0);
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes4.dex */
        public static final class LimitedInputStream extends FilterInputStream {
            private int limit;

            /* JADX INFO: Access modifiers changed from: package-private */
            public LimitedInputStream(InputStream in, int limit) {
                super(in);
                this.limit = limit;
            }

            @Override // java.io.FilterInputStream, java.io.InputStream
            public int available() throws IOException {
                return Math.min(super.available(), this.limit);
            }

            @Override // java.io.FilterInputStream, java.io.InputStream
            public int read() throws IOException {
                if (this.limit <= 0) {
                    return -1;
                }
                int result = super.read();
                if (result >= 0) {
                    this.limit--;
                }
                return result;
            }

            @Override // java.io.FilterInputStream, java.io.InputStream
            public int read(byte[] b, int off, int len) throws IOException {
                int i = this.limit;
                if (i <= 0) {
                    return -1;
                }
                int result = super.read(b, off, Math.min(len, i));
                if (result >= 0) {
                    this.limit -= result;
                }
                return result;
            }

            @Override // java.io.FilterInputStream, java.io.InputStream
            public long skip(long n) throws IOException {
                int result = (int) super.skip(Math.min(n, this.limit));
                if (result >= 0) {
                    this.limit -= result;
                }
                return result;
            }
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            int firstByte = input.read();
            if (firstByte == -1) {
                return false;
            }
            int size = CodedInputStream.readRawVarint32(firstByte, input);
            InputStream limitedInput = new LimitedInputStream(input, size);
            mergeFrom(limitedInput, extensionRegistry);
            return true;
        }

        @Override // com.android.framework.protobuf.MessageLite.Builder
        public boolean mergeDelimitedFrom(InputStream input) throws IOException {
            return mergeDelimitedFrom(input, ExtensionRegistryLite.getEmptyRegistry());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.android.framework.protobuf.MessageLite.Builder
        public BuilderType mergeFrom(MessageLite other) {
            if (!getDefaultInstanceForType().getClass().isInstance(other)) {
                throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
            }
            return (BuilderType) internalMergeFrom((AbstractMessageLite) other);
        }

        private String getReadingExceptionMessage(String target) {
            return "Reading " + getClass().getName() + " from a " + target + " threw an IOException (should never happen).";
        }

        private static <T> void addAllCheckingNulls(Iterable<T> values, List<? super T> list) {
            if ((list instanceof ArrayList) && (values instanceof Collection)) {
                ((ArrayList) list).ensureCapacity(list.size() + ((Collection) values).size());
            }
            int begin = list.size();
            for (T value : values) {
                if (value == null) {
                    String message = "Element at index " + (list.size() - begin) + " is null.";
                    for (int i = list.size() - 1; i >= begin; i--) {
                        list.remove(i);
                    }
                    throw new NullPointerException(message);
                }
                list.add(value);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public static UninitializedMessageException newUninitializedMessageException(MessageLite message) {
            return new UninitializedMessageException(message);
        }

        @Deprecated
        protected static <T> void addAll(Iterable<T> values, Collection<? super T> list) {
            addAll((Iterable) values, (List) list);
        }

        protected static <T> void addAll(Iterable<T> values, List<? super T> list) {
            Internal.checkNotNull(values);
            if (values instanceof LazyStringList) {
                List<?> lazyValues = ((LazyStringList) values).getUnderlyingElements();
                LazyStringList lazyList = (LazyStringList) list;
                int begin = list.size();
                for (Object value : lazyValues) {
                    if (value == null) {
                        String message = "Element at index " + (lazyList.size() - begin) + " is null.";
                        for (int i = lazyList.size() - 1; i >= begin; i--) {
                            lazyList.remove(i);
                        }
                        throw new NullPointerException(message);
                    } else if (value instanceof ByteString) {
                        lazyList.add((ByteString) value);
                    } else {
                        lazyList.add((String) value);
                    }
                }
            } else if (values instanceof PrimitiveNonBoxingCollection) {
                list.addAll((Collection) values);
            } else {
                addAllCheckingNulls(values, list);
            }
        }
    }
}
