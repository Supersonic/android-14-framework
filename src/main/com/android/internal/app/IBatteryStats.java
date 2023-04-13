package com.android.internal.app;

import android.Manifest;
import android.app.ActivityThread;
import android.bluetooth.BluetoothActivityEnergyInfo;
import android.content.AttributionSource;
import android.net.NetworkStack;
import android.p008os.BatteryUsageStats;
import android.p008os.BatteryUsageStatsQuery;
import android.p008os.Binder;
import android.p008os.BluetoothBatteryStats;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import android.p008os.WakeLockStats;
import android.p008os.WorkSource;
import android.p008os.connectivity.CellularBatteryStats;
import android.p008os.connectivity.GpsBatteryStats;
import android.p008os.connectivity.WifiActivityEnergyInfo;
import android.p008os.connectivity.WifiBatteryStats;
import android.p008os.health.HealthStatsParceler;
import android.telephony.ModemActivityInfo;
import android.telephony.SignalStrength;
import java.util.List;
/* loaded from: classes4.dex */
public interface IBatteryStats extends IInterface {
    long computeBatteryScreenOffRealtimeMs() throws RemoteException;

    long computeBatteryTimeRemaining() throws RemoteException;

    long computeChargeTimeRemaining() throws RemoteException;

    long getAwakeTimeBattery() throws RemoteException;

    long getAwakeTimePlugged() throws RemoteException;

    List<BatteryUsageStats> getBatteryUsageStats(List<BatteryUsageStatsQuery> list) throws RemoteException;

    BluetoothBatteryStats getBluetoothBatteryStats() throws RemoteException;

    CellularBatteryStats getCellularBatteryStats() throws RemoteException;

    GpsBatteryStats getGpsBatteryStats() throws RemoteException;

    long getScreenOffDischargeMah() throws RemoteException;

    WakeLockStats getWakeLockStats() throws RemoteException;

    WifiBatteryStats getWifiBatteryStats() throws RemoteException;

    boolean isCharging() throws RemoteException;

    void noteBleScanReset() throws RemoteException;

    void noteBleScanResults(WorkSource workSource, int i) throws RemoteException;

    void noteBleScanStarted(WorkSource workSource, boolean z) throws RemoteException;

    void noteBleScanStopped(WorkSource workSource, boolean z) throws RemoteException;

    void noteBluetoothControllerActivity(BluetoothActivityEnergyInfo bluetoothActivityEnergyInfo) throws RemoteException;

    void noteBluetoothOff(int i, int i2, String str) throws RemoteException;

    void noteBluetoothOn(int i, int i2, String str) throws RemoteException;

    void noteChangeWakelockFromSource(WorkSource workSource, int i, String str, String str2, int i2, WorkSource workSource2, int i3, String str3, String str4, int i4, boolean z) throws RemoteException;

    void noteConnectivityChanged(int i, String str) throws RemoteException;

    void noteDeviceIdleMode(int i, String str, int i2) throws RemoteException;

    void noteEvent(int i, String str, int i2) throws RemoteException;

    void noteFlashlightOff(int i) throws RemoteException;

    void noteFlashlightOn(int i) throws RemoteException;

    void noteFullWifiLockAcquired(int i) throws RemoteException;

    void noteFullWifiLockAcquiredFromSource(WorkSource workSource) throws RemoteException;

    void noteFullWifiLockReleased(int i) throws RemoteException;

    void noteFullWifiLockReleasedFromSource(WorkSource workSource) throws RemoteException;

    void noteGpsChanged(WorkSource workSource, WorkSource workSource2) throws RemoteException;

    void noteGpsSignalQuality(int i) throws RemoteException;

    void noteInteractive(boolean z) throws RemoteException;

    void noteJobFinish(String str, int i, int i2) throws RemoteException;

    void noteJobStart(String str, int i) throws RemoteException;

    void noteLongPartialWakelockFinish(String str, String str2, int i) throws RemoteException;

    void noteLongPartialWakelockFinishFromSource(String str, String str2, WorkSource workSource) throws RemoteException;

    void noteLongPartialWakelockStart(String str, String str2, int i) throws RemoteException;

    void noteLongPartialWakelockStartFromSource(String str, String str2, WorkSource workSource) throws RemoteException;

    void noteMobileRadioPowerState(int i, long j, int i2) throws RemoteException;

    void noteModemControllerActivity(ModemActivityInfo modemActivityInfo) throws RemoteException;

    void noteNetworkInterfaceForTransports(String str, int[] iArr) throws RemoteException;

    void noteNetworkStatsEnabled() throws RemoteException;

    void notePhoneDataConnectionState(int i, boolean z, int i2, int i3) throws RemoteException;

    void notePhoneOff() throws RemoteException;

    void notePhoneOn() throws RemoteException;

    void notePhoneSignalStrength(SignalStrength signalStrength) throws RemoteException;

    void notePhoneState(int i) throws RemoteException;

    void noteResetAudio() throws RemoteException;

    void noteResetCamera() throws RemoteException;

    void noteResetFlashlight() throws RemoteException;

    void noteResetVideo() throws RemoteException;

    void noteScreenBrightness(int i) throws RemoteException;

    void noteScreenState(int i) throws RemoteException;

    void noteStartAudio(int i) throws RemoteException;

    void noteStartCamera(int i) throws RemoteException;

    void noteStartSensor(int i, int i2) throws RemoteException;

    void noteStartVideo(int i) throws RemoteException;

    void noteStartWakelock(int i, int i2, String str, String str2, int i3, boolean z) throws RemoteException;

    void noteStartWakelockFromSource(WorkSource workSource, int i, String str, String str2, int i2, boolean z) throws RemoteException;

    void noteStopAudio(int i) throws RemoteException;

    void noteStopCamera(int i) throws RemoteException;

    void noteStopSensor(int i, int i2) throws RemoteException;

    void noteStopVideo(int i) throws RemoteException;

    void noteStopWakelock(int i, int i2, String str, String str2, int i3) throws RemoteException;

    void noteStopWakelockFromSource(WorkSource workSource, int i, String str, String str2, int i2) throws RemoteException;

    void noteSyncFinish(String str, int i) throws RemoteException;

    void noteSyncStart(String str, int i) throws RemoteException;

    void noteUserActivity(int i, int i2) throws RemoteException;

    void noteVibratorOff(int i) throws RemoteException;

    void noteVibratorOn(int i, long j) throws RemoteException;

    void noteWakeUp(String str, int i) throws RemoteException;

    void noteWifiBatchedScanStartedFromSource(WorkSource workSource, int i) throws RemoteException;

    void noteWifiBatchedScanStoppedFromSource(WorkSource workSource) throws RemoteException;

    void noteWifiControllerActivity(WifiActivityEnergyInfo wifiActivityEnergyInfo) throws RemoteException;

    void noteWifiMulticastDisabled(int i) throws RemoteException;

    void noteWifiMulticastEnabled(int i) throws RemoteException;

    void noteWifiOff() throws RemoteException;

    void noteWifiOn() throws RemoteException;

    void noteWifiRadioPowerState(int i, long j, int i2) throws RemoteException;

    void noteWifiRssiChanged(int i) throws RemoteException;

    void noteWifiRunning(WorkSource workSource) throws RemoteException;

    void noteWifiRunningChanged(WorkSource workSource, WorkSource workSource2) throws RemoteException;

