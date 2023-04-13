package com.android.ims;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
@Deprecated
/* loaded from: classes4.dex */
public class ImsException extends Exception {
    private int mCode;

    public ImsException() {
    }

    public ImsException(String message, int code) {
        super(message + NavigationBarInflaterView.KEY_CODE_START + code + NavigationBarInflaterView.KEY_CODE_END);
        this.mCode = code;
    }

    public ImsException(String message, Throwable cause, int code) {
        super(message, cause);
        this.mCode = code;
    }

    public int getCode() {
        return this.mCode;
    }
}
