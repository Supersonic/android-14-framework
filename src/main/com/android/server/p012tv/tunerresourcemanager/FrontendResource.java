package com.android.server.p012tv.tunerresourcemanager;

import com.android.server.p012tv.tunerresourcemanager.TunerResourceBasic;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
/* renamed from: com.android.server.tv.tunerresourcemanager.FrontendResource */
/* loaded from: classes2.dex */
public final class FrontendResource extends TunerResourceBasic {
    public final int mExclusiveGroupId;
    public Set<Integer> mExclusiveGroupMemberHandles;
    public final int mType;

    public FrontendResource(Builder builder) {
        super(builder);
        this.mExclusiveGroupMemberHandles = new HashSet();
        this.mType = builder.mType;
        this.mExclusiveGroupId = builder.mExclusiveGroupId;
    }

    public int getType() {
        return this.mType;
    }

    public int getExclusiveGroupId() {
        return this.mExclusiveGroupId;
    }

    public Set<Integer> getExclusiveGroupMemberFeHandles() {
        return this.mExclusiveGroupMemberHandles;
    }

    public void addExclusiveGroupMemberFeHandle(int i) {
        this.mExclusiveGroupMemberHandles.add(Integer.valueOf(i));
    }

    public void addExclusiveGroupMemberFeHandles(Collection<Integer> collection) {
        this.mExclusiveGroupMemberHandles.addAll(collection);
    }

    public void removeExclusiveGroupMemberFeId(int i) {
        this.mExclusiveGroupMemberHandles.remove(Integer.valueOf(i));
    }

    public String toString() {
        return "FrontendResource[handle=" + this.mHandle + ", type=" + this.mType + ", exclusiveGId=" + this.mExclusiveGroupId + ", exclusiveGMemeberHandles=" + this.mExclusiveGroupMemberHandles + ", isInUse=" + this.mIsInUse + ", ownerClientId=" + this.mOwnerClientId + "]";
    }

    /* renamed from: com.android.server.tv.tunerresourcemanager.FrontendResource$Builder */
    /* loaded from: classes2.dex */
    public static class Builder extends TunerResourceBasic.Builder {
        public int mExclusiveGroupId;
        public int mType;

        public Builder(int i) {
            super(i);
        }

        public Builder type(int i) {
            this.mType = i;
            return this;
        }

        public Builder exclusiveGroupId(int i) {
            this.mExclusiveGroupId = i;
            return this;
        }

        public FrontendResource build() {
            return new FrontendResource(this);
        }
    }
}
