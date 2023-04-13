package com.android.server.p012tv.tunerresourcemanager;
/* renamed from: com.android.server.tv.tunerresourcemanager.TunerResourceBasic */
/* loaded from: classes2.dex */
public class TunerResourceBasic {
    public final int mHandle;
    public boolean mIsInUse;
    public int mOwnerClientId = -1;

    public TunerResourceBasic(Builder builder) {
        this.mHandle = builder.mHandle;
    }

    public int getHandle() {
        return this.mHandle;
    }

    public boolean isInUse() {
        return this.mIsInUse;
    }

    public int getOwnerClientId() {
        return this.mOwnerClientId;
    }

    public void setOwner(int i) {
        this.mIsInUse = true;
        this.mOwnerClientId = i;
    }

    public void removeOwner() {
        this.mIsInUse = false;
        this.mOwnerClientId = -1;
    }

    /* renamed from: com.android.server.tv.tunerresourcemanager.TunerResourceBasic$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        public final int mHandle;

        public Builder(int i) {
            this.mHandle = i;
        }
    }
}
