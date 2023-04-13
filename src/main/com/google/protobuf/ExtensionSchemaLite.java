package com.google.protobuf;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@CheckReturnValue
/* loaded from: classes.dex */
final class ExtensionSchemaLite extends ExtensionSchema<GeneratedMessageLite.ExtensionDescriptor> {
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ExtensionSchema
    public boolean hasExtensions(MessageLite prototype) {
        return prototype instanceof GeneratedMessageLite.ExtendableMessage;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ExtensionSchema
    public FieldSet<GeneratedMessageLite.ExtensionDescriptor> getExtensions(Object message) {
        return ((GeneratedMessageLite.ExtendableMessage) message).extensions;
    }

    @Override // com.google.protobuf.ExtensionSchema
    void setExtensions(Object message, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) {
        ((GeneratedMessageLite.ExtendableMessage) message).extensions = extensions;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ExtensionSchema
    public FieldSet<GeneratedMessageLite.ExtensionDescriptor> getMutableExtensions(Object message) {
        return ((GeneratedMessageLite.ExtendableMessage) message).ensureExtensionsAreMutable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ExtensionSchema
    public void makeImmutable(Object message) {
        getExtensions(message).makeImmutable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.protobuf.ExtensionSchema
    public <UT, UB> UB parseExtension(Object containerMessage, Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) throws IOException {
        Object value;
        UB unknownFields2 = unknownFields;
        GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension) extensionObject;
        int fieldNumber = extension.getNumber();
        if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
            switch (C00141.$SwitchMap$com$google$protobuf$WireFormat$FieldType[extension.getLiteType().ordinal()]) {
                case 1:
                    List<Integer> list = new ArrayList<>();
                    reader.readDoubleList(list);
                    value = list;
                    break;
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    List<Long> list2 = new ArrayList<>();
                    reader.readFloatList(list2);
                    value = list2;
                    break;
                case WireFormat.WIRETYPE_START_GROUP /* 3 */:
                    List<Long> list3 = new ArrayList<>();
                    reader.readInt64List(list3);
                    value = list3;
                    break;
                case 4:
                    List<Long> list4 = new ArrayList<>();
                    reader.readUInt64List(list4);
                    value = list4;
                    break;
                case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                    List<Long> list5 = new ArrayList<>();
                    reader.readInt32List(list5);
                    value = list5;
                    break;
                case 6:
                    List<Long> list6 = new ArrayList<>();
                    reader.readFixed64List(list6);
                    value = list6;
                    break;
                case 7:
                    List<Long> list7 = new ArrayList<>();
                    reader.readFixed32List(list7);
                    value = list7;
                    break;
                case 8:
                    List<Long> list8 = new ArrayList<>();
                    reader.readBoolList(list8);
                    value = list8;
                    break;
                case 9:
                    List<Long> list9 = new ArrayList<>();
                    reader.readUInt32List(list9);
                    value = list9;
                    break;
                case 10:
                    List<Long> list10 = new ArrayList<>();
                    reader.readSFixed32List(list10);
                    value = list10;
                    break;
                case 11:
                    List<Long> list11 = new ArrayList<>();
                    reader.readSFixed64List(list11);
                    value = list11;
                    break;
                case 12:
                    List<Long> list12 = new ArrayList<>();
                    reader.readSInt32List(list12);
                    value = list12;
                    break;
                case 13:
                    List<Long> list13 = new ArrayList<>();
                    reader.readSInt64List(list13);
                    value = list13;
                    break;
                case 14:
                    List<Integer> list14 = new ArrayList<>();
                    reader.readEnumList(list14);
                    value = list14;
                    unknownFields2 = SchemaUtil.filterUnknownEnumList(containerMessage, fieldNumber, list14, extension.descriptor.getEnumType(), unknownFields, unknownFieldSchema);
                    break;
                default:
                    throw new IllegalStateException("Type cannot be packed: " + extension.descriptor.getLiteType());
            }
            extensions.setField(extension.descriptor, value);
        } else {
            Object value2 = null;
            if (extension.getLiteType() == WireFormat.FieldType.ENUM) {
                int number = reader.readInt32();
                Object enumValue = extension.descriptor.getEnumType().findValueByNumber(number);
                if (enumValue == null) {
                    return (UB) SchemaUtil.storeUnknownEnum(containerMessage, fieldNumber, number, unknownFields2, unknownFieldSchema);
                }
                value2 = Integer.valueOf(number);
            } else {
                switch (C00141.$SwitchMap$com$google$protobuf$WireFormat$FieldType[extension.getLiteType().ordinal()]) {
                    case 1:
                        value2 = Double.valueOf(reader.readDouble());
                        break;
                    case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                        value2 = Float.valueOf(reader.readFloat());
                        break;
                    case WireFormat.WIRETYPE_START_GROUP /* 3 */:
                        value2 = Long.valueOf(reader.readInt64());
                        break;
                    case 4:
                        value2 = Long.valueOf(reader.readUInt64());
                        break;
                    case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                        value2 = Integer.valueOf(reader.readInt32());
                        break;
                    case 6:
                        value2 = Long.valueOf(reader.readFixed64());
                        break;
                    case 7:
                        value2 = Integer.valueOf(reader.readFixed32());
                        break;
                    case 8:
                        value2 = Boolean.valueOf(reader.readBool());
                        break;
                    case 9:
                        value2 = Integer.valueOf(reader.readUInt32());
                        break;
                    case 10:
                        value2 = Integer.valueOf(reader.readSFixed32());
                        break;
                    case 11:
                        value2 = Long.valueOf(reader.readSFixed64());
                        break;
                    case 12:
                        value2 = Integer.valueOf(reader.readSInt32());
                        break;
                    case 13:
                        value2 = Long.valueOf(reader.readSInt64());
                        break;
                    case 14:
                        throw new IllegalStateException("Shouldn't reach here.");
                    case 15:
                        value2 = reader.readBytes();
                        break;
                    case 16:
                        value2 = reader.readString();
                        break;
                    case 17:
                        if (!extension.isRepeated()) {
                            Object oldValue = extensions.getField(extension.descriptor);
                            if (oldValue instanceof GeneratedMessageLite) {
                                Schema extSchema = Protobuf.getInstance().schemaFor((Protobuf) oldValue);
                                if (!((GeneratedMessageLite) oldValue).isMutable()) {
                                    Object newValue = extSchema.newInstance();
                                    extSchema.mergeFrom(newValue, oldValue);
                                    extensions.setField(extension.descriptor, newValue);
                                    oldValue = newValue;
                                }
                                reader.mergeGroupField(oldValue, extSchema, extensionRegistry);
                                return unknownFields2;
                            }
                        }
                        value2 = reader.readGroup(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
                        break;
                    case 18:
                        if (!extension.isRepeated()) {
                            Object oldValue2 = extensions.getField(extension.descriptor);
                            if (oldValue2 instanceof GeneratedMessageLite) {
                                Schema extSchema2 = Protobuf.getInstance().schemaFor((Protobuf) oldValue2);
                                if (!((GeneratedMessageLite) oldValue2).isMutable()) {
                                    Object newValue2 = extSchema2.newInstance();
                                    extSchema2.mergeFrom(newValue2, oldValue2);
                                    extensions.setField(extension.descriptor, newValue2);
                                    oldValue2 = newValue2;
                                }
                                reader.mergeMessageField(oldValue2, extSchema2, extensionRegistry);
                                return unknownFields2;
                            }
                        }
                        value2 = reader.readMessage(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
                        break;
                }
            }
            if (extension.isRepeated()) {
                extensions.addRepeatedField(extension.descriptor, value2);
            } else {
                switch (extension.getLiteType()) {
                    case GROUP:
                    case MESSAGE:
                        Object oldValue3 = extensions.getField(extension.descriptor);
                        if (oldValue3 != null) {
                            value2 = Internal.mergeMessage(oldValue3, value2);
                            break;
                        }
                        break;
                }
                extensions.setField(extension.descriptor, value2);
            }
        }
        return unknownFields2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ExtensionSchema
    public int extensionNumber(Map.Entry<?, ?> extension) {
        GeneratedMessageLite.ExtensionDescriptor descriptor = (GeneratedMessageLite.ExtensionDescriptor) extension.getKey();
        return descriptor.getNumber();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ExtensionSchema
    public void serializeExtension(Writer writer, Map.Entry<?, ?> extension) throws IOException {
        GeneratedMessageLite.ExtensionDescriptor descriptor = (GeneratedMessageLite.ExtensionDescriptor) extension.getKey();
        if (descriptor.isRepeated()) {
            switch (C00141.$SwitchMap$com$google$protobuf$WireFormat$FieldType[descriptor.getLiteType().ordinal()]) {
                case 1:
                    SchemaUtil.writeDoubleList(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                    SchemaUtil.writeFloatList(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case WireFormat.WIRETYPE_START_GROUP /* 3 */:
                    SchemaUtil.writeInt64List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 4:
                    SchemaUtil.writeUInt64List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                    SchemaUtil.writeInt32List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 6:
                    SchemaUtil.writeFixed64List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 7:
                    SchemaUtil.writeFixed32List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 8:
                    SchemaUtil.writeBoolList(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 9:
                    SchemaUtil.writeUInt32List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 10:
                    SchemaUtil.writeSFixed32List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 11:
                    SchemaUtil.writeSFixed64List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 12:
                    SchemaUtil.writeSInt32List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 13:
                    SchemaUtil.writeSInt64List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 14:
                    SchemaUtil.writeInt32List(descriptor.getNumber(), (List) extension.getValue(), writer, descriptor.isPacked());
                    return;
                case 15:
                    SchemaUtil.writeBytesList(descriptor.getNumber(), (List) extension.getValue(), writer);
                    return;
                case 16:
                    SchemaUtil.writeStringList(descriptor.getNumber(), (List) extension.getValue(), writer);
                    return;
                case 17:
                    List<?> data = (List) extension.getValue();
                    if (data != null && !data.isEmpty()) {
                        SchemaUtil.writeGroupList(descriptor.getNumber(), (List) extension.getValue(), writer, Protobuf.getInstance().schemaFor((Class) data.get(0).getClass()));
                        return;
                    }
                    return;
                case 18:
                    List<?> data2 = (List) extension.getValue();
                    if (data2 != null && !data2.isEmpty()) {
                        SchemaUtil.writeMessageList(descriptor.getNumber(), (List) extension.getValue(), writer, Protobuf.getInstance().schemaFor((Class) data2.get(0).getClass()));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
        switch (C00141.$SwitchMap$com$google$protobuf$WireFormat$FieldType[descriptor.getLiteType().ordinal()]) {
            case 1:
                writer.writeDouble(descriptor.getNumber(), ((Double) extension.getValue()).doubleValue());
                return;
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                writer.writeFloat(descriptor.getNumber(), ((Float) extension.getValue()).floatValue());
                return;
            case WireFormat.WIRETYPE_START_GROUP /* 3 */:
                writer.writeInt64(descriptor.getNumber(), ((Long) extension.getValue()).longValue());
                return;
            case 4:
                writer.writeUInt64(descriptor.getNumber(), ((Long) extension.getValue()).longValue());
                return;
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                writer.writeInt32(descriptor.getNumber(), ((Integer) extension.getValue()).intValue());
                return;
            case 6:
                writer.writeFixed64(descriptor.getNumber(), ((Long) extension.getValue()).longValue());
                return;
            case 7:
                writer.writeFixed32(descriptor.getNumber(), ((Integer) extension.getValue()).intValue());
                return;
            case 8:
                writer.writeBool(descriptor.getNumber(), ((Boolean) extension.getValue()).booleanValue());
                return;
            case 9:
                writer.writeUInt32(descriptor.getNumber(), ((Integer) extension.getValue()).intValue());
                return;
            case 10:
                writer.writeSFixed32(descriptor.getNumber(), ((Integer) extension.getValue()).intValue());
                return;
            case 11:
                writer.writeSFixed64(descriptor.getNumber(), ((Long) extension.getValue()).longValue());
                return;
            case 12:
                writer.writeSInt32(descriptor.getNumber(), ((Integer) extension.getValue()).intValue());
                return;
            case 13:
                writer.writeSInt64(descriptor.getNumber(), ((Long) extension.getValue()).longValue());
                return;
            case 14:
                writer.writeInt32(descriptor.getNumber(), ((Integer) extension.getValue()).intValue());
                return;
            case 15:
                writer.writeBytes(descriptor.getNumber(), (ByteString) extension.getValue());
                return;
            case 16:
                writer.writeString(descriptor.getNumber(), (String) extension.getValue());
                return;
            case 17:
                writer.writeGroup(descriptor.getNumber(), extension.getValue(), Protobuf.getInstance().schemaFor((Class) extension.getValue().getClass()));
                return;
            case 18:
                writer.writeMessage(descriptor.getNumber(), extension.getValue(), Protobuf.getInstance().schemaFor((Class) extension.getValue().getClass()));
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ExtensionSchema
    public Object findExtensionByNumber(ExtensionRegistryLite extensionRegistry, MessageLite defaultInstance, int number) {
        return extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ExtensionSchema
    public void parseLengthPrefixedMessageSetItem(Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
        GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension) extensionObject;
        Object value = reader.readMessage(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
        extensions.setField(extension.descriptor, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ExtensionSchema
    public void parseMessageSetItem(ByteString data, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
        GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension) extensionObject;
        MessageLite.Builder builder = extension.getMessageDefaultInstance().newBuilderForType();
        CodedInputStream input = data.newCodedInput();
        builder.mergeFrom(input, extensionRegistry);
        extensions.setField(extension.descriptor, builder.buildPartial());
        input.checkLastTagWas(0);
    }
}
