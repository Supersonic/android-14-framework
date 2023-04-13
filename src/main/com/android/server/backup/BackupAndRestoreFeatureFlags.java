package com.android.server.backup;

import android.annotation.RequiresPermission;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
/* loaded from: classes.dex */
public class BackupAndRestoreFeatureFlags {
    @RequiresPermission("android.permission.READ_DEVICE_CONFIG")
    public static long getBackupTransportFutureTimeoutMillis() {
        return DeviceConfig.getLong("backup_and_restore", "backup_transport_future_timeout_millis", 600000L);
    }

    @RequiresPermission("android.permission.READ_DEVICE_CONFIG")
    public static long getBackupTransportCallbackTimeoutMillis() {
        return DeviceConfig.getLong("backup_and_restore", "backup_transport_callback_timeout_millis", (long) BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
    }

    @RequiresPermission("android.permission.READ_DEVICE_CONFIG")
    public static int getFullBackupWriteToTransportBufferSizeBytes() {
        return DeviceConfig.getInt("backup_and_restore", "full_backup_write_to_transport_buffer_size_bytes", (int) IInstalld.FLAG_FORCE);
    }

    @RequiresPermission("android.permission.READ_DEVICE_CONFIG")
    public static int getFullBackupUtilsRouteBufferSizeBytes() {
        return DeviceConfig.getInt("backup_and_restore", "full_backup_utils_route_buffer_size_bytes", 32768);
    }
}
