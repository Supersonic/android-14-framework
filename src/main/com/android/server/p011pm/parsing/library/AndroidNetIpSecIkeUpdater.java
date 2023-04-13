package com.android.server.p011pm.parsing.library;

import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
@VisibleForTesting
/* renamed from: com.android.server.pm.parsing.library.AndroidNetIpSecIkeUpdater */
/* loaded from: classes2.dex */
public class AndroidNetIpSecIkeUpdater extends PackageSharedLibraryUpdater {
    @Override // com.android.server.p011pm.parsing.library.PackageSharedLibraryUpdater
    public void updatePackage(ParsedPackage parsedPackage, boolean z, boolean z2) {
        PackageSharedLibraryUpdater.removeLibrary(parsedPackage, "android.net.ipsec.ike");
    }
}
