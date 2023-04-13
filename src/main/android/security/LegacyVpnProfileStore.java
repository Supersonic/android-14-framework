package android.security;

import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.security.legacykeystore.ILegacyKeystore;
import android.util.Log;
/* loaded from: classes3.dex */
public class LegacyVpnProfileStore {
    private static final String LEGACY_KEYSTORE_SERVICE_NAME = "android.security.legacykeystore";
    public static final int PROFILE_NOT_FOUND = 7;
    public static final int SYSTEM_ERROR = 4;
    private static final String TAG = "LegacyVpnProfileStore";

    private static ILegacyKeystore getService() {
        return ILegacyKeystore.Stub.asInterface(ServiceManager.checkService(LEGACY_KEYSTORE_SERVICE_NAME));
    }

    public static boolean put(String alias, byte[] profile) {
        try {
            getService().put(alias, -1, profile);
            return true;
        } catch (Exception e) {
            Log.m109e(TAG, "Failed to put vpn profile.", e);
            return false;
        }
    }

    public static byte[] get(String alias) {
        try {
            return getService().get(alias, -1);
        } catch (ServiceSpecificException e) {
            if (e.errorCode != 7) {
                Log.m109e(TAG, "Failed to get vpn profile.", e);
                return null;
            }
            return null;
        } catch (Exception e2) {
            Log.m109e(TAG, "Failed to get vpn profile.", e2);
            return null;
        }
    }

    public static boolean remove(String alias) {
        try {
            getService().remove(alias, -1);
            return true;
        } catch (ServiceSpecificException e) {
            if (e.errorCode != 7) {
                Log.m109e(TAG, "Failed to remove vpn profile.", e);
                return false;
            }
            return false;
        } catch (Exception e2) {
            Log.m109e(TAG, "Failed to remove vpn profile.", e2);
            return false;
        }
    }

    public static String[] list(String prefix) {
        try {
            String[] aliases = getService().list(prefix, -1);
            for (int i = 0; i < aliases.length; i++) {
                aliases[i] = aliases[i].substring(prefix.length());
            }
            return aliases;
        } catch (Exception e) {
            Log.m109e(TAG, "Failed to list vpn profiles.", e);
            return new String[0];
        }
    }
}
