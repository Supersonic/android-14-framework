package com.android.phone.ecc.nano;

import com.android.phone.ecc.nano.ExtendableMessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>> extends MessageNano {
    protected FieldArray unknownFieldData;

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

    @Override // com.android.phone.ecc.nano.MessageNano
    /* renamed from: clone */
    public M mo1273clone() throws CloneNotSupportedException {
        M m = (M) super.mo1273clone();
        InternalNano.cloneUnknownFieldData(this, m);
        return m;
    }
}
