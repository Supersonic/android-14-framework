package com.android.server.devicepolicy;

import android.app.admin.DeviceAdminService;
import android.app.admin.IDeviceAdminService;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.IndentingPrintWriter;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.server.appbinding.AppBindingUtils;
import com.android.server.devicepolicy.DevicePolicyManagerService;
import com.android.server.p006am.PersistentConnection;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class DeviceAdminServiceController {
    public final DevicePolicyConstants mConstants;
    public final Context mContext;
    public final Handler mHandler;
    public final DevicePolicyManagerService.Injector mInjector;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final SparseArray<Map<String, DevicePolicyServiceConnection>> mConnections = new SparseArray<>();

    /* loaded from: classes.dex */
    public class DevicePolicyServiceConnection extends PersistentConnection<IDeviceAdminService> {
        @Override // com.android.server.p006am.PersistentConnection
        public int getBindFlags() {
            return 67108864;
        }

        public DevicePolicyServiceConnection(int i, ComponentName componentName) {
            super("DevicePolicyManager", DeviceAdminServiceController.this.mContext, DeviceAdminServiceController.this.mHandler, i, componentName, DeviceAdminServiceController.this.mConstants.DAS_DIED_SERVICE_RECONNECT_BACKOFF_SEC, DeviceAdminServiceController.this.mConstants.DAS_DIED_SERVICE_RECONNECT_BACKOFF_INCREASE, DeviceAdminServiceController.this.mConstants.DAS_DIED_SERVICE_RECONNECT_MAX_BACKOFF_SEC, DeviceAdminServiceController.this.mConstants.DAS_DIED_SERVICE_STABLE_CONNECTION_THRESHOLD_SEC);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.p006am.PersistentConnection
        public IDeviceAdminService asInterface(IBinder iBinder) {
            return IDeviceAdminService.Stub.asInterface(iBinder);
        }
    }

    public DeviceAdminServiceController(DevicePolicyManagerService devicePolicyManagerService, DevicePolicyConstants devicePolicyConstants) {
        DevicePolicyManagerService.Injector injector = devicePolicyManagerService.mInjector;
        this.mInjector = injector;
        this.mContext = injector.mContext;
        this.mHandler = new Handler(BackgroundThread.get().getLooper());
        this.mConstants = devicePolicyConstants;
    }

    public final ServiceInfo findService(String str, int i) {
        return AppBindingUtils.findService(str, i, "android.app.action.DEVICE_ADMIN_SERVICE", "android.permission.BIND_DEVICE_ADMIN", DeviceAdminService.class, this.mInjector.getIPackageManager(), new StringBuilder());
    }

    public void startServiceForAdmin(String str, int i, String str2) {
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            synchronized (this.mLock) {
                ServiceInfo findService = findService(str, i);
                if (findService == null) {
                    disconnectServiceOnUserLocked(str, i, str2);
                    return;
                }
                if ((this.mConnections.contains(i) ? this.mConnections.get(i).get(str) : null) != null) {
                    disconnectServiceOnUserLocked(str, i, str2);
                }
                DevicePolicyServiceConnection devicePolicyServiceConnection = new DevicePolicyServiceConnection(i, findService.getComponentName());
                if (!this.mConnections.contains(i)) {
                    this.mConnections.put(i, new HashMap());
                }
                this.mConnections.get(i).put(str, devicePolicyServiceConnection);
                devicePolicyServiceConnection.bind();
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public void stopServiceForAdmin(String str, int i, String str2) {
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            synchronized (this.mLock) {
                disconnectServiceOnUserLocked(str, i, str2);
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public void stopServicesForUser(int i, String str) {
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            synchronized (this.mLock) {
                disconnectServiceOnUserLocked(i, str);
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    @GuardedBy({"mLock"})
    public final void disconnectServiceOnUserLocked(String str, int i, String str2) {
        DevicePolicyServiceConnection devicePolicyServiceConnection = this.mConnections.contains(i) ? this.mConnections.get(i).get(str) : null;
        if (devicePolicyServiceConnection != null) {
            devicePolicyServiceConnection.unbind();
            this.mConnections.get(i).remove(str);
            if (this.mConnections.get(i).isEmpty()) {
                this.mConnections.remove(i);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void disconnectServiceOnUserLocked(int i, String str) {
        if (this.mConnections.contains(i)) {
            for (String str2 : this.mConnections.get(i).keySet()) {
                this.mConnections.get(i).get(str2).unbind();
            }
            this.mConnections.remove(i);
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            if (this.mConnections.size() == 0) {
                return;
            }
            indentingPrintWriter.println("Admin Services:");
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < this.mConnections.size(); i++) {
                int keyAt = this.mConnections.keyAt(i);
                indentingPrintWriter.print("User: ");
                indentingPrintWriter.println(keyAt);
                for (String str : this.mConnections.get(keyAt).keySet()) {
                    indentingPrintWriter.increaseIndent();
                    indentingPrintWriter.print("Package: ");
                    indentingPrintWriter.println(str);
                    indentingPrintWriter.increaseIndent();
                    this.mConnections.valueAt(i).get(str).dump("", indentingPrintWriter);
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.decreaseIndent();
                }
            }
            indentingPrintWriter.decreaseIndent();
        }
    }
}
