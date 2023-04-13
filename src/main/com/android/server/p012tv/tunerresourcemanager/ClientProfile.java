package com.android.server.p012tv.tunerresourcemanager;

import java.util.HashSet;
import java.util.Set;
/* renamed from: com.android.server.tv.tunerresourcemanager.ClientProfile */
/* loaded from: classes2.dex */
public final class ClientProfile {
    public int mGroupId;
    public final int mId;
    public boolean mIsPriorityOverwritten;
    public int mNiceValue;
    public int mPrimaryUsingFrontendHandle;
    public int mPriority;
    public final int mProcessId;
    public Set<Integer> mShareFeClientIds;
    public final String mTvInputSessionId;
    public final int mUseCase;
    public int mUsingCasSystemId;
    public int mUsingCiCamId;
    public Set<Integer> mUsingDemuxHandles;
    public Set<Integer> mUsingFrontendHandles;
    public Set<Integer> mUsingLnbHandles;

    public ClientProfile(Builder builder) {
        this.mGroupId = -1;
        this.mPrimaryUsingFrontendHandle = -1;
        this.mUsingFrontendHandles = new HashSet();
        this.mShareFeClientIds = new HashSet();
        this.mUsingDemuxHandles = new HashSet();
        this.mUsingLnbHandles = new HashSet();
        this.mUsingCasSystemId = -1;
        this.mUsingCiCamId = -1;
        this.mIsPriorityOverwritten = false;
        this.mId = builder.mId;
        this.mTvInputSessionId = builder.mTvInputSessionId;
        this.mUseCase = builder.mUseCase;
        this.mProcessId = builder.mProcessId;
    }

    public int getId() {
        return this.mId;
    }

    public int getUseCase() {
        return this.mUseCase;
    }

    public int getProcessId() {
        return this.mProcessId;
    }

    public boolean isPriorityOverwritten() {
        return this.mIsPriorityOverwritten;
    }

    public int getPriority() {
        return this.mPriority - this.mNiceValue;
    }

    public void setPriority(int i) {
        if (i < 0) {
            return;
        }
        this.mPriority = i;
    }

    public void overwritePriority(int i) {
        if (i < 0) {
            return;
        }
        this.mIsPriorityOverwritten = true;
        this.mPriority = i;
    }

    public void setNiceValue(int i) {
        this.mNiceValue = i;
    }

    public void useFrontend(int i) {
        this.mUsingFrontendHandles.add(Integer.valueOf(i));
    }

    public void setPrimaryFrontend(int i) {
        this.mPrimaryUsingFrontendHandle = i;
    }

    public int getPrimaryFrontend() {
        return this.mPrimaryUsingFrontendHandle;
    }

    public void shareFrontend(int i) {
        this.mShareFeClientIds.add(Integer.valueOf(i));
    }

    public void stopSharingFrontend(int i) {
        this.mShareFeClientIds.remove(Integer.valueOf(i));
    }

    public Set<Integer> getInUseFrontendHandles() {
        return this.mUsingFrontendHandles;
    }

    public Set<Integer> getShareFeClientIds() {
        return this.mShareFeClientIds;
    }

    public void releaseFrontend() {
        this.mUsingFrontendHandles.clear();
        this.mShareFeClientIds.clear();
        this.mPrimaryUsingFrontendHandle = -1;
    }

    public void useDemux(int i) {
        this.mUsingDemuxHandles.add(Integer.valueOf(i));
    }

    public Set<Integer> getInUseDemuxHandles() {
        return this.mUsingDemuxHandles;
    }

    public void releaseDemux(int i) {
        this.mUsingDemuxHandles.remove(Integer.valueOf(i));
    }

    public void useLnb(int i) {
        this.mUsingLnbHandles.add(Integer.valueOf(i));
    }

    public Set<Integer> getInUseLnbHandles() {
        return this.mUsingLnbHandles;
    }

    public void releaseLnb(int i) {
        this.mUsingLnbHandles.remove(Integer.valueOf(i));
    }

    public void useCas(int i) {
        this.mUsingCasSystemId = i;
    }

    public int getInUseCasSystemId() {
        return this.mUsingCasSystemId;
    }

    public void releaseCas() {
        this.mUsingCasSystemId = -1;
    }

    public void useCiCam(int i) {
        this.mUsingCiCamId = i;
    }

    public int getInUseCiCamId() {
        return this.mUsingCiCamId;
    }

    public void releaseCiCam() {
        this.mUsingCiCamId = -1;
    }

    public void reclaimAllResources() {
        this.mUsingFrontendHandles.clear();
        this.mShareFeClientIds.clear();
        this.mPrimaryUsingFrontendHandle = -1;
        this.mUsingLnbHandles.clear();
        this.mUsingCasSystemId = -1;
        this.mUsingCiCamId = -1;
    }

    public String toString() {
        return "ClientProfile[id=" + this.mId + ", tvInputSessionId=" + this.mTvInputSessionId + ", useCase=" + this.mUseCase + ", processId=" + this.mProcessId + "]";
    }

    /* renamed from: com.android.server.tv.tunerresourcemanager.ClientProfile$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        public final int mId;
        public int mProcessId;
        public String mTvInputSessionId;
        public int mUseCase;

        public Builder(int i) {
            this.mId = i;
        }

        public Builder useCase(int i) {
            this.mUseCase = i;
            return this;
        }

        public Builder tvInputSessionId(String str) {
            this.mTvInputSessionId = str;
            return this;
        }

        public Builder processId(int i) {
            this.mProcessId = i;
            return this;
        }

        public ClientProfile build() {
            return new ClientProfile(this);
        }
    }
}
