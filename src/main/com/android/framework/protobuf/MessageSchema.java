package com.android.framework.protobuf;

import com.android.framework.protobuf.ArrayDecoders;
import com.android.framework.protobuf.ByteString;
import com.android.framework.protobuf.FieldSet;
import com.android.framework.protobuf.Internal;
import com.android.framework.protobuf.InvalidProtocolBufferException;
import com.android.framework.protobuf.MapEntryLite;
import com.android.framework.protobuf.WireFormat;
import com.android.framework.protobuf.Writer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.misc.Unsafe;
/* JADX INFO: Access modifiers changed from: package-private */
@CheckReturnValue
/* loaded from: classes4.dex */
public final class MessageSchema<T> implements Schema<T> {
    private static final int ENFORCE_UTF8_MASK = 536870912;
    private static final int FIELD_TYPE_MASK = 267386880;
    private static final int INTS_PER_FIELD = 3;
    private static final int NO_PRESENCE_SENTINEL = 1048575;
    private static final int OFFSET_BITS = 20;
    private static final int OFFSET_MASK = 1048575;
    static final int ONEOF_TYPE_OFFSET = 51;
    private static final int REQUIRED_MASK = 268435456;
    private final int[] buffer;
    private final int checkInitializedCount;
    private final MessageLite defaultInstance;
    private final ExtensionSchema<?> extensionSchema;
    private final boolean hasExtensions;
    private final int[] intArray;
    private final ListFieldSchema listFieldSchema;
    private final boolean lite;
    private final MapFieldSchema mapFieldSchema;
    private final int maxFieldNumber;
    private final int minFieldNumber;
    private final NewInstanceSchema newInstanceSchema;
    private final Object[] objects;
    private final boolean proto3;
    private final int repeatedFieldOffsetStart;
    private final UnknownFieldSchema<?, ?> unknownFieldSchema;
    private final boolean useCachedSizeField;
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

