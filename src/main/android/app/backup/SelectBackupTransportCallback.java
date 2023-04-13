package android.app.backup;

import android.annotation.SystemApi;
@SystemApi
/* loaded from: classes.dex */
public abstract class SelectBackupTransportCallback {
    public void onSuccess(String transportName) {
    }

    public void onFailure(int reason) {
    }
}
