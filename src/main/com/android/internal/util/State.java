package com.android.internal.util;

import android.p008os.Message;
/* loaded from: classes3.dex */
public class State implements IState {
    @Override // com.android.internal.util.IState
    public void enter() {
    }

    @Override // com.android.internal.util.IState
    public void exit() {
    }

    @Override // com.android.internal.util.IState
    public boolean processMessage(Message msg) {
        return false;
    }

    @Override // com.android.internal.util.IState
    public String getName() {
        String name = getClass().getName();
        int lastDollar = name.lastIndexOf(36);
        return name.substring(lastDollar + 1);
    }
}
