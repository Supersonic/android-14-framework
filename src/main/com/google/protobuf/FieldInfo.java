package com.google.protobuf;

import com.google.protobuf.Internal;
import java.lang.reflect.Field;
@CheckReturnValue
/* loaded from: classes.dex */
final class FieldInfo implements Comparable<FieldInfo> {
    private final Field cachedSizeField;
    private final boolean enforceUtf8;
    private final Internal.EnumVerifier enumVerifier;
    private final Field field;
    private final int fieldNumber;
    private final Object mapDefaultEntry;
    private final Class<?> messageClass;
    private final OneofInfo oneof;
    private final Class<?> oneofStoredType;
    private final Field presenceField;
    private final int presenceMask;
    private final boolean required;
    private final FieldType type;

    public static FieldInfo forField(Field field, int fieldNumber, FieldType fieldType, boolean enforceUtf8) {
        checkFieldNumber(fieldNumber);
        Internal.checkNotNull(field, "field");
        Internal.checkNotNull(fieldType, "fieldType");
        if (fieldType == FieldType.MESSAGE_LIST || fieldType == FieldType.GROUP_LIST) {
            throw new IllegalStateException("Shouldn't be called for repeated message fields.");
        }
        return new FieldInfo(field, fieldNumber, fieldType, null, null, 0, false, enforceUtf8, null, null, null, null, null);
    }

    public static FieldInfo forPackedField(Field field, int fieldNumber, FieldType fieldType, Field cachedSizeField) {
        checkFieldNumber(fieldNumber);
        Internal.checkNotNull(field, "field");
        Internal.checkNotNull(fieldType, "fieldType");
        if (fieldType == FieldType.MESSAGE_LIST || fieldType == FieldType.GROUP_LIST) {
            throw new IllegalStateException("Shouldn't be called for repeated message fields.");
        }
        return new FieldInfo(field, fieldNumber, fieldType, null, null, 0, false, false, null, null, null, null, cachedSizeField);
    }

    public static FieldInfo forRepeatedMessageField(Field field, int fieldNumber, FieldType fieldType, Class<?> messageClass) {
        checkFieldNumber(fieldNumber);
        Internal.checkNotNull(field, "field");
        Internal.checkNotNull(fieldType, "fieldType");
        Internal.checkNotNull(messageClass, "messageClass");
        return new FieldInfo(field, fieldNumber, fieldType, messageClass, null, 0, false, false, null, null, null, null, null);
    }

    public static FieldInfo forFieldWithEnumVerifier(Field field, int fieldNumber, FieldType fieldType, Internal.EnumVerifier enumVerifier) {
        checkFieldNumber(fieldNumber);
        Internal.checkNotNull(field, "field");
        return new FieldInfo(field, fieldNumber, fieldType, null, null, 0, false, false, null, null, null, enumVerifier, null);
    }

    public static FieldInfo forPackedFieldWithEnumVerifier(Field field, int fieldNumber, FieldType fieldType, Internal.EnumVerifier enumVerifier, Field cachedSizeField) {
        checkFieldNumber(fieldNumber);
        Internal.checkNotNull(field, "field");
        return new FieldInfo(field, fieldNumber, fieldType, null, null, 0, false, false, null, null, null, enumVerifier, cachedSizeField);
    }

    public static FieldInfo forProto2OptionalField(Field field, int fieldNumber, FieldType fieldType, Field presenceField, int presenceMask, boolean enforceUtf8, Internal.EnumVerifier enumVerifier) {
        checkFieldNumber(fieldNumber);
        Internal.checkNotNull(field, "field");
        Internal.checkNotNull(fieldType, "fieldType");
        Internal.checkNotNull(presenceField, "presenceField");
        if (presenceField != null && !isExactlyOneBitSet(presenceMask)) {
            throw new IllegalArgumentException("presenceMask must have exactly one bit set: " + presenceMask);
        }
        return new FieldInfo(field, fieldNumber, fieldType, null, presenceField, presenceMask, false, enforceUtf8, null, null, null, enumVerifier, null);
    }

