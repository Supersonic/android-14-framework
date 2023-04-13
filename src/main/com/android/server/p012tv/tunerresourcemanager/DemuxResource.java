package com.android.server.p012tv.tunerresourcemanager;

import com.android.server.p012tv.tunerresourcemanager.TunerResourceBasic;
/* renamed from: com.android.server.tv.tunerresourcemanager.DemuxResource */
/* loaded from: classes2.dex */
public final class DemuxResource extends TunerResourceBasic {
    public final int mFilterTypes;

    public DemuxResource(Builder builder) {
        super(builder);
        this.mFilterTypes = builder.mFilterTypes;
    }

    public String toString() {
        return "DemuxResource[handle=" + this.mHandle + ", filterTypes=" + this.mFilterTypes + ", isInUse=" + this.mIsInUse + ", ownerClientId=" + this.mOwnerClientId + "]";
    }

    public boolean hasSufficientCaps(int i) {
        return i == (this.mFilterTypes & i);
    }

    public int getNumOfCaps() {
        int i = 1;
        int i2 = 0;
        for (int i3 = 0; i3 < 32; i3++) {
            if ((this.mFilterTypes & i) == i) {
                i2++;
            }
            i <<= 1;
        }
        return i2;
    }

    /* renamed from: com.android.server.tv.tunerresourcemanager.DemuxResource$Builder */
    /* loaded from: classes2.dex */
    public static class Builder extends TunerResourceBasic.Builder {
        public int mFilterTypes;

        public Builder(int i) {
            super(i);
        }

        public Builder filterTypes(int i) {
            this.mFilterTypes = i;
            return this;
        }

        public DemuxResource build() {
            return new DemuxResource(this);
        }
    }
}
