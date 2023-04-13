package com.android.server.credentials.metrics;
/* loaded from: classes.dex */
public class CandidateBrowsingPhaseMetric {
    public int mSessionId = -1;
    public int mEntryEnum = EntryEnum.UNKNOWN.getMetricCode();
    public int mProviderUid = -1;

    public void setSessionId(int i) {
        this.mSessionId = i;
    }

    public void setEntryEnum(int i) {
        this.mEntryEnum = i;
    }

    public int getEntryEnum() {
        return this.mEntryEnum;
    }

    public void setProviderUid(int i) {
        this.mProviderUid = i;
    }

    public int getProviderUid() {
        return this.mProviderUid;
    }
}
