package com.android.server.integrity.model;
/* loaded from: classes.dex */
public class RuleMetadata {
    public final String mRuleProvider;
    public final String mVersion;

    public RuleMetadata(String str, String str2) {
        this.mRuleProvider = str;
        this.mVersion = str2;
    }

    public String getRuleProvider() {
        return this.mRuleProvider;
    }

    public String getVersion() {
        return this.mVersion;
    }
}
