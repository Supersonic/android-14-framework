package com.android.server.p011pm;

import android.content.pm.ResolveInfo;
/* renamed from: com.android.server.pm.CrossProfileDomainInfo */
/* loaded from: classes2.dex */
public final class CrossProfileDomainInfo {
    public int mHighestApprovalLevel;
    public ResolveInfo mResolveInfo;
    public int mTargetUserId;

    public CrossProfileDomainInfo(ResolveInfo resolveInfo, int i, int i2) {
        this.mResolveInfo = resolveInfo;
        this.mHighestApprovalLevel = i;
        this.mTargetUserId = i2;
    }

    public String toString() {
        return "CrossProfileDomainInfo{resolveInfo=" + this.mResolveInfo + ", highestApprovalLevel=" + this.mHighestApprovalLevel + ", targetUserId= " + this.mTargetUserId + '}';
    }
}
