package com.google.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
@CheckReturnValue
/* loaded from: classes.dex */
public final class CodedInputStreamReader implements Reader {
    private static final int FIXED32_MULTIPLE_MASK = 3;
    private static final int FIXED64_MULTIPLE_MASK = 7;
    private static final int NEXT_TAG_UNSET = 0;
    private int endGroupTag;
    private final CodedInputStream input;
    private int nextTag = 0;
    private int tag;

    public static CodedInputStreamReader forCodedInput(CodedInputStream input) {
        if (input.wrapper != null) {
            return input.wrapper;
        }
        return new CodedInputStreamReader(input);
    }

    private CodedInputStreamReader(CodedInputStream input) {
        CodedInputStream codedInputStream = (CodedInputStream) Internal.checkNotNull(input, "input");
        this.input = codedInputStream;
        codedInputStream.wrapper = this;
    }

    @Override // com.google.protobuf.Reader
    public boolean shouldDiscardUnknownFields() {
        return this.input.shouldDiscardUnknownFields();
    }

    @Override // com.google.protobuf.Reader
    public int getFieldNumber() throws IOException {
        int i = this.nextTag;
        if (i != 0) {
            this.tag = i;
            this.nextTag = 0;
        } else {
            this.tag = this.input.readTag();
        }
        int i2 = this.tag;
        if (i2 == 0 || i2 == this.endGroupTag) {
            return Reader.READ_DONE;
        }
        return WireFormat.getTagFieldNumber(i2);
    }

    @Override // com.google.protobuf.Reader
    public int getTag() {
        return this.tag;
    }

    @Override // com.google.protobuf.Reader
    public boolean skipField() throws IOException {
        int i;
        if (this.input.isAtEnd() || (i = this.tag) == this.endGroupTag) {
            return false;
        }
        return this.input.skipField(i);
    }

    private void requireWireType(int requiredWireType) throws IOException {
        if (WireFormat.getTagWireType(this.tag) != requiredWireType) {
            throw InvalidProtocolBufferException.invalidWireType();
        }
    }

    @Override // com.google.protobuf.Reader
    public double readDouble() throws IOException {
        requireWireType(1);
        return this.input.readDouble();
    }

    @Override // com.google.protobuf.Reader
    public float readFloat() throws IOException {
        requireWireType(5);
        return this.input.readFloat();
    }

    @Override // com.google.protobuf.Reader
    public long readUInt64() throws IOException {
        requireWireType(0);
        return this.input.readUInt64();
    }

    @Override // com.google.protobuf.Reader
    public long readInt64() throws IOException {
        requireWireType(0);
        return this.input.readInt64();
    }

    @Override // com.google.protobuf.Reader
    public int readInt32() throws IOException {
        requireWireType(0);
        return this.input.readInt32();
    }

    @Override // com.google.protobuf.Reader
    public long readFixed64() throws IOException {
        requireWireType(1);
        return this.input.readFixed64();
    }

    @Override // com.google.protobuf.Reader
    public int readFixed32() throws IOException {
        requireWireType(5);
        return this.input.readFixed32();
    }

    @Override // com.google.protobuf.Reader
    public boolean readBool() throws IOException {
        requireWireType(0);
        return this.input.readBool();
    }

    @Override // com.google.protobuf.Reader
    public String readString() throws IOException {
        requireWireType(2);
        return this.input.readString();
    }

    @Override // com.google.protobuf.Reader
    public String readStringRequireUtf8() throws IOException {
        requireWireType(2);
        return this.input.readStringRequireUtf8();
    }

    @Override // com.google.protobuf.Reader
    public <T> T readMessage(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
        requireWireType(2);
        return (T) readMessage(Protobuf.getInstance().schemaFor((Class) clazz), extensionRegistry);
    }

