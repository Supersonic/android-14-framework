package android.app.backup;

import android.annotation.SystemApi;
/* loaded from: classes.dex */
public abstract class RestoreObserver {
    @SystemApi
    public void restoreSetsAvailable(RestoreSet[] result) {
    }

    public void restoreStarting(int numPackages) {
    }

    public void onUpdate(int nowBeingRestored, String currentPackage) {
    }

    public void restoreFinished(int error) {
    }
}
