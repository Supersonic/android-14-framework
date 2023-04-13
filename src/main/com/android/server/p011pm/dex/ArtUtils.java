package com.android.server.p011pm.dex;

import com.android.server.p011pm.InstructionSets;
import com.android.server.p011pm.PackageDexOptimizer;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import java.io.File;
import java.util.Arrays;
/* renamed from: com.android.server.pm.dex.ArtUtils */
/* loaded from: classes2.dex */
public final class ArtUtils {
    public static ArtPackageInfo createArtPackageInfo(AndroidPackage androidPackage, PackageState packageState) {
        return new ArtPackageInfo(androidPackage.getPackageName(), Arrays.asList(InstructionSets.getAppDexInstructionSets(packageState.getPrimaryCpuAbi(), packageState.getSecondaryCpuAbi())), AndroidPackageUtils.getAllCodePaths(androidPackage), getOatDir(androidPackage, packageState));
    }

    public static String getOatDir(AndroidPackage androidPackage, PackageState packageState) {
        if (AndroidPackageUtils.canHaveOatDir(packageState, androidPackage)) {
            File file = new File(androidPackage.getPath());
            if (file.isDirectory()) {
                return PackageDexOptimizer.getOatDir(file).getAbsolutePath();
            }
            return null;
        }
        return null;
    }
}
