package com.android.framework.protobuf;

import android.hardware.biometrics.fingerprint.AcquiredInfo;
import com.android.framework.protobuf.GeneratedMessageLite;
import com.android.framework.protobuf.Internal;
import com.android.framework.protobuf.WireFormat;
import java.io.IOException;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
@CheckReturnValue
/* loaded from: classes4.dex */
public final class ArrayDecoders {
    private ArrayDecoders() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static final class Registers {
        public final ExtensionRegistryLite extensionRegistry;
        public int int1;
        public long long1;
        public Object object1;

        Registers() {
            this.extensionRegistry = ExtensionRegistryLite.getEmptyRegistry();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Registers(ExtensionRegistryLite extensionRegistry) {
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            this.extensionRegistry = extensionRegistry;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeVarint32(byte[] data, int position, Registers registers) {
        int position2 = position + 1;
        int value = data[position];
        if (value >= 0) {
            registers.int1 = value;
            return position2;
        }
        return decodeVarint32(value, data, position2, registers);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeVarint32(int firstByte, byte[] data, int position, Registers registers) {
        int value = firstByte & 127;
        int position2 = position + 1;
        byte b2 = data[position];
        if (b2 >= 0) {
            registers.int1 = (b2 << 7) | value;
            return position2;
        }
        int value2 = value | ((b2 & Byte.MAX_VALUE) << 7);
        int position3 = position2 + 1;
        byte b3 = data[position2];
        if (b3 >= 0) {
            registers.int1 = (b3 << AcquiredInfo.POWER_PRESS) | value2;
            return position3;
        }
        int value3 = value2 | ((b3 & Byte.MAX_VALUE) << 14);
        int position4 = position3 + 1;
        byte b4 = data[position3];
        if (b4 >= 0) {
            registers.int1 = (b4 << 21) | value3;
            return position4;
        }
        int value4 = value3 | ((b4 & Byte.MAX_VALUE) << 21);
        int position5 = position4 + 1;
        byte b5 = data[position4];
        if (b5 >= 0) {
            registers.int1 = (b5 << 28) | value4;
            return position5;
        }
        int value5 = value4 | ((b5 & Byte.MAX_VALUE) << 28);
        while (true) {
            int position6 = position5 + 1;
            if (data[position5] >= 0) {
                registers.int1 = value5;
                return position6;
            }
            position5 = position6;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeVarint64(byte[] data, int position, Registers registers) {
        int position2 = position + 1;
        long value = data[position];
        if (value >= 0) {
            registers.long1 = value;
            return position2;
        }
        return decodeVarint64(value, data, position2, registers);
    }

    static int decodeVarint64(long firstByte, byte[] data, int position, Registers registers) {
        long value = 127 & firstByte;
        int position2 = position + 1;
        byte next = data[position];
        int shift = 7;
        long value2 = value | ((next & Byte.MAX_VALUE) << 7);
        while (next < 0) {
            next = data[position2];
            shift += 7;
            value2 |= (next & Byte.MAX_VALUE) << shift;
            position2++;
        }
        registers.long1 = value2;
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeFixed32(byte[] data, int position) {
        return (data[position] & 255) | ((data[position + 1] & 255) << 8) | ((data[position + 2] & 255) << 16) | ((data[position + 3] & 255) << 24);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long decodeFixed64(byte[] data, int position) {
        return (data[position] & 255) | ((data[position + 1] & 255) << 8) | ((data[position + 2] & 255) << 16) | ((data[position + 3] & 255) << 24) | ((data[position + 4] & 255) << 32) | ((data[position + 5] & 255) << 40) | ((data[position + 6] & 255) << 48) | ((255 & data[position + 7]) << 56);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static double decodeDouble(byte[] data, int position) {
        return Double.longBitsToDouble(decodeFixed64(data, position));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float decodeFloat(byte[] data, int position) {
        return Float.intBitsToFloat(decodeFixed32(data, position));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeString(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
        int position2 = decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length == 0) {
            registers.object1 = "";
            return position2;
        }
        registers.object1 = new String(data, position2, length, Internal.UTF_8);
        return position2 + length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeStringRequireUtf8(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
        int position2 = decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length == 0) {
            registers.object1 = "";
            return position2;
        }
        registers.object1 = Utf8.decodeUtf8(data, position2, length);
        return position2 + length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeBytes(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
        int position2 = decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length > data.length - position2) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        if (length == 0) {
            registers.object1 = ByteString.EMPTY;
            return position2;
        }
        registers.object1 = ByteString.copyFrom(data, position2, length);
        return position2 + length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeMessageField(Schema schema, byte[] data, int position, int limit, Registers registers) throws IOException {
        Object msg = schema.newInstance();
        int offset = mergeMessageField(msg, schema, data, position, limit, registers);
        schema.makeImmutable(msg);
        registers.object1 = msg;
        return offset;
    }

    static int decodeGroupField(Schema schema, byte[] data, int position, int limit, int endGroup, Registers registers) throws IOException {
        Object msg = schema.newInstance();
        int offset = mergeGroupField(msg, schema, data, position, limit, endGroup, registers);
        schema.makeImmutable(msg);
        registers.object1 = msg;
        return offset;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int mergeMessageField(Object msg, Schema schema, byte[] data, int position, int limit, Registers registers) throws IOException {
        int position2;
        int position3 = position + 1;
        int length = data[position];
        if (length >= 0) {
            position2 = position3;
        } else {
            int position4 = decodeVarint32(length, data, position3, registers);
            length = registers.int1;
            position2 = position4;
        }
        if (length < 0 || length > limit - position2) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        schema.mergeFrom(msg, data, position2, position2 + length, registers);
        registers.object1 = msg;
        return position2 + length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int mergeGroupField(Object msg, Schema schema, byte[] data, int position, int limit, int endGroup, Registers registers) throws IOException {
        MessageSchema messageSchema = (MessageSchema) schema;
        int endPosition = messageSchema.parseProto2Message(msg, data, position, limit, endGroup, registers);
        registers.object1 = msg;
        return endPosition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeVarint32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        IntArrayList output = (IntArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        output.addInt(registers.int1);
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeVarint32(data, nextPosition, registers);
            output.addInt(registers.int1);
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeVarint64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        LongArrayList output = (LongArrayList) list;
        int position2 = decodeVarint64(data, position, registers);
        output.addLong(registers.long1);
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeVarint64(data, nextPosition, registers);
            output.addLong(registers.long1);
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeFixed32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        IntArrayList output = (IntArrayList) list;
        output.addInt(decodeFixed32(data, position));
        int position2 = position + 4;
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            output.addInt(decodeFixed32(data, nextPosition));
            position2 = nextPosition + 4;
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeFixed64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        LongArrayList output = (LongArrayList) list;
        output.addLong(decodeFixed64(data, position));
        int position2 = position + 8;
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            output.addLong(decodeFixed64(data, nextPosition));
            position2 = nextPosition + 8;
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeFloatList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        FloatArrayList output = (FloatArrayList) list;
        output.addFloat(decodeFloat(data, position));
        int position2 = position + 4;
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            output.addFloat(decodeFloat(data, nextPosition));
            position2 = nextPosition + 4;
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeDoubleList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        DoubleArrayList output = (DoubleArrayList) list;
        output.addDouble(decodeDouble(data, position));
        int position2 = position + 8;
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            output.addDouble(decodeDouble(data, nextPosition));
            position2 = nextPosition + 8;
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeBoolList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        BooleanArrayList output = (BooleanArrayList) list;
        int position2 = decodeVarint64(data, position, registers);
        output.addBoolean(registers.long1 != 0);
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeVarint64(data, nextPosition, registers);
            output.addBoolean(registers.long1 != 0);
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeSInt32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        IntArrayList output = (IntArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeVarint32(data, nextPosition, registers);
            output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeSInt64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        LongArrayList output = (LongArrayList) list;
        int position2 = decodeVarint64(data, position, registers);
        output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeVarint64(data, nextPosition, registers);
            output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodePackedVarint32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        IntArrayList output = (IntArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        int fieldLimit = registers.int1 + position2;
        while (position2 < fieldLimit) {
            position2 = decodeVarint32(data, position2, registers);
            output.addInt(registers.int1);
        }
        if (position2 != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodePackedVarint64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        LongArrayList output = (LongArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        int fieldLimit = registers.int1 + position2;
        while (position2 < fieldLimit) {
            position2 = decodeVarint64(data, position2, registers);
            output.addLong(registers.long1);
        }
        if (position2 != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodePackedFixed32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        IntArrayList output = (IntArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        int fieldLimit = registers.int1 + position2;
        while (position2 < fieldLimit) {
            output.addInt(decodeFixed32(data, position2));
            position2 += 4;
        }
        if (position2 != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodePackedFixed64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        LongArrayList output = (LongArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        int fieldLimit = registers.int1 + position2;
        while (position2 < fieldLimit) {
            output.addLong(decodeFixed64(data, position2));
            position2 += 8;
        }
        if (position2 != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodePackedFloatList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        FloatArrayList output = (FloatArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        int fieldLimit = registers.int1 + position2;
        while (position2 < fieldLimit) {
            output.addFloat(decodeFloat(data, position2));
            position2 += 4;
        }
        if (position2 != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodePackedDoubleList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        DoubleArrayList output = (DoubleArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        int fieldLimit = registers.int1 + position2;
        while (position2 < fieldLimit) {
            output.addDouble(decodeDouble(data, position2));
            position2 += 8;
        }
        if (position2 != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodePackedBoolList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        BooleanArrayList output = (BooleanArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        int fieldLimit = registers.int1 + position2;
        while (position2 < fieldLimit) {
            position2 = decodeVarint64(data, position2, registers);
            output.addBoolean(registers.long1 != 0);
        }
        if (position2 != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodePackedSInt32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        IntArrayList output = (IntArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        int fieldLimit = registers.int1 + position2;
        while (position2 < fieldLimit) {
            position2 = decodeVarint32(data, position2, registers);
            output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
        }
        if (position2 != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodePackedSInt64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        LongArrayList output = (LongArrayList) list;
        int position2 = decodeVarint32(data, position, registers);
        int fieldLimit = registers.int1 + position2;
        while (position2 < fieldLimit) {
            position2 = decodeVarint64(data, position2, registers);
            output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
        }
        if (position2 != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeStringList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        int position2 = decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length == 0) {
            list.add("");
        } else {
            String value = new String(data, position2, length, Internal.UTF_8);
            list.add(value);
            position2 += length;
        }
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeVarint32(data, nextPosition, registers);
            int nextLength = registers.int1;
            if (nextLength < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if (nextLength == 0) {
                list.add("");
            } else {
                String value2 = new String(data, position2, nextLength, Internal.UTF_8);
                list.add(value2);
                position2 += nextLength;
            }
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeStringListRequireUtf8(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        int position2 = decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length == 0) {
            list.add("");
        } else if (!Utf8.isValidUtf8(data, position2, position2 + length)) {
            throw InvalidProtocolBufferException.invalidUtf8();
        } else {
            String value = new String(data, position2, length, Internal.UTF_8);
            list.add(value);
            position2 += length;
        }
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeVarint32(data, nextPosition, registers);
            int nextLength = registers.int1;
            if (nextLength < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if (nextLength == 0) {
                list.add("");
            } else if (!Utf8.isValidUtf8(data, position2, position2 + nextLength)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            } else {
                String value2 = new String(data, position2, nextLength, Internal.UTF_8);
                list.add(value2);
                position2 += nextLength;
            }
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeBytesList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        int position2 = decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length > data.length - position2) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        if (length == 0) {
            list.add(ByteString.EMPTY);
        } else {
            list.add(ByteString.copyFrom(data, position2, length));
            position2 += length;
        }
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeVarint32(data, nextPosition, registers);
            int nextLength = registers.int1;
            if (nextLength < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if (nextLength > data.length - position2) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            if (nextLength == 0) {
                list.add(ByteString.EMPTY);
            } else {
                list.add(ByteString.copyFrom(data, position2, nextLength));
                position2 += nextLength;
            }
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeMessageList(Schema<?> schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        int position2 = decodeMessageField(schema, data, position, limit, registers);
        list.add(registers.object1);
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeMessageField(schema, data, nextPosition, limit, registers);
            list.add(registers.object1);
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeGroupList(Schema schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        int endgroup = (tag & (-8)) | 4;
        int position2 = decodeGroupField(schema, data, position, limit, endgroup, registers);
        list.add(registers.object1);
        while (position2 < limit) {
            int nextPosition = decodeVarint32(data, position2, registers);
            if (tag != registers.int1) {
                break;
            }
            position2 = decodeGroupField(schema, data, nextPosition, limit, endgroup, registers);
            list.add(registers.object1);
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeExtensionOrUnknownField(int tag, byte[] data, int position, int limit, Object message, MessageLite defaultInstance, UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema, Registers registers) throws IOException {
        int number = tag >>> 3;
        GeneratedMessageLite.GeneratedExtension extension = registers.extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
        if (extension == null) {
            return decodeUnknownField(tag, data, position, limit, MessageSchema.getMutableUnknownFields(message), registers);
        }
        ((GeneratedMessageLite.ExtendableMessage) message).ensureExtensionsAreMutable();
        return decodeExtension(tag, data, position, limit, (GeneratedMessageLite.ExtendableMessage) message, extension, unknownFieldSchema, registers);
    }

    static int decodeExtension(int tag, byte[] data, int position, int limit, GeneratedMessageLite.ExtendableMessage<?, ?> message, GeneratedMessageLite.GeneratedExtension<?, ?> extension, UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema, Registers registers) throws IOException {
        int position2;
        Object oldValue;
        Object oldValue2;
        FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = message.extensions;
        int fieldNumber = tag >>> 3;
        if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
            switch (C39951.$SwitchMap$com$google$protobuf$WireFormat$FieldType[extension.getLiteType().ordinal()]) {
                case 1:
                    DoubleArrayList list = new DoubleArrayList();
                    int position3 = decodePackedDoubleList(data, position, list, registers);
                    extensions.setField(extension.descriptor, list);
                    return position3;
                case 2:
                    FloatArrayList list2 = new FloatArrayList();
                    int position4 = decodePackedFloatList(data, position, list2, registers);
                    extensions.setField(extension.descriptor, list2);
                    return position4;
                case 3:
                case 4:
                    LongArrayList list3 = new LongArrayList();
                    int position5 = decodePackedVarint64List(data, position, list3, registers);
                    extensions.setField(extension.descriptor, list3);
                    return position5;
                case 5:
                case 6:
                    IntArrayList list4 = new IntArrayList();
                    int position6 = decodePackedVarint32List(data, position, list4, registers);
                    extensions.setField(extension.descriptor, list4);
                    return position6;
                case 7:
                case 8:
                    LongArrayList list5 = new LongArrayList();
                    int position7 = decodePackedFixed64List(data, position, list5, registers);
                    extensions.setField(extension.descriptor, list5);
                    return position7;
                case 9:
                case 10:
                    IntArrayList list6 = new IntArrayList();
                    int position8 = decodePackedFixed32List(data, position, list6, registers);
                    extensions.setField(extension.descriptor, list6);
                    return position8;
                case 11:
                    BooleanArrayList list7 = new BooleanArrayList();
                    int position9 = decodePackedBoolList(data, position, list7, registers);
                    extensions.setField(extension.descriptor, list7);
                    return position9;
                case 12:
                    IntArrayList list8 = new IntArrayList();
                    int position10 = decodePackedSInt32List(data, position, list8, registers);
                    extensions.setField(extension.descriptor, list8);
                    return position10;
                case 13:
                    LongArrayList list9 = new LongArrayList();
                    int position11 = decodePackedSInt64List(data, position, list9, registers);
                    extensions.setField(extension.descriptor, list9);
                    return position11;
                case 14:
                    IntArrayList list10 = new IntArrayList();
                    int position12 = decodePackedVarint32List(data, position, list10, registers);
                    SchemaUtil.filterUnknownEnumList((Object) message, fieldNumber, (List<Integer>) list10, extension.descriptor.getEnumType(), (Object) null, (UnknownFieldSchema<UT, Object>) unknownFieldSchema);
                    extensions.setField(extension.descriptor, list10);
                    return position12;
                default:
                    throw new IllegalStateException("Type cannot be packed: " + extension.descriptor.getLiteType());
            }
        }
        Object value = null;
        if (extension.getLiteType() == WireFormat.FieldType.ENUM) {
            position2 = decodeVarint32(data, position, registers);
            Object enumValue = extension.descriptor.getEnumType().findValueByNumber(registers.int1);
            if (enumValue == null) {
                SchemaUtil.storeUnknownEnum(message, fieldNumber, registers.int1, null, unknownFieldSchema);
                return position2;
            }
            value = Integer.valueOf(registers.int1);
        } else {
            switch (C39951.$SwitchMap$com$google$protobuf$WireFormat$FieldType[extension.getLiteType().ordinal()]) {
                case 1:
                    value = Double.valueOf(decodeDouble(data, position));
                    position2 = position + 8;
                    break;
                case 2:
                    value = Float.valueOf(decodeFloat(data, position));
                    position2 = position + 4;
                    break;
                case 3:
                case 4:
                    position2 = decodeVarint64(data, position, registers);
                    value = Long.valueOf(registers.long1);
                    break;
                case 5:
                case 6:
                    position2 = decodeVarint32(data, position, registers);
                    value = Integer.valueOf(registers.int1);
                    break;
                case 7:
                case 8:
                    value = Long.valueOf(decodeFixed64(data, position));
                    position2 = position + 8;
                    break;
                case 9:
                case 10:
                    value = Integer.valueOf(decodeFixed32(data, position));
                    position2 = position + 4;
                    break;
                case 11:
                    position2 = decodeVarint64(data, position, registers);
                    value = Boolean.valueOf(registers.long1 != 0);
                    break;
                case 12:
                    position2 = decodeVarint32(data, position, registers);
                    value = Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1));
                    break;
                case 13:
                    position2 = decodeVarint64(data, position, registers);
                    value = Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1));
                    break;
                case 14:
                    throw new IllegalStateException("Shouldn't reach here.");
                case 15:
                    position2 = decodeBytes(data, position, registers);
                    value = registers.object1;
                    break;
                case 16:
                    position2 = decodeString(data, position, registers);
                    value = registers.object1;
                    break;
                case 17:
                    int endTag = (fieldNumber << 3) | 4;
                    Schema fieldSchema = Protobuf.getInstance().schemaFor((Class) extension.getMessageDefaultInstance().getClass());
                    if (extension.isRepeated()) {
                        int position13 = decodeGroupField(fieldSchema, data, position, limit, endTag, registers);
                        extensions.addRepeatedField(extension.descriptor, registers.object1);
                        return position13;
                    }
                    Object oldValue3 = extensions.getField(extension.descriptor);
                    if (oldValue3 != null) {
                        oldValue = oldValue3;
                    } else {
                        Object oldValue4 = fieldSchema.newInstance();
                        extensions.setField(extension.descriptor, oldValue4);
                        oldValue = oldValue4;
                    }
                    return mergeGroupField(oldValue, fieldSchema, data, position, limit, endTag, registers);
                case 18:
                    Schema fieldSchema2 = Protobuf.getInstance().schemaFor((Class) extension.getMessageDefaultInstance().getClass());
                    if (extension.isRepeated()) {
                        int position14 = decodeMessageField(fieldSchema2, data, position, limit, registers);
                        extensions.addRepeatedField(extension.descriptor, registers.object1);
                        return position14;
                    }
                    Object oldValue5 = extensions.getField(extension.descriptor);
                    if (oldValue5 != null) {
                        oldValue2 = oldValue5;
                    } else {
                        Object oldValue6 = fieldSchema2.newInstance();
                        extensions.setField(extension.descriptor, oldValue6);
                        oldValue2 = oldValue6;
                    }
                    return mergeMessageField(oldValue2, fieldSchema2, data, position, limit, registers);
                default:
                    position2 = position;
                    break;
            }
        }
        if (extension.isRepeated()) {
            extensions.addRepeatedField(extension.descriptor, value);
        } else {
            extensions.setField(extension.descriptor, value);
        }
        return position2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.framework.protobuf.ArrayDecoders$1 */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class C39951 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$FieldType;

        static {
            int[] iArr = new int[WireFormat.FieldType.values().length];
            $SwitchMap$com$google$protobuf$WireFormat$FieldType = iArr;
            try {
                iArr[WireFormat.FieldType.DOUBLE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FLOAT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT64.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT64.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT32.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT32.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED64.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED64.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED32.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED32.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BOOL.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT32.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT64.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.ENUM.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BYTES.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.STRING.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.GROUP.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.MESSAGE.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int decodeUnknownField(int tag, byte[] data, int position, int limit, UnknownFieldSetLite unknownFields, Registers registers) throws InvalidProtocolBufferException {
        if (WireFormat.getTagFieldNumber(tag) == 0) {
            throw InvalidProtocolBufferException.invalidTag();
        }
        switch (WireFormat.getTagWireType(tag)) {
            case 0:
                int position2 = decodeVarint64(data, position, registers);
                unknownFields.storeField(tag, Long.valueOf(registers.long1));
                return position2;
            case 1:
                unknownFields.storeField(tag, Long.valueOf(decodeFixed64(data, position)));
                return position + 8;
            case 2:
                int position3 = decodeVarint32(data, position, registers);
                int length = registers.int1;
                if (length < 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                if (length > data.length - position3) {
                    throw InvalidProtocolBufferException.truncatedMessage();
                }
                if (length == 0) {
                    unknownFields.storeField(tag, ByteString.EMPTY);
                } else {
                    unknownFields.storeField(tag, ByteString.copyFrom(data, position3, length));
                }
                return position3 + length;
            case 3:
                UnknownFieldSetLite child = UnknownFieldSetLite.newInstance();
                int endGroup = (tag & (-8)) | 4;
                int lastTag = 0;
                while (true) {
                    if (position < limit) {
                        position = decodeVarint32(data, position, registers);
                        int lastTag2 = registers.int1;
                        if (lastTag2 == endGroup) {
                            lastTag = lastTag2;
                        } else {
                            lastTag = lastTag2;
                            position = decodeUnknownField(lastTag, data, position, limit, child, registers);
                        }
                    }
                }
                if (position > limit || lastTag != endGroup) {
                    throw InvalidProtocolBufferException.parseFailure();
                }
                unknownFields.storeField(tag, child);
                return position;
            case 4:
            default:
                throw InvalidProtocolBufferException.invalidTag();
            case 5:
                unknownFields.storeField(tag, Integer.valueOf(decodeFixed32(data, position)));
                return position + 4;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int skipField(int tag, byte[] data, int position, int limit, Registers registers) throws InvalidProtocolBufferException {
        if (WireFormat.getTagFieldNumber(tag) == 0) {
            throw InvalidProtocolBufferException.invalidTag();
        }
        switch (WireFormat.getTagWireType(tag)) {
            case 0:
                return decodeVarint64(data, position, registers);
            case 1:
                return position + 8;
            case 2:
                return registers.int1 + decodeVarint32(data, position, registers);
            case 3:
                int endGroup = (tag & (-8)) | 4;
                int lastTag = 0;
                while (position < limit) {
                    position = decodeVarint32(data, position, registers);
                    lastTag = registers.int1;
                    if (lastTag != endGroup) {
                        position = skipField(lastTag, data, position, limit, registers);
                    } else if (position <= limit || lastTag != endGroup) {
                        throw InvalidProtocolBufferException.parseFailure();
                    } else {
                        return position;
                    }
                }
                if (position <= limit) {
                }
                throw InvalidProtocolBufferException.parseFailure();
            case 4:
            default:
                throw InvalidProtocolBufferException.invalidTag();
            case 5:
                return position + 4;
        }
    }
}