    public static FieldInfo forOneofMemberField(int fieldNumber, FieldType fieldType, OneofInfo oneof, Class<?> oneofStoredType, boolean enforceUtf8, Internal.EnumVerifier enumVerifier) {
        checkFieldNumber(fieldNumber);
        Internal.checkNotNull(fieldType, "fieldType");
        Internal.checkNotNull(oneof, "oneof");
        Internal.checkNotNull(oneofStoredType, "oneofStoredType");
        if (!fieldType.isScalar()) {
            throw new IllegalArgumentException("Oneof is only supported for scalar fields. Field " + fieldNumber + " is of type " + fieldType);
        }
        return new FieldInfo(null, fieldNumber, fieldType, null, null, 0, false, enforceUtf8, oneof, oneofStoredType, null, enumVerifier, null);
    }

    private static void checkFieldNumber(int fieldNumber) {
        if (fieldNumber <= 0) {
            throw new IllegalArgumentException("fieldNumber must be positive: " + fieldNumber);
        }
    }

    public static FieldInfo forProto2RequiredField(Field field, int fieldNumber, FieldType fieldType, Field presenceField, int presenceMask, boolean enforceUtf8, Internal.EnumVerifier enumVerifier) {
        checkFieldNumber(fieldNumber);
        Internal.checkNotNull(field, "field");
        Internal.checkNotNull(fieldType, "fieldType");
        Internal.checkNotNull(presenceField, "presenceField");
        if (presenceField != null && !isExactlyOneBitSet(presenceMask)) {
            throw new IllegalArgumentException("presenceMask must have exactly one bit set: " + presenceMask);
        }
        return new FieldInfo(field, fieldNumber, fieldType, null, presenceField, presenceMask, true, enforceUtf8, null, null, null, enumVerifier, null);
    }

    public static FieldInfo forMapField(Field field, int fieldNumber, Object mapDefaultEntry, Internal.EnumVerifier enumVerifier) {
        Internal.checkNotNull(mapDefaultEntry, "mapDefaultEntry");
        checkFieldNumber(fieldNumber);
        Internal.checkNotNull(field, "field");
        return new FieldInfo(field, fieldNumber, FieldType.MAP, null, null, 0, false, true, null, null, mapDefaultEntry, enumVerifier, null);
    }

    private FieldInfo(Field field, int fieldNumber, FieldType type, Class<?> messageClass, Field presenceField, int presenceMask, boolean required, boolean enforceUtf8, OneofInfo oneof, Class<?> oneofStoredType, Object mapDefaultEntry, Internal.EnumVerifier enumVerifier, Field cachedSizeField) {
        this.field = field;
        this.type = type;
        this.messageClass = messageClass;
        this.fieldNumber = fieldNumber;
        this.presenceField = presenceField;
        this.presenceMask = presenceMask;
        this.required = required;
        this.enforceUtf8 = enforceUtf8;
        this.oneof = oneof;
        this.oneofStoredType = oneofStoredType;
        this.mapDefaultEntry = mapDefaultEntry;
        this.enumVerifier = enumVerifier;
        this.cachedSizeField = cachedSizeField;
    }

    public int getFieldNumber() {
        return this.fieldNumber;
    }

    public Field getField() {
        return this.field;
    }

    public FieldType getType() {
        return this.type;
    }

    public OneofInfo getOneof() {
        return this.oneof;
    }

    public Class<?> getOneofStoredType() {
        return this.oneofStoredType;
    }

    public Internal.EnumVerifier getEnumVerifier() {
        return this.enumVerifier;
    }

    @Override // java.lang.Comparable
    public int compareTo(FieldInfo o) {
        return this.fieldNumber - o.fieldNumber;
    }

    public Class<?> getListElementType() {
        return this.messageClass;
    }

    public Field getPresenceField() {
        return this.presenceField;
    }

    public Object getMapDefaultEntry() {
        return this.mapDefaultEntry;
    }

