package android.app.admin;

import com.android.server.LocalServices;
/* loaded from: classes.dex */
public abstract class DeviceStateCache {
    public abstract boolean isDeviceProvisioned();

    public abstract boolean isUserOrganizationManaged(int i);

    protected DeviceStateCache() {
    }

    public static DeviceStateCache getInstance() {
        DevicePolicyManagerInternal dpmi = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        return dpmi != null ? dpmi.getDeviceStateCache() : EmptyDeviceStateCache.INSTANCE;
    }

    /* loaded from: classes.dex */
    private static class EmptyDeviceStateCache extends DeviceStateCache {
        private static final EmptyDeviceStateCache INSTANCE = new EmptyDeviceStateCache();

        private EmptyDeviceStateCache() {
        }

        @Override // android.app.admin.DeviceStateCache
        public boolean isDeviceProvisioned() {
            return false;
        }

        @Override // android.app.admin.DeviceStateCache
        public boolean isUserOrganizationManaged(int userHandle) {
            return false;
        }
    }
}
