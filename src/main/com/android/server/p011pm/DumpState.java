package com.android.server.p011pm;
/* renamed from: com.android.server.pm.DumpState */
/* loaded from: classes2.dex */
public final class DumpState {
    public boolean mBrief;
    public boolean mCheckIn;
    public boolean mFullPreferred;
    public int mOptions;
    public SharedUserSetting mSharedUser;
    public String mTargetPackageName;
    public boolean mTitlePrinted;
    public int mTypes;

    public boolean isDumping(int i) {
        int i2 = this.mTypes;
        return (i2 == 0 && i != 8192) || (i2 & i) != 0;
    }

    public void setDump(int i) {
        this.mTypes = i | this.mTypes;
    }

    public boolean isOptionEnabled(int i) {
        return (this.mOptions & i) != 0;
    }

    public void setOptionEnabled(int i) {
        this.mOptions = i | this.mOptions;
    }

    public boolean onTitlePrinted() {
        boolean z = this.mTitlePrinted;
        this.mTitlePrinted = true;
        return z;
    }

    public boolean getTitlePrinted() {
        return this.mTitlePrinted;
    }

    public void setTitlePrinted(boolean z) {
        this.mTitlePrinted = z;
    }

    public SharedUserSetting getSharedUser() {
        return this.mSharedUser;
    }

    public void setSharedUser(SharedUserSetting sharedUserSetting) {
        this.mSharedUser = sharedUserSetting;
    }

    public String getTargetPackageName() {
        return this.mTargetPackageName;
    }

    public void setTargetPackageName(String str) {
        this.mTargetPackageName = str;
    }

    public boolean isFullPreferred() {
        return this.mFullPreferred;
    }

    public void setFullPreferred(boolean z) {
        this.mFullPreferred = z;
    }

    public boolean isCheckIn() {
        return this.mCheckIn;
    }

    public void setCheckIn(boolean z) {
        this.mCheckIn = z;
    }

    public boolean isBrief() {
        return this.mBrief;
    }

    public void setBrief(boolean z) {
        this.mBrief = z;
    }
}