    @Override // com.google.protobuf.Reader
    public <T> T readMessageBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        requireWireType(2);
        return (T) readMessage(schema, extensionRegistry);
    }

    @Override // com.google.protobuf.Reader
    @Deprecated
    public <T> T readGroup(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
        requireWireType(3);
        return (T) readGroup(Protobuf.getInstance().schemaFor((Class) clazz), extensionRegistry);
    }

    @Override // com.google.protobuf.Reader
    @Deprecated
    public <T> T readGroupBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        requireWireType(3);
        return (T) readGroup(schema, extensionRegistry);
    }

    @Override // com.google.protobuf.Reader
    public <T> void mergeMessageField(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        requireWireType(2);
        mergeMessageFieldInternal(target, schema, extensionRegistry);
    }

    private <T> void mergeMessageFieldInternal(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        int size = this.input.readUInt32();
        if (this.input.recursionDepth >= this.input.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
        }
        int prevLimit = this.input.pushLimit(size);
        this.input.recursionDepth++;
        schema.mergeFrom(target, this, extensionRegistry);
        this.input.checkLastTagWas(0);
        CodedInputStream codedInputStream = this.input;
        codedInputStream.recursionDepth--;
        this.input.popLimit(prevLimit);
    }

    private <T> T readMessage(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        T newInstance = schema.newInstance();
        mergeMessageFieldInternal(newInstance, schema, extensionRegistry);
        schema.makeImmutable(newInstance);
        return newInstance;
    }

    @Override // com.google.protobuf.Reader
    public <T> void mergeGroupField(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        requireWireType(3);
        mergeGroupFieldInternal(target, schema, extensionRegistry);
    }

    private <T> void mergeGroupFieldInternal(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        int prevEndGroupTag = this.endGroupTag;
        this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);
        try {
            schema.mergeFrom(target, this, extensionRegistry);
            if (this.tag != this.endGroupTag) {
                throw InvalidProtocolBufferException.parseFailure();
            }
        } finally {
            this.endGroupTag = prevEndGroupTag;
        }
    }

    private <T> T readGroup(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        T newInstance = schema.newInstance();
        mergeGroupFieldInternal(newInstance, schema, extensionRegistry);
        schema.makeImmutable(newInstance);
        return newInstance;
    }

    @Override // com.google.protobuf.Reader
    public ByteString readBytes() throws IOException {
        requireWireType(2);
        return this.input.readBytes();
    }

    @Override // com.google.protobuf.Reader
    public int readUInt32() throws IOException {
        requireWireType(0);
        return this.input.readUInt32();
    }

    @Override // com.google.protobuf.Reader
    public int readEnum() throws IOException {
        requireWireType(0);
        return this.input.readEnum();
    }

    @Override // com.google.protobuf.Reader
    public int readSFixed32() throws IOException {
        requireWireType(5);
        return this.input.readSFixed32();
    }

    @Override // com.google.protobuf.Reader
    public long readSFixed64() throws IOException {
        requireWireType(1);
        return this.input.readSFixed64();
    }

    @Override // com.google.protobuf.Reader
    public int readSInt32() throws IOException {
        requireWireType(0);
        return this.input.readSInt32();
    }

    @Override // com.google.protobuf.Reader
    public long readSInt64() throws IOException {
        requireWireType(0);
        return this.input.readSInt64();
    }

    @Override // com.google.protobuf.Reader
    public void readDoubleList(List<Double> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof DoubleArrayList) {
            DoubleArrayList plist = (DoubleArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 1:
                    break;
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    verifyPackedFixed64Length(bytes);
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addDouble(this.input.readDouble());
                    } while (this.input.getTotalBytesRead() < endPos);
                    return;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
            do {
                plist.addDouble(this.input.readDouble());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
                break;
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                verifyPackedFixed64Length(bytes2);
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Double.valueOf(this.input.readDouble()));
                } while (this.input.getTotalBytesRead() < endPos2);
                return;
            default:
                throw InvalidProtocolBufferException.invalidWireType();
        }
        do {
            target.add(Double.valueOf(this.input.readDouble()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readFloatList(List<Float> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof FloatArrayList) {
            FloatArrayList plist = (FloatArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    verifyPackedFixed32Length(bytes);
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addFloat(this.input.readFloat());
                    } while (this.input.getTotalBytesRead() < endPos);
                    return;
                case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                    break;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
            do {
                plist.addFloat(this.input.readFloat());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                verifyPackedFixed32Length(bytes2);
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Float.valueOf(this.input.readFloat()));
                } while (this.input.getTotalBytesRead() < endPos2);
                return;
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                break;
            default:
                throw InvalidProtocolBufferException.invalidWireType();
        }
        do {
            target.add(Float.valueOf(this.input.readFloat()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readUInt64List(List<Long> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof LongArrayList) {
            LongArrayList plist = (LongArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 0:
                    break;
                case 1:
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addLong(this.input.readUInt64());
                    } while (this.input.getTotalBytesRead() < endPos);
                    requirePosition(endPos);
                    return;
            }
            do {
                plist.addLong(this.input.readUInt64());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
                break;
            case 1:
            default:
                throw InvalidProtocolBufferException.invalidWireType();
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Long.valueOf(this.input.readUInt64()));
                } while (this.input.getTotalBytesRead() < endPos2);
                requirePosition(endPos2);
                return;
        }
        do {
            target.add(Long.valueOf(this.input.readUInt64()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readInt64List(List<Long> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof LongArrayList) {
            LongArrayList plist = (LongArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 0:
                    break;
                case 1:
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addLong(this.input.readInt64());
                    } while (this.input.getTotalBytesRead() < endPos);
                    requirePosition(endPos);
                    return;
            }
            do {
                plist.addLong(this.input.readInt64());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
                break;
            case 1:
            default:
                throw InvalidProtocolBufferException.invalidWireType();
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Long.valueOf(this.input.readInt64()));
                } while (this.input.getTotalBytesRead() < endPos2);
                requirePosition(endPos2);
                return;
        }
        do {
            target.add(Long.valueOf(this.input.readInt64()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readInt32List(List<Integer> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof IntArrayList) {
            IntArrayList plist = (IntArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 0:
                    break;
                case 1:
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addInt(this.input.readInt32());
                    } while (this.input.getTotalBytesRead() < endPos);
                    requirePosition(endPos);
                    return;
            }
            do {
                plist.addInt(this.input.readInt32());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
                break;
            case 1:
            default:
                throw InvalidProtocolBufferException.invalidWireType();
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Integer.valueOf(this.input.readInt32()));
                } while (this.input.getTotalBytesRead() < endPos2);
                requirePosition(endPos2);
                return;
        }
        do {
            target.add(Integer.valueOf(this.input.readInt32()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readFixed64List(List<Long> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof LongArrayList) {
            LongArrayList plist = (LongArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 1:
                    break;
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    verifyPackedFixed64Length(bytes);
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addLong(this.input.readFixed64());
                    } while (this.input.getTotalBytesRead() < endPos);
                    return;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
            do {
                plist.addLong(this.input.readFixed64());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
                break;
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                verifyPackedFixed64Length(bytes2);
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Long.valueOf(this.input.readFixed64()));
                } while (this.input.getTotalBytesRead() < endPos2);
                return;
            default:
                throw InvalidProtocolBufferException.invalidWireType();
        }
        do {
            target.add(Long.valueOf(this.input.readFixed64()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readFixed32List(List<Integer> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof IntArrayList) {
            IntArrayList plist = (IntArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    verifyPackedFixed32Length(bytes);
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addInt(this.input.readFixed32());
                    } while (this.input.getTotalBytesRead() < endPos);
                    return;
                case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                    break;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
            do {
                plist.addInt(this.input.readFixed32());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                verifyPackedFixed32Length(bytes2);
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Integer.valueOf(this.input.readFixed32()));
                } while (this.input.getTotalBytesRead() < endPos2);
                return;
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                break;
            default:
                throw InvalidProtocolBufferException.invalidWireType();
        }
        do {
            target.add(Integer.valueOf(this.input.readFixed32()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readBoolList(List<Boolean> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof BooleanArrayList) {
            BooleanArrayList plist = (BooleanArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 0:
                    break;
                case 1:
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addBoolean(this.input.readBool());
                    } while (this.input.getTotalBytesRead() < endPos);
                    requirePosition(endPos);
                    return;
            }
            do {
                plist.addBoolean(this.input.readBool());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
                break;
            case 1:
            default:
                throw InvalidProtocolBufferException.invalidWireType();
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Boolean.valueOf(this.input.readBool()));
                } while (this.input.getTotalBytesRead() < endPos2);
                requirePosition(endPos2);
                return;
        }
        do {
            target.add(Boolean.valueOf(this.input.readBool()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readStringList(List<String> target) throws IOException {
        readStringListInternal(target, false);
    }

    @Override // com.google.protobuf.Reader
    public void readStringListRequireUtf8(List<String> target) throws IOException {
        readStringListInternal(target, true);
    }

    public void readStringListInternal(List<String> target, boolean requireUtf8) throws IOException {
        int nextTag;
        int nextTag2;
        if (WireFormat.getTagWireType(this.tag) != 2) {
            throw InvalidProtocolBufferException.invalidWireType();
        }
        if ((target instanceof LazyStringList) && !requireUtf8) {
            LazyStringList lazyList = (LazyStringList) target;
            do {
                lazyList.add(readBytes());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        do {
            target.add(requireUtf8 ? readStringRequireUtf8() : readString());
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public <T> void readMessageList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
        Schema<T> schema = Protobuf.getInstance().schemaFor((Class) targetType);
        readMessageList(target, schema, extensionRegistry);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.protobuf.Reader
    public <T> void readMessageList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        int nextTag;
        if (WireFormat.getTagWireType(this.tag) != 2) {
            throw InvalidProtocolBufferException.invalidWireType();
        }
        int listTag = this.tag;
        do {
            target.add(readMessage(schema, extensionRegistry));
            if (this.input.isAtEnd() || this.nextTag != 0) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == listTag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    @Deprecated
    public <T> void readGroupList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
        Schema<T> schema = Protobuf.getInstance().schemaFor((Class) targetType);
        readGroupList(target, schema, extensionRegistry);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.protobuf.Reader
    @Deprecated
    public <T> void readGroupList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        int nextTag;
        if (WireFormat.getTagWireType(this.tag) != 3) {
            throw InvalidProtocolBufferException.invalidWireType();
        }
        int listTag = this.tag;
        do {
            target.add(readGroup(schema, extensionRegistry));
            if (this.input.isAtEnd() || this.nextTag != 0) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == listTag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readBytesList(List<ByteString> target) throws IOException {
        int nextTag;
        if (WireFormat.getTagWireType(this.tag) != 2) {
            throw InvalidProtocolBufferException.invalidWireType();
        }
        do {
            target.add(readBytes());
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readUInt32List(List<Integer> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof IntArrayList) {
            IntArrayList plist = (IntArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 0:
                    break;
                case 1:
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addInt(this.input.readUInt32());
                    } while (this.input.getTotalBytesRead() < endPos);
                    requirePosition(endPos);
                    return;
            }
            do {
                plist.addInt(this.input.readUInt32());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
                break;
            case 1:
            default:
                throw InvalidProtocolBufferException.invalidWireType();
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Integer.valueOf(this.input.readUInt32()));
                } while (this.input.getTotalBytesRead() < endPos2);
                requirePosition(endPos2);
                return;
        }
        do {
            target.add(Integer.valueOf(this.input.readUInt32()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readEnumList(List<Integer> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof IntArrayList) {
            IntArrayList plist = (IntArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 0:
                    break;
                case 1:
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addInt(this.input.readEnum());
                    } while (this.input.getTotalBytesRead() < endPos);
                    requirePosition(endPos);
                    return;
            }
            do {
                plist.addInt(this.input.readEnum());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
                break;
            case 1:
            default:
                throw InvalidProtocolBufferException.invalidWireType();
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Integer.valueOf(this.input.readEnum()));
                } while (this.input.getTotalBytesRead() < endPos2);
                requirePosition(endPos2);
                return;
        }
        do {
            target.add(Integer.valueOf(this.input.readEnum()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readSFixed32List(List<Integer> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof IntArrayList) {
            IntArrayList plist = (IntArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    verifyPackedFixed32Length(bytes);
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addInt(this.input.readSFixed32());
                    } while (this.input.getTotalBytesRead() < endPos);
                    return;
                case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                    break;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
            do {
                plist.addInt(this.input.readSFixed32());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                verifyPackedFixed32Length(bytes2);
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Integer.valueOf(this.input.readSFixed32()));
                } while (this.input.getTotalBytesRead() < endPos2);
                return;
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                break;
            default:
                throw InvalidProtocolBufferException.invalidWireType();
        }
        do {
            target.add(Integer.valueOf(this.input.readSFixed32()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readSFixed64List(List<Long> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof LongArrayList) {
            LongArrayList plist = (LongArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 1:
                    break;
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    verifyPackedFixed64Length(bytes);
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addLong(this.input.readSFixed64());
                    } while (this.input.getTotalBytesRead() < endPos);
                    return;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
            do {
                plist.addLong(this.input.readSFixed64());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
                break;
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                verifyPackedFixed64Length(bytes2);
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Long.valueOf(this.input.readSFixed64()));
                } while (this.input.getTotalBytesRead() < endPos2);
                return;
            default:
                throw InvalidProtocolBufferException.invalidWireType();
        }
        do {
            target.add(Long.valueOf(this.input.readSFixed64()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readSInt32List(List<Integer> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof IntArrayList) {
            IntArrayList plist = (IntArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 0:
                    break;
                case 1:
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addInt(this.input.readSInt32());
                    } while (this.input.getTotalBytesRead() < endPos);
                    requirePosition(endPos);
                    return;
            }
            do {
                plist.addInt(this.input.readSInt32());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
                break;
            case 1:
            default:
                throw InvalidProtocolBufferException.invalidWireType();
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Integer.valueOf(this.input.readSInt32()));
                } while (this.input.getTotalBytesRead() < endPos2);
                requirePosition(endPos2);
                return;
        }
        do {
            target.add(Integer.valueOf(this.input.readSInt32()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    @Override // com.google.protobuf.Reader
    public void readSInt64List(List<Long> target) throws IOException {
        int nextTag;
        int nextTag2;
        if (target instanceof LongArrayList) {
            LongArrayList plist = (LongArrayList) target;
            switch (WireFormat.getTagWireType(this.tag)) {
                case 0:
                    break;
                case 1:
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    int bytes = this.input.readUInt32();
                    int endPos = this.input.getTotalBytesRead() + bytes;
                    do {
                        plist.addLong(this.input.readSInt64());
                    } while (this.input.getTotalBytesRead() < endPos);
                    requirePosition(endPos);
                    return;
            }
            do {
                plist.addLong(this.input.readSInt64());
                if (this.input.isAtEnd()) {
                    return;
                }
                nextTag2 = this.input.readTag();
            } while (nextTag2 == this.tag);
            this.nextTag = nextTag2;
            return;
        }
        switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
                break;
            case 1:
            default:
                throw InvalidProtocolBufferException.invalidWireType();
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                int bytes2 = this.input.readUInt32();
                int endPos2 = this.input.getTotalBytesRead() + bytes2;
                do {
                    target.add(Long.valueOf(this.input.readSInt64()));
                } while (this.input.getTotalBytesRead() < endPos2);
                requirePosition(endPos2);
                return;
        }
        do {
            target.add(Long.valueOf(this.input.readSInt64()));
            if (this.input.isAtEnd()) {
                return;
            }
            nextTag = this.input.readTag();
        } while (nextTag == this.tag);
        this.nextTag = nextTag;
    }

    private void verifyPackedFixed64Length(int bytes) throws IOException {
        if ((bytes & FIXED64_MULTIPLE_MASK) != 0) {
            throw InvalidProtocolBufferException.parseFailure();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.protobuf.Reader
    public <K, V> void readMap(Map<K, V> target, MapEntryLite.Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
        requireWireType(2);
        int size = this.input.readUInt32();
        int prevLimit = this.input.pushLimit(size);
        Object obj = metadata.defaultKey;
        Object obj2 = metadata.defaultValue;
        while (true) {
            try {
                int number = getFieldNumber();
                if (number != Integer.MAX_VALUE && !this.input.isAtEnd()) {
                    switch (number) {
                        case 1:
                            obj = readField(metadata.keyType, null, null);
                            break;
                        case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                            obj2 = readField(metadata.valueType, metadata.defaultValue.getClass(), extensionRegistry);
                            break;
                        default:
                            try {
                                if (!skipField()) {
                                    throw new InvalidProtocolBufferException("Unable to parse map entry.");
                                    break;
                                }
                            } catch (InvalidProtocolBufferException.InvalidWireTypeException e) {
                                if (!skipField()) {
                                    throw new InvalidProtocolBufferException("Unable to parse map entry.");
                                }
                                break;
                            }
                            break;
                    }
                }
            } finally {
                this.input.popLimit(prevLimit);
            }
        }
        target.put(obj, obj2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.google.protobuf.CodedInputStreamReader$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C00111 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$FieldType;

        static {
            int[] iArr = new int[WireFormat.FieldType.values().length];
            $SwitchMap$com$google$protobuf$WireFormat$FieldType = iArr;
            try {
                iArr[WireFormat.FieldType.BOOL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BYTES.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.DOUBLE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.ENUM.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED32.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED64.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FLOAT.ordinal()] = CodedInputStreamReader.FIXED64_MULTIPLE_MASK;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT32.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT64.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.MESSAGE.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED32.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED64.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT32.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT64.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.STRING.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT32.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT64.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
        }
    }

    private Object readField(WireFormat.FieldType fieldType, Class<?> messageType, ExtensionRegistryLite extensionRegistry) throws IOException {
        switch (C00111.$SwitchMap$com$google$protobuf$WireFormat$FieldType[fieldType.ordinal()]) {
            case 1:
                return Boolean.valueOf(readBool());
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                return readBytes();
            case 3:
                return Double.valueOf(readDouble());
            case 4:
                return Integer.valueOf(readEnum());
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                return Integer.valueOf(readFixed32());
            case 6:
                return Long.valueOf(readFixed64());
            case FIXED64_MULTIPLE_MASK /* 7 */:
                return Float.valueOf(readFloat());
            case 8:
                return Integer.valueOf(readInt32());
            case 9:
                return Long.valueOf(readInt64());
            case 10:
                return readMessage(messageType, extensionRegistry);
            case 11:
                return Integer.valueOf(readSFixed32());
            case 12:
                return Long.valueOf(readSFixed64());
            case 13:
                return Integer.valueOf(readSInt32());
            case 14:
                return Long.valueOf(readSInt64());
            case 15:
                return readStringRequireUtf8();
            case 16:
                return Integer.valueOf(readUInt32());
            case 17:
                return Long.valueOf(readUInt64());
            default:
                throw new IllegalArgumentException("unsupported field type.");
        }
    }

    private void verifyPackedFixed32Length(int bytes) throws IOException {
        if ((bytes & 3) != 0) {
            throw InvalidProtocolBufferException.parseFailure();
        }
    }

    private void requirePosition(int expectedPosition) throws IOException {
        if (this.input.getTotalBytesRead() != expectedPosition) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
    }
}
