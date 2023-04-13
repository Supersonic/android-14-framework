package com.android.server.p011pm.parsing.library;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import java.util.List;
@VisibleForTesting
/* renamed from: com.android.server.pm.parsing.library.PackageSharedLibraryUpdater */
/* loaded from: classes2.dex */
public abstract class PackageSharedLibraryUpdater {
    public abstract void updatePackage(ParsedPackage parsedPackage, boolean z, boolean z2);

    public static void removeLibrary(ParsedPackage parsedPackage, String str) {
        parsedPackage.removeUsesLibrary(str).removeUsesOptionalLibrary(str);
    }

    public static boolean isLibraryPresent(List<String> list, List<String> list2, String str) {
        return ArrayUtils.contains(list, str) || ArrayUtils.contains(list2, str);
    }

    public void prefixImplicitDependency(ParsedPackage parsedPackage, String str, String str2) {
        List<String> usesLibraries = parsedPackage.getUsesLibraries();
        List<String> usesOptionalLibraries = parsedPackage.getUsesOptionalLibraries();
        if (isLibraryPresent(usesLibraries, usesOptionalLibraries, str2)) {
            return;
        }
        if (ArrayUtils.contains(usesLibraries, str)) {
            parsedPackage.addUsesLibrary(0, str2);
        } else if (ArrayUtils.contains(usesOptionalLibraries, str)) {
            parsedPackage.addUsesOptionalLibrary(0, str2);
        }
    }

    public void prefixRequiredLibrary(ParsedPackage parsedPackage, String str) {
        if (isLibraryPresent(parsedPackage.getUsesLibraries(), parsedPackage.getUsesOptionalLibraries(), str)) {
            return;
        }
        parsedPackage.addUsesLibrary(0, str);
    }
}
