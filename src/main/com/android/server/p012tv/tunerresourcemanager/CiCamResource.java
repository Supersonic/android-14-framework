package com.android.server.p012tv.tunerresourcemanager;

import com.android.server.p012tv.tunerresourcemanager.CasResource;
/* renamed from: com.android.server.tv.tunerresourcemanager.CiCamResource */
/* loaded from: classes2.dex */
public final class CiCamResource extends CasResource {
    public CiCamResource(Builder builder) {
        super(builder);
    }

    @Override // com.android.server.p012tv.tunerresourcemanager.CasResource
    public String toString() {
        return "CiCamResource[systemId=" + getSystemId() + ", isFullyUsed=" + isFullyUsed() + ", maxSessionNum=" + getMaxSessionNum() + ", ownerClients=" + ownersMapToString() + "]";
    }

    public int getCiCamId() {
        return getSystemId();
    }

    /* renamed from: com.android.server.tv.tunerresourcemanager.CiCamResource$Builder */
    /* loaded from: classes2.dex */
    public static class Builder extends CasResource.Builder {
        public Builder(int i) {
            super(i);
        }

        @Override // com.android.server.p012tv.tunerresourcemanager.CasResource.Builder
        public Builder maxSessionNum(int i) {
            this.mMaxSessionNum = i;
            return this;
        }

        @Override // com.android.server.p012tv.tunerresourcemanager.CasResource.Builder
        public CiCamResource build() {
            return new CiCamResource(this);
        }
    }
}
