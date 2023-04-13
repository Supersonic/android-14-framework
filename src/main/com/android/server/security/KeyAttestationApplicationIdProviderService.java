package com.android.server.security;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.security.keymaster.IKeyAttestationApplicationIdProvider;
import android.security.keymaster.KeyAttestationApplicationId;
import android.security.keymaster.KeyAttestationPackageInfo;
/* loaded from: classes2.dex */
public class KeyAttestationApplicationIdProviderService extends IKeyAttestationApplicationIdProvider.Stub {
    public PackageManager mPackageManager;

    public KeyAttestationApplicationIdProviderService(Context context) {
        this.mPackageManager = context.getPackageManager();
    }

    public KeyAttestationApplicationId getKeyAttestationApplicationId(int i) throws RemoteException {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 1017 && callingUid != 1076) {
            throw new SecurityException("This service can only be used by Keystore or Credstore");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                String[] packagesForUid = this.mPackageManager.getPackagesForUid(i);
                if (packagesForUid == null) {
                    throw new RemoteException("No packages for uid");
                }
                int userId = UserHandle.getUserId(i);
                KeyAttestationPackageInfo[] keyAttestationPackageInfoArr = new KeyAttestationPackageInfo[packagesForUid.length];
                for (int i2 = 0; i2 < packagesForUid.length; i2++) {
                    PackageInfo packageInfoAsUser = this.mPackageManager.getPackageInfoAsUser(packagesForUid[i2], 64, userId);
                    keyAttestationPackageInfoArr[i2] = new KeyAttestationPackageInfo(packagesForUid[i2], packageInfoAsUser.getLongVersionCode(), packageInfoAsUser.signatures);
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return new KeyAttestationApplicationId(keyAttestationPackageInfoArr);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RemoteException(e.getMessage());
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }
}
