package android.app;

import android.content.Intent;
import android.content.p001pm.IPackageInstallObserver2;
import android.p008os.Bundle;
/* loaded from: classes.dex */
public class PackageInstallObserver {
    private final IPackageInstallObserver2.Stub mBinder = new IPackageInstallObserver2.Stub() { // from class: android.app.PackageInstallObserver.1
        @Override // android.content.p001pm.IPackageInstallObserver2
        public void onUserActionRequired(Intent intent) {
            PackageInstallObserver.this.onUserActionRequired(intent);
        }

        @Override // android.content.p001pm.IPackageInstallObserver2
        public void onPackageInstalled(String basePackageName, int returnCode, String msg, Bundle extras) {
            PackageInstallObserver.this.onPackageInstalled(basePackageName, returnCode, msg, extras);
        }
    };

    public IPackageInstallObserver2 getBinder() {
        return this.mBinder;
    }

    public void onUserActionRequired(Intent intent) {
    }

    public void onPackageInstalled(String basePackageName, int returnCode, String msg, Bundle extras) {
    }
}
