package com.android.server.integrity.serializer;
/* loaded from: classes.dex */
public class RuleIndexingDetails {
    public int mIndexType;
    public String mRuleKey;

    public RuleIndexingDetails(int i) {
        this.mIndexType = i;
        this.mRuleKey = "N/A";
    }

    public RuleIndexingDetails(int i, String str) {
        this.mIndexType = i;
        this.mRuleKey = str;
    }

    public int getIndexType() {
        return this.mIndexType;
    }

    public String getRuleKey() {
        return this.mRuleKey;
    }
}
