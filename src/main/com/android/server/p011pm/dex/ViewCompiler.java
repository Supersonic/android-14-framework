package com.android.server.p011pm.dex;

import android.os.Binder;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.parsing.PackageInfoUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import java.io.File;
/* renamed from: com.android.server.pm.dex.ViewCompiler */
/* loaded from: classes2.dex */
public class ViewCompiler {
    public final Object mInstallLock;
    @GuardedBy({"mInstallLock"})
    public final Installer mInstaller;

    public ViewCompiler(Object obj, Installer installer) {
        this.mInstallLock = obj;
        this.mInstaller = installer;
    }

    public boolean compileLayouts(AndroidPackage androidPackage) {
        boolean compileLayouts;
        try {
            String packageName = androidPackage.getPackageName();
            String baseApkPath = androidPackage.getBaseApkPath();
            File dataDir = PackageInfoUtils.getDataDir(androidPackage, UserHandle.myUserId());
            String str = dataDir.getAbsolutePath() + "/code_cache/compiled_view.dex";
            Log.i("PackageManager", "Compiling layouts in " + packageName + " (" + baseApkPath + ") to " + str);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            synchronized (this.mInstallLock) {
                compileLayouts = this.mInstaller.compileLayouts(baseApkPath, packageName, str, androidPackage.getUid());
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return compileLayouts;
        } catch (Throwable th) {
            Log.e("PackageManager", "Failed to compile layouts", th);
            return false;
        }
    }
}
