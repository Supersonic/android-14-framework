package com.android.server.p011pm.parsing.library;

import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.SystemConfig;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import java.util.ArrayList;
import java.util.List;
@VisibleForTesting
/* renamed from: com.android.server.pm.parsing.library.PackageBackwardCompatibility */
/* loaded from: classes2.dex */
public class PackageBackwardCompatibility extends PackageSharedLibraryUpdater {
    public static final PackageBackwardCompatibility INSTANCE;
    public static final String TAG = "PackageBackwardCompatibility";
    public final boolean mBootClassPathContainsATB;
    public final PackageSharedLibraryUpdater[] mPackageUpdaters;

    static {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new AndroidNetIpSecIkeUpdater());
        arrayList.add(new ComGoogleAndroidMapsUpdater());
        arrayList.add(new OrgApacheHttpLegacyUpdater());
        arrayList.add(new AndroidHidlUpdater());
        arrayList.add(new AndroidTestRunnerSplitUpdater());
        arrayList.add(new ApexSharedLibraryUpdater(SystemConfig.getInstance().getSharedLibraries()));
        INSTANCE = new PackageBackwardCompatibility(!addUpdaterForAndroidTestBase(arrayList), (PackageSharedLibraryUpdater[]) arrayList.toArray(new PackageSharedLibraryUpdater[0]));
    }

    public static boolean addUpdaterForAndroidTestBase(List<PackageSharedLibraryUpdater> list) {
        try {
            r1 = ParsingPackage.class.getClassLoader().loadClass("android.content.pm.AndroidTestBaseUpdater") != null;
            String str = TAG;
            Log.i(str, "Loaded android.content.pm.AndroidTestBaseUpdater");
        } catch (ClassNotFoundException unused) {
            String str2 = TAG;
            Log.i(str2, "Could not find android.content.pm.AndroidTestBaseUpdater, ignoring");
        }
        if (r1) {
            list.add(new AndroidTestBaseUpdater());
        } else {
            list.add(new RemoveUnnecessaryAndroidTestBaseLibrary());
        }
        return r1;
    }

    @VisibleForTesting
    public static PackageSharedLibraryUpdater getInstance() {
        return INSTANCE;
    }

    @VisibleForTesting
    public PackageSharedLibraryUpdater[] getPackageUpdaters() {
        return this.mPackageUpdaters;
    }

    public PackageBackwardCompatibility(boolean z, PackageSharedLibraryUpdater[] packageSharedLibraryUpdaterArr) {
        this.mBootClassPathContainsATB = z;
        this.mPackageUpdaters = packageSharedLibraryUpdaterArr;
    }

    @VisibleForTesting
    public static void modifySharedLibraries(ParsedPackage parsedPackage, boolean z, boolean z2) {
        INSTANCE.updatePackage(parsedPackage, z, z2);
    }

    @Override // com.android.server.p011pm.parsing.library.PackageSharedLibraryUpdater
    public void updatePackage(ParsedPackage parsedPackage, boolean z, boolean z2) {
        for (PackageSharedLibraryUpdater packageSharedLibraryUpdater : this.mPackageUpdaters) {
            packageSharedLibraryUpdater.updatePackage(parsedPackage, z, z2);
        }
    }

    @VisibleForTesting
    public static boolean bootClassPathContainsATB() {
        return INSTANCE.mBootClassPathContainsATB;
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.parsing.library.PackageBackwardCompatibility$AndroidTestRunnerSplitUpdater */
    /* loaded from: classes2.dex */
    public static class AndroidTestRunnerSplitUpdater extends PackageSharedLibraryUpdater {
        @Override // com.android.server.p011pm.parsing.library.PackageSharedLibraryUpdater
        public void updatePackage(ParsedPackage parsedPackage, boolean z, boolean z2) {
            prefixImplicitDependency(parsedPackage, "android.test.runner", "android.test.mock");
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.parsing.library.PackageBackwardCompatibility$RemoveUnnecessaryOrgApacheHttpLegacyLibrary */
    /* loaded from: classes2.dex */
    public static class RemoveUnnecessaryOrgApacheHttpLegacyLibrary extends PackageSharedLibraryUpdater {
        @Override // com.android.server.p011pm.parsing.library.PackageSharedLibraryUpdater
        public void updatePackage(ParsedPackage parsedPackage, boolean z, boolean z2) {
            PackageSharedLibraryUpdater.removeLibrary(parsedPackage, "org.apache.http.legacy");
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.parsing.library.PackageBackwardCompatibility$RemoveUnnecessaryAndroidTestBaseLibrary */
    /* loaded from: classes2.dex */
    public static class RemoveUnnecessaryAndroidTestBaseLibrary extends PackageSharedLibraryUpdater {
        @Override // com.android.server.p011pm.parsing.library.PackageSharedLibraryUpdater
        public void updatePackage(ParsedPackage parsedPackage, boolean z, boolean z2) {
            PackageSharedLibraryUpdater.removeLibrary(parsedPackage, "android.test.base");
        }
    }
}
