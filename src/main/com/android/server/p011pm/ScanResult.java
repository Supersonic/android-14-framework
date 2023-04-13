package com.android.server.p011pm;

import android.content.pm.SharedLibraryInfo;
import com.android.internal.annotations.VisibleForTesting;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
@VisibleForTesting
/* renamed from: com.android.server.pm.ScanResult */
/* loaded from: classes2.dex */
public final class ScanResult {
    public final List<String> mChangedAbiCodePath;
    public final List<SharedLibraryInfo> mDynamicSharedLibraryInfos;
    public final boolean mExistingSettingCopied;
    public final PackageSetting mPkgSetting;
    public final int mPreviousAppId = -1;
    public final ScanRequest mRequest;
    public final SharedLibraryInfo mSdkSharedLibraryInfo;
    public final SharedLibraryInfo mStaticSharedLibraryInfo;

    public ScanResult(ScanRequest scanRequest, PackageSetting packageSetting, List<String> list, boolean z, int i, SharedLibraryInfo sharedLibraryInfo, SharedLibraryInfo sharedLibraryInfo2, List<SharedLibraryInfo> list2) {
        this.mRequest = scanRequest;
        this.mPkgSetting = packageSetting;
        this.mChangedAbiCodePath = list;
        this.mExistingSettingCopied = z;
        this.mSdkSharedLibraryInfo = sharedLibraryInfo;
        this.mStaticSharedLibraryInfo = sharedLibraryInfo2;
        this.mDynamicSharedLibraryInfos = list2;
    }
}
