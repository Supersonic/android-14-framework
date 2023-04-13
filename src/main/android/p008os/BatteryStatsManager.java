package android.p008os;

import android.annotation.SystemApi;
import android.p008os.connectivity.CellularBatteryStats;
import android.p008os.connectivity.WifiBatteryStats;
import com.android.internal.app.IBatteryStats;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
@SystemApi
/* renamed from: android.os.BatteryStatsManager */
/* loaded from: classes3.dex */
public final class BatteryStatsManager {
    public static final int NUM_WIFI_STATES = 8;
    public static final int NUM_WIFI_SUPPL_STATES = 13;
    public static final int WIFI_STATE_OFF = 0;
    public static final int WIFI_STATE_OFF_SCANNING = 1;
    public static final int WIFI_STATE_ON_CONNECTED_P2P = 5;
    public static final int WIFI_STATE_ON_CONNECTED_STA = 4;
    public static final int WIFI_STATE_ON_CONNECTED_STA_P2P = 6;
    public static final int WIFI_STATE_ON_DISCONNECTED = 3;
    public static final int WIFI_STATE_ON_NO_NETWORKS = 2;
    public static final int WIFI_STATE_SOFT_AP = 7;
    public static final int WIFI_SUPPL_STATE_ASSOCIATED = 7;
    public static final int WIFI_SUPPL_STATE_ASSOCIATING = 6;
    public static final int WIFI_SUPPL_STATE_AUTHENTICATING = 5;
    public static final int WIFI_SUPPL_STATE_COMPLETED = 10;
    public static final int WIFI_SUPPL_STATE_DISCONNECTED = 1;
    public static final int WIFI_SUPPL_STATE_DORMANT = 11;
    public static final int WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE = 8;
    public static final int WIFI_SUPPL_STATE_GROUP_HANDSHAKE = 9;
    public static final int WIFI_SUPPL_STATE_INACTIVE = 3;
    public static final int WIFI_SUPPL_STATE_INTERFACE_DISABLED = 2;
    public static final int WIFI_SUPPL_STATE_INVALID = 0;
    public static final int WIFI_SUPPL_STATE_SCANNING = 4;
    public static final int WIFI_SUPPL_STATE_UNINITIALIZED = 12;
    private final IBatteryStats mBatteryStats;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BatteryStatsManager$WifiState */
    /* loaded from: classes3.dex */
    public @interface WifiState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BatteryStatsManager$WifiSupplState */
    /* loaded from: classes3.dex */
    public @interface WifiSupplState {
    }

    public BatteryStatsManager(IBatteryStats batteryStats) {
        this.mBatteryStats = batteryStats;
    }

    public BatteryUsageStats getBatteryUsageStats() {
        return getBatteryUsageStats(BatteryUsageStatsQuery.DEFAULT);
    }

    public BatteryUsageStats getBatteryUsageStats(BatteryUsageStatsQuery query) {
        return getBatteryUsageStats(List.of(query)).get(0);
    }

