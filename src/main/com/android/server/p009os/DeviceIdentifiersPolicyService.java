package com.android.server.p009os;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IDeviceIdentifiersPolicyService;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import com.android.internal.telephony.TelephonyPermissions;
import com.android.server.SystemService;
/* renamed from: com.android.server.os.DeviceIdentifiersPolicyService */
/* loaded from: classes2.dex */
public final class DeviceIdentifiersPolicyService extends SystemService {
    public DeviceIdentifiersPolicyService(Context context) {
        super(context);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("device_identifiers", new DeviceIdentifiersPolicy(getContext()));
    }

    /* renamed from: com.android.server.os.DeviceIdentifiersPolicyService$DeviceIdentifiersPolicy */
    /* loaded from: classes2.dex */
    public static final class DeviceIdentifiersPolicy extends IDeviceIdentifiersPolicyService.Stub {
        public final Context mContext;

        public DeviceIdentifiersPolicy(Context context) {
            this.mContext = context;
        }

        public String getSerial() throws RemoteException {
            return !TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(this.mContext, (String) null, (String) null, "getSerial") ? "unknown" : SystemProperties.get("ro.serialno", "unknown");
        }

        public String getSerialForPackage(String str, String str2) throws RemoteException {
            if (checkPackageBelongsToCaller(str)) {
                return !TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(this.mContext, str, str2, "getSerial") ? "unknown" : SystemProperties.get("ro.serialno", "unknown");
            }
            throw new IllegalArgumentException("Invalid callingPackage or callingPackage does not belong to caller's uid:" + Binder.getCallingUid());
        }

        public final boolean checkPackageBelongsToCaller(String str) {
            int callingUid = Binder.getCallingUid();
            try {
                return this.mContext.getPackageManager().getPackageUidAsUser(str, UserHandle.getUserId(callingUid)) == callingUid;
            } catch (PackageManager.NameNotFoundException unused) {
                return false;
            }
        }
    }
}
