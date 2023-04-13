package com.android.server.locales;
/* loaded from: classes.dex */
public final class AppSupportedLocalesChangedAtomRecord {
    public final int mCallingUid;
    public int mTargetUid = -1;
    public int mNumLocales = -1;
    public boolean mOverrideRemoved = false;
    public boolean mSameAsResConfig = false;
    public boolean mSameAsPrevConfig = false;
    public int mStatus = 0;

    public AppSupportedLocalesChangedAtomRecord(int i) {
        this.mCallingUid = i;
    }

    public void setTargetUid(int i) {
        this.mTargetUid = i;
    }

    public void setNumLocales(int i) {
        this.mNumLocales = i;
    }

    public void setOverrideRemoved(boolean z) {
        this.mOverrideRemoved = z;
    }

    public void setSameAsResConfig(boolean z) {
        this.mSameAsResConfig = z;
    }

    public void setSameAsPrevConfig(boolean z) {
        this.mSameAsPrevConfig = z;
    }

    public void setStatus(int i) {
        this.mStatus = i;
    }
}
