package com.android.ims.internal;
/* loaded from: classes.dex */
public interface ICall {
    boolean checkIfRemoteUserIsSame(String str);

    void close();

    boolean equalsTo(ICall iCall);
}
