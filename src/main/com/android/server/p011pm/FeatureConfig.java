package com.android.server.p011pm;

import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
/* JADX INFO: Access modifiers changed from: package-private */
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
/* renamed from: com.android.server.pm.FeatureConfig */
/* loaded from: classes2.dex */
public interface FeatureConfig {
    void enableLogging(int i, boolean z);

    boolean isGloballyEnabled();

    boolean isLoggingEnabled(int i);

    void onSystemReady();

    boolean packageIsEnabled(AndroidPackage androidPackage);

    FeatureConfig snapshot();

    void updatePackageState(PackageStateInternal packageStateInternal, boolean z);
}
