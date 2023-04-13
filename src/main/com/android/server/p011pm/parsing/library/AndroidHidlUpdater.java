package com.android.server.p011pm.parsing.library;

import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
@VisibleForTesting
/* renamed from: com.android.server.pm.parsing.library.AndroidHidlUpdater */
/* loaded from: classes2.dex */
public class AndroidHidlUpdater extends PackageSharedLibraryUpdater {
    @Override // com.android.server.p011pm.parsing.library.PackageSharedLibraryUpdater
    public void updatePackage(ParsedPackage parsedPackage, boolean z, boolean z2) {
        if ((parsedPackage.getTargetSdkVersion() <= 28) && (z || z2)) {
            prefixRequiredLibrary(parsedPackage, "android.hidl.base-V1.0-java");
            prefixRequiredLibrary(parsedPackage, "android.hidl.manager-V1.0-java");
            return;
        }
        PackageSharedLibraryUpdater.removeLibrary(parsedPackage, "android.hidl.base-V1.0-java");
        PackageSharedLibraryUpdater.removeLibrary(parsedPackage, "android.hidl.manager-V1.0-java");
    }
}
