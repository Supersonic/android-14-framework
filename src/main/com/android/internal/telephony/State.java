package com.android.internal.telephony;

import android.annotation.SuppressLint;
import android.os.Message;
@SuppressLint({"AndroidFrameworkRequiresPermission"})
/* loaded from: classes.dex */
public class State implements IState {
    @Override // com.android.internal.telephony.IState
    public void enter() {
    }

    @Override // com.android.internal.telephony.IState
    public void exit() {
    }

    @Override // com.android.internal.telephony.IState
    public boolean processMessage(Message message) {
        return false;
    }

    @Override // com.android.internal.telephony.IState
    public String getName() {
        String name = getClass().getName();
        return name.substring(name.lastIndexOf(36) + 1);
    }
}