    private MessageSchema(int[] buffer, Object[] objects, int minFieldNumber, int maxFieldNumber, MessageLite defaultInstance, boolean proto3, boolean useCachedSizeField, int[] intArray, int checkInitialized, int mapFieldPositions, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        this.buffer = buffer;
        this.objects = objects;
        this.minFieldNumber = minFieldNumber;
        this.maxFieldNumber = maxFieldNumber;
        this.lite = defaultInstance instanceof GeneratedMessageLite;
        this.proto3 = proto3;
        this.hasExtensions = extensionSchema != null && extensionSchema.hasExtensions(defaultInstance);
        this.useCachedSizeField = useCachedSizeField;
        this.intArray = intArray;
        this.checkInitializedCount = checkInitialized;
        this.repeatedFieldOffsetStart = mapFieldPositions;
        this.newInstanceSchema = newInstanceSchema;
        this.listFieldSchema = listFieldSchema;
        this.unknownFieldSchema = unknownFieldSchema;
        this.extensionSchema = extensionSchema;
        this.defaultInstance = defaultInstance;
        this.mapFieldSchema = mapFieldSchema;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> MessageSchema<T> newSchema(Class<T> messageClass, MessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        if (messageInfo instanceof RawMessageInfo) {
            return newSchemaForRawMessageInfo((RawMessageInfo) messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
        }
        return newSchemaForMessageInfo((StructuralMessageInfo) messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    static <T> MessageSchema<T> newSchemaForRawMessageInfo(RawMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        int objectsPosition;
        int[] intArray;
        int oneofCount;
        int minFieldNumber;
        int maxFieldNumber;
        int numEntries;
        int mapFieldCount;
        int checkInitialized;
        int i;
        int next;
        int i2;
        int next2;
        int i3;
        int next3;
        int i4;
        int next4;
        int i5;
        int next5;
        int i6;
        int next6;
        int i7;
        int next7;
        int i8;
        int next8;
        int fieldNumber;
        boolean z;
        int fieldOffset;
        String info;
        int oneofCount2;
        int presenceFieldOffset;
        int next9;
        int i9;
        int presenceMaskShift;
        Field hasBitsField;
        int i10;
        int next10;
        Field oneofField;
        Field oneofCaseField;
        int i11;
        int next11;
        int i12;
        int next12;
        int i13;
        int next13;
        int i14;
        int next14;
        int i15;
        int next15;
        boolean isProto3 = messageInfo.getSyntax() == ProtoSyntax.PROTO3;
        String info2 = messageInfo.getStringInfo();
        int length = info2.length();
        int i16 = 0 + 1;
        int next16 = info2.charAt(0);
        int i17 = 55296;
        if (next16 >= 55296) {
            int result = next16 & 8191;
            int shift = 13;
            while (true) {
                i15 = i16 + 1;
                next15 = info2.charAt(i16);
                if (next15 < 55296) {
                    break;
                }
                result |= (next15 & 8191) << shift;
                shift += 13;
                i16 = i15;
            }
            next16 = result | (next15 << shift);
            i16 = i15;
        }
        int i18 = i16 + 1;
        int next17 = info2.charAt(i16);
        if (next17 >= 55296) {
            int result2 = next17 & 8191;
            int shift2 = 13;
            while (true) {
                i14 = i18 + 1;
                next14 = info2.charAt(i18);
                if (next14 < 55296) {
                    break;
                }
                result2 |= (next14 & 8191) << shift2;
                shift2 += 13;
                i18 = i14;
            }
            next17 = result2 | (next14 << shift2);
            i18 = i14;
        }
        int fieldCount = next17;
        if (fieldCount == 0) {
            int[] intArray2 = EMPTY_INT_ARRAY;
            oneofCount = 0;
            minFieldNumber = 0;
            maxFieldNumber = 0;
            numEntries = 0;
            mapFieldCount = 0;
            checkInitialized = 0;
            intArray = intArray2;
            objectsPosition = 0;
        } else {
            int i19 = i18 + 1;
            int next18 = info2.charAt(i18);
            if (next18 >= 55296) {
                int result3 = next18 & 8191;
                int shift3 = 13;
                while (true) {
                    i8 = i19 + 1;
                    next8 = info2.charAt(i19);
                    if (next8 < 55296) {
                        break;
                    }
                    result3 |= (next8 & 8191) << shift3;
                    shift3 += 13;
                    i19 = i8;
                }
                next18 = result3 | (next8 << shift3);
                i19 = i8;
            }
            int result4 = next18;
            int i20 = i19 + 1;
            int next19 = info2.charAt(i19);
            if (next19 >= 55296) {
                int result5 = next19 & 8191;
                int shift4 = 13;
                while (true) {
                    i7 = i20 + 1;
                    next7 = info2.charAt(i20);
                    if (next7 < 55296) {
                        break;
                    }
                    result5 |= (next7 & 8191) << shift4;
                    shift4 += 13;
                    i20 = i7;
                }
                next19 = result5 | (next7 << shift4);
                i20 = i7;
            }
            int result6 = next19;
            int i21 = i20 + 1;
            int next20 = info2.charAt(i20);
            if (next20 >= 55296) {
                int result7 = next20 & 8191;
                int shift5 = 13;
                while (true) {
                    i6 = i21 + 1;
                    next6 = info2.charAt(i21);
                    if (next6 < 55296) {
                        break;
                    }
                    result7 |= (next6 & 8191) << shift5;
                    shift5 += 13;
                    i21 = i6;
                }
                next20 = result7 | (next6 << shift5);
                i21 = i6;
            }
            int result8 = next20;
            int i22 = i21 + 1;
            int next21 = info2.charAt(i21);
            if (next21 >= 55296) {
                int result9 = next21 & 8191;
                int shift6 = 13;
                while (true) {
                    i5 = i22 + 1;
                    next5 = info2.charAt(i22);
                    if (next5 < 55296) {
                        break;
                    }
                    result9 |= (next5 & 8191) << shift6;
                    shift6 += 13;
                    i22 = i5;
                }
                next21 = result9 | (next5 << shift6);
                i22 = i5;
            }
            int result10 = next21;
            int i23 = i22 + 1;
            int next22 = info2.charAt(i22);
            if (next22 >= 55296) {
                int result11 = next22 & 8191;
                int shift7 = 13;
                while (true) {
                    i4 = i23 + 1;
                    next4 = info2.charAt(i23);
                    if (next4 < 55296) {
                        break;
                    }
                    result11 |= (next4 & 8191) << shift7;
                    shift7 += 13;
                    i23 = i4;
                }
                next22 = result11 | (next4 << shift7);
                i23 = i4;
            }
            int result12 = next22;
            int i24 = i23 + 1;
            int next23 = info2.charAt(i23);
            if (next23 >= 55296) {
                int result13 = next23 & 8191;
                int shift8 = 13;
                while (true) {
                    i3 = i24 + 1;
                    next3 = info2.charAt(i24);
                    if (next3 < 55296) {
                        break;
                    }
                    result13 |= (next3 & 8191) << shift8;
                    shift8 += 13;
                    i24 = i3;
                }
                next23 = result13 | (next3 << shift8);
                i24 = i3;
            }
            int result14 = next23;
            int i25 = i24 + 1;
            int next24 = info2.charAt(i24);
            if (next24 >= 55296) {
                int result15 = next24 & 8191;
                int shift9 = 13;
                while (true) {
                    i2 = i25 + 1;
                    next2 = info2.charAt(i25);
                    if (next2 < 55296) {
                        break;
                    }
                    result15 |= (next2 & 8191) << shift9;
                    shift9 += 13;
                    i25 = i2;
                }
                next24 = result15 | (next2 << shift9);
                i25 = i2;
            }
            int result16 = next24;
            int i26 = i25 + 1;
            next17 = info2.charAt(i25);
            if (next17 >= 55296) {
                int result17 = next17 & 8191;
                int shift10 = 13;
                while (true) {
                    i = i26 + 1;
                    next = info2.charAt(i26);
                    if (next < 55296) {
                        break;
                    }
                    result17 |= (next & 8191) << shift10;
                    shift10 += 13;
                    i26 = i;
                }
                next17 = result17 | (next << shift10);
                i26 = i;
            }
            int result18 = next17;
            int[] intArray3 = new int[result18 + result14 + result16];
            objectsPosition = (result4 * 2) + result6;
            intArray = intArray3;
            oneofCount = result4;
            minFieldNumber = result8;
            maxFieldNumber = result10;
            numEntries = result12;
            mapFieldCount = result14;
            checkInitialized = result18;
            i18 = i26;
        }
        Unsafe unsafe = UNSAFE;
        Object[] messageInfoObjects = messageInfo.getObjects();
        Class<?> messageClass = messageInfo.getDefaultInstance().getClass();
        int[] buffer = new int[numEntries * 3];
        Object[] objects = new Object[numEntries * 2];
        int mapFieldIndex = checkInitialized;
        int repeatedFieldIndex = checkInitialized + mapFieldCount;
        int checkInitializedPosition = 0;
        int mapFieldIndex2 = mapFieldIndex;
        int repeatedFieldIndex2 = repeatedFieldIndex;
        int bufferIndex = 0;
        int objectsPosition2 = objectsPosition;
        int i27 = i18;
        while (i27 < length) {
            int i28 = i27 + 1;
            int next25 = info2.charAt(i27);
            if (next25 >= i17) {
                int result19 = next25 & 8191;
                int shift11 = 13;
                while (true) {
                    i13 = i28 + 1;
                    next13 = info2.charAt(i28);
                    if (next13 < i17) {
                        break;
                    }
                    result19 |= (next13 & 8191) << shift11;
                    shift11 += 13;
                    i28 = i13;
                }
                next25 = result19 | (next13 << shift11);
                i28 = i13;
            }
            int result20 = next25;
            int i29 = i28 + 1;
            int next26 = info2.charAt(i28);
            if (next26 >= i17) {
                int result21 = next26 & 8191;
                int shift12 = 13;
                while (true) {
                    i12 = i29 + 1;
                    next12 = info2.charAt(i29);
                    if (next12 < i17) {
                        break;
                    }
                    result21 |= (next12 & 8191) << shift12;
                    shift12 += 13;
                    i29 = i12;
                }
                next26 = result21 | (next12 << shift12);
                i29 = i12;
            }
            int result22 = next26;
            int fieldType = result22 & 255;
            if ((result22 & 1024) != 0) {
                intArray[checkInitializedPosition] = bufferIndex;
                checkInitializedPosition++;
            }
            if (fieldType >= 51) {
                i27 = i29 + 1;
                int next27 = info2.charAt(i29);
                if (next27 >= i17) {
                    int result23 = next27 & 8191;
                    int shift13 = 13;
                    while (true) {
                        i11 = i27 + 1;
                        next11 = info2.charAt(i27);
                        if (next11 < i17) {
                            break;
                        }
                        result23 |= (next11 & 8191) << shift13;
                        shift13 += 13;
                        i27 = i11;
                    }
                    next27 = result23 | (next11 << shift13);
                    i27 = i11;
                }
                int result24 = next27;
                int oneofFieldType = fieldType - 51;
                if (oneofFieldType == 9 || oneofFieldType == 17) {
                    objects[((bufferIndex / 3) * 2) + 1] = messageInfoObjects[objectsPosition2];
                    objectsPosition2++;
                } else if (oneofFieldType == 12 && !isProto3) {
                    objects[((bufferIndex / 3) * 2) + 1] = messageInfoObjects[objectsPosition2];
                    objectsPosition2++;
                }
                int index = result24 * 2;
                Object o = messageInfoObjects[index];
                if (o instanceof Field) {
                    oneofField = (Field) o;
                } else {
                    oneofField = reflectField(messageClass, (String) o);
                    messageInfoObjects[index] = oneofField;
                }
                fieldNumber = result20;
                fieldOffset = (int) unsafe.objectFieldOffset(oneofField);
                int index2 = index + 1;
                Object o2 = messageInfoObjects[index2];
                if (o2 instanceof Field) {
                    oneofCaseField = (Field) o2;
                } else {
                    oneofCaseField = reflectField(messageClass, (String) o2);
                    messageInfoObjects[index2] = oneofCaseField;
                }
                presenceFieldOffset = (int) unsafe.objectFieldOffset(oneofCaseField);
                presenceMaskShift = 0;
                info = info2;
                oneofCount2 = oneofCount;
            } else {
                fieldNumber = result20;
                int objectsPosition3 = objectsPosition2 + 1;
                Field field = reflectField(messageClass, (String) messageInfoObjects[objectsPosition2]);
                if (fieldType == 9 || fieldType == 17) {
                    z = true;
                    objects[((bufferIndex / 3) * 2) + 1] = field.getType();
                } else if (fieldType == 27 || fieldType == 49) {
                    objects[((bufferIndex / 3) * 2) + 1] = messageInfoObjects[objectsPosition3];
                    objectsPosition3++;
                    z = true;
                } else if (fieldType == 12 || fieldType == 30 || fieldType == 44) {
                    if (isProto3) {
                        z = true;
                    } else {
                        objects[((bufferIndex / 3) * 2) + 1] = messageInfoObjects[objectsPosition3];
                        objectsPosition3++;
                        z = true;
                    }
                } else if (fieldType != 50) {
                    z = true;
                } else {
                    int mapFieldIndex3 = mapFieldIndex2 + 1;
                    intArray[mapFieldIndex2] = bufferIndex;
                    int objectsPosition4 = objectsPosition3 + 1;
                    objects[(bufferIndex / 3) * 2] = messageInfoObjects[objectsPosition3];
                    if ((result22 & 2048) == 0) {
                        mapFieldIndex2 = mapFieldIndex3;
                        objectsPosition3 = objectsPosition4;
                        z = true;
                    } else {
                        objects[((bufferIndex / 3) * 2) + 1] = messageInfoObjects[objectsPosition4];
                        mapFieldIndex2 = mapFieldIndex3;
                        objectsPosition3 = objectsPosition4 + 1;
                        z = true;
                    }
                }
                int i30 = i29;
                fieldOffset = (int) unsafe.objectFieldOffset(field);
                int objectsPosition5 = objectsPosition3;
                boolean hasHasBit = (result22 & 4096) == 4096 ? z : false;
                if (!hasHasBit || fieldType > 17) {
                    info = info2;
                    oneofCount2 = oneofCount;
                    presenceFieldOffset = 1048575;
                    next9 = next26;
                    i9 = i30;
                    presenceMaskShift = 0;
                } else {
                    i9 = i30 + 1;
                    int next28 = info2.charAt(i30);
                    if (next28 < 55296) {
                        info = info2;
                    } else {
                        int result25 = next28 & 8191;
                        int shift14 = 13;
                        while (true) {
                            i10 = i9 + 1;
                            next10 = info2.charAt(i9);
                            info = info2;
                            if (next10 < 55296) {
                                break;
                            }
                            result25 |= (next10 & 8191) << shift14;
                            shift14 += 13;
                            i9 = i10;
                            info2 = info;
                        }
                        next28 = result25 | (next10 << shift14);
                        i9 = i10;
                    }
                    int hasBitsIndex = next28;
                    int index3 = (oneofCount * 2) + (hasBitsIndex / 32);
                    Object o3 = messageInfoObjects[index3];
                    oneofCount2 = oneofCount;
                    if (o3 instanceof Field) {
                        hasBitsField = (Field) o3;
                    } else {
                        hasBitsField = reflectField(messageClass, (String) o3);
                        messageInfoObjects[index3] = hasBitsField;
                    }
                    next9 = next28;
                    presenceFieldOffset = (int) unsafe.objectFieldOffset(hasBitsField);
                    int presenceMaskShift2 = hasBitsIndex % 32;
                    presenceMaskShift = presenceMaskShift2;
                }
                if (fieldType >= 18 && fieldType <= 49) {
                    intArray[repeatedFieldIndex2] = fieldOffset;
                    repeatedFieldIndex2++;
                    i27 = i9;
                    objectsPosition2 = objectsPosition5;
                } else {
                    i27 = i9;
                    objectsPosition2 = objectsPosition5;
                }
            }
            int bufferIndex2 = bufferIndex + 1;
            buffer[bufferIndex] = fieldNumber;
            int bufferIndex3 = bufferIndex2 + 1;
            buffer[bufferIndex2] = ((result22 & 512) != 0 ? 536870912 : 0) | ((result22 & 256) != 0 ? 268435456 : 0) | (fieldType << 20) | fieldOffset;
            bufferIndex = bufferIndex3 + 1;
            buffer[bufferIndex3] = (presenceMaskShift << 20) | presenceFieldOffset;
            info2 = info;
            oneofCount = oneofCount2;
            i17 = 55296;
        }
        int length2 = checkInitialized + mapFieldCount;
        return new MessageSchema<>(buffer, objects, minFieldNumber, maxFieldNumber, messageInfo.getDefaultInstance(), isProto3, false, intArray, checkInitialized, length2, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    private static Field reflectField(Class<?> messageClass, String fieldName) {
        try {
            return messageClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Field[] fields = messageClass.getDeclaredFields();
            for (Field field : fields) {
                if (fieldName.equals(field.getName())) {
                    return field;
                }
            }
            throw new RuntimeException("Field " + fieldName + " for " + messageClass.getName() + " not found. Known fields are " + Arrays.toString(fields));
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:34:0x0087 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    static <T> MessageSchema<T> newSchemaForMessageInfo(StructuralMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        int minFieldNumber;
        int maxFieldNumber;
        int[] checkInitialized;
        int[] mapFieldPositions;
        boolean isProto3;
        boolean isProto32 = messageInfo.getSyntax() == ProtoSyntax.PROTO3;
        FieldInfo[] fis = messageInfo.getFields();
        if (fis.length == 0) {
            minFieldNumber = 0;
            maxFieldNumber = 0;
        } else {
            minFieldNumber = fis[0].getFieldNumber();
            maxFieldNumber = fis[fis.length - 1].getFieldNumber();
        }
        int numEntries = fis.length;
        int[] buffer = new int[numEntries * 3];
        Object[] objects = new Object[numEntries * 2];
        int mapFieldCount = 0;
        int repeatedFieldCount = 0;
        for (FieldInfo fi : fis) {
            if (fi.getType() == FieldType.MAP) {
                mapFieldCount++;
            } else if (fi.getType().m74id() >= 18 && fi.getType().m74id() <= 49) {
                repeatedFieldCount++;
            }
        }
        int[] mapFieldPositions2 = mapFieldCount > 0 ? new int[mapFieldCount] : null;
        int[] repeatedFieldOffsets = repeatedFieldCount > 0 ? new int[repeatedFieldCount] : null;
        int[] checkInitialized2 = messageInfo.getCheckInitialized();
        if (checkInitialized2 != null) {
            checkInitialized = checkInitialized2;
        } else {
            checkInitialized = EMPTY_INT_ARRAY;
        }
        int mapFieldCount2 = 0;
        int repeatedFieldCount2 = 0;
        int mapFieldCount3 = 0;
        int checkInitializedIndex = 0;
        int checkInitializedIndex2 = 0;
        while (checkInitializedIndex2 < repeatedFieldCount) {
            FieldInfo fi2 = fis[checkInitializedIndex2];
            int fieldNumber = fi2.getFieldNumber();
            storeFieldData(fi2, buffer, mapFieldCount3, objects);
            if (checkInitializedIndex < checkInitialized.length && checkInitialized[checkInitializedIndex] == fieldNumber) {
                checkInitialized[checkInitializedIndex] = mapFieldCount3;
                checkInitializedIndex++;
            }
            FieldInfo[] fis2 = fis;
            if (fi2.getType() == FieldType.MAP) {
                mapFieldPositions2[mapFieldCount2] = mapFieldCount3;
                mapFieldCount2++;
                isProto3 = isProto32;
            } else if (fi2.getType().m74id() < 18 || fi2.getType().m74id() > 49) {
                isProto3 = isProto32;
            } else {
                isProto3 = isProto32;
                repeatedFieldOffsets[repeatedFieldCount2] = (int) UnsafeUtil.objectFieldOffset(fi2.getField());
                repeatedFieldCount2++;
            }
            checkInitializedIndex2++;
            mapFieldCount3 += 3;
            fis = fis2;
            isProto32 = isProto3;
        }
        boolean isProto33 = isProto32;
        if (mapFieldPositions2 != null) {
            mapFieldPositions = mapFieldPositions2;
        } else {
            mapFieldPositions = EMPTY_INT_ARRAY;
        }
        if (repeatedFieldOffsets == null) {
            repeatedFieldOffsets = EMPTY_INT_ARRAY;
        }
        int[] combined = new int[checkInitialized.length + mapFieldPositions.length + repeatedFieldOffsets.length];
        System.arraycopy(checkInitialized, 0, combined, 0, checkInitialized.length);
        System.arraycopy(mapFieldPositions, 0, combined, checkInitialized.length, mapFieldPositions.length);
        System.arraycopy(repeatedFieldOffsets, 0, combined, checkInitialized.length + mapFieldPositions.length, repeatedFieldOffsets.length);
        int fieldIndex = minFieldNumber;
        return new MessageSchema<>(buffer, objects, fieldIndex, maxFieldNumber, messageInfo.getDefaultInstance(), isProto33, true, combined, checkInitialized.length, checkInitialized.length + mapFieldPositions.length, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    private static void storeFieldData(FieldInfo fi, int[] buffer, int bufferIndex, Object[] objects) {
        int fieldOffset;
        int typeId;
        int typeId2;
        int presenceFieldOffset;
        int presenceFieldOffset2;
        OneofInfo oneof = fi.getOneof();
        if (oneof != null) {
            typeId = fi.getType().m74id() + 51;
            fieldOffset = (int) UnsafeUtil.objectFieldOffset(oneof.getValueField());
            typeId2 = (int) UnsafeUtil.objectFieldOffset(oneof.getCaseField());
            presenceFieldOffset = 0;
        } else {
            FieldType type = fi.getType();
            fieldOffset = (int) UnsafeUtil.objectFieldOffset(fi.getField());
            int typeId3 = type.m74id();
            if (!type.isList() && !type.isMap()) {
                Field presenceField = fi.getPresenceField();
                if (presenceField == null) {
                    presenceFieldOffset2 = 1048575;
                } else {
                    presenceFieldOffset2 = (int) UnsafeUtil.objectFieldOffset(presenceField);
                }
                presenceFieldOffset = Integer.numberOfTrailingZeros(fi.getPresenceMask());
                typeId = typeId3;
                typeId2 = presenceFieldOffset2;
            } else if (fi.getCachedSizeField() == null) {
                typeId = typeId3;
                typeId2 = 0;
                presenceFieldOffset = 0;
            } else {
                int presenceFieldOffset3 = (int) UnsafeUtil.objectFieldOffset(fi.getCachedSizeField());
                typeId = typeId3;
                typeId2 = presenceFieldOffset3;
                presenceFieldOffset = 0;
            }
        }
        buffer[bufferIndex] = fi.getFieldNumber();
        buffer[bufferIndex + 1] = (fi.isEnforceUtf8() ? 536870912 : 0) | (fi.isRequired() ? 268435456 : 0) | (typeId << 20) | fieldOffset;
        buffer[bufferIndex + 2] = (presenceFieldOffset << 20) | typeId2;
        Object messageFieldClass = fi.getMessageFieldClass();
        if (fi.getMapDefaultEntry() != null) {
            objects[(bufferIndex / 3) * 2] = fi.getMapDefaultEntry();
            if (messageFieldClass != null) {
                objects[((bufferIndex / 3) * 2) + 1] = messageFieldClass;
            } else if (fi.getEnumVerifier() != null) {
                objects[((bufferIndex / 3) * 2) + 1] = fi.getEnumVerifier();
            }
        } else if (messageFieldClass != null) {
            objects[((bufferIndex / 3) * 2) + 1] = messageFieldClass;
        } else if (fi.getEnumVerifier() != null) {
            objects[((bufferIndex / 3) * 2) + 1] = fi.getEnumVerifier();
        }
    }

    @Override // com.android.framework.protobuf.Schema
    public T newInstance() {
        return (T) this.newInstanceSchema.newInstance(this.defaultInstance);
    }

    @Override // com.android.framework.protobuf.Schema
    public boolean equals(T message, T other) {
        int bufferLength = this.buffer.length;
        for (int pos = 0; pos < bufferLength; pos += 3) {
            if (!equals(message, other, pos)) {
                return false;
            }
        }
        Object messageUnknown = this.unknownFieldSchema.getFromMessage(message);
        Object otherUnknown = this.unknownFieldSchema.getFromMessage(other);
        if (messageUnknown.equals(otherUnknown)) {
            if (this.hasExtensions) {
                FieldSet<?> messageExtensions = this.extensionSchema.getExtensions(message);
                FieldSet<?> otherExtensions = this.extensionSchema.getExtensions(other);
                return messageExtensions.equals(otherExtensions);
            }
            return true;
        }
        return false;
    }

    private boolean equals(T message, T other, int pos) {
        int typeAndOffset = typeAndOffsetAt(pos);
        long offset = offset(typeAndOffset);
        switch (type(typeAndOffset)) {
            case 0:
                return arePresentForEquals(message, other, pos) && Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)) == Double.doubleToLongBits(UnsafeUtil.getDouble(other, offset));
            case 1:
                return arePresentForEquals(message, other, pos) && Float.floatToIntBits(UnsafeUtil.getFloat(message, offset)) == Float.floatToIntBits(UnsafeUtil.getFloat(other, offset));
            case 2:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 3:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 4:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 5:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 6:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 7:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getBoolean(message, offset) == UnsafeUtil.getBoolean(other, offset);
            case 8:
                return arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 9:
                return arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 10:
                return arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 11:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 12:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 13:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 14:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 15:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 16:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 17:
                return arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
                return SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 50:
                return SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
                return isOneofCaseEqual(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            default:
                return true;
        }
    }

    @Override // com.android.framework.protobuf.Schema
    public int hashCode(T message) {
        int hashCode = 0;
        int bufferLength = this.buffer.length;
        for (int pos = 0; pos < bufferLength; pos += 3) {
            int typeAndOffset = typeAndOffsetAt(pos);
            int entryNumber = numberAt(pos);
            long offset = offset(typeAndOffset);
            switch (type(typeAndOffset)) {
                case 0:
                    hashCode = (hashCode * 53) + Internal.hashLong(Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)));
                    break;
                case 1:
                    hashCode = (hashCode * 53) + Float.floatToIntBits(UnsafeUtil.getFloat(message, offset));
                    break;
                case 2:
                    hashCode = (hashCode * 53) + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    break;
                case 3:
                    hashCode = (hashCode * 53) + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    break;
                case 4:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 5:
                    hashCode = (hashCode * 53) + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    break;
                case 6:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 7:
                    hashCode = (hashCode * 53) + Internal.hashBoolean(UnsafeUtil.getBoolean(message, offset));
                    break;
                case 8:
                    int protoHash = hashCode * 53;
                    int hashCode2 = protoHash + ((String) UnsafeUtil.getObject(message, offset)).hashCode();
                    hashCode = hashCode2;
                    break;
                case 9:
                    int protoHash2 = 37;
                    Object submessage = UnsafeUtil.getObject(message, offset);
                    if (submessage != null) {
                        protoHash2 = submessage.hashCode();
                    }
                    hashCode = (hashCode * 53) + protoHash2;
                    break;
                case 10:
                    hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                    break;
                case 11:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 12:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 13:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 14:
                    hashCode = (hashCode * 53) + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    break;
                case 15:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 16:
                    int protoHash3 = hashCode * 53;
                    int hashCode3 = protoHash3 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    hashCode = hashCode3;
                    break;
                case 17:
                    int protoHash4 = 37;
                    Object submessage2 = UnsafeUtil.getObject(message, offset);
                    if (submessage2 != null) {
                        protoHash4 = submessage2.hashCode();
                    }
                    hashCode = (hashCode * 53) + protoHash4;
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                    hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                    break;
                case 50:
                    hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                    break;
                case 51:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(Double.doubleToLongBits(oneofDoubleAt(message, offset)));
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Float.floatToIntBits(oneofFloatAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                        break;
                    } else {
                        break;
                    }
                case 56:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 57:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                        break;
                    } else {
                        break;
                    }
                case 58:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashBoolean(oneofBooleanAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + ((String) UnsafeUtil.getObject(message, offset)).hashCode();
                        break;
                    } else {
                        break;
                    }
                case 60:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                        break;
                    } else {
                        break;
                    }
                case 61:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                        break;
                    } else {
                        break;
                    }
                case 63:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                        break;
                    } else {
                        break;
                    }
                case 65:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                        break;
                    } else {
                        break;
                    }
            }
        }
        int pos2 = hashCode * 53;
        int hashCode4 = pos2 + this.unknownFieldSchema.getFromMessage(message).hashCode();
        if (this.hasExtensions) {
            return (hashCode4 * 53) + this.extensionSchema.getExtensions(message).hashCode();
        }
        return hashCode4;
    }

    @Override // com.android.framework.protobuf.Schema
    public void mergeFrom(T message, T other) {
        checkMutable(message);
        if (other == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < this.buffer.length; i += 3) {
            mergeSingleField(message, other, i);
        }
        SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
        if (this.hasExtensions) {
            SchemaUtil.mergeExtensions(this.extensionSchema, message, other);
        }
    }

    private void mergeSingleField(T message, T other, int pos) {
        int typeAndOffset = typeAndOffsetAt(pos);
        long offset = offset(typeAndOffset);
        int number = numberAt(pos);
        switch (type(typeAndOffset)) {
            case 0:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putDouble(message, offset, UnsafeUtil.getDouble(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 1:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putFloat(message, offset, UnsafeUtil.getFloat(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 2:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 3:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 4:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 5:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 6:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 7:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putBoolean(message, offset, UnsafeUtil.getBoolean(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 8:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 9:
                mergeMessage(message, other, pos);
                return;
            case 10:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 11:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 12:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 13:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 14:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 15:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 16:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                    return;
                }
                return;
            case 17:
                mergeMessage(message, other, pos);
                return;
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
                this.listFieldSchema.mergeListsAt(message, other, offset);
                return;
            case 50:
                SchemaUtil.mergeMap(this.mapFieldSchema, message, other, offset);
                return;
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
                if (isOneofPresent(other, number, pos)) {
                    UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                    setOneofPresent(message, number, pos);
                    return;
                }
                return;
            case 60:
                mergeOneofMessage(message, other, pos);
                return;
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
                if (isOneofPresent(other, number, pos)) {
                    UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                    setOneofPresent(message, number, pos);
                    return;
                }
                return;
            case 68:
                mergeOneofMessage(message, other, pos);
                return;
            default:
                return;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void mergeMessage(T targetParent, T sourceParent, int pos) {
        if (!isFieldPresent(sourceParent, pos)) {
            return;
        }
        int typeAndOffset = typeAndOffsetAt(pos);
        long offset = offset(typeAndOffset);
        Unsafe unsafe = UNSAFE;
        Object source = unsafe.getObject(sourceParent, offset);
        if (source == null) {
            throw new IllegalStateException("Source subfield " + numberAt(pos) + " is present but null: " + sourceParent);
        }
        Schema fieldSchema = getMessageFieldSchema(pos);
        if (!isFieldPresent(targetParent, pos)) {
            if (!isMutable(source)) {
                unsafe.putObject(targetParent, offset, source);
            } else {
                Object copyOfSource = fieldSchema.newInstance();
                fieldSchema.mergeFrom(copyOfSource, source);
                unsafe.putObject(targetParent, offset, copyOfSource);
            }
            setFieldPresent(targetParent, pos);
            return;
        }
        Object target = unsafe.getObject(targetParent, offset);
        if (!isMutable(target)) {
            Object newInstance = fieldSchema.newInstance();
            fieldSchema.mergeFrom(newInstance, target);
            unsafe.putObject(targetParent, offset, newInstance);
            target = newInstance;
        }
        fieldSchema.mergeFrom(target, source);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void mergeOneofMessage(T targetParent, T sourceParent, int pos) {
        int number = numberAt(pos);
        if (!isOneofPresent(sourceParent, number, pos)) {
            return;
        }
        long offset = offset(typeAndOffsetAt(pos));
        Unsafe unsafe = UNSAFE;
        Object source = unsafe.getObject(sourceParent, offset);
        if (source == null) {
            throw new IllegalStateException("Source subfield " + numberAt(pos) + " is present but null: " + sourceParent);
        }
        Schema fieldSchema = getMessageFieldSchema(pos);
        if (!isOneofPresent(targetParent, number, pos)) {
            if (!isMutable(source)) {
                unsafe.putObject(targetParent, offset, source);
            } else {
                Object copyOfSource = fieldSchema.newInstance();
                fieldSchema.mergeFrom(copyOfSource, source);
                unsafe.putObject(targetParent, offset, copyOfSource);
            }
            setOneofPresent(targetParent, number, pos);
            return;
        }
        Object target = unsafe.getObject(targetParent, offset);
        if (!isMutable(target)) {
            Object newInstance = fieldSchema.newInstance();
            fieldSchema.mergeFrom(newInstance, target);
            unsafe.putObject(targetParent, offset, newInstance);
            target = newInstance;
        }
        fieldSchema.mergeFrom(target, source);
    }

    @Override // com.android.framework.protobuf.Schema
    public int getSerializedSize(T message) {
        return this.proto3 ? getSerializedSizeProto3(message) : getSerializedSizeProto2(message);
    }

    private int getSerializedSizeProto2(T message) {
        int currentPresenceFieldOffset;
        int size = 0;
        Unsafe unsafe = UNSAFE;
        int currentPresenceFieldOffset2 = 1048575;
        int currentPresenceField = 0;
        int i = 0;
        while (i < this.buffer.length) {
            int typeAndOffset = typeAndOffsetAt(i);
            int number = numberAt(i);
            int fieldType = type(typeAndOffset);
            int presenceMaskAndOffset = 0;
            int presenceMask = 0;
            if (fieldType > 17) {
                if (this.useCachedSizeField && fieldType >= FieldType.DOUBLE_LIST_PACKED.m74id() && fieldType <= FieldType.SINT64_LIST_PACKED.m74id()) {
                    presenceMaskAndOffset = this.buffer[i + 2] & 1048575;
                }
            } else {
                presenceMaskAndOffset = this.buffer[i + 2];
                int presenceFieldOffset = presenceMaskAndOffset & 1048575;
                presenceMask = 1 << (presenceMaskAndOffset >>> 20);
                if (presenceFieldOffset != currentPresenceFieldOffset2) {
                    currentPresenceFieldOffset2 = presenceFieldOffset;
                    currentPresenceField = unsafe.getInt(message, presenceFieldOffset);
                }
            }
            long offset = offset(typeAndOffset);
            switch (fieldType) {
                case 0:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset3 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset3 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeDoubleSize(number, 0.0d);
                        break;
                    }
                case 1:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset4 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset4 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeFloatSize(number, 0.0f);
                        break;
                    }
                case 2:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset5 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset5 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeInt64Size(number, unsafe.getLong(message, offset));
                        break;
                    }
                case 3:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset6 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset6 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeUInt64Size(number, unsafe.getLong(message, offset));
                        break;
                    }
                case 4:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset7 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset7 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeInt32Size(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 5:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset8 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset8 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeFixed64Size(number, 0L);
                        break;
                    }
                case 6:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset9 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset9 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeFixed32Size(number, 0);
                        break;
                    }
                case 7:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset10 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset10 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeBoolSize(number, true);
                        break;
                    }
                case 8:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset11 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset11 == 0) {
                        break;
                    } else {
                        Object value = unsafe.getObject(message, offset);
                        if (value instanceof ByteString) {
                            size += CodedOutputStream.computeBytesSize(number, (ByteString) value);
                            break;
                        } else {
                            size += CodedOutputStream.computeStringSize(number, (String) value);
                            break;
                        }
                    }
                case 9:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset12 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset12 == 0) {
                        break;
                    } else {
                        size += SchemaUtil.computeSizeMessage(number, unsafe.getObject(message, offset), getMessageFieldSchema(i));
                        break;
                    }
                case 10:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset13 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset13 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeBytesSize(number, (ByteString) unsafe.getObject(message, offset));
                        break;
                    }
                case 11:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset14 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset14 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeUInt32Size(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 12:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset15 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset15 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeEnumSize(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 13:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset16 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset16 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeSFixed32Size(number, 0);
                        break;
                    }
                case 14:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset17 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset17 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeSFixed64Size(number, 0L);
                        break;
                    }
                case 15:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset18 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset18 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeSInt32Size(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 16:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset19 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset19 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeSInt64Size(number, unsafe.getLong(message, offset));
                        break;
                    }
                case 17:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int currentPresenceFieldOffset20 = currentPresenceField & presenceMask;
                    if (currentPresenceFieldOffset20 == 0) {
                        break;
                    } else {
                        size += CodedOutputStream.computeGroupSize(number, (MessageLite) unsafe.getObject(message, offset), getMessageFieldSchema(i));
                        break;
                    }
                case 18:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeFixed64List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 19:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeFixed32List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 20:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeInt64List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 21:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeUInt64List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 22:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeInt32List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 23:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeFixed64List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 24:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeFixed32List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 25:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeBoolList(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 26:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeStringList(number, (List) unsafe.getObject(message, offset));
                    break;
                case 27:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeMessageList(number, (List) unsafe.getObject(message, offset), getMessageFieldSchema(i));
                    break;
                case 28:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeByteStringList(number, (List) unsafe.getObject(message, offset));
                    break;
                case 29:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeUInt32List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 30:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeEnumList(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 31:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeFixed32List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 32:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeFixed64List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 33:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeSInt32List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 34:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeSInt64List(number, (List) unsafe.getObject(message, offset), false);
                    break;
                case 35:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                        break;
                    } else {
                        break;
                    }
                case 36:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize2 = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize2 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize2);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize2) + fieldSize2;
                        break;
                    } else {
                        break;
                    }
                case 37:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize3 = SchemaUtil.computeSizeInt64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize3 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize3);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize3) + fieldSize3;
                        break;
                    } else {
                        break;
                    }
                case 38:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize4 = SchemaUtil.computeSizeUInt64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize4 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize4);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize4) + fieldSize4;
                        break;
                    } else {
                        break;
                    }
                case 39:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize5 = SchemaUtil.computeSizeInt32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize5 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize5);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize5) + fieldSize5;
                        break;
                    } else {
                        break;
                    }
                case 40:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize6 = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize6 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize6);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize6) + fieldSize6;
                        break;
                    } else {
                        break;
                    }
                case 41:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize7 = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize7 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize7);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize7) + fieldSize7;
                        break;
                    } else {
                        break;
                    }
                case 42:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize8 = SchemaUtil.computeSizeBoolListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize8 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize8);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize8) + fieldSize8;
                        break;
                    } else {
                        break;
                    }
                case 43:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize9 = SchemaUtil.computeSizeUInt32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize9 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize9);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize9) + fieldSize9;
                        break;
                    } else {
                        break;
                    }
                case 44:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize10 = SchemaUtil.computeSizeEnumListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize10 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize10);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize10) + fieldSize10;
                        break;
                    } else {
                        break;
                    }
                case 45:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize11 = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize11 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize11);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize11) + fieldSize11;
                        break;
                    } else {
                        break;
                    }
                case 46:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize12 = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize12 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize12);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize12) + fieldSize12;
                        break;
                    } else {
                        break;
                    }
                case 47:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize13 = SchemaUtil.computeSizeSInt32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize13 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize13);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize13) + fieldSize13;
                        break;
                    } else {
                        break;
                    }
                case 48:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    int fieldSize14 = SchemaUtil.computeSizeSInt64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize14 > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, presenceMaskAndOffset, fieldSize14);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize14) + fieldSize14;
                        break;
                    } else {
                        break;
                    }
                case 49:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += SchemaUtil.computeSizeGroupList(number, (List) unsafe.getObject(message, offset), getMessageFieldSchema(i));
                    break;
                case 50:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    size += this.mapFieldSchema.getSerializedSize(number, unsafe.getObject(message, offset), getMapFieldDefaultEntry(i));
                    break;
                case 51:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeDoubleSize(number, 0.0d);
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 52:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeFloatSize(number, 0.0f);
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 53:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeInt64Size(number, oneofLongAt(message, offset));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 54:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeUInt64Size(number, oneofLongAt(message, offset));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 55:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeInt32Size(number, oneofIntAt(message, offset));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 56:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeFixed64Size(number, 0L);
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 57:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeFixed32Size(number, 0);
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 58:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeBoolSize(number, true);
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 59:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        Object value2 = unsafe.getObject(message, offset);
                        if (value2 instanceof ByteString) {
                            size += CodedOutputStream.computeBytesSize(number, (ByteString) value2);
                        } else {
                            size += CodedOutputStream.computeStringSize(number, (String) value2);
                        }
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 60:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += SchemaUtil.computeSizeMessage(number, unsafe.getObject(message, offset), getMessageFieldSchema(i));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 61:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeBytesSize(number, (ByteString) unsafe.getObject(message, offset));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 62:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeUInt32Size(number, oneofIntAt(message, offset));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 63:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeEnumSize(number, oneofIntAt(message, offset));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 64:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeSFixed32Size(number, 0);
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 65:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeSFixed64Size(number, 0L);
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 66:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeSInt32Size(number, oneofIntAt(message, offset));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 67:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeSInt64Size(number, oneofLongAt(message, offset));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                case 68:
                    if (!isOneofPresent(message, number, i)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    } else {
                        size += CodedOutputStream.computeGroupSize(number, (MessageLite) unsafe.getObject(message, offset), getMessageFieldSchema(i));
                        currentPresenceFieldOffset = currentPresenceFieldOffset2;
                        break;
                    }
                default:
                    currentPresenceFieldOffset = currentPresenceFieldOffset2;
                    break;
            }
            i += 3;
            currentPresenceFieldOffset2 = currentPresenceFieldOffset;
        }
        int size2 = size + getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
        if (this.hasExtensions) {
            return size2 + this.extensionSchema.getExtensions(message).getSerializedSize();
        }
        return size2;
    }

    private int getSerializedSizeProto3(T message) {
        int cachedSizeOffset;
        Unsafe unsafe = UNSAFE;
        int size = 0;
        for (int i = 0; i < this.buffer.length; i += 3) {
            int typeAndOffset = typeAndOffsetAt(i);
            int fieldType = type(typeAndOffset);
            int number = numberAt(i);
            long offset = offset(typeAndOffset);
            if (fieldType >= FieldType.DOUBLE_LIST_PACKED.m74id() && fieldType <= FieldType.SINT64_LIST_PACKED.m74id()) {
                cachedSizeOffset = this.buffer[i + 2] & 1048575;
            } else {
                cachedSizeOffset = 0;
            }
            switch (fieldType) {
                case 0:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeDoubleSize(number, 0.0d);
                        break;
                    } else {
                        break;
                    }
                case 1:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeFloatSize(number, 0.0f);
                        break;
                    } else {
                        break;
                    }
                case 2:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeInt64Size(number, UnsafeUtil.getLong(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 3:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeUInt64Size(number, UnsafeUtil.getLong(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 4:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeInt32Size(number, UnsafeUtil.getInt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 5:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeFixed64Size(number, 0L);
                        break;
                    } else {
                        break;
                    }
                case 6:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeFixed32Size(number, 0);
                        break;
                    } else {
                        break;
                    }
                case 7:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeBoolSize(number, true);
                        break;
                    } else {
                        break;
                    }
                case 8:
                    if (isFieldPresent(message, i)) {
                        Object value = UnsafeUtil.getObject(message, offset);
                        if (value instanceof ByteString) {
                            size += CodedOutputStream.computeBytesSize(number, (ByteString) value);
                            break;
                        } else {
                            size += CodedOutputStream.computeStringSize(number, (String) value);
                            break;
                        }
                    } else {
                        break;
                    }
                case 9:
                    if (isFieldPresent(message, i)) {
                        size += SchemaUtil.computeSizeMessage(number, UnsafeUtil.getObject(message, offset), getMessageFieldSchema(i));
                        break;
                    } else {
                        break;
                    }
                case 10:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeBytesSize(number, (ByteString) UnsafeUtil.getObject(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 11:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeUInt32Size(number, UnsafeUtil.getInt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 12:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeEnumSize(number, UnsafeUtil.getInt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 13:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeSFixed32Size(number, 0);
                        break;
                    } else {
                        break;
                    }
                case 14:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeSFixed64Size(number, 0L);
                        break;
                    } else {
                        break;
                    }
                case 15:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeSInt32Size(number, UnsafeUtil.getInt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 16:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeSInt64Size(number, UnsafeUtil.getLong(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 17:
                    if (isFieldPresent(message, i)) {
                        size += CodedOutputStream.computeGroupSize(number, (MessageLite) UnsafeUtil.getObject(message, offset), getMessageFieldSchema(i));
                        break;
                    } else {
                        break;
                    }
                case 18:
                    size += SchemaUtil.computeSizeFixed64List(number, listAt(message, offset), false);
                    break;
                case 19:
                    size += SchemaUtil.computeSizeFixed32List(number, listAt(message, offset), false);
                    break;
                case 20:
                    size += SchemaUtil.computeSizeInt64List(number, listAt(message, offset), false);
                    break;
                case 21:
                    size += SchemaUtil.computeSizeUInt64List(number, listAt(message, offset), false);
                    break;
                case 22:
                    size += SchemaUtil.computeSizeInt32List(number, listAt(message, offset), false);
                    break;
                case 23:
                    size += SchemaUtil.computeSizeFixed64List(number, listAt(message, offset), false);
                    break;
                case 24:
                    size += SchemaUtil.computeSizeFixed32List(number, listAt(message, offset), false);
                    break;
                case 25:
                    size += SchemaUtil.computeSizeBoolList(number, listAt(message, offset), false);
                    break;
                case 26:
                    size += SchemaUtil.computeSizeStringList(number, listAt(message, offset));
                    break;
                case 27:
                    size += SchemaUtil.computeSizeMessageList(number, listAt(message, offset), getMessageFieldSchema(i));
                    break;
                case 28:
                    size += SchemaUtil.computeSizeByteStringList(number, listAt(message, offset));
                    break;
                case 29:
                    size += SchemaUtil.computeSizeUInt32List(number, listAt(message, offset), false);
                    break;
                case 30:
                    size += SchemaUtil.computeSizeEnumList(number, listAt(message, offset), false);
                    break;
                case 31:
                    size += SchemaUtil.computeSizeFixed32List(number, listAt(message, offset), false);
                    break;
                case 32:
                    size += SchemaUtil.computeSizeFixed64List(number, listAt(message, offset), false);
                    break;
                case 33:
                    size += SchemaUtil.computeSizeSInt32List(number, listAt(message, offset), false);
                    break;
                case 34:
                    size += SchemaUtil.computeSizeSInt64List(number, listAt(message, offset), false);
                    break;
                case 35:
                    int fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                        break;
                    }
                case 36:
                    int fieldSize2 = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize2 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize2);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize2) + fieldSize2;
                        break;
                    }
                case 37:
                    int fieldSize3 = SchemaUtil.computeSizeInt64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize3 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize3);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize3) + fieldSize3;
                        break;
                    }
                case 38:
                    int fieldSize4 = SchemaUtil.computeSizeUInt64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize4 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize4);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize4) + fieldSize4;
                        break;
                    }
                case 39:
                    int fieldSize5 = SchemaUtil.computeSizeInt32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize5 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize5);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize5) + fieldSize5;
                        break;
                    }
                case 40:
                    int fieldSize6 = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize6 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize6);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize6) + fieldSize6;
                        break;
                    }
                case 41:
                    int fieldSize7 = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize7 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize7);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize7) + fieldSize7;
                        break;
                    }
                case 42:
                    int fieldSize8 = SchemaUtil.computeSizeBoolListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize8 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize8);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize8) + fieldSize8;
                        break;
                    }
                case 43:
                    int fieldSize9 = SchemaUtil.computeSizeUInt32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize9 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize9);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize9) + fieldSize9;
                        break;
                    }
                case 44:
                    int fieldSize10 = SchemaUtil.computeSizeEnumListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize10 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize10);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize10) + fieldSize10;
                        break;
                    }
                case 45:
                    int fieldSize11 = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize11 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize11);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize11) + fieldSize11;
                        break;
                    }
                case 46:
                    int fieldSize12 = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize12 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize12);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize12) + fieldSize12;
                        break;
                    }
                case 47:
                    int fieldSize13 = SchemaUtil.computeSizeSInt32ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize13 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize13);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize13) + fieldSize13;
                        break;
                    }
                case 48:
                    int fieldSize14 = SchemaUtil.computeSizeSInt64ListNoTag((List) unsafe.getObject(message, offset));
                    if (fieldSize14 <= 0) {
                        break;
                    } else {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(message, cachedSizeOffset, fieldSize14);
                        }
                        size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize14) + fieldSize14;
                        break;
                    }
                case 49:
                    size += SchemaUtil.computeSizeGroupList(number, listAt(message, offset), getMessageFieldSchema(i));
                    break;
                case 50:
                    size += this.mapFieldSchema.getSerializedSize(number, UnsafeUtil.getObject(message, offset), getMapFieldDefaultEntry(i));
                    break;
                case 51:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeDoubleSize(number, 0.0d);
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeFloatSize(number, 0.0f);
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeInt64Size(number, oneofLongAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeUInt64Size(number, oneofLongAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeInt32Size(number, oneofIntAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 56:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeFixed64Size(number, 0L);
                        break;
                    } else {
                        break;
                    }
                case 57:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeFixed32Size(number, 0);
                        break;
                    } else {
                        break;
                    }
                case 58:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeBoolSize(number, true);
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (isOneofPresent(message, number, i)) {
                        Object value2 = UnsafeUtil.getObject(message, offset);
                        if (value2 instanceof ByteString) {
                            size += CodedOutputStream.computeBytesSize(number, (ByteString) value2);
                            break;
                        } else {
                            size += CodedOutputStream.computeStringSize(number, (String) value2);
                            break;
                        }
                    } else {
                        break;
                    }
                case 60:
                    if (isOneofPresent(message, number, i)) {
                        size += SchemaUtil.computeSizeMessage(number, UnsafeUtil.getObject(message, offset), getMessageFieldSchema(i));
                        break;
                    } else {
                        break;
                    }
                case 61:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeBytesSize(number, (ByteString) UnsafeUtil.getObject(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeUInt32Size(number, oneofIntAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 63:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeEnumSize(number, oneofIntAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeSFixed32Size(number, 0);
                        break;
                    } else {
                        break;
                    }
                case 65:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeSFixed64Size(number, 0L);
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeSInt32Size(number, oneofIntAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeSInt64Size(number, oneofLongAt(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (isOneofPresent(message, number, i)) {
                        size += CodedOutputStream.computeGroupSize(number, (MessageLite) UnsafeUtil.getObject(message, offset), getMessageFieldSchema(i));
                        break;
                    } else {
                        break;
                    }
            }
        }
        return size + getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
    }

    private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
        UT unknowns = schema.getFromMessage(message);
        return schema.getSerializedSize(unknowns);
    }

    private static List<?> listAt(Object message, long offset) {
        return (List) UnsafeUtil.getObject(message, offset);
    }

    @Override // com.android.framework.protobuf.Schema
    public void writeTo(T message, Writer writer) throws IOException {
        if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
            writeFieldsInDescendingOrder(message, writer);
        } else if (this.proto3) {
            writeFieldsInAscendingOrderProto3(message, writer);
        } else {
            writeFieldsInAscendingOrderProto2(message, writer);
        }
    }

    private void writeFieldsInAscendingOrderProto2(T message, Writer writer) throws IOException {
        Map.Entry nextExtension;
        int currentPresenceFieldOffset;
        Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
        Map.Entry nextExtension2 = null;
        if (this.hasExtensions) {
            FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
            if (!extensions.isEmpty()) {
                extensionIterator = extensions.iterator();
                nextExtension2 = extensionIterator.next();
            }
        }
        int currentPresenceFieldOffset2 = 1048575;
        int currentPresenceField = 0;
        int bufferLength = this.buffer.length;
        Unsafe unsafe = UNSAFE;
        int pos = 0;
        while (pos < bufferLength) {
            int typeAndOffset = typeAndOffsetAt(pos);
            int number = numberAt(pos);
            int fieldType = type(typeAndOffset);
            int presenceMask = 0;
            Map.Entry nextExtension3 = nextExtension2;
            if (fieldType > 17) {
                nextExtension = nextExtension3;
            } else {
                int presenceMaskAndOffset = this.buffer[pos + 2];
                int presenceFieldOffset = 1048575 & presenceMaskAndOffset;
                if (presenceFieldOffset != currentPresenceFieldOffset2) {
                    currentPresenceField = unsafe.getInt(message, presenceFieldOffset);
                    currentPresenceFieldOffset2 = presenceFieldOffset;
                }
                presenceMask = 1 << (presenceMaskAndOffset >>> 20);
                nextExtension = nextExtension3;
            }
            while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) <= number) {
                this.extensionSchema.serializeExtension(writer, nextExtension);
                nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
            }
            Map.Entry nextExtension4 = nextExtension;
            int currentPresenceFieldOffset3 = currentPresenceFieldOffset2;
            long offset = offset(typeAndOffset);
            int bufferLength2 = bufferLength;
            switch (fieldType) {
                case 0:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeDouble(number, doubleAt(message, offset));
                        break;
                    }
                case 1:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeFloat(number, floatAt(message, offset));
                        break;
                    }
                case 2:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeInt64(number, unsafe.getLong(message, offset));
                        break;
                    }
                case 3:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeUInt64(number, unsafe.getLong(message, offset));
                        break;
                    }
                case 4:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeInt32(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 5:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeFixed64(number, unsafe.getLong(message, offset));
                        break;
                    }
                case 6:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeFixed32(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 7:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeBool(number, booleanAt(message, offset));
                        break;
                    }
                case 8:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writeString(number, unsafe.getObject(message, offset), writer);
                        break;
                    }
                case 9:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        Object value = unsafe.getObject(message, offset);
                        writer.writeMessage(number, value, getMessageFieldSchema(pos));
                        break;
                    }
                case 10:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeBytes(number, (ByteString) unsafe.getObject(message, offset));
                        break;
                    }
                case 11:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeUInt32(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 12:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeEnum(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 13:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeSFixed32(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 14:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeSFixed64(number, unsafe.getLong(message, offset));
                        break;
                    }
                case 15:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeSInt32(number, unsafe.getInt(message, offset));
                        break;
                    }
                case 16:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeSInt64(number, unsafe.getLong(message, offset));
                        break;
                    }
                case 17:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if ((currentPresenceField & presenceMask) == 0) {
                        break;
                    } else {
                        writer.writeGroup(number, unsafe.getObject(message, offset), getMessageFieldSchema(pos));
                        break;
                    }
                case 18:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset4 = numberAt(pos);
                    SchemaUtil.writeDoubleList(currentPresenceFieldOffset4, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 19:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset5 = numberAt(pos);
                    SchemaUtil.writeFloatList(currentPresenceFieldOffset5, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 20:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset6 = numberAt(pos);
                    SchemaUtil.writeInt64List(currentPresenceFieldOffset6, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 21:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset7 = numberAt(pos);
                    SchemaUtil.writeUInt64List(currentPresenceFieldOffset7, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 22:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset8 = numberAt(pos);
                    SchemaUtil.writeInt32List(currentPresenceFieldOffset8, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 23:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset9 = numberAt(pos);
                    SchemaUtil.writeFixed64List(currentPresenceFieldOffset9, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 24:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset10 = numberAt(pos);
                    SchemaUtil.writeFixed32List(currentPresenceFieldOffset10, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 25:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset11 = numberAt(pos);
                    SchemaUtil.writeBoolList(currentPresenceFieldOffset11, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 26:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeStringList(numberAt(pos), (List) unsafe.getObject(message, offset), writer);
                    break;
                case 27:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeMessageList(numberAt(pos), (List) unsafe.getObject(message, offset), writer, getMessageFieldSchema(pos));
                    break;
                case 28:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeBytesList(numberAt(pos), (List) unsafe.getObject(message, offset), writer);
                    break;
                case 29:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset12 = numberAt(pos);
                    SchemaUtil.writeUInt32List(currentPresenceFieldOffset12, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 30:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset13 = numberAt(pos);
                    SchemaUtil.writeEnumList(currentPresenceFieldOffset13, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 31:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset14 = numberAt(pos);
                    SchemaUtil.writeSFixed32List(currentPresenceFieldOffset14, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 32:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset15 = numberAt(pos);
                    SchemaUtil.writeSFixed64List(currentPresenceFieldOffset15, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 33:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset16 = numberAt(pos);
                    SchemaUtil.writeSInt32List(currentPresenceFieldOffset16, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 34:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    int currentPresenceFieldOffset17 = numberAt(pos);
                    SchemaUtil.writeSInt64List(currentPresenceFieldOffset17, (List) unsafe.getObject(message, offset), writer, false);
                    break;
                case 35:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeDoubleList(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 36:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeFloatList(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 37:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeInt64List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 38:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeUInt64List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 39:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeInt32List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 40:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeFixed64List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 41:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeFixed32List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 42:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeBoolList(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 43:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeUInt32List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 44:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeEnumList(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 45:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeSFixed32List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 46:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeSFixed64List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 47:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeSInt32List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 48:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeSInt64List(numberAt(pos), (List) unsafe.getObject(message, offset), writer, true);
                    break;
                case 49:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    SchemaUtil.writeGroupList(numberAt(pos), (List) unsafe.getObject(message, offset), writer, getMessageFieldSchema(pos));
                    break;
                case 50:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    writeMapHelper(writer, number, unsafe.getObject(message, offset), pos);
                    break;
                case 51:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeDouble(number, oneofDoubleAt(message, offset));
                        break;
                    }
                case 52:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeFloat(number, oneofFloatAt(message, offset));
                        break;
                    }
                case 53:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeInt64(number, oneofLongAt(message, offset));
                        break;
                    }
                case 54:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeUInt64(number, oneofLongAt(message, offset));
                        break;
                    }
                case 55:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeInt32(number, oneofIntAt(message, offset));
                        break;
                    }
                case 56:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeFixed64(number, oneofLongAt(message, offset));
                        break;
                    }
                case 57:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeFixed32(number, oneofIntAt(message, offset));
                        break;
                    }
                case 58:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeBool(number, oneofBooleanAt(message, offset));
                        break;
                    }
                case 59:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writeString(number, unsafe.getObject(message, offset), writer);
                        break;
                    }
                case 60:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        Object value2 = unsafe.getObject(message, offset);
                        writer.writeMessage(number, value2, getMessageFieldSchema(pos));
                        break;
                    }
                case 61:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeBytes(number, (ByteString) unsafe.getObject(message, offset));
                        break;
                    }
                case 62:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeUInt32(number, oneofIntAt(message, offset));
                        break;
                    }
                case 63:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeEnum(number, oneofIntAt(message, offset));
                        break;
                    }
                case 64:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeSFixed32(number, oneofIntAt(message, offset));
                        break;
                    }
                case 65:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeSFixed64(number, oneofLongAt(message, offset));
                        break;
                    }
                case 66:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeSInt32(number, oneofIntAt(message, offset));
                        break;
                    }
                case 67:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    if (!isOneofPresent(message, number, pos)) {
                        break;
                    } else {
                        writer.writeSInt64(number, oneofLongAt(message, offset));
                        break;
                    }
                case 68:
                    if (!isOneofPresent(message, number, pos)) {
                        currentPresenceFieldOffset = currentPresenceFieldOffset3;
                        break;
                    } else {
                        currentPresenceFieldOffset = currentPresenceFieldOffset3;
                        writer.writeGroup(number, unsafe.getObject(message, offset), getMessageFieldSchema(pos));
                        break;
                    }
                default:
                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                    break;
            }
            pos += 3;
            currentPresenceFieldOffset2 = currentPresenceFieldOffset;
            nextExtension2 = nextExtension4;
            bufferLength = bufferLength2;
        }
        while (nextExtension2 != null) {
            this.extensionSchema.serializeExtension(writer, nextExtension2);
            nextExtension2 = extensionIterator.hasNext() ? extensionIterator.next() : null;
        }
        writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
    }

    private void writeFieldsInAscendingOrderProto3(T message, Writer writer) throws IOException {
        Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
        Map.Entry nextExtension = null;
        if (this.hasExtensions) {
            FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
            if (!extensions.isEmpty()) {
                extensionIterator = extensions.iterator();
                nextExtension = extensionIterator.next();
            }
        }
        int bufferLength = this.buffer.length;
        for (int pos = 0; pos < bufferLength; pos += 3) {
            int typeAndOffset = typeAndOffsetAt(pos);
            int number = numberAt(pos);
            while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) <= number) {
                this.extensionSchema.serializeExtension(writer, nextExtension);
                nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
            }
            switch (type(typeAndOffset)) {
                case 0:
                    if (isFieldPresent(message, pos)) {
                        writer.writeDouble(number, doubleAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 1:
                    if (isFieldPresent(message, pos)) {
                        writer.writeFloat(number, floatAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 2:
                    if (isFieldPresent(message, pos)) {
                        writer.writeInt64(number, longAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 3:
                    if (isFieldPresent(message, pos)) {
                        writer.writeUInt64(number, longAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 4:
                    if (isFieldPresent(message, pos)) {
                        writer.writeInt32(number, intAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 5:
                    if (isFieldPresent(message, pos)) {
                        writer.writeFixed64(number, longAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 6:
                    if (isFieldPresent(message, pos)) {
                        writer.writeFixed32(number, intAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 7:
                    if (isFieldPresent(message, pos)) {
                        writer.writeBool(number, booleanAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 8:
                    if (isFieldPresent(message, pos)) {
                        writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                        break;
                    } else {
                        break;
                    }
                case 9:
                    if (isFieldPresent(message, pos)) {
                        Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
                        writer.writeMessage(number, value, getMessageFieldSchema(pos));
                        break;
                    } else {
                        break;
                    }
                case 10:
                    if (isFieldPresent(message, pos)) {
                        writer.writeBytes(number, (ByteString) UnsafeUtil.getObject(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 11:
                    if (isFieldPresent(message, pos)) {
                        writer.writeUInt32(number, intAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 12:
                    if (isFieldPresent(message, pos)) {
                        writer.writeEnum(number, intAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 13:
                    if (isFieldPresent(message, pos)) {
                        writer.writeSFixed32(number, intAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 14:
                    if (isFieldPresent(message, pos)) {
                        writer.writeSFixed64(number, longAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 15:
                    if (isFieldPresent(message, pos)) {
                        writer.writeSInt32(number, intAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 16:
                    if (isFieldPresent(message, pos)) {
                        writer.writeSInt64(number, longAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 17:
                    if (isFieldPresent(message, pos)) {
                        writer.writeGroup(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), getMessageFieldSchema(pos));
                        break;
                    } else {
                        break;
                    }
                case 18:
                    SchemaUtil.writeDoubleList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 19:
                    SchemaUtil.writeFloatList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 20:
                    SchemaUtil.writeInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 21:
                    SchemaUtil.writeUInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 22:
                    SchemaUtil.writeInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 23:
                    SchemaUtil.writeFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 24:
                    SchemaUtil.writeFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 25:
                    SchemaUtil.writeBoolList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 26:
                    SchemaUtil.writeStringList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                    break;
                case 27:
                    SchemaUtil.writeMessageList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, getMessageFieldSchema(pos));
                    break;
                case 28:
                    SchemaUtil.writeBytesList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                    break;
                case 29:
                    SchemaUtil.writeUInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 30:
                    SchemaUtil.writeEnumList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 31:
                    SchemaUtil.writeSFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 32:
                    SchemaUtil.writeSFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 33:
                    SchemaUtil.writeSInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 34:
                    SchemaUtil.writeSInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                    break;
                case 35:
                    SchemaUtil.writeDoubleList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 36:
                    SchemaUtil.writeFloatList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 37:
                    SchemaUtil.writeInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 38:
                    SchemaUtil.writeUInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 39:
                    SchemaUtil.writeInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 40:
                    SchemaUtil.writeFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 41:
                    SchemaUtil.writeFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 42:
                    SchemaUtil.writeBoolList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 43:
                    SchemaUtil.writeUInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 44:
                    SchemaUtil.writeEnumList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 45:
                    SchemaUtil.writeSFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 46:
                    SchemaUtil.writeSFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 47:
                    SchemaUtil.writeSInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 48:
                    SchemaUtil.writeSInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                    break;
                case 49:
                    SchemaUtil.writeGroupList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, getMessageFieldSchema(pos));
                    break;
                case 50:
                    writeMapHelper(writer, number, UnsafeUtil.getObject(message, offset(typeAndOffset)), pos);
                    break;
                case 51:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeDouble(number, oneofDoubleAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeFloat(number, oneofFloatAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeInt64(number, oneofLongAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeUInt64(number, oneofLongAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeInt32(number, oneofIntAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 56:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 57:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 58:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeBool(number, oneofBooleanAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (isOneofPresent(message, number, pos)) {
                        writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                        break;
                    } else {
                        break;
                    }
                case 60:
                    if (isOneofPresent(message, number, pos)) {
                        Object value2 = UnsafeUtil.getObject(message, offset(typeAndOffset));
                        writer.writeMessage(number, value2, getMessageFieldSchema(pos));
                        break;
                    } else {
                        break;
                    }
                case 61:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeBytes(number, (ByteString) UnsafeUtil.getObject(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeUInt32(number, oneofIntAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 63:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeEnum(number, oneofIntAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeSFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 65:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeSFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeSInt32(number, oneofIntAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeSInt64(number, oneofLongAt(message, offset(typeAndOffset)));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (isOneofPresent(message, number, pos)) {
                        writer.writeGroup(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), getMessageFieldSchema(pos));
                        break;
                    } else {
                        break;
                    }
            }
        }
        while (nextExtension != null) {
            this.extensionSchema.serializeExtension(writer, nextExtension);
            nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
        }
        writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
    }

    private void writeFieldsInDescendingOrder(T message, Writer writer) throws IOException {
        writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
        Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
        Map.Entry nextExtension = null;
        if (this.hasExtensions) {
            FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
            if (!extensions.isEmpty()) {
                extensionIterator = extensions.descendingIterator();
                nextExtension = extensionIterator.next();
            }
        }
        int pos = this.buffer.length;
        while (true) {
            pos -= 3;
            if (pos >= 0) {
                int typeAndOffset = typeAndOffsetAt(pos);
                int number = numberAt(pos);
                while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) > number) {
                    this.extensionSchema.serializeExtension(writer, nextExtension);
                    nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
                }
                switch (type(typeAndOffset)) {
                    case 0:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeDouble(number, doubleAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 1:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeFloat(number, floatAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 2:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeInt64(number, longAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 3:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeUInt64(number, longAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 4:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeInt32(number, intAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 5:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeFixed64(number, longAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 6:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeFixed32(number, intAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 7:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeBool(number, booleanAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 8:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                            break;
                        }
                    case 9:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
                            writer.writeMessage(number, value, getMessageFieldSchema(pos));
                            break;
                        }
                    case 10:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeBytes(number, (ByteString) UnsafeUtil.getObject(message, offset(typeAndOffset)));
                            break;
                        }
                    case 11:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeUInt32(number, intAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 12:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeEnum(number, intAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 13:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeSFixed32(number, intAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 14:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeSFixed64(number, longAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 15:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeSInt32(number, intAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 16:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeSInt64(number, longAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 17:
                        if (!isFieldPresent(message, pos)) {
                            break;
                        } else {
                            writer.writeGroup(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), getMessageFieldSchema(pos));
                            break;
                        }
                    case 18:
                        SchemaUtil.writeDoubleList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 19:
                        SchemaUtil.writeFloatList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 20:
                        SchemaUtil.writeInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 21:
                        SchemaUtil.writeUInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 22:
                        SchemaUtil.writeInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 23:
                        SchemaUtil.writeFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 24:
                        SchemaUtil.writeFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 25:
                        SchemaUtil.writeBoolList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 26:
                        SchemaUtil.writeStringList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                        break;
                    case 27:
                        SchemaUtil.writeMessageList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, getMessageFieldSchema(pos));
                        break;
                    case 28:
                        SchemaUtil.writeBytesList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                        break;
                    case 29:
                        SchemaUtil.writeUInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 30:
                        SchemaUtil.writeEnumList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 31:
                        SchemaUtil.writeSFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 32:
                        SchemaUtil.writeSFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 33:
                        SchemaUtil.writeSInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 34:
                        SchemaUtil.writeSInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 35:
                        SchemaUtil.writeDoubleList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 36:
                        SchemaUtil.writeFloatList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 37:
                        SchemaUtil.writeInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 38:
                        SchemaUtil.writeUInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 39:
                        SchemaUtil.writeInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 40:
                        SchemaUtil.writeFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 41:
                        SchemaUtil.writeFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 42:
                        SchemaUtil.writeBoolList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 43:
                        SchemaUtil.writeUInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 44:
                        SchemaUtil.writeEnumList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 45:
                        SchemaUtil.writeSFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 46:
                        SchemaUtil.writeSFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 47:
                        SchemaUtil.writeSInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 48:
                        SchemaUtil.writeSInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 49:
                        SchemaUtil.writeGroupList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, getMessageFieldSchema(pos));
                        break;
                    case 50:
                        writeMapHelper(writer, number, UnsafeUtil.getObject(message, offset(typeAndOffset)), pos);
                        break;
                    case 51:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeDouble(number, oneofDoubleAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 52:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeFloat(number, oneofFloatAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 53:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeInt64(number, oneofLongAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 54:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeUInt64(number, oneofLongAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 55:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeInt32(number, oneofIntAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 56:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 57:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 58:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeBool(number, oneofBooleanAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 59:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                            break;
                        }
                    case 60:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            Object value2 = UnsafeUtil.getObject(message, offset(typeAndOffset));
                            writer.writeMessage(number, value2, getMessageFieldSchema(pos));
                            break;
                        }
                    case 61:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeBytes(number, (ByteString) UnsafeUtil.getObject(message, offset(typeAndOffset)));
                            break;
                        }
                    case 62:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeUInt32(number, oneofIntAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 63:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeEnum(number, oneofIntAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 64:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeSFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 65:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeSFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 66:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeSInt32(number, oneofIntAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 67:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeSInt64(number, oneofLongAt(message, offset(typeAndOffset)));
                            break;
                        }
                    case 68:
                        if (!isOneofPresent(message, number, pos)) {
                            break;
                        } else {
                            writer.writeGroup(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), getMessageFieldSchema(pos));
                            break;
                        }
                }
            } else {
                while (nextExtension != null) {
                    this.extensionSchema.serializeExtension(writer, nextExtension);
                    nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
                }
                return;
            }
        }
    }

    private <K, V> void writeMapHelper(Writer writer, int number, Object mapField, int pos) throws IOException {
        if (mapField != null) {
            writer.writeMap(number, this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(pos)), this.mapFieldSchema.forMapData(mapField));
        }
    }

    private <UT, UB> void writeUnknownInMessageTo(UnknownFieldSchema<UT, UB> schema, T message, Writer writer) throws IOException {
        schema.writeTo(schema.getFromMessage(message), writer);
    }

    @Override // com.android.framework.protobuf.Schema
    public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        if (extensionRegistry == null) {
            throw new NullPointerException();
        }
        checkMutable(message);
        mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
    }

    /* JADX WARN: Code restructure failed: missing block: B:279:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00cc, code lost:
        r10 = r20.checkInitializedCount;
        r11 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00d2, code lost:
        if (r10 >= r20.repeatedFieldOffsetStart) goto L222;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00d4, code lost:
        r11 = (UB) filterMapUnknownEnumValues(r23, r20.intArray[r10], r11, r21, r23);
        r10 = r10 + 1;
        r5 = r5;
        r2 = r2;
        r3 = r3;
        r4 = r4;
        r9 = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00f8, code lost:
        r14 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0100, code lost:
        if (r11 == null) goto L227;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0102, code lost:
        r7.setBuilderToMessage(r14, r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0105, code lost:
        return;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:193:0x06fa A[Catch: all -> 0x0764, TRY_LEAVE, TryCatch #2 {all -> 0x0764, blocks: (B:174:0x06b6, B:191:0x06f4, B:193:0x06fa, B:206:0x0730, B:207:0x0735, B:71:0x0136, B:72:0x014a, B:73:0x0160, B:74:0x0176, B:75:0x018c, B:76:0x01a2, B:78:0x01ac, B:81:0x01b3, B:82:0x01bd, B:83:0x01ce, B:84:0x01e4, B:85:0x01f5, B:86:0x020a, B:87:0x0213, B:88:0x0229, B:89:0x023f, B:90:0x0255, B:91:0x026b, B:92:0x0281, B:93:0x0297, B:94:0x02ad), top: B:232:0x06b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:204:0x072c  */
    /* JADX WARN: Removed duplicated region for block: B:226:0x077b A[LOOP:2: B:224:0x0777->B:226:0x077b, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:228:0x0791  */
    /* JADX WARN: Type inference failed for: r11v15 */
    /* JADX WARN: Type inference failed for: r11v16, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r11v17, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v122, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v92, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r3v22, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r3v52, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r7v3 */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v53 */
    /* JADX WARN: Type inference failed for: r7v54 */
    /* JADX WARN: Type inference failed for: r7v56 */
    /* JADX WARN: Type inference failed for: r7v58 */
    /* JADX WARN: Type inference failed for: r7v60 */
    /* JADX WARN: Type inference failed for: r7v70 */
    /* JADX WARN: Type inference failed for: r7v71 */
    /* JADX WARN: Type inference failed for: r7v72 */
    /* JADX WARN: Type inference failed for: r7v73 */
    /* JADX WARN: Type inference failed for: r9v7, types: [java.lang.Object] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, ExtensionSchema<ET> extensionSchema, T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        T t;
        Throwable th;
        UnknownFieldSchema unknownFieldSchema2;
        int i;
        Object obj;
        FieldSet<ET> extensions;
        InvalidProtocolBufferException.InvalidWireTypeException invalidWireTypeException;
        UnknownFieldSchema<UT, UB> unknownFieldSchema3;
        UB unknownFields;
        UnknownFieldSchema<UT, UB> unknownFieldSchema4;
        UnknownFieldSchema<UT, UB> unknownFieldSchema5;
        UnknownFieldSchema<UT, UB> unknownFieldSchema6 = unknownFieldSchema;
        T t2 = message;
        Reader reader2 = reader;
        ExtensionRegistryLite extensionRegistryLite = extensionRegistry;
        UB unknownFields2 = null;
        FieldSet<ET> extensions2 = null;
        while (true) {
            try {
                int number = reader.getFieldNumber();
                int pos = positionForFieldNumber(number);
                if (pos >= 0) {
                    FieldSet<ET> extensions3 = extensions2;
                    Reader reader3 = reader2;
                    t = t2;
                    ExtensionRegistryLite extensionRegistryLite2 = extensionRegistryLite;
                    try {
                        int typeAndOffset = typeAndOffsetAt(pos);
                        try {
                            switch (type(typeAndOffset)) {
                                case 0:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putDouble(t, offset(typeAndOffset), reader.readDouble());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 1:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putFloat(t, offset(typeAndOffset), reader.readFloat());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 2:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putLong(t, offset(typeAndOffset), reader.readInt64());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 3:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putLong(t, offset(typeAndOffset), reader.readUInt64());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 4:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putInt(t, offset(typeAndOffset), reader.readInt32());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 5:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putLong(t, offset(typeAndOffset), reader.readFixed64());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 6:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putInt(t, offset(typeAndOffset), reader.readFixed32());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 7:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putBoolean(t, offset(typeAndOffset), reader.readBool());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 8:
                                    unknownFields = unknownFields2;
                                    readString(t, typeAndOffset, reader3);
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 9:
                                    unknownFields = unknownFields2;
                                    MessageLite current = (MessageLite) mutableMessageFieldForMerge(t, pos);
                                    reader3.mergeMessageField(current, getMessageFieldSchema(pos), extensionRegistryLite2);
                                    storeMessageField(t, pos, current);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 10:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), reader.readBytes());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 11:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putInt(t, offset(typeAndOffset), reader.readUInt32());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 12:
                                    unknownFields = unknownFields2;
                                    int enumValue = reader.readEnum();
                                    Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(pos);
                                    if (enumVerifier == null) {
                                        unknownFieldSchema4 = unknownFieldSchema;
                                    } else if (!enumVerifier.isInRange(enumValue)) {
                                        UnknownFieldSchema<UT, UB> unknownFieldSchema7 = unknownFieldSchema;
                                        unknownFields2 = SchemaUtil.storeUnknownEnum(t, number, enumValue, unknownFields, unknownFieldSchema7);
                                        unknownFieldSchema6 = unknownFieldSchema7;
                                        break;
                                    } else {
                                        unknownFieldSchema4 = unknownFieldSchema;
                                    }
                                    UnsafeUtil.putInt(t, offset(typeAndOffset), enumValue);
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema4;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 13:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putInt(t, offset(typeAndOffset), reader.readSFixed32());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 14:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putLong(t, offset(typeAndOffset), reader.readSFixed64());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 15:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putInt(t, offset(typeAndOffset), reader.readSInt32());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 16:
                                    unknownFields = unknownFields2;
                                    UnsafeUtil.putLong(t, offset(typeAndOffset), reader.readSInt64());
                                    setFieldPresent(t, pos);
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 17:
                                    unknownFields = unknownFields2;
                                    MessageLite current2 = (MessageLite) mutableMessageFieldForMerge(t, pos);
                                    reader3.mergeGroupField(current2, getMessageFieldSchema(pos), extensionRegistryLite2);
                                    storeMessageField(t, pos, current2);
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 18:
                                    unknownFields = unknownFields2;
                                    reader3.readDoubleList(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 19:
                                    unknownFields = unknownFields2;
                                    reader3.readFloatList(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 20:
                                    unknownFields = unknownFields2;
                                    reader3.readInt64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 21:
                                    unknownFields = unknownFields2;
                                    reader3.readUInt64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 22:
                                    unknownFields = unknownFields2;
                                    reader3.readInt32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 23:
                                    unknownFields = unknownFields2;
                                    reader3.readFixed64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 24:
                                    unknownFields = unknownFields2;
                                    reader3.readFixed32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 25:
                                    unknownFields = unknownFields2;
                                    reader3.readBoolList(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 26:
                                    unknownFields = unknownFields2;
                                    readStringList(t, typeAndOffset, reader3);
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 27:
                                    unknownFields = unknownFields2;
                                    readMessageList(message, typeAndOffset, reader, getMessageFieldSchema(pos), extensionRegistry);
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 28:
                                    unknownFields = unknownFields2;
                                    reader3.readBytesList(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 29:
                                    unknownFields = unknownFields2;
                                    reader3.readUInt32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 30:
                                    List<Integer> enumList = this.listFieldSchema.mutableListAt(t, offset(typeAndOffset));
                                    reader3.readEnumList(enumList);
                                    unknownFields2 = SchemaUtil.filterUnknownEnumList(message, number, enumList, getEnumFieldVerifier(pos), unknownFields2, unknownFieldSchema);
                                    unknownFieldSchema6 = unknownFieldSchema;
                                    break;
                                case 31:
                                    unknownFields = unknownFields2;
                                    reader3.readSFixed32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 32:
                                    unknownFields = unknownFields2;
                                    reader3.readSFixed64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 33:
                                    unknownFields = unknownFields2;
                                    reader3.readSInt32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 34:
                                    unknownFields = unknownFields2;
                                    reader3.readSInt64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 35:
                                    unknownFields = unknownFields2;
                                    reader3.readDoubleList(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 36:
                                    unknownFields = unknownFields2;
                                    reader3.readFloatList(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 37:
                                    unknownFields = unknownFields2;
                                    reader3.readInt64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 38:
                                    unknownFields = unknownFields2;
                                    reader3.readUInt64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 39:
                                    unknownFields = unknownFields2;
                                    reader3.readInt32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 40:
                                    unknownFields = unknownFields2;
                                    reader3.readFixed64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 41:
                                    unknownFields = unknownFields2;
                                    reader3.readFixed32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 42:
                                    unknownFields = unknownFields2;
                                    reader3.readBoolList(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 43:
                                    unknownFields = unknownFields2;
                                    reader3.readUInt32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 44:
                                    List<Integer> enumList2 = this.listFieldSchema.mutableListAt(t, offset(typeAndOffset));
                                    reader3.readEnumList(enumList2);
                                    unknownFields2 = SchemaUtil.filterUnknownEnumList(message, number, enumList2, getEnumFieldVerifier(pos), unknownFields2, unknownFieldSchema);
                                    unknownFieldSchema6 = unknownFieldSchema;
                                    break;
                                case 45:
                                    unknownFields = unknownFields2;
                                    reader3.readSFixed32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 46:
                                    unknownFields = unknownFields2;
                                    reader3.readSFixed64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 47:
                                    unknownFields = unknownFields2;
                                    reader3.readSInt32List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 48:
                                    unknownFields = unknownFields2;
                                    reader3.readSInt64List(this.listFieldSchema.mutableListAt(t, offset(typeAndOffset)));
                                    unknownFieldSchema5 = unknownFieldSchema;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 49:
                                    unknownFields = unknownFields2;
                                    try {
                                        readGroupList(message, offset(typeAndOffset), reader, getMessageFieldSchema(pos), extensionRegistry);
                                        unknownFieldSchema5 = unknownFieldSchema;
                                        unknownFields2 = unknownFields;
                                        unknownFieldSchema6 = unknownFieldSchema5;
                                    } catch (InvalidProtocolBufferException.InvalidWireTypeException e) {
                                        e = e;
                                        unknownFieldSchema6 = unknownFieldSchema;
                                        invalidWireTypeException = e;
                                        unknownFields2 = unknownFields;
                                        unknownFieldSchema3 = unknownFieldSchema6;
                                        InvalidProtocolBufferException.InvalidWireTypeException e2 = invalidWireTypeException;
                                        if (!unknownFieldSchema3.shouldDiscardUnknownFields(reader3)) {
                                        }
                                        extensionRegistryLite = extensionRegistryLite2;
                                        t2 = t;
                                        reader2 = reader3;
                                        extensions2 = extensions3;
                                    } catch (Throwable th2) {
                                        th = th2;
                                        unknownFieldSchema6 = unknownFieldSchema;
                                        th = th;
                                        unknownFields2 = unknownFields;
                                        unknownFieldSchema2 = unknownFieldSchema6;
                                        obj = unknownFields2;
                                        while (i < this.repeatedFieldOffsetStart) {
                                        }
                                        if (obj != null) {
                                        }
                                        throw th;
                                    }
                                    break;
                                case 50:
                                    try {
                                        Object mapFieldDefaultEntry = getMapFieldDefaultEntry(pos);
                                        unknownFields = unknownFields2;
                                        mergeMap(message, pos, mapFieldDefaultEntry, extensionRegistry, reader);
                                        unknownFieldSchema5 = unknownFieldSchema6;
                                        unknownFields2 = unknownFields;
                                        unknownFieldSchema6 = unknownFieldSchema5;
                                    } catch (InvalidProtocolBufferException.InvalidWireTypeException e3) {
                                        invalidWireTypeException = e3;
                                        unknownFieldSchema3 = unknownFieldSchema6;
                                        InvalidProtocolBufferException.InvalidWireTypeException e22 = invalidWireTypeException;
                                        if (!unknownFieldSchema3.shouldDiscardUnknownFields(reader3)) {
                                        }
                                        extensionRegistryLite = extensionRegistryLite2;
                                        t2 = t;
                                        reader2 = reader3;
                                        extensions2 = extensions3;
                                    }
                                    break;
                                case 51:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Double.valueOf(reader.readDouble()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 52:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Float.valueOf(reader.readFloat()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 53:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Long.valueOf(reader.readInt64()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 54:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Long.valueOf(reader.readUInt64()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 55:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Integer.valueOf(reader.readInt32()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 56:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Long.valueOf(reader.readFixed64()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 57:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Integer.valueOf(reader.readFixed32()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 58:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Boolean.valueOf(reader.readBool()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 59:
                                    readString(t, typeAndOffset, reader3);
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 60:
                                    MessageLite current3 = (MessageLite) mutableOneofMessageFieldForMerge(t, number, pos);
                                    reader3.mergeMessageField(current3, getMessageFieldSchema(pos), extensionRegistryLite2);
                                    storeOneofMessageField(t, number, pos, current3);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 61:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), reader.readBytes());
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 62:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Integer.valueOf(reader.readUInt32()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 63:
                                    int enumValue2 = reader.readEnum();
                                    Internal.EnumVerifier enumVerifier2 = getEnumFieldVerifier(pos);
                                    if (enumVerifier2 != null && !enumVerifier2.isInRange(enumValue2)) {
                                        unknownFields2 = SchemaUtil.storeUnknownEnum(t, number, enumValue2, unknownFields2, unknownFieldSchema6);
                                        unknownFieldSchema6 = unknownFieldSchema6;
                                        break;
                                    }
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Integer.valueOf(enumValue2));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 64:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Integer.valueOf(reader.readSFixed32()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 65:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Long.valueOf(reader.readSFixed64()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 66:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Integer.valueOf(reader.readSInt32()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 67:
                                    UnsafeUtil.putObject(t, offset(typeAndOffset), Long.valueOf(reader.readSInt64()));
                                    setOneofPresent(t, number, pos);
                                    unknownFields = unknownFields2;
                                    unknownFieldSchema5 = unknownFieldSchema6;
                                    unknownFields2 = unknownFields;
                                    unknownFieldSchema6 = unknownFieldSchema5;
                                    break;
                                case 68:
                                    try {
                                        MessageLite current4 = (MessageLite) mutableOneofMessageFieldForMerge(t, number, pos);
                                        reader3.mergeGroupField(current4, getMessageFieldSchema(pos), extensionRegistryLite2);
                                        storeOneofMessageField(t, number, pos, current4);
                                        unknownFields = unknownFields2;
                                        unknownFieldSchema5 = unknownFieldSchema6;
                                        unknownFields2 = unknownFields;
                                        unknownFieldSchema6 = unknownFieldSchema5;
                                    } catch (InvalidProtocolBufferException.InvalidWireTypeException e4) {
                                        invalidWireTypeException = e4;
                                        unknownFieldSchema3 = unknownFieldSchema6;
                                        InvalidProtocolBufferException.InvalidWireTypeException e222 = invalidWireTypeException;
                                        if (!unknownFieldSchema3.shouldDiscardUnknownFields(reader3)) {
                                        }
                                        extensionRegistryLite = extensionRegistryLite2;
                                        t2 = t;
                                        reader2 = reader3;
                                        extensions2 = extensions3;
                                    }
                                    break;
                                default:
                                    unknownFields = unknownFields2;
                                    if (unknownFields == null) {
                                        try {
                                            unknownFields2 = unknownFieldSchema6.getBuilderFromMessage(t);
                                        } catch (InvalidProtocolBufferException.InvalidWireTypeException e5) {
                                            e = e5;
                                            invalidWireTypeException = e;
                                            unknownFields2 = unknownFields;
                                            unknownFieldSchema3 = unknownFieldSchema6;
                                            InvalidProtocolBufferException.InvalidWireTypeException e2222 = invalidWireTypeException;
                                            if (!unknownFieldSchema3.shouldDiscardUnknownFields(reader3)) {
                                            }
                                            extensionRegistryLite = extensionRegistryLite2;
                                            t2 = t;
                                            reader2 = reader3;
                                            extensions2 = extensions3;
                                        } catch (Throwable th3) {
                                            th = th3;
                                            th = th;
                                            unknownFields2 = unknownFields;
                                            unknownFieldSchema2 = unknownFieldSchema6;
                                            obj = unknownFields2;
                                            while (i < this.repeatedFieldOffsetStart) {
                                            }
                                            if (obj != null) {
                                            }
                                            throw th;
                                        }
                                    } else {
                                        unknownFields2 = unknownFields;
                                    }
                                    try {
                                        try {
                                            if (!unknownFieldSchema6.mergeOneFieldFrom(unknownFields2, reader3)) {
                                                int i2 = this.checkInitializedCount;
                                                while (i2 < this.repeatedFieldOffsetStart) {
                                                    unknownFields2 = (UB) filterMapUnknownEnumValues(message, this.intArray[i2], unknownFields2, unknownFieldSchema, message);
                                                    i2++;
                                                    typeAndOffset = typeAndOffset;
                                                }
                                                UB unknownFields3 = unknownFields2;
                                                if (unknownFields3 != null) {
                                                    unknownFieldSchema6.setBuilderToMessage(t, unknownFields3);
                                                    return;
                                                }
                                                return;
                                            }
                                            unknownFieldSchema6 = unknownFieldSchema6;
                                        } catch (Throwable th4) {
                                            th = th4;
                                            unknownFieldSchema2 = unknownFieldSchema6;
                                            obj = unknownFields2;
                                            for (i = this.checkInitializedCount; i < this.repeatedFieldOffsetStart; i++) {
                                                obj = filterMapUnknownEnumValues(message, this.intArray[i], obj, unknownFieldSchema, message);
                                            }
                                            if (obj != null) {
                                                unknownFieldSchema2.setBuilderToMessage(t, obj);
                                            }
                                            throw th;
                                        }
                                    } catch (InvalidProtocolBufferException.InvalidWireTypeException e6) {
                                        invalidWireTypeException = e6;
                                        unknownFieldSchema3 = unknownFieldSchema6;
                                        InvalidProtocolBufferException.InvalidWireTypeException e22222 = invalidWireTypeException;
                                        if (!unknownFieldSchema3.shouldDiscardUnknownFields(reader3)) {
                                            if (unknownFields2 == null) {
                                                UB unknownFields4 = unknownFieldSchema3.getBuilderFromMessage(t);
                                                unknownFields2 = unknownFields4;
                                            }
                                            boolean mergeOneFieldFrom = unknownFieldSchema3.mergeOneFieldFrom(unknownFields2, reader3);
                                            unknownFieldSchema6 = unknownFieldSchema3;
                                            if (!mergeOneFieldFrom) {
                                                ?? r11 = unknownFields2;
                                                for (int i3 = this.checkInitializedCount; i3 < this.repeatedFieldOffsetStart; i3++) {
                                                    r11 = filterMapUnknownEnumValues(message, this.intArray[i3], r11, unknownFieldSchema, message);
                                                }
                                                if (r11 != null) {
                                                    unknownFieldSchema3.setBuilderToMessage(t, r11);
                                                    return;
                                                }
                                                return;
                                            }
                                        } else if (!reader.skipField()) {
                                            int i4 = this.checkInitializedCount;
                                            while (i4 < this.repeatedFieldOffsetStart) {
                                                unknownFields2 = (UB) filterMapUnknownEnumValues(message, this.intArray[i4], unknownFields2, unknownFieldSchema, message);
                                                i4++;
                                                e22222 = e22222;
                                            }
                                            UB unknownFields5 = unknownFields2;
                                            if (unknownFields5 != null) {
                                                unknownFieldSchema3.setBuilderToMessage(t, unknownFields5);
                                                return;
                                            }
                                            return;
                                        } else {
                                            unknownFieldSchema6 = unknownFieldSchema3;
                                        }
                                        extensionRegistryLite = extensionRegistryLite2;
                                        t2 = t;
                                        reader2 = reader3;
                                        extensions2 = extensions3;
                                    }
                            }
                        } catch (InvalidProtocolBufferException.InvalidWireTypeException e7) {
                            invalidWireTypeException = e7;
                            unknownFieldSchema3 = unknownFieldSchema6;
                        }
                        extensionRegistryLite = extensionRegistryLite2;
                        t2 = t;
                        reader2 = reader3;
                        extensions2 = extensions3;
                    } catch (Throwable th5) {
                        th = th5;
                        unknownFieldSchema2 = unknownFieldSchema6;
                    }
                } else if (number == Integer.MAX_VALUE) {
                    int i5 = this.checkInitializedCount;
                    while (i5 < this.repeatedFieldOffsetStart) {
                        unknownFields2 = (UB) filterMapUnknownEnumValues(message, this.intArray[i5], unknownFields2, unknownFieldSchema, message);
                        i5++;
                        pos = pos;
                    }
                    UB unknownFields6 = unknownFields2;
                    if (unknownFields6 != null) {
                        unknownFieldSchema6.setBuilderToMessage(t2, unknownFields6);
                        return;
                    }
                    return;
                } else {
                    try {
                        Object extension = !this.hasExtensions ? null : extensionSchema.findExtensionByNumber(extensionRegistryLite, this.defaultInstance, number);
                        if (extension != null) {
                            if (extensions2 == null) {
                                try {
                                    extensions = extensionSchema.getMutableExtensions(message);
                                } catch (Throwable th6) {
                                    th = th6;
                                    t = t2;
                                    unknownFieldSchema2 = unknownFieldSchema6;
                                    obj = unknownFields2;
                                    while (i < this.repeatedFieldOffsetStart) {
                                    }
                                    if (obj != null) {
                                    }
                                    throw th;
                                }
                            } else {
                                extensions = extensions2;
                            }
                            ExtensionRegistryLite extensionRegistryLite3 = extensionRegistryLite;
                            Reader reader4 = reader2;
                            T t3 = t2;
                            try {
                                unknownFields2 = extensionSchema.parseExtension(message, reader, extension, extensionRegistry, extensions, unknownFields2, unknownFieldSchema);
                                extensions2 = extensions;
                                extensionRegistryLite = extensionRegistryLite3;
                                t2 = t3;
                                reader2 = reader4;
                            } catch (Throwable th7) {
                                th = th7;
                                t = t3;
                                unknownFieldSchema2 = unknownFieldSchema6;
                                obj = unknownFields2;
                                while (i < this.repeatedFieldOffsetStart) {
                                }
                                if (obj != null) {
                                }
                                throw th;
                            }
                        } else {
                            int number2 = number;
                            ExtensionRegistryLite extensionRegistryLite4 = extensionRegistryLite;
                            Reader reader5 = reader2;
                            T t4 = t2;
                            try {
                                if (unknownFieldSchema6.shouldDiscardUnknownFields(reader5)) {
                                    try {
                                        if (reader.skipField()) {
                                            extensionRegistryLite = extensionRegistryLite4;
                                            t2 = t4;
                                            reader2 = reader5;
                                        }
                                    } catch (Throwable th8) {
                                        th = th8;
                                        t = t4;
                                        unknownFieldSchema2 = unknownFieldSchema6;
                                        obj = unknownFields2;
                                        while (i < this.repeatedFieldOffsetStart) {
                                        }
                                        if (obj != null) {
                                        }
                                        throw th;
                                    }
                                } else {
                                    if (unknownFields2 == null) {
                                        UB unknownFields7 = unknownFieldSchema6.getBuilderFromMessage(t4);
                                        unknownFields2 = unknownFields7;
                                    }
                                    if (unknownFieldSchema6.mergeOneFieldFrom(unknownFields2, reader5)) {
                                        extensionRegistryLite = extensionRegistryLite4;
                                        t2 = t4;
                                        reader2 = reader5;
                                    }
                                }
                            } catch (Throwable th9) {
                                th = th9;
                                t = t4;
                                th = th;
                                unknownFieldSchema2 = unknownFieldSchema6;
                                obj = unknownFields2;
                                while (i < this.repeatedFieldOffsetStart) {
                                }
                                if (obj != null) {
                                }
                                throw th;
                            }
                        }
                    } catch (Throwable th10) {
                        th = th10;
                        t = t2;
                    }
                }
            } catch (Throwable th11) {
                t = t2;
                th = th11;
                unknownFieldSchema2 = unknownFieldSchema6;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static UnknownFieldSetLite getMutableUnknownFields(Object message) {
        UnknownFieldSetLite unknownFields = ((GeneratedMessageLite) message).unknownFields;
        if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
            UnknownFieldSetLite unknownFields2 = UnknownFieldSetLite.newInstance();
            ((GeneratedMessageLite) message).unknownFields = unknownFields2;
            return unknownFields2;
        }
        return unknownFields;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.framework.protobuf.MessageSchema$1 */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class C40151 {
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
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED32.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED32.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED64.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED64.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FLOAT.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.ENUM.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT32.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT32.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT64.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT64.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.MESSAGE.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT32.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT64.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.STRING.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
        }
    }

    private int decodeMapEntryValue(byte[] data, int position, int limit, WireFormat.FieldType fieldType, Class<?> messageType, ArrayDecoders.Registers registers) throws IOException {
        switch (C40151.$SwitchMap$com$google$protobuf$WireFormat$FieldType[fieldType.ordinal()]) {
            case 1:
                int position2 = ArrayDecoders.decodeVarint64(data, position, registers);
                registers.object1 = Boolean.valueOf(registers.long1 != 0);
                return position2;
            case 2:
                return ArrayDecoders.decodeBytes(data, position, registers);
            case 3:
                registers.object1 = Double.valueOf(ArrayDecoders.decodeDouble(data, position));
                return position + 8;
            case 4:
            case 5:
                registers.object1 = Integer.valueOf(ArrayDecoders.decodeFixed32(data, position));
                return position + 4;
            case 6:
            case 7:
                registers.object1 = Long.valueOf(ArrayDecoders.decodeFixed64(data, position));
                return position + 8;
            case 8:
                registers.object1 = Float.valueOf(ArrayDecoders.decodeFloat(data, position));
                return position + 4;
            case 9:
            case 10:
            case 11:
                int position3 = ArrayDecoders.decodeVarint32(data, position, registers);
                registers.object1 = Integer.valueOf(registers.int1);
                return position3;
            case 12:
            case 13:
                int position4 = ArrayDecoders.decodeVarint64(data, position, registers);
                registers.object1 = Long.valueOf(registers.long1);
                return position4;
            case 14:
                return ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor((Class) messageType), data, position, limit, registers);
            case 15:
                int position5 = ArrayDecoders.decodeVarint32(data, position, registers);
                registers.object1 = Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1));
                return position5;
            case 16:
                int position6 = ArrayDecoders.decodeVarint64(data, position, registers);
                registers.object1 = Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1));
                return position6;
            case 17:
                return ArrayDecoders.decodeStringRequireUtf8(data, position, registers);
            default:
                throw new RuntimeException("unsupported field type.");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v1, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r13v2, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r13v3 */
    /* JADX WARN: Type inference failed for: r14v1, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r14v2, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r14v3 */
    /* JADX WARN: Type inference failed for: r23v0, types: [java.util.Map, java.util.Map<K, V>] */
    private <K, V> int decodeMapEntry(byte[] data, int position, int limit, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map, ArrayDecoders.Registers registers) throws IOException {
        int tag;
        int position2;
        int position3;
        int length;
        int position4 = ArrayDecoders.decodeVarint32(data, position, registers);
        int wireType = registers.int1;
        if (wireType < 0 || wireType > limit - position4) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        int end = position4 + wireType;
        K key = metadata.defaultKey;
        K key2 = key;
        V value = metadata.defaultValue;
        while (position4 < end) {
            int position5 = position4 + 1;
            int tag2 = data[position4];
            if (tag2 >= 0) {
                tag = tag2;
                position2 = position5;
            } else {
                int position6 = ArrayDecoders.decodeVarint32(tag2, data, position5, registers);
                tag = registers.int1;
                position2 = position6;
            }
            int fieldNumber = tag >>> 3;
            int wireType2 = tag & 7;
            switch (fieldNumber) {
                case 1:
                    position3 = position2;
                    length = wireType;
                    if (wireType2 != metadata.keyType.getWireType()) {
                        break;
                    } else {
                        position4 = decodeMapEntryValue(data, position3, limit, metadata.keyType, null, registers);
                        key2 = registers.object1;
                        wireType = length;
                        continue;
                    }
                case 2:
                    if (wireType2 != metadata.valueType.getWireType()) {
                        position3 = position2;
                        length = wireType;
                        break;
                    } else {
                        int length2 = wireType;
                        position4 = decodeMapEntryValue(data, position2, limit, metadata.valueType, metadata.defaultValue.getClass(), registers);
                        value = registers.object1;
                        wireType = length2;
                        continue;
                    }
                default:
                    position3 = position2;
                    length = wireType;
                    break;
            }
            position4 = ArrayDecoders.skipField(tag, data, position3, limit, registers);
            wireType = length;
        }
        if (position4 == end) {
            map.put(key2, value);
            return end;
        }
        throw InvalidProtocolBufferException.parseFailure();
    }

    private int parseRepeatedField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int bufferPosition, long typeAndOffset, int fieldType, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
        Internal.ProtobufList<?> list;
        int position2;
        int position3;
        Unsafe unsafe = UNSAFE;
        Internal.ProtobufList<?> list2 = (Internal.ProtobufList) unsafe.getObject(message, fieldOffset);
        if (list2.isModifiable()) {
            list = list2;
        } else {
            int size = list2.size();
            Internal.ProtobufList<?> list3 = list2.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
            unsafe.putObject(message, fieldOffset, list3);
            list = list3;
        }
        switch (fieldType) {
            case 18:
            case 35:
                position2 = position;
                Internal.ProtobufList<?> list4 = list;
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedDoubleList(data, position2, list4, registers);
                }
                if (wireType == 1) {
                    return ArrayDecoders.decodeDoubleList(tag, data, position, limit, list4, registers);
                }
                break;
            case 19:
            case 36:
                position2 = position;
                Internal.ProtobufList<?> list5 = list;
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedFloatList(data, position2, list5, registers);
                }
                if (wireType == 5) {
                    return ArrayDecoders.decodeFloatList(tag, data, position, limit, list5, registers);
                }
                break;
            case 20:
            case 21:
            case 37:
            case 38:
                position2 = position;
                Internal.ProtobufList<?> list6 = list;
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedVarint64List(data, position2, list6, registers);
                }
                if (wireType == 0) {
                    return ArrayDecoders.decodeVarint64List(tag, data, position, limit, list6, registers);
                }
                break;
            case 22:
            case 29:
            case 39:
            case 43:
                position2 = position;
                Internal.ProtobufList<?> list7 = list;
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedVarint32List(data, position2, list7, registers);
                }
                if (wireType == 0) {
                    return ArrayDecoders.decodeVarint32List(tag, data, position, limit, list7, registers);
                }
                break;
            case 23:
            case 32:
            case 40:
            case 46:
                position2 = position;
                Internal.ProtobufList<?> list8 = list;
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedFixed64List(data, position2, list8, registers);
                }
                if (wireType == 1) {
                    return ArrayDecoders.decodeFixed64List(tag, data, position, limit, list8, registers);
                }
                break;
            case 24:
            case 31:
            case 41:
            case 45:
                position2 = position;
                Internal.ProtobufList<?> list9 = list;
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedFixed32List(data, position2, list9, registers);
                }
                if (wireType == 5) {
                    return ArrayDecoders.decodeFixed32List(tag, data, position, limit, list9, registers);
                }
                break;
            case 25:
            case 42:
                position2 = position;
                Internal.ProtobufList<?> list10 = list;
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedBoolList(data, position2, list10, registers);
                }
                if (wireType == 0) {
                    return ArrayDecoders.decodeBoolList(tag, data, position, limit, list10, registers);
                }
                break;
            case 26:
                Internal.ProtobufList<?> list11 = list;
                if (wireType != 2) {
                    position2 = position;
                    break;
                } else {
                    return (typeAndOffset & 536870912) == 0 ? ArrayDecoders.decodeStringList(tag, data, position, limit, list11, registers) : ArrayDecoders.decodeStringListRequireUtf8(tag, data, position, limit, list11, registers);
                }
            case 27:
                Internal.ProtobufList<?> list12 = list;
                if (wireType != 2) {
                    position2 = position;
                    break;
                } else {
                    return ArrayDecoders.decodeMessageList(getMessageFieldSchema(bufferPosition), tag, data, position, limit, list12, registers);
                }
            case 28:
                Internal.ProtobufList<?> list13 = list;
                if (wireType != 2) {
                    position2 = position;
                    break;
                } else {
                    return ArrayDecoders.decodeBytesList(tag, data, position, limit, list13, registers);
                }
            case 30:
            case 44:
                Internal.ProtobufList<?> list14 = list;
                if (wireType == 2) {
                    position3 = ArrayDecoders.decodePackedVarint32List(data, position, list14, registers);
                } else if (wireType != 0) {
                    position2 = position;
                    break;
                } else {
                    position3 = ArrayDecoders.decodeVarint32List(tag, data, position, limit, list14, registers);
                }
                SchemaUtil.filterUnknownEnumList((Object) message, number, (List<Integer>) list14, getEnumFieldVerifier(bufferPosition), (Object) null, (UnknownFieldSchema<UT, Object>) this.unknownFieldSchema);
                return position3;
            case 33:
            case 47:
                Internal.ProtobufList<?> list15 = list;
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedSInt32List(data, position, list15, registers);
                }
                if (wireType != 0) {
                    position2 = position;
                    break;
                } else {
                    return ArrayDecoders.decodeSInt32List(tag, data, position, limit, list15, registers);
                }
            case 34:
            case 48:
                Internal.ProtobufList<?> list16 = list;
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedSInt64List(data, position, list16, registers);
                }
                if (wireType != 0) {
                    position2 = position;
                    break;
                } else {
                    return ArrayDecoders.decodeSInt64List(tag, data, position, limit, list16, registers);
                }
            case 49:
                if (wireType != 3) {
                    position2 = position;
                    break;
                } else {
                    return ArrayDecoders.decodeGroupList(getMessageFieldSchema(bufferPosition), tag, data, position, limit, list, registers);
                }
            default:
                position2 = position;
                break;
        }
        return position2;
    }

    private <K, V> int parseMapField(T message, byte[] data, int position, int limit, int bufferPosition, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
        Object mapField;
        Unsafe unsafe = UNSAFE;
        Object mapDefaultEntry = getMapFieldDefaultEntry(bufferPosition);
        Object mapField2 = unsafe.getObject(message, fieldOffset);
        if (!this.mapFieldSchema.isImmutable(mapField2)) {
            mapField = mapField2;
        } else {
            Object mapField3 = this.mapFieldSchema.newMapField(mapDefaultEntry);
            this.mapFieldSchema.mergeFrom(mapField3, mapField2);
            unsafe.putObject(message, fieldOffset, mapField3);
            mapField = mapField3;
        }
        return decodeMapEntry(data, position, limit, this.mapFieldSchema.forMapMetadata(mapDefaultEntry), this.mapFieldSchema.forMutableMapData(mapField), registers);
    }

    private int parseOneofField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int typeAndOffset, int fieldType, long fieldOffset, int bufferPosition, ArrayDecoders.Registers registers) throws IOException {
        long oneofCaseOffset;
        long oneofCaseOffset2;
        Unsafe unsafe = UNSAFE;
        long oneofCaseOffset3 = this.buffer[bufferPosition + 2] & 1048575;
        switch (fieldType) {
            case 51:
                if (wireType == 1) {
                    unsafe.putObject(message, fieldOffset, Double.valueOf(ArrayDecoders.decodeDouble(data, position)));
                    int position2 = position + 8;
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position2;
                }
                break;
            case 52:
                if (wireType == 5) {
                    unsafe.putObject(message, fieldOffset, Float.valueOf(ArrayDecoders.decodeFloat(data, position)));
                    int position3 = position + 4;
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position3;
                }
                break;
            case 53:
            case 54:
                if (wireType == 0) {
                    int position4 = ArrayDecoders.decodeVarint64(data, position, registers);
                    unsafe.putObject(message, fieldOffset, Long.valueOf(registers.long1));
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position4;
                }
                break;
            case 55:
            case 62:
                if (wireType == 0) {
                    int position5 = ArrayDecoders.decodeVarint32(data, position, registers);
                    unsafe.putObject(message, fieldOffset, Integer.valueOf(registers.int1));
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position5;
                }
                break;
            case 56:
            case 65:
                if (wireType == 1) {
                    unsafe.putObject(message, fieldOffset, Long.valueOf(ArrayDecoders.decodeFixed64(data, position)));
                    int position6 = position + 8;
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position6;
                }
                break;
            case 57:
            case 64:
                if (wireType == 5) {
                    unsafe.putObject(message, fieldOffset, Integer.valueOf(ArrayDecoders.decodeFixed32(data, position)));
                    int position7 = position + 4;
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position7;
                }
                break;
            case 58:
                if (wireType == 0) {
                    int position8 = ArrayDecoders.decodeVarint64(data, position, registers);
                    unsafe.putObject(message, fieldOffset, Boolean.valueOf(registers.long1 != 0));
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position8;
                }
                break;
            case 59:
                if (wireType != 2) {
                    break;
                } else {
                    int position9 = ArrayDecoders.decodeVarint32(data, position, registers);
                    int length = registers.int1;
                    if (length == 0) {
                        unsafe.putObject(message, fieldOffset, "");
                    } else if ((typeAndOffset & 536870912) != 0 && !Utf8.isValidUtf8(data, position9, position9 + length)) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    } else {
                        String value = new String(data, position9, length, Internal.UTF_8);
                        unsafe.putObject(message, fieldOffset, value);
                        position9 += length;
                    }
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position9;
                }
                break;
            case 60:
                if (wireType != 2) {
                    break;
                } else {
                    Object current = mutableOneofMessageFieldForMerge(message, number, bufferPosition);
                    int position10 = ArrayDecoders.mergeMessageField(current, getMessageFieldSchema(bufferPosition), data, position, limit, registers);
                    storeOneofMessageField(message, number, bufferPosition, current);
                    return position10;
                }
            case 61:
                if (wireType != 2) {
                    break;
                } else {
                    int position11 = ArrayDecoders.decodeBytes(data, position, registers);
                    unsafe.putObject(message, fieldOffset, registers.object1);
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position11;
                }
            case 63:
                if (wireType != 0) {
                    break;
                } else {
                    int position12 = ArrayDecoders.decodeVarint32(data, position, registers);
                    int enumValue = registers.int1;
                    Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(bufferPosition);
                    if (enumVerifier == null) {
                        oneofCaseOffset = oneofCaseOffset3;
                    } else if (!enumVerifier.isInRange(enumValue)) {
                        getMutableUnknownFields(message).storeField(tag, Long.valueOf(enumValue));
                        oneofCaseOffset2 = oneofCaseOffset3;
                        return position12;
                    } else {
                        oneofCaseOffset = oneofCaseOffset3;
                    }
                    unsafe.putObject(message, fieldOffset, Integer.valueOf(enumValue));
                    oneofCaseOffset2 = oneofCaseOffset;
                    unsafe.putInt(message, oneofCaseOffset2, number);
                    return position12;
                }
            case 66:
                if (wireType != 0) {
                    break;
                } else {
                    int position13 = ArrayDecoders.decodeVarint32(data, position, registers);
                    unsafe.putObject(message, fieldOffset, Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1)));
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position13;
                }
            case 67:
                if (wireType != 0) {
                    break;
                } else {
                    int position14 = ArrayDecoders.decodeVarint64(data, position, registers);
                    unsafe.putObject(message, fieldOffset, Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1)));
                    unsafe.putInt(message, oneofCaseOffset3, number);
                    return position14;
                }
            case 68:
                if (wireType != 3) {
                    break;
                } else {
                    Object current2 = mutableOneofMessageFieldForMerge(message, number, bufferPosition);
                    int endTag = (tag & (-8)) | 4;
                    int position15 = ArrayDecoders.mergeGroupField(current2, getMessageFieldSchema(bufferPosition), data, position, limit, endTag, registers);
                    storeOneofMessageField(message, number, bufferPosition, current2);
                    return position15;
                }
        }
        return position;
    }

    private Schema getMessageFieldSchema(int pos) {
        int index = (pos / 3) * 2;
        Schema schema = (Schema) this.objects[index];
        if (schema != null) {
            return schema;
        }
        Schema schema2 = Protobuf.getInstance().schemaFor((Class) ((Class) this.objects[index + 1]));
        this.objects[index] = schema2;
        return schema2;
    }

    private Object getMapFieldDefaultEntry(int pos) {
        return this.objects[(pos / 3) * 2];
    }

    private Internal.EnumVerifier getEnumFieldVerifier(int pos) {
        return (Internal.EnumVerifier) this.objects[((pos / 3) * 2) + 1];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int parseProto2Message(T message, byte[] data, int position, int limit, int endGroup, ArrayDecoders.Registers registers) throws IOException {
        Unsafe unsafe;
        int i;
        MessageSchema<T> messageSchema;
        int position2;
        int tag;
        T t;
        int tag2;
        int position3;
        int pos;
        int wireType;
        int tag3;
        int pos2;
        int position4;
        int currentPresenceField;
        ArrayDecoders.Registers registers2;
        int position5;
        int currentPresenceFieldOffset;
        int currentPresenceFieldOffset2;
        int presenceFieldOffset;
        int tag4;
        int pos3;
        MessageSchema<T> messageSchema2 = this;
        T t2 = message;
        byte[] bArr = data;
        int pos4 = limit;
        int wireType2 = endGroup;
        ArrayDecoders.Registers registers3 = registers;
        checkMutable(message);
        Unsafe unsafe2 = UNSAFE;
        int tag5 = 0;
        int oldNumber = -1;
        int pos5 = 0;
        int currentPresenceFieldOffset3 = 1048575;
        int currentPresenceField2 = 0;
        int position6 = position;
        while (true) {
            if (position6 < pos4) {
                int position7 = position6 + 1;
                int tag6 = bArr[position6];
                if (tag6 >= 0) {
                    tag2 = tag6;
                    position3 = position7;
                } else {
                    int position8 = ArrayDecoders.decodeVarint32(tag6, bArr, position7, registers3);
                    int tag7 = registers3.int1;
                    tag2 = tag7;
                    position3 = position8;
                }
                int position9 = tag2 >>> 3;
                int wireType3 = tag2 & 7;
                if (position9 > oldNumber) {
                    pos = messageSchema2.positionForFieldNumber(position9, pos5 / 3);
                } else {
                    int pos6 = messageSchema2.positionForFieldNumber(position9);
                    pos = pos6;
                }
                if (pos == -1) {
                    wireType = wireType3;
                    tag3 = tag2;
                    pos2 = 0;
                    position4 = position3;
                    currentPresenceField = currentPresenceField2;
                    unsafe = unsafe2;
                } else {
                    int typeAndOffset = messageSchema2.buffer[pos + 1];
                    int fieldType = type(typeAndOffset);
                    long fieldOffset = offset(typeAndOffset);
                    int tag8 = tag2;
                    if (fieldType <= 17) {
                        int presenceMaskAndOffset = messageSchema2.buffer[pos + 2];
                        int presenceMask = 1 << (presenceMaskAndOffset >>> 20);
                        int presenceFieldOffset2 = presenceMaskAndOffset & 1048575;
                        if (presenceFieldOffset2 == currentPresenceFieldOffset3) {
                            position5 = position3;
                        } else {
                            if (currentPresenceFieldOffset3 == 1048575) {
                                position5 = position3;
                            } else {
                                position5 = position3;
                                unsafe2.putInt(t2, currentPresenceFieldOffset3, currentPresenceField2);
                            }
                            currentPresenceField2 = unsafe2.getInt(t2, presenceFieldOffset2);
                            currentPresenceFieldOffset3 = presenceFieldOffset2;
                        }
                        switch (fieldType) {
                            case 0:
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                pos3 = pos;
                                tag4 = tag8;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset == 1) {
                                    UnsafeUtil.putDouble(t2, fieldOffset, ArrayDecoders.decodeDouble(data, currentPresenceFieldOffset2));
                                    position6 = currentPresenceFieldOffset2 + 8;
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    bArr = data;
                                    oldNumber = position9;
                                    pos5 = pos3;
                                    tag5 = tag4;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                } else {
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                }
                            case 1:
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                pos3 = pos;
                                tag4 = tag8;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset == 5) {
                                    UnsafeUtil.putFloat(t2, fieldOffset, ArrayDecoders.decodeFloat(data, currentPresenceFieldOffset2));
                                    position6 = currentPresenceFieldOffset2 + 4;
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    bArr = data;
                                    oldNumber = position9;
                                    pos5 = pos3;
                                    tag5 = tag4;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                } else {
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                }
                            case 2:
                            case 3:
                                int pos7 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                tag4 = tag8;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset != 0) {
                                    pos3 = pos7;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                } else {
                                    int position10 = ArrayDecoders.decodeVarint64(data, currentPresenceFieldOffset2, registers3);
                                    unsafe2.putLong(message, fieldOffset, registers3.long1);
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    bArr = data;
                                    position6 = position10;
                                    oldNumber = position9;
                                    pos5 = pos7;
                                    tag5 = tag4;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                }
                            case 4:
                            case 11:
                                int pos8 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                tag4 = tag8;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset == 0) {
                                    position6 = ArrayDecoders.decodeVarint32(data, currentPresenceFieldOffset2, registers3);
                                    unsafe2.putInt(t2, fieldOffset, registers3.int1);
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    pos5 = pos8;
                                    oldNumber = position9;
                                    tag5 = tag4;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    bArr = data;
                                    break;
                                } else {
                                    pos3 = pos8;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                }
                            case 5:
                            case 14:
                                int pos9 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                presenceFieldOffset = wireType3;
                                tag4 = tag8;
                                if (presenceFieldOffset == 1) {
                                    unsafe2.putLong(message, fieldOffset, ArrayDecoders.decodeFixed64(data, currentPresenceFieldOffset2));
                                    position6 = currentPresenceFieldOffset2 + 8;
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    pos5 = pos9;
                                    oldNumber = position9;
                                    tag5 = tag4;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    bArr = data;
                                    break;
                                } else {
                                    pos3 = pos9;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                }
                            case 6:
                            case 13:
                                int pos10 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                presenceFieldOffset = wireType3;
                                tag4 = tag8;
                                if (presenceFieldOffset == 5) {
                                    unsafe2.putInt(t2, fieldOffset, ArrayDecoders.decodeFixed32(data, currentPresenceFieldOffset2));
                                    int position11 = currentPresenceFieldOffset2 + 4;
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    position6 = position11;
                                    pos5 = pos10;
                                    oldNumber = position9;
                                    tag5 = tag4;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    bArr = data;
                                    break;
                                } else {
                                    pos3 = pos10;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                }
                            case 7:
                                int pos11 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                tag4 = tag8;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset != 0) {
                                    pos3 = pos11;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                } else {
                                    position6 = ArrayDecoders.decodeVarint64(data, currentPresenceFieldOffset2, registers3);
                                    UnsafeUtil.putBoolean(t2, fieldOffset, registers3.long1 != 0);
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    pos5 = pos11;
                                    oldNumber = position9;
                                    tag5 = tag4;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    bArr = data;
                                    break;
                                }
                            case 8:
                                int pos12 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                Unsafe unsafe3 = unsafe2;
                                tag4 = tag8;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset != 2) {
                                    unsafe2 = unsafe3;
                                    pos3 = pos12;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                } else {
                                    if ((typeAndOffset & 536870912) == 0) {
                                        position6 = ArrayDecoders.decodeString(data, currentPresenceFieldOffset2, registers3);
                                    } else {
                                        position6 = ArrayDecoders.decodeStringRequireUtf8(data, currentPresenceFieldOffset2, registers3);
                                    }
                                    unsafe2 = unsafe3;
                                    unsafe2.putObject(t2, fieldOffset, registers3.object1);
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    pos5 = pos12;
                                    oldNumber = position9;
                                    tag5 = tag4;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    bArr = data;
                                    break;
                                }
                            case 9:
                                int pos13 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset != 2) {
                                    tag4 = tag8;
                                    pos3 = pos13;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                } else {
                                    Object current = messageSchema2.mutableMessageFieldForMerge(t2, pos13);
                                    position6 = ArrayDecoders.mergeMessageField(current, messageSchema2.getMessageFieldSchema(pos13), data, currentPresenceFieldOffset2, limit, registers);
                                    messageSchema2.storeMessageField(t2, pos13, current);
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    pos5 = pos13;
                                    oldNumber = position9;
                                    unsafe2 = unsafe2;
                                    tag5 = tag8;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    bArr = data;
                                    break;
                                }
                            case 10:
                                int pos14 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset != 2) {
                                    tag4 = tag8;
                                    pos3 = pos14;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                } else {
                                    position6 = ArrayDecoders.decodeBytes(data, currentPresenceFieldOffset2, registers3);
                                    unsafe2.putObject(t2, fieldOffset, registers3.object1);
                                    currentPresenceField2 |= presenceMask;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    tag5 = tag8;
                                    bArr = data;
                                    oldNumber = position9;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    pos5 = pos14;
                                    break;
                                }
                            case 12:
                                int pos15 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset != 0) {
                                    tag4 = tag8;
                                    pos3 = pos15;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                } else {
                                    position6 = ArrayDecoders.decodeVarint32(data, currentPresenceFieldOffset2, registers3);
                                    int enumValue = registers3.int1;
                                    Internal.EnumVerifier enumVerifier = messageSchema2.getEnumFieldVerifier(pos15);
                                    if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
                                        unsafe2.putInt(t2, fieldOffset, enumValue);
                                        currentPresenceField2 |= presenceMask;
                                        pos4 = limit;
                                        wireType2 = endGroup;
                                        tag5 = tag8;
                                        bArr = data;
                                        oldNumber = position9;
                                        currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                        pos5 = pos15;
                                        break;
                                    } else {
                                        getMutableUnknownFields(message).storeField(tag8, Long.valueOf(enumValue));
                                        pos4 = limit;
                                        wireType2 = endGroup;
                                        tag5 = tag8;
                                        bArr = data;
                                        oldNumber = position9;
                                        currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                        pos5 = pos15;
                                        break;
                                    }
                                }
                                break;
                            case 15:
                                int pos16 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset != 0) {
                                    tag4 = tag8;
                                    pos3 = pos16;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                } else {
                                    position6 = ArrayDecoders.decodeVarint32(data, currentPresenceFieldOffset2, registers3);
                                    unsafe2.putInt(t2, fieldOffset, CodedInputStream.decodeZigZag32(registers3.int1));
                                    currentPresenceField2 |= presenceMask;
                                    tag5 = tag8;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    bArr = data;
                                    oldNumber = position9;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    pos5 = pos16;
                                    break;
                                }
                            case 16:
                                int pos17 = pos;
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                presenceFieldOffset = wireType3;
                                if (presenceFieldOffset == 0) {
                                    int position12 = ArrayDecoders.decodeVarint64(data, currentPresenceFieldOffset2, registers3);
                                    unsafe2.putLong(message, fieldOffset, CodedInputStream.decodeZigZag64(registers3.long1));
                                    currentPresenceField2 |= presenceMask;
                                    tag5 = tag8;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    bArr = data;
                                    position6 = position12;
                                    oldNumber = position9;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    pos5 = pos17;
                                    break;
                                } else {
                                    tag4 = tag8;
                                    pos3 = pos17;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                }
                            case 17:
                                if (wireType3 == 3) {
                                    Object current2 = messageSchema2.mutableMessageFieldForMerge(t2, pos);
                                    int endTag = pos;
                                    position6 = ArrayDecoders.mergeGroupField(current2, messageSchema2.getMessageFieldSchema(pos), data, position5, limit, (position9 << 3) | 4, registers);
                                    messageSchema2.storeMessageField(t2, endTag, current2);
                                    currentPresenceField2 |= presenceMask;
                                    bArr = data;
                                    tag5 = tag8;
                                    wireType2 = endGroup;
                                    pos5 = endTag;
                                    oldNumber = position9;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset3;
                                    pos4 = limit;
                                    break;
                                } else {
                                    currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                    currentPresenceFieldOffset2 = position5;
                                    presenceFieldOffset = wireType3;
                                    tag4 = tag8;
                                    pos3 = pos;
                                    currentPresenceField = currentPresenceField2;
                                    unsafe = unsafe2;
                                    wireType = presenceFieldOffset;
                                    pos2 = pos3;
                                    tag3 = tag4;
                                    position4 = currentPresenceFieldOffset2;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                    break;
                                }
                            default:
                                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                                currentPresenceFieldOffset2 = position5;
                                pos3 = pos;
                                tag4 = tag8;
                                presenceFieldOffset = wireType3;
                                currentPresenceField = currentPresenceField2;
                                unsafe = unsafe2;
                                wireType = presenceFieldOffset;
                                pos2 = pos3;
                                tag3 = tag4;
                                position4 = currentPresenceFieldOffset2;
                                currentPresenceFieldOffset3 = currentPresenceFieldOffset;
                                break;
                        }
                    } else {
                        int pos18 = pos;
                        int currentPresenceFieldOffset4 = currentPresenceFieldOffset3;
                        int currentPresenceFieldOffset5 = position3;
                        if (fieldType != 27) {
                            pos2 = pos18;
                            if (fieldType <= 49) {
                                currentPresenceField = currentPresenceField2;
                                tag3 = tag8;
                                unsafe = unsafe2;
                                position6 = parseRepeatedField(message, data, currentPresenceFieldOffset5, limit, tag8, position9, wireType3, pos2, typeAndOffset, fieldType, fieldOffset, registers);
                                if (position6 != currentPresenceFieldOffset5) {
                                    messageSchema2 = this;
                                    t2 = message;
                                    bArr = data;
                                    tag5 = tag3;
                                    pos4 = limit;
                                    wireType2 = endGroup;
                                    registers3 = registers;
                                    oldNumber = position9;
                                    pos5 = pos2;
                                    currentPresenceField2 = currentPresenceField;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset4;
                                    unsafe2 = unsafe;
                                } else {
                                    position4 = position6;
                                    wireType = wireType3;
                                    currentPresenceFieldOffset3 = currentPresenceFieldOffset4;
                                }
                            } else {
                                currentPresenceField = currentPresenceField2;
                                position4 = currentPresenceFieldOffset5;
                                unsafe = unsafe2;
                                tag3 = tag8;
                                if (fieldType == 50) {
                                    wireType = wireType3;
                                    if (wireType == 2) {
                                        position6 = parseMapField(message, data, position4, limit, pos2, fieldOffset, registers);
                                        if (position6 != position4) {
                                            messageSchema2 = this;
                                            t2 = message;
                                            bArr = data;
                                            tag5 = tag3;
                                            pos4 = limit;
                                            wireType2 = endGroup;
                                            registers3 = registers;
                                            oldNumber = position9;
                                            pos5 = pos2;
                                            currentPresenceField2 = currentPresenceField;
                                            currentPresenceFieldOffset3 = currentPresenceFieldOffset4;
                                            unsafe2 = unsafe;
                                        } else {
                                            position4 = position6;
                                            currentPresenceFieldOffset3 = currentPresenceFieldOffset4;
                                        }
                                    } else {
                                        currentPresenceFieldOffset3 = currentPresenceFieldOffset4;
                                    }
                                } else {
                                    wireType = wireType3;
                                    position6 = parseOneofField(message, data, position4, limit, tag3, position9, wireType, typeAndOffset, fieldType, fieldOffset, pos2, registers);
                                    if (position6 == position4) {
                                        position4 = position6;
                                        currentPresenceFieldOffset3 = currentPresenceFieldOffset4;
                                    } else {
                                        messageSchema2 = this;
                                        t2 = message;
                                        bArr = data;
                                        tag5 = tag3;
                                        pos4 = limit;
                                        wireType2 = endGroup;
                                        registers3 = registers;
                                        oldNumber = position9;
                                        pos5 = pos2;
                                        currentPresenceField2 = currentPresenceField;
                                        currentPresenceFieldOffset3 = currentPresenceFieldOffset4;
                                        unsafe2 = unsafe;
                                    }
                                }
                            }
                        } else if (wireType3 == 2) {
                            Internal.ProtobufList<?> list = (Internal.ProtobufList) unsafe2.getObject(t2, fieldOffset);
                            if (!list.isModifiable()) {
                                int size = list.size();
                                list = list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
                                unsafe2.putObject(t2, fieldOffset, list);
                            }
                            position6 = ArrayDecoders.decodeMessageList(messageSchema2.getMessageFieldSchema(pos18), tag8, data, currentPresenceFieldOffset5, limit, list, registers);
                            bArr = data;
                            pos4 = limit;
                            wireType2 = endGroup;
                            oldNumber = position9;
                            pos5 = pos18;
                            tag5 = tag8;
                            currentPresenceFieldOffset3 = currentPresenceFieldOffset4;
                        } else {
                            pos2 = pos18;
                            currentPresenceField = currentPresenceField2;
                            position4 = currentPresenceFieldOffset5;
                            unsafe = unsafe2;
                            wireType = wireType3;
                            tag3 = tag8;
                            currentPresenceFieldOffset3 = currentPresenceFieldOffset4;
                        }
                    }
                }
                tag = tag3;
                i = endGroup;
                if (tag == i && i != 0) {
                    messageSchema = this;
                    currentPresenceField2 = currentPresenceField;
                    position2 = position4;
                } else {
                    if (!this.hasExtensions) {
                        registers2 = registers;
                    } else {
                        registers2 = registers;
                        if (registers2.extensionRegistry != ExtensionRegistryLite.getEmptyRegistry()) {
                            position6 = ArrayDecoders.decodeExtensionOrUnknownField(tag, data, position4, limit, message, this.defaultInstance, this.unknownFieldSchema, registers);
                            t2 = message;
                            bArr = data;
                            tag5 = tag;
                            messageSchema2 = this;
                            oldNumber = position9;
                            pos5 = pos2;
                            currentPresenceField2 = currentPresenceField;
                            unsafe2 = unsafe;
                            wireType2 = i;
                            registers3 = registers2;
                            pos4 = limit;
                        }
                    }
                    position6 = ArrayDecoders.decodeUnknownField(tag, data, position4, limit, getMutableUnknownFields(message), registers);
                    t2 = message;
                    bArr = data;
                    tag5 = tag;
                    messageSchema2 = this;
                    oldNumber = position9;
                    pos5 = pos2;
                    currentPresenceField2 = currentPresenceField;
                    unsafe2 = unsafe;
                    wireType2 = i;
                    registers3 = registers2;
                    pos4 = limit;
                }
            } else {
                unsafe = unsafe2;
                i = wireType2;
                messageSchema = messageSchema2;
                position2 = position6;
                tag = tag5;
            }
        }
        if (currentPresenceFieldOffset3 == 1048575) {
            t = message;
        } else {
            t = message;
            unsafe.putInt(t, currentPresenceFieldOffset3, currentPresenceField2);
        }
        UnknownFieldSetLite unknownFields = null;
        for (int i2 = messageSchema.checkInitializedCount; i2 < messageSchema.repeatedFieldOffsetStart; i2++) {
            unknownFields = (UnknownFieldSetLite) filterMapUnknownEnumValues(message, messageSchema.intArray[i2], unknownFields, messageSchema.unknownFieldSchema, message);
        }
        if (unknownFields != null) {
            messageSchema.unknownFieldSchema.setBuilderToMessage(t, unknownFields);
        }
        if (i == 0) {
            if (position2 != limit) {
                throw InvalidProtocolBufferException.parseFailure();
            }
        } else if (position2 > limit || tag != i) {
            throw InvalidProtocolBufferException.parseFailure();
        }
        return position2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object mutableMessageFieldForMerge(T message, int pos) {
        Schema fieldSchema = getMessageFieldSchema(pos);
        long offset = offset(typeAndOffsetAt(pos));
        if (!isFieldPresent(message, pos)) {
            return fieldSchema.newInstance();
        }
        Object current = UNSAFE.getObject(message, offset);
        if (isMutable(current)) {
            return current;
        }
        Object newMessage = fieldSchema.newInstance();
        if (current != null) {
            fieldSchema.mergeFrom(newMessage, current);
        }
        return newMessage;
    }

    private void storeMessageField(T message, int pos, Object field) {
        UNSAFE.putObject(message, offset(typeAndOffsetAt(pos)), field);
        setFieldPresent(message, pos);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object mutableOneofMessageFieldForMerge(T message, int fieldNumber, int pos) {
        Schema fieldSchema = getMessageFieldSchema(pos);
        if (!isOneofPresent(message, fieldNumber, pos)) {
            return fieldSchema.newInstance();
        }
        Object current = UNSAFE.getObject(message, offset(typeAndOffsetAt(pos)));
        if (isMutable(current)) {
            return current;
        }
        Object newMessage = fieldSchema.newInstance();
        if (current != null) {
            fieldSchema.mergeFrom(newMessage, current);
        }
        return newMessage;
    }

    private void storeOneofMessageField(T message, int fieldNumber, int pos, Object field) {
        UNSAFE.putObject(message, offset(typeAndOffsetAt(pos)), field);
        setOneofPresent(message, fieldNumber, pos);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private int parseProto3Message(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
        int tag;
        int position2;
        int pos;
        int currentPresenceFieldOffset;
        Unsafe unsafe;
        int typeAndOffset;
        int presenceFieldOffset;
        int typeAndOffset2;
        int currentPresenceFieldOffset2;
        int position3;
        int position4;
        int currentPresenceField;
        int currentPresenceFieldOffset3;
        MessageSchema<T> messageSchema = this;
        T t = message;
        byte[] bArr = data;
        int pos2 = limit;
        ArrayDecoders.Registers registers2 = registers;
        checkMutable(message);
        Unsafe unsafe2 = UNSAFE;
        int oldNumber = -1;
        int pos3 = 0;
        int currentPresenceFieldOffset4 = 1048575;
        int currentPresenceField2 = 0;
        int position5 = position;
        while (position5 < pos2) {
            int position6 = position5 + 1;
            int i = bArr[position5];
            if (i >= 0) {
                tag = i;
                position2 = position6;
            } else {
                int position7 = ArrayDecoders.decodeVarint32(i, bArr, position6, registers2);
                tag = registers2.int1;
                position2 = position7;
            }
            int number = tag >>> 3;
            int wireType = tag & 7;
            if (number > oldNumber) {
                pos = messageSchema.positionForFieldNumber(number, pos3 / 3);
            } else {
                int pos4 = messageSchema.positionForFieldNumber(number);
                pos = pos4;
            }
            if (pos == -1) {
                currentPresenceFieldOffset = 0;
                unsafe = unsafe2;
            } else {
                int typeAndOffset3 = messageSchema.buffer[pos + 1];
                int fieldType = type(typeAndOffset3);
                int position8 = position2;
                long fieldOffset = offset(typeAndOffset3);
                if (fieldType <= 17) {
                    int presenceMaskAndOffset = messageSchema.buffer[pos + 2];
                    int presenceMask = 1 << (presenceMaskAndOffset >>> 20);
                    int presenceFieldOffset2 = presenceMaskAndOffset & 1048575;
                    if (presenceFieldOffset2 == currentPresenceFieldOffset4) {
                        typeAndOffset = typeAndOffset3;
                    } else {
                        if (currentPresenceFieldOffset4 == 1048575) {
                            typeAndOffset = typeAndOffset3;
                        } else {
                            typeAndOffset = typeAndOffset3;
                            unsafe2.putInt(t, currentPresenceFieldOffset4, currentPresenceField2);
                        }
                        if (presenceFieldOffset2 != 1048575) {
                            currentPresenceField2 = unsafe2.getInt(t, presenceFieldOffset2);
                        }
                        currentPresenceFieldOffset4 = presenceFieldOffset2;
                    }
                    switch (fieldType) {
                        case 0:
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            position3 = position8;
                            presenceFieldOffset = pos;
                            if (currentPresenceFieldOffset2 == 1) {
                                UnsafeUtil.putDouble(t, fieldOffset, ArrayDecoders.decodeDouble(bArr, position3));
                                position5 = position3 + 8;
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            } else {
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            }
                        case 1:
                            position3 = position8;
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            presenceFieldOffset = pos;
                            currentPresenceFieldOffset2 = wireType;
                            if (currentPresenceFieldOffset2 == 5) {
                                UnsafeUtil.putFloat(t, fieldOffset, ArrayDecoders.decodeFloat(bArr, position3));
                                position5 = position3 + 4;
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            } else {
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            }
                        case 2:
                        case 3:
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            position3 = position8;
                            presenceFieldOffset = pos;
                            if (currentPresenceFieldOffset2 == 0) {
                                int position9 = ArrayDecoders.decodeVarint64(bArr, position3, registers2);
                                unsafe2.putLong(message, fieldOffset, registers2.long1);
                                currentPresenceField2 |= presenceMask;
                                position5 = position9;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            } else {
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            }
                        case 4:
                        case 11:
                            position3 = position8;
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            presenceFieldOffset = pos;
                            currentPresenceFieldOffset2 = wireType;
                            if (currentPresenceFieldOffset2 == 0) {
                                position5 = ArrayDecoders.decodeVarint32(bArr, position3, registers2);
                                unsafe2.putInt(t, fieldOffset, registers2.int1);
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            } else {
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            }
                        case 5:
                        case 14:
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            position3 = position8;
                            presenceFieldOffset = pos;
                            if (currentPresenceFieldOffset2 == 1) {
                                unsafe2.putLong(message, fieldOffset, ArrayDecoders.decodeFixed64(bArr, position3));
                                position5 = position3 + 8;
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            } else {
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            }
                        case 6:
                        case 13:
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            position3 = position8;
                            presenceFieldOffset = pos;
                            if (currentPresenceFieldOffset2 == 5) {
                                unsafe2.putInt(t, fieldOffset, ArrayDecoders.decodeFixed32(bArr, position3));
                                position5 = position3 + 4;
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            } else {
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            }
                        case 7:
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            position3 = position8;
                            presenceFieldOffset = pos;
                            if (currentPresenceFieldOffset2 == 0) {
                                position5 = ArrayDecoders.decodeVarint64(bArr, position3, registers2);
                                UnsafeUtil.putBoolean(t, fieldOffset, registers2.long1 != 0);
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            } else {
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            }
                        case 8:
                            int typeAndOffset4 = typeAndOffset;
                            position3 = position8;
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            presenceFieldOffset = pos;
                            currentPresenceFieldOffset2 = wireType;
                            if (currentPresenceFieldOffset2 == 2) {
                                if ((typeAndOffset4 & 536870912) == 0) {
                                    position5 = ArrayDecoders.decodeString(bArr, position3, registers2);
                                } else {
                                    position5 = ArrayDecoders.decodeStringRequireUtf8(bArr, position3, registers2);
                                }
                                unsafe2.putObject(t, fieldOffset, registers2.object1);
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            } else {
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            }
                        case 9:
                            presenceFieldOffset = pos;
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            if (currentPresenceFieldOffset2 != 2) {
                                position3 = position8;
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            } else {
                                Object current = messageSchema.mutableMessageFieldForMerge(t, presenceFieldOffset);
                                position5 = ArrayDecoders.mergeMessageField(current, messageSchema.getMessageFieldSchema(presenceFieldOffset), data, position8, limit, registers);
                                messageSchema.storeMessageField(t, presenceFieldOffset, current);
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            }
                        case 10:
                            presenceFieldOffset = pos;
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            if (currentPresenceFieldOffset2 != 2) {
                                position3 = position8;
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            } else {
                                position5 = ArrayDecoders.decodeBytes(bArr, position8, registers2);
                                unsafe2.putObject(t, fieldOffset, registers2.object1);
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            }
                        case 12:
                            presenceFieldOffset = pos;
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            if (currentPresenceFieldOffset2 != 0) {
                                position3 = position8;
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            } else {
                                position5 = ArrayDecoders.decodeVarint32(bArr, position8, registers2);
                                unsafe2.putInt(t, fieldOffset, registers2.int1);
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            }
                        case 15:
                            presenceFieldOffset = pos;
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            if (currentPresenceFieldOffset2 != 0) {
                                position3 = position8;
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            } else {
                                position5 = ArrayDecoders.decodeVarint32(bArr, position8, registers2);
                                unsafe2.putInt(t, fieldOffset, CodedInputStream.decodeZigZag32(registers2.int1));
                                currentPresenceField2 |= presenceMask;
                                pos3 = presenceFieldOffset;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                pos2 = limit;
                                break;
                            }
                        case 16:
                            if (wireType != 0) {
                                presenceFieldOffset = pos;
                                typeAndOffset2 = currentPresenceFieldOffset4;
                                currentPresenceFieldOffset2 = wireType;
                                position3 = position8;
                                position2 = position3;
                                unsafe = unsafe2;
                                currentPresenceFieldOffset4 = typeAndOffset2;
                                currentPresenceFieldOffset = presenceFieldOffset;
                                break;
                            } else {
                                int position10 = ArrayDecoders.decodeVarint64(bArr, position8, registers2);
                                unsafe2.putLong(message, fieldOffset, CodedInputStream.decodeZigZag64(registers2.long1));
                                currentPresenceField2 |= presenceMask;
                                pos3 = pos;
                                oldNumber = number;
                                currentPresenceFieldOffset4 = currentPresenceFieldOffset4;
                                position5 = position10;
                                pos2 = limit;
                                break;
                            }
                        default:
                            typeAndOffset2 = currentPresenceFieldOffset4;
                            currentPresenceFieldOffset2 = wireType;
                            position3 = position8;
                            presenceFieldOffset = pos;
                            position2 = position3;
                            unsafe = unsafe2;
                            currentPresenceFieldOffset4 = typeAndOffset2;
                            currentPresenceFieldOffset = presenceFieldOffset;
                            break;
                    }
                } else {
                    int pos5 = pos;
                    int currentPresenceFieldOffset5 = currentPresenceFieldOffset4;
                    if (fieldType != 27) {
                        position4 = position8;
                        if (fieldType <= 49) {
                            int currentPresenceField3 = currentPresenceField2;
                            unsafe = unsafe2;
                            currentPresenceFieldOffset = pos5;
                            position5 = parseRepeatedField(message, data, position4, limit, tag, number, wireType, pos5, typeAndOffset3, fieldType, fieldOffset, registers);
                            if (position5 != position4) {
                                messageSchema = this;
                                t = message;
                                bArr = data;
                                pos2 = limit;
                                registers2 = registers;
                                oldNumber = number;
                                pos3 = currentPresenceFieldOffset;
                                currentPresenceField2 = currentPresenceField3;
                                currentPresenceFieldOffset4 = currentPresenceFieldOffset5;
                                unsafe2 = unsafe;
                            } else {
                                position2 = position5;
                                currentPresenceField2 = currentPresenceField3;
                                currentPresenceFieldOffset4 = currentPresenceFieldOffset5;
                            }
                        } else {
                            currentPresenceField = currentPresenceField2;
                            unsafe = unsafe2;
                            currentPresenceFieldOffset3 = currentPresenceFieldOffset5;
                            currentPresenceFieldOffset = pos5;
                            if (fieldType == 50) {
                                if (wireType == 2) {
                                    position5 = parseMapField(message, data, position4, limit, currentPresenceFieldOffset, fieldOffset, registers);
                                    if (position5 != position4) {
                                        messageSchema = this;
                                        t = message;
                                        bArr = data;
                                        pos2 = limit;
                                        registers2 = registers;
                                        oldNumber = number;
                                        pos3 = currentPresenceFieldOffset;
                                        currentPresenceField2 = currentPresenceField;
                                        currentPresenceFieldOffset4 = currentPresenceFieldOffset3;
                                        unsafe2 = unsafe;
                                    } else {
                                        position2 = position5;
                                        currentPresenceField2 = currentPresenceField;
                                        currentPresenceFieldOffset4 = currentPresenceFieldOffset3;
                                    }
                                } else {
                                    position2 = position4;
                                    currentPresenceField2 = currentPresenceField;
                                    currentPresenceFieldOffset4 = currentPresenceFieldOffset3;
                                }
                            } else {
                                position5 = parseOneofField(message, data, position4, limit, tag, number, wireType, typeAndOffset3, fieldType, fieldOffset, currentPresenceFieldOffset, registers);
                                if (position5 == position4) {
                                    position2 = position5;
                                    currentPresenceField2 = currentPresenceField;
                                    currentPresenceFieldOffset4 = currentPresenceFieldOffset3;
                                } else {
                                    messageSchema = this;
                                    t = message;
                                    bArr = data;
                                    pos2 = limit;
                                    registers2 = registers;
                                    oldNumber = number;
                                    pos3 = currentPresenceFieldOffset;
                                    currentPresenceField2 = currentPresenceField;
                                    currentPresenceFieldOffset4 = currentPresenceFieldOffset3;
                                    unsafe2 = unsafe;
                                }
                            }
                        }
                    } else if (wireType == 2) {
                        Internal.ProtobufList<?> list = (Internal.ProtobufList) unsafe2.getObject(t, fieldOffset);
                        if (!list.isModifiable()) {
                            int size = list.size();
                            list = list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
                            unsafe2.putObject(t, fieldOffset, list);
                        }
                        position5 = ArrayDecoders.decodeMessageList(messageSchema.getMessageFieldSchema(pos5), tag, data, position8, limit, list, registers);
                        pos3 = pos5;
                        oldNumber = number;
                        currentPresenceFieldOffset4 = currentPresenceFieldOffset5;
                        pos2 = limit;
                    } else {
                        position4 = position8;
                        currentPresenceField = currentPresenceField2;
                        unsafe = unsafe2;
                        currentPresenceFieldOffset3 = currentPresenceFieldOffset5;
                        currentPresenceFieldOffset = pos5;
                        position2 = position4;
                        currentPresenceField2 = currentPresenceField;
                        currentPresenceFieldOffset4 = currentPresenceFieldOffset3;
                    }
                }
            }
            position5 = ArrayDecoders.decodeUnknownField(tag, data, position2, limit, getMutableUnknownFields(message), registers);
            messageSchema = this;
            t = message;
            bArr = data;
            pos2 = limit;
            registers2 = registers;
            oldNumber = number;
            pos3 = currentPresenceFieldOffset;
            unsafe2 = unsafe;
        }
        int currentPresenceField4 = currentPresenceField2;
        Unsafe unsafe3 = unsafe2;
        if (currentPresenceFieldOffset4 != 1048575) {
            unsafe3.putInt(message, currentPresenceFieldOffset4, currentPresenceField4);
        }
        if (position5 != limit) {
            throw InvalidProtocolBufferException.parseFailure();
        }
        return position5;
    }

    @Override // com.android.framework.protobuf.Schema
    public void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
        if (this.proto3) {
            parseProto3Message(message, data, position, limit, registers);
        } else {
            parseProto2Message(message, data, position, limit, 0, registers);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.framework.protobuf.Schema
    public void makeImmutable(T message) {
        if (!isMutable(message)) {
            return;
        }
        if (message instanceof GeneratedMessageLite) {
            GeneratedMessageLite<?, ?> generatedMessage = (GeneratedMessageLite) message;
            generatedMessage.clearMemoizedSerializedSize();
            generatedMessage.clearMemoizedHashCode();
            generatedMessage.markImmutable();
        }
        int bufferLength = this.buffer.length;
        for (int pos = 0; pos < bufferLength; pos += 3) {
            int typeAndOffset = typeAndOffsetAt(pos);
            long offset = offset(typeAndOffset);
            switch (type(typeAndOffset)) {
                case 9:
                case 17:
                    if (isFieldPresent(message, pos)) {
                        getMessageFieldSchema(pos).makeImmutable(UNSAFE.getObject(message, offset));
                        break;
                    } else {
                        break;
                    }
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                    this.listFieldSchema.makeImmutableListAt(message, offset);
                    break;
                case 50:
                    Unsafe unsafe = UNSAFE;
                    Object mapField = unsafe.getObject(message, offset);
                    if (mapField != null) {
                        unsafe.putObject(message, offset, this.mapFieldSchema.toImmutable(mapField));
                        break;
                    } else {
                        break;
                    }
            }
        }
        this.unknownFieldSchema.makeImmutable(message);
        if (this.hasExtensions) {
            this.extensionSchema.makeImmutable(message);
        }
    }

    private final <K, V> void mergeMap(Object message, int pos, Object mapDefaultEntry, ExtensionRegistryLite extensionRegistry, Reader reader) throws IOException {
        long offset = offset(typeAndOffsetAt(pos));
        Object mapField = UnsafeUtil.getObject(message, offset);
        if (mapField == null) {
            mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
            UnsafeUtil.putObject(message, offset, mapField);
        } else if (this.mapFieldSchema.isImmutable(mapField)) {
            mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
            this.mapFieldSchema.mergeFrom(mapField, mapField);
            UnsafeUtil.putObject(message, offset, mapField);
        }
        reader.readMap(this.mapFieldSchema.forMutableMapData(mapField), this.mapFieldSchema.forMapMetadata(mapDefaultEntry), extensionRegistry);
    }

    private <UT, UB> UB filterMapUnknownEnumValues(Object message, int pos, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema, Object containerMessage) {
        int fieldNumber = numberAt(pos);
        long offset = offset(typeAndOffsetAt(pos));
        Object mapField = UnsafeUtil.getObject(message, offset);
        if (mapField == null) {
            return unknownFields;
        }
        Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(pos);
        if (enumVerifier == null) {
            return unknownFields;
        }
        return (UB) filterUnknownEnumMap(pos, fieldNumber, this.mapFieldSchema.forMutableMapData(mapField), enumVerifier, unknownFields, unknownFieldSchema, containerMessage);
    }

    private <K, V, UT, UB> UB filterUnknownEnumMap(int pos, int number, Map<K, V> mapData, Internal.EnumVerifier enumVerifier, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema, Object containerMessage) {
        MapEntryLite.Metadata<?, ?> forMapMetadata = this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(pos));
        Iterator<Map.Entry<K, V>> it = mapData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            if (!enumVerifier.isInRange(((Integer) entry.getValue()).intValue())) {
                if (unknownFields == null) {
                    UB unknownFields2 = unknownFieldSchema.getBuilderFromMessage(containerMessage);
                    unknownFields = unknownFields2;
                }
                int entrySize = MapEntryLite.computeSerializedSize(forMapMetadata, entry.getKey(), entry.getValue());
                ByteString.CodedBuilder codedBuilder = ByteString.newCodedBuilder(entrySize);
                CodedOutputStream codedOutput = codedBuilder.getCodedOutput();
                try {
                    MapEntryLite.writeTo(codedOutput, forMapMetadata, entry.getKey(), entry.getValue());
                    unknownFieldSchema.addLengthDelimited(unknownFields, number, codedBuilder.build());
                    it.remove();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return unknownFields;
    }

    @Override // com.android.framework.protobuf.Schema
    public final boolean isInitialized(T message) {
        int currentPresenceFieldOffset;
        int currentPresenceField;
        int currentPresenceFieldOffset2 = 1048575;
        int currentPresenceField2 = 0;
        int i = 0;
        while (i < this.checkInitializedCount) {
            int pos = this.intArray[i];
            int number = numberAt(pos);
            int typeAndOffset = typeAndOffsetAt(pos);
            int presenceMaskAndOffset = this.buffer[pos + 2];
            int presenceFieldOffset = presenceMaskAndOffset & 1048575;
            int presenceMask = 1 << (presenceMaskAndOffset >>> 20);
            if (presenceFieldOffset == currentPresenceFieldOffset2) {
                currentPresenceFieldOffset = currentPresenceFieldOffset2;
                currentPresenceField = currentPresenceField2;
            } else if (presenceFieldOffset == 1048575) {
                currentPresenceFieldOffset = presenceFieldOffset;
                currentPresenceField = currentPresenceField2;
            } else {
                int currentPresenceField3 = UNSAFE.getInt(message, presenceFieldOffset);
                currentPresenceFieldOffset = presenceFieldOffset;
                currentPresenceField = currentPresenceField3;
            }
            if (isRequired(typeAndOffset) && !isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                return false;
            }
            switch (type(typeAndOffset)) {
                case 9:
                case 17:
                    if (isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask) && !isInitialized(message, typeAndOffset, getMessageFieldSchema(pos))) {
                        return false;
                    }
                    break;
                case 27:
                case 49:
                    if (isListInitialized(message, typeAndOffset, pos)) {
                        break;
                    } else {
                        return false;
                    }
                case 50:
                    if (isMapInitialized(message, typeAndOffset, pos)) {
                        break;
                    } else {
                        return false;
                    }
                case 60:
                case 68:
                    if (isOneofPresent(message, number, pos) && !isInitialized(message, typeAndOffset, getMessageFieldSchema(pos))) {
                        return false;
                    }
                    break;
            }
            i++;
            currentPresenceFieldOffset2 = currentPresenceFieldOffset;
            currentPresenceField2 = currentPresenceField;
        }
        return !this.hasExtensions || this.extensionSchema.getExtensions(message).isInitialized();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static boolean isInitialized(Object message, int typeAndOffset, Schema schema) {
        Object nested = UnsafeUtil.getObject(message, offset(typeAndOffset));
        return schema.isInitialized(nested);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <N> boolean isListInitialized(Object message, int typeAndOffset, int pos) {
        List<N> list = (List) UnsafeUtil.getObject(message, offset(typeAndOffset));
        if (list.isEmpty()) {
            return true;
        }
        Schema schema = getMessageFieldSchema(pos);
        for (int i = 0; i < list.size(); i++) {
            N nested = list.get(i);
            if (!schema.isInitialized(nested)) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v4, types: [com.android.framework.protobuf.Schema] */
    /* JADX WARN: Type inference failed for: r4v6 */
    private boolean isMapInitialized(T message, int typeAndOffset, int pos) {
        Map<?, ?> map = this.mapFieldSchema.forMapData(UnsafeUtil.getObject(message, offset(typeAndOffset)));
        if (map.isEmpty()) {
            return true;
        }
        Object mapDefaultEntry = getMapFieldDefaultEntry(pos);
        MapEntryLite.Metadata<?, ?> metadata = this.mapFieldSchema.forMapMetadata(mapDefaultEntry);
        if (metadata.valueType.getJavaType() != WireFormat.JavaType.MESSAGE) {
            return true;
        }
        Schema<T> schema = 0;
        for (Object nested : map.values()) {
            if (schema == null) {
                schema = Protobuf.getInstance().schemaFor((Class) nested.getClass());
            }
            boolean isInitialized = schema.isInitialized(nested);
            schema = schema;
            if (!isInitialized) {
                return false;
            }
        }
        return true;
    }

    private void writeString(int fieldNumber, Object value, Writer writer) throws IOException {
        if (value instanceof String) {
            writer.writeString(fieldNumber, (String) value);
        } else {
            writer.writeBytes(fieldNumber, (ByteString) value);
        }
    }

    private void readString(Object message, int typeAndOffset, Reader reader) throws IOException {
        if (isEnforceUtf8(typeAndOffset)) {
            UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readStringRequireUtf8());
        } else if (this.lite) {
            UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readString());
        } else {
            UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readBytes());
        }
    }

    private void readStringList(Object message, int typeAndOffset, Reader reader) throws IOException {
        if (isEnforceUtf8(typeAndOffset)) {
            reader.readStringListRequireUtf8(this.listFieldSchema.mutableListAt(message, offset(typeAndOffset)));
        } else {
            reader.readStringList(this.listFieldSchema.mutableListAt(message, offset(typeAndOffset)));
        }
    }

    private <E> void readMessageList(Object message, int typeAndOffset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        long offset = offset(typeAndOffset);
        reader.readMessageList(this.listFieldSchema.mutableListAt(message, offset), schema, extensionRegistry);
    }

    private <E> void readGroupList(Object message, long offset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        reader.readGroupList(this.listFieldSchema.mutableListAt(message, offset), schema, extensionRegistry);
    }

    private int numberAt(int pos) {
        return this.buffer[pos];
    }

    private int typeAndOffsetAt(int pos) {
        return this.buffer[pos + 1];
    }

    private int presenceMaskAndOffsetAt(int pos) {
        return this.buffer[pos + 2];
    }

    private static int type(int value) {
        return (FIELD_TYPE_MASK & value) >>> 20;
    }

    private static boolean isRequired(int value) {
        return (268435456 & value) != 0;
    }

    private static boolean isEnforceUtf8(int value) {
        return (536870912 & value) != 0;
    }

    private static long offset(int value) {
        return 1048575 & value;
    }

    private static boolean isMutable(Object message) {
        if (message == null) {
            return false;
        }
        if (message instanceof GeneratedMessageLite) {
            return ((GeneratedMessageLite) message).isMutable();
        }
        return true;
    }

    private static void checkMutable(Object message) {
        if (!isMutable(message)) {
            throw new IllegalArgumentException("Mutating immutable message: " + message);
        }
    }

    private static <T> double doubleAt(T message, long offset) {
        return UnsafeUtil.getDouble(message, offset);
    }

    private static <T> float floatAt(T message, long offset) {
        return UnsafeUtil.getFloat(message, offset);
    }

    private static <T> int intAt(T message, long offset) {
        return UnsafeUtil.getInt(message, offset);
    }

    private static <T> long longAt(T message, long offset) {
        return UnsafeUtil.getLong(message, offset);
    }

    private static <T> boolean booleanAt(T message, long offset) {
        return UnsafeUtil.getBoolean(message, offset);
    }

    private static <T> double oneofDoubleAt(T message, long offset) {
        return ((Double) UnsafeUtil.getObject(message, offset)).doubleValue();
    }

    private static <T> float oneofFloatAt(T message, long offset) {
        return ((Float) UnsafeUtil.getObject(message, offset)).floatValue();
    }

    private static <T> int oneofIntAt(T message, long offset) {
        return ((Integer) UnsafeUtil.getObject(message, offset)).intValue();
    }

    private static <T> long oneofLongAt(T message, long offset) {
        return ((Long) UnsafeUtil.getObject(message, offset)).longValue();
    }

    private static <T> boolean oneofBooleanAt(T message, long offset) {
        return ((Boolean) UnsafeUtil.getObject(message, offset)).booleanValue();
    }

    private boolean arePresentForEquals(T message, T other, int pos) {
        return isFieldPresent(message, pos) == isFieldPresent(other, pos);
    }

    private boolean isFieldPresent(T message, int pos, int presenceFieldOffset, int presenceField, int presenceMask) {
        if (presenceFieldOffset == 1048575) {
            return isFieldPresent(message, pos);
        }
        return (presenceField & presenceMask) != 0;
    }

    private boolean isFieldPresent(T message, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        long presenceFieldOffset = presenceMaskAndOffset & 1048575;
        if (presenceFieldOffset == 1048575) {
            int typeAndOffset = typeAndOffsetAt(pos);
            long offset = offset(typeAndOffset);
            switch (type(typeAndOffset)) {
                case 0:
                    return Double.doubleToRawLongBits(UnsafeUtil.getDouble(message, offset)) != 0;
                case 1:
                    return Float.floatToRawIntBits(UnsafeUtil.getFloat(message, offset)) != 0;
                case 2:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 3:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 4:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 5:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 6:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 7:
                    return UnsafeUtil.getBoolean(message, offset);
                case 8:
                    Object value = UnsafeUtil.getObject(message, offset);
                    if (value instanceof String) {
                        return !((String) value).isEmpty();
                    }
                    if (value instanceof ByteString) {
                        return !ByteString.EMPTY.equals(value);
                    }
                    throw new IllegalArgumentException();
                case 9:
                    return UnsafeUtil.getObject(message, offset) != null;
                case 10:
                    return !ByteString.EMPTY.equals(UnsafeUtil.getObject(message, offset));
                case 11:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 12:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 13:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 14:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 15:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 16:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 17:
                    return UnsafeUtil.getObject(message, offset) != null;
                default:
                    throw new IllegalArgumentException();
            }
        }
        int presenceMask = 1 << (presenceMaskAndOffset >>> 20);
        return (UnsafeUtil.getInt(message, (long) (1048575 & presenceMaskAndOffset)) & presenceMask) != 0;
    }

    private void setFieldPresent(T message, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        long presenceFieldOffset = 1048575 & presenceMaskAndOffset;
        if (presenceFieldOffset == 1048575) {
            return;
        }
        int presenceMask = 1 << (presenceMaskAndOffset >>> 20);
        UnsafeUtil.putInt(message, presenceFieldOffset, UnsafeUtil.getInt(message, presenceFieldOffset) | presenceMask);
    }

    private boolean isOneofPresent(T message, int fieldNumber, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        return UnsafeUtil.getInt(message, (long) (1048575 & presenceMaskAndOffset)) == fieldNumber;
    }

    private boolean isOneofCaseEqual(T message, T other, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        return UnsafeUtil.getInt(message, (long) (presenceMaskAndOffset & 1048575)) == UnsafeUtil.getInt(other, (long) (1048575 & presenceMaskAndOffset));
    }

    private void setOneofPresent(T message, int fieldNumber, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        UnsafeUtil.putInt(message, 1048575 & presenceMaskAndOffset, fieldNumber);
    }

    private int positionForFieldNumber(int number) {
        if (number >= this.minFieldNumber && number <= this.maxFieldNumber) {
            return slowPositionForFieldNumber(number, 0);
        }
        return -1;
    }

    private int positionForFieldNumber(int number, int min) {
        if (number >= this.minFieldNumber && number <= this.maxFieldNumber) {
            return slowPositionForFieldNumber(number, min);
        }
        return -1;
    }

    private int slowPositionForFieldNumber(int number, int min) {
        int max = (this.buffer.length / 3) - 1;
        while (min <= max) {
            int mid = (max + min) >>> 1;
            int pos = mid * 3;
            int midFieldNumber = numberAt(pos);
            if (number == midFieldNumber) {
                return pos;
            }
            if (number < midFieldNumber) {
                max = mid - 1;
            } else {
                min = mid + 1;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSchemaSize() {
        return this.buffer.length * 3;
    }
}