    public int getPresenceMask() {
        return this.presenceMask;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isEnforceUtf8() {
        return this.enforceUtf8;
    }

    public Field getCachedSizeField() {
        return this.cachedSizeField;
    }

    /* renamed from: com.google.protobuf.FieldInfo$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C00151 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$FieldType;

        static {
            int[] iArr = new int[FieldType.values().length];
            $SwitchMap$com$google$protobuf$FieldType = iArr;
            try {
                iArr[FieldType.MESSAGE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$FieldType[FieldType.GROUP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$FieldType[FieldType.MESSAGE_LIST.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$protobuf$FieldType[FieldType.GROUP_LIST.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public Class<?> getMessageFieldClass() {
        switch (C00151.$SwitchMap$com$google$protobuf$FieldType[this.type.ordinal()]) {
            case 1:
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                Field field = this.field;
                return field != null ? field.getType() : this.oneofStoredType;
            case WireFormat.WIRETYPE_START_GROUP /* 3 */:
            case 4:
                return this.messageClass;
            default:
                return null;
        }
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private Field cachedSizeField;
        private boolean enforceUtf8;
        private Internal.EnumVerifier enumVerifier;
        private Field field;
        private int fieldNumber;
        private Object mapDefaultEntry;
        private OneofInfo oneof;
        private Class<?> oneofStoredType;
        private Field presenceField;
        private int presenceMask;
        private boolean required;
        private FieldType type;

        /* synthetic */ Builder(C00151 x0) {
            this();
        }

        private Builder() {
        }

        public Builder withField(Field field) {
            if (this.oneof != null) {
                throw new IllegalStateException("Cannot set field when building a oneof.");
            }
            this.field = field;
            return this;
        }

        public Builder withType(FieldType type) {
            this.type = type;
            return this;
        }

        public Builder withFieldNumber(int fieldNumber) {
            this.fieldNumber = fieldNumber;
            return this;
        }

        public Builder withPresence(Field presenceField, int presenceMask) {
            this.presenceField = (Field) Internal.checkNotNull(presenceField, "presenceField");
            this.presenceMask = presenceMask;
            return this;
        }

        public Builder withOneof(OneofInfo oneof, Class<?> oneofStoredType) {
            if (this.field != null || this.presenceField != null) {
                throw new IllegalStateException("Cannot set oneof when field or presenceField have been provided");
            }
            this.oneof = oneof;
            this.oneofStoredType = oneofStoredType;
            return this;
        }

        public Builder withRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder withMapDefaultEntry(Object mapDefaultEntry) {
            this.mapDefaultEntry = mapDefaultEntry;
            return this;
        }

        public Builder withEnforceUtf8(boolean enforceUtf8) {
            this.enforceUtf8 = enforceUtf8;
            return this;
        }

        public Builder withEnumVerifier(Internal.EnumVerifier enumVerifier) {
            this.enumVerifier = enumVerifier;
            return this;
        }

        public Builder withCachedSizeField(Field cachedSizeField) {
            this.cachedSizeField = cachedSizeField;
            return this;
        }

        public FieldInfo build() {
            OneofInfo oneofInfo = this.oneof;
            if (oneofInfo != null) {
                return FieldInfo.forOneofMemberField(this.fieldNumber, this.type, oneofInfo, this.oneofStoredType, this.enforceUtf8, this.enumVerifier);
            }
            Object obj = this.mapDefaultEntry;
            if (obj != null) {
                return FieldInfo.forMapField(this.field, this.fieldNumber, obj, this.enumVerifier);
            }
            Field field = this.presenceField;
            if (field != null) {
                if (this.required) {
                    return FieldInfo.forProto2RequiredField(this.field, this.fieldNumber, this.type, field, this.presenceMask, this.enforceUtf8, this.enumVerifier);
                }
                return FieldInfo.forProto2OptionalField(this.field, this.fieldNumber, this.type, field, this.presenceMask, this.enforceUtf8, this.enumVerifier);
            }
            Internal.EnumVerifier enumVerifier = this.enumVerifier;
            if (enumVerifier != null) {
                Field field2 = this.cachedSizeField;
                if (field2 == null) {
                    return FieldInfo.forFieldWithEnumVerifier(this.field, this.fieldNumber, this.type, enumVerifier);
                }
                return FieldInfo.forPackedFieldWithEnumVerifier(this.field, this.fieldNumber, this.type, enumVerifier, field2);
            }
            Field field3 = this.cachedSizeField;
            if (field3 == null) {
                return FieldInfo.forField(this.field, this.fieldNumber, this.type, this.enforceUtf8);
            }
            return FieldInfo.forPackedField(this.field, this.fieldNumber, this.type, field3);
        }
    }

    private static boolean isExactlyOneBitSet(int value) {
        return value != 0 && ((value + (-1)) & value) == 0;
    }
}
