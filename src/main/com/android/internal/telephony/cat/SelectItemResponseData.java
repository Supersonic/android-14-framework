package com.android.internal.telephony.cat;

import java.io.ByteArrayOutputStream;
/* compiled from: ResponseData.java */
/* loaded from: classes.dex */
class SelectItemResponseData extends ResponseData {
    private int mId;

    public SelectItemResponseData(int i) {
        this.mId = i;
    }

    @Override // com.android.internal.telephony.cat.ResponseData
    public void format(ByteArrayOutputStream byteArrayOutputStream) {
        byteArrayOutputStream.write(ComprehensionTlvTag.ITEM_ID.value() | 128);
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(this.mId);
    }
}
