package com.android.server.p011pm;

import android.util.ArraySet;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import java.io.File;
@VisibleForTesting
/* renamed from: com.android.server.pm.PackageAbiHelper */
/* loaded from: classes2.dex */
public interface PackageAbiHelper {
    NativeLibraryPaths deriveNativeLibraryPaths(AndroidPackage androidPackage, boolean z, boolean z2, File file);

    Pair<Abis, NativeLibraryPaths> derivePackageAbi(AndroidPackage androidPackage, boolean z, boolean z2, String str, File file) throws PackageManagerException;

    String getAdjustedAbiForSharedUser(ArraySet<? extends PackageStateInternal> arraySet, AndroidPackage androidPackage);

    Abis getBundledAppAbis(AndroidPackage androidPackage);

    /* renamed from: com.android.server.pm.PackageAbiHelper$NativeLibraryPaths */
    /* loaded from: classes2.dex */
    public static final class NativeLibraryPaths {
        public final String nativeLibraryDir;
        public final String nativeLibraryRootDir;
        public final boolean nativeLibraryRootRequiresIsa;
        public final String secondaryNativeLibraryDir;

        @VisibleForTesting
        public NativeLibraryPaths(String str, boolean z, String str2, String str3) {
            this.nativeLibraryRootDir = str;
            this.nativeLibraryRootRequiresIsa = z;
            this.nativeLibraryDir = str2;
            this.secondaryNativeLibraryDir = str3;
        }

        public void applyTo(ParsedPackage parsedPackage) {
            parsedPackage.setNativeLibraryRootDir(this.nativeLibraryRootDir).setNativeLibraryRootRequiresIsa(this.nativeLibraryRootRequiresIsa).setNativeLibraryDir(this.nativeLibraryDir).setSecondaryNativeLibraryDir(this.secondaryNativeLibraryDir);
        }
    }

    /* renamed from: com.android.server.pm.PackageAbiHelper$Abis */
    /* loaded from: classes2.dex */
    public static final class Abis {
        public final String primary;
        public final String secondary;

        @VisibleForTesting
        public Abis(String str, String str2) {
            this.primary = str;
            this.secondary = str2;
        }

        public void applyTo(ParsedPackage parsedPackage) {
            parsedPackage.setPrimaryCpuAbi(this.primary).setSecondaryCpuAbi(this.secondary);
        }

        public void applyTo(PackageSetting packageSetting) {
            if (packageSetting != null) {
                packageSetting.setPrimaryCpuAbi(this.primary);
                packageSetting.setSecondaryCpuAbi(this.secondary);
            }
        }
    }
}
