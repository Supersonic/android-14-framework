package com.android.server.p011pm;

import android.os.UserHandle;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import com.android.server.p011pm.pkg.AndroidPackage;
/* JADX INFO: Access modifiers changed from: package-private */
@VisibleForTesting
/* renamed from: com.android.server.pm.ScanRequest */
/* loaded from: classes2.dex */
public final class ScanRequest {
    public final String mCpuAbiOverride;
    public final PackageSetting mDisabledPkgSetting;
    public final boolean mIsPlatformPackage;
    public final AndroidPackage mOldPkg;
    public final PackageSetting mOldPkgSetting;
    public final SharedUserSetting mOldSharedUserSetting;
    public final PackageSetting mOriginalPkgSetting;
    public final int mParseFlags;
    public final ParsedPackage mParsedPackage;
    public final PackageSetting mPkgSetting;
    public final String mRealPkgName;
    public final int mScanFlags;
    public final SharedUserSetting mSharedUserSetting;
    public final UserHandle mUser;

    public ScanRequest(ParsedPackage parsedPackage, SharedUserSetting sharedUserSetting, AndroidPackage androidPackage, PackageSetting packageSetting, SharedUserSetting sharedUserSetting2, PackageSetting packageSetting2, PackageSetting packageSetting3, String str, int i, int i2, boolean z, UserHandle userHandle, String str2) {
        this.mParsedPackage = parsedPackage;
        this.mOldPkg = androidPackage;
        this.mPkgSetting = packageSetting;
        this.mOldSharedUserSetting = sharedUserSetting;
        this.mSharedUserSetting = sharedUserSetting2;
        this.mOldPkgSetting = packageSetting == null ? null : new PackageSetting(packageSetting);
        this.mDisabledPkgSetting = packageSetting2;
        this.mOriginalPkgSetting = packageSetting3;
        this.mRealPkgName = str;
        this.mParseFlags = i;
        this.mScanFlags = i2;
        this.mIsPlatformPackage = z;
        this.mUser = userHandle;
        this.mCpuAbiOverride = str2;
    }
}
