package com.android.internal.telephony.protobuf.nano;

import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>> extends MessageNano {
    protected FieldArray unknownFieldData;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        if (this.unknownFieldData != null) {
            int i = 0;
            for (int i2 = 0; i2 < this.unknownFieldData.size(); i2++) {
                i += this.unknownFieldData.dataAt(i2).computeSerializedSize();
            }
            return i;
        }
        return 0;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.unknownFieldData == null) {
            return;
        }
        for (int i = 0; i < this.unknownFieldData.size(); i++) {
            this.unknownFieldData.dataAt(i).writeTo(codedOutputByteBufferNano);
        }
    }

    public final boolean hasExtension(Extension<M, ?> extension) {
        FieldArray fieldArray = this.unknownFieldData;
        return (fieldArray == null || fieldArray.get(WireFormatNano.getTagFieldNumber(extension.tag)) == null) ? false : true;
    }

    public final <T> T getExtension(Extension<M, T> extension) {
        FieldData fieldData;
        FieldArray fieldArray = this.unknownFieldData;
        if (fieldArray == null || (fieldData = fieldArray.get(WireFormatNano.getTagFieldNumber(extension.tag))) == null) {
            return null;
        }
        return (T) fieldData.getValue(extension);
    }

    public final <T> M setExtension(Extension<M, T> extension, T t) {
        int tagFieldNumber = WireFormatNano.getTagFieldNumber(extension.tag);
        FieldData fieldData = null;
        if (t == null) {
            FieldArray fieldArray = this.unknownFieldData;
            if (fieldArray != null) {
                fieldArray.remove(tagFieldNumber);
                if (this.unknownFieldData.isEmpty()) {
                    this.unknownFieldData = null;
                }
            }
        } else {
            FieldArray fieldArray2 = this.unknownFieldData;
            if (fieldArray2 == null) {
                this.unknownFieldData = new FieldArray();
            } else {
                fieldData = fieldArray2.get(tagFieldNumber);
            }
            if (fieldData == null) {
                this.unknownFieldData.put(tagFieldNumber, new FieldData(extension, t));
            } else {
                fieldData.setValue(extension, t);
            }
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean storeUnknownField(CodedInputByteBufferNano codedInputByteBufferNano, int i) throws IOException {
        FieldData fieldData;
        int position = codedInputByteBufferNano.getPosition();
        if (codedInputByteBufferNano.skipField(i)) {
            int tagFieldNumber = WireFormatNano.getTagFieldNumber(i);
            UnknownFieldData unknownFieldData = new UnknownFieldData(i, codedInputByteBufferNano.getData(position, codedInputByteBufferNano.getPosition() - position));
            FieldArray fieldArray = this.unknownFieldData;
            if (fieldArray == null) {
                this.unknownFieldData = new FieldArray();
                fieldData = null;
            } else {
                fieldData = fieldArray.get(tagFieldNumber);
            }
            if (fieldData == null) {
                fieldData = new FieldData();
                this.unknownFieldData.put(tagFieldNumber, fieldData);
            }
            fieldData.addUnknownField(unknownFieldData);
            return true;
        }
        return false;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    /* renamed from: clone */
    public M mo980clone() throws CloneNotSupportedException {
        M m = (M) super.mo980clone();
        InternalNano.cloneUnknownFieldData(this, m);
        return m;
    }
}
