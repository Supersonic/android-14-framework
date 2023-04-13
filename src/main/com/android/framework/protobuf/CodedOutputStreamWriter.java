package com.android.framework.protobuf;

import com.android.framework.protobuf.MapEntryLite;
import com.android.framework.protobuf.WireFormat;
import com.android.framework.protobuf.Writer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
@CheckReturnValue
/* loaded from: classes4.dex */
public final class CodedOutputStreamWriter implements Writer {
    private final CodedOutputStream output;

    public static CodedOutputStreamWriter forCodedOutput(CodedOutputStream output) {
        if (output.wrapper != null) {
            return output.wrapper;
        }
        return new CodedOutputStreamWriter(output);
    }

    private CodedOutputStreamWriter(CodedOutputStream output) {
        CodedOutputStream codedOutputStream = (CodedOutputStream) Internal.checkNotNull(output, "output");
        this.output = codedOutputStream;
        codedOutputStream.wrapper = this;
    }

    @Override // com.android.framework.protobuf.Writer
    public Writer.FieldOrder fieldOrder() {
        return Writer.FieldOrder.ASCENDING;
    }

    public int getTotalBytesWritten() {
        return this.output.getTotalBytesWritten();
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeSFixed32(int fieldNumber, int value) throws IOException {
        this.output.writeSFixed32(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeInt64(int fieldNumber, long value) throws IOException {
        this.output.writeInt64(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeSFixed64(int fieldNumber, long value) throws IOException {
        this.output.writeSFixed64(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeFloat(int fieldNumber, float value) throws IOException {
        this.output.writeFloat(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeDouble(int fieldNumber, double value) throws IOException {
        this.output.writeDouble(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeEnum(int fieldNumber, int value) throws IOException {
        this.output.writeEnum(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeUInt64(int fieldNumber, long value) throws IOException {
        this.output.writeUInt64(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeInt32(int fieldNumber, int value) throws IOException {
        this.output.writeInt32(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeFixed64(int fieldNumber, long value) throws IOException {
        this.output.writeFixed64(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeFixed32(int fieldNumber, int value) throws IOException {
        this.output.writeFixed32(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeBool(int fieldNumber, boolean value) throws IOException {
        this.output.writeBool(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeString(int fieldNumber, String value) throws IOException {
        this.output.writeString(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeBytes(int fieldNumber, ByteString value) throws IOException {
        this.output.writeBytes(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeUInt32(int fieldNumber, int value) throws IOException {
        this.output.writeUInt32(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeSInt32(int fieldNumber, int value) throws IOException {
        this.output.writeSInt32(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeSInt64(int fieldNumber, long value) throws IOException {
        this.output.writeSInt64(fieldNumber, value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeMessage(int fieldNumber, Object value) throws IOException {
        this.output.writeMessage(fieldNumber, (MessageLite) value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
        this.output.writeMessage(fieldNumber, (MessageLite) value, schema);
    }

    @Override // com.android.framework.protobuf.Writer
    @Deprecated
    public void writeGroup(int fieldNumber, Object value) throws IOException {
        this.output.writeGroup(fieldNumber, (MessageLite) value);
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
        this.output.writeGroup(fieldNumber, (MessageLite) value, schema);
    }

    @Override // com.android.framework.protobuf.Writer
    @Deprecated
    public void writeStartGroup(int fieldNumber) throws IOException {
        this.output.writeTag(fieldNumber, 3);
    }

    @Override // com.android.framework.protobuf.Writer
    @Deprecated
    public void writeEndGroup(int fieldNumber) throws IOException {
        this.output.writeTag(fieldNumber, 4);
    }

    @Override // com.android.framework.protobuf.Writer
    public final void writeMessageSetItem(int fieldNumber, Object value) throws IOException {
        if (value instanceof ByteString) {
            this.output.writeRawMessageSetExtension(fieldNumber, (ByteString) value);
        } else {
            this.output.writeMessageSetExtension(fieldNumber, (MessageLite) value);
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeInt32SizeNoTag(value.get(i).intValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeInt32NoTag(value.get(i2).intValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeInt32(fieldNumber, value.get(i3).intValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeFixed32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeFixed32SizeNoTag(value.get(i).intValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeFixed32NoTag(value.get(i2).intValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeFixed32(fieldNumber, value.get(i3).intValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeInt64SizeNoTag(value.get(i).longValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeInt64NoTag(value.get(i2).longValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeInt64(fieldNumber, value.get(i3).longValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeUInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeUInt64SizeNoTag(value.get(i).longValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeUInt64NoTag(value.get(i2).longValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeUInt64(fieldNumber, value.get(i3).longValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeFixed64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeFixed64SizeNoTag(value.get(i).longValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeFixed64NoTag(value.get(i2).longValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeFixed64(fieldNumber, value.get(i3).longValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeFloatList(int fieldNumber, List<Float> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeFloatSizeNoTag(value.get(i).floatValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeFloatNoTag(value.get(i2).floatValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeFloat(fieldNumber, value.get(i3).floatValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeDoubleList(int fieldNumber, List<Double> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeDoubleSizeNoTag(value.get(i).doubleValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeDoubleNoTag(value.get(i2).doubleValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeDouble(fieldNumber, value.get(i3).doubleValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeEnumList(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeEnumSizeNoTag(value.get(i).intValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeEnumNoTag(value.get(i2).intValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeEnum(fieldNumber, value.get(i3).intValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeBoolList(int fieldNumber, List<Boolean> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeBoolSizeNoTag(value.get(i).booleanValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeBoolNoTag(value.get(i2).booleanValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeBool(fieldNumber, value.get(i3).booleanValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeStringList(int fieldNumber, List<String> value) throws IOException {
        if (value instanceof LazyStringList) {
            LazyStringList lazyList = (LazyStringList) value;
            for (int i = 0; i < value.size(); i++) {
                writeLazyString(fieldNumber, lazyList.getRaw(i));
            }
            return;
        }
        for (int i2 = 0; i2 < value.size(); i2++) {
            this.output.writeString(fieldNumber, value.get(i2));
        }
    }

    private void writeLazyString(int fieldNumber, Object value) throws IOException {
        if (value instanceof String) {
            this.output.writeString(fieldNumber, (String) value);
        } else {
            this.output.writeBytes(fieldNumber, (ByteString) value);
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeBytesList(int fieldNumber, List<ByteString> value) throws IOException {
        for (int i = 0; i < value.size(); i++) {
            this.output.writeBytes(fieldNumber, value.get(i));
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeUInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeUInt32SizeNoTag(value.get(i).intValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeUInt32NoTag(value.get(i2).intValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeUInt32(fieldNumber, value.get(i3).intValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeSFixed32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeSFixed32SizeNoTag(value.get(i).intValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeSFixed32NoTag(value.get(i2).intValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeSFixed32(fieldNumber, value.get(i3).intValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeSFixed64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeSFixed64SizeNoTag(value.get(i).longValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeSFixed64NoTag(value.get(i2).longValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeSFixed64(fieldNumber, value.get(i3).longValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeSInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeSInt32SizeNoTag(value.get(i).intValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeSInt32NoTag(value.get(i2).intValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeSInt32(fieldNumber, value.get(i3).intValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeSInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (int i = 0; i < value.size(); i++) {
                dataSize += CodedOutputStream.computeSInt64SizeNoTag(value.get(i).longValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (int i2 = 0; i2 < value.size(); i2++) {
                this.output.writeSInt64NoTag(value.get(i2).longValue());
            }
            return;
        }
        for (int i3 = 0; i3 < value.size(); i3++) {
            this.output.writeSInt64(fieldNumber, value.get(i3).longValue());
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeMessageList(int fieldNumber, List<?> value) throws IOException {
        for (int i = 0; i < value.size(); i++) {
            writeMessage(fieldNumber, value.get(i));
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeMessageList(int fieldNumber, List<?> value, Schema schema) throws IOException {
        for (int i = 0; i < value.size(); i++) {
            writeMessage(fieldNumber, value.get(i), schema);
        }
    }

    @Override // com.android.framework.protobuf.Writer
    @Deprecated
    public void writeGroupList(int fieldNumber, List<?> value) throws IOException {
        for (int i = 0; i < value.size(); i++) {
            writeGroup(fieldNumber, value.get(i));
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public void writeGroupList(int fieldNumber, List<?> value, Schema schema) throws IOException {
        for (int i = 0; i < value.size(); i++) {
            writeGroup(fieldNumber, value.get(i), schema);
        }
    }

    @Override // com.android.framework.protobuf.Writer
    public <K, V> void writeMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
        if (this.output.isSerializationDeterministic()) {
            writeDeterministicMap(fieldNumber, metadata, map);
            return;
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            this.output.writeTag(fieldNumber, 2);
            this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, entry.getKey(), entry.getValue()));
            MapEntryLite.writeTo(this.output, metadata, entry.getKey(), entry.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.framework.protobuf.CodedOutputStreamWriter$1 */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class C40041 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$FieldType;

        static {
            int[] iArr = new int[WireFormat.FieldType.values().length];
            $SwitchMap$com$google$protobuf$WireFormat$FieldType = iArr;
            try {
                iArr[WireFormat.FieldType.BOOL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED32.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT32.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED32.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT32.ordinal()] = 5;
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
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT64.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED64.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT64.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT64.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.STRING.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    private <K, V> void writeDeterministicMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
        switch (C40041.$SwitchMap$com$google$protobuf$WireFormat$FieldType[metadata.keyType.ordinal()]) {
            case 1:
                V value = map.get(Boolean.FALSE);
                if (value != null) {
                    writeDeterministicBooleanMapEntry(fieldNumber, false, value, metadata);
                }
                V value2 = map.get(Boolean.TRUE);
                if (value2 != null) {
                    writeDeterministicBooleanMapEntry(fieldNumber, true, value2, metadata);
                    return;
                }
                return;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                writeDeterministicIntegerMap(fieldNumber, metadata, map);
                return;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                writeDeterministicLongMap(fieldNumber, metadata, map);
                return;
            case 12:
                writeDeterministicStringMap(fieldNumber, metadata, map);
                return;
            default:
                throw new IllegalArgumentException("does not support key type: " + metadata.keyType);
        }
    }

    private <V> void writeDeterministicBooleanMapEntry(int fieldNumber, boolean key, V value, MapEntryLite.Metadata<Boolean, V> metadata) throws IOException {
        this.output.writeTag(fieldNumber, 2);
        this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, Boolean.valueOf(key), value));
        MapEntryLite.writeTo(this.output, metadata, Boolean.valueOf(key), value);
    }

    private <V> void writeDeterministicIntegerMap(int fieldNumber, MapEntryLite.Metadata<Integer, V> metadata, Map<Integer, V> map) throws IOException {
        int[] keys = new int[map.size()];
        int index = 0;
        for (Integer num : map.keySet()) {
            int k = num.intValue();
            keys[index] = k;
            index++;
        }
        Arrays.sort(keys);
        for (int key : keys) {
            V value = map.get(Integer.valueOf(key));
            this.output.writeTag(fieldNumber, 2);
            this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, Integer.valueOf(key), value));
            MapEntryLite.writeTo(this.output, metadata, Integer.valueOf(key), value);
        }
    }

    private <V> void writeDeterministicLongMap(int fieldNumber, MapEntryLite.Metadata<Long, V> metadata, Map<Long, V> map) throws IOException {
        long[] keys = new long[map.size()];
        int index = 0;
        for (Long l : map.keySet()) {
            long k = l.longValue();
            keys[index] = k;
            index++;
        }
        Arrays.sort(keys);
        for (long key : keys) {
            V value = map.get(Long.valueOf(key));
            this.output.writeTag(fieldNumber, 2);
            this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, Long.valueOf(key), value));
            MapEntryLite.writeTo(this.output, metadata, Long.valueOf(key), value);
        }
    }

    private <V> void writeDeterministicStringMap(int fieldNumber, MapEntryLite.Metadata<String, V> metadata, Map<String, V> map) throws IOException {
        String[] keys = new String[map.size()];
        int index = 0;
        for (String k : map.keySet()) {
            keys[index] = k;
            index++;
        }
        Arrays.sort(keys);
        for (String key : keys) {
            V value = map.get(key);
            this.output.writeTag(fieldNumber, 2);
            this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, key, value));
            MapEntryLite.writeTo(this.output, metadata, key, value);
        }
    }
}
