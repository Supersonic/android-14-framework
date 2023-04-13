package android.app;

import android.content.Intent;
import android.content.p001pm.IPackageDeleteObserver2;
/* loaded from: classes.dex */
public class PackageDeleteObserver {
    private final IPackageDeleteObserver2.Stub mBinder = new IPackageDeleteObserver2.Stub() { // from class: android.app.PackageDeleteObserver.1
        @Override // android.content.p001pm.IPackageDeleteObserver2
        public void onUserActionRequired(Intent intent) {
            PackageDeleteObserver.this.onUserActionRequired(intent);
        }

        @Override // android.content.p001pm.IPackageDeleteObserver2
        public void onPackageDeleted(String basePackageName, int returnCode, String msg) {
            PackageDeleteObserver.this.onPackageDeleted(basePackageName, returnCode, msg);
        }
    };

    public IPackageDeleteObserver2 getBinder() {
        return this.mBinder;
    }

    public void onUserActionRequired(Intent intent) {
    }

    public void onPackageDeleted(String basePackageName, int returnCode, String msg) {
    }
}
