package com.android.server.devicepolicy;

import android.app.admin.StartInstallingUpdateCallback;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.os.RecoverySystem;
import android.util.Log;
import com.android.server.devicepolicy.DevicePolicyManagerService;
import java.io.IOException;
/* loaded from: classes.dex */
public class NonAbUpdateInstaller extends UpdateInstaller {
    public NonAbUpdateInstaller(Context context, ParcelFileDescriptor parcelFileDescriptor, StartInstallingUpdateCallback startInstallingUpdateCallback, DevicePolicyManagerService.Injector injector, DevicePolicyConstants devicePolicyConstants) {
        super(context, parcelFileDescriptor, startInstallingUpdateCallback, injector, devicePolicyConstants);
    }

    @Override // com.android.server.devicepolicy.UpdateInstaller
    public void installUpdateInThread() {
        try {
            RecoverySystem.installPackage(this.mContext, this.mCopiedUpdateFile);
            notifyCallbackOnSuccess();
        } catch (IOException e) {
            Log.w("UpdateInstaller", "IO error while trying to install non AB update.", e);
            notifyCallbackOnError(1, Log.getStackTraceString(e));
        }
    }
}
