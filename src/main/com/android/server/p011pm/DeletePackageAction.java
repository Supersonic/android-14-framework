package com.android.server.p011pm;

import android.os.UserHandle;
/* renamed from: com.android.server.pm.DeletePackageAction */
/* loaded from: classes2.dex */
public final class DeletePackageAction {
    public final PackageSetting mDeletingPs;
    public final PackageSetting mDisabledPs;
    public final int mFlags;
    public final PackageRemovedInfo mRemovedInfo;
    public final UserHandle mUser;

    public DeletePackageAction(PackageSetting packageSetting, PackageSetting packageSetting2, PackageRemovedInfo packageRemovedInfo, int i, UserHandle userHandle) {
        this.mDeletingPs = packageSetting;
        this.mDisabledPs = packageSetting2;
        this.mRemovedInfo = packageRemovedInfo;
        this.mFlags = i;
        this.mUser = userHandle;
    }
}
