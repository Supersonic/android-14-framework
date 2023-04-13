package com.android.framework.protobuf.nano;

import com.android.framework.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;
/* loaded from: classes4.dex */
public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>> extends MessageNano {
    protected FieldArray unknownFieldData;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.framework.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int size = 0;
        if (this.unknownFieldData != null) {
            for (int i = 0; i < this.unknownFieldData.size(); i++) {
                FieldData field = this.unknownFieldData.dataAt(i);
                size += field.computeSerializedSize();
            }
        }
        return size;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (this.unknownFieldData == null) {
            return;
        }
        for (int i = 0; i < this.unknownFieldData.size(); i++) {
            FieldData field = this.unknownFieldData.dataAt(i);
            field.writeTo(output);
        }
    }

    public final boolean hasExtension(Extension<M, ?> extension) {
        FieldArray fieldArray = this.unknownFieldData;
        if (fieldArray == null) {
            return false;
        }
        FieldData field = fieldArray.get(WireFormatNano.getTagFieldNumber(extension.tag));
        return field != null;
    }

    public final <T> T getExtension(Extension<M, T> extension) {
        FieldData field;
        FieldArray fieldArray = this.unknownFieldData;
        if (fieldArray == null || (field = fieldArray.get(WireFormatNano.getTagFieldNumber(extension.tag))) == null) {
            return null;
        }
        return (T) field.getValue(extension);
    }

    public final <T> M setExtension(Extension<M, T> extension, T value) {
        int fieldNumber = WireFormatNano.getTagFieldNumber(extension.tag);
        if (value == null) {
            FieldArray fieldArray = this.unknownFieldData;
            if (fieldArray != null) {
                fieldArray.remove(fieldNumber);
                if (this.unknownFieldData.isEmpty()) {
                    this.unknownFieldData = null;
                }
            }
        } else {
            FieldData field = null;
            FieldArray fieldArray2 = this.unknownFieldData;
            if (fieldArray2 == null) {
                this.unknownFieldData = new FieldArray();
            } else {
                field = fieldArray2.get(fieldNumber);
            }
            if (field == null) {
                this.unknownFieldData.put(fieldNumber, new FieldData(extension, value));
            } else {
                field.setValue(extension, value);
            }
        }
        return this;
    }

    protected final boolean storeUnknownField(CodedInputByteBufferNano input, int tag) throws IOException {
        int startPos = input.getPosition();
        if (!input.skipField(tag)) {
            return false;
        }
        int fieldNumber = WireFormatNano.getTagFieldNumber(tag);
        int endPos = input.getPosition();
        byte[] bytes = input.getData(startPos, endPos - startPos);
        UnknownFieldData unknownField = new UnknownFieldData(tag, bytes);
        FieldData field = null;
        FieldArray fieldArray = this.unknownFieldData;
        if (fieldArray == null) {
            this.unknownFieldData = new FieldArray();
        } else {
            field = fieldArray.get(fieldNumber);
        }
        if (field == null) {
            field = new FieldData();
            this.unknownFieldData.put(fieldNumber, field);
        }
        field.addUnknownField(unknownField);
        return true;
    }

    @Override // com.android.framework.protobuf.nano.MessageNano
    /* renamed from: clone */
    public M mo6506clone() throws CloneNotSupportedException {
        M cloned = (M) super.mo6506clone();
        InternalNano.cloneUnknownFieldData(this, cloned);
        return cloned;
    }
}
