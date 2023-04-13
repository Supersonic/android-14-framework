package com.android.server;

import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.CpuUsageInfo;
import android.os.IHardwarePropertiesManager;
import android.os.UserHandle;
import com.android.internal.util.DumpUtils;
import com.android.server.p013vr.VrManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
/* loaded from: classes.dex */
public class HardwarePropertiesManagerService extends IHardwarePropertiesManager.Stub {
    public final AppOpsManager mAppOps;
    public final Context mContext;
    public final Object mLock;

    private static native CpuUsageInfo[] nativeGetCpuUsages();

    private static native float[] nativeGetDeviceTemperatures(int i, int i2);

    private static native float[] nativeGetFanSpeeds();

    private static native void nativeInit();

    public HardwarePropertiesManagerService(Context context) {
        Object obj = new Object();
        this.mLock = obj;
        this.mContext = context;
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
        synchronized (obj) {
            nativeInit();
        }
    }

    public float[] getDeviceTemperatures(String str, int i, int i2) throws SecurityException {
        float[] nativeGetDeviceTemperatures;
        enforceHardwarePropertiesRetrievalAllowed(str);
        synchronized (this.mLock) {
            nativeGetDeviceTemperatures = nativeGetDeviceTemperatures(i, i2);
        }
        return nativeGetDeviceTemperatures;
    }

    public CpuUsageInfo[] getCpuUsages(String str) throws SecurityException {
        CpuUsageInfo[] nativeGetCpuUsages;
        enforceHardwarePropertiesRetrievalAllowed(str);
        synchronized (this.mLock) {
            nativeGetCpuUsages = nativeGetCpuUsages();
        }
        return nativeGetCpuUsages;
    }

    public float[] getFanSpeeds(String str) throws SecurityException {
        float[] nativeGetFanSpeeds;
        enforceHardwarePropertiesRetrievalAllowed(str);
        synchronized (this.mLock) {
            nativeGetFanSpeeds = nativeGetFanSpeeds();
        }
        return nativeGetFanSpeeds;
    }

    public final String getCallingPackageName() {
        PackageManager packageManager = this.mContext.getPackageManager();
        int callingUid = Binder.getCallingUid();
        String[] packagesForUid = packageManager.getPackagesForUid(callingUid);
        if (packagesForUid != null && packagesForUid.length > 0) {
            return packagesForUid[0];
        }
        String nameForUid = packageManager.getNameForUid(callingUid);
        return nameForUid != null ? nameForUid : String.valueOf(callingUid);
    }

    public final void dumpTempValues(String str, PrintWriter printWriter, int i, String str2) {
        dumpTempValues(str, printWriter, i, str2, "temperatures: ", 0);
        dumpTempValues(str, printWriter, i, str2, "throttling temperatures: ", 1);
        dumpTempValues(str, printWriter, i, str2, "shutdown temperatures: ", 2);
        dumpTempValues(str, printWriter, i, str2, "vr throttling temperatures: ", 3);
    }

    public final void dumpTempValues(String str, PrintWriter printWriter, int i, String str2, String str3, int i2) {
        printWriter.println(str2 + str3 + Arrays.toString(getDeviceTemperatures(str, i, i2)));
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "HardwarePropertiesManagerService", printWriter)) {
            printWriter.println("****** Dump of HardwarePropertiesManagerService ******");
            String callingPackageName = getCallingPackageName();
            dumpTempValues(callingPackageName, printWriter, 0, "CPU ");
            dumpTempValues(callingPackageName, printWriter, 1, "GPU ");
            dumpTempValues(callingPackageName, printWriter, 2, "Battery ");
            dumpTempValues(callingPackageName, printWriter, 3, "Skin ");
            float[] fanSpeeds = getFanSpeeds(callingPackageName);
            printWriter.println("Fan speed: " + Arrays.toString(fanSpeeds) + "\n");
            CpuUsageInfo[] cpuUsages = getCpuUsages(callingPackageName);
            for (int i = 0; i < cpuUsages.length; i++) {
                printWriter.println("Cpu usage of core: " + i + ", active = " + cpuUsages[i].getActive() + ", total = " + cpuUsages[i].getTotal());
            }
            printWriter.println("****** End of HardwarePropertiesManagerService dump ******");
        }
    }

    public final void enforceHardwarePropertiesRetrievalAllowed(String str) throws SecurityException {
        this.mAppOps.checkPackage(Binder.getCallingUid(), str);
        int userId = UserHandle.getUserId(Binder.getCallingUid());
        VrManagerInternal vrManagerInternal = (VrManagerInternal) LocalServices.getService(VrManagerInternal.class);
        if (((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).isDeviceOwnerApp(str) || this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") == 0) {
            return;
        }
        if (vrManagerInternal == null || !vrManagerInternal.isCurrentVrListener(str, userId)) {
            throw new SecurityException("The caller is neither a device owner, nor holding the DEVICE_POWER permission, nor the current VrListener.");
        }
    }
}