    void noteWifiScanStarted(int i) throws RemoteException;

    void noteWifiScanStartedFromSource(WorkSource workSource) throws RemoteException;

    void noteWifiScanStopped(int i) throws RemoteException;

    void noteWifiScanStoppedFromSource(WorkSource workSource) throws RemoteException;

    void noteWifiState(int i, String str) throws RemoteException;

    void noteWifiStopped(WorkSource workSource) throws RemoteException;

    void noteWifiSupplicantStateChanged(int i, boolean z) throws RemoteException;

    void resetBattery(boolean z) throws RemoteException;

    void setBatteryLevel(int i, boolean z) throws RemoteException;

    void setBatteryState(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, long j) throws RemoteException;

    void setChargerAcOnline(boolean z, boolean z2) throws RemoteException;

    boolean setChargingStateUpdateDelayMillis(int i) throws RemoteException;

    void suspendBatteryInput() throws RemoteException;

    HealthStatsParceler takeUidSnapshot(int i) throws RemoteException;

    HealthStatsParceler[] takeUidSnapshots(int[] iArr) throws RemoteException;

    void unplugBattery(boolean z) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IBatteryStats {
        @Override // com.android.internal.app.IBatteryStats
        public void noteStartSensor(int uid, int sensor) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStopSensor(int uid, int sensor) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStartVideo(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStopVideo(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStartAudio(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStopAudio(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteResetVideo() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteResetAudio() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteFlashlightOn(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteFlashlightOff(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStartCamera(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStopCamera(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteResetCamera() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteResetFlashlight() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public List<BatteryUsageStats> getBatteryUsageStats(List<BatteryUsageStatsQuery> queries) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IBatteryStats
        public boolean isCharging() throws RemoteException {
            return false;
        }

        @Override // com.android.internal.app.IBatteryStats
        public long computeBatteryTimeRemaining() throws RemoteException {
            return 0L;
        }

        @Override // com.android.internal.app.IBatteryStats
        public long computeChargeTimeRemaining() throws RemoteException {
            return 0L;
        }

        @Override // com.android.internal.app.IBatteryStats
        public long computeBatteryScreenOffRealtimeMs() throws RemoteException {
            return 0L;
        }

        @Override // com.android.internal.app.IBatteryStats
        public long getScreenOffDischargeMah() throws RemoteException {
            return 0L;
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteEvent(int code, String name, int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteSyncStart(String name, int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteSyncFinish(String name, int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteJobStart(String name, int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteJobFinish(String name, int uid, int stopReason) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStartWakelock(int uid, int pid, String name, String historyName, int type, boolean unimportantForLogging) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStopWakelock(int uid, int pid, String name, String historyName, int type) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStartWakelockFromSource(WorkSource ws, int pid, String name, String historyName, int type, boolean unimportantForLogging) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteChangeWakelockFromSource(WorkSource ws, int pid, String name, String histyoryName, int type, WorkSource newWs, int newPid, String newName, String newHistoryName, int newType, boolean newUnimportantForLogging) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteStopWakelockFromSource(WorkSource ws, int pid, String name, String historyName, int type) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteLongPartialWakelockStart(String name, String historyName, int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteLongPartialWakelockStartFromSource(String name, String historyName, WorkSource workSource) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteLongPartialWakelockFinish(String name, String historyName, int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteLongPartialWakelockFinishFromSource(String name, String historyName, WorkSource workSource) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteVibratorOn(int uid, long durationMillis) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteVibratorOff(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteGpsChanged(WorkSource oldSource, WorkSource newSource) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteGpsSignalQuality(int signalLevel) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteScreenState(int state) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteScreenBrightness(int brightness) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteUserActivity(int uid, int event) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWakeUp(String reason, int reasonUid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteInteractive(boolean interactive) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteConnectivityChanged(int type, String extra) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteMobileRadioPowerState(int powerState, long timestampNs, int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void notePhoneOn() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void notePhoneOff() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void notePhoneSignalStrength(SignalStrength signalStrength) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void notePhoneDataConnectionState(int dataType, boolean hasData, int serviceType, int nrFrequency) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void notePhoneState(int phoneState) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiOn() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiOff() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiRunning(WorkSource ws) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiRunningChanged(WorkSource oldWs, WorkSource newWs) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiStopped(WorkSource ws) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiState(int wifiState, String accessPoint) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiSupplicantStateChanged(int supplState, boolean failedAuth) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiRssiChanged(int newRssi) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteFullWifiLockAcquired(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteFullWifiLockReleased(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiScanStarted(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiScanStopped(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiMulticastEnabled(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiMulticastDisabled(int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteFullWifiLockAcquiredFromSource(WorkSource ws) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteFullWifiLockReleasedFromSource(WorkSource ws) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiScanStartedFromSource(WorkSource ws) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiScanStoppedFromSource(WorkSource ws) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiBatchedScanStartedFromSource(WorkSource ws, int csph) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiBatchedScanStoppedFromSource(WorkSource ws) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiRadioPowerState(int powerState, long timestampNs, int uid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteNetworkInterfaceForTransports(String iface, int[] transportTypes) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteNetworkStatsEnabled() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteDeviceIdleMode(int mode, String activeReason, int activeUid) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void setBatteryState(int status, int health, int plugType, int level, int temp, int volt, int chargeUAh, int chargeFullUAh, long chargeTimeToFullSeconds) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public long getAwakeTimeBattery() throws RemoteException {
            return 0L;
        }

        @Override // com.android.internal.app.IBatteryStats
        public long getAwakeTimePlugged() throws RemoteException {
            return 0L;
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteBluetoothOn(int uid, int reason, String packageName) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteBluetoothOff(int uid, int reason, String packageName) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteBleScanStarted(WorkSource ws, boolean isUnoptimized) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteBleScanStopped(WorkSource ws, boolean isUnoptimized) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteBleScanReset() throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteBleScanResults(WorkSource ws, int numNewResults) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public CellularBatteryStats getCellularBatteryStats() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IBatteryStats
        public WifiBatteryStats getWifiBatteryStats() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IBatteryStats
        public GpsBatteryStats getGpsBatteryStats() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IBatteryStats
        public WakeLockStats getWakeLockStats() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IBatteryStats
        public BluetoothBatteryStats getBluetoothBatteryStats() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IBatteryStats
        public HealthStatsParceler takeUidSnapshot(int uid) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IBatteryStats
        public HealthStatsParceler[] takeUidSnapshots(int[] uid) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteBluetoothControllerActivity(BluetoothActivityEnergyInfo info) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteModemControllerActivity(ModemActivityInfo info) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void noteWifiControllerActivity(WifiActivityEnergyInfo info) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public boolean setChargingStateUpdateDelayMillis(int delay) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.app.IBatteryStats
        public void setChargerAcOnline(boolean online, boolean forceUpdate) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void setBatteryLevel(int level, boolean forceUpdate) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void unplugBattery(boolean forceUpdate) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void resetBattery(boolean forceUpdate) throws RemoteException {
        }

        @Override // com.android.internal.app.IBatteryStats
        public void suspendBatteryInput() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IBatteryStats {
        public static final String DESCRIPTOR = "com.android.internal.app.IBatteryStats";
        static final int TRANSACTION_computeBatteryScreenOffRealtimeMs = 19;
        static final int TRANSACTION_computeBatteryTimeRemaining = 17;
        static final int TRANSACTION_computeChargeTimeRemaining = 18;
        static final int TRANSACTION_getAwakeTimeBattery = 76;
        static final int TRANSACTION_getAwakeTimePlugged = 77;
        static final int TRANSACTION_getBatteryUsageStats = 15;
        static final int TRANSACTION_getBluetoothBatteryStats = 88;
        static final int TRANSACTION_getCellularBatteryStats = 84;
        static final int TRANSACTION_getGpsBatteryStats = 86;
        static final int TRANSACTION_getScreenOffDischargeMah = 20;
        static final int TRANSACTION_getWakeLockStats = 87;
        static final int TRANSACTION_getWifiBatteryStats = 85;
        static final int TRANSACTION_isCharging = 16;
        static final int TRANSACTION_noteBleScanReset = 82;
        static final int TRANSACTION_noteBleScanResults = 83;
        static final int TRANSACTION_noteBleScanStarted = 80;
        static final int TRANSACTION_noteBleScanStopped = 81;
        static final int TRANSACTION_noteBluetoothControllerActivity = 91;
        static final int TRANSACTION_noteBluetoothOff = 79;
        static final int TRANSACTION_noteBluetoothOn = 78;
        static final int TRANSACTION_noteChangeWakelockFromSource = 29;
        static final int TRANSACTION_noteConnectivityChanged = 44;
        static final int TRANSACTION_noteDeviceIdleMode = 74;
        static final int TRANSACTION_noteEvent = 21;
        static final int TRANSACTION_noteFlashlightOff = 10;
        static final int TRANSACTION_noteFlashlightOn = 9;
        static final int TRANSACTION_noteFullWifiLockAcquired = 59;
        static final int TRANSACTION_noteFullWifiLockAcquiredFromSource = 65;
        static final int TRANSACTION_noteFullWifiLockReleased = 60;
        static final int TRANSACTION_noteFullWifiLockReleasedFromSource = 66;
        static final int TRANSACTION_noteGpsChanged = 37;
        static final int TRANSACTION_noteGpsSignalQuality = 38;
        static final int TRANSACTION_noteInteractive = 43;
        static final int TRANSACTION_noteJobFinish = 25;
        static final int TRANSACTION_noteJobStart = 24;
        static final int TRANSACTION_noteLongPartialWakelockFinish = 33;
        static final int TRANSACTION_noteLongPartialWakelockFinishFromSource = 34;
        static final int TRANSACTION_noteLongPartialWakelockStart = 31;
        static final int TRANSACTION_noteLongPartialWakelockStartFromSource = 32;
        static final int TRANSACTION_noteMobileRadioPowerState = 45;
        static final int TRANSACTION_noteModemControllerActivity = 92;
        static final int TRANSACTION_noteNetworkInterfaceForTransports = 72;
        static final int TRANSACTION_noteNetworkStatsEnabled = 73;
        static final int TRANSACTION_notePhoneDataConnectionState = 49;
        static final int TRANSACTION_notePhoneOff = 47;
        static final int TRANSACTION_notePhoneOn = 46;
        static final int TRANSACTION_notePhoneSignalStrength = 48;
        static final int TRANSACTION_notePhoneState = 50;
        static final int TRANSACTION_noteResetAudio = 8;
        static final int TRANSACTION_noteResetCamera = 13;
        static final int TRANSACTION_noteResetFlashlight = 14;
        static final int TRANSACTION_noteResetVideo = 7;
        static final int TRANSACTION_noteScreenBrightness = 40;
        static final int TRANSACTION_noteScreenState = 39;
        static final int TRANSACTION_noteStartAudio = 5;
        static final int TRANSACTION_noteStartCamera = 11;
        static final int TRANSACTION_noteStartSensor = 1;
        static final int TRANSACTION_noteStartVideo = 3;
        static final int TRANSACTION_noteStartWakelock = 26;
        static final int TRANSACTION_noteStartWakelockFromSource = 28;
        static final int TRANSACTION_noteStopAudio = 6;
        static final int TRANSACTION_noteStopCamera = 12;
        static final int TRANSACTION_noteStopSensor = 2;
        static final int TRANSACTION_noteStopVideo = 4;
        static final int TRANSACTION_noteStopWakelock = 27;
        static final int TRANSACTION_noteStopWakelockFromSource = 30;
        static final int TRANSACTION_noteSyncFinish = 23;
        static final int TRANSACTION_noteSyncStart = 22;
        static final int TRANSACTION_noteUserActivity = 41;
        static final int TRANSACTION_noteVibratorOff = 36;
        static final int TRANSACTION_noteVibratorOn = 35;
        static final int TRANSACTION_noteWakeUp = 42;
        static final int TRANSACTION_noteWifiBatchedScanStartedFromSource = 69;
        static final int TRANSACTION_noteWifiBatchedScanStoppedFromSource = 70;
        static final int TRANSACTION_noteWifiControllerActivity = 93;
        static final int TRANSACTION_noteWifiMulticastDisabled = 64;
        static final int TRANSACTION_noteWifiMulticastEnabled = 63;
        static final int TRANSACTION_noteWifiOff = 52;
        static final int TRANSACTION_noteWifiOn = 51;
        static final int TRANSACTION_noteWifiRadioPowerState = 71;
        static final int TRANSACTION_noteWifiRssiChanged = 58;
        static final int TRANSACTION_noteWifiRunning = 53;
        static final int TRANSACTION_noteWifiRunningChanged = 54;
        static final int TRANSACTION_noteWifiScanStarted = 61;
        static final int TRANSACTION_noteWifiScanStartedFromSource = 67;
        static final int TRANSACTION_noteWifiScanStopped = 62;
        static final int TRANSACTION_noteWifiScanStoppedFromSource = 68;
        static final int TRANSACTION_noteWifiState = 56;
        static final int TRANSACTION_noteWifiStopped = 55;
        static final int TRANSACTION_noteWifiSupplicantStateChanged = 57;
        static final int TRANSACTION_resetBattery = 98;
        static final int TRANSACTION_setBatteryLevel = 96;
        static final int TRANSACTION_setBatteryState = 75;
        static final int TRANSACTION_setChargerAcOnline = 95;
        static final int TRANSACTION_setChargingStateUpdateDelayMillis = 94;
        static final int TRANSACTION_suspendBatteryInput = 99;
        static final int TRANSACTION_takeUidSnapshot = 89;
        static final int TRANSACTION_takeUidSnapshots = 90;
        static final int TRANSACTION_unplugBattery = 97;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IBatteryStats asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBatteryStats)) {
                return (IBatteryStats) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "noteStartSensor";
                case 2:
                    return "noteStopSensor";
                case 3:
                    return "noteStartVideo";
                case 4:
                    return "noteStopVideo";
                case 5:
                    return "noteStartAudio";
                case 6:
                    return "noteStopAudio";
                case 7:
                    return "noteResetVideo";
                case 8:
                    return "noteResetAudio";
                case 9:
                    return "noteFlashlightOn";
                case 10:
                    return "noteFlashlightOff";
                case 11:
                    return "noteStartCamera";
                case 12:
                    return "noteStopCamera";
                case 13:
                    return "noteResetCamera";
                case 14:
                    return "noteResetFlashlight";
                case 15:
                    return "getBatteryUsageStats";
                case 16:
                    return "isCharging";
                case 17:
                    return "computeBatteryTimeRemaining";
                case 18:
                    return "computeChargeTimeRemaining";
                case 19:
                    return "computeBatteryScreenOffRealtimeMs";
                case 20:
                    return "getScreenOffDischargeMah";
                case 21:
                    return "noteEvent";
                case 22:
                    return "noteSyncStart";
                case 23:
                    return "noteSyncFinish";
                case 24:
                    return "noteJobStart";
                case 25:
                    return "noteJobFinish";
                case 26:
                    return "noteStartWakelock";
                case 27:
                    return "noteStopWakelock";
                case 28:
                    return "noteStartWakelockFromSource";
                case 29:
                    return "noteChangeWakelockFromSource";
                case 30:
                    return "noteStopWakelockFromSource";
                case 31:
                    return "noteLongPartialWakelockStart";
                case 32:
                    return "noteLongPartialWakelockStartFromSource";
                case 33:
                    return "noteLongPartialWakelockFinish";
                case 34:
                    return "noteLongPartialWakelockFinishFromSource";
                case 35:
                    return "noteVibratorOn";
                case 36:
                    return "noteVibratorOff";
                case 37:
                    return "noteGpsChanged";
                case 38:
                    return "noteGpsSignalQuality";
                case 39:
                    return "noteScreenState";
                case 40:
                    return "noteScreenBrightness";
                case 41:
                    return "noteUserActivity";
                case 42:
                    return "noteWakeUp";
                case 43:
                    return "noteInteractive";
                case 44:
                    return "noteConnectivityChanged";
                case 45:
                    return "noteMobileRadioPowerState";
                case 46:
                    return "notePhoneOn";
                case 47:
                    return "notePhoneOff";
                case 48:
                    return "notePhoneSignalStrength";
                case 49:
                    return "notePhoneDataConnectionState";
                case 50:
                    return "notePhoneState";
                case 51:
                    return "noteWifiOn";
                case 52:
                    return "noteWifiOff";
                case 53:
                    return "noteWifiRunning";
                case 54:
                    return "noteWifiRunningChanged";
                case 55:
                    return "noteWifiStopped";
                case 56:
                    return "noteWifiState";
                case 57:
                    return "noteWifiSupplicantStateChanged";
                case 58:
                    return "noteWifiRssiChanged";
                case 59:
                    return "noteFullWifiLockAcquired";
                case 60:
                    return "noteFullWifiLockReleased";
                case 61:
                    return "noteWifiScanStarted";
                case 62:
                    return "noteWifiScanStopped";
                case 63:
                    return "noteWifiMulticastEnabled";
                case 64:
                    return "noteWifiMulticastDisabled";
                case 65:
                    return "noteFullWifiLockAcquiredFromSource";
                case 66:
                    return "noteFullWifiLockReleasedFromSource";
                case 67:
                    return "noteWifiScanStartedFromSource";
                case 68:
                    return "noteWifiScanStoppedFromSource";
                case 69:
                    return "noteWifiBatchedScanStartedFromSource";
                case 70:
                    return "noteWifiBatchedScanStoppedFromSource";
                case 71:
                    return "noteWifiRadioPowerState";
                case 72:
                    return "noteNetworkInterfaceForTransports";
                case 73:
                    return "noteNetworkStatsEnabled";
                case 74:
                    return "noteDeviceIdleMode";
                case 75:
                    return "setBatteryState";
                case 76:
                    return "getAwakeTimeBattery";
                case 77:
                    return "getAwakeTimePlugged";
                case 78:
                    return "noteBluetoothOn";
                case 79:
                    return "noteBluetoothOff";
                case 80:
                    return "noteBleScanStarted";
                case 81:
                    return "noteBleScanStopped";
                case 82:
                    return "noteBleScanReset";
                case 83:
                    return "noteBleScanResults";
                case 84:
                    return "getCellularBatteryStats";
                case 85:
                    return "getWifiBatteryStats";
                case 86:
                    return "getGpsBatteryStats";
                case 87:
                    return "getWakeLockStats";
                case 88:
                    return "getBluetoothBatteryStats";
                case 89:
                    return "takeUidSnapshot";
                case 90:
                    return "takeUidSnapshots";
                case 91:
                    return "noteBluetoothControllerActivity";
                case 92:
                    return "noteModemControllerActivity";
                case 93:
                    return "noteWifiControllerActivity";
                case 94:
                    return "setChargingStateUpdateDelayMillis";
                case 95:
                    return "setChargerAcOnline";
                case 96:
                    return "setBatteryLevel";
                case 97:
                    return "unplugBattery";
                case 98:
                    return "resetBattery";
                case 99:
                    return "suspendBatteryInput";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStartSensor(_arg0, _arg1);
                            reply.writeNoException();
                            return true;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStopSensor(_arg02, _arg12);
                            reply.writeNoException();
                            return true;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStartVideo(_arg03);
                            reply.writeNoException();
                            return true;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStopVideo(_arg04);
                            reply.writeNoException();
                            return true;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStartAudio(_arg05);
                            reply.writeNoException();
                            return true;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStopAudio(_arg06);
                            reply.writeNoException();
                            return true;
                        case 7:
                            noteResetVideo();
                            reply.writeNoException();
                            return true;
                        case 8:
                            noteResetAudio();
                            reply.writeNoException();
                            return true;
                        case 9:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            noteFlashlightOn(_arg07);
                            reply.writeNoException();
                            return true;
                        case 10:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            noteFlashlightOff(_arg08);
                            reply.writeNoException();
                            return true;
                        case 11:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStartCamera(_arg09);
                            reply.writeNoException();
                            return true;
                        case 12:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStopCamera(_arg010);
                            reply.writeNoException();
                            return true;
                        case 13:
                            noteResetCamera();
                            reply.writeNoException();
                            return true;
                        case 14:
                            noteResetFlashlight();
                            reply.writeNoException();
                            return true;
                        case 15:
                            List<BatteryUsageStatsQuery> _arg011 = data.createTypedArrayList(BatteryUsageStatsQuery.CREATOR);
                            data.enforceNoDataAvail();
                            List<BatteryUsageStats> _result = getBatteryUsageStats(_arg011);
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            return true;
                        case 16:
                            boolean _result2 = isCharging();
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            return true;
                        case 17:
                            long _result3 = computeBatteryTimeRemaining();
                            reply.writeNoException();
                            reply.writeLong(_result3);
                            return true;
                        case 18:
                            long _result4 = computeChargeTimeRemaining();
                            reply.writeNoException();
                            reply.writeLong(_result4);
                            return true;
                        case 19:
                            long _result5 = computeBatteryScreenOffRealtimeMs();
                            reply.writeNoException();
                            reply.writeLong(_result5);
                            return true;
                        case 20:
                            long _result6 = getScreenOffDischargeMah();
                            reply.writeNoException();
                            reply.writeLong(_result6);
                            return true;
                        case 21:
                            int _arg012 = data.readInt();
                            String _arg13 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            noteEvent(_arg012, _arg13, _arg2);
                            reply.writeNoException();
                            return true;
                        case 22:
                            String _arg013 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            noteSyncStart(_arg013, _arg14);
                            reply.writeNoException();
                            return true;
                        case 23:
                            String _arg014 = data.readString();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            noteSyncFinish(_arg014, _arg15);
                            reply.writeNoException();
                            return true;
                        case 24:
                            String _arg015 = data.readString();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            noteJobStart(_arg015, _arg16);
                            reply.writeNoException();
                            return true;
                        case 25:
                            String _arg016 = data.readString();
                            int _arg17 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            noteJobFinish(_arg016, _arg17, _arg22);
                            reply.writeNoException();
                            return true;
                        case 26:
                            int _arg017 = data.readInt();
                            int _arg18 = data.readInt();
                            String _arg23 = data.readString();
                            String _arg3 = data.readString();
                            int _arg4 = data.readInt();
                            boolean _arg5 = data.readBoolean();
                            data.enforceNoDataAvail();
                            noteStartWakelock(_arg017, _arg18, _arg23, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            return true;
                        case 27:
                            int _arg018 = data.readInt();
                            int _arg19 = data.readInt();
                            String _arg24 = data.readString();
                            String _arg32 = data.readString();
                            int _arg42 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStopWakelock(_arg018, _arg19, _arg24, _arg32, _arg42);
                            reply.writeNoException();
                            return true;
                        case 28:
                            WorkSource _arg019 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            int _arg110 = data.readInt();
                            String _arg25 = data.readString();
                            String _arg33 = data.readString();
                            int _arg43 = data.readInt();
                            boolean _arg52 = data.readBoolean();
                            data.enforceNoDataAvail();
                            noteStartWakelockFromSource(_arg019, _arg110, _arg25, _arg33, _arg43, _arg52);
                            reply.writeNoException();
                            return true;
                        case 29:
                            WorkSource _arg020 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            int _arg111 = data.readInt();
                            String _arg26 = data.readString();
                            String _arg34 = data.readString();
                            int _arg44 = data.readInt();
                            WorkSource _arg53 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            int _arg6 = data.readInt();
                            String _arg7 = data.readString();
                            String _arg8 = data.readString();
                            int _arg9 = data.readInt();
                            boolean _arg10 = data.readBoolean();
                            data.enforceNoDataAvail();
                            noteChangeWakelockFromSource(_arg020, _arg111, _arg26, _arg34, _arg44, _arg53, _arg6, _arg7, _arg8, _arg9, _arg10);
                            reply.writeNoException();
                            return true;
                        case 30:
                            WorkSource _arg021 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            int _arg112 = data.readInt();
                            String _arg27 = data.readString();
                            String _arg35 = data.readString();
                            int _arg45 = data.readInt();
                            data.enforceNoDataAvail();
                            noteStopWakelockFromSource(_arg021, _arg112, _arg27, _arg35, _arg45);
                            reply.writeNoException();
                            return true;
                        case 31:
                            String _arg022 = data.readString();
                            String _arg113 = data.readString();
                            int _arg28 = data.readInt();
                            data.enforceNoDataAvail();
                            noteLongPartialWakelockStart(_arg022, _arg113, _arg28);
                            reply.writeNoException();
                            return true;
                        case 32:
                            String _arg023 = data.readString();
                            String _arg114 = data.readString();
                            WorkSource _arg29 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteLongPartialWakelockStartFromSource(_arg023, _arg114, _arg29);
                            reply.writeNoException();
                            return true;
                        case 33:
                            String _arg024 = data.readString();
                            String _arg115 = data.readString();
                            int _arg210 = data.readInt();
                            data.enforceNoDataAvail();
                            noteLongPartialWakelockFinish(_arg024, _arg115, _arg210);
                            reply.writeNoException();
                            return true;
                        case 34:
                            String _arg025 = data.readString();
                            String _arg116 = data.readString();
                            WorkSource _arg211 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteLongPartialWakelockFinishFromSource(_arg025, _arg116, _arg211);
                            reply.writeNoException();
                            return true;
                        case 35:
                            int _arg026 = data.readInt();
                            long _arg117 = data.readLong();
                            data.enforceNoDataAvail();
                            noteVibratorOn(_arg026, _arg117);
                            reply.writeNoException();
                            return true;
                        case 36:
                            int _arg027 = data.readInt();
                            data.enforceNoDataAvail();
                            noteVibratorOff(_arg027);
                            reply.writeNoException();
                            return true;
                        case 37:
                            WorkSource _arg028 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            WorkSource _arg118 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteGpsChanged(_arg028, _arg118);
                            reply.writeNoException();
                            return true;
                        case 38:
                            int _arg029 = data.readInt();
                            data.enforceNoDataAvail();
                            noteGpsSignalQuality(_arg029);
                            reply.writeNoException();
                            return true;
                        case 39:
                            int _arg030 = data.readInt();
                            data.enforceNoDataAvail();
                            noteScreenState(_arg030);
                            reply.writeNoException();
                            return true;
                        case 40:
                            int _arg031 = data.readInt();
                            data.enforceNoDataAvail();
                            noteScreenBrightness(_arg031);
                            reply.writeNoException();
                            return true;
                        case 41:
                            int _arg032 = data.readInt();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            noteUserActivity(_arg032, _arg119);
                            reply.writeNoException();
                            return true;
                        case 42:
                            String _arg033 = data.readString();
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            noteWakeUp(_arg033, _arg120);
                            reply.writeNoException();
                            return true;
                        case 43:
                            boolean _arg034 = data.readBoolean();
                            data.enforceNoDataAvail();
                            noteInteractive(_arg034);
                            reply.writeNoException();
                            return true;
                        case 44:
                            int _arg035 = data.readInt();
                            String _arg121 = data.readString();
                            data.enforceNoDataAvail();
                            noteConnectivityChanged(_arg035, _arg121);
                            reply.writeNoException();
                            return true;
                        case 45:
                            int _arg036 = data.readInt();
                            long _arg122 = data.readLong();
                            int _arg212 = data.readInt();
                            data.enforceNoDataAvail();
                            noteMobileRadioPowerState(_arg036, _arg122, _arg212);
                            reply.writeNoException();
                            return true;
                        case 46:
                            notePhoneOn();
                            reply.writeNoException();
                            return true;
                        case 47:
                            notePhoneOff();
                            reply.writeNoException();
                            return true;
                        case 48:
                            SignalStrength _arg037 = (SignalStrength) data.readTypedObject(SignalStrength.CREATOR);
                            data.enforceNoDataAvail();
                            notePhoneSignalStrength(_arg037);
                            reply.writeNoException();
                            return true;
                        case 49:
                            int _arg038 = data.readInt();
                            boolean _arg123 = data.readBoolean();
                            int _arg213 = data.readInt();
                            int _arg36 = data.readInt();
                            data.enforceNoDataAvail();
                            notePhoneDataConnectionState(_arg038, _arg123, _arg213, _arg36);
                            reply.writeNoException();
                            return true;
                        case 50:
                            int _arg039 = data.readInt();
                            data.enforceNoDataAvail();
                            notePhoneState(_arg039);
                            reply.writeNoException();
                            return true;
                        case 51:
                            noteWifiOn();
                            reply.writeNoException();
                            return true;
                        case 52:
                            noteWifiOff();
                            reply.writeNoException();
                            return true;
                        case 53:
                            WorkSource _arg040 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteWifiRunning(_arg040);
                            reply.writeNoException();
                            return true;
                        case 54:
                            WorkSource _arg041 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            WorkSource _arg124 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteWifiRunningChanged(_arg041, _arg124);
                            reply.writeNoException();
                            return true;
                        case 55:
                            WorkSource _arg042 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteWifiStopped(_arg042);
                            reply.writeNoException();
                            return true;
                        case 56:
                            int _arg043 = data.readInt();
                            String _arg125 = data.readString();
                            data.enforceNoDataAvail();
                            noteWifiState(_arg043, _arg125);
                            reply.writeNoException();
                            return true;
                        case 57:
                            int _arg044 = data.readInt();
                            boolean _arg126 = data.readBoolean();
                            data.enforceNoDataAvail();
                            noteWifiSupplicantStateChanged(_arg044, _arg126);
                            reply.writeNoException();
                            return true;
                        case 58:
                            int _arg045 = data.readInt();
                            data.enforceNoDataAvail();
                            noteWifiRssiChanged(_arg045);
                            reply.writeNoException();
                            return true;
                        case 59:
                            int _arg046 = data.readInt();
                            data.enforceNoDataAvail();
                            noteFullWifiLockAcquired(_arg046);
                            reply.writeNoException();
                            return true;
                        case 60:
                            int _arg047 = data.readInt();
                            data.enforceNoDataAvail();
                            noteFullWifiLockReleased(_arg047);
                            reply.writeNoException();
                            return true;
                        case 61:
                            int _arg048 = data.readInt();
                            data.enforceNoDataAvail();
                            noteWifiScanStarted(_arg048);
                            reply.writeNoException();
                            return true;
                        case 62:
                            int _arg049 = data.readInt();
                            data.enforceNoDataAvail();
                            noteWifiScanStopped(_arg049);
                            reply.writeNoException();
                            return true;
                        case 63:
                            int _arg050 = data.readInt();
                            data.enforceNoDataAvail();
                            noteWifiMulticastEnabled(_arg050);
                            reply.writeNoException();
                            return true;
                        case 64:
                            int _arg051 = data.readInt();
                            data.enforceNoDataAvail();
                            noteWifiMulticastDisabled(_arg051);
                            reply.writeNoException();
                            return true;
                        case 65:
                            WorkSource _arg052 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteFullWifiLockAcquiredFromSource(_arg052);
                            reply.writeNoException();
                            return true;
                        case 66:
                            WorkSource _arg053 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteFullWifiLockReleasedFromSource(_arg053);
                            reply.writeNoException();
                            return true;
                        case 67:
                            WorkSource _arg054 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteWifiScanStartedFromSource(_arg054);
                            reply.writeNoException();
                            return true;
                        case 68:
                            WorkSource _arg055 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteWifiScanStoppedFromSource(_arg055);
                            reply.writeNoException();
                            return true;
                        case 69:
                            WorkSource _arg056 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            int _arg127 = data.readInt();
                            data.enforceNoDataAvail();
                            noteWifiBatchedScanStartedFromSource(_arg056, _arg127);
                            reply.writeNoException();
                            return true;
                        case 70:
                            WorkSource _arg057 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            data.enforceNoDataAvail();
                            noteWifiBatchedScanStoppedFromSource(_arg057);
                            reply.writeNoException();
                            return true;
                        case 71:
                            int _arg058 = data.readInt();
                            long _arg128 = data.readLong();
                            int _arg214 = data.readInt();
                            data.enforceNoDataAvail();
                            noteWifiRadioPowerState(_arg058, _arg128, _arg214);
                            reply.writeNoException();
                            return true;
                        case 72:
                            String _arg059 = data.readString();
                            int[] _arg129 = data.createIntArray();
                            data.enforceNoDataAvail();
                            noteNetworkInterfaceForTransports(_arg059, _arg129);
                            reply.writeNoException();
                            return true;
                        case 73:
                            noteNetworkStatsEnabled();
                            reply.writeNoException();
                            return true;
                        case 74:
                            int _arg060 = data.readInt();
                            String _arg130 = data.readString();
                            int _arg215 = data.readInt();
                            data.enforceNoDataAvail();
                            noteDeviceIdleMode(_arg060, _arg130, _arg215);
                            reply.writeNoException();
                            return true;
                        case 75:
                            int _arg061 = data.readInt();
                            int _arg131 = data.readInt();
                            int _arg216 = data.readInt();
                            int _arg37 = data.readInt();
                            int _arg46 = data.readInt();
                            int _arg54 = data.readInt();
                            int _arg62 = data.readInt();
                            int _arg72 = data.readInt();
                            long _arg82 = data.readLong();
                            data.enforceNoDataAvail();
                            setBatteryState(_arg061, _arg131, _arg216, _arg37, _arg46, _arg54, _arg62, _arg72, _arg82);
                            reply.writeNoException();
                            return true;
                        case 76:
                            long _result7 = getAwakeTimeBattery();
                            reply.writeNoException();
                            reply.writeLong(_result7);
                            return true;
                        case 77:
                            long _result8 = getAwakeTimePlugged();
                            reply.writeNoException();
                            reply.writeLong(_result8);
                            return true;
                        case 78:
                            int _arg062 = data.readInt();
                            int _arg132 = data.readInt();
                            String _arg217 = data.readString();
                            data.enforceNoDataAvail();
                            noteBluetoothOn(_arg062, _arg132, _arg217);
                            reply.writeNoException();
                            return true;
                        case 79:
                            int _arg063 = data.readInt();
                            int _arg133 = data.readInt();
                            String _arg218 = data.readString();
                            data.enforceNoDataAvail();
                            noteBluetoothOff(_arg063, _arg133, _arg218);
                            reply.writeNoException();
                            return true;
                        case 80:
                            WorkSource _arg064 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            boolean _arg134 = data.readBoolean();
                            data.enforceNoDataAvail();
                            noteBleScanStarted(_arg064, _arg134);
                            reply.writeNoException();
                            return true;
                        case 81:
                            WorkSource _arg065 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            boolean _arg135 = data.readBoolean();
                            data.enforceNoDataAvail();
                            noteBleScanStopped(_arg065, _arg135);
                            reply.writeNoException();
                            return true;
                        case 82:
                            noteBleScanReset();
                            reply.writeNoException();
                            return true;
                        case 83:
                            WorkSource _arg066 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            int _arg136 = data.readInt();
                            data.enforceNoDataAvail();
                            noteBleScanResults(_arg066, _arg136);
                            reply.writeNoException();
                            return true;
                        case 84:
                            CellularBatteryStats _result9 = getCellularBatteryStats();
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            return true;
                        case 85:
                            WifiBatteryStats _result10 = getWifiBatteryStats();
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            return true;
                        case 86:
                            GpsBatteryStats _result11 = getGpsBatteryStats();
                            reply.writeNoException();
                            reply.writeTypedObject(_result11, 1);
                            return true;
                        case 87:
                            WakeLockStats _result12 = getWakeLockStats();
                            reply.writeNoException();
                            reply.writeTypedObject(_result12, 1);
                            return true;
                        case 88:
                            BluetoothBatteryStats _result13 = getBluetoothBatteryStats();
                            reply.writeNoException();
                            reply.writeTypedObject(_result13, 1);
                            return true;
                        case 89:
                            int _arg067 = data.readInt();
                            data.enforceNoDataAvail();
                            HealthStatsParceler _result14 = takeUidSnapshot(_arg067);
                            reply.writeNoException();
                            reply.writeTypedObject(_result14, 1);
                            return true;
                        case 90:
                            int[] _arg068 = data.createIntArray();
                            data.enforceNoDataAvail();
                            HealthStatsParceler[] _result15 = takeUidSnapshots(_arg068);
                            reply.writeNoException();
                            reply.writeTypedArray(_result15, 1);
                            return true;
                        case 91:
                            BluetoothActivityEnergyInfo _arg069 = (BluetoothActivityEnergyInfo) data.readTypedObject(BluetoothActivityEnergyInfo.CREATOR);
                            data.enforceNoDataAvail();
                            noteBluetoothControllerActivity(_arg069);
                            return true;
                        case 92:
                            ModemActivityInfo _arg070 = (ModemActivityInfo) data.readTypedObject(ModemActivityInfo.CREATOR);
                            data.enforceNoDataAvail();
                            noteModemControllerActivity(_arg070);
                            return true;
                        case 93:
                            WifiActivityEnergyInfo _arg071 = (WifiActivityEnergyInfo) data.readTypedObject(WifiActivityEnergyInfo.CREATOR);
                            data.enforceNoDataAvail();
                            noteWifiControllerActivity(_arg071);
                            return true;
                        case 94:
                            int _arg072 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result16 = setChargingStateUpdateDelayMillis(_arg072);
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            return true;
                        case 95:
                            boolean _arg073 = data.readBoolean();
                            boolean _arg137 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setChargerAcOnline(_arg073, _arg137);
                            reply.writeNoException();
                            return true;
                        case 96:
                            int _arg074 = data.readInt();
                            boolean _arg138 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBatteryLevel(_arg074, _arg138);
                            reply.writeNoException();
                            return true;
                        case 97:
                            boolean _arg075 = data.readBoolean();
                            data.enforceNoDataAvail();
                            unplugBattery(_arg075);
                            reply.writeNoException();
                            return true;
                        case 98:
                            boolean _arg076 = data.readBoolean();
                            data.enforceNoDataAvail();
                            resetBattery(_arg076);
                            reply.writeNoException();
                            return true;
                        case 99:
                            suspendBatteryInput();
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IBatteryStats {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartSensor(int uid, int sensor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(sensor);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopSensor(int uid, int sensor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(sensor);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartVideo(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopVideo(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartAudio(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopAudio(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteResetVideo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteResetAudio() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteFlashlightOn(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteFlashlightOff(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartCamera(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopCamera(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteResetCamera() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteResetFlashlight() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public List<BatteryUsageStats> getBatteryUsageStats(List<BatteryUsageStatsQuery> queries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(queries, 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    List<BatteryUsageStats> _result = _reply.createTypedArrayList(BatteryUsageStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public boolean isCharging() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public long computeBatteryTimeRemaining() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public long computeChargeTimeRemaining() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public long computeBatteryScreenOffRealtimeMs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public long getScreenOffDischargeMah() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteEvent(int code, String name, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(name);
                    _data.writeInt(uid);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteSyncStart(String name, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(uid);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteSyncFinish(String name, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(uid);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteJobStart(String name, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(uid);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteJobFinish(String name, int uid, int stopReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(uid);
                    _data.writeInt(stopReason);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartWakelock(int uid, int pid, String name, String historyName, int type, boolean unimportantForLogging) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(pid);
                    _data.writeString(name);
                    _data.writeString(historyName);
                    _data.writeInt(type);
                    _data.writeBoolean(unimportantForLogging);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopWakelock(int uid, int pid, String name, String historyName, int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(pid);
                    _data.writeString(name);
                    _data.writeString(historyName);
                    _data.writeInt(type);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartWakelockFromSource(WorkSource ws, int pid, String name, String historyName, int type, boolean unimportantForLogging) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    _data.writeInt(pid);
                    _data.writeString(name);
                    _data.writeString(historyName);
                    _data.writeInt(type);
                    _data.writeBoolean(unimportantForLogging);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteChangeWakelockFromSource(WorkSource ws, int pid, String name, String histyoryName, int type, WorkSource newWs, int newPid, String newName, String newHistoryName, int newType, boolean newUnimportantForLogging) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    try {
                        _data.writeInt(pid);
                        try {
                            _data.writeString(name);
                        } catch (Throwable th) {
                            th = th;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
                try {
                    _data.writeString(histyoryName);
                    try {
                        _data.writeInt(type);
                        try {
                            _data.writeTypedObject(newWs, 0);
                            try {
                                _data.writeInt(newPid);
                            } catch (Throwable th4) {
                                th = th4;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th5) {
                            th = th5;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(newName);
                        try {
                            _data.writeString(newHistoryName);
                            try {
                                _data.writeInt(newType);
                                try {
                                    _data.writeBoolean(newUnimportantForLogging);
                                } catch (Throwable th7) {
                                    th = th7;
                                }
                            } catch (Throwable th8) {
                                th = th8;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th9) {
                            th = th9;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th10) {
                        th = th10;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th11) {
                    th = th11;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
                try {
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th12) {
                    th = th12;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopWakelockFromSource(WorkSource ws, int pid, String name, String historyName, int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    _data.writeInt(pid);
                    _data.writeString(name);
                    _data.writeString(historyName);
                    _data.writeInt(type);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteLongPartialWakelockStart(String name, String historyName, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(historyName);
                    _data.writeInt(uid);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteLongPartialWakelockStartFromSource(String name, String historyName, WorkSource workSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(historyName);
                    _data.writeTypedObject(workSource, 0);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteLongPartialWakelockFinish(String name, String historyName, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(historyName);
                    _data.writeInt(uid);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteLongPartialWakelockFinishFromSource(String name, String historyName, WorkSource workSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(historyName);
                    _data.writeTypedObject(workSource, 0);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteVibratorOn(int uid, long durationMillis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeLong(durationMillis);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteVibratorOff(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteGpsChanged(WorkSource oldSource, WorkSource newSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(oldSource, 0);
                    _data.writeTypedObject(newSource, 0);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteGpsSignalQuality(int signalLevel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(signalLevel);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteScreenState(int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteScreenBrightness(int brightness) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(brightness);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteUserActivity(int uid, int event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(event);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWakeUp(String reason, int reasonUid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    _data.writeInt(reasonUid);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteInteractive(boolean interactive) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(interactive);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteConnectivityChanged(int type, String extra) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(extra);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteMobileRadioPowerState(int powerState, long timestampNs, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(powerState);
                    _data.writeLong(timestampNs);
                    _data.writeInt(uid);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneOn() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneOff() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneSignalStrength(SignalStrength signalStrength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(signalStrength, 0);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneDataConnectionState(int dataType, boolean hasData, int serviceType, int nrFrequency) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(dataType);
                    _data.writeBoolean(hasData);
                    _data.writeInt(serviceType);
                    _data.writeInt(nrFrequency);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneState(int phoneState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneState);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiOn() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiOff() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiRunning(WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiRunningChanged(WorkSource oldWs, WorkSource newWs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(oldWs, 0);
                    _data.writeTypedObject(newWs, 0);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiStopped(WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiState(int wifiState, String accessPoint) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(wifiState);
                    _data.writeString(accessPoint);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiSupplicantStateChanged(int supplState, boolean failedAuth) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(supplState);
                    _data.writeBoolean(failedAuth);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiRssiChanged(int newRssi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(newRssi);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteFullWifiLockAcquired(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteFullWifiLockReleased(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiScanStarted(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiScanStopped(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiMulticastEnabled(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiMulticastDisabled(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteFullWifiLockAcquiredFromSource(WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteFullWifiLockReleasedFromSource(WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiScanStartedFromSource(WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiScanStoppedFromSource(WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiBatchedScanStartedFromSource(WorkSource ws, int csph) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    _data.writeInt(csph);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiBatchedScanStoppedFromSource(WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiRadioPowerState(int powerState, long timestampNs, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(powerState);
                    _data.writeLong(timestampNs);
                    _data.writeInt(uid);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteNetworkInterfaceForTransports(String iface, int[] transportTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeIntArray(transportTypes);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteNetworkStatsEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteDeviceIdleMode(int mode, String activeReason, int activeUid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeString(activeReason);
                    _data.writeInt(activeUid);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void setBatteryState(int status, int health, int plugType, int level, int temp, int volt, int chargeUAh, int chargeFullUAh, long chargeTimeToFullSeconds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeInt(health);
                    _data.writeInt(plugType);
                    _data.writeInt(level);
                    _data.writeInt(temp);
                    _data.writeInt(volt);
                    _data.writeInt(chargeUAh);
                    _data.writeInt(chargeFullUAh);
                    _data.writeLong(chargeTimeToFullSeconds);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public long getAwakeTimeBattery() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public long getAwakeTimePlugged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteBluetoothOn(int uid, int reason, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(reason);
                    _data.writeString(packageName);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteBluetoothOff(int uid, int reason, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(reason);
                    _data.writeString(packageName);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteBleScanStarted(WorkSource ws, boolean isUnoptimized) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    _data.writeBoolean(isUnoptimized);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteBleScanStopped(WorkSource ws, boolean isUnoptimized) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    _data.writeBoolean(isUnoptimized);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteBleScanReset() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(82, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteBleScanResults(WorkSource ws, int numNewResults) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ws, 0);
                    _data.writeInt(numNewResults);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public CellularBatteryStats getCellularBatteryStats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                    CellularBatteryStats _result = (CellularBatteryStats) _reply.readTypedObject(CellularBatteryStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public WifiBatteryStats getWifiBatteryStats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    WifiBatteryStats _result = (WifiBatteryStats) _reply.readTypedObject(WifiBatteryStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public GpsBatteryStats getGpsBatteryStats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                    GpsBatteryStats _result = (GpsBatteryStats) _reply.readTypedObject(GpsBatteryStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public WakeLockStats getWakeLockStats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                    WakeLockStats _result = (WakeLockStats) _reply.readTypedObject(WakeLockStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public BluetoothBatteryStats getBluetoothBatteryStats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                    BluetoothBatteryStats _result = (BluetoothBatteryStats) _reply.readTypedObject(BluetoothBatteryStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public HealthStatsParceler takeUidSnapshot(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                    HealthStatsParceler _result = (HealthStatsParceler) _reply.readTypedObject(HealthStatsParceler.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public HealthStatsParceler[] takeUidSnapshots(int[] uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(uid);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                    HealthStatsParceler[] _result = (HealthStatsParceler[]) _reply.createTypedArray(HealthStatsParceler.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteBluetoothControllerActivity(BluetoothActivityEnergyInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(91, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteModemControllerActivity(ModemActivityInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(92, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiControllerActivity(WifiActivityEnergyInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(93, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public boolean setChargingStateUpdateDelayMillis(int delay) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(delay);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void setChargerAcOnline(boolean online, boolean forceUpdate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(online);
                    _data.writeBoolean(forceUpdate);
                    this.mRemote.transact(95, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void setBatteryLevel(int level, boolean forceUpdate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(level);
                    _data.writeBoolean(forceUpdate);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void unplugBattery(boolean forceUpdate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(forceUpdate);
                    this.mRemote.transact(97, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void resetBattery(boolean forceUpdate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(forceUpdate);
                    this.mRemote.transact(98, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void suspendBatteryInput() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(99, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void noteStartSensor_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStopSensor_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStartVideo_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStopVideo_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStartAudio_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStopAudio_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteResetVideo_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteResetAudio_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteFlashlightOn_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteFlashlightOff_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStartCamera_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStopCamera_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteResetCamera_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteResetFlashlight_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void getBatteryUsageStats_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BATTERY_STATS, source);
        }

        protected void computeBatteryScreenOffRealtimeMs_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BATTERY_STATS, source);
        }

        protected void getScreenOffDischargeMah_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BATTERY_STATS, source);
        }

        protected void noteEvent_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteSyncStart_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteSyncFinish_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteJobStart_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteJobFinish_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStartWakelock_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStopWakelock_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStartWakelockFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteChangeWakelockFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteStopWakelockFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteLongPartialWakelockStart_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteLongPartialWakelockStartFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteLongPartialWakelockFinish_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteLongPartialWakelockFinishFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteVibratorOn_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteVibratorOff_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteGpsChanged_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteGpsSignalQuality_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteScreenState_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteScreenBrightness_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteUserActivity_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWakeUp_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteInteractive_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteConnectivityChanged_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteMobileRadioPowerState_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void notePhoneOn_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void notePhoneOff_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void notePhoneSignalStrength_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void notePhoneDataConnectionState_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void notePhoneState_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiOn_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiOff_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiRunning_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiRunningChanged_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiStopped_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiState_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiSupplicantStateChanged_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiRssiChanged_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteFullWifiLockAcquired_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteFullWifiLockReleased_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiScanStarted_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiScanStopped_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiMulticastEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiMulticastDisabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteFullWifiLockAcquiredFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteFullWifiLockReleasedFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiScanStartedFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiScanStoppedFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiBatchedScanStartedFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiBatchedScanStoppedFromSource_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiRadioPowerState_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteNetworkInterfaceForTransports_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.NETWORK_STACK, NetworkStack.PERMISSION_MAINLINE_NETWORK_STACK}, source);
        }

        protected void noteNetworkStatsEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteDeviceIdleMode_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void setBatteryState_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void getAwakeTimeBattery_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BATTERY_STATS, source);
        }

        protected void getAwakeTimePlugged_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BATTERY_STATS, source);
        }

        protected void noteBluetoothOn_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BLUETOOTH_CONNECT, source);
        }

        protected void noteBluetoothOff_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BLUETOOTH_CONNECT, source);
        }

        protected void noteBleScanStarted_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteBleScanStopped_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteBleScanReset_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteBleScanResults_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void getCellularBatteryStats_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.UPDATE_DEVICE_STATS, Manifest.C0000permission.BATTERY_STATS}, source);
        }

        protected void getWifiBatteryStats_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.UPDATE_DEVICE_STATS, Manifest.C0000permission.BATTERY_STATS}, source);
        }

        protected void getGpsBatteryStats_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BATTERY_STATS, source);
        }

        protected void getWakeLockStats_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BATTERY_STATS, source);
        }

        protected void getBluetoothBatteryStats_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BATTERY_STATS, source);
        }

        protected void noteBluetoothControllerActivity_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteModemControllerActivity_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void noteWifiControllerActivity_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.UPDATE_DEVICE_STATS, source);
        }

        protected void setChargingStateUpdateDelayMillis_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.POWER_SAVER, source);
        }

        protected void setChargerAcOnline_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.DEVICE_POWER, source);
        }

        protected void setBatteryLevel_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.DEVICE_POWER, source);
        }

        protected void unplugBattery_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.DEVICE_POWER, source);
        }

        protected void resetBattery_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.DEVICE_POWER, source);
        }

        protected void suspendBatteryInput_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.DEVICE_POWER, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 98;
        }
    }
}
