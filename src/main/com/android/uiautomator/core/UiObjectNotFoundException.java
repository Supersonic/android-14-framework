package com.android.uiautomator.core;
@Deprecated
/* loaded from: classes.dex */
public class UiObjectNotFoundException extends Exception {
    private static final long serialVersionUID = 1;

    public UiObjectNotFoundException(String msg) {
        super(msg);
    }

    public UiObjectNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UiObjectNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
