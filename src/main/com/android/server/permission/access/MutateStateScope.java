package com.android.server.permission.access;
/* compiled from: AccessState.kt */
/* loaded from: classes2.dex */
public final class MutateStateScope extends GetStateScope {
    public final AccessState newState;
    public final AccessState oldState;

    public final AccessState getOldState() {
        return this.oldState;
    }

    public final AccessState getNewState() {
        return this.newState;
    }

    public MutateStateScope(AccessState accessState, AccessState accessState2) {
        super(accessState2);
        this.oldState = accessState;
        this.newState = accessState2;
    }
}
