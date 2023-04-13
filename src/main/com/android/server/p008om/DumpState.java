package com.android.server.p008om;

import android.content.om.OverlayIdentifier;
/* renamed from: com.android.server.om.DumpState */
/* loaded from: classes2.dex */
public final class DumpState {
    public String mField;
    public String mOverlayName;
    public String mPackageName;
    public int mUserId = -1;
    public boolean mVerbose;

    public void setUserId(int i) {
        this.mUserId = i;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public void setOverlyIdentifier(String str) {
        OverlayIdentifier fromString = OverlayIdentifier.fromString(str);
        this.mPackageName = fromString.getPackageName();
        this.mOverlayName = fromString.getOverlayName();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getOverlayName() {
        return this.mOverlayName;
    }

    public void setField(String str) {
        this.mField = str;
    }

    public String getField() {
        return this.mField;
    }

    public void setVerbose(boolean z) {
        this.mVerbose = z;
    }

    public boolean isVerbose() {
        return this.mVerbose;
    }
}
