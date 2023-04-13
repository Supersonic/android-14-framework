package com.android.server.p011pm.parsing.pkg;

import com.android.internal.content.om.OverlayConfig;
import com.android.server.p011pm.pkg.AndroidPackage;
/* renamed from: com.android.server.pm.parsing.pkg.AndroidPackageInternal */
/* loaded from: classes2.dex */
public interface AndroidPackageInternal extends AndroidPackage, OverlayConfig.PackageProvider.Package {
    String[] getUsesLibrariesSorted();

    String[] getUsesOptionalLibrariesSorted();

    String[] getUsesSdkLibrariesSorted();

    String[] getUsesStaticLibrariesSorted();
}
