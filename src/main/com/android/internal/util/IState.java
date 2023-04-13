package com.android.internal.util;

import android.p008os.Message;
/* loaded from: classes3.dex */
public interface IState {
    public static final boolean HANDLED = true;
    public static final boolean NOT_HANDLED = false;

    void enter();

    void exit();

    String getName();

    boolean processMessage(Message message);
}
