package com.android.server.grammaticalinflection;

import com.android.internal.content.PackageMonitor;
/* loaded from: classes.dex */
public class GrammaticalInflectionPackageMonitor extends PackageMonitor {
    public GrammaticalInflectionBackupHelper mBackupHelper;

    public void onPackageAdded(String str, int i) {
        this.mBackupHelper.onPackageAdded(str, i);
    }

    public void onPackageDataCleared(String str, int i) {
        this.mBackupHelper.onPackageDataCleared();
    }

    public void onPackageRemoved(String str, int i) {
        this.mBackupHelper.onPackageRemoved();
    }
}
