package com.android.server.app;

import android.content.pm.PackageManager;
import android.os.UserHandle;
/* loaded from: classes.dex */
public final class GameClassifierImpl implements GameClassifier {
    public final PackageManager mPackageManager;

    public GameClassifierImpl(PackageManager packageManager) {
        this.mPackageManager = packageManager;
    }

    @Override // com.android.server.app.GameClassifier
    public boolean isGame(String str, UserHandle userHandle) {
        try {
            return this.mPackageManager.getApplicationInfoAsUser(str, 0, userHandle.getIdentifier()).category == 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }
}
