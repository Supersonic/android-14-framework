package com.android.internal.telephony.protobuf.nano;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class FieldData implements Cloneable {
    private Extension<?, ?> cachedExtension;
    private List<UnknownFieldData> unknownFieldData;
    private Object value;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public <T> FieldData(Extension<?, T> extension, T t) {
        this.cachedExtension = extension;
        this.value = t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldData() {
        this.unknownFieldData = new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addUnknownField(UnknownFieldData unknownFieldData) {
        this.unknownFieldData.add(unknownFieldData);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public <T> T getValue(Extension<?, T> extension) {
        if (this.value != null) {
            if (this.cachedExtension != extension) {
                throw new IllegalStateException("Tried to getExtension with a differernt Extension.");
            }
        } else {
            this.cachedExtension = extension;
            this.value = extension.getValueFrom(this.unknownFieldData);
            this.unknownFieldData = null;
        }
        return (T) this.value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public <T> void setValue(Extension<?, T> extension, T t) {
        this.cachedExtension = extension;
        this.value = t;
        this.unknownFieldData = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int computeSerializedSize() {
        Object obj = this.value;
        if (obj != null) {
            return this.cachedExtension.computeSerializedSize(obj);
        }
        int i = 0;
        for (UnknownFieldData unknownFieldData : this.unknownFieldData) {
            i += unknownFieldData.computeSerializedSize();
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        Object obj = this.value;
        if (obj != null) {
            this.cachedExtension.writeTo(obj, codedOutputByteBufferNano);
            return;
        }
        for (UnknownFieldData unknownFieldData : this.unknownFieldData) {
            unknownFieldData.writeTo(codedOutputByteBufferNano);
        }
    }

    public boolean equals(Object obj) {
        List<UnknownFieldData> list;
        if (obj == this) {
            return true;
        }
        if (obj instanceof FieldData) {
            FieldData fieldData = (FieldData) obj;
            if (this.value != null && fieldData.value != null) {
                Extension<?, ?> extension = this.cachedExtension;
                if (extension != fieldData.cachedExtension) {
                    return false;
                }
                if (!extension.clazz.isArray()) {
                    return this.value.equals(fieldData.value);
                }
                Object obj2 = this.value;
                if (obj2 instanceof byte[]) {
                    return Arrays.equals((byte[]) obj2, (byte[]) fieldData.value);
                }
                if (obj2 instanceof int[]) {
                    return Arrays.equals((int[]) obj2, (int[]) fieldData.value);
                }
                if (obj2 instanceof long[]) {
                    return Arrays.equals((long[]) obj2, (long[]) fieldData.value);
                }
                if (obj2 instanceof float[]) {
                    return Arrays.equals((float[]) obj2, (float[]) fieldData.value);
                }
                if (obj2 instanceof double[]) {
                    return Arrays.equals((double[]) obj2, (double[]) fieldData.value);
                }
                if (obj2 instanceof boolean[]) {
                    return Arrays.equals((boolean[]) obj2, (boolean[]) fieldData.value);
                }
                return Arrays.deepEquals((Object[]) obj2, (Object[]) fieldData.value);
            }
            List<UnknownFieldData> list2 = this.unknownFieldData;
            if (list2 != null && (list = fieldData.unknownFieldData) != null) {
                return list2.equals(list);
            }
            try {
                return Arrays.equals(toByteArray(), fieldData.toByteArray());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }

    public int hashCode() {
        try {
            return 527 + Arrays.hashCode(toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[computeSerializedSize()];
        writeTo(CodedOutputByteBufferNano.newInstance(bArr));
        return bArr;
    }

    /* renamed from: clone */
    public final FieldData m982clone() {
        FieldData fieldData = new FieldData();
        try {
            fieldData.cachedExtension = this.cachedExtension;
            List<UnknownFieldData> list = this.unknownFieldData;
            if (list == null) {
                fieldData.unknownFieldData = null;
            } else {
                fieldData.unknownFieldData.addAll(list);
            }
            Object obj = this.value;
            if (obj != null) {
                if (obj instanceof MessageNano) {
                    fieldData.value = ((MessageNano) obj).mo980clone();
                } else if (obj instanceof byte[]) {
                    fieldData.value = ((byte[]) obj).clone();
                } else {
                    int i = 0;
                    if (obj instanceof byte[][]) {
                        byte[][] bArr = (byte[][]) obj;
                        byte[][] bArr2 = new byte[bArr.length];
                        fieldData.value = bArr2;
                        while (i < bArr.length) {
                            bArr2[i] = (byte[]) bArr[i].clone();
                            i++;
                        }
                    } else if (obj instanceof boolean[]) {
                        fieldData.value = ((boolean[]) obj).clone();
                    } else if (obj instanceof int[]) {
                        fieldData.value = ((int[]) obj).clone();
                    } else if (obj instanceof long[]) {
                        fieldData.value = ((long[]) obj).clone();
                    } else if (obj instanceof float[]) {
                        fieldData.value = ((float[]) obj).clone();
                    } else if (obj instanceof double[]) {
                        fieldData.value = ((double[]) obj).clone();
                    } else if (obj instanceof MessageNano[]) {
                        MessageNano[] messageNanoArr = (MessageNano[]) obj;
                        MessageNano[] messageNanoArr2 = new MessageNano[messageNanoArr.length];
                        fieldData.value = messageNanoArr2;
                        while (i < messageNanoArr.length) {
                            messageNanoArr2[i] = messageNanoArr[i].mo980clone();
                            i++;
                        }
                    }
                }
            }
            return fieldData;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
