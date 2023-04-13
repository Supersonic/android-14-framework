package com.android.server.permission.access;
/* compiled from: AccessState.kt */
/* loaded from: classes2.dex */
public abstract class WritableState {
    public int writeMode;

    public final int getWriteMode() {
        return this.writeMode;
    }

    public static /* synthetic */ void requestWrite$default(WritableState writableState, boolean z, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: requestWrite");
        }
        if ((i & 1) != 0) {
            z = false;
        }
        writableState.requestWrite(z);
    }

    public final void requestWrite(boolean z) {
        if (z) {
            this.writeMode = 1;
        } else if (this.writeMode != 1) {
            this.writeMode = 2;
        }
    }
}
