package android.security;

import android.hardware.security.keymint.HardwareAuthToken;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.security.authorization.IKeystoreAuthorization;
import android.util.Log;
/* loaded from: classes3.dex */
public class Authorization {
    public static final int SYSTEM_ERROR = 4;
    private static final String TAG = "KeystoreAuthorization";

    private static IKeystoreAuthorization getService() {
        return IKeystoreAuthorization.Stub.asInterface(ServiceManager.checkService("android.security.authorization"));
    }

    public static int addAuthToken(HardwareAuthToken authToken) {
        try {
            getService().addAuthToken(authToken);
            return 0;
        } catch (RemoteException | NullPointerException e) {
            Log.m103w(TAG, "Can not connect to keystore", e);
            return 4;
        } catch (ServiceSpecificException e2) {
            return e2.errorCode;
        }
    }

    public static int addAuthToken(byte[] authToken) {
        return addAuthToken(AuthTokenUtils.toHardwareAuthToken(authToken));
    }

    public static int onLockScreenEvent(boolean locked, int userId, byte[] syntheticPassword, long[] unlockingSids) {
        try {
            if (!locked) {
                getService().onLockScreenEvent(0, userId, syntheticPassword, unlockingSids);
            } else {
                getService().onLockScreenEvent(1, userId, null, unlockingSids);
            }
            return 0;
        } catch (RemoteException | NullPointerException e) {
            Log.m103w(TAG, "Can not connect to keystore", e);
            return 4;
        } catch (ServiceSpecificException e2) {
            return e2.errorCode;
        }
    }
}
