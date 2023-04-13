package android.net.wifi;

import android.annotation.SystemApi;
import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.security.legacykeystore.ILegacyKeystore;
import android.util.Log;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* loaded from: classes2.dex */
public final class WifiKeystore {
    private static final String LEGACY_KEYSTORE_SERVICE_NAME = "android.security.legacykeystore";
    private static final String TAG = "WifiKeystore";

    private static ILegacyKeystore getService() {
        return ILegacyKeystore.Stub.asInterface(ServiceManager.checkService(LEGACY_KEYSTORE_SERVICE_NAME));
    }

    WifiKeystore() {
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static boolean put(String alias, byte[] blob) {
        try {
            Log.m108i(TAG, "put blob. alias " + alias);
            getService().put(alias, 1010, blob);
            return true;
        } catch (Exception e) {
            Log.m109e(TAG, "Failed to put blob.", e);
            return false;
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static byte[] get(String alias) {
        try {
            Log.m108i(TAG, "get blob. alias " + alias);
            return getService().get(alias, 1010);
        } catch (ServiceSpecificException e) {
            if (e.errorCode != 7) {
                Log.m109e(TAG, "Failed to get blob.", e);
                return null;
            }
            return null;
        } catch (Exception e2) {
            Log.m109e(TAG, "Failed to get blob.", e2);
            return null;
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static boolean remove(String alias) {
        try {
            getService().remove(alias, 1010);
            return true;
        } catch (ServiceSpecificException e) {
            if (e.errorCode != 7) {
                Log.m109e(TAG, "Failed to remove blob.", e);
                return false;
            }
            return false;
        } catch (Exception e2) {
            Log.m109e(TAG, "Failed to remove blob.", e2);
            return false;
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static String[] list(String prefix) {
        try {
            String[] aliases = getService().list(prefix, 1010);
            for (int i = 0; i < aliases.length; i++) {
                aliases[i] = aliases[i].substring(prefix.length());
            }
            return aliases;
        } catch (Exception e) {
            Log.m109e(TAG, "Failed to list blobs.", e);
            return new String[0];
        }
    }
}
