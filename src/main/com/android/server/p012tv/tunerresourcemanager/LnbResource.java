package com.android.server.p012tv.tunerresourcemanager;

import com.android.server.p012tv.tunerresourcemanager.TunerResourceBasic;
/* renamed from: com.android.server.tv.tunerresourcemanager.LnbResource */
/* loaded from: classes2.dex */
public final class LnbResource extends TunerResourceBasic {
    public LnbResource(Builder builder) {
        super(builder);
    }

    public String toString() {
        return "LnbResource[handle=" + this.mHandle + ", isInUse=" + this.mIsInUse + ", ownerClientId=" + this.mOwnerClientId + "]";
    }

    /* renamed from: com.android.server.tv.tunerresourcemanager.LnbResource$Builder */
    /* loaded from: classes2.dex */
    public static class Builder extends TunerResourceBasic.Builder {
        public Builder(int i) {
            super(i);
        }

        public LnbResource build() {
            return new LnbResource(this);
        }
    }
}
