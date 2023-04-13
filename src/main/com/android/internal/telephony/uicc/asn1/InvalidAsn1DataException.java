package com.android.internal.telephony.uicc.asn1;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
/* loaded from: classes3.dex */
public class InvalidAsn1DataException extends Exception {
    private final int mTag;

    public InvalidAsn1DataException(int tag, String message) {
        super(message);
        this.mTag = tag;
    }

    public InvalidAsn1DataException(int tag, String message, Throwable throwable) {
        super(message, throwable);
        this.mTag = tag;
    }

    public int getTag() {
        return this.mTag;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return super.getMessage() + " (tag=" + this.mTag + NavigationBarInflaterView.KEY_CODE_END;
    }
}
