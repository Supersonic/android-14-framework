package com.android.internal.telephony.uicc.asn1;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
/* loaded from: classes3.dex */
public class TagNotFoundException extends Exception {
    private final int mTag;

    public TagNotFoundException(int tag) {
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
