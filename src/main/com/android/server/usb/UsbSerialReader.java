package com.android.server.usb;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbSerialReader;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Binder;
import android.os.RemoteException;
import android.os.UserHandle;
import com.android.internal.util.ArrayUtils;
/* loaded from: classes2.dex */
public class UsbSerialReader extends IUsbSerialReader.Stub {
    public final Context mContext;
    public Object mDevice;
    public final UsbPermissionManager mPermissionManager;
    public final String mSerialNumber;

    public UsbSerialReader(Context context, UsbPermissionManager usbPermissionManager, String str) {
        this.mContext = context;
        this.mPermissionManager = usbPermissionManager;
        this.mSerialNumber = str;
    }

    public void setDevice(Object obj) {
        this.mDevice = obj;
    }

    public String getSerial(String str) throws RemoteException {
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        if (callingUid != 1000) {
            enforcePackageBelongsToUid(callingUid, str);
            UserHandle callingUserHandle = Binder.getCallingUserHandle();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    if (this.mContext.getPackageManager().getPackageInfoAsUser(str, 0, callingUserHandle.getIdentifier()).applicationInfo.targetSdkVersion >= 29 && this.mContext.checkPermission("android.permission.MANAGE_USB", callingPid, callingUid) == -1) {
                        int userId = UserHandle.getUserId(callingUid);
                        if (this.mDevice instanceof UsbDevice) {
                            this.mPermissionManager.getPermissionsForUser(userId).checkPermission((UsbDevice) this.mDevice, str, callingPid, callingUid);
                        } else {
                            this.mPermissionManager.getPermissionsForUser(userId).checkPermission((UsbAccessory) this.mDevice, callingPid, callingUid);
                        }
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    throw new RemoteException("package " + str + " cannot be found");
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return this.mSerialNumber;
    }

    public final void enforcePackageBelongsToUid(int i, String str) {
        if (ArrayUtils.contains(this.mContext.getPackageManager().getPackagesForUid(i), str)) {
            return;
        }
        throw new IllegalArgumentException(str + " does to belong to the " + i);
    }
}
