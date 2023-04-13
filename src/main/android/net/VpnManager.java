package android.net;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.p008os.RemoteException;
import com.android.internal.C4057R;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.GeneralSecurityException;
import java.util.List;
/* loaded from: classes2.dex */
public class VpnManager {
    public static final String ACTION_VPN_MANAGER_EVENT = "android.net.action.VPN_MANAGER_EVENT";
    public static final String CATEGORY_EVENT_ALWAYS_ON_STATE_CHANGED = "android.net.category.EVENT_ALWAYS_ON_STATE_CHANGED";
    public static final String CATEGORY_EVENT_DEACTIVATED_BY_USER = "android.net.category.EVENT_DEACTIVATED_BY_USER";
    public static final String CATEGORY_EVENT_IKE_ERROR = "android.net.category.EVENT_IKE_ERROR";
    public static final String CATEGORY_EVENT_NETWORK_ERROR = "android.net.category.EVENT_NETWORK_ERROR";
    public static final int ERROR_CLASS_NOT_RECOVERABLE = 1;
    public static final int ERROR_CLASS_RECOVERABLE = 2;
    public static final int ERROR_CODE_NETWORK_IO = 3;
    public static final int ERROR_CODE_NETWORK_LOST = 2;
    public static final int ERROR_CODE_NETWORK_PROTOCOL_TIMEOUT = 1;
    public static final int ERROR_CODE_NETWORK_UNKNOWN_HOST = 0;
    public static final String EXTRA_ERROR_CLASS = "android.net.extra.ERROR_CLASS";
    public static final String EXTRA_ERROR_CODE = "android.net.extra.ERROR_CODE";
    public static final String EXTRA_SESSION_KEY = "android.net.extra.SESSION_KEY";
    public static final String EXTRA_TIMESTAMP_MILLIS = "android.net.extra.TIMESTAMP_MILLIS";
    public static final String EXTRA_UNDERLYING_LINK_PROPERTIES = "android.net.extra.UNDERLYING_LINK_PROPERTIES";
    public static final String EXTRA_UNDERLYING_NETWORK = "android.net.extra.UNDERLYING_NETWORK";
    public static final String EXTRA_UNDERLYING_NETWORK_CAPABILITIES = "android.net.extra.UNDERLYING_NETWORK_CAPABILITIES";
    public static final String EXTRA_VPN_PROFILE_STATE = "android.net.extra.VPN_PROFILE_STATE";
    public static final String NOTIFICATION_CHANNEL_VPN = "VPN";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int TYPE_VPN_LEGACY = 3;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int TYPE_VPN_NONE = -1;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int TYPE_VPN_OEM = 4;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int TYPE_VPN_PLATFORM = 2;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int TYPE_VPN_SERVICE = 1;
    private final Context mContext;
    private final IVpnManager mService;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface VpnType {
    }

    private static Intent getIntentForConfirmation() {
        Intent intent = new Intent();
        ComponentName componentName = ComponentName.unflattenFromString(Resources.getSystem().getString(C4057R.string.config_platformVpnConfirmDialogComponent));
        intent.setComponent(componentName);
        return intent;
    }

    public VpnManager(Context ctx, IVpnManager service) {
        this.mContext = (Context) Preconditions.checkNotNull(ctx, "missing Context");
        this.mService = (IVpnManager) Preconditions.checkNotNull(service, "missing IVpnManager");
    }

    public Intent provisionVpnProfile(PlatformVpnProfile profile) {
        try {
            VpnProfile internalProfile = profile.toVpnProfile();
            try {
                if (this.mService.provisionVpnProfile(internalProfile, this.mContext.getOpPackageName())) {
                    return null;
                }
                return getIntentForConfirmation();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        } catch (IOException | GeneralSecurityException e2) {
            throw new IllegalArgumentException("Failed to serialize PlatformVpnProfile", e2);
        }
    }

    public void deleteProvisionedVpnProfile() {
        try {
            this.mService.deleteVpnProfile(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String startProvisionedVpnProfileSession() {
        try {
            return this.mService.startVpnProfile(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void startProvisionedVpnProfile() {
        startProvisionedVpnProfileSession();
    }

    public void stopProvisionedVpnProfile() {
        try {
            this.mService.stopVpnProfile(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public VpnConfig getVpnConfig(int userId) {
        try {
            return this.mService.getVpnConfig(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public VpnProfileState getProvisionedVpnProfileState() {
        try {
            return this.mService.getProvisionedVpnProfileState(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void factoryReset() {
        try {
            this.mService.factoryReset();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean prepareVpn(String oldPackage, String newPackage, int userId) {
        try {
            return this.mService.prepareVpn(oldPackage, newPackage, userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setVpnPackageAuthorization(String packageName, int userId, int vpnType) {
        try {
            this.mService.setVpnPackageAuthorization(packageName, userId, vpnType);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAlwaysOnVpnPackageSupportedForUser(int userId, String vpnPackage) {
        try {
            return this.mService.isAlwaysOnVpnPackageSupported(userId, vpnPackage);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean setAlwaysOnVpnPackageForUser(int userId, String vpnPackage, boolean lockdownEnabled, List<String> lockdownAllowlist) {
        try {
            return this.mService.setAlwaysOnVpnPackage(userId, vpnPackage, lockdownEnabled, lockdownAllowlist);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getAlwaysOnVpnPackageForUser(int userId) {
        try {
            return this.mService.getAlwaysOnVpnPackage(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isVpnLockdownEnabled(int userId) {
        try {
            return this.mService.isVpnLockdownEnabled(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean setAppExclusionList(int userId, String vpnPackage, List<String> excludedApps) {
        try {
            return this.mService.setAppExclusionList(userId, vpnPackage, excludedApps);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<String> getAppExclusionList(int userId, String vpnPackage) {
        try {
            return this.mService.getAppExclusionList(userId, vpnPackage);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<String> getVpnLockdownAllowlist(int userId) {
        try {
            return this.mService.getVpnLockdownAllowlist(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public LegacyVpnInfo getLegacyVpnInfo(int userId) {
        try {
            return this.mService.getLegacyVpnInfo(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void startLegacyVpn(VpnProfile profile) {
        try {
            this.mService.startLegacyVpn(profile);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean updateLockdownVpn() {
        try {
            return this.mService.updateLockdownVpn();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
