package com.android.framework.protobuf;

import com.android.framework.protobuf.FieldSet.FieldDescriptorLite;
import com.android.framework.protobuf.Internal;
import com.android.framework.protobuf.LazyField;
import com.android.framework.protobuf.MessageLite;
import com.android.framework.protobuf.WireFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public final class FieldSet<T extends FieldDescriptorLite<T>> {
    private static final int DEFAULT_FIELD_MAP_ARRAY_SIZE = 16;
    private static final FieldSet DEFAULT_INSTANCE = new FieldSet(true);
    private final SmallSortedMap<T, Object> fields;
    private boolean hasLazyField;
    private boolean isImmutable;

    /* loaded from: classes4.dex */
    public interface FieldDescriptorLite<T extends FieldDescriptorLite<T>> extends Comparable<T> {
        Internal.EnumLiteMap<?> getEnumType();

        WireFormat.JavaType getLiteJavaType();

        WireFormat.FieldType getLiteType();

        int getNumber();

        MessageLite.Builder internalMergeFrom(MessageLite.Builder builder, MessageLite messageLite);

        boolean isPacked();

        boolean isRepeated();
    }

    /* synthetic */ FieldSet(SmallSortedMap x0, C40071 x1) {
        this(x0);
    }

    private FieldSet() {
        this.fields = SmallSortedMap.newFieldMap(16);
    }

    private FieldSet(boolean dummy) {
        this(SmallSortedMap.newFieldMap(0));
        makeImmutable();
    }

    private FieldSet(SmallSortedMap<T, Object> fields) {
        this.fields = fields;
        makeImmutable();
    }

    public static <T extends FieldDescriptorLite<T>> FieldSet<T> newFieldSet() {
        return new FieldSet<>();
    }

    public static <T extends FieldDescriptorLite<T>> FieldSet<T> emptySet() {
        return DEFAULT_INSTANCE;
    }

    public static <T extends FieldDescriptorLite<T>> Builder<T> newBuilder() {
        return new Builder<>((C40071) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.fields.isEmpty();
    }

    public void makeImmutable() {
        if (this.isImmutable) {
            return;
        }
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            if (entry.getValue() instanceof GeneratedMessageLite) {
                ((GeneratedMessageLite) entry.getValue()).makeImmutable();
            }
        }
        this.fields.makeImmutable();
        this.isImmutable = true;
    }

    public boolean isImmutable() {
        return this.isImmutable;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldSet)) {
            return false;
        }
        FieldSet<?> other = (FieldSet) o;
        return this.fields.equals(other.fields);
    }

    public int hashCode() {
        return this.fields.hashCode();
    }

    /* renamed from: clone */
    public FieldSet<T> m6505clone() {
        FieldSet<T> clone = newFieldSet();
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            clone.setField(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<T, Object> entry2 : this.fields.getOverflowEntries()) {
            clone.setField(entry2.getKey(), entry2.getValue());
        }
        clone.hasLazyField = this.hasLazyField;
        return clone;
    }

    public void clear() {
        this.fields.clear();
        this.hasLazyField = false;
    }

    public Map<T, Object> getAllFields() {
        if (!this.hasLazyField) {
            return this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields);
        }
        SmallSortedMap<T, Object> result = cloneAllFieldsMap(this.fields, false);
        if (this.fields.isImmutable()) {
            result.makeImmutable();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T extends FieldDescriptorLite<T>> SmallSortedMap<T, Object> cloneAllFieldsMap(SmallSortedMap<T, Object> fields, boolean copyList) {
        SmallSortedMap<T, Object> result = SmallSortedMap.newFieldMap(16);
        for (int i = 0; i < fields.getNumArrayEntries(); i++) {
            cloneFieldEntry(result, fields.getArrayEntryAt(i), copyList);
        }
        for (Map.Entry<T, Object> entry : fields.getOverflowEntries()) {
            cloneFieldEntry(result, entry, copyList);
        }
        return result;
    }

    private static <T extends FieldDescriptorLite<T>> void cloneFieldEntry(Map<T, Object> map, Map.Entry<T, Object> entry, boolean copyList) {
        T key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof LazyField) {
            map.put(key, ((LazyField) value).getValue());
        } else if (copyList && (value instanceof List)) {
            map.put(key, new ArrayList((List) value));
        } else {
            map.put(key, value);
        }
    }

    public Iterator<Map.Entry<T, Object>> iterator() {
        if (this.hasLazyField) {
            return new LazyField.LazyIterator(this.fields.entrySet().iterator());
        }
        return this.fields.entrySet().iterator();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Iterator<Map.Entry<T, Object>> descendingIterator() {
        if (this.hasLazyField) {
            return new LazyField.LazyIterator(this.fields.descendingEntrySet().iterator());
        }
        return this.fields.descendingEntrySet().iterator();
    }

    public boolean hasField(T descriptor) {
        if (descriptor.isRepeated()) {
            throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
        }
        return this.fields.get(descriptor) != null;
    }

    public Object getField(T descriptor) {
        Object o = this.fields.get(descriptor);
        if (o instanceof LazyField) {
            return ((LazyField) o).getValue();
        }
        return o;
    }

    public void setField(T descriptor, Object value) {
        if (descriptor.isRepeated()) {
            if (!(value instanceof List)) {
                throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }
            List newList = new ArrayList();
            newList.addAll((List) value);
            for (Object element : newList) {
                verifyType(descriptor, element);
            }
            value = newList;
        } else {
            verifyType(descriptor, value);
        }
        if (value instanceof LazyField) {
            this.hasLazyField = true;
        }
        this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) value);
    }

    public void clearField(T descriptor) {
        this.fields.remove(descriptor);
        if (this.fields.isEmpty()) {
            this.hasLazyField = false;
        }
    }

    public int getRepeatedFieldCount(T descriptor) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object value = getField(descriptor);
        if (value == null) {
            return 0;
        }
        return ((List) value).size();
    }

    public Object getRepeatedField(T descriptor, int index) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object value = getField(descriptor);
        if (value == null) {
            throw new IndexOutOfBoundsException();
        }
        return ((List) value).get(index);
    }

    public void setRepeatedField(T descriptor, int index, Object value) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object list = getField(descriptor);
        if (list == null) {
            throw new IndexOutOfBoundsException();
        }
        verifyType(descriptor, value);
        ((List) list).set(index, value);
    }

    public void addRepeatedField(T descriptor, Object value) {
        List<Object> list;
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
        }
        verifyType(descriptor, value);
        Object existingValue = getField(descriptor);
        if (existingValue == null) {
            list = new ArrayList<>();
            this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) list);
        } else {
            list = (List) existingValue;
        }
        list.add(value);
    }

    private void verifyType(T descriptor, Object value) {
        if (!isValidType(descriptor.getLiteType(), value)) {
            throw new IllegalArgumentException(String.format("Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n", Integer.valueOf(descriptor.getNumber()), descriptor.getLiteType().getJavaType(), value.getClass().getName()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isValidType(WireFormat.FieldType type, Object value) {
        Internal.checkNotNull(value);
        switch (C40071.$SwitchMap$com$google$protobuf$WireFormat$JavaType[type.getJavaType().ordinal()]) {
            case 1:
                return value instanceof Integer;
            case 2:
                return value instanceof Long;
            case 3:
                return value instanceof Float;
            case 4:
                return value instanceof Double;
            case 5:
                return value instanceof Boolean;
            case 6:
                return value instanceof String;
            case 7:
                return (value instanceof ByteString) || (value instanceof byte[]);
            case 8:
                return (value instanceof Integer) || (value instanceof Internal.EnumLite);
            case 9:
                return (value instanceof MessageLite) || (value instanceof LazyField);
            default:
                return false;
        }
    }

    public boolean isInitialized() {
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            if (!isInitialized(this.fields.getArrayEntryAt(i))) {
                return false;
            }
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            if (!isInitialized(entry)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T extends FieldDescriptorLite<T>> boolean isInitialized(Map.Entry<T, Object> entry) {
        T descriptor = entry.getKey();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            if (descriptor.isRepeated()) {
                for (Object element : (List) entry.getValue()) {
                    if (!isMessageFieldValueInitialized(element)) {
                        return false;
                    }
                }
                return true;
            }
            return isMessageFieldValueInitialized(entry.getValue());
        }
        return true;
    }

    private static boolean isMessageFieldValueInitialized(Object value) {
        if (value instanceof MessageLiteOrBuilder) {
            return ((MessageLiteOrBuilder) value).isInitialized();
        }
        if (value instanceof LazyField) {
            return true;
        }
        throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getWireFormatForFieldType(WireFormat.FieldType type, boolean isPacked) {
        if (isPacked) {
            return 2;
        }
        return type.getWireType();
    }

    public void mergeFrom(FieldSet<T> other) {
        for (int i = 0; i < other.fields.getNumArrayEntries(); i++) {
            mergeFromField(other.fields.getArrayEntryAt(i));
        }
        for (Map.Entry<T, Object> entry : other.fields.getOverflowEntries()) {
            mergeFromField(entry);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object cloneIfMutable(Object value) {
        if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            byte[] copy = new byte[bytes.length];
            System.arraycopy(bytes, 0, copy, 0, bytes.length);
            return copy;
        }
        return value;
    }

    private void mergeFromField(Map.Entry<T, Object> entry) {
        T descriptor = entry.getKey();
        Object otherValue = entry.getValue();
        if (otherValue instanceof LazyField) {
            otherValue = ((LazyField) otherValue).getValue();
        }
        if (descriptor.isRepeated()) {
            Object value = getField(descriptor);
            if (value == null) {
                value = new ArrayList();
            }
            for (Object element : (List) otherValue) {
                ((List) value).add(cloneIfMutable(element));
            }
            this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) value);
        } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            Object value2 = getField(descriptor);
            if (value2 == null) {
                this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) cloneIfMutable(otherValue));
                return;
            }
            this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) descriptor.internalMergeFrom(((MessageLite) value2).toBuilder(), (MessageLite) otherValue).build());
        } else {
            this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) cloneIfMutable(otherValue));
        }
    }

    public static Object readPrimitiveField(CodedInputStream input, WireFormat.FieldType type, boolean checkUtf8) throws IOException {
        if (checkUtf8) {
            return WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.STRICT);
        }
        return WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.LOOSE);
    }

    public void writeTo(CodedOutputStream output) throws IOException {
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            writeField(entry.getKey(), entry.getValue(), output);
        }
        for (Map.Entry<T, Object> entry2 : this.fields.getOverflowEntries()) {
            writeField(entry2.getKey(), entry2.getValue(), output);
        }
    }

    public void writeMessageSetTo(CodedOutputStream output) throws IOException {
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            writeMessageSetTo(this.fields.getArrayEntryAt(i), output);
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            writeMessageSetTo(entry, output);
        }
    }

    private void writeMessageSetTo(Map.Entry<T, Object> entry, CodedOutputStream output) throws IOException {
        T descriptor = entry.getKey();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
            Object value = entry.getValue();
            if (value instanceof LazyField) {
                value = ((LazyField) value).getValue();
            }
            output.writeMessageSetExtension(entry.getKey().getNumber(), (MessageLite) value);
            return;
        }
        writeField(descriptor, entry.getValue(), output);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeElement(CodedOutputStream output, WireFormat.FieldType type, int number, Object value) throws IOException {
        if (type == WireFormat.FieldType.GROUP) {
            output.writeGroup(number, (MessageLite) value);
            return;
        }
        output.writeTag(number, getWireFormatForFieldType(type, false));
        writeElementNoTag(output, type, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.framework.protobuf.FieldSet$1 */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class C40071 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$FieldType;
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$JavaType;

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
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED64.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED32.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BOOL.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.GROUP.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.MESSAGE.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.STRING.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BYTES.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT32.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED32.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED64.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT32.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT64.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.ENUM.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            int[] iArr2 = new int[WireFormat.JavaType.values().length];
            $SwitchMap$com$google$protobuf$WireFormat$JavaType = iArr2;
            try {
                iArr2[WireFormat.JavaType.INT.ordinal()] = 1;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.LONG.ordinal()] = 2;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.FLOAT.ordinal()] = 3;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.DOUBLE.ordinal()] = 4;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.BOOLEAN.ordinal()] = 5;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.STRING.ordinal()] = 6;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.BYTE_STRING.ordinal()] = 7;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.ENUM.ordinal()] = 8;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.MESSAGE.ordinal()] = 9;
            } catch (NoSuchFieldError e27) {
            }
        }
    }

    static void writeElementNoTag(CodedOutputStream output, WireFormat.FieldType type, Object value) throws IOException {
        switch (C40071.$SwitchMap$com$google$protobuf$WireFormat$FieldType[type.ordinal()]) {
            case 1:
                output.writeDoubleNoTag(((Double) value).doubleValue());
                return;
            case 2:
                output.writeFloatNoTag(((Float) value).floatValue());
                return;
            case 3:
                output.writeInt64NoTag(((Long) value).longValue());
                return;
            case 4:
                output.writeUInt64NoTag(((Long) value).longValue());
                return;
            case 5:
                output.writeInt32NoTag(((Integer) value).intValue());
                return;
            case 6:
                output.writeFixed64NoTag(((Long) value).longValue());
                return;
            case 7:
                output.writeFixed32NoTag(((Integer) value).intValue());
                return;
            case 8:
                output.writeBoolNoTag(((Boolean) value).booleanValue());
                return;
            case 9:
                output.writeGroupNoTag((MessageLite) value);
                return;
            case 10:
                output.writeMessageNoTag((MessageLite) value);
                return;
            case 11:
                if (value instanceof ByteString) {
                    output.writeBytesNoTag((ByteString) value);
                    return;
                } else {
                    output.writeStringNoTag((String) value);
                    return;
                }
            case 12:
                if (value instanceof ByteString) {
                    output.writeBytesNoTag((ByteString) value);
                    return;
                } else {
                    output.writeByteArrayNoTag((byte[]) value);
                    return;
                }
            case 13:
                output.writeUInt32NoTag(((Integer) value).intValue());
                return;
            case 14:
                output.writeSFixed32NoTag(((Integer) value).intValue());
                return;
            case 15:
                output.writeSFixed64NoTag(((Long) value).longValue());
                return;
            case 16:
                output.writeSInt32NoTag(((Integer) value).intValue());
                return;
            case 17:
                output.writeSInt64NoTag(((Long) value).longValue());
                return;
            case 18:
                if (value instanceof Internal.EnumLite) {
                    output.writeEnumNoTag(((Internal.EnumLite) value).getNumber());
                    return;
                } else {
                    output.writeEnumNoTag(((Integer) value).intValue());
                    return;
                }
            default:
                return;
        }
    }

    public static void writeField(FieldDescriptorLite<?> descriptor, Object value, CodedOutputStream output) throws IOException {
        WireFormat.FieldType type = descriptor.getLiteType();
        int number = descriptor.getNumber();
        if (descriptor.isRepeated()) {
            List<?> valueList = (List) value;
            if (descriptor.isPacked()) {
                output.writeTag(number, 2);
                int dataSize = 0;
                for (Object element : valueList) {
                    dataSize += computeElementSizeNoTag(type, element);
                }
                output.writeUInt32NoTag(dataSize);
                for (Object element2 : valueList) {
                    writeElementNoTag(output, type, element2);
                }
                return;
            }
            for (Object element3 : valueList) {
                writeElement(output, type, number, element3);
            }
        } else if (value instanceof LazyField) {
            writeElement(output, type, number, ((LazyField) value).getValue());
        } else {
            writeElement(output, type, number, value);
        }
    }

    public int getSerializedSize() {
        int size = 0;
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            size += computeFieldSize(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<T, Object> entry2 : this.fields.getOverflowEntries()) {
            size += computeFieldSize(entry2.getKey(), entry2.getValue());
        }
        return size;
    }

    public int getMessageSetSerializedSize() {
        int size = 0;
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            size += getMessageSetSerializedSize(this.fields.getArrayEntryAt(i));
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            size += getMessageSetSerializedSize(entry);
        }
        return size;
    }

    private int getMessageSetSerializedSize(Map.Entry<T, Object> entry) {
        T descriptor = entry.getKey();
        Object value = entry.getValue();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
            if (value instanceof LazyField) {
                return CodedOutputStream.computeLazyFieldMessageSetExtensionSize(entry.getKey().getNumber(), (LazyField) value);
            }
            return CodedOutputStream.computeMessageSetExtensionSize(entry.getKey().getNumber(), (MessageLite) value);
        }
        return computeFieldSize(descriptor, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeElementSize(WireFormat.FieldType type, int number, Object value) {
        int tagSize = CodedOutputStream.computeTagSize(number);
        if (type == WireFormat.FieldType.GROUP) {
            tagSize *= 2;
        }
        return computeElementSizeNoTag(type, value) + tagSize;
    }

    static int computeElementSizeNoTag(WireFormat.FieldType type, Object value) {
        switch (C40071.$SwitchMap$com$google$protobuf$WireFormat$FieldType[type.ordinal()]) {
            case 1:
                return CodedOutputStream.computeDoubleSizeNoTag(((Double) value).doubleValue());
            case 2:
                return CodedOutputStream.computeFloatSizeNoTag(((Float) value).floatValue());
            case 3:
                return CodedOutputStream.computeInt64SizeNoTag(((Long) value).longValue());
            case 4:
                return CodedOutputStream.computeUInt64SizeNoTag(((Long) value).longValue());
            case 5:
                return CodedOutputStream.computeInt32SizeNoTag(((Integer) value).intValue());
            case 6:
                return CodedOutputStream.computeFixed64SizeNoTag(((Long) value).longValue());
            case 7:
                return CodedOutputStream.computeFixed32SizeNoTag(((Integer) value).intValue());
            case 8:
                return CodedOutputStream.computeBoolSizeNoTag(((Boolean) value).booleanValue());
            case 9:
                return CodedOutputStream.computeGroupSizeNoTag((MessageLite) value);
            case 10:
                if (value instanceof LazyField) {
                    return CodedOutputStream.computeLazyFieldSizeNoTag((LazyField) value);
                }
                return CodedOutputStream.computeMessageSizeNoTag((MessageLite) value);
            case 11:
                if (value instanceof ByteString) {
                    return CodedOutputStream.computeBytesSizeNoTag((ByteString) value);
                }
                return CodedOutputStream.computeStringSizeNoTag((String) value);
            case 12:
                if (value instanceof ByteString) {
                    return CodedOutputStream.computeBytesSizeNoTag((ByteString) value);
                }
                return CodedOutputStream.computeByteArraySizeNoTag((byte[]) value);
            case 13:
                return CodedOutputStream.computeUInt32SizeNoTag(((Integer) value).intValue());
            case 14:
                return CodedOutputStream.computeSFixed32SizeNoTag(((Integer) value).intValue());
            case 15:
                return CodedOutputStream.computeSFixed64SizeNoTag(((Long) value).longValue());
            case 16:
                return CodedOutputStream.computeSInt32SizeNoTag(((Integer) value).intValue());
            case 17:
                return CodedOutputStream.computeSInt64SizeNoTag(((Long) value).longValue());
            case 18:
                if (value instanceof Internal.EnumLite) {
                    return CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite) value).getNumber());
                }
                return CodedOutputStream.computeEnumSizeNoTag(((Integer) value).intValue());
            default:
                throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
        }
    }

    public static int computeFieldSize(FieldDescriptorLite<?> descriptor, Object value) {
        WireFormat.FieldType type = descriptor.getLiteType();
        int number = descriptor.getNumber();
        if (descriptor.isRepeated()) {
            if (descriptor.isPacked()) {
                int dataSize = 0;
                for (Object element : (List) value) {
                    dataSize += computeElementSizeNoTag(type, element);
                }
                return CodedOutputStream.computeTagSize(number) + dataSize + CodedOutputStream.computeUInt32SizeNoTag(dataSize);
            }
            int size = 0;
            for (Object element2 : (List) value) {
                size += computeElementSize(type, number, element2);
            }
            return size;
        }
        int size2 = computeElementSize(type, number, value);
        return size2;
    }

    /* loaded from: classes4.dex */
    static final class Builder<T extends FieldDescriptorLite<T>> {
        private SmallSortedMap<T, Object> fields;
        private boolean hasLazyField;
        private boolean hasNestedBuilders;
        private boolean isMutable;

        /* synthetic */ Builder(C40071 x0) {
            this();
        }

        private Builder() {
            this(SmallSortedMap.newFieldMap(16));
        }

        private Builder(SmallSortedMap<T, Object> fields) {
            this.fields = fields;
            this.isMutable = true;
        }

        public FieldSet<T> build() {
            return buildImpl(false);
        }

        public FieldSet<T> buildPartial() {
            return buildImpl(true);
        }

        private FieldSet<T> buildImpl(boolean partial) {
            if (this.fields.isEmpty()) {
                return FieldSet.emptySet();
            }
            this.isMutable = false;
            SmallSortedMap<T, Object> fieldsForBuild = this.fields;
            if (this.hasNestedBuilders) {
                fieldsForBuild = FieldSet.cloneAllFieldsMap(this.fields, false);
                replaceBuilders(fieldsForBuild, partial);
            }
            FieldSet<T> fieldSet = new FieldSet<>(fieldsForBuild, null);
            ((FieldSet) fieldSet).hasLazyField = this.hasLazyField;
            return fieldSet;
        }

        private static <T extends FieldDescriptorLite<T>> void replaceBuilders(SmallSortedMap<T, Object> fieldMap, boolean partial) {
            for (int i = 0; i < fieldMap.getNumArrayEntries(); i++) {
                replaceBuilders(fieldMap.getArrayEntryAt(i), partial);
            }
            for (Map.Entry<T, Object> entry : fieldMap.getOverflowEntries()) {
                replaceBuilders(entry, partial);
            }
        }

        private static <T extends FieldDescriptorLite<T>> void replaceBuilders(Map.Entry<T, Object> entry, boolean partial) {
            entry.setValue(replaceBuilders(entry.getKey(), entry.getValue(), partial));
        }

        private static <T extends FieldDescriptorLite<T>> Object replaceBuilders(T descriptor, Object value, boolean partial) {
            if (value == null) {
                return value;
            }
            if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
                if (descriptor.isRepeated()) {
                    if (!(value instanceof List)) {
                        throw new IllegalStateException("Repeated field should contains a List but actually contains type: " + value.getClass());
                    }
                    List<Object> list = (List) value;
                    for (int i = 0; i < list.size(); i++) {
                        Object oldElement = list.get(i);
                        Object newElement = replaceBuilder(oldElement, partial);
                        if (newElement != oldElement) {
                            if (list == value) {
                                list = new ArrayList<>(list);
                            }
                            list.set(i, newElement);
                        }
                    }
                    return list;
                }
                return replaceBuilder(value, partial);
            }
            return value;
        }

        private static Object replaceBuilder(Object value, boolean partial) {
            if (!(value instanceof MessageLite.Builder)) {
                return value;
            }
            MessageLite.Builder builder = (MessageLite.Builder) value;
            if (partial) {
                return builder.buildPartial();
            }
            return builder.build();
        }

        public static <T extends FieldDescriptorLite<T>> Builder<T> fromFieldSet(FieldSet<T> fieldSet) {
            Builder<T> builder = new Builder<>(FieldSet.cloneAllFieldsMap(((FieldSet) fieldSet).fields, true));
            ((Builder) builder).hasLazyField = ((FieldSet) fieldSet).hasLazyField;
            return builder;
        }

        public Map<T, Object> getAllFields() {
            if (!this.hasLazyField) {
                return this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields);
            }
            SmallSortedMap<T, Object> result = FieldSet.cloneAllFieldsMap(this.fields, false);
            if (this.fields.isImmutable()) {
                result.makeImmutable();
            } else {
                replaceBuilders((SmallSortedMap) result, true);
            }
            return result;
        }

        public boolean hasField(T descriptor) {
            if (descriptor.isRepeated()) {
                throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
            }
            return this.fields.get(descriptor) != null;
        }

        public Object getField(T descriptor) {
            Object value = getFieldAllowBuilders(descriptor);
            return replaceBuilders(descriptor, value, true);
        }

        Object getFieldAllowBuilders(T descriptor) {
            Object o = this.fields.get(descriptor);
            if (o instanceof LazyField) {
                return ((LazyField) o).getValue();
            }
            return o;
        }

        private void ensureIsMutable() {
            if (!this.isMutable) {
                this.fields = FieldSet.cloneAllFieldsMap(this.fields, true);
                this.isMutable = true;
            }
        }

        public void setField(T descriptor, Object value) {
            ensureIsMutable();
            boolean z = false;
            if (descriptor.isRepeated()) {
                if (!(value instanceof List)) {
                    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
                }
                List newList = new ArrayList((List) value);
                for (Object element : newList) {
                    verifyType(descriptor, element);
                    this.hasNestedBuilders = this.hasNestedBuilders || (element instanceof MessageLite.Builder);
                }
                value = newList;
            } else {
                verifyType(descriptor, value);
            }
            if (value instanceof LazyField) {
                this.hasLazyField = true;
            }
            this.hasNestedBuilders = (this.hasNestedBuilders || (value instanceof MessageLite.Builder)) ? true : true;
            this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) value);
        }

        public void clearField(T descriptor) {
            ensureIsMutable();
            this.fields.remove(descriptor);
            if (this.fields.isEmpty()) {
                this.hasLazyField = false;
            }
        }

        public int getRepeatedFieldCount(T descriptor) {
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("getRepeatedFieldCount() can only be called on repeated fields.");
            }
            Object value = getFieldAllowBuilders(descriptor);
            if (value == null) {
                return 0;
            }
            return ((List) value).size();
        }

        public Object getRepeatedField(T descriptor, int index) {
            if (this.hasNestedBuilders) {
                ensureIsMutable();
            }
            Object value = getRepeatedFieldAllowBuilders(descriptor, index);
            return replaceBuilder(value, true);
        }

        Object getRepeatedFieldAllowBuilders(T descriptor, int index) {
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
            }
            Object value = getFieldAllowBuilders(descriptor);
            if (value == null) {
                throw new IndexOutOfBoundsException();
            }
            return ((List) value).get(index);
        }

        public void setRepeatedField(T descriptor, int index, Object value) {
            ensureIsMutable();
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
            }
            this.hasNestedBuilders = this.hasNestedBuilders || (value instanceof MessageLite.Builder);
            Object list = getFieldAllowBuilders(descriptor);
            if (list == null) {
                throw new IndexOutOfBoundsException();
            }
            verifyType(descriptor, value);
            ((List) list).set(index, value);
        }

        public void addRepeatedField(T descriptor, Object value) {
            List<Object> list;
            ensureIsMutable();
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
            }
            this.hasNestedBuilders = this.hasNestedBuilders || (value instanceof MessageLite.Builder);
            verifyType(descriptor, value);
            Object existingValue = getFieldAllowBuilders(descriptor);
            if (existingValue == null) {
                list = new ArrayList<>();
                this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) list);
            } else {
                list = (List) existingValue;
            }
            list.add(value);
        }

        private void verifyType(T descriptor, Object value) {
            if (!FieldSet.isValidType(descriptor.getLiteType(), value)) {
                if (descriptor.getLiteType().getJavaType() == WireFormat.JavaType.MESSAGE && (value instanceof MessageLite.Builder)) {
                    return;
                }
                throw new IllegalArgumentException(String.format("Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n", Integer.valueOf(descriptor.getNumber()), descriptor.getLiteType().getJavaType(), value.getClass().getName()));
            }
        }

        public boolean isInitialized() {
            for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
                if (!FieldSet.isInitialized(this.fields.getArrayEntryAt(i))) {
                    return false;
                }
            }
            for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
                if (!FieldSet.isInitialized(entry)) {
                    return false;
                }
            }
            return true;
        }

        public void mergeFrom(FieldSet<T> other) {
            ensureIsMutable();
            for (int i = 0; i < ((FieldSet) other).fields.getNumArrayEntries(); i++) {
                mergeFromField(((FieldSet) other).fields.getArrayEntryAt(i));
            }
            for (Map.Entry<T, Object> entry : ((FieldSet) other).fields.getOverflowEntries()) {
                mergeFromField(entry);
            }
        }

        private void mergeFromField(Map.Entry<T, Object> entry) {
            T descriptor = entry.getKey();
            Object otherValue = entry.getValue();
            if (otherValue instanceof LazyField) {
                otherValue = ((LazyField) otherValue).getValue();
            }
            if (descriptor.isRepeated()) {
                List<Object> value = (List) getFieldAllowBuilders(descriptor);
                if (value == null) {
                    value = new ArrayList<>();
                    this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) value);
                }
                for (Object element : (List) otherValue) {
                    value.add(FieldSet.cloneIfMutable(element));
                }
            } else if (descriptor.getLiteJavaType() != WireFormat.JavaType.MESSAGE) {
                this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) FieldSet.cloneIfMutable(otherValue));
            } else {
                Object value2 = getFieldAllowBuilders(descriptor);
                if (value2 == null) {
                    this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) FieldSet.cloneIfMutable(otherValue));
                } else if (value2 instanceof MessageLite.Builder) {
                    descriptor.internalMergeFrom((MessageLite.Builder) value2, (MessageLite) otherValue);
                } else {
                    this.fields.put((SmallSortedMap<T, Object>) descriptor, (T) descriptor.internalMergeFrom(((MessageLite) value2).toBuilder(), (MessageLite) otherValue).build());
                }
            }
        }
    }
}
