package com.android.server.p011pm.parsing.library;

import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import com.android.server.p011pm.pkg.AndroidPackage;
@VisibleForTesting
/* renamed from: com.android.server.pm.parsing.library.OrgApacheHttpLegacyUpdater */
/* loaded from: classes2.dex */
public class OrgApacheHttpLegacyUpdater extends PackageSharedLibraryUpdater {
    public static boolean apkTargetsApiLevelLessThanOrEqualToOMR1(AndroidPackage androidPackage) {
        return androidPackage.getTargetSdkVersion() < 28;
    }

    @Override // com.android.server.p011pm.parsing.library.PackageSharedLibraryUpdater
    public void updatePackage(ParsedPackage parsedPackage, boolean z, boolean z2) {
        if (apkTargetsApiLevelLessThanOrEqualToOMR1(parsedPackage)) {
            prefixRequiredLibrary(parsedPackage, "org.apache.http.legacy");
        }
    }
}
