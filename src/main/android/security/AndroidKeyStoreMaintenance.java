package android.security;

import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.security.maintenance.IKeystoreMaintenance;
import android.system.keystore2.KeyDescriptor;
import android.util.Log;
/* loaded from: classes3.dex */
public class AndroidKeyStoreMaintenance {
    public static final int INVALID_ARGUMENT = 20;
    public static final int KEY_NOT_FOUND = 7;
    public static final int PERMISSION_DENIED = 6;
    public static final int SYSTEM_ERROR = 4;
    private static final String TAG = "AndroidKeyStoreMaintenance";

    private static IKeystoreMaintenance getService() {
        return IKeystoreMaintenance.Stub.asInterface(ServiceManager.checkService("android.security.maintenance"));
    }

    public static int onUserAdded(int userId) {
        try {
            getService().onUserAdded(userId);
            return 0;
        } catch (ServiceSpecificException e) {
            Log.m109e(TAG, "onUserAdded failed", e);
            return e.errorCode;
        } catch (Exception e2) {
            Log.m109e(TAG, "Can not connect to keystore", e2);
            return 4;
        }
    }

    public static int onUserRemoved(int userId) {
        try {
            getService().onUserRemoved(userId);
            return 0;
        } catch (ServiceSpecificException e) {
            Log.m109e(TAG, "onUserRemoved failed", e);
            return e.errorCode;
        } catch (Exception e2) {
            Log.m109e(TAG, "Can not connect to keystore", e2);
            return 4;
        }
    }

    public static int onUserPasswordChanged(int userId, byte[] password) {
        try {
            getService().onUserPasswordChanged(userId, password);
            return 0;
        } catch (ServiceSpecificException e) {
            Log.m109e(TAG, "onUserPasswordChanged failed", e);
            return e.errorCode;
        } catch (Exception e2) {
            Log.m109e(TAG, "Can not connect to keystore", e2);
            return 4;
        }
    }

    public static int clearNamespace(int domain, long namespace) {
        try {
            getService().clearNamespace(domain, namespace);
            return 0;
        } catch (ServiceSpecificException e) {
            Log.m109e(TAG, "clearNamespace failed", e);
            return e.errorCode;
        } catch (Exception e2) {
            Log.m109e(TAG, "Can not connect to keystore", e2);
            return 4;
        }
    }

    public static int getState(int userId) {
        try {
            return getService().getState(userId);
        } catch (ServiceSpecificException e) {
            Log.m109e(TAG, "getState failed", e);
            return e.errorCode;
        } catch (Exception e2) {
            Log.m109e(TAG, "Can not connect to keystore", e2);
            return 4;
        }
    }

    public static void onDeviceOffBody() {
        try {
            getService().onDeviceOffBody();
        } catch (Exception e) {
            Log.m109e(TAG, "Error while reporting device off body event.", e);
        }
    }

    public static int migrateKeyNamespace(KeyDescriptor source, KeyDescriptor destination) {
        try {
            getService().migrateKeyNamespace(source, destination);
            return 0;
        } catch (ServiceSpecificException e) {
            Log.m109e(TAG, "migrateKeyNamespace failed", e);
            return e.errorCode;
        } catch (Exception e2) {
            Log.m109e(TAG, "Can not connect to keystore", e2);
            return 4;
        }
    }
}
