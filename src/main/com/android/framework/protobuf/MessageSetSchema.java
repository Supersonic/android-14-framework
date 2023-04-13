package com.android.framework.protobuf;

import com.android.framework.protobuf.ArrayDecoders;
import com.android.framework.protobuf.FieldSet;
import com.android.framework.protobuf.GeneratedMessageLite;
import com.android.framework.protobuf.LazyField;
import com.android.framework.protobuf.WireFormat;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
@CheckReturnValue
/* loaded from: classes4.dex */
final class MessageSetSchema<T> implements Schema<T> {
    private final MessageLite defaultInstance;
    private final ExtensionSchema<?> extensionSchema;
    private final boolean hasExtensions;
    private final UnknownFieldSchema<?, ?> unknownFieldSchema;

    private MessageSetSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
        this.unknownFieldSchema = unknownFieldSchema;
        this.hasExtensions = extensionSchema.hasExtensions(defaultInstance);
        this.extensionSchema = extensionSchema;
        this.defaultInstance = defaultInstance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> MessageSetSchema<T> newSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
        return new MessageSetSchema<>(unknownFieldSchema, extensionSchema, defaultInstance);
    }

    @Override // com.android.framework.protobuf.Schema
    public T newInstance() {
        MessageLite messageLite = this.defaultInstance;
        if (messageLite instanceof GeneratedMessageLite) {
            return (T) ((GeneratedMessageLite) messageLite).newMutableInstance();
        }
        return (T) messageLite.newBuilderForType().buildPartial();
    }

    @Override // com.android.framework.protobuf.Schema
    public boolean equals(T message, T other) {
        Object messageUnknown = this.unknownFieldSchema.getFromMessage(message);
        Object otherUnknown = this.unknownFieldSchema.getFromMessage(other);
        if (!messageUnknown.equals(otherUnknown)) {
            return false;
        }
        if (this.hasExtensions) {
            FieldSet<?> messageExtensions = this.extensionSchema.getExtensions(message);
            FieldSet<?> otherExtensions = this.extensionSchema.getExtensions(other);
            return messageExtensions.equals(otherExtensions);
        }
        return true;
    }

    @Override // com.android.framework.protobuf.Schema
    public int hashCode(T message) {
        int hashCode = this.unknownFieldSchema.getFromMessage(message).hashCode();
        if (this.hasExtensions) {
            FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
            return (hashCode * 53) + extensions.hashCode();
        }
        return hashCode;
    }

    @Override // com.android.framework.protobuf.Schema
    public void mergeFrom(T message, T other) {
        SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
        if (this.hasExtensions) {
            SchemaUtil.mergeExtensions(this.extensionSchema, message, other);
        }
    }

    @Override // com.android.framework.protobuf.Schema
    public void writeTo(T message, Writer writer) throws IOException {
        FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
        Iterator<?> iterator = extensions.iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, Object> extension = iterator.next();
            FieldSet.FieldDescriptorLite<?> fd = (FieldSet.FieldDescriptorLite) extension.getKey();
            if (fd.getLiteJavaType() != WireFormat.JavaType.MESSAGE || fd.isRepeated() || fd.isPacked()) {
                throw new IllegalStateException("Found invalid MessageSet item.");
            }
            if (extension instanceof LazyField.LazyEntry) {
                writer.writeMessageSetItem(fd.getNumber(), ((LazyField.LazyEntry) extension).getField().toByteString());
            } else {
                writer.writeMessageSetItem(fd.getNumber(), extension.getValue());
            }
        }
        writeUnknownFieldsHelper(this.unknownFieldSchema, message, writer);
    }

    private <UT, UB> void writeUnknownFieldsHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, T message, Writer writer) throws IOException {
        unknownFieldSchema.writeAsMessageSetTo(unknownFieldSchema.getFromMessage(message), writer);
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x00f0  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00ee A[SYNTHETIC] */
    @Override // com.android.framework.protobuf.Schema
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
        UnknownFieldSetLite unknownFields;
        GeneratedMessageLite.GeneratedExtension<?, ?> extension;
        UnknownFieldSetLite unknownFields2 = ((GeneratedMessageLite) message).unknownFields;
        if (unknownFields2 != UnknownFieldSetLite.getDefaultInstance()) {
            unknownFields = unknownFields2;
        } else {
            UnknownFieldSetLite unknownFields3 = UnknownFieldSetLite.newInstance();
            ((GeneratedMessageLite) message).unknownFields = unknownFields3;
            unknownFields = unknownFields3;
        }
        FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = ((GeneratedMessageLite.ExtendableMessage) message).ensureExtensionsAreMutable();
        GeneratedMessageLite.GeneratedExtension<?, ?> extension2 = null;
        int typeId = position;
        while (typeId < limit) {
            int number = ArrayDecoders.decodeVarint32(data, typeId, registers);
            int startTag = registers.int1;
            if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
                if (WireFormat.getTagWireType(startTag) == 2) {
                    GeneratedMessageLite.GeneratedExtension<?, ?> extension3 = (GeneratedMessageLite.GeneratedExtension) this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, WireFormat.getTagFieldNumber(startTag));
                    if (extension3 != null) {
                        typeId = ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor((Class) extension3.getMessageDefaultInstance().getClass()), data, number, limit, registers);
                        extensions.setField(extension3.descriptor, registers.object1);
                        extension2 = extension3;
                    } else {
                        typeId = ArrayDecoders.decodeUnknownField(startTag, data, number, limit, unknownFields, registers);
                        extension2 = extension3;
                    }
                } else {
                    typeId = ArrayDecoders.skipField(startTag, data, number, limit, registers);
                }
            } else {
                int typeId2 = 0;
                ByteString rawBytes = null;
                while (true) {
                    if (number >= limit) {
                        extension = extension2;
                    } else {
                        int position2 = ArrayDecoders.decodeVarint32(data, number, registers);
                        int tag = registers.int1;
                        int number2 = WireFormat.getTagFieldNumber(tag);
                        int wireType = WireFormat.getTagWireType(tag);
                        switch (number2) {
                            case 2:
                                if (wireType != 0) {
                                    extension = extension2;
                                    if (tag == WireFormat.MESSAGE_SET_ITEM_END_TAG) {
                                        number = position2;
                                        break;
                                    } else {
                                        number = ArrayDecoders.skipField(tag, data, position2, limit, registers);
                                        extension2 = extension;
                                    }
                                } else {
                                    int position3 = ArrayDecoders.decodeVarint32(data, position2, registers);
                                    typeId2 = registers.int1;
                                    extension2 = (GeneratedMessageLite.GeneratedExtension) this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, typeId2);
                                    number = position3;
                                }
                            case 3:
                                if (extension2 != null) {
                                    int position4 = ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor((Class) extension2.getMessageDefaultInstance().getClass()), data, position2, limit, registers);
                                    extensions.setField(extension2.descriptor, registers.object1);
                                    number = position4;
                                } else if (wireType != 2) {
                                    extension = extension2;
                                    if (tag == WireFormat.MESSAGE_SET_ITEM_END_TAG) {
                                    }
                                } else {
                                    int position5 = ArrayDecoders.decodeBytes(data, position2, registers);
                                    rawBytes = (ByteString) registers.object1;
                                    number = position5;
                                }
                                break;
                            default:
                                extension = extension2;
                                if (tag == WireFormat.MESSAGE_SET_ITEM_END_TAG) {
                                }
                                break;
                        }
                    }
                }
                if (rawBytes != null) {
                    unknownFields.storeField(WireFormat.makeTag(typeId2, 2), rawBytes);
                }
                typeId = number;
                extension2 = extension;
            }
        }
        if (typeId != limit) {
            throw InvalidProtocolBufferException.parseFailure();
        }
    }

    @Override // com.android.framework.protobuf.Schema
    public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
    }

    private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, ExtensionSchema<ET> extensionSchema, T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        UB unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
        FieldSet<ET> extensions = extensionSchema.getMutableExtensions(message);
        do {
            try {
                int number = reader.getFieldNumber();
                if (number == Integer.MAX_VALUE) {
                    return;
                }
            } finally {
                unknownFieldSchema.setBuilderToMessage(message, unknownFields);
            }
        } while (parseMessageSetItemOrUnknownField(reader, extensionRegistry, extensionSchema, extensions, unknownFieldSchema, unknownFields));
    }

    @Override // com.android.framework.protobuf.Schema
    public void makeImmutable(T message) {
        this.unknownFieldSchema.makeImmutable(message);
        this.extensionSchema.makeImmutable(message);
    }

    /* JADX WARN: Incorrect condition in loop: B:16:0x0034 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> boolean parseMessageSetItemOrUnknownField(Reader reader, ExtensionRegistryLite extensionRegistry, ExtensionSchema<ET> extensionSchema, FieldSet<ET> extensions, UnknownFieldSchema<UT, UB> unknownFieldSchema, UB unknownFields) throws IOException {
        int startTag = reader.getTag();
        if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
            if (WireFormat.getTagWireType(startTag) == 2) {
                Object extension = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, WireFormat.getTagFieldNumber(startTag));
                if (extension != null) {
                    extensionSchema.parseLengthPrefixedMessageSetItem(reader, extension, extensionRegistry, extensions);
                    return true;
                }
                return unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader);
            }
            return reader.skipField();
        }
        int typeId = 0;
        ByteString rawBytes = null;
        Object extension2 = null;
        while (number != Integer.MAX_VALUE) {
            int tag = reader.getTag();
            if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
                typeId = reader.readUInt32();
                extension2 = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, typeId);
            } else if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
                if (extension2 != null) {
                    extensionSchema.parseLengthPrefixedMessageSetItem(reader, extension2, extensionRegistry, extensions);
                } else {
                    rawBytes = reader.readBytes();
                }
            } else if (!reader.skipField()) {
                break;
            }
        }
        int number = reader.getTag();
        if (number != WireFormat.MESSAGE_SET_ITEM_END_TAG) {
            throw InvalidProtocolBufferException.invalidEndTag();
        }
        if (rawBytes != null) {
            if (extension2 != null) {
                extensionSchema.parseMessageSetItem(rawBytes, extension2, extensionRegistry, extensions);
            } else {
                unknownFieldSchema.addLengthDelimited(unknownFields, typeId, rawBytes);
            }
        }
        return true;
    }

    @Override // com.android.framework.protobuf.Schema
    public final boolean isInitialized(T message) {
        FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
        return extensions.isInitialized();
    }

    @Override // com.android.framework.protobuf.Schema
    public int getSerializedSize(T message) {
        int size = 0 + getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
        if (this.hasExtensions) {
            return size + this.extensionSchema.getExtensions(message).getMessageSetSerializedSize();
        }
        return size;
    }

    private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
        UT unknowns = schema.getFromMessage(message);
        return schema.getSerializedSizeAsMessageSet(unknowns);
    }
}
