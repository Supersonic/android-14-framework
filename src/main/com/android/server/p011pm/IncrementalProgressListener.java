package com.android.server.p011pm;

import android.content.pm.IPackageLoadingProgressCallback;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.mutate.PackageStateWrite;
import java.util.function.Consumer;
/* renamed from: com.android.server.pm.IncrementalProgressListener */
/* loaded from: classes2.dex */
public final class IncrementalProgressListener extends IPackageLoadingProgressCallback.Stub {
    public final String mPackageName;
    public final PackageManagerService mPm;

    public IncrementalProgressListener(String str, PackageManagerService packageManagerService) {
        this.mPackageName = str;
        this.mPm = packageManagerService;
    }

    public void onPackageLoadingProgressChanged(final float f) {
        PackageStateInternal packageStateInternal = this.mPm.snapshotComputer().getPackageStateInternal(this.mPackageName);
        if (packageStateInternal != null && packageStateInternal.isLoading()) {
            this.mPm.commitPackageStateMutation(null, this.mPackageName, new Consumer() { // from class: com.android.server.pm.IncrementalProgressListener$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((PackageStateWrite) obj).setLoadingProgress(f);
                }
            });
            if (Math.abs(1.0f - f) < 1.0E-8f) {
                this.mPm.mIncrementalManager.unregisterLoadingProgressCallbacks(packageStateInternal.getPathString());
                this.mPm.scheduleWriteSettings();
            }
        }
    }
}
