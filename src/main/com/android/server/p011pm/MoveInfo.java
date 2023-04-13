package com.android.server.p011pm;
/* renamed from: com.android.server.pm.MoveInfo */
/* loaded from: classes2.dex */
public final class MoveInfo {
    public final int mAppId;
    public final String mFromCodePath;
    public final String mFromUuid;
    public final int mMoveId;
    public final String mPackageName;
    public final String mSeInfo;
    public final int mTargetSdkVersion;
    public final String mToUuid;

    public MoveInfo(int i, String str, String str2, String str3, int i2, String str4, int i3, String str5) {
        this.mMoveId = i;
        this.mFromUuid = str;
        this.mToUuid = str2;
        this.mPackageName = str3;
        this.mAppId = i2;
        this.mSeInfo = str4;
        this.mTargetSdkVersion = i3;
        this.mFromCodePath = str5;
    }
}