    public List<BatteryUsageStats> getBatteryUsageStats(List<BatteryUsageStatsQuery> queries) {
        try {
            return this.mBatteryStats.getBatteryUsageStats(queries);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportWifiRssiChanged(int newRssi) {
        try {
            this.mBatteryStats.noteWifiRssiChanged(newRssi);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiOn() {
        try {
            this.mBatteryStats.noteWifiOn();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiOff() {
        try {
            this.mBatteryStats.noteWifiOff();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiState(int newWifiState, String accessPoint) {
        try {
            this.mBatteryStats.noteWifiState(newWifiState, accessPoint);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiScanStartedFromSource(WorkSource ws) {
        try {
            this.mBatteryStats.noteWifiScanStartedFromSource(ws);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiScanStoppedFromSource(WorkSource ws) {
        try {
            this.mBatteryStats.noteWifiScanStoppedFromSource(ws);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiBatchedScanStartedFromSource(WorkSource ws, int csph) {
        try {
            this.mBatteryStats.noteWifiBatchedScanStartedFromSource(ws, csph);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiBatchedScanStoppedFromSource(WorkSource ws) {
        try {
            this.mBatteryStats.noteWifiBatchedScanStoppedFromSource(ws);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public CellularBatteryStats getCellularBatteryStats() {
        try {
            return this.mBatteryStats.getCellularBatteryStats();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return null;
        }
    }

    public WifiBatteryStats getWifiBatteryStats() {
        try {
            return this.mBatteryStats.getWifiBatteryStats();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return null;
        }
    }

    public WakeLockStats getWakeLockStats() {
        try {
            return this.mBatteryStats.getWakeLockStats();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public BluetoothBatteryStats getBluetoothBatteryStats() {
        try {
            return this.mBatteryStats.getBluetoothBatteryStats();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportFullWifiLockAcquiredFromSource(WorkSource ws) {
        try {
            this.mBatteryStats.noteFullWifiLockAcquiredFromSource(ws);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportFullWifiLockReleasedFromSource(WorkSource ws) {
        try {
            this.mBatteryStats.noteFullWifiLockReleasedFromSource(ws);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiSupplicantStateChanged(int newSupplState, boolean failedAuth) {
        try {
            this.mBatteryStats.noteWifiSupplicantStateChanged(newSupplState, failedAuth);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiMulticastEnabled(WorkSource ws) {
        try {
            this.mBatteryStats.noteWifiMulticastEnabled(ws.getAttributionUid());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiMulticastDisabled(WorkSource ws) {
        try {
            this.mBatteryStats.noteWifiMulticastDisabled(ws.getAttributionUid());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportMobileRadioPowerState(boolean isActive, int uid) {
        try {
            this.mBatteryStats.noteMobileRadioPowerState(getDataConnectionPowerState(isActive), SystemClock.elapsedRealtimeNanos(), uid);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportWifiRadioPowerState(boolean isActive, int uid) {
        try {
            this.mBatteryStats.noteWifiRadioPowerState(getDataConnectionPowerState(isActive), SystemClock.elapsedRealtimeNanos(), uid);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void reportNetworkInterfaceForTransports(String iface, int[] transportTypes) throws RuntimeException {
        try {
            this.mBatteryStats.noteNetworkInterfaceForTransports(iface, transportTypes);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportBluetoothOn(int uid, int reason, String packageName) {
        try {
            this.mBatteryStats.noteBluetoothOn(uid, reason, packageName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportBluetoothOff(int uid, int reason, String packageName) {
        try {
            this.mBatteryStats.noteBluetoothOff(uid, reason, packageName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportBleScanStarted(WorkSource ws, boolean isUnoptimized) {
        try {
            this.mBatteryStats.noteBleScanStarted(ws, isUnoptimized);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportBleScanStopped(WorkSource ws, boolean isUnoptimized) {
        try {
            this.mBatteryStats.noteBleScanStopped(ws, isUnoptimized);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportBleScanReset() {
        try {
            this.mBatteryStats.noteBleScanReset();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void reportBleScanResults(WorkSource ws, int numNewResults) {
        try {
            this.mBatteryStats.noteBleScanResults(ws, numNewResults);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    private static int getDataConnectionPowerState(boolean isActive) {
        return isActive ? 3 : 1;
    }

    public void setChargerAcOnline(boolean online, boolean forceUpdate) {
        try {
            this.mBatteryStats.setChargerAcOnline(online, forceUpdate);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setBatteryLevel(int level, boolean forceUpdate) {
        try {
            this.mBatteryStats.setBatteryLevel(level, forceUpdate);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void unplugBattery(boolean forceUpdate) {
        try {
            this.mBatteryStats.unplugBattery(forceUpdate);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void resetBattery(boolean forceUpdate) {
        try {
            this.mBatteryStats.resetBattery(forceUpdate);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void suspendBatteryInput() {
        try {
            this.mBatteryStats.suspendBatteryInput();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }
}
