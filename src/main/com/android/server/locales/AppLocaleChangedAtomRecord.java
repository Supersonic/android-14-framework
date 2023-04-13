package com.android.server.locales;
/* loaded from: classes.dex */
public final class AppLocaleChangedAtomRecord {
    public final int mCallingUid;
    public int mTargetUid = -1;
    public String mNewLocales = "";
    public String mPrevLocales = "";
    public int mStatus = 0;

    public AppLocaleChangedAtomRecord(int i) {
        this.mCallingUid = i;
    }

    public void setNewLocales(String str) {
        this.mNewLocales = str;
    }

    public void setTargetUid(int i) {
        this.mTargetUid = i;
    }

    public void setPrevLocales(String str) {
        this.mPrevLocales = str;
    }

    public void setStatus(int i) {
        this.mStatus = i;
    }
}
