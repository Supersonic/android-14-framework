package com.android.framework.protobuf.nano;

import com.android.framework.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class Extension<M extends ExtendableMessageNano<M>, T> {
    public static final int TYPE_BOOL = 8;
    public static final int TYPE_BYTES = 12;
    public static final int TYPE_DOUBLE = 1;
    public static final int TYPE_ENUM = 14;
    public static final int TYPE_FIXED32 = 7;
    public static final int TYPE_FIXED64 = 6;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_GROUP = 10;
    public static final int TYPE_INT32 = 5;
    public static final int TYPE_INT64 = 3;
    public static final int TYPE_MESSAGE = 11;
    public static final int TYPE_SFIXED32 = 15;
    public static final int TYPE_SFIXED64 = 16;
    public static final int TYPE_SINT32 = 17;
    public static final int TYPE_SINT64 = 18;
    public static final int TYPE_STRING = 9;
    public static final int TYPE_UINT32 = 13;
    public static final int TYPE_UINT64 = 4;
    protected final Class<T> clazz;
    protected final boolean repeated;
    public final int tag;
    protected final int type;

    @Deprecated
    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T> createMessageTyped(int type, Class<T> clazz, int tag) {
        return new Extension<>(type, clazz, tag, false);
    }

    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T> createMessageTyped(int type, Class<T> clazz, long tag) {
        return new Extension<>(type, clazz, (int) tag, false);
    }

    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T[]> createRepeatedMessageTyped(int type, Class<T[]> clazz, long tag) {
        return new Extension<>(type, clazz, (int) tag, true);
    }

    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> createPrimitiveTyped(int type, Class<T> clazz, long tag) {
        return new PrimitiveExtension(type, clazz, (int) tag, false, 0, 0);
    }

    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> createRepeatedPrimitiveTyped(int type, Class<T> clazz, long tag, long nonPackedTag, long packedTag) {
        return new PrimitiveExtension(type, clazz, (int) tag, true, (int) nonPackedTag, (int) packedTag);
    }

    private Extension(int type, Class<T> clazz, int tag, boolean repeated) {
        this.type = type;
        this.clazz = clazz;
        this.tag = tag;
        this.repeated = repeated;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final T getValueFrom(List<UnknownFieldData> unknownFields) {
        if (unknownFields == null) {
            return null;
        }
        return this.repeated ? getRepeatedValueFrom(unknownFields) : getSingularValueFrom(unknownFields);
    }

    private T getRepeatedValueFrom(List<UnknownFieldData> unknownFields) {
        List<Object> resultList = new ArrayList<>();
        for (int i = 0; i < unknownFields.size(); i++) {
            UnknownFieldData data = unknownFields.get(i);
            if (data.bytes.length != 0) {
                readDataInto(data, resultList);
            }
        }
        int resultSize = resultList.size();
        if (resultSize == 0) {
            return null;
        }
        Class<T> cls = this.clazz;
        T result = cls.cast(Array.newInstance(cls.getComponentType(), resultSize));
        for (int i2 = 0; i2 < resultSize; i2++) {
            Array.set(result, i2, resultList.get(i2));
        }
        return result;
    }

    private T getSingularValueFrom(List<UnknownFieldData> unknownFields) {
        if (unknownFields.isEmpty()) {
            return null;
        }
        UnknownFieldData lastData = unknownFields.get(unknownFields.size() - 1);
        return this.clazz.cast(readData(CodedInputByteBufferNano.newInstance(lastData.bytes)));
    }

    protected Object readData(CodedInputByteBufferNano input) {
        Class componentType = this.repeated ? this.clazz.getComponentType() : this.clazz;
        try {
            switch (this.type) {
                case 10:
                    MessageNano group = (MessageNano) componentType.newInstance();
                    input.readGroup(group, WireFormatNano.getTagFieldNumber(this.tag));
                    return group;
                case 11:
                    MessageNano message = (MessageNano) componentType.newInstance();
                    input.readMessage(message);
                    return message;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading extension field", e);
        } catch (IllegalAccessException e2) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e2);
        } catch (InstantiationException e3) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e3);
        }
    }

    protected void readDataInto(UnknownFieldData data, List<Object> resultList) {
        resultList.add(readData(CodedInputByteBufferNano.newInstance(data.bytes)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeTo(Object value, CodedOutputByteBufferNano output) throws IOException {
        if (this.repeated) {
            writeRepeatedData(value, output);
        } else {
            writeSingularData(value, output);
        }
    }

    protected void writeSingularData(Object value, CodedOutputByteBufferNano out) {
        try {
            out.writeRawVarint32(this.tag);
            switch (this.type) {
                case 10:
                    MessageNano groupValue = (MessageNano) value;
                    int fieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
                    out.writeGroupNoTag(groupValue);
                    out.writeTag(fieldNumber, 4);
                    return;
                case 11:
                    MessageNano messageValue = (MessageNano) value;
                    out.writeMessageNoTag(messageValue);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void writeRepeatedData(Object array, CodedOutputByteBufferNano output) {
        int arrayLength = Array.getLength(array);
        for (int i = 0; i < arrayLength; i++) {
            Object element = Array.get(array, i);
            if (element != null) {
                writeSingularData(element, output);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int computeSerializedSize(Object value) {
        if (this.repeated) {
            return computeRepeatedSerializedSize(value);
        }
        return computeSingularSerializedSize(value);
    }

    protected int computeRepeatedSerializedSize(Object array) {
        int size = 0;
        int arrayLength = Array.getLength(array);
        for (int i = 0; i < arrayLength; i++) {
            Object element = Array.get(array, i);
            if (element != null) {
                size += computeSingularSerializedSize(Array.get(array, i));
            }
        }
        return size;
    }

    protected int computeSingularSerializedSize(Object value) {
        int fieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
        switch (this.type) {
            case 10:
                MessageNano groupValue = (MessageNano) value;
                return CodedOutputByteBufferNano.computeGroupSize(fieldNumber, groupValue);
            case 11:
                MessageNano messageValue = (MessageNano) value;
                return CodedOutputByteBufferNano.computeMessageSize(fieldNumber, messageValue);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }

    /* loaded from: classes4.dex */
    private static class PrimitiveExtension<M extends ExtendableMessageNano<M>, T> extends Extension<M, T> {
        private final int nonPackedTag;
        private final int packedTag;

        public PrimitiveExtension(int type, Class<T> clazz, int tag, boolean repeated, int nonPackedTag, int packedTag) {
            super(type, clazz, tag, repeated);
            this.nonPackedTag = nonPackedTag;
            this.packedTag = packedTag;
        }

        @Override // com.android.framework.protobuf.nano.Extension
        protected Object readData(CodedInputByteBufferNano input) {
            try {
                return input.readPrimitiveField(this.type);
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading extension field", e);
            }
        }

        @Override // com.android.framework.protobuf.nano.Extension
        protected void readDataInto(UnknownFieldData data, List<Object> resultList) {
            if (data.tag == this.nonPackedTag) {
                resultList.add(readData(CodedInputByteBufferNano.newInstance(data.bytes)));
                return;
            }
            CodedInputByteBufferNano buffer = CodedInputByteBufferNano.newInstance(data.bytes);
            try {
                buffer.pushLimit(buffer.readRawVarint32());
                while (!buffer.isAtEnd()) {
                    resultList.add(readData(buffer));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading extension field", e);
            }
        }

        @Override // com.android.framework.protobuf.nano.Extension
        protected final void writeSingularData(Object value, CodedOutputByteBufferNano output) {
            try {
                output.writeRawVarint32(this.tag);
                switch (this.type) {
                    case 1:
                        Double doubleValue = (Double) value;
                        output.writeDoubleNoTag(doubleValue.doubleValue());
                        return;
                    case 2:
                        Float floatValue = (Float) value;
                        output.writeFloatNoTag(floatValue.floatValue());
                        return;
                    case 3:
                        Long int64Value = (Long) value;
                        output.writeInt64NoTag(int64Value.longValue());
                        return;
                    case 4:
                        Long uint64Value = (Long) value;
                        output.writeUInt64NoTag(uint64Value.longValue());
                        return;
                    case 5:
                        Integer int32Value = (Integer) value;
                        output.writeInt32NoTag(int32Value.intValue());
                        return;
                    case 6:
                        Long fixed64Value = (Long) value;
                        output.writeFixed64NoTag(fixed64Value.longValue());
                        return;
                    case 7:
                        Integer fixed32Value = (Integer) value;
                        output.writeFixed32NoTag(fixed32Value.intValue());
                        return;
                    case 8:
                        Boolean boolValue = (Boolean) value;
                        output.writeBoolNoTag(boolValue.booleanValue());
                        return;
                    case 9:
                        String stringValue = (String) value;
                        output.writeStringNoTag(stringValue);
                        return;
                    case 10:
                    case 11:
                    default:
                        throw new IllegalArgumentException("Unknown type " + this.type);
                    case 12:
                        byte[] bytesValue = (byte[]) value;
                        output.writeBytesNoTag(bytesValue);
                        return;
                    case 13:
                        Integer uint32Value = (Integer) value;
                        output.writeUInt32NoTag(uint32Value.intValue());
                        return;
                    case 14:
                        Integer enumValue = (Integer) value;
                        output.writeEnumNoTag(enumValue.intValue());
                        return;
                    case 15:
                        Integer sfixed32Value = (Integer) value;
                        output.writeSFixed32NoTag(sfixed32Value.intValue());
                        return;
                    case 16:
                        Long sfixed64Value = (Long) value;
                        output.writeSFixed64NoTag(sfixed64Value.longValue());
                        return;
                    case 17:
                        Integer sint32Value = (Integer) value;
                        output.writeSInt32NoTag(sint32Value.intValue());
                        return;
                    case 18:
                        Long sint64Value = (Long) value;
                        output.writeSInt64NoTag(sint64Value.longValue());
                        return;
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override // com.android.framework.protobuf.nano.Extension
        protected void writeRepeatedData(Object array, CodedOutputByteBufferNano output) {
            if (this.tag == this.nonPackedTag) {
                super.writeRepeatedData(array, output);
            } else if (this.tag == this.packedTag) {
                int arrayLength = Array.getLength(array);
                int dataSize = computePackedDataSize(array);
                try {
                    output.writeRawVarint32(this.tag);
                    output.writeRawVarint32(dataSize);
                    switch (this.type) {
                        case 1:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeDoubleNoTag(Array.getDouble(array, i));
                            }
                            return;
                        case 2:
                            for (int i2 = 0; i2 < arrayLength; i2++) {
                                output.writeFloatNoTag(Array.getFloat(array, i2));
                            }
                            return;
                        case 3:
                            for (int i3 = 0; i3 < arrayLength; i3++) {
                                output.writeInt64NoTag(Array.getLong(array, i3));
                            }
                            return;
                        case 4:
                            for (int i4 = 0; i4 < arrayLength; i4++) {
                                output.writeUInt64NoTag(Array.getLong(array, i4));
                            }
                            return;
                        case 5:
                            for (int i5 = 0; i5 < arrayLength; i5++) {
                                output.writeInt32NoTag(Array.getInt(array, i5));
                            }
                            return;
                        case 6:
                            for (int i6 = 0; i6 < arrayLength; i6++) {
                                output.writeFixed64NoTag(Array.getLong(array, i6));
                            }
                            return;
                        case 7:
                            for (int i7 = 0; i7 < arrayLength; i7++) {
                                output.writeFixed32NoTag(Array.getInt(array, i7));
                            }
                            return;
                        case 8:
                            for (int i8 = 0; i8 < arrayLength; i8++) {
                                output.writeBoolNoTag(Array.getBoolean(array, i8));
                            }
                            return;
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        default:
                            throw new IllegalArgumentException("Unpackable type " + this.type);
                        case 13:
                            for (int i9 = 0; i9 < arrayLength; i9++) {
                                output.writeUInt32NoTag(Array.getInt(array, i9));
                            }
                            return;
                        case 14:
                            for (int i10 = 0; i10 < arrayLength; i10++) {
                                output.writeEnumNoTag(Array.getInt(array, i10));
                            }
                            return;
                        case 15:
                            for (int i11 = 0; i11 < arrayLength; i11++) {
                                output.writeSFixed32NoTag(Array.getInt(array, i11));
                            }
                            return;
                        case 16:
                            for (int i12 = 0; i12 < arrayLength; i12++) {
                                output.writeSFixed64NoTag(Array.getLong(array, i12));
                            }
                            return;
                        case 17:
                            for (int i13 = 0; i13 < arrayLength; i13++) {
                                output.writeSInt32NoTag(Array.getInt(array, i13));
                            }
                            return;
                        case 18:
                            for (int i14 = 0; i14 < arrayLength; i14++) {
                                output.writeSInt64NoTag(Array.getLong(array, i14));
                            }
                            return;
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            } else {
                throw new IllegalArgumentException("Unexpected repeated extension tag " + this.tag + ", unequal to both non-packed variant " + this.nonPackedTag + " and packed variant " + this.packedTag);
            }
        }

        private int computePackedDataSize(Object array) {
            int dataSize = 0;
            int arrayLength = Array.getLength(array);
            switch (this.type) {
                case 1:
                case 6:
                case 16:
                    return arrayLength * 8;
                case 2:
                case 7:
                case 15:
                    return arrayLength * 4;
                case 3:
                    for (int i = 0; i < arrayLength; i++) {
                        dataSize += CodedOutputByteBufferNano.computeInt64SizeNoTag(Array.getLong(array, i));
                    }
                    return dataSize;
                case 4:
                    for (int i2 = 0; i2 < arrayLength; i2++) {
                        dataSize += CodedOutputByteBufferNano.computeUInt64SizeNoTag(Array.getLong(array, i2));
                    }
                    return dataSize;
                case 5:
                    for (int i3 = 0; i3 < arrayLength; i3++) {
                        dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(Array.getInt(array, i3));
                    }
                    return dataSize;
                case 8:
                    return arrayLength;
                case 9:
                case 10:
                case 11:
                case 12:
                default:
                    throw new IllegalArgumentException("Unexpected non-packable type " + this.type);
                case 13:
                    for (int i4 = 0; i4 < arrayLength; i4++) {
                        dataSize += CodedOutputByteBufferNano.computeUInt32SizeNoTag(Array.getInt(array, i4));
                    }
                    return dataSize;
                case 14:
                    for (int i5 = 0; i5 < arrayLength; i5++) {
                        dataSize += CodedOutputByteBufferNano.computeEnumSizeNoTag(Array.getInt(array, i5));
                    }
                    return dataSize;
                case 17:
                    for (int i6 = 0; i6 < arrayLength; i6++) {
                        dataSize += CodedOutputByteBufferNano.computeSInt32SizeNoTag(Array.getInt(array, i6));
                    }
                    return dataSize;
                case 18:
                    for (int i7 = 0; i7 < arrayLength; i7++) {
                        dataSize += CodedOutputByteBufferNano.computeSInt64SizeNoTag(Array.getLong(array, i7));
                    }
                    return dataSize;
            }
        }

        @Override // com.android.framework.protobuf.nano.Extension
        protected int computeRepeatedSerializedSize(Object array) {
            if (this.tag == this.nonPackedTag) {
                return super.computeRepeatedSerializedSize(array);
            }
            if (this.tag == this.packedTag) {
                int dataSize = computePackedDataSize(array);
                int payloadSize = CodedOutputByteBufferNano.computeRawVarint32Size(dataSize) + dataSize;
                return CodedOutputByteBufferNano.computeRawVarint32Size(this.tag) + payloadSize;
            }
            throw new IllegalArgumentException("Unexpected repeated extension tag " + this.tag + ", unequal to both non-packed variant " + this.nonPackedTag + " and packed variant " + this.packedTag);
        }

        @Override // com.android.framework.protobuf.nano.Extension
        protected final int computeSingularSerializedSize(Object value) {
            int fieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
            switch (this.type) {
                case 1:
                    Double doubleValue = (Double) value;
                    return CodedOutputByteBufferNano.computeDoubleSize(fieldNumber, doubleValue.doubleValue());
                case 2:
                    Float floatValue = (Float) value;
                    return CodedOutputByteBufferNano.computeFloatSize(fieldNumber, floatValue.floatValue());
                case 3:
                    Long int64Value = (Long) value;
                    return CodedOutputByteBufferNano.computeInt64Size(fieldNumber, int64Value.longValue());
                case 4:
                    Long uint64Value = (Long) value;
                    return CodedOutputByteBufferNano.computeUInt64Size(fieldNumber, uint64Value.longValue());
                case 5:
                    Integer int32Value = (Integer) value;
                    return CodedOutputByteBufferNano.computeInt32Size(fieldNumber, int32Value.intValue());
                case 6:
                    Long fixed64Value = (Long) value;
                    return CodedOutputByteBufferNano.computeFixed64Size(fieldNumber, fixed64Value.longValue());
                case 7:
                    Integer fixed32Value = (Integer) value;
                    return CodedOutputByteBufferNano.computeFixed32Size(fieldNumber, fixed32Value.intValue());
                case 8:
                    Boolean boolValue = (Boolean) value;
                    return CodedOutputByteBufferNano.computeBoolSize(fieldNumber, boolValue.booleanValue());
                case 9:
                    String stringValue = (String) value;
                    return CodedOutputByteBufferNano.computeStringSize(fieldNumber, stringValue);
                case 10:
                case 11:
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
                case 12:
                    byte[] bytesValue = (byte[]) value;
                    return CodedOutputByteBufferNano.computeBytesSize(fieldNumber, bytesValue);
                case 13:
                    Integer uint32Value = (Integer) value;
                    return CodedOutputByteBufferNano.computeUInt32Size(fieldNumber, uint32Value.intValue());
                case 14:
                    Integer enumValue = (Integer) value;
                    return CodedOutputByteBufferNano.computeEnumSize(fieldNumber, enumValue.intValue());
                case 15:
                    Integer sfixed32Value = (Integer) value;
                    return CodedOutputByteBufferNano.computeSFixed32Size(fieldNumber, sfixed32Value.intValue());
                case 16:
                    Long sfixed64Value = (Long) value;
                    return CodedOutputByteBufferNano.computeSFixed64Size(fieldNumber, sfixed64Value.longValue());
                case 17:
                    Integer sint32Value = (Integer) value;
                    return CodedOutputByteBufferNano.computeSInt32Size(fieldNumber, sint32Value.intValue());
                case 18:
                    Long sint64Value = (Long) value;
                    return CodedOutputByteBufferNano.computeSInt64Size(fieldNumber, sint64Value.longValue());
            }
        }
    }
}
